package com.lloydsbanking.salsa.apaloans;

import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ExtraConditions;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import com.lloydstsb.schema.personal.serviceplatform.tms.CreateTaskResponse;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreationResponse;
import com.lloydstsb.schema.personal.serviceplatform.tms.v0001.TMSRoutingInformationBO;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public class TestDataHelper {
    public static final String TEST_RETAIL_CHANNEL_ID = "IBL";

    public static final String TEST_BUSINESS_TRANSACTION = "activateProductArrangement";

    public static final String TEST_INTERACTION_ID = "62bdfea6f48211e4add0e3a875e8c881";

    public final static String TEST_OCIS_ID = "1433933835";

    public static final String TEST_CUSTOMER_ID = "RD888225 ";

    public static final String TEST_MESSAGE_ID = "a0fn7kh3mtma1201i2z6psoe9";

    public static final String TEST_CONTACT_POINT_ID = "0000777505";

    public static final String INVALID_CONTACT_POINT_ID = "0000777";

    HeaderRetriever headerRetriever = new HeaderRetriever();

    @Autowired
    ApplicationStatusDao appStatusDao;

    @Autowired
    ApplicationTypesDao applicationTypesDao;

    @Autowired
    BrandsDao brandsDao;

    @Autowired
    ChannelsDao channelsDao;

    @Autowired
    PromotionChannelsDao promotionChannelsDao;

    @Autowired
    ProductTypesDao productTypesDao;

    @Autowired
    UserTypesDao userTypesDao;

    @Autowired
    ApplicationParametersDao applicationParametersDao;

    @Autowired
    ParameterGroupsDao parameterGroupsDao;

    @Autowired
    ReferralStatusDao referralStatusDao;

    @Transactional
    @Modifying
    public void cleanUp() {
        appStatusDao.deleteAll();
        applicationTypesDao.deleteAll();
        brandsDao.deleteAll();
        channelsDao.deleteAll();
        promotionChannelsDao.deleteAll();
        productTypesDao.deleteAll();
        userTypesDao.deleteAll();
        applicationParametersDao.deleteAll();
        parameterGroupsDao.deleteAll();
        referralStatusDao.deleteAll();
    }

    public RequestHeader createApaRequestHeader() {

        RequestHeaderBuilder requestHeaderBuilder = new RequestHeaderBuilder();
        return requestHeaderBuilder.businessTransaction(TEST_BUSINESS_TRANSACTION).channelId(TEST_RETAIL_CHANNEL_ID).interactionId(TEST_INTERACTION_ID).bapiInformation(TEST_RETAIL_CHANNEL_ID, TEST_INTERACTION_ID, TEST_OCIS_ID, "ns5").securityHeader("lgsm", TEST_CUSTOMER_ID).serviceRequest("ns4", TEST_BUSINESS_TRANSACTION, "10.245.182.105", TEST_MESSAGE_ID).contactPoint("ns4", "003", TEST_CONTACT_POINT_ID, "Internet Banking", "Browser", "127.0.0.1", "Customer").build();
    }

    public ActivateProductArrangementRequest createApaRequestForLoans() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangement());
        return activateProductArrangementRequest;
    }

    public DepositArrangement createDepositArrangement() {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementType("LRA");

        Product product = new Product();

        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("502");

        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);

        product.getAssociatedFamily().add(productFamily);
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Customer customer = getPrimaryInvolvedParty();

        depositArrangement.setPrimaryInvolvedParty(customer);
        depositArrangement.setIsJointParty(false);

        RuleCondition condition1 = new RuleCondition();
        condition1.setName("Occupation");
        condition1.setResult("Outside Worker;sss");

        RuleCondition condition2 = new RuleCondition();
        condition2.setName("Residential status");
        condition2.setResult("Tenant (Council)");

        RuleCondition condition3 = new RuleCondition();
        condition3.setName("TRIGGER");
        condition3.setResult("STRAIGHT_THROUGH_PROCESSING_ACTIVATE_PRODUCT_ARRANGEMENT");

        depositArrangement.getConditions().add(condition1);
        depositArrangement.getConditions().add(condition2);
        depositArrangement.getConditions().add(condition3);

        depositArrangement.setLoanRefinanceIndicator(false);
        return depositArrangement;
    }


    public Customer getPrimaryInvolvedParty() {
        Customer customer = new Customer();
        customer.setEmailAddress("GalaxyTestAccount02@LloydsTSB.co.uk");

        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("0505");

        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setPostCodeOut("HR7");
        structuredAddress.setPostCodeIn("4BS");
        postalAddress.setStructuredAddress(structuredAddress);

        customer.getPostalAddress().add(postalAddress);

        Individual individual = new Individual();

        IndividualName individualName = new IndividualName();
        individualName.setLastName("Qjllyb");
        individual.getIndividualName().add(individualName);

        customer.setIsPlayedBy(individual);
        customer.setCustomerIdentifier("1212054124");

        CustomerScore eidvScore = new CustomerScore();
        eidvScore.setScoreIdentifier("STPL8869150311080925");
        customer.getCustomerScore().add(eidvScore);
        customer.setUserType("1001");
        customer.setInternalUserIdentifier("WF414271  ");

        return customer;
    }

    public RequestHeader createApaRequestHeaderWithInvalidContactPoint() {
        RequestHeaderBuilder requestHeaderBuilder = new RequestHeaderBuilder();
        return requestHeaderBuilder.businessTransaction(TEST_BUSINESS_TRANSACTION).channelId(TEST_RETAIL_CHANNEL_ID).interactionId(TEST_INTERACTION_ID).bapiInformation(TEST_RETAIL_CHANNEL_ID, TEST_INTERACTION_ID, TEST_OCIS_ID, "ns5").securityHeader("lgsm", TEST_CUSTOMER_ID).serviceRequest("ns4", TEST_BUSINESS_TRANSACTION, "10.245.182.105", TEST_MESSAGE_ID).contactPoint("ns4", "003", INVALID_CONTACT_POINT_ID, "Internet Banking", "Browser", "127.0.0.1", "Customer").build();
    }

    public TaskCreationResponse createTaskCreationResponse() {
        TaskCreationResponse taskCreationResponse = new TaskCreationResponse();
        CreateTaskResponse createTaskResponse = new CreateTaskResponse();

        TMSRoutingInformationBO tmsRoutingInformationBO = new TMSRoutingInformationBO();
        tmsRoutingInformationBO.setTaskId(45);
        createTaskResponse.setTaskRoutingInformation(tmsRoutingInformationBO);
        com.lloydstsb.schema.infrastructure.soap.ResultCondition resultcondition = new com.lloydstsb.schema.infrastructure.soap.ResultCondition();
        resultcondition.setReasonCode(88);
        resultcondition.setSeverityCode((byte) 0);
        resultcondition.setReasonText("text");

        com.lloydstsb.schema.infrastructure.soap.ExtraConditions extraConditions = new ExtraConditions();
        resultcondition.setExtraConditions(extraConditions);

        createTaskResponse.setResultCondition(resultcondition);

        taskCreationResponse.setCreateTaskReturn(createTaskResponse);
        return taskCreationResponse;

    }

    public ContactPoint getContactPointFromRequestHeader(RequestHeader requestHeader) {
        return headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
    }

    public ServiceRequest getServiceRequestFromRequestHeader(RequestHeader requestHeader) {
        return headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders());
    }

    public SecurityHeaderType getSecurityHeaderTypeFromRequestHeader(RequestHeader requestHeader) {
        return headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
    }

    public BapiInformation getBapiInformationFromRequestHeader(RequestHeader requestHeader) {
        return headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders());
    }

    public void createPamReferenceData() {
        appStatusDao.save(new ApplicationStatus("1003", "Referred"));
        appStatusDao.save(new ApplicationStatus("1002", "Approved"));
        appStatusDao.save(new ApplicationStatus("1008", "Awaiting Referral Processing"));
        appStatusDao.findAll();
        applicationTypesDao.save(new ApplicationTypes("10001", "New Application"));
        applicationTypesDao.findAll();
        brandsDao.save(new Brands("LTB", "Lloyds"));
        brandsDao.findAll();
        channelsDao.save(new Channels("001", "Telephone"));
        channelsDao.findAll();
        promotionChannelsDao.save(new PromotionChannels("004", "Affiliates"));
        promotionChannelsDao.findAll();
        productTypesDao.save(new ProductTypes("101", "Savings Account"));
        productTypesDao.save(new ProductTypes("102", "Current Account"));
        productTypesDao.save(new ProductTypes("103", "Loan Account"));
        productTypesDao.findAll();

        userTypesDao.save(new UserTypes("1001", "Customer"));
        userTypesDao.findAll();

        parameterGroupsDao.save(new ParameterGroups("IB", "Internet Banking"));
        parameterGroupsDao.findAll();
        applicationParametersDao.save(new ApplicationParameters("100013", new ParameterGroups("IB")));
        applicationParametersDao.findAll();
        referralStatusDao.save(new ReferralStatus("PEN", "Pending"));
        referralStatusDao.save(new ReferralStatus("COM", "Completed"));
        referralStatusDao.findAll();

    }
}
