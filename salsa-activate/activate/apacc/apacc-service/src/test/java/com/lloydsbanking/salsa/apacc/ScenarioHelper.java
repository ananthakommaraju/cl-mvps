package com.lloydsbanking.salsa.apacc;


import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Resp;
import lib_sim_bo.businessobjects.*;
import lib_sim_communicationmanager.ia_generatedocument.GenerateDocumentInternalServiceErrorMsg;
import lib_sim_communicationmanager.ia_generatedocument.GenerateDocumentResourceNotAvailableErrorMsg;
import lib_sim_communicationmanager.id_communicationeventscheduler.*;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ScenarioHelper {
    void clearUp();

    @Transactional
    public String expectChannelIdByContactPointID();

    @Transactional
    public long expectApplicationDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String subStatus, String niNumber);

    @Transactional
    public void expectReferenceDataForPAM();

    @Transactional
    public void expectLookUpValues();

    @Transactional
    public void expectLookUpValuesWithInCorrectGroupCodes();

    @Transactional
    ProductArrangement expectApplicationDetailsHavingPamAndRequestData(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productTypes) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg;

    public List<ProductOffer> expectRetrieveProductCondition(Product product, RequestHeader header) throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg;

    void sleep();

    void verifyExpectCalls();

    void expectF425Call(RequestHeader header, String requestNo, String sourceSystemCode, String asmDecisionCode, String asmCreditScore);

    void expectF061Call(String customerId, RequestHeader header);

    void expectF425CallWithError(RequestHeader header, String requestNo, String sourceSystemCode);

    void expectScheduleCommunicationCall(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType) throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg;

    void expectX741Call(Integer taskId, ProductArrangement productArrangement, RequestHeader header);

    String expectF062Call(Customer customer, RequestHeader header);

    void expectF062CallFails(Customer customer, RequestHeader header);

    void expectF251Call(FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, String contactPointId);

    void expectF251CallWithError(FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, String contactPointId);

    void expectSendCommunicationCall(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType, boolean isFirstCall) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg;

    void expectSendCommunicationCallWithError(ProductArrangement productArrangement, String notificationEmail, RequestHeader header) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg;

    @Transactional
    public void expectReferralsTeamDetails(String taskType);

    @Transactional
    void expectLookUpValuesForEvidenceAndPurposeData();

    @Transactional
    void expectLookUpValuesForInvalidEvidenceAndPurposeData();

    F241Resp expectCardAccountV1Call(ProductArrangement productArrangement, RequestHeader header, String severityCode);

    public void expectF259Call(FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, String severityCode);

    @Transactional
    public void expectRelatedApplicationDetails(Applications applications, ApplicationStatus applicationStatus);

    public String expectCardAccountCall(ProductArrangement productArrangement, RequestHeader requestHeader, String severityCode);

    public void expectB748Call(RequestHeader requestHeader, int errorNo);

    void expectGenerateDocumentCall(ProductArrangement productArrangement, List<ProductOffer> productOfferList, RequestHeader header) throws GenerateDocumentResourceNotAvailableErrorMsg, GenerateDocumentInternalServiceErrorMsg;

    String expectB751Call(ProductArrangement productArrangement, RequestHeader header, int tacver);

    void delayB751Call(long milliseconds);

    void expectRetrieveEncryptDataForBT(List<BalanceTransfer> balanceTransfers, String encryptionKey, RequestHeader requestHeader);

    void expectRetrieveEncryptData(FinanceServiceArrangement financeServiceArrangement, String encryptionKey, RequestHeader requestHeader);

    void expectF060Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode);

    void delayF060Invocation(long milliseconds);

    void delayEncryptDataInvocation(long milliseconds);
}
