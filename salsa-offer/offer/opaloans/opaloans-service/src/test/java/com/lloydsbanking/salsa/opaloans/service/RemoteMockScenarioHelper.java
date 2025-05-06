package com.lloydsbanking.salsa.opaloans.service;

import com.lloydsbanking.salsa.downstream.loan.client.LoanClient;
import com.lloydsbanking.salsa.downstream.ocis.client.c216.C216Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f336.F336Client;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.prd.jdbc.RefInstructionHierarchyPrdDao;
import com.lloydsbanking.salsa.downstream.prd.jdbc.RefInstructionRulesPrdDao;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionHierarchyPrdDto;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydsbanking.salsa.downstream.switches.jdbc.SwitchDao;
import com.lloydsbanking.salsa.downstream.switches.model.SwitchDto;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.downstream.EligibilityServiceClient;
import com.lloydsbanking.salsa.remotemock.*;
import com.lloydsbanking.salsa.soap.ocis.c216.objects.C216Req;
import com.lloydsbanking.salsa.soap.ocis.c216.objects.C216Resp;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Req;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Req;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Resp;
import com.lloydstsb.ib.wsbridge.loan.StB231ALoanPartyProductsGet;
import com.lloydstsb.ib.wsbridge.loan.StB231BLoanPartyProductsGet;
import com.lloydstsb.ib.wsbridge.loan.StB237ALoanSavedListGet;
import com.lloydstsb.ib.wsbridge.loan.StB237BLoanSavedListGet;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm_arrangementreporting.ErrorInfo;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RemoteMockScenarioHelper implements ScenarioHelper {
    @Autowired
    MockControlServicePortType mockControl;

    @Autowired
    MockControlOcisF336ServicePortType mockOcisF336Control;

    @Autowired
    MockControlOcisF061ServicePortType mockOcisF061Control;

    @Autowired
    MockControlFsLoanServicePortType mockFsLoanControl;

    @Autowired
    MockControlOcisC216ServicePortType mockOcisC216Control;

    @Autowired
    MockControlEligibilityServicePortType mockEligibilityControl;

    @Autowired
    TestDataHelper dataHelper;

    @Autowired
    F336Client f336Client;

    @Autowired
    F061Client f061Client;

    @Autowired
    LoanClient loanClient;

    @Autowired
    C216Client c216Client;

    @Autowired
    EligibilityServiceClient eligibilityServiceClient;

    @Autowired
    ReferenceDataLookUpDao referenceDataLookUpDao;

    @Autowired
    ApplicationStatusDao appStatusDao;

    @Autowired
    ApplicationTypesDao applicationTypesDao;

    @Autowired
    PromotionChannelsDao promotionChannelsDao;

    @Autowired
    ProductTypesDao productTypesDao;

    @Autowired
    UserTypesDao userTypesDao;

    @Autowired
    BrandsDao brandsDao;

    @Autowired
    ChannelsDao channelsDao;

    @Autowired
    ParameterGroupsDao parameterGroupsDao;

    @Autowired
    ApplicationParametersDao applicationParametersDao;

    @Autowired
    ApplicationPartyRolesDao applicationPartyRolesDao;

    @Autowired
    KycStatusDao kycStatusDao;

    @Autowired
    ApplicationsDao applicationsDao;

    @Autowired
    IndividualsDao individualsDao;

    @Autowired
    ApprovalStatusDao approvalStatusDao;

    @Autowired
    PromotionPartiesDao promotionPartiesDao;

    @Autowired
    SwitchDao switchDao;

    @Autowired
    RefInstructionHierarchyPrdDao refInstructionHierarchyPrdDao;

    @Autowired
    RefInstructionRulesPrdDao refInstructionRulesPrdDao;

    @Value("${salsa.fs.boxid}")
    int boxId;

    HeaderRetriever headerRetriever = new HeaderRetriever();

    @Override
    public void expectF336Call(RequestHeader header, String partyIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders());
        F336Req f336Req = dataHelper.createF336Request(bapiInformation, partyIdentifier);
        F336Resp f336Resp = dataHelper.createF336Response();
        f336Client.getProductHoldings(f336Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF336Control.thenReturn(f336Resp);
    }

    @Override
    public void expectF061Call(RequestHeader header, String customerIdentifier, String partyIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
        F061Req f061Req = dataHelper.createF061Req(bapiInformation, customerIdentifier);
        F061Resp f061Resp = dataHelper.createF061Resp(customerIdentifier, partyIdentifier);
        f061Client.f061(f061Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF061Control.thenReturn(f061Resp);
    }

    @Override
    public void expectF061CallForNonKycCompliance(RequestHeader header, String customerIdentifier, String partyIdentifier) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
        F061Req f061Req = dataHelper.createF061Req(bapiInformation, customerIdentifier);
        F061Resp f061Resp = dataHelper.createF061Resp(customerIdentifier, partyIdentifier);
        f061Resp.getPartyEnqData().getPersonalData().setBirthDt("04081973");
        f061Client.f061(f061Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisF061Control.thenReturn(f061Resp);
    }

    @Override
    public void clearUp() {
        refInstructionHierarchyPrdDao.deleteAll();
        refInstructionRulesPrdDao.deleteAll();
        switchDao.deleteAll();
        referenceDataLookUpDao.deleteAll();
        applicationsDao.deleteAll();
        appStatusDao.deleteAll();
        applicationTypesDao.deleteAll();
        productTypesDao.deleteAll();
        brandsDao.deleteAll();
        userTypesDao.deleteAll();
        promotionChannelsDao.deleteAll();
        channelsDao.deleteAll();
        applicationParametersDao.deleteAll();
        parameterGroupsDao.deleteAll();
        individualsDao.deleteAll();
        applicationPartyRolesDao.deleteAll();
        kycStatusDao.deleteAll();
        approvalStatusDao.deleteAll();
        promotionPartiesDao.deleteAll();
    }

    @Override
    public void expectPAMReferenceData() {
        dataHelper.createPamReferenceData();
    }

    @Override
    public void expectB231Call(RequestHeader requestHeader, String customerIdentifier, String partyIdentifier, int errorNo) throws ErrorInfo, com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo {
        StB231ALoanPartyProductsGet b231Request = dataHelper.createB231Request(requestHeader, customerIdentifier, partyIdentifier);
        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231Response(errorNo);
        loanClient.retrieveEligibleLoanProducts(b231Request);
        mockFsLoanControl.thenReturn(b231Response);
    }

    @Override
    public void expectB231CallWithoutEligibleProducts(RequestHeader requestHeader, String customerIdentifier, String partyIdentifier, int errorNo) throws com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo {
        StB231ALoanPartyProductsGet b231Request = dataHelper.createB231Request(requestHeader, customerIdentifier, partyIdentifier);
        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231ResponseWithoutStLoanProduct(errorNo);
        loanClient.retrieveEligibleLoanProducts(b231Request);
        mockFsLoanControl.thenReturn(b231Response);
    }

    @Override
    public void expectB237Call(RequestHeader header, String customerIdentifier, String partyIdentifier, String duplicateStatus, int errorNo) throws com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo, ErrorInfo {
        StB237ALoanSavedListGet b237Request = dataHelper.createB237Request(header, customerIdentifier, partyIdentifier);
        StB237BLoanSavedListGet b237Response = dataHelper.createB237Response(errorNo, duplicateStatus);
        loanClient.loanSavedList(b237Request);
        mockFsLoanControl.thenReturn(b237Response);
    }

    @Override
    public void expectEligibilityCall(RequestHeader header, String customerIdentifier, String eligibilityStatus, String code, String description) throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = dataHelper.createEligibilityRequest(header, customerIdentifier);
        mockControl.matching("actual.target == 'eligibilityWz' && actual.methodName == 'determineEligibleCustomerInstructions' && differingProperties(expected.arguments[1], actual.arguments[1], " +
                "['requestHeader']).isEmpty()");
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = dataHelper.createEligibilityResponse(eligibilityStatus, code, description);
        eligibilityServiceClient.determineEligibility(eligibilityRequest);
        mockEligibilityControl.thenReturn(eligibilityResponse);
    }

    @Override
    public void expectLRASwitchCall(String channel, String switchStatus) {
        String value = ("ON".equals(switchStatus) ? "1" : "0");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        SwitchDto switchDto = new SwitchDto(channel, "SW_EnableLRA", cal.getTime(), boxId, 1, "A", value);
        switchDao.save(switchDto);
    }

    @Override
    public void expectVerdeSwitchCall(String channel, String switchStatus) {
        String value = ("ON".equals(switchStatus) ? "1" : "0");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        SwitchDto switchDto = new SwitchDto(channel, "SW_STPLnsVrdTrns", cal.getTime(), boxId, 1, "A", value);
        switchDao.save(switchDto);
    }

    @Override
    @Transactional
    public void expectLookupDataForLegalEntity(String channel, String groupCode) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp(groupCode, "VER", "Manufacturing Legal entity Code", Long.valueOf("2226"), "VAG", channel, Long.valueOf("1"));
        ReferenceDataLookUp referenceDataLookUp1 = new ReferenceDataLookUp(groupCode, "VER", "Manufacturing Legal entity Code", new Long("2227"), "VGD", channel, Long.valueOf("1"));
        ReferenceDataLookUp referenceDataLookUp2 = new ReferenceDataLookUp(groupCode, "VER", "Manufacturing Legal entity Code", new Long("2228"), "VGI", channel, Long.valueOf("1"));
        ReferenceDataLookUp referenceDataLookUp3 = new ReferenceDataLookUp(groupCode, "VER", "Manufacturing Legal entity Code", new Long("2229"), "VTB", channel, Long.valueOf("1"));
        ReferenceDataLookUp referenceDataLookUp4 = new ReferenceDataLookUp(groupCode, "VER", "Manufacturing Legal entity Code", new Long("2230"), "VTS", channel, Long.valueOf("1"));
        ReferenceDataLookUp referenceDataLookUp5 = new ReferenceDataLookUp(groupCode, "VER", "Manufacturing Legal entity Code", new Long("2241"), "BOS", channel, Long.valueOf("1"));
        ReferenceDataLookUp referenceDataLookUp6 = new ReferenceDataLookUp(groupCode, "BOS", "Manufacturing Legal entity Code", new Long("2242"), "BSD", channel, Long.valueOf("1"));
        ReferenceDataLookUp referenceDataLookUp7 = new ReferenceDataLookUp(groupCode, "BOS", "Manufacturing Legal entity Code", new Long("2243"), "CPF", channel, Long.valueOf("1"));
        ReferenceDataLookUp referenceDataLookUp8 = new ReferenceDataLookUp(groupCode, "HLX", "Manufacturing Legal entity Code", new Long("2244"), "CAG", channel, Long.valueOf("1"));
        referenceDataLookUpList.add(referenceDataLookUp);
        referenceDataLookUpList.add(referenceDataLookUp1);
        referenceDataLookUpList.add(referenceDataLookUp2);
        referenceDataLookUpList.add(referenceDataLookUp3);
        referenceDataLookUpList.add(referenceDataLookUp4);
        referenceDataLookUpList.add(referenceDataLookUp5);
        referenceDataLookUpList.add(referenceDataLookUp6);
        referenceDataLookUpList.add(referenceDataLookUp7);
        referenceDataLookUpList.add(referenceDataLookUp8);
        referenceDataLookUpDao.save(referenceDataLookUpList);
    }

    @Override
    @Transactional
    public void expectGetChannelIdFromContactPointId(String contactPointId) {
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("Cnt_Pnt_Prtflio", contactPointId, "Display Contact_Point_Portfolio", Long.valueOf("1698"), "CONTACT_POINT_ID", "VER", Long.valueOf("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
    }

    @Override
    public void expectC216CallForUniqueDateOfBirth(RequestHeader header, String sortCode, String accountNumber, long partyId) {
        C216Resp c216Resp = dataHelper.createC216Response(partyId);
        expectC216Call(header, sortCode, accountNumber, c216Resp);
    }

    @Override
    public void expectC216CallForBirthDateNotMatched(RequestHeader header, String sortCode, String accountNumber, String additionalDataIndicator, long partyId) {
        C216Resp c216Resp = dataHelper.createC216Response(partyId);
        if ("true".equals(additionalDataIndicator)) {
            c216Resp.getPartyProdData().add(dataHelper.createPartyProdDataType(partyId));
        } else {
            c216Resp.getPartyProdData().get(0).setBirthDt("22011948");
        }
        expectC216Call(header, sortCode, accountNumber, c216Resp);
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

    private void expectC216Call(RequestHeader header, String sortCode, String accountNumber, C216Resp c216Resp) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        C216Req c216Req = dataHelper.createC216Request(sortCode, accountNumber);
        c216Client.c216(c216Req, contactPoint, serviceRequest, securityHeaderType);
        mockOcisC216Control.thenReturn(c216Resp);
    }

    @Override
    @Transactional
    public void expectPrdDbCalls(String insMnemonic, String brand, String rule) {
        RefInstructionHierarchyPrdDto hierarchyPrdDto;
        RefInstructionRulesPrdDto refInstructionRulesPrdDto;
        if ("CR046".equals(rule)) {
            hierarchyPrdDto = new RefInstructionHierarchyPrdDto(insMnemonic, "Personal Loan", "G_LOAN", "Personal Loan", null, brand);
            refInstructionRulesPrdDto = new RefInstructionRulesPrdDto(insMnemonic, "GR001", "Customer not eligible for personal loan", "CR046", "Post returned from this address", "GRP", "20", "CST", brand, BigDecimal.ONE);
        } else {
            hierarchyPrdDto = new RefInstructionHierarchyPrdDto(insMnemonic, "Personal Loan", "G_LOAN", "Personal Loan", null, brand);
            refInstructionRulesPrdDto = new RefInstructionRulesPrdDto(insMnemonic, "GR001", "Customer not eligible for personal loan", "CR001", "Customer cannot be older that 74 years", "GRP", "150", "CST", brand, BigDecimal.ONE);
        }
        refInstructionHierarchyPrdDao.save(hierarchyPrdDto);
        refInstructionRulesPrdDao.save(refInstructionRulesPrdDto);
    }

    @Override
    public void expectB695Call(RequestHeader header, String accType, String eventType) throws com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.ErrorInfo {
    }

    @Override
    public void expectE141Call(RequestHeader header, List<Integer> indicators, String sortCode, String accNo, String maxLimitAmt) {
    }
}