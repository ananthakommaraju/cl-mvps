package com.lloydsbanking.salsa.eligibility.wz;

import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydsbanking.salsa.downstream.cbs.client.e141.E141Client;
import com.lloydsbanking.salsa.downstream.cbs.client.e220.E220Client;
import com.lloydsbanking.salsa.downstream.cbs.client.e591.E591Client;
import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClient;
import com.lloydsbanking.salsa.downstream.fsystem.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.downstream.ocis.client.f075.F075Client;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.prd.jdbc.*;
import com.lloydsbanking.salsa.downstream.prd.model.*;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefLookupDao;
import com.lloydsbanking.salsa.downstream.ref.model.RefLookupDto;
import com.lloydsbanking.salsa.downstream.switches.jdbc.SwitchDao;
import com.lloydsbanking.salsa.downstream.switches.model.SwitchDto;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.remotemock.*;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Req;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Resp;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Req;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Resp;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.E591Req;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.E591Resp;
import com.lloydsbanking.salsa.soap.fs.system.StError;
import com.lloydsbanking.salsa.soap.fs.system.StEventType;
import com.lloydsbanking.salsa.soap.fs.system.StHeader;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Req;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Resp;
import com.lloydstsb.ib.wsbridge.system.StB695AProductEventReadList;
import com.lloydstsb.ib.wsbridge.system.StB695BProductEventReadList;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.ErrorInfo;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RemoteMockScenarioHelper implements ScenarioHelper {

    @Autowired
    ReferenceDataLookUpDao referencePamDataLookUpDao;

    @Autowired
    ProductEligibilityRulesDao productEligibilityRulesDao;
    @Autowired
    ExternalSystemProductsDao externalSystemProductsDao;
    @Autowired
    ApplicationsDao applicationsDao;

    @Autowired
    PartyApplicationsDao dao;

    @Autowired
    RefInstructionLookupPrdDao refInstructionLookupPrdDao;

    @Autowired
    RefInstructionHierarchyPrdDao refInstructionHierarchyPrdDao;

    @Autowired
    IndividualsDao individualsDao;

    @Autowired
    FetchChildInstructionDao fetchChildInstructionDao;

    @Autowired
    RefInstructionRulesPrdDao refInstructionRulesPrdDao;

    @Autowired
    RefLookupDao refLookupDao;
    ApplicationTypesDao applicationTypesDao;


    @Value("${salsa.fs.boxid}")
    int boxId;

    ChannelToBrandMapping channelToBrandMapping = new ChannelToBrandMapping();

    @Autowired
    SwitchDao switchDao;

    @Autowired
    MockControlFsAccountServicePortType mockFsAccountControl;

    @Autowired
    AccountClient accountClient;

    @Autowired
    TestDataHelper dataHelper;

    @Autowired
    MockControlCbsE141ServicePortType mockControlCbsE141;

    HeaderRetriever headerRetriever = new HeaderRetriever();

    Map<String, E220Client> cbsE220ClientMap;

    Map<String, E591Client> cbsE591ClientMap;

    Map<String, E141Client> cbsE141ClientMap;

    com.lloydsbanking.salsa.eligibility.TestDataHelper dataHelperBZ = new com.lloydsbanking.salsa.eligibility.TestDataHelper();

    @Autowired
    MockControlCbsE220ServicePortType mockControlCbsE220;

    @Autowired
    MockControlCbsE591ServicePortType mockControlCbsE591;

    @Autowired
    F075Client f075Client;

    @Autowired
    MockControlOcisF075ServicePortType mockOcisF075Control;

    @Autowired
    FsSystemClient systemClient;

    @Autowired
    MockControlFsSystemServicePortType mockControlFsSystemServicePortType;

    @Rollback(false)
    public void clearUp() {

        referencePamDataLookUpDao.deleteAll();
        refInstructionLookupPrdDao.deleteAll();
        refInstructionHierarchyPrdDao.deleteAll();
        fetchChildInstructionDao.deleteAll();
        refInstructionRulesPrdDao.deleteAll();
        dataHelper.cleanUpPam();

    }

    @Override
    @Transactional
    public void expectGetPamLookUpData(String groupCode, String lookUpvalueDesc, String description, String lookupText, String channel, Long sequence) {
        ReferenceDataLookUp referenceDataLkForChannelId = new ReferenceDataLookUp(groupCode, lookUpvalueDesc, description, new Long(lookUpvalueDesc), lookupText, channel, new Long(sequence));
        referencePamDataLookUpDao.save(referenceDataLkForChannelId);
    }

    @Override
    @Transactional
    public void expectPamData() {

        expectGetPamLookUpData("Cnt_Pnt_Prtflio", "0000115219", "Contact Point", "HAL", "HLX", new Long("1"));
        expectGetPamLookUpData("Cnt_Pnt_Prtflio", "0000777505", "Contact Point", "IBL", "LTB", new Long("2"));
        expectGetPamLookUpData("Cnt_Pnt_Prtflio", "0000805121", "Contact Point", "IBS", "BOS", new Long("3"));

    }

    @Override
    @Transactional
    public void expectGetPrdInstructionLookupData(String instMnemonic, String extSysId, String prodId, String brand) {
        InstructionLookupId instructionLookupId = new InstructionLookupId(instMnemonic, extSysId, brand, prodId);
        RefInstructionLookupPrdDto refInstructionLookupDto = new RefInstructionLookupPrdDto(instructionLookupId);
        refInstructionLookupPrdDao.save(refInstructionLookupDto);
    }

    @Override
    @Transactional
    public void expectGetPrdInstructionHierarchyData(String instMnemonic, String insDescription, String parInsMnemonic, String parInsDescription, Integer insPriority, String braCode) {
        RefInstructionHierarchyPrdDto refInstructionHierarchyPrdDto = new RefInstructionHierarchyPrdDto(instMnemonic, insDescription, parInsMnemonic, parInsDescription, insPriority, braCode);
        refInstructionHierarchyPrdDao.save(refInstructionHierarchyPrdDto);
    }

    @Override
    @Transactional
    public void expectGetPrdFetchChildInstructionData(String childMnemonic, String childDescription, String childPriority, String childInsId, String parentMnemonic, String parentDescription, String parentId, String brandCode) {
        FetchChildInstructionDto fetchChildInstructionDto = new FetchChildInstructionDto(childMnemonic, childDescription, childPriority, Long.valueOf(childInsId), parentMnemonic, parentDescription, parentId, brandCode);
        fetchChildInstructionDao.save(fetchChildInstructionDto);
    }

    @Override
    @Transactional
    public void expectRefInstructionRulesPrdData(String insMnemonic, String groupRule, String groupDesc, String rule, String groupRuleType, String ruleDesc, String ruleParamValue, String ruleType, String bracode, BigDecimal ruleParamSeq) {
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto(insMnemonic, groupRule, groupDesc, rule, groupRuleType, ruleDesc, ruleParamValue, ruleType, bracode, ruleParamSeq);
        refInstructionRulesPrdDao.save(refInstructionRulesPrdDto);
    }

    @Override
    @Transactional
    public void expectCompositeRefInstructionRulesPrdData(List<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos) {
        refInstructionRulesPrdDao.save(refInstructionRulesPrdDtos);
    }

    @Override
    public void expectCBSGenericGatewaySwitchCall(String channel, boolean switchStatus) {
        SwitchDto switchDto = new SwitchDto(channelToBrandMapping.getBrandForChannel(channel), "SW_CBSGenGtwy", new Date(), boxId, 1, "A", switchStatus ? "1" : "0");
        switchDao.save(switchDto);
    }

    @Override
    public void expectB766Call(RequestHeader requestHeader, String sortCode) {
        accountClient.retrieveCBSAppGroup(dataHelperBZ.createB766Request(requestHeader, sortCode));
        mockFsAccountControl.thenReturn(dataHelperBZ.createB766Response(dataHelperBZ.getCBSAppGrpForSortCode(sortCode), 0));
    }

    @Override
    public void expectE220Call(RequestHeader header, String sortCode, String participantId, String shadowLimitZero, String stricFlag) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        E220Req e220Req = dataHelper.createShadowLimitRequest(sortCode, participantId);
        e220Req.setMaxRepeatGroupQy(10);
        e220Req.setCAPSShdwDecnScrCd(" ");
        CBSAppGrp cbsAppGrp = dataHelperBZ.createCBSAppGroupFromSortCode(sortCode);
        E220Resp e220Resp = dataHelperBZ.createShadowLimitResponse(shadowLimitZero, stricFlag);
        e220Client(headerRetriever.getChannelId(header)).getShadowLimit(e220Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        mockControlCbsE220.thenReturn(e220Resp);
    }

    @Override
    public void expectE591Call(RequestHeader header, String sortCode, String participantId, String shadowLimitZero, String stricFlag, String cbsDecisionCd) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        E591Req e591Req = dataHelper.createE591Request(participantId);
        CBSAppGrp cbsAppGrp = dataHelperBZ.createCBSAppGroupFromSortCode(sortCode);
        E591Resp e591Resp = dataHelper.createE591Response(shadowLimitZero);
        e591Resp.getDecisionGp().getDecnSubGp().get(0).setDcnCdCarLoanFinancIn(cbsDecisionCd);
        e591Client(headerRetriever.getChannelId(header)).enqCbsCustDecnTrl(e591Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        mockControlCbsE591.thenReturn(e591Resp);
    }

    @Override
    public void expectE141Call(RequestHeader header, List<Integer> indicators, String sortCode, String accNo, String maxLimitAmt) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        CBSAppGrp cbsAppGrp = dataHelperBZ.createCBSAppGroupFromSortCode(sortCode);
        E141Req e141Req = dataHelper.createE141Request(sortCode, accNo);
        E141Resp e141Resp = dataHelper.createE141Response(indicators, maxLimitAmt);
        expectE141Call(headerRetriever.getChannelId(header), e141Resp, e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
    }

    private void expectE141Call(String channel, E141Resp e141Resp, E141Req e141Req, ContactPoint contactPoint, ServiceRequest serviceRequest, SecurityHeaderType securityHeaderType, CBSAppGrp cbsAppGrp) {
        e141Client(channel).getProductArrangementIndicator(e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        mockControlCbsE141.thenReturn(e141Resp);
    }

    @Override
    @Transactional
    public void expectRefLookUpGrdData(BigDecimal lookupId, String groupCd, String groupCdDesc, String lookupIn, String lookupType, BigDecimal lookupCd, String lookupTxt, String lookupValSd, String lookupValLd, String lookupValMd, String lookupValLcl, String active, String channel, BigDecimal displaySqn) {
        RefLookupDto refLookupDto = new RefLookupDto(lookupId, groupCd, groupCdDesc, lookupIn, lookupType, lookupCd, lookupTxt, lookupValSd, lookupValLd, lookupValMd, lookupValLcl, active, channel, displaySqn);
        refLookupDao.save(refLookupDto);
    }

    @Override
    public void expectF075Call(RequestHeader header, String kycStatus, String customerId, String partyEvidenceTypeCd, String partyEvidenceRefTx, String addrEvidenceTypeCd, String addrEvidenceRefTx) throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        F075Resp f075Response = dataHelper.createKYCResponse(kycStatus, partyEvidenceTypeCd, partyEvidenceRefTx, addrEvidenceTypeCd, addrEvidenceRefTx);
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        F075Req request = dataHelper.createF075Request(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()), customerId);

        f075Client.knowYourCustomerStatus(request, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF075Control.thenReturn(f075Response);
    }

    private E220Client e220Client(String channel) {

        return cbsE220ClientMap.get(dataHelperBZ.getBrandForChannel(channel));
    }

    public Map<String, E220Client> getCbsE220ClientMap() {
        return cbsE220ClientMap;
    }

    public void setCbsE220ClientMap(Map<String, E220Client> cbsE220ClientMap) {
        this.cbsE220ClientMap = cbsE220ClientMap;
    }

    private E591Client e591Client(String channel) {

        return cbsE591ClientMap.get(Channel.getBrandForChannel(Channel.fromString(channel)).asString());
    }

    public Map<String, E591Client> getCbsE591ClientMap() {
        return cbsE591ClientMap;
    }

    public void setCbsE591ClientMap(Map<String, E591Client> cbsE591ClientMap) {
        this.cbsE591ClientMap = cbsE591ClientMap;
    }

    private E141Client e141Client(String channel) {
        return cbsE141ClientMap.get(Channel.getBrandForChannel(Channel.fromString(channel)).asString());
    }

    public Map<String, E141Client> getCbsE141ClientMap() {
        return cbsE141ClientMap;
    }

    public void setCbsE141ClientMap(Map<String, E141Client> cbsE141ClientMap) {
        this.cbsE141ClientMap = cbsE141ClientMap;
    }

    @Override
    @Transactional
    public void expectPamApplications(String ocisId, int daysPrior) {
        dataHelper.createPamPartyApplicationsData(ocisId, daysPrior);
    }

    @Override
    public void expectMultipleCashISASwitchCall(String channel, boolean switchStatus) {
        SwitchDto switchDto = new SwitchDto(channelToBrandMapping.getBrandForChannel(channel), "SW_EnbMulCashISA", new Date(), boxId, 1, "A", switchStatus ? "1" : "0");
        switchDao.save(switchDto);
    }

    @Override
    public void expectExternalSystemProductsData(Long id, String esCode, Long proId, String externalSysProdId) {
        ExternalSystemProducts externalSystemProducts = new ExternalSystemProducts(id, esCode, proId, externalSysProdId);
        externalSystemProductsDao.save(externalSystemProducts);
    }

    @Override
    public void expectProductEligibilityRulesData(final Long id, final String petCode, final Long appliedProductId, final Long existingProductID, final Timestamp startDate, final Timestamp endDate) {
        ProductEligibilityRulesDto productEligibilityRulesDto = new ProductEligibilityRulesDto(id, petCode, appliedProductId, existingProductID, startDate, endDate);
        productEligibilityRulesDao.save(productEligibilityRulesDto);
    }

    @Override
    public void expectB695Call(RequestHeader header, String accType, String eventType) throws ErrorInfo {
        BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter = new BapiHeaderToStHeaderConverter();
        StB695AProductEventReadList b695Req = new StB695AProductEventReadList();
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(header).getBAPIHeader();
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        String contactPointId = headerRetriever.getContactPoint(header).getContactPointId();
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(bapiHeader, serviceRequest, contactPointId);
        b695Req.setStheader(stHeader);
        b695Req.setAcctype(accType);

        StB695BProductEventReadList b695Resp=new StB695BProductEventReadList();
        b695Resp.setSterror(new StError());
        b695Resp.getSterror().setErrorno(0);
        b695Resp.setMoreind("N");
        StEventType evtType=new StEventType();
        if (eventType.equals("37")) {
            evtType.setEvttype("B040");
        }else if (eventType.equals("55")){
            evtType.setEvttype("B062");
        }
        else if(eventType.equals("30")){
            evtType.setEvttype("B033");
        }
        b695Resp.getAstevttype().add(evtType);

        systemClient.retMandateDetails(b695Req);
        mockControlFsSystemServicePortType.thenReturn(b695Resp);
    }
}





