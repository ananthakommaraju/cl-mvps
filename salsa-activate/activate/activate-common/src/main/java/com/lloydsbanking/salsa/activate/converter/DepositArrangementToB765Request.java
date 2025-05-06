package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.account.client.b765.CreateAccountRequestBuilder;
import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.account.StAddress;
import com.lloydsbanking.salsa.soap.fs.account.StCBSCustDtls;
import com.lloydsbanking.salsa.soap.fs.account.StHeader;
import com.lloydstsb.ib.wsbridge.account.StB765AAccCreateAccount;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DepositArrangementToB765Request {
    public static final String STATUS_CODE = "CURRENT";
    public static final String INS_MNEMONIC_CODE = "P_STUDENT";
    public static final String TARIFF_CODE = "TRF";
    public static final String SYS_CODE = "00004";
    public static final String EXP_MONTHLY_DEPOSIT_AMOUNT = "EXP_MONTHLY_DEPOSIT_AMOUNT";
    @Autowired
    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    ObtainAddressProductAccountAndTariff obtainAddressProductAccountAndTariff;
    @Autowired
    ObtainAddressProductAccountAndTariffAndPprIdForSA obtainAddressProductAccountAndTariffAndPprIdForSA;

    public StB765AAccCreateAccount getCreateAccountRequest(RequestHeader header, DepositArrangement depositArrangement, Product product, Map<String, String> accountPurposeMap) {
        String arrangementType = depositArrangement.getArrangementType();
        CreateAccountRequestBuilder createAccountRequestBuilder = new CreateAccountRequestBuilder();
        String sortCode = null;
        Customer customer = depositArrangement.getPrimaryInvolvedParty();
        if (!ArrangementType.SAVINGS.getValue().equalsIgnoreCase(arrangementType)) {
            createAccountRequestBuilder.accountNumber(depositArrangement.getAccountNumber());
            if (depositArrangement.getFinancialInstitution() != null && depositArrangement.getFinancialInstitution().getHasOrganisationUnits() != null) {
                sortCode = depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
                createAccountRequestBuilder.sortCode(sortCode);
            }
        }
        createAccountRequestBuilder.accountPurposeCode(accountPurposeMap.get(depositArrangement.getAccountPurpose()));
        createAccountRequestBuilder.stHeader(getStHeader(arrangementType, header, depositArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()));
        createAccountRequestBuilder.stpartyObo(customer.getCustomerIdentifier(), customer.getCidPersID()).defaults();
        List<String> productNumberAndAccountType = getProductDetails(arrangementType, product, sortCode);
        if (!productNumberAndAccountType.isEmpty()) {
            createAccountRequestBuilder.accountTypeAndProductNumber(new BigInteger(productNumberAndAccountType.get(0)), new BigInteger(productNumberAndAccountType.get(1)));
        }
        if (ArrangementType.SAVINGS.getValue().equalsIgnoreCase(arrangementType) && getTariffSA(depositArrangement, getProductOptionMap(product)) != null) {
            createAccountRequestBuilder.tariff(BigInteger.valueOf(Long.parseLong(getTariffSA(depositArrangement, getProductOptionMap(product)))));
        } else if (getTariff(product, depositArrangement.getPrimaryInvolvedParty()) != null) {
            createAccountRequestBuilder.tariff(BigInteger.valueOf(Long.parseLong(getTariff(product, depositArrangement.getPrimaryInvolvedParty()))));
        }
        StB765AAccCreateAccount createAccountRequest = createAccountRequestBuilder.build();
        setRequestField(customer, createAccountRequest, depositArrangement, arrangementType);
        return createAccountRequest;
    }

    private void setRequestField(Customer customer, StB765AAccCreateAccount createAccountRequest, DepositArrangement depositArrangement, String arrangementType) {
        createAccountRequest.setStCBScutomerdetails(getCustomerDetails(customer.getIsPlayedBy().getIndividualName(), customer.getPostalAddress()));
        createAccountRequest.setAmtMnthlyDeposit(getAmtMonthlyDeposit(depositArrangement.getConditions()));
        if (ArrangementType.SAVINGS.getValue().equalsIgnoreCase(arrangementType)) {
            createAccountRequest.setPprId(obtainAddressProductAccountAndTariffAndPprIdForSA.getPprID(depositArrangement));
        }
    }

    private String getTariffSA(DepositArrangement depositArrangement, Map<String, String> productOptionMap) {
        return obtainAddressProductAccountAndTariffAndPprIdForSA.getTariffSA(depositArrangement, productOptionMap);
    }

    private StCBSCustDtls getCustomerDetails(List<IndividualName> individualNames, List<PostalAddress> postalAddressList) {
        StCBSCustDtls custDetails = new StCBSCustDtls();
        custDetails.setSalutation(individualNames.get(0).getPrefixTitle());
        custDetails.setFirstname(individualNames.get(0).getFirstName());
        custDetails.setSurname(individualNames.get(0).getLastName());
        if (!StringUtils.isEmpty(individualNames.get(0).getMiddleNames())) {
            if (individualNames.get(0).getMiddleNames().get(0) != null && individualNames.get(0).getMiddleNames().get(0).length() > 1) {
                custDetails.setSecondinitial(individualNames.get(0).getMiddleNames().get(0).substring(0, 1));
            }
        }
        custDetails.setStaddress(getAddress(postalAddressList));
        return custDetails;
    }

    private StAddress getAddress(List<PostalAddress> postalAddresses) {
        StAddress b765Address = null;
        for (PostalAddress postalAddress : postalAddresses) {
            if (STATUS_CODE.equals(postalAddress.getStatusCode())) {
                if (postalAddress.getStructuredAddress() != null) {
                    b765Address = obtainAddressProductAccountAndTariff.getStructureAddress(postalAddress.getStructuredAddress());
                } else if (postalAddress.getUnstructuredAddress() != null) {
                    b765Address = obtainAddressProductAccountAndTariff.getUnstructuredAddress(postalAddress.getUnstructuredAddress());
                }
            }
        }
        return b765Address;
    }

    private List<String> getProductDetails(String arrangementType, Product product, String sortCode) {
        List<String> productDetails = new ArrayList<>();
        if (product != null && product.getExternalSystemProductIdentifier() != null) {
            for (ExtSysProdIdentifier extSysProdIdentifier : product.getExternalSystemProductIdentifier()) {
                if (extSysProdIdentifier != null && SYS_CODE.equalsIgnoreCase(extSysProdIdentifier.getSystemCode()) && extSysProdIdentifier.getProductIdentifier() != null) {
                    productDetails = obtainAddressProductAccountAndTariff.getProdAcc(sortCode, extSysProdIdentifier.getProductIdentifier(), arrangementType);
                }
            }
        }
        return productDetails;
    }

    private String getTariff(Product product, Customer customer) {
        String tariffKey = null;
        if (product != null) {
            if (INS_MNEMONIC_CODE.equalsIgnoreCase(product.getInstructionDetails().getInstructionMnemonic()) && customer.getIsPlayedBy().getCurrentYearOfStudy() != null) {
                tariffKey = obtainAddressProductAccountAndTariff.getAccountTariff(customer.getIsPlayedBy().getCurrentYearOfStudy().intValue());
            } else {
                tariffKey = TARIFF_CODE;
            }
        }
        return tariffKey != null ? getProductOptionMap(product).get(tariffKey) : null;
    }

    private Map<String, String> getProductOptionMap(Product product) {
        Map<String, String> optionMap = new HashMap<>();
        for (ProductOptions productOptions : product.getProductoptions()) {
            if (productOptions.getOptionsType() != null && productOptions.getOptionsValue() != null) {
                optionMap.put(productOptions.getOptionsType(), productOptions.getOptionsValue());
            }
        }
        return optionMap;
    }

    private BigDecimal getAmtMonthlyDeposit(List<RuleCondition> ruleConditionList) {
        BigDecimal amount = null;
        for (RuleCondition condition : ruleConditionList) {
            if (condition.getValue() != null && EXP_MONTHLY_DEPOSIT_AMOUNT.equalsIgnoreCase(condition.getName())) {
                amount = condition.getValue().getAmount();
                break;
            }
        }
        return amount;
    }

    private StHeader getStHeader(String arrangementType, RequestHeader requestHeader, String customerId) {
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader();
        String contactPointId = headerRetriever.getContactPoint(requestHeader).getContactPointId();
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader);
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(bapiHeader, serviceRequest, contactPointId);
        if (!StringUtils.isEmpty(customerId)) {
            stHeader.getStpartyObo().setOcisid(new BigInteger(customerId));
        }
        if (ArrangementType.SAVINGS.getValue().equalsIgnoreCase(arrangementType)) {
            stHeader.setUseridAuthor(bapiHeader.getUseridAuthor());
            stHeader.getStpartyObo().setPartyid(bapiHeader.getStpartyObo().getPartyid());
        } else {
            stHeader.setUseridAuthor("AAGATEWAY");
            stHeader.getStpartyObo().setPartyid("AAGATEWAY");
        }
        stHeader.getStpartyObo().setHost("I");
        stHeader.setChanidObo(stHeader.getChanid());
        return stHeader;
    }
}