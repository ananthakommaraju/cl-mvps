package com.lloydsbanking.salsa.opaloans.service;

import com.lloydsbanking.salsa.downstream.loan.client.b231.B231RequestBuilder;
import com.lloydsbanking.salsa.downstream.loan.client.b237.B237RequestBuilder;
import com.lloydsbanking.salsa.downstream.loan.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061RequestBuilder;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydsbanking.salsa.opaloans.client.FinanceServiceArrangementBuilder;
import com.lloydsbanking.salsa.opaloans.client.InvolvedPartyBuilder;
import com.lloydsbanking.salsa.opaloans.client.OpaLoansRequestBuilder;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.*;
import com.lloydsbanking.salsa.soap.fs.ifw.account.RetrieveProductArrangementsRequest;
import com.lloydsbanking.salsa.soap.fs.ifw.account.RetrieveProductArrangementsResponse;
import com.lloydsbanking.salsa.soap.fs.loan.*;
import com.lloydsbanking.salsa.soap.ocis.c216.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.StructuredAddress;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.UnstructuredAddress;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.*;
import com.lloydstsb.ib.wsbridge.loan.StB231ALoanPartyProductsGet;
import com.lloydstsb.ib.wsbridge.loan.StB231BLoanPartyProductsGet;
import com.lloydstsb.ib.wsbridge.loan.StB237ALoanSavedListGet;
import com.lloydstsb.ib.wsbridge.loan.StB237BLoanSavedListGet;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm.CurrencyAmount;
import com.lloydstsb.schema.enterprise.lcsm.ResponseHeader;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.ArrangementType;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.FinanceServiceArrangementStatus;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.DetermineCustomerEligibleProductsRequest;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.DetermineCustomerEligibleProductsResponse;
import com.lloydstsb.schema.enterprise.lcsm_authorization.AccessPermissionDetail;
import com.lloydstsb.schema.enterprise.lcsm_authorization.AccessPermissionDetailType;
import com.lloydstsb.schema.enterprise.lcsm_authorization.AccessPermissions;
import com.lloydstsb.schema.enterprise.lcsm_authorization.Mandate;
import com.lloydstsb.schema.enterprise.lcsm_common.AlternateId;
import com.lloydstsb.schema.enterprise.lcsm_common.ObjectReference;
import com.lloydstsb.schema.enterprise.lcsm_common.wz.*;
import com.lloydstsb.schema.enterprise.lcsm_common.wz.RuleCondition;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.InvolvedPartyRole;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.InvolvedPartyRoleType;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.RetrieveMandateAccessDetailsRequest;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.RetrieveMandateAccessDetailsResponse;
import com.lloydstsb.schema.enterprise.lcsm_product.wz.ProductComponentDetails;
import com.lloydstsb.schema.enterprise.lcsm_resourceitem.wz.ResourceItem;
import com.lloydstsb.schema.infrastructure.soap.*;
import com.lloydstsb.schema.infrastructure.soap.Condition;
import com.lloydstsb.schema.infrastructure.soap.ExtraConditions;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Component
public class TestDataHelper {
    public static final Short DEFAULT_PARTY_EXT_SYS_ID = 2;
    public static final Short PARTY_EXT_SYS_ID = 1;

    ApplicationTypes applicationTypes = new ApplicationTypes("10001", "New Application");
    PromotionChannels promotionChannels = new PromotionChannels("001", "Internet Banking");
    UserTypes userTypes = new UserTypes("1001", "Customer");
    Channels channels = new Channels("004", "Internet");
    ProductTypes productTypes = new ProductTypes("103", "Loan Account");
    Brands brands = new Brands("LTB", "Lloyds");
    Brands brands1 = new Brands("VER", "Verde");
    ApplicationStatus applicationStatus = new ApplicationStatus("1001", "Initialised");
    ParameterGroups parameterGroups = new ParameterGroups("IB", "Internet Banking");
    ApplicationParameters applicationParameters = new ApplicationParameters("100013", parameterGroups, "Credit Score Request Number");
    ApplicationPartyRoles roles1 = new ApplicationPartyRoles("0001", "Key Party");
    ApplicationPartyRoles roles2 = new ApplicationPartyRoles("0003", "Promotional Party Affiliate");
    KycStatus kycStatus = new KycStatus("ACCEPT");
    ApprovalStatus approvalStatus = new ApprovalStatus("001");

    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter = new BapiHeaderToStHeaderConverter();
    HeaderRetriever headerRetriever = new HeaderRetriever();

    private HashMap<String, String> sortCodeAppGrpMap;

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
    StreetAddressesDao streetAddressesDao;

    public TestDataHelper() {
        generateSortCodeAppGrpMap();
    }

    public RequestHeader createOpaLoansRequestHeader(String channelId) {
        return new RequestHeaderBuilder().businessTransaction("OfferProductArrangement").interactionId("1vh3pw30tmuui1mmjywmquu5e8").
                serviceRequest("ns4", "offerProductArrangement", "10.240.147.57", "1vh3pw30tmuui1mmjywmquu5e8").
                contactPoint("ns4", "003", "0000306993", "Internet Banking", "Browser", "10.16.1.18", "Customer").
                bapiInformation(channelId, "1vh3pw30tmuui1mmjywmquu5e8", "0", "ns5").securityHeader("lgsm", "UNAUTHSALE").build();
    }

    public OfferProductArrangementRequest generateOfferProductArrangementLoansRequest(String channelId) {
        RequestHeader header = createOpaLoansRequestHeader(channelId);
        return new OpaLoansRequestBuilder().requestHeader(header).financeServiceArrangement(createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268")).build();
    }

    public FinanceServiceArrangement createFinanceServiceArrangement(String birthDate, String existingSortCode, String existingAccountNo) {
        return new FinanceServiceArrangementBuilder().arrangementType().associatedProduct(createAssociatedProduct()).initiatedThrough(createInitiatedThrough()).
                primaryInvolvedParty(createInvolvedParty(birthDate, existingSortCode, existingAccountNo)).applicationType("10001").build();
    }

    public F336Req createF336Request(BapiInformation bapiInformation, String partyIdentifier) {
        String host = bapiInformation.getBAPIHeader().getStpartyObo().getHost();
        F336Req request = new F336Req();
        request.setExtSysId((short) 19);
        request.setExtPartyIdTx(partyIdentifier);
        request.setPartyId(null != partyIdentifier ? Long.valueOf(partyIdentifier) : 0l);
        request.setPartyExtSysId((null != host && host.endsWith("L")) ? (short) 1 : (short) 2);
        request.setClosurePeriodMonthsDr("000");
        request.setClosedOnlyIn("0");
        request.setCustomerConsentIn("2");
        return request;
    }

    public F336Resp createF336Response() {
        F336Resp f336Resp = new F336Resp();
        f336Resp.setAdditionalDataIn(0);

        F336Result f336Result = new F336Result();
        f336Result.setResultCondition(new ResultCondition());
        f336Result.getResultCondition().setSeverityCode((byte) 0);
        f336Resp.setF336Result(f336Result);

        f336Resp.getProductPartyData().add(0, createProductPartyData());
        return f336Resp;
    }

    public F061Req createF061Req(BapiInformation bapiInformation, String customerIdentifier) {
        String extPartyId = "";
        String host = "";
        Short partyExtSysId = DEFAULT_PARTY_EXT_SYS_ID;
        if (null != bapiInformation) {
            extPartyId = bapiInformation.getBAPIHeader().getStpartyObo().getPartyid();
            host = bapiInformation.getBAPIHeader().getStpartyObo().getHost();
        }
        if ("L".equals(host)) {
            partyExtSysId = PARTY_EXT_SYS_ID;
        }
        F061RequestBuilder requestBuilder = new F061RequestBuilder();
        F061Req request = requestBuilder.extPartyIdTx(extPartyId).partyExtSysId(partyExtSysId).build();
        request.setExtSysId((short) 19);
        request.setPartyId(Long.valueOf(customerIdentifier));
        return request;
    }

    public F061Resp createF061Resp(String customerIdentifier, String partyIdentifier) {
        F061Resp f061Resp = new F061Resp();

        F061Result f061Result = new F061Result();
        f061Result.setResultCondition(new ResultCondition());
        f061Result.getResultCondition().setSeverityCode((byte) 0);
        f061Resp.setF061Result(f061Result);

        f061Resp.setAdditionalDataIn(0);

        f061Resp.setPartyEnqData(new PartyEnqData());
        f061Resp.getPartyEnqData().setPersonalData(createPersonalData(customerIdentifier, partyIdentifier));
        f061Resp.getPartyEnqData().setAddressData(createAddressData());
        f061Resp.getPartyEnqData().getPhoneData().add(0, createPhoneData("001", "Residential", "07404065484"));
        f061Resp.getPartyEnqData().getPhoneData().add(1, createPhoneData("003", "Work", "07404065483"));
        f061Resp.getPartyEnqData().getPhoneData().add(2, createPhoneData("005", "Mobile / Personal", "07404065482"));
        f061Resp.getPartyEnqData().setPartyNonCoreData(createPartyNonCoreData());
        f061Resp.getPartyEnqData().setKYCNonCorePartyData(createKYCNonCorePartyData());
        f061Resp.getPartyEnqData().setEvidenceData(createEvidenceData());
        f061Resp.getPartyEnqData().setKYCPartyData(createKYCPartyData());
        return f061Resp;
    }

    public C216Req createC216Request(String sortCode, String accountNumber) {
        C216Req c216Req = new C216Req();
        c216Req.setMaxRepeatGroupQy(0);
        c216Req.setExtSysId(19);
        c216Req.setProdHeldExtSysId((short) 4);
        c216Req.setExtProdHeldIdTx(sortCode.concat(accountNumber).concat("00000"));
        return c216Req;
    }

    public C216Resp createC216Response(long partyId) {
        C216Resp c216Resp = new C216Resp();
        c216Resp.setC216Result(new C216Result());
        c216Resp.getC216Result().setResultCondition(new ResultCondition());
        c216Resp.getC216Result().getResultCondition().setSeverityCode((byte) 0);
        c216Resp.setAdditionalDataIn(0);
        c216Resp.getPartyProdData().add(0, createPartyProdDataType(partyId));
        return c216Resp;
    }

    public C216Resp createC216ResponseWithoutCustomerDetails() {
        C216Resp c216Resp = new C216Resp();
        c216Resp.setC216Result(new C216Result());
        c216Resp.getC216Result().setResultCondition(new ResultCondition());
        c216Resp.getC216Result().getResultCondition().setSeverityCode((byte) 0);
        c216Resp.setAdditionalDataIn(0);
        return c216Resp;
    }

    public StB231ALoanPartyProductsGet createB231Request(RequestHeader requestHeader, String customerIdentifier, String partyIdentifier) {
        B231RequestBuilder builder = new B231RequestBuilder();
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders()).getBAPIHeader();
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader);
        String contactPointId = headerRetriever.getContactPoint(requestHeader).getContactPointId();
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(bapiHeader, serviceRequest, contactPointId);
        builder.stHeader(stHeader);
        if (!StringUtils.isEmpty(customerIdentifier)) {
            builder.stParty("T", new BigInteger(customerIdentifier), partyIdentifier);
        } else {
            builder.stParty("T", BigInteger.ZERO, partyIdentifier);
        }
        builder.bNewApplication(true);
        return builder.build();
    }

    public StB231ALoanPartyProductsGet createB231RequestWithGivenStHeader(String customerIdentifier, String partyIdentifier) {
        B231RequestBuilder builder = new B231RequestBuilder();
        StHeader stHeader = createStHeader();
        builder.stHeader(stHeader);
        if (!StringUtils.isEmpty(customerIdentifier)) {
            builder.stParty("T", new BigInteger(customerIdentifier), partyIdentifier);
        } else {
            builder.stParty("T", BigInteger.ZERO, partyIdentifier);
        }
        builder.bNewApplication(true);
        return builder.build();
    }

    public StB231BLoanPartyProductsGet createB231Response(int errorNo) {
        StB231BLoanPartyProductsGet b231Response = new StB231BLoanPartyProductsGet();

        StError stError = new StError();
        stError.setErrorno(errorNo);
        b231Response.setSterror(stError);

        if (0 == errorNo) {
            StLoanHeader stLoanHeader = new StLoanHeader();
            stLoanHeader.setCustnum("77714600421506");
            stLoanHeader.setOcisid(BigInteger.valueOf(456662112));
            stLoanHeader.setPartyidPersId("00090001232");
            b231Response.setStloanheader(stLoanHeader);

            b231Response.getAstloanproduct().add(createStLoanProduct());
            b231Response.setCreditscoreno("STPL5819141218084429");
            b231Response.setBElecSigAvail(true);
            b231Response.setBSkipSplashPage(true);
        } else {
            b231Response.getSterror().setErrormsg("External Business Error");
            StLoanHeader stLoanHeader = new StLoanHeader();
            stLoanHeader.setOcisid(BigInteger.ZERO);
            b231Response.setStloanheader(stLoanHeader);
            b231Response.setBElecSigAvail(false);
            b231Response.setBSkipSplashPage(false);
        }
        return b231Response;
    }

    public StB231BLoanPartyProductsGet createB231ResponseWithoutStLoanProduct(int errorNo) {
        StB231BLoanPartyProductsGet b231Response = new StB231BLoanPartyProductsGet();

        StError stError = new StError();
        stError.setErrorno(errorNo);
        b231Response.setSterror(stError);

        StLoanHeader stLoanHeader = new StLoanHeader();
        stLoanHeader.setCustnum("77714600421506");
        b231Response.setStloanheader(stLoanHeader);

        b231Response.setCreditscoreno("STPL5819141218084429");
        return b231Response;
    }

    public StB237ALoanSavedListGet createB237RequestWithGivenStHeader(String customerIdentifier, String partyId) {
        B237RequestBuilder builder = new B237RequestBuilder();
        builder.stHeader(createStHeader());
        BigInteger customerId = (null != customerIdentifier ? new BigInteger(customerIdentifier) : null);
        builder.stPartyDetails(customerId, partyId, "T");
        builder.populateCache(false);
        return builder.build();
    }

    public StB237ALoanSavedListGet createB237Request(RequestHeader requestHeader, String customerIdentifier, String partyId) {
        B237RequestBuilder builder = new B237RequestBuilder();
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders()).getBAPIHeader();
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader);
        String contactPointId = headerRetriever.getContactPoint(requestHeader).getContactPointId();
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(bapiHeader, serviceRequest, contactPointId);
        builder.stHeader(stHeader);
        BigInteger customerId = (null != customerIdentifier ? new BigInteger(customerIdentifier) : null);
        builder.stPartyDetails(customerId, partyId, "T");
        builder.populateCache(false);
        return builder.build();
    }

    public StB237BLoanSavedListGet createB237Response(int errorNo, String duplicateLoanStatus) {
        StB237BLoanSavedListGet b237Response = new StB237BLoanSavedListGet();

        StError stError = new StError();
        stError.setErrorno(errorNo);
        b237Response.setSterror(stError);

        if (0 == errorNo && "true".equals(duplicateLoanStatus)) {
            b237Response.getAstloansavedsummary().add(createStLoanSavedSummary("U"));
            b237Response.getAstloansavedsummary().get(0).setCreditscoreno("1");
        }
        return b237Response;
    }

    public DetermineEligibleCustomerInstructionsRequest createEligibilityRequest(RequestHeader header, String customerIdentifier) {
        FinanceServiceArrangement productArrangement = createFinanceServiceArrangementForEligibility(customerIdentifier);
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = new DetermineEligibleCustomerInstructionsRequest();
        eligibilityRequest.setHeader(header);
        eligibilityRequest.getExistingProductArrangments().add(productArrangement);
        eligibilityRequest.setCustomerDetails(productArrangement.getPrimaryInvolvedParty());
        String instructionMnemonic = productArrangement.getAssociatedProduct().getInstructionDetails() != null ?
                productArrangement.getAssociatedProduct().getInstructionDetails().getInstructionMnemonic() : null;
        eligibilityRequest.getCandidateInstructions().add(instructionMnemonic);
        eligibilityRequest.setArrangementType(productArrangement.getArrangementType());
        return eligibilityRequest;
    }

    public DetermineEligibleCustomerInstructionsResponse createEligibilityResponse(String eligibilityStatus, String code, String description) {
        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        ProductEligibilityDetails productEligibilityDetails = new ProductEligibilityDetails();
        productEligibilityDetails.setIsEligible(eligibilityStatus);

        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("P_LOAN_STP");
        productEligibilityDetails.getProduct().add(new Product());
        productEligibilityDetails.getProduct().get(0).setInstructionDetails(instructionDetails);

        if (null != code) {
            ReasonCode reasonCode = new ReasonCode();
            reasonCode.setCode(code);
            reasonCode.setDescription(description);
            productEligibilityDetails.getDeclineReasons().add(reasonCode);
        }
        response.getProductEligibilityDetails().add(productEligibilityDetails);
        return response;
    }

    public RetrieveProductArrangementsRequest createRetrieveProductArrangementsRequest() {
        RetrieveProductArrangementsRequest retrieveProductArrangementsRequest = new RetrieveProductArrangementsRequest();

        com.lloydstsb.schema.enterprise.lcsm_arrangement.ProductArrangement searchCriteria = new com.lloydstsb.schema.enterprise.lcsm_arrangement.ProductArrangement();
        ArrangementType arrangementType = new ArrangementType();
        arrangementType.setName("SAVED_LOAN");
        searchCriteria.setHasArrangementType(arrangementType);

        InvolvedPartyRole involvedPartyRole = new InvolvedPartyRole();
        involvedPartyRole.setType(new InvolvedPartyRoleType());
        involvedPartyRole.getType().setName("CUSTOMER");
        involvedPartyRole.setObjectReference(new ObjectReference());
        involvedPartyRole.getObjectReference().setKeyGroupType("ProductArrangement");
        involvedPartyRole.getObjectReference().getAlternateId().addAll(getAlternateIdListForRetrieveProductArrangements());
        searchCriteria.getRoles().add(involvedPartyRole);

        retrieveProductArrangementsRequest.setSearchCriteria(searchCriteria);
        return retrieveProductArrangementsRequest;
    }

    public RetrieveProductArrangementsResponse createRetrieveProductArrangementsResponse() {
        RetrieveProductArrangementsResponse retrieveProductArrangementsResponse = new RetrieveProductArrangementsResponse();

        ResponseHeader responseHeader = new ResponseHeader();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setExtraConditions(getExtraConditions());
        responseHeader.setResultCondition(resultCondition);

        retrieveProductArrangementsResponse.setResponseHeader(responseHeader);
        return retrieveProductArrangementsResponse;
    }

    public RetrieveProductArrangementsResponse createRetrieveProductArrangementsResponseWithDuplicateSavedLoan() {
        RetrieveProductArrangementsResponse retrieveProductArrangementsResponse = new RetrieveProductArrangementsResponse();

        ResponseHeader responseHeader = new ResponseHeader();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setExtraConditions(getExtraConditions());
        responseHeader.setResultCondition(resultCondition);
        retrieveProductArrangementsResponse.setResponseHeader(responseHeader);

        com.lloydstsb.schema.enterprise.lcsm_arrangement.FinanceServiceArrangement financeServiceArrangement = new com.lloydstsb.schema.enterprise.lcsm_arrangement.FinanceServiceArrangement();
        financeServiceArrangement.setFinanceServiceArrangementStatus(FinanceServiceArrangementStatus.LOANAPPSTATUS_BEING_PROCESSED);

        ObjectReference objectReference = new ObjectReference();
        AlternateId alternateId = new AlternateId();
        alternateId.setAttributeString("CREDIT_SCORE_NUMBER");
        alternateId.setValue("1");
        objectReference.getAlternateId().add(alternateId);
        financeServiceArrangement.setObjectReference(objectReference);

        retrieveProductArrangementsResponse.getRetrievedProductArrangements().add(financeServiceArrangement);
        return retrieveProductArrangementsResponse;
    }

    public DetermineCustomerEligibleProductsRequest createDetermineCustomerEligibleProductsRequest() {
        DetermineCustomerEligibleProductsRequest determineCustomerEligibleProductsRequest = new DetermineCustomerEligibleProductsRequest();
        com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.Customer customer = new com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.Customer();

        com.lloydstsb.schema.enterprise.lcsm_common.wz.ObjectReference objectReference = new com.lloydstsb.schema.enterprise.lcsm_common.wz.ObjectReference();
        objectReference.setKeyGroupType("Customer");
        objectReference.getAlternateId().add(getAlternateIdForDetermineCustomerEligibleProducts("PARTY_IDENTIFIER", "+00090001232"));
        objectReference.getAlternateId().get(0).setSourceLogicalId("T");
        objectReference.getAlternateId().add(getAlternateIdForDetermineCustomerEligibleProducts("CUSTOMER_IDENTIFIER", "456662112"));
        objectReference.getAlternateId().get(1).setSourceLogicalId("T");
        customer.setObjectReference(objectReference);

        customer.getHasObjectConditions().addAll(getHasObjectConditions());

        determineCustomerEligibleProductsRequest.setCustomer(customer);
        return determineCustomerEligibleProductsRequest;
    }

    public DetermineCustomerEligibleProductsRequest createDetermineCustomerEligibleProductsRequestWithCustomerIdentifierNull() {
        DetermineCustomerEligibleProductsRequest determineCustomerEligibleProductsRequest = new DetermineCustomerEligibleProductsRequest();
        com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.Customer customer = new com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.Customer();

        com.lloydstsb.schema.enterprise.lcsm_common.wz.ObjectReference objectReference = new com.lloydstsb.schema.enterprise.lcsm_common.wz.ObjectReference();
        objectReference.setKeyGroupType("Customer");
        objectReference.getAlternateId().add(getAlternateIdForDetermineCustomerEligibleProducts("PARTY_IDENTIFIER", "+00090001232"));
        objectReference.getAlternateId().get(0).setSourceLogicalId("T");
        objectReference.getAlternateId().add(getAlternateIdForDetermineCustomerEligibleProducts("CUSTOMER_IDENTIFIER", "456662112"));
        objectReference.getAlternateId().get(1).setSourceLogicalId("T");
        customer.setObjectReference(objectReference);

        customer.getHasObjectConditions().addAll(getHasObjectConditions());

        determineCustomerEligibleProductsRequest.setCustomer(customer);
        return determineCustomerEligibleProductsRequest;
    }

    public DetermineCustomerEligibleProductsResponse createDetermineCustomerEligibleProductsResponse() {
        DetermineCustomerEligibleProductsResponse determineCustomerEligibleProductsResponse = new DetermineCustomerEligibleProductsResponse();

        ResponseHeader responseHeader = new ResponseHeader();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setExtraConditions(getExtraConditions());
        responseHeader.setResultCondition(resultCondition);
        determineCustomerEligibleProductsResponse.setResponseHeader(responseHeader);

        ResourceItem resourceItem = new ResourceItem();
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("SKIP_SPLASH_PAGE_FLAG");
        ruleCondition.setResult("Skip");
        ruleCondition.setUtiliseRule(true);
        resourceItem.getHasObjectConditions().add(ruleCondition);
        determineCustomerEligibleProductsResponse.getRelatedResourceItems().add(resourceItem);

        determineCustomerEligibleProductsResponse.setCustomerDetails(getCustomerDetails());
        determineCustomerEligibleProductsResponse.getEligibleProducts().add(getEligibleProduct());
        return determineCustomerEligibleProductsResponse;
    }

    public DetermineCustomerEligibleProductsResponse createDetermineCustomerEligibleProductsResponseWithReasonCode() {
        DetermineCustomerEligibleProductsResponse determineCustomerEligibleProductsResponse = new DetermineCustomerEligibleProductsResponse();

        ResponseHeader responseHeader = new ResponseHeader();

        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setExtraConditions(new ExtraConditions());
        resultCondition.getExtraConditions().getCondition().add(0, new Condition());
        resultCondition.getExtraConditions().getCondition().get(0).setReasonCode(823005);
        resultCondition.getExtraConditions().getCondition().get(0).setReasonText("External Business Error");

        responseHeader.setResultCondition(resultCondition);
        determineCustomerEligibleProductsResponse.setResponseHeader(responseHeader);
        return determineCustomerEligibleProductsResponse;
    }

    public E141Req createE141Request(String sortCode, String accountNumber) {
        E141Req e141Req = new E141Req();
        e141Req.setMaxRepeatGroupQy(0);
        e141Req.setCBSRequestGp(new CBSRequestGp());
        e141Req.getCBSRequestGp().setInputOfficerFlagStatusCd(0);
        e141Req.getCBSRequestGp().setOverrideDetailsCd(0);
        e141Req.setCBSAccountNoId(sortCode.concat(accountNumber));
        return e141Req;
    }

    public E141Resp createE141Response(List<Integer> indicators, String maxLimitAmt) {
        E141Resp e141Resp = new E141Resp();
        E141Result e141Result = new E141Result();

        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode(new Byte("0"));
        resultCondition.setReasonCode(0);
        e141Result.setResultCondition(resultCondition);
        e141Resp.setE141Result(e141Result);

        Indicator2Gp indicator2Gp = getIndicator2Gp(indicators);
        e141Resp.setIndicator2Gp(indicator2Gp);

        ISADetailsGp isaDetailsGp = new ISADetailsGp();
        isaDetailsGp.setISADetailsSubGp(new ISADetailsSubGp());
        isaDetailsGp.getISADetailsSubGp().setTaxYearTotalDepositAm(maxLimitAmt);
        isaDetailsGp.getISADetailsSubGp().setISARmnDpsAm("10");
        e141Resp.setISADetailsGp(isaDetailsGp);
        return e141Resp;
    }

    public RetrieveMandateAccessDetailsRequest createRetrieveMandateAccessDetailsRequest(String accType) {
        RetrieveMandateAccessDetailsRequest request = new RetrieveMandateAccessDetailsRequest();
        request.setMandate(new Mandate());
        request.getMandate().getConditionContext().add(new com.lloydstsb.schema.enterprise.lcsm_common.ConditionContext());
        com.lloydstsb.schema.enterprise.lcsm_common.RuleCondition ruleCondition = new com.lloydstsb.schema.enterprise.lcsm_common.RuleCondition();
        ruleCondition.setName("MandateEventDetail");
        com.lloydstsb.schema.enterprise.lcsm_common.RuleCondition subRule = new com.lloydstsb.schema.enterprise.lcsm_common.RuleCondition();
        subRule.setName("MandateEventsForArrangementType");
        ruleCondition.getSubrules().add(subRule);
        request.getMandate().getConditionContext().get(0).setCondition(ruleCondition);
        request.getMandate().setRegistrationType(new com.lloydstsb.schema.enterprise.lcsm_common.RegistrationType());
        request.getMandate().getRegistrationType().setName("MandateDetail");
        AlternateId alternateId = new AlternateId();
        alternateId.setAttributeString("ACCOUNT_TYPE");
        alternateId.setValue(accType);
        com.lloydstsb.schema.enterprise.lcsm_arrangement.FinanceServiceArrangement arrangement = new com.lloydstsb.schema.enterprise.lcsm_arrangement.FinanceServiceArrangement();
        arrangement.setProduct(new com.lloydstsb.schema.enterprise.lcsm_product.Product());
        arrangement.getProduct().setObjectReference(new com.lloydstsb.schema.enterprise.lcsm_common.ObjectReference());
        arrangement.getProduct().getObjectReference().getAlternateId().add(alternateId);
        request.getMandate().getArrangements().add(arrangement);
        return request;
    }

    public RetrieveMandateAccessDetailsResponse createRetrieveMandateAccessDetailsResponse(String evtType) {
        RetrieveMandateAccessDetailsResponse response = new RetrieveMandateAccessDetailsResponse();
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultCondition(new ResultCondition());
        response.getResponseHeader().getResultCondition().setExtraConditions(new ExtraConditions());
        response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().add(new Condition());
        response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().get(0).setReasonCode(0);
        response.setMandate(new Mandate());
        response.getMandate().setObjectReference(new ObjectReference());
        response.getMandate().getObjectReference().setSendMeMore(false);
        response.getMandate().getAccesspermissions().add(new AccessPermissions());
        response.getMandate().getAccesspermissions().get(0).getAccesspermissiondetail().add(new AccessPermissionDetail());
        AccessPermissionDetailType accessPermissionDetailType = new AccessPermissionDetailType();
        accessPermissionDetailType.setName(evtType);
        response.getMandate().getAccesspermissions().get(0).getAccesspermissiondetail().get(0).setAccessPermissionDetailType(accessPermissionDetailType);
        return response;
    }

    public void createPamReferenceData() {
        appStatusDao.save(applicationStatus);
        appStatusDao.findAll();
        applicationTypesDao.save(applicationTypes);
        applicationTypesDao.findAll();
        promotionChannelsDao.save(promotionChannels);
        promotionChannelsDao.findAll();
        productTypesDao.save(productTypes);
        productTypesDao.findAll();
        userTypesDao.save(userTypes);
        userTypesDao.findAll();
        brandsDao.save(brands);
        brandsDao.save(brands1);
        brandsDao.findAll();
        channelsDao.save(channels);
        channelsDao.findAll();
        parameterGroupsDao.save(parameterGroups);
        parameterGroupsDao.findAll();
        applicationParametersDao.save(applicationParameters);
        applicationParametersDao.findAll();
        applicationPartyRolesDao.save(roles1);
        applicationPartyRolesDao.save(roles2);
        applicationPartyRolesDao.findAll();
        kycStatusDao.save(kycStatus);
        kycStatusDao.findAll();
        approvalStatusDao.save(approvalStatus);
        promotionPartiesDao.save(createPromotionPartyList());
    }

    public int getApplicationsSize() {
        int count = 0;
        for (Applications applications : applicationsDao.findAll()) {
            count++;
        }
        return count;
    }

    public int getIndividualsSize() {
        int count = 0;
        for (Individuals individuals : individualsDao.findAll()) {
            count++;
        }
        return count;
    }

    public List<CustomerScore> createCustomerScoreList() {
        List<CustomerScore> customerScoreList = new ArrayList<>();
        CustomerScore customerScore = new CustomerScore();
        customerScore.setAssessmentType("CREDIT_SCORE");
        customerScore.setScoreIdentifier("STPL5819141218084429");
        customerScore.setScoreResult("ACCEPT");
        customerScoreList.add(customerScore);
        return customerScoreList;
    }

    public StHeader createStHeader() {
        StHeader stHeader = new StHeader();
        stHeader.setAcceptLanguage(null);
        stHeader.setChanctxt(BigInteger.ONE);
        stHeader.setChanid("IBL");
        stHeader.setChansecmode("PWD");
        stHeader.setCodosouid("306993");
        stHeader.setEncVerNo(BigInteger.ZERO);
        stHeader.setInboxidClient("GX");
        stHeader.setIpAddressCaller("10.240.147.57,null");
        stHeader.setSessionid("1vh3pw30tmuui1mmjywmquu5e8");
        stHeader.setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1.7) Gecko/20091221 Firefox/3.5.7");
        stHeader.setUseridAuthor("OX982035  ");

        StParty stParty = new StParty();
        stParty.setOcisid(new BigInteger("0"));
        stParty.setHost("T");
        stParty.setPartyid("+00434307833                  ");
        stHeader.setStpartyObo(stParty);

        return stHeader;
    }

    public PartyProdDataType createPartyProdDataType(long partyId) {
        PartyProdDataType partyProdDataType = new PartyProdDataType();
        partyProdDataType.setPartyId(partyId);
        partyProdDataType.setProdHeldId(1112990515);
        partyProdDataType.setPartyExtSysId((short) 4);
        partyProdDataType.setExtPartyIdTx("00090001232");
        partyProdDataType.setProdHeldRoleCd("001");
        partyProdDataType.setProdHeldRoleTx("Primary Product Holder");
        partyProdDataType.setAmdEffDt("15122015");
        partyProdDataType.setExpiryDt(" ");
        partyProdDataType.setPartyUpdAuditDt("15122015");
        partyProdDataType.setPartyUpdAuditTm("035247");
        partyProdDataType.setPartyCreateDt("15122015");
        partyProdDataType.setPartyCreateAuditDt("15122015");
        partyProdDataType.setPartyCreateAuditTm("035247");
        partyProdDataType.setPartyStatusCd("O");
        partyProdDataType.setPartyStatusTx("OPEN");
        partyProdDataType.setPersonCreateAuditDt("15122015");
        partyProdDataType.setPersonCreateAuditTm("035247");
        partyProdDataType.setBirthDt("22011988");
        partyProdDataType.setDOBAuditDt("15122015");
        partyProdDataType.setDOBAuditTm("035247");
        partyProdDataType.setDeathAuditDt("15122015");
        partyProdDataType.setDeathAuditTm("035247");
        partyProdDataType.setPartyTl("Mr");
        partyProdDataType.setSurname("Lockheart");
        partyProdDataType.setFirstIt("A");
        partyProdDataType.setSecondIt("M");
        partyProdDataType.setThirdIt("N");
        partyProdDataType.setFirstForeNm("Ariyana");
        partyProdDataType.setSecondForeNm("Myrtle");
        partyProdDataType.setThirdForeNm("Nella");
        partyProdDataType.setNameAuditDt("15122015");
        partyProdDataType.setNameAuditTm("035247");
        partyProdDataType.setGenderCd("M");
        partyProdDataType.setGenderAuditDt("15122015");
        partyProdDataType.setGenderAuditTm("035247");
        partyProdDataType.setArmedForcesAuditDt("15122015");
        partyProdDataType.setArmedForcesAuditTm("035247");
        partyProdDataType.setCorresSalutatnTx("Dear Mr Lockheart");
        partyProdDataType.setSalutatnAuditDt("15122015");
        partyProdDataType.setSalutatnAuditTm("035247");

        PtyUpdateAuditType auditType = new PtyUpdateAuditType();
        auditType.setExtSysId((short) 81);
        auditType.setExtUserIdTx("8705906");
        auditType.setExtLocIdTx("0000777141");
        partyProdDataType.setPtyUpdateAudit(auditType);

        ShortAddressData shortAddressData = new ShortAddressData();
        shortAddressData.setAddressLine1Tx("Troy");
        shortAddressData.setAddressStatusCd("001");
        shortAddressData.setAddressStatusCodeTx("Active");
        shortAddressData.setPostCd("SE1  9EQ");
        partyProdDataType.setShortAddressData(shortAddressData);
        partyProdDataType.setAddressAuditData(createAddressAuditDataType());
        return partyProdDataType;
    }

    public List<AffiliateDetails> createAffiliateDetailsList() {
        List<AffiliateDetails> affiliateDetailsList = new ArrayList<>();
        AffiliateDetails affiliateDetails = new AffiliateDetails();
        affiliateDetails.setAffiliateIdentifier("000");
        affiliateDetails.setAffiliateDescription("Public Site");
        affiliateDetails.setAffliateAddress(new lib_sim_bo.businessobjects.UnstructuredAddress());
        affiliateDetails.getAffliateAddress().setAddressLine1("14 FURTHERWICK ROAD ");
        affiliateDetails.getAffliateAddress().setAddressLine2("CANVEY ISLAND ESSEX");
        affiliateDetails.getAffliateAddress().setAddressLine6("LONDON");
        affiliateDetails.getAffliateAddress().setAddressLine8("United Kingdom");
        affiliateDetails.getAffliateAddress().setPostCode("SS8 7AE");
        affiliateDetails.getAffliateAddress().setPointSuffix("1B");
        affiliateDetails.setIsCreditIntermediary(false);
        affiliateDetailsList.add(affiliateDetails);
        return affiliateDetailsList;
    }

    public CBSAppGrp createCBSAppGroupFromSortCode(String sortCode) {
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        if (sortCodeAppGrpMap.containsKey(sortCode)) {
            cbsAppGrp.setCBSApplicationGroupNumber(sortCodeAppGrpMap.get(sortCode));
        } else {
            cbsAppGrp.setCBSApplicationGroupNumber("01");
        }
        return cbsAppGrp;
    }

    private FinanceServiceArrangement createFinanceServiceArrangementForEligibility(String customerIdentifier) {
        FinanceServiceArrangement productArrangement = createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().addAll(createPostalAddressList());
        productArrangement.getPrimaryInvolvedParty().setSourceSystemId("3");
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(customerIdentifier);
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().addAll(createCustomerScoreList());
        productArrangement.getPrimaryInvolvedParty().setCidPersID("+00090001232");
        productArrangement.getPrimaryInvolvedParty().setCustomerSegment("1");
        productArrangement.getPrimaryInvolvedParty().setCustomerNumber("77714600421506");
        productArrangement.getPrimaryInvolvedParty().setCbsCustomerNumber("77714600421506");
        productArrangement.getPrimaryInvolvedParty().setNewCustomerIndicator(false);
        productArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(false);
        productArrangement.getPrimaryInvolvedParty().getAuditData().add(createAuditData("ADDRESS"));
        productArrangement.getPrimaryInvolvedParty().getAuditData().add(createAuditData("PARTY_EVIDENCE"));
        productArrangement.getPrimaryInvolvedParty().getAuditData().add(createAuditData("ADDRESS_EVIDENCE"));
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setIsStaffMember(false);
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setResidentialStatus("000");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setMaritalStatus("001");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setGender("001");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setEmploymentStatus("023");

        IndividualName individualName = new IndividualName();
        individualName.setFirstName("Ariyana");
        individualName.setLastName("Lockheart");
        individualName.setPrefixTitle("Mr");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(individualName);

        productArrangement.getAssociatedProduct().setProductIdentifier("1");
        productArrangement.getAssociatedProduct().setBrandName("LTB");
        productArrangement.getAssociatedProduct().setIPRTypeCode(" ");
        productArrangement.getAssociatedProduct().setRoleCode("001");
        productArrangement.getAssociatedProduct().setStatusCode("001");
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        productArrangement.getAssociatedProduct().setAmendmentEffectiveDate(datatypeFactory.newXMLGregorianCalendar("2015-12-15T06:40:56.046Z"));

        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode("00004");
        extSysProdIdentifier.setProductIdentifier("2071776000");
        productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().add(0, extSysProdIdentifier);

        productArrangement.getAssociatedProduct().getProductoffer().add(0, new ProductOffer());
        productArrangement.getAssociatedProduct().getProductoffer().get(0).setStartDate(datatypeFactory.newXMLGregorianCalendar("2015-12-15T06:40:56.046Z"));
        productArrangement.getAssociatedProduct().setProductName("Classic");
        productArrangement.getAssociatedProduct().setProductType("1");
        productArrangement.getAssociatedProduct().setExternalProductHeldIdentifier("7771460318226800000");
        productArrangement.getAssociatedProduct().setExtPartyIdTx("77714600421506");
        return productArrangement;
    }

    private ProductPartyData createProductPartyData() {
        ProductPartyData productPartyData = new ProductPartyData();
        productPartyData.setPartyId(0L);
        productPartyData.setIPRTypeCd(" ");
        productPartyData.setProductHeldOpenDt("15122015");
        productPartyData.setProdHeldId(1112990515);
        productPartyData.setExtSysId((short) 4);
        productPartyData.setExtProdHeldIdTx("7771460318226800000");
        productPartyData.setExtProdIdTx("2071776000");
        productPartyData.setProdGroupId(1);
        productPartyData.setProdGroupMcTx(" ");
        productPartyData.setCBSAccountTypeCd("06");
        productPartyData.setProductId(23767);
        productPartyData.setBusAcTypeCd((short) 1);
        productPartyData.setJointSignCd((short) 0);
        productPartyData.setProdHeldStatusCd("001");
        productPartyData.setExtPartyIdTx("77714600421506");
        productPartyData.setProductHeldClosedDt("00000000");
        productPartyData.setSellerLegalEntCd("VTB");
        productPartyData.setSellerCompanyDs("TSB Bank Plc");
        productPartyData.setProdHeldRoleCd("001");
        productPartyData.setProdHeldRoleTx("Primary Product Holder");
        productPartyData.setProdHeldRoleExpiryDt("00000000");
        productPartyData.setAuditDt("15122015");
        productPartyData.setAuditTm("035247");
        productPartyData.setAmdEffDt("15122015");
        productPartyData.setExtProductDs("Classic");
        productPartyData.setManfctLegalEntCd("VTB");
        productPartyData.setSellerCompDs("TSB Bank Plc");
        productPartyData.setEmbeddedInsCd("001");
        productPartyData.setEmbeddedInsDs("None");
        productPartyData.setCAISNonPersonalProdDescIn(0);

        productPartyData.setProductHeldAudit(createProductHeldAudit());
        return productPartyData;
    }

    private PersonalData createPersonalData(String customerIdentifier, String partyIdentifier) {
        PersonalData personalData = new PersonalData();
        personalData.setPartyId(Long.parseLong(customerIdentifier));
        personalData.setCIDPersId(partyIdentifier);
        personalData.setPartyTypeCd("P  ");
        personalData.setBirthDt("22011988");
        personalData.setDeceasedIn("N");
        personalData.setGenderCd("M");
        personalData.setPartyTl("Mr");
        personalData.setSurname("Lockheart");
        personalData.setFirstForeNm("Ariyana");
        personalData.setSecondForeNm("Myrtle");
        personalData.setThirdForeNm("Nella");

        DOBAuditData dobAuditData = new DOBAuditData();
        dobAuditData.setExtSysId((short) 81);
        dobAuditData.setExtUserIdTx("8705906");
        dobAuditData.setExtLocIdTx("0000777141");
        dobAuditData.setAuditDt("15122015");
        dobAuditData.setAuditTm("035247");
        personalData.setDOBAuditData(dobAuditData);

        DeathAuditData deathAuditData = new DeathAuditData();
        deathAuditData.setExtSysId((short) 81);
        deathAuditData.setExtUserIdTx("8705906");
        deathAuditData.setExtLocIdTx("0000777141");
        deathAuditData.setAuditDt("15122015");
        deathAuditData.setAuditTm("035247");
        personalData.setDeathAuditData(deathAuditData);

        GenderAuditData genderAuditData = new GenderAuditData();
        genderAuditData.setExtSysId((short) 81);
        genderAuditData.setExtUserIdTx("8705906");
        genderAuditData.setExtLocIdTx("0000777141");
        genderAuditData.setAuditDt("15122015");
        genderAuditData.setAuditTm("035247");
        personalData.setGenderAuditData(genderAuditData);

        NameAuditData nameAuditData = new NameAuditData();
        nameAuditData.setExtSysId((short) 81);
        nameAuditData.setExtUserIdTx("8705906");
        nameAuditData.setExtLocIdTx("0000777141");
        nameAuditData.setAuditDt("15122015");
        nameAuditData.setAuditTm("035247");
        personalData.setNameAuditData(nameAuditData);
        return personalData;
    }

    private AddressData createAddressData() {
        AddressData addressData = new AddressData();
        addressData.setStuAddrId(865247859);
        addressData.setAmdEffDt("01122000");
        addressData.setAddrLifeCycleStageCd("C");
        addressData.setAddressTypeCd("001");
        addressData.setAddressTypeTx("Residential");
        addressData.setAddressStatusCd("001");
        addressData.setAddressStatusCdTx("Active");
        addressData.setAddressCareOfNm("LOCKHEART");

        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1Tx("Troy");
        unstructuredAddress.setAddressLine2Tx("20 Park Street");
        unstructuredAddress.setAddressLine3Tx("Hereshireford");
        unstructuredAddress.setAddressLine4Tx("Devon Meanchestor");
        unstructuredAddress.setAddressLine5Tx("Cornwall");
        unstructuredAddress.setAddressLine6Tx("London");
        unstructuredAddress.setAddressLine7Tx("UK");
        unstructuredAddress.setPostCd("SE1  9EQ");
        addressData.setUnstructuredAddress(unstructuredAddress);

        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNm("Troy");
        structuredAddress.setBuildingNo("20");

        AddressLinePaf addressLinePaf = new AddressLinePaf();
        addressLinePaf.setAddressLinePafTx("Park Street");
        AddressLinePaf addressLinePaf1 = new AddressLinePaf();
        addressLinePaf1.setAddressLinePafTx("Hereshireford");
        AddressLinePaf addressLinePaf2 = new AddressLinePaf();
        addressLinePaf2.setAddressLinePafTx("Devon Meanchestor");

        structuredAddress.getAddressLinePaf().add(addressLinePaf);
        structuredAddress.getAddressLinePaf().add(addressLinePaf1);
        structuredAddress.getAddressLinePaf().add(addressLinePaf2);
        structuredAddress.setAddressDistrictNm("Cornwall");
        structuredAddress.setAddressPostTownNm("London");
        structuredAddress.setAddressCountyNm("UK");
        structuredAddress.setInPostCd("9EQ");
        structuredAddress.setDelivPointSuffixCd("8K");
        addressData.setStructuredAddress(structuredAddress);

        AddressAuditData addressAuditData = new AddressAuditData();
        addressAuditData.setExtSysId((short) 81);
        addressAuditData.setExtUserIdTx("8705906");
        addressAuditData.setExtLocIdTx("0000777141");
        addressAuditData.setAuditDt("15122015");
        addressAuditData.setAuditTm("035247");
        addressData.setAddressAuditData(addressAuditData);
        return addressData;
    }

    private PhoneData createPhoneData(String partyPhoneTypeCd, String phoneTypeTx, String phoneSubscriberNo) {
        PhoneData phoneData = new PhoneData();
        phoneData.setPartyPhoneTypeCd(partyPhoneTypeCd);
        phoneData.setPhoneTypeTx(phoneTypeTx);
        phoneData.setPhoneDeviceTypeCd("004");
        phoneData.setPhoneDeviceTypeTx("Mobile Voice");
        phoneData.setPhoneSeqNo((short) 1);
        phoneData.setPhoneCountryCd("44");
        phoneData.setPhoneSubscriberNo(phoneSubscriberNo);
        phoneData.setContactPreferenceCd("001");
        phoneData.setContactPreferenceTx("Daytime");

        PhoneAudit phoneAudit = new PhoneAudit();
        phoneAudit.setExtSysId((short) 81);
        phoneAudit.setExtUserIdTx("8705906");
        phoneAudit.setExtLocIdTx("0000777141");
        phoneAudit.setAuditDt("15122015");
        phoneAudit.setAuditTm("035247");
        phoneData.setPhoneAudit(phoneAudit);
        return phoneData;
    }

    private EvidenceData createEvidenceData() {
        EvidenceData evidenceData = new EvidenceData();

        PartyEvid partyEvid = new PartyEvid();
        partyEvid.setPartyEvidTypeCd("040");
        partyEvid.setPartyEvidPurposeCd("010");
        partyEvid.setPartyEvidRefTx("Test");
        PartyEvidAuditData partyEvidAuditData = new PartyEvidAuditData();
        partyEvidAuditData.setExtSysId((short) 81);
        partyEvidAuditData.setExtUserIdTx("8705906");
        partyEvidAuditData.setExtLocIdTx("0000777141");
        partyEvidAuditData.setAuditDt("15122015");
        partyEvidAuditData.setAuditTm("035247");
        partyEvid.setPartyEvidAuditData(partyEvidAuditData);
        evidenceData.getPartyEvid().add(partyEvid);

        evidenceData.setPartyEvidImageId("000900012321");

        AddrEvid addrEvid = new AddrEvid();
        addrEvid.setAddrEvidTypeCd("248");
        addrEvid.setAddrEvidPurposeCd("009");
        addrEvid.setAddrEvidRefTx("Test");
        AddrEvidAuditData addrEvidAuditData = new AddrEvidAuditData();
        addrEvidAuditData.setExtSysId((short) 81);
        addrEvidAuditData.setExtUserIdTx("8705906");
        addrEvidAuditData.setExtLocIdTx("0000777141");
        addrEvidAuditData.setAuditDt("15122015");
        addrEvidAuditData.setAuditTm("035247");
        addrEvid.setAddrEvidAuditData(addrEvidAuditData);
        evidenceData.getAddrEvid().add(addrEvid);
        return evidenceData;
    }

    private KYCPartyData createKYCPartyData() {
        KYCPartyData kycPartyData = new KYCPartyData();

        CtyRes ctyRes = new CtyRes();
        ctyRes.setCountryOfResidCd("GBR");
        ctyRes.setCtyResAuditData(new CtyResAuditData());
        ctyRes.getCtyResAuditData().setExtSysId((short) 81);
        ctyRes.getCtyResAuditData().setExtUserIdTx("8705906");
        ctyRes.getCtyResAuditData().setExtLocIdTx("0000777141");
        ctyRes.getCtyResAuditData().setAuditDt("15122015");
        ctyRes.getCtyResAuditData().setAuditTm("035247");
        kycPartyData.setCtyRes(ctyRes);

        FrstNtn frstNtn = new FrstNtn();
        frstNtn.setFirstNationltyCd("GBR");
        frstNtn.setFrstNtnAuditData(new FrstNtnAuditData());
        frstNtn.getFrstNtnAuditData().setExtSysId((short) 81);
        frstNtn.getFrstNtnAuditData().setExtUserIdTx("8705906");
        frstNtn.getFrstNtnAuditData().setExtLocIdTx("0000777141");
        frstNtn.getFrstNtnAuditData().setAuditDt("15122015");
        frstNtn.getFrstNtnAuditData().setAuditTm("035247");
        kycPartyData.setFrstNtn(frstNtn);
        return kycPartyData;
    }

    private PartyNonCoreData createPartyNonCoreData() {
        PartyNonCoreData partyNonCoreData = new PartyNonCoreData();
        partyNonCoreData.setStaffIn("0");
        partyNonCoreData.setMaritalStatusCd((short) 1);
        partyNonCoreData.setMaritalStatusTx("SINGLE");
        partyNonCoreData.setEmploymentStatusCd((short) 23);
        partyNonCoreData.setEmploymentStatusTx("LBG Member of Staff");
        partyNonCoreData.setOccupationalRoleCd((short) 13);
        partyNonCoreData.setOccupationalRoleTx("PROFESSIONAL");
        partyNonCoreData.setResidentialStatusTx("UNKNOWN");

        NonCoreAuditData nonCoreAuditData = new NonCoreAuditData();
        nonCoreAuditData.setExtSysId((short) 81);
        nonCoreAuditData.setExtUserIdTx("8705906");
        nonCoreAuditData.setExtLocIdTx("0000777141");
        nonCoreAuditData.setAuditDt("15122015");
        nonCoreAuditData.setAuditTm("035247");
        partyNonCoreData.setNonCoreAuditData(nonCoreAuditData);
        return partyNonCoreData;
    }

    private KYCNonCorePartyData createKYCNonCorePartyData() {
        KYCNonCorePartyData kycNonCorePartyData = new KYCNonCorePartyData();
        kycNonCorePartyData.setEmployerNm("British Airways");
        kycNonCorePartyData.setCurrEmployerStartDt("29092006");
        kycNonCorePartyData.setPrevEmployerStartDt("01011999");
        kycNonCorePartyData.setPrevEmployerEndDt("27092006");

        NonCorePartyAuditData nonCorePartyAuditData = new NonCorePartyAuditData();
        nonCorePartyAuditData.setExtSysId((short) 81);
        nonCorePartyAuditData.setExtUserIdTx("8705906");
        nonCorePartyAuditData.setExtLocIdTx("0000777141");
        nonCorePartyAuditData.setAuditDt("15122015");
        nonCorePartyAuditData.setAuditTm("035247");
        kycNonCorePartyData.setNonCorePartyAuditData(nonCorePartyAuditData);
        return kycNonCorePartyData;
    }

    private AuditData createAuditData(String auditType) {
        AuditData auditData = new AuditData();
        auditData.setAuditType(auditType);
        auditData.setAuditDate("15122015");
        auditData.setAuditTime("035247");
        return auditData;
    }

    private List<PostalAddress> createPostalAddressList() {
        List<PostalAddress> postalAddressList = new ArrayList<>();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setEvidenceTypeCode("001");
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("1500");
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        postalAddress.setEffectiveFrom(datatypeFactory.newXMLGregorianCalendar("2000-12-01T06:40:56.046Z"));
        postalAddress.setStructuredAddress(createStructuredAddress());
        postalAddressList.add(postalAddress);
        return postalAddressList;
    }

    private Product createAssociatedProduct() {
        Product associatedProduct = new Product();
        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("P_LOAN_STP");
        associatedProduct.setInstructionDetails(instructionDetails);
        return associatedProduct;
    }

    private Channel createInitiatedThrough() {
        Channel initiatedThrough = new Channel();
        initiatedThrough.setChannelCode("004");
        initiatedThrough.setSubChannelCode("001");
        return initiatedThrough;
    }

    private Customer createInvolvedParty(String birthDate, String existingSortCode, String existingAccountNo) {
        return new InvolvedPartyBuilder().isPlayedBy(createIsPlayedBy(birthDate)).existingSortCode(existingSortCode).existingAccountNumber(existingAccountNo).
                userType("1001").internalUserIdentifier("stploan_user").partyRole("0001").build();
    }

    private Individual createIsPlayedBy(String birthDate) {
        Individual isPlayedBy = new Individual();
        try {
            isPlayedBy.setBirthDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(birthDate));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        isPlayedBy.getIndividualName().add(0, new IndividualName());
        return isPlayedBy;
    }

    private StLoanSavedSummary createStLoanSavedSummary(String loanAppStatus) {
        StLoanSavedSummary stLoanSavedSummary = new StLoanSavedSummary();
        stLoanSavedSummary.setLoanappstatus(loanAppStatus);
        stLoanSavedSummary.setCreditscoreno("2");
        return stLoanSavedSummary;
    }

    private StLoanProduct createStLoanProduct() {
        StLoanProduct stLoanProduct = new StLoanProduct();
        stLoanProduct.setLoanprodid(343);
        stLoanProduct.setLoanprodtxt("TSB PERSONAL LOAN");
        stLoanProduct.setInsuranceavail("O");
        stLoanProduct.setCurrencycode("GBP");
        stLoanProduct.setAmtMinLoan(BigDecimal.valueOf(1000));
        stLoanProduct.setAmtMaxLoan(BigDecimal.valueOf(25000));
        stLoanProduct.setLoantermMin(BigInteger.valueOf(12));
        stLoanProduct.setLoantermMax(BigInteger.valueOf(84));
        stLoanProduct.setLoantermMinDefer(BigInteger.ZERO);
        stLoanProduct.setLoantermMaxDefer(BigInteger.ZERO);
        stLoanProduct.setLoantermMaxRepaymentHol(BigInteger.ZERO);
        stLoanProduct.setLoantermMinRepaymentHol(BigInteger.ZERO);
        stLoanProduct.setUrltxtDisplay("Not");
        stLoanProduct.setUrltxtURL("Not");
        stLoanProduct.setStloancharges(createStLoanCharges());
        return stLoanProduct;
    }

    private StLoanCharges createStLoanCharges() {
        StLoanCharges stLoanCharges = new StLoanCharges();
        stLoanCharges.setAmtLetterCharge(BigDecimal.valueOf(25));
        stLoanCharges.setNDaysIntCharge(58);
        stLoanCharges.setAmtMaxCharge(BigDecimal.valueOf(250));
        stLoanCharges.setAmtAdminCharge(BigDecimal.valueOf(1));
        stLoanCharges.setLoantermExemptStart(BigInteger.valueOf(30));
        stLoanCharges.setLoantermExemptEnd(BigInteger.valueOf(90));
        return stLoanCharges;
    }

    private List<PromotionParties> createPromotionPartyList() {
        List<PromotionParties> promotionPartiesList = new ArrayList<>();
        PromotionParties promotionParties = new PromotionParties();
        promotionParties.setId(1l);
        promotionParties.setName("Public Site");
        promotionParties.setIsCreditIntermediary("N");
        promotionParties.setAffiliateId("000");
        promotionParties.setEnabled("Y");
        promotionParties.setApprovalStatus(approvalStatusDao.findOne("001"));
        promotionParties.setIsNetwork('N');
        promotionParties.setLockId(Long.valueOf("0"));
        promotionParties.setPromotionPartyAddresseses(createPromotionPartyAddressesSet(promotionParties));
        promotionPartiesList.add(promotionParties);
        return promotionPartiesList;
    }

    private Set<PromotionPartyAddresses> createPromotionPartyAddressesSet(PromotionParties promotionParties) {
        Set<PromotionPartyAddresses> promotionPartyAddressesSet = new HashSet<>();
        PromotionPartyAddresses promotionPartyAddresses = new PromotionPartyAddresses();
        promotionPartyAddresses.setId(1l);
        promotionPartyAddresses.setPromotionParties(promotionParties);
        promotionPartyAddresses.setStreetAddresses(createStreetAddresses());
        promotionPartyAddresses.setLockId(Long.valueOf("0"));
        promotionPartyAddressesSet.add(promotionPartyAddresses);
        return promotionPartyAddressesSet;
    }

    private StreetAddresses createStreetAddresses() {
        StreetAddresses streetAddresses = new StreetAddresses();
        streetAddresses.setAddressLine1("14 FURTHERWICK ROAD ");
        streetAddresses.setAddressLine2("CANVEY ISLAND ESSEX");
        streetAddresses.setAddressLine3(null);
        streetAddresses.setCity("LONDON");
        streetAddresses.setCountry("United Kingdom");
        streetAddresses.setCounty(null);
        streetAddresses.setPostCode("SS8 7AE");
        streetAddresses.setDeliveryPointSuffix("1B");
        streetAddressesDao.save(streetAddresses);
        return streetAddresses;
    }

    private AddressAuditDataType createAddressAuditDataType() {
        AddressAuditDataType addressAuditDataType = new AddressAuditDataType();
        addressAuditDataType.setExtSysId((short) 81);
        addressAuditDataType.setAuditDt("15122015");
        addressAuditDataType.setAuditTm("035247");
        addressAuditDataType.setExtUserIdTx("8705906");
        addressAuditDataType.setExtLocIdTx("0000777141");
        addressAuditDataType.setAmdEffDt("01122000");
        return addressAuditDataType;
    }

    private ProductHeldAudit createProductHeldAudit() {
        ProductHeldAudit productHeldAudit = new ProductHeldAudit();
        productHeldAudit.setExtSysId((short) 4);
        productHeldAudit.setAuditDt("15122015");
        productHeldAudit.setAuditTm("035247");
        productHeldAudit.setExtUserIdTx("8705906");
        productHeldAudit.setExtLocIdTx("0000777146");
        return productHeldAudit;
    }

    private lib_sim_bo.businessobjects.StructuredAddress createStructuredAddress() {
        lib_sim_bo.businessobjects.StructuredAddress structuredAddress = new lib_sim_bo.businessobjects.StructuredAddress();
        structuredAddress.setBuilding("Troy");
        structuredAddress.setBuildingNumber("20");
        structuredAddress.getAddressLinePAFData().add("Park Street");
        structuredAddress.getAddressLinePAFData().add("Hereshireford");
        structuredAddress.getAddressLinePAFData().add("Devon Meanchestor");
        structuredAddress.setDistrict("Cornwall");
        structuredAddress.setPostTown("London");
        structuredAddress.setCountry("UK");
        structuredAddress.setPostCodeOut("SE1");
        structuredAddress.setPostCodeIn("9EQ");
        structuredAddress.setPointSuffix("8K");
        structuredAddress.setCounty("UK");
        return structuredAddress;
    }

    private List<AlternateId> getAlternateIdListForRetrieveProductArrangements() {
        List<AlternateId> alternateIdList = new ArrayList<>();
        AlternateId alternateId1 = new AlternateId();
        alternateId1.setAttributeString("PARTY_IDENTIFIER");
        alternateId1.setSourceLogicalId("T");
        alternateId1.setValue("+00090001232");

        AlternateId alternateId2 = new AlternateId();
        alternateId2.setAttributeString("CUSTOMER_IDENTIFIER");
        alternateId2.setSourceLogicalId("T");
        alternateId2.setValue("456662112");

        alternateIdList.add(alternateId1);
        alternateIdList.add(alternateId2);
        return alternateIdList;
    }

    private List<RuleCondition> getHasObjectConditions() {
        List<RuleCondition> conditionList = new ArrayList<>();

        RuleCondition ruleCondition1 = new RuleCondition();
        ruleCondition1.setName("CustomerApplicationStatus");

        RuleCondition ruleCondition2 = new RuleCondition();
        ruleCondition2.setName("NEW_APPLICATION_FLAG");
        ruleCondition2.setResult("New");
        ruleCondition2.setUtiliseRule(true);

        conditionList.add(ruleCondition1);
        conditionList.add(ruleCondition2);
        return conditionList;
    }

    private ExtraConditions getExtraConditions() {
        ExtraConditions extraConditions = new ExtraConditions();
        Condition condition = new Condition();
        condition.setReasonCode(0);
        condition.setReasonText("");
        extraConditions.getCondition().add(condition);
        return extraConditions;
    }

    private com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.Customer getCustomerDetails() {
        com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.Customer customer = new com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.Customer();

        com.lloydstsb.schema.enterprise.lcsm_common.wz.ObjectReference objectReference = new com.lloydstsb.schema.enterprise.lcsm_common.wz.ObjectReference();
        objectReference.setKeyGroupType("CUSTOMER");
        objectReference.getAlternateId().add(0, getAlternateIdForDetermineCustomerEligibleProducts("CUSTOMER_IDENTIFIER", "456662112"));
        objectReference.getAlternateId().add(1, getAlternateIdForDetermineCustomerEligibleProducts("PartyPersonalId", "+00090001232"));
        objectReference.getAlternateId().add(2, getAlternateIdForDetermineCustomerEligibleProducts("CUSTOMER_IDENTIFIER", "77714600421506"));
        objectReference.getAlternateId().get(2).setState(AlternateIdState.REFERENCED);
        customer.setObjectReference(objectReference);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("ELECTRONIC_SIGNATURE_AVAILABLE_FLAG");
        ruleCondition.setUtiliseRule(true);
        customer.getHasConditions().add(ruleCondition);

        com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.CustomerScore customerScore = new com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.CustomerScore();
        customerScore.setObjectReference(getObjectReferenceForDetermineCustomerEligibleProducts("CustomerScore", "CREDIT_SCORE_NUMBER", "STPL5819141218084429"));
        customer.setCreditRating(customerScore);
        return customer;
    }

    private com.lloydstsb.schema.enterprise.lcsm_product.wz.Product getEligibleProduct() {
        com.lloydstsb.schema.enterprise.lcsm_product.wz.Product eligibleProduct = new com.lloydstsb.schema.enterprise.lcsm_product.wz.Product();

        eligibleProduct.setObjectReference(getObjectReferenceForDetermineCustomerEligibleProducts("Product", "PRODUCT_IDENTIFIER", "343"));
        eligibleProduct.setMaximumAmount(new CurrencyAmount());
        eligibleProduct.getMaximumAmount().setTheCurrencyAmount(BigDecimal.valueOf(25000));
        eligibleProduct.setMinimumAmount(new CurrencyAmount());
        eligibleProduct.getMinimumAmount().setTheCurrencyAmount(BigDecimal.valueOf(1000));
        eligibleProduct.setMaximumTerm("84");
        eligibleProduct.setMinimumTerm("12");
        eligibleProduct.setName("TSB PERSONAL LOAN");

        ProductComponentDetails productComponentDetails = new ProductComponentDetails();
        productComponentDetails.setSubcomponent(new com.lloydstsb.schema.enterprise.lcsm_product.wz.Product());
        productComponentDetails.getSubcomponent().setName("Currency");
        productComponentDetails.getSubcomponent().setObjectReference(getObjectReferenceForDetermineCustomerEligibleProducts("Product", "CurrencyCode", "GBP"));
        eligibleProduct.getSubcomponentDetails().add(productComponentDetails);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("INSURANCE_AVAILABLE");
        eligibleProduct.getConditions().add(ruleCondition);
        eligibleProduct.getConditions().add(getFeeCondition("LETTER_CHARGE_AMOUNT", BigDecimal.valueOf(25)));
        eligibleProduct.getConditions().add(getFeeCondition("DAYS_INTEREST_CHARGE", BigDecimal.valueOf(58)));
        eligibleProduct.getConditions().add(getFeeCondition("MAXIMUM_CHARGE_AMOUNT", BigDecimal.valueOf(250)));
        eligibleProduct.getConditions().add(getFeeCondition("ADMINISTRATION_CHARGE_AMOUNT", BigDecimal.valueOf(1)));
        eligibleProduct.getConditions().add(getLimitCondition("LOAN_TERM_EXEMPT_START", BigDecimal.valueOf(30)));
        eligibleProduct.getConditions().add(getLimitCondition("LOAN_TERM_EXEMPT_END", BigDecimal.valueOf(90)));
        eligibleProduct.getConditions().add(getAttributeCondition("URL_TEXT_DISPLAY", "Not"));
        eligibleProduct.getConditions().add(getAttributeCondition("URL_TEXT", "Not"));
        eligibleProduct.getConditions().add(getLimitCondition("LOAN_TERM_MINIMUM_DEFERRABLE", BigDecimal.valueOf(0)));
        eligibleProduct.getConditions().add(getLimitCondition("LOAN_TERM_MAXIMUM_DEFERRABLE", BigDecimal.valueOf(0)));
        eligibleProduct.getConditions().add(getLimitCondition("LOAN_TERM_MINIMUM_REPAYMENT_HOLIDAY", BigDecimal.valueOf(0)));
        eligibleProduct.getConditions().add(getLimitCondition("LOAN_TERM_MAXIMUM_REPAYMENT_HOLIDAY", BigDecimal.valueOf(0)));
        return eligibleProduct;
    }

    private com.lloydstsb.schema.enterprise.lcsm_common.wz.ObjectReference getObjectReferenceForDetermineCustomerEligibleProducts(String keyGroupType, String attributeString, String value) {
        com.lloydstsb.schema.enterprise.lcsm_common.wz.ObjectReference objectReference = new com.lloydstsb.schema.enterprise.lcsm_common.wz.ObjectReference();
        objectReference.setKeyGroupType(keyGroupType);
        objectReference.getAlternateId().add(0, getAlternateIdForDetermineCustomerEligibleProducts(attributeString, value));
        return objectReference;
    }

    private com.lloydstsb.schema.enterprise.lcsm_common.wz.AlternateId getAlternateIdForDetermineCustomerEligibleProducts(String attributeString, String value) {
        com.lloydstsb.schema.enterprise.lcsm_common.wz.AlternateId alternateId = new com.lloydstsb.schema.enterprise.lcsm_common.wz.AlternateId();
        alternateId.setAttributeString(attributeString);
        alternateId.setValue(value);
        return alternateId;
    }

    private AttributeCondition getAttributeCondition(String name, String dataItem) {
        AttributeCondition attributeCondition = new AttributeCondition();
        attributeCondition.setName(name);
        attributeCondition.setDataItem(dataItem);
        return attributeCondition;
    }

    private FeeCondition getFeeCondition(String name, BigDecimal amount) {
        FeeCondition feeCondition = new FeeCondition();
        feeCondition.setName(name);

        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setTheCurrencyAmount(amount);
        feeCondition.setAmount(currencyAmount);
        return feeCondition;
    }

    private LimitCondition getLimitCondition(String name, BigDecimal valueNominator) {
        LimitCondition limitCondition = new LimitCondition();
        limitCondition.setName(name);
        limitCondition.setValueNominator(valueNominator);
        return limitCondition;
    }

    private Indicator2Gp getIndicator2Gp(List<Integer> indicators) {
        List<StandardIndicators2Gp> standardIndicators2Gps = new ArrayList<>();
        Indicator2Gp indicator2Gp = new Indicator2Gp();
        for (Integer indicator : indicators) {
            StandardIndicators2Gp standardIndicators2Gp = new StandardIndicators2Gp();
            standardIndicators2Gp.setIndicator2Cd(indicator);
            standardIndicators2Gps.add(standardIndicators2Gp);

        }
        indicator2Gp.getStandardIndicators2Gp().addAll(standardIndicators2Gps);
        return indicator2Gp;
    }

    private HashMap<String, String> generateSortCodeAppGrpMap() {
        sortCodeAppGrpMap = new HashMap<String, String>();
        sortCodeAppGrpMap.put("35345", "01");
        sortCodeAppGrpMap.put("2342345", "01");
        sortCodeAppGrpMap.put("124234", "07");
        sortCodeAppGrpMap.put("24345", "07");
        sortCodeAppGrpMap.put("456457", "09");
        sortCodeAppGrpMap.put("1313124", "09");
        sortCodeAppGrpMap.put("772519", "01");
        return sortCodeAppGrpMap;
    }

    private String getCBSAppGrpForSortCode(String sortCode) {
        return sortCodeAppGrpMap.get(sortCode);
    }
}