package com.lloydsbanking.salsa.opapca.service;


import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementInternalServiceErrorMsg;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;

public interface ScenarioHelper {
    public void expectF336Call(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier);

    void expectLookUpValuesWithISOCode();

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

    public void expectF205Call(RequestHeader requestHeader, String scoreResult, List<ReferralCode> referralCodeList, int caseNo) throws ParseException;

    public void expectF205CallForExistingCustomer(RequestHeader requestHeader, String scoreResult, List<ReferralCode> referralCodeList, int caseNo) throws ParseException;

    public void expectLookupListFromGroupCodeAndChannelAndLookUpText(String groupCd, String channel, List<String> lookUpText);

    public void expectLookupListFromChannelAndEvidenceList(String channel);

    public void expectPAMReferenceData();

    public Applications expectRetrieveApplicationFromPAM(Long arrangementId);

    public void expectRpc(RequestHeader requestHeader, int offerType) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg;

    public void expectF062Call(String arrangementType, boolean marketingPrefIndicator, Customer primaryInvolvedParty, RequestHeader requestHeader, String addressStrength, String partyStrength, int reasoncode) throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg;

    public void expectLookupListFromChannelAndGroupCodeList(String channelId, List<String> groupCodeList);

    public void expectDuplicationApplications();


    public void expectEligibility(int caseNo) throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;

    public void expectLookupDataForDuplicateApplication(String channel, String groupCode);

    public long expectChildApplication();

    public int expectIndividualsCreated();

    public int expectPartyCountryAssociationCreated();

    public int expectApplicationsUpdated();

    public int expectApplicationParametersCreated(long applicationId);

    public int expectApplicationFeaturesCreated(long applicationId);

    public int expectPartyApplicationsCreated();

    public void expectX711Call(Customer customer, RequestHeader header) throws OfferProductArrangementInternalServiceErrorMsg;

    public void expectLookupDataForX711(String channel);

    @Transactional
    void expectLookupDataForSira(String channel);

    public void expectLookupDataForX711Decline(String channel);

    public void expectLookupDataForX711Refer(String channel);

    public void expectX711CallEidvRefer(Customer customer, RequestHeader header) throws OfferProductArrangementInternalServiceErrorMsg;

    public String expectChildApplicationForAppStatus();

    public void expectEligibilityCa(String isEligible, RequestHeader requestHeader, boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
    public void expectF336CallWithProductHoldings(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier);
    public void expectEligibilityCaWithCustomerId(String isEligible, RequestHeader requestHeader, boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
    public void expectF062CallWhenBfpoNotPresent(String arrangementType, boolean marketingPrefIndicator, Customer primaryInvolvedParty, RequestHeader requestHeader, String addressStrength, String partyStrength, int reasoncode) throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg;

    void expectSiraCall(BigInteger totalRuleScore,DepositArrangement depositArrangement,RequestHeader requestHeader);

    void expectSiraCallWithError(BigInteger totalRuleScore, DepositArrangement depositArrangement,RequestHeader requestHeader);

    public void expectLookupCallForCoHolding(String brand);
    public void expectEligibilityFailure(RequestHeader requestHeader, boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
    public int expectApplicationsParameterValueCreated();
    public void expectInstructionRulesViewCallforEligibilityFailure(String insMnemonic, String brand);
    public void expectRpcEmptyProductListResponse(RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg ;

        public long expectRelatedapplicationId();

    public void expectF336CallWithValidProductHoldings(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier);

    void expectF447CallWithError(RequestHeader ltb, List<PostalAddress> postalAddressList, Individual isPlayedBy);

    public void expectF336CallWithError(RequestHeader header,String partyIdentifier) ;

    public void expectF061CallWithError(RequestHeader header, String customerIdentifier) ;

    public void expectF061CallWithImproperResponse(RequestHeader header, String s);

    public void expectF204CallWithExternalBusinessError(RequestHeader header, String s, List<ReferralCode> referralCodeList);

    public void expectF204CallWithExternalServiceError(RequestHeader header, String s, List<ReferralCode> referralCodeList);

    public void expectF205CallWithExternalBusinessError(RequestHeader requestHeader, String scoreResult, List<ReferralCode> referralCodeList, int caseNo) throws ParseException;

    public void expectF205CallWithExternalServiceError(RequestHeader requestHeader, String scoreResult, List<ReferralCode> referralCodeList, int caseNo) throws ParseException;

    public void expectF205CallWithInternalServiceError(RequestHeader requestHeader, String scoreResult, List<ReferralCode> referralCodeList, int caseNo) throws ParseException;

    public void expectEligibilityWithException( RequestHeader requestHeader,int exceptionType) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;

    public void expectF336CallWithEmptyAmdEffDt(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier);

    public void expectF061CallWithNoAddressLinePaf(RequestHeader header, String customerIdentifier) ;
}
