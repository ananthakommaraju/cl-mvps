package com.lloydsbanking.salsa.opasaving.service;

import com.lloydsbanking.salsa.downstream.asm.client.f204.F204Client;
import com.lloydsbanking.salsa.downstream.eidv.client.x711.X711Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f336.F336Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f447.F447Client;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.prd.jdbc.*;
import com.lloydsbanking.salsa.downstream.prd.model.*;
import com.lloydsbanking.salsa.downstream.soa.client.determinecustomerproductcondition.DCPCClient;
import com.lloydsbanking.salsa.downstream.switches.model.SwitchDto;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveFraudDecisionRequestFactory;
import com.lloydsbanking.salsa.offer.apply.downstream.RpcServiceClient;
import com.lloydsbanking.salsa.offer.downstream.EligibilityServiceClient;
import com.lloydsbanking.salsa.remotemock.*;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Req;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Req;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Resp;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Req;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Resp;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Req;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Resp;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.productcustomermatching.v2.productcustomermatching.serviceparameters.iproductcustomermatching.DetermineCustomerProductConditionsRequest;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.productcustomermatching.v2.productcustomermatching.serviceparameters.iproductcustomermatching.DetermineCustomerProductConditionsResponse;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyParty;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyPartyResp;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RemoteMockScenarioHelper implements ScenarioHelper {
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
    F062Client f062Client;

    @Autowired
    F204Client f204Client;

    @Autowired
    RpcServiceClient rpcServiceClient;

    @Autowired
    DCPCClient dcpcClient;

    @Autowired
    MockControlEligibilityServicePortType mockEligibilityControl;

    @Autowired
    EligibilityServiceClient eligibilityServiceClient;

    @Autowired
    MockControlAsmF204ServicePortType mockAsmF204Control;

    @Autowired
    MockControlOcisF336ServicePortType mockOcisF336Control;

    @Autowired
    MockControlOcisF447ServicePortType mockOcisF447Control;

    @Autowired
    MockControlOcisF061ServicePortType mockOcisF061Control;

    @Autowired
    MockControlOcisF062ServicePortType mockOcisF062Control;

    @Autowired
    MockControlRpcServicePortType mockRpcControl;

    @Autowired
    MockControlDcpcServicePortType mockDcpcControl;

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
    ProductPropositionDao productPropositionDao;

    @Autowired
    ProductGroupDao productGroupDao;

    @Autowired
    ProductPackageDao productPackageDao;

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
    ApplicationPartyRolesDao applicationPartyRolesDao;

    @Autowired
    ApprovalStatusDao approvalStatusDao;

    @Autowired
    PromotionPartiesDao promotionPartiesDao;

    @Autowired
    ProductFeatureDao productFeatureDao;

    @Autowired
    ProductEligibilityRulesDao productEligibilityRulesDao;

    @Autowired
    ExternalSystemProductsDao externalSystemProductsDao;

    @Autowired
    ExternalSystemTemplateDao externalSystemTemplateDao;

    @Autowired
    com.lloydsbanking.salsa.downstream.switches.jdbc.SwitchDao switchDao;

    @Value("${wps.cache.url:}")
    String wpsCacheUrl;

    @Autowired
    ApplicationRelationshipTypesDao applicationRelationshipTypesDao;

    Map<String, X711Client> x711ClientMap;

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
    public void expectF336CallForAccept(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F336Req f336Req = dataHelper.createF336Request(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()), partyIdentifier);

        F336Resp f336Resp = dataHelper.createF336Response2(productOneGroupId, productTwoGroupId);
        f336Client.getProductHoldings(f336Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF336Control.thenReturn(f336Resp);
    }

    @Override
    public void expectF447Call(RequestHeader header, List<PostalAddress> postalAddresses, Individual isPlayedBy) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        F447Req request = dataHelper.createF447Request(postalAddresses, isPlayedBy);
        F447Resp response = dataHelper.createF447Response(0);
        f447Client.enquirePartyId(request, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF447Control.thenReturn(response);
    }

    @Override
    public void expectF447CallForNewCustomer(RequestHeader header, List<PostalAddress> postalAddresses, Individual isPlayedBy) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        F447Req request = dataHelper.createF447Request(postalAddresses, isPlayedBy);
        //F447Resp response = dataHelper.createF447ResponseForNewCustomer(0);
        F447Resp response = dataHelper.createF447ResponseForNewCustomer(163137);
        // body/F447Response/F447Result/ResultCondition/ReasonCode=163137 or 163135 or 163137 or 163140 or 163141 or 163142 ----- External Service Error Codes --- Consumed to get a New Customer
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
    public void expectF204Call(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) throws ParseException, DatatypeConfigurationException {

        mockControl.matching("actual.target == 'asmF204' && actual.methodName == 'f204' && differingProperties(expected.arguments[1], actual.arguments[1], " + "['requestDetails/creditScoreRequestNo']).isEmpty()");


        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementSavingRequest("777505");
        upstreamRequest.getProductArrangement().setFinancialInstitution(new Organisation());
        upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F204Req f204Req = new RetrieveFraudDecisionRequestFactory().create(upstreamRequest.getProductArrangement()
                .getFinancialInstitution()
                .getHasOrganisationUnits()
                .get(0)
                .getRegionCode(), upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode(), upstreamRequest.getHeader()
                .getContactPointId(), upstreamRequest.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier(), upstreamRequest.getProductArrangement()
                .getArrangementId(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), upstreamRequest.getProductArrangement()
                .getPrimaryInvolvedParty(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress());
        f204Req.getRequestDetails().setSortCd(header.getContactPointId());
        F204Resp resp = dataHelper.createF204Response2(asmCreditScoreResultCd, referralCodeList);
        f204Client.performFraudCheck(f204Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF204Control.thenReturn(resp);
    }


    @Override
    public void expectF204CallForExistingCustomers(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        mockControl.matching("actual.target == 'asmF204' && actual.methodName == 'f204' && differingProperties(expected.arguments[1], actual.arguments[1], " + "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementSavingRequest("777505");
        upstreamRequest.getProductArrangement().setFinancialInstitution(new Organisation());
        upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F204Req f204Req1 = new RetrieveFraudDecisionRequestFactory().create(upstreamRequest.getProductArrangement()
                .getFinancialInstitution()
                .getHasOrganisationUnits()
                .get(0)
                .getRegionCode(), upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode(), upstreamRequest.getHeader()
                .getContactPointId(), upstreamRequest.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier(), upstreamRequest.getProductArrangement()
                .getArrangementId(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), upstreamRequest.getProductArrangement()
                .getPrimaryInvolvedParty(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress());
        f204Req1.getRequestDetails().setSortCd(header.getContactPointId());
        f204Req1.getPersonalDetails().get(0).getPartyIdentifiers().get(0).setExtSysId((short) 3);
        f204Req1.getPersonalDetails().get(0).setPartyId(12345);
        F204Resp resp = dataHelper.createF204Response2(asmCreditScoreResultCd, referralCodeList);
        f204Client.performFraudCheck(f204Req1, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF204Control.thenReturn(resp);
    }

    @Override
    @Transactional
    public void expectGetChannelIdFromContactPointId(String contactPointId) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("Cnt_Pnt_Prtflio", contactPointId, "Cnt_Pnt_Prtflio", new Long("1323"), "BHR", "LTB", new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
    }

    @Override
    @Transactional
    public void expectLookupDataForLegalEntity(String channel, String groupCode) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp(groupCode, "BOS", "Manufacturing Legal entity Code", new Long("44"), "BOS", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp(groupCode, "BSD", "Manufacturing Legal entity Code", new Long("45"), "BOS", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp2 = new ReferenceDataLookUp(groupCode, "CPF", "Manufacturing Legal entity Code", new Long("46"), "HLX", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp3 = new ReferenceDataLookUp(groupCode, "CAG", "Manufacturing Legal entity Code", new Long("47"), "LTB", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp4 = new ReferenceDataLookUp(groupCode, "LTB", "Manufacturing Legal entity Code", new Long("48"), "LTB", channel, new Long("1"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpList.add(referenceDataLkp2);
        referenceDataLookUpList.add(referenceDataLkp3);
        referenceDataLookUpList.add(referenceDataLkp4);
        referenceDataLookUpDao.save(referenceDataLookUpList);
    }

    @Override
    @Transactional
    public void expectInstructionHierarchyCall(String insMnemonic, String brand) {
        RefInstructionHierarchyPrdDto hierarchyPrdDto = new RefInstructionHierarchyPrdDto(insMnemonic, "Club Lloyds", "G_saving", "saving", null, brand);
        refInstructionHierarchyPrdDao.save(hierarchyPrdDto);
    }

    @Override
    @Transactional
    public void expectInstructionRulesViewCall(String insMnemonic, String brand) {
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto(insMnemonic, "GR020", "Customer not eligible for Current Account", "CR002", "Customer cannot be younger that 18 years", "GRP", "17", "CST", brand, BigDecimal.ONE);
        refInstructionRulesPrdDao.save(refInstructionRulesPrdDto);
    }

    @Override
    @Transactional
    public void expectInstructionRulesViewCallForDecline(String insMnemonic, String brand) {
        refInstructionRulesPrdDao.deleteAll();
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto(insMnemonic, "GR020", "Customer not eligible for Current Account", "CR002", "Customer cannot be younger that 18 years", "GRP", "200", "CST", brand, BigDecimal.ONE);
        refInstructionRulesPrdDao.save(refInstructionRulesPrdDto);
    }

    public void expectLookupListFromGroupCodeAndChannelAndLookUpText(String groupCd, String channel, List<String> lookUpText) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp(groupCd, "12232", lookUpText.get(0), new Long("2"), lookUpText.get(0), channel, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
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
    public void expectF062Call(String arrangementType, boolean marketingPrefIndicator, Customer primaryInvolvedParty, RequestHeader header, String addressStrength, String partyStrength, int reasoncode) throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F062Req req = dataHelper.createF062Req(arrangementType, primaryInvolvedParty, header, marketingPrefIndicator, addressStrength, partyStrength);
        req.getPartyUpdData().getPartyNonCoreUpdData().setOccupationalRoleCd((short) 0);
        F062Resp response = dataHelper.createF062Response(reasoncode);
        f062Client.updateCustomerRecord(req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF062Control.thenReturn(response);
    }

    @Override
    public void clearUp() {
        refInstructionHierarchyPrdDao.deleteAll();
        refInstructionRulesPrdDao.deleteAll();
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
        demographicValuesDao.deleteAll();
        demographicsDao.deleteAll();
        applicationParametersDao.deleteAll();
        parameterGroupsDao.deleteAll();
        individualsDao.deleteAll();
        telephoneAddressTypesDao.deleteAll();
        applicationPartyRolesDao.deleteAll();
        approvalStatusDao.deleteAll();
        promotionPartiesDao.deleteAll();
        switchDao.deleteAll();
        applicationRelationshipTypesDao.deleteAll();
    }

    @Override
    public void expectPrdDbCalls() {
        expectInstructionHierarchyCall("P_CLUB", "LTB");
        expectInstructionHierarchyCall("G_LOAN", "LTB");
        expectInstructionRulesViewCall("G_saving", "LTB");
        expectInstructionRulesViewCall("P_CLUB", "IBL");
        expectInstructionRulesViewCall("P_CLUB", "LTB");
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
        productPropositions.add(new ProductProposition(123l, 20044l, "Clarity3"));
        productPropositionDao.save(productPropositions);
    }


    @Override
    @Transactional
    public void expectPAMReferenceData() {
        expectLookupCallForCoHolding("LTB");
        dataHelper.createPamReferenceData();
    }

    @Override
    public void expectEligibility(String isEligible, RequestHeader requestHeader, boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockControl.matching("actual.target == 'eligibilityWz' && actual.methodName == 'determineEligibleCustomerInstructions' && differingProperties(expected.arguments[1], actual.arguments[1], " + "['requestHeader']).isEmpty()");
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = null;
        switch (type) {
            case 1:
                eligibilityRequest = dataHelper.createEligibilityRequestWhenBfpoNotPresentAndEligibilityIsTrue(request, requestHeader, isBFPOIndicatorPresent);
                break;
            case 2:
                eligibilityRequest = dataHelper.createEligibilityRequestWhenBfpoPresentAndEligibilityIsTrue(request, requestHeader, isBFPOIndicatorPresent);
                break;
        }
        lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse eligibilityResponse = null;

        eligibilityResponse = dataHelper.eligibilityResponse(isEligible);


        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);
    }

    @Override
    public void expectEligibilityFailure(RequestHeader requestHeader, boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockControl.matching("actual.target == 'eligibilityWz' && actual.methodName == 'determineEligibleCustomerInstructions' && differingProperties(expected.arguments[1], actual.arguments[1], " + "['requestHeader']).isEmpty()");
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = null;
        switch (type) {
            case 1:
                eligibilityRequest = dataHelper.createEligibilityRequestWhenBfpoNotPresentAndEligibilityIsTrue(request, requestHeader, isBFPOIndicatorPresent);
                break;
            case 2:
                eligibilityRequest = dataHelper.createEligibilityRequestWhenBfpoPresentAndEligibilityIsTrue(request, requestHeader, isBFPOIndicatorPresent);
                break;
        }
        lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse eligibilityResponse = null;

        eligibilityResponse = dataHelper.eligibilityResponseForFailue(17);


        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);
    }

    @Override
    public void expectEligibilityFailureWhenCustomerYoungerThan200(RequestHeader requestHeader, boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockControl.matching("actual.target == 'eligibilityWz' && actual.methodName == 'determineEligibleCustomerInstructions' && differingProperties(expected.arguments[1], actual.arguments[1], " + "['requestHeader']).isEmpty()");
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = null;
        switch (type) {
            case 1:
                eligibilityRequest = dataHelper.createEligibilityRequestWhenBfpoNotPresentAndEligibilityIsTrue(request, requestHeader, isBFPOIndicatorPresent);
                break;
            case 2:
                eligibilityRequest = dataHelper.createEligibilityRequestWhenBfpoPresentAndEligibilityIsTrue(request, requestHeader, isBFPOIndicatorPresent);
                break;
        }
        lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse eligibilityResponse = null;

        eligibilityResponse = dataHelper.eligibilityResponseForFailue(200);


        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);
    }

    @Override
    public void expectX711CallEidvRefer(Customer customer, RequestHeader header) throws OfferProductArrangementInternalServiceErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        IdentifyParty identifyParty = dataHelper.createX711Request(customer, contactPoint.getContactPointId());
        getX711Client("LTB").retrieveIdentityAndScores(identifyParty, contactPoint, serviceRequest, securityHeaderType);
        IdentifyPartyResp identifyPartyResp = dataHelper.createX711ResponseWithEidvRefer();
        mockControl.thenReturn(identifyPartyResp);
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
    public void expectLookupDataForX711Decline(String channel) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("EIDV_THRESHOLD_VALUE", "1000", "EIDV Threshold values", new Long("6"), "Upper Threshold", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp("EIDV_THRESHOLD_VALUE", "950", "EIDV Threshold values", new Long("7"), "Lower Threshold", channel, new Long("2"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpDao.save(referenceDataLookUpList);
    }

    @Override
    @Transactional
    public void expectLookupDataForX711Refer(String channel) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("EIDV_REFERRAL_CODES", "003", "EIDV Referral codes", new Long("1968"), "Refer", channel, new Long("3"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpDao.save(referenceDataLookUpList);
        Iterable it = referenceDataLookUpDao.findAll();
    }

    @Override
    public void expectX711Call(Customer customer, RequestHeader header) throws OfferProductArrangementInternalServiceErrorMsg {
        mockControl.matching("actual.target == 'eidvX711' && actual.methodName == 'identifyParty' && differingProperties(expected.arguments[2], actual.arguments[2], " + "['identifyParty/identifyPartyInput/personalCustomerDetail/personalCustomerAddresses/personalCustomerAddress/effectiveFrom','identifyParty/identifyPartyInput/personalCustomerDetail/employmentEffectiveFrom']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        IdentifyParty identifyParty = dataHelper.createX711Request(customer, contactPoint.getContactPointId());
        getX711Client("LTB").retrieveIdentityAndScores(identifyParty, contactPoint, serviceRequest, securityHeaderType);
        IdentifyPartyResp identifyPartyResp = dataHelper.createX711ResponseWithEidvApproved();
        mockControl.thenReturn(identifyPartyResp);
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
    public void expectLookupListFromChannelAndGroupCodeList(String channelId, List<String> groupCodeList) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp(groupCodeList.get(0), "103", "Cnt_Pnt_Prtflio", new Long("1321"), "BHR", channelId, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
        ReferenceDataLookUp referenceDataLkp2 = new ReferenceDataLookUp(groupCodeList.get(0), "104", "Cnt_Pnt_Prtflio", new Long("1322"), "BHR", channelId, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp2);
        ReferenceDataLookUp referenceDataLkp3 = new ReferenceDataLookUp(groupCodeList.get(0), "105", "Cnt_Pnt_Prtflio", new Long("1320"), "BHR", channelId, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp3);
    }

    @Override
    @Transactional
    public void expectDuplicationApplications(String appStatus, String appDescription, String productId, String productName) {
        dataHelper.createApplicationWithStatusAndProduct(true, appStatus, appDescription, productId, productName);
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
    public java.util.HashMap<String, Long> expectChildApplication() {
        return dataHelper.createApplication(false);
    }

    @Override
    @Transactional
    public int expectIndividualsCreated() {
        return dataHelper.getIndividualsSize();
    }

    @Override
    @Transactional
    public int expectApplicationsUpdated() {
        return dataHelper.getApplicationSize();
    }

    @Override
    @Transactional
    public int expectApplicationParametersCreated(long applicationId) {
        return dataHelper.getApplicationParametersSize(applicationId);
    }

    @Override
    @Transactional
    public int expectApplicationFeaturesCreated(long applicationId) {
        return dataHelper.getApplicationFeaturesSize(applicationId);
    }

    @Override
    @Transactional
    public int expectPartyApplicationsCreated() {
        return dataHelper.getPartyApplicationsSize();
    }

    @Override
    @Transactional
    public void expectSwitchValueFromSwitchName(String switchName, String channelId, int boxId, String value) {
        SwitchDto switchDto = new SwitchDto(channelId, switchName, new Date(), boxId, 1, "A", value);
        switchDao.save(switchDto);
    }

    @Override
    public void expectRpc(ProductArrangement productArrangement, RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = dataHelper.rpcRequest(productArrangement, requestHeader);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResponse = dataHelper.rpcResponse();
        rpcServiceClient.retrieveProductConditions(rpcRequest);
        mockRpcControl.thenReturn(rpcResponse);
    }

    @Override
    public void expectDcpc(lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResp, ProductArrangement productArrangement, RequestHeader header) throws InternalServiceErrorMsg {
        mockControl.matching("actual.target == 'dcpc' ");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        DetermineCustomerProductConditionsRequest dcpcRequest = dataHelper.createDcpcRequest(rpcResp, productArrangement, header);
        DetermineCustomerProductConditionsResponse dcpcResponse = dataHelper.createDcpcResponse();
        dcpcClient.determineCustomerProductCondition(dcpcRequest, contactPoint, serviceRequest, securityHeaderType);
        mockDcpcControl.thenReturn(dcpcResponse);
    }

    @Override
    @Transactional
    public int expectPartyCountryAssociationCreated() {
        return dataHelper.getPartyCountryAssociationSize();
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

        } catch (Exception e) {

        }
    }

    @Override
    public void expectF447CallWithError(RequestHeader header, List<PostalAddress> postalAddresses, Individual isPlayedBy) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        F447Req request = dataHelper.createF447Request(postalAddresses, isPlayedBy);
        F447Resp response = dataHelper.createF447Response(12345);
        f447Client.enquirePartyId(request, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF447Control.thenReturn(response);
    }

    @Override
    public void expectF336CallWithError(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F336Req f336Req = dataHelper.createF336Request(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()), partyIdentifier);

        F336Resp f336Resp = dataHelper.createF336ResponseWithExternalServiceError();
        f336Client.getProductHoldings(f336Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF336Control.thenReturn(f336Resp);
    }

    @Override
    public long expectRelatedApplicationId() {
        return dataHelper.createRelatedApplication().getId();
    }
}