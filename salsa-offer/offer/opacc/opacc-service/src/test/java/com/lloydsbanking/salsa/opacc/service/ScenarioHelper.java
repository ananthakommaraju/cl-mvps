package com.lloydsbanking.salsa.opacc.service;


import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.ReferralCode;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementInternalServiceErrorMsg;

import java.text.ParseException;
import java.util.List;

public interface ScenarioHelper {
    public void expectF336Call(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier);

    public void expectF447Call(RequestHeader header, List<PostalAddress> postalAddresses, Individual isPlayedBy);

    public void expectF061Call(RequestHeader header, String customerIdentifier);

    public void expectGetChannelIdFromContactPointId(String channel);

    public void expectLookupDataForLegalEntity(String channel, String groupCode);

    public void clearUp();

    public void expectF061CallWithoutEvidenceData(RequestHeader header, String s);

    public void expectF061CallWithoutEvidenceDataAndPersonalData(RequestHeader header, String customerIdentifier) ;

    public void expectInstructionHierarchyCall(String insMnemonic, String brand);

    public void expectInstructionRulesViewCall(String insMnemonic, String brand);

    public void expectPrdDbCalls();

    public void expectF424Call(RequestHeader requestHeader, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList);

    public void expectLookupListFromGroupCodeAndChannelAndLookUpText(String groupCd, String channel, List<String> lookUpText);

    void expectEncryptDataServiceCall(RequestHeader requestHeader, String memorableInfo);

    void expectLookupListtFromChannelAndGroupCodeList(String groupCd, String channel);

    public void expectPAMReferenceData();

    public void expectSavePromotionPartyExtSystemsInPamDb(String extId);

    public void expectDuplicationApplications();

    public void expectDuplicationApplicationsWithASMDecline();

    public void expectLookupDataForDuplicateApplication(String channel, String groupCode);

    public int expectApplicationsCreated();

    public void expectEligibility(String isEligible, RequestHeader requestHeader) throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg;

    public int expectPartyApplicationsCreated();

    public int expectIndividualsCreated();

    public int expectStreetAddressesCreated();

    public void expectEligibilityForNewCustomer(String isEligible, RequestHeader requestHeader) throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg;

    public void expectSwitchDtoFromSwitchName(String switchName, String channelId, int boxId, String value);

    public void expectF336CallWithProductHoldings(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier);

    public int expectPromoPartyApplicationsCreated();

    public void expectProductFeatureFromProductId(Long productId);

    public Applications expectRetrieveApplicationFromPAM(Long arrangementId);

    public void expectExternalSystemProductsPrdData(String esCode, String externalSysProId);

    public void expectProductEligibilityRulesPrdData(String petCode, String appliedProId, String existingProId);

    public void expectF424CallWithAddressDetailsWithResolutionNotPresent(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList);

    public void expectX711Call(Customer customer, RequestHeader header) throws OfferProductArrangementInternalServiceErrorMsg;

    public void expectLookupDataForX711(String channel);

    public void expectLookupDataForX711Decline(String channel);

    public void expectLookupDataForX711Refer(String channel);

    public void expectX711CallEidvRefer(Customer customer, RequestHeader header) throws OfferProductArrangementInternalServiceErrorMsg;

    public void expectF062Call(String arrangementType, boolean marketingPrefIndicator, Customer primaryInvolvedParty, RequestHeader header, String addressStrength, String partyStrength, int reasoncode, String eidvStatus, boolean assessmentEvidNull) throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg;

    public void expectRpc(RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg;

    public void expectRpcWhenApplicationStatusIsUnscored(RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg;

    public void expectProductFeatureFromProductIdForIneligibleOfferedProducts(Long productId);

    public void expectLookupListFromChannelAndEvidenceList(String channel);

    public void expectF062CallSuccessful(String arrangementType, boolean marketingPrefIndicator, Customer primaryInvolvedParty, RequestHeader header, String addressStrength, String partyStrength, int reasoncode, String eidvStatus, boolean assessmentEvidNull) throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg;

    public void expectLookupCallForCoHolding(String brand);

    public void expectF447CallForNewCustomer(RequestHeader header, List<PostalAddress> postalAddressList, Individual isPlayedBy);

    public void expectLookupDataForEvaluateStrength(String ltb);

    public void clearWpsCache();

    public long expectRelatedapplicationId();

    public void expectRpcProducts(RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg ;

    void expectRpcProductsForUnscored(RequestHeader header) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg ;

    public void expectF424CallWithExternalServiceError(RequestHeader requestHeader, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList);

    public void expectF424CallWithExternalBusinessError(RequestHeader requestHeader, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList);
}

