package com.lloydsbanking.salsa.apasa;

import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.Product;
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

public interface ScenarioHelper {
    void clearUp();

    @Transactional
    public String expectChannelIdByContactPointID();

    @Transactional
    public long expectApplicationDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision);

    @Transactional
    public void expectReferenceDataForPAM();

    @Transactional
    public void expectLookUpValues();

    @Transactional
    public void expectLookUpValuesWithInCorrectGroupCodes();

    @Transactional
    ProductArrangement expectApplicationDetailsHavingPamAndRequestData(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg;

    @Transactional
    ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithNiNumberWithoutGuardianDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg;

    @Transactional
    ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithNiNumber(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg;


    @Transactional
    ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithNiNumberWithInterestRemittanceDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg;

    void expectRpcCall(DepositArrangement productArrangement, RequestHeader header) throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg;

    @Transactional
    public void expectRelatedApplicationDetails(Applications applications, ApplicationStatus applicationStatus);

    @Transactional
    ProductArrangement expectApplicationDetailsHavingPamAndRequestData(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg;


    void expectF425Call(RequestHeader header, String requestNo, String sourceSystemCode, String asmDecisionCode, String asmCreditScore);

    void expectF425CallWithError(RequestHeader header, String requestNo, String sourceSystemCode);

    void expectSendCommunicationCall(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType, boolean isFirstCall) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg;

    void expectF061Call(String customerId, RequestHeader header);

    String expectF062Call(Customer customer, RequestHeader header);

    void expectF062CallFails(Customer customer, RequestHeader header);

    void expectScheduleCommunicationCall(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header, String source, String communicationType) throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg;

    void expectSendCommunicationCallWithError(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg;

    void expectX741Call(Integer taskId, ProductArrangement productArrangement, RequestHeader header);

    @Transactional
    public void expectReferralsTeamDetails(String taskType);

    void verifyExpectCalls();

    void expectCreateAccountB675Call(RequestHeader header, ProductArrangement productArrangement);

    void expectCreateAccountB675CallFails(RequestHeader header, ProductArrangement productArrangement);

    void expectE502Call(RequestHeader header, DepositArrangement productArrangement);

    void expectE502CallFails(RequestHeader header, DepositArrangement productArrangement);

    void expectCreateStandingOrderCall(String sortCode, String accNo, String beneficiaryAccountNumber, String beneficiarySortCode, String transactionName, RequestHeader requestHeader, byte severityCode, String cbsAppGroup);

    void expectB766Call(RequestHeader requestHeader, String sortCode);

    void sleep();

    String expectB751CallWithTacver(ProductArrangement productArrangement, RequestHeader header, BigInteger ocisId, String accNo, BigInteger appId, BigInteger appVer);

    void expectSendCommunicationCallForPostFulfilment(ProductArrangement productArrangement, String notificationEmail, RequestHeader header) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg;

    void expectC658Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    void delayF060Invocation(long milliseconds);

    void expectLookUpValuesWithLifeStyleBenefitCode();

    void expectC234Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    void expectF060Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    void expectActivateBenefitCall(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    void expectFATCAUpdateSwitchCall(String channel, boolean switchStatus);

    void expectRecordCustomerDetails(ProductArrangement productArrangement, RequestHeader requestHeader, boolean isCRSSwitch) throws ErrorInfo;

    void expectC241Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    void expectC241CallFails(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    void expectLookUpValuesWithISOCode();

    void expectDurableMediumSwitchCall(String channel, boolean switchStatus);

    boolean expectCRSSwitchCall(String channel, boolean switchStatus);

    void expectRetrieveDocumentContent(ProductArrangement productArrangement, RequestHeader requestHeader) throws com.lloydstsb.schema.documentmanagement.ifw.ErrorInfo, ParseException, DatatypeConfigurationException;

    void expectRecordDocumentContent(ProductArrangement productArrangement, RequestHeader requestHeader) throws ParseException, DatatypeConfigurationException, com.lloydstsb.schema.documentmanagement.ifw.ErrorInfo;

    void expectRetrieveInvolvedPartyDetailsWithError(ProductArrangement productArrangement, RequestHeader requestHeader) throws ErrorInfo;

    void expectRecordInvolvedPartyDetailsWithError(ProductArrangement productArrangement, RequestHeader requestHeader, boolean isCRSSwitch) throws ErrorInfo;

    void expectRecordDocumentContentCallFails(ProductArrangement productArrangement, RequestHeader requestHeader) throws ParseException, DatatypeConfigurationException, com.lloydstsb.schema.documentmanagement.ifw.ErrorInfo;
}
