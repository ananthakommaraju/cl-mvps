package com.lloydsbanking.salsa.apapca.service.fulfil.converter;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.apapca.service.fulfil.rules.RateType;
import com.lloydsbanking.salsa.downstream.account.client.b276.RetrieveAccProcessOverdraftRequestBuilder;
import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.account.StHeader;
import com.lloydsbanking.salsa.soap.fs.account.StODRates;
import com.lloydstsb.ib.wsbridge.account.StB276AAccProcessOverdraft;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class B276RequestFactory {

    @Autowired
    AddressDetails addressDetails;

    @Autowired
    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    StODRatesFactory stODRatesFactory;

    private static final BigInteger OVERDRAFT_TERM_IN_MONTHS = new BigInteger("12");

    public StB276AAccProcessOverdraft convert(DepositArrangement depositArrangement, RequestHeader requestHeader) {

        RetrieveAccProcessOverdraftRequestBuilder builder = new RetrieveAccProcessOverdraftRequestBuilder();
        addressDetails.setAddressDetails(depositArrangement.getPrimaryInvolvedParty().getPostalAddress(), builder);

        OverdraftDetails overdraftDetails = depositArrangement.getOverdraftDetails();
        String productType = null;
        BigDecimal amtOverdraftNew = null;
        Boolean isChargingEnabled = false;
        Boolean isBBaseRateLinked = null;
        XMLGregorianCalendar expiryDate = null;
        Map<String, BigDecimal> interestRateMap = new HashMap<>();
        if (overdraftDetails != null) {
            setInterestRateValues(overdraftDetails.getInterestRates(), interestRateMap);
            productType = overdraftDetails.getProductType();
            isChargingEnabled = overdraftDetails.isIsChargingEnabled();
            isBBaseRateLinked = overdraftDetails.isBBaseRateLinked();
            expiryDate = overdraftDetails.getExpiryDate();
            amtOverdraftNew = overdraftDetails.getAmount() != null ? overdraftDetails.getAmount().getAmount() : null;
        }

        builder.amountOverdraftCurrentAndNewAndRevertTo(BigDecimal.ZERO, amtOverdraftNew, null)
                .amtMnthlyFeeAndAmtWaiver(getDefaultValueIfNull(interestRateMap.get(RateType.AMT_MONTHLY_FEE)), interestRateMap.get(RateType.AMT_WAIVER_AMT))
                .asmDecision(true)
                .cctmSessionId(depositArrangement.getCctmSessionId());

        String cidPersId = null;
        String customerId = null;
        if (depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy() != null) {
            cidPersId = depositArrangement.getPrimaryInvolvedParty().getCidPersID();
            customerId = depositArrangement.getPrimaryInvolvedParty().getCustomerIdentifier();
            Individual individual = depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy();
            IndividualName individualName = individual.getIndividualName().get(0);
            builder.nameAndDobAndEmpStatus(individualName.getLastName(), individualName.getFirstName(), individual.getBirthDate(), individual.getEmploymentStatus());
        }

        builder.nOverdraftTermAndPurpose(OVERDRAFT_TERM_IN_MONTHS, ActivateCommonConstant.ApaPcaServiceConstants.OVERDRAFT_PURPOSE_CREATE)
                .overdraftProductType(productType)
                .repaymentSource(ActivateCommonConstant.ApaPcaServiceConstants.REPAYMENT_SOURCE_14)
                .stAccount(ActivateCommonConstant.ApaPcaServiceConstants.HOST_TSB_ACCOUNT, ActivateCommonConstant.ApaPcaServiceConstants.PRODUCT_TYPE_ACCOUNT, depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode(), depositArrangement.getAccountNumber());

        StODRates stODRates = stODRatesFactory.getStODRates(isBBaseRateLinked, expiryDate, interestRateMap);
        builder.stODRatesAndMnthlyChargedEnabled(stODRates, isChargingEnabled).stHeader(getStHeader(requestHeader, cidPersId, customerId));
        return builder.build();
    }

    private StHeader getStHeader(RequestHeader requestHeader, String cidPersId, String customerId) {
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader();
        String contactPointId = headerRetriever.getContactPoint(requestHeader).getContactPointId();
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader);
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(bapiHeader, serviceRequest, contactPointId);
        stHeader.getStpartyObo().setHost("T");
        stHeader.getStpartyObo().setPartyid(String.format("%-30s", cidPersId));
        stHeader.setChanidObo(stHeader.getChanid());
        if (!StringUtils.isEmpty(customerId)) {
            stHeader.getStpartyObo().setOcisid(new BigInteger(customerId));
        }
        return stHeader;
    }


    private void setInterestRateValues(List<Rates> interestRateList, Map<String, BigDecimal> interestRateMap) {
        for (Rates rates : interestRateList) {
            interestRateMap.put(rates.getType(), rates.getValue());
        }
    }

    private BigDecimal getDefaultValueIfNull(BigDecimal rate) {
        return rate == null ? BigDecimal.ZERO : rate;
    }
}