package com.lloydsbanking.salsa.opacc.service;


import com.lloydsbanking.salsa.downstream.asm.client.f424.F424Client;
import com.lloydsbanking.salsa.downstream.dp.client.encrypt.EncryptClient;
import com.lloydsbanking.salsa.downstream.eidv.client.x711.X711Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f336.F336Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f447.F447Client;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.prd.jdbc.*;
import com.lloydsbanking.salsa.downstream.prd.model.*;
import com.lloydsbanking.salsa.downstream.switches.model.SwitchDto;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveCreditDecisionRequestFactory;
import com.lloydsbanking.salsa.offer.apply.downstream.RpcServiceClient;
import com.lloydsbanking.salsa.offer.downstream.EligibilityServiceClient;
import com.lloydsbanking.salsa.remotemock.*;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Req;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import com.lloydsbanking.salsa.soap.encrpyt.objects.EncryptDataRequest;
import com.lloydsbanking.salsa.soap.encrpyt.objects.EncryptDataResponse;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Req;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Resp;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Req;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Resp;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Req;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Resp;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyParty;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyPartyResp;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RemoteMockScenarioHelper implements ScenarioHelper {
    @Value("${wps.cache.url}")
    String wpsCacheUrl;

    @Autowired
    MockControlServicePortType mockControl;

    @Autowired
    TestDataHelper dataHelper;

    @Autowired
    F336Client f336Client;

    @Autowired
    F447Client f447Client;

    @Autowired
    F061Client f061Client;

    @Autowired
    F424Client f424Client;

    @Autowired
    EncryptClient encryptClient;

    @Autowired
    F062Client f062Client;

    @Autowired
    MockControlAsmF424ServicePortType mockAsmF424Control;

    @Autowired
    MockControlOcisF336ServicePortType mockOcisF336Control;

    @Autowired
    MockControlOcisF447ServicePortType mockOcisF447Control;

    @Autowired
    MockControlOcisF061ServicePortType mockOcisF061Control;

    @Autowired
    MockControlEncryptDataServicePortType mockEncryptDataServiceControl;

    @Autowired
    MockControlEligibilityServicePortType mockEligibilityControl;

    @Autowired
    EligibilityServiceClient eligibilityServiceClient;

    /*@Autowired
    MockControlEidvX711ServicePortType mockEidvX711Control;
*/
    Map<String, X711Client> x711ClientMap;


    HeaderRetriever headerRetriever = new HeaderRetriever();

    @Autowired
    ReferenceDataLookUpDao referenceDataLookUpDao;

    @Autowired
    RefInstructionHierarchyPrdDao refInstructionHierarchyPrdDao;

    @Autowired
    RefInstructionRulesPrdDao refInstructionRulesPrdDao;

    @Autowired
    ApplicationsDao applicationsDao;

    @Autowired
    IndividualsDao individualsDao;

    @Autowired
    ProductTypesDao productTypesDao;

    @Autowired
    BrandsDao brandsDao;

    @Autowired
    PromotionChannelsDao promotionChannelsDao;

    @Autowired
    ChannelsDao channelsDao;

    @Autowired
    UserTypesDao userTypesDao;

    @Autowired
    ApplicationStatusDao appStatusDao;

    @Autowired
    ApplicationTypesDao applicationTypesDao;

    @Autowired
    ApplicationFeatureTypesDao applicationFeatureTypesDao;

    @Autowired
    DemographicsDao demographicsDao;

    @Autowired
    DemographicsValuesDao demographicValuesDao;

    @Autowired
    TelephoneAddressTypesDao telephoneAddressTypesDao;

    @Autowired
    ParameterGroupsDao parameterGroupsDao;

    @Autowired
    ApplicationParametersDao applicationParametersDao;

    @Autowired
    ApplicationRelationshipTypesDao applicationRelationshipTypesDao;

    @Autowired
    ApplicationPartyRolesDao applicationPartyRolesDao;

    @Autowired
    ApprovalStatusDao approvalStatusDao;

    @Autowired
    PromotionPartiesDao promotionPartiesDao;

    @Autowired
    PromotionPartyExtSystemsDao promotionPartyExtSystemsDao;

    @Autowired
    KycStatusDao kycStatusDao;

    @Autowired
    com.lloydsbanking.salsa.downstream.switches.jdbc.SwitchDao switchDao;

    @Autowired
    AbandonDeclineReasonDao abandonDeclineReasonDao;

    @Autowired
    ProductFeatureDao productFeatureDao;

    @Autowired
    ProductEligibilityRulesDao productEligibilityRulesDao;

    @Autowired
    ExternalSystemProductsDao externalSystemProductsDao;

    @Autowired
    ExternalSystemTemplateDao externalSystemTemplateDao;

    @Autowired
    MockControlOcisF062ServicePortType mockOcisF062Control;

    @Autowired
    MockControlRpcServicePortType mockRpcControl;

    @Autowired
    RpcServiceClient rpcServiceClient;


    @Autowired
    ProductPropositionDao productPropositionDao;

    @Autowired
    ProductGroupDao productGroupDao;

    @Autowired
    ProductPackageDao productPackageDao;


    @Override
    public void expectPrdDbCalls() {
        expectInstructionHierarchyCall("P_CLUB", "LTB");
        expectInstructionRulesViewCall("G_PCA", "LTB");
        expectProductPropositionCall();
        expectProductFamilyCall();
        expectProductPackageVwCall();
        expectExternalSystemTemplateVwCall();
    }

    @Transactional
    private void expectExternalSystemTemplateVwCall() {
        externalSystemTemplateDao.save(new ExternalSystemTemplate(new ExternalSystemTemplateId("00001", 20043l), "CCA_Generic", 1l));
        externalSystemTemplateDao.save(new ExternalSystemTemplate(new ExternalSystemTemplateId("00001", 20042l), "CCA_Clarity", 1l));
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


    @Transactional
    private void expectProductFamilyCall() {
        List<ProductGroup> productGroups = new ArrayList<>();
        productGroups.add(new ProductGroup(18l, "300", 20042l, "901", "00107", "102"));
        productGroups.add(new ProductGroup(17l, "500", 20040l, "500", "00107", "102"));
        productGroups.add(new ProductGroup(19l, "700", 20043l, "700", "00107", "102"));

        productGroupDao.save(productGroups);
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

    @Override
    public void expectF336Call(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F336Req f336Req = dataHelper.createF336Request(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()), partyIdentifier);

        F336Resp f336Resp = dataHelper.createF336Response(productOneGroupId, productTwoGroupId);
        f336Client.getProductHoldings(f336Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF336Control.thenReturn(f336Resp);
    }

    @Override
    public void expectF447CallForNewCustomer(RequestHeader header, List<PostalAddress> postalAddressList, Individual isPlayedBy) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        F447Req request = dataHelper.createF447Request(postalAddressList, isPlayedBy);
        F447Resp response = dataHelper.createF447ResponseForNewCustomer(0);


        /*ASKKKK*/
        request.setPostCd("BR7  6LB");
        request.setBirthDt("19480101");
        mockControl.matching("actual.target == 'ocisF447'");

        f447Client.enquirePartyId(request, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF447Control.thenReturn(response);
    }

    @Override
    public void expectF447Call(RequestHeader header, List<PostalAddress> postalAddresses, Individual isPlayedBy) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        F447Req request = dataHelper.createF447Request(postalAddresses, isPlayedBy);
        F447Resp response = dataHelper.createF447Response(0);


        /*ASKKKK*/
        request.setPostCd("BR7  6LB");
        request.setBirthDt("19480101");
        mockControl.matching("actual.target == 'ocisF447'");

        f447Client.enquirePartyId(request, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF447Control.thenReturn(response);
    }

    @Override
    public void expectF061Call(RequestHeader header, String customerIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
        F061Req f061Req = dataHelper.createF061Req(bapiInformation, customerIdentifier);
        F061Resp resp = dataHelper.createF061Resp(0);
        f061Client.f061(f061Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF061Control.thenReturn(resp);
    }

    @Override
    public void expectF061CallWithoutEvidenceData(RequestHeader header, String customerIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
        F061Req f061Req = dataHelper.createF061Req(bapiInformation, customerIdentifier);
        F061Resp resp = dataHelper.createF061RespWithoutEvidenceData(0);
        f061Client.f061(f061Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF061Control.thenReturn(resp);
    }

    @Override
    public void expectF061CallWithoutEvidenceDataAndPersonalData(RequestHeader header, String customerIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
        F061Req f061Req = dataHelper.createF061Req(bapiInformation, customerIdentifier);
        F061Resp resp = dataHelper.createF061RespWithoutEvidenceData2(0);
        f061Client.f061(f061Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF061Control.thenReturn(resp);
    }

    @Override
    public void expectF424Call(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        mockControl.matching("actual.target == 'asmF424' && actual.methodName == 'f424' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");

        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCCRequest("777505");
        FinanceServiceArrangement financeServiceArrangement = null;
        F424Req f424Req = null;
        if (upstreamRequest.getProductArrangement() instanceof FinanceServiceArrangement) {
            financeServiceArrangement = (FinanceServiceArrangement) upstreamRequest.getProductArrangement();
            String directDebitIn = (financeServiceArrangement.getBalanceTransfer() != null && !financeServiceArrangement.getBalanceTransfer().isEmpty()) ? "Y" : "N";
            String isEligible = financeServiceArrangement.getAssociatedProduct().getEligibilityDetails() != null ? financeServiceArrangement.getAssociatedProduct().getEligibilityDetails().getIsEligible() : null;
            f424Req = new RetrieveCreditDecisionRequestFactory().create(upstreamRequest.getHeader().getContactPointId(),
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

        F424Resp resp = dataHelper.createF424Response(asmCreditScoreResultCd, referralCodeList);
        f424Client.f424(f424Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF424Control.thenReturn(resp);
    }

    @Override
    public void expectF424CallWithAddressDetailsWithResolutionNotPresent(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCCRequest("777505");
        FinanceServiceArrangement financeServiceArrangement = null;
        F424Req f424Req = null;
        if (upstreamRequest.getProductArrangement() instanceof FinanceServiceArrangement) {
            financeServiceArrangement = (FinanceServiceArrangement) upstreamRequest.getProductArrangement();
            String directDebitIn = (financeServiceArrangement.getBalanceTransfer() != null && !financeServiceArrangement.getBalanceTransfer().isEmpty()) ? "Y" : "N";
            String isEligible = financeServiceArrangement.getAssociatedProduct().getEligibilityDetails() != null ? financeServiceArrangement.getAssociatedProduct().getEligibilityDetails().getIsEligible() : null;
            f424Req = new RetrieveCreditDecisionRequestFactory().create(upstreamRequest.getHeader().getContactPointId(),
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
        f424Req.getRequestDetails().setSortCd("777505");
        f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().clear();
        F424Resp resp = dataHelper.createF424Response(asmCreditScoreResultCd, referralCodeList);
        f424Client.f424(f424Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF424Control.thenReturn(resp);
    }


    @Override
    @Transactional
    public void expectLookupDataForLegalEntity(String channel, String groupCode) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp(groupCode, "BOS", "Manufacturing Legal entity Code", new Long("44"), "BOS", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp(groupCode, "BSD", "Manufacturing Legal entity Code", new Long("45"), "BOS", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp2 = new ReferenceDataLookUp(groupCode, "CPF", "Manufacturing Legal entity Code", new Long("46"), "HLX", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp3 = new ReferenceDataLookUp(groupCode, "CAG", "Manufacturing Legal entity Code", new Long("47"), "LTB", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp4 = new ReferenceDataLookUp(groupCode, "LTB", "Manufacturing Legal entity Code", new Long("48"), "CAG", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp5 = new ReferenceDataLookUp(groupCode, "LTB", "Manufacturing Legal entity Code", new Long("49"), "LTB", channel, new Long("1"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpList.add(referenceDataLkp2);
        referenceDataLookUpList.add(referenceDataLkp3);
        referenceDataLookUpList.add(referenceDataLkp4);
        referenceDataLookUpList.add(referenceDataLkp5);
        referenceDataLookUpDao.save(referenceDataLookUpList);
        Iterable it = referenceDataLookUpDao.findAll();
    }

    @Override
    @Transactional
    public void expectInstructionHierarchyCall(String insMnemonic, String brand) {
        RefInstructionHierarchyPrdDto hierarchyPrdDto = new RefInstructionHierarchyPrdDto(insMnemonic, "Club Lloyds", "G_PCA", "PCA", null, brand);
        refInstructionHierarchyPrdDao.save(hierarchyPrdDto);
    }

    @Override
    @Transactional
    public void expectInstructionRulesViewCall(String insMnemonic, String brand) {
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto(insMnemonic, "GR020", "Customer not eligible for Current Account", "CR002", "Customer cannot be younger that 18 years", "GRP", "17", "CST", brand, BigDecimal.ONE);
        refInstructionRulesPrdDao.save(refInstructionRulesPrdDto);
    }

    public void expectLookupListFromGroupCodeAndChannelAndLookUpText(String groupCd, String channel, List<String> lookUpText) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp(groupCd, "12232", "Cnt_Pnt_Prtflio", new Long("1323"), lookUpText.get(0), channel, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
    }

    @Override
    public void expectEncryptDataServiceCall(RequestHeader header, String memorableInfo) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        EncryptDataRequest encryptDataRequest = dataHelper.createEncryptDataServiceRequest(memorableInfo, header);
        EncryptDataResponse encryptDataResponse = dataHelper.createEncryptDataRequestResponse(0);
        encryptClient.retrieveEncryptData(encryptDataRequest, contactPoint, serviceRequest, securityHeaderType);
        mockEncryptDataServiceControl.thenReturn(encryptDataResponse);
    }

    @Override
    public void expectLookupListtFromChannelAndGroupCodeList(String groupCd, String channel) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("Cnt_Pnt_Prtflio", "0000777505", "Cnt_Pnt_Prtflio", new Long("1325"), "BHR", channel, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
        ReferenceDataLookUp referenceDataLkp2 = new ReferenceDataLookUp(groupCd, "encryptKey", "0000", new Long("1324"), "lookUpText", channel, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp2);
    }

    public void clearUp() {
        referenceDataLookUpDao.deleteAll();
        refInstructionHierarchyPrdDao.deleteAll();
        applicationsDao.deleteAll();
        appStatusDao.deleteAll();
        applicationTypesDao.deleteAll();
        productTypesDao.deleteAll();
        brandsDao.deleteAll();
        userTypesDao.deleteAll();
        promotionChannelsDao.deleteAll();
        channelsDao.deleteAll();
        applicationFeatureTypesDao.deleteAll();
        promotionPartyExtSystemsDao.deleteAll();
        demographicValuesDao.deleteAll();
        demographicsDao.deleteAll();
        applicationParametersDao.deleteAll();
        parameterGroupsDao.deleteAll();
        individualsDao.deleteAll();
        telephoneAddressTypesDao.deleteAll();
        applicationPartyRolesDao.deleteAll();
        approvalStatusDao.deleteAll();
        promotionPartiesDao.deleteAll();
        kycStatusDao.deleteAll();
        abandonDeclineReasonDao.deleteAll();

        switchDao.deleteAll();
        productFeatureDao.deleteAll();
        externalSystemProductsDao.deleteAll();
        productEligibilityRulesDao.deleteAll();
        System.out.println("referenceDataLookUpDao" + referenceDataLookUpDao.count());
        clearWpsCache();
        applicationRelationshipTypesDao.deleteAll();
    }

    @Override
    @Transactional
    public void expectGetChannelIdFromContactPointId(String contactPointId) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("Cnt_Pnt_Prtflio", contactPointId, "Cnt_Pnt_Prtflio", new Long("132356"), "BHR", "LTB", new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
    }

    @Override
    @Transactional
    public void expectPAMReferenceData() {
        dataHelper.createPamReferenceData();
    }

    @Override
    @Transactional
    public void expectSavePromotionPartyExtSystemsInPamDb(String extId) {
        dataHelper.savePromotionPartyExtSystems(extId);
    }

    @Transactional
    public void expectDuplicationApplications() {
        dataHelper.createDuplicateApplicationCC();
    }

    @Transactional
    public void expectDuplicationApplicationsWithASMDecline() {
        dataHelper.createDuplicateApplicationWithASMDecline();
    }

    @Override
    public void expectEligibility(String isEligible, RequestHeader requestHeader) throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockControl.matching("actual.target == 'eligibilityWz' && actual.methodName == 'determineEligibleCustomerInstructions' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestHeader']).isEmpty()");
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = dataHelper.createEligibilityRequest(request, requestHeader);
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = dataHelper.eligibilityResponse(isEligible);
        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);
    }

    @Override
    public void expectEligibilityForNewCustomer(String isEligible, RequestHeader requestHeader) throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockControl.matching("actual.target == 'eligibilityWz' && actual.methodName == 'determineEligibleCustomerInstructions' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestHeader']).isEmpty()");
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = dataHelper.createEligibilityRequestForNewCustomer(request, requestHeader);
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = dataHelper.eligibilityResponse(isEligible);
        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);
    }

    @Override
    @Transactional
    public void expectSwitchDtoFromSwitchName(String switchName, String channelId, int boxId, String value) {
        SwitchDto switchDto = new SwitchDto(channelId, switchName, new Date(), boxId, 1, "A", value);
        switchDao.save(switchDto);
    }

    @Override
    public void expectF336CallWithProductHoldings(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F336Req f336Req = dataHelper.createF336Request(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()), partyIdentifier);
        F336Resp f336Resp = dataHelper.createF336ResponseWithProductHoldings(productOneGroupId, productTwoGroupId);
        f336Client.getProductHoldings(f336Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF336Control.thenReturn(f336Resp);
    }

    @Override
    @Transactional
    public void expectLookupDataForDuplicateApplication(String channel, String groupCode) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp(groupCode, "LTB", "Co holding Information", new Long("107"), "LTB", channel, new Long("1"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpDao.save(referenceDataLookUpList);
    }

    @Override
    @Transactional
    public void expectLookupDataForX711(String channel) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("EIDV_THRESHOLD_VALUE", "1000", "EIDV Threshold values", new Long("6"), "Upper Threshold", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp("EIDV_THRESHOLD_VALUE", "0", "EIDV Threshold values", new Long("7"), "Lower Threshold", channel, new Long("2"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpDao.save(referenceDataLookUpList);
        Iterable it = referenceDataLookUpDao.findAll();
    }

    @Override
    @Transactional
    public void expectLookupDataForEvaluateStrength(String channel) {

        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("PTY_EVIDENCE_CODE", "148", "Party Evidence Type Code", new Long("22"), "Party Evidence", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp("ADD_EVIDENCE_CODE", "149", "Address Evidence Type Code", new Long("23"), "Address Evidence", channel, new Long("1"));

        ReferenceDataLookUp referenceDataLkp3 = new ReferenceDataLookUp("PTY_PURPOSE_CODE", "010", "Party Purpose Type Code", new Long("24"), "Party Evidence", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp4 = new ReferenceDataLookUp("ADD_PURPOSE_CODE", "009", "Address Purpose Type Code", new Long("25"), "Address Evidence", channel, new Long("1"));

        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpList.add(referenceDataLkp3);
        referenceDataLookUpList.add(referenceDataLkp4);
        referenceDataLookUpDao.save(referenceDataLookUpList);
        Iterable it = referenceDataLookUpDao.findAll();
    }

    @Override
    @Transactional
    public void expectLookupDataForX711Decline(String channel) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("EIDV_THRESHOLD_VALUE", "1000", "EIDV Threshold values", new Long("96"), "Upper Threshold", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp("EIDV_THRESHOLD_VALUE", "950", "EIDV Threshold values", new Long("97"), "Lower Threshold", channel, new Long("2"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpDao.save(referenceDataLookUpList);
    }

    @Override
    @Transactional
    public void expectLookupDataForX711Refer(String channel) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("EIDV_REFERRAL_CODES", "003", "EIDV Referral codes", new Long("1968"), "Refer", channel, new Long("3"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp("EIDV_THRESHOLD_VALUE", "10", "EIDV Threshold values", new Long("6"), "Upper Threshold", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp2 = new ReferenceDataLookUp("EIDV_THRESHOLD_VALUE", "0", "EIDV Threshold values", new Long("7"), "Lower Threshold", channel, new Long("2"));

        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpList.add(referenceDataLkp2);
        referenceDataLookUpDao.save(referenceDataLookUpList);
        Iterable it = referenceDataLookUpDao.findAll();
    }

    @Override
    @Transactional
    public int expectApplicationsCreated() {
        return dataHelper.getApplicationsSize();
    }

    @Override
    @Transactional
    public int expectIndividualsCreated() {
        return dataHelper.getIndividualsSize();
    }

    @Override
    public int expectStreetAddressesCreated() {
        return dataHelper.getStreetAddressesSize();
    }

    @Override
    @Transactional
    public int expectPartyApplicationsCreated() {
        return dataHelper.getPartyApplicationsSize();
    }


    @Override
    @Transactional
    public int expectPromoPartyApplicationsCreated() {
        return dataHelper.getPromoPartyApplicationsSize();
    }

    @Override
    @Transactional
    public Applications expectRetrieveApplicationFromPAM(Long arrangementId) {
        Applications applications = applicationsDao.findOne(arrangementId);
        return applications;
    }

    @Override
    @Transactional
    public void expectExternalSystemProductsPrdData(String esCode, String externalSysProId) {
        ExternalSystemProducts externalSystemProducts = new ExternalSystemProducts(new Long(12345), esCode, new Long("303"), externalSysProId);
        ExternalSystemProducts externalSystemProducts2 = new ExternalSystemProducts(new Long(12346), "00007", new Long("304"), "1");
        ExternalSystemProducts externalSystemProducts3 = new ExternalSystemProducts(new Long(12347), "10107", new Long("305"), "3");
        externalSystemProductsDao.save(externalSystemProducts);
        externalSystemProductsDao.save(externalSystemProducts2);
        externalSystemProductsDao.save(externalSystemProducts3);
    }

    @Override
    public void expectX711Call(Customer customer, RequestHeader header) throws OfferProductArrangementInternalServiceErrorMsg {
        mockControl.matching("actual.target == 'eidvX711' && actual.methodName == 'identifyParty' && differingProperties(expected.arguments[2], actual.arguments[2], " +
                "['identifyParty/identifyPartyInput/personalCustomerDetail/personalCustomerAddresses/personalCustomerAddress/effectiveFrom','identifyParty/identifyPartyInput/personalCustomerDetail/employmentEffectiveFrom']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        IdentifyParty identifyParty = dataHelper.createX711Request(customer, contactPoint.getContactPointId());
        getX711Client("LTB").retrieveIdentityAndScores(identifyParty, contactPoint, serviceRequest, securityHeaderType);
        IdentifyPartyResp identifyPartyResp = dataHelper.createX711ResponseWithEidvApproved();
        mockControl.thenReturn(identifyPartyResp);

    }

    @Override
    public void expectX711CallEidvRefer(Customer customer, RequestHeader header) throws OfferProductArrangementInternalServiceErrorMsg {
        mockControl.matching("actual.target == 'eidvX711' && actual.methodName == 'identifyParty' && differingProperties(expected.arguments[2], actual.arguments[2], " +
                "['identifyParty/identifyPartyInput/personalCustomerDetail/personalCustomerAddresses/personalCustomerAddress/effectiveFrom','identifyParty/identifyPartyInput/personalCustomerDetail/employmentEffectiveFrom']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        IdentifyParty identifyParty = dataHelper.createX711Request(customer, contactPoint.getContactPointId());
        getX711Client("LTB").retrieveIdentityAndScores(identifyParty, contactPoint, serviceRequest, securityHeaderType);
        IdentifyPartyResp identifyPartyResp = dataHelper.createX711ResponseWithEidvRefer();
        mockControl.thenReturn(identifyPartyResp);

    }

    private X711Client getX711Client(String channel) {
        return x711ClientMap.get(channel);
    }

    public Map<String, X711Client> getX711ClientMap() {
        return x711ClientMap;
    }

    public void setX711ClientMap(Map<String, X711Client> x711ClientMap) {
        this.x711ClientMap = x711ClientMap;
    }


    @Override
    public void expectF062Call(String arrangementType, boolean marketingPrefIndicator, Customer primaryInvolvedParty, RequestHeader header, String addressStrength, String partyStrength, int reasoncode, String eidvStatus, boolean assessmentEvidNull) throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {

        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F062Req req = dataHelper.createF062Req(arrangementType, primaryInvolvedParty, header, marketingPrefIndicator, addressStrength, partyStrength, eidvStatus, assessmentEvidNull);
        F062Resp response = new TestDataHelper().createF062Response(reasoncode);
        f062Client.updateCustomerRecord(req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF062Control.thenReturn(response);
    }

    @Override
    public void expectF062CallSuccessful(String arrangementType, boolean marketingPrefIndicator, Customer primaryInvolvedParty, RequestHeader header, String addressStrength, String partyStrength, int reasoncode, String eidvStatus, boolean assessmentEvidNull) throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {

        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F062Req req = dataHelper.createF062Req(arrangementType, primaryInvolvedParty, header, marketingPrefIndicator, addressStrength, partyStrength, eidvStatus, assessmentEvidNull);
        F062Resp response = new TestDataHelper().createF062Response(reasoncode);

        f062Client.updateCustomerRecord(req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF062Control.thenReturn(response);
    }

    @Override
    @Transactional
    public void expectProductFeatureFromProductId(Long productId) {
        ProductFeature productFeature = new ProductFeature(productId, "100069", "Max eligible cards", "MaxElgblCard", "Max eligible cards", "Platinum Purchase Card", "100", "4", new Date("29-SEP-15"), null);
        productFeatureDao.save(productFeature);
        ProductFeature productFeature1 = new ProductFeature(20051L, "100068", "Max eligible cards", "MaxElgblCard", "Max eligible cards", "Platinum Purchase Card", "100", "5", new Date("29-SEP-15"), null);
        productFeatureDao.save(productFeature1);
        ProductFeature productFeature2 = new ProductFeature(20052L, "100067", "Max eligible cards", "MaxElgblCard", "Max eligible cards", "Platinum Purchase Card", "100", "6", new Date("29-SEP-15"), null);
        productFeatureDao.save(productFeature2);
    }

    @Override
    @Transactional
    public void expectProductFeatureFromProductIdForIneligibleOfferedProducts(Long productId) {
        ProductFeature productFeature = new ProductFeature(productId, "100069", "Max eligible cards", "MaxElgblCard", "Max eligible cards", "Platinum Purchase Card", "100", "4", new Date("29-SEP-15"), null);
        productFeatureDao.save(productFeature);

    }

    @Override
    @Transactional
    public void expectProductEligibilityRulesPrdData(String petCode, String appliedProId, String existingProId) {
        ProductEligibilityRulesDto productEligibilityRulesDto = new ProductEligibilityRulesDto(new Long("1004"), petCode, new Long(appliedProId), new Long(existingProId), new Timestamp(new Date(2014, 01, 01, 00, 00, 00).getTime()), null);
        productEligibilityRulesDao.save(productEligibilityRulesDto);
        ProductEligibilityRulesDto productEligibilityRulesDto1 = new ProductEligibilityRulesDto(new Long("1005"), petCode, 20051L, new Long(existingProId), new Timestamp(new Date(2014, 01, 01, 00, 00, 00).getTime()), null);
        productEligibilityRulesDao.save(productEligibilityRulesDto1);
        ProductEligibilityRulesDto productEligibilityRulesDto2 = new ProductEligibilityRulesDto(new Long("1006"), petCode, 20052L, new Long(existingProId), new Timestamp(new Date(2014, 01, 01, 00, 00, 00).getTime()), null);
        productEligibilityRulesDao.save(productEligibilityRulesDto2);
        ProductEligibilityRulesDto productEligibilityRulesDto3 = new ProductEligibilityRulesDto(new Long("1007"), petCode, 20052L, new Long("304"), new Timestamp(new Date(2014, 01, 01, 00, 00, 00).getTime()), null);
        productEligibilityRulesDao.save(productEligibilityRulesDto3);
        ProductEligibilityRulesDto productEligibilityRulesDto4 = new ProductEligibilityRulesDto(new Long("1008"), petCode, 20052L, new Long("305"), new Timestamp(new Date(2014, 01, 01, 00, 00, 00).getTime()), null);
        productEligibilityRulesDao.save(productEligibilityRulesDto4);
        Iterable it = productEligibilityRulesDao.findAll();
    }

    @Override
    public void expectRpc(RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = dataHelper.rpcRequest(requestHeader);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResponse = dataHelper.rpcResponse();
        mockRpcControl.matching("actual.target == 'rpc'");
        rpcServiceClient.retrieveProductConditions(rpcRequest);
        mockRpcControl.thenReturn(rpcResponse);
    }

    @Override
    public void expectRpcProducts(RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = dataHelper.rpcRequest(requestHeader);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResponse = dataHelper.rpcResponse2();
        mockRpcControl.matching("actual.target == 'rpc'");
        rpcServiceClient.retrieveProductConditions(rpcRequest);
        mockRpcControl.thenReturn(rpcResponse);
    }

    @Override
    public void expectRpcProductsForUnscored(RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = dataHelper.rpcRequest(requestHeader);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResponse = dataHelper.rpcResponseForUnscored();
        mockRpcControl.matching("actual.target == 'rpc'");
        rpcServiceClient.retrieveProductConditions(rpcRequest);
        mockRpcControl.thenReturn(rpcResponse);
    }

    @Override
    public void expectF424CallWithExternalServiceError(RequestHeader requestHeader, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        mockControl.matching("actual.target == 'asmF424' && actual.methodName == 'f424' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");

        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCCRequest("777505");
        FinanceServiceArrangement financeServiceArrangement = null;
        F424Req f424Req = null;
        if (upstreamRequest.getProductArrangement() instanceof FinanceServiceArrangement) {
            financeServiceArrangement = (FinanceServiceArrangement) upstreamRequest.getProductArrangement();
            String directDebitIn = (financeServiceArrangement.getBalanceTransfer() != null && !financeServiceArrangement.getBalanceTransfer().isEmpty()) ? "Y" : "N";
            String isEligible = financeServiceArrangement.getAssociatedProduct().getEligibilityDetails() != null ? financeServiceArrangement.getAssociatedProduct().getEligibilityDetails().getIsEligible() : null;
            f424Req = new RetrieveCreditDecisionRequestFactory().create(upstreamRequest.getHeader().getContactPointId(),
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
        f424Req.getRequestDetails().setSortCd(requestHeader.getContactPointId());

        F424Resp resp = dataHelper.createF424ResponseWithExternalServiceErrorCode();
        f424Client.f424(f424Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF424Control.thenReturn(resp);

    }

    @Override
    public void expectF424CallWithExternalBusinessError(RequestHeader requestHeader, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        mockControl.matching("actual.target == 'asmF424' && actual.methodName == 'f424' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");

        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCCRequest("777505");
        FinanceServiceArrangement financeServiceArrangement = null;
        F424Req f424Req = null;
        if (upstreamRequest.getProductArrangement() instanceof FinanceServiceArrangement) {
            financeServiceArrangement = (FinanceServiceArrangement) upstreamRequest.getProductArrangement();
            String directDebitIn = (financeServiceArrangement.getBalanceTransfer() != null && !financeServiceArrangement.getBalanceTransfer().isEmpty()) ? "Y" : "N";
            String isEligible = financeServiceArrangement.getAssociatedProduct().getEligibilityDetails() != null ? financeServiceArrangement.getAssociatedProduct().getEligibilityDetails().getIsEligible() : null;
            f424Req = new RetrieveCreditDecisionRequestFactory().create(upstreamRequest.getHeader().getContactPointId(),
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
        f424Req.getRequestDetails().setSortCd(requestHeader.getContactPointId());

        F424Resp resp = dataHelper.createF424ResponseWithExternalBusinessErrorCode();
        f424Client.f424(f424Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF424Control.thenReturn(resp);
    }

    @Override
    public void expectRpcWhenApplicationStatusIsUnscored(RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = dataHelper.rpcRequestWhenApplicationStatusIsUnscored(requestHeader);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResponse = dataHelper.rpcResponse();
        rpcServiceClient.retrieveProductConditions(rpcRequest);
        mockRpcControl.thenReturn(rpcResponse);
    }

    @Override
    public void expectLookupListFromChannelAndEvidenceList(String channel) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("ADD_EVIDENCE_CODE", "149", "Address Evidence Type Code", new Long("23"), "Address Evidence", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp("PTY_EVIDENCE_CODE", "148", "Party Evidence Type Code", new Long("22"), "Party Evidence", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp2 = new ReferenceDataLookUp("PTY_PURPOSE_CODE", "010", "Party Evidence Purpose Code", new Long("24"), "Party Evidence", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp3 = new ReferenceDataLookUp("ADD_PURPOSE_CODE", "009", "Address Evidence Purpose Code", new Long("25"), "Address Evidence", channel, new Long("1"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpList.add(referenceDataLkp2);
        referenceDataLookUpList.add(referenceDataLkp3);
        referenceDataLookUpDao.save(referenceDataLookUpList);
    }

    @Override
    @Transactional
    public void expectLookupCallForCoHolding(String channel) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("BRAND_COHOLDING", "VER", "Co holding Information", 2450l, "VER", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp("BRAND_COHOLDING", "LTB", "Co holding Information", 107l, "LTB", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp2 = new ReferenceDataLookUp("BRAND_COHOLDING", "HLX", "Co holding Information", 108l, "HLX", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp3 = new ReferenceDataLookUp("BRAND_COHOLDING", "BOS", "Co holding Information", 109l, "BOS", channel, new Long("1"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpList.add(referenceDataLkp2);
        referenceDataLookUpList.add(referenceDataLkp3);
        referenceDataLookUpDao.save(referenceDataLookUpList);

    }

    @Override
    public void clearWpsCache() {
        try {
            URL wpsCache = new URL(wpsCacheUrl);
            URLConnection uc = wpsCache.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            in.close();
            System.out.println("Cache cleared---");
        } catch (Exception e) {

        }
    }

    @Override
    public long expectRelatedapplicationId() {
        com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus applicationStatus = new com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus("1002", "Approved");
        return dataHelper.createApplication(applicationStatus).getId();
    }


}

