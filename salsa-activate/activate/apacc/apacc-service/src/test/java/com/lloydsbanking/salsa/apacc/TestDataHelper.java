package com.lloydsbanking.salsa.apacc;

import com.infracast.schemas.emx.v10_2.EmxSendSmsRequest;
import com.infracast.schemas.emx.v10_2.EmxSendSmsResponse;
import com.infracast.schemas.emx.v10_2.EmxTxSmsMessageDetails;
import com.infracast.schemas.emx.v10_2.EmxTxSmsMessageResult;
import com.infracast.schemas.managedtext.v10_2.EmxStatus;
import com.infracast.schemas.managedtext.v10_2.EmxTemplateAttribsKVP;
import com.infracast.schemas.managedtext.v10_2.EmxTemplateValueKVP;
import com.infracast.schemas.managedtext.v10_2.EmxTxSmsMessageAttribs;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.F241RequestFactory;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.F241V1RequestFactory;
import com.lloydsbanking.salsa.commsmanager.ref.model.*;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.fsystem.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydsbanking.salsa.soap.asm.f425.objects.*;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.F241Result;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.CardData;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Req;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Resp;
import com.lloydsbanking.salsa.soap.fs.application.StParty;
import com.lloydsbanking.salsa.soap.fs.system.StError;
import com.lloydsbanking.salsa.soap.fs.system.StHeader;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Resp;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Result;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Resp;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Result;
import com.lloydsbanking.salsa.soap.ocis.f259.objects.F259Resp;
import com.lloydsbanking.salsa.soap.ocis.f259.objects.F259Result;
import com.lloydstsb.ib.wsbridge.application.StB751AAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.system.*;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import com.lloydstsb.schema.personal.serviceplatform.tms.CreateTaskResponse;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreationResponse;
import com.lloydstsb.schema.personal.serviceplatform.tms.v0001.TMSRoutingInformationBO;
import lib_sim_bo.businessobjects.*;
import lib_sim_bo.businessobjects.Channel;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import lib_sim_communicationmanager.messages.SendCommunicationRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
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
    public static final String NEW_APPLICATION_TYPE = "10001";

    public static final String TEST_RETAIL_CHANNEL_ID = "IBL";
    public static final String TEST_BUSINESS_TRANSACTION = "activateProductArrangement";

    public static final String TEST_INTERACTION_ID = "62bdfea6f48211e4add0e3a875e8c881";

    public final static String TEST_OCIS_ID = "1433933835";

    public static final String TEST_CUSTOMER_ID = "RD888225 ";

    public static final String TEST_MESSAGE_ID = "a0fn7kh3mtma1201i2z6psoe9";

    public static final String TEST_CONTACT_POINT_ID = "0000777505";

    public static final String INVALID_CONTACT_POINT_ID = "0000777";

    public static final String GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER = "1";

    public static final String DB_EVENT_SOURCE_SYSTEM_IDENTIFIER = "2";

    public static final String OAP_SOURCE_SYSTEM_IDENTIFIER = "3";

    public static final String QUEUE_SMS_SWITCH = "SW_CM_Queue_SMS";

    public static final String QUEUE_EMAIL_SWITCH = "SW_CM_Queue_EM";

    public static final String DURABLE_MEDIUM_SWITCH = "SW_EnSTPPCAWcMl";

    public static final String TEST_EMAIL_TEMPLATE = "WELCOME_MSG";

    public static final String TEST_EMAIL_SUCCESS_TEMPLATE = "IB_STP_REGISTRATION_SUCCESS_MAIL";

    public static final String TEST_SMS_TEMPLATE = "apbs_sms_LBG";

    ProductTypes productTypeCreditCard = new ProductTypes("100", "Credit Card");

    Brands brands = new Brands("LTB", "Lloyds");

    UserTypes userTypes = new UserTypes("1001", "Customer");

    PromotionChannels promotionChannels = new PromotionChannels("003", "Direct Mail");

    Channels channels = new Channels("004", "Internet");

    ApplicationForms applicationForm = new ApplicationForms(10002, "Ava Sales Question");

    ApplicationPartyRoles applicationPartyRoles = new ApplicationPartyRoles("0001", "Key Party");

    ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");

    ApplicationTypes applicationTypes = new ApplicationTypes("10001", "New Application");

    ProductTypes productTypesSavings = new ProductTypes("101", "Savings Account");

    ParameterGroups parameterGroups1 = new ParameterGroups("CBS", "CBS Param Group");

    ParameterGroups parameterGroups2 = new ParameterGroups("ASM", "ASM Param Group");

    ParameterGroups parameterGroups7 = new ParameterGroups("IB", "Internet Banking");

    ParameterGroups parameterGroups8 = new ParameterGroups("OCIS", "OCIS Param Group");

    ParameterGroups parameterGroups3 = new ParameterGroups("CMAS", "CMAS Param Group");

    ParameterGroups parameterGroups4 = new ParameterGroups("PEGA", "PEGA Param Group");

    ParameterGroups parameterGroups5 = new ParameterGroups("CCD", "Call Credit Param Group");

    ParameterGroups parameterGroups6 = new ParameterGroups("EIDV", "EIDV Param Group");

    ApplicationParameters applicationParameters1 = new ApplicationParameters("100009", parameterGroups7, "Instruction Mnemonic");

    ApplicationParameters applicationParameters2 = new ApplicationParameters("100037", parameterGroups7, "Debit Card Facility Required Indicator");

    ApplicationParameters applicationParameters3 = new ApplicationParameters("100011", parameterGroups2, "ASM Product Family Id Selected");

    ApplicationParameters applicationParameters4 = new ApplicationParameters("100012", parameterGroups2, "Debit Card Risk Code");

    ApplicationParameters applicationParameters5 = new ApplicationParameters("100004", parameterGroups6, "EIDV strength token");

    ApplicationParameters applicationParameters6 = new ApplicationParameters("100005", parameterGroups7, "IB registration reference number");

    ApplicationParameters applicationParameters7 = new ApplicationParameters("100006", parameterGroups2, "ASM Approve");

    ApplicationParameters applicationParameters8 = new ApplicationParameters("100036", parameterGroups2, "CBS Customer Number");

    ApplicationParameters applicationParameters9 = new ApplicationParameters("100008", parameterGroups8, "OCIS Party Identifier");

    ApplicationParameters applicationParameters10 = new ApplicationParameters("100010", parameterGroups7, "Application Version Number");

    ApplicationParameters applicationParameters11 = new ApplicationParameters("100042", parameterGroups2, "New Customer Indicator");

    ApplicationPartyRoles applicationPartyRolesForPromoParty = new ApplicationPartyRoles("0003", "Promotional Party Affiliate");

    ApplicationPartyRoles roles = new ApplicationPartyRoles("0001", "Key Party");
    ApplicationPartyRoles applicationPartyRolesForAdditionalCard = new ApplicationPartyRoles("0002", "Additional Card Holder");
    ApplicationPartyRoles applicationPartyRolesForSecondaryParty = new ApplicationPartyRoles("0005", "Secondary Party");
    ApplicationPartyRoles applicationPartyRolesForPromoPartyAffiliate = new ApplicationPartyRoles("0004", "Promotional Party Affiliate Network Provider");

    ApplicationFeatureTypes applicationFeatureTypes1 = new ApplicationFeatureTypes("STUD", "Student");
    ApplicationFeatureTypes applicationFeatureTypes2 = new ApplicationFeatureTypes("CHQBK", "ChequeBook");
    ApplicationFeatureTypes applicationFeatureTypes3 = new ApplicationFeatureTypes("SAV", "Savings");
    ApplicationFeatureTypes applicationFeatureTypes4 = new ApplicationFeatureTypes("BENPA", "Benefit Payments");
    ApplicationFeatureTypes applicationFeatureTypes5 = new ApplicationFeatureTypes("BT_CA", "Balance Transfer to Current Account Setup");
    ApplicationFeatureTypes applicationFeatureTypes6 = new ApplicationFeatureTypes("PCCI", "Date PCCI viewed");
    ApplicationFeatureTypes applicationFeatureTypes7 = new ApplicationFeatureTypes("FIXED", "Fixed Direct Debit");
    ApplicationFeatureTypes applicationFeatureTypes8 = new ApplicationFeatureTypes("MIN", "Minimum Direct Debit");
    ApplicationFeatureTypes applicationFeatureTypes9 = new ApplicationFeatureTypes("FULL", "Full Direct Debit");
    ApplicationFeatureTypes applicationFeatureTypes10 = new ApplicationFeatureTypes("BT", "Balance Transfer Setup");
    ApplicationFeatureTypes applicationFeatureTypes11 = new ApplicationFeatureTypes("APDT", "Date APDT viewed");

    public static final List<String> groupCodeList = Arrays.asList(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE, ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE);

    KycStatus kycStatus = new KycStatus();

    ApprovalStatus approvalStatus = new ApprovalStatus();

    @Autowired
    ApplicationParameterValuesDao applicationParameterValuesDao;

    @Autowired
    ApplicationFeatureTypesDao applicationFeatureTypesDao;

    @Autowired
    PartyApplicationsDao partyApplicationsDao;

    @Autowired
    ApplicationStatusDao appStatusDao;

    @Autowired
    IndividualsDao individualsDao;

    @Autowired
    IndividualAddressesDao individualAddressesDao;

    @Autowired
    StreetAddressesDao streetAddressesDao;

    @Autowired
    ApplicationProductsDao applicationProductsDao;

    @Autowired
    ProductPackageTypesDao productPackageTypesDao;

    @Autowired
    ProductTypesDao productTypesDao;


    @Autowired
    ParameterGroupsDao parameterGroupsDao;

    @Autowired
    ApplicationParametersDao applicationParametersDao;

    @Autowired
    UserTypesDao userTypesDao;

    @Autowired
    ApplicationsDao applicationsDao;

    @Autowired
    PromotionChannelsDao promotionChannelsDao;

    @Autowired
    ApplicationTypesDao applicationTypesDao;

    @Autowired
    BrandsDao brandsDao;

    @Autowired
    ApplicationPartyRolesDao applicationPartyRolesDao;

    @Autowired
    KycStatusDao kycStatusDao;

    @Autowired
    ChannelsDao channelsDao;

    @Autowired
    ApprovalStatusDao approvalStatusDao;

    @Autowired
    ApplicationFormsDao applicationFormsDao;

    @Autowired
    ApplicationsRelatedDao applicationsRelatedDao;

    @Autowired
    ApplicationFeaturesDao applicationFeaturesDao;

    @Autowired
    ApplicationRelationshipTypesDao applicationRelationshipTypesDao;

    HeaderRetriever headerRetriever = new HeaderRetriever();

    @Autowired
    F241V1RequestFactory f241V1RequestFactory;

    @Autowired
    F241RequestFactory f241RequestFactory;

    @Autowired
    ReferralTeamsDao referralTeamsDao;
    @Autowired
    ReferralsDao referralsDao;

    @Autowired
    DateFactory dateFactory;
    @Autowired
    TelephoneAddressesDao telephoneAddressesDao;
    @Autowired
    TelephoneAddressTypesDao telephoneAddressTypesDao;
    @Autowired
    ReferralStatusDao referralStatusDao;
    @Autowired
    AbandonDeclineReasonDao abandonDeclineReasonDao;

    TelephoneAddressTypes telephoneAddressTypes = new TelephoneAddressTypes();


    @Transactional
    @Modifying
    public void cleanUp() {

        applicationsRelatedDao.deleteAll();
        referralsDao.deleteAll();
        referralTeamsDao.deleteAll();
        partyApplicationsDao.deleteAll();
        applicationsDao.deleteAll();
        applicationProductsDao.deleteAll();
        productPackageTypesDao.deleteAll();
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
        telephoneAddressesDao.deleteAll();
        telephoneAddressTypesDao.deleteAll();
        applicationFeaturesDao.deleteAll();
        applicationFeatureTypesDao.deleteAll();
        referralStatusDao.deleteAll();
        abandonDeclineReasonDao.deleteAll();
    }

    public RequestHeader createApaRequestHeader() {

        RequestHeaderBuilder requestHeaderBuilder = new RequestHeaderBuilder();
        return requestHeaderBuilder.businessTransaction(TEST_BUSINESS_TRANSACTION).channelId(TEST_RETAIL_CHANNEL_ID).interactionId(TEST_INTERACTION_ID).bapiInformation(TEST_RETAIL_CHANNEL_ID, TEST_INTERACTION_ID, TEST_OCIS_ID, "ns5").securityHeader("lgsm", TEST_CUSTOMER_ID).serviceRequest("ns4", TEST_BUSINESS_TRANSACTION, "10.245.182.105", TEST_MESSAGE_ID).contactPoint("ns4", "003", TEST_CONTACT_POINT_ID, "Internet Banking", "Browser", "127.0.0.1", "Customer").build();
    }

    public RequestHeader createApaRequestHeaderWithInvalidContactPoint() {

        RequestHeaderBuilder requestHeaderBuilder = new RequestHeaderBuilder();
        return requestHeaderBuilder.businessTransaction(TEST_BUSINESS_TRANSACTION).channelId(TEST_RETAIL_CHANNEL_ID).interactionId(TEST_INTERACTION_ID).bapiInformation(TEST_RETAIL_CHANNEL_ID, TEST_INTERACTION_ID, TEST_OCIS_ID, "ns5").securityHeader("lgsm", TEST_CUSTOMER_ID).serviceRequest("ns4", TEST_BUSINESS_TRANSACTION, "10.245.182.105", TEST_MESSAGE_ID).contactPoint("ns4", "003", INVALID_CONTACT_POINT_ID, "Internet Banking", "Browser", "127.0.0.1", "Customer").build();
    }

    public ActivateProductArrangementRequest createApaRequestForCc(long arrangementId) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createFinanceServiceArrangement(String.valueOf(arrangementId)));
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestForCcWithBt() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createFinanceServiceArrangementForCCWithBt());
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestForCcForJointParty() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createFinanceServiceArrangementForJointParty(null));
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }


    public FinanceServiceArrangement createFinanceServiceArrangementForCCWithBt() {

        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setArrangementId("109865");
        financeServiceArrangement.setArrangementType("CC");

        Product product = new Product();
        product.setProductIdentifier("92");
        product.setProductName("Platinum Purchase Card");
        product.getProductoffer().add(new ProductOffer());
        product.getProductoffer().get(0).setProdOfferIdentifier("2000866");
        financeServiceArrangement.setAssociatedProduct(product);


        financeServiceArrangement.getOfferedProducts().add(product);

        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(new Individual());
        financeServiceArrangement.getJointParties().add(jointPartyCustomer);
        financeServiceArrangement.setIsJointParty(false);

        Customer customer = new Customer();
        CustomerScore customerScore = new CustomerScore();
        customerScore.getAssessmentEvidence().add(new AssessmentEvidence());
        customer.getCustomerScore().add(customerScore);
        financeServiceArrangement.setPrimaryInvolvedParty(customer);

        financeServiceArrangement.setApplicationType(NEW_APPLICATION_TYPE);
        financeServiceArrangement.setAccountDetails(new AccountDetails());
        financeServiceArrangement.getReferral().add(new Referral());
        financeServiceArrangement.setDirectDebit(new DirectDebit());
        financeServiceArrangement.setPaymentProtectionIndicator(false);

        financeServiceArrangement.getBalanceTransfer().add(new BalanceTransfer());
        financeServiceArrangement.getBalanceTransfer().get(0).setCreditCardNumber("*****");
        financeServiceArrangement.getBalanceTransfer().get(0).setAmount(new CurrencyAmount());
        financeServiceArrangement.getBalanceTransfer().get(0).getAmount().setAmount(BigDecimal.valueOf(100));
        financeServiceArrangement.getBalanceTransfer().get(0).setExpiryDate("042025");

        return financeServiceArrangement;
    }

    public FinanceServiceArrangement createFinanceServiceArrangementForJointParty(String arrangementId) {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setArrangementId(arrangementId);
        financeServiceArrangement.setArrangementType("CC");

        Product product = new Product();
        product.setProductIdentifier("10005");
        product.setProductName("Advance Credit Card");
        product.getProductoffer().add(new ProductOffer());
        product.getProductoffer().get(0).setProdOfferIdentifier("2000866");
        financeServiceArrangement.setAssociatedProduct(product);


        financeServiceArrangement.getOfferedProducts().add(product);

        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(new Individual());
        IndividualName individualName = new IndividualName();
        individualName.setFirstName("Dan");
        individualName.setLastName("Weber");
        individualName.setPrefixTitle("Mr");
        jointPartyCustomer.getIsPlayedBy().getIndividualName().add(individualName);
        jointPartyCustomer.getIsPlayedBy().setBirthDate(createXMLGregorianCalendar(1948,10,28));
        jointPartyCustomer.getIsPlayedBy().setNationality("GBR");
        jointPartyCustomer.getIsPlayedBy().setGender("01");
        financeServiceArrangement.getJointParties().add(jointPartyCustomer);
        financeServiceArrangement.setIsJointParty(true);

        Customer customer = new Customer();
        CustomerScore customerScore = new CustomerScore();
        customerScore.getAssessmentEvidence().add(new AssessmentEvidence());
        customer.getCustomerScore().add(customerScore);
        customer.setIsRegistrationSelected(true);
        customer.setPassword("password1234");
        financeServiceArrangement.setPrimaryInvolvedParty(customer);

        financeServiceArrangement.setApplicationType(NEW_APPLICATION_TYPE);
        financeServiceArrangement.setAccountDetails(new AccountDetails());
        financeServiceArrangement.getReferral().add(new Referral());
        financeServiceArrangement.setDirectDebit(new DirectDebit());
        financeServiceArrangement.setPaymentProtectionIndicator(false);
        financeServiceArrangement.setApplicationType("10001");
        return financeServiceArrangement;
    }

    public ActivateProductArrangementRequest createApaRequestForCc() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createFinanceServiceArrangement(null));
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public FinanceServiceArrangement createFinanceServiceArrangement(String arrangementId) {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setArrangementId(arrangementId);
        financeServiceArrangement.setArrangementType("CC");

        Product product = new Product();
        product.setProductIdentifier("92");
        product.setProductName("Platinum Purchase Card");
        product.getProductoffer().add(new ProductOffer());
        product.getProductoffer().get(0).setProdOfferIdentifier("2000866");
        financeServiceArrangement.setAssociatedProduct(product);
        Product product1 = new Product();
        product1.setProductIdentifier("92");
        product1.setProductName("Platinum Purchase Card");
        product1.setStatusCode("isAccepted");
        product1.getProductoffer().add(new ProductOffer());
        product1.getProductoffer().get(0).setProdOfferIdentifier("2000866");

        financeServiceArrangement.getOfferedProducts().add(product1);

        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(new Individual());
        financeServiceArrangement.getJointParties().add(jointPartyCustomer);
        financeServiceArrangement.setIsJointParty(false);

        Customer customer = new Customer();
        CustomerScore customerScore = new CustomerScore();
        customerScore.getAssessmentEvidence().add(new AssessmentEvidence());
        customer.getCustomerScore().add(customerScore);
        financeServiceArrangement.setPrimaryInvolvedParty(customer);

        financeServiceArrangement.setApplicationType(NEW_APPLICATION_TYPE);
        financeServiceArrangement.setAccountDetails(new AccountDetails());
        financeServiceArrangement.getReferral().add(new Referral());
        financeServiceArrangement.setDirectDebit(new DirectDebit());
        financeServiceArrangement.setPaymentProtectionIndicator(false);

        return financeServiceArrangement;
    }

    public FinanceServiceArrangement createFinanceServiceArrangement() {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setArrangementId("12345");
        financeServiceArrangement.setArrangementType("CC");
        financeServiceArrangement.setApplicationStatus("1002");

        Product product = new Product();
        product.setProductIdentifier("1002");
        product.setBrandName("LTB");
        product.setGuaranteedOfferCode("Y");
        product.setProductName("Classic Credit Card");

        Channel channel = new Channel();
        channel.setChannelCode("004");
        channel.setSubChannelCode("002");

        Customer customer = new Customer();
        customer.setEmailAddress("a@a.com");

        PostalAddress postalAddress = new PostalAddress();

        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("1");
        structuredAddress.setPostTown("LONDON");
        structuredAddress.setCountry("United Kingdom");
        structuredAddress.setPostCodeOut("SE1");
        structuredAddress.setPostCodeIn("2EA");
        structuredAddress.setPointSuffix("1A");
        structuredAddress.getAddressLinePAFData().add("PROVIDENCE SQUARE");
        TelephoneNumber telephoneNumber = new TelephoneNumber();
        telephoneNumber.setAreaCode("44");
        telephoneNumber.setCountryPhoneCode("44");
        telephoneNumber.setPhoneNumber("44");
        telephoneNumber.setTelephoneType("1");

        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("0505");
        postalAddress.setStructuredAddress(structuredAddress);
        customer.getPostalAddress().add(postalAddress);
        customer.getTelephoneNumber().add(telephoneNumber);
        Individual individual = new Individual();
        IndividualName individualName = new IndividualName();
        Individual individual1 = new Individual();
        IndividualName individualName1 = new IndividualName();
        individualName.setFirstName("ghg");
        individualName.getMiddleNames().add("hg");
        individualName.setLastName("hg");
        individualName.setPrefixTitle("Mr");

        individual.setIsStaffMember(false);
        individual.setResidentialStatus("002");
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        individual.setBirthDate(dateFactory.stringToXMLGregorianCalendar("1948-10-28", dateFormat));
        individual.setNationality("GBR");
        individual.setNumberOfDependents(new BigInteger("1"));
        individual.setMaritalStatus("003");
        individual.setGender("001");
        individual.setEmploymentStatus("003");
        individual.setCurrentEmploymentDuration("0505");
        individual.getIndividualName().add(individualName);
        customer.setIsPlayedBy(individual);

        CurrencyAmount currencyAmount = new CurrencyAmount();
        AffiliateDetails affiliateDetails = new AffiliateDetails();
        affiliateDetails.setAffiliateIdentifier("102");
        affiliateDetails.setIsCreditIntermediary(true);
        financeServiceArrangement.getAffiliatedetails().add(affiliateDetails);
        CurrencyAmount currencyAmount1 = new CurrencyAmount();
        currencyAmount1.setAmount(new BigDecimal("12000"));
        currencyAmount1.setAmount(new BigDecimal("200"));
        currencyAmount1.setAmount(new BigDecimal("500"));

        Employer employer = new Employer();
        employer.setName("235126");
        individual.setOccupation("33");
        customer.setCustomerIdentifier("294374712");

        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("ACCEPT");
        customerScore.setAssessmentType("EIDV");
        customerScore.setScoreResult("1");
        customerScore.setAssessmentType("ASM");

        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("601");
        referralCode.setDescription("Accept.");

        customer.setExistingSortCode("772216");
        customer.setExistingAccountNumber("32771660");
        customer.setExistingAccountDuration("0505");
        customer.setCidPersID("+00026267042");
        customer.setUserType("1001");
        customer.setInternalUserIdentifier("10.245.224.125");
        customer.setHasExistingCreditCard(false);
        customer.setIsRegistrationSelected(false);
        customer.setCustomerSegment("3");

        AccessToken accessToken = new AccessToken();
        byte[] memorableInfo = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPGRhdGE6RW5jcnlwdERhdGFSZXNwb25zZSB4bWxuczpfeD0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8wNC94bWxlbmMjIiB4bWxuczpfeF8xPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIiB4bWxuczpkYXRhPSJodHRwOi8vd3d3Lmxsb3lkc3RzYi5jb20vU2NoZW1hL0ludGVybmV0QmFua2luZy9FbmNyeXB0RGF0YSI+CiAgPGRhdGE6b3V0ZGV0YWlscz4KICAgIDxkYXRhOmFzeW1tS2V5PldaX0VTQl9WMS1wcml2a2V5LnBlbTwvZGF0YTphc3ltbUtleT4KICAgIDxkYXRhOmFzeW1tQ2VydD5XWl9FU0JfVjEtc3NjZXJ0LnBlbTwvZGF0YTphc3ltbUNlcnQ+CiAgICA8ZGF0YTpvdXR0ZXh0PjxfeDpFbmNyeXB0ZWREYXRhIFR5cGU9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMDQveG1sZW5jI0NvbnRlbnQiPgogICAgICAgIDxfeDpFbmNyeXB0aW9uTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8wNC94bWxlbmMjYWVzMTI4LWNiYyIvPgogICAgICAgIDxfeF8xOktleUluZm8+PF94OkVuY3J5cHRlZEtleSBSZWNpcGllbnQ9Im5hbWU6V1pfRVNCX1YxIj4KICAgICAgICAgICAgPF94OkVuY3J5cHRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGVuYyNyc2EtMV81Ii8+CiAgICAgICAgICAgIDxfeF8xOktleUluZm8+PF94XzE6S2V5TmFtZT5XWl9FU0JfVjE8L194XzE6S2V5TmFtZT48L194XzE6S2V5SW5mbz4KICAgICAgICAgICAgPF94OkNpcGhlckRhdGE+CiAgICAgICAgICAgICAgPF94OkNpcGhlclZhbHVlPmJSLy9udTI2MEFkcjRscHJQOExRTHM0YzU0dlNzOW1ObmFPSk5vV0Y0UTBQOE0zeTBpR0VSbm0yUjVUMzQvenFJWkhyUFlDQWx0QjBENVZ1SWtFcDkvWTZGakFQaDgxaVJ3SHdLa1R6UHU2cW9sYUp0STRaV3RPYkEzRU52cXo5UklBSUhZcFZMdDkvM3NoSGdyZGdVdnRJNERuQ2F3YzJsRTJ6YU5DVzc0dEdNS0VzWU10b0VYVWwxOFV1S0h0b0NMNUJyOFFCTEE3SSszdFozbTQzdVMydUtCZUVTTnBBSlcxZ01SbEhVZzBhODRrc0ZnNHRzNXB6RklCcjVYTWxZNW4rY2l6Y1grZmMxODFLaWdNa1dWQjUweHlZUU1DaG03TVQ2eVY2akU1cHJ5OGZ2SFEzZStEMGt2SkVzVzRtcmRlNE52bWhPNUR6amxZNVNNdzhBQT09PC9feDpDaXBoZXJWYWx1ZT4KICAgICAgICAgICAgPC9feDpDaXBoZXJEYXRhPgogICAgICAgICAgPC9feDpFbmNyeXB0ZWRLZXk+PC9feF8xOktleUluZm8+CiAgICAgICAgPF94OkNpcGhlckRhdGE+CiAgICAgICAgICA8X3g6Q2lwaGVyVmFsdWU+SzlZV21QKzdFQ29qSHhnbVIrRERFQ1hWZnV2TVNYb0lDWHdLcm9qQ2Fzbz08L194OkNpcGhlclZhbHVlPgogICAgICAgIDwvX3g6Q2lwaGVyRGF0YT4KICAgICAgPC9feDpFbmNyeXB0ZWREYXRhPjwvZGF0YTpvdXR0ZXh0PgogICAgPGRhdGE6b3V0RW5jb2RlPmJhc2U2NDwvZGF0YTpvdXRFbmNvZGU+CiAgPC9kYXRhOm91dGRldGFpbHM+CjwvZGF0YTpFbmNyeXB0RGF0YVJlc3BvbnNlPg==".getBytes();
        accessToken.setEncryptedMemorableInfo(memorableInfo);
        customer.setAccessToken(accessToken);
        customer.setNewCustomerIndicator(true);
        customer.setIsAuthCustomer(false);

        financeServiceArrangement.setMarketingPreferenceBySMS(true);
        financeServiceArrangement.setIsJointParty(false);
        financeServiceArrangement.setApplicationSubStatus("1013");
        product.setProductIdentifier("36374");
        product.setStatusCode("isAccepted");

        ProductOffer productOffer = new ProductOffer();
        productOffer.setProdOfferIdentifier("1000001");
        productOffer.setOfferType("2004");

        ProductAttributes productAttributes = new ProductAttributes();
        productAttributes.setAttributeIdentifier("2054");
        productAttributes.setAttributeCode("BT_OFF_1");
        productAttributes.setAttributeType("BT_OF");
        productAttributes.setAttributeValue("BT066");
        productAttributes.setAttribtueDescription("BT Offer One");

        ProductAttributes productAttributes1 = new ProductAttributes();
        productAttributes1.setAttributeIdentifier("2018743");
        productAttributes1.setAttributeCode("SrvcChrgfeeOvrdEndDtPrd");
        productAttributes1.setAttributeType("F_PRM");
        productAttributes1.setAttribtueDescription("Fee table override expiry period");

        ProductAttributes productAttributes2 = new ProductAttributes();
        productAttributes2.setAttributeIdentifier("2018743");
        productAttributes2.setAttributeCode("SrvcChrgfeeOvrdEndDtPrd");
        productAttributes2.setAttributeType("F_PRM");
        productAttributes2.setAttribtueDescription("Fee table override expiry period");

        ProductAttributes productAttributes3 = new ProductAttributes();
        productAttributes3.setAttributeIdentifier("2018747");
        productAttributes3.setAttributeCode("AcntOvrd");
        productAttributes3.setAttributeType("F_PRM");
        productAttributes3.setAttribtueDescription("ACT Override");

        ProductAttributes productAttributes4 = new ProductAttributes();
        productAttributes4.setAttributeIdentifier("2018746");
        productAttributes4.setAttributeCode("LyltCd");
        productAttributes4.setAttributeType("F_PRM");
        productAttributes4.setAttribtueDescription("Loyalty Code");

        ProductAttributes productAttributes5 = new ProductAttributes();
        productAttributes5.setAttributeIdentifier("2018748");
        productAttributes5.setAttributeCode("LyltPromCd");
        productAttributes5.setAttributeType("F_PRM");
        productAttributes5.setAttribtueDescription("Loyalty Promotional Code");

        productOffer.getProductattributes().add(productAttributes);
        productOffer.getProductattributes().add(productAttributes1);
        productOffer.getProductattributes().add(productAttributes2);
        productOffer.getProductattributes().add(productAttributes3);
        productOffer.getProductattributes().add(productAttributes4);
        productOffer.getProductattributes().add(productAttributes5);

        CurrencyAmount currencyAmount2 = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal("1210.00"));
        productOffer.setOfferAmount(currencyAmount2);
        product.getProductoffer().add(productOffer);
        financeServiceArrangement.setPrimaryInvolvedParty(customer);
        financeServiceArrangement.setAssociatedProduct(product);
        ArrangementHistory arrangementHistory = new ArrangementHistory();
        arrangementHistory.setStatus("1002");
        arrangementHistory.setSubStatus("1013");
        arrangementHistory.setRetryCount("1");
        arrangementHistory.setUserType("1001");
        arrangementHistory.setUserIdentifier("10.245.224.125");
        arrangementHistory.setDateModified(dateFactory.stringToXMLGregorianCalendar("2015-09-28T09:36:30Z", dateFormat));
        financeServiceArrangement.setApplicationType("10001");
        financeServiceArrangement.setRetryCount(new Integer(1));
        Channel channel1 = new Channel();
        channel1.setChannelCode("004");
        channel1.setSubChannelCode("002");
        financeServiceArrangement.setInitiatedThrough(channel1);
        financeServiceArrangement.setLastModifiedDate(dateFactory.stringToXMLGregorianCalendar("2015-09-28T08:36:30Z", dateFormat));
        financeServiceArrangement.setRelatedApplicationExists(false);
        financeServiceArrangement.setMarketingPreferenceByEmail(true);
        financeServiceArrangement.setMarketingPreferenceByPhone(true);
        financeServiceArrangement.setMarketingPreferenceByMail(true);

        financeServiceArrangement.setIsDirectDebitRequired(false);
        financeServiceArrangement.setPaymentProtectionIndicator(false);
        financeServiceArrangement.setMarketingPrefereceIndicator(true);

        currencyAmount.setAmount(new BigDecimal("0.0"));
        financeServiceArrangement.setAgreementAcceptedDate(dateFactory.stringToXMLGregorianCalendar("2015-09-28", dateFormat));
        financeServiceArrangement.setPcciViewedDate(dateFactory.stringToXMLGregorianCalendar("2015-09-28", dateFormat));
        financeServiceArrangement.setNameAndAddressVerifiedFlag(false);
        return financeServiceArrangement;
    }

    public Customer createCustomerDetails(){
        Customer customer = new Customer();
        customer.setEmailAddress("a@a.com");

        PostalAddress postalAddress = new PostalAddress();

        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("1");
        structuredAddress.setPostTown("LONDON");
        structuredAddress.setCountry("United Kingdom");
        structuredAddress.setPostCodeOut("SE1");
        structuredAddress.setPostCodeIn("2EA");
        structuredAddress.setPointSuffix("1A");
        structuredAddress.getAddressLinePAFData().add("PROVIDENCE SQUARE");
        TelephoneNumber telephoneNumber = new TelephoneNumber();
        telephoneNumber.setAreaCode("44");
        telephoneNumber.setCountryPhoneCode("44");
        telephoneNumber.setPhoneNumber("44");
        telephoneNumber.setTelephoneType("1");

        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("0505");
        postalAddress.setStructuredAddress(structuredAddress);
        customer.getPostalAddress().add(postalAddress);
        customer.getTelephoneNumber().add(telephoneNumber);
        Individual individual = new Individual();
        IndividualName individualName = new IndividualName();
        Individual individual1 = new Individual();
        IndividualName individualName1 = new IndividualName();
        individualName.setFirstName("ghg");
        individualName.getMiddleNames().add("hg");
        individualName.setLastName("hg");
        individualName.setPrefixTitle("Mr");

        individual.setIsStaffMember(false);
        individual.setResidentialStatus("002");
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        individual.setBirthDate(dateFactory.stringToXMLGregorianCalendar("1948-10-28", dateFormat));
        individual.setNationality("GBR");
        individual.setNumberOfDependents(new BigInteger("1"));
        individual.setMaritalStatus("003");
        individual.setGender("001");
        individual.setEmploymentStatus("003");
        individual.setCurrentEmploymentDuration("0505");
        individual.getIndividualName().add(individualName);
        customer.setIsPlayedBy(individual);
        customer.setNewCustomerIndicator(true);
        customer.setIsAuthCustomer(false);
        customer.setExistingSortCode("772216");
        customer.setExistingAccountNumber("32771660");
        customer.setExistingAccountDuration("0505");
        customer.setCidPersID("+00026267042");
        customer.setUserType("1001");
        customer.setInternalUserIdentifier("10.245.224.125");
        customer.setHasExistingCreditCard(false);
        customer.setIsRegistrationSelected(false);
        customer.setCustomerSegment("3");
        customer.setCustomerIdentifier("294374712");
return customer;
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

    public FinanceServiceArrangement createFSAForCC(long arrangementId) {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setArrangementId(String.valueOf(arrangementId));
        financeServiceArrangement.setArrangementType("CC");
        financeServiceArrangement.setAccountNumber("4545");
        financeServiceArrangement.setInitiatedThrough(new Channel());
        financeServiceArrangement.getInitiatedThrough().setChannelCode("545");
        financeServiceArrangement.setAssociatedProduct(new Product());

        Product product = new Product();
        product.setProductIdentifier("10002");

        ProductOffer productOffer = new ProductOffer();
        productOffer.setProdOfferIdentifier("1000001");
        product.getProductoffer().add(productOffer);
        product.setProductName("Classic Credit Card");
        financeServiceArrangement.setAssociatedProduct(product);

        return financeServiceArrangement;
    }

    public List<ReferenceDataLookUp> createChannelIdLookupData() {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(new ReferenceDataLookUp("Cnt_Pnt_Prtflio", "0000777505", "Display Contact_Point_Portfolio", 26L, "CONTACT_POINT_ID", "LTB", 1L));

        return referenceDataLookUpList;
    }

    public List<ReferenceDataLookUp> createLookupData() {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 513L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "1", "Purpose of Account", 1091L, "SPORI", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "2", "Purpose of Account", 1092L, "BIEXP", "LTB", 1L));

        return referenceDataLookUpList;
    }

    @Transactional
    public long createApplicationSA(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber) {
        return createApplication(applicationStatus, eidvStatus, asmDecision, applicationSubStatus, niNumber, productTypeCreditCard).getId();
    }

    @Transactional
    public Applications createNewApplication(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        return createApplication(applicationStatus, eidvStatus, asmDecision, applicationSubStatus, niNumber, productType);
    }

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

        ApplicationParameterValues applicationParameterValues1 = new ApplicationParameterValues(applicationParameters5, applications, "2974109HL890678330000003225830841102", null);
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

        applicationParameterValuesDao.save(applicationParameterValues1);
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
        applicationParameterValuesSet.add(applicationParameterValues1);
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

        ProductPackageTypes productPackageTypes1 = new ProductPackageTypes("2004","Typical");
        ApplicationProducts applicationProducts1 = new ApplicationProducts(productPackageTypes1,applications,"Y","Y","Y",null,2000866L);
        Set<ApplicationProducts> applicationProductsSet = new HashSet<>();
        applicationProductsSet.add(applicationProducts1);


        productPackageTypesDao.save(productPackageTypes1);
        productPackageTypesDao.findAll();

        applicationProductsDao.save(applicationProducts1);
        applicationProductsDao.findAll();

        applications.getApplicationProducts().addAll(applicationProductsSet);
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

        TelephoneAddresses telephoneAddresses = new TelephoneAddresses();
        telephoneAddresses.setTelephoneNumber(new BigDecimal("9123456789"));
        telephoneAddresses.setTelephoneAddressTypes(telephoneAddressTypes);
        telephoneAddresses.setIndividuals(individuals);
        Set<TelephoneAddresses> telephoneAddressesSet = new HashSet<>();
        telephoneAddressesSet.add(telephoneAddresses);
        telephoneAddressesDao.save(telephoneAddressesSet);
        telephoneAddressesDao.findAll();

        individuals.setTelephoneAddresses(telephoneAddressesSet);


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
        productTypesDao.save(productTypeCreditCard);
        productTypesDao.save(productTypesSavings);
        productTypesDao.findAll();
        brandsDao.save(brands);
        brandsDao.findAll();
        userTypesDao.save(userTypes);
        userTypesDao.findAll();
        appStatusDao.save(applicationStatus);
        appStatusDao.save(new ApplicationStatus("1003", "Referred"));
        appStatusDao.save(new ApplicationStatus(com.lloydsbanking.salsa.constant.ApplicationStatus.AWAITING_FULFILMENT.getValue(), "Awaiting Fulfilment"));
        appStatusDao.save(new ApplicationStatus("1010", "Fulfill"));
        appStatusDao.save(new ApplicationStatus("1008","Awaiting Referral Processing"));
        appStatusDao.save(new ApplicationStatus("1004","Decline"));
        appStatusDao.save(new ApplicationStatus("1007","Awaiting Manual IDandV"));
        appStatusDao.save(new ApplicationStatus("1013","Awaiting Post Fulfilment Process"));
        appStatusDao.findAll();
        applicationTypesDao.save(applicationTypes);
        applicationTypesDao.findAll();
        promotionChannelsDao.save(promotionChannels);
        promotionChannelsDao.findAll();
        channelsDao.save(channels);
        channelsDao.findAll();
        applicationFormsDao.save(applicationForm);
        applicationFormsDao.findAll();
        applicationPartyRolesDao.save(applicationPartyRoles);
        applicationPartyRolesDao.save(applicationPartyRolesForAdditionalCard);
        applicationPartyRolesDao.save(applicationPartyRolesForPromoParty);
        applicationPartyRolesDao.save(applicationPartyRolesForPromoPartyAffiliate);
        applicationPartyRolesDao.save(applicationPartyRolesForSecondaryParty);
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
        applicationParametersDao.save(applicationParameters5);
        applicationParametersDao.save(applicationParameters6);
        applicationParametersDao.save(applicationParameters7);
        applicationParametersDao.save(applicationParameters8);
        applicationParametersDao.save(applicationParameters9);
        applicationParametersDao.save(applicationParameters10);
        applicationParametersDao.save(applicationParameters11);
        applicationParametersDao.save(applicationParameters11);

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

        telephoneAddressTypes.setCode("7");
        telephoneAddressTypes.setDescription("Mobile");
        telephoneAddressTypesDao.save(telephoneAddressTypes);
        telephoneAddressTypesDao.findAll();


        applicationFeatureTypesDao.save(applicationFeatureTypes1);
        applicationFeatureTypesDao.save(applicationFeatureTypes2);
        applicationFeatureTypesDao.save(applicationFeatureTypes3);
        applicationFeatureTypesDao.save(applicationFeatureTypes4);
        applicationFeatureTypesDao.save(applicationFeatureTypes5);
        applicationFeatureTypesDao.save(applicationFeatureTypes6);
        applicationFeatureTypesDao.save(applicationFeatureTypes7);
        applicationFeatureTypesDao.save(applicationFeatureTypes8);
        applicationFeatureTypesDao.save(applicationFeatureTypes9);
        applicationFeatureTypesDao.save(applicationFeatureTypes10);
        applicationFeatureTypesDao.save(applicationFeatureTypes11);
        applicationFeatureTypesDao.findAll();

        referralStatusDao.save(new ReferralStatus("PEN","Pending"));
        referralStatusDao.save(new ReferralStatus("COM","Completed"));
        referralStatusDao.findAll();

        abandonDeclineReasonDao.save(new AbandonDeclineReasons("102","ASM Decline"));
        abandonDeclineReasonDao.findAll();
    }

    public ActivateProductArrangementRequest createApaRequestByDBEvent() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createFinanceServiceArrangementForDBEvent());
        activateProductArrangementRequest.setSourceSystemIdentifier(DB_EVENT_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestWithInvalidStatus(long arrangementId) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createFinanceServiceArrangement(String.valueOf(arrangementId)));
        activateProductArrangementRequest.setSourceSystemIdentifier(OAP_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    private ProductArrangement createFinanceServiceArrangementForDBEvent() {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setArrangementId("90261");
        financeServiceArrangement.setArrangementType("CC");
        financeServiceArrangement.setApplicationStatus("1002");

        Product product = new Product();
        product.setProductIdentifier("92");
        product.setBrandName("LTB");
        product.getProductoffer().add(new ProductOffer());
        product.getProductoffer().get(0).setProdOfferIdentifier("2000866");

        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("P_CLUB");
        product.setInstructionDetails(instructionDetails);

        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("502");

        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);

        product.getAssociatedFamily().add(productFamily);
        product.setProductName("Classic Account");
        financeServiceArrangement.setAssociatedProduct(product);

        Channel channel = new Channel();
        channel.setChannelCode("004");
        channel.setSubChannelCode("003");
        financeServiceArrangement.setInitiatedThrough(channel);

        Customer customer = getPrimaryInvolvedParty();
        customer.setIsRegistrationSelected(Boolean.FALSE);
        financeServiceArrangement.setPrimaryInvolvedParty(customer);

        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("306521");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        financeServiceArrangement.setFinancialInstitution(organisation);
        financeServiceArrangement.getFinancialInstitution().setChannel("LTB");
        financeServiceArrangement.setMarketingPreferenceByEmail(false);
        financeServiceArrangement.setIsJointParty(false);
        financeServiceArrangement.setApplicationSubStatus("1023");

        AffiliateDetails affiliateDetails = getAffiliateDetails();
        financeServiceArrangement.getAffiliatedetails().add(affiliateDetails);

        updateArrangementHistory(financeServiceArrangement);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("N");
        financeServiceArrangement.getConditions().add(ruleCondition);

        financeServiceArrangement.setApplicationType("10001");
        financeServiceArrangement.setRetryCount(1);
        financeServiceArrangement.setAccountPurpose("BIEXP");
        financeServiceArrangement.setFundingSource("1");
        financeServiceArrangement.setLastModifiedDate(createXMLGregorianCalendar(2015, 8, 25));
        financeServiceArrangement.setRelatedApplicationExists(false);
        financeServiceArrangement.setMarketingPreferenceByEmail(false);
        financeServiceArrangement.setMarketingPreferenceByMail(false);
        financeServiceArrangement.setMarketingPreferenceByPhone(false);

        return financeServiceArrangement;
    }

    private void updateArrangementHistory(FinanceServiceArrangement financeServiceArrangement) {
        ArrangementHistory arrangementHistory1 = new ArrangementHistory();
        arrangementHistory1.setStatus("1001");
        arrangementHistory1.setUserType("1001");
        arrangementHistory1.setUserIdentifier("10.245.224.125");
        arrangementHistory1.setDateModified(createXMLGregorianCalendar(2015, 8, 15));
        financeServiceArrangement.getArrangementHistory().add(arrangementHistory1);

        ArrangementHistory arrangementHistory2 = new ArrangementHistory();
        arrangementHistory2.setStatus("1002");
        arrangementHistory2.setSubStatus("1023");
        arrangementHistory2.setRetryCount("1");
        arrangementHistory2.setUserType("1001");
        arrangementHistory2.setUserIdentifier("10.245.224.125");
        arrangementHistory2.setDateModified(createXMLGregorianCalendar(2015, 8, 25));
        financeServiceArrangement.getArrangementHistory().add(arrangementHistory2);
    }

    public AffiliateDetails getAffiliateDetails() {
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

    public RetrieveProductConditionsRequest createRetrieveProductConditionsRequest(Product product) {
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        retrieveProductConditionsRequest.setProduct(product);
        return retrieveProductConditionsRequest;
    }

    public RetrieveProductConditionsResponse createRetrieveProductConditionsResponse() {
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
        productOffer.setProdOfferIdentifier("2000866");
        Template template = new Template();
        template.setTemplateIdentifier("1");
        template.setSystemCode("00001");
        template.setExternalTemplateIdentifier("CCA_Generic");
        productOffer.getTemplate().add(template);
        product.getProductoffer().add(productOffer);
        product.setProductPropositionIdentifier("9211");
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

    public F425Resp createF425Resp(String asmDecisionCode, String asmCreditScore) {
        F425Resp response = createF425Resp();
        response.getResultsDetails().setASMDecisionSourceCd(asmDecisionCode);
        response.getResultsDetails().setASMCreditScoreResultCd(asmCreditScore);
        ProductOffered productOffered = new ProductOffered();
        productOffered.setProductsOfferedCd("502");
        response.getProductOffered().add(productOffered);
        return response;
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


    public F241Resp createF241V1Response(String severityCode) {
        F241Resp f241Resp = new F241Resp();
        com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Result f241Result = new com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode(Byte.valueOf(severityCode));
        f241Result.setResultCondition(resultCondition);
        f241Resp.setF241Result(f241Result);
        CardData cardData = new CardData();
        cardData.setCardLogoId((short)300);
        cardData.setCardNo("3324");
        cardData.setCreditCardSequenceNo((short)1);
        cardData.setCardAddResultCd((short)0);
        cardData.setChipSequenceNo((short)1);
        f241Resp.getCardData().add(cardData);
        f241Resp.setAccountNumberExternalId("0001203300013611675");
        f241Resp.setCustomerNumberExternalId("9120000000123682616");
        return f241Resp;
    }

    public com.lloydsbanking.salsa.soap.fdi.f241.objects.F241Resp createF241Response(String severityCode) {
        com.lloydsbanking.salsa.soap.fdi.f241.objects.F241Resp f241Resp = new com.lloydsbanking.salsa.soap.fdi.f241.objects.F241Resp();
        F241Result f241Result = new F241Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode(Byte.valueOf(severityCode));
        f241Result.setResultCondition(resultCondition);
        f241Resp.setF241Result(f241Result);
        com.lloydsbanking.salsa.soap.fdi.f241.objects.CardData cardData = new com.lloydsbanking.salsa.soap.fdi.f241.objects.CardData();
        cardData.setCardLogoId((short)300);
        cardData.setCardNo("3324");
        cardData.setCreditCardSequenceNo((short)1);
        cardData.setCardAddResultCd((short)0);
        cardData.setChipSequenceNo((short)1);
        f241Resp.getCardData().add(cardData);
        f241Resp.setAccountNumberExternalId("0001203300013611675");
        f241Resp.setCustomerNumberExternalId("9120000000123682616");
        return f241Resp;
    }

    public F241Req createF241V1Request(ProductArrangement productArrangement) {
        F241Req f241Req = f241V1RequestFactory.convert((FinanceServiceArrangement) productArrangement);
        return f241Req;
    }

    public com.lloydsbanking.salsa.soap.fdi.f241.objects.F241Req createF241Request(ProductArrangement productArrangement) {
        com.lloydsbanking.salsa.soap.fdi.f241.objects.F241Req f241Req = f241RequestFactory.convert((FinanceServiceArrangement) productArrangement);
        return f241Req;
    }

    public F259Resp createF259Response(String severityCode) {
        F259Resp f259Resp = new F259Resp();
        F259Result f259Result = new F259Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode(Byte.valueOf(severityCode));
        resultCondition.setReasonCode(0);
        f259Result.setResultCondition(resultCondition);
        f259Resp.setF259Result(f259Result);
        f259Resp.setAdditionalDataIn(0);
        com.lloydsbanking.salsa.soap.ocis.f259.objects.AuditData auditData = new com.lloydsbanking.salsa.soap.ocis.f259.objects.AuditData();
        auditData.setProdHeldId(1042400119l);
        auditData.setProdHeldAuditDt("16122015");
        auditData.setProdHeldAuditTm("115929");
        auditData.setProdHeldRoleAuditDt("16122015");
        auditData.setProdHeldRoleAuditTm("115929");
        f259Resp.setAuditData(auditData);
        return f259Resp;
    }

    public StB748BWrkngDateAfterXDays createB748Response(int severityCode) {
        StB748BWrkngDateAfterXDays stB748BWrkngDateAfterXDays = new StB748BWrkngDateAfterXDays();
        stB748BWrkngDateAfterXDays.setSterror(new StError());
        stB748BWrkngDateAfterXDays.getSterror().setErrorno(severityCode);
        return stB748BWrkngDateAfterXDays;
    }

    public StB748AWrkngDateAfterXDays createB748Request(RequestHeader requestHeader) {
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader();
        String contactPointId = headerRetriever.getContactPoint(requestHeader).getContactPointId();
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader);
        BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter = new BapiHeaderToStHeaderConverter();
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(bapiHeader,serviceRequest,contactPointId);
        StB748AWrkngDateAfterXDays stB748AWrkngDateAfterXDays = new StB748AWrkngDateAfterXDays();
        stB748AWrkngDateAfterXDays.setStheader(stHeader);
        stB748AWrkngDateAfterXDays.setNumOfDays(1);
        stB748AWrkngDateAfterXDays.setDateUserRequested(dateFactory.getCurrentDate());
        return stB748AWrkngDateAfterXDays;
    }

    public F061Resp createF061Resp() {
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

    public AssessmentEvidence createAssessmentEvidence() {
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        assessmentEvidence.setAddressStrength("6:7");
        assessmentEvidence.setIdentityStrength("8:9");
        return assessmentEvidence;
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

    public F062Resp createF062RespWithError() {
        F062Resp f062Resp = new F062Resp();
        f062Resp.setF062Result(new F062Result());
        f062Resp.getF062Result().setResultCondition(new ResultCondition());
        f062Resp.getF062Result().getResultCondition().setReasonCode(163003);
        f062Resp.getF062Result().getResultCondition().setReasonText("CHANNEL_OUTLET_TYPE_INVALID_CODE");
        return f062Resp;
    }
    public StB751BAppPerCCRegAuth createB751Response(int tacver) {
        StB751BAppPerCCRegAuth b751response = new StB751BAppPerCCRegAuth();
        com.lloydsbanking.salsa.soap.fs.application.StError stError = new com.lloydsbanking.salsa.soap.fs.application.StError();
        stError.setErrorno(0);
        b751response.setSterror(stError);
        b751response.setTacver(tacver);
        b751response.setPartyidEmergingChannelUserId("777602816");
        return b751response;
    }

    public StB751BAppPerCCRegAuth createB751ResponseWithError() {
        StB751BAppPerCCRegAuth b751response = new StB751BAppPerCCRegAuth();
        com.lloydsbanking.salsa.soap.fs.application.StError stError = new com.lloydsbanking.salsa.soap.fs.application.StError();
        stError.setErrorno(1121453);
        b751response.setSterror(stError);
        return b751response;
    }

    public StB751AAppPerCCRegAuth createB751Request() {
        StB751AAppPerCCRegAuth b751request = new StB751AAppPerCCRegAuth();
        com.lloydsbanking.salsa.soap.fs.application.StHeader stHeader = new com.lloydsbanking.salsa.soap.fs.application.StHeader();
        stHeader.setUseridAuthor("UNAUTHSALE");
        StParty stParty = new StParty();
        stParty.setHost("I");
        stParty.setPartyid("AAGATEWAY");
        stParty.setOcisid(new BigInteger("1022245653"));
        stHeader.setStpartyObo(stParty);
        stHeader.setChanid("IBL");
        stHeader.setChanidObo("IBL");
        stHeader.setChansecmode("PWD");
        stHeader.setSessionid("mBsicvvHKgZriAP1OzdStfP");
        stHeader.setIpAddressCaller("10.16.30.136");
        stHeader.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
        stHeader.setAcceptLanguage("0");
        stHeader.setInboxidClient("GX");
        stHeader.setChanctxt(new BigInteger("1"));
        stHeader.setEncVerNo(new BigInteger("0"));
        stHeader.setCodosouid("777505");
        b751request.setStheader(stHeader);
        b751request.setBNewToBank(true);
        b751request.setAppid(new BigInteger("1160534527"));
        b751request.setAppver(new BigInteger("0"));

        StParty stParty1 = new StParty();
        stParty1.setHost("I");
        stParty1.setPartyid("AAGATEWAY");
        stParty1.setOcisid(new BigInteger("1022245653"));
        b751request.setStparty(stParty1);
        com.lloydsbanking.salsa.soap.fs.application.StAccount stAccount = new com.lloydsbanking.salsa.soap.fs.application.StAccount();
        stAccount.setHost("I");
        stAccount.setProdtype("AAGATEWAY");
        stAccount.setAccno("09545468");
        b751request.setStaccount(stAccount);
        return b751request;

    }

    public F060Resp createF060Resp() {
        F060Resp f060Resp = new F060Resp();
        f060Resp.setAdditionalDataIn(1);
        f060Resp.setF060Result(new F060Result());
        return f060Resp;
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
        stHeader.setStpartyObo(new com.lloydsbanking.salsa.soap.fs.system.StParty());
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

    public List<RefDataLookup> generateLookUpData() {

        RefDataLookup lookUpChannel = new RefDataLookup(new RefDataLookupId("Cnt_Pnt_Prtflio", "000777505"), "description", 1L, (short) 1, "LTB", "LTB");
        RefDataLookup lookUpEmailId = new RefDataLookup(new RefDataLookupId("SENDER_EMAIL_ID", "LTB"), "description", 2L, (short) 1, "donotreply@test.lloydsbank.co.uk", "donotreply@test.lloydsbank.co.uk");

        RefDataLookup lookUpUserId = new RefDataLookup(new RefDataLookupId("AUTH_DETAILS", "USER_ID"), "USER_ID", 3L, (short) 1, "GALCRCPOC1", "GALCRCPOC1");
        RefDataLookup lookUpPasswd = new RefDataLookup(new RefDataLookupId("AUTH_DETAILS", "PASSWORD"), "PASSWORD", 4L, (short) 1, "surv1v0r21", "surv1v0r21");

        RefDataLookup lookUpUserIdO2 = new RefDataLookup(new RefDataLookupId("AUTH_DETAILS", "USER_ID_O2"), "USER_ID_O2", 5L, (short) 1, "GALCRCPOC2", "GALCRCPOC2");
        RefDataLookup lookUpPasswdO2 = new RefDataLookup(new RefDataLookupId("AUTH_DETAILS", "PASSWORD_O2"), "PASSWORD_O2", 6L, (short) 1, "surv1v0r22", "surv1v0r22");

        RefDataLookup lookUpUserIdVer = new RefDataLookup(new RefDataLookupId("AUTH_DETAILS", "USER_ID_O2_VER"), "USER_ID_O2_VER", 7L, (short) 1, "GALCRCPOC3", "GALCRCPOC3");
        RefDataLookup lookUpPasswdVer = new RefDataLookup(new RefDataLookupId("AUTH_DETAILS", "PASSWORD_O2_VER"), "PASSWORD_O2_VER", 8L, (short) 1, "surv1v0r23", "surv1v0r23");

        RefDataLookup lookUpCompanyCode = new RefDataLookup(new RefDataLookupId("AUTH_DETAILS", "COMPANY_CODE"), "COMPANY_CODE", 9L, (short) 1, "LTSB", "LTSB");

        RefDataLookup lookUpServiceId = new RefDataLookup(new RefDataLookupId("AUTH_DETAILS", "SERVICE_ID"), "SERVICE_ID", 10L, (short) 1, "1576", "1576");
        RefDataLookup lookUpServiceIdVer = new RefDataLookup(new RefDataLookupId("AUTH_DETAILS", "SERVICE_ID_VER"), "SERVICE_ID_VER", 11L, (short) 1, "1577", "1577");


        List<RefDataLookup> lookUps = new ArrayList<>();

        lookUps.add(lookUpChannel);
        lookUps.add(lookUpEmailId);

        lookUps.add(lookUpUserId);
        lookUps.add(lookUpPasswd);

        lookUps.add(lookUpUserIdO2);
        lookUps.add(lookUpPasswdO2);

        lookUps.add(lookUpUserIdVer);
        lookUps.add(lookUpPasswdVer);

        lookUps.add(lookUpCompanyCode);

        lookUps.add(lookUpServiceId);
        lookUps.add(lookUpServiceIdVer);

        return lookUps;

    }

    public com.lloydsbanking.salsa.commsmanager.ref.model.Channel generateChannelData() {
        com.lloydsbanking.salsa.commsmanager.ref.model.Channel channel = new com.lloydsbanking.salsa.commsmanager.ref.model.Channel();
        channel.setCode("2");
        channel.setType("LTB");
        channel.setDescription("LLyods");
        return channel;
    }

    public com.lloydsbanking.salsa.commsmanager.ref.model.CommunicationTemplate generateEmailCommunicationTemplate(String templateId) {

        com.lloydsbanking.salsa.commsmanager.ref.model.CommunicationTemplate communicationTemplate = new com.lloydsbanking.salsa.commsmanager.ref.model.CommunicationTemplate();

        communicationTemplate.setSubject("Your credit card application was successful");
        communicationTemplate.setTemplateID(templateId);
        communicationTemplate.setTemplateType("EMAIL");

        communicationTemplate.setCommunicationTemplateElements(generateCommunicationTemplateElements(templateId));

        return communicationTemplate;

    }

    public com.lloydsbanking.salsa.commsmanager.ref.model.CommunicationTemplate generateSmsCommunicationTemplate(String templateId) {

        com.lloydsbanking.salsa.commsmanager.ref.model.CommunicationTemplate communicationTemplate = new com.lloydsbanking.salsa.commsmanager.ref.model.CommunicationTemplate();

        communicationTemplate.setSubject("APBS");
        communicationTemplate.setTemplateID(templateId);
        communicationTemplate.setTemplateType("SMS");

        communicationTemplate.setCommunicationTemplateElements(generateCommunicationTemplateElements(templateId, templateId));

        return communicationTemplate;

    }

    private Set<CommunicationTemplateElement> generateCommunicationTemplateElements(String templateId, String contentPath) {

        Set<CommunicationTemplateElement> elementList = new HashSet<>();

        CommunicationTemplateElement element1 = new CommunicationTemplateElement();
        element1.setAttachmentId("1");
        element1.setContentId(contentPath);
        element1.setContentSequence((short) 1);
        element1.setTemplateID(templateId);

        CommunicationTemplateElement element2 = new CommunicationTemplateElement();
        element2.setAttachmentId("2");
        element2.setContentId(contentPath);
        element2.setContentSequence((short) 1);
        element2.setTemplateID(templateId);

        elementList.add(element1);
        elementList.add(element2);

        return elementList;

    }

    private Set<CommunicationTemplateElement> generateCommunicationTemplateElements(String templateId) {

        Set<CommunicationTemplateElement> elementList = new HashSet<>();

        CommunicationTemplateElement element1 = new CommunicationTemplateElement();
        element1.setAttachmentId("1");
        element1.setContentId("product/creditcard/emailtemplate/e001_welcome");
        element1.setContentSequence((short) 1);
        element1.setTemplateID(templateId);

        CommunicationTemplateElement element2 = new CommunicationTemplateElement();
        element2.setAttachmentId("1");
        element2.setContentId("product/creditcard/emailtemplate/e001_welcome");
        element2.setContentSequence((short) 2);
        element2.setTemplateID(templateId);

        elementList.add(element1);
        elementList.add(element2);

        return elementList;

    }
}
