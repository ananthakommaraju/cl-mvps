package com.lloydsbanking.salsa.opapca.service;


import com.lloydsbanking.salsa.downstream.asm.client.f204.F204Client;
import com.lloydsbanking.salsa.downstream.asm.client.f205.F205Client;
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
import com.lloydsbanking.salsa.downstream.sira.client.SiraClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveCreditScoreRequestFactory;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveFraudDecisionRequestFactory;
import com.lloydsbanking.salsa.offer.apply.downstream.RpcServiceClient;
import com.lloydsbanking.salsa.offer.downstream.EligibilityServiceClient;
import com.lloydsbanking.salsa.opapca.service.convert.SiraRequestFactory;
import com.lloydsbanking.salsa.remotemock.*;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Req;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Req;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
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
import com.synectics_solutions.dataservices.schemas.core.v1_1.dsfault.DSFaultType;
import com.synectics_solutions.sira.schemas.realtime.core.v1_0.realtimeresulttype4.RealtimeResultType4Type;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.AuthenticationHeader;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.Source;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.SubmitWorkItemResponse;
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

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.*;

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
    SiraClient siraClient;

    @Autowired
    TmxDetailsDao tmxDao;

    @Autowired
    SiraRequestFactory siraRequestFactory;

    @Autowired
    F061Client f061Client;

    @Autowired
    F062Client f062Client;

    @Autowired
    F204Client f204Client;

    @Autowired
    F205Client f205Client;

    @Autowired
    MockControlAsmF204ServicePortType mockAsmF204Control;

    @Autowired
    MockControlAsmF205ServicePortType mockAsmF205Control;

    @Autowired
    MockControlOcisF336ServicePortType mockOcisF336Control;

    @Autowired
    MockControlOcisF447ServicePortType mockOcisF447Control;

    @Autowired
    MockControlOcisF061ServicePortType mockOcisF061Control;

    @Autowired
    MockControlSiraServicePortType mockSiraControl;
    @Autowired
    MockControlOcisF062ServicePortType mockOcisF062Control;

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

    Map<String, X711Client> x711ClientMap;


    @Autowired
    ApplicationPartyRolesDao applicationPartyRolesDao;

    @Autowired
    ApprovalStatusDao approvalStatusDao;

    @Autowired
    PromotionPartiesDao promotionPartiesDao;

    @Autowired
    ExternalSystemProductDao externalSystemProductDao;

    @Autowired
    ExternalSystemTemplateDao externalSystemTemplateDao;

    @Autowired
    ProductPropositionDao productPropositionDao;

    @Autowired
    ProductGroupDao productGroupDao;

    @Autowired
    ProductPackageDao productPackageDao;

    @Autowired
    ProductFeatureDao productFeatureDao;

    @Autowired
    MockControlRpcServicePortType mockRpcControl;

    @Autowired
    RpcServiceClient rpcServiceClient;

    @Autowired
    EligibilityServiceClient eligibilityServiceClient;

    @Autowired
    MockControlEligibilityServicePortType mockEligibilityControl;

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
    public void expectF336CallWithValidProductHoldings(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F336Req f336Req = dataHelper.createF336Request(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()), partyIdentifier);

        F336Resp f336Resp = dataHelper.createF336ResponseWithValidProductHoldings(productOneGroupId, productTwoGroupId);
        f336Client.getProductHoldings(f336Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF336Control.thenReturn(f336Resp);
    }

    @Override
    public void expectLookUpValuesWithISOCode() {
        referenceDataLookUpDao.save(new ReferenceDataLookUp("ISO_COUNTRY_CODE", "1", "Purpose of Account", 1095L, "SPORI", "IBL", 1L));

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
    public void expectF447CallWithError(RequestHeader header, List<PostalAddress> postalAddresses, Individual isPlayedBy) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        F447Req request = dataHelper.createF447Request(postalAddresses, isPlayedBy);
        F447Resp response = dataHelper.createF447Response(163132);
        f447Client.enquirePartyId(request, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF447Control.thenReturn(response);
    }


    @Override
    public void expectF447CallForNewCustomer(RequestHeader header, List<PostalAddress> postalAddresses, Individual isPlayedBy) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        F447Req request = dataHelper.createF447Request(postalAddresses, isPlayedBy);
        F447Resp response = dataHelper.createF447ResponseForNewCustomer(163137);
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

        mockControl.matching("actual.target == 'asmF204' && actual.methodName == 'f204' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");


        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCARequest("777505");
        upstreamRequest.getProductArrangement().setFinancialInstitution(new Organisation());
        upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F204Req f204Req = new RetrieveFraudDecisionRequestFactory().create(upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(), upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode()
                , upstreamRequest.getHeader().getContactPointId(), upstreamRequest.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier(), upstreamRequest.getProductArrangement().getArrangementId(),
                upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress());
        f204Req.getRequestDetails().setSortCd(header.getContactPointId());
        F204Resp resp = dataHelper.createF204Response2(asmCreditScoreResultCd, referralCodeList);
        f204Client.performFraudCheck(f204Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF204Control.thenReturn(resp);
    }

    @Override
    public void expectF205Call(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, int caseNo) throws ParseException {
        mockControl.matching("actual.target == 'asmF205' && actual.methodName == 'f205' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCARequest("777505");
        upstreamRequest.getProductArrangement().setFinancialInstitution(new Organisation());
        upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F205Req f205Req = new RetrieveCreditScoreRequestFactory().create(upstreamRequest.getHeader().getContactPointId(), upstreamRequest.getProductArrangement().getArrangementId(),
                upstreamRequest.getProductArrangement().getAssociatedProduct().getBrandName(), upstreamRequest.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty(), upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode(),
                upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(),
                upstreamRequest.getProductArrangement().getAccountPurpose(),
                upstreamRequest.getProductArrangement().getExistingProducts(), upstreamRequest.getProductArrangement().getConditions());

        f205Req.getRequestDetails().setSortCd(header.getContactPointId());
        F205Resp resp = dataHelper.createF205Response2(asmCreditScoreResultCd, referralCodeList, caseNo);
        f205Client.fetchCreditDecisionForCurrentAccount(f205Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF205Control.thenReturn(resp);


    }

    @Override
    public void expectF205CallForExistingCustomer(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, int caseNo) throws ParseException {
        mockControl.matching("actual.target == 'asmF205' && actual.methodName == 'f205' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCARequest("777505");
        upstreamRequest.getProductArrangement().setFinancialInstitution(new Organisation());
        upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F205Req f205Req = new RetrieveCreditScoreRequestFactory().create(upstreamRequest.getHeader().getContactPointId(), upstreamRequest.getProductArrangement().getArrangementId(),
                upstreamRequest.getProductArrangement().getAssociatedProduct().getBrandName(), upstreamRequest.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty(), upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode(),
                upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(),
                upstreamRequest.getProductArrangement().getAccountPurpose(),
                upstreamRequest.getProductArrangement().getExistingProducts(), upstreamRequest.getProductArrangement().getConditions());

        f205Req.getRequestDetails().setSortCd(header.getContactPointId());
        f205Req.getPersonalDetails().get(0).getPartyIdentifiers().get(0).setExtSysId((short) 3);
        f205Req.getPersonalDetails().get(0).setPartyId(12345);
        f205Req.getPersonalDetails().get(0).setPartyStaffIn("Y");
        F205Resp resp = dataHelper.createF205Response2(asmCreditScoreResultCd, referralCodeList, caseNo);
        f205Client.fetchCreditDecisionForCurrentAccount(f205Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF205Control.thenReturn(resp);


    }

    @Override
    public void expectF204CallForExistingCustomers(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        mockControl.matching("actual.target == 'asmF204' && actual.methodName == 'f204' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCARequest("777505");
        upstreamRequest.getProductArrangement().setFinancialInstitution(new Organisation());
        upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F204Req f204Req1 = new RetrieveFraudDecisionRequestFactory().create(upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(), upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode()
                , upstreamRequest.getHeader().getContactPointId(), upstreamRequest.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier(), upstreamRequest.getProductArrangement().getArrangementId(),
                upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress());
        f204Req1.getRequestDetails().setSortCd(header.getContactPointId());
        f204Req1.getPersonalDetails().get(0).getPartyIdentifiers().get(0).setExtSysId((short) 3);
        f204Req1.getPersonalDetails().get(0).setPartyId(12345);
        F204Resp resp = dataHelper.createF204Response2(asmCreditScoreResultCd, referralCodeList);
        f204Client.performFraudCheck(f204Req1, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF204Control.thenReturn(resp);
    }

    @Override
    public void expectLookupListFromChannelAndGroupCodeList(String channelId, List<String> groupCodeList) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp(groupCodeList.get(0), "103", "Cnt_Pnt_Prtflio", new Long("1321"), "BHR", channelId, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
        ReferenceDataLookUp referenceDataLkp2 = new ReferenceDataLookUp(groupCodeList.get(0), "104", "Cnt_Pnt_Prtflio", new Long("1322"), "BHR", channelId, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp2);
        ReferenceDataLookUp referenceDataLkp3 = new ReferenceDataLookUp(groupCodeList.get(0), "105", "Cnt_Pnt_Prtflio", new Long("1320"), "BHR", channelId, new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp3);


        ReferenceDataLookUp referenceDataLkp4 = new ReferenceDataLookUp("MIN_OVERDRAFT_LIMIT", "106", "Cnt_Pnt_Prtflio", new Long("1324"), "BHR", "LTB", new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp4);


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
        ReferenceDataLookUp referenceDataLkp4 = new ReferenceDataLookUp(groupCode, "LTB", "Manufacturing Legal entity Code", new Long("48"), "CAG", channel, new Long("1"));
        ReferenceDataLookUp referenceDataLkp5 = new ReferenceDataLookUp(groupCode, "LTB", "Manufacturing Legal entity Code", new Long("49"), "LTB", channel, new Long("1"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpList.add(referenceDataLkp2);
        referenceDataLookUpList.add(referenceDataLkp3);
        referenceDataLookUpList.add(referenceDataLkp4);
        referenceDataLookUpList.add(referenceDataLkp5);
        referenceDataLookUpDao.save(referenceDataLookUpList);
    }

    @Override
    @Transactional
    public void expectInstructionHierarchyCall(String insMnemonic, String brand) {
        RefInstructionHierarchyPrdDto hierarchyPrdDto = new RefInstructionHierarchyPrdDto(insMnemonic, "Club Lloyds", "G_PCA", "PCA", null, brand);
        RefInstructionHierarchyPrdDto hierarchyPrdDto1 = new RefInstructionHierarchyPrdDto(insMnemonic, "Club Lloyds", "P_CLUB", "PCA", null, brand);
        refInstructionHierarchyPrdDao.save(hierarchyPrdDto);
        refInstructionHierarchyPrdDao.save(hierarchyPrdDto1);

    }

    @Override
    @Transactional
    public void expectInstructionRulesViewCall(String insMnemonic, String brand) {
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto(insMnemonic, "GR020", "Customer not eligible for Current Account", "CR002", "Customer cannot be younger that 18 years", "GRP", "17", "CST", brand, BigDecimal.ONE);
        RefInstructionRulesPrdDto refInstructionRulesPrdDto1 = new RefInstructionRulesPrdDto("P_CLUB", "GR020", "Customer not eligible for Current Account", "CR002", "Customer cannot be younger that 18 years", "GRP", "17", "CST", "LTB", BigDecimal.ONE);
        refInstructionRulesPrdDao.save(refInstructionRulesPrdDto);
        refInstructionRulesPrdDao.save(refInstructionRulesPrdDto1);
    }

    @Override
    @Transactional
    public void expectInstructionRulesViewCallforEligibilityFailure(String insMnemonic, String brand) {
        refInstructionRulesPrdDao.deleteAll();
        RefInstructionRulesPrdDto refInstructionRulesPrdDto1 = new RefInstructionRulesPrdDto(insMnemonic, "GR020", "Customer not eligible for Current Account", "CR002", "Customer cannot be younger that 200 years", "GRP", "200", "CST", brand, BigDecimal.ONE);

        refInstructionRulesPrdDao.save(refInstructionRulesPrdDto1);
    }

    public void expectLookupListFromGroupCodeAndChannelAndLookUpText(String groupCd, String channel, List<String> lookUpText) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp(groupCd, "12232", "Cnt_Pnt_Prtflio", new Long("13231"), lookUpText.get(0), channel, new Long("501"));
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
        mockControl.matching("actual.target == 'ocisF062'");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F062Req req = dataHelper.createF062Req(arrangementType, primaryInvolvedParty, header, marketingPrefIndicator, addressStrength, partyStrength);
        F062Resp response = new TestDataHelper().createF062Response(reasoncode);
        f062Client.updateCustomerRecord(req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF062Control.thenReturn(response);
    }

    @Override
    public void expectF062CallWhenBfpoNotPresent(String arrangementType, boolean marketingPrefIndicator, Customer primaryInvolvedParty, RequestHeader header, String addressStrength, String partyStrength, int reasoncode) throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {

        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F062Req req = dataHelper.createF062ReqWhenBfpoNotPresent(arrangementType, primaryInvolvedParty, header, marketingPrefIndicator, addressStrength, partyStrength);
        F062Resp response = new TestDataHelper().createF062Response(reasoncode);
        f062Client.updateCustomerRecord(req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF062Control.thenReturn(response);
    }

    @Override
    public void clearUp() {
        referenceDataLookUpDao.deleteAll();
        tmxDao.deleteAll();
        //refInstructionHierarchyPrdDao.deleteAll();
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
        externalSystemProductDao.deleteAll();
        // productFeatureDao.deleteAll();
        clearWpsCache();

    }

    @Override
    public void expectPrdDbCalls() {
        expectInstructionHierarchyCall("P_CLUB", "LTB");
        expectInstructionRulesViewCall("G_PCA", "LTB");
        expectProductPropositionCall();
        expectProductFamilyCall();
        expectProductPackageVwCall();
        expectExternalSystemTemplateVwCall();
        expectExternalSystemProductsPrdData();
        expectProductFeatureDaoVwCall();
    }

    @Transactional
    private void expectExternalSystemTemplateVwCall() {
        externalSystemTemplateDao.save(new ExternalSystemTemplate(new ExternalSystemTemplateId("00001", 20045l), "CCA_Generic", 1l));
        externalSystemTemplateDao.save(new ExternalSystemTemplate(new ExternalSystemTemplateId("00001", 20044l), "CCA_Generic", 1l));
        externalSystemTemplateDao.save(new ExternalSystemTemplate(new ExternalSystemTemplateId("00001", 20043l), "CCA_Generic", 1l));
        externalSystemTemplateDao.save(new ExternalSystemTemplate(new ExternalSystemTemplateId("00001", 20042l), "CCA_Clarity", 1l));
    }

    @Transactional
    private void expectProductFeatureDaoVwCall() {
        ProductFeature productFeature = new ProductFeature(20043L, "100069", "Max eligible cards", "MaxElgblCard", "Max eligible cards", "Platinum Purchase Card", "100", "4", new Date("29-SEP-15"), null);
        ProductFeature productFeature1 = new ProductFeature(20044L, "100070", "Max eligible cards", "MaxElgblCard", "Max eligible cards", "Platinum Purchase Card", "100", "4", new Date("29-SEP-15"), null);
        ProductFeature productFeature2 = new ProductFeature(20045L, "100071", "Max eligible cards", "MaxElgblCard", "Max eligible cards", "Platinum Purchase Card", "100", "4", new Date("29-SEP-15"), null);

        productFeatureDao.save(productFeature);
        productFeatureDao.save(productFeature1);
        productFeatureDao.save(productFeature2);

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
        productGroups.add(new ProductGroup(20l, "1", 20044l, "700", "00107", "102"));
        productGroups.add(new ProductGroup(21l, "2", 20045l, "700", "00107", "102"));
        productGroupDao.save(productGroups);
    }

    @Transactional
    private void expectProductPropositionCall() {
        List<ProductProposition> productPropositions = new ArrayList<>();
        productPropositions.add(new ProductProposition(25l, 10017l, "Platinum 9/9"));
        productPropositions.add(new ProductProposition(42l, 20042l, "Clarity"));
        productPropositions.add(new ProductProposition(32l, 20040l, "Clarity1"));
        productPropositions.add(new ProductProposition(33l, 20043l, "Clarity2"));
        productPropositions.add(new ProductProposition(34l, 20044l, "Clarity2"));
        productPropositions.add(new ProductProposition(35l, 20045l, "Clarity2"));
        productPropositionDao.save(productPropositions);
    }

    @Transactional
    public void expectExternalSystemProductsPrdData() {
        ExternalSystemProductId id = new ExternalSystemProductId(20043l, "2541", "00013");
        ExternalSystemProductId id1 = new ExternalSystemProductId(20044l, "2541", "00013");
        ExternalSystemProductId id2 = new ExternalSystemProductId(20045l, "2541", "00013");
        ExternalSystemProduct externalSystemProducts = new ExternalSystemProduct(id, "Club Account", "Club Account", "LTB");
        ExternalSystemProduct externalSystemProducts1 = new ExternalSystemProduct(id1, "Club Account", "Club Account", "LTB");
        ExternalSystemProduct externalSystemProducts2 = new ExternalSystemProduct(id2, "Club Account", "Club Account", "LTB");

        externalSystemProductDao.save(externalSystemProducts);
        externalSystemProductDao.save(externalSystemProducts1);
        externalSystemProductDao.save(externalSystemProducts2);

    }


    @Override
    @Transactional
    public void expectPAMReferenceData() {
        dataHelper.createPamReferenceData();
    }

    @Override
    public void expectRpc(RequestHeader requestHeader, int offerType) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = dataHelper.rpcRequest(requestHeader);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResponse = null;
        switch (offerType) {
            case 2001:
                rpcResponse = dataHelper.rpcResponse();
                break;
            case 2003:
                rpcResponse = dataHelper.rpcResponseForDownsell();
                break;
            case 2002:
                rpcResponse = dataHelper.rpcResponseForUpsell();
                break;
            case 2005:
                rpcResponse = dataHelper.rpcResponseForNormal();
                break;
        }
        mockControl.matching("actual.target == 'rpc'");
        rpcServiceClient.retrieveProductConditions(rpcRequest);
        mockRpcControl.thenReturn(rpcResponse);
    }

    @Override
    public void expectRpcEmptyProductListResponse(RequestHeader requestHeader) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = dataHelper.rpcRequest(requestHeader);
        lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse rpcResponse = new lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse();
        Product product = new Product();
        product.setProductIdentifier("2004");
        rpcResponse.getProduct().add(product);
        rpcResponse.getProduct().get(0).getProductoffer().add(new ProductOffer());
        rpcResponse.getProduct().get(0).getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        ProductOptions options = new ProductOptions();
        options.setOptionsType("");
        rpcResponse.getProduct().get(0).getProductoptions().add(options);

        mockControl.matching("actual.target == 'rpc'");
        rpcServiceClient.retrieveProductConditions(rpcRequest);
        mockRpcControl.thenReturn(rpcResponse);
    }

    @Transactional
    public void expectDuplicationApplications() {
        dataHelper.createApplication();
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
    public long expectChildApplication() {
        return dataHelper.createApplication().getId();
    }

    @Override
    @Transactional
    public String expectChildApplicationForAppStatus() {
        return dataHelper.createApplication().getApplicationStatus().getStatus();
    }

    @Override
    @Transactional
    public int expectIndividualsCreated() {
        return dataHelper.getIndividualsSize();
    }

    @Override
    @Transactional
    public int expectPartyCountryAssociationCreated() {
        return dataHelper.getPartyCountryAssociationSize();
    }


    @Override
    @Transactional
    public int expectApplicationsUpdated() {
        return dataHelper.getApplicationSize();
    }

    @Override
    public void expectEligibility(int caseNo) throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = null;
        switch (caseNo) {
            case 1:
                eligibilityRequest = dataHelper.eligibilityRequestForDownsell();
                break;

            case 2:
                eligibilityRequest = dataHelper.eligibilityRequestForUpsell();
                break;

            case 3:
                eligibilityRequest = dataHelper.eligibilityRequestForNormal();
                break;

            default:
                break;

        }
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = dataHelper.eligibilityResponse();

        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);

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
    public Applications expectRetrieveApplicationFromPAM(Long arrangementId) {
        Applications applications = applicationsDao.findOne(arrangementId);
        return applications;
    }


    public void expectX711Call(Customer customer, RequestHeader header) throws OfferProductArrangementInternalServiceErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        IdentifyParty identifyParty = dataHelper.createX711Request(customer, contactPoint.getContactPointId());
        getX711Client("LTB").retrieveIdentityAndScores(identifyParty, contactPoint, serviceRequest, securityHeaderType);
        IdentifyPartyResp identifyPartyResp = dataHelper.createX711ResponseWithEidvApproved();
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
    public void expectEligibilityFailure(RequestHeader requestHeader, boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
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
        eligibilityResponse = dataHelper.eligibilityResponseForFailue();


        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);
    }

    @Override
    public void expectEligibilityCa(String isEligible, RequestHeader requestHeader, boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        mockControl.matching("actual.target == 'eligibilityWz' && actual.methodName == 'determineEligibleCustomerInstructions' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestHeader']).isEmpty()");

        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = null;
        switch (type) {
            case 1:
                eligibilityRequest = dataHelper.createEligibilityRequestWhenBfpoNotPresentAndEligibilityIsTrue(request, requestHeader, isBFPOIndicatorPresent);
                break;
            case 2:
                eligibilityRequest = dataHelper.createEligibilityRequestWhenBfpoPresentAndEligibilityIsTrue(request, requestHeader, isBFPOIndicatorPresent);
                break;
        }
        lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse eligibilityResponse = dataHelper.eligibilityResponse(isEligible);
        if (isEligible.equals("true")) {
            eligibilityResponse = dataHelper.eligibilityResponse(isEligible);
        } else {
            eligibilityResponse = dataHelper.eligibilityResponseForFailue();
        }
        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);
    }

    @Override
    public void expectEligibilityCaWithCustomerId(String isEligible, RequestHeader requestHeader, boolean isBFPOIndicatorPresent, int type) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCARequest("LTB");

        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = dataHelper.createEligibilityRequestWhenBfpoPresentAndEligibilityIsTrue(request, requestHeader, isBFPOIndicatorPresent);
        eligibilityRequest.getCustomerDetails().setCustomerIdentifier("12345");
        lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse eligibilityResponse = dataHelper.eligibilityResponse(isEligible);
        if (isEligible.equals("true")) {
            eligibilityResponse = dataHelper.eligibilityResponse(isEligible);
        } else {
            eligibilityResponse = dataHelper.eligibilityResponse(isEligible);
        }
        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);
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
        Source source=siraRequestFactory.convert(depositArrangement,requestHeader,new Date());
        mockControl.matching("actual.target == 'sira'");
        siraClient.submitWorkItemResult(source
                , depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerDeviceDetails().getWorkFlowName(), false, authenticationHeader);
        mockSiraControl.thenReturn(submitWorkItemResult);
    }
    @Override
    public void expectSiraCallWithError(BigInteger totalRuleScore,DepositArrangement depositArrangement,RequestHeader requestHeader) {
        SubmitWorkItemResponse.SubmitWorkItemResult submitWorkItemResult = new SubmitWorkItemResponse.SubmitWorkItemResult();
        RealtimeResultType4Type realtimeResultType4Type = new RealtimeResultType4Type();
        realtimeResultType4Type.setTotalRuleScore(totalRuleScore);
        realtimeResultType4Type.setFault(new DSFaultType());
        realtimeResultType4Type.getFault().setCode("E50035");
        JAXBElement jaxbElement = new JAXBElement(new QName("http://www.synectics-solutions.com/sira/schemas/realtime/core/v1.0/RealtimeResultType4.xsd", "RealtimeResultType4"), RealtimeResultType4Type.class, realtimeResultType4Type);
        submitWorkItemResult.getContent().add(jaxbElement);
        AuthenticationHeader authenticationHeader = new AuthenticationHeader();
        authenticationHeader.setClientName("LBG");
        authenticationHeader.setPassword("");
        authenticationHeader.setUsername("");
        Source source=siraRequestFactory.convert(depositArrangement,requestHeader,new Date());
        mockControl.matching("actual.target == 'sira'");
        siraClient.submitWorkItemResult(source
                , depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerDeviceDetails().getWorkFlowName(), false, authenticationHeader);
        mockSiraControl.thenReturn(submitWorkItemResult);
    }

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
    public void expectF336CallWithError(RequestHeader header,String partyIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F336Req f336Req = dataHelper.createF336Request(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()), partyIdentifier);

        F336Resp f336Resp = dataHelper.createF336ResponseWithError();
        f336Client.getProductHoldings(f336Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF336Control.thenReturn(f336Resp);
    }

    @Override
    @Transactional
    public int expectApplicationsParameterValueCreated() {
        return dataHelper.getApplicationsParameterValueSize();
    }

    @Override
    public long expectRelatedapplicationId() {
        return dataHelper.createApplication().getId();
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

    @Override
    public void expectF061CallWithError(RequestHeader header, String customerIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
        F061Req f061Req = dataHelper.createF061Req(bapiInformation, customerIdentifier);
        F061Resp f061Resp = dataHelper.createF061RespWithError();
        f061Client.f061(f061Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF061Control.thenReturn(f061Resp);
    }

    @Override
    public void expectF061CallWithImproperResponse(RequestHeader header, String customerIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
        F061Req f061Req = dataHelper.createF061Req(bapiInformation, customerIdentifier);
        F061Resp f061Resp = dataHelper.createF061RespWithImproperResponse();
        f061Client.f061(f061Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF061Control.thenReturn(f061Resp);
    }

    @Override
    public void expectF204CallWithExternalBusinessError(RequestHeader header, String s, List<ReferralCode> referralCodeList){
        mockControl.matching("actual.target == 'asmF204' && actual.methodName == 'f204' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCARequest("777505");
        upstreamRequest.getProductArrangement().setFinancialInstitution(new Organisation());
        upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F204Req f204Req = new RetrieveFraudDecisionRequestFactory().create(upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(), upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode()
                , upstreamRequest.getHeader().getContactPointId(), upstreamRequest.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier(), upstreamRequest.getProductArrangement().getArrangementId(),
                upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress());
        f204Req.getRequestDetails().setSortCd(header.getContactPointId());
        F204Resp resp = dataHelper.createF204ResponseWithExternalBusinessError();
        f204Client.performFraudCheck(f204Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF204Control.thenReturn(resp);
    }

    @Override
    public void expectF204CallWithExternalServiceError(RequestHeader header, String s, List<ReferralCode> referralCodeList) {
        mockControl.matching("actual.target == 'asmF204' && actual.methodName == 'f204' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCARequest("777505");
        upstreamRequest.getProductArrangement().setFinancialInstitution(new Organisation());
        upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F204Req f204Req = new RetrieveFraudDecisionRequestFactory().create(upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(), upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode()
                , upstreamRequest.getHeader().getContactPointId(), upstreamRequest.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier(), upstreamRequest.getProductArrangement().getArrangementId(),
                upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress());
        f204Req.getRequestDetails().setSortCd(header.getContactPointId());
        F204Resp resp = dataHelper.createF204ResponseWithExternalServiceError();
        f204Client.performFraudCheck(f204Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF204Control.thenReturn(resp);
    }

    @Override
    public void expectF205CallWithExternalServiceError(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, int caseNo) throws ParseException {
        mockControl.matching("actual.target == 'asmF205' && actual.methodName == 'f205' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCARequest("777505");
        upstreamRequest.getProductArrangement().setFinancialInstitution(new Organisation());
        upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F205Req f205Req = new RetrieveCreditScoreRequestFactory().create(upstreamRequest.getHeader().getContactPointId(), upstreamRequest.getProductArrangement().getArrangementId(),
                upstreamRequest.getProductArrangement().getAssociatedProduct().getBrandName(), upstreamRequest.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty(), upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode(),
                upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(),
                upstreamRequest.getProductArrangement().getAccountPurpose(),
                upstreamRequest.getProductArrangement().getExistingProducts(),upstreamRequest.getProductArrangement().getConditions());

        f205Req.getRequestDetails().setSortCd(header.getContactPointId());
        F205Resp resp = dataHelper.createF205Response(153116);
        f205Client.fetchCreditDecisionForCurrentAccount(f205Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF205Control.thenReturn(resp);
    }

    @Override
    public void expectF205CallWithExternalBusinessError(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, int caseNo) throws ParseException {
        mockControl.matching("actual.target == 'asmF205' && actual.methodName == 'f205' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCARequest("777505");
        upstreamRequest.getProductArrangement().setFinancialInstitution(new Organisation());
        upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F205Req f205Req = new RetrieveCreditScoreRequestFactory().create(upstreamRequest.getHeader().getContactPointId(), upstreamRequest.getProductArrangement().getArrangementId(),
                upstreamRequest.getProductArrangement().getAssociatedProduct().getBrandName(), upstreamRequest.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty(), upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode(),
                upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(),
                upstreamRequest.getProductArrangement().getAccountPurpose(),
                upstreamRequest.getProductArrangement().getExistingProducts(),upstreamRequest.getProductArrangement().getConditions());

        f205Req.getRequestDetails().setSortCd(header.getContactPointId());
        F205Resp resp = dataHelper.createF205Response(159179);
        f205Client.fetchCreditDecisionForCurrentAccount(f205Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF205Control.thenReturn(resp);
    }

    @Override
    public void expectF205CallWithInternalServiceError(RequestHeader header, String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, int caseNo) throws ParseException {
        mockControl.matching("actual.target == 'asmF205' && actual.methodName == 'f205' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestDetails/creditScoreRequestNo']).isEmpty()");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        OfferProductArrangementRequest upstreamRequest = dataHelper.generateOfferProductArrangementPCARequest("777505");
        upstreamRequest.getProductArrangement().setFinancialInstitution(new Organisation());
        upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        F205Req f205Req = new RetrieveCreditScoreRequestFactory().create(upstreamRequest.getHeader().getContactPointId(), upstreamRequest.getProductArrangement().getArrangementId(),
                upstreamRequest.getProductArrangement().getAssociatedProduct().getBrandName(), upstreamRequest.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier(), upstreamRequest.getProductArrangement().getPrimaryInvolvedParty(), upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode(),
                upstreamRequest.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode(),
                upstreamRequest.getProductArrangement().getAccountPurpose(),
                upstreamRequest.getProductArrangement().getExistingProducts(),upstreamRequest.getProductArrangement().getConditions());

        f205Req.getRequestDetails().setSortCd(header.getContactPointId());
        F205Resp resp = dataHelper.createF205Response(155012);
        f205Client.fetchCreditDecisionForCurrentAccount(f205Req, contactPoint, serviceRequest, securityHeaderType);
        mockAsmF205Control.thenReturn(resp);
    }

    @Override
    public void expectEligibilityWithException(RequestHeader requestHeader, int exceptionType) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        mockControl.matching("actual.target == 'eligibilityWz' && actual.methodName == 'determineEligibleCustomerInstructions' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestHeader']).isEmpty()");
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = null;
        lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse eligibilityResponse = dataHelper.eligibilityResponseWithException(exceptionType);
        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);
    }

    @Override
    public void expectF336CallWithEmptyAmdEffDt(RequestHeader header, int productOneGroupId, int productTwoGroupId, String partyIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        F336Req f336Req = dataHelper.createF336Request(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()), partyIdentifier);

        F336Resp f336Resp = dataHelper.createF336ResponseWithEmptyAmdEffDt(productOneGroupId, productTwoGroupId);
        f336Client.getProductHoldings(f336Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF336Control.thenReturn(f336Resp);
    }

    @Override
    public void expectF061CallWithNoAddressLinePaf(RequestHeader header, String customerIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
        F061Req f061Req = dataHelper.createF061Req(bapiInformation, customerIdentifier);
        F061Resp resp = dataHelper.createF061RespWithNoAddressLinePaf(0);
        f061Client.f061(f061Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF061Control.thenReturn(resp);
    }

}
