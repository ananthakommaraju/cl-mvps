package com.lloydsbanking.salsa.ppae;


import com.lloydsbanking.salsa.activate.communication.convert.CommunicationRequestFactory;
import com.lloydsbanking.salsa.activate.downstream.PrdClient;
import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.downstream.arrangement.client.wz.ArrangementClient;
import com.lloydsbanking.salsa.downstream.asm.client.f204.F204Client;
import com.lloydsbanking.salsa.downstream.asm.client.f205.F205Client;
import com.lloydsbanking.salsa.downstream.asm.client.f424.F424Client;
import com.lloydsbanking.salsa.downstream.cm.client.ScheduleCommunicationClient;
import com.lloydsbanking.salsa.downstream.cm.client.SendCommunicationClient;
import com.lloydsbanking.salsa.downstream.loan.client.LoanClient;
import com.lloydsbanking.salsa.downstream.ocis.client.f595.F595Client;
import com.lloydsbanking.salsa.downstream.pad.client.f263.F263Client;
import com.lloydsbanking.salsa.downstream.pad.client.q028.Q028Client;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationsDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferralTeamsDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferralsDao;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import com.lloydsbanking.salsa.downstream.pp.client.PaymentProcessingClient;
import com.lloydsbanking.salsa.downstream.prd.jdbc.*;
import com.lloydsbanking.salsa.downstream.prd.model.*;
import com.lloydsbanking.salsa.downstream.switches.jdbc.SwitchDao;
import com.lloydsbanking.salsa.downstream.switches.model.SwitchDto;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveCreditDecisionRequestFactory;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveCreditScoreRequestFactory;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveFraudDecisionRequestFactory;
import com.lloydsbanking.salsa.offer.apply.downstream.RpcServiceClient;
import com.lloydsbanking.salsa.offer.downstream.EligibilityServiceClient;
import com.lloydsbanking.salsa.ppae.service.client.ActivateProductSAClient;
import com.lloydsbanking.salsa.ppae.service.convert.*;
import com.lloydsbanking.salsa.ppae.service.downstream.ActivateProductManager;
import com.lloydsbanking.salsa.remotemock.*;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Req;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Req;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Req;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import com.lloydsbanking.salsa.soap.fs.ftp.IssueInpaymentInstructionRequest;
import com.lloydsbanking.salsa.soap.fs.ftp.IssueInpaymentInstructionResponse;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.F595Req;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.F595Resp;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.F595Result;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Req;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Resp;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Req;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Resp;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Result;
import com.lloydstsb.ib.wsbridge.loan.StB233ALoanIllustrate;
import com.lloydstsb.ib.wsbridge.loan.StB233BLoanIllustrate;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm.ResponseHeader;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.VerifyProductArrangementDetailsRequest;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.VerifyProductArrangementDetailsResponse;
import com.lloydstsb.schema.enterprise.lcsm_common.wz.RuleCondition;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.ContactPreference;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.InvolvedParty;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.InvolvedPartyRole;
import com.lloydstsb.schema.infrastructure.soap.Condition;
import com.lloydstsb.schema.infrastructure.soap.*;
import com.lloydstsb.schema.infrastructure.soap.ExtraConditions;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import lib_sim_bo.businessobjects.*;
import lib_sim_communicationmanager.id_communicationeventscheduler.*;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_communicationmanager.messages.ScheduleCommunicationRequest;
import lib_sim_communicationmanager.messages.SendCommunicationRequest;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RemoteMockScenarioHelper implements ScenarioHelper {

    @Autowired
    ReferenceDataLookUpDao referenceDataLookUpDao;
    @Autowired
    EligibilityServiceClient eligibilityServiceClient;
    @Autowired
    MockControlEligibilityServicePortType mockEligibilityControl;
    @Autowired
    TestDataHelper dataHelper;
    @Autowired
    MockControlServicePortType mockControl;
    @Autowired
    MockControlAsmF204ServicePortType mockAsmF204Control;
    @Autowired
    PrdClient prdClient;
    @Autowired
    F205Client f205Client;
    @Autowired
    MockControlRpcServicePortType mockPrdControl;
    @Autowired
    MockControlAsmF424ServicePortType mockAsmF424Control;
    @Autowired
    RetrievePamService retrievePamService;
    @Autowired
    CommunicationRequestFactory communicationRequestFactory;
    @Autowired
    ApplicationsDao applicationDao;
    @Autowired
    ProductGroupDao productGroupDao;
    @Autowired
    ScheduleCommunicationClient scheduleCommunicationClient;
    @Autowired
    ProductPackageDao productPackageDao;
    @Autowired
    MockControlAsmF205ServicePortType mockAsmF205Control;
    @Autowired
    MockControlSendCommunicationManagerServicePortType mockControlSendCommunicationManager;
    @Autowired
    MockControlScheduleCommunicationManagerServicePortType mockControlScheduleCommunicationManager;
    @Autowired
    RefInstructionHierarchyPrdDao refInstructionHierarchyPrdDao;
    @Autowired
    ExternalSystemTemplateDao externalSystemTemplateDao;
    @Autowired
    MockControlActivateServicePortType mockControlActivateServicePortType;
    @Autowired
    SendCommunicationClient sendCommunicationClient;
    @Autowired
    ActivateProductSAClient activateProductSAClient;
    @Autowired
    RefInstructionRulesPrdDao refInstructionRulesPrdDao;
    @Autowired
    F204Client f204Client;
    @Autowired
    F424Client f424Client;
    @Autowired
    RpcServiceClient rpcServiceClient;
    @Autowired
    ProductPropositionDao productPropositionDao;
    @Autowired
    MockControlPadF263ServicePortType f263ServicePortType;
    @Autowired
    ActivateProductManager activateProductManager;
    @Autowired
    private SwitchDao switchDao;
    @Autowired
    F263RequestFactory f263RequestFactory;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    F263Client f263Client;
    @Autowired
    ReferralsDao referralsDao;
    @Autowired
    ReferralTeamsDao referralTeamsDao;
    @Autowired
    Q028Client q028Client;
    @Autowired
    F595Client f595Client;
    @Autowired
    LoanClient loanClient;
    @Autowired
    MockControlPadQ028ServicePortType mockPadQ028Control;
    @Autowired
    MockControlFsLoanServicePortType mockFsLoanControl;
    @Autowired
    B233RequestFactory b233RequestFactory;
    @Autowired
    MockControlOcisF595ServicePortType mockOcisF595Control;
    @Autowired
    MockControlArrangementSetupWzServicePortType mockArrangementSetupControl;
    @Autowired
    PrdRequestFactory rpcRequestFactory;
    @Autowired
    VerifyProductArrangementDetailsRequestFactory verifyProductArrangementDetailsRequestFactory;
    @Autowired
    ArrangementClient arrangementClient;
    @Autowired
    IssueInPaymentInstRequestFactory issueInPaymentInstRequestFactory;
    @Autowired
    PaymentProcessingClient paymentProcessingClient;

    @Value("${salsa.fs.boxid}")
    int boxId;

    ChannelToBrandMapping channelToBrandMapping = new ChannelToBrandMapping();

    private static final Logger LOGGER = Logger.getLogger(RemoteMockScenarioHelper.class);

    @Override
    @Transactional
    @Modifying
    @Rollback(false)
    public void clearUp() {
        dataHelper.cleanUp();
        referenceDataLookUpDao.deleteAll();
        refInstructionHierarchyPrdDao.deleteAll();
        clearUpForWPS();
    }

    public void clearUpForWPS() {

    }


    @Override
    public String expectContactPointIDdByChannelID() {
        String contactPointID = "0000777505";
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("Cnt_Pnt_Prtflio", contactPointID, "Display Contact_Point_Portfolio", new Long("26"), "CONTACT_POINT_ID", "LTB", new Long("1"));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("Cnt_Pnt_Prtflio", "0000115219", "Display Contact_Point_Portfolio", 28L, "CONTACT_POINT_ID", "HLX", 1L));
        referenceDataLookUpDao.save(referenceDataLkp);
        return contactPointID;
    }

    @Override
    public void verifyExpectCalls() {
        String result = mockControl.verify();
        if (!result.equals("verified")) {
            throw new IllegalStateException("Following Expect not called: " + result);
        }
    }


    @Override
    @Transactional
    public void expectReferenceDataForPAM(ApplicationStatus applicationStatus, ProductTypes productTypes, Brands brands) {
        dataHelper.createPamReferenceData(applicationStatus, productTypes, brands);
    }

    @Override
    @Transactional
    public void expectApplicationRelatedData(ApplicationStatus applicationStatus, ProductTypes productTypes, Brands brands) {
        dataHelper.createPamReferenceData(applicationStatus, productTypes, brands);

    }

    @Override
    public List<ExtSysProdIdentifier> expectRetrieveProductCondition(RequestHeader requestHeader, ProductArrangement productArrangement) throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {


        /*mockControl.matching("actual.target == 'rpc' && actual.methodName == 'retrieveProductConditions'");*/
        System.out.println("dataHelper = " + dataHelper.createRetrieveProductConditionsRequest(requestHeader, productArrangement));
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = dataHelper.createRetrieveProductConditionsRequest(requestHeader, productArrangement);
        retrieveProductConditionsRequest.getHeader().setContactPointId("0000777505");
        prdClient.retrieveProductConditions(retrieveProductConditionsRequest);
        mockPrdControl.thenReturn(dataHelper.createRetrieveProductConditionsResponse());
        return dataHelper.createRetrieveProductConditionsResponse().getProduct().get(0).getExternalSystemProductIdentifier();
    }

    @Override
    public void expectLookUpValuesFromPAMToRetrieveLookUpData() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("CUSTOMER_NO_SHOW_UPD", "35", "Updating status Days for Awaiting Docs", (long) 43, "Cust.NoShow_Upddays", "LTB", (long) 1));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("CCA_SIGNED_DAYS", "2", "STP Loans CCA Signed days", (long) 1730, "CCA_SIGNED_DAYS", "HLX", (long) 1));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("CCA_PENDING_DAYS", "1", "STP Loans CCA Pending days", (long) 1728, "CCA_PENDING_DAYS", "HLX", (long) 1));
    }


    @Override
    public void expectSendCommunicationCall(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockControl.matching("actual.target == 'scm' && actual.methodName == 'sendCommunication'");
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(productArrangement, notificationEmail, header, source, communicationType);
        sendCommunicationClient.sendCommunication(sendCommunicationRequest);
        lib_sim_communicationmanager.messages.SendCommunicationResponse sendCommunicationResponse = new lib_sim_communicationmanager.messages.SendCommunicationResponse();
        sendCommunicationResponse.setIsSuccessful(true);
        mockControlSendCommunicationManager.thenReturn(sendCommunicationResponse);
    }

    @Override
    public void expectScheduleCommunicationCall(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType, int configDays) throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg {
        ScheduleCommunicationRequest scheduleCommunicationRequest = communicationRequestFactory.convertToScheduleCommunicationRequestWithConfiguredDays(productArrangement, notificationEmail, header, source, communicationType, configDays);
        mockControl.matching("actual.target == 'schedule-communication' && actual.methodName == 'scheduleCommunication'");
        scheduleCommunicationClient.scheduleCommunication(scheduleCommunicationRequest);
        lib_sim_communicationmanager.messages.ScheduleCommunicationResponse scheduleCommunicationResponse = new lib_sim_communicationmanager.messages.ScheduleCommunicationResponse();
        mockControlScheduleCommunicationManager.thenReturn(scheduleCommunicationResponse);
    }


    @Override
    public ProductArrangement expectProductArrangementDetails(String channelId, String applicationId, List<Referral> referralList) throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        try {
            return retrievePamService.retrievePendingArrangement(channelId, applicationId, referralList);
        } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            internalServiceErrorMsg.printStackTrace();
        }
        return null;
    }

    @Override
    public void expectF263CallForCcaSigned(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(upStreamRequest.getHeader().getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(upStreamRequest.getHeader().getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(upStreamRequest.getHeader().getLloydsHeaders());
        F263Req f263Req = f263RequestFactory.createF263Req(productArrangement.getPrimaryInvolvedParty());
        f263Client.enquireLoanApplication(f263Req, contactPoint, serviceRequest, securityHeaderType);
        f263ServicePortType.thenReturn(dataHelper.createF263Resp());
    }

    @Override
    public void expectF263CallForCcaPending(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(upStreamRequest.getHeader().getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(upStreamRequest.getHeader().getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(upStreamRequest.getHeader().getLloydsHeaders());
        F263Req f263Req = f263RequestFactory.createF263Req(productArrangement.getPrimaryInvolvedParty());
        f263Client.enquireLoanApplication(f263Req, contactPoint, serviceRequest, securityHeaderType);
        F263Resp f263Resp = dataHelper.createF263Resp();
        f263Resp.getApplicationDetails().setLoanApplnStatusCd(3);
        f263ServicePortType.thenReturn(f263Resp);
    }


    @Override
    public void setPPAEBatchSwitch(boolean ppaeBatchSwitch) {
        SwitchDto switchBatchForLBG = new SwitchDto(channelToBrandMapping.getGlobalBrandForChannel(TestDataHelper.TEST_CHANNEL_ID), TestDataHelper.PPAE_SWITCH, new Date(), boxId, 1, "A", ppaeBatchSwitch ? "1" : "0");
        switchDao.save(switchBatchForLBG);
        SwitchDto switchBatchForTSB = new SwitchDto(channelToBrandMapping.getGlobalBrandForChannel(TestDataHelper.TEST_CHANNEL_ID_TSB), TestDataHelper.PPAE_SWITCH, new Date(), boxId, 1, "A", ppaeBatchSwitch ? "1" : "0");
        switchDao.save(switchBatchForTSB);
    }

    @Override
    @Transactional
    public void expectApplicationDataForPAM(ApplicationStatus applicationStatus, ProductTypes productTypes, Brands brands) {
        dataHelper.createPamReferenceData(applicationStatus, productTypes, brands);

    }

    @Override
    @Transactional
    @Modifying
    public void expectScheduleEvent(String objectKey) {
        dataHelper.createEventStoresData(objectKey);
    }

    @Override
    public boolean expectUpdatedDao() {
        return dataHelper.eventStoresData();
    }

    @Override
    public void expectF424Call(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, String error) {
        mockControl.matching("actual.target == 'asmF424' && actual.methodName == 'f424' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        ProductArrangement productArrangement = dataHelper.createFinanceArrangementForCC();
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0);
        FinanceServiceArrangement financeServiceArrangement = null;
        F424Req f424Req = null;
        F424Resp resp = null;
        if (productArrangement instanceof FinanceServiceArrangement) {
            financeServiceArrangement = (FinanceServiceArrangement) productArrangement;
            String directDebitIn = (financeServiceArrangement.getBalanceTransfer() != null && !financeServiceArrangement.getBalanceTransfer().isEmpty()) ? "Y" : "N";
            String isEligible = financeServiceArrangement.getAssociatedProduct().getEligibilityDetails() != null ? financeServiceArrangement.getAssociatedProduct().getEligibilityDetails().getIsEligible() : null;
            f424Req = new RetrieveCreditDecisionRequestFactory().create(header.getContactPointId(),
                    financeServiceArrangement.getArrangementId(),
                    financeServiceArrangement.getAssociatedProduct().getGuaranteedOfferCode(),
                    financeServiceArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().get(0).getProductIdentifier(),
                    isEligible,
                    financeServiceArrangement.getInitiatedThrough().getSubChannelCode(),
                    financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(),
                    financeServiceArrangement.getPrimaryInvolvedParty(),
                    financeServiceArrangement.getArrangementType(),
                    financeServiceArrangement.isMarketingPrefereceIndicator(),
                    directDebitIn,
                    financeServiceArrangement.getTotalBalanceTransferAmount());
        }
        f424Req.getRequestDetails().setSortCd(header.getContactPointId());
        if (error.equalsIgnoreCase("ERROR")) {
            resp = dataHelper.createF424ResponseForError(asmCreditScoreResultCd, referralCodeList);
            f424Client.f424(f424Req, contactPoint, serviceRequest, securityHeaderType);
        } else {
            resp = dataHelper.createF424Response(asmCreditScoreResultCd, referralCodeList);
            f424Client.f424(f424Req, contactPoint, serviceRequest, securityHeaderType);
        }


        mockAsmF424Control.thenReturn(resp);
    }

    @Override
    public void expectPrdDbCalls() {
        expectInstructionHierarchyCall("P_CLUB", "LTB");
        expectInstructionRulesViewCall("G_PCA", "LTB");
        expectProductPropositionCall();
        expectProductFamilyCall();
        expectProductPackageVwCall();
        expectExternalSystemTemplateVwCall();
    }

    @Override
    public void expectF204Call(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, String error) throws ParseException, DatatypeConfigurationException {
        mockControl.matching("actual.target == 'asmF204' && actual.methodName == 'f204' && differingProperties(expected.arguments[1], actual.arguments[1], " + "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        ProductArrangement productArrangement = dataHelper.createDepositArrangementForSA();
        productArrangement.setFinancialInstitution(new Organisation());
        productArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());

        F204Req f204Req = new RetrieveFraudDecisionRequestFactory().create(productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode()
                , header.getChannelId(), productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier(), productArrangement.getArrangementId(),
                productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), productArrangement.getPrimaryInvolvedParty(), productArrangement.getPrimaryInvolvedParty().getPostalAddress());
        F204Resp resp = null;
        f204Req.getRequestDetails().setSortCd(header.getContactPointId());
        f204Client.performFraudCheck(f204Req, contactPoint, serviceRequest, securityHeaderType);
        if (error.equalsIgnoreCase("ERROR")) {
            resp = dataHelper.createF204ResponseWithError(asmCreditScoreResultCd, referralCodeList);
        } else {
            resp = dataHelper.createF204Response(asmCreditScoreResultCd, referralCodeList);
        }
        mockAsmF204Control.thenReturn(resp);
    }


    @Override
    @Transactional
    public void expectInstructionHierarchyCall(String insMnemonic, String brand) {
        RefInstructionHierarchyPrdDto hierarchyPrdDto = new RefInstructionHierarchyPrdDto(insMnemonic, "Club Lloyds", "G_PCA", "PCA", null, brand);
        refInstructionHierarchyPrdDao.save(hierarchyPrdDto);
    }

    @Transactional
    private void expectExternalSystemTemplateVwCall() {
        externalSystemTemplateDao.save(new ExternalSystemTemplate(new ExternalSystemTemplateId("00001", 20043l), "CCA_Generic", 1l));
        externalSystemTemplateDao.save(new ExternalSystemTemplate(new ExternalSystemTemplateId("00001", 20042l), "CCA_Clarity", 1l));
    }

    @Override
    @Transactional
    public void expectInstructionRulesViewCall(String insMnemonic, String brand) {
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto(insMnemonic, "GR020", "Customer not eligible for Current Account", "CR002", "Customer cannot be younger that 18 years", "GRP", "17", "CST", brand, BigDecimal.ONE);
        refInstructionRulesPrdDao.save(refInstructionRulesPrdDto);
    }

    @Transactional
    private void expectProductPropositionCall() {
        List<ProductProposition> productPropositions = new ArrayList<>();
        productPropositions.add(new ProductProposition(25l, 10017l, "Platinum 9/9"));
        productPropositions.add(new ProductProposition(42l, 20042l, "Clarity"));
        productPropositions.add(new ProductProposition(32l, 20040l, "Clarity1"));
        productPropositions.add(new ProductProposition(33l, 20043l, "Clarity2"));

        productPropositionDao.save(productPropositions);
    }

    @Transactional
    private void expectProductFamilyCall() {
        List<ProductGroup> productGroups = new ArrayList<>();
        productGroups.add(new ProductGroup(18l, "300", 20042l, "901", "00107", "102"));
        productGroups.add(new ProductGroup(17l, "500", 20040l, "500", "00107", "102"));
        productGroups.add(new ProductGroup(19l, "700", 20043l, "700", "00107", "102"));

        productGroupDao.save(productGroups);
    }

    @Transactional
    private void expectProductPackageVwCall() {
        productPackageDao.save(new ProductPackage(new ProductPackageId(40l, 200l, "00107"), "H20 13 13/6 0%Fee 18.9% 3 Trs", "100", 20042l, null, "200"));
        productPackageDao.save(new ProductPackage(new ProductPackageId(32l, 400l, "00107"), "H20 13 13/6 0%Fee 18.9% 3 Trs", "101", 20042l, null, "400"));
        productPackageDao.save(new ProductPackage(new ProductPackageId(42l, 401l, "00107"), "H20 13 13/6 0%Fee 18.9% 3 Trs", "102", 20042l, null, "200"));
        productPackageDao.save(new ProductPackage(new ProductPackageId(33l, 402l, "00107"), "H20 13 13/6 0%Fee 18.9% 3 Trs", "102", 20043l, null, "600"));
        productPackageDao.save(new ProductPackage(new ProductPackageId(42l, 1l, "00107"), "Clarity", "100", 00107l, 0l, "200"));
        productPackageDao.save(new ProductPackage(new ProductPackageId(42l, 1l, "00024"), "Clarity", "100", 00024l, 0l, "600"));

    }

    @Override
    public void expectRpcForOffer(RequestHeader requestHeader, String productMatched) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = dataHelper.rpcRequestForOffer(requestHeader);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResponse = dataHelper.rpcResponseForOffer(productMatched);
        rpcServiceClient.retrieveProductConditions(rpcRequest);
        mockPrdControl.thenReturn(rpcResponse);
    }

    @Override
    public void expectRpcOfferForDecline(RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = dataHelper.rpcRequestForOffer(requestHeader);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResponse = dataHelper.rpcResponseForOffer();
        rpcServiceClient.retrieveProductConditions(rpcRequest);
        mockPrdControl.thenReturn(rpcResponse);


    }

    public void expectLookupListFromGroupCodeAndChannelAndLookUpText(String groupCd, String channel, List<String> lookUpText) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp(groupCd, "BUREAU", "Cnt_Pnt_Prtflio", new Long("1323"), lookUpText.get(0), channel, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
    }

    public void expectLookupListFromGroupCodeAndChannelAndLookUpTextForDuplicate(String groupCd, String channel, List<String> lookUpText) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp(groupCd, "Duplicate Application", "Cnt_Pnt_Prtflio", new Long("1325"), lookUpText.get(0), channel, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
    }


    public void expectLookupListFromChannelAndGroupCodeList(String channelId) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("CC_CROSS_SELL_FC", "103", "Cnt_Pnt_Prtflio", new Long("1321"), "BHR", channelId, new Long("1"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp("MIN_OVERDRAFT_LIMIT", "103", "Cnt_Pnt_Prtflio", new Long("1322"), "BHR", channelId, new Long("1"));

        referenceDataLookUpDao.save(referenceDataLkp);
        referenceDataLookUpDao.save(referenceDataLkp1);
    }


    public void expectLookupListFromChannelAndGroupCode() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ASM_DECLINE_CODE", "8", "ABC", 5L, "0011", "LTB", 1L));
    }

    public void expectActivateProductCall(ProductArrangement productArrangement, RequestHeader requestHeader) throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {

        ActivateProductArrangementRequest activateProductArrangementRequest = activateProductManager.createActivateRequest(productArrangement, requestHeader);
        activateProductSAClient.activateProductArrangement(activateProductArrangementRequest);
        ActivateProductArrangementResponse response = new ActivateProductArrangementResponse();
        response.setProductArrangement(new DepositArrangement());
        mockControlActivateServicePortType.thenReturn(response);

    }

    @Override
    public void expectF205Call(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, int caseNo, String error) throws ParseException {
        mockControl.matching("actual.target == 'asmF205' && actual.methodName == 'f205' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        ProductArrangement productArrangement = dataHelper.createDepositArrangementForSA();

        productArrangement.setFinancialInstitution(new Organisation());
        productArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F205Req f205Req = new RetrieveCreditScoreRequestFactory().create(header.getContactPointId(), productArrangement.getArrangementId(),
                productArrangement.getAssociatedProduct().getBrandName(), productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier(), productArrangement.getPrimaryInvolvedParty(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode(),
                productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(),
                productArrangement.getAccountPurpose(),
                productArrangement.getExistingProducts(), productArrangement.getConditions());

        f205Req.getRequestDetails().setSortCd(header.getContactPointId());
        F205Resp resp = dataHelper.createF205Response2(asmCreditScoreResultCd, referralCodeList, caseNo);
        if (error.equalsIgnoreCase("ERROR")) {
            resp.getF205Result().getResultCondition().setReasonCode(155012);
        }
        f205Client.fetchCreditDecisionForCurrentAccount(f205Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF205Control.thenReturn(resp);


    }

    @Override
    public void expectRpcForF205(RequestHeader requestHeader, int offerType) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = dataHelper.rpcRequestForF205(requestHeader);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResponse = null;
        switch (offerType) {
            case 2001:
                rpcResponse = dataHelper.rpcResponseForF205();
                break;
            case 2003:
                rpcResponse = dataHelper.rpcResponseForDownsellForF205();
                break;
            case 2002:
                rpcResponse = dataHelper.rpcResponseForUpsellForF205();
                break;
            case 2005:
                rpcResponse = dataHelper.rpcResponseForNormalForF205();
                break;
        }
       /* mockControl.matching("actual.target == 'rpc'");*/
        rpcServiceClient.retrieveProductConditions(rpcRequest);
        mockPrdControl.thenReturn(rpcResponse);
    }

    public void expectEligibilityCa(ProcessPendingArrangementEventRequest request, boolean isBFPOIndicatorPresent) throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {

        mockControl.matching("actual.target == 'eligibilityWz' && actual.methodName == 'determineEligibleCustomerInstructions' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestHeader']).isEmpty()");

        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = null;


        eligibilityRequest = dataHelper.createEligibilityRequestWhenBfpoNotPresentAndEligibilityIsTrue(request, isBFPOIndicatorPresent);


        lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse eligibilityResponse = dataHelper.eligibilityResponse();

        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);
    }

    @Override
    public void expectRpcCallForBtOffAttribute(ProductArrangement productArrangement, RequestHeader requestHeader, String btOffAttributeValue) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = rpcRequestFactory.convert(productArrangement, requestHeader);
        rpcServiceClient.retrieveProductConditions(rpcRequest);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResponse = dataHelper.rpcResponseForF205();
        rpcResponse.getProduct().get(0).getProductoffer().add(new ProductOffer());
        rpcResponse.getProduct().get(0).getProductoffer().get(0).getProductattributes().add(new ProductAttributes());
        rpcResponse.getProduct().get(0).getProductoffer().get(0).getProductattributes().get(0).setAttributeCode("BT_OFF_1");
        rpcResponse.getProduct().get(0).getProductoffer().get(0).getProductattributes().get(0).setAttributeValue(btOffAttributeValue);
        mockPrdControl.thenReturn(rpcResponse);
    }

    @Override
    public void expectVerifyCall(BalanceTransfer balanceTransfer, Customer customer, RequestHeader header, int errorCode) throws ErrorInfo, JAXBException {
        VerifyProductArrangementDetailsRequest request = verifyProductArrangementDetailsRequestFactory.convert(balanceTransfer, customer);
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders());

        VerifyProductArrangementDetailsResponse response = new VerifyProductArrangementDetailsResponse();
        if (errorCode > 0) {
            response.setResponseHeader(new ResponseHeader());
            response.getResponseHeader().setResultCondition(new ResultCondition());
            response.getResponseHeader().getResultCondition().setExtraConditions(new ExtraConditions());
            response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().add(new Condition());
            response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().get(0).setReasonCode(errorCode);
        } else {
            response.getVerificationResult().add(new com.lloydstsb.schema.enterprise.lcsm_arrangement.wz.ProductArrangement());
            response.getVerificationResult().get(0).getRoles().add(new InvolvedPartyRole());
            response.getVerificationResult().get(0).getRoles().add(new InvolvedPartyRole());
            response.getVerificationResult().get(0).getRoles().get(1).setInvolvedParty(new InvolvedParty());
            response.getVerificationResult().get(0).getRoles().get(1).getInvolvedParty().getContactPreferences().add(new ContactPreference());
            RuleCondition condition = new RuleCondition();
            condition.setName("AVS_RESULT");
            condition.setResult("PASSED");
            response.getVerificationResult().get(0).getRoles().get(1).getInvolvedParty().getContactPreferences().get(0).getHasObjectConditions().add(condition);
        }
        mockControl.matching("actual.target == 'arrangementSetupWz'");
        arrangementClient.verifyProductArrangementDetails(request, contactPoint, serviceRequest, securityHeaderType, bapiInformation);
        mockArrangementSetupControl.thenReturn(response);
    }

    @Override
    public void expectIssueInPaymentCall(String btOffAttributeValue, BalanceTransfer balanceTransfer, String sourceCreditCard, RequestHeader header, int errorCode) throws com.lloydstsb.schema.enterprise.lcsm_financialtransactionprocessing.ErrorInfo {
        IssueInpaymentInstructionRequest issueInpaymentInstructionRequest = issueInPaymentInstRequestFactory.convert(btOffAttributeValue, balanceTransfer, sourceCreditCard);
        IssueInpaymentInstructionResponse response = new IssueInpaymentInstructionResponse();
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultCondition(new ResultCondition());
        response.getResponseHeader().getResultCondition().setExtraConditions(new ExtraConditions());
        response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().add(new Condition());
        response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().get(0).setSeverityCode((byte) errorCode);
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders());
        paymentProcessingClient.issueInPaymentInstruction(issueInpaymentInstructionRequest, contactPoint, serviceRequest, securityHeaderType, bapiInformation);
        mockControl.thenReturn(response);
    }

    @Override
    @Transactional
    public void expectReferralDetails(long taskId, String appId) {
        ReferralTeams referralTeams1 = new ReferralTeams();
        referralTeams1.setId(1);
        referralTeams1.setPriority(5l);
        referralTeams1.setOuId("123");
        referralTeams1.setTaskType("102");
        referralTeamsDao.save(referralTeams1);
        referralTeamsDao.findAll();
        Referrals referrals = new Referrals();
        referrals.setApplications(applicationDao.findOne(Long.valueOf(appId)));
        referrals.setTmsTaskId(taskId);
        referrals.setReferralStatus(new ReferralStatus("PEN"));
        referrals.setReferralTeams(referralTeams1);
        referrals.setModifiedDate(new Date());
        referralsDao.save(referrals);
        referralsDao.findAll();
    }

    @Override
    public Q028Resp expectQ028Call(Customer customer, String asmDecision, RequestHeader header, int errorCode) {
        Q028Req q028Req = dataHelper.createQ028Request(customer);
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        q028Client.retrieveLoanDetails(q028Req, contactPoint, serviceRequest, securityHeaderType);
        Q028Resp q028Resp = new Q028Resp();
        if (errorCode > 0) {
            Q028Result q028Result = new Q028Result();
            ResultCondition resultCondition = new ResultCondition();
            resultCondition.setReasonCode(errorCode);
            q028Result.setResultCondition(resultCondition);
            q028Resp.setQ028Result(q028Result);
        } else {
            q028Resp = dataHelper.createQ028Response();
            q028Resp.getApplicationDetails().setASMCreditScoreResultCd(asmDecision);
            q028Resp.getApplicationDetails().setLoanApplnStatusCd(23);
        }
        mockPadQ028Control.thenReturn(q028Resp);
        return q028Resp;
    }

    @Override
    public void expectB233Call(Q028Resp q028Resp, RequestHeader header) {
        StB233ALoanIllustrate b233Req = b233RequestFactory.convert(q028Resp, header);
        mockControl.matching("actual.target == 'fsLoan' && actual.methodName == 'b233LoanIllustrate'");
        loanClient.b233BLoanIllustrate(b233Req);
        mockFsLoanControl.thenReturn(new StB233BLoanIllustrate());
    }

    @Override
    public String expectLookUpValueForDeclineTemplate() {
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setChannel("LTB");
        referenceDataLookUp.setLookupValueDesc("F02");
        referenceDataLookUp.setDescription("Decline emails for LRA");
        referenceDataLookUp.setGroupCode("DECLINE_EMAILS_LRA");
        referenceDataLookUp.setLookupText("Generic");
        referenceDataLookUp.setLookupId(3520l);
        referenceDataLookUp.setSequence(1l);
        referenceDataLookUpDao.save(referenceDataLookUp);
        return "F02";
    }

    @Override
    public F595Resp expectF595Call(String custId, RequestHeader header, int errorCode) {
        F595Req f595Req = dataHelper.createF595Req(custId);
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        f595Client.retrievePersonalDetails(f595Req, contactPoint, serviceRequest, securityHeaderType);
        F595Resp f595Resp = new F595Resp();
        if (errorCode > 0) {
            F595Result f595Result = new F595Result();
            ResultCondition resultCondition = new ResultCondition();
            resultCondition.setSeverityCode((byte) errorCode);
            f595Result.setResultCondition(resultCondition);
            f595Resp.setF595Result(f595Result);
        } else {
            f595Resp = dataHelper.createF595Resp();
        }
        mockOcisF595Control.thenReturn(f595Resp);
        return f595Resp;
    }

    public void sleep() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
