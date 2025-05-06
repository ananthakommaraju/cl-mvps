package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;

import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062RequestBuilder;
import com.lloydsbanking.salsa.offer.EIDVStatus;
import com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate.EvaluateAddressUpdData;
import com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate.EvaluatePersonalUpdData;
import com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate.EvaluateStrength;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.*;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

public class CreateOcisCustomerRequestFactory {
    private static final Logger LOGGER = Logger.getLogger(CreateOcisCustomerRequestFactory.class);

    private static final String ADDRESS_TYPE_CURRENT = "CURRENT";

    private static final String INTERNAL_SERVICE_ERROR_CODE = "820001";

    @Autowired
    EvaluateAddressUpdData evaluateAddressUpdData;

    @Autowired
    EvaluatePersonalUpdData evaluatePersonalUpdData;

    @Autowired
    PhoneUpdDataFactory phoneUpdDataFactory;

    @Autowired
    EmployerAddressDataFactory employerAddressDataFactory;

    @Autowired
    PartyNonCoreUpdDataFactory partyNonCoreUpdDataFactory;

    @Autowired
    EvaluateStrength evaluateStrength;

    @Autowired
    ExceptionUtility exceptionUtility;

    public F062Req convert(String arrangementType, Customer primaryInvolvedParty, boolean marketingPref, String channelId) throws ParseException, InternalServiceErrorMsg {
        AddressUpdDataType addressUpdDataType = new AddressUpdDataType();
        F062RequestBuilder requestBuilder = new F062RequestBuilder();
        F062Req request = requestBuilder.defaults().build();
        if (!StringUtils.isEmpty(primaryInvolvedParty.getCustomerIdentifier())) {
            request.setPartyId(Long.valueOf(primaryInvolvedParty.getCustomerIdentifier()));
        }
        request.setPartyUpdData(new PartyUpdDataType());
        request.getPartyUpdData().setPersonalUpdData(evaluatePersonalUpdData.generatePersonalUpdData(arrangementType, primaryInvolvedParty, marketingPref));
        request.getPartyUpdData().getPhoneUpdData().addAll(phoneUpdDataFactory.generatePhoneUpdData(primaryInvolvedParty.getTelephoneNumber()));
        request.getPartyUpdData().setPartyNonCoreUpdData(partyNonCoreUpdDataFactory.generatePartyNonCoreUpdData(primaryInvolvedParty.getIsPlayedBy()));

        evaluateAddressUpdData.generateAddressUpdData(primaryInvolvedParty.getPostalAddress(), addressUpdDataType);
        setStructuredUnstructuredAddress(primaryInvolvedParty.getPostalAddress(), addressUpdDataType);
        //Address setting done here to avoid cyclic dependency if same is moved in EvaluateAddressUpdData
        request.getPartyUpdData().setAddressUpdData(addressUpdDataType);

        if (null != primaryInvolvedParty.getIsPlayedBy().getCurrentEmployer()) {
            if (null != primaryInvolvedParty.getIsPlayedBy().getCurrentEmployer().getName()) {
                request.getPartyUpdData().setKYCNonCorePartyUpdData(new KYCNonCorePartyUpdDataType());
                request.getPartyUpdData().getKYCNonCorePartyUpdData().setEmployerNm(primaryInvolvedParty.getIsPlayedBy().getCurrentEmployer().getName());
            }
            if (!org.springframework.util.CollectionUtils.isEmpty(primaryInvolvedParty.getIsPlayedBy().getCurrentEmployer().getHasPostalAddress())) {
                request.getPartyUpdData()
                        .setEmployersAddrUpdData(employerAddressDataFactory.generateEmployerAddress(primaryInvolvedParty.getIsPlayedBy()
                                .getCurrentEmployer()
                                .getHasPostalAddress()
                                .get(0)
                                .getUnstructuredAddress()));
            }
        }
        if (EIDVStatus.ACCEPT.getValue().equals(primaryInvolvedParty.getCustomerScore().get(0).getScoreResult())) {
            try {
                request.getPartyUpdData()
                        .setEvidenceUpdData(evaluateStrength.fetchAddressAndPartyEvidenceAndPurposeCode(primaryInvolvedParty.getCustomerScore().get(0), channelId));
            } catch (DataNotAvailableErrorMsg errorMsg) {
                LOGGER.error("Returning DataNotAvailableErrorMsg while fetching Party Evidence and Purpose code from PAM: ", errorMsg);
                throw exceptionUtility.internalServiceError(INTERNAL_SERVICE_ERROR_CODE, errorMsg.getMessage());
            }
        }
        if (null != primaryInvolvedParty.getIsPlayedBy().getNationality()) {
            request.getPartyUpdData().setKYCPartyUpdData(new KYCPartyUpdDataType());
            request.getPartyUpdData().getKYCPartyUpdData().setFrstNtn(new FrstNtnType());
            request.getPartyUpdData().getKYCPartyUpdData().getFrstNtn().setFirstNationltyCd(primaryInvolvedParty.getIsPlayedBy().getNationality());
        }
        return request;
    }

    private void setStructuredUnstructuredAddress(List<PostalAddress> postalAddressList, AddressUpdDataType addressUpdDataType) {
        for (PostalAddress postalAddress : postalAddressList) {
            if (ADDRESS_TYPE_CURRENT.equalsIgnoreCase(postalAddress.getStatusCode())) {
                if (null != postalAddress.getUnstructuredAddress()) {
                    addressUpdDataType.setUnstructuredAddress(new UnstructuredAddressFactory().generateUnstructuredAddress(postalAddress.getUnstructuredAddress(), postalAddress.isIsBFPOAddress()));
                }
                if (null != postalAddress.getStructuredAddress()) {
                    addressUpdDataType.setStructuredAddress(new StructuredAddressFactory().generateStructuredAddress(postalAddress.getStructuredAddress()));
                }
            }
        }
    }
}
