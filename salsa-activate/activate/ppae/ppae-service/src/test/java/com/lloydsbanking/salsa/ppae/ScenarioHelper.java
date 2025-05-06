package com.lloydsbanking.salsa.ppae;


import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.F595Resp;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Resp;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo;
import lib_sim_bo.businessobjects.*;
import lib_sim_communicationmanager.id_communicationeventscheduler.*;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.List;

public interface ScenarioHelper {
    void clearUp();

    void sleep();

    @Transactional
    public String expectContactPointIDdByChannelID();

    void verifyExpectCalls();

    @Transactional

    public void expectApplicationRelatedData(ApplicationStatus applicationStatus, ProductTypes productTypes, Brands brands);

    public void expectReferenceDataForPAM(ApplicationStatus applicationStatus, ProductTypes productTypes, Brands brands);


    List<ExtSysProdIdentifier> expectRetrieveProductCondition(RequestHeader requestHeader, ProductArrangement productArrangement) throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg;

    public void setPPAEBatchSwitch(boolean ppaeBatchSwitch);

    public void expectRpcForOffer(RequestHeader requestHeader, String productMatched) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg;

    public void expectRpcOfferForDecline(RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg;

    @Transactional
    public void expectApplicationDataForPAM(ApplicationStatus applicationStatus, ProductTypes productTypes, Brands brands);

    @Transactional
    public void expectScheduleEvent(String event);

    @Transactional
    public boolean expectUpdatedDao();

    public void expectInstructionRulesViewCall(String insMnemonic, String brand);

    public void expectInstructionHierarchyCall(String insMnemonic, String brand);

    @Transactional
    public void expectLookUpValuesFromPAMToRetrieveLookUpData();

    public void expectLookupListFromGroupCodeAndChannelAndLookUpText(String groupCd, String channel, List<String> lookUpText);

    public void expectLookupListFromGroupCodeAndChannelAndLookUpTextForDuplicate(String groupCd, String channel, List<String> lookUpText);

    public void expectRpcForF205(RequestHeader requestHeader, int offerType) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg;


    public void expectF205Call(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, int caseNo, String error) throws ParseException;

    public void expectEligibilityCa(ProcessPendingArrangementEventRequest request, boolean isBFPOIndicatorPresent) throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;

    void expectRpcCallForBtOffAttribute(ProductArrangement productArrangement, RequestHeader requestHeader, String btOffAttributeValue) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg;

    void expectVerifyCall(BalanceTransfer balanceTransfer, Customer customer, RequestHeader header, int errorCode) throws ErrorInfo, JAXBException;

    void expectIssueInPaymentCall(String btOffAttributeValue, BalanceTransfer balanceTransfer, String sourceCreditCard, RequestHeader header, int errorCode) throws com.lloydstsb.schema.enterprise.lcsm_financialtransactionprocessing.ErrorInfo;

    void expectSendCommunicationCall(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg;

    void expectScheduleCommunicationCall(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType, int configDays) throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg;

    public ProductArrangement expectProductArrangementDetails(String channelId, String applicationId, List<Referral> referralList) throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg;

    public void expectF424Call(RequestHeader requestHeader, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, String error);

    public void expectPrdDbCalls();

    public void expectF204Call(RequestHeader requestHeader, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, String error) throws ParseException, DatatypeConfigurationException;

    public void expectActivateProductCall(ProductArrangement productArrangement, RequestHeader requestHeader) throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg;

    public void expectF263CallForCcaSigned(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest);

    public void expectLookupListFromChannelAndGroupCode();

    public void expectLookupListFromChannelAndGroupCodeList(String channelId);

    public void expectReferralDetails(long taskId, String appId);

    public Q028Resp expectQ028Call(Customer customer, String asmDecision, RequestHeader header, int errorCode);

    public void expectB233Call(Q028Resp q028Resp, RequestHeader header);

    public void expectF263CallForCcaPending(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest);

    public F595Resp expectF595Call(String custId, RequestHeader header, int errorCode);

    public String expectLookUpValueForDeclineTemplate();
}
