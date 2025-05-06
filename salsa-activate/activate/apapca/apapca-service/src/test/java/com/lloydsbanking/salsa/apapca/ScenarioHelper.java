package com.lloydsbanking.salsa.apapca;

import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_communicationmanager.id_communicationeventscheduler.*;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;

public interface ScenarioHelper {

    void clearUp();

    void expectFATCAUpdateSwitchCall(String channel, boolean switchStatus);

    boolean expectCRSSwitchCall(String channel, boolean switchStatus);

    void sleep();

    @Transactional
    public String expectChannelIdByContactPointID();

    @Transactional
    public void expectLookUpValues();

    @Transactional
    public void expectLookUpValuesWithInCorrectGroupCodes();

    BigInteger expectB750Call(RequestHeader requestHeader, ProductArrangement productArrangement);

    @Transactional
    void expectApplicationIdFromReferralsTmsTaskId(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String subStatus, String niNumber);

    @Transactional
    public void expectReferralsTeamDetails(String taskType);

    @Transactional
    public void expectReferenceDataForPAM();

    void expectDurableMediumSwitchCall(String channel, boolean switchStatus);

    @Transactional
    List<ReferralTeams> expectReferralsTeamDetailsForSira(String name);

    void expectB766Call(RequestHeader requestHeader, String sortCode);

    @Transactional
    long expectApplicationDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String subStatus, String niNumber);

    @Transactional
    Applications expectApplicationDetail(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String subStatus, String niNumber, ProductTypes productTypes);

    @Transactional
    ProductArrangement expectApplicationDetailsHavingPamAndRequestData(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productTypes) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg;

    @Transactional
    ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithIntendToSwitch(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg;

    @Transactional
    ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithRelatedAplicationId(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productTypes) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg;

    @Transactional
    ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithoutIbRegistrationDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productTypes) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg;

    String expectE229Call(RequestHeader header, String sortCode);

    void expectE469Call(RequestHeader header, String contactPointID);

    void expectC808Call(String sortCode, String accountNumber, long custID, RequestHeader header);

    void expectC846Call(String prodID, String ccApplicableIndicator, String decisionText, int decisionCode, long custID, RequestHeader header);

    void expectC812Call(String prodID, String ccApplicableIndicator, String sortCode, long reasonCode, int sysCode, long custID, String accountNumber, RequestHeader header);

    void expectC818Call(String prodID, String ccApplicableIndicator, long custId, String sortCode, int sysCode, String accountNo, RequestHeader header);

    void expectC812CallWithSortCode(String prodID, String ccApplicableIndicator, String sortCode, long reasonCode, int sysCode, String accountNumber, long custID, RequestHeader header);

    @Transactional
    public void expectRelatedApplicationDetails(Applications applications, ApplicationStatus applicationStatus);

    void expectF425Call(RequestHeader header, String requestNo, String sourceSystemCode, String asmDecisionCode, String asmCreditScore);

    void expectF425CallWithError(RequestHeader header, String requestNo, String sourceSystemCode);

    void expectX741Call(Integer taskId, ProductArrangement productArrangement, RequestHeader header);

    void expectSendCommunicationCall(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header, String source, String communicationType, boolean isFirstCall) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg;

    void expectScheduleCommunicationCall(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header, String source, String communicationType) throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg;

    void expectSendCommunicationCall(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header, boolean isFirstCall) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg;

    void expectSendCommunicationCallWithError(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg;

    void verifyExpectCalls();

    @Transactional
    void expectLookUpValuesWithISOCode();

    @Transactional
    void expectLookUpValuesWithoutISOCode();

    String expectRetrieveProductCondition(DepositArrangement productArrangement,RequestHeader header) throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg;

    String expectCreateAccountB675Call(RequestHeader header, ProductArrangement productArrangement);

    void expectCreateAccountB675CallWithError(RequestHeader header, ProductArrangement productArrangement);

    void expectPegaCall(DepositArrangement depositArrangement, RequestHeader header);

    void expectPegaCallWithError(DepositArrangement depositArrangement, RequestHeader header);

    void expectB751CallWithAppIdAndAppVer(ProductArrangement productArrangement, RequestHeader header, BigInteger ocisId, String accNo, BigInteger appId, BigInteger appVer);

    void expectB751CallWithTacver(ProductArrangement productArrangement, RequestHeader header, BigInteger ocisId, String accNo, BigInteger appId, BigInteger appVer);

    void expectSendCommunicationCallForPostFulfilment(ProductArrangement productArrangement, String notificationEmail, RequestHeader header) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg;

    void expectE226AddsOverdraftDetails(long arrangementId, String channel, RequestHeader header);

    void expectE226AddsOverdraftDetailWithSaving(long arrangementId, String channel, RequestHeader header);


    void expectE226AddsOverdraftDetail(long arrangementId, String channel, RequestHeader header);

    void expectE226Call(DepositArrangement depositArrangement, String channel, RequestHeader header);

    void expectE226CallFails(long arrangementId, String channel, RequestHeader header);

    void expectC658Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    void expectC234Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    void expectC241Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    void expectB276Call(DepositArrangement depositArrangement, RequestHeader header);

    void expectC808CallWithSubStatus1025(String sortCode, String accountNumber, long custID, RequestHeader header);

    void delayF061Invocation(long milliseconds);

    public void expectC812CallWithSubstatus2015(String prodID, String ccApplicableIndicator, String sortCode, long reasonCode, int sysCode, long custID, String accountNumber, RequestHeader header);

    void expectE226AddsOverdraftDetailInSecondScenario(long arrangementId, String channel, RequestHeader header);

    void expectB276CallBySubstatusNullCallFails(DepositArrangement productArrangement, RequestHeader header);

    void expectF061Call(String customerId, RequestHeader header);

    void expectF060Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    void expectActivateBenefitCall(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    String expectF062Call(Customer customer, RequestHeader header);

    void expectH071Call(Customer customer, RequestHeader header);

    void expectRetrieveEncryptData(List<String> creditCardNumbers, String encryptionKey, RequestHeader requestHeader);

    void expectF062CallFails(Customer customer, RequestHeader header);

    void expectLookUpValuesWithLifeStyleBenefitCode();

    void expectLookUpValuesWithIntendToSwitch();

    void expectLookUpValuesWithBenefitMessages();

    @Transactional
    public void expectLookUpValuesForEvidenceAndPurposeData();

    void delayC808Invocation(long milliseconds);

    void expectRecordCustomerDetails(ProductArrangement productArrangement, RequestHeader requestHeader, boolean isCRSSwitch) throws ErrorInfo;

    void expectRetrieveDocumentContent(ProductArrangement productArrangement, RequestHeader requestHeader) throws com.lloydstsb.schema.documentmanagement.ifw.ErrorInfo, ParseException, DatatypeConfigurationException;

    void expectRecordDocumentContent(ProductArrangement productArrangement, RequestHeader requestHeader) throws ParseException, DatatypeConfigurationException, com.lloydstsb.schema.documentmanagement.ifw.ErrorInfo;

    void expectRecordDocumentContentCallFails(ProductArrangement productArrangement, RequestHeader requestHeader) throws ParseException, DatatypeConfigurationException, com.lloydstsb.schema.documentmanagement.ifw.ErrorInfo;

    void expectRecordCustomerDetailsWithError(ProductArrangement productArrangement, RequestHeader requestHeader, boolean isCRSSwitch) throws ErrorInfo;

    @Transactional
    void expectLookupDataForSira(String channel);

    void expectSiraCall(BigInteger totalRuleScore, DepositArrangement depositArrangement, RequestHeader requestHeader);
}
