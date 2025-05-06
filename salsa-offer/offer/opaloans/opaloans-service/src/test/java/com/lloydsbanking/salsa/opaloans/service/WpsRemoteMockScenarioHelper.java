package com.lloydsbanking.salsa.opaloans.service;

import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.downstream.ar.client.ArrangementReportingClient;
import com.lloydsbanking.salsa.downstream.cbs.client.e141.E141Client;
import com.lloydsbanking.salsa.downstream.ipa.client.IpaClient;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionHierarchyPrdDto;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydsbanking.salsa.downstream.productcustomermatching.client.wz.ProductCustomerMatchingClient;
import com.lloydsbanking.salsa.remotemock.MockControlArrangementSetupWzServicePortType;
import com.lloydsbanking.salsa.remotemock.MockControlCbsE141ServicePortType;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Req;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Resp;
import com.lloydsbanking.salsa.soap.fs.ifw.account.RetrieveProductArrangementsRequest;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.DetermineCustomerEligibleProductsRequest;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.DetermineCustomerEligibleProductsResponse;
import com.lloydstsb.schema.enterprise.lcsm_arrangementreporting.ErrorInfo;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.RetrieveMandateAccessDetailsRequest;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.RetrieveMandateAccessDetailsResponse;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class WpsRemoteMockScenarioHelper extends RemoteMockScenarioHelper {
    @Autowired
    ArrangementReportingClient arrangementReportingClient;

    @Autowired
    ProductCustomerMatchingClient productCustomerMatchingClient;

    @Autowired
    IpaClient ipaClient;

    @Autowired
    MockControlArrangementSetupWzServicePortType mockControlArrangementSetupWz;

    @Autowired
    MockControlCbsE141ServicePortType mockControlCbsE141;

    Map<String, E141Client> cbsE141ClientMap;

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
    public void expectB231Call(RequestHeader requestHeader, String customerIdentifier, String partyIdentifier, int errorNo) throws ErrorInfo, com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo {
        DetermineCustomerEligibleProductsRequest determineCustomerEligibleProductsRequest;
        if (null == customerIdentifier) {
            determineCustomerEligibleProductsRequest = dataHelper.createDetermineCustomerEligibleProductsRequestWithCustomerIdentifierNull();
        } else {
            determineCustomerEligibleProductsRequest = dataHelper.createDetermineCustomerEligibleProductsRequest();
        }
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());

        productCustomerMatchingClient.determineCustomerEligibleProducts(determineCustomerEligibleProductsRequest, contactPoint, serviceRequest, securityHeaderType);
        if (errorNo == 0) {
            mockControlArrangementSetupWz.thenReturn(dataHelper.createDetermineCustomerEligibleProductsResponse());
        } else {
            mockControlArrangementSetupWz.thenReturn(dataHelper.createDetermineCustomerEligibleProductsResponseWithReasonCode());
        }
    }

    @Override
    public void expectB231CallWithoutEligibleProducts(RequestHeader requestHeader, String customerIdentifier, String partyIdentifier, int errorNo) throws com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo {
        DetermineCustomerEligibleProductsRequest determineCustomerEligibleProductsRequest = dataHelper.createDetermineCustomerEligibleProductsRequestWithCustomerIdentifierNull();
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());

        productCustomerMatchingClient.determineCustomerEligibleProducts(determineCustomerEligibleProductsRequest, contactPoint, serviceRequest, securityHeaderType);
        if (errorNo == 0) {
            DetermineCustomerEligibleProductsResponse response = dataHelper.createDetermineCustomerEligibleProductsResponse();
            response.getEligibleProducts().clear();
            mockControlArrangementSetupWz.thenReturn(response);
        } else {
            mockControlArrangementSetupWz.thenReturn(dataHelper.createDetermineCustomerEligibleProductsResponseWithReasonCode());
        }
    }

    @Override
    public void expectB237Call(RequestHeader requestHeader, String customerIdentifier, String partyIdentifier, String duplicateStatus, int errorNo) throws ErrorInfo {
        RetrieveProductArrangementsRequest retrieveProductArrangementsRequest = dataHelper.createRetrieveProductArrangementsRequest();
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());

        mockControl.matching("actual.target == 'fsArrangementReportingIfw' && actual.methodName == 'retrieveProductArrangements'");
        arrangementReportingClient.retrieveProductArrangements(retrieveProductArrangementsRequest, contactPoint, serviceRequest, securityHeaderType);

        if ("true".equals(duplicateStatus)) {
            mockControl.thenReturn(dataHelper.createRetrieveProductArrangementsResponseWithDuplicateSavedLoan());
        } else {
            mockControl.thenReturn(dataHelper.createRetrieveProductArrangementsResponse());
        }
    }

    @Override
    @Transactional
    public void expectPrdDbCalls(final String insMnemonic, final String brand, final String rule) {
        RefInstructionHierarchyPrdDto hierarchyPrdDto = new RefInstructionHierarchyPrdDto(insMnemonic, "Personal Loan", "G_LOAN", "Personal Loan", null, brand);
        RefInstructionRulesPrdDto refInstructionRulesPrdDto;
        RefInstructionRulesPrdDto refInstructionRulesPrdDto1;
        if ("CR046".equals(rule)) {
            refInstructionRulesPrdDto1 = new RefInstructionRulesPrdDto(insMnemonic, "GR001", "Customer not eligible for personal loan ", "CR044", "The customers account is dormant", "GRP", "615", "AGT", brand, BigDecimal.ONE);
            refInstructionRulesPrdDto = new RefInstructionRulesPrdDto(insMnemonic, "GR001", "Customer not eligible for personal loan", "CR046", "Post returned from this address", "GRP", "20", "CST", brand, BigDecimal.ONE);
            refInstructionRulesPrdDao.save(refInstructionRulesPrdDto1);
        } else if ("CR047".equals(rule)) {
            refInstructionRulesPrdDto = new RefInstructionRulesPrdDto(insMnemonic, "GR001", "Customer not eligible for personal loan", "CR047", "Address Validation Failed", "GRP", "2", "CST", brand, BigDecimal.ONE);
        } else {
            refInstructionRulesPrdDto = new RefInstructionRulesPrdDto(insMnemonic, "GR001", "Customer not eligible for personal loan", "CR001", "Customer cannot be older that 74 years", "GRP", "74", "CST", brand, BigDecimal.ONE);
        }
        refInstructionHierarchyPrdDao.save(hierarchyPrdDto);
        refInstructionRulesPrdDao.save(refInstructionRulesPrdDto);
    }

    @Override
    public void expectB695Call(RequestHeader header, String accType, String eventType) throws com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.ErrorInfo {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        RetrieveMandateAccessDetailsRequest request = dataHelper.createRetrieveMandateAccessDetailsRequest(accType);

        RetrieveMandateAccessDetailsResponse response = dataHelper.createRetrieveMandateAccessDetailsResponse(eventType);
        ipaClient.retrieveMandateAccessDetails(request, contactPoint, serviceRequest, securityHeaderType);
        mockControl.thenReturn(response);
    }

    @Override
    public void expectE141Call(RequestHeader header, List<Integer> indicators, String sortCode, String accNo, String maxLimitAmt) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header);
        CBSAppGrp cbsAppGrp = dataHelper.createCBSAppGroupFromSortCode(sortCode);
        E141Req e141Req = dataHelper.createE141Request(sortCode, accNo);
        E141Resp e141Resp = dataHelper.createE141Response(indicators, maxLimitAmt);
        expectE141Call(headerRetriever.getChannelId(header), e141Resp, e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
    }

    private void expectE141Call(String channel, E141Resp e141Resp, E141Req e141Req, ContactPoint contactPoint, ServiceRequest serviceRequest, SecurityHeaderType securityHeaderType, CBSAppGrp cbsAppGrp) {
        e141Client(channel).getProductArrangementIndicator(e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        mockControlCbsE141.thenReturn(e141Resp);
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
}