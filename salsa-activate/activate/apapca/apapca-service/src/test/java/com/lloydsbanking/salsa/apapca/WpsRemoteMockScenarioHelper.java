package com.lloydsbanking.salsa.apapca;


import com.infracast.schemas.emx.v10_2.EmxSendSmsRequest;
import com.infracast.schemas.emx.v10_2.EmxSendSmsResponse;
import com.lloydsbanking.salsa.commsmanager.ref.jdbc.ChannelDao;
import com.lloydsbanking.salsa.commsmanager.ref.jdbc.CommunicationDao;
import com.lloydsbanking.salsa.commsmanager.ref.jdbc.CommunicationTemplateDAO;
import com.lloydsbanking.salsa.commsmanager.ref.jdbc.RefDataLookupDAO;
import com.lloydsbanking.salsa.commsmanager.ref.model.Communication;
import com.lloydsbanking.salsa.downstream.arrangement.client.ArrangementClient;
import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClient;
import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClientImpl;
import com.lloydsbanking.salsa.downstream.o2.client.O2Client;
import com.lloydsbanking.salsa.downstream.prd.jdbc.*;
import com.lloydsbanking.salsa.downstream.prd.model.*;
import com.lloydsbanking.salsa.downstream.switches.jdbc.SwitchDao;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.remotemock.MockControlFsSystemServicePortType;
import com.lloydsbanking.salsa.soap.fs.system.StHeader;
import com.lloydsbanking.salsa.soap.fs.system.StSwitchValue;
import com.lloydstsb.ib.wsbridge.system.StB500AServiceMonitor;
import com.lloydstsb.ib.wsbridge.system.StB500BServiceMonitor;
import com.lloydstsb.ib.wsbridge.system.StB555AWServMIS;
import com.lloydstsb.ib.wsbridge.system.StB555BWServMIS;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.ActivateBenefitArrangementRequest;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.ActivateBenefitArrangementResponse;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_communicationmanager.id_communicationeventscheduler.*;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_communicationmanager.messages.SendCommunicationRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WpsRemoteMockScenarioHelper extends RemoteMockScenarioHelper {

    @Autowired
    ProductGroupDao productGroupDao;

    @Autowired
    ProductFeatureDao productFeatureDao;

    @Autowired
    ExternalSystemProductDao externalSystemProductDao;

    @Autowired
    ExternalSystemProductsDao externalSystemProductsDao;

    @Autowired
    ExternalSystemTemplateDao externalSystemTemplateDao;

    @Autowired
    ProductFeatureRelatedDao productFeatureRelatedDao;

    @Autowired
    ProductPropositionDao productPropositionDao;

    @Autowired
    ProductPackageDao productPackageDao;

    @Autowired
    FsSystemClientImpl systemClient;

    @Autowired
    RefDataLookupDAO dao;

    @Autowired
    CommunicationTemplateDAO communicationTemplateDao;

    @Autowired
    ChannelDao channelDao;
    @Autowired
    ArrangementClient arrangementClient;

    @Autowired
    TestDataHelper dataHelper;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    FsSystemClient fsSystemClient;

    @Autowired
    O2Client o2Client;

    @Autowired
    CommunicationDao communicationDao;

    @Autowired
    MockControlFsSystemServicePortType mockControlFsSystemServicePortType;

    @Autowired
    private SwitchDao switchDao;
    @Value("${wps.cache.url}")
    String wpsCacheUrl;

    @Override
    @Rollback(false)
    public void clearUpForWPS() {
        productGroupDao.deleteAll();
        productFeatureDao.deleteAll();
        externalSystemProductDao.deleteAll();
        externalSystemProductsDao.deleteAll();
        externalSystemTemplateDao.deleteAll();
        productFeatureRelatedDao.deleteAll();
        productPropositionDao.deleteAll();
        productPackageDao.deleteAll();
        resetSwitches();
        clearCmEvents();
        clearWpsCache();
    }

    @Override
    public String expectRetrieveProductCondition(DepositArrangement productArrangement,RequestHeader header) throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        expectExternalSystemProductList("92", "LTB");
        expectExternalSystemProductsList("92", "LTB");
        expectExternalSystemTemplateList("92");
        try {
            expectProductFeature("92");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "CCA_Generic";
    }

    @Transactional
    public void expectExternalSystemProductList(String productId, String braCode) {
        ExternalSystemProduct externalSystemProduct1 = new ExternalSystemProduct();
        externalSystemProduct1.setExternalSystemProductId(new ExternalSystemProductId());
        externalSystemProduct1.getExternalSystemProductId().setProductId(new Long(productId));
        externalSystemProduct1.setProductName("Classic Plus Account");
        externalSystemProduct1.getExternalSystemProductId().setExternalSysProdId("01000");
        externalSystemProduct1.getExternalSystemProductId().setEsCode("00010");
        externalSystemProduct1.setEsName("OCIS");
        externalSystemProduct1.setBraCode(braCode);
        externalSystemProductDao.save(externalSystemProduct1);
    }

    @Transactional
    public void expectExternalSystemTemplateList(String productId) {
        ExternalSystemTemplate externalSystemTemplate = new ExternalSystemTemplate();
        externalSystemTemplate.setExternalSystemTemplateId(new ExternalSystemTemplateId());
        externalSystemTemplate.getExternalSystemTemplateId().setProductId(new Long(productId));
        externalSystemTemplate.getExternalSystemTemplateId().setExternalSysCode("00001");
        externalSystemTemplate.setTemplateId(new Long(1));
        externalSystemTemplate.setExternalSysTemplId("CCA_Generic");
        externalSystemTemplateDao.save(externalSystemTemplate);
    }


    @Transactional
    public void expectExternalSystemProductsList(String productId, String braCode) {
        ExternalSystemProducts externalSystemProducts = new ExternalSystemProducts();
        externalSystemProducts.setExternalSysProdId("01000");
        externalSystemProducts.setEsCode("00010");
        externalSystemProducts.setId(123l);
        externalSystemProducts.setProId(new Long(productId));
        externalSystemProductsDao.save(externalSystemProducts);
    }

    @Transactional
    public void expectProductFeature(String productId) throws ParseException {
        List<ProductFeature> productFeatureList = new ArrayList<>();
        ProductFeature productFeature = new ProductFeature();
        productFeature.setProductId(new Long(productId));
        productFeature.setProductFeatureCode("6465");
        productFeature.setProductFeatureName("isVantage");
        productFeature.setProductFeatureType("OD_Offer_Flag");
        productFeature.setProductFeatureTypeDesc("Overdraft Offer Flag");
        productFeature.setProductName("Classic Plus Account");
        productFeature.setProductType("102");
        productFeature.setFeatureValue("Yes");
        String inputStr = "20-JAN-12";
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date inputDate = dateFormat.parse(inputStr);
        productFeature.setStartDate(inputDate);
        inputStr = "20-JAN-13";
        Date endDate = dateFormat.parse(inputStr);
        productFeature.setEndDate(endDate);
        productFeatureList.add(productFeature);
        productFeatureDao.save(productFeatureList);
    }

    @Override
    public void expectActivateBenefitCall(ProductArrangement productArrangement, RequestHeader header, int reasonCode) {
        ActivateBenefitArrangementRequest activateBenefitArrangementRequest = dataHelper.activateBenefitArrangementRequest();
        mockControl.matching("actual.target == 'arrangementSetup' && actual.methodName == 'activateBenefitArrangement'");
        ContactPoint contactPoint = dataHelper.getContactPointFromRequestHeader(header);
        ServiceRequest serviceRequest = dataHelper.getServiceRequestFromRequestHeader(header);
        SecurityHeaderType securityHeaderType = dataHelper.getSecurityHeaderTypeFromRequestHeader(header);
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders());
        arrangementClient.activateBenefitArrangement(activateBenefitArrangementRequest, contactPoint, serviceRequest, securityHeaderType, bapiInformation);
        ActivateBenefitArrangementResponse activateBenefitArrangementResponse = new ActivateBenefitArrangementResponse();
        mockControl.thenReturn(activateBenefitArrangementResponse);
    }

    @Override
    public void expectSendCommunicationCall(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header, String source, String communicationType, boolean isFirstCall) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        if (isFirstCall) {
            setUpLookData();
            setUpChannelData();
            setUpTemplateData();
            //expectSwitchCall(false, false, false);
        }
        //expectB555AuditCall();

        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, notificationEmail, header, source, communicationType);
        if (communicationType.equalsIgnoreCase("SMS")) {
        //expectO2ServiceCall(dataHelper.generateO2Request(sendCommunicationRequest), dataHelper.createO2SuccessResponse(), header);
    }
    }
    @Override
    public void expectScheduleCommunicationCall(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header, String source, String communicationType) throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg {
    }

    private void setUpLookData() {
        dao.save(dataHelper.generateLookUpData());
    }

    public void setUpChannelData() {
        channelDao.save(dataHelper.generateChannelData());
    }

    public void setUpTemplateData() {
        communicationTemplateDao.save(dataHelper.generateEmailCommunicationTemplate(dataHelper.TEST_EMAIL_TEMPLATE));
        communicationTemplateDao.save(dataHelper.generateEmailCommunicationTemplate(dataHelper.TEST_EMAIL_SUCCESS_TEMPLATE));
        communicationTemplateDao.save(dataHelper.generateSmsCommunicationTemplate(dataHelper.TEST_SMS_TEMPLATE));
        communicationTemplateDao.save(dataHelper.generateSmsCommunicationTemplate(dataHelper.TEST_EMAIL_BENEFIT_TEMPLATE));
    }



    public void expectSwitchCall(boolean emailSwitchValue, boolean smsSwitchValue, boolean durableMedSwitchValue) {
        StB500AServiceMonitor stB500AServiceMonitor = new StB500AServiceMonitor();
        StHeader stHeader = new StHeader();
        stHeader.setUseridAuthor("AAGATEWAY");
        stHeader.setChanid("IBL");
        stHeader.setChansecmode("PWD");
        stHeader.setChanctxt(BigInteger.ONE);

        stB500AServiceMonitor.setStheader(stHeader);

        StB500BServiceMonitor stB500BServiceMonitor = new StB500BServiceMonitor();
        stB500BServiceMonitor.getAstswitchvalue().add(setSwitch(dataHelper.QUEUE_EMAIL_SWITCH, emailSwitchValue));
        stB500BServiceMonitor.getAstswitchvalue().add(setSwitch(dataHelper.QUEUE_SMS_SWITCH, smsSwitchValue));
        stB500BServiceMonitor.getAstswitchvalue().add(setSwitch(dataHelper.DURABLE_MEDIUM_SWITCH, durableMedSwitchValue));
        stB500BServiceMonitor.getAstswitchvalue().add(setSwitch("SW_CM_UseO2", true));
        stB500BServiceMonitor.getAstswitchvalue().add(setSwitch("SW_EmailPhishing", true));
        stB500BServiceMonitor.getAstswitchvalue().add(setSwitch("SW_CM_Enable_MI", true));
        stB500BServiceMonitor.getAstswitchvalue().add(setSwitch("SW_FATCAupdate", true));

        mockControl.matching("actual.target == 'fsSystem' && actual.methodName == 'b500ServiceMonitor'");
        fsSystemClient.retSwitchValues(stB500AServiceMonitor);
        mockControlFsSystemServicePortType.thenReturn(stB500BServiceMonitor);

    }

    private StSwitchValue setSwitch(final String switchName, boolean switchStatus) {
        StSwitchValue stSwitchValue = new StSwitchValue();
        stSwitchValue.setBSwitchValue(switchStatus);
        stSwitchValue.setSwitchname(switchName);
        stSwitchValue.setSwitchbrand("LBG");

        return stSwitchValue;
    }

    public void expectB555AuditCall() {
        StB555AWServMIS stB555AWServMIS = dataHelper.createB555Request();
        StB555BWServMIS stB555BWServMIS = dataHelper.createB555Response();
        expectB555Call(stB555AWServMIS, stB555BWServMIS);
    }

    private void expectB555Call(StB555AWServMIS stB555AWServMIS, StB555BWServMIS stB555BWServMIS) {
        fsSystemClient.b555WServMIS(stB555AWServMIS);
        mockControlFsSystemServicePortType.thenReturn(stB555BWServMIS);
    }

    public void expectO2ServiceCall(EmxSendSmsRequest smsRequest, EmxSendSmsResponse smsResponse, RequestHeader header) {
        ContactPoint contactPoint = dataHelper.getContactPointFromRequestHeader(header);
        ServiceRequest serviceRequest = dataHelper.getServiceRequestFromRequestHeader(header);
        SecurityHeaderType securityHeaderType = dataHelper.getSecurityHeaderTypeFromRequestHeader(header);

        mockControl.matching("actual.target == 'o2sms'");

        o2Client.sendSMS(smsRequest, contactPoint, serviceRequest, securityHeaderType);
        mockControl.thenReturn(smsResponse);
    }

    @Override
    public void expectSendCommunicationCallForPostFulfilment(ProductArrangement productArrangement, String notificationEmail, RequestHeader header) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest((DepositArrangement) productArrangement, notificationEmail, header, null, "EMAIL");
        expectSwitchCall(true, true, true);
        expectB555AuditCall();
        expectO2ServiceCall(dataHelper.generateO2Request(sendCommunicationRequest), dataHelper.createO2SuccessResponse(), header);
    }

    @Override
    public void expectSendCommunicationCallWithError(DepositArrangement depositArrangement, String notificationEmail, RequestHeader header) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, notificationEmail, header, null, "Email");
        expectSwitchCall(true, true, true);
        expectB555AuditCall();
        expectO2ServiceCall(dataHelper.generateO2Request(sendCommunicationRequest), dataHelper.createO2SuccessResponse(), header);
    }



    public void clearWpsCache() {
        try {
            URL wpsCache = new URL(wpsCacheUrl);
            URLConnection uc = wpsCache.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            in.close();
        } catch (Exception e) {

        }
    }
    public void sleep() {
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resetSwitches() {
        switchDao.deleteAll();
    }

    public void clearCmEvents() {
        for (Communication communication : communicationDao.findAll()) {
            communicationDao.delete(communication);
        }
    }
}
