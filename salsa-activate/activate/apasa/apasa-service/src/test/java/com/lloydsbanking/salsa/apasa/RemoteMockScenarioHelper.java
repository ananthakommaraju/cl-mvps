package com.lloydsbanking.salsa.apasa;

import com.lloydsbanking.salsa.activate.administer.convert.X741RequestFactory;
import com.lloydsbanking.salsa.activate.communication.convert.CommunicationRequestFactory;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.converter.F060RequestFactory;
import com.lloydsbanking.salsa.activate.converter.F061RespToF062ReqConverter;
import com.lloydsbanking.salsa.activate.downstream.PrdClient;
import com.lloydsbanking.salsa.activate.postfulfil.convert.*;
import com.lloydsbanking.salsa.activate.postfulfil.downstream.RecordDocumentMetaContent;
import com.lloydsbanking.salsa.activate.registration.converter.B751RequestFactory;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apasa.service.fulfil.convert.E032RequestFactory;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydsbanking.salsa.downstream.application.client.ApplicationClient;
import com.lloydsbanking.salsa.downstream.asm.client.f425.F425Client;
import com.lloydsbanking.salsa.downstream.cbs.client.e032.E032Client;
import com.lloydsbanking.salsa.downstream.cbs.client.e502.E502Client;
import com.lloydsbanking.salsa.downstream.cm.client.ScheduleCommunicationClient;
import com.lloydsbanking.salsa.downstream.cm.client.SendCommunicationClient;
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
import com.lloydsbanking.salsa.downstream.soaipm.client.involvedpartymanager.IPMClient;
import com.lloydsbanking.salsa.downstream.soa.servicearrangement.client.CSAClient;
import com.lloydsbanking.salsa.downstream.soadms.client.documentmanager.SOADocumentManagerClient;
import com.lloydsbanking.salsa.downstream.switches.jdbc.SwitchDao;
import com.lloydsbanking.salsa.downstream.switches.model.SwitchDto;
import com.lloydsbanking.salsa.downstream.tms.client.x741.X741Client;
import com.lloydsbanking.salsa.remotemock.*;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Req;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Resp;
import com.lloydsbanking.salsa.soap.cbs.e032.objects.E032Req;
import com.lloydsbanking.salsa.soap.cbs.e032.objects.E032Resp;
import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Req;
import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Resp;
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
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RecordDocumentMetaContentRequest;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RetrieveDocumentMetaContentRequest;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsRequest;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsResponse;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsResponse;
import com.lloydsbanking.salsa.soap.soa.servicearrangement.CreateServiceArrangementRequest;
import com.lloydsbanking.salsa.soap.soa.servicearrangement.CreateServiceArrangementResponse;
import com.lloydstsb.ib.wsbridge.application.StB751AAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ServiceArrangement;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreation;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreationResponse;
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
import lib_sim_productsalesreferencedatamanager.messages.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.apache.cxf.common.util.StringUtils;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RemoteMockScenarioHelper implements ScenarioHelper {
    @Autowired
    F061Client f061Client;
    @Autowired
    F062Client f062Client;
    @Autowired
    F061RespToF062ReqConverter f061RespToF062ReqConverter;
    @Autowired
    MockControlOcisF062ServicePortType mockOcisF062Control;
    @Autowired
    MockControlOcisF061ServicePortType mockOcisF061Control;
    @Autowired
    RecordDocumentMetaContentRequestBuilder recordDocumentMetaContentRequestBuilder;
    @Autowired
    RecordDocumentMetaContent recordDocumentMetaContent;
    @Autowired
    Map<String, E502Client> cbsE502ClientMap;
    @Autowired
    Map<String, E032Client> cbsE032ClientMap;
    @Autowired
    MockControlFsAccountServicePortType mockFsAccountControl;
    @Autowired
    MockControlCbsE502ServicePortType mockCbsE502Control;
    @Autowired
    AccountClient accountClient;
    @Autowired
    C241Client c241Client;
    @Autowired
    MockControlOcisC241ServicePortType c241ServicePortType;
    @Autowired
    MockControlOcisC658ServicePortType c658ServicePortType;
    @Autowired
    F060RequestFactory f060RequestFactory;
    @Autowired
    RetrieveInvolvedPartyDetailsRequestFactory retrieveInvolvedPartyDetailsRequestFactory;
    @Autowired
    MockControlSoaServiceArrangementSetupServicePortType mockControlSoaServiceArrangementSetupServicePortType;
    @Autowired
    RecordInvolvedPartyDetailsRequestFactory recordInvolvedPartyDetailsRequestFactory;
    @Autowired
    CSAClient csaClient;
    @Autowired
    ScheduleCommunicationClient scheduleCommunicationClient;
    @Autowired
    MockControlScheduleCommunicationManagerServicePortType mockControlScheduleCommunicationManager;
    @Autowired
    CreateServiceArrangementRequestFactory createServiceArrangementRequestFactory;
    @Autowired
    F060Client f060Client;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    MockControlOcisF060ServicePortType f060ServicePortType;
    @Autowired
    SOADocumentManagerClient soaDocumentManagerClient;
    @Autowired
    MockControlSoaDMSServicePortType mockControlSoaDMSServicePortType;
    @Autowired
    IPMClient soaIpmClient;
    @Autowired
    MockControlSoaInvolvedPartyManagementServicePortType involvedPartyManagementServicePortType;
    @Autowired
    MockControlOcisC234ServicePortType c234ServicePortType;
    @Autowired
    C234RequestFactory c234RequestFactory;
    @Autowired
    C234Client c234Client;
    @Autowired
    TestDataHelper dataHelper;
    @Autowired
    E502Client e502ClientVer;
    @Autowired
    E502Client e502ClientBos;
    @Autowired
    E502Client e502ClientHlx;
    @Autowired
    E502Client e502ClientLtb;
    @Autowired
    ReferenceDataLookUpDao referenceDataLookUpDao;
    @Autowired
    RetrievePamService retrievePamService;
    @Autowired
    PrdClient prdClient;
    @Autowired
    F425Client f425Client;
    @Autowired
    MockControlRpcServicePortType mockRpcControl;
    @Autowired
    MockControlFsApplicationServicePortType mockControlFsApplicationServicePortType;
    @Autowired
    X741RequestFactory x741RequestFactory;
    @Autowired
    C658RequestFactory c658RequestFactory;
    @Autowired
    C658Client c658Client;
    @Autowired
    X741Client x741Client;
    @Autowired
    MockControlAsmF425ServicePortType mockAsmF425Control;
    @Autowired
    MockControlTmsX741ServicePortType mockTmsX741Control;
    @Autowired
    CommunicationRequestFactory communicationRequestFactory;
    @Autowired
    SendCommunicationClient sendCommunicationClient;
    @Autowired
    MockControlSendCommunicationManagerServicePortType mockControlSendCommunicationManager;
    @Autowired
    ReferralTeamsDao referralTeamsDao;
    @Autowired
    MockControlServicePortType mockControl;
    @Autowired
    B751RequestFactory b751RequestFactory;
    @Autowired
    ApplicationClient applicationClient;
    @Autowired
    E032RequestFactory requestFactory;
    @Autowired
    E032Client e032ClientLtb;
    @Autowired
    E032Client e032ClientVer;
    @Autowired
    E032Client e032ClientHlx;
    @Autowired
    E032Client e032ClientBos;
    @Autowired
    MockControlCbsE032ServicePortType mockControlCbsE032ServicePortType;
    @Value("${salsa.fs.boxid}")
    int boxId;
    @Autowired
    private SwitchDao switchDao;

    @Override
    public String expectChannelIdByContactPointID() {
        String channelId = "LTB";
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("Cnt_Pnt_Prtflio", "0000777505", "Acquire Contry Name", new Long("132356"), "Bahrain", "LTB", new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
        return channelId;
    }

    @Override
    @Transactional
    public long expectApplicationDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision) {
        return dataHelper.createApplicationSA(applicationStatus, eidvStatus, asmDecision);
    }

    @Override
    public void clearUp() {
        dataHelper.cleanUp();
        referenceDataLookUpDao.deleteAll();
        switchDao.deleteAll();
        clearUpForWPS();
    }

    public void clearUpForWPS() {

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
    public ProductArrangement expectApplicationDetailsHavingPamAndRequestData(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {

        Applications applications = dataHelper.createNewApplication(applicationStatus, eidvStatus, asmDecision, subStatus);
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
        customer.setIsRegistrationSelected(upStreamCustomer.isIsRegistrationSelected());
        customer.setPassword(upStreamCustomer.getPassword());
        customer.setUserType(upStreamCustomer.getUserType());
        customer.setInternalUserIdentifier(upStreamCustomer.getInternalUserIdentifier());

        DepositArrangement depositArrangement = (DepositArrangement) productArrangement;
        if (null != depositArrangement.isIsOverdraftRequired() && depositArrangement.isIsOverdraftRequired()
                && (upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER)
                || upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER))) {

            RuleCondition ruleCondition = new RuleCondition();
            ruleCondition.setName("ODPCI_VIEWED");
            ruleCondition.setResult("Y");
            productArrangement.getConditions().add(ruleCondition);
        }
        productArrangement.setFinancialInstitution(upstreamRequest.getProductArrangement().getFinancialInstitution());
        productArrangement.setAccountDetails(new AccountDetails());
        return productArrangement;
    }

    @Override
    @Transactional
    public ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithNiNumberWithoutGuardianDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        Applications applications = dataHelper.createNewApplicationWithNiNumberWithoutGuardian(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber, productType);
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
        if (!ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OAP_SOURCE_SYSTEM_IDENTIFIER.equalsIgnoreCase(upstreamRequest.getSourceSystemIdentifier())) {
            Customer customer = productArrangement.getPrimaryInvolvedParty();
            Customer upStreamCustomer = upstreamRequest.getProductArrangement().getPrimaryInvolvedParty();
            productArrangement.setApplicationSubStatus(upstreamRequest.getProductArrangement().getApplicationSubStatus());
            //productArrangement.setGuardianDetails(upstreamRequest.getProductArrangement().getGuardianDetails());
            customer.setIsRegistrationSelected(upStreamCustomer.isIsRegistrationSelected());
            customer.setPassword(upStreamCustomer.getPassword());
            customer.setUserType(upStreamCustomer.getUserType());
            customer.setInternalUserIdentifier(upStreamCustomer.getInternalUserIdentifier());

            DepositArrangement depositArrangement = (DepositArrangement) productArrangement;
            if (null != depositArrangement.isIsOverdraftRequired() && depositArrangement.isIsOverdraftRequired()
                    && (upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER)
                    || upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER))) {

                RuleCondition ruleCondition = new RuleCondition();
                ruleCondition.setName("ODPCI_VIEWED");
                ruleCondition.setResult("Y");
                productArrangement.getConditions().add(ruleCondition);
            }
            if (!CollectionUtils.isEmpty(upstreamRequest.getProductArrangement().getConditions())) {
                productArrangement.getConditions().addAll(upstreamRequest.getProductArrangement().getConditions());


            }
            productArrangement.setFinancialInstitution(upstreamRequest.getProductArrangement().getFinancialInstitution());
            productArrangement.setAccountDetails(new AccountDetails());
        } else {
            productArrangement.setApplicationSubStatus(upstreamRequest.getProductArrangement().getApplicationSubStatus());
            setPamDetailsForOAPMode(upstreamRequest, productArrangement);
        }
        return productArrangement;
    }


    @Override
    @Transactional
    public ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithNiNumber(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {

        Applications applications = dataHelper.createNewApplicationWithNiNumber(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber, productType);
        ProductArrangement productArrangement;
        try {
            productArrangement = retrievePamService.retrievePendingArrangement(channelId, String.valueOf(applications.getId()), upstreamRequest.getProductArrangement().getReferral());
        } catch (ResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
            throw exceptionUtilityActivate.resourceNotAvailableError(upstreamRequest.getHeader(), "Resource Not Available Error");
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
            throw exceptionUtilityActivate.dataNotAvailableError(null, null, "Data Not Available Error", upstreamRequest.getHeader());
        }
        catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            throw exceptionUtilityActivate.internalServiceError(null, "Internal Service Error", upstreamRequest.getHeader());
        }
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        Customer upStreamCustomer = upstreamRequest.getProductArrangement().getPrimaryInvolvedParty();
        productArrangement.setApplicationSubStatus(upstreamRequest.getProductArrangement().getApplicationSubStatus());
        //productArrangement.setGuardianDetails(upstreamRequest.getProductArrangement().getGuardianDetails());
        customer.setIsRegistrationSelected(upStreamCustomer.isIsRegistrationSelected());
        customer.setPassword(upStreamCustomer.getPassword());
        customer.setUserType(upStreamCustomer.getUserType());
        customer.setInternalUserIdentifier(upStreamCustomer.getInternalUserIdentifier());

        DepositArrangement depositArrangement = (DepositArrangement) productArrangement;
        if (null != depositArrangement.isIsOverdraftRequired() && depositArrangement.isIsOverdraftRequired()
                && (upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER)
                || upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER))) {

            RuleCondition ruleCondition = new RuleCondition();
            ruleCondition.setName("ODPCI_VIEWED");
            ruleCondition.setResult("Y");
            productArrangement.getConditions().add(ruleCondition);
        }
        if (!CollectionUtils.isEmpty(upstreamRequest.getProductArrangement().getConditions())) {
            productArrangement.getConditions().addAll(upstreamRequest.getProductArrangement().getConditions());


        }
        productArrangement.setFinancialInstitution(upstreamRequest.getProductArrangement().getFinancialInstitution());
        productArrangement.setAccountDetails(upstreamRequest.getProductArrangement().getAccountDetails());
        return productArrangement;
    }


    @Override
    @Transactional
    public ProductArrangement expectApplicationDetailsHavingPamAndRequestDataWithNiNumberWithInterestRemittanceDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String channelId, ActivateProductArrangementRequest upstreamRequest, String subStatus, String niNumber, ProductTypes productType) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {

        Applications applications = dataHelper.createNewApplicationWithNiNumberWithInterestRemittanceDetails(applicationStatus, eidvStatus, asmDecision, subStatus, niNumber, productType);
        ProductArrangement productArrangement;
        try {
            productArrangement = retrievePamService.retrievePendingArrangement(channelId, String.valueOf(applications.getId()), upstreamRequest.getProductArrangement().getReferral());
        } catch (ResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
            throw exceptionUtilityActivate.resourceNotAvailableError(upstreamRequest.getHeader(), "Resource Not Available Error");
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
            throw exceptionUtilityActivate.dataNotAvailableError(null, null, "Data Not Available Error", upstreamRequest.getHeader());
        }
        catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            throw exceptionUtilityActivate.internalServiceError(null, "Internal Service Error", upstreamRequest.getHeader());
        }
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        Customer upStreamCustomer = upstreamRequest.getProductArrangement().getPrimaryInvolvedParty();
        productArrangement.setApplicationSubStatus(upstreamRequest.getProductArrangement().getApplicationSubStatus());
        //productArrangement.setGuardianDetails(upstreamRequest.getProductArrangement().getGuardianDetails());
        customer.setIsRegistrationSelected(upStreamCustomer.isIsRegistrationSelected());
        customer.setPassword(upStreamCustomer.getPassword());
        customer.setUserType(upStreamCustomer.getUserType());
        customer.setInternalUserIdentifier(upStreamCustomer.getInternalUserIdentifier());

        DepositArrangement depositArrangement = (DepositArrangement) productArrangement;
        if (null != depositArrangement.isIsOverdraftRequired() && depositArrangement.isIsOverdraftRequired()
                && (upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER)
                || upstreamRequest.getSourceSystemIdentifier().equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER))) {

            RuleCondition ruleCondition = new RuleCondition();
            ruleCondition.setName("ODPCI_VIEWED");
            ruleCondition.setResult("Y");
            productArrangement.getConditions().add(ruleCondition);
        }
        if (!CollectionUtils.isEmpty(upstreamRequest.getProductArrangement().getConditions())) {
            productArrangement.getConditions().addAll(upstreamRequest.getProductArrangement().getConditions());


        }
        productArrangement.setFinancialInstitution(upstreamRequest.getProductArrangement().getFinancialInstitution());
        productArrangement.setAccountDetails(upstreamRequest.getProductArrangement().getAccountDetails());
        return productArrangement;
    }

    private void setPamDetailsForOAPMode(ActivateProductArrangementRequest upStreamRequest, ProductArrangement productArrangement) {
        List<AssessmentEvidence> primaryAssessmentEvidences = new ArrayList<>();
        List<AssessmentEvidence> guardianAssessmentEvidences = new ArrayList<>();
        if (!CollectionUtils.isEmpty(upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore())) {
            primaryAssessmentEvidences = upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentEvidence();
        }
        if (upStreamRequest.getProductArrangement().getGuardianDetails() != null && !CollectionUtils.isEmpty(upStreamRequest.getProductArrangement().getGuardianDetails().getCustomerScore())) {
            guardianAssessmentEvidences = upStreamRequest.getProductArrangement().getGuardianDetails().getCustomerScore().get(0).getAssessmentEvidence();
        }
        String userType = upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().getUserType();
        String internalUserId = upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().getInternalUserIdentifier();
        if (!CollectionUtils.isEmpty(primaryAssessmentEvidences)) {
            productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentEvidence().addAll(primaryAssessmentEvidences);
        }
        if (!StringUtils.isEmpty(internalUserId)) {
            productArrangement.getPrimaryInvolvedParty().setInternalUserIdentifier(internalUserId);
        }
        if (!StringUtils.isEmpty(userType)) {
            productArrangement.getPrimaryInvolvedParty().setUserType(userType);
        }
        if (productArrangement.getGuardianDetails() != null && !StringUtils.isEmpty(productArrangement.getGuardianDetails().getCustomerIdentifier()) && !CollectionUtils.isEmpty(guardianAssessmentEvidences)) {
            productArrangement.getGuardianDetails().getCustomerScore().get(0).getAssessmentEvidence().addAll(guardianAssessmentEvidences);
        }
    }


    @Override
    public void expectRpcCall(DepositArrangement productArrangement, RequestHeader header) throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        retrieveProductConditionsRequest.setHeader(header);
        String productIdentifier = productArrangement.getAssociatedProduct().getProductIdentifier();
        Product product = new Product();
        product.setProductIdentifier(productIdentifier);
        retrieveProductConditionsRequest.setProduct(product);
        prdClient.retrieveProductConditions(retrieveProductConditionsRequest);
        RetrieveProductConditionsResponse response=dataHelper.createRpcResponse();
        response.getProduct().get(0).setInstructionDetails(new InstructionDetails());
        response.getProduct().get(0).getInstructionDetails().setInstructionMnemonic("P_CLASSIC");
        mockRpcControl.thenReturn(response);
        productArrangement.getAssociatedProduct().setInstructionDetails(response.getProduct().get(0).getInstructionDetails());
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

        }
        catch (InternalServiceErrorMsg internalServiceErrorMsg) {
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
    public void expectF425Call(RequestHeader header, String requestNo, String sourceSystemCode, String asmDecisionCode, String asmCreditScore) {
        F425Req f425Req = dataHelper.createF425Req(requestNo, sourceSystemCode);
        f425Client.f425(f425Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockAsmF425Control.thenReturn(dataHelper.createF425Resp(asmDecisionCode, asmCreditScore));
    }

    @Override
    public void expectF425CallWithError(RequestHeader header, String requestNo, String sourceSystemCode) {
        F425Req f425Req = dataHelper.createF425Req(requestNo, sourceSystemCode);
        f425Client.f425(f425Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        F425Resp f425Resp = dataHelper.createF425Resp();
        f425Resp.getF425Result().getResultCondition().setSeverityCode((byte) 1);
        mockAsmF425Control.thenReturn(f425Resp);
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
    public void expectF061Call(String customerId, RequestHeader header) {
        F061Req f061Req = new F061Req();
        f061Req.setExtSysId(Short.valueOf("19"));
        f061Req.setPartyId(Long.valueOf(customerId));
        f061Client.f061(f061Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockOcisF061Control.thenReturn(dataHelper.createF061Resp(Long.valueOf(customerId)));
    }

    @Override
    public String expectF062Call(Customer customer, RequestHeader header) {
        F062Req f062Req = f061RespToF062ReqConverter.convert(dataHelper.createF061Resp(Long.valueOf(customer.getCustomerIdentifier())), dataHelper.createAssessmentEvidence(), customer);
        f062Client.updateCustomerRecord(f062Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockOcisF062Control.thenReturn(dataHelper.createF062Resp());
        return dataHelper.getCustIdFromF062Resp(dataHelper.createF062Resp().getPartyId(), customer.getCustomerIdentifier());
    }

    @Override
    public void expectF062CallFails(Customer customer, RequestHeader header) {
        F062Req f062Req = f061RespToF062ReqConverter.convert(dataHelper.createF061Resp(Long.valueOf(customer.getCustomerIdentifier())), dataHelper.createAssessmentEvidence(), customer);
        f062Client.updateCustomerRecord(f062Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        mockOcisF062Control.thenReturn(dataHelper.createF062RespWithError());
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
    public void expectSendCommunicationCallWithError(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(productArrangement, notificationEmail, header, null, "Email");
        sendCommunicationClient.sendCommunication(sendCommunicationRequest);
        lib_sim_communicationmanager.messages.SendCommunicationResponse sendCommunicationResponse = new lib_sim_communicationmanager.messages.SendCommunicationResponse();
        sendCommunicationResponse.setIsSuccessful(false);
        mockControlSendCommunicationManager.thenReturn(sendCommunicationResponse);
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

    @Transactional
    public void expectRelatedApplicationDetails(Applications applications, ApplicationStatus applicationStatus) {
        dataHelper.createRelatedApplications(applications, applicationStatus);
    }

    @Override
    public void verifyExpectCalls() {
        String result = mockControl.verify();
        if (!result.equals("verified")&& (result.equals("fsSystem")|| result.equals("tmsX741"))) {
            throw new IllegalStateException("Following Expect not called: " + result);
        }
    }

    @Override
    public void expectCreateAccountB675Call(RequestHeader header, ProductArrangement productArrangement) {
        accountClient.createAccount(dataHelper.createB765Request(header, productArrangement));
        mockFsAccountControl.thenReturn(dataHelper.createResponseB765(productArrangement.getAccountNumber(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode()));
    }

    @Override
    public void expectCreateAccountB675CallFails(RequestHeader header, ProductArrangement productArrangement) {
        accountClient.createAccount(dataHelper.createB765Request(header, productArrangement));
        mockFsAccountControl.thenReturn(dataHelper.createResponseB765Fails(productArrangement.getAccountNumber(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode()));
    }

    @Override
    public void expectE502Call(RequestHeader header, DepositArrangement productArrangement) {
        String cbsAppGroup = productArrangement.getFinancialInstitution().getChannel();
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(cbsAppGroup);
        expectE502Call(header.getChannelId(), dataHelper.createE502RespWithoutError(), dataHelper.createE502Request(productArrangement), dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), cbsAppGrp);

    }

    private void expectE502Call(String channel, E502Resp e229Resp, E502Req e229Req, ContactPoint contactPoint, ServiceRequest serviceRequest, SecurityHeaderType securityHeaderType, CBSAppGrp cbsAppGrp) {
        e502Client(channel).amendRollOverAccount(e229Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        mockCbsE502Control.thenReturn(e229Resp);
    }

    private E502Client e502Client(String channel) {
        return cbsE502ClientMap.get(channel);
    }

    public Map<String, E502Client> getCbsE502ClientMap() {
        return cbsE502ClientMap;
    }

    public void setCbsE502ClientMap(Map<String, E502Client> cbsE502ClientMap) {
        this.cbsE502ClientMap = cbsE502ClientMap;
    }

    @Override
    public void expectE502CallFails(RequestHeader header, DepositArrangement productArrangement) {
        String cbsAppGroup = productArrangement.getFinancialInstitution().getChannel();
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(cbsAppGroup);
        expectE502Call(header.getChannelId(), dataHelper.createE502RespWithError(), dataHelper.createE502Request(productArrangement), dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), cbsAppGrp);

    }

    @Override
    public void expectCreateStandingOrderCall(String sortCode, String accNo, String beneficiaryAccountNumber, String beneficiarySortCode, String transactionName, RequestHeader requestHeader, byte severityCode, String cbsAppGroup) {
        E032Req e032Req = requestFactory.convert(sortCode, accNo, beneficiaryAccountNumber, beneficiarySortCode, transactionName);
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(cbsAppGroup);
        E032Resp e032Resp = dataHelper.createE032Resp(severityCode);
        expectE032Call(requestHeader.getChannelId(), e032Resp, e032Req, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader), cbsAppGrp);
    }

    private E032Client e032Client(String channel) {
        return cbsE032ClientMap.get(channel);
    }

    public Map<String, E032Client> getCbsE032ClientMap() {
        return cbsE032ClientMap;
    }

    public void setCbsE032ClientMap(Map<String, E032Client> cbsE032ClientMap) {
        this.cbsE032ClientMap = cbsE032ClientMap;
    }

    private void expectE032Call(String channel, E032Resp e032Resp, E032Req e032Req, ContactPoint contactPoint, ServiceRequest serviceRequest, SecurityHeaderType securityHeaderType, CBSAppGrp cbsAppGrp) {
        e032Client(channel).createStandingOrder(e032Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        mockControlCbsE032ServicePortType.thenReturn(e032Resp);
    }

    @Override
    public void expectB766Call(RequestHeader requestHeader, String sortCode) {
        mockControl.matching("actual.target == 'fsAccount' && actual.methodName == 'b766RetrieveCBSAppGroup'");
        accountClient.retrieveCBSAppGroup(dataHelper.createB766Request(requestHeader, sortCode));
        mockFsAccountControl.thenReturn(dataHelper.createB766Response(dataHelper.getCBSAppGrpForSortCode(sortCode), 0));
    }

    @Override
    public String expectB751CallWithTacver(ProductArrangement productArrangement, RequestHeader header, BigInteger ocisId, String accNo, BigInteger appId, BigInteger appVer) {
        StB751AAppPerCCRegAuth stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, header);
        stB751AAppPerCCRegAuth.getStheader().getStpartyObo().setOcisid(ocisId);
        stB751AAppPerCCRegAuth.getStparty().setOcisid(ocisId);
        stB751AAppPerCCRegAuth.setAppid(appId);
        stB751AAppPerCCRegAuth.setAppver(appVer);
        stB751AAppPerCCRegAuth.setBNewToBank(true);
        mockControl.matching("actual.target == 'fsApplication' && actual.methodName == 'b751AppPerCCRegAuth'");
        applicationClient.createAppPerCCRegAuth(stB751AAppPerCCRegAuth);
        StB751BAppPerCCRegAuth stB751BAppPerCCRegAuth = dataHelper.createB751Response();
        stB751BAppPerCCRegAuth.setTacver(-4);
        mockControlFsApplicationServicePortType.thenReturn(stB751BAppPerCCRegAuth);
        return stB751BAppPerCCRegAuth.getPartyidEmergingChannelUserId();
    }

    @Override
    public void expectSendCommunicationCallForPostFulfilment(ProductArrangement productArrangement, String notificationEmail, RequestHeader header) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest((DepositArrangement) productArrangement, notificationEmail, header, null, "Email");
        sendCommunicationClient.sendCommunication(sendCommunicationRequest);
        lib_sim_communicationmanager.messages.SendCommunicationResponse sendCommunicationResponse = new lib_sim_communicationmanager.messages.SendCommunicationResponse();
        sendCommunicationResponse.setIsSuccessful(true);
        mockControlSendCommunicationManager.thenReturn(sendCommunicationResponse);
    }

    @Override
    public void expectC658Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode) {
        C658Req c658Req = c658RequestFactory.convert(productArrangement);
        c658Client.c658(c658Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        C658Resp c658Resp = dataHelper.createC658Resp();
        c658Resp.getC658Result().getResultCondition().setReasonCode(reasonCode);
        c658ServicePortType.thenReturn(c658Resp);
    }


    @Override
    public void delayF060Invocation(long milliseconds) {
        f060ServicePortType.delayInvocation("ocisF060", milliseconds);
    }

    @Override
    public void expectLookUpValuesWithLifeStyleBenefitCode() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 1L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "1", "Purpose of Account", 2L, "SPORI", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "2", "Purpose of Account", 3L, "BIEXP", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("REFERRAL_TEAM_GROUPS", "3", "Purpose of Account", 4L, "002", "LTB", 1L));
        referenceDataLookUpDao.save(new ReferenceDataLookUp("LIFE_STYLE_BENEFIT", "4", "Purpose of Account", 5L, "002", "LTB", 1L));
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
    public void expectF060Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode) {
        F060Req f060Req = f060RequestFactory.convert(productArrangement);
        mockControl.matching("actual.target == 'ocisF060'");
        f060Client.f060(f060Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        F060Resp f060Resp = dataHelper.createF060Resp();
        f060Resp.getF060Result().setResultCondition(new com.lloydstsb.schema.infrastructure.soap.ResultCondition());
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
    public void expectFATCAUpdateSwitchCall(String channel, boolean switchStatus) {
        SwitchDto switchDto = new SwitchDto("LBG", "SW_FATCAupdate", new Date(), boxId, 1, "A", switchStatus ? "1" : "0");
        switchDao.save(switchDto);
    }

    @Override
    public void expectRecordCustomerDetails(ProductArrangement productArrangement, RequestHeader requestHeader, boolean isCRSSwitch) throws ErrorInfo {
        com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsRequest retrieveInvolvedPartyDetailsRequest;
        if (productArrangement.getGuardianDetails() != null && productArrangement.getGuardianDetails().getIsPlayedBy() != null) {
            retrieveInvolvedPartyDetailsRequest = retrieveInvolvedPartyDetailsRequestFactory.convert(productArrangement.getGuardianDetails().getCustomerIdentifier());
        } else {
            retrieveInvolvedPartyDetailsRequest = retrieveInvolvedPartyDetailsRequestFactory.convert(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        }
        soaIpmClient.retrieveInvolvedPartyDetails(retrieveInvolvedPartyDetailsRequest, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        involvedPartyManagementServicePortType.thenReturn(dataHelper.createRetrieveInvolvedPartyResponse());
        RecordInvolvedPartyDetailsRequest recordInvolvedPartyDetailsRequest;
        if (productArrangement.getGuardianDetails() != null && productArrangement.getGuardianDetails().getIsPlayedBy() != null) {
            recordInvolvedPartyDetailsRequest = recordInvolvedPartyDetailsRequestFactory.convert(dataHelper.createRetrieveInvolvedPartyResponse().getInvolvedParty(), productArrangement.getGuardianDetails(), null, isCRSSwitch);
        } else {
            recordInvolvedPartyDetailsRequest = recordInvolvedPartyDetailsRequestFactory.convert(dataHelper.createRetrieveInvolvedPartyResponse().getInvolvedParty(), productArrangement.getPrimaryInvolvedParty(), null, isCRSSwitch);
        }
        soaIpmClient.recordInvolvedPartyDetails(recordInvolvedPartyDetailsRequest, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        involvedPartyManagementServicePortType.thenReturn(dataHelper.createRecordInvolvedPartyResponse());
    }

    @Override
    public void expectC241Call(ProductArrangement productArrangement, RequestHeader header, int reasonCode) {
        C241Req c241Req = createC241Request(productArrangement.getGuardianDetails().getCidPersID(), productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), productArrangement.getGuardianDetails().getCustomerIdentifier());
        c241Client.c241(c241Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        C241Resp c241Resp = dataHelper.createC241Resp();
        c241Resp.getC241Result().getResultCondition().setSeverityCode((byte) 0);
        c241ServicePortType.thenReturn(c241Resp);
    }

    @Override
    public void expectC241CallFails(ProductArrangement productArrangement, RequestHeader header, int reasonCode) {
        C241Req c241Req = createC241Request(productArrangement.getGuardianDetails().getCidPersID(), productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), productArrangement.getGuardianDetails().getCustomerIdentifier());
        c241Client.c241(c241Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header));
        C241Resp c241Resp = dataHelper.createC241Resp();
        c241ServicePortType.thenReturn(c241Resp);
    }

    private C241Req createC241Request(String extPartyIdTx, String partyId, String stPartyId) {
        C241Req c241Req = new C241Req();
        c241Req.setMaxRepeatGroupQy(1);
        c241Req.setExtSysId((short) 19);
        c241Req.setExtPartyIdTx(extPartyIdTx);
        if (!StringUtils.isEmpty(partyId)) {
            c241Req.setPartyId(Long.parseLong(partyId));
        }
        c241Req.setPartyExtSysId((short) 19);
        StPartyRelData stPartyRelData = new StPartyRelData();
        if (!StringUtils.isEmpty(stPartyId)) {
            stPartyRelData.setPartyId(Long.parseLong(stPartyId));
        }
        stPartyRelData.setExtSysId((short) 19);
        stPartyRelData.setRelTypeCd("008");
        c241Req.setStPartyRelData(stPartyRelData);
        return c241Req;

    }


    @Override
    public void expectLookUpValuesWithISOCode() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ISO_COUNTRY_CODE", "1", "Purpose of Account", 1095L, "SPORI", "LTB", 1L));

    }

    @Override
    public void expectDurableMediumSwitchCall(String channel, boolean switchStatus) {
        SwitchDto switchDto = new SwitchDto("LBG", "SW_EnSTPPCAWcMl", new Date(), boxId, 1, "A", switchStatus ? "1" : "0");
        switchDao.save(switchDto);
    }

    @Override
    public boolean expectCRSSwitchCall(String channel, boolean switchStatus) {
        SwitchDto switchDto = new SwitchDto("LTB", "SW_EnDispKYCDtls", new Date(), boxId, 1, "A", switchStatus ? "1" : "0");
        switchDao.save(switchDto);
        return switchStatus;
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
    public void expectRetrieveInvolvedPartyDetailsWithError(ProductArrangement productArrangement, RequestHeader requestHeader) throws ErrorInfo {
        com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsRequest retrieveInvolvedPartyDetailsRequest;
        if (productArrangement.getGuardianDetails() != null && productArrangement.getGuardianDetails().getIsPlayedBy() != null) {
            retrieveInvolvedPartyDetailsRequest = retrieveInvolvedPartyDetailsRequestFactory.convert(productArrangement.getGuardianDetails().getCustomerIdentifier());
        } else {
            retrieveInvolvedPartyDetailsRequest = retrieveInvolvedPartyDetailsRequestFactory.convert(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        }
        soaIpmClient.retrieveInvolvedPartyDetails(retrieveInvolvedPartyDetailsRequest, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        RetrieveInvolvedPartyDetailsResponse retrieveInvolvedPartyDetailsResponse = dataHelper.createRetrieveInvolvedPartyResponse();
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("2");
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("1031");
        involvedPartyManagementServicePortType.thenReturn(retrieveInvolvedPartyDetailsResponse);

    }

    @Override
    public void expectRecordInvolvedPartyDetailsWithError(ProductArrangement productArrangement, RequestHeader requestHeader, boolean isCRSSwitch) throws ErrorInfo {
        com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsRequest retrieveInvolvedPartyDetailsRequest;
        if (productArrangement.getGuardianDetails() != null && productArrangement.getGuardianDetails().getIsPlayedBy() != null) {
            retrieveInvolvedPartyDetailsRequest = retrieveInvolvedPartyDetailsRequestFactory.convert(productArrangement.getGuardianDetails().getCustomerIdentifier());
        } else {
            retrieveInvolvedPartyDetailsRequest = retrieveInvolvedPartyDetailsRequestFactory.convert(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        }
        soaIpmClient.retrieveInvolvedPartyDetails(retrieveInvolvedPartyDetailsRequest, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        RetrieveInvolvedPartyDetailsResponse retrieveInvolvedPartyDetailsResponse = dataHelper.createRetrieveInvolvedPartyResponse();
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("3");
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("1031");
        involvedPartyManagementServicePortType.thenReturn(retrieveInvolvedPartyDetailsResponse);
        RecordInvolvedPartyDetailsRequest recordInvolvedPartyDetailsRequest;
        if (productArrangement.getGuardianDetails() != null && productArrangement.getGuardianDetails().getIsPlayedBy() != null) {
            recordInvolvedPartyDetailsRequest = recordInvolvedPartyDetailsRequestFactory.convert(dataHelper.createRetrieveInvolvedPartyResponse().getInvolvedParty(), productArrangement.getGuardianDetails(), null, isCRSSwitch);
        } else {
            recordInvolvedPartyDetailsRequest = recordInvolvedPartyDetailsRequestFactory.convert(dataHelper.createRetrieveInvolvedPartyResponse().getInvolvedParty(), productArrangement.getPrimaryInvolvedParty(), null, isCRSSwitch);
        }
        soaIpmClient.recordInvolvedPartyDetails(recordInvolvedPartyDetailsRequest, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        RecordInvolvedPartyDetailsResponse recordInvolvedPartyDetailsResponse = dataHelper.createRecordInvolvedPartyResponse();
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("2");
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setReasonCode("999");
        involvedPartyManagementServicePortType.thenReturn(recordInvolvedPartyDetailsResponse);
    }

    @Override
    public void expectRecordDocumentContentCallFails(ProductArrangement productArrangement, RequestHeader requestHeader) throws ParseException, DatatypeConfigurationException, com.lloydstsb.schema.documentmanagement.ifw.ErrorInfo {
        RecordDocumentMetaContentRequest request = recordDocumentMetaContentRequestBuilder.convert(productArrangement.getPrimaryInvolvedParty(), dataHelper.createRetrieveDocContentResponse());
        mockControl.matching("actual.target == 'soaDMS' && actual.methodName == 'recordDocumentMetaContent'");
        soaDocumentManagerClient.recordDocumentMetaContent(request, dataHelper.getContactPointFromRequestHeader(requestHeader), dataHelper.getServiceRequestFromRequestHeader(requestHeader), dataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        mockControlSoaDMSServicePortType.thenReturn(dataHelper.createRecordDocContentResponseFails());
    }
}
