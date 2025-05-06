package com.lloydsbanking.salsa.apacc;

import com.lloydsbanking.salsa.activate.administer.convert.X741RequestFactory;
import com.lloydsbanking.salsa.activate.communication.convert.CommunicationRequestFactory;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.converter.F060RequestFactory;
import com.lloydsbanking.salsa.activate.converter.F061RespToF062ReqConverter;
import com.lloydsbanking.salsa.activate.downstream.PrdClient;
import com.lloydsbanking.salsa.activate.registration.converter.B751RequestFactory;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.F251RequestFactory;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.F259RequestFactory;
import com.lloydsbanking.salsa.apacc.service.fulfil.gendoc.convert.GenerateDocumentRequestFactory;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.application.client.ApplicationClient;
import com.lloydsbanking.salsa.downstream.asm.client.f425.F425Client;
import com.lloydsbanking.salsa.downstream.cm.client.GenerateDocumentClient;
import com.lloydsbanking.salsa.downstream.cm.client.ScheduleCommunicationClient;
import com.lloydsbanking.salsa.downstream.cm.client.SendCommunicationClient;
import com.lloydsbanking.salsa.downstream.dp.client.encrypt.EncryptClient;
import com.lloydsbanking.salsa.downstream.fdi.client.f241.F241Client;
import com.lloydsbanking.salsa.downstream.fdi.client.f241v1.F241V1Client;
import com.lloydsbanking.salsa.downstream.fdi.client.f251.F251Client;
import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClient;
import com.lloydsbanking.salsa.downstream.ocis.client.f060.F060Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f259.F259Client;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferralTeamsDao;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import com.lloydsbanking.salsa.downstream.tms.client.x741.X741Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.remotemock.*;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Req;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Resp;
import com.lloydsbanking.salsa.soap.encrpyt.objects.*;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Resp;
import com.lloydsbanking.salsa.soap.fdi.f251.objects.F251Req;
import com.lloydsbanking.salsa.soap.fdi.f251.objects.F251Resp;
import com.lloydsbanking.salsa.soap.fdi.f251.objects.F251Result;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Req;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Resp;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Req;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import com.lloydsbanking.salsa.soap.ocis.f259.objects.F259Req;
import com.lloydstsb.ib.wsbridge.application.StB751AAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.system.StB748AWrkngDateAfterXDays;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreation;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreationResponse;
import lib_sim_bo.businessobjects.*;
import lib_sim_communicationmanager.ia_generatedocument.GenerateDocumentInternalServiceErrorMsg;
import lib_sim_communicationmanager.ia_generatedocument.GenerateDocumentResourceNotAvailableErrorMsg;
import lib_sim_communicationmanager.id_communicationeventscheduler.*;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_communicationmanager.messages.GenerateDocumentRequest;
import lib_sim_communicationmanager.messages.GenerateDocumentResponse;
import lib_sim_communicationmanager.messages.ScheduleCommunicationRequest;
import lib_sim_communicationmanager.messages.SendCommunicationRequest;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class RemoteMockScenarioHelper implements ScenarioHelper {
    @Autowired
    TestDataHelper dataHelper;

    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;

    @Autowired
    ReferenceDataLookUpDao referenceDataLookUpDao;

    @Autowired
    RetrievePamService retrievePamService;

    @Autowired
    PrdClient prdClient;

    @Autowired
    F061Client f061Client;
    @Autowired
    F062Client f062Client;

    @Autowired
    MockControlOcisF061ServicePortType mockOcisF061Control;

    @Autowired
    MockControlEncryptDataServicePortType mockControlEncryptDataServicePortType;

    @Autowired
    F060RequestFactory f060RequestFactory;

    @Autowired
    F060Client f060Client;

    @Autowired
    MockControlOcisF060ServicePortType f060ServicePortType;

    @Autowired
    EncryptClient encryptClient;

    @Autowired
    F061RespToF062ReqConverter f061RespToF062ReqConverter;
    @Autowired
    MockControlOcisF062ServicePortType mockOcisF062Control;

    @Autowired
    MockControlRpcServicePortType mockRpcControl;

    @Autowired
    MockControlServicePortType mockControl;

    @Autowired
    F425Client f425Client;

    @Autowired
    X741RequestFactory x741RequestFactory;

    @Autowired
    B751RequestFactory b751RequestFactory;

    @Autowired
    X741Client x741Client;

    @Autowired
    ApplicationClient applicationClient;

    @Autowired
    MockControlFsApplicationServicePortType mockControlFsApplicationServicePortType;


    @Autowired
    MockControlAsmF425ServicePortType mockAsmF425Control;

    @Autowired
    MockControlTmsX741ServicePortType mockTmsX741Control;

    @Autowired
    MockControlFdiF241ServicePortType mockFdiF241Control;

    @Autowired
    CommunicationRequestFactory communicationRequestFactory;
    @Autowired
    SendCommunicationClient sendCommunicationClient;
    @Autowired
    MockControlSendCommunicationManagerServicePortType mockControlSendCommunicationManager;
    @Autowired
    MockControlGenerateDocumentServicePortType mockControlGenerateDocumentServicePortType;
    @Autowired
    ReferralTeamsDao referralTeamsDao;
    @Autowired
    GenerateDocumentRequestFactory generateDocumentRequestFactory;
    @Autowired
    GenerateDocumentClient generateDocumentClient;

    @Autowired
    F241V1Client f241V1Client;

    @Autowired
    F241Client f241Client;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    MockControlFdiF241V1ServicePortType mockControlFdiF241V1;

    @Autowired
    F259RequestFactory f259RequestFactory;

    @Autowired
    F251RequestFactory f251RequestFactory;
    @Autowired
    F259Client f259Client;

    @Autowired
    F251Client f251Client;

    @Autowired
    MockControlOcisF259ServicePortType mockf259Control;

    @Autowired
    MockControlFdiF251ServicePortType mockf251Control;

    @Autowired
    MockControlFsSystemServicePortType mockControlFsSystemServicePortType;

    @Autowired
    FsSystemClient fsSystemClient;
    @Autowired
    ScheduleCommunicationClient scheduleCommunicationClient;
    @Autowired
    MockControlScheduleCommunicationManagerServicePortType mockControlScheduleCommunicationManager;

    @Value("${salsa.fs.boxid}")
    int boxId;

    @Override
    public void clearUp() {
        dataHelper.cleanUp();
        referenceDataLookUpDao.deleteAll();
        clearUpForWPS();
    }

    public void clearUpForWPS() {

    }

    @Override
    public String expectChannelIdByContactPointID() {
        String channelId = "LTB";
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("Cnt_Pnt_Prtflio", "0000777505", "Acquire Contry Name", new Long("132356"), "Bahrain", "LTB", new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
        return channelId;
    }

    @Override
    @Transactional
    public long expectApplicationDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String subStatus, String niNumber) {
        return dataHelper.createApplicationSA(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber);
    }

    @Override
    @Transactional
    public void expectReferenceDataForPAM() {
        dataHelper.createPamReferenceData();
    }

    @Override
    @Transactional
    public void expectLookUpValues() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 513L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "1", "Purpose of Account", 1091L, "SPORI", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "2", "Purpose of Account", 1092L, "BIEXP", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("REFERRAL_TEAM_GROUPS", "209178", "Purpose of Account", 1092L, "002", "LTB", 1L));
    }

    @Override
    @Transactional
    public void expectLookUpValuesWithInCorrectGroupCodes() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP1", "WZ_ESB_V1-sscert123.pem", "Card Encryption Key", 513L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT1", "1", "Purpose of Account", 1091L, "SPORI", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT1", "2", "Purpose of Account", 1092L, "BIEXP", "LTB", 1L));
    }

    @Override
    @Transactional
    public ProductArrangement expectApplicationDetailsHavingPamAndRequestData(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        Applications applications = dataHelper.createNewApplication(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber, productType);
        ProductArrangement productArrangement = null;
        try {
            productArrangement = retrievePamService.retrievePendingArrangement(channelId, String.valueOf(applications.getId()), upstreamRequest.getProductArrangement().getReferral());
        } catch (ResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
            throw exceptionUtilityActivate.resourceNotAvailableError(upstreamRequest.getHeader(), "Resource Not Available Error");
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
            throw exceptionUtilityActivate.dataNotAvailableError(null, null, "Data Not Available Error", upstreamRequest.getHeader());
        } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            throw exceptionUtilityActivate.internalServiceError(null, "Internal Service Error", upstreamRequest.getHeader());
        }
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        Customer upStreamCustomer = upstreamRequest.getProductArrangement().getPrimaryInvolvedParty();
        productArrangement.setApplicationSubStatus(upstreamRequest.getProductArrangement().getApplicationSubStatus());
        productArrangement.setAccountNumber(upstreamRequest.getProductArrangement().getAccountNumber());
        productArrangement.setGuardianDetails(upstreamRequest.getProductArrangement().getGuardianDetails());
        if (!upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OAP_SOURCE_SYSTEM_IDENTIFIER)) {
            customer.setIsRegistrationSelected(upStreamCustomer.isIsRegistrationSelected());
            customer.setPassword(upStreamCustomer.getPassword());
            customer.setUserType(upStreamCustomer.getUserType());
            customer.setInternalUserIdentifier(upStreamCustomer.getInternalUserIdentifier());
        }
        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CREDITCARD.getValue())) {
            String brandName = productArrangement.getAssociatedProduct().getBrandName();
            productArrangement.setAssociatedProduct(upstreamRequest.getProductArrangement().getAssociatedProduct());
            productArrangement.getAssociatedProduct().setBrandName(brandName);
            if (!upstreamRequest.getProductArrangement().getJointParties().isEmpty()) {
                productArrangement.getJointParties().add(upstreamRequest.getProductArrangement().getJointParties().get(0));
            }
            ((FinanceServiceArrangement) productArrangement).getBalanceTransfer().addAll(((FinanceServiceArrangement) upstreamRequest.getProductArrangement()).getBalanceTransfer());

        }
        productArrangement.setFinancialInstitution(upstreamRequest.getProductArrangement().getFinancialInstitution());
        productArrangement.setInitiatedThrough(upstreamRequest.getProductArrangement().getInitiatedThrough());

        return productArrangement;
    }

    @Override
    public List<ProductOffer> expectRetrieveProductCondition(Product product, RequestHeader header) throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = dataHelper.createRetrieveProductConditionsRequest(product);
        retrieveProductConditionsRequest.setHeader(header);
        prdClient.retrieveProductConditions(retrieveProductConditionsRequest);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse response = dataHelper.createRetrieveProductConditionsResponse();
        mockRpcControl.thenReturn(response);
        return response.getProduct().get(0).getProductoffer();
    }

    @Override
    public void verifyExpectCalls() {
        String result = mockControl.verify();
        if (!result.equals("verified") && result.equals("fsSystem")) {
            throw new IllegalStateException("Following Expect not called: " + result);
        }
    }

    @Override
    public void expectF425Call(RequestHeader header, String requestNo, String sourceSystemCode, String asmDecisionCode, String asmCreditScore) {
        F425Req f425Req = dataHelper.createF425Req(requestNo, sourceSystemCode);
        mockControl.matching("actual.target == 'asmF425'");
        f425Client.f425(f425Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockAsmF425Control.thenReturn(dataHelper.createF425Resp(asmDecisionCode, asmCreditScore));
    }

    @Override
    public void expectF061Call(String customerId, RequestHeader header) {
        F061Req f061Req = new F061Req();
        f061Req.setExtSysId(Short.valueOf("19"));
        f061Req.setPartyId(Long.valueOf(customerId));
        f061Client.f061(f061Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockOcisF061Control.thenReturn(dataHelper.createF061Resp());
    }

    @Override
    public void expectF425CallWithError(RequestHeader header, String requestNo, String sourceSystemCode) {
        F425Req f425Req = dataHelper.createF425Req(requestNo, sourceSystemCode);
        //  mockControl.matching("actual.target == 'asmF425'");
        f425Client.f425(f425Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        F425Resp f425Resp = dataHelper.createF425Resp();
        f425Resp.getF425Result().getResultCondition().setSeverityCode((byte) 1);
        mockAsmF425Control.thenReturn(f425Resp);
    }

    @Override
    public String expectF062Call(Customer customer, RequestHeader header) {
        F062Req f062Req = f061RespToF062ReqConverter.convert(dataHelper.createF061Resp(), dataHelper.createAssessmentEvidence(), customer);
        f062Client.updateCustomerRecord(f062Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockOcisF062Control.thenReturn(dataHelper.createF062Resp());
        return dataHelper.getCustIdFromF062Resp(dataHelper.createF062Resp().getPartyId(), customer.getCustomerIdentifier());
    }

    @Override
    public void expectF062CallFails(Customer customer, RequestHeader header) {
        F062Req f062Req = f061RespToF062ReqConverter.convert(dataHelper.createF061Resp(), dataHelper.createAssessmentEvidence(), customer);
        f062Client.updateCustomerRecord(f062Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockOcisF062Control.thenReturn(dataHelper.createF062RespWithError());
    }

    @Override
    public void expectF251Call(FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, String contactPointId) {
        F251Req f251Req = f251RequestFactory.convert(financeServiceArrangement, contactPointId);
        F251Resp f251Resp = new F251Resp();
        f251Resp.setAdditionalDataIn(1);
        f251Resp.setF251Result(new F251Result());
        f251Resp.getF251Result().setResultCondition(new ResultCondition());
        f251Resp.getF251Result().getResultCondition().setReasonCode(0);
        f251Client.addOMSOffer(f251Req, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        mockf251Control.thenReturn(f251Resp);
    }

    @Override
    public void expectF251CallWithError(FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, String contactPointId) {
        F251Req f251Req = f251RequestFactory.convert(financeServiceArrangement, contactPointId);
        f251Client.addOMSOffer(f251Req, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        F251Resp f251Resp = new F251Resp();
        f251Resp.setAdditionalDataIn(1);
        f251Resp.setF251Result(new F251Result());
        f251Resp.getF251Result().setResultCondition(new ResultCondition());
        f251Resp.getF251Result().getResultCondition().setReasonCode(41164);
        mockf251Control.thenReturn(f251Resp);
    }


    @Override
    public void expectSendCommunicationCall(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType, boolean isFirstCall) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(productArrangement, notificationEmail, header, source, communicationType);
        sendCommunicationClient.sendCommunication(sendCommunicationRequest);
        lib_sim_communicationmanager.messages.SendCommunicationResponse sendCommunicationResponse = new lib_sim_communicationmanager.messages.SendCommunicationResponse();
        sendCommunicationResponse.setIsSuccessful(true);
        mockControlSendCommunicationManager.thenReturn(sendCommunicationResponse);
    }

    @Override
    public void expectSendCommunicationCallWithError(ProductArrangement productArrangement, String notificationEmail, RequestHeader header) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(productArrangement, notificationEmail, header, null, "Email");
        sendCommunicationClient.sendCommunication(sendCommunicationRequest);
        lib_sim_communicationmanager.messages.SendCommunicationResponse sendCommunicationResponse = new lib_sim_communicationmanager.messages.SendCommunicationResponse();
        sendCommunicationResponse.setIsSuccessful(false);
        mockControlSendCommunicationManager.thenReturn(sendCommunicationResponse);
    }

    @Override
    public void expectScheduleCommunicationCall(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType) throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg {
        ScheduleCommunicationRequest scheduleCommunicationRequest = communicationRequestFactory.convertToScheduleCommunicationRequest(productArrangement, notificationEmail, header, source, communicationType);
        mockControl.matching("actual.target == 'schedule-communication' && actual.methodName == 'scheduleCommunication'");
        scheduleCommunicationClient.scheduleCommunication(scheduleCommunicationRequest);
        lib_sim_communicationmanager.messages.ScheduleCommunicationResponse scheduleCommunicationResponse = new lib_sim_communicationmanager.messages.ScheduleCommunicationResponse();
        mockControlScheduleCommunicationManager.thenReturn(scheduleCommunicationResponse);
    }

    @Override
    public void expectX741Call(Integer taskId, ProductArrangement productArrangement, RequestHeader header) {
        TaskCreation x741Req = x741RequestFactory.convert(productArrangement);
        mockControl.matching("actual.target == 'tmsX741'");
        x741Client.x741(x741Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), dataHelper.getBapiInformationFromRequestHeader(header));
        TaskCreationResponse taskCreationResponse = dataHelper.createTaskCreationResponse();
        taskCreationResponse.getCreateTaskReturn().getTaskRoutingInformation().setTaskId(taskId);
        mockTmsX741Control.thenReturn(taskCreationResponse);
    }

    @Override
    @Transactional
    public void expectReferralsTeamDetails(String taskType) {
        ReferralTeams referralTeams1 = new ReferralTeams();
        referralTeams1.setId(1);
        referralTeams1.setPriority(5l);
        referralTeams1.setOuId("123");
        referralTeams1.setTaskType(taskType);
        referralTeamsDao.save(referralTeams1);
    }

    @Override
    @Transactional
    public void expectLookUpValuesForEvidenceAndPurposeData() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PTY_EVIDENCE_CODE", "8", "ABC", 5L, "0011", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ADD_EVIDENCE_CODE", "6", "DEF", 6L, "0031", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PTY_PURPOSE_CODE", "9", "JKL", 7L, "0041", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ADD_PURPOSE_CODE", "7", "LMN", 8L, "0051", "LTB", 1L));
    }

    @Override
    @Transactional
    public void expectLookUpValuesForInvalidEvidenceAndPurposeData() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("EVIDENCE_CODE", "8", "ABC", 5L, "0011", "LTB", 1L));
    }

    @Override
    public F241Resp expectCardAccountV1Call(ProductArrangement productArrangement, RequestHeader requestHeader, String severityCode) {
        f241V1Client.createCardAccountV1(dataHelper.createF241V1Request(productArrangement), headerRetriever.getContactPoint(requestHeader), headerRetriever.getServiceRequest(requestHeader), headerRetriever.getSecurityHeader(requestHeader));
        F241Resp f241Resp = dataHelper.createF241V1Response(severityCode);
        mockControlFdiF241V1.thenReturn(f241Resp);
        return f241Resp;
    }

    @Override
    public String expectCardAccountCall(ProductArrangement productArrangement, RequestHeader requestHeader, String severityCode) {
        f241Client.createCardAccount(dataHelper.createF241Request(productArrangement), headerRetriever.getContactPoint(requestHeader), headerRetriever.getServiceRequest(requestHeader), headerRetriever.getSecurityHeader(requestHeader));
        com.lloydsbanking.salsa.soap.fdi.f241.objects.F241Resp f241Resp = dataHelper.createF241Response(severityCode);
        mockFdiF241Control.thenReturn(f241Resp);
        String cardNo = f241Resp.getCardData().get(0).getCardNo();
        String cardNumber = cardNo.length() == 19 ? cardNo.substring(3) : cardNo;
        return cardNumber;
    }

    @Override
    public void expectF259Call(FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, String severityCode) {
        F259Req f259Req = f259RequestFactory.convert(financeServiceArrangement, requestHeader.getChannelId());
        f259Client.addNewCustomer(f259Req, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        mockf259Control.thenReturn(dataHelper.createF259Response(severityCode));
    }

    @Transactional
    public void expectRelatedApplicationDetails(Applications applications, ApplicationStatus applicationStatus) {
        dataHelper.createRelatedApplications(applications, applicationStatus);
    }

    @Override
    public void expectB748Call(RequestHeader requestHeader, int errorNo) {
        StB748AWrkngDateAfterXDays stB748AWrkngDateAfterXDays = dataHelper.createB748Request(requestHeader);
        mockControl.matching("actual.target == 'fsSystem' && actual.methodName == 'b748WrkngDateAfterXDays'");
        fsSystemClient.retrieveNextBusinessDay(stB748AWrkngDateAfterXDays);
        mockControlFsSystemServicePortType.thenReturn(dataHelper.createB748Response(errorNo));
    }

    @Override
    public void expectGenerateDocumentCall(ProductArrangement productArrangement, List<ProductOffer> productOfferList, RequestHeader header) throws
            GenerateDocumentResourceNotAvailableErrorMsg, GenerateDocumentInternalServiceErrorMsg {
        GenerateDocumentRequest generateDocumentRequest = generateDocumentRequestFactory.convert((FinanceServiceArrangement) productArrangement, header, productOfferList);
        generateDocumentClient.generateDocument(generateDocumentRequest);
        GenerateDocumentResponse generateDocumentResponse = new GenerateDocumentResponse();
        DocumentationItem documentationItem = new DocumentationItem();
        byte[] image = {1, 2};
        documentationItem.setDocument(image);
        generateDocumentResponse.setDocumentationItem(documentationItem);
        mockControlGenerateDocumentServicePortType.thenReturn(generateDocumentResponse);
    }

    @Override
    public String expectB751Call(ProductArrangement productArrangement, RequestHeader header, int tacver) {
        StB751AAppPerCCRegAuth stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, header);
        mockControl.matching("actual.target == 'fsApplication' && actual.methodName == 'b751AppPerCCRegAuth'");
        applicationClient.createAppPerCCRegAuth(stB751AAppPerCCRegAuth);
        StB751BAppPerCCRegAuth b751Resp = dataHelper.createB751Response(tacver);
        mockControlFsApplicationServicePortType.thenReturn(dataHelper.createB751Response(tacver));
        return b751Resp.getPartyidEmergingChannelUserId();
    }

    @Override
    public void delayB751Call(long milliseconds) {
        /*StB751AAppPerCCRegAuth stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, header);
        applicationClient.createAppPerCCRegAuth(stB751AAppPerCCRegAuth);*/
        mockControlFsApplicationServicePortType.delayInvocation("applicationClient", milliseconds);
    }

    private List<String> getCreditCardNumbersForBT(List<BalanceTransfer> balanceTransfers) {
        List<String> creditCardNumbers = new ArrayList<>();
        for (BalanceTransfer balanceTransfer : balanceTransfers) {
            if (!StringUtils.isEmpty(balanceTransfer.getCreditCardNumber())) {
                balanceTransfer.setMaskedCreditCardNumber(balanceTransfer.getCreditCardNumber());
                creditCardNumbers.add(balanceTransfer.getCreditCardNumber());
            }
        }
        return creditCardNumbers;
    }

    private List<String> getCreditCardNumbers(FinanceServiceArrangement financeServiceArrangement) {
        List<String> creditCardNumbers = new ArrayList<>();
        creditCardNumbers.add(financeServiceArrangement.getCreditCardNumber());
        if (financeServiceArrangement.getAddOnCreditCardNumber() != null) {
            creditCardNumbers.add(financeServiceArrangement.getAddOnCreditCardNumber());
        }
        return creditCardNumbers;
    }

    @Override
    public void expectRetrieveEncryptDataForBT(List<BalanceTransfer> balanceTransfers, String encryptionKey, RequestHeader requestHeader) {
        List<String> creditCardNumbers = getCreditCardNumbersForBT(balanceTransfers);
        EncryptDataRequest encryptDataRequest = retrieveEncryptDataRequest(creditCardNumbers, encryptionKey);
        encryptClient.retrieveEncryptData(encryptDataRequest, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        EncryptDataResponse response = new EncryptDataResponse();
        response.getOutdetails().add(new Outdetails());
        response.getOutdetails().get(0).setOuttextDetails("123");
        mockControlEncryptDataServicePortType.thenReturn(response);
    }

    @Override
    public void expectRetrieveEncryptData(FinanceServiceArrangement financeServiceArrangement, String encryptionKey, RequestHeader requestHeader) {
        List<String> creditCardNumbers = getCreditCardNumbers(financeServiceArrangement);
        EncryptDataRequest encryptDataRequest = retrieveEncryptDataRequest(creditCardNumbers, encryptionKey);
        encryptClient.retrieveEncryptData(encryptDataRequest, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        EncryptDataResponse response = new EncryptDataResponse();
        response.getOutdetails().add(new Outdetails());
        response.getOutdetails().get(0).setOuttextDetails("123");
        mockControlEncryptDataServicePortType.thenReturn(response);
    }

    private EncryptDataRequest retrieveEncryptDataRequest(List<String> creditCardNumbers, String encryptKey) {
        EncryptDataRequest encryptDataRequest = new EncryptDataRequest();
        for (String creditCardNumber : creditCardNumbers) {
            Indetails indetails = new Indetails();
            indetails.setIntext(creditCardNumber);
            indetails.setEncryptKey(encryptKey);
            indetails.setEncryptType(EncryptionType.SYMM);
            indetails.setInpEncode("base64");
            encryptDataRequest.getIndetails().add(indetails);
        }
        return encryptDataRequest;
    }

    @Override
    public void expectF060Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode) {
        F060Req f060Req = f060RequestFactory.convert(productArrangement);
        mockControl.matching("actual.target == 'ocisF060'");
        f060Client.f060(f060Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        F060Resp f060Resp = dataHelper.createF060Resp();
        f060Resp.getF060Result().setResultCondition(new ResultCondition());
        f060Resp.getF060Result().getResultCondition().setReasonCode(reasonCode);
        f060ServicePortType.thenReturn(f060Resp);
    }

    @Override
    public void delayF060Invocation(long milliseconds) {
        f060ServicePortType.delayInvocation("ocisF060", milliseconds);
    }

    @Override
    public void delayEncryptDataInvocation(long milliseconds) {
        mockControlEncryptDataServicePortType.delayInvocation("encryptDataService", milliseconds);
    }

    public void sleep() {
    }

}
