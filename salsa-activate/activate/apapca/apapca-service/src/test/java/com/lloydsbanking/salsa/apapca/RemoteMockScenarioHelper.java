package com.lloydsbanking.salsa.apapca;

import com.lloydsbanking.salsa.activate.administer.convert.X741RequestFactory;
import com.lloydsbanking.salsa.activate.communication.convert.CommunicationRequestFactory;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.converter.F060RequestFactory;
import com.lloydsbanking.salsa.activate.converter.F061RespToF062ReqConverter;
import com.lloydsbanking.salsa.activate.downstream.PrdClient;
import com.lloydsbanking.salsa.activate.postfulfil.convert.*;
import com.lloydsbanking.salsa.activate.postfulfil.downstream.RecordDocumentMetaContent;
import com.lloydsbanking.salsa.activate.registration.converter.B750RequestResponseConverter;
import com.lloydsbanking.salsa.activate.registration.converter.B751RequestFactory;
import com.lloydsbanking.salsa.activate.sira.convert.SiraRequestFactory;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apapca.service.convert.H071RequestFactory;
import com.lloydsbanking.salsa.apapca.service.fulfil.converter.CreateCaseRequestFactory;
import com.lloydsbanking.salsa.apapca.service.fulfil.converter.DepositArrangementToE226Request;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydsbanking.salsa.downstream.application.client.ApplicationClient;
import com.lloydsbanking.salsa.downstream.asm.client.f425.F425Client;
import com.lloydsbanking.salsa.downstream.cbs.client.e226.E226Client;
import com.lloydsbanking.salsa.downstream.cbs.client.e229.E229Client;
import com.lloydsbanking.salsa.downstream.cbs.client.e469.E469Client;
import com.lloydsbanking.salsa.downstream.cm.client.ScheduleCommunicationClient;
import com.lloydsbanking.salsa.downstream.cm.client.SendCommunicationClient;
import com.lloydsbanking.salsa.downstream.cmas.client.c808.C808Client;
import com.lloydsbanking.salsa.downstream.cmas.client.c812.C812Client;
import com.lloydsbanking.salsa.downstream.cmas.client.c818.C818Client;
import com.lloydsbanking.salsa.downstream.cmas.client.c846.C846Client;
import com.lloydsbanking.salsa.downstream.dp.client.encrypt.EncryptClient;
import com.lloydsbanking.salsa.downstream.fsou.client.FsOuClient;
import com.lloydsbanking.salsa.downstream.ocis.client.c234.C234Client;
import com.lloydsbanking.salsa.downstream.ocis.client.c241.C241Client;
import com.lloydsbanking.salsa.downstream.ocis.client.c658.C658Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f060.F060Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062Client;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferralTeamsDao;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import com.lloydsbanking.salsa.downstream.pega.client.PegaClient;
import com.lloydsbanking.salsa.downstream.sira.client.SiraClient;
import com.lloydsbanking.salsa.downstream.soa.servicearrangement.client.CSAClient;
import com.lloydsbanking.salsa.downstream.soadms.client.documentmanager.SOADocumentManagerClient;
import com.lloydsbanking.salsa.downstream.soaipm.client.involvedpartymanager.IPMClient;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.downstream.switches.jdbc.SwitchDao;
import com.lloydsbanking.salsa.downstream.switches.model.SwitchDto;
import com.lloydsbanking.salsa.downstream.tms.client.x741.X741Client;
import com.lloydsbanking.salsa.remotemock.*;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Req;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Resp;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.E226Req;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.E229Req;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.E229Resp;
import com.lloydsbanking.salsa.soap.cbs.e469.objects.E469Req;
import com.lloydsbanking.salsa.soap.cbs.e469.objects.E469Resp;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.*;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.*;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.CardDeliveryAddress;
import com.lloydsbanking.salsa.soap.encrpyt.objects.*;
import com.lloydsbanking.salsa.soap.fs.ou.StBranchDetail;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Req;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Resp;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Req;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Resp;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.StPartyRelData;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Req;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Resp;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Req;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Resp;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Req;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import com.lloydsbanking.salsa.soap.pega.objects.CreateCaseRequestType;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RecordDocumentMetaContentRequest;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RetrieveDocumentMetaContentRequest;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsRequest;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsResponse;
import com.lloydsbanking.salsa.soap.soa.servicearrangement.CreateServiceArrangementRequest;
import com.lloydsbanking.salsa.soap.soa.servicearrangement.CreateServiceArrangementResponse;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.CreateCasePayloadResponseType;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.InitiateSwitchInType;
import com.lloydstsb.ib.wsbridge.account.StB765BAccCreateAccount;
import com.lloydstsb.ib.wsbridge.application.StB750AAppPerCCRegCreate;
import com.lloydstsb.ib.wsbridge.application.StB750BAppPerCCRegCreate;
import com.lloydstsb.ib.wsbridge.application.StB751AAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.ou.StH071AGetSortCodeByCoordinates;
import com.lloydstsb.ib.wsbridge.ou.StH071BGetSortCodeByCoordinates;
import com.lloydstsb.schema.casetracking.ifw.GenericResponseType;
import com.lloydstsb.schema.enterprise.ifwxml.ResponseHeader;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ServiceArrangement;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreation;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreationResponse;
import com.synectics_solutions.sira.schemas.realtime.core.v1_0.realtimeresulttype4.RealtimeResultType4Type;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.AuthenticationHeader;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.Source;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.SubmitWorkItemResponse;
import lib_sim_bo.businessobjects.*;
import lib_sim_communicationmanager.id_communicationeventscheduler.*;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_communicationmanager.messages.ScheduleCommunicationRequest;
import lib_sim_communicationmanager.messages.SendCommunicationRequest;
import lib_sim_communicationmanager.messages.SendCommunicationResponse;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.apache.cxf.common.util.StringUtils;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class RemoteMockScenarioHelper implements ScenarioHelper {
    @Autowired
    RecordDocumentMetaContent recordDocumentMetaContent;
    @Autowired
    SiraClient siraClient;
    @Autowired
    RecordDocumentMetaContentRequestBuilder recordDocumentMetaContentRequestBuilder;
    @Autowired
    B750RequestResponseConverter b750RequestResponseConverter;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    MockControlSiraServicePortType mockSiraControl;
    @Autowired
    SiraRequestFactory siraRequestFactory;
    @Autowired
    PrdClient prdClient;

    @Autowired
    MockControlRpcServicePortType mockRpcControl;

    @Autowired
    MockControlCbsE226ServicePortType mockCbsE226Control;

    Map<String, E226Client> cbsE226ClientMap;

    @Autowired
    AccountClient accountClient;

    @Autowired
    C808Client c808Client;

    @Autowired
    C846Client c846Client;

    @Autowired
    C812Client c812Client;

    @Autowired
    C818Client c818Client;

    @Autowired
    C234Client c234Client;

    @Autowired
    PegaClient pegaclient;

    @Autowired
    MockControlServicePortType mockControl;

    Map<String, E229Client> cbsE229ClientMap;

    @Autowired
    MockControlCbsE229ServicePortType mockCbsE229Control;

    @Autowired
    MockControlCmasC808ServicePortType mockCmasC808Control;

    @Autowired
    MockControlCmasC812ServicePortType mockCmasC812Control;

    @Autowired
    MockControlEncryptDataServicePortType mockControlEncryptDataServicePortType;

    @Autowired
    MockControlCmasC818ServicePortType mockCmasC818Control;

    @Autowired
    MockControlCmasC846ServicePortType mockCmasC846Control;

    Map<String, E469Client> cbsE469ClientMap;

    @Autowired
    MockControlCbsE469ServicePortType mockCbsE469Control;

    @Autowired
    MockControlAsmF425ServicePortType mockAsmF425Control;


    @Autowired
    MockControlTmsX741ServicePortType mockTmsX741Control;

    @Autowired
    E229Client e229ClientVer;

    @Autowired
    E229Client e229ClientBos;

    @Autowired
    E229Client e229ClientHlx;

    @Autowired
    E229Client e229ClientLtb;

    @Autowired
    SwitchService switchClient;
    @Autowired
    MockControlFsAccountServicePortType mockFsAccountControl;
    @Autowired
    ScheduleCommunicationClient scheduleCommunicationClient;
    @Autowired
    SendCommunicationClient sendCommunicationClient;
    @Autowired
    EncryptClient encryptClient;
    @Autowired
    TestDataHelper dataHelper;
    @Autowired
    ApplicationClient applicationClient;
    @Autowired
    F425Client f425Client;
    @Autowired
    MockControlFsApplicationServicePortType mockControlFsApplicationServicePortType;
    @Autowired
    CommunicationRequestFactory communicationRequestFactory;
    @Autowired
    ReferralTeamsDao referralTeamsDao;
    @Autowired
    F062Client f062Client;
    @Autowired
    F061Client f061Client;
    @Autowired
    C241Client c241Client;
    @Autowired
    IPMClient soaIpmClient;
    @Autowired
    SOADocumentManagerClient soaDocumentManagerClient;
    @Autowired
    F061RespToF062ReqConverter f061RespToF062ReqConverter;
    @Autowired
    H071RequestFactory requestFactory;
    @Autowired
    CreateServiceArrangementRequestFactory createServiceArrangementRequestFactory;
    @Autowired
    MockControlSoaServiceArrangementSetupServicePortType mockControlSoaServiceArrangementSetupServicePortType;
    @Autowired
    CreateCaseRequestFactory createCaseRequestFactory;
    @Autowired
    CSAClient csaClient;
    @Autowired
    MockControlOcisF061ServicePortType mockOcisF061Control;
    @Autowired
    MockControlOcisF062ServicePortType mockOcisF062Control;
    @Autowired
    MockControlSoaDMSServicePortType mockControlSoaDMSServicePortType;
    @Value("${salsa.fs.boxid}")
    int boxId;
    @Autowired
    RetrievePamService retrievePamService;
    @Autowired
    ReferenceDataLookUpDao referenceDataLookUpDao;
    @Autowired
    X741RequestFactory x741RequestFactory;
    @Autowired
    X741Client x741Client;
    @Autowired
    B751RequestFactory b751RequestFactory;
    @Autowired
    C658RequestFactory c658RequestFactory;
    @Autowired
    C234RequestFactory c234RequestFactory;
    @Autowired
    F060RequestFactory f060RequestFactory;
    @Autowired
    RetrieveInvolvedPartyDetailsRequestFactory retrieveInvolvedPartyDetailsRequestFactory;
    @Autowired
    RecordInvolvedPartyDetailsRequestFactory recordInvolvedPartyDetailsRequestFactory;
    @Autowired
    C658Client c658Client;
    @Autowired
    F060Client f060Client;
    @Autowired
    MockControlOcisC658ServicePortType c658ServicePortType;
    @Autowired
    MockControlOcisC234ServicePortType c234ServicePortType;
    @Autowired
    MockControlOcisC241ServicePortType c241ServicePortType;
    @Autowired
    MockControlOcisF060ServicePortType f060ServicePortType;
    @Autowired
    MockControlPegaServicePortType pegaControlServicePortType;
    @Autowired
    MockControlSoaInvolvedPartyManagementServicePortType involvedPartyManagementServicePortType;
    @Autowired
    MockControlSendCommunicationManagerServicePortType mockControlSendCommunicationManager;
    @Autowired
    MockControlScheduleCommunicationManagerServicePortType mockControlScheduleCommunicationManager;
    @Autowired
    private SwitchDao switchDao;
    @Autowired
    DepositArrangementToE226Request depositArrangementToE226Request;
    @Autowired
    FsOuClient fsOuClient;
    @Autowired
    MockControlFsOuServicePortType mockControlFsOuServicePortType;

    @Override
    @Transactional
    @Modifying
    @Rollback(false)
    public void clearUp() {
        dataHelper.cleanUp();
        referenceDataLookUpDao.deleteAll();
        switchDao.deleteAll();
        clearUpForWPS();
    }

    public void clearUpForWPS() {

    }

    @Override
    @Modifying
    public void expectFATCAUpdateSwitchCall(String channel, boolean switchStatus) {
        SwitchDto switchDto = new SwitchDto("LBG", "SW_FATCAupdate", new Date(), boxId, 1, "A", switchStatus ? "1" : "0");
        switchDao.save(switchDto);
    }

    @Override
    @Modifying
    public void expectDurableMediumSwitchCall(String channel, boolean switchStatus) {
        SwitchDto switchDto = new SwitchDto("LBG", "SW_EnSTPPCAWcMl", new Date(), boxId, 1, "A", switchStatus ? "1" : "0");
        switchDao.save(switchDto);
    }

    @Override
    @Modifying
    public boolean expectCRSSwitchCall(String channel, boolean switchStatus) {
        SwitchDto switchDto = new SwitchDto("LTB", "SW_EnDispKYCDtls", new Date(), boxId, 1, "A", switchStatus ? "1" : "0");
        switchDao.save(switchDto);
        return switchStatus;
    }

    @Override
    public String expectChannelIdByContactPointID() {
        String channelId = "LTB";
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("Cnt_Pnt_Prtflio", "0000777505", "Acquire Contry Name", new Long("132356"), "Bahrain", channelId, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
        return channelId;
    }

    @Override
    public void expectLookUpValues() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 1L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "1", "Purpose of Account", 2L, "SPORI", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "2", "Purpose of Account", 3L, "BIEXP", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("REFERRAL_TEAM_GROUPS", "209178", "Purpose of Account", 4L, "002", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("REFERRAL_REASON_CODE", "6363", "CMAS Referral Codes", 5L, "REFERRAL_REASON_CODE", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("REFERRAL_TEAM_GROUPS", "206536", "Purpose of Account", 6L, "003", "LTB", 1L));

    }

    @Override
    public void expectLookUpValuesWithLifeStyleBenefitCode() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 1L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "1", "Purpose of Account", 2L, "SPORI", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "2", "Purpose of Account", 3L, "BIEXP", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("REFERRAL_TEAM_GROUPS", "3", "Purpose of Account", 4L, "002", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("LIFE_STYLE_BENEFIT", "4", "Purpose of Account", 5L, "002", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("REFERRAL_REASON_CODE", "6363", "CMAS Referral Codes", 5L, "REFERRAL_REASON_CODE", "LTB", 1L));
    }

    @Override
    public void expectLookUpValuesWithIntendToSwitch() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 1L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "1", "Purpose of Account", 2L, "SPORI", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "2", "Purpose of Account", 3L, "BIEXP", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("REFERRAL_TEAM_GROUPS", "3", "Purpose of Account", 4L, "002", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("LIFE_STYLE_BENEFIT", "4", "Purpose of Account", 5L, "002", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("INTEND_TO_SWITCH", "5", "Purpose of Account", 6L, "002", "LTB", 1L));
    }

    @Override
    public void expectLookUpValuesWithBenefitMessages() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 1L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "1", "Purpose of Account", 2L, "SPORI", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "2", "Purpose of Account", 3L, "BIEXP", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("REFERRAL_TEAM_GROUPS", "3", "Purpose of Account", 4L, "002", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("LIFE_STYLE_BENEFIT", "4", "Purpose of Account", 5L, "002", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ALERT_MSGES", "5", "Purpose of Account", 6L, "002", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("REFERRAL_REASON_CODE", "6363", "CMAS Referral Codes", 5L, "REFERRAL_REASON_CODE", "LTB", 1L));
    }

    @Override
    public void expectLookUpValuesForEvidenceAndPurposeData() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PTY_EVIDENCE_CODE", "8", "ABC", 5L, "0011", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ADD_EVIDENCE_CODE", "6", "DEF", 6L, "0031", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PTY_PURPOSE_CODE", "9", "JKL", 7L, "0041", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ADD_PURPOSE_CODE", "7", "LMN", 8L, "0051", "LTB", 1L));
    }

    @Override
    public void expectLookUpValuesWithInCorrectGroupCodes() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP1", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 513L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT1", "1", "Purpose of Account", 1091L, "SPORI", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT1", "2", "Purpose of Account", 1092L, "BIEXP", "LTB", 1L));
    }

    @Override
    public BigInteger expectB750Call(RequestHeader requestHeader, ProductArrangement productArrangement) {
        StB750AAppPerCCRegCreate b750Request = b750RequestResponseConverter.createB750Request(requestHeader, productArrangement.getPrimaryInvolvedParty(), productArrangement.getAccountNumber(), 0, "C");
        StB750BAppPerCCRegCreate b750Response = dataHelper.createB750Response();
        mockB750Call(b750Request, b750Response);
        return b750Response.getAppid();
    }

    private void mockB750Call(StB750AAppPerCCRegCreate b750Request, StB750BAppPerCCRegCreate b750Response) {
        mockControl.matching("actual.target == 'fsApplication' && actual.methodName == 'b750AppPerCCRegCreate'");
        applicationClient.createIBApplication(b750Request);
        mockControlFsApplicationServicePortType.thenReturn(b750Response);
    }

    @Override
    @Transactional
    public void expectApplicationIdFromReferralsTmsTaskId(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String subStatus, String niNumber) {
        ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");
        dataHelper.createApplication(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber, productTypesCurrent);
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
    public List<ReferralTeams> expectReferralsTeamDetailsForSira(String name) {
        ReferralTeams referralTeams1 = new ReferralTeams();
        referralTeams1.setId(1);
        referralTeams1.setPriority(6l);
        referralTeams1.setOuId("123");
        referralTeams1.setTaskType("206536");
        referralTeams1.setName(name);
        referralTeamsDao.save(referralTeams1);
        return referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc(name);
    }

    @Override
    public void expectB766Call(RequestHeader requestHeader, String sortCode) {
        mockControl.matching("actual.target == 'fsAccount' && actual.methodName == 'b766RetrieveCBSAppGroup'");
        accountClient.retrieveCBSAppGroup(dataHelper.createB766Request(requestHeader, sortCode));
        mockFsAccountControl.thenReturn(dataHelper.createB766Response(dataHelper.getCBSAppGrpForSortCode(sortCode), 0));
    }

    @Override
    @Transactional
    public long expectApplicationDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String subStatus, String niNumber) {
        return dataHelper.createApplicationCA(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber);
    }

    @Override
    @Transactional
    public Applications expectApplicationDetail(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String subStatus, String niNumber, ProductTypes productType) {
        return dataHelper.createNewApplication(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber, productType);
    }

    @Override
    @Transactional
    public ProductArrangement expectApplicationDetailsHavingPamAndRequestData(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        Applications applications = dataHelper.createNewApplication(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber, productType);
        ProductArrangement productArrangement;
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
            customer.getIsPlayedBy().setCustomerLocation(upStreamCustomer.getIsPlayedBy().getCustomerLocation());
        }

        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.SAVINGS.getValue())) {
            productArrangement.getOfferedProducts().addAll(upstreamRequest.getProductArrangement().getOfferedProducts());
        }

        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CURRENT_ACCOUNT.getValue())) {
            String brandName = productArrangement.getAssociatedProduct().getBrandName();
            productArrangement.setAssociatedProduct(upstreamRequest.getProductArrangement().getAssociatedProduct());
            productArrangement.getAssociatedProduct().setBrandName(brandName);
            productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setCustomerDeviceDetails(upStreamCustomer.getIsPlayedBy().getCustomerDeviceDetails());
            productArrangement.setSIRAEnabledSwitch(upstreamRequest.getProductArrangement().isSIRAEnabledSwitch());
            productArrangement.setApplicationSubStatus(upstreamRequest.getProductArrangement().getApplicationSubStatus());
            ((DepositArrangement) productArrangement).setAccountSwitchingDetails(((DepositArrangement) upstreamRequest.getProductArrangement()).getAccountSwitchingDetails());
        }


        DepositArrangement depositArrangement = (DepositArrangement) productArrangement;

        if (!CollectionUtils.isEmpty(upstreamRequest.getProductArrangement().getConditions())) {
            productArrangement.getConditions().addAll(upstreamRequest.getProductArrangement().getConditions());


        }
        if (null != depositArrangement.isIsOverdraftRequired() && depositArrangement.isIsOverdraftRequired()
                && (upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER)
                || upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER))) {

            RuleCondition ruleCondition = new RuleCondition();
            ruleCondition.setName("ODPCI_VIEWED");
            ruleCondition.setResult("Y");
            productArrangement.getConditions().add(ruleCondition);
        }
        productArrangement.setFinancialInstitution(upstreamRequest.getProductArrangement().getFinancialInstitution());

        return productArrangement;
    }


    @Override
    @Transactional
    public ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithIntendToSwitch(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        Applications applications = dataHelper.createNewApplicationWithIntendToSwitch(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber, productType);
        ProductArrangement productArrangement;
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
            customer.getIsPlayedBy().setCustomerLocation(upStreamCustomer.getIsPlayedBy().getCustomerLocation());
        }

        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.SAVINGS.getValue())) {
            productArrangement.getOfferedProducts().addAll(upstreamRequest.getProductArrangement().getOfferedProducts());
        }

        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CURRENT_ACCOUNT.getValue())) {
            String brandName = productArrangement.getAssociatedProduct().getBrandName();
            productArrangement.setAssociatedProduct(upstreamRequest.getProductArrangement().getAssociatedProduct());
            productArrangement.getAssociatedProduct().setBrandName(brandName);
            ((DepositArrangement) productArrangement).setAccountSwitchingDetails(((DepositArrangement) upstreamRequest.getProductArrangement()).getAccountSwitchingDetails());
        }


        DepositArrangement depositArrangement = (DepositArrangement) productArrangement;

        if (!CollectionUtils.isEmpty(upstreamRequest.getProductArrangement().getConditions())) {
            productArrangement.getConditions().addAll(upstreamRequest.getProductArrangement().getConditions());


        }
        if (null != depositArrangement.isIsOverdraftRequired() && depositArrangement.isIsOverdraftRequired()
                && (upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER)
                || upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER))) {

            RuleCondition ruleCondition = new RuleCondition();
            ruleCondition.setName("ODPCI_VIEWED");
            ruleCondition.setResult("Y");
            productArrangement.getConditions().add(ruleCondition);
        }
        productArrangement.setFinancialInstitution(upstreamRequest.getProductArrangement().getFinancialInstitution());

        return productArrangement;
    }


    @Override
    @Transactional
    public ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithRelatedAplicationId(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        Applications applications = dataHelper.createNewApplication(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber, productType);
        dataHelper.createRelatedApplications(applications, applicationStatus);
        ProductArrangement productArrangement;
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
            customer.getIsPlayedBy().setCustomerLocation(upStreamCustomer.getIsPlayedBy().getCustomerLocation());
        }

        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.SAVINGS.getValue())) {
            productArrangement.getOfferedProducts().addAll(upstreamRequest.getProductArrangement().getOfferedProducts());
        }

        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CURRENT_ACCOUNT.getValue())) {
            String brandName = productArrangement.getAssociatedProduct().getBrandName();
            productArrangement.setAssociatedProduct(upstreamRequest.getProductArrangement().getAssociatedProduct());
            productArrangement.getAssociatedProduct().setBrandName(brandName);
            ((DepositArrangement) productArrangement).setAccountSwitchingDetails(((DepositArrangement) upstreamRequest.getProductArrangement()).getAccountSwitchingDetails());
        }


        DepositArrangement depositArrangement = (DepositArrangement) productArrangement;

        if (!CollectionUtils.isEmpty(upstreamRequest.getProductArrangement().getConditions())) {
            productArrangement.getConditions().addAll(upstreamRequest.getProductArrangement().getConditions());
        }
        if (null != depositArrangement.isIsOverdraftRequired() && depositArrangement.isIsOverdraftRequired()
                && (upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER)
                || upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER))) {

            RuleCondition ruleCondition = new RuleCondition();
            ruleCondition.setName("ODPCI_VIEWED");
            ruleCondition.setResult("Y");
            productArrangement.getConditions().add(ruleCondition);
        }
        productArrangement.setFinancialInstitution(upstreamRequest.getProductArrangement().getFinancialInstitution());

        return productArrangement;
    }


    @Override
    @Transactional
    public ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithoutIbRegistrationDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        Applications applications = dataHelper.createNewApplicationWithoutIbregistrationData(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber, productType);
        ProductArrangement productArrangement;
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

        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.SAVINGS.getValue())) {
            productArrangement.getOfferedProducts().addAll(upstreamRequest.getProductArrangement().getOfferedProducts());
        }

        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CURRENT_ACCOUNT.getValue())) {
            String brandName = productArrangement.getAssociatedProduct().getBrandName();
            productArrangement.setAssociatedProduct(upstreamRequest.getProductArrangement().getAssociatedProduct());
            productArrangement.getAssociatedProduct().setBrandName(brandName);
            ((DepositArrangement) productArrangement).setAccountSwitchingDetails(((DepositArrangement) upstreamRequest.getProductArrangement()).getAccountSwitchingDetails());
        }


        DepositArrangement depositArrangement = (DepositArrangement) productArrangement;

        if (!CollectionUtils.isEmpty(upstreamRequest.getProductArrangement().getConditions())) {
            productArrangement.getConditions().addAll(upstreamRequest.getProductArrangement().getConditions());
        }
        if (null != depositArrangement.isIsOverdraftRequired() && depositArrangement.isIsOverdraftRequired()
                && (upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER)
                || upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER))) {

            RuleCondition ruleCondition = new RuleCondition();
            ruleCondition.setName("ODPCI_VIEWED");
            ruleCondition.setResult("Y");
            productArrangement.getConditions().add(ruleCondition);
        }
        productArrangement.setFinancialInstitution(upstreamRequest.getProductArrangement().getFinancialInstitution());

        return productArrangement;
    }


    private void expectE229Call(String channel, E229Resp e229Resp, E229Req e229Req, ContactPoint contactPoint, ServiceRequest serviceRequest, SecurityHeaderType securityHeaderType, CBSAppGrp cbsAppGrp) {
        e229Client(channel).proposeAccount(e229Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        mockCbsE229Control.thenReturn(e229Resp);
    }

    @Override
    public String expectE229Call(RequestHeader header, String sortCode) {
        expectE229Call(header.getChannelId(), dataHelper.createE229RespWithoutError(), dataHelper.createE229Request(sortCode), dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), dataHelper.createCBSAppGroupFromSortCode(sortCode));
        return dataHelper.getAccountNumberFromE229Resp(dataHelper.createE229RespWithoutError());
    }

    @Override
    public void expectF425Call(RequestHeader header, String requestNo, String sourceSystemCode, String asmDecisionCode, String asmCreditScore) {
        F425Req f425Req = dataHelper.createF425Req(requestNo, sourceSystemCode);
        mockControl.matching("actual.target == 'asmF425'");
        f425Client.f425(f425Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockAsmF425Control.thenReturn(dataHelper.createF425Resp(asmDecisionCode, asmCreditScore));
    }

    @Override
    public void expectF425CallWithError(RequestHeader header, String requestNo, String sourceSystemCode) {
        F425Req f425Req = dataHelper.createF425Req(requestNo, sourceSystemCode);
        mockControl.matching("actual.target == 'asmF425'");
        f425Client.f425(f425Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        F425Resp f425Resp = dataHelper.createF425Resp();
        f425Resp.getF425Result().getResultCondition().setSeverityCode((byte) 1);
        mockAsmF425Control.thenReturn(f425Resp);
    }

    private E229Client e229Client(String channel) {
        return cbsE229ClientMap.get(channel);
    }

    public Map<String, E229Client> getCbsE229ClientMap() {
        return cbsE229ClientMap;
    }

    public void setCbsE229ClientMap(Map<String, E229Client> cbsE229ClientMap) {
        this.cbsE229ClientMap = cbsE229ClientMap;
    }

    @Override
    @Transactional
    public void expectReferenceDataForPAM() {
        dataHelper.createPamReferenceData();
    }

    @Override
    public void expectE469Call(RequestHeader header, String contactPointID) {
        expectE469Call(header.getChannelId(), dataHelper.createE469Resp(), dataHelper.createE469Request(contactPointID), dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), dataHelper.createCBSAppGroupFromSortCode(contactPointID.substring(4, 10)));

    }

    @Override
    public void expectC808Call(String sortCode, String accountNumber, long custID, RequestHeader header) {
        c808Client.initiateCardOrder(dataHelper.createC808Request(sortCode, accountNumber, custID), dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockCmasC808Control.thenReturn(dataHelper.createC808Res());
    }

    @Override
    public void expectC846Call(String prodID, String ccApplicableIndicator, String decisionText, int decisionCode, long custID, RequestHeader header) {
        c846Client.retrieveEligibleCards(dataHelper.createC846Request(prodID, ccApplicableIndicator, decisionText, decisionCode, custID), dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockCmasC846Control.thenReturn(dataHelper.createC846Response());
    }

    @Override
    public void expectC812Call(String prodID, String ccApplicableIndicator, String sortCode, long reasonCode, int sysCode, long custID, String accountNumber, RequestHeader header) {
        C812Req c812Req = dataHelper.createC812Request(createCardOrderNew(custID, sortCode, sysCode, prodID, accountNumber), createCardOrderCBSData(ccApplicableIndicator), null);
        c812Client.validateCardOrder(c812Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        C812Resp c812Resp = dataHelper.createC812Response();
        c812Resp.getCardOrderReferralReasons().getCardOrderReferralReason().get(0).setCardOrderReferralReasonCd(reasonCode);
        mockCmasC812Control.thenReturn(c812Resp);
    }

    @Override
    public void expectC818Call(String prodID, String ccApplicableIndicator, long custId, String sortCode, int sysCode, String accountNo, RequestHeader header) {
        C818Req c818Req = dataHelper.createC818Request(cardOrderAdd(custId, sortCode, sysCode, accountNo), createCardOrderAddNew(), createCardOrderCBSCCA(ccApplicableIndicator), new CardDeliveryAddress(), dataHelper.createCardOrderActions());

        c818Client.fulfilCardOrder(c818Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        C818Resp c818Resp = dataHelper.createC818Resp();
        mockCmasC818Control.thenReturn(c818Resp);
    }

    @Override
    public void expectC812CallWithSortCode(String prodID, String ccApplicableIndicator, String sortCode, long reasonCode, int sysCode, String accountNumber, long custID, RequestHeader header) {
        C812Req c812Req = dataHelper.createC812Request(createCardOrderNew(custID, sortCode, sysCode, prodID, accountNumber), createCardOrderCBSData(ccApplicableIndicator), new CardOrderCBSAddress());

        c812Client.validateCardOrder(c812Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        C812Resp c812Resp = dataHelper.createC812Response();
        c812Resp.getCardOrderReferralReasons().getCardOrderReferralReason().get(0).setCardOrderReferralReasonCd(reasonCode);
        mockCmasC812Control.thenReturn(c812Resp);
    }

    @Override
    @Transactional
    public void expectRelatedApplicationDetails(Applications applications, ApplicationStatus applicationStatus) {
        dataHelper.createRelatedApplications(applications, applicationStatus);
    }

    private void expectE469Call(String channel, E469Resp e469Resp, E469Req e469Req, ContactPoint contactPoint, ServiceRequest serviceRequest, SecurityHeaderType securityHeaderType, CBSAppGrp cbsAppGrp) {
        e469Client(channel).retrieveAlternateSortCode(e469Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        mockCbsE469Control.thenReturn(e469Resp);

    }

    private CardOrderAddNew createCardOrderAddNew() {
        CardOrderAddNew cardOrderAddNew = new CardOrderAddNew();
        cardOrderAddNew.setPINRequiredIn("1");
        cardOrderAddNew.setPINServiceCd(1);
        return cardOrderAddNew;
    }

    private CardOrderCBSCCA createCardOrderCBSCCA(String ccApplicableIndicator) {
        CardOrderCBSCCA cardOrderCBSCCA = new CardOrderCBSCCA();
        cardOrderCBSCCA.setCCAApplicableIn(ccApplicableIndicator);
        return cardOrderCBSCCA;
    }

    private CardOrderNew createCardOrderNew(long custID, String sortCode, int sysCode, String prodId, String accountNumber) {
        CardOrderNew cardOrderNew = dataHelper.createCardOrderNew();
        cardOrderNew.setCardAuthorisingPartyId(Long.valueOf(227323270));
        cardOrderNew.setCardHoldingPartyId(Long.valueOf(227323270));
        cardOrderNew.setPlasticTypeCd(0);
        cardOrderNew.getCardOrderAccount().setSortCd(sortCode);
        cardOrderNew.getCardOrderAccount().setAccountNo8(accountNumber);
        cardOrderNew.getCardOrderAccount().setProdExtSysId(sysCode);
        cardOrderNew.getCardOrderAccount().setExtProdIdTx(prodId);
        return cardOrderNew;
    }

    private CardOrderNew createCardOrderNewWithSubstatus1025(long custID, String sortCode, int sysCode, String prodId, String accountNumber) {
        CardOrderNew cardOrderNew = dataHelper.createCardOrderNew();
        cardOrderNew.setCardAuthorisingPartyId(Long.valueOf(227323270));
        cardOrderNew.setCardHoldingPartyId(Long.valueOf(227323270));
        cardOrderNew.setPlasticTypeCd(0);
        cardOrderNew.getCardOrderAccount().setSortCd(sortCode);
        cardOrderNew.getCardOrderAccount().setAccountNo8(accountNumber);
        cardOrderNew.getCardOrderAccount().setProdExtSysId(sysCode);
        cardOrderNew.getCardOrderAccount().setExtProdIdTx(prodId);
        return cardOrderNew;
    }

    private CardOrderCBSData createCardOrderCBSData(String ccaApplicableIndicator) {
        CardOrderCBSData cardOrderCBSData = dataHelper.createCardOrderCBSData();
        cardOrderCBSData.setCCAApplicableIn(ccaApplicableIndicator);
        cardOrderCBSData.getCardOrderCBSDecision().setDebitCardRenewalCd(50);
        return cardOrderCBSData;
    }

    private CardOrderAdd cardOrderAdd(long custId, String sortCode, int systemCode, String accountNo) {
        CardOrderAdd cardOrderAdd = new CardOrderAdd();
        cardOrderAdd.setCardAuthorisingPartyId(custId);
        cardOrderAdd.setCardHoldingPartyId(custId);
        com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderAccount cardOrderAccount = new com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderAccount();
        cardOrderAccount.setSortCd(sortCode);
        cardOrderAccount.setAccountNo8(accountNo);
        cardOrderAccount.setProdExtSysId(systemCode);
        cardOrderAccount.setExtProdIdTx("0071776000");
        cardOrderAdd.setCardOrderAccount(cardOrderAccount);
        cardOrderAdd.setCardOrderStatusCd(1);
        cardOrderAdd.setCardOrderStatusDsCd(0);
        cardOrderAdd.setCardholderNm("FGHI/ABCDEFHI.MR");
        cardOrderAdd.setCardOrderAuthorityCd("Y");
        cardOrderAdd.setCardOrderConsentCd("A");
        cardOrderAdd.setCardOrderTypeCd(1);
        cardOrderAdd.setCardTypeCd("2");
        cardOrderAdd.setPlasticTypeCd(0);
        cardOrderAdd.setCustomerCollectIn(0);
        cardOrderAdd.setChequeBookOrderedIn("0");
        cardOrderAdd.setCardClassificationCd("P");
        cardOrderAdd.setPlasticTypeServiceLevelCd("A");
        return cardOrderAdd;
    }

    private E469Client e469Client(String channel) {
        return cbsE469ClientMap.get(channel);
    }

    public Map<String, E469Client> getCbsE469ClientMap() {
        return cbsE469ClientMap;
    }

    public void setCbsE469ClientMap(Map<String, E469Client> cbsE469ClientMap) {
        this.cbsE469ClientMap = cbsE469ClientMap;
    }

    int totalTime = 0;

    @Override
    public void verifyExpectCalls() {
        String result = mockControl.verify();
        if (!result.equals("verified") && !result.contains("VerificationResult{uninvokedInteractions=[Interaction{expectedMethodCall=MethodCall{target='tmsX741'") && totalTime < 200000) {
            try {
                totalTime = totalTime + 10000;
                Thread.sleep(10000);
                verifyExpectCalls();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!result.equals("verified") && !result.contains("VerificationResult{uninvokedInteractions=[Interaction{expectedMethodCall=MethodCall{target='tmsX741'")) {
            throw new IllegalStateException("Following Expect not called: " + result);
        }

    }

    @Override
    public void expectSendCommunicationCall(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header, String source, String communicationType, boolean isFirstCall) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, notificationEmail, header, source, communicationType);
        sendCommunicationClient.sendCommunication(sendCommunicationRequest);
        SendCommunicationResponse sendCommunicationResponse = new SendCommunicationResponse();
        sendCommunicationResponse.setIsSuccessful(true);
        mockControlSendCommunicationManager.thenReturn(sendCommunicationResponse);
    }

    @Override
    public void expectScheduleCommunicationCall(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header, String source, String communicationType) throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg {
        ScheduleCommunicationRequest scheduleCommunicationRequest = communicationRequestFactory.convertToScheduleCommunicationRequest(depositArrangement, notificationEmail, header, source, communicationType);

        mockControl.matching("actual.target == 'schedule-communication' && actual.methodName == 'scheduleCommunication'");
        scheduleCommunicationClient.scheduleCommunication(scheduleCommunicationRequest);
        lib_sim_communicationmanager.messages.ScheduleCommunicationResponse scheduleCommunicationResponse = new lib_sim_communicationmanager.messages.ScheduleCommunicationResponse();
        mockControlScheduleCommunicationManager.thenReturn(scheduleCommunicationResponse);
    }

    @Override
    public void expectSendCommunicationCall(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header, boolean isFirstCall) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, notificationEmail, header, null, "Email");
        sendCommunicationClient.sendCommunication(sendCommunicationRequest);
        SendCommunicationResponse sendCommunicationResponse = new SendCommunicationResponse();
        sendCommunicationResponse.setIsSuccessful(true);
        mockControlSendCommunicationManager.thenReturn(sendCommunicationResponse);
    }


    @Override
    public void expectSendCommunicationCallWithError(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, notificationEmail, header, null, "Email");
        sendCommunicationClient.sendCommunication(sendCommunicationRequest);
        SendCommunicationResponse sendCommunicationResponse = new SendCommunicationResponse();
        sendCommunicationResponse.setIsSuccessful(false);
        mockControlSendCommunicationManager.thenReturn(sendCommunicationResponse);
    }

    public void expectX741Call(Integer taskId, ProductArrangement productArrangement, RequestHeader header) {
        TaskCreation x741Req = x741RequestFactory.convert(productArrangement);
        mockControl.matching("actual.target == 'tmsX741'");
        x741Client.x741(x741Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), dataHelper.getBapiInformationFromRequestHeader(header));
        TaskCreationResponse taskCreationResponse = dataHelper.createTaskCreationResponse();
        taskCreationResponse.getCreateTaskReturn().getTaskRoutingInformation().setTaskId(taskId);
        mockTmsX741Control.thenReturn(taskCreationResponse);
    }

    @Override
    public void expectLookUpValuesWithISOCode() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ISO_COUNTRY_CODE", "1", "Purpose of Account", 1095L, "SPORI", "LTB", 1L));

    }

    @Override
    public void expectLookUpValuesWithoutISOCode() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ISO_CODE", "1", "Purpose of Account", 1096L, "SPORI", "LTB", 1L));
    }

    @Override
    public String expectRetrieveProductCondition(DepositArrangement productArrangement, RequestHeader header) throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = dataHelper.createRetrieveProductConditionsRequest();
        retrieveProductConditionsRequest.setHeader(header);
        prdClient.retrieveProductConditions(retrieveProductConditionsRequest);
        RetrieveProductConditionsResponse response = dataHelper.createRetrieveProductConditionsResponse();
        response.getProduct().get(0).setInstructionDetails(new InstructionDetails());
        response.getProduct().get(0).getInstructionDetails().setInstructionMnemonic("P_CLASSIC");
        mockRpcControl.thenReturn(response);
        productArrangement.getAssociatedProduct().setInstructionDetails(response.getProduct().get(0).getInstructionDetails());
        return response.getProduct().get(0).getProductoffer().get(0).getTemplate().get(0).getExternalTemplateIdentifier();
    }

    @Override
    public String expectCreateAccountB675Call(RequestHeader header, ProductArrangement productArrangement) {
        mockControl.matching("actual.target == 'fsAccount' && actual.methodName == 'b765AccCreateAccount'");

        accountClient.createAccount(dataHelper.createB765Request(header, productArrangement));
        StB765BAccCreateAccount b765Resp = dataHelper.createResponseB765(productArrangement.getAccountNumber(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        mockFsAccountControl.thenReturn(b765Resp);
        return b765Resp.getCustnum();
    }

    @Override
    public void expectCreateAccountB675CallWithError(RequestHeader header, ProductArrangement productArrangement) {
        mockControl.matching("actual.target == 'fsAccount' && actual.methodName == 'b765AccCreateAccount'");
        accountClient.createAccount(dataHelper.createB765Request(header, productArrangement));
        mockFsAccountControl.thenReturn(dataHelper.createResponseB765WithErrorCode());
    }

    @Override
    public void expectB751CallWithAppIdAndAppVer(ProductArrangement productArrangement, RequestHeader header, BigInteger ocisId, String accNo, BigInteger appId, BigInteger appVer) {
        mockControl.matching("actual.target == 'fsApplication' && actual.methodName == 'b751AppPerCCRegAuth'");
        StB751AAppPerCCRegAuth stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, header);
        stB751AAppPerCCRegAuth.getStheader().getStpartyObo().setOcisid(ocisId);
        stB751AAppPerCCRegAuth.getStparty().setOcisid(ocisId);
        stB751AAppPerCCRegAuth.getStaccount().setAccno(accNo);
        stB751AAppPerCCRegAuth.setAppid(appId);
        stB751AAppPerCCRegAuth.setAppver(appVer);
        stB751AAppPerCCRegAuth.setBNewToBank(true);
        applicationClient.createAppPerCCRegAuth(stB751AAppPerCCRegAuth);
        mockControlFsApplicationServicePortType.thenReturn(dataHelper.createB751Response());
    }

    @Override
    public void expectB751CallWithTacver(ProductArrangement productArrangement, RequestHeader header, BigInteger ocisId, String accNo, BigInteger appId, BigInteger appVer) {
        StB751AAppPerCCRegAuth stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, header);
        stB751AAppPerCCRegAuth.getStheader().getStpartyObo().setOcisid(ocisId);
        stB751AAppPerCCRegAuth.getStparty().setOcisid(ocisId);
        stB751AAppPerCCRegAuth.getStaccount().setAccno(accNo);
        stB751AAppPerCCRegAuth.setAppid(appId);
        stB751AAppPerCCRegAuth.setAppver(appVer);
        stB751AAppPerCCRegAuth.setBNewToBank(true);
        applicationClient.createAppPerCCRegAuth(stB751AAppPerCCRegAuth);
        StB751BAppPerCCRegAuth stB751BAppPerCCRegAuth = dataHelper.createB751Response();
        stB751BAppPerCCRegAuth.setTacver(-4);
        mockControlFsApplicationServicePortType.thenReturn(stB751BAppPerCCRegAuth);
    }

    @Override
    public void expectSendCommunicationCallForPostFulfilment(ProductArrangement productArrangement, String notificationEmail, RequestHeader header) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        productArrangement.setAccountNumber("7791296112");
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest((DepositArrangement) productArrangement, notificationEmail, header, null, "EMAIL");
        productArrangement.setAccountNumber(null);
        sendCommunicationClient.sendCommunication(sendCommunicationRequest);
        SendCommunicationResponse sendCommunicationResponse = new SendCommunicationResponse();
        sendCommunicationResponse.setIsSuccessful(true);
        mockControlSendCommunicationManager.thenReturn(sendCommunicationResponse);
    }

    private E226Client e226Client(String channel) {
        return cbsE226ClientMap.get(channel);
    }

    public Map<String, E226Client> getCbsE226ClientMap() {
        return cbsE226ClientMap;
    }

    public void setCbsE226ClientMap(Map<String, E226Client> cbsE226ClientMap) {
        this.cbsE226ClientMap = cbsE226ClientMap;
    }

    @Override
    public void expectE226AddsOverdraftDetails(long arrangementId, String channel, RequestHeader header) {
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(channel);
        e226Client(channel).createDecisionTrailersInCBS(dataHelper.createE226Request(arrangementId), dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), cbsAppGrp);
        mockCbsE226Control.thenReturn(dataHelper.createE226Resp());

    }

    @Override
    public void expectE226Call(DepositArrangement depositArrangement, String channel, RequestHeader header) {
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(channel);
        e226Client(channel).createDecisionTrailersInCBS(depositArrangementToE226Request.getAddInterPartyRelationshipRequest(depositArrangement.getConditions(), depositArrangement.getPrimaryInvolvedParty().getCustomerNumber(), BigDecimal.ZERO),
                dataHelper.getContactPointId(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), cbsAppGrp);
        mockCbsE226Control.thenReturn(dataHelper.createE226Resp());
    }

    @Override
    public void expectE226AddsOverdraftDetail(long arrangementId, String channel, RequestHeader header) {
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(channel);
        e226Client(channel).createDecisionTrailersInCBS(dataHelper.createE226RequestWithNoSubstatus(arrangementId), dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), cbsAppGrp);
        mockCbsE226Control.thenReturn(dataHelper.createE226Resp());
    }

    @Override
    public void expectE226AddsOverdraftDetailWithSaving(long arrangementId, String channel, RequestHeader header) {
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(channel);
        E226Req e226Req = dataHelper.createE226RequestWithNoSubstatus(arrangementId);
        e226Req.setCardOfferCd(0);
        e226Client(channel).createDecisionTrailersInCBS(e226Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), cbsAppGrp);
        mockCbsE226Control.thenReturn(dataHelper.createE226Resp());
    }

    @Override
    public void expectE226CallFails(long arrangementId, String channel, RequestHeader header) {
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(channel);
        e226Client(channel).createDecisionTrailersInCBS(dataHelper.createE226Request(arrangementId), dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), cbsAppGrp);
        mockCbsE226Control.thenReturn(dataHelper.createE226RespFailure());
    }

    @Override
    public void expectC658Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode) {
        C658Req c658Req = c658RequestFactory.convert((DepositArrangement) productArrangement);
        c658Req.setPartyId(227323270L);
        c658Req.setExtPartyIdTx("+00211135806");
        c658Req.setTlcmmnAddressTx("GalaxyTestAccount02@LloydsTSB.co.uk");
        c658Client.c658(c658Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        C658Resp c658Resp = dataHelper.createC658Resp();
        c658Resp.getC658Result().getResultCondition().setReasonCode(reasonCode);
        c658ServicePortType.thenReturn(c658Resp);
    }

    @Override
    public void expectC234Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode) {
        C234Req c234Req = c234RequestFactory.convert(productArrangement.getPrimaryInvolvedParty());
        c234Client.c234(c234Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        C234Resp c234Resp = dataHelper.createC234Resp();
        c234Resp.getC234Result().getResultCondition().setReasonCode(reasonCode);
        c234ServicePortType.thenReturn(c234Resp);
    }

    @Override
    public void expectC241Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode) {
        C241Req c241Req = createC241Request();
        c241Client.c241(c241Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        C241Resp c241Resp = dataHelper.createC241Resp();
        c241Resp.getC241Result().getResultCondition().setSeverityCode((byte) 0);
        c241ServicePortType.thenReturn(c241Resp);
    }

    private C241Req createC241Request() {
        C241Req c241Req = new C241Req();
        c241Req.setMaxRepeatGroupQy(1);
        c241Req.setExtSysId((short) 19);
        c241Req.setExtPartyIdTx("+00211135806");
        if (!StringUtils.isEmpty("227323270")) {
            c241Req.setPartyId(Long.parseLong("227323270"));
        }
        c241Req.setPartyExtSysId((short) 19);
        StPartyRelData stPartyRelData = new StPartyRelData();
        if (!StringUtils.isEmpty("1234")) {
            stPartyRelData.setPartyId(Long.parseLong("1234"));
        }
        stPartyRelData.setExtSysId((short) 19);
        stPartyRelData.setRelTypeCd("008");
        c241Req.setStPartyRelData(stPartyRelData);
        return c241Req;

    }

    @Override
    public void expectB276Call(DepositArrangement depositArrangement, RequestHeader header) {
        mockControl.matching("actual.target == 'fsAccount' && actual.methodName == 'b276AccProcessOverdraft'");
        accountClient.retrieveAccountProcessOverdraft(dataHelper.createB276Request(depositArrangement, header));
        mockFsAccountControl.thenReturn(dataHelper.createResponseB276());
    }

    @Override
    public void expectB276CallBySubstatusNullCallFails(DepositArrangement productArrangement, RequestHeader header) {
        mockControl.matching("actual.target == 'fsAccount' && actual.methodName == 'b276AccProcessOverdraft'");
        accountClient.retrieveAccountProcessOverdraft(dataHelper.createB276Request(productArrangement, header));
        mockFsAccountControl.thenReturn(dataHelper.createResponseB276Fails());
    }


    @Override
    public void expectC808CallWithSubStatus1025(String sortCode, String accountNumber, long custID, RequestHeader header) {
        c808Client.initiateCardOrder(dataHelper.createC808RequestWithSubStatus1025(sortCode, accountNumber, custID), dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockCmasC808Control.thenReturn(dataHelper.createC808Res());
    }

    @Override
    public void delayF061Invocation(long milliseconds) {
        f060ServicePortType.delayInvocation("ocisF060", milliseconds);
    }


    @Override
    public void delayC808Invocation(long milliseconds) {
        mockCmasC808Control.delayInvocation("cmasC808", milliseconds);
    }

    @Override
    public void expectC812CallWithSubstatus2015(String prodID, String ccApplicableIndicator, String sortCode, long reasonCode, int sysCode, long custID, String accountNumber, RequestHeader header) {
        C812Req c812Req = dataHelper.createC812RequestWithSubstatus2015(createCardOrderNewWithSubstatus1025(custID, sortCode, sysCode, prodID, accountNumber), createCardOrderCBSData(ccApplicableIndicator), null);

        c812Client.validateCardOrder(c812Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        C812Resp c812Resp = dataHelper.createC812Response();
        c812Resp.getCardOrderReferralReasons().getCardOrderReferralReason().get(0).setCardOrderReferralReasonCd(reasonCode);
        mockCmasC812Control.thenReturn(c812Resp);
    }

    @Override
    public void expectPegaCall(DepositArrangement depositArrangement, RequestHeader header) {
        mockControl.matching("actual.target == 'Pega' && actual.methodName == 'genericCreateCase'");
        CreateCaseRequestType request = createCaseRequestFactory.convert(depositArrangement, header);
        pegaclient.createTask(request, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        GenericResponseType response = new GenericResponseType();
        CreateCasePayloadResponseType createCasePayloadResponseType = new CreateCasePayloadResponseType();
        createCasePayloadResponseType.setInitiateSwitchIn(new InitiateSwitchInType());
        createCasePayloadResponseType.getInitiateSwitchIn().setCaseId("123");
        response.setPayload(createCasePayloadResponseType);
        pegaControlServicePortType.thenReturn(response);

    }

    @Override
    public void expectPegaCallWithError(DepositArrangement depositArrangement, RequestHeader header) {
        mockControl.matching("actual.target == 'Pega' && actual.methodName == 'genericCreateCase'");
        CreateCaseRequestType request = createCaseRequestFactory.convert(depositArrangement, header);
        pegaclient.createTask(request, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        GenericResponseType response = new GenericResponseType();
        CreateCasePayloadResponseType createCasePayloadResponseType = new CreateCasePayloadResponseType();
        response.setPayload(createCasePayloadResponseType);
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultConditions(new com.lloydstsb.schema.enterprise.ifwxml.ResultCondition());
        response.getResponseHeader().getResultConditions().setSeverityCode("1");
        pegaControlServicePortType.thenReturn(response);

    }

    @Override
    public void expectE226AddsOverdraftDetailInSecondScenario(long arrangementId, String channel, RequestHeader header) {
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(channel);
        e226Client(channel).createDecisionTrailersInCBS(dataHelper.createE226RequestWithNoSubstatusInSecondScenario(arrangementId), dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), cbsAppGrp);
        mockCbsE226Control.thenReturn(dataHelper.createE226Resp());

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
    public void expectActivateBenefitCall(ProductArrangement productArrangement, RequestHeader header, int reasonCode) {
        CreateServiceArrangementRequest createServiceArrangementRequest = createServiceArrangementRequestFactory.convert(productArrangement, "004");
        mockControl.matching("actual.target == 'soaSas'");
        csaClient.createServiceArrangement(createServiceArrangementRequest, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        CreateServiceArrangementResponse createServiceArrangementResponse = new CreateServiceArrangementResponse();
        createServiceArrangementResponse.setServiceArrangement(new ServiceArrangement());
        mockControlSoaServiceArrangementSetupServicePortType.thenReturn(createServiceArrangementResponse);
    }

    @Override
    public String expectF062Call(Customer customer, RequestHeader header) {
        F062Req f062Req = f061RespToF062ReqConverter.convert(dataHelper.createF061Resp(), dataHelper.createAssessmentEvidence(), customer);
        f062Client.updateCustomerRecord(f062Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockOcisF062Control.thenReturn(dataHelper.createF062Resp());
        return dataHelper.getCustIdFromF062Resp(dataHelper.createF062Resp().getPartyId(), customer.getCustomerIdentifier());
    }

    @Override
    public void expectH071Call(Customer customer, RequestHeader header) {
        mockControl.matching("actual.target == 'fsOu' && actual.methodName == 'h071GetSortCodeByCoordinates'");
        StH071AGetSortCodeByCoordinates h071Req = requestFactory.convert(customer.getIsPlayedBy().getCustomerLocation().getLatitude(), customer.getIsPlayedBy().getCustomerLocation().getLongitude(), header);
        fsOuClient.getSortCodeByCoordinates(h071Req);
        StH071BGetSortCodeByCoordinates response = new StH071BGetSortCodeByCoordinates();
        response.getStbranchdetails().add(new StBranchDetail());
        response.getStbranchdetails().get(0).setSortcode("779129");
        mockControlFsOuServicePortType.thenReturn(response);
    }

    @Override
    public void expectRetrieveEncryptData(List<String> creditCardNumbers, String encryptionKey, RequestHeader requestHeader) {
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
    public void expectF062CallFails(Customer customer, RequestHeader header) {
        F062Req f062Req = f061RespToF062ReqConverter.convert(dataHelper.createF061Resp(), dataHelper.createAssessmentEvidence(), customer);
        f062Client.updateCustomerRecord(f062Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockOcisF062Control.thenReturn(dataHelper.createF062RespWithError());
    }

    @Override
    public void expectRecordCustomerDetails(ProductArrangement productArrangement, RequestHeader requestHeader, boolean isCRSSwitch) throws ErrorInfo {
        com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsRequest retrieveInvolvedPartyDetailsRequest = retrieveInvolvedPartyDetailsRequestFactory.convert(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        soaIpmClient.retrieveInvolvedPartyDetails(retrieveInvolvedPartyDetailsRequest, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        involvedPartyManagementServicePortType.thenReturn(dataHelper.createRetrieveInvolvedPartyResponse());
        RecordInvolvedPartyDetailsRequest recordInvolvedPartyDetailsRequest = recordInvolvedPartyDetailsRequestFactory.convert(dataHelper.createRetrieveInvolvedPartyResponse().getInvolvedParty(), productArrangement.getPrimaryInvolvedParty(), null, isCRSSwitch);
        soaIpmClient.recordInvolvedPartyDetails(recordInvolvedPartyDetailsRequest, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        involvedPartyManagementServicePortType.thenReturn(dataHelper.createRecordInvolvedPartyResponse());
    }

    @Override
    public void expectRetrieveDocumentContent(ProductArrangement productArrangement, RequestHeader requestHeader) throws com.lloydstsb.schema.documentmanagement.ifw.ErrorInfo, ParseException, DatatypeConfigurationException {
        RetrieveDocumentMetaContentRequest request = recordDocumentMetaContent.customerToRetrieveDocMetContentReq(productArrangement.getPrimaryInvolvedParty());
        soaDocumentManagerClient.retrieveDocumentMetaContent(request, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        mockControlSoaDMSServicePortType.thenReturn(dataHelper.createRetrieveDocContentResponse());
    }

    @Override
    public void expectRecordDocumentContent(ProductArrangement productArrangement, RequestHeader requestHeader) throws ParseException, DatatypeConfigurationException, com.lloydstsb.schema.documentmanagement.ifw.ErrorInfo {
        RecordDocumentMetaContentRequest request = recordDocumentMetaContentRequestBuilder.convert(productArrangement.getPrimaryInvolvedParty(), dataHelper.createRetrieveDocContentResponse());
        mockControl.matching("actual.target == 'soaDMS' && actual.methodName == 'recordDocumentMetaContent'");
        soaDocumentManagerClient.recordDocumentMetaContent(request, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        mockControlSoaDMSServicePortType.thenReturn(dataHelper.createRecordDocContentResponse());
    }

    @Override
    public void expectRecordDocumentContentCallFails(ProductArrangement productArrangement, RequestHeader requestHeader) throws ParseException, DatatypeConfigurationException, com.lloydstsb.schema.documentmanagement.ifw.ErrorInfo {
        RecordDocumentMetaContentRequest request = recordDocumentMetaContentRequestBuilder.convert(productArrangement.getPrimaryInvolvedParty(), dataHelper.createRetrieveDocContentResponse());
        mockControl.matching("actual.target == 'soaDMS' && actual.methodName == 'recordDocumentMetaContent'");
        soaDocumentManagerClient.recordDocumentMetaContent(request, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        mockControlSoaDMSServicePortType.thenReturn(dataHelper.createRecordDocContentResponseFails());
    }

    @Override
    public void expectRecordCustomerDetailsWithError(ProductArrangement productArrangement, RequestHeader requestHeader, boolean isCRSSwitch) throws ErrorInfo {
        com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsRequest retrieveInvolvedPartyDetailsRequest = retrieveInvolvedPartyDetailsRequestFactory.convert(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        soaIpmClient.retrieveInvolvedPartyDetails(retrieveInvolvedPartyDetailsRequest, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        RetrieveInvolvedPartyDetailsResponse retrieveInvolvedPartyDetailsResponse = dataHelper.createRetrieveInvolvedPartyResponse();
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("3");
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("1031");
        involvedPartyManagementServicePortType.thenReturn(retrieveInvolvedPartyDetailsResponse);
        RecordInvolvedPartyDetailsRequest recordInvolvedPartyDetailsRequest = recordInvolvedPartyDetailsRequestFactory.convert(dataHelper.createRetrieveInvolvedPartyResponse().getInvolvedParty(), productArrangement.getPrimaryInvolvedParty(), null, isCRSSwitch);
        soaIpmClient.recordInvolvedPartyDetails(recordInvolvedPartyDetailsRequest, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        involvedPartyManagementServicePortType.thenReturn(dataHelper.createRecordInvolvedPartyResponse());
    }

    public void sleep() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    @Transactional
    public void expectLookupDataForSira(String channel) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp lookupDto1 = new ReferenceDataLookUp("SIRA_THRESHOLD_VALUE", "20", "Rule1", 101l, "HAL", channel, new Long("3"));
        ReferenceDataLookUp lookupDto2 = new ReferenceDataLookUp("SIRA_THRESHOLD_VALUE", "0", "Rule2", 102l, "HAL", channel, new Long("1"));
        ReferenceDataLookUp lookupDto3 = new ReferenceDataLookUp("SIRA_THRESHOLD_VALUE", "19", "Rule3", 103l, "HAL", channel, new Long("2"));
        ReferenceDataLookUp lookupDto4 = new ReferenceDataLookUp("SIRA_THRESHOLD_VALUE", "420", "Rule3", 104l, "HAL", channel, new Long("4"));
        ReferenceDataLookUp lookupDto5 = new ReferenceDataLookUp("SIRA_THRESHOLD_VALUE", "421", "Rule3", 105l, "HAL", channel, new Long("5"));
        ReferenceDataLookUp lookupDto6 = new ReferenceDataLookUp("SIRA_THRESHOLD_VALUE", "9900", "Rule3", 106l, "HAL", channel, new Long("6"));
        ReferenceDataLookUp lookupDto7 = new ReferenceDataLookUp("SIRA_THRESHOLD_VALUE", "9901", "Rule3", 107l, "HAL", channel, new Long("7"));
        referenceDataLookUpList.add(lookupDto1);
        referenceDataLookUpList.add(lookupDto2);
        referenceDataLookUpList.add(lookupDto3);
        referenceDataLookUpList.add(lookupDto4);
        referenceDataLookUpList.add(lookupDto5);
        referenceDataLookUpList.add(lookupDto6);
        referenceDataLookUpList.add(lookupDto7);
        referenceDataLookUpDao.save(referenceDataLookUpList);
        Iterable it = referenceDataLookUpDao.findAll();
    }
    @Override
    public void expectSiraCall(BigInteger totalRuleScore,DepositArrangement depositArrangement,RequestHeader requestHeader) {
        SubmitWorkItemResponse.SubmitWorkItemResult submitWorkItemResult = new SubmitWorkItemResponse.SubmitWorkItemResult();
        RealtimeResultType4Type realtimeResultType4Type = new RealtimeResultType4Type();
        realtimeResultType4Type.setTotalRuleScore(totalRuleScore);
        JAXBElement jaxbElement = new JAXBElement(new QName("http://www.synectics-solutions.com/sira/schemas/realtime/core/v1.0/RealtimeResultType4.xsd", "RealtimeResultType4"), RealtimeResultType4Type.class, realtimeResultType4Type);

        submitWorkItemResult.getContent().add(jaxbElement);
        AuthenticationHeader authenticationHeader = new AuthenticationHeader();
        authenticationHeader.setClientName("LBG");
        authenticationHeader.setPassword("");
        authenticationHeader.setUsername("");
        Source source=siraRequestFactory.convert(depositArrangement,requestHeader.getChannelId(),"UNAUTHSALE",null,null);
        mockControl.matching("actual.target == 'sira'");
        siraClient.submitWorkItemResult(source
                , depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerDeviceDetails().getWorkFlowName(), false, authenticationHeader);
        mockSiraControl.thenReturn(submitWorkItemResult);
    }

}
