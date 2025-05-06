package com.lloydsbanking.salsa.apasa;

import com.infracast.schemas.emx.v10_2.EmxSendSmsRequest;
import com.infracast.schemas.emx.v10_2.EmxSendSmsResponse;
import com.infracast.schemas.emx.v10_2.EmxTxSmsMessageDetails;
import com.infracast.schemas.emx.v10_2.EmxTxSmsMessageResult;
import com.infracast.schemas.managedtext.v10_2.EmxStatus;
import com.infracast.schemas.managedtext.v10_2.EmxTemplateAttribsKVP;
import com.infracast.schemas.managedtext.v10_2.EmxTemplateValueKVP;
import com.infracast.schemas.managedtext.v10_2.EmxTxSmsMessageAttribs;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.converter.DepositArrangementToB765Request;
import com.lloydsbanking.salsa.apasa.service.fulfil.convert.E502RequestFactory;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydsbanking.salsa.soap.asm.f425.objects.*;
import com.lloydsbanking.salsa.soap.cbs.e032.objects.E032Resp;
import com.lloydsbanking.salsa.soap.cbs.e032.objects.E032Result;
import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Req;
import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Resp;
import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Result;
import com.lloydsbanking.salsa.soap.fs.account.StAccount;
import com.lloydsbanking.salsa.soap.fs.account.StHeader;
import com.lloydsbanking.salsa.soap.fs.application.StError;
import com.lloydsbanking.salsa.soap.fs.system.StParty;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Resp;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Result;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Resp;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Result;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Resp;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Result;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Resp;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Result;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Resp;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Result;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RecordDocumentMetaContentResponse;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RetrieveDocumentMetaContentResponse;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsResponse;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsResponse;
import com.lloydstsb.ib.wsbridge.account.StB765AAccCreateAccount;
import com.lloydstsb.ib.wsbridge.account.StB765BAccCreateAccount;
import com.lloydstsb.ib.wsbridge.account.StB766ARetrieveCBSAppGroup;
import com.lloydstsb.ib.wsbridge.account.StB766BRetrieveCBSAppGroup;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.system.StB555AWServMIS;
import com.lloydstsb.ib.wsbridge.system.StB555BWServMIS;
import com.lloydstsb.ib.wsbridge.system.TAudit;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import com.lloydstsb.schema.personal.serviceplatform.tms.CreateTaskResponse;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreationResponse;
import com.lloydstsb.schema.personal.serviceplatform.tms.v0001.TMSRoutingInformationBO;
import lib_sim_bo.businessobjects.*;
import lib_sim_bo.businessobjects.AccessToken;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.TelephoneNumber;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import lib_sim_communicationmanager.messages.SendCommunicationRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestDataHelper {
    public static final String TEST_RETAIL_CHANNEL_ID = "IBL";

    public static final String TEST_BUSINESS_TRANSACTION = "activateProductArrangement";

    public static final String TEST_INTERACTION_ID = "62bdfea6f48211e4add0e3a875e8c881";

    public final static String TEST_OCIS_ID = "1433933835";

    public static final String TEST_CUSTOMER_ID = "RD888225 ";

    public static final String TEST_MESSAGE_ID = "a0fn7kh3mtma1201i2z6psoe9";

    public static final String TEST_CONTACT_POINT_ID = "0000777505";

    public static final String NEW_APPLICATION_TYPE = "10001";

    public static final String GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER = "1";
    public static final String GALAXY_OAP_SOURCE_SYSTEM_IDENTIFIER = "3";

    public static final String INVALID_CONTACT_POINT_ID = "0000777";

    public static final String QUEUE_SMS_SWITCH = "SW_CM_Queue_SMS";

    public static final String QUEUE_EMAIL_SWITCH = "SW_CM_Queue_EM";

    public static final String DURABLE_MEDIUM_SWITCH = "SW_EnSTPPCAWcMl";
    public static final List<String> groupCodeList = Arrays.asList(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE, ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE);
    ApplicationForms applicationForm = new ApplicationForms(10002, "Ava Sales Question");
    ApplicationPartyRoles applicationPartyRoles = new ApplicationPartyRoles("0001", "Key Party");
    ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");
    ApplicationTypes applicationTypes = new ApplicationTypes("10001", "New Application");
    ProductTypes productTypes = new ProductTypes("102", "Current Account");
    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");
    ProductTypes productTypesSavings = new ProductTypes("101", "Savings Account");
    Brands brands = new Brands("LTB", "Lloyds");
    UserTypes userTypes = new UserTypes("1001", "Customer");
    PromotionChannels promotionChannels = new PromotionChannels("003", "Direct Mail");
    Channels channels = new Channels("004", "Internet");
    ParameterGroups parameterGroups1 = new ParameterGroups("CBS", "CBS Param Group");
    ParameterGroups parameterGroups2 = new ParameterGroups("ASM", "ASM Param Group");
    ApplicationParameters applicationParameters3 = new ApplicationParameters("100011", parameterGroups2, "ASM Product Family Id Selected");
    ApplicationParameters applicationParameters4 = new ApplicationParameters("100012", parameterGroups2, "Debit Card Risk Code");
    ApplicationParameters applicationParameters7 = new ApplicationParameters("100006", parameterGroups2, "ASM Approve");
    ApplicationParameters applicationParameters8 = new ApplicationParameters("100036", parameterGroups2, "CBS Customer Number");
    ApplicationParameters applicationParameters11 = new ApplicationParameters("100042", parameterGroups2, "New Customer Indicator");
    ApplicationParameters applicationParameters12 = new ApplicationParameters("100057", parameterGroups2, "Intend To Switch");
    ParameterGroups parameterGroups7 = new ParameterGroups("IB", "Internet Banking");
    ApplicationParameters applicationParameters1 = new ApplicationParameters("100009", parameterGroups7, "Instruction Mnemonic");
    ApplicationParameters applicationParameters2 = new ApplicationParameters("100037", parameterGroups7, "Debit Card Facility Required Indicator");
    ApplicationParameters applicationParameters6 = new ApplicationParameters("100005", parameterGroups7, "IB registration reference number");
    ApplicationParameters applicationParameters10 = new ApplicationParameters("100010", parameterGroups7, "Application Version Number");
    ParameterGroups parameterGroups8 = new ParameterGroups("OCIS", "OCIS Param Group");
    ApplicationParameters applicationParameters9 = new ApplicationParameters("100008", parameterGroups8, "OCIS Party Identifier");
    ParameterGroups parameterGroups3 = new ParameterGroups("CMAS", "CMAS Param Group");
    ParameterGroups parameterGroups4 = new ParameterGroups("PEGA", "PEGA Param Group");

    //ApplicationParameters applicationParameters5 = new ApplicationParameters("100004", parameterGroups6, "EIDV strength token");
    ParameterGroups parameterGroups5 = new ParameterGroups("CCD", "Call Credit Param Group");
    ParameterGroups parameterGroups6 = new ParameterGroups("EIDV", "EIDV Param Group");
    ApplicationPartyRoles applicationPartyRolesForPromoParty = new ApplicationPartyRoles("0003", "Promotional Party Affiliate");
    ApplicationPartyRoles roles = new ApplicationPartyRoles("0001", "Key Party");
    ApplicationPartyRoles rolesGuardian = new ApplicationPartyRoles("0005", "Secondary Party");
    ApprovalStatus approvalStatus = new ApprovalStatus();
    KycStatus kycStatus = new KycStatus();
    TelephoneAddressTypes telephoneAddressTypes = new TelephoneAddressTypes();
    ApplicationFeatureTypes applicationFeatureTypes1 = new ApplicationFeatureTypes("OD", "Overdraft");
    ApplicationFeatureTypes applicationFeatureTypes2 = new ApplicationFeatureTypes("CHQBK", "ChequeBook");
    ApplicationFeatureTypes applicationFeatureTypes3 = new ApplicationFeatureTypes("IBRA", "IB Registration Accepted");
    ApplicationFeatureTypes applicationFeatureTypes4 = new ApplicationFeatureTypes("BENPA", "Benefit Payments");
    ApplicationFeatureTypes applicationFeatureTypes5 = new ApplicationFeatureTypes("CIN", "Cinema");
    ApplicationFeatureTypes applicationFeatureTypes6 = new ApplicationFeatureTypes("IBRC", "IB Registration Completed");
    ApplicationFeatureTypes applicationFeatureTypes7 = new ApplicationFeatureTypes("ASA", "Account Switching Accepted");
    ApplicationFeatureTypes applicationFeatureTypes8 = new ApplicationFeatureTypes("ODPCI", "Date Pcci Viewed");
    ApplicationFeatureTypes applicationFeatureTypes9 = new ApplicationFeatureTypes("PCCI", "Date PCCI viewed");

    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter = new BapiHeaderToStHeaderConverter();
    @Autowired
    BrandsDao brandsDao;
    @Autowired
    DepositArrangementToB765Request depositArrangementToB765Request;
    @Autowired
    E502RequestFactory requestFactory;
    @Autowired
    ApplicationPartyRolesDao applicationPartyRolesDao;
    @Autowired
    KycStatusDao kycStatusDao;
    @Autowired
    TelephoneAddressTypesDao telephoneAddressTypesDao;
    @Autowired
    TelephoneAddressesDao telephoneAddressesDao;
    @Autowired
    ApprovalStatusDao approvalStatusDao;
    @Autowired
    DateFactory dateFactory;
    @Autowired
    PartyRelatedDao partyRelatedDao;
    @Autowired
    ParameterGroupsDao parameterGroupsDao;
    @Autowired
    ApplicationParametersDao applicationParametersDao;
    @Autowired
    ApplicationTypesDao applicationTypesDao;
    @Autowired
    UserTypesDao userTypesDao;
    @Autowired
    ApplicationsDao applicationsDao;
    @Autowired
    PromotionChannelsDao promotionChannelsDao;
    @Autowired
    ProductTypesDao productTypesDao;
    @Autowired
    ChannelsDao channelsDao;
    @Autowired
    ApplicationParameterValuesDao applicationParameterValuesDao;
    @Autowired
    ApplicationFeaturesDao applicationFeaturesDao;
    @Autowired
    ApplicationFeatureTypesDao applicationFeatureTypesDao;
    @Autowired
    PartyApplicationsDao partyApplicationsDao;
    @Autowired
    ApplicationStatusDao appStatusDao;
    @Autowired
    IndividualsDao individualsDao;
    @Autowired
    ApplicationFormsDao applicationFormsDao;
    @Autowired
    ReferralTeamsDao referralTeamsDao;
    @Autowired
    ReferralsDao referralsDao;
    @Autowired
    ApplicationsRelatedDao applicationsRelatedDao;
    @Autowired
    ApplicationRelationshipTypesDao applicationRelationshipTypesDao;
    @Autowired
    IndividualAddressesDao individualAddressesDao;
    @Autowired
    StreetAddressesDao streetAddressesDao;
    @Autowired
    ReferralStatusDao referralStatusDao;
    @Autowired
    AbandonDeclineReasonDao abandonDeclineReasonDao;
    HeaderRetriever headerRetriever = new HeaderRetriever();
    private HashMap<String, String> sortCodeAppGrpMap;


    public TestDataHelper() {
        generateSortCodeAppGrpMap();
    }

    public RequestHeader createApaRequestHeader() {

        RequestHeaderBuilder requestHeaderBuilder = new RequestHeaderBuilder();
        return requestHeaderBuilder.businessTransaction(TEST_BUSINESS_TRANSACTION).channelId(TEST_RETAIL_CHANNEL_ID).interactionId(TEST_INTERACTION_ID).bapiInformation(TEST_RETAIL_CHANNEL_ID, TEST_INTERACTION_ID, TEST_OCIS_ID, "ns5").securityHeader("lgsm", TEST_CUSTOMER_ID).serviceRequest("ns4", TEST_BUSINESS_TRANSACTION, "10.245.182.105", TEST_MESSAGE_ID).contactPoint("ns4", "003", TEST_CONTACT_POINT_ID, "Internet Banking", "Browser", "127.0.0.1", "Customer").build();
    }

    public ActivateProductArrangementRequest createApaRequestForSa() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangement(null));
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestForSaWithLifeStyleBenefitCode() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangement(null));
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("LIFE_STYLE_BENEFIT_CODE");
        ruleCondition.setResult("CIN");
        activateProductArrangementRequest.getProductArrangement().getConditions().add(ruleCondition);

        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestForSaFor502() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangementFor502(null));
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestForSaForOAPCase() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangement(null));
        activateProductArrangementRequest.getProductArrangement().setApplicationStatus("1007");
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_OAP_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public DepositArrangement createDepositArrangement(String arrangementId) {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementId(arrangementId);
        depositArrangement.setArrangementType("SA");

        Product product = new Product();
        product.setProductIdentifier("92");
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Customer customer = new Customer();
        customer.setIsRegistrationSelected(true);
        customer.setPassword("password1234");
        customer.setIsPlayedBy(new Individual());
        CustomerScore customerScore = new CustomerScore();
        customerScore.getAssessmentEvidence().add(new AssessmentEvidence());
        customer.getCustomerScore().add(customerScore);
        depositArrangement.setPrimaryInvolvedParty(customer);
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("007505");
        Organisation organisation = new Organisation();
        organisation.setChannel("LTB");
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);
        depositArrangement.setIsJointParty(false);
        depositArrangement.getReferral().add(new Referral());
        depositArrangement.setAccountDetails(new AccountDetails());
        depositArrangement.setApplicationType(NEW_APPLICATION_TYPE);
        depositArrangement.setIsSecondaryAccount(false);
        depositArrangement.setAccountNumber("08676168");
        return depositArrangement;
    }

    public DepositArrangement createDepositArrangementFor502(String arrangementId) {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementId(arrangementId);
        depositArrangement.setArrangementType("SA");

        Product product = new Product();
        product.setProductIdentifier("92");
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Customer customer = new Customer();
        customer.setIsRegistrationSelected(true);
        customer.setPassword("password1234");
        customer.setIsPlayedBy(new Individual());
        CustomerScore customerScore = new CustomerScore();
        customerScore.getAssessmentEvidence().add(new AssessmentEvidence());
        customer.getCustomerScore().add(customerScore);
        depositArrangement.setPrimaryInvolvedParty(customer);
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("007505");
        Organisation organisation = new Organisation();
        organisation.setChannel("LTB");
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);
        depositArrangement.setIsJointParty(false);
        depositArrangement.getReferral().add(new Referral());
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountNumber("123");
        accountDetails.setSortCode("124");
        accountDetails.setBeneficiaryName("");
        depositArrangement.setAccountDetails(accountDetails);
        depositArrangement.setApplicationType(NEW_APPLICATION_TYPE);
        depositArrangement.setIsSecondaryAccount(true);
        depositArrangement.setAccountNumber("08676168");
        return depositArrangement;
    }

    public List<ReferenceDataLookUp> createChannelIdLookupData() {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(new ReferenceDataLookUp("Cnt_Pnt_Prtflio", "0000777505", "Display Contact_Point_Portfolio", 26L, "CONTACT_POINT_ID", "LTB", 1L));

        return referenceDataLookUpList;
    }

    public RequestHeader createApaRequestHeaderWithInvalidContactPoint() {

        RequestHeaderBuilder requestHeaderBuilder = new RequestHeaderBuilder();
        return requestHeaderBuilder.businessTransaction(TEST_BUSINESS_TRANSACTION).channelId(TEST_RETAIL_CHANNEL_ID).interactionId(TEST_INTERACTION_ID).bapiInformation(TEST_RETAIL_CHANNEL_ID, TEST_INTERACTION_ID, TEST_OCIS_ID, "ns5").securityHeader("lgsm", TEST_CUSTOMER_ID).serviceRequest("ns4", TEST_BUSINESS_TRANSACTION, "10.245.182.105", TEST_MESSAGE_ID).contactPoint("ns4", "003", INVALID_CONTACT_POINT_ID, "Internet Banking", "Browser", "127.0.0.1", "Customer").build();
    }

    @Transactional
    public long createApplicationSA(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision) {
        return createApplication(applicationStatus, eidvStatus, asmDecision, null).getId();
    }

    @Transactional
    @Modifying
    public void cleanUp() {
        applicationsRelatedDao.deleteAll();
        referralsDao.deleteAll();
        referralTeamsDao.deleteAll();
        partyApplicationsDao.deleteAll();
        applicationsDao.deleteAll();
        streetAddressesDao.deleteAll();
        individualAddressesDao.deleteAll();
        individualsDao.deleteAll();
        applicationPartyRolesDao.deleteAll();
        kycStatusDao.deleteAll();
        appStatusDao.deleteAll();
        applicationTypesDao.deleteAll();
        productTypesDao.deleteAll();
        brandsDao.deleteAll();
        userTypesDao.deleteAll();
        promotionChannelsDao.deleteAll();
        channelsDao.deleteAll();
        applicationRelationshipTypesDao.deleteAll();
        applicationParameterValuesDao.deleteAll();
        applicationParametersDao.deleteAll();
        applicationFeaturesDao.deleteAll();
        applicationFeatureTypesDao.deleteAll();
        referralStatusDao.deleteAll();
        abandonDeclineReasonDao.deleteAll();
    }

    public Applications createApplication(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus) {

        Applications applications = new Applications(applicationTypes, productTypesSavings, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        applications.setSortCode("773315");
        applications.setArrangementNumber("08676168");
        applications.setSubStatus(applicationSubStatus);
        applications.setUserId("10.245.176.43");
        applications.setProductRequested("92");
        applications.setProductName("Classic Account");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH.mm.ss");
        Date d = null;
        try {
            d = (formatter.parse("26-NOV-14 09.24.31.000000000"));

            applications.setDateModified(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        applicationsDao.save(applications);

        applicationsDao.findAll();

        // ApplicationParameterValues applicationParameterValues1 = new ApplicationParameterValues(applicationParameters5, applications, "2974109HL890678330000003225830841102", null);
        ApplicationParameterValues applicationParameterValues2 = new ApplicationParameterValues(applicationParameters9, applications, "AAGATEWAY", null);
        ApplicationParameterValues applicationParameterValues3 = new ApplicationParameterValues(applicationParameters1, applications, "P_CLASSIC", null);
        ApplicationParameterValues applicationParameterValues4 = new ApplicationParameterValues(applicationParameters11, applications, "true", null);

        ApplicationParameterValues applicationParameterValues5 = new ApplicationParameterValues(applicationParameters7, applications, "601", "Accept");
        ApplicationParameterValues applicationParameterValues6 = new ApplicationParameterValues(applicationParameters7, applications, "502", "Additional Data req - block A.");
        ApplicationParameterValues applicationParameterValues7 = new ApplicationParameterValues(applicationParameters7, applications, "503", "Additional Data req - block B.");
        ApplicationParameterValues applicationParameterValues8 = new ApplicationParameterValues(applicationParameters7, applications, "601", "Accept");

        ApplicationParameterValues applicationParameterValues9 = new ApplicationParameterValues(applicationParameters4, applications, "50", null);
        ApplicationParameterValues applicationParameterValues10 = new ApplicationParameterValues(applicationParameters10, applications, "0", null);
        ApplicationParameterValues applicationParameterValues11 = new ApplicationParameterValues(applicationParameters6, applications, "971461460", null);
        ApplicationParameterValues applicationParameterValues12 = new ApplicationParameterValues(applicationParameters2, applications, "Y", null);

        ApplicationParameterValues applicationParameterValues13 = new ApplicationParameterValues(applicationParameters3, applications, "502", null);

        // applicationParameterValuesDao.save(applicationParameterValues1);
        applicationParameterValuesDao.save(applicationParameterValues2);
        applicationParameterValuesDao.save(applicationParameterValues3);
        applicationParameterValuesDao.save(applicationParameterValues4);
        applicationParameterValuesDao.save(applicationParameterValues5);
        applicationParameterValuesDao.save(applicationParameterValues6);
        applicationParameterValuesDao.save(applicationParameterValues7);
        applicationParameterValuesDao.save(applicationParameterValues8);
        applicationParameterValuesDao.save(applicationParameterValues9);
        applicationParameterValuesDao.save(applicationParameterValues10);
        applicationParameterValuesDao.save(applicationParameterValues11);
        applicationParameterValuesDao.save(applicationParameterValues12);
        applicationParameterValuesDao.save(applicationParameterValues13);

        applicationParameterValuesDao.findAll();

        Set<ApplicationParameterValues> applicationParameterValuesSet = new HashSet<>();
        //  applicationParameterValuesSet.add(applicationParameterValues1);
        applicationParameterValuesSet.add(applicationParameterValues2);
        applicationParameterValuesSet.add(applicationParameterValues3);
        applicationParameterValuesSet.add(applicationParameterValues4);
        applicationParameterValuesSet.add(applicationParameterValues5);
        applicationParameterValuesSet.add(applicationParameterValues6);
        applicationParameterValuesSet.add(applicationParameterValues7);
        applicationParameterValuesSet.add(applicationParameterValues8);
        applicationParameterValuesSet.add(applicationParameterValues9);
        applicationParameterValuesSet.add(applicationParameterValues10);
        applicationParameterValuesSet.add(applicationParameterValues11);
        applicationParameterValuesSet.add(applicationParameterValues12);
        applicationParameterValuesSet.add(applicationParameterValues13);

        applications.setApplicationParameterValues(toSet(applicationParameterValuesDao.findAll()));

        ApplicationFeatureTypes applicationFeatureTypes1 = new ApplicationFeatureTypes("OD", "Overdraft");
        ApplicationFeatureTypes applicationFeatureTypes2 = new ApplicationFeatureTypes("CHQBK", "ChequeBook");
        ApplicationFeatureTypes applicationFeatureTypes3 = new ApplicationFeatureTypes("IBRA", "IB Registration Accepted");
        ApplicationFeatureTypes applicationFeatureTypes4 = new ApplicationFeatureTypes("BENPA", "Benefit Payments");
        ApplicationFeatureTypes applicationFeatureTypes5 = new ApplicationFeatureTypes("IBRC", "IB Registration Completed");
        ApplicationFeatureTypes applicationFeatureTypes6 = new ApplicationFeatureTypes("ASA", "Account Switching Accepted");
        ApplicationFeatureTypes applicationFeatureTypes7 = new ApplicationFeatureTypes("PCCI", "Date Pcci Viewed");

        applicationFeatureTypesDao.save(applicationFeatureTypes1);
        applicationFeatureTypesDao.save(applicationFeatureTypes2);
        applicationFeatureTypesDao.save(applicationFeatureTypes3);
        applicationFeatureTypesDao.save(applicationFeatureTypes4);
        applicationFeatureTypesDao.save(applicationFeatureTypes5);
        applicationFeatureTypesDao.save(applicationFeatureTypes6);
        applicationFeatureTypesDao.save(applicationFeatureTypes7);

        applicationFeatureTypesDao.findAll();

        ApplicationFeatures applicationFeatures1 = new ApplicationFeatures();
        applicationFeatures1.setApplications(applications);
        applicationFeatures1.setApplicationFeatureTypes(applicationFeatureTypes2);
        applicationFeatures1.setFeatureRequired("N");

        ApplicationFeatures applicationFeatures2 = new ApplicationFeatures();
        applicationFeatures2.setApplications(applications);
        applicationFeatures2.setApplicationFeatureTypes(applicationFeatureTypes1);
        applicationFeatures2.setFeatureRequired("N");
        applicationFeatures2.setAmount(new BigDecimal(0));

        ApplicationFeatures applicationFeatures3 = new ApplicationFeatures();
        applicationFeatures3.setApplications(applications);
        applicationFeatures3.setApplicationFeatureTypes(applicationFeatureTypes3);

        ApplicationFeatures applicationFeatures4 = new ApplicationFeatures();
        applicationFeatures4.setApplications(applications);
        applicationFeatures4.setApplicationFeatureTypes(applicationFeatureTypes4);
        applicationFeatures4.setFeatureRequired("Y");

        Set<ApplicationFeatures> applicationFeaturesSet = new HashSet<>();
        applicationFeaturesSet.add(applicationFeatures1);
        applicationFeaturesSet.add(applicationFeatures2);
        applicationFeaturesSet.add(applicationFeatures3);
        applicationFeaturesSet.add(applicationFeatures4);
        applications.setApplicationFeatures(applicationFeaturesSet);

        Iterable it = applicationsDao.findAll();

        Individuals individuals = new Individuals();
        individuals.setOcisId("227323270");
        individuals.setCidpersid("+00211135806");
        individuals.setEmailId("GalaxyTestAccount02@LloydsTSB.co.uk");
        individuals.setMaritalStatus("001");
        individuals.setNationality("GBR");
        individuals.setDateOfBirth(new Date());
        individuals.setEmploymentStatus("006");
        individuals.setResidentialStatus("001");
        individuals.setFirstName("meera");
        individuals.setLastName("radha");
        individuals.setGender("001");
        individuals.setAnnualGrossIncome(4800L);
        individuals.setYearsCurrEmp((byte) 0);
        individuals.setMonthsCurrEmp((short) 0);
        individuals.setCreditCardHeld("N");
        individuals.setTitle("Mr");
        individuals.setStaffInd('N');
        individuals.setYearsWithBank((short) 29);
        individuals.setMonthsWithBank((short) 0);
        individuals.setCountryOfBirth("United Kingdom");
        individuals.setOccupation("001");

        StreetAddresses streetAddresses = new StreetAddresses("addressLine1", "addressLine2", "addressLine3", "city", null, "country", "SE1-2EA", new Byte("5"), Short.valueOf("5"), "Y", "buildingName", "1", "1A", "street", "subBuildingName", "district", "addressLine4");
        IndividualAddresses individualAddresses = new IndividualAddresses(streetAddresses, null, 1L);
        Set<IndividualAddresses> individualAddressesSet = new HashSet<>();
        individualAddressesSet.add(individualAddresses);
        streetAddresses.setIndividualAddresseses(individualAddressesSet);

        streetAddressesDao.save(streetAddresses);
        streetAddressesDao.findAll();

        individuals.setIndividualAddresses(individualAddressesSet);
        individualsDao.save(individuals);
        individualsDao.findAll();

/*        TelephoneAddresses telephoneAddresses = new TelephoneAddresses();
        telephoneAddresses.setTelephoneNumber(new BigDecimal("9123456789"));
        telephoneAddresses.setTelephoneAddressTypes(telephoneAddressTypes);
        telephoneAddresses.setIndividuals(individuals);
        Set<TelephoneAddresses> telephoneAddressesSet = new HashSet<>();
        telephoneAddressesSet.add(telephoneAddresses);
        telephoneAddressesDao.save(telephoneAddressesSet);
        telephoneAddressesDao.findAll();

        individuals.setTelephoneAddresses(telephoneAddressesSet);*/


        individualAddresses.setIndividuals(individuals);
        individualAddressesSet.add(individualAddresses);
        individualAddressesDao.save(individualAddresses);
        individualAddressesDao.findAll();
        PartyApplications partyApplications = new PartyApplications();
        partyApplications.setApplicationPartyRoles(roles);
        partyApplications.setScoringStatus(asmDecision);
        partyApplications.setCustomerSegments("3");
        partyApplications.setKycStatus(eidvStatus);
        partyApplications.setLockId(0L);
        //partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);
        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();

        Set<PartyApplications> partyApplicationsSet = new HashSet<>();
        partyApplicationsSet.add(partyApplications);
        individuals.getIndividualAddresses().add(individualAddresses);
        individuals.setPartyApplications(partyApplicationsSet);


        //

        Individuals individuals1 = new Individuals();
        individuals1.setOcisId("227323271");
        individuals1.setCidpersid("+00211135806");
        individuals1.setEmailId("GalaxyTestAccount02@LloydsTSB.co.uk");
        individuals1.setMaritalStatus("001");
        individuals1.setNationality("GBR");
        individuals1.setDateOfBirth(new Date());
        individuals1.setEmploymentStatus("006");
        individuals1.setResidentialStatus("001");
        individuals1.setFirstName("meera");
        individuals1.setLastName("radha");
        individuals1.setGender("001");
        individuals1.setAnnualGrossIncome(4800L);
        individuals1.setYearsCurrEmp((byte) 0);
        individuals1.setMonthsCurrEmp((short) 0);
        individuals1.setCreditCardHeld("N");
        individuals1.setTitle("Mr");
        individuals1.setStaffInd('N');
        individuals1.setYearsWithBank((short) 29);
        individuals1.setMonthsWithBank((short) 0);
        individuals1.setCountryOfBirth("United Kingdom");
        individuals1.setOccupation("001");

        StreetAddresses streetAddresses1 = new StreetAddresses("addressLine1", "addressLine2", "addressLine3", "city", null, "country", "SE1-2EA", new Byte("5"), Short.valueOf("5"), "Y", "buildingName", "1", "1A", "street", "subBuildingName", "district", "addressLine4");
        IndividualAddresses individualAddresses1 = new IndividualAddresses(streetAddresses1, null, 1L);
        Set<IndividualAddresses> individualAddressesSet1 = new HashSet<>();
        individualAddressesSet1.add(individualAddresses1);
        streetAddresses1.setIndividualAddresseses(individualAddressesSet1);

        streetAddressesDao.save(streetAddresses1);
        streetAddressesDao.findAll();

        individuals.setIndividualAddresses(individualAddressesSet1);
        individualsDao.save(individuals1);
        individualsDao.findAll();


/*        TelephoneAddresses telephoneAddresses = new TelephoneAddresses();
        telephoneAddresses.setTelephoneNumber(new BigDecimal("9123456789"));
        telephoneAddresses.setTelephoneAddressTypes(telephoneAddressTypes);
        telephoneAddresses.setIndividuals(individuals);
        Set<TelephoneAddresses> telephoneAddressesSet = new HashSet<>();
        telephoneAddressesSet.add(telephoneAddresses);
        telephoneAddressesDao.save(telephoneAddressesSet);
        telephoneAddressesDao.findAll();

        individuals.setTelephoneAddresses(telephoneAddressesSet);*/


        individualAddresses1.setIndividuals(individuals1);
        individualAddressesSet1.add(individualAddresses1);
        individualAddressesDao.save(individualAddresses1);
        individualAddressesDao.findAll();
        PartyApplications partyApplications1 = new PartyApplications();
        partyApplications1.setApplicationPartyRoles(rolesGuardian);
        partyApplications1.setScoringStatus(asmDecision);
        partyApplications1.setCustomerSegments("3");
        partyApplications1.setKycStatus(eidvStatus);
        partyApplications1.setLockId(1L);
        //partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);
        partyApplications1.setApplications(applications);
        partyApplications1.setIndividuals(individuals1);
        partyApplicationsDao.save(partyApplications1);
        partyApplicationsDao.findAll();

        Set<PartyApplications> partyApplicationsSet1 = new HashSet<>();
        partyApplicationsSet1.add(partyApplications1);
        individuals1.getIndividualAddresses().add(individualAddresses1);
        individuals1.setPartyApplications(partyApplicationsSet1);

        Set<PartyApplications> partyApplicationsSetAll = new HashSet<>();
        partyApplicationsSetAll.add(partyApplications);
        partyApplicationsSetAll.add(partyApplications1);
        applications.setPartyApplications(partyApplicationsSetAll);

        return applications;
    }


    @Transactional
    @Modifying()
    public Applications createApplication(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        Applications applications = new Applications(applicationTypes, productType, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        applications.setSortCode("773315");
        applications.setArrangementNumber("08676168");
        applications.setSubStatus(applicationSubStatus);
        applications.setUserId("10.245.176.43");
        applications.setProductRequested("92");
        applications.setProductName("Classic Account");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH.mm.ss");
        Date d = null;
        try {
            d = (formatter.parse("26-NOV-14 09.24.31.000000000"));

            applications.setDateModified(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        applicationsDao.save(applications);
        applicationsDao.findAll();

        // ApplicationParameterValues applicationParameterValues1 = new ApplicationParameterValues(applicationParameters5, applications, "2974109HL890678330000003225830841102", null);
        ApplicationParameterValues applicationParameterValues2 = new ApplicationParameterValues(applicationParameters9, applications, "AAGATEWAY", null);
        ApplicationParameterValues applicationParameterValues3 = new ApplicationParameterValues(applicationParameters1, applications, "P_CLASSIC", null);
        ApplicationParameterValues applicationParameterValues4 = new ApplicationParameterValues(applicationParameters11, applications, "true", null);

        ApplicationParameterValues applicationParameterValues5 = new ApplicationParameterValues(applicationParameters7, applications, "601", "Accept");
        ApplicationParameterValues applicationParameterValues6 = new ApplicationParameterValues(applicationParameters7, applications, "502", "Additional Data req - block A.");
        ApplicationParameterValues applicationParameterValues7 = new ApplicationParameterValues(applicationParameters7, applications, "503", "Additional Data req - block B.");
        ApplicationParameterValues applicationParameterValues8 = new ApplicationParameterValues(applicationParameters7, applications, "601", "Accept");

        ApplicationParameterValues applicationParameterValues9 = new ApplicationParameterValues(applicationParameters4, applications, "50", null);
        ApplicationParameterValues applicationParameterValues10 = new ApplicationParameterValues(applicationParameters10, applications, "0", null);
        ApplicationParameterValues applicationParameterValues11 = new ApplicationParameterValues(applicationParameters6, applications, "971461460", null);
        ApplicationParameterValues applicationParameterValues12 = new ApplicationParameterValues(applicationParameters2, applications, "Y", null);

        ApplicationParameterValues applicationParameterValues13 = new ApplicationParameterValues(applicationParameters3, applications, "502", null);

        ApplicationParameterValues applicationParameterValues14 = new ApplicationParameterValues(1L, applicationParameters12, applications, "345", null);

        //applicationParameterValuesDao.save(applicationParameterValues1);
        applicationParameterValuesDao.save(applicationParameterValues2);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues3);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues4);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues5);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues6);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues7);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues8);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues9);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues10);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues11);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues12);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues13);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues14);
        applicationParameterValuesDao.findAll();

        Set<ApplicationParameterValues> applicationParameterValuesSet = new HashSet<>();
        //applicationParameterValuesSet.add(applicationParameterValues1);
        applicationParameterValuesSet.add(applicationParameterValues2);
        applicationParameterValuesSet.add(applicationParameterValues3);
        applicationParameterValuesSet.add(applicationParameterValues4);
        applicationParameterValuesSet.add(applicationParameterValues5);
        applicationParameterValuesSet.add(applicationParameterValues6);
        applicationParameterValuesSet.add(applicationParameterValues7);
        applicationParameterValuesSet.add(applicationParameterValues8);
        applicationParameterValuesSet.add(applicationParameterValues9);
        applicationParameterValuesSet.add(applicationParameterValues10);
        applicationParameterValuesSet.add(applicationParameterValues11);
        applicationParameterValuesSet.add(applicationParameterValues12);
        applicationParameterValuesSet.add(applicationParameterValues13);
        applicationParameterValuesSet.add(applicationParameterValues14);

        applications.setApplicationParameterValues(toSet(applicationParameterValuesDao.findAll()));

        ApplicationFeatures applicationFeatures1 = new ApplicationFeatures();
        applicationFeatures1.setApplications(applications);
        applicationFeatures1.setApplicationFeatureTypes(applicationFeatureTypes2);
        applicationFeatures1.setFeatureRequired("N");

        ApplicationFeatures applicationFeatures2 = new ApplicationFeatures();
        applicationFeatures2.setApplications(applications);
        applicationFeatures2.setApplicationFeatureTypes(applicationFeatureTypes1);
        applicationFeatures2.setFeatureRequired("N");
        applicationFeatures2.setAmount(new BigDecimal(0));

        ApplicationFeatures applicationFeatures3 = new ApplicationFeatures();
        applicationFeatures3.setApplications(applications);
        applicationFeatures3.setApplicationFeatureTypes(applicationFeatureTypes3);

        //applicationFeatures3.setExpiryDate("26-NOV-14 09.24.31");

        ApplicationFeatures applicationFeatures4 = new ApplicationFeatures();
        applicationFeatures4.setApplications(applications);
        applicationFeatures4.setApplicationFeatureTypes(applicationFeatureTypes4);
        applicationFeatures4.setFeatureRequired("Y");

        Set<ApplicationFeatures> applicationFeaturesSet = new HashSet<>();
        applicationFeaturesSet.add(applicationFeatures1);
        applicationFeaturesSet.add(applicationFeatures2);
        applicationFeaturesSet.add(applicationFeatures3);
        applicationFeaturesSet.add(applicationFeatures4);
        applications.setApplicationFeatures(applicationFeaturesSet);

        Iterable it = applicationsDao.findAll();

        Individuals individuals = new Individuals();
        individuals.setOcisId("227323270");
        individuals.setCidpersid("+00211135806");
        individuals.setEmailId("GalaxyTestAccount02@LloydsTSB.co.uk");
        individuals.setMaritalStatus("001");
        individuals.setNationality("GBR");
        individuals.setDateOfBirth(DateFactory.toDate(createXMLGregorianCalendar(1992, 07, 12)));
        individuals.setEmploymentStatus("006");
        individuals.setResidentialStatus("001");
        individuals.setFirstName("meera");
        individuals.setLastName("radha");
        individuals.setGender("001");
        individuals.setAnnualGrossIncome(4800L);
        individuals.setYearsCurrEmp((byte) 0);
        individuals.setMonthsCurrEmp((short) 0);
        individuals.setCreditCardHeld("N");
        individuals.setTitle("Mr");
        individuals.setStaffInd('N');
        individuals.setYearsWithBank((short) 29);
        individuals.setMonthsWithBank((short) 0);
        individuals.setCountryOfBirth("United Kingdom");
        individuals.setOccupation("001");
        individuals.setPlaceOfBirth("Place of Birth");

        StreetAddresses streetAddresses = new StreetAddresses("addressLine1", "addressLine2", "addressLine3", "city", null, "country", "SE1-2EA", new Byte("5"), Short.valueOf("5"), "Y", "buildingName", "1", "1A", "street", "subBuildingName", "district", "addressLine4");
        IndividualAddresses individualAddresses = new IndividualAddresses(streetAddresses, null, 1L);
        Set<IndividualAddresses> individualAddressesSet = new HashSet<>();
        individualAddressesSet.add(individualAddresses);
        streetAddresses.setIndividualAddresseses(individualAddressesSet);

        streetAddressesDao.save(streetAddresses);
        streetAddressesDao.findAll();

        individuals.setIndividualAddresses(individualAddressesSet);
        individualsDao.save(individuals);
        individualsDao.findAll();

/*        TelephoneAddresses telephoneAddresses = new TelephoneAddresses();
        telephoneAddresses.setTelephoneNumber(new BigDecimal("9123456789"));
        telephoneAddresses.setTelephoneAddressTypes(telephoneAddressTypes);
        telephoneAddresses.setIndividuals(individuals);
        Set<TelephoneAddresses> telephoneAddressesSet = new HashSet<>();
        telephoneAddressesSet.add(telephoneAddresses);
        telephoneAddressesDao.save(telephoneAddressesSet);
        telephoneAddressesDao.findAll();

        individuals.setTelephoneAddresses(telephoneAddressesSet);*/


        individualAddresses.setIndividuals(individuals);
        individualAddressesSet.add(individualAddresses);
        individualAddressesDao.save(individualAddresses);
        individualAddressesDao.findAll();
        PartyApplications partyApplications = new PartyApplications();
        partyApplications.setApplicationPartyRoles(roles);
        partyApplications.setScoringStatus(asmDecision);
        partyApplications.setCustomerSegments("3");

        //kycEvidenceDetailsDao.save(kycEvidenceDetails);

        partyApplications.setKycStatus(eidvStatus);
        partyApplications.setLockId(0L);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);

        KycEvidenceDetails kycEvidenceDetails = new KycEvidenceDetails();
        kycEvidenceDetails.setPartyApplications(partyApplications);
        kycEvidenceDetails.setNiNumber(niNumber);
        Set<KycEvidenceDetails> kycEvidenceDetailsSet = new HashSet<>();
        kycEvidenceDetailsSet.add(kycEvidenceDetails);
        partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);

        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();

        individuals.getIndividualAddresses().add(individualAddresses);
        Set<PartyApplications> partyApplicationsSet = new HashSet<>();
        partyApplicationsSet.add(partyApplications);

        individuals.setPartyApplications(partyApplicationsSet);

        applications.setPartyApplications(partyApplicationsSet);
        return applications;
    }

    public <T> Set<T> toSet(Iterable<T> collection) {
        HashSet<T> set = new HashSet<T>();
        for (T item : collection)
            set.add(item);
        return set;
    }

    public void createPamReferenceData() {
        productTypesDao.save(productTypesCurrent);
        productTypesDao.save(productTypesSavings);
        productTypesDao.findAll();
        brandsDao.save(brands);
        brandsDao.findAll();
        userTypesDao.save(userTypes);
        userTypesDao.findAll();
        appStatusDao.save(applicationStatus);
        appStatusDao.save(new ApplicationStatus("1003", "Referred"));
        appStatusDao.save(new ApplicationStatus("1007", "AwaitingManualIdv"));
        appStatusDao.save(new ApplicationStatus(com.lloydsbanking.salsa.constant.ApplicationStatus.AWAITING_FULFILMENT.getValue(), "Awaiting Fulfilment"));
        appStatusDao.save(new ApplicationStatus("1010", "Fulfill"));
        appStatusDao.save(new ApplicationStatus("1004", "Decline"));
        appStatusDao.save(new ApplicationStatus("1007", "Awaiting Manual IDandV"));
        appStatusDao.save(new ApplicationStatus("1008", "Awaiting Referral Processing"));
        appStatusDao.findAll();
        applicationTypesDao.save(applicationTypes);
        applicationTypesDao.findAll();
        promotionChannelsDao.save(promotionChannels);
        promotionChannelsDao.findAll();
        channelsDao.save(channels);
        channelsDao.findAll();
        applicationFormsDao.save(applicationForm);
        applicationFormsDao.findAll();
        applicationPartyRolesDao.save(roles);
        applicationPartyRolesDao.findAll();
        applicationPartyRolesDao.save(rolesGuardian);
        applicationPartyRolesDao.findAll();


        parameterGroupsDao.save(parameterGroups2);
        parameterGroupsDao.save(parameterGroups3);
        parameterGroupsDao.save(parameterGroups1);
        parameterGroupsDao.save(parameterGroups4);
        parameterGroupsDao.save(parameterGroups5);
        parameterGroupsDao.save(parameterGroups6);
        parameterGroupsDao.save(parameterGroups7);
        parameterGroupsDao.save(parameterGroups8);
        parameterGroupsDao.findAll();

        applicationParametersDao.save(applicationParameters1);
        applicationParametersDao.save(applicationParameters2);
        applicationParametersDao.save(applicationParameters3);
        applicationParametersDao.save(applicationParameters4);
        // applicationParametersDao.save(applicationParameters5);
        applicationParametersDao.save(applicationParameters6);
        applicationParametersDao.save(applicationParameters7);
        applicationParametersDao.save(applicationParameters8);
        applicationParametersDao.save(applicationParameters9);
        applicationParametersDao.save(applicationParameters10);
        applicationParametersDao.save(applicationParameters11);
        applicationParametersDao.save(applicationParameters11);
        applicationParametersDao.save(applicationParameters11);
        applicationParametersDao.save(applicationParameters12);
        applicationParametersDao.findAll();

        kycStatus.setCode("ACCEPT");
        kycStatus.setDescription("ACCEPT");
        kycStatusDao.save(kycStatus);

        KycStatus kycStatus1 = new KycStatus();
        kycStatus1.setCode("REFER");
        kycStatus1.setDescription("REFER");
        kycStatusDao.save(kycStatus1);

        kycStatusDao.findAll();

        applicationPartyRolesDao.save(applicationPartyRolesForPromoParty);
        applicationPartyRolesDao.findAll();

        approvalStatus.setCode("001");
        approvalStatus.setDescription("Approved");
        approvalStatusDao.save(approvalStatus);
        approvalStatusDao.findAll();

        applicationPartyRolesDao.save(roles);
        applicationPartyRolesDao.findAll();
        applicationPartyRolesDao.save(rolesGuardian);
        applicationPartyRolesDao.findAll();

        applicationFeatureTypesDao.save(applicationFeatureTypes1);
        applicationFeatureTypesDao.save(applicationFeatureTypes2);
        applicationFeatureTypesDao.save(applicationFeatureTypes3);
        applicationFeatureTypesDao.save(applicationFeatureTypes4);
        applicationFeatureTypesDao.save(applicationFeatureTypes5);
        applicationFeatureTypesDao.save(applicationFeatureTypes6);
        applicationFeatureTypesDao.save(applicationFeatureTypes7);
        applicationFeatureTypesDao.save(applicationFeatureTypes8);
        applicationFeatureTypesDao.save(applicationFeatureTypes9);

        applicationFeatureTypesDao.findAll();

        referralStatusDao.save(new ReferralStatus("PEN", "Pending"));
        referralStatusDao.save(new ReferralStatus("COM", "Completed"));
        referralStatusDao.findAll();

        abandonDeclineReasonDao.save(new AbandonDeclineReasons("102", "ASM Decline"));
        abandonDeclineReasonDao.findAll();
    }

    public List<ReferenceDataLookUp> createLookupData() {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 513L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "1", "Purpose of Account", 1091L, "SPORI", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "2", "Purpose of Account", 1092L, "BIEXP", "LTB", 1L));

        return referenceDataLookUpList;
    }

    @Transactional
    public Applications createNewApplication(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus) {
        return createApplication(applicationStatus, eidvStatus, asmDecision, applicationSubStatus);
    }

    @Transactional
    public Applications createNewApplication(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        return createApplication(applicationStatus, eidvStatus, asmDecision, applicationSubStatus, niNumber, productType);
    }

    public ActivateProductArrangementRequest createApaRequestWithInvalidStatus(long arrangementId) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangement(String.valueOf(arrangementId)));
        activateProductArrangementRequest.setSourceSystemIdentifier("3");
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestByDBEvent() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangementForDBEvent());
        activateProductArrangementRequest.setSourceSystemIdentifier("2");
        return activateProductArrangementRequest;
    }

    private ProductArrangement createDepositArrangementForDBEvent() {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementId("90261");
        depositArrangement.setArrangementType("SA");
        depositArrangement.setApplicationStatus("1002");

        Product product = new Product();
        product.setProductIdentifier("92");
        product.setBrandName("LTB");

        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("P_CLUB");
        product.setInstructionDetails(instructionDetails);

        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("502");

        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);

        product.getAssociatedFamily().add(productFamily);
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Channel channel = new Channel();
        channel.setChannelCode("004");
        channel.setSubChannelCode("003");
        depositArrangement.setInitiatedThrough(channel);

        Customer customer = getPrimaryInvolvedParty();
        customer.setIsRegistrationSelected(Boolean.FALSE);
        depositArrangement.setPrimaryInvolvedParty(customer);

        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("306521");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);
        depositArrangement.getFinancialInstitution().setChannel("LTB");
        depositArrangement.setMarketingPreferenceByEmail(false);
        depositArrangement.setIsJointParty(false);
        depositArrangement.setApplicationSubStatus("1023");

        AffiliateDetails affiliateDetails = getAffiliateDetails();
        depositArrangement.getAffiliatedetails().add(affiliateDetails);

        updateArrangementHistory(depositArrangement);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("N");
        depositArrangement.getConditions().add(ruleCondition);

        depositArrangement.setApplicationType("10001");
        depositArrangement.setRetryCount(1);
        depositArrangement.setAccountPurpose("BIEXP");
        depositArrangement.setFundingSource("1");
        depositArrangement.setLastModifiedDate(createXMLGregorianCalendar(2015, 8, 25));
        depositArrangement.setRelatedApplicationExists(false);
        depositArrangement.setMarketingPreferenceByEmail(false);
        depositArrangement.setMarketingPreferenceByMail(false);
        depositArrangement.setMarketingPreferenceByPhone(false);
        depositArrangement.setIsCashCard(false);
        depositArrangement.setIsR85Opt(false);
        depositArrangement.setIsOverdraftRequired(false);

        return depositArrangement;
    }

    public Customer getPrimaryInvolvedParty() {
        Customer customer = new Customer();
        customer.setEmailAddress("sdfgd@fgh.com");

        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("0505");

        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuilding("THE CROSSKEYS");
        structuredAddress.setSubBuilding("1");
        structuredAddress.setBuildingNumber("39");
        structuredAddress.setPostTown("SHAFTESBURY");
        structuredAddress.setCountry("United Kingdom");
        structuredAddress.setPostCodeOut("SP7");
        structuredAddress.setPostCodeIn("8JE");
        structuredAddress.setPointSuffix("2Z");
        structuredAddress.setCounty("DORSET");
        structuredAddress.getAddressLinePAFData().add("HIGH STREET");
        postalAddress.setStructuredAddress(structuredAddress);

        customer.getPostalAddress().add(postalAddress);

        TelephoneNumber telephoneNumber = new TelephoneNumber();
        telephoneNumber.setCountryPhoneCode("44");
        telephoneNumber.setPhoneNumber("2353455");
        telephoneNumber.setTelephoneType("7");
        customer.getTelephoneNumber().add(telephoneNumber);

        InternetBankingRegistration internetBankingRegistration = new InternetBankingRegistration();
        internetBankingRegistration.setRegistrationIdentifier("1130651701");
        internetBankingRegistration.setApplicationVersion("0");
        customer.setIsRegisteredIn(internetBankingRegistration);

        Individual individual = new Individual();

        individual.setIsStaffMember(false);
        individual.setResidentialStatus("002");
        individual.setNationality("GBR");
        individual.setNumberOfDependents(new BigInteger("1"));
        individual.setMaritalStatus("001");
        individual.setGender("001");
        individual.setEmploymentStatus("003");
        individual.setCurrentEmploymentDuration("0505");
        individual.setOccupation("021");
        individual.setCountryOfBirth("United Kingdom");

        IndividualName individualName = new IndividualName();
        individualName.setFirstName("zdfgdfg");
        individualName.setLastName("dfgdfg");
        individualName.setPrefixTitle("Mr");
        individual.getIndividualName().add(individualName);

        CurrencyAmount totalSavingAmount = new CurrencyAmount();
        totalSavingAmount.setAmount(new BigDecimal("500"));
        individual.setTotalSavingsAmount(totalSavingAmount);

        CurrencyAmount netMonthlyIncome = new CurrencyAmount();
        netMonthlyIncome.setAmount(new BigDecimal("4000"));
        individual.setNetMonthlyIncome(netMonthlyIncome);

        CurrencyAmount monthlyLoanRepaymentAmount = new CurrencyAmount();
        monthlyLoanRepaymentAmount.setAmount(new BigDecimal("100"));
        individual.setMonthlyLoanRepaymentAmount(monthlyLoanRepaymentAmount);

        CurrencyAmount monthlyMortgageAmount = new CurrencyAmount();
        monthlyMortgageAmount.setAmount(new BigDecimal("100"));
        individual.setMonthlyMortgageAmount(monthlyMortgageAmount);

        customer.setIsPlayedBy(individual);
        customer.setCustomerIdentifier("969499399");

        CustomerScore eidvScore = new CustomerScore();
        eidvScore.setScoreResult("ACCEPT");
        eidvScore.setAssessmentType("EIDV");
        customer.getCustomerScore().add(eidvScore);

        CustomerScore asmScore = new CustomerScore();
        asmScore.setAssessmentType("ASM");
        asmScore.setScoreResult("1");
        customer.getCustomerScore().add(asmScore);

        customer.setExistingAccountDuration("2900");
        customer.setCidPersID("+00736191311");
        customer.setUserType("1001");
        customer.setInternalUserIdentifier("10.245.224.125");
        customer.setHasExistingCreditCard(false);
        customer.setCustomerSegment("3");
        customer.setOtherBankDuration("2900");
        customer.setNewCustomerIndicator(true);

        TaxResidencyDetails taxResidencyDetails = new TaxResidencyDetails();
        taxResidencyDetails.getTaxResidencyCountries().add("GBR");
        customer.setTaxResidencyDetails(taxResidencyDetails);

        return customer;
    }

    private AffiliateDetails getAffiliateDetails() {
        AffiliateDetails affiliateDetails = new AffiliateDetails();
        affiliateDetails.setAffiliateIdentifier("A18");
        affiliateDetails.setAffiliateDescription("Fair Investment Company Ltd");

        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1("Kings House");
        unstructuredAddress.setAddressLine2("14 Orchard Street");
        unstructuredAddress.setAddressLine6("Bristol");
        unstructuredAddress.setPostCode("BS1 5EH");

        affiliateDetails.setAffliateAddress(unstructuredAddress);
        affiliateDetails.setIsCreditIntermediary(false);

        NetworkAffiliatedDetails networkAffiliatedDetail1 = new NetworkAffiliatedDetails();
        networkAffiliatedDetail1.setAffiliateNetworkAffiliateId("233");
        affiliateDetails.getIsAffiliateNetworkFor().add(networkAffiliatedDetail1);

        NetworkAffiliatedDetails networkAffiliatedDetail2 = new NetworkAffiliatedDetails();
        networkAffiliatedDetail2.setAffiliateNetworkAffiliateId("A18");
        affiliateDetails.getIsAffiliateNetworkFor().add(networkAffiliatedDetail2);
        return affiliateDetails;
    }

    private void updateArrangementHistory(DepositArrangement depositArrangement) {
        ArrangementHistory arrangementHistory1 = new ArrangementHistory();
        arrangementHistory1.setStatus("1001");
        arrangementHistory1.setUserType("1001");
        arrangementHistory1.setUserIdentifier("10.245.224.125");
        arrangementHistory1.setDateModified(createXMLGregorianCalendar(2015, 8, 15));
        depositArrangement.getArrangementHistory().add(arrangementHistory1);

        ArrangementHistory arrangementHistory2 = new ArrangementHistory();
        arrangementHistory2.setStatus("1002");
        arrangementHistory2.setSubStatus("1023");
        arrangementHistory2.setRetryCount("1");
        arrangementHistory2.setUserType("1001");
        arrangementHistory2.setUserIdentifier("10.245.224.125");
        arrangementHistory2.setDateModified(createXMLGregorianCalendar(2015, 8, 25));
        depositArrangement.getArrangementHistory().add(arrangementHistory2);
    }

    public XMLGregorianCalendar createXMLGregorianCalendar(int year, int month, int day) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar xcal = datatypeFactory.newXMLGregorianCalendar();
            xcal.setYear(year);
            xcal.setMonth(month);
            xcal.setDay(day);
            xcal.setTime(10, 5, 15, 0);
            xcal.setTimezone(0);
            return xcal;
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public RetrieveProductConditionsResponse createRpcResponse() {
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        Product product = new Product();
        InstructionDetails instructionDetails = new InstructionDetails();
        List<ExtSysProdIdentifier> extSysProdIdentifierList = new ArrayList<>();
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("01000");
        extSysProdIdentifier.setSystemCode("2Vm");
        extSysProdIdentifierList.add(extSysProdIdentifier);
        product.setProductIdentifier("92");
        product.setBrandName("LTB");
        product.setInstructionDetails(instructionDetails);
        product.setProductType("Classic Plus Account");

        ProductOffer productOffer = new ProductOffer();
        Template template = new Template();
        template.setTemplateIdentifier("1");
        template.setSystemCode("00001");
        template.setExternalTemplateIdentifier("CCA_Generic");
        productOffer.getTemplate().add(template);
        product.getProductoffer().add(productOffer);

        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsDescription("Overdraft Offer Flag");
        productOptions.setOptionsCode("6465");
        productOptions.setOptionsName("isVantage");
        productOptions.setOptionsType("OD_Offer_Flag");
        productOptions.setOptionsValue("Yes");
        product.getProductoptions().add(productOptions);

        retrieveProductConditionsResponse.getProduct().add(product);
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier();
        return retrieveProductConditionsResponse;
    }

    public RetrieveProductConditionsResponse createRpcResponseFails() {
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        Product product = new Product();
        InstructionDetails instructionDetails = new InstructionDetails();
        List<ExtSysProdIdentifier> extSysProdIdentifierList = new ArrayList<>();
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("01000");
        extSysProdIdentifier.setSystemCode("2Vm");
        extSysProdIdentifierList.add(extSysProdIdentifier);
        product.setProductIdentifier("01000");
        product.setInstructionDetails(instructionDetails);
        product.setProductType("ABS");

        retrieveProductConditionsResponse.getProduct().add(product);
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier();

        return retrieveProductConditionsResponse;
    }


    public TaskCreationResponse createTaskCreationResponse() {
        TaskCreationResponse taskCreationResponse = new TaskCreationResponse();
        CreateTaskResponse createTaskResponse = new CreateTaskResponse();

        TMSRoutingInformationBO tmsRoutingInformationBO = new TMSRoutingInformationBO();
        tmsRoutingInformationBO.setTaskId(45);
        createTaskResponse.setTaskRoutingInformation(tmsRoutingInformationBO);
        ResultCondition resultcondition = new ResultCondition();
        resultcondition.setReasonCode(88);
        resultcondition.setSeverityCode((byte) 0);
        resultcondition.setReasonText("text");
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

    public F425Req createF425Req(String requestNo, String sourceSystemCode) {
        F425Req f425Req = new F425Req();
        f425Req.setApplicationSourceCd("004");
        f425Req.setCreditScoreRequestNo(requestNo);
        f425Req.setCreditScoreSourceSystemCd(sourceSystemCode);
        f425Req.setCSOrganisationCd("001");
        f425Req.setMaxRepeatGroupQy(0);
        return f425Req;
    }

    public F425Resp createF425Resp(String asmDecisionCode, String asmCreditScore) {
        F425Resp response = createF425Resp();
        response.getResultsDetails().setASMDecisionSourceCd(asmDecisionCode);
        response.getResultsDetails().setASMCreditScoreResultCd(asmCreditScore);
        ProductOffered productOffered = new ProductOffered();
        productOffered.setProductsOfferedCd("502");
        response.getProductOffered().add(productOffered);
        return response;
    }

    public void createRelatedApplications(Applications applications, ApplicationStatus applicationStatus) {

        Applications relatedApplications = new Applications(applicationTypes, productTypesSavings, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        relatedApplications.setArrangementNumber("09674868");

        relatedApplications.setUserId("10.245.176.43");
        relatedApplications.setProductRequested("20199");
        relatedApplications.setProductName("Club Lloyds Saver (Annual)");

        ApplicationRelationshipTypes applicationRelationshipTypes = new ApplicationRelationshipTypes();
        applicationRelationshipTypes.setCode("20001");
        applicationRelationshipTypes.setDescription("Cross Sell");
        applicationRelationshipTypesDao.save(applicationRelationshipTypes);
        applicationsDao.save(relatedApplications);
        applicationsDao.findAll();
        ApplicationsRelated applicationsRelated = new ApplicationsRelated();
        applicationsRelated.setApplicationsByAppIdRelated(relatedApplications);
        applicationsRelated.setApplicationsByAppId(applications);
        applicationsRelated.setApplicationRelationshipTypes(applicationRelationshipTypes);
        applicationsRelatedDao.save(applicationsRelated);
    }

    public F425Resp createF425Resp() {
        F425Resp response = new F425Resp();
        F425Result f425Result = new F425Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode((byte) 0);
        f425Result.setResultCondition(resultCondition);
        response.setF425Result(f425Result);
        ResultsDetails details = new ResultsDetails();
        details.setASMDecisionSourceCd("1");
        details.setASMCreditScoreResultCd("2");
        response.setResultsDetails(details);
        DecisionDetails decisionDetails = new DecisionDetails();
        decisionDetails.setCSDecisionReasonTypeCd("002");
        decisionDetails.setCSDecisnReasonTypeNr("Connected Personal Account.");
        DecisionDetails decisionDetails1 = new DecisionDetails();
        decisionDetails1.setCSDecisionReasonTypeCd("031");
        decisionDetails1.setCSDecisnReasonTypeNr("Customer unemployed.");
        DecisionDetails decisionDetails2 = new DecisionDetails();
        decisionDetails2.setCSDecisionReasonTypeCd("253");
        decisionDetails2.setCSDecisnReasonTypeNr("High risk account. (RI 7/8/9)");
        FacilitiesOffered facilitiesOffered = new FacilitiesOffered();
        facilitiesOffered.setCSFacilityOfferedAm("121");
        facilitiesOffered.setCSFacilityOfferedCd("2231");
        FacilitiesOffered facilitiesOffered1 = new FacilitiesOffered();
        facilitiesOffered1.setCSFacilityOfferedAm("11");
        facilitiesOffered1.setCSFacilityOfferedCd("122");
        FacilitiesOffered facilitiesOffered2 = new FacilitiesOffered();
        facilitiesOffered2.setCSFacilityOfferedAm("23");
        facilitiesOffered2.setCSFacilityOfferedCd("998");
        response.getFacilitiesOffered().add(facilitiesOffered);
        response.getFacilitiesOffered().add(facilitiesOffered1);
        response.getFacilitiesOffered().add(facilitiesOffered2);
        response.getDecisionDetails().add(decisionDetails);
        response.getDecisionDetails().add(decisionDetails1);
        response.getDecisionDetails().add(decisionDetails2);
        response.setAdditionalDataIn(0);
        return response;
    }

    public StB765AAccCreateAccount createB765Request(RequestHeader header, ProductArrangement productArrangement) {
        Product product = new Product();
        InstructionDetails instructionDetails = new InstructionDetails();
        List<ExtSysProdIdentifier> extSysProdIdentifierList = new ArrayList<>();
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("01000");
        extSysProdIdentifier.setSystemCode("2Vm");
        extSysProdIdentifierList.add(extSysProdIdentifier);
        product.setProductIdentifier("01000");
        product.setInstructionDetails(instructionDetails);
        product.setProductType("ABS");
        DepositArrangement depositArrangement = (DepositArrangement) productArrangement;
        Map<String, String> accountPurposeMap = new HashMap<>();
        accountPurposeMap.put("SPORI", "1");
        accountPurposeMap.put("BIEXP", "2");
        StB765AAccCreateAccount request = depositArrangementToB765Request.getCreateAccountRequest(header, depositArrangement, product, accountPurposeMap);
        return request;
    }

    public StB765BAccCreateAccount createResponseB765(String accountNumber, String sortCode) {
        StB765BAccCreateAccount resp = new StB765BAccCreateAccount();
        StAccount stAccount = new StAccount();
        com.lloydsbanking.salsa.soap.fs.account.StError stError = new com.lloydsbanking.salsa.soap.fs.account.StError();
        resp.setStacc(stAccount);
        resp.setSterror(stError);
        resp.getStacc().setSortcode(sortCode);
        resp.setCustnum("227323270");
        resp.getSterror().setErrorno(0);
        resp.getSterror().setErrormsg("rText");
        resp.getStacc().setAccno(accountNumber);
        return resp;
    }

    public StB765BAccCreateAccount createResponseB765Fails(String accountNumber, String sortCode) {
        StB765BAccCreateAccount resp = new StB765BAccCreateAccount();
        StAccount stAccount = new StAccount();
        com.lloydsbanking.salsa.soap.fs.account.StError stError = new com.lloydsbanking.salsa.soap.fs.account.StError();
        resp.setStacc(stAccount);
        resp.setSterror(stError);
        resp.getStacc().setSortcode(sortCode);
        resp.setCustnum("227323270");
        resp.getSterror().setErrorno(131188);
        resp.getSterror().setErrormsg("rText");
        resp.getStacc().setAccno(accountNumber);
        return resp;
    }

    public String getBrandForChannel(String channel) {
        return com.lloydsbanking.salsa.brand.Channel.getBrandForChannel(com.lloydsbanking.salsa.brand.Channel.fromString(channel)).asString();
    }

    public E502Req createE502Request(DepositArrangement productArrangement) {
        E502Req req = requestFactory.convert(productArrangement.getAccountDetails().getAccountNumber(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode() + productArrangement.getAccountNumber());
        return req;
    }

    public E502Resp createE502RespWithoutError() {
        E502Resp e502Resp = new E502Resp();
        E502Result result = new E502Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(0);
        resultCondition.setSeverityCode(Byte.valueOf("0"));
        result.setResultCondition(resultCondition);
        e502Resp.setE502Result(result);
        return e502Resp;
    }

    public E502Resp createE502RespWithError() {
        E502Resp e502Resp = new E502Resp();
        E502Result result = new E502Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(12);
        resultCondition.setSeverityCode(Byte.valueOf("12"));
        result.setResultCondition(resultCondition);
        e502Resp.setE502Result(result);
        return e502Resp;
    }

    public E032Resp createE032Resp(byte severityCode) {
        E032Resp e032Resp = new E032Resp();
        e032Resp.setE032Result(new E032Result());
        e032Resp.getE032Result().setResultCondition(new ResultCondition());
        e032Resp.getE032Result().getResultCondition().setSeverityCode(severityCode);
        return e032Resp;
    }

    public StB766ARetrieveCBSAppGroup createB766Request(RequestHeader requestHeader, String sortCode) {
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders()).getBAPIHeader(), headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders()), headerRetriever.getContactPoint(requestHeader.getLloydsHeaders()).getContactPointId());
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = new StB766ARetrieveCBSAppGroup();
        stB766ARetrieveCBSAppGroup.setStheader(stHeader);
        stB766ARetrieveCBSAppGroup.setSortcode(sortCode);

        return stB766ARetrieveCBSAppGroup;

    }

    public StB766BRetrieveCBSAppGroup createB766Response(String appGroup, int errorNo) {
        StB766BRetrieveCBSAppGroup stB766BRetrieveCBSAppGroup = new StB766BRetrieveCBSAppGroup();
        com.lloydsbanking.salsa.soap.fs.account.StError stError = new com.lloydsbanking.salsa.soap.fs.account.StError();
        stError.setErrorno(errorNo);

        stB766BRetrieveCBSAppGroup.setSterror(stError);
        stB766BRetrieveCBSAppGroup.setCbsappgroup(appGroup);

        return stB766BRetrieveCBSAppGroup;

    }

    public String getCBSAppGrpForSortCode(String sortCode) {
        return sortCodeAppGrpMap.get(sortCode);
    }

    private HashMap<String, String> generateSortCodeAppGrpMap() {
        sortCodeAppGrpMap = new HashMap<String, String>();
        sortCodeAppGrpMap.put("007505", "01");
        sortCodeAppGrpMap.put("2342345", "01");
        sortCodeAppGrpMap.put("124234", "07");
        sortCodeAppGrpMap.put("779129", "07");
        sortCodeAppGrpMap.put("456457", "09");
        sortCodeAppGrpMap.put("1313124", "09");
        return sortCodeAppGrpMap;
    }

    public StB751BAppPerCCRegAuth createB751Response() {
        StB751BAppPerCCRegAuth b751response = new StB751BAppPerCCRegAuth();
        StError stError = new StError();
        stError.setErrorno(0);
        b751response.setSterror(stError);
        b751response.setTacver(24);
        b751response.setPartyidEmergingChannelUserId("777602816");
        return b751response;
    }

    public C658Resp createC658Resp() {
        C658Resp c658Resp = new C658Resp();
        c658Resp.setC658Result(new C658Result());
        c658Resp.getC658Result().setResultCondition(new ResultCondition());
        c658Resp.getC658Result().getResultCondition().setReasonCode(Integer.valueOf(0));
        return c658Resp;
    }

    public C234Resp createC234Resp() {
        C234Resp c234Resp = new C234Resp();
        c234Resp.setC234Result(new C234Result());
        c234Resp.getC234Result().setResultCondition(new ResultCondition());
        c234Resp.getC234Result().getResultCondition().setReasonCode(0);
        c234Resp.getC234Result().getResultCondition().setSeverityCode(new Byte("0"));
        return c234Resp;
    }

    public F060Resp createF060Resp() {
        F060Resp f060Resp = new F060Resp();
        f060Resp.setAdditionalDataIn(1);
        f060Resp.setF060Result(new F060Result());
        return f060Resp;
    }

    public RetrieveInvolvedPartyDetailsResponse createRetrieveInvolvedPartyResponse() {
        RetrieveInvolvedPartyDetailsResponse retrieveInvolvedPartyDetailsResponse = new RetrieveInvolvedPartyDetailsResponse();
        retrieveInvolvedPartyDetailsResponse.setInvolvedParty(new com.lloydsbanking.salsa.soap.soa.ipm.esb.ip.Individual());
        retrieveInvolvedPartyDetailsResponse.setResponseHeader(new com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.ResponseHeader());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("3");
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().add(new com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("0");
        return retrieveInvolvedPartyDetailsResponse;
    }

    public C241Resp createC241Resp() {
        C241Resp c241Resp = new C241Resp();
        c241Resp.setC241Result(new C241Result());
        c241Resp.getC241Result().setResultCondition(new ResultCondition());
        c241Resp.getC241Result().getResultCondition().setSeverityCode((byte) 1);
        c241Resp.getC241Result().getResultCondition().setReasonCode(Integer.valueOf(0));
        return c241Resp;
    }

    public RecordInvolvedPartyDetailsResponse createRecordInvolvedPartyResponse() {
        RecordInvolvedPartyDetailsResponse recordInvolvedPartyDetailsResponse = new RecordInvolvedPartyDetailsResponse();
        recordInvolvedPartyDetailsResponse.setResponseHeader(new com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.ResponseHeader());
        recordInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.ResultCondition());
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("3");
        return recordInvolvedPartyDetailsResponse;
    }

    public RecordDocumentMetaContentResponse createRecordDocContentResponseFails() {
        RecordDocumentMetaContentResponse response = new RecordDocumentMetaContentResponse();
        com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResultCondition resultCondition = new com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResultCondition();
        resultCondition.setSeverityCode("1");
        com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResponseHeader responseHeader = new com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResponseHeader();
        responseHeader.setResultConditions(resultCondition);
        response.setResponseHeader(responseHeader);
        return response;
    }

    @Transactional
    public Applications createNewApplicationWithNiNumber(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        return createApplicationWithNiNumber(applicationStatus, eidvStatus, asmDecision, applicationSubStatus, niNumber, productType);
    }
    @Transactional
    public Applications createNewApplicationWithNiNumberWithInterestRemittanceDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        return createApplicationWithNiNumberWithInterestRemittanceDetails(applicationStatus, eidvStatus, asmDecision, applicationSubStatus, niNumber, productType);
    }
    @Transactional
    public Applications createNewApplicationWithNiNumberWithoutGuardian(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        return createApplicationWithNiNumberWithoutGuardian(applicationStatus, eidvStatus, asmDecision, applicationSubStatus, niNumber, productType);
    }

    @Transactional
    @Modifying()
    public Applications createApplicationWithNiNumberWithInterestRemittanceDetails(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        Applications applications = new Applications(applicationTypes, productTypesSavings, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        applications.setSortCode("773315");
        applications.setArrangementNumber("08676168");
        applications.setSubStatus(applicationSubStatus);
        applications.setUserId("10.245.176.43");
        applications.setProductRequested("92");
        applications.setProductName("Classic Account");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH.mm.ss");
        Date d = null;
        try {
            d = (formatter.parse("26-NOV-14 09.24.31.000000000"));

            applications.setDateModified(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        applicationsDao.save(applications);

        applicationsDao.findAll();

        ApplicationParameterValues applicationParameterValues2 = new ApplicationParameterValues(applicationParameters9, applications, "AAGATEWAY", null);
        ApplicationParameterValues applicationParameterValues3 = new ApplicationParameterValues(applicationParameters1, applications, "P_CLASSIC", null);
        ApplicationParameterValues applicationParameterValues4 = new ApplicationParameterValues(applicationParameters11, applications, "true", null);

        ApplicationParameterValues applicationParameterValues5 = new ApplicationParameterValues(applicationParameters7, applications, "601", "Accept");
        ApplicationParameterValues applicationParameterValues6 = new ApplicationParameterValues(applicationParameters7, applications, "502", "Additional Data req - block A.");
        ApplicationParameterValues applicationParameterValues7 = new ApplicationParameterValues(applicationParameters7, applications, "503", "Additional Data req - block B.");
        ApplicationParameterValues applicationParameterValues8 = new ApplicationParameterValues(applicationParameters7, applications, "601", "Accept");

        ApplicationParameterValues applicationParameterValues9 = new ApplicationParameterValues(applicationParameters4, applications, "50", null);
        ApplicationParameterValues applicationParameterValues10 = new ApplicationParameterValues(applicationParameters10, applications, "0", null);
        ApplicationParameterValues applicationParameterValues11 = new ApplicationParameterValues(applicationParameters6, applications, "971461460", null);
        ApplicationParameterValues applicationParameterValues12 = new ApplicationParameterValues(applicationParameters2, applications, "Y", null);

        ApplicationParameterValues applicationParameterValues13 = new ApplicationParameterValues(applicationParameters3, applications, "502", null);

        applicationParameterValuesDao.save(applicationParameterValues2);
        applicationParameterValuesDao.save(applicationParameterValues3);
        applicationParameterValuesDao.save(applicationParameterValues4);
        applicationParameterValuesDao.save(applicationParameterValues5);
        applicationParameterValuesDao.save(applicationParameterValues6);
        applicationParameterValuesDao.save(applicationParameterValues7);
        applicationParameterValuesDao.save(applicationParameterValues8);
        applicationParameterValuesDao.save(applicationParameterValues9);
        applicationParameterValuesDao.save(applicationParameterValues10);
        applicationParameterValuesDao.save(applicationParameterValues11);
        applicationParameterValuesDao.save(applicationParameterValues12);
        applicationParameterValuesDao.save(applicationParameterValues13);

        applicationParameterValuesDao.findAll();

        Set<ApplicationParameterValues> applicationParameterValuesSet = new HashSet<>();
        applicationParameterValuesSet.add(applicationParameterValues2);
        applicationParameterValuesSet.add(applicationParameterValues3);
        applicationParameterValuesSet.add(applicationParameterValues4);
        applicationParameterValuesSet.add(applicationParameterValues5);
        applicationParameterValuesSet.add(applicationParameterValues6);
        applicationParameterValuesSet.add(applicationParameterValues7);
        applicationParameterValuesSet.add(applicationParameterValues8);
        applicationParameterValuesSet.add(applicationParameterValues9);
        applicationParameterValuesSet.add(applicationParameterValues10);
        applicationParameterValuesSet.add(applicationParameterValues11);
        applicationParameterValuesSet.add(applicationParameterValues12);
        applicationParameterValuesSet.add(applicationParameterValues13);

        applications.setApplicationParameterValues(toSet(applicationParameterValuesDao.findAll()));

        ApplicationFeatureTypes applicationFeatureTypes1 = new ApplicationFeatureTypes("OD", "Overdraft");
        ApplicationFeatureTypes applicationFeatureTypes2 = new ApplicationFeatureTypes("CHQBK", "ChequeBook");
        ApplicationFeatureTypes applicationFeatureTypes3 = new ApplicationFeatureTypes("IBRA", "IB Registration Accepted");
        ApplicationFeatureTypes applicationFeatureTypes4 = new ApplicationFeatureTypes("BENPA", "Benefit Payments");
        ApplicationFeatureTypes applicationFeatureTypes5 = new ApplicationFeatureTypes("CRD", "Capture Remittance Setails");

        applicationFeatureTypesDao.save(applicationFeatureTypes1);
        applicationFeatureTypesDao.save(applicationFeatureTypes2);
        applicationFeatureTypesDao.save(applicationFeatureTypes3);
        applicationFeatureTypesDao.save(applicationFeatureTypes4);
        applicationFeatureTypesDao.save(applicationFeatureTypes5);

        applicationFeatureTypesDao.findAll();

        ApplicationFeatures applicationFeatures1 = new ApplicationFeatures();
        applicationFeatures1.setApplications(applications);
        applicationFeatures1.setApplicationFeatureTypes(applicationFeatureTypes2);
        applicationFeatures1.setFeatureRequired("N");

        ApplicationFeatures applicationFeatures2 = new ApplicationFeatures();
        applicationFeatures2.setApplications(applications);
        applicationFeatures2.setApplicationFeatureTypes(applicationFeatureTypes1);
        applicationFeatures2.setFeatureRequired("N");
        applicationFeatures2.setAmount(new BigDecimal(0));

        ApplicationFeatures applicationFeatures3 = new ApplicationFeatures();
        applicationFeatures3.setApplications(applications);
        applicationFeatures3.setApplicationFeatureTypes(applicationFeatureTypes3);

        ApplicationFeatures applicationFeatures4 = new ApplicationFeatures();
        applicationFeatures4.setApplications(applications);
        applicationFeatures4.setApplicationFeatureTypes(applicationFeatureTypes4);
        applicationFeatures4.setFeatureRequired("Y");

        ApplicationFeatures applicationFeatures5 = new ApplicationFeatures();
        applicationFeatures5.setApplications(applications);
        applicationFeatures5.setApplicationFeatureTypes(applicationFeatureTypes5);
        applicationFeatures5.setFeatureRequired("Y");
        applicationFeatures5.setAccountNo("123");
        applicationFeatures5.setSortCode("124");

        Set<ApplicationFeatures> applicationFeaturesSet = new HashSet<>();
        applicationFeaturesSet.add(applicationFeatures1);
        applicationFeaturesSet.add(applicationFeatures2);
        applicationFeaturesSet.add(applicationFeatures3);
        applicationFeaturesSet.add(applicationFeatures4);
        applicationFeaturesSet.add(applicationFeatures5);
        applications.setApplicationFeatures(applicationFeaturesSet);

        Iterable it = applicationsDao.findAll();

        Individuals individuals = new Individuals();
        individuals.setOcisId("227323270");
        individuals.setCidpersid("+00211135806");
        individuals.setEmailId("GalaxyTestAccount02@LloydsTSB.co.uk");
        individuals.setMaritalStatus("001");
        individuals.setNationality("GBR");
        individuals.setDateOfBirth(new Date());
        individuals.setEmploymentStatus("006");
        individuals.setResidentialStatus("001");
        individuals.setFirstName("meera");
        individuals.setLastName("radha");
        individuals.setGender("001");
        individuals.setAnnualGrossIncome(4800L);
        individuals.setYearsCurrEmp((byte) 0);
        individuals.setMonthsCurrEmp((short) 0);
        individuals.setCreditCardHeld("N");
        individuals.setTitle("Mr");
        individuals.setStaffInd('N');
        individuals.setYearsWithBank((short) 29);
        individuals.setMonthsWithBank((short) 0);
        individuals.setCountryOfBirth("United Kingdom");
        individuals.setOccupation("001");
        individuals.setPlaceOfBirth("Place of Birth");


        StreetAddresses streetAddresses = new StreetAddresses("addressLine1", "addressLine2", "addressLine3", "city", null, "country", "SE1-2EA", new Byte("5"), Short.valueOf("5"), "Y", "buildingName", "1", "1A", "street", "subBuildingName", "district", "addressLine4");
        IndividualAddresses individualAddresses = new IndividualAddresses(streetAddresses, null, 1L);
        Set<IndividualAddresses> individualAddressesSet = new HashSet<>();
        individualAddressesSet.add(individualAddresses);
        streetAddresses.setIndividualAddresseses(individualAddressesSet);

        streetAddressesDao.save(streetAddresses);
        streetAddressesDao.findAll();

        individuals.setIndividualAddresses(individualAddressesSet);
        individualsDao.save(individuals);
        individualsDao.findAll();

/*        TelephoneAddresses telephoneAddresses = new TelephoneAddresses();
        telephoneAddresses.setTelephoneNumber(new BigDecimal("9123456789"));
        telephoneAddresses.setTelephoneAddressTypes(telephoneAddressTypes);
        telephoneAddresses.setIndividuals(individuals);
        Set<TelephoneAddresses> telephoneAddressesSet = new HashSet<>();
        telephoneAddressesSet.add(telephoneAddresses);
        telephoneAddressesDao.save(telephoneAddressesSet);
        telephoneAddressesDao.findAll();

        individuals.setTelephoneAddresses(telephoneAddressesSet);*/


        individualAddresses.setIndividuals(individuals);
        individualAddressesSet.add(individualAddresses);
        individualAddressesDao.save(individualAddresses);
        individualAddressesDao.findAll();

        individualsDao.save(individuals);
        individualsDao.findAll();
        PartyApplications partyApplications = new PartyApplications();
        partyApplications.setApplicationPartyRoles(roles);
        partyApplications.setScoringStatus(asmDecision);
        partyApplications.setCustomerSegments("3");
        partyApplications.setKycStatus(eidvStatus);
        partyApplications.setLockId(0L);
        //partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);
        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();

        Set<PartyApplications> partyApplicationsSet = new HashSet<>();
        partyApplicationsSet.add(partyApplications);


        //kycEvidenceDetailsDao.save(kycEvidenceDetails);

        partyApplications.setKycStatus(eidvStatus);
        partyApplications.setLockId(0L);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);

        KycEvidenceDetails kycEvidenceDetails = new KycEvidenceDetails();
        kycEvidenceDetails.setPartyApplications(partyApplications);
        kycEvidenceDetails.setNiNumber(niNumber);
        Set<KycEvidenceDetails> kycEvidenceDetailsSet = new HashSet<>();
        kycEvidenceDetailsSet.add(kycEvidenceDetails);
        partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);

        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();


        partyApplicationsSet.add(partyApplications);
        individuals.getIndividualAddresses().add(individualAddresses);
        individuals.setPartyApplications(partyApplicationsSet);

        //

        Individuals individuals1 = new Individuals();
        individuals1.setOcisId("227323271");
        individuals1.setCidpersid("+00211135806");
        individuals1.setEmailId("GalaxyTestAccount02@LloydsTSB.co.uk");
        individuals1.setMaritalStatus("001");
        individuals1.setNationality("GBR");
        individuals1.setDateOfBirth(new Date());
        individuals1.setEmploymentStatus("006");
        individuals1.setResidentialStatus("001");
        individuals1.setFirstName("meera");
        individuals1.setLastName("radha");
        individuals1.setGender("001");
        individuals1.setAnnualGrossIncome(4800L);
        individuals1.setYearsCurrEmp((byte) 0);
        individuals1.setMonthsCurrEmp((short) 0);
        individuals1.setCreditCardHeld("N");
        individuals1.setTitle("Mr");
        individuals1.setStaffInd('N');
        individuals1.setYearsWithBank((short) 29);
        individuals1.setMonthsWithBank((short) 0);
        individuals1.setCountryOfBirth("United Kingdom");
        individuals1.setOccupation("001");

        StreetAddresses streetAddresses1 = new StreetAddresses("addressLine1", "addressLine2", "addressLine3", "city", null, "country", "SE1-2EA", new Byte("5"), Short.valueOf("5"), "Y", "buildingName", "1", "1A", "street", "subBuildingName", "district", "addressLine4");
        IndividualAddresses individualAddresses1 = new IndividualAddresses(streetAddresses1, null, 1L);
        Set<IndividualAddresses> individualAddressesSet1 = new HashSet<>();
        individualAddressesSet1.add(individualAddresses1);
        streetAddresses1.setIndividualAddresseses(individualAddressesSet1);

        streetAddressesDao.save(streetAddresses1);
        streetAddressesDao.findAll();

        individuals.setIndividualAddresses(individualAddressesSet1);
        individualsDao.save(individuals1);
        individualsDao.findAll();


        PartyRelated partyRelated = new PartyRelated();
        partyRelated.setIndividualsByRelatedPartyId(individuals1);
        partyRelated.setIndividualsByPartyId(individuals);
        partyRelatedDao.save(partyRelated);
        partyRelatedDao.findAll();


        Set<PartyRelated> partyRelatedSet = new HashSet<>();
        partyRelatedSet.add(partyRelated);
        individuals.setPartyRelatedForPartyId(partyRelatedSet);
        individuals1.setPartyRelatedForRelatedPartyId(partyRelatedSet);
        individualsDao.findAll();

/*        TelephoneAddresses telephoneAddresses = new TelephoneAddresses();
        telephoneAddresses.setTelephoneNumber(new BigDecimal("9123456789"));
        telephoneAddresses.setTelephoneAddressTypes(telephoneAddressTypes);
        telephoneAddresses.setIndividuals(individuals);
        Set<TelephoneAddresses> telephoneAddressesSet = new HashSet<>();
        telephoneAddressesSet.add(telephoneAddresses);
        telephoneAddressesDao.save(telephoneAddressesSet);
        telephoneAddressesDao.findAll();

        individuals.setTelephoneAddresses(telephoneAddressesSet);*/


        individualAddresses1.setIndividuals(individuals1);
        individualAddressesSet1.add(individualAddresses1);
        individualAddressesDao.save(individualAddresses1);
        individualAddressesDao.findAll();
        PartyApplications partyApplications1 = new PartyApplications();
        partyApplications1.setApplicationPartyRoles(rolesGuardian);
        partyApplications1.setScoringStatus(asmDecision);
        partyApplications1.setCustomerSegments("3");
        partyApplications1.setKycStatus(eidvStatus);
        partyApplications1.setLockId(1L);
        //partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);
        partyApplications1.setApplications(applications);
        partyApplications1.setIndividuals(individuals1);
        partyApplicationsDao.save(partyApplications1);
        partyApplicationsDao.findAll();

        partyApplicationsSet.add(partyApplications1);
        individuals1.getIndividualAddresses().add(individualAddresses1);
        individuals1.getPartyApplications().add(partyApplications1);

        Set<PartyApplications> partyApplicationsSetAll = new HashSet<>();
        partyApplicationsSetAll.add(partyApplications);
        partyApplicationsSetAll.add(partyApplications1);
        applications.setPartyApplications(partyApplicationsSetAll);
        return applications;
    }
    @Transactional
    @Modifying()
    public Applications createApplicationWithNiNumberWithoutGuardian(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        Applications applications = new Applications(applicationTypes, productTypesSavings, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        applications.setSortCode("773315");
        applications.setArrangementNumber("08676168");
        applications.setSubStatus(applicationSubStatus);
        applications.setUserId("10.245.176.43");
        applications.setProductRequested("92");
        applications.setProductName("Classic Account");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH.mm.ss");
        Date d = null;
        try {
            d = (formatter.parse("26-NOV-14 09.24.31.000000000"));

            applications.setDateModified(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        applicationsDao.save(applications);

        applicationsDao.findAll();

        ApplicationParameterValues applicationParameterValues2 = new ApplicationParameterValues(applicationParameters9, applications, "AAGATEWAY", null);
        ApplicationParameterValues applicationParameterValues3 = new ApplicationParameterValues(applicationParameters1, applications, "P_CLASSIC", null);
        ApplicationParameterValues applicationParameterValues4 = new ApplicationParameterValues(applicationParameters11, applications, "true", null);

        ApplicationParameterValues applicationParameterValues5 = new ApplicationParameterValues(applicationParameters7, applications, "601", "Accept");
        ApplicationParameterValues applicationParameterValues6 = new ApplicationParameterValues(applicationParameters7, applications, "502", "Additional Data req - block A.");
        ApplicationParameterValues applicationParameterValues7 = new ApplicationParameterValues(applicationParameters7, applications, "503", "Additional Data req - block B.");
        ApplicationParameterValues applicationParameterValues8 = new ApplicationParameterValues(applicationParameters7, applications, "601", "Accept");

        ApplicationParameterValues applicationParameterValues9 = new ApplicationParameterValues(applicationParameters4, applications, "50", null);
        ApplicationParameterValues applicationParameterValues10 = new ApplicationParameterValues(applicationParameters10, applications, "0", null);
        ApplicationParameterValues applicationParameterValues11 = new ApplicationParameterValues(applicationParameters6, applications, "971461460", null);
        ApplicationParameterValues applicationParameterValues12 = new ApplicationParameterValues(applicationParameters2, applications, "Y", null);

        ApplicationParameterValues applicationParameterValues13 = new ApplicationParameterValues(applicationParameters3, applications, "502", null);

        applicationParameterValuesDao.save(applicationParameterValues2);
        applicationParameterValuesDao.save(applicationParameterValues3);
        applicationParameterValuesDao.save(applicationParameterValues4);
        applicationParameterValuesDao.save(applicationParameterValues5);
        applicationParameterValuesDao.save(applicationParameterValues6);
        applicationParameterValuesDao.save(applicationParameterValues7);
        applicationParameterValuesDao.save(applicationParameterValues8);
        applicationParameterValuesDao.save(applicationParameterValues9);
        applicationParameterValuesDao.save(applicationParameterValues10);
        applicationParameterValuesDao.save(applicationParameterValues11);
        applicationParameterValuesDao.save(applicationParameterValues12);
        applicationParameterValuesDao.save(applicationParameterValues13);

        applicationParameterValuesDao.findAll();

        Set<ApplicationParameterValues> applicationParameterValuesSet = new HashSet<>();
        applicationParameterValuesSet.add(applicationParameterValues2);
        applicationParameterValuesSet.add(applicationParameterValues3);
        applicationParameterValuesSet.add(applicationParameterValues4);
        applicationParameterValuesSet.add(applicationParameterValues5);
        applicationParameterValuesSet.add(applicationParameterValues6);
        applicationParameterValuesSet.add(applicationParameterValues7);
        applicationParameterValuesSet.add(applicationParameterValues8);
        applicationParameterValuesSet.add(applicationParameterValues9);
        applicationParameterValuesSet.add(applicationParameterValues10);
        applicationParameterValuesSet.add(applicationParameterValues11);
        applicationParameterValuesSet.add(applicationParameterValues12);
        applicationParameterValuesSet.add(applicationParameterValues13);

        applications.setApplicationParameterValues(toSet(applicationParameterValuesDao.findAll()));

        ApplicationFeatureTypes applicationFeatureTypes1 = new ApplicationFeatureTypes("OD", "Overdraft");
        ApplicationFeatureTypes applicationFeatureTypes2 = new ApplicationFeatureTypes("CHQBK", "ChequeBook");
        ApplicationFeatureTypes applicationFeatureTypes3 = new ApplicationFeatureTypes("IBRA", "IB Registration Accepted");
        ApplicationFeatureTypes applicationFeatureTypes4 = new ApplicationFeatureTypes("BENPA", "Benefit Payments");

        applicationFeatureTypesDao.save(applicationFeatureTypes1);
        applicationFeatureTypesDao.save(applicationFeatureTypes2);
        applicationFeatureTypesDao.save(applicationFeatureTypes3);
        applicationFeatureTypesDao.save(applicationFeatureTypes4);

        applicationFeatureTypesDao.findAll();

        ApplicationFeatures applicationFeatures1 = new ApplicationFeatures();
        applicationFeatures1.setApplications(applications);
        applicationFeatures1.setApplicationFeatureTypes(applicationFeatureTypes2);
        applicationFeatures1.setFeatureRequired("N");

        ApplicationFeatures applicationFeatures2 = new ApplicationFeatures();
        applicationFeatures2.setApplications(applications);
        applicationFeatures2.setApplicationFeatureTypes(applicationFeatureTypes1);
        applicationFeatures2.setFeatureRequired("N");
        applicationFeatures2.setAmount(new BigDecimal(0));

        ApplicationFeatures applicationFeatures3 = new ApplicationFeatures();
        applicationFeatures3.setApplications(applications);
        applicationFeatures3.setApplicationFeatureTypes(applicationFeatureTypes3);

        ApplicationFeatures applicationFeatures4 = new ApplicationFeatures();
        applicationFeatures4.setApplications(applications);
        applicationFeatures4.setApplicationFeatureTypes(applicationFeatureTypes4);
        applicationFeatures4.setFeatureRequired("Y");

        Set<ApplicationFeatures> applicationFeaturesSet = new HashSet<>();
        applicationFeaturesSet.add(applicationFeatures1);
        applicationFeaturesSet.add(applicationFeatures2);
        applicationFeaturesSet.add(applicationFeatures3);
        applicationFeaturesSet.add(applicationFeatures4);
        applications.setApplicationFeatures(applicationFeaturesSet);

        Iterable it = applicationsDao.findAll();

        Individuals individuals = new Individuals();
        individuals.setOcisId("227323270");
        individuals.setCidpersid("+00211135806");
        individuals.setEmailId("GalaxyTestAccount02@LloydsTSB.co.uk");
        individuals.setMaritalStatus("001");
        individuals.setNationality("GBR");
        individuals.setDateOfBirth(new Date());
        individuals.setEmploymentStatus("006");
        individuals.setResidentialStatus("001");
        individuals.setFirstName("meera");
        individuals.setLastName("radha");
        individuals.setGender("001");
        individuals.setAnnualGrossIncome(4800L);
        individuals.setYearsCurrEmp((byte) 0);
        individuals.setMonthsCurrEmp((short) 0);
        individuals.setCreditCardHeld("N");
        individuals.setTitle("Mr");
        individuals.setStaffInd('N');
        individuals.setYearsWithBank((short) 29);
        individuals.setMonthsWithBank((short) 0);
        individuals.setCountryOfBirth("United Kingdom");
        individuals.setOccupation("001");
        individuals.setPlaceOfBirth("Place of Birth");

        StreetAddresses streetAddresses = new StreetAddresses("addressLine1", "addressLine2", "addressLine3", "city", null, "country", "SE1-2EA", new Byte("5"), Short.valueOf("5"), "Y", "buildingName", "1", "1A", "street", "subBuildingName", "district", "addressLine4");
        IndividualAddresses individualAddresses = new IndividualAddresses(streetAddresses, null, 1L);
        Set<IndividualAddresses> individualAddressesSet = new HashSet<>();
        individualAddressesSet.add(individualAddresses);
        streetAddresses.setIndividualAddresseses(individualAddressesSet);

        streetAddressesDao.save(streetAddresses);
        streetAddressesDao.findAll();

        individuals.setIndividualAddresses(individualAddressesSet);
        individualsDao.save(individuals);
        individualsDao.findAll();

/*        TelephoneAddresses telephoneAddresses = new TelephoneAddresses();
        telephoneAddresses.setTelephoneNumber(new BigDecimal("9123456789"));
        telephoneAddresses.setTelephoneAddressTypes(telephoneAddressTypes);
        telephoneAddresses.setIndividuals(individuals);
        Set<TelephoneAddresses> telephoneAddressesSet = new HashSet<>();
        telephoneAddressesSet.add(telephoneAddresses);
        telephoneAddressesDao.save(telephoneAddressesSet);
        telephoneAddressesDao.findAll();

        individuals.setTelephoneAddresses(telephoneAddressesSet);*/


        individualAddresses.setIndividuals(individuals);
        individualAddressesSet.add(individualAddresses);
        individualAddressesDao.save(individualAddresses);
        individualAddressesDao.findAll();

        individualsDao.save(individuals);
        individualsDao.findAll();
        PartyApplications partyApplications = new PartyApplications();
        partyApplications.setApplicationPartyRoles(roles);
        partyApplications.setScoringStatus(asmDecision);
        partyApplications.setCustomerSegments("3");
        partyApplications.setKycStatus(eidvStatus);
        partyApplications.setLockId(0L);
        //partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);
        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();

        Set<PartyApplications> partyApplicationsSet = new HashSet<>();
        partyApplicationsSet.add(partyApplications);


        //kycEvidenceDetailsDao.save(kycEvidenceDetails);

        partyApplications.setKycStatus(eidvStatus);
        partyApplications.setLockId(0L);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);

        KycEvidenceDetails kycEvidenceDetails = new KycEvidenceDetails();
        kycEvidenceDetails.setPartyApplications(partyApplications);
        kycEvidenceDetails.setNiNumber(niNumber);
        Set<KycEvidenceDetails> kycEvidenceDetailsSet = new HashSet<>();
        kycEvidenceDetailsSet.add(kycEvidenceDetails);
        partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);

        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();


        partyApplicationsSet.add(partyApplications);
        individuals.getIndividualAddresses().add(individualAddresses);

        individuals.setPartyApplications(partyApplicationsSet);

        applications.setPartyApplications(partyApplicationsSet);
        return applications;
    }

    @Transactional
    @Modifying()
    public Applications createApplicationWithNiNumber(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        Applications applications = new Applications(applicationTypes, productTypesSavings, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        applications.setSortCode("773315");
        applications.setArrangementNumber("08676168");
        applications.setSubStatus(applicationSubStatus);
        applications.setUserId("10.245.176.43");
        applications.setProductRequested("92");
        applications.setProductName("Classic Account");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH.mm.ss");
        Date d = null;
        try {
            d = (formatter.parse("26-NOV-14 09.24.31.000000000"));

            applications.setDateModified(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        applicationsDao.save(applications);

        applicationsDao.findAll();

        ApplicationParameterValues applicationParameterValues2 = new ApplicationParameterValues(applicationParameters9, applications, "AAGATEWAY", null);
        ApplicationParameterValues applicationParameterValues3 = new ApplicationParameterValues(applicationParameters1, applications, "P_CLASSIC", null);
        ApplicationParameterValues applicationParameterValues4 = new ApplicationParameterValues(applicationParameters11, applications, "true", null);

        ApplicationParameterValues applicationParameterValues5 = new ApplicationParameterValues(applicationParameters7, applications, "601", "Accept");
        ApplicationParameterValues applicationParameterValues6 = new ApplicationParameterValues(applicationParameters7, applications, "502", "Additional Data req - block A.");
        ApplicationParameterValues applicationParameterValues7 = new ApplicationParameterValues(applicationParameters7, applications, "503", "Additional Data req - block B.");
        ApplicationParameterValues applicationParameterValues8 = new ApplicationParameterValues(applicationParameters7, applications, "601", "Accept");

        ApplicationParameterValues applicationParameterValues9 = new ApplicationParameterValues(applicationParameters4, applications, "50", null);
        ApplicationParameterValues applicationParameterValues10 = new ApplicationParameterValues(applicationParameters10, applications, "0", null);
        ApplicationParameterValues applicationParameterValues11 = new ApplicationParameterValues(applicationParameters6, applications, "971461460", null);
        ApplicationParameterValues applicationParameterValues12 = new ApplicationParameterValues(applicationParameters2, applications, "Y", null);

        ApplicationParameterValues applicationParameterValues13 = new ApplicationParameterValues(applicationParameters3, applications, "502", null);

        applicationParameterValuesDao.save(applicationParameterValues2);
        applicationParameterValuesDao.save(applicationParameterValues3);
        applicationParameterValuesDao.save(applicationParameterValues4);
        applicationParameterValuesDao.save(applicationParameterValues5);
        applicationParameterValuesDao.save(applicationParameterValues6);
        applicationParameterValuesDao.save(applicationParameterValues7);
        applicationParameterValuesDao.save(applicationParameterValues8);
        applicationParameterValuesDao.save(applicationParameterValues9);
        applicationParameterValuesDao.save(applicationParameterValues10);
        applicationParameterValuesDao.save(applicationParameterValues11);
        applicationParameterValuesDao.save(applicationParameterValues12);
        applicationParameterValuesDao.save(applicationParameterValues13);

        applicationParameterValuesDao.findAll();

        Set<ApplicationParameterValues> applicationParameterValuesSet = new HashSet<>();
        applicationParameterValuesSet.add(applicationParameterValues2);
        applicationParameterValuesSet.add(applicationParameterValues3);
        applicationParameterValuesSet.add(applicationParameterValues4);
        applicationParameterValuesSet.add(applicationParameterValues5);
        applicationParameterValuesSet.add(applicationParameterValues6);
        applicationParameterValuesSet.add(applicationParameterValues7);
        applicationParameterValuesSet.add(applicationParameterValues8);
        applicationParameterValuesSet.add(applicationParameterValues9);
        applicationParameterValuesSet.add(applicationParameterValues10);
        applicationParameterValuesSet.add(applicationParameterValues11);
        applicationParameterValuesSet.add(applicationParameterValues12);
        applicationParameterValuesSet.add(applicationParameterValues13);

        applications.setApplicationParameterValues(toSet(applicationParameterValuesDao.findAll()));

        ApplicationFeatureTypes applicationFeatureTypes1 = new ApplicationFeatureTypes("OD", "Overdraft");
        ApplicationFeatureTypes applicationFeatureTypes2 = new ApplicationFeatureTypes("CHQBK", "ChequeBook");
        ApplicationFeatureTypes applicationFeatureTypes3 = new ApplicationFeatureTypes("IBRA", "IB Registration Accepted");
        ApplicationFeatureTypes applicationFeatureTypes4 = new ApplicationFeatureTypes("BENPA", "Benefit Payments");

        applicationFeatureTypesDao.save(applicationFeatureTypes1);
        applicationFeatureTypesDao.save(applicationFeatureTypes2);
        applicationFeatureTypesDao.save(applicationFeatureTypes3);
        applicationFeatureTypesDao.save(applicationFeatureTypes4);

        applicationFeatureTypesDao.findAll();

        ApplicationFeatures applicationFeatures1 = new ApplicationFeatures();
        applicationFeatures1.setApplications(applications);
        applicationFeatures1.setApplicationFeatureTypes(applicationFeatureTypes2);
        applicationFeatures1.setFeatureRequired("N");

        ApplicationFeatures applicationFeatures2 = new ApplicationFeatures();
        applicationFeatures2.setApplications(applications);
        applicationFeatures2.setApplicationFeatureTypes(applicationFeatureTypes1);
        applicationFeatures2.setFeatureRequired("N");
        applicationFeatures2.setAmount(new BigDecimal(0));

        ApplicationFeatures applicationFeatures3 = new ApplicationFeatures();
        applicationFeatures3.setApplications(applications);
        applicationFeatures3.setApplicationFeatureTypes(applicationFeatureTypes3);

        ApplicationFeatures applicationFeatures4 = new ApplicationFeatures();
        applicationFeatures4.setApplications(applications);
        applicationFeatures4.setApplicationFeatureTypes(applicationFeatureTypes4);
        applicationFeatures4.setFeatureRequired("Y");

        Set<ApplicationFeatures> applicationFeaturesSet = new HashSet<>();
        applicationFeaturesSet.add(applicationFeatures1);
        applicationFeaturesSet.add(applicationFeatures2);
        applicationFeaturesSet.add(applicationFeatures3);
        applicationFeaturesSet.add(applicationFeatures4);
        applications.setApplicationFeatures(applicationFeaturesSet);

        Iterable it = applicationsDao.findAll();

        Individuals individuals = new Individuals();
        individuals.setOcisId("227323270");
        individuals.setCidpersid("+00211135806");
        individuals.setEmailId("GalaxyTestAccount02@LloydsTSB.co.uk");
        individuals.setMaritalStatus("001");
        individuals.setNationality("GBR");
        individuals.setDateOfBirth(new Date());
        individuals.setEmploymentStatus("006");
        individuals.setResidentialStatus("001");
        individuals.setFirstName("meera");
        individuals.setLastName("radha");
        individuals.setGender("001");
        individuals.setAnnualGrossIncome(4800L);
        individuals.setYearsCurrEmp((byte) 0);
        individuals.setMonthsCurrEmp((short) 0);
        individuals.setCreditCardHeld("N");
        individuals.setTitle("Mr");
        individuals.setStaffInd('N');
        individuals.setYearsWithBank((short) 29);
        individuals.setMonthsWithBank((short) 0);
        individuals.setCountryOfBirth("United Kingdom");
        individuals.setOccupation("001");
        individuals.setPlaceOfBirth("Place of Birth");


        StreetAddresses streetAddresses = new StreetAddresses("addressLine1", "addressLine2", "addressLine3", "city", null, "country", "SE1-2EA", new Byte("5"), Short.valueOf("5"), "Y", "buildingName", "1", "1A", "street", "subBuildingName", "district", "addressLine4");
        IndividualAddresses individualAddresses = new IndividualAddresses(streetAddresses, null, 1L);
        Set<IndividualAddresses> individualAddressesSet = new HashSet<>();
        individualAddressesSet.add(individualAddresses);
        streetAddresses.setIndividualAddresseses(individualAddressesSet);

        streetAddressesDao.save(streetAddresses);
        streetAddressesDao.findAll();

        individuals.setIndividualAddresses(individualAddressesSet);
        individualsDao.save(individuals);
        individualsDao.findAll();

/*        TelephoneAddresses telephoneAddresses = new TelephoneAddresses();
        telephoneAddresses.setTelephoneNumber(new BigDecimal("9123456789"));
        telephoneAddresses.setTelephoneAddressTypes(telephoneAddressTypes);
        telephoneAddresses.setIndividuals(individuals);
        Set<TelephoneAddresses> telephoneAddressesSet = new HashSet<>();
        telephoneAddressesSet.add(telephoneAddresses);
        telephoneAddressesDao.save(telephoneAddressesSet);
        telephoneAddressesDao.findAll();

        individuals.setTelephoneAddresses(telephoneAddressesSet);*/


        individualAddresses.setIndividuals(individuals);
        individualAddressesSet.add(individualAddresses);
        individualAddressesDao.save(individualAddresses);
        individualAddressesDao.findAll();

        individualsDao.save(individuals);
        individualsDao.findAll();
        PartyApplications partyApplications = new PartyApplications();
        partyApplications.setApplicationPartyRoles(roles);
        partyApplications.setScoringStatus(asmDecision);
        partyApplications.setCustomerSegments("3");
        partyApplications.setKycStatus(eidvStatus);
        partyApplications.setLockId(0L);
        //partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);
        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();

        Set<PartyApplications> partyApplicationsSet = new HashSet<>();
        partyApplicationsSet.add(partyApplications);


        //kycEvidenceDetailsDao.save(kycEvidenceDetails);

        partyApplications.setKycStatus(eidvStatus);
        partyApplications.setLockId(0L);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);

        KycEvidenceDetails kycEvidenceDetails = new KycEvidenceDetails();
        kycEvidenceDetails.setPartyApplications(partyApplications);
        kycEvidenceDetails.setNiNumber(niNumber);
        Set<KycEvidenceDetails> kycEvidenceDetailsSet = new HashSet<>();
        kycEvidenceDetailsSet.add(kycEvidenceDetails);
        partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);

        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();


        partyApplicationsSet.add(partyApplications);
        individuals.getIndividualAddresses().add(individualAddresses);
        individuals.setPartyApplications(partyApplicationsSet);

        //

        Individuals individuals1 = new Individuals();
        individuals1.setOcisId("227323271");
        individuals1.setCidpersid("+00211135806");
        individuals1.setEmailId("GalaxyTestAccount02@LloydsTSB.co.uk");
        individuals1.setMaritalStatus("001");
        individuals1.setNationality("GBR");
        individuals1.setDateOfBirth(new Date());
        individuals1.setEmploymentStatus("006");
        individuals1.setResidentialStatus("001");
        individuals1.setFirstName("meera");
        individuals1.setLastName("radha");
        individuals1.setGender("001");
        individuals1.setAnnualGrossIncome(4800L);
        individuals1.setYearsCurrEmp((byte) 0);
        individuals1.setMonthsCurrEmp((short) 0);
        individuals1.setCreditCardHeld("N");
        individuals1.setTitle("Mr");
        individuals1.setStaffInd('N');
        individuals1.setYearsWithBank((short) 29);
        individuals1.setMonthsWithBank((short) 0);
        individuals1.setCountryOfBirth("United Kingdom");
        individuals1.setOccupation("001");

        StreetAddresses streetAddresses1 = new StreetAddresses("addressLine1", "addressLine2", "addressLine3", "city", null, "country", "SE1-2EA", new Byte("5"), Short.valueOf("5"), "Y", "buildingName", "1", "1A", "street", "subBuildingName", "district", "addressLine4");
        IndividualAddresses individualAddresses1 = new IndividualAddresses(streetAddresses1, null, 1L);
        Set<IndividualAddresses> individualAddressesSet1 = new HashSet<>();
        individualAddressesSet1.add(individualAddresses1);
        streetAddresses1.setIndividualAddresseses(individualAddressesSet1);

        streetAddressesDao.save(streetAddresses1);
        streetAddressesDao.findAll();

        individuals.setIndividualAddresses(individualAddressesSet1);
        individualsDao.save(individuals1);
        individualsDao.findAll();


        PartyRelated partyRelated = new PartyRelated();
        partyRelated.setIndividualsByRelatedPartyId(individuals1);
        partyRelated.setIndividualsByPartyId(individuals);
        partyRelatedDao.save(partyRelated);
        partyRelatedDao.findAll();


        Set<PartyRelated> partyRelatedSet = new HashSet<>();
        partyRelatedSet.add(partyRelated);
        individuals.setPartyRelatedForPartyId(partyRelatedSet);
        individuals1.setPartyRelatedForRelatedPartyId(partyRelatedSet);
        individualsDao.findAll();

/*        TelephoneAddresses telephoneAddresses = new TelephoneAddresses();
        telephoneAddresses.setTelephoneNumber(new BigDecimal("9123456789"));
        telephoneAddresses.setTelephoneAddressTypes(telephoneAddressTypes);
        telephoneAddresses.setIndividuals(individuals);
        Set<TelephoneAddresses> telephoneAddressesSet = new HashSet<>();
        telephoneAddressesSet.add(telephoneAddresses);
        telephoneAddressesDao.save(telephoneAddressesSet);
        telephoneAddressesDao.findAll();

        individuals.setTelephoneAddresses(telephoneAddressesSet);*/


        individualAddresses1.setIndividuals(individuals1);
        individualAddressesSet1.add(individualAddresses1);
        individualAddressesDao.save(individualAddresses1);
        individualAddressesDao.findAll();
        PartyApplications partyApplications1 = new PartyApplications();
        partyApplications1.setApplicationPartyRoles(rolesGuardian);
        partyApplications1.setScoringStatus(asmDecision);
        partyApplications1.setCustomerSegments("3");
        partyApplications1.setKycStatus(eidvStatus);
        partyApplications1.setLockId(1L);
        //partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);
        partyApplications1.setApplications(applications);
        partyApplications1.setIndividuals(individuals1);
        partyApplicationsDao.save(partyApplications1);
        partyApplicationsDao.findAll();

        partyApplicationsSet.add(partyApplications1);
        individuals1.getIndividualAddresses().add(individualAddresses1);
        individuals1.getPartyApplications().add(partyApplications1);

        Set<PartyApplications> partyApplicationsSetAll = new HashSet<>();
        partyApplicationsSetAll.add(partyApplications);
        partyApplicationsSetAll.add(partyApplications1);
        applications.setPartyApplications(partyApplicationsSetAll);
        return applications;
    }

    public RetrieveDocumentMetaContentResponse createRetrieveDocContentResponse() throws ParseException, DatatypeConfigurationException {
        RetrieveDocumentMetaContentResponse retrieveDocumentMetaContentResponse = new RetrieveDocumentMetaContentResponse();
        com.lloydsbanking.salsa.soap.soa.dm.esb.ri.DocumentContent documentContent = new com.lloydsbanking.salsa.soap.soa.dm.esb.ri.DocumentContent();
        com.lloydsbanking.salsa.soap.soa.dm.esb.ri.ContentType contentType = new com.lloydsbanking.salsa.soap.soa.dm.esb.ri.ContentType();
        contentType.setDescription("Place of Birth");
        documentContent.setContentType(contentType);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss.SSS");
        String time = "10:14:28.999Z";
        Date date = dateFormat1.parse(time);
        XMLGregorianCalendar maintainanceDate = dateFactory.stringToXMLGregorianCalendar("2015-07-07", dateFormat);
        XMLGregorianCalendar maintainanceTime = dateFactory.dateToXMLGregorianCalendar(date);


        com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditElement maintenanceAuditElement = new com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditElement();
        maintenanceAuditElement.setMaintenanceAuditDate(maintainanceDate);
        maintenanceAuditElement.setMaintenanceAuditTime(maintainanceTime);
        List<com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditElement> maintenanceAuditElementList = new ArrayList<>();
        maintenanceAuditElementList.add(maintenanceAuditElement);
        com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditData maintenanceAuditData = new com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditData();
        maintenanceAuditData.getHasMaintenanceAuditElement().addAll(maintenanceAuditElementList);
        List<com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditData> maintenanceAuditDataList = new ArrayList<>();
        maintenanceAuditDataList.add(maintenanceAuditData);
        documentContent.getMaintenanceAuditData().addAll(maintenanceAuditDataList);
        List<com.lloydsbanking.salsa.soap.soa.dm.esb.ri.InformationContent> informationContentList = new ArrayList<>();
        com.lloydsbanking.salsa.soap.soa.dm.esb.ri.InformationContent informationContent = new com.lloydsbanking.salsa.soap.soa.dm.esb.ri.DocumentContent();
        informationContent.setDescription("<![CDATA[<IFWXML_Argument:argument xmlns:IFWXML_Argument=\"http://xml.lloydsbanking.com/Schema/Enterprise/InformationTechnology/ESB/IFWXML_Argument\"\n" +
                "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "\txsi:schemaLocation=\"http://xml.lloydsbanking.com/Schema/Enterprise/InformationTechnology/ESB/IFWXML_Argument LDMArgument.xsd\"><IFWXML_Argument:argumentData><IFWXML_Argument:effectiveDate>2015-08-28+01:00</IFWXML_Argument:effectiveDate><IFWXML_Argument:name>Place of Birth</IFWXML_Argument:name><IFWXML_Argument:identifier>220</IFWXML_Argument:identifier><IFWXML_Argument:sequenceNumber>1</IFWXML_Argument:sequenceNumber><IFWXML_Argument:value>Bhopal</IFWXML_Argument:value><IFWXML_Argument:argumentAudit><IFWXML_Argument:externalSystemID>19</IFWXML_Argument:externalSystemID><IFWXML_Argument:externalUserID>KS442736</IFWXML_Argument:externalUserID><IFWXML_Argument:externalLocationID>0000777505</IFWXML_Argument:externalLocationID><IFWXML_Argument:auditDate>2015-10-21</IFWXML_Argument:auditDate><IFWXML_Argument:auditTime>10:14:29.105Z</IFWXML_Argument:auditTime></IFWXML_Argument:argumentAudit></IFWXML_Argument:argumentData></IFWXML_Argument:argument>]]>");
        informationContentList.add(informationContent);
        documentContent.getIncludesContent().addAll(informationContentList);

        com.lloydsbanking.salsa.soap.soa.dm.esb.ri.DocumentContent documentContent1 = new com.lloydsbanking.salsa.soap.soa.dm.esb.ri.DocumentContent();
        com.lloydsbanking.salsa.soap.soa.dm.esb.ri.ContentType contentType1 = new com.lloydsbanking.salsa.soap.soa.dm.esb.ri.ContentType();
        contentType1.setDescription("Place of Birth");
        documentContent1.setContentType(contentType1);
        SimpleDateFormat newdateFormat = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat newdateFormat1 = new SimpleDateFormat("HH:mm:ss.SSS");
        String newtime = "10:14:28.999Z";
        Date newdate = newdateFormat1.parse(newtime);
        XMLGregorianCalendar maintainanceDate1 = dateFactory.stringToXMLGregorianCalendar("2014-07-07", newdateFormat);
        XMLGregorianCalendar maintainanceTime1 = dateFactory.dateToXMLGregorianCalendar(newdate);
        com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditElement maintenanceAuditElement1 = new com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditElement();
        maintenanceAuditElement1.setMaintenanceAuditDate(maintainanceDate1);
        maintenanceAuditElement1.setMaintenanceAuditTime(maintainanceTime1);
        List<com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditElement> maintenanceAuditElementList1 = new ArrayList<>();
        maintenanceAuditElementList1.add(maintenanceAuditElement1);
        com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditData maintenanceAuditData1 = new com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditData();
        maintenanceAuditData1.getHasMaintenanceAuditElement().addAll(maintenanceAuditElementList1);
        List<com.lloydsbanking.salsa.soap.soa.dm.esb.common.MaintenanceAuditData> maintenanceAuditDataList1 = new ArrayList<>();
        maintenanceAuditDataList1.add(maintenanceAuditData1);
        documentContent1.getMaintenanceAuditData().addAll(maintenanceAuditDataList1);
        List<com.lloydsbanking.salsa.soap.soa.dm.esb.ri.InformationContent> informationContentList1 = new ArrayList<>();
        com.lloydsbanking.salsa.soap.soa.dm.esb.ri.InformationContent informationContent1 = new com.lloydsbanking.salsa.soap.soa.dm.esb.ri.DocumentContent();
        informationContent1.setDescription("<![CDATA[<IFWXML_Argument:argument xmlns:IFWXML_Argument=\"http://xml.lloydsbanking.com/Schema/Enterprise/InformationTechnology/ESB/IFWXML_Argument\"\n" +
                "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "\txsi:schemaLocation=\"http://xml.lloydsbanking.com/Schema/Enterprise/InformationTechnology/ESB/IFWXML_Argument LDMArgument.xsd\"><IFWXML_Argument:argumentData><IFWXML_Argument:effectiveDate>2015-08-28+01:00</IFWXML_Argument:effectiveDate><IFWXML_Argument:name>Place of Birth</IFWXML_Argument:name><IFWXML_Argument:identifier>220</IFWXML_Argument:identifier><IFWXML_Argument:sequenceNumber>1</IFWXML_Argument:sequenceNumber><IFWXML_Argument:value>Bhopal</IFWXML_Argument:value><IFWXML_Argument:argumentAudit><IFWXML_Argument:externalSystemID>19</IFWXML_Argument:externalSystemID><IFWXML_Argument:externalUserID>KS442736</IFWXML_Argument:externalUserID><IFWXML_Argument:externalLocationID>0000777505</IFWXML_Argument:externalLocationID><IFWXML_Argument:auditDate>2015-10-21</IFWXML_Argument:auditDate><IFWXML_Argument:auditTime>10:14:29.105Z</IFWXML_Argument:auditTime></IFWXML_Argument:argumentAudit></IFWXML_Argument:argumentData></IFWXML_Argument:argument>]]>");
        informationContentList1.add(informationContent1);
        documentContent1.getIncludesContent().addAll(informationContentList1);


        retrieveDocumentMetaContentResponse.getDocumentContent().add(documentContent);
        retrieveDocumentMetaContentResponse.getDocumentContent().add(documentContent1);
        return retrieveDocumentMetaContentResponse;
    }

    public RecordDocumentMetaContentResponse createRecordDocContentResponse() {
        RecordDocumentMetaContentResponse response = new RecordDocumentMetaContentResponse();
        com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResultCondition resultCondition = new com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResultCondition();
        resultCondition.setSeverityCode("0");
        com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResponseHeader responseHeader = new com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResponseHeader();
        responseHeader.setResultConditions(resultCondition);
        response.setResponseHeader(responseHeader);
        return response;
    }

    public F061Resp createF061Resp(Long partyId) {
        F061Resp f061Resp = new F061Resp();
        PersonalData personalData = new PersonalData();
        personalData.setDOBAuditData(new DOBAuditData());
        personalData.getDOBAuditData().setAuditDt("03082015");
        personalData.getDOBAuditData().setAuditTm("083703");
        personalData.setGenderAuditData(new GenderAuditData());
        personalData.getGenderAuditData().setAuditDt("03082015");
        personalData.getGenderAuditData().setAuditTm("083703");
        personalData.setNameAuditData(new NameAuditData());
        personalData.getNameAuditData().setAuditDt("03082015");
        personalData.getNameAuditData().setAuditTm("083703");
        personalData.setSurname("surName");
        personalData.setFirstForeNm("firstForeNm");
        personalData.setSecondForeNm("secondForeNm");
        personalData.setThirdForeNm("Abcdefhi");
        personalData.setPartyTl("124515");
        personalData.setPartyId(partyId);
        f061Resp.setPartyEnqData(new PartyEnqData());
        f061Resp.getPartyEnqData().setPersonalData(personalData);
        f061Resp.getPartyEnqData().setPartyNonCoreData(new PartyNonCoreData());
        f061Resp.getPartyEnqData().getPartyNonCoreData().setStaffIn("12564");
        f061Resp.getPartyEnqData().getPartyNonCoreData().setStaffMemberNo("65");
        f061Resp.getPartyEnqData().setKYCNonCorePartyData(new KYCNonCorePartyData());
        f061Resp.getPartyEnqData().getKYCNonCorePartyData().setCurrEmployerStartDt("02101990");
        f061Resp.getPartyEnqData().getKYCNonCorePartyData().setEmployerNm("0210458");
        f061Resp.getPartyEnqData().getKYCNonCorePartyData().setNonCorePartyAuditData(new NonCorePartyAuditData());
        f061Resp.getPartyEnqData().getKYCNonCorePartyData().getNonCorePartyAuditData().setAuditDt("035482");
        f061Resp.getPartyEnqData().getKYCNonCorePartyData().getNonCorePartyAuditData().setAuditTm("086897");
        f061Resp.getPartyEnqData().setKYCPartyData(new KYCPartyData());
        f061Resp.getPartyEnqData().getKYCPartyData().setCtyRes(new CtyRes());
        f061Resp.getPartyEnqData().getKYCPartyData().getCtyRes().setCtyResAuditData(new CtyResAuditData());
        f061Resp.getPartyEnqData().getKYCPartyData().getCtyRes().getCtyResAuditData().setAuditDt("8759124");
        f061Resp.getPartyEnqData().getKYCPartyData().getCtyRes().getCtyResAuditData().setAuditTm("5684120");
        f061Resp.getPartyEnqData().getKYCPartyData().setFrstNtn(new FrstNtn());
        f061Resp.getPartyEnqData().getKYCPartyData().getFrstNtn().setFrstNtnAuditData(new FrstNtnAuditData());
        f061Resp.getPartyEnqData().getKYCPartyData().getFrstNtn().getFrstNtnAuditData().setAuditDt("06598314");
        f061Resp.getPartyEnqData().getKYCPartyData().getFrstNtn().getFrstNtnAuditData().setAuditTm("087954");
        f061Resp.getPartyEnqData().getKYCPartyData().getFrstNtn().setFirstNationltyCd("124580");
        f061Resp.getPartyEnqData().getKYCPartyData().getCtyRes().setCountryOfResidCd("124580");
        f061Resp.getPartyEnqData().getPartyNonCoreData().setNonCoreAuditData(new NonCoreAuditData());
        f061Resp.getPartyEnqData().getPartyNonCoreData().getNonCoreAuditData().setAuditDt("10254");
        f061Resp.getPartyEnqData().getPartyNonCoreData().getNonCoreAuditData().setAuditTm("023546");
        return f061Resp;
    }

    public F062Resp createF062Resp() {
        F062Resp f062Resp = new F062Resp();
        f062Resp.setPartyId(123l);
        return f062Resp;
    }

    public String getCustIdFromF062Resp(long f062RespCustId, String reqCustId) {
        String customerId = null;
        if (reqCustId != null) {
            customerId = reqCustId;
        } else {
            customerId = String.valueOf(f062RespCustId);
        }
        return customerId;
    }

    public AssessmentEvidence createAssessmentEvidence() {
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        assessmentEvidence.setAddressStrength("6:7");
        assessmentEvidence.setIdentityStrength("8:9");
        return assessmentEvidence;
    }

    public F062Resp createF062RespWithError() {
        F062Resp f062Resp = new F062Resp();
        f062Resp.setF062Result(new F062Result());
        f062Resp.getF062Result().setResultCondition(new ResultCondition());
        f062Resp.getF062Result().getResultCondition().setReasonCode(163003);
        f062Resp.getF062Result().getResultCondition().setReasonText("CHANNEL_OUTLET_TYPE_INVALID_CODE");
        return f062Resp;
    }

    public StB555BWServMIS createB555Response() {
        StB555BWServMIS stB555BWServMIS = new StB555BWServMIS();
        stB555BWServMIS.setSterror(new com.lloydsbanking.salsa.soap.fs.system.StError());
        stB555BWServMIS.getSterror().setErrorno(0);
        return stB555BWServMIS;
    }

    public StB555AWServMIS createB555Request() {
        StB555AWServMIS stB555AWServMIS = new StB555AWServMIS();
        com.lloydsbanking.salsa.soap.fs.system.StHeader stHeader = new com.lloydsbanking.salsa.soap.fs.system.StHeader();
        stHeader.setUseridAuthor("OX982035  ");
        stHeader.setStpartyObo(new StParty());
        stHeader.getStpartyObo().setHost("T");
        stHeader.getStpartyObo().setOcisid(BigInteger.valueOf(1597203803));
        stHeader.getStpartyObo().setPartyid("+00434307833                  ");
        stHeader.setChanid("LTB");
        stHeader.setChanidObo("LTB");
        stHeader.setChansecmode("PWD");
        stHeader.setSessionid("vjnvupvl4cs21lmon561dgger");
        stHeader.setIpAddressCaller("10.240.147.57,null");
        stHeader.setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1.7) Gecko/20091221 Firefox/3.5.7");
        stHeader.setInboxidClient("GX");
        stHeader.setCodosouid("777505");
        stHeader.setChanctxt(BigInteger.valueOf(1));
        stHeader.setEncVerNo(BigInteger.ZERO);
        stB555AWServMIS.setStheader(stHeader);

        stB555AWServMIS.setStaudit(new TAudit());
        stB555AWServMIS.getStaudit().setEvttype("I416");
        stB555AWServMIS.getStaudit().setEvtlogtext("SendEmail To : a@a.com TemplateId : WELCOME_MSG");
        stB555AWServMIS.getStaudit().setErrorno(0);
        return stB555AWServMIS;
    }

    public EmxSendSmsResponse createO2SuccessResponse() {
        EmxSendSmsResponse emxSendSmsResponse = new EmxSendSmsResponse();

        EmxStatus emxStatus = new EmxStatus();
        emxStatus.setValue(0);

        emxSendSmsResponse.setStatus(emxStatus);

        EmxTxSmsMessageResult messageResult = new EmxTxSmsMessageResult();
        messageResult.setMtMessageId(12345L);

        EmxStatus emxMsgStatus = new EmxStatus();
        emxMsgStatus.setValue(0);
        emxMsgStatus.setCode("OK");
        emxMsgStatus.setText("Text message submitted");
        messageResult.setStatus(emxMsgStatus);

        messageResult.setCustMessageId(0);

        emxSendSmsResponse.getTxSmsMessageResult().add(messageResult);

        return emxSendSmsResponse;

    }

    public EmxSendSmsRequest generateO2Request(SendCommunicationRequest request) {

        int serviceId = 1576;
        String destinationNumber = request.getCommunication().getContactPointId();
        String source = request.getCommunication().getSource();

        AccessToken accessToken = new AccessToken();
        accessToken.setUserName("EMXAPI_SIT_USER");
        accessToken.setCompanyCode("LTSB");
        accessToken.setPasscode("somepasspcode");

        PartyData partyData = request.getCommunication().getPartyData();
        String messageTemplateName = request.getCommunication().getCommunicationTemplate().getTemplateId();

        List<InformationContent> contents = request.getCommunication().getHasCommunicationContent();

        return createO2Request(serviceId, destinationNumber, source, accessToken, partyData, messageTemplateName, contents);
    }

    public EmxSendSmsRequest createO2Request(int serviceId, String destinationNumber, String source, AccessToken accessToken, PartyData partyData, String messageTemplateName, List<InformationContent> contents) {
        return generateSms(serviceId, destinationNumber, source, accessToken, partyData, messageTemplateName, contents);
    }

    public EmxSendSmsRequest generateSms(int serviceId, String destinationNumber, String source, AccessToken accessToken, PartyData partyData, String messageTemplateName, List<InformationContent> contents) {

        EmxSendSmsRequest sendSmsRequest = new EmxSendSmsRequest();

        sendSmsRequest.setCustomerId(15);
        sendSmsRequest.setUsername(accessToken.getUserName());
        sendSmsRequest.setPassword(accessToken.getPasscode());
        sendSmsRequest.setServiceId(serviceId);

        sendSmsRequest.getTxSmsMessageDetails().addAll(createSmsMessagePart(destinationNumber, source, partyData, messageTemplateName, contents));

        return sendSmsRequest;

    }

    private List<EmxTxSmsMessageDetails> createSmsMessagePart(String destinationNumber, String source, PartyData partyData, String messageTemplateName, List<InformationContent> contents) {

        List<EmxTxSmsMessageDetails> emxTxSmsMessageDetails = new ArrayList<>();

        EmxTxSmsMessageDetails emxTxSmsMessageDetail = new EmxTxSmsMessageDetails();

        emxTxSmsMessageDetail.setNumericDestination(destinationNumber);
        emxTxSmsMessageDetail.setCustMessageId(0);
        emxTxSmsMessageDetail.setCustMessageRef(source);

        EmxTxSmsMessageAttribs messageAttribs = new EmxTxSmsMessageAttribs();

        if (null != partyData) {
            messageAttribs.setCustSystemRef1(String.valueOf(partyData.getPartyId()));
            messageAttribs.setCustSystemRef2(partyData.getExtPartyIdTx());
        }

        emxTxSmsMessageDetail.setTxSmsAttributes(messageAttribs);

        EmxTemplateAttribsKVP templateAttribsKVP = new EmxTemplateAttribsKVP();
        templateAttribsKVP.setMessageTemplateName(messageTemplateName);

        List<EmxTemplateValueKVP> templateValueKVPs = new ArrayList<>();

        for (InformationContent content : contents) {
            EmxTemplateValueKVP templateValueKVP = new EmxTemplateValueKVP();
            templateValueKVP.setTemplateFieldName("$$" + content.getKey());
            templateValueKVP.setTemplateFieldValue(content.getValue());

            templateValueKVPs.add(templateValueKVP);
        }

        templateAttribsKVP.getTemplateValueKVP().addAll(templateValueKVPs);

        emxTxSmsMessageDetail.setTemplateAttribsKVP(templateAttribsKVP);

        emxTxSmsMessageDetails.add(emxTxSmsMessageDetail);

        return emxTxSmsMessageDetails;

    }
}


