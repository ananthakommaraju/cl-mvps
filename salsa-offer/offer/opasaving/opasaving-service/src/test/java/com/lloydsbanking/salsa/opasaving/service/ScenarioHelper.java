package com.lloydsbanking.salsa.opasaving.service;

import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementInternalServiceErrorMsg;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.List;

public interface ScenarioHelper {
    public void expectF336Call(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier);

    public void expectF336CallForAccept(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier);

    public void expectF447Call(RequestHeader header, List<PostalAddress> postalAddresses, Individual isPlayedBy);

    public void expectF447CallForNewCustomer(RequestHeader header, List<PostalAddress> postalAddresses, Individual isPlayedBy);

    public void expectF061Call(RequestHeader header, String customerIdentifier);

    public void expectGetChannelIdFromContactPointId(String channel);

    public void expectLookupDataForLegalEntity(String channel, String groupCode);

    public void clearUp();

    public void expectF061CallWithoutEvidenceData(RequestHeader header, String s);

    public void expectInstructionHierarchyCall(String insMnemonic, String brand);

    public void expectInstructionRulesViewCall(String insMnemonic, String brand);

    public void expectPrdDbCalls();

    public void expectF204Call(RequestHeader requestHeader, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) throws ParseException, DatatypeConfigurationException;

    public void expectF204CallForExistingCustomers(RequestHeader requestHeader, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList);

    public void expectLookupListFromGroupCodeAndChannelAndLookUpText(String groupCd, String channel, List<String> lookUpText);

    public void expectLookupListFromChannelAndEvidenceList(String channel);

    public void expectPAMReferenceData();

    /*public void expectRpc(RequestHeader requestHeader, int offerType) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg;*/

    public void expectF062Call(String arrangementType, boolean marketingPrefIndicator, Customer primaryInvolvedParty, RequestHeader requestHeader, String addressStrength, String partyStrength, int reasoncode) throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg;

    public void expectEligibility(String isEligible, RequestHeader requestHeader, boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;

    public void expectX711Call(Customer customer, RequestHeader header) throws OfferProductArrangementInternalServiceErrorMsg;

    public void expectLookupDataForX711(String channel);

    public void expectLookupDataForX711Decline(String channel);

    public void expectLookupDataForX711Refer(String channel);

    public void expectX711CallEidvRefer(Customer customer, RequestHeader header) throws OfferProductArrangementInternalServiceErrorMsg;

    public void expectF336CallWithProductHoldings(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier);

    public void expectLookupDataForDuplicateApplication(String channel, String groupCode);

    public void expectLookupListFromChannelAndGroupCodeList(String channelId, List<String> groupCodeList);

    public void expectDuplicationApplications(String appStatus, String appDescription, String productId, String productName);

    public java.util.HashMap<String, Long> expectChildApplication();

    public int expectApplicationsUpdated();

    public int expectApplicationParametersCreated(long applicationId);

    public int expectApplicationFeaturesCreated(long applicationId);

    public int expectPartyApplicationsCreated();

    public int expectIndividualsCreated();

    public void expectSwitchValueFromSwitchName(String switchName, String channelId, int boxId, String value);

    public void expectRpc(ProductArrangement productArrangement, RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg;

    public void expectDcpc(lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResp, ProductArrangement productArrangement, RequestHeader header) throws InternalServiceErrorMsg;

    public int expectPartyCountryAssociationCreated();

    public void expectLookupCallForCoHolding(String channel);

    public void clearWpsCache();

    public long expectRelatedApplicationId();

    public void expectInstructionRulesViewCallForDecline(String insMnemonic, String brand);

    public void expectEligibilityFailure(RequestHeader header,  boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;

    public void expectEligibilityFailureWhenCustomerYoungerThan200(RequestHeader requestHeader, boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg ;

    public void expectF447CallWithError(RequestHeader ltb, List<PostalAddress> postalAddressList, Individual isPlayedBy);

    public void expectF336CallWithError(RequestHeader ltb, int i, int i1, String s);
}
