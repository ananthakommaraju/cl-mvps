package com.lloydsbanking.salsa.apacc;


import com.infracast.schemas.emx.v10_2.EmxSendSmsRequest;
import com.infracast.schemas.emx.v10_2.EmxSendSmsResponse;
import com.lloydsbanking.salsa.commsmanager.ref.jdbc.ChannelDao;
import com.lloydsbanking.salsa.commsmanager.ref.jdbc.CommunicationDao;
import com.lloydsbanking.salsa.commsmanager.ref.jdbc.CommunicationTemplateDAO;
import com.lloydsbanking.salsa.commsmanager.ref.jdbc.RefDataLookupDAO;
import com.lloydsbanking.salsa.commsmanager.ref.model.*;
import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClient;
import com.lloydsbanking.salsa.downstream.o2.client.O2Client;
import com.lloydsbanking.salsa.downstream.prd.jdbc.*;
import com.lloydsbanking.salsa.downstream.prd.model.*;
import com.lloydsbanking.salsa.downstream.switches.jdbc.SwitchDao;
import com.lloydsbanking.salsa.remotemock.MockControlFsSystemServicePortType;
import com.lloydsbanking.salsa.soap.fs.system.StHeader;
import com.lloydsbanking.salsa.soap.fs.system.StSwitchValue;
import com.lloydstsb.ib.wsbridge.system.StB500AServiceMonitor;
import com.lloydstsb.ib.wsbridge.system.StB500BServiceMonitor;
import com.lloydstsb.ib.wsbridge.system.StB555AWServMIS;
import com.lloydstsb.ib.wsbridge.system.StB555BWServMIS;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.ProductOffer;
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
import java.util.*;

public class WpsRemoteMockScenarioHelper extends RemoteMockScenarioHelper {

    @Autowired
    ProductGroupDao productGroupDao;

    @Autowired
    ProductFeatureDao productFeatureDao;
    @Autowired
    CommunicationDao communicationDao;
    @Autowired
    ExternalSystemProductDao externalSystemProductDao;
    @Autowired
    ExternalSystemProductsDao externalSystemProductsDao;
    @Autowired
    ExternalSystemTemplateDao externalSystemTemplateDao;
    @Autowired
    CommunicationTemplateDAO communicationTemplateDao;
    @Autowired
    ProductFeatureRelatedDao productFeatureRelatedDao;
    @Autowired
    ProductPropositionDao productPropositionDao;
    @Autowired
    ProductPackageDao productPackageDao;
    @Autowired
    FsSystemClient fsSystemClient;
    @Autowired
    MockControlFsSystemServicePortType mockControlFsSystemServicePortType;
    @Autowired
    RefDataLookupDAO dao;
    @Autowired
    CommunicationTemplateDAO communicationTemplateDAO;
    @Autowired
    O2Client o2Client;
    @Autowired
    ChannelDao channelDao;
    @Autowired
    ProductPackagesDao productPackagesDao;
    @Value("${wps.cache.url}")
    String wpsCacheUrl;
    @Autowired
    private SwitchDao switchDao;

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
    public List<ProductOffer> expectRetrieveProductCondition(Product product, RequestHeader header) throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        expectExternalSystemProductList(product.getProductIdentifier(), "LTB");
        expectExternalSystemProductsList(product.getProductIdentifier(), "LTB");
        expectExternalSystemTemplateList(product.getProductIdentifier());
        expectProductPackages(product.getProductoffer().get(0).getProdOfferIdentifier());
        expectProductPropositionListByPrepositionId(product.getProductIdentifier());
        try {
            expectProductFeature(product.getProductIdentifier());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse response = dataHelper.createRetrieveProductConditionsResponse();
        return response.getProduct().get(0).getProductoffer();
    }

    @Transactional
    public void expectExternalSystemProductList(String productId, String braCode) {
        ExternalSystemProduct externalSystemProduct1 = new ExternalSystemProduct();
        externalSystemProduct1.setExternalSystemProductId(new ExternalSystemProductId());
        externalSystemProduct1.getExternalSystemProductId().setProductId(new Long(productId));
        externalSystemProduct1.setProductName("Classic Plus Account");
        externalSystemProduct1.getExternalSystemProductId().setExternalSysProdId("01000");
        externalSystemProduct1.getExternalSystemProductId().setEsCode("2Vm");
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
        externalSystemProducts.setEsCode("2Vm");
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

    @Transactional
    public void expectProductPackages(String packID) {
        ProductPackages productPackages = new ProductPackages();
        productPackages.setId(Long.valueOf(packID));
        productPackages.setPpnId(new Long(9211));
        productPackagesDao.save(productPackages);
    }

    @Transactional
    public void expectProductPropositionListByPrepositionId(String productId) {
        ProductProposition productProposition = new ProductProposition();
        productProposition.setId(Long.valueOf("9211"));
        productProposition.setProId(Long.valueOf(productId));
        productProposition.setPropositionName("Advance");
        productPropositionDao.save(productProposition);
    }

    @Override
    public void expectSendCommunicationCall(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType, boolean isFirstCall) throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        if (isFirstCall) {
            setUpLookData();
            setUpChannelData();
            setUpTemplateData();
            expectSwitchCall(false, false, false);
        }
        expectB555AuditCall();
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(productArrangement, notificationEmail, header, source, communicationType);
        expectO2ServiceCall(dataHelper.generateO2Request(sendCommunicationRequest), dataHelper.createO2SuccessResponse(), header);

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

    public void expectO2ServiceCall(EmxSendSmsRequest smsRequest, EmxSendSmsResponse smsResponse, RequestHeader header) {
        ContactPoint contactPoint = dataHelper.getContactPointFromRequestHeader(header);
        ServiceRequest serviceRequest = dataHelper.getServiceRequestFromRequestHeader(header);
        SecurityHeaderType securityHeaderType = dataHelper.getSecurityHeaderTypeFromRequestHeader(header);

        mockControl.matching("actual.target == 'o2sms'");

        o2Client.sendSMS(smsRequest, contactPoint, serviceRequest, securityHeaderType);
        mockControl.thenReturn(smsResponse);
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

    @Override
    public void expectGenerateDocumentCall(ProductArrangement productArrangement, List<ProductOffer> productOfferList, RequestHeader header) {
        setLookUpData();
        insertTemplateData("CCA_Generic_SGND", "pages/p40_06_agreement/4006htm532");
    }

    public void setLookUpData() {
        RefDataLookup lookUp = new RefDataLookup(new RefDataLookupId("Cnt_Pnt_Prtflio", "0000777505"), "description", 1L, (short) 1, "LTB", "LTB");
        dao.save(lookUp);
    }

    public void insertTemplateData(String templateId, String contentKey) {
        CommunicationTemplateElement element1 = new CommunicationTemplateElement(templateId, Short.valueOf("1"), "1", contentKey);
        Set<CommunicationTemplateElement> communicationTemplateElements = new HashSet<CommunicationTemplateElement>();
        communicationTemplateElements.add(element1);
        CommunicationTemplate communicationTemplate = new CommunicationTemplate(templateId, "EMAIL", "Mail subject", "2", communicationTemplateElements);
        communicationTemplateDAO.save(communicationTemplate);
    }

    @Override
    public void expectScheduleCommunicationCall(ProductArrangement productArrangement, String notificationEmail, RequestHeader header, String source, String communicationType) throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg {

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
    }

    public void resetSwitches() {
        switchDao.deleteAll();
    }

    public void clearCmEvents() {
        for (Communication communication : communicationDao.findAll()) {
            communicationDao.delete(communication);
        }
    }

    public void  sleep() {
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
