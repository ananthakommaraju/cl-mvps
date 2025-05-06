package com.lloydsbanking.salsa.ppae;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydsbanking.salsa.offer.eligibility.convert.OfferToEligibilityRequestConverter;
import com.lloydsbanking.salsa.offer.eligibility.downstream.EligibilityService;
import com.lloydsbanking.salsa.ppae.client.*;
import com.lloydsbanking.salsa.ppae.service.convert.PrdRequestFactory;
import com.lloydsbanking.salsa.soap.asm.f204.objects.CSAddressDetails;
import com.lloydsbanking.salsa.soap.asm.f204.objects.DecisionDetails;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Result;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Result;
import com.lloydsbanking.salsa.soap.asm.f205.objects.FacilitiesOffered;
import com.lloydsbanking.salsa.soap.asm.f205.objects.ProductsOffered;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Result;
import com.lloydsbanking.salsa.soap.asm.f424.objects.ProductOffered;
import com.lloydsbanking.salsa.soap.fs.loan.*;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.AddressGroup;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.F595Req;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.F595Resp;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.PartyGroup;
import com.lloydsbanking.salsa.soap.pad.f263.objects.*;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Identifiers;
import com.lloydsbanking.salsa.soap.pad.q028.objects.OutIdentifiers;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Req;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Resp;
import com.lloydstsb.ib.wsbridge.loan.StB232ALoanCCASign;
import com.lloydstsb.ib.wsbridge.loan.StB232BLoanCCASign;
import com.lloydstsb.ib.wsbridge.loan.StB233ALoanIllustrate;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import lib_sim_bo.businessobjects.*;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_gmo.messages.ResponseHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
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

    @Autowired
    ProductTypesDao productTypesDao;

    @Autowired
    PromoPartyApplicationsDao promoPartyApplicationsDao;

    @Autowired
    OfferToEligibilityRequestConverter offerToEligibilityRequestConverter;

    @Autowired
    BrandsDao brandsDao;

    @Autowired
    PartyApplicationsDao partyApplicationsDao;

    @Autowired
    PromotionChannelsDao promotionChannelsDao;

    @Autowired
    ChannelsDao channelsDao;

    @Autowired
    IndividualAddressesDao individualAddressesDao;

    @Autowired
    ApplicationRelationshipTypesDao applicationRelationshipTypesDao;

    @Autowired
    ApplicationFormsDao applicationFormsDao;

    @Autowired
    ApplicationFeatureTypesDao applicationFeatureTypesDao;

    @Autowired
    PrdRequestFactory prdRequestFactory;

    @Autowired
    ApplicationParametersDao applicationParametersDao;

    @Autowired
    ParameterGroupsDao parameterGroupsDao;

    @Autowired
    UserTypesDao userTypesDao;

    @Autowired
    ApplicationParameterValuesDao applicationParameterValuesDao;

    @Autowired
    TelephoneAddressesDao telephoneAddressesDao;

    @Autowired
    TelephoneAddressTypesDao telephoneAddressTypesDao;

    @Autowired
    ApplicationsRelatedDao applicationsRelatedDao;

    @Autowired
    ApplicationStatusDao appStatusDao;

    @Autowired
    ApplicationTypesDao applicationTypesDao;

    @Autowired
    ApplicationPartyRolesDao applicationPartyRolesDao;

    @Autowired
    IndividualsDao individualsDao;

    @Autowired
    StreetAddressesDao streetAddressesDao;

    @Autowired
    KycStatusDao kycStatusDao;

    @Autowired
    ApplicationsDao applicationsDao;

    @Autowired
    ProductPackageTypesDao productPackageTypesDao;

    @Autowired
    ApprovalStatusDao approvalStatusDao;

    @Autowired
    EventStoresDao eventStoresDao;
    @Autowired
    ReferralsDao referralsDao;
    @Autowired
    ReferralTeamsDao referralTeamsDao;
    @Autowired
    ReferralStatusDao referralStatusDao;
    @Autowired
    ApplicationFeaturesDao applicationFeaturesDao;
    @Autowired
    KycEvidenceDetailsDao kycEvidenceDetailsDao;

    public static final String TEST_BUSINESS_TRANSACTION = "processPendingArrangement";
    public static final String TEST_INTERACTION_ID = "70bdfea6f48211e4add0e3a875e8c842";
    public static final String TEST_CONTACT_POINT_ID = "0000777505";
    public final static String TEST_OCIS_ID = "1433933835";
    public static final String TEST_CUSTOMER_ID = "RD888225 ";
    public static final String TEST_MESSAGE_ID = "a0fn7kh3mtma1201i2z6psoe9";

    public static final String TEST_CHANNEL_ID = "LTB";
    public static final String TEST_CHANNEL_ID_TSB = "VER";
    public static final String PPAE_SWITCH = "SW_EnSlsPrPndgSv";


    UserTypes userTypes = new UserTypes("1001", "Customer");

    ApplicationTypes applicationTypes = new ApplicationTypes("10001", "New Application");

    PromotionChannels promotionChannels = new PromotionChannels("003", "Direct Mail");

    Channels channels = new Channels("004", "Internet");
    ProductPackageTypes productPackageTypes = new ProductPackageTypes("2004", "Typical");

    ApplicationForms applicationForm = new ApplicationForms(10002, "Ava Sales Question");


    ApprovalStatus approvalStatus = new ApprovalStatus();

    ApplicationPartyRoles applicationPartyRolesForPromoParty = new ApplicationPartyRoles("0003", "Promotional Party Affiliate");

    ParameterGroups parameterGroups1 = new ParameterGroups("CBS", "CBS Param Group");
    ParameterGroups parameterGroups2 = new ParameterGroups("ASM", "ASM Param Group");
    ParameterGroups parameterGroups3 = new ParameterGroups("CMAS", "CMAS Param Group");
    ParameterGroups parameterGroups4 = new ParameterGroups("PEGA", "PEGA Param Group");
    ParameterGroups parameterGroups5 = new ParameterGroups("CCD", "Call Credit Param Group");
    ParameterGroups parameterGroups6 = new ParameterGroups("EIDV", "EIDV Param Group");
    ParameterGroups parameterGroups7 = new ParameterGroups("IB", "Internet Banking");
    ParameterGroups parameterGroups8 = new ParameterGroups("OCIS", "OCIS Param Group");

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
    ApplicationParameters applicationParameters12 = new ApplicationParameters("100057", parameterGroups2, "Intend To Switch");
    ApplicationParameters applicationParameters13 = new ApplicationParameters("100017", parameterGroups3, "Card Order Id");
    ApplicationParameters applicationParameters14 = new ApplicationParameters("100018", parameterGroups4, "Account Switching Workflow Task Id");
    ApplicationParameters applicationParameters15 = new ApplicationParameters("100019", parameterGroups1, "Interest Rate Auth EAR");
    ApplicationParameters applicationParameters16 = new ApplicationParameters("100020", parameterGroups1, "Interest Rate Auth Monthly");
    ApplicationParameters applicationParameters17 = new ApplicationParameters("100021", parameterGroups1, "Interest Rate Base");
    ApplicationParameters applicationParameters18 = new ApplicationParameters("100022", parameterGroups1, "Interest Rate Margin OBR");
    ApplicationParameters applicationParameters19 = new ApplicationParameters("100023", parameterGroups1, "Interest Rate Unauth EAR");
    ApplicationParameters applicationParameters20 = new ApplicationParameters("100024", parameterGroups1, "Interest Rate Unauth Monthly");
    ApplicationParameters applicationParameters21 = new ApplicationParameters(" 100025", parameterGroups1, "OD Expiry Date");
    ApplicationParameters applicationParameters22 = new ApplicationParameters(" 100026", parameterGroups1, "Currency code");
    ApplicationParameters applicationParameters23 = new ApplicationParameters("100027", parameterGroups1, "Interest Free Overdraft Amount");
    ApplicationParameters applicationParameters24 = new ApplicationParameters("100028", parameterGroups1, "Excess Fee Amount");
    ApplicationParameters applicationParameters25 = new ApplicationParameters("100029", parameterGroups1, "Excess Fee Balance Incremented");
    ApplicationParameters applicationParameters26 = new ApplicationParameters("100030", parameterGroups1, "Excess Fee Cap");
    ApplicationParameters applicationParameters27 = new ApplicationParameters("100031", parameterGroups1, "Base Rate Linked");
    ApplicationParameters applicationParameters28 = new ApplicationParameters("100032", parameterGroups7, "CCTM Session Id");
    ApplicationParameters applicationParameters29 = new ApplicationParameters("100033", parameterGroups5, "Bank Code");
    ApplicationParameters applicationParameters30 = new ApplicationParameters(" 100034", parameterGroups5, "	Bank Name");
    ApplicationParameters applicationParameters31 = new ApplicationParameters(" 100040", parameterGroups1, "Total Cost of Credit");
    ApplicationParameters applicationParameters32 = new ApplicationParameters("100041", parameterGroups2, "Gauranteed Offer Code");
    ApplicationParameters applicationParameters33 = new ApplicationParameters("100044", parameterGroups7, "Benefit Disclosure Alerts");
    ApplicationParameters applicationParameters34 = new ApplicationParameters(" 100049", parameterGroups7, "Car Finance expiry date");
    ApplicationParameters applicationParameters35 = new ApplicationParameters("100050", parameterGroups7, "Car Finance - Delivery Date");
    ApplicationParameters applicationParameters36 = new ApplicationParameters(" 100051", parameterGroups7, "Car Finance - Approval Date");
    ApplicationParameters applicationParameters37 = new ApplicationParameters("100061", parameterGroups7, "Expected Monthly Deposit Amount");
    ApplicationParameters applicationParameters40 = new ApplicationParameters("100039", parameterGroups1, "Auth Customer");
    ApplicationParameters applicationParameters41 = new ApplicationParameters("100047", parameterGroups8, "Product Preferential Rate Identifier");
    ApplicationParameters applicationParameters42 = new ApplicationParameters("100048", parameterGroups7, "User Notes");
    ApplicationParameters applicationParameters43 = new ApplicationParameters("100052", parameterGroups7, "Fuel Type");
    ApplicationParameters applicationParameters44 = new ApplicationParameters("100053", parameterGroups7, "Transmission");
    ApplicationParameters applicationParameters45 = new ApplicationParameters(" 100054", parameterGroups7, "Body Type");
    ApplicationParameters applicationParameters46 = new ApplicationParameters(" 100055", parameterGroups7, "Engine Type");
    ApplicationParameters applicationParameters47 = new ApplicationParameters("100056", parameterGroups1, "Link Failure");
    ApplicationParameters applicationParameters48 = new ApplicationParameters(" 100001", parameterGroups2, "ASM decline code");
    ApplicationParameters applicationParameters49 = new ApplicationParameters("100002", parameterGroups6, "EIDV decline code");
    ApplicationParameters applicationParameters50 = new ApplicationParameters(" 100003", parameterGroups6, "EIDV referral code");
    ApplicationParameters applicationParameters51 = new ApplicationParameters(" 100007", parameterGroups2, "ASM REFER");
    ApplicationParameters applicationParameters53 = new ApplicationParameters("100013", parameterGroups7, "Credit Score Request Number");
    ApplicationParameters applicationParameters54 = new ApplicationParameters(" 100014", parameterGroups7, "Loan Agreement Number");
    ApplicationParameters applicationParameters55 = new ApplicationParameters("100015", parameterGroups7, "IB Registered Flag");
    ApplicationParameters applicationParameters56 = new ApplicationParameters("100016", parameterGroups7, "Name and Address Verified Flag");
    ApplicationParameters applicationParameters57 = new ApplicationParameters("100035", parameterGroups1, "Temporary Overdraft");
    ApplicationParameters applicationParameters58 = new ApplicationParameters("100038", parameterGroups2, "Credit Limit");
    ApplicationParameters applicationParameters59 = new ApplicationParameters("100043", parameterGroups1, "Monthly Fee Amount");
    ApplicationParameters applicationParameters61 = new ApplicationParameters(" 100058", parameterGroups7, "Intend to overdraft");
    ApplicationParameters applicationParameters38 = new ApplicationParameters(" 100059", parameterGroups7, "Overdraft amount");
    ApplicationParameters applicationParameters39 = new ApplicationParameters("100060", parameterGroups2, "Overdraft Risk Code");
    ApplicationParameters applicationParameters52 = new ApplicationParameters("100045", parameterGroups2, "Credit Limit Amount");
    ApplicationParameters applicationParameters60 = new ApplicationParameters("100046", parameterGroups7, "Number of Credit Cards Held");

    ApplicationPartyRoles roles = new ApplicationPartyRoles("0001", "Key Party");


    ApplicationFeatureTypes applicationFeatureTypes1 = new ApplicationFeatureTypes("OD", "Overdraft");
    ApplicationFeatureTypes applicationFeatureTypes2 = new ApplicationFeatureTypes("CHQBK", "ChequeBook");
    ApplicationFeatureTypes applicationFeatureTypes3 = new ApplicationFeatureTypes("IBRA", "IB Registration Accepted");
    ApplicationFeatureTypes applicationFeatureTypes4 = new ApplicationFeatureTypes("BENPA", "Benefit Payments");
    ApplicationFeatureTypes applicationFeatureTypes5 = new ApplicationFeatureTypes("CIN", "Cinema");
    ApplicationFeatureTypes applicationFeatureTypes6 = new ApplicationFeatureTypes("IBRC", "IB Registration Completed");
    ApplicationFeatureTypes applicationFeatureTypes7 = new ApplicationFeatureTypes("ASA", "Account Switching Accepted");
    ApplicationFeatureTypes applicationFeatureTypes8 = new ApplicationFeatureTypes("ODPCI", "Date Pcci Viewed");
    ApplicationFeatureTypes applicationFeatureTypes9 = new ApplicationFeatureTypes("BT", "Balance Transfer Setup");

    TelephoneAddressTypes telephoneAddressTypes = new TelephoneAddressTypes();
    @Autowired
    AbandonDeclineReasonDao abandonDeclineReasonDao;

    @Transactional
    @Modifying
    public void cleanUp() {
        applicationsRelatedDao.deleteAll();
        referralsDao.deleteAll();
        referralTeamsDao.deleteAll();
        partyApplicationsDao.deleteAll();
        applicationParameterValuesDao.deleteAll();
        promoPartyApplicationsDao.deleteAll();
        applicationsDao.deleteAll();
        streetAddressesDao.deleteAll();
        individualAddressesDao.deleteAll();
        telephoneAddressesDao.deleteAll();
        individualsDao.deleteAll();
        kycStatusDao.deleteAll();
        appStatusDao.deleteAll();
        applicationTypesDao.deleteAll();
        productTypesDao.deleteAll();
        brandsDao.deleteAll();

        promotionChannelsDao.deleteAll();
        channelsDao.deleteAll();
        applicationRelationshipTypesDao.deleteAll();
        applicationParametersDao.deleteAll();
        telephoneAddressTypesDao.deleteAll();
        applicationFeatureTypesDao.deleteAll();
        userTypesDao.deleteAll();
        referralStatusDao.deleteAll();
        applicationPartyRolesDao.deleteAll();
        parameterGroupsDao.deleteAll();
        abandonDeclineReasonDao.deleteAll();
    }

    public RequestHeader createPpaeRequestHeader(String channel) {

        RequestHeaderBuilder requestHeaderBuilder = new RequestHeaderBuilder();
        return requestHeaderBuilder.businessTransaction(TEST_BUSINESS_TRANSACTION).channelId(channel).interactionId(TEST_INTERACTION_ID).bapiInformation(channel, TEST_INTERACTION_ID, TEST_OCIS_ID, "ns5").securityHeader("lgsm", TEST_CUSTOMER_ID).serviceRequest("ns4", TEST_BUSINESS_TRANSACTION, "10.245.182.105", TEST_MESSAGE_ID).contactPoint("ns4", "003", TEST_CONTACT_POINT_ID, "Internet Banking", "Browser", "127.0.0.1", "Customer").build();
    }

    public ProcessPendingArrangementEventRequest createPpaeRequest() {
        return createPpaeRequest("2", "LTB");
    }

    public ProcessPendingArrangementEventRequest createPpaeRequest(String applicationId, String channel) {
        ProcessPendingArrangementEventRequest prcProcessPendingArrangementEventRequest = new ProcessPendingArrangementEventRequest();
        prcProcessPendingArrangementEventRequest.setHeader(createPpaeRequestHeader(channel));
        prcProcessPendingArrangementEventRequest.getHeader().setChannelId(channel);
        prcProcessPendingArrangementEventRequest.getHeader().setArrangementId(applicationId);
        return prcProcessPendingArrangementEventRequest;
    }


    public void createPamReferenceData(ApplicationStatus applicationStatus, ProductTypes productTypes, Brands brands) {
        productTypesDao.save(productTypes);
        productTypesDao.findAll();
        brandsDao.save(brands);
        brandsDao.findAll();
        userTypesDao.save(userTypes);
        userTypesDao.findAll();
        appStatusDao.save(applicationStatus);
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
        applicationParametersDao.save(applicationParameters12);
        applicationParametersDao.save(applicationParameters13);
        applicationParametersDao.save(applicationParameters14);
        applicationParametersDao.save(applicationParameters15);
        applicationParametersDao.save(applicationParameters16);
        applicationParametersDao.save(applicationParameters17);
        applicationParametersDao.save(applicationParameters18);
        applicationParametersDao.save(applicationParameters19);
        applicationParametersDao.save(applicationParameters20);
        applicationParametersDao.save(applicationParameters21);
        applicationParametersDao.save(applicationParameters22);
        applicationParametersDao.save(applicationParameters23);
        applicationParametersDao.save(applicationParameters24);
        applicationParametersDao.save(applicationParameters25);
        applicationParametersDao.save(applicationParameters26);
        applicationParametersDao.save(applicationParameters27);
        applicationParametersDao.save(applicationParameters28);
        applicationParametersDao.save(applicationParameters29);
        applicationParametersDao.save(applicationParameters30);
        applicationParametersDao.save(applicationParameters31);
        applicationParametersDao.save(applicationParameters32);
        applicationParametersDao.save(applicationParameters33);
        applicationParametersDao.save(applicationParameters34);
        applicationParametersDao.save(applicationParameters35);
        applicationParametersDao.save(applicationParameters36);
        applicationParametersDao.save(applicationParameters37);
        applicationParametersDao.save(applicationParameters38);
        applicationParametersDao.save(applicationParameters39);
        applicationParametersDao.save(applicationParameters40);
        applicationParametersDao.save(applicationParameters41);
        applicationParametersDao.save(applicationParameters42);
        applicationParametersDao.save(applicationParameters43);
        applicationParametersDao.save(applicationParameters44);
        applicationParametersDao.save(applicationParameters45);
        applicationParametersDao.save(applicationParameters46);
        applicationParametersDao.save(applicationParameters47);
        applicationParametersDao.save(applicationParameters48);
        applicationParametersDao.save(applicationParameters49);
        applicationParametersDao.save(applicationParameters50);
        applicationParametersDao.save(applicationParameters51);
        applicationParametersDao.save(applicationParameters52);
        applicationParametersDao.save(applicationParameters53);
        applicationParametersDao.save(applicationParameters54);
        applicationParametersDao.save(applicationParameters55);
        applicationParametersDao.save(applicationParameters56);
        applicationParametersDao.save(applicationParameters57);
        applicationParametersDao.save(applicationParameters58);
        applicationParametersDao.save(applicationParameters59);
        applicationParametersDao.save(applicationParameters60);
        applicationParametersDao.save(applicationParameters61);
        applicationParametersDao.findAll();

        KycStatus kycStatus = new KycStatus();
        kycStatus.setCode("ACCEPT");
        kycStatus.setDescription("ACCEPT");
        kycStatusDao.save(kycStatus);
        kycStatusDao.findAll();

        KycStatus kycStatus1 = new KycStatus();
        kycStatus1.setCode("REFER");
        kycStatus1.setDescription("REFER");
        kycStatusDao.save(kycStatus1);
        kycStatusDao.findAll();


        KycStatus kycStatus2 = new KycStatus();
        kycStatus2.setCode("DECLINE");
        kycStatus2.setDescription("DECLINE");
        kycStatusDao.save(kycStatus2);
        kycStatusDao.findAll();

        KycStatus kycStatus3 = new KycStatus();
        kycStatus3.setCode("N/A");
        kycStatus3.setDescription("N/A");
        kycStatusDao.save(kycStatus3);
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

        applicationFeatureTypesDao.findAll();

        referralStatusDao.save(new ReferralStatus("PEN", "Pending"));
        referralStatusDao.save(new ReferralStatus("COM", "Completed"));
        referralStatusDao.findAll();

        abandonDeclineReasonDao.save(new AbandonDeclineReasons("102", "ASM Decline"));
        abandonDeclineReasonDao.findAll();

    }


    public ProductArrangement createProductArrangement() throws DatatypeConfigurationException {
        ProductArrangement productArrangement = new ProductArrangement();
        ProductOffer productOffer = new ProductOffer();
        productArrangement.setArrangementType("CC");
        productArrangement.setApplicationStatus("1006");
        productArrangement.setArrangementId("109254");
        Referral referral = new Referral();
        productArrangement.getReferral().add(referral);
        Product product = new Product();
        product.setStatusCode("isAccepted");
        productOffer.setOfferType("2004");
        productOffer.setProdOfferIdentifier("1000006");
        productOffer.setStatus("23e445");
        Channel channel = new Channel();
        channel.setChannelCode("004");
        channel.setSubChannelCode("002");
        Customer customer = new Customer();
        customer.setEmailAddress("asd@gmail.com");
        customer.setIsPlayedBy(new Individual());
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("1203");
        TelephoneNumber telephoneNumber1 = new TelephoneNumber();
        telephoneNumber1.setTelephoneType("7");
        telephoneNumber1.setAreaCode("12221111");
        telephoneNumber1.setCountryPhoneCode("44");
        telephoneNumber1.setPhoneNumber("74749446281");
        TelephoneNumber telephoneNumber2 = new TelephoneNumber();
        telephoneNumber2.setTelephoneType("7");
        telephoneNumber2.setCountryPhoneCode("44");
        telephoneNumber2.setPhoneNumber("112221111111");
        IndividualName individualName = new IndividualName();
        individualName.setFirstName("apoorv");
        individualName.setLastName("mehta");
        individualName.setPrefixTitle("Mr");
        product.getProductoffer().add(productOffer);
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        BigInteger bigInteger1 = new BigInteger("2");
        BigDecimal bigDecimal1 = new BigDecimal("123421");
        BigDecimal bigDecimal2 = new BigDecimal("1231");
        BigDecimal bigDecimal3 = new BigDecimal("2312");
        CurrencyAmount currencyAmount1 = new CurrencyAmount();
        currencyAmount1.setAmount(bigDecimal1);
        CurrencyAmount currencyAmount2 = new CurrencyAmount();
        currencyAmount2.setAmount(bigDecimal2);
        CurrencyAmount currencyAmount3 = new CurrencyAmount();
        currencyAmount3.setAmount(bigDecimal3);
        CustomerScore customerScore1 = new CustomerScore();
        customerScore1.setScoreResult("REFER");
        customerScore1.setAssessmentType("EIDV");
        CustomerScore customerScore2 = new CustomerScore();
        customerScore2.setScoreResult("2");
        customerScore2.setAssessmentType("ASM");
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("501");
        referralCode.setDescription("Credit Bureau Unavailable.");
        customerScore2.getReferralCode().add(referralCode);
        byte byte_value[] = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPGRhdGE6RW5jcnlwdERhdGFSZXNwb25zZSB4bWxuczpfeD0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8wNC94bWxlbmMjIiB4bWxuczpfeF8xPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIiB4bWxuczpkYXRhPSJodHRwOi8vd3d3Lmxsb3lkc3RzYi5jb20vU2NoZW1hL0ludGVybmV0QmFua2luZy9FbmNyeXB0RGF0YSI+CiAgPGRhdGE6b3V0ZGV0YWlscz4KICAgIDxkYXRhOmFzeW1tS2V5PldaX0VTQl9WMy1wcml2a2V5LnBlbTwvZGF0YTphc3ltbUtleT4KICAgIDxkYXRhOmFzeW1tQ2VydD5XWl9FU0JfVjMtc3NjZXJ0LnBlbTwvZGF0YTphc3ltbUNlcnQ+CiAgICA8ZGF0YTpvdXR0ZXh0PjxfeDpFbmNyeXB0ZWREYXRhIFR5cGU9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMDQveG1sZW5jI0NvbnRlbnQiPgogICAgICAgIDxfeDpFbmNyeXB0aW9uTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8wNC94bWxlbmMjYWVzMTI4LWNiYyIvPgogICAgICAgIDxfeF8xOktleUluZm8+PF94OkVuY3J5cHRlZEtleSBSZWNpcGllbnQ9Im5hbWU6V1pfRVNCX1YzIj4KICAgICAgICAgICAgPF94OkVuY3J5cHRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGVuYyNyc2EtMV81Ii8+CiAgICAgICAgICAgIDxfeF8xOktleUluZm8+PF94XzE6S2V5TmFtZT5XWl9FU0JfVjM8L194XzE6S2V5TmFtZT48L194XzE6S2V5SW5mbz4KICAgICAgICAgICAgPF94OkNpcGhlckRhdGE+CiAgICAgICAgICAgICAgPF94OkNpcGhlclZhbHVlPipOYW1lZCBjZXJ0aWZpY2F0ZSAnV1pfRVNCX1YzJyBub3QgdXAqPC9feDpDaXBoZXJWYWx1ZT4KICAgICAgICAgICAgPC9feDpDaXBoZXJEYXRhPgogICAgICAgICAgPC9feDpFbmNyeXB0ZWRLZXk+PC9feF8xOktleUluZm8+CiAgICAgICAgPF94OkNpcGhlckRhdGE+CiAgICAgICAgICA8X3g6Q2lwaGVyVmFsdWU+Kk5hbWVkIGNlcnRpZmljYXRlICdXWl9FU0JfVjMnIG5vdCB1cCo8L194OkNpcGhlclZhbHVlPgogICAgICAgIDwvX3g6Q2lwaGVyRGF0YT4KICAgICAgPC9feDpFbmNyeXB0ZWREYXRhPjwvZGF0YTpvdXR0ZXh0PgogICAgPGRhdGE6b3V0RW5jb2RlPmJhc2U2NDwvZGF0YTpvdXRFbmNvZGU+CiAgPC9kYXRhOm91dGRldGFpbHM+CjwvZGF0YTpFbmNyeXB0RGF0YVJlc3BvbnNlPg==".getBytes();
        AccessToken accessToken = new AccessToken();
        accessToken.setEncryptedMemorableInfo(byte_value);

        productArrangement.setAssociatedProduct(product);
        productArrangement.setInitiatedThrough(channel);
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.getAssociatedProduct().setProductIdentifier("10005");
        productArrangement.getAssociatedProduct().setBrandName("LTB");
        productArrangement.getAssociatedProduct().setGuaranteedOfferCode("Y");
        productArrangement.getAssociatedProduct().setProductName("Advance Credit Card");
        productArrangement.getOfferedProducts().add(new Product());
        productArrangement.getOfferedProducts().get(0).setStatusCode("isAccepted");
        productArrangement.getOfferedProducts().get(0).getProductoffer().add(new ProductOffer());
        productArrangement.getOfferedProducts().get(0).getProductoffer().get(0).setOfferType("2004");
        productArrangement.getOfferedProducts().get(0).getProductoffer().get(0).setProdOfferIdentifier("1000006");
        productArrangement.getOfferedProducts().get(0).getProductoffer().get(0).setStatus("23e445");

        productArrangement.getPrimaryInvolvedParty().getPostalAddress().add(postalAddress);
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setStructuredAddress(new StructuredAddress());
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setBuildingNumber("22");
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().add("PARK STREET");
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setPostTown("LONDON");
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setCountry("United Kingdom");
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setPostCodeIn("9EQ");
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setPostCodeOut("SE1");
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setPointSuffix("1D");
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().add(telephoneNumber1);
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().add(telephoneNumber2);


        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setIsStaffMember(false);
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setResidentialStatus("001");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setBirthDate(dateFactory.stringToXMLGregorianCalendar("1981-06-17", dateFormat));
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setNationality("BRB");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setNumberOfDependents(bigInteger1);
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setMaritalStatus("002");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setGender("001");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setEmploymentStatus("004");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setCurrentEmploymentDuration("0000");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setGrossAnnualIncome(currencyAmount1);
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setMonthlyLoanRepaymentAmount(currencyAmount2);
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setMonthlyMortgageAmount(currencyAmount3);

        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("1487435976");
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore1);
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore2);
        productArrangement.getPrimaryInvolvedParty().setExistingSortCode("771224");
        productArrangement.getPrimaryInvolvedParty().setExistingAccountNumber("11924168");
        productArrangement.getPrimaryInvolvedParty().setCidPersID("+00639348253");
        productArrangement.getPrimaryInvolvedParty().setUserType("1001");
        productArrangement.getPrimaryInvolvedParty().setInternalUserIdentifier("127.0.0.1");
        productArrangement.getPrimaryInvolvedParty().setHasExistingCreditCard(false);
        productArrangement.getPrimaryInvolvedParty().setHasExistingCreditCard(false);
        productArrangement.getPrimaryInvolvedParty().setCustomerSegment("3");
        productArrangement.getPrimaryInvolvedParty().setAccessToken(accessToken);
        productArrangement.getPrimaryInvolvedParty().setNewCustomerIndicator(true);
        productArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(false);
        productArrangement.setMarketingPreferenceBySMS(false);
        productArrangement.setIsJointParty(false);
        productArrangement.getOfferedProducts().get(0).setProductIdentifier("39917");
        productArrangement.getOfferedProducts().get(0).setStatusCode("isAccepted");
        ArrangementHistory arrangementHistory1 = new ArrangementHistory();
        arrangementHistory1.setStatus("1006");
        arrangementHistory1.setRetryCount("6");
        arrangementHistory1.setUserType("1001");
        arrangementHistory1.setUserIdentifier("127.0.0.1");
        arrangementHistory1.setDateModified(dateFactory.stringToXMLGregorianCalendar("2015-09-28T09:36:30Z", dateFormat));
        productArrangement.getArrangementHistory().add(arrangementHistory1);
        ArrangementHistory arrangementHistory2 = new ArrangementHistory();
        arrangementHistory2.setStatus("1006");
        arrangementHistory2.setRetryCount("2");
        arrangementHistory2.setUserType("1001");
        arrangementHistory2.setUserIdentifier("127.0.0.1");
        arrangementHistory2.setDateModified(dateFactory.stringToXMLGregorianCalendar("2015-12-23T10:16:15Z", dateFormat));
        ArrangementHistory arrangementHistory3 = new ArrangementHistory();
        arrangementHistory3.setStatus("1001");
        arrangementHistory3.setUserType("1001");
        arrangementHistory3.setUserIdentifier("127.0.0.1");
        arrangementHistory3.setDateModified(dateFactory.stringToXMLGregorianCalendar("2015-12-23T09:12:24Z", dateFormat));
        ArrangementHistory arrangementHistory4 = new ArrangementHistory();
        arrangementHistory4.setStatus("1006");
        arrangementHistory4.setRetryCount("3");
        arrangementHistory4.setUserType("1001");
        arrangementHistory4.setUserIdentifier("127.0.0.1");
        arrangementHistory4.setDateModified(dateFactory.stringToXMLGregorianCalendar("2015-12-23T10:17:05Z", dateFormat));
        ArrangementHistory arrangementHistory5 = new ArrangementHistory();
        arrangementHistory5.setStatus("1006");
        arrangementHistory5.setRetryCount("4");
        arrangementHistory5.setUserType("1001");
        arrangementHistory5.setUserIdentifier("127.0.0.1");
        arrangementHistory5.setDateModified(dateFactory.stringToXMLGregorianCalendar("2015-12-23T12:17:52Z", dateFormat));
        ArrangementHistory arrangementHistory6 = new ArrangementHistory();
        arrangementHistory6.setStatus("1006");
        arrangementHistory6.setRetryCount("7");
        arrangementHistory6.setUserType("1001");
        arrangementHistory6.setUserIdentifier("127.0.0.1");
        arrangementHistory6.setDateModified(dateFactory.stringToXMLGregorianCalendar("2015-12-24T20:17:55Z", dateFormat));
        ArrangementHistory arrangementHistory7 = new ArrangementHistory();
        arrangementHistory7.setStatus("1005");
        arrangementHistory7.setUserType("1001");
        arrangementHistory7.setUserIdentifier("127.0.0.1");
        arrangementHistory7.setDateModified(dateFactory.stringToXMLGregorianCalendar("2015-12-23T09:12:32Z", dateFormat));
        ArrangementHistory arrangementHistory8 = new ArrangementHistory();
        arrangementHistory8.setStatus("1006");
        arrangementHistory8.setRetryCount("1");
        arrangementHistory8.setUserType("1001");
        arrangementHistory8.setUserIdentifier("127.0.0.1");
        arrangementHistory8.setDateModified(dateFactory.stringToXMLGregorianCalendar("2015-12-23T09:16:12Z", dateFormat));
        ArrangementHistory arrangementHistory9 = new ArrangementHistory();
        arrangementHistory9.setStatus("1006");
        arrangementHistory9.setRetryCount("5");
        arrangementHistory9.setUserType("1001");
        arrangementHistory9.setUserIdentifier("127.0.0.1");
        arrangementHistory9.setDateModified(dateFactory.stringToXMLGregorianCalendar("2015-12-23T12:21:39Z", dateFormat));
        productArrangement.setApplicationType("10001");
        productArrangement.setRetryCount(7);
        productArrangement.setLastModifiedDate(dateFactory.stringToXMLGregorianCalendar("2015-12-24T20:17:55Z", dateFormat));
        productArrangement.setMarketingPreferenceByEmail(true);
        productArrangement.setMarketingPreferenceByPhone(true);
        productArrangement.setMarketingPreferenceByMail(true);
        return productArrangement;

    }

    public List<ReferralCode> createReferralCodeList(String code, String description) {

        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode(code);
        referralCode.setDescription(description);
        referralCodeList.add(referralCode);
        return referralCodeList;

    }


    public RetrieveProductConditionsRequest createRetrieveProductConditionsRequest(RequestHeader requestHeader, ProductArrangement productArrangement) {
        return prdRequestFactory.convert(productArrangement, requestHeader);

    }

    public RetrieveProductConditionsResponse createRetrieveProductConditionsResponse() {
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        Product product = new Product();
        InstructionDetails instructionDetails = new InstructionDetails();
        List<ExtSysProdIdentifier> extSysProdIdentifierList = new ArrayList<>();
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("01000");
        extSysProdIdentifier.setSystemCode("00107");
        extSysProdIdentifierList.add(extSysProdIdentifier);
        product.setProductIdentifier("01000");
        product.setInstructionDetails(instructionDetails);
        product.setProductType("LTB");
        retrieveProductConditionsResponse.getProduct().add(product);
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().addAll(extSysProdIdentifierList);

        return retrieveProductConditionsResponse;
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
            xcal.setTime(6, 4, 15, 0);
            xcal.setTimezone(0);
            return xcal;
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Transactional
    @Modifying()
    public Applications createApplication(ApplicationStatus applicationStatus, int year, int month, int day, ProductTypes productTypes, Brands brands) {
        Applications applications = new Applications(applicationTypes, productTypes, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        applications.setSortCode("773315");
        applications.setArrangementNumber("08676168");

        applications.setUserId("10.245.176.43");
        applications.setProductRequested("92");
        applications.setRetryCount(Long.valueOf(3));
        applications.setProductName("Classic Account");

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH.mm.ss");
        Date d = null;
        try {
            Calendar c = Calendar.getInstance();

            c.set(year, month, day, 0, 0);

            String date = (formatter.format(c.getTime()));


            applications.setDateModified(formatter.parse(date));
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
        ApplicationParameterValues applicationParameterValues14 = new ApplicationParameterValues(applicationParameters12, applications, "345", null);
        ApplicationParameterValues applicationParameterValues15 = new ApplicationParameterValues(applicationParameters13, applications, "false", null);
        ApplicationParameterValues applicationParameterValues16 = new ApplicationParameterValues(applicationParameters14, applications, "false", null);
        ApplicationParameterValues applicationParameterValues17 = new ApplicationParameterValues(applicationParameters15, applications, "100", null);

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
        applicationParameterValuesDao.save(applicationParameterValues15);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues16);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues17);
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
        applicationParameterValuesSet.add(applicationParameterValues14);
        applicationParameterValuesSet.add(applicationParameterValues15);
        applicationParameterValuesSet.add(applicationParameterValues16);
        applicationParameterValuesSet.add(applicationParameterValues17);

        applications.setApplicationParameterValues(applicationParameterValuesSet);

        ApplicationFeatures applicationFeatures1 = new ApplicationFeatures();
        applicationFeatures1.setApplications(applications);
        applicationFeatures1.setApplicationFeatureTypes(applicationFeatureTypes2);
        applicationFeatures1.setFeatureRequired("N");
        applicationFeaturesDao.save(applicationFeatures1);
        applicationFeaturesDao.findAll();

        ApplicationFeatures applicationFeatures2 = new ApplicationFeatures();
        applicationFeatures2.setApplications(applications);
        applicationFeatures2.setApplicationFeatureTypes(applicationFeatureTypes1);
        applicationFeatures2.setFeatureRequired("N");
        applicationFeatures2.setAmount(new BigDecimal(0));
        applicationFeaturesDao.save(applicationFeatures2);
        applicationFeaturesDao.findAll();

        ApplicationFeatures applicationFeatures3 = new ApplicationFeatures();
        applicationFeatures3.setApplications(applications);
        applicationFeatures3.setApplicationFeatureTypes(applicationFeatureTypes3);
        applicationFeaturesDao.save(applicationFeatures3);
        applicationFeaturesDao.findAll();

        ApplicationFeatures applicationFeatures4 = new ApplicationFeatures();
        applicationFeatures4.setApplications(applications);
        applicationFeatures4.setApplicationFeatureTypes(applicationFeatureTypes4);
        applicationFeatures4.setFeatureRequired("Y");
        applicationFeaturesDao.save(applicationFeatures4);
        applicationFeaturesDao.findAll();

        ApplicationFeatures applicationFeatures5 = new ApplicationFeatures();
        applicationFeatures5.setApplications(applications);
        applicationFeatures5.setApplicationFeatureTypes(applicationFeatureTypes9);
        applicationFeatures5.setAmount(new BigDecimal("100"));
        applicationFeatures5.setCcNumber("gsugfsbhgbs");
        applicationFeatures5.setExpiryDate("042017");
        applicationFeatures5.setCcNumberMasked("XXXXXXXXXXXX4535");
        applicationFeaturesDao.save(applicationFeatures5);
        applicationFeaturesDao.findAll();

        ApplicationFeatures applicationFeatures6 = new ApplicationFeatures();
        applicationFeatures6.setApplications(applications);
        applicationFeatures6.setApplicationFeatureTypes(applicationFeatureTypes9);
        applicationFeatures6.setAmount(new BigDecimal("500"));
        applicationFeatures6.setCcNumber("wruiwrtwyiwr");
        applicationFeatures6.setExpiryDate("052018");
        applicationFeatures6.setCcNumberMasked("XXXXXXXXXXXX3216");
        applicationFeaturesDao.save(applicationFeatures6);
        applicationFeaturesDao.findAll();

        Set<ApplicationFeatures> applicationFeaturesSet = new HashSet<>();
        applicationFeaturesSet.add(applicationFeatures1);
        applicationFeaturesSet.add(applicationFeatures2);
        applicationFeaturesSet.add(applicationFeatures3);
        applicationFeaturesSet.add(applicationFeatures4);
        applicationFeaturesSet.add(applicationFeatures5);
        applicationFeaturesSet.add(applicationFeatures6);
        applications.setApplicationFeatures(applicationFeaturesSet);

        //  Iterable it = applicationsDao.findAll();

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


        individualsDao.save(individuals);
        individualsDao.findAll();

        StreetAddresses streetAddresses = new StreetAddresses("addressLine1", "addressLine2", "addressLine3", "city", null, "country", "SE1-2EA", new Byte("5"), Short.valueOf("5"), "Y", "buildingName", "1", "1A", "street", "subBuildingName", "district", "addressLine4");
        streetAddressesDao.save(streetAddresses);
        streetAddressesDao.findAll();


        IndividualAddresses individualAddresses = new IndividualAddresses(streetAddresses, null, 1L);
        individualAddresses.setIndividuals(individuals);
        Set<IndividualAddresses> individualAddressesSet = new HashSet<>();
        individualAddressesSet.add(individualAddresses);

        individualAddressesDao.save(individualAddresses);
        individualAddressesDao.findAll();


        streetAddresses.setIndividualAddresseses(individualAddressesSet);
        individuals.setIndividualAddresses(individualAddressesSet);


        TelephoneAddresses telephoneAddresses = new TelephoneAddresses();
        telephoneAddresses.setTelephoneNumber(new BigDecimal("79123456789"));
        telephoneAddresses.setTelephoneAddressTypes(telephoneAddressTypes);
        telephoneAddresses.setIndividuals(individuals);
        Set<TelephoneAddresses> telephoneAddressesSet = new HashSet<>();
        telephoneAddressesSet.add(telephoneAddresses);
        telephoneAddressesDao.save(telephoneAddressesSet);
        telephoneAddressesDao.findAll();

        individuals.setTelephoneAddresses(telephoneAddressesSet);

        PartyApplications partyApplications = new PartyApplications();
        partyApplications.setApplicationPartyRoles(roles);
        partyApplications.setScoringStatus("1");
        partyApplications.setCustomerSegments("1");

        //setting encrypted credit card number
        partyApplications.setCcNumber("bsfjkdbghei");

        partyApplications.setKycStatus(new KycStatus("ACCEPT"));
        partyApplications.setLockId(1L);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);

        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();

        KycEvidenceDetails kycEvidenceDetails = new KycEvidenceDetails();
        kycEvidenceDetails.setPartyApplications(partyApplications);
        kycEvidenceDetails.setNiNumber("AB123456C");
        kycEvidenceDetailsDao.save(kycEvidenceDetails);

        Set<KycEvidenceDetails> kycEvidenceDetailsSet = new HashSet<>();
        kycEvidenceDetailsSet.add(kycEvidenceDetails);
        partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);


        Set<PartyApplications> partyApplicationsSet = new HashSet<>();
        partyApplicationsSet.add(partyApplications);

        applications.setPartyApplications(partyApplicationsSet);
        individuals.setPartyApplications(partyApplicationsSet);

        return applications;
    }

    @Transactional
    @Modifying()
    public Applications createApplicationWithBTAmountLessThanThreshold(ApplicationStatus applicationStatus, int year, int month, int day, ProductTypes productTypes, Brands brands) {
        Applications applications = new Applications(applicationTypes, productTypes, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        applications.setSortCode("773315");
        applications.setArrangementNumber("08676168");

        applications.setUserId("10.245.176.43");
        applications.setProductRequested("92");
        applications.setRetryCount(Long.valueOf(3));
        applications.setProductName("Classic Account");

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH.mm.ss");
        Date d = null;
        try {
            Calendar c = Calendar.getInstance();

            c.set(year, month, day, 0, 0);

            String date = (formatter.format(c.getTime()));


            applications.setDateModified(formatter.parse(date));
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
        ApplicationParameterValues applicationParameterValues14 = new ApplicationParameterValues(applicationParameters12, applications, "345", null);
        ApplicationParameterValues applicationParameterValues15 = new ApplicationParameterValues(applicationParameters13, applications, "false", null);
        ApplicationParameterValues applicationParameterValues16 = new ApplicationParameterValues(applicationParameters14, applications, "false", null);
        ApplicationParameterValues applicationParameterValues17 = new ApplicationParameterValues(applicationParameters15, applications, "100", null);

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
        applicationParameterValuesDao.save(applicationParameterValues15);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues16);
        applicationParameterValuesDao.findAll();
        applicationParameterValuesDao.save(applicationParameterValues17);
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
        applicationParameterValuesSet.add(applicationParameterValues14);
        applicationParameterValuesSet.add(applicationParameterValues15);
        applicationParameterValuesSet.add(applicationParameterValues16);
        applicationParameterValuesSet.add(applicationParameterValues17);

        applications.setApplicationParameterValues(applicationParameterValuesSet);

        ApplicationFeatures applicationFeatures1 = new ApplicationFeatures();
        applicationFeatures1.setApplications(applications);
        applicationFeatures1.setApplicationFeatureTypes(applicationFeatureTypes2);
        applicationFeatures1.setFeatureRequired("N");
        applicationFeaturesDao.save(applicationFeatures1);
        applicationFeaturesDao.findAll();

        ApplicationFeatures applicationFeatures2 = new ApplicationFeatures();
        applicationFeatures2.setApplications(applications);
        applicationFeatures2.setApplicationFeatureTypes(applicationFeatureTypes1);
        applicationFeatures2.setFeatureRequired("N");
        applicationFeatures2.setAmount(new BigDecimal(0));
        applicationFeaturesDao.save(applicationFeatures2);
        applicationFeaturesDao.findAll();

        ApplicationFeatures applicationFeatures3 = new ApplicationFeatures();
        applicationFeatures3.setApplications(applications);
        applicationFeatures3.setApplicationFeatureTypes(applicationFeatureTypes3);
        applicationFeaturesDao.save(applicationFeatures3);
        applicationFeaturesDao.findAll();

        ApplicationFeatures applicationFeatures4 = new ApplicationFeatures();
        applicationFeatures4.setApplications(applications);
        applicationFeatures4.setApplicationFeatureTypes(applicationFeatureTypes4);
        applicationFeatures4.setFeatureRequired("Y");
        applicationFeaturesDao.save(applicationFeatures4);
        applicationFeaturesDao.findAll();

        ApplicationFeatures applicationFeatures5 = new ApplicationFeatures();
        applicationFeatures5.setApplications(applications);
        applicationFeatures5.setApplicationFeatureTypes(applicationFeatureTypes9);
        applicationFeatures5.setAmount(new BigDecimal("-1"));
        applicationFeatures5.setCcNumber("gsugfsbhgbs");
        applicationFeatures5.setExpiryDate("042017");
        applicationFeatures5.setCcNumberMasked("XXXXXXXXXXXX4535");
        applicationFeaturesDao.save(applicationFeatures5);
        applicationFeaturesDao.findAll();

        ApplicationFeatures applicationFeatures6 = new ApplicationFeatures();
        applicationFeatures6.setApplications(applications);
        applicationFeatures6.setApplicationFeatureTypes(applicationFeatureTypes9);
        applicationFeatures6.setAmount(new BigDecimal("-1"));
        applicationFeatures6.setCcNumber("wruiwrtwyiwr");
        applicationFeatures6.setExpiryDate("052018");
        applicationFeatures6.setCcNumberMasked("XXXXXXXXXXXX3216");
        applicationFeaturesDao.save(applicationFeatures6);
        applicationFeaturesDao.findAll();

        Set<ApplicationFeatures> applicationFeaturesSet = new HashSet<>();
        applicationFeaturesSet.add(applicationFeatures1);
        applicationFeaturesSet.add(applicationFeatures2);
        applicationFeaturesSet.add(applicationFeatures3);
        applicationFeaturesSet.add(applicationFeatures4);
        applicationFeaturesSet.add(applicationFeatures5);
        applicationFeaturesSet.add(applicationFeatures6);
        applications.setApplicationFeatures(applicationFeaturesSet);

        //  Iterable it = applicationsDao.findAll();

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


        individualsDao.save(individuals);
        individualsDao.findAll();

        StreetAddresses streetAddresses = new StreetAddresses("addressLine1", "addressLine2", "addressLine3", "city", null, "country", "SE1-2EA", new Byte("5"), Short.valueOf("5"), "Y", "buildingName", "1", "1A", "street", "subBuildingName", "district", "addressLine4");
        streetAddressesDao.save(streetAddresses);
        streetAddressesDao.findAll();


        IndividualAddresses individualAddresses = new IndividualAddresses(streetAddresses, null, 1L);
        individualAddresses.setIndividuals(individuals);
        Set<IndividualAddresses> individualAddressesSet = new HashSet<>();
        individualAddressesSet.add(individualAddresses);

        individualAddressesDao.save(individualAddresses);
        individualAddressesDao.findAll();


        streetAddresses.setIndividualAddresseses(individualAddressesSet);
        individuals.setIndividualAddresses(individualAddressesSet);


        TelephoneAddresses telephoneAddresses = new TelephoneAddresses();
        telephoneAddresses.setTelephoneNumber(new BigDecimal("79123456789"));
        telephoneAddresses.setTelephoneAddressTypes(telephoneAddressTypes);
        telephoneAddresses.setIndividuals(individuals);
        Set<TelephoneAddresses> telephoneAddressesSet = new HashSet<>();
        telephoneAddressesSet.add(telephoneAddresses);
        telephoneAddressesDao.save(telephoneAddressesSet);
        telephoneAddressesDao.findAll();

        individuals.setTelephoneAddresses(telephoneAddressesSet);

        PartyApplications partyApplications = new PartyApplications();
        partyApplications.setApplicationPartyRoles(roles);
        partyApplications.setScoringStatus("1");
        partyApplications.setCustomerSegments("1");

        //setting encrypted credit card number
        partyApplications.setCcNumber("bsfjkdbghei");

        partyApplications.setKycStatus(new KycStatus("ACCEPT"));
        partyApplications.setLockId(1L);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);

        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();

        KycEvidenceDetails kycEvidenceDetails = new KycEvidenceDetails();
        kycEvidenceDetails.setPartyApplications(partyApplications);
        kycEvidenceDetails.setNiNumber("AB123456C");
        kycEvidenceDetailsDao.save(kycEvidenceDetails);

        Set<KycEvidenceDetails> kycEvidenceDetailsSet = new HashSet<>();
        kycEvidenceDetailsSet.add(kycEvidenceDetails);
        partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);


        Set<PartyApplications> partyApplicationsSet = new HashSet<>();
        partyApplicationsSet.add(partyApplications);

        applications.setPartyApplications(partyApplicationsSet);
        individuals.setPartyApplications(partyApplicationsSet);

        return applications;
    }

    public <T> Set<T> toSet(Iterable<T> collection) {
        HashSet<T> set = new HashSet<T>();
        for (T item : collection)
            set.add(item);
        return set;
    }

    public void createEventStoresData(String objectKey) {

        EventStores eventStores1 = new EventStores();
        eventStores1.setEventPriority((short) 1);
        eventStores1.setObjectKey(objectKey);
        eventStores1.setXid(null);
        eventStores1.setObjectName("Applications");
        eventStores1.setObjectFunction("Update");
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, Calendar.YEAR - 1);
        calendar1.set(Calendar.MONTH, 10);
        calendar1.set(Calendar.DATE, 01);
        Date date1 = calendar1.getTime();
        eventStores1.setEventTime(date1);
        eventStores1.setEventStatus((short) 0);
        eventStores1.setEventComment("inserted by trigger APP_STATUS_CCA_TRG");
        eventStores1.setLockId(0);
        eventStores1.setConnectorId("001");
        eventStoresDao.save(eventStores1);
        eventStoresDao.findAll();

    }

    public F263Resp createF263Resp() {
        F263Resp f263Resp = new F263Resp();
        F263Result f263Result = new F263Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode((byte) 0);
        f263Result.setResultCondition(resultCondition);
        f263Resp.setF263Result(f263Result);
        BIdentifiers bIdentifiers = new BIdentifiers();
        bIdentifiers.setSourceSystemCd("009");
        bIdentifiers.setRequestNo("STPL4540150430094623");
        bIdentifiers.setUpdateTs("30042015105314");
        bIdentifiers.setLoanAgreementNo("100001151620");
        f263Resp.setBIdentifiers(bIdentifiers);
        IllustrationDetails illustrationDetails = new IllustrationDetails();
        com.lloydsbanking.salsa.soap.pad.f263.objects.Product product = new com.lloydsbanking.salsa.soap.pad.f263.objects.Product();
        product.setProductId(717);
        product.setProductNm("HALIFAX CLARITY LOAN");
        product.setCurrencyCd("GBP");
        product.setInsuranceTakeUpIn("0");
        product.setInsProductNm("0");
        illustrationDetails.setProduct(product);
        LoanAm loanAm = new LoanAm();
        loanAm.setLoanCshAm("00000120000");
        loanAm.setLoanTtlAm("00000120000");
        loanAm.setInsPremAm("00000000000");
        loanAm.setLoanBalloonAm("00000000000");
        illustrationDetails.setLoanAm(loanAm);
        ArrangementFee arrangementFee = new ArrangementFee();
        arrangementFee.setArrgeFeeCshAm("00000000000");
        arrangementFee.setArrgeFeeInsAm("00000000000");
        arrangementFee.setArrgeFeeTtlAm("00000000000");
        illustrationDetails.setArrangementFee(arrangementFee);
        InterestPayable interestPayable = new InterestPayable();
        interestPayable.setIntPyblCshAm("00000000000");
        interestPayable.setIntPyblInsAm("00000000000");
        interestPayable.setIntPyblTtlAm("00000000000");
        illustrationDetails.setInterestPayable(interestPayable);
        TotalAmountPayable totalAmountPayable = new TotalAmountPayable();
        totalAmountPayable.setTtlPyblCshAm("00000171598");
        totalAmountPayable.setTtlPyblInsAm("00000000000");
        totalAmountPayable.setTtlPyblTtlAm("00000171598");
        illustrationDetails.setTotalAmountPayable(totalAmountPayable);
        MonthlyRepayment monthlyRepayment = new MonthlyRepayment();
        monthlyRepayment.setPriRpymtCshAm("00000005047");
        monthlyRepayment.setPriRpymtInsAm("00000000000");
        monthlyRepayment.setPriRpymtTtlAm("00000005047");
        monthlyRepayment.setSecRpymtCshAm("00000000000");
        monthlyRepayment.setSecRpymtInsAm("00000000000");
        monthlyRepayment.setSecRpymtTtlAm("00000000000");
        illustrationDetails.setMonthlyRepayment(monthlyRepayment);
        LoanTerm loanTerm = new LoanTerm();
        loanTerm.setDeferredMonthsNo((short) 0);
        loanTerm.setPrimaryLoanTermDr((short) 34);
        loanTerm.setSecondaryLoanTermDr((short) 0);
        illustrationDetails.setLoanTerm(loanTerm);
        InterestRates interestRates = new InterestRates();
        interestRates.setRecAPRRt("0298000");
        interestRates.setActualAPRRt("0298000");
        interestRates.setMinAPRRt("0000000");
        interestRates.setPriMthlyIntPcRt("0021968");
        interestRates.setPriAnnualIntPcRt("0263616");
        interestRates.setSecMthlyIntPcRt("0000000");
        interestRates.setSecAnnualIntPcRt("0000000");
        interestRates.setNetPresentValueAm("00000000000");
        interestRates.setFinalQteIn("1");
        illustrationDetails.setInterestRates(interestRates);
        Cashback cashback = new Cashback();
        cashback.setCashbackAm("00000000000");
        illustrationDetails.setCashback(cashback);
        illustrationDetails.setFeatures(new Features());
        CCAVariables ccaVariables = new CCAVariables();
        ccaVariables.setLetterChargeAm("00000000000");
        EarlySettParameters earlySettParameters = new EarlySettParameters();
        earlySettParameters.setDaysIntChgQy((short) 0);
        earlySettParameters.setMaxChgAm("00000000000");
        earlySettParameters.setAdministrationCg("00000000000");
        earlySettParameters.setStartTermExempQy((short) 0);
        earlySettParameters.setEndTermExempQy((short) 0);
        ccaVariables.setEarlySettParameters(earlySettParameters);
        illustrationDetails.setCCAVariables(ccaVariables);

        EarlySettlementEstimates earlySettlementEstimates = new EarlySettlementEstimates();
        earlySettlementEstimates.setEarlySettMthsQy((short) 0);
        earlySettlementEstimates.setEarlySettCshAm("00000171598");
        earlySettlementEstimates.setEarlySettInsAm("00000000000");
        earlySettlementEstimates.setEarlySettTtlAm("00000171598");
        earlySettlementEstimates.setEarlySettFeeAm("00000000000");
        earlySettlementEstimates.setEarlySettInsRbtAm("00000000000");

        EarlySettlementEstimates earlySettlementEstimates1 = new EarlySettlementEstimates();
        earlySettlementEstimates1.setEarlySettMthsQy((short) 0);
        earlySettlementEstimates1.setEarlySettCshAm("00000000000");
        earlySettlementEstimates1.setEarlySettInsAm("00000000000");
        earlySettlementEstimates1.setEarlySettTtlAm("00000000000");
        earlySettlementEstimates1.setEarlySettFeeAm("00000000000");
        earlySettlementEstimates1.setEarlySettInsRbtAm("00000000000");

        EarlySettlementEstimates earlySettlementEstimates3 = new EarlySettlementEstimates();
        earlySettlementEstimates1.setEarlySettMthsQy((short) 0);
        earlySettlementEstimates1.setEarlySettCshAm("00000000000");
        earlySettlementEstimates1.setEarlySettInsAm("00000000000");
        earlySettlementEstimates1.setEarlySettTtlAm("00000000000");
        earlySettlementEstimates1.setEarlySettFeeAm("00000000000");
        earlySettlementEstimates1.setEarlySettInsRbtAm("00000000000");

        illustrationDetails.getEarlySettlementEstimates().add(earlySettlementEstimates);
        illustrationDetails.getEarlySettlementEstimates().add(earlySettlementEstimates1);
        illustrationDetails.getEarlySettlementEstimates().add(earlySettlementEstimates3);

        ApplicationDetails applicationDetails = new ApplicationDetails();
        applicationDetails.setLoanApplnStatusCd(6);
        applicationDetails.setASMCreditScoreResultCd("1");
        DecisionReasons decisionReasons = new DecisionReasons();
        decisionReasons.setCSDecisionReasonTypeCd("601");
        decisionReasons.setCSDecisionReasonTypeNr("Accept.");
        applicationDetails.getDecisionReasons().add(decisionReasons);
        applicationDetails.setLoanPurposeCd("09");
        applicationDetails.setPurchaseItemAm("000000000");
        applicationDetails.setSellerLegalEntCd("HAL");
        applicationDetails.setClientVersionNo(0);
        applicationDetails.setMarketingCd("013");
        applicationDetails.setMailingRefTx("");
        applicationDetails.setCampaignCodeTx("");
        applicationDetails.setGuaranteedProductMailingCd("1");
        applicationDetails.setLastQteValidDt("22052015");
        applicationDetails.setLastCCAValidDt("30052015");
        applicationDetails.setCancellableCCAIn("0");
        applicationDetails.setLoanStartDt("30042015");
        applicationDetails.setPaymentMethodCd("003");
        applicationDetails.setPaymentSortCd("110323");
        applicationDetails.setPaymentAccNo("50007866");
        applicationDetails.setPaymentAcc1Nm("S GSBB1414");
        applicationDetails.setPaymentAcc2Nm("");
        applicationDetails.setRepaymentDayOfMonthNo("01");
        applicationDetails.setRepaymentMethodCd("005");
        applicationDetails.setRepaymentSortCd("110323");
        applicationDetails.setRepaymentAccNo("50007866");
        applicationDetails.setRepaymentAcc1Nm("S GSBB1414");
        applicationDetails.setRepaymentAcc2Nm("");
        applicationDetails.setCreditScoreId(404441619);
        applicationDetails.setAdditionalDataCd("z");
        applicationDetails.setShadowDecnLowerAm("000000000");
        applicationDetails.setShadowDecnUpperAm("000000000");
        applicationDetails.setSettlementQteDt("30042015");
        applicationDetails.setAddDaysInterestQy(0);
        applicationDetails.setInsQteDaysQy(0);
        RefinDetails refinDetails = new RefinDetails();
        refinDetails.setLoanRefinAm("00000000000");
        refinDetails.setRefinESFAm("00000000000");
        refinDetails.setRefinESFWaivedIn("0");
        applicationDetails.setRefinDetails(refinDetails);
        f263Resp.setIllustrationDetails(illustrationDetails);
        ApplicantDetails applicantDetails = new ApplicantDetails();
        applicantDetails.setContactPhoneNo("");
        Party party = new Party();
        party.setPartyId(Long.valueOf(299512626));
        party.setPersId("+00382350249");
        party.setCSExtPartyIdTx("11032350008507");
        party.setCSExtSysId((short) 4);
        party.setPartyTl("Mr");
        party.setFirstForeNm("SGBB1414");
        party.setSurname("GSBB1414");
        party.setBirthDt("01011973");
        party.setBirthDtConfirmedIn("0");
        party.setPrevAddrOutPostCd("");
        party.setPrevAddrInPostCd("");
        party.setPrevAddrLine1Tx("");
        party.setPrevAddrLine2Tx("");
        party.setPrevAddrLine3Tx("");
        party.setPrevAddrLine4Tx("");
        party.setPrevAddrTypeCd("000");
        party.setPrevAddressResidenceDr(0);
        party.setCurrAddressTypeCd("000");
        party.setCurrAddressResidenceDr(607);
        party.setBankersAssociationDr(0);
        party.setCreditCardHeldQy(0);
        party.setCurrentEmploymentDr(0);
        party.setEmploymentStatusCd("");
        party.setHomeTelephoneNumberIn("");
        party.setIncomeExpenditureIn("N");
        party.setLoanCmmtmtMnthlyAm("000000000");
        party.setLTSBChequeCardHeldIn("");
        party.setLTSBCreditCardHeldIn("");
        party.setMaritalStatusCd("000");
        party.setMnthlyAccmmnPaymntAm("000000000");
        party.setOutstandApplnMonthAm("00000000000");
        party.setPartyBusinessRltnspCd("1");
        party.setPartyDpndntChildrnQy(0);
        party.setPeriodNetIncomeAm("000000000");
        party.setPeriodNetIncomeFrqncyCd("");
        party.setPrevLoanAppQy(0);
        party.setResidentialStatusCd("");
        party.setStaffIn("");
        party.setStaffFileNo("");
        applicantDetails.getParty().add(party);
        f263Resp.setApplicationDetails(applicationDetails);
        f263Resp.setApplicantDetails(applicantDetails);
        return f263Resp;
    }

    public Q028Resp createQ028Response() {
        Q028Resp q028Resp = new Q028Resp();
        OutIdentifiers outIdentifiers = new OutIdentifiers();
        outIdentifiers.setSourceSystemCd("009");
        outIdentifiers.setRequestNo("STPL4540150430094623");
        outIdentifiers.setUpdateTs("30042015105314");
        outIdentifiers.setLoanAgreementNo("100001151620");
        q028Resp.setOutIdentifiers(outIdentifiers);
        com.lloydsbanking.salsa.soap.pad.q028.objects.IllustrationDetails illustrationDetails = new com.lloydsbanking.salsa.soap.pad.q028.objects.IllustrationDetails();
        com.lloydsbanking.salsa.soap.pad.q028.objects.Product product = new com.lloydsbanking.salsa.soap.pad.q028.objects.Product();
        product.setProductId(717);
        product.setProductNm("HALIFAX CLARITY LOAN");
        product.setCurrencyCd("GBP");
        product.setInsuranceTakeUpIn("0");
        product.setInsProductNm("0");
        illustrationDetails.setProduct(product);
        com.lloydsbanking.salsa.soap.pad.q028.objects.LoanAm loanAm = new com.lloydsbanking.salsa.soap.pad.q028.objects.LoanAm();
        loanAm.setLoanCshAm("00000120000");
        loanAm.setLoanTtlAm("00000120000");
        loanAm.setInsPremAm("00000000000");
        loanAm.setLoanBalloonAm("00000000000");
        illustrationDetails.setLoanAm(loanAm);
        com.lloydsbanking.salsa.soap.pad.q028.objects.ArrangementFee arrangementFee = new com.lloydsbanking.salsa.soap.pad.q028.objects.ArrangementFee();
        arrangementFee.setArrgeFeeCshAm("00000000000");
        arrangementFee.setArrgeFeeInsAm("00000000000");
        arrangementFee.setArrgeFeeTtlAm("00000000000");
        illustrationDetails.setArrangementFee(arrangementFee);
        com.lloydsbanking.salsa.soap.pad.q028.objects.InterestPayable interestPayable = new com.lloydsbanking.salsa.soap.pad.q028.objects.InterestPayable();
        interestPayable.setIntPyblCshAm("00000000000");
        interestPayable.setIntPyblInsAm("00000000000");
        interestPayable.setIntPyblTtlAm("00000000000");
        illustrationDetails.setInterestPayable(interestPayable);
        com.lloydsbanking.salsa.soap.pad.q028.objects.TotalAmountPayable totalAmountPayable = new com.lloydsbanking.salsa.soap.pad.q028.objects.TotalAmountPayable();
        totalAmountPayable.setTtlPyblCshAm("00000171598");
        totalAmountPayable.setTtlPyblInsAm("00000000000");
        totalAmountPayable.setTtlPyblTtlAm("00000171598");
        illustrationDetails.setTotalAmountPayable(totalAmountPayable);
        com.lloydsbanking.salsa.soap.pad.q028.objects.MonthlyRepayment monthlyRepayment = new com.lloydsbanking.salsa.soap.pad.q028.objects.MonthlyRepayment();
        monthlyRepayment.setPriRpymtCshAm("00000005047");
        monthlyRepayment.setPriRpymtInsAm("00000000000");
        monthlyRepayment.setPriRpymtTtlAm("00000005047");
        monthlyRepayment.setSecRpymtCshAm("00000000000");
        monthlyRepayment.setSecRpymtInsAm("00000000000");
        monthlyRepayment.setSecRpymtTtlAm("00000000000");
        illustrationDetails.setMonthlyRepayment(monthlyRepayment);
        com.lloydsbanking.salsa.soap.pad.q028.objects.LoanTerm loanTerm = new com.lloydsbanking.salsa.soap.pad.q028.objects.LoanTerm();
        loanTerm.setDeferredMonthsNo((short) 0);
        loanTerm.setPrimaryLoanTermDr((short) 34);
        loanTerm.setSecondaryLoanTermDr((short) 0);
        illustrationDetails.setLoanTerm(loanTerm);
        com.lloydsbanking.salsa.soap.pad.q028.objects.InterestRates interestRates = new com.lloydsbanking.salsa.soap.pad.q028.objects.InterestRates();
        interestRates.setRecAPRRt("0298000");
        interestRates.setActualAPRRt("0298000");
        interestRates.setMinAPRRt("0000000");
        interestRates.setPriMthlyIntPcRt("0021968");
        interestRates.setPriAnnualIntPcRt("0263616");
        interestRates.setSecMthlyIntPcRt("0000000");
        interestRates.setSecAnnualIntPcRt("0000000");
        interestRates.setNetPresentValueAm("00000000000");
        interestRates.setFinalQteIn("1");
        illustrationDetails.setInterestRates(interestRates);
        com.lloydsbanking.salsa.soap.pad.q028.objects.Cashback cashback = new com.lloydsbanking.salsa.soap.pad.q028.objects.Cashback();
        cashback.setCashbackAm("00000000000");
        illustrationDetails.setCashback(cashback);
        illustrationDetails.setFeatures(new com.lloydsbanking.salsa.soap.pad.q028.objects.Features());
        com.lloydsbanking.salsa.soap.pad.q028.objects.CCAVariables ccaVariables = new com.lloydsbanking.salsa.soap.pad.q028.objects.CCAVariables();
        ccaVariables.setLetterChargeAm("00000000000");
        com.lloydsbanking.salsa.soap.pad.q028.objects.EarlySettParameters earlySettParameters = new com.lloydsbanking.salsa.soap.pad.q028.objects.EarlySettParameters();
        earlySettParameters.setDaysIntChgQy((short) 0);
        earlySettParameters.setMaxChgAm("00000000000");
        earlySettParameters.setAdministrationCg("00000000000");
        earlySettParameters.setStartTermExempQy((short) 0);
        earlySettParameters.setEndTermExempQy((short) 0);
        ccaVariables.setEarlySettParameters(earlySettParameters);
        illustrationDetails.setCCAVariables(ccaVariables);

        com.lloydsbanking.salsa.soap.pad.q028.objects.EarlySettlementEstimates earlySettlementEstimates = new com.lloydsbanking.salsa.soap.pad.q028.objects.EarlySettlementEstimates();
        earlySettlementEstimates.setEarlySettMthsQy((short) 0);
        earlySettlementEstimates.setEarlySettCshAm("00000171598");
        earlySettlementEstimates.setEarlySettInsAm("00000000000");
        earlySettlementEstimates.setEarlySettTtlAm("00000171598");
        earlySettlementEstimates.setEarlySettFeeAm("00000000000");
        earlySettlementEstimates.setEarlySettInsRbtAm("00000000000");

        com.lloydsbanking.salsa.soap.pad.q028.objects.EarlySettlementEstimates earlySettlementEstimates1 = new com.lloydsbanking.salsa.soap.pad.q028.objects.EarlySettlementEstimates();
        earlySettlementEstimates1.setEarlySettMthsQy((short) 0);
        earlySettlementEstimates1.setEarlySettCshAm("00000000000");
        earlySettlementEstimates1.setEarlySettInsAm("00000000000");
        earlySettlementEstimates1.setEarlySettTtlAm("00000000000");
        earlySettlementEstimates1.setEarlySettFeeAm("00000000000");
        earlySettlementEstimates1.setEarlySettInsRbtAm("00000000000");

        com.lloydsbanking.salsa.soap.pad.q028.objects.EarlySettlementEstimates earlySettlementEstimates3 = new com.lloydsbanking.salsa.soap.pad.q028.objects.EarlySettlementEstimates();
        earlySettlementEstimates1.setEarlySettMthsQy((short) 0);
        earlySettlementEstimates1.setEarlySettCshAm("00000000000");
        earlySettlementEstimates1.setEarlySettInsAm("00000000000");
        earlySettlementEstimates1.setEarlySettTtlAm("00000000000");
        earlySettlementEstimates1.setEarlySettFeeAm("00000000000");
        earlySettlementEstimates1.setEarlySettInsRbtAm("00000000000");

        illustrationDetails.getEarlySettlementEstimates().add(earlySettlementEstimates);
        illustrationDetails.getEarlySettlementEstimates().add(earlySettlementEstimates1);
        illustrationDetails.getEarlySettlementEstimates().add(earlySettlementEstimates3);

        com.lloydsbanking.salsa.soap.pad.q028.objects.ApplicationDetails applicationDetails = new com.lloydsbanking.salsa.soap.pad.q028.objects.ApplicationDetails();
        applicationDetails.setLoanApplnStatusCd(6);
        applicationDetails.setASMCreditScoreResultCd("1");
        com.lloydsbanking.salsa.soap.pad.q028.objects.DecisionReasons decisionReasons = new com.lloydsbanking.salsa.soap.pad.q028.objects.DecisionReasons();
        decisionReasons.setCSDecisionReasonTypeCd("601");
        decisionReasons.setCSDecisionReasonTypeNr("Accept.");
        applicationDetails.getDecisionReasons().add(decisionReasons);
        applicationDetails.setLoanPurposeCd("09");
        applicationDetails.setPurchaseItemAm("000000000");
        applicationDetails.setSellerLegalEntCd("HAL");
        applicationDetails.setClientVersionNo(0);
        applicationDetails.setMarketingCd("013");
        applicationDetails.setMailingRefTx("");
        applicationDetails.setCampaignCodeTx("");
        applicationDetails.setGuaranteedProductMailingCd("1");
        applicationDetails.setLastQteValidDt("22052015");
        applicationDetails.setLastCCAValidDt("30052015");
        applicationDetails.setCancellableCCAIn("0");
        applicationDetails.setLoanStartDt("30042015");
        applicationDetails.setPaymentMethodCd("003");
        applicationDetails.setPaymentSortCd("110323");
        applicationDetails.setPaymentAccNo("50007866");
        applicationDetails.setPaymentAcc1Nm("S GSBB1414");
        applicationDetails.setPaymentAcc2Nm("");
        applicationDetails.setRepaymentDayOfMonthNo("01");
        applicationDetails.setRepaymentMethodCd("005");
        applicationDetails.setRepaymentSortCd("110323");
        applicationDetails.setRepaymentAccNo("50007866");
        applicationDetails.setRepaymentAcc1Nm("S GSBB1414");
        applicationDetails.setRepaymentAcc2Nm("");
        applicationDetails.setCreditScoreId(404441619);
        applicationDetails.setAdditionalDataCd("z");
        applicationDetails.setShadowDecnLowerAm("000000000");
        applicationDetails.setShadowDecnUpperAm("000000000");
        applicationDetails.setSettlementQteDt("30042015");
        applicationDetails.setAddDaysInterestQy(0);
        applicationDetails.setInsQteDaysQy(0);
        com.lloydsbanking.salsa.soap.pad.q028.objects.RefinDetails refinDetails = new com.lloydsbanking.salsa.soap.pad.q028.objects.RefinDetails();
        refinDetails.setLoanRefinAm("00000000000");
        refinDetails.setRefinESFAm("00000000000");
        refinDetails.setRefinESFWaivedIn("0");
        applicationDetails.setRefinDetails(refinDetails);
        q028Resp.setIllustrationDetails(illustrationDetails);
        com.lloydsbanking.salsa.soap.pad.q028.objects.ApplicantDetails applicantDetails = new com.lloydsbanking.salsa.soap.pad.q028.objects.ApplicantDetails();
        applicantDetails.setContactPhoneNo("");
        com.lloydsbanking.salsa.soap.pad.q028.objects.Party party = new com.lloydsbanking.salsa.soap.pad.q028.objects.Party();
        party.setPartyId(Long.valueOf(299512626));
        party.setPersId("+00382350249");
        party.setCSExtPartyIdTx("11032350008507");
        party.setCSExtSysId((short) 4);
        party.setPartyTl("Mr");
        party.setFirstForeNm("SGBB1414");
        party.setSurname("GSBB1414");
        party.setBirthDt("01011973");
        party.setBirthDtConfirmedIn("0");
        party.setPrevAddrOutPostCd("");
        party.setPrevAddrInPostCd("");
        party.setPrevAddrLine1Tx("");
        party.setPrevAddrLine2Tx("");
        party.setPrevAddrLine3Tx("");
        party.setPrevAddrLine4Tx("");
        party.setPrevAddrTypeCd("000");
        party.setPrevAddressResidenceDr(0);
        party.setCurrAddressTypeCd("000");
        party.setCurrAddressResidenceDr(607);
        party.setBankersAssociationDr(0);
        party.setCreditCardHeldQy(0);
        party.setCurrentEmploymentDr(0);
        party.setEmploymentStatusCd("");
        party.setHomeTelephoneNumberIn("");
        party.setIncomeExpenditureIn("N");
        party.setLoanCmmtmtMnthlyAm("000000000");
        party.setLTSBChequeCardHeldIn("");
        party.setLTSBCreditCardHeldIn("");
        party.setMaritalStatusCd("000");
        party.setMnthlyAccmmnPaymntAm("000000000");
        party.setOutstandApplnMonthAm("00000000000");
        party.setPartyBusinessRltnspCd("1");
        party.setPartyDpndntChildrnQy(0);
        party.setPeriodNetIncomeAm("000000000");
        party.setPeriodNetIncomeFrqncyCd("");
        party.setPrevLoanAppQy(0);
        party.setResidentialStatusCd("");
        party.setStaffIn("");
        party.setStaffFileNo("");
        applicantDetails.getParty().add(party);
        applicantDetails.getParty().add(party);
        q028Resp.setApplicationDetails(applicationDetails);
        q028Resp.setApplicantDetails(applicantDetails);
        return q028Resp;
    }

    public StB232ALoanCCASign createB232Request() {
        StB232ALoanCCASign b232Req = new StB232ALoanCCASign();
        b232Req.setBBatchRetry(false);
        b232Req.setCreditscoreid(295735883);
        b232Req.setLoanprodtxt("Halifax Staff Loan");
        b232Req.setCctmSessionId("0");
        b232Req.setQuoteType(new BigInteger("0"));
        StCCASignLetter stCCASignLetter = new StCCASignLetter();
        stCCASignLetter.setLoanmode("1");
        StAddress stAddress = new StAddress();
        stAddress.setPostcode("dummy");
        stCCASignLetter.getAstaddrssAffl().add(stAddress);
        b232Req.getAstccasignletter().add(stCCASignLetter);
        b232Req.setStloancore(createStLoanCore());
        StHeader stHeader = new StHeader();
        stHeader.setUseridAuthor("OX982035");
        StParty stParty = new StParty();
        stParty.setHost("T");
        stParty.setPartyid("+00434307833");
        stParty.setOcisid(new BigInteger("77090139679885"));
        stHeader.setChanid("IBL");
        stHeader.setChansecmode("PWD");
        stHeader.setStpartyObo(stParty);
        b232Req.setStheader(stHeader);
        return b232Req;
    }

    private St2LoanCoreDetails createStLoanCore() {
        St2LoanCoreDetails stLoanCore = new St2LoanCoreDetails();
        stLoanCore.setLoanno("100001154615");
        stLoanCore.setCreditscoreno("STPL3643150519081750");
        stLoanCore.setGuaranteedProductMailing("1");
        stLoanCore.setDaynoInstalment("10");
        StLoanHeader stLoanHeader = new StLoanHeader();
        stLoanHeader.setOcisid(new BigInteger("1780610833"));
        stLoanHeader.setPartyidPersId("+00136640351");
        stLoanHeader.setCustnum("11103150001506      ");
        stLoanCore.setStloanhdr(stLoanHeader);
        StLoanDetails1 stLoanDetails1 = new StLoanDetails1();
        stLoanDetails1.setLoanprodid(717);
        stLoanDetails1.setInsuranceind(0);
        stLoanDetails1.setCurrencycode("GBP");
        stLoanDetails1.setDependentscount("2");
        stLoanCore.setStloandets1(stLoanDetails1);
        St2LoanIllustration st2LoanIllustration = new St2LoanIllustration();
        st2LoanIllustration.setBIndicativeIllustration(false);
        st2LoanIllustration.setAmtLoan(new BigDecimal("18000"));
        st2LoanIllustration.setLoanterm(new BigInteger("34"));
        st2LoanIllustration.setLoantermDefer(new BigInteger("0"));
        st2LoanIllustration.setAmtMonthlyLoanPlusInterest(new BigDecimal("757.1"));
        st2LoanIllustration.setAmtMonthlyRepayment(new BigDecimal("757.1"));
        st2LoanIllustration.setAmtTotalRepayment(new BigDecimal("25741.4"));
        st2LoanIllustration.setAmtTotalInterest(new BigDecimal("7741.4"));
        St2LoanInsurance st2LoanInsurance = new St2LoanInsurance();
        st2LoanInsurance.setAmtArrangementFeeWithIns(new BigDecimal("0"));
        st2LoanInsurance.setAmtMonthlyInsurancePlusInterest(new BigDecimal("0"));
        st2LoanInsurance.setAmtArrangementFeeWithIns(new BigDecimal("0"));
        st2LoanInsurance.setInsuranceprod("0");
        st2LoanInsurance.setAmtTotalCostOfInsurance(new BigDecimal("0"));
        st2LoanIllustration.setStinsure(st2LoanInsurance);
        St2LoanRates st2LoanRates = new St2LoanRates();
        st2LoanRates.setIntrateRecommendedAPR("0298000");
        st2LoanRates.setIntrateActualAPR("0298000");
        st2LoanRates.setIntrateMonthly("0021972");
        st2LoanRates.setIntrateAnnual("0263664");
        st2LoanIllustration.setStrates(st2LoanRates);
        St2LoanEarly st2LoanEarly = new St2LoanEarly();
        st2LoanEarly.setLoanterm(new BigInteger("0"));
        st2LoanEarly.setAmtLoan(new BigDecimal("25741.4"));
        st2LoanEarly.setAmtIns(new BigDecimal("0"));
        st2LoanEarly.setAmtTotal(new BigDecimal("25741.4"));
        st2LoanEarly.setAmtFee(new BigDecimal("0"));
        st2LoanEarly.setAmtInsRebate(new BigDecimal("0"));
        st2LoanIllustration.getAstearly().add(st2LoanEarly);
        stLoanCore.setStillust(st2LoanIllustration);
        return stLoanCore;
    }

    public StB232BLoanCCASign createB232Response() {
        StB232BLoanCCASign response = new StB232BLoanCCASign();
        response.setBDeferred(false);
        StError stError = new StError();
        stError.setErrorno(0);
        response.setSterror(stError);
        response.setLoanno("000000000000");
        return response;
    }

    public F595Req createF595Req(String custId) {
        F595Req f595Req = new F595Req();
        f595Req.setPartyId(Long.valueOf(custId));
        f595Req.setMaxRepeatGroupQy(16);
        f595Req.setExtSysId(new Short("4"));
        f595Req.setKeyExtSysId(new Short("4"));
        f595Req.setExtPartyIdTx("");
        return f595Req;
    }

    public F595Resp createF595Resp() {
        F595Resp f595Resp = new F595Resp();
        PartyGroup partyGroup = new PartyGroup();
        com.lloydsbanking.salsa.soap.ocis.f595.objects.PersonalDetails personalDetails = new com.lloydsbanking.salsa.soap.ocis.f595.objects.PersonalDetails();
        personalDetails.setEmailAddressTx("abc@xyz.com");
        personalDetails.setSurname("Wyllie");
        personalDetails.setPartyTl("Mr");
        partyGroup.setPersonalDetails(personalDetails);

        AddressGroup addressGroup = new AddressGroup();
        addressGroup.setPostCd("SE19EQ");
        partyGroup.setAddressGroup(addressGroup);
        f595Resp.setPartyGroup(partyGroup);
        return f595Resp;
    }

    public boolean eventStoresData() {

        return eventStoresDao.findAll().iterator().hasNext();

    }


    public FinanceServiceArrangement createFinanceArrangementForCC() {
        AccessToken accessToken = new AccessToken();
        accessToken.setMemorableInfo("aaa");
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangementBuilder().arrangementType()
                .setAssProductAndIniThrough(createAssociatedProduct(), createInitiatedThrough())
                .setCodeTypeAndAffiliateId("HXAA01B001RPCC5", "10001", null)
                .primaryInvolvedParty(new InvolvedPartyBuilder().partyIdentifier("+00883965244")
                        .setPersonalDetails("a@a.com", createPostalAddressList(), createTelephoneNumber(), createIsPlayedBy())
                        .setCstSegmentAndSrcSystemId("4", "2")
                        .customerIdentifier("227323270")
                        .setUserTypeAndPartyRole("1001", "0001")
                        .internalUserIdentifier("BE205960")
                        .setAccessTokenAndOtherBankAssTnDr(accessToken, "2900")
                        .hasExistingCreditCard(false)
                        .build())
                .setMarketingPreference(null, true, true, true, true)
                .affiliateDetails(createAffiliateDetailsListCC())
                .conditions(createRuleConditionListCC())
                .balanceTransferAmount(createCurrencyAmount(new BigDecimal(0)))
                .build();
        return financeServiceArrangement;
    }

    public FinanceServiceArrangement createFinanceServiceArrangementForIssueInPayment() {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        BalanceTransfer balanceTransfer = new BalanceTransfer();
        balanceTransfer.setCreditCardNumber("123456");
        financeServiceArrangement.getBalanceTransfer().add(balanceTransfer);
        financeServiceArrangement.setCreditCardNumber("7859645");
        return financeServiceArrangement;
    }

    public DepositArrangement createDepositArrangementForSA() {
        DepositArrangement depositArrangement = new DepositArrangementBuilder().arrangementType()
                .setAssProductAndIniThrough(createAssociatedProductForSA(), createInitiatedThrough())
                .primaryInvolvedParty(new InvolvedPartyBuilder().partyIdentifier("AAGATEWAY")
                        .setPersonalDetails("a@a.com", createPostalAddressListSA(), createTelephoneNumberSA(), createIsPlayedBySA())
                        .setUserTypeAndPartyRole("1001", "0001")
                        .internalUserIdentifier("127.0.0.1")
                        .setCstSegmentAndSrcSystemId("3", "3")
                        .setAccessTokenAndOtherBankAssTnDr(null, "0000")
                        .build())
                .setMarketingPreference(false, false, false, true)
                .applicationTypeAndAccountPurpose("10001", "BENPA")
                .fundingSource("1")
                .conditions(createRuleConditionList())
                .build();
        return depositArrangement;
    }

    public Individual createIsPlayedBySA() {
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        Individual isPlayedBy = new IndividualBuilderSA().individualName(createIndividualNameSA())
                .residentialStatus("002")
                .personalDetails(datatypeFactory.newXMLGregorianCalendar("1930-03-03T06:40:56.046Z"), "GBR", "UK")
                .genderMaritalAndDependentDetails(new BigInteger("3"), "001", "001")
                .employmentStatusAndCurrentEmploymentDuration("006", "0707")
                .savingAndMonthlyIncome(createCurrencyAmountSA(), createCurrencyAmountSA())
                .monthlyLoanRepaymentAndMortgageAmount(createCurrencyAmountSA(), createCurrencyAmountSA())
                .otherMonthlyIncomeAmount(createCurrencyAmountSA())
                .build();
        return isPlayedBy;
    }

    private CurrencyAmount createCurrencyAmountSA() {
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal(300));
        return currencyAmount;
    }

    public List<TelephoneNumber> createTelephoneNumberSA() {
        TelephoneNumber telephoneNumber = new TelephoneNumberBuilder().countryPhoneCode("44").phoneNumber("7440696125").telephoneType("7").deviceType("Mobile").build();
        List<TelephoneNumber> telephoneNumberList = new ArrayList<TelephoneNumber>();
        telephoneNumberList.add(0, telephoneNumber);
        return telephoneNumberList;
    }


    public List<PostalAddress> createPostalAddressListSA() {
        PostalAddress postalAddress = new PostalAddressBuilder().durationOfStay("0707")
                .isBFPOAddressBuilder(false)
                .isPAFFormat(true)
                .statusCode("CURRENT")
                .structuredAddress(createStructuredAddressSA())
                .build();
        List<PostalAddress> postalAddressList = new ArrayList<>();
        postalAddressList.add(0, postalAddress);
        return postalAddressList;
    }

    private StructuredAddress createStructuredAddressSA() {
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setSubBuilding("subBuildingName");

        structuredAddress.setBuildingNumber("1");
        structuredAddress.setHouseNumber("23");
        structuredAddress.setPostCodeIn("9EQ");
        structuredAddress.setPostCodeOut("SE1");
        structuredAddress.setPostTown("London");
        structuredAddress.setPointSuffix("1E");
        List<String> addressLinePaf = new ArrayList<String>();
        addressLinePaf.add(0, "PARK STREET");
        structuredAddress.getAddressLinePAFData().addAll(addressLinePaf);
        return structuredAddress;
    }

    private Product createAssociatedProductForSA() {
        Product associatedProduct = new AssociatedProductBuilder().productIdentifier("20198")
                .externalSystemProductIdentifier(createExtSysProdIdSA())
                .instructionDetails(createInstructionDetailsSA())
                .productName("Account B")
                .productOptions(createProductOptionsSA())
                .build();
        associatedProduct.setBrandName("LTB");
        return associatedProduct;
    }


    private InstructionDetails createInstructionDetailsSA() {
        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("P_CLUB");
        return instructionDetails;
    }

    private List<ProductOptions> createProductOptionsSA() {
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("UP_SELL_DISPLAY_VALUE");
        productOptions.setOptionsValue("0");
        List<ProductOptions> productOptionsList = new ArrayList<ProductOptions>();
        productOptionsList.add(0, productOptions);
        return productOptionsList;
    }


    public List<TelephoneNumber> createTelephoneNumber() {
        TelephoneNumber telephoneNumber = new TelephoneNumberBuilder().phoneNumber("79123456789").telephoneType("7").deviceType("Mobile").build();
        List<TelephoneNumber> telephoneNumberList = new ArrayList<TelephoneNumber>();
        telephoneNumberList.add(0, telephoneNumber);
        return telephoneNumberList;
    }


    public List<PostalAddress> createPostalAddressList() {
        PostalAddress postalAddress = new PostalAddressBuilder().durationOfStay("0505")
                .isPAFFormat(true)
                .statusCode("CURRENT")
                .structuredAddress(createStructuredAddress())
                .build();


        //*************  not in request
        postalAddress.setIsBFPOAddress(false);


        List<PostalAddress> postalAddressList = new ArrayList<>();
        postalAddressList.add(0, postalAddress);
        return postalAddressList;
    }

    private StructuredAddress createStructuredAddress() {
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("1");
        structuredAddress.setSubBuilding("subBuildingName");
        structuredAddress.setBuilding("buildingName");
        structuredAddress.setHouseNumber("23");
        structuredAddress.setPostCodeIn("2EA");
        structuredAddress.setPostCodeOut("SE1");
        structuredAddress.setPostTown("city");
        structuredAddress.setPointSuffix("1E");
        structuredAddress.setDistrict("district");
        List<String> addressLinePaf = new ArrayList<String>();
        addressLinePaf.add(0, "addressLine1");

        structuredAddress.getAddressLinePAFData().addAll(addressLinePaf);
        return structuredAddress;
    }

    private UnstructuredAddress createUnstructuredAddressCC() {
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1("96 EDGEHILL ROAD");
        unstructuredAddress.setAddressLine2("CHISLEHURST");
        unstructuredAddress.setAddressLine3("KENT");
        unstructuredAddress.setAddressLine6("United Kingdom");
        unstructuredAddress.setPostCode("BR7  6LB");


        //*************  not in request
        unstructuredAddress.setPointSuffix("1E");

        return unstructuredAddress;
    }

    public CurrencyAmount createCurrencyAmount(BigDecimal amount) {
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(amount);
        return currencyAmount;
    }

    public F424Resp createF424Response(String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        F424Resp f424Resp = new F424Resp();
        f424Resp.setF424Result(new F424Result());
        f424Resp.getF424Result().setResultCondition(new com.lloydstsb.schema.infrastructure.soap.ResultCondition());
        f424Resp.getF424Result().getResultCondition().setReasonCode(0);
        f424Resp.getF424Result().getResultCondition().setReasonText("abc");
        f424Resp.getF424Result().getResultCondition().setSeverityCode((byte) 0);
        f424Resp.setASMCreditScoreResultCd(asmCreditScoreResultCd);
        f424Resp.setCreditScoreId(12345);
        for (ReferralCode referralCode : referralCodeList) {
            com.lloydsbanking.salsa.soap.asm.f424.objects.DecisionDetails decisionDetails = new com.lloydsbanking.salsa.soap.asm.f424.objects.DecisionDetails();
            decisionDetails.setCSDecisionReasonTypeCd(referralCode.getCode());
            decisionDetails.setCSDecisionReasonTypeNr(referralCode.getDescription());
            f424Resp.getDecisionDetails().add(decisionDetails);
        }

        ProductOffered productOffered1 = new ProductOffered();
        productOffered1.setProductOfferedAm("10000");
        productOffered1.setPriceTierCd("200");
        productOffered1.setProductOfferedCd("300");
        f424Resp.getProductOffered().add(productOffered1);

        ProductOffered productOffered2 = new ProductOffered();
        productOffered2.setProductOfferedAm("20000");
        productOffered2.setPriceTierCd("400");
        productOffered2.setProductOfferedCd("500");
        f424Resp.getProductOffered().add(productOffered2);

        ProductOffered productOffered3 = new ProductOffered();
        productOffered3.setProductOfferedAm("30000");
        productOffered3.setPriceTierCd("600");
        productOffered3.setProductOfferedCd("700");
        f424Resp.getProductOffered().add(productOffered3);
        return f424Resp;
    }

    public F424Resp createF424ResponseForError(String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        F424Resp f424Resp = new F424Resp();
        f424Resp.setF424Result(new F424Result());
        f424Resp.getF424Result().setResultCondition(new com.lloydstsb.schema.infrastructure.soap.ResultCondition());
        f424Resp.getF424Result().getResultCondition().setReasonCode(159242);
        f424Resp.getF424Result().getResultCondition().setReasonText("NET_INCOME_AMOUNT_MUST_BE_NUMERIC");
        f424Resp.getF424Result().getResultCondition().setSeverityCode((byte) 0);
        f424Resp.setASMCreditScoreResultCd(asmCreditScoreResultCd);
        f424Resp.setCreditScoreId(12345);
        for (ReferralCode referralCode : referralCodeList) {
            com.lloydsbanking.salsa.soap.asm.f424.objects.DecisionDetails decisionDetails = new com.lloydsbanking.salsa.soap.asm.f424.objects.DecisionDetails();
            decisionDetails.setCSDecisionReasonTypeCd(referralCode.getCode());
            decisionDetails.setCSDecisionReasonTypeNr(referralCode.getDescription());
            f424Resp.getDecisionDetails().add(decisionDetails);
        }

        ProductOffered productOffered1 = new ProductOffered();
        productOffered1.setProductOfferedAm("10000");
        productOffered1.setPriceTierCd("200");
        productOffered1.setProductOfferedCd("300");
        f424Resp.getProductOffered().add(productOffered1);

        ProductOffered productOffered2 = new ProductOffered();
        productOffered2.setProductOfferedAm("20000");
        productOffered2.setPriceTierCd("400");
        productOffered2.setProductOfferedCd("500");
        f424Resp.getProductOffered().add(productOffered2);

        ProductOffered productOffered3 = new ProductOffered();
        productOffered3.setProductOfferedAm("30000");
        productOffered3.setPriceTierCd("600");
        productOffered3.setProductOfferedCd("700");
        f424Resp.getProductOffered().add(productOffered3);
        return f424Resp;
    }


    public F204Resp createF204Response(String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        F204Resp f204Resp = new F204Resp();
        f204Resp.setF204Result(new F204Result());
        f204Resp.getF204Result().setResultCondition(new com.lloydstsb.schema.infrastructure.soap.ResultCondition());
        f204Resp.getF204Result().getResultCondition().setSeverityCode((byte) 0);
        f204Resp.setAdditionalDataIn(0);
        f204Resp.setASMCreditScoreResultCd(asmCreditScoreResultCd);
        f204Resp.setCreditScoreId(805051165);
        f204Resp.getDecisionDetails().add(new DecisionDetails());
        for (ReferralCode referralCode : referralCodeList) {
            f204Resp.getDecisionDetails().get(0).setCSDecisionReasonTypeCd(referralCode.getCode());
            f204Resp.getDecisionDetails().get(0).setCSDecisionReasonTypeNr(referralCode.getDescription());
        }
        f204Resp.setAddressDetailIn("N");
        f204Resp.setCSAddressDetails(new CSAddressDetails());
        f204Resp.getCSAddressDetails().setApplicantNo((short) 0);
        f204Resp.getCSAddressDetails().setApplctAddrssSequncNo((short) 0);
        f204Resp.getCSAddressDetails().setBureauAddressMoreIn("");
        f204Resp.getCSAddressDetails().setCreditReferenceAgencyCd("");
        f204Resp.getCSAddressDetails().setBureauReferenceId("");
        f204Resp.getCSAddressDetails().setBureauAddressResultCd("");
        f204Resp.getF204Result().getResultCondition().setReasonCode(0);
        return f204Resp;
    }

    public F204Resp createF204ResponseWithError(String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        F204Resp f204Resp = new F204Resp();
        f204Resp.setF204Result(new F204Result());
        f204Resp.getF204Result().setResultCondition(new com.lloydstsb.schema.infrastructure.soap.ResultCondition());
        f204Resp.getF204Result().getResultCondition().setReasonCode(159242);
        f204Resp.getF204Result().getResultCondition().setSeverityCode((byte) 0);
        f204Resp.setAdditionalDataIn(0);
        f204Resp.setASMCreditScoreResultCd(asmCreditScoreResultCd);
        f204Resp.setCreditScoreId(805051165);
        f204Resp.getDecisionDetails().add(new DecisionDetails());
        for (ReferralCode referralCode : referralCodeList) {
            f204Resp.getDecisionDetails().get(0).setCSDecisionReasonTypeCd(referralCode.getCode());
            f204Resp.getDecisionDetails().get(0).setCSDecisionReasonTypeNr(referralCode.getDescription());
        }
        f204Resp.setAddressDetailIn("N");
        f204Resp.setCSAddressDetails(new CSAddressDetails());
        f204Resp.getCSAddressDetails().setApplicantNo((short) 0);
        f204Resp.getCSAddressDetails().setApplctAddrssSequncNo((short) 0);
        f204Resp.getCSAddressDetails().setBureauAddressMoreIn("");
        f204Resp.getCSAddressDetails().setCreditReferenceAgencyCd("");
        f204Resp.getCSAddressDetails().setBureauReferenceId("");
        f204Resp.getCSAddressDetails().setBureauAddressResultCd("");
        f204Resp.getF204Result().getResultCondition().setReasonCode(0);
        return f204Resp;
    }


    private List<RuleCondition> createRuleConditionListCC() {
        RuleCondition condition = new RuleCondition();
        condition.setName("CREDIT_CARD_DUPLICATE_SWITCH");
        condition.setResult("0");
        List<RuleCondition> conditionList = new ArrayList<>();
        conditionList.add(0, condition);
        return conditionList;
    }

    private List<RuleCondition> createRuleConditionList() {
        RuleCondition condition = new RuleCondition();
        condition.setName("EIDV_REFERRAL_DISABLED_SWITCH");
        condition.setResult("DISABLED");
        List<RuleCondition> conditionList = new ArrayList<RuleCondition>();
        conditionList.add(0, condition);
        return conditionList;
    }

    private List<AffiliateDetails> createAffiliateDetailsListCC() {
        AffiliateDetails affiliateDetails = new AffiliateDetails();
        affiliateDetails.setAffiliateIdentifier(null);
        List<AffiliateDetails> affiliateDetailsList = new ArrayList<AffiliateDetails>();
        affiliateDetailsList.add(0, affiliateDetails);
        return affiliateDetailsList;
    }

    public Individual createIsPlayedBy() {
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        Employer currentEmployer = new Employer();
        Individual isPlayedBy = new IndividualBuilderCC().individualName(createIndividualName())
                .residentialStatus("001")
                .birthDate(datatypeFactory.newXMLGregorianCalendar("1992-07-12T06:40:56.046Z"))
                .nationality("GBR")
                .maritalStatus("001")
                .gender("001")
                .setEmploymentDetails("006", "0", createCurrencyAmount(new BigDecimal(4800)), "1")
                .currentEmployer(currentEmployer)
                .build();
        return isPlayedBy;
    }

    public List<IndividualName> createIndividualName() {
        IndividualName individualName = new IndividualName();
        List<IndividualName> individualNameList = new ArrayList<IndividualName>();
        individualName.setFirstName("meera");
        individualName.setLastName("radha");
        individualName.setPrefixTitle("Mr");
        individualNameList.add(0, individualName);
        return individualNameList;
    }

    public List<IndividualName> createIndividualNameSA() {
        IndividualName individualName = new IndividualName();
        List<IndividualName> individualNameList = new ArrayList<IndividualName>();
        individualName.setFirstName("firstname");
        individualName.setLastName("lastname");
        individualName.setPrefixTitle("Mr");
        List<String> middleNameList = new ArrayList<String>();
        middleNameList.add(0, "middleName");
        individualName.getMiddleNames().addAll(middleNameList);
        individualNameList.add(0, individualName);
        return individualNameList;
    }

    private Channel createInitiatedThrough() {
        Channel initiatedThrough = new Channel();
        initiatedThrough.setChannelCode("004");
        initiatedThrough.setSubChannelCode("003");
        return initiatedThrough;
    }

    private Product createAssociatedProduct() {
        Product associatedProduct = new AssociatedProductBuilder().productIdentifier("20042")
                .externalSystemProductIdentifier(createExtSysProdId())
                .productOffer(createProductOfferCC())
                .productName("Clarity Credit Card")
                .productPropositionIdentifier("42")
                .build();
        return associatedProduct;
    }

    public List<ExtSysProdIdentifier> createExtSysProdId() {
        ExtSysProdIdentifier extSysProdIdentifierASM = new ExtSysProdIdentifier();
        extSysProdIdentifierASM.setProductIdentifier("1000");
        extSysProdIdentifierASM.setSystemCode("00107");
        ExtSysProdIdentifier extSysProdIdentifierCAAS = new ExtSysProdIdentifier();
        extSysProdIdentifierCAAS.setProductIdentifier("201");
        extSysProdIdentifierCAAS.setSystemCode("10107");
        ExtSysProdIdentifier extSysProdIdentifierCAAAS = new ExtSysProdIdentifier();
        extSysProdIdentifierCAAS.setProductIdentifier("201");
        extSysProdIdentifierCAAS.setSystemCode("10107");
        List<ExtSysProdIdentifier> extSysProdIdentifierList = new ArrayList<ExtSysProdIdentifier>();
        extSysProdIdentifierList.add(0, extSysProdIdentifierASM);
        extSysProdIdentifierList.add(1, extSysProdIdentifierCAAS);
        extSysProdIdentifierList.add(1, extSysProdIdentifierCAAAS);
        return extSysProdIdentifierList;
    }

    public List<ExtSysProdIdentifier> createExtSysProdIdSA() {
        ExtSysProdIdentifier extSysProdIdentifierASM = new ExtSysProdIdentifier();
        extSysProdIdentifierASM.setProductIdentifier("1000");
        extSysProdIdentifierASM.setSystemCode("00107");
        ExtSysProdIdentifier extSysProdIdentifierCAAS = new ExtSysProdIdentifier();
        extSysProdIdentifierCAAS.setProductIdentifier("201");
        extSysProdIdentifierCAAS.setSystemCode("10107");
        List<ExtSysProdIdentifier> extSysProdIdentifierList = new ArrayList<ExtSysProdIdentifier>();
        extSysProdIdentifierList.add(0, extSysProdIdentifierASM);
        extSysProdIdentifierList.add(1, extSysProdIdentifierCAAS);
        return extSysProdIdentifierList;
    }


    private List<ProductOffer> createProductOfferCC() {
        ProductOffer productOffer = new ProductOffer();
        productOffer.setProdOfferIdentifier("1000086");
        productOffer.setOfferType("2004");
        List<ProductOffer> productOfferList = new ArrayList<>();
        productOfferList.add(0, productOffer);
        return productOfferList;
    }

    public RetrieveProductConditionsRequest rpcRequestForOffer(RequestHeader requestHeader) {
        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();

        rpcRequest.setHeader(requestHeader);
        rpcRequest.getHeader().setContactPointId("0000777505");

        Product product = new Product();


        rpcRequest.setProduct(product);


        ProductFamily productFamily = new ProductFamily();
        Product product1 = new Product();
        ProductOffer productOffer1 = new ProductOffer();
        PricePoint pricePoint = new PricePoint();
        pricePoint.setSystemCode("00107");
        pricePoint.setExternalSystemIdentifier("200");
        productOffer1.getPricepoint().add(pricePoint);
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount((BigDecimal.valueOf(100.00).setScale(2)));
        productOffer1.setOfferAmount(currencyAmount);
        product1.getProductoffer().add(productOffer1);
        productFamily.getProductFamily().add(product1);
        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("300");
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);
        rpcRequest.getProductFamily().add(productFamily);

        ProductFamily productFamily1 = new ProductFamily();
        Product product2 = new Product();
        ProductOffer productOffer2 = new ProductOffer();
        PricePoint pricePoint1 = new PricePoint();
        pricePoint1.setSystemCode("00107");
        pricePoint1.setExternalSystemIdentifier("400");
        productOffer2.getPricepoint().add(pricePoint1);
        CurrencyAmount currencyAmount1 = new CurrencyAmount();
        currencyAmount1.setAmount((BigDecimal.valueOf(200.00).setScale(2)));
        productOffer2.setOfferAmount(currencyAmount1);
        product2.getProductoffer().add(productOffer2);
        productFamily1.getProductFamily().add(product2);
        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier1 = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier1.setProductFamilyIdentifier("500");
        productFamily1.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier1);
        rpcRequest.getProductFamily().add(productFamily1);

        ProductFamily productFamily2 = new ProductFamily();
        Product product3 = new Product();
        ProductOffer productOffer3 = new ProductOffer();
        PricePoint pricePoint2 = new PricePoint();
        pricePoint2.setSystemCode("00107");
        pricePoint2.setExternalSystemIdentifier("600");
        productOffer3.getPricepoint().add(pricePoint2);
        CurrencyAmount currencyAmount2 = new CurrencyAmount();
        currencyAmount2.setAmount((BigDecimal.valueOf(300.00).setScale(2)));
        productOffer3.setOfferAmount(currencyAmount2);
        product3.getProductoffer().add(productOffer3);
        productFamily2.getProductFamily().add(product3);
        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier2 = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier2.setProductFamilyIdentifier("700");
        productFamily2.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier2);
        rpcRequest.getProductFamily().add(productFamily2);

        return rpcRequest;
    }

    public RetrieveProductConditionsResponse rpcResponseForOffer(String productMatched) {
        RetrieveProductConditionsResponse rpcResponse = new RetrieveProductConditionsResponse();
        rpcResponse.setIsGauranteedOfferAvailable(true);
        ResponseHeader header = new ResponseHeader();
        header.setArrangementId("1");
        rpcResponse.setHeader(header);

        Product product1 = new Product();
        product1.setProductIdentifier("20051");
        product1.setProductType("3");
        product1.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        ProductOffer productOffer1 = new ProductOffer();
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(BigDecimal.valueOf(100));
        productOffer1.setOfferAmount(currencyAmount);
        productOffer1.setProdOfferIdentifier("242");
        product1.getProductoffer().add(productOffer1);
        product1.setStatusCode("IsAccepted");
        rpcResponse.getProduct().add(product1);


        Product product2 = new Product();
        product2.setProductIdentifier("20052");
        product2.setProductType("3");
        product2.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        ProductOffer productOffer2 = new ProductOffer();
        CurrencyAmount currencyAmount2 = new CurrencyAmount();
        currencyAmount2.setAmount(BigDecimal.valueOf(200));
        productOffer2.setOfferAmount(currencyAmount);
        if (productMatched.equalsIgnoreCase("Yes")) {
            productOffer2.setProdOfferIdentifier("242");
        } else {
            productOffer2.setProdOfferIdentifier("222");
        }
        product2.getProductoffer().add(productOffer2);
        rpcResponse.getProduct().add(product2);
        return rpcResponse;
    }

    public RetrieveProductConditionsResponse rpcResponseForOffer() {
        RetrieveProductConditionsResponse rpcResponse = new RetrieveProductConditionsResponse();
        rpcResponse.setIsGauranteedOfferAvailable(true);
        ResponseHeader header = new ResponseHeader();
        header.setArrangementId("1");
        rpcResponse.setHeader(header);

        Product product1 = new Product();
        product1.setProductIdentifier("20051");
        product1.setProductType("3");
        product1.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        ProductOffer productOffer1 = new ProductOffer();
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(BigDecimal.valueOf(100));
        productOffer1.setOfferAmount(currencyAmount);
        productOffer1.setProdOfferIdentifier("242");
        product1.getProductoffer().add(productOffer1);
        product1.setStatusCode("IsAccepted");
        rpcResponse.getProduct().add(product1);


        Product product2 = new Product();
        product2.setProductIdentifier("20052");
        product2.setProductType("3");
        product2.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        ProductOffer productOffer2 = new ProductOffer();
        CurrencyAmount currencyAmount2 = new CurrencyAmount();
        currencyAmount2.setAmount(BigDecimal.valueOf(200));
        productOffer2.setOfferAmount(currencyAmount);
        productOffer2.setProdOfferIdentifier("222");
        product2.getProductoffer().add(productOffer2);
        rpcResponse.getProduct().add(product2);

        return rpcResponse;
    }

    public ProductArrangement createProductArrangementForLaon() throws DatatypeConfigurationException {
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setPrimaryInvolvedParty(createProductArrangement().getPrimaryInvolvedParty());
        productArrangement.setArrangementType("SA");
        return productArrangement;
    }

    public StB233ALoanIllustrate createB233Request() {
        StB233ALoanIllustrate b233ALoanIllustrate = new StB233ALoanIllustrate();
        b233ALoanIllustrate.setLoantermProdMax(new BigInteger("84"));
        b233ALoanIllustrate.setBNewApplication(false);
        b233ALoanIllustrate.setLoanpurpose("Max");
        b233ALoanIllustrate.setBAvoidWriteToPAD(true);
        return b233ALoanIllustrate;
    }

    public F205Resp createF205Response2(String asmCreditScoreResultCd, List<ReferralCode> referralCodeList, int caseNo) {
        F205Resp response = new F205Resp();
        response.setF205Result(new F205Result());
        response.getF205Result().setResultCondition(new ResultCondition());
        response.getF205Result().getResultCondition().setReasonCode(0);
        response.getF205Result().getResultCondition().setSeverityCode((byte) 0);
        response.getF205Result().getResultCondition().setReasonText("abc");
        response.setCreditScoreId(805051164);

        response.getDecisionDetails().add(new com.lloydsbanking.salsa.soap.asm.f205.objects.DecisionDetails());
        for (ReferralCode referralCode : referralCodeList) {
            response.getDecisionDetails().get(0).setCSDecisionReasonTypeCd(referralCode.getCode());
            response.getDecisionDetails().get(0).setCSDecisionReasonTypeNr(referralCode.getDescription());
        }

        response.setASMCreditScoreResultCd(asmCreditScoreResultCd);

        ProductsOffered productsOffered = new ProductsOffered();
        productsOffered.setCSProductsOfferedCd("1");
        response.getProductsOffered().add(productsOffered);


        ProductsOffered productsOffered1 = new ProductsOffered();
        productsOffered1.setCSProductsOfferedCd("2");
        response.getProductsOffered().add(productsOffered1);

        switch (caseNo) {
            case 1:
                response.getFacilitiesOffered().add(facilitiesOfferedForCheckBook());
                break;
            case 2:
                response.getFacilitiesOffered().add(facilitiesOfferedForOverdraft());
                break;
            case 3:
                response.getFacilitiesOffered().add(facilitiesOfferedForCreditCard());
                break;
            case 4:
                response.getFacilitiesOffered().add(facilitiesOfferedForCheckBook());
                response.getFacilitiesOffered().add(facilitiesOfferedForOverdraft());
                break;
            case 5:
                response.getFacilitiesOffered().add(facilitiesOfferedForCheckBook());
                response.getFacilitiesOffered().add(facilitiesOfferedForCreditCard());
                break;
            case 6:
                response.getFacilitiesOffered().add(facilitiesOfferedForCreditCard());
                response.getFacilitiesOffered().add(facilitiesOfferedForOverdraft());
                break;
            case 7:
                response.getFacilitiesOffered().add(facilitiesOfferedForCheckBook());
                response.getFacilitiesOffered().add(facilitiesOfferedForOverdraft());
                response.getFacilitiesOffered().add(facilitiesOfferedForCreditCard());
                break;
            case 8:
                break;
            default:
                response.getFacilitiesOffered().add(facilitiesOfferedForDebitCard());
                break;

        }

        return response;
    }

    private FacilitiesOffered facilitiesOfferedForCheckBook() {
        FacilitiesOffered facilitiesOffered = new FacilitiesOffered();
        facilitiesOffered.setCSFacilityOfferedAm("100");
        facilitiesOffered.setCSFacilityOfferedCd("101");
        return facilitiesOffered;
    }

    private FacilitiesOffered facilitiesOfferedForOverdraft() {
        FacilitiesOffered facilitiesOffered = new FacilitiesOffered();
        facilitiesOffered.setCSFacilityOfferedAm("200");
        facilitiesOffered.setCSFacilityOfferedCd("102");
        return facilitiesOffered;
    }

    private FacilitiesOffered facilitiesOfferedForCreditCard() {
        FacilitiesOffered facilitiesOffered = new FacilitiesOffered();
        facilitiesOffered.setCSFacilityOfferedAm("300");
        facilitiesOffered.setCSFacilityOfferedCd("103");
        return facilitiesOffered;
    }


    private FacilitiesOffered facilitiesOfferedForDebitCard() {
        FacilitiesOffered facilitiesOffered = new FacilitiesOffered();
        facilitiesOffered.setCSFacilityOfferedAm("400");
        facilitiesOffered.setCSFacilityOfferedCd("50");
        return facilitiesOffered;
    }

    public RetrieveProductConditionsRequest rpcRequestForF205(RequestHeader header) {

        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();

        retrieveProductConditionsRequest.setHeader(header);


        ProductFamily productFamily = new ProductFamily();
        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("1");
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);


        ProductFamily productFamily1 = new ProductFamily();
        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier1 = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier1.setProductFamilyIdentifier("2");
        productFamily1.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier1);


        retrieveProductConditionsRequest.getProductFamily().add(productFamily);
        retrieveProductConditionsRequest.getProductFamily().add(productFamily1);

        return retrieveProductConditionsRequest;
    }

    public RetrieveProductConditionsResponse rpcResponseForF205() {
        RetrieveProductConditionsResponse offeredProducts = new RetrieveProductConditionsResponse();
        Product product1 = new Product();
        product1.setProductIdentifier("20198");

        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsType("PrdPriority");
        productOptions1.setOptionsValue("0");

        product1.getProductoptions().add(productOptions1);

        ProductOptions productOptions2 = new ProductOptions();
        productOptions2.setOptionsType("Priority");
        productOptions2.setOptionsValue("1");

        product1.getProductoptions().add(productOptions2);


        //......
        ExtSysProdIdentifier identifier1 = new ExtSysProdIdentifier();
        identifier1.setSystemCode("code1");
        identifier1.setProductIdentifier("id1");
        product1.getExternalSystemProductIdentifier().add(identifier1);

        ExtSysProdIdentifier identifier2 = new ExtSysProdIdentifier();
        identifier2.setSystemCode("00010");
        identifier2.setProductIdentifier("id2");
        product1.getExternalSystemProductIdentifier().add(identifier2);

        offeredProducts.getProduct().add(product1);
        //******

        Product product2 = new Product();
        product2.setProductIdentifier("2");

        ProductOptions productOptions3 = new ProductOptions();
        productOptions3.setOptionsType("PrdPriority");
        productOptions3.setOptionsValue("2");

        product2.getProductoptions().add(productOptions3);

        ProductOptions productOptions4 = new ProductOptions();
        productOptions4.setOptionsType("Priority");
        productOptions4.setOptionsValue("3");

        product2.getProductoptions().add(productOptions4);

        ExtSysProdIdentifier identifier3 = new ExtSysProdIdentifier();
        identifier3.setSystemCode("code3");
        identifier3.setProductIdentifier("id3");
        product2.getExternalSystemProductIdentifier().add(identifier3);

        ExtSysProdIdentifier identifier4 = new ExtSysProdIdentifier();
        identifier4.setSystemCode("00010");
        identifier4.setProductIdentifier("mnemonic 4");
        product2.getExternalSystemProductIdentifier().add(identifier4);


        offeredProducts.getProduct().add(product2);

        return offeredProducts;
    }

    public RetrieveProductConditionsResponse rpcResponseForDownsellForF205() {
        RetrieveProductConditionsResponse offeredProducts = new RetrieveProductConditionsResponse();

        Product product1 = new Product();
        product1.setProductIdentifier("11");

        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsType("PrdPriority");
        productOptions1.setOptionsValue("3");

        product1.getProductoptions().add(productOptions1);

        ProductOptions productOptions2 = new ProductOptions();
        productOptions2.setOptionsType("Priority");
        productOptions2.setOptionsValue("1");

        product1.getProductoptions().add(productOptions2);


        ExtSysProdIdentifier identifier1 = new ExtSysProdIdentifier();
        identifier1.setSystemCode("00010");
        identifier1.setProductIdentifier("id1");
        product1.getExternalSystemProductIdentifier().add(identifier1);

        ExtSysProdIdentifier identifier2 = new ExtSysProdIdentifier();
        identifier2.setSystemCode("code2");
        identifier2.setProductIdentifier("id2");
        product1.getExternalSystemProductIdentifier().add(identifier2);

        offeredProducts.getProduct().add(product1);

        Product product2 = new Product();
        product2.setProductIdentifier("2");

        ProductOptions productOptions3 = new ProductOptions();
        productOptions3.setOptionsType("PrdPriority");
        productOptions3.setOptionsValue("2");

        product2.getProductoptions().add(productOptions3);

        ProductOptions productOptions4 = new ProductOptions();
        productOptions4.setOptionsType("Priority");
        productOptions4.setOptionsValue("3");

        product2.getProductoptions().add(productOptions4);


        ExtSysProdIdentifier identifier3 = new ExtSysProdIdentifier();
        identifier3.setSystemCode("00010");
        identifier3.setProductIdentifier("p_CLUB");
        product2.getExternalSystemProductIdentifier().add(identifier3);

        ExtSysProdIdentifier identifier4 = new ExtSysProdIdentifier();
        identifier4.setSystemCode("00010");
        identifier4.setProductIdentifier("id4");
        product2.getExternalSystemProductIdentifier().add(identifier4);

        offeredProducts.getProduct().add(product2);

        return offeredProducts;
    }

    public RetrieveProductConditionsResponse rpcResponseForUpsellForF205() {
        RetrieveProductConditionsResponse offeredProducts = new RetrieveProductConditionsResponse();

        Product product1 = new Product();
        product1.setProductIdentifier("20198");

        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsType("PrdPriority");
        productOptions1.setOptionsValue("3");

        product1.getProductoptions().add(productOptions1);

        ProductOptions productOptions2 = new ProductOptions();
        productOptions2.setOptionsType("Priority");
        productOptions2.setOptionsValue("1");

        product1.getProductoptions().add(productOptions2);

        ExtSysProdIdentifier identifier1 = new ExtSysProdIdentifier();
        identifier1.setSystemCode("00010");
        identifier1.setProductIdentifier("mnemonic 2");
        product1.getExternalSystemProductIdentifier().add(identifier1);

        ExtSysProdIdentifier identifier2 = new ExtSysProdIdentifier();
        identifier2.setSystemCode("code 2");
        identifier2.setProductIdentifier("P_CLUB");
        product1.getExternalSystemProductIdentifier().add(identifier2);

        offeredProducts.getProduct().add(product1);

        Product product2 = new Product();
        product2.setProductIdentifier("2");

        ProductOptions productOptions3 = new ProductOptions();
        productOptions3.setOptionsType("PrdPriority");
        productOptions3.setOptionsValue("1");

        product2.getProductoptions().add(productOptions3);


        ProductOptions productOptions4 = new ProductOptions();
        productOptions4.setOptionsType("Priority");
        productOptions4.setOptionsValue("2");

        product2.getProductoptions().add(productOptions4);

        ExtSysProdIdentifier identifier3 = new ExtSysProdIdentifier();
        identifier3.setSystemCode("00010");
        identifier3.setProductIdentifier("P_CLUB");
        product2.getExternalSystemProductIdentifier().add(identifier3);

        ExtSysProdIdentifier identifier4 = new ExtSysProdIdentifier();
        identifier4.setSystemCode("code4");
        identifier4.setProductIdentifier("id4");
        product2.getExternalSystemProductIdentifier().add(identifier4);


        offeredProducts.getProduct().add(product2);

        return offeredProducts;
    }

    public RetrieveProductConditionsResponse rpcResponseForNormalForF205() {
        RetrieveProductConditionsResponse offeredProducts = new RetrieveProductConditionsResponse();

        Product product1 = new Product();
        product1.setProductIdentifier("20198");

        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsType("PrdPriority");
        productOptions1.setOptionsValue("3");

        product1.getProductoptions().add(productOptions1);

        ProductOptions productOptions2 = new ProductOptions();
        productOptions2.setOptionsType("Priority");
        productOptions2.setOptionsValue("1");

        product1.getProductoptions().add(productOptions2);
        offeredProducts.getProduct().add(product1);

        Product product2 = new Product();
        product2.setProductIdentifier("2");

        ProductOptions productOptions3 = new ProductOptions();
        productOptions3.setOptionsType("PrdPriority");
        productOptions3.setOptionsValue("1");

        product2.getProductoptions().add(productOptions3);

        ProductOptions productOptions4 = new ProductOptions();
        productOptions4.setOptionsType("Priority");
        productOptions4.setOptionsValue("2");

        product2.getProductoptions().add(productOptions4);
        offeredProducts.getProduct().add(product2);
        return offeredProducts;
    }

    public DetermineEligibleCustomerInstructionsRequest createEligibilityRequestWhenBfpoNotPresentAndEligibilityIsTrue(ProcessPendingArrangementEventRequest request, boolean isBFPOIndicatorPresent) {
        EligibilityService eligibilityService = new EligibilityService();
        ProductArrangement productArrangement = createDepositArrangementForSA();
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = offerToEligibilityRequestConverter.convertOfferToEligibilityRequest(productArrangement, request.getHeader(), isBFPOIndicatorPresent);
        eligibilityRequest.getCustomerDetails().setInternalUserIdentifier("1");
        if (eligibilityRequest.getCustomerDetails().getCustomerScore() != null && !eligibilityRequest.getCustomerDetails().getCustomerScore().isEmpty()) {
            eligibilityRequest.getCustomerDetails().getCustomerScore().get(0).setScoreResult("ACCEPT");
            eligibilityRequest.getCustomerDetails().getCustomerScore().get(0).setAssessmentType("EIDV");
        } else {
            eligibilityRequest.getCustomerDetails().getCustomerScore().add(new CustomerScore());
            eligibilityRequest.getCustomerDetails().getCustomerScore().get(0).setScoreResult("ACCEPT");
            eligibilityRequest.getCustomerDetails().getCustomerScore().get(0).setAssessmentType("EIDV");

        }
        eligibilityRequest.getHeader().setContactPointId("0000777505");
        eligibilityRequest.getCustomerDetails().setSourceSystemId("3");

        AuditData auditData1 = new AuditData();
        auditData1.setAuditType("PARTY_EVIDENCE");
        auditData1.setAuditDate("12345");
        auditData1.setAuditTime("12");

        AuditData auditData2 = new AuditData();
        auditData2.setAuditType("ADDRESS_EVIDENCE");
        auditData2.setAuditDate("12345");
        auditData2.setAuditTime("12");

        eligibilityRequest.getCustomerDetails().getAuditData().add(auditData1);
        eligibilityRequest.getCustomerDetails().getAuditData().add(auditData2);

        eligibilityRequest.getCustomerDetails().setCustomerIdentifier("12345");
        eligibilityRequest.getCustomerDetails().setCidPersID("1234");
        eligibilityRequest.getCustomerDetails().setApplicantType("03");

        eligibilityRequest.getCustomerDetails().getTelephoneNumber().addAll(createTelephoneNumber());
       /* if (!isBFPOIndicatorPresent) {
            eligibilityRequest.getCustomerDetails().getCustomerScore().add(new CustomerScore());
            eligibilityRequest.getCustomerDetails().getCustomerScore().get(0).setAssessmentType("EIDV");
            eligibilityRequest.getCustomerDetails().getCustomerScore().get(0).setScoreResult("N/A");
            eligibilityRequest.getCustomerDetails().setApplicantType("03");
            eligibilityRequest.getCustomerDetails().getTelephoneNumber().addAll(createTelephoneNumber());
        }
*/
        return eligibilityRequest;
    }

    public DetermineEligibleCustomerInstructionsResponse eligibilityResponse() {
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = new DetermineEligibleCustomerInstructionsResponse();

        ProductEligibilityDetails productEligibilityDetails1 = new ProductEligibilityDetails();
        productEligibilityDetails1.setIsEligible("true");
        Product product1 = new Product();
        InstructionDetails value = new InstructionDetails();
        value.setInstructionMnemonic("mnemonic 1");
        product1.setInstructionDetails(value);
        productEligibilityDetails1.getProduct().add(product1);
        eligibilityResponse.getProductEligibilityDetails().add(productEligibilityDetails1);

        ProductEligibilityDetails productEligibilityDetails2 = new ProductEligibilityDetails();
        productEligibilityDetails2.setIsEligible("false");
        Product product2 = new Product();
        InstructionDetails value2 = new InstructionDetails();
        value2.setInstructionMnemonic("mnemonic 2");
        product2.setInstructionDetails(value2);
        productEligibilityDetails2.getProduct().add(product2);
        eligibilityResponse.getProductEligibilityDetails().add(productEligibilityDetails2);

        ProductEligibilityDetails productEligibilityDetails3 = new ProductEligibilityDetails();
        productEligibilityDetails3.setIsEligible("true");
        Product product3 = new Product();
        InstructionDetails value3 = new InstructionDetails();
        value3.setInstructionMnemonic("mnemonic 3");
        product3.setInstructionDetails(value3);
        productEligibilityDetails3.getProduct().add(product3);
        eligibilityResponse.getProductEligibilityDetails().add(productEligibilityDetails3);

        ProductEligibilityDetails productEligibilityDetails4 = new ProductEligibilityDetails();
        productEligibilityDetails4.setIsEligible("false");
        Product product4 = new Product();
        InstructionDetails value4 = new InstructionDetails();
        value4.setInstructionMnemonic("mnemonic 4");
        product4.setInstructionDetails(value4);
        productEligibilityDetails4.getProduct().add(product4);
        eligibilityResponse.getProductEligibilityDetails().add(productEligibilityDetails4);
        return eligibilityResponse;
    }

    public Q028Req createQ028Request(Customer customer) {
        Q028Req q028Req = new Q028Req();
        Identifiers identifiers = new Identifiers();
        if (!customer.getCustomerScore().isEmpty() && null != customer.getCustomerScore().get(0)) {
            identifiers.setRequestNo(customer.getCustomerScore().get(0).getScoreIdentifier());
        }
        identifiers.setLoanAgreementNo("0");
        identifiers.setSourceSystemCd("009");
        q028Req.setIdentifiers(identifiers);
        return q028Req;
    }

}
