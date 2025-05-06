package com.lloydsbanking.salsa.apapca.service.fulfil.converter;

import com.lloydsbanking.salsa.soap.pega.objects.CreateCaseRequestType;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.*;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_gmo.messages.SOAPHeader;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class CreateCaseRequestFactory {
    public static final Logger LOGGER = Logger.getLogger(CreateCaseRequestFactory.class);
    public static final String PCA_ONLINE = "PCAOnline";
    public static final String MODE_ONLINE = "Online";
    public static final String COUNTRY = "GB";

    @Autowired
    CreateCaseAccountFactory createCaseAccountFactory;

    public CreateCaseRequestType convert(DepositArrangement depositArrangement, RequestHeader header) {
        CreateCaseRequestType request = new CreateCaseRequestType();
        CreateCasePayloadRequestType payloadRequest = new CreateCasePayloadRequestType();
        payloadRequest.setRequestResponseCorrelationId(depositArrangement.getArrangementId());
        InitiateSwitchInType initiateSwitchIn = new InitiateSwitchInType();
        initiateSwitchIn.setRequestedByUserName(getUserId(header));
        initiateSwitchIn.setRequestedBySystemId(PCA_ONLINE);
        initiateSwitchIn.getSwitchDetails().add(getSwitchDetails(depositArrangement, header.getChannelId()));
        payloadRequest.setInitiateSwitchIn(initiateSwitchIn);
        request.setPayload(payloadRequest);
        return request;
    }

    private SwitchDetailsType getSwitchDetails(DepositArrangement depositArrangement, String channelId) {
        SwitchDetailsType switchDetails = new SwitchDetailsType();
        switchDetails.setSwitchScenario(SwitchScenarioType.IAS_RETAIL_CURRENT_FULL);
        switchDetails.setSwitchType(SwitchTypeType.FULL);
        if (depositArrangement.getAccountSwitchingDetails() != null) {
            DirectDebit accountSwitchingDetails = depositArrangement.getAccountSwitchingDetails();
            if (accountSwitchingDetails.getSwitchDate() != null) {
                switchDetails.setSwitchDate(accountSwitchingDetails.getSwitchDate());
            }
        }
        switchDetails.setNewAccount(createCaseAccountFactory.createNewAccount(depositArrangement, getPostalAddress(depositArrangement.getPrimaryInvolvedParty()), channelId));
        switchDetails.setOldAccount(createCaseAccountFactory.createOldAccountType(depositArrangement.getAccountSwitchingDetails()));
        switchDetails.setCustomerInterviewDetails(createCustomerInterviewDetails(depositArrangement.getAccountSwitchingDetails(), depositArrangement.getFinancialInstitution()));
        return switchDetails;
    }


    private PartyPostalAddressType getPostalAddress(Customer primaryInvolvedParty) {
        PartyPostalAddressType postalAddress = new PartyPostalAddressType();
        if (!CollectionUtils.isEmpty(primaryInvolvedParty.getPostalAddress()) && primaryInvolvedParty.getPostalAddress().get(0).isIsBFPOAddress() != null) {
            if (!primaryInvolvedParty.getPostalAddress().get(0).isIsBFPOAddress()) {
                postalAddress.setAddressType(AddressTypeType.HOME);
                postalAddress.setPartyPostalAddress(getAddressType(postalAddress, primaryInvolvedParty.getPostalAddress().get(0)));
            } else {
                postalAddress.setAddressType(AddressTypeType.BFPO);
            }
        }
        return postalAddress;
    }

    private AddressType getAddressType(PartyPostalAddressType postalAddress, PostalAddress postalAddressReq) {
        AddressType addressType = new AddressType();
        addressType.setCountry(COUNTRY);
        checkStructuresAddress(postalAddressReq, addressType);
        checkUnstructuredAddress(postalAddress, postalAddressReq, addressType);
        return addressType;
    }

    private void checkStructuresAddress(PostalAddress postalAddressReq, AddressType addressType) {
        if (postalAddressReq.getStructuredAddress() != null) {
            StructuredAddress structuredAddress = postalAddressReq.getStructuredAddress();
            if (!StringUtils.isEmpty(structuredAddress.getBuildingNumber()) && !StringUtils.isEmpty(structuredAddress.getHouseNumber())) {
                addressType.setHouseNameBuildingNumber(structuredAddress.getBuildingNumber().concat(structuredAddress.getHouseNumber()));
            }
            if (!StringUtils.isEmpty(structuredAddress.getPostCodeOut()) && !StringUtils.isEmpty(structuredAddress.getPostCodeIn())) {
                addressType.setPostCode(structuredAddress.getPostCodeOut().concat("").concat(structuredAddress.getPostCodeIn()));
            }
            if (!CollectionUtils.isEmpty(structuredAddress.getAddressLinePAFData())) {
                addressType.getAddressLine().addAll(structuredAddress.getAddressLinePAFData());
            }
        }
    }

    private void checkUnstructuredAddress(PartyPostalAddressType postalAddress, PostalAddress postalAddressReq, AddressType addressType) {
        if (postalAddressReq.getUnstructuredAddress() != null) {
            UnstructuredAddress unstructuredAddress = postalAddressReq.getUnstructuredAddress();
            if (!StringUtils.isEmpty(unstructuredAddress.getAddressLine1())) {
                addressType.setHouseNameBuildingNumber(unstructuredAddress.getAddressLine1());
            }
            if (!StringUtils.isEmpty(unstructuredAddress.getAddressLine2())) {
                addressType.setHouseNameBuildingNumber(unstructuredAddress.getAddressLine2() + addressType.getHouseNameBuildingNumber());
            }
            if (!StringUtils.isEmpty(unstructuredAddress.getAddressLine3())) {
                addressType.setHouseNameBuildingNumber(unstructuredAddress.getAddressLine3() + addressType.getHouseNameBuildingNumber());
            }
            addAddressLineIfNotEmpty(unstructuredAddress.getAddressLine5(), addressType.getAddressLine());
            addAddressLineIfNotEmpty(unstructuredAddress.getAddressLine6(), addressType.getAddressLine());
            addressType.setCountry(unstructuredAddress.getAddressLine7());

            if (!StringUtils.isEmpty(unstructuredAddress.getPostCode())) {
                addressType.setPostCode(unstructuredAddress.getPostCode());
            }
        }
    }

    private void addAddressLineIfNotEmpty(String addressLine, List<String> addressLineList) {
        if (!StringUtils.isEmpty(addressLine)) {
            addressLineList.add(addressLine);
        }
    }

    private CustomerInterviewDetailsType createCustomerInterviewDetails(DirectDebit accountSwitchingDetails, Organisation financialInstitution) {
        CustomerInterviewDetailsType customerInterviewDetails = new CustomerInterviewDetailsType();
        if (accountSwitchingDetails != null && !StringUtils.isEmpty(accountSwitchingDetails.getOverdraftHeldIndicator())) {
            customerInterviewDetails.setSwitchersOverdraftOfferAgreedIndicator(Boolean.valueOf(accountSwitchingDetails.getOverdraftHeldIndicator()));
        }
        if (financialInstitution != null && !CollectionUtils.isEmpty(financialInstitution.getHasOrganisationUnits())) {
            if (!StringUtils.isEmpty(financialInstitution.getHasOrganisationUnits().get(0).getSortCode())) {
                customerInterviewDetails.setBranchSortCode(financialInstitution.getHasOrganisationUnits().get(0).getSortCode());
            }
        }
        customerInterviewDetails.setOldAccountProductCategory(AccountProductCategoryType.CURRENT);
        customerInterviewDetails.setOldAccountSoleOrJoint(SoleOrJointType.SOLE);
        customerInterviewDetails.setChannelId(MODE_ONLINE);
        customerInterviewDetails.setStartedIn(MODE_ONLINE);
        return customerInterviewDetails;
    }

    private String getUserId(RequestHeader header) {
        List<SOAPHeader> soapHeaderList = header.getLloydsHeaders();
        String userId = "";
        for (SOAPHeader soapHeader : soapHeaderList) {
            if (soapHeader.getName().equalsIgnoreCase("bapiInformation")) {
                BapiInformation bapiInfo = (BapiInformation) soapHeader.getValue();
                if (bapiInfo != null && bapiInfo.getBAPIHeader() != null
                        && bapiInfo.getBAPIHeader().getStpartyObo() != null) {
                    if (bapiInfo.getBAPIHeader().getStpartyObo().getPartyid() != null) {
                        userId = bapiInfo.getBAPIHeader().getStpartyObo().getPartyid();
                    }
                }
                break;
            }
        }
        return userId;
    }
}