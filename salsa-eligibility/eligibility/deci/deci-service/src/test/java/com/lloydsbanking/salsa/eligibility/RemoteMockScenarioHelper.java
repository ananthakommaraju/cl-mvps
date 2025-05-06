package com.lloydsbanking.salsa.eligibility;

import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydsbanking.salsa.downstream.cbs.client.e184.E184Client;
import com.lloydsbanking.salsa.downstream.cbs.client.e220.E220Client;
import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClient;
import com.lloydsbanking.salsa.downstream.ocis.client.f075.F075Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f336.F336Client;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefInstructionHierarchyDao;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefInstructionLookupDao;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefInstructionRulesDao;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefLookupDao;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionHierarchyDto;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionLookupDto;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.downstream.ref.model.RefLookupDto;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.downstream.switches.jdbc.SwitchDao;
import com.lloydsbanking.salsa.downstream.switches.model.SwitchDto;
import com.lloydsbanking.salsa.downstream.user.client.UserClient;
import com.lloydsbanking.salsa.downstream.user.convert.BapiHeaderUserToStHeaderConverter;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.remotemock.MockControlCbsE184ServicePortType;
import com.lloydsbanking.salsa.remotemock.MockControlCbsE220ServicePortType;
import com.lloydsbanking.salsa.remotemock.MockControlFsAccountServicePortType;
import com.lloydsbanking.salsa.remotemock.MockControlFsSystemServicePortType;
import com.lloydsbanking.salsa.remotemock.MockControlFsUserServicePortType;
import com.lloydsbanking.salsa.remotemock.MockControlOcisF075ServicePortType;
import com.lloydsbanking.salsa.remotemock.MockControlOcisF336ServicePortType;
import com.lloydsbanking.salsa.remotemock.MockControlServicePortType;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Req;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Resp;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Req;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Resp;
import com.lloydsbanking.salsa.soap.fs.user.StHeader;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Req;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Resp;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Req;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Resp;
import com.lloydstsb.ib.wsbridge.system.StB093AEventLogReadList;
import com.lloydstsb.ib.wsbridge.system.StB093BEventLogReadList;
import com.lloydstsb.ib.wsbridge.user.StB162AUserAccReadList;
import com.lloydstsb.ib.wsbridge.user.StB162BUserAccReadList;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.messages.RequestHeader;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RemoteMockScenarioHelper implements ScenarioHelper {
    @Autowired
    MockControlServicePortType mockControl;

    @Autowired
    MockControlCbsE220ServicePortType mockControlCbsE220;

    @Autowired
    RefInstructionHierarchyDao refInstructionHierarchyDao;

    @Autowired
    RefInstructionLookupDao refInstructionLookupDao;

    @Autowired
    RefInstructionRulesDao refInstructionRulesDao;

    @Autowired
    RefLookupDao refLookupDao;

    @Autowired
    F075Client f075Client;

    @Autowired
    SwitchService switchClient;

    @Autowired
    private SwitchDao switchDao;

    @Autowired
    MockControlFsAccountServicePortType mockFsAccountControl;

    @Autowired
    AccountClient accountClient;

    Map<String, E220Client> cbsE220ClientMap;

    @Autowired
    TestDataHelper dataHelper;


    @Autowired
    F336Client f336Client;

    @Autowired
    UserClient userClient;

    @Autowired
    MockControlFsUserServicePortType mockFsUserControl;

    @Autowired
    MockControlCbsE220ServicePortType mockE220Control;

    @Autowired
    MockControlFsSystemServicePortType mockFsSystemControl;

    @Autowired
    MockControlOcisF336ServicePortType mockOcisF336Control;

    @Autowired
    MockControlOcisF075ServicePortType mockOcisF075Control;

    @Autowired
    FsSystemClient fsSystemClient;

    Map<String, E184Client> cbsE184ClientMap;

    @Autowired
    MockControlCbsE184ServicePortType mockControlCbsE184ServicePortType;

    HeaderRetriever headerRetriever = new HeaderRetriever();

    BapiHeaderUserToStHeaderConverter bapiHeaderUserToStHeaderConverter = new BapiHeaderUserToStHeaderConverter();

    @Value("${salsa.fs.boxid}")
    int boxId;

    ChannelToBrandMapping channelToBrandMapping = new ChannelToBrandMapping();

    @Override
    @Transactional
    public void expectGetParentInstructionCall(String insMnemonic, String insNarrative, Integer insPriority, String parInsMnemonic, String channel, String parInsNarrative) {

        RefInstructionHierarchyDto hierarchyDto = new RefInstructionHierarchyDto(insMnemonic, insNarrative, insPriority, parInsMnemonic, channel, parInsNarrative);
        refInstructionHierarchyDao.save(hierarchyDto);
    }

    @Override
    @Transactional
    public void expectGetParentInstructionCallWithEmptyResult() {
    }

    @Override
    @Transactional
    public void expectGetProductArrangementInstructionCall(String insMnemonic, String host, String extSysId, String channel, String ephText) {

        RefInstructionLookupDto instructionLookupDto = new RefInstructionLookupDto(insMnemonic, host, new BigDecimal(extSysId), channel, ephText);
        refInstructionLookupDao.save(instructionLookupDto);
    }

    @Override
    @Transactional
    public void expectGetProductArrangementInstructionCallWithEmptyResult() {

    }

    @Override
    @Transactional
    public void expectGetCompositeInstructionConditionCall(List<RefInstructionRulesDto> refInstructionRulesDtos) {
        refInstructionRulesDao.save(refInstructionRulesDtos);


    }

    @Override
    @Transactional
    public void expectCompositeInstructionConditionCall(String insMnemonic, String groupRule, String groupDesc, String groupCmsReason, String rule, String ruleDesc, String cmsReason, String groupRuleType, String ruleType, String ruleParamValue, String channel, BigDecimal ruleParamSeq) {

        refInstructionRulesDao.save(dataHelper.createRuleDto(insMnemonic, groupRule, groupDesc, groupCmsReason, rule, ruleDesc, cmsReason, groupRuleType, ruleType, ruleParamValue, channel, ruleParamSeq));
    }

    @Override
    @Transactional
    public void expectRefDataAvailable() {

        expectGetParentInstructionCall("P_CLASSIC", "Classic Account", null, "G_AVA", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Added Value Account");
        expectGetParentInstructionCall("P_CLASSIC", "Classic Account", null, "G_AVA", "IBH", "Added Value Account");
        expectGetParentInstructionCall("P_BOD_RBB", "Apply Overdraft", null, "G_OD", "IBH", "Business Overdraft");
        expectGetParentInstructionCall("P_BOD_RBB", "Apply Overdraft", null, "G_OD", "STL", "Business Overdraft");
         expectGetParentInstructionCall("P_BLN_RBB", "Apply Overdraft", null, "G_OD", "STL", "Business Overdraft");


        expectGetParentInstructionCall("P_BOD_RBB", "Apply Overdraft", null, "G_OD", "BBL", "Business Overdraft");
        expectGetParentInstructionCall("P_BLN_RBB", "Apply Overdraft", null, "G_OD", "BBL", "Business Overdraft");
        expectGetParentInstructionCall("P_BOD_RBB", "Apply Overdraft", null, "G_OD", "STS", "Business Overdraft");
        expectGetParentInstructionCall("P_BLN_RBB", "Apply Overdraft", null, "G_OD", "STS", "Business Overdraft");
        expectGetParentInstructionCall("P_BOD_RBB", "Apply Overdraft", null, "G_OD", "STV", "Business Overdraft");
        expectGetParentInstructionCall("P_BLN_RBB", "Apply Overdraft", null, "G_OD", "STV", "Business Overdraft");


        expectGetParentInstructionCall("G_TRAV_MON", "G Travel Money", null, "", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "G Travel Money");
        expectGetParentInstructionCall("P_TRAV_MON2", "Travel Money2", Integer.valueOf(0), "G_TRAV_MON", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Travel Money2");
        expectGetParentInstructionCall("P_TRAV_MON3", "Travel Money3", Integer.valueOf(2), "G_TRAV_MON", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Travel Money3");
        expectGetParentInstructionCall("P_TRAV_MON", "Travel Money", Integer.valueOf(1), "G_TRAV_MON", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Travel Money");
        expectGetParentInstructionCall("P_LOANS", "Loans", Integer.valueOf(1), "G_LOANS", "IBH", "LOANS");

        expectGetParentInstructionCall("P_TRAV_MONEY", "Travel Money", Integer.valueOf(1), "G_TRAV_MON", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Travel Money");

        expectGetParentInstructionCall("P_EASY_SVR", "Savings", Integer.valueOf(4), "G_SAVINGS", "IBH", "Savings");
        expectGetParentInstructionCall("G_LOANS", "Loans", Integer.valueOf(4), "G_LOANS", "IBH", "Loans");
        expectGetParentInstructionCall("P_CASH_ISA", "Cash ISA", null, "G_CASH_ISA", "IBL", "Cash ISA");
        expectGetParentInstructionCall("P_LOAN", "Personal Loan", null, "G_LOAN", "IBH", "Personal Loan");
        expectGetParentInstructionCall("P_LOAN", "Personal Loan", null, "G_LOAN", "IBL", "Personal Loan");
        expectGetParentInstructionCall("P_LOAN", "Personal Loan", null, "G_LOAN", "STL", "Personal Loan");
        expectGetParentInstructionCall("P_CCLI", "Credit Card Limit Increase", null, "G_CCLI", "IBL", "Credit Card Limit Increase");
        expectGetParentInstructionCall("G_CCLI", "Credit Card Limit Increase", null, null, "IBL", "Credit Card Limit Increase");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "IBL0", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "IBL0", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "TBV", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "TBV", "Balance Transfer");

        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "IBL2", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "IBL2", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "IBL", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "IBL", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "IBH", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "IBH", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "IBV", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "IBV", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "IBS", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "IBS", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "STL", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "STL", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "STV", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "STV", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "STS", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "STS", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "BBL", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "BBL", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "BBV0", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "BBV0", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "BBV1", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "BBV1", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "BBV2", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "BBV2", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "BBV3", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "BBV3", "Balance Transfer");
        expectGetParentInstructionCall("P_BT", "Balance Transfer", null, "G_BT", "BBV4", "Balance Transfer");
        expectGetParentInstructionCall("G_BT", "Balance Transfer", null, null, "BBV4", "Balance Transfer");

        expectGetParentInstructionCall("P_PPC", "Payment Protection Cover", null, "G_PPC", "IBL", "Payment Protection Cover");
        expectGetParentInstructionCall("G_PPC", "Payment Protection Cover", null, null, "IBL", "Payment Protection Cover");
        expectGetParentInstructionCall("P_CC_PLAT", "Platinum Balance Transfer Card", null, "G_CREDCARD", "IBL0", "Credit Card");
        expectGetParentInstructionCall("G_CREDCARD", "Platinum Balance Transfer Card", null, null, "IBL0", "Credit Card");
        expectGetParentInstructionCall("P_CC_ADV", "Advance Credit Card", null, "G_CREDCARD", "IBL", "Credit Card");
        expectGetParentInstructionCall("P_OD", "Overdraft", null, "G_OD", "IBL", "Overdraft");
        expectGetParentInstructionCall("G_OD", "Overdraft", null, null, "IBL", "Overdraft");
        expectGetParentInstructionCall("P_BODA_RBB", "Amend Overdraft", null, "G_OD", "IBH", "Business Overdraft");
        expectGetCompositeInstructionConditionCall(new TestDataHelper().createRuleDtoList());
    }

    @Override
    public void expectF075Call(RequestHeader header, String kycStatus) throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        F075Resp f075Response = new TestDataHelper().createKYCResponse(kycStatus);
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        F075Req request = new TestDataHelper().createF075Request(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()));

        f075Client.knowYourCustomerStatus(request, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF075Control.thenReturn(f075Response);
    }

    @Override
    public void expectF075CallWithErrorCode(RequestHeader header, int errorCode) throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        F075Resp f075Response = new TestDataHelper().createKYCErrorResponse(errorCode);
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        F075Req request = new TestDataHelper().createF075Request(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()));
        f075Client.knowYourCustomerStatus(request, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF075Control.thenReturn(f075Response);
    }

    @Override
    public void expectE220Call(RequestHeader header, String sortCode, String participantId, String shadowLimitZero , String stricFlag) {
        dataHelper = new TestDataHelper();
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        E220Req e220Req = new TestDataHelper().createShadowLimitRequest(sortCode, participantId);
        CBSAppGrp cbsAppGrp = dataHelper.createCBSAppGroupFromSortCode(sortCode);
        E220Resp e220Resp = dataHelper.createShadowLimitResponse(shadowLimitZero, stricFlag);
        e220Client(header.getChannelId()).getShadowLimit(e220Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        mockControlCbsE220.thenReturn(e220Resp);
    }

    @Rollback(false)
    public void clearUp() {
        refInstructionHierarchyDao.deleteAll();
        //many times instructionHierarchyVw is not getting cleared while executing tests
        Iterable<RefInstructionHierarchyDto> refInstructionHierarchyDtos = refInstructionHierarchyDao.findAll();
        if(refInstructionHierarchyDtos!=null) {
            for (RefInstructionHierarchyDto refInstructionHierarchyDto :refInstructionHierarchyDtos) {
                refInstructionHierarchyDao.delete(refInstructionHierarchyDto);
            }
        }
        refInstructionLookupDao.deleteAll();
        refInstructionRulesDao.deleteAll();
        refLookupDao.deleteAll();
    }

    private E220Client e220Client(String channel) {

        return cbsE220ClientMap.get(dataHelper.getBrandForChannel(channel));
    }

    public Map<String, E220Client> getCbsE220ClientMap() {
        return cbsE220ClientMap;
    }

    public void setCbsE220ClientMap(Map<String, E220Client> cbsE220ClientMap) {
        this.cbsE220ClientMap = cbsE220ClientMap;
    }

    @Override
    public void expectCBSGenericGatewaySwitchCall(String channel, boolean switchStatus) {
        SwitchDto switchDto = new SwitchDto(channelToBrandMapping.getBrandForChannel(channel), "SW_CBSGenGtwy", new Date(), boxId, 1, "A", switchStatus ? "1" : "0");
        switchDao.save(switchDto);
    }

    @Override
    public void expectB766Call(RequestHeader requestHeader, String sortCode) {
        accountClient.retrieveCBSAppGroup(dataHelper.createB766Request(requestHeader, sortCode));
        mockFsAccountControl.thenReturn(dataHelper.createB766Response(dataHelper.getCBSAppGrpForSortCode(sortCode), 0));
    }

    @Override
    public void expectF336Call(RequestHeader header, int productOneGroupId, int productTwoGroupId) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F336Req f336Req = dataHelper.createF336Request(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()));

        F336Resp f336Resp = dataHelper.createF336Response(productOneGroupId, productTwoGroupId);
        f336Client.getProductHoldings(f336Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF336Control.thenReturn(f336Resp);

    }

    @Override
    public void expectB162Call(RequestHeader header, String spndngRewardId, String productOneAccountType, String productTwoAccountTyp, String sellerEntity) {
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()).getBAPIHeader();
        StHeader stHeader = bapiHeaderUserToStHeaderConverter.convert(bapiHeader);
        StB162AUserAccReadList stB162Req = dataHelper.createB162Request(stHeader);
        StB162BUserAccReadList b162Resp = dataHelper.createB162Response(spndngRewardId, productOneAccountType, productTwoAccountTyp, sellerEntity);
        userClient.retrieveAccessibleArrangements(stB162Req);

        mockFsUserControl.thenReturn(b162Resp);
    }

    @Override
    public void expectB093Call(String threshold, RequestHeader header, boolean isAboveThresold) throws DetermineEligibleInstructionsInternalServiceErrorMsg {

        StB093AEventLogReadList b093Request = null;
        try {
            b093Request = new TestDataHelper().createRequest(threshold, "W074", header);
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        StB093BEventLogReadList b093Response = new TestDataHelper().createB093Response(isAboveThresold);

        mockControl.matching("actual.target == 'fsSystem' && actual.methodName == 'b093EventLogReadList' && differingProperties(expected.arguments[8], actual.arguments[8], " +
                "['tmstmpStart/millisecond', 'tmstmpEnd/millisecond', 'tmstmpEnd/fractionalSecond'," +
                "'tmstmpStart/timezone','tmstmpEnd/timezone','tmstmpEnd/hour','tmstmpStart/hour','tmstmpStart/fractionalSecond']).isEmpty()");
        try {
            fsSystemClient.fetchAccountEvent(b093Request);
            mockFsSystemControl.thenReturn(b093Response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void expectE184CallForCBSIndicator(RequestHeader header,String sortCode, String accountNo, int cbsIndicator) {
        E184Req e184Req = dataHelper.createE184Request(sortCode,accountNo);
        E184Resp e184Resp = dataHelper.createE184Response(cbsIndicator);
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), "{http://www.lloydstsb.com/Schema/Enterprise/LCSM_CommunicationManagement}CommunicationAcceptanceService", "determineEligibleCustomerInstruction");
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        CBSAppGrp cbsAppGrp = dataHelper.createCBSAppGroupFromSortCode(sortCode);

        E184Client e184Client = e184Client(TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID);

        try {
            e184Client.getCbsIndicator(e184Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mockControlCbsE184ServicePortType.thenReturn(e184Resp);
    }

    @Override
    public void expectRBBSlookupCall(String channel) {

        refLookupDao.save(new RefLookupDto(BigDecimal.valueOf(48570), "RBB_LOAN_ACCPT", "Y", "TL", "RBB_LOAN_ACCPT", null , "B_LN_ACCPT", "B_LN_ACCPT", "Business Loan is accepted", "RBB_LOAN_ACCPT", "en", "Active", channel, null));
        refLookupDao.save(new RefLookupDto(BigDecimal.valueOf(48569), "RBB_LOAN_DCLN", "Y", "TL", "RBB_LOAN_DCLN", null , "B_LN_DEC", "B_LN_DEC", "Business Loan is declined", "RBB_LOAN_DEC", "en", "Active", channel, null));
        refLookupDao.save(new RefLookupDto(BigDecimal.valueOf(48568), "RBB_OD_ACCPT", "Y", "TL", "RBB_OD_ACCPT", null , "B_OD_ACCPT", "B_OD_ACCPT","Business Overdraft is accepted",  "RBB_OD_ACCPT", "en", "Active", channel, null));
        refLookupDao.save(new RefLookupDto(BigDecimal.valueOf(48567), "RBB_OD_DCLN", "Y", "TL", "RBB_OD_DCLN", null , "B_OD_DEC","B_OD_DEC", "Business Overdraft is declined",  "RBB_OD_DEC", "en", "Active", channel, null));
        refLookupDao.save(new RefLookupDto(BigDecimal.valueOf(48566), "TRKNG_EVENT_IDS", "Y", "TL", "W074", null , "W074", "TRKNG_EVENT_IDS", "TRKNG_EVENT_IDS", "RBB_OD_DEC", "en", "Active", channel, null));

    }

    @Override
    public void expectGetChannelFromContactPointId(String contactPointid, String channel) {
        RefLookupDto lookupDto = new RefLookupDto(new BigDecimal("58190"),
                "Cnt_Pnt_Prtflio",
                "HI_DISCOUNT",
                "Y",
                "TL",
                new BigDecimal("008"),
                "CONTACT_POINT_ID",
                contactPointid,
                "staff",
                null,
                "en",
                "active",
                channel,
                null);
        refLookupDao.save(lookupDto);

    }

    private E184Client e184Client(String channel) {
        return cbsE184ClientMap.get(dataHelper.getBrandForChannel(channel));
    }

    public Map<String, E184Client> getCbsE184ClientMap() {
        return cbsE184ClientMap;
    }

    public void setCbsE184ClientMap(Map<String, E184Client> cbsE184ClientMap) {
        this.cbsE184ClientMap = cbsE184ClientMap;
    }

}

