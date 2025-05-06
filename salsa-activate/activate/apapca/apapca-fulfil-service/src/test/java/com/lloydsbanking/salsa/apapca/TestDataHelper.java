package com.lloydsbanking.salsa.apapca;

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
import com.lloydsbanking.salsa.activate.converter.ObtainAddressProductAccountAndTariff;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.apapca.service.fulfil.converter.B276RequestFactory;
import com.lloydsbanking.salsa.apapca.service.fulfil.converter.DepositArrangementToE226Request;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydsbanking.salsa.soap.asm.f425.objects.*;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.CustNoGp;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.E226Req;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.E226Resp;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.E226Result;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.*;
import com.lloydsbanking.salsa.soap.cbs.e469.objects.E469Req;
import com.lloydsbanking.salsa.soap.cbs.e469.objects.E469Resp;
import com.lloydsbanking.salsa.soap.cbs.e469.objects.E469Result;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.*;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.CardOrderCBSDecision;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.*;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.CardDeliveryAddress;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderAccount;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderCBSAddress;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.*;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderCBSCCA;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.*;
import com.lloydsbanking.salsa.soap.fs.account.*;
import com.lloydsbanking.salsa.soap.fs.application.StError;
import com.lloydsbanking.salsa.soap.fs.application.StParty;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Resp;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Result;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Resp;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Result;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Resp;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Result;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Resp;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Result;

import com.lloydsbanking.salsa.soap.soa.documentation.manager.RecordDocumentMetaContentResponse;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RetrieveDocumentMetaContentResponse;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsRequest;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsResponse;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsResponse;
import com.lloydstsb.ib.wsbridge.account.*;
import com.lloydstsb.ib.wsbridge.application.StB750BAppPerCCRegCreate;
import com.lloydstsb.ib.wsbridge.application.StB751AAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.system.StB555AWServMIS;
import com.lloydstsb.ib.wsbridge.system.StB555BWServMIS;
import com.lloydstsb.ib.wsbridge.system.TAudit;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm.HostInformation;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.ActivateBenefitArrangementRequest;
import com.lloydstsb.schema.enterprise.lcsm_common.AlternateId;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.InvolvedPartyRole;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.InvolvedPartyRoleType;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.Organization;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import com.lloydstsb.schema.personal.serviceplatform.tms.CreateTaskResponse;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreationResponse;
import com.lloydstsb.schema.personal.serviceplatform.tms.v0001.TMSRoutingInformationBO;
import com.synectics_solutions.sira.schemas.realtime.core.v1_0.realtimeresulttype4.RealtimeResultType4Type;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.SubmitWorkItemResponse;
import lib_sim_bo.businessobjects.*;
import lib_sim_bo.businessobjects.AccessToken;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_bo.businessobjects.InformationContent;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.TelephoneNumber;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import lib_sim_communicationmanager.messages.SendCommunicationRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.apache.commons.lang3.time.FastDateFormat;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
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

    public static final String DB_EVENT_SOURCE_SYSTEM_IDENTIFIER = "2";

    public static final String OAP_SOURCE_SYSTEM_IDENTIFIER = "3";

    public static final String INVALID_CONTACT_POINT_ID = "0000777";

    public static final int MAX_REPEAT_GROUP_QY = 0;

    public static final int INPUT_OFFICER_FLAG_STATUS_CD = 0;

    public static final int INPUT_OFFICER_STATUS_CD = 0;

    public static final int OVERRIDE_DETAILS_CD = 0;

    public static final String OVERRIDING_OFFICER_STAFF_NO = "0";

    public static final String CARD_ATH_RSG_PARTY_TYPE_CD = "P";

    public static final int MAX_REPEAT_GROUP_QY_CMAS = 1;

    public static final int EXTERNAL_SYS_SALSA = 19;

    public static final int MAX_REPEAT_GROUP_QY_C846 = 0;

    public static final String OPERATOR_NM = "Internet Banking";

    public static final String BSO2_AUDIT_CD = "Y";

    public static final String BSO2_PROGRAM_NM = "C812-IB";

    public static final String QUEUE_SMS_SWITCH = "SW_CM_Queue_SMS";

    public static final String QUEUE_EMAIL_SWITCH = "SW_CM_Queue_EM";

    public static final String DURABLE_MEDIUM_SWITCH = "SW_EnSTPPCAWcMl";

    public static final List<String> groupCodeList = Arrays.asList(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE, ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE);

    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter = new BapiHeaderToStHeaderConverter();
    @Autowired
    DepositArrangementToB765Request depositArrangementToB765Request;


    HeaderRetriever headerRetriever = new HeaderRetriever();

    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");

    Brands brands = new Brands("LTB", "Lloyds");

    UserTypes userTypes = new UserTypes("1001", "Customer");

    ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");

    ApplicationTypes applicationTypes = new ApplicationTypes("10001", "New Application");

    ProductTypes productTypesSavings = new ProductTypes("101", "Savings Account");

    PromotionChannels promotionChannels = new PromotionChannels("003", "Direct Mail");

    Channels channels = new Channels("004", "Internet");

    ApplicationForms applicationForm = new ApplicationForms(10002, "Ava Sales Question");


    KycStatus kycStatus = new KycStatus();

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

    ApplicationPartyRoles roles = new ApplicationPartyRoles("0001", "Key Party");


    ApplicationFeatureTypes applicationFeatureTypes1 = new ApplicationFeatureTypes("OD", "Overdraft");
    ApplicationFeatureTypes applicationFeatureTypes2 = new ApplicationFeatureTypes("CHQBK", "ChequeBook");
    ApplicationFeatureTypes applicationFeatureTypes3 = new ApplicationFeatureTypes("IBRA", "IB Registration Accepted");
    ApplicationFeatureTypes applicationFeatureTypes4 = new ApplicationFeatureTypes("BENPA", "Benefit Payments");
    ApplicationFeatureTypes applicationFeatureTypes5 = new ApplicationFeatureTypes("CIN", "Cinema");
    ApplicationFeatureTypes applicationFeatureTypes6 = new ApplicationFeatureTypes("IBRC", "IB Registration Completed");
    ApplicationFeatureTypes applicationFeatureTypes7 = new ApplicationFeatureTypes("ASA", "Account Switching Accepted");
    ApplicationFeatureTypes applicationFeatureTypes8 = new ApplicationFeatureTypes("ODPCI", "Date Pcci Viewed");

    TelephoneAddressTypes telephoneAddressTypes = new TelephoneAddressTypes();

    KycStatus status = new KycStatus();
    @Autowired
    DateFactory dateFactory;

    @Autowired
    ProductTypesDao productTypesDao;

    @Autowired
    BrandsDao brandsDao;

    @Autowired
    PromotionChannelsDao promotionChannelsDao;

    @Autowired
    ChannelsDao channelsDao;

    @Autowired
    ApplicationFormsDao applicationFormsDao;

    @Autowired
    ApplicationFeatureTypesDao applicationFeatureTypesDao;

    @Autowired
    ApplicationFeaturesDao applicationFeaturesDao;

    @Autowired
    ApplicationParametersDao applicationParametersDao;

    @Autowired
    ParameterGroupsDao parameterGroupsDao;

    @Autowired
    ReferralTeamsDao referralTeamsDao;
    @Autowired
    B276RequestFactory b276RequestFactory;

    @Autowired
    ReferralStatusDao referralStatusDao;

    @Autowired
    ApplicationFormQuestionsDao applicationFormQuestionsDao;

    @Autowired
    DemographicsDao demographicsDao;

    @Autowired
    DemographicsValuesDao demographicsValuesDao;


    @Autowired
    UserTypesDao userTypesDao;

    @Autowired
    IndividualsDao individualsDao;

    @Autowired
    IndividualAddressesDao individualAddressesDao;

    @Autowired
    StreetAddressesDao streetAddressesDao;

    @Autowired
    TelephoneAddressTypesDao telephoneAddressTypesDao;

    @Autowired
    ApplicationsDao applicationsDao;

    @Autowired
    ApplicationStatusDao appStatusDao;

    @Autowired
    ApplicationTypesDao applicationTypesDao;

    @Autowired
    PartyApplicationsDao partyApplicationsDao;

    @Autowired
    ApplicationPartyRolesDao applicationPartyRolesDao;

    @Autowired
    KycStatusDao kycStatusDao;

    @Autowired
    ApprovalStatusDao approvalStatusDao;

    @Autowired
    PromoPartyApplicationsDao promoPartyApplicationsDao;

    @Autowired
    ApplicationParameterValuesDao applicationParameterValuesDao;

    @Autowired
    ApplicationsRelatedDao applicationsRelatedDao;

    @Autowired
    ApplicationRelationshipTypesDao applicationRelationshipTypesDao;

    @Autowired
    ReferralsDao referralsDao;

    @Autowired
    TelephoneAddressesDao telephoneAddressesDao;

    @Autowired
    AbandonDeclineReasonDao abandonDeclineReasonDao;


    private HashMap<String, String> sortCodeAppGrpMap;

    public TestDataHelper() {
        generateSortCodeAppGrpMap();
    }

    public RequestHeader createApaRequestHeader() {

        RequestHeaderBuilder requestHeaderBuilder = new RequestHeaderBuilder();
        return requestHeaderBuilder.businessTransaction(TEST_BUSINESS_TRANSACTION).channelId(TEST_RETAIL_CHANNEL_ID).interactionId(TEST_INTERACTION_ID).bapiInformation(TEST_RETAIL_CHANNEL_ID, TEST_INTERACTION_ID, TEST_OCIS_ID, "ns5").securityHeader("lgsm", TEST_CUSTOMER_ID).serviceRequest("ns4", TEST_BUSINESS_TRANSACTION, "10.245.182.105", TEST_MESSAGE_ID).contactPoint("ns4", "003", TEST_CONTACT_POINT_ID, "Internet Banking", "Browser", "127.0.0.1", "Customer").build();
    }

    public ActivateProductArrangementRequest createApaRequestForPca(long arrangementId) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangement(String.valueOf(arrangementId)));
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestForPca() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangement(null));
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestForPcaForIBRegistration(long arrangementId) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangement(String.valueOf(arrangementId)));
        activateProductArrangementRequest.getProductArrangement().getPrimaryInvolvedParty().setIsRegistrationSelected(false);
        activateProductArrangementRequest.getProductArrangement().getPrimaryInvolvedParty().setPassword("password1234");
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;

    }

    public ContactPoint getContactPointId(RequestHeader requestHeader) {
        return headerRetriever.getContactPoint(requestHeader);
    }

    public BapiInformation getBapiInformationFromRequestHeader(RequestHeader requestHeader) {
        return headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders());
    }

    public DepositArrangement createDepositArrangement(String arrangementId) {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementId(arrangementId);
        depositArrangement.setArrangementType("CA");

        Product product = new Product();
        product.setProductIdentifier("92");

        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("502");

        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);

        product.getAssociatedFamily().add(productFamily);
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Customer customer = new Customer();
        customer.setIsRegistrationSelected(false);
        customer.setPassword("password1234");
        customer.setCustomerIdentifier("123");
        Individual individual1 = new Individual();
        individual1.setNationalInsuranceNumber("1 2");
        customer.setIsPlayedBy(individual1);
        List<PostalAddress> postalAddressList = new ArrayList<>();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("20120101");
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("21");
        List<String> addressLinePAFdata = new ArrayList<>();
        addressLinePAFdata.add("PARK STREET");
        structuredAddress.getAddressLinePAFData().addAll(addressLinePAFdata);
        structuredAddress.setPostTown("LONDON");
        structuredAddress.setCountry("United Kingdom");
        structuredAddress.setPostCodeOut("SE1");
        structuredAddress.setPostCodeIn("2EA");
        structuredAddress.setPointSuffix("1B");
        postalAddress.setStructuredAddress(structuredAddress);
        postalAddressList.add(postalAddress);
        customer.getPostalAddress().addAll(postalAddressList);
        depositArrangement.setPrimaryInvolvedParty(customer);
        depositArrangement.getPrimaryInvolvedParty().setCidPersID("123");
        depositArrangement.setIsOverdraftRequired(false);
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("779129");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);

        Individual individual = new Individual();
        individual.getIndividualName().add(new IndividualName());

        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(individual);
        depositArrangement.getJointParties().add(jointPartyCustomer);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        depositArrangement.getFinancialInstitution().setChannel("LTB");
        depositArrangement.setIsJointParty(false);
        depositArrangement.setApplicationType(NEW_APPLICATION_TYPE);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("Y");
        depositArrangement.getConditions().add(ruleCondition);
        RuleCondition ruleCondition1 = new RuleCondition();
        ruleCondition1.setName("LIFE_STYLE_BENEFIT_CODE");
        ruleCondition1.setResult("CIN");
        depositArrangement.getConditions().add(ruleCondition1);


        RuleCondition ruleCondition2 = new RuleCondition();
        ruleCondition2.setName("ALERT_MSGES");
        ruleCondition2.setResult("5");
        depositArrangement.getConditions().add(ruleCondition2);

        depositArrangement.setIsOverdraftRequired(false);
        depositArrangement.setAccountDetails(new AccountDetails());
        depositArrangement.setAccountSwitchingDetails(new DirectDebit());
        depositArrangement.getAccountSwitchingDetails().setCardNumber("789543277");
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setCustomerLocation(new Location());
        return depositArrangement;
    }

    public DepositArrangement createDepositArrangementForB750() {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementId("90261");
        depositArrangement.setArrangementType("CA");

        //Application status will be retrieved from retrieveProductArrangementDetails service for Online Activate call,
        // below line of code needs to be removed after this service call integration
        depositArrangement.setApplicationStatus("1002");

        Product product = new Product();
        product.setProductIdentifier("92");

        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("502");

        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);

        product.getAssociatedFamily().add(productFamily);
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Customer customer = new Customer();
        customer.setIsRegistrationSelected(true);
        customer.setPassword("****");

        customer.setEmailAddress("GalaxyTestAccount02@LloydsTSB.co.uk");

        List<PostalAddress> postalAddressList = new ArrayList<>();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("0505");
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("21");
        List<String> addressLinePAFdata = new ArrayList<>();
        addressLinePAFdata.add("PARK STREET");
        structuredAddress.getAddressLinePAFData().addAll(addressLinePAFdata);
        structuredAddress.setPostTown("LONDON");
        structuredAddress.setCountry("United Kingdom");
        structuredAddress.setPostCodeOut("SE1");
        structuredAddress.setPostCodeIn("9EQ");
        structuredAddress.setPointSuffix("1B");
        postalAddress.setStructuredAddress(structuredAddress);
        postalAddressList.add(postalAddress);
        customer.getPostalAddress().addAll(postalAddressList);

        List<TelephoneNumber> telephoneNumberList = new ArrayList<>();
        TelephoneNumber telephoneNumber = new TelephoneNumber();
        telephoneNumber.setCountryPhoneCode("44");
        telephoneNumber.setPhoneNumber("7896541231");
        telephoneNumber.setTelephoneType("7");
        telephoneNumberList.add(telephoneNumber);
        customer.getTelephoneNumber().addAll(telephoneNumberList);

        Individual isPlayedBy = new Individual();
        List<IndividualName> indNameList = new ArrayList<>();
        IndividualName indName = new IndividualName();
        indName.setFirstName("AbcdeFHI");
        indName.setLastName("Fghi");
        indName.setPrefixTitle("Mr");
        indNameList.add(indName);
        isPlayedBy.getIndividualName().addAll(indNameList);
        isPlayedBy.setIsStaffMember(false);
        isPlayedBy.setResidentialStatus("002");
        isPlayedBy.setBirthDate(createXMLGregorianCalendar(1935, 01, 01));
        isPlayedBy.setNationality("GBR");
        isPlayedBy.setNumberOfDependents(new BigInteger("0"));
        isPlayedBy.setMaritalStatus("001");
        isPlayedBy.setGender("002");
        isPlayedBy.setEmploymentStatus("006");
        isPlayedBy.setCurrentEmploymentDuration("0000");
        isPlayedBy.setOccupation("-");
        isPlayedBy.setCountryOfBirth("United Kingdom");
        CurrencyAmount totalSavingsAmount = new CurrencyAmount();
        totalSavingsAmount.setAmount(new BigDecimal(500));
        isPlayedBy.setTotalSavingsAmount(totalSavingsAmount);
        CurrencyAmount netMonthlyIncome = new CurrencyAmount();
        netMonthlyIncome.setAmount(new BigDecimal(400));
        isPlayedBy.setNetMonthlyIncome(netMonthlyIncome);
        CurrencyAmount monthlyLoanRepaymentAmount = new CurrencyAmount();
        monthlyLoanRepaymentAmount.setAmount(new BigDecimal(0));
        isPlayedBy.setMonthlyLoanRepaymentAmount(monthlyLoanRepaymentAmount);
        CurrencyAmount monthlyMortgageAmount = new CurrencyAmount();
        monthlyMortgageAmount.setAmount(new BigDecimal(0));
        isPlayedBy.setMonthlyMortgageAmount(monthlyMortgageAmount);
        customer.setIsPlayedBy(isPlayedBy);

        customer.setCustomerIdentifier("137178748");

        List<CustomerScore> custoemrScoreList = new ArrayList<>();
        CustomerScore customerScoreEidv = new CustomerScore();
        customerScoreEidv.setScoreResult("ACCEPT");
        customerScoreEidv.setAssessmentType("EIDV");
        custoemrScoreList.add(customerScoreEidv);
        CustomerScore customerScoreAsm = new CustomerScore();
        customerScoreAsm.setScoreResult("1");
        customerScoreAsm.setAssessmentType("ASM");
        custoemrScoreList.add(customerScoreAsm);
        customer.getCustomerScore().addAll(custoemrScoreList);

        customer.setExistingAccountDuration("2900");
        customer.setCidPersID("+00950101202");
        customer.setHasExistingCreditCard(false);
        customer.setCustomerSegment("3");
        customer.setOtherBankDuration("2900");
        customer.setNewCustomerIndicator(true);

        TaxResidencyDetails taxResidencyDetails = new TaxResidencyDetails();
        List<String> taxResidencyCountries = new ArrayList<>();
        taxResidencyCountries.add("GBR");
        taxResidencyDetails.getTaxResidencyCountries().addAll(taxResidencyCountries);
        customer.setTaxResidencyDetails(taxResidencyDetails);

        InternetBankingRegistration registeredIn = new InternetBankingRegistration();
        customer.setIsRegisteredIn(registeredIn);

        depositArrangement.setPrimaryInvolvedParty(customer);

        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("779129");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);
        depositArrangement.getFinancialInstitution().setChannel("LTB");
        Individual individual = new Individual();
        individual.getIndividualName().add(new IndividualName());

        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(individual);
        depositArrangement.getJointParties().add(jointPartyCustomer);

        depositArrangement.setIsJointParty(false);
        depositArrangement.setApplicationType("10001");

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("N");
        depositArrangement.getConditions().add(ruleCondition);

        depositArrangement.setIsOverdraftRequired(false);
        depositArrangement.setAccountDetails(new AccountDetails());
        depositArrangement.setMarketingPreferenceByEmail(false);
        depositArrangement.setMarketingPreferenceBySMS(true);
        depositArrangement.setMarketingPreferenceByMail(true);
        depositArrangement.setMarketingPreferenceByPhone(false);
        return depositArrangement;
    }

    public DepositArrangement createDepositArrangement(String arrangementId, String substatus) {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementId(arrangementId);
        depositArrangement.setArrangementType("CA");

        Product product = new Product();
        product.setProductIdentifier("92");

        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("502");

        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);

        product.getAssociatedFamily().add(productFamily);
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Customer customer = new Customer();
        customer.setIsRegistrationSelected(false);
        customer.setPassword("password1234");
        customer.setCustomerIdentifier("123");
        Individual individual1 = new Individual();
        individual1.setNationalInsuranceNumber("1 2");
        customer.setIsPlayedBy(individual1);
        List<PostalAddress> postalAddressList = new ArrayList<>();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("20120101");
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("21");
        List<String> addressLinePAFdata = new ArrayList<>();
        addressLinePAFdata.add("PARK STREET");
        structuredAddress.getAddressLinePAFData().addAll(addressLinePAFdata);
        structuredAddress.setPostTown("LONDON");
        structuredAddress.setCountry("United Kingdom");
        structuredAddress.setPostCodeOut("SE1");
        structuredAddress.setPostCodeIn("2EA");
        structuredAddress.setPointSuffix("1B");
        postalAddress.setStructuredAddress(structuredAddress);
        postalAddressList.add(postalAddress);
        customer.getPostalAddress().addAll(postalAddressList);
        depositArrangement.setPrimaryInvolvedParty(customer);
        depositArrangement.getPrimaryInvolvedParty().setCidPersID("123");
        depositArrangement.setIsOverdraftRequired(false);
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("779129");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);

        Individual individual = new Individual();
        individual.getIndividualName().add(new IndividualName());

        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(individual);
        depositArrangement.getJointParties().add(jointPartyCustomer);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        depositArrangement.getFinancialInstitution().setChannel("LTB");
        depositArrangement.setIsJointParty(false);
        depositArrangement.setApplicationType(NEW_APPLICATION_TYPE);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("N");
        depositArrangement.getConditions().add(ruleCondition);
        depositArrangement.setApplicationSubStatus(substatus);

        depositArrangement.setIsOverdraftRequired(false);
        depositArrangement.setAccountDetails(new AccountDetails());
        return depositArrangement;
    }

    public DepositArrangement createDepositArrangementFor1026(String arrangementId) {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementId(arrangementId);
        depositArrangement.setArrangementType("CA");

        Product product = new Product();
        product.setProductIdentifier("92");

        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("502");

        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);

        product.getAssociatedFamily().add(productFamily);
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Customer customer = new Customer();
        customer.setIsRegistrationSelected(false);
        customer.setPassword("password1234");
        customer.setCustomerIdentifier("123");
        Individual individual1 = new Individual();
        individual1.setNationalInsuranceNumber("1 2");
        customer.setIsPlayedBy(individual1);
        List<PostalAddress> postalAddressList = new ArrayList<>();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("20120101");
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("21");
        List<String> addressLinePAFdata = new ArrayList<>();
        addressLinePAFdata.add("PARK STREET");
        structuredAddress.getAddressLinePAFData().addAll(addressLinePAFdata);
        structuredAddress.setPostTown("LONDON");
        structuredAddress.setCountry("United Kingdom");
        structuredAddress.setPostCodeOut("SE1");
        structuredAddress.setPostCodeIn("2EA");
        structuredAddress.setPointSuffix("1B");
        postalAddress.setStructuredAddress(structuredAddress);
        postalAddressList.add(postalAddress);
        customer.getPostalAddress().addAll(postalAddressList);
        depositArrangement.setPrimaryInvolvedParty(customer);
        depositArrangement.getPrimaryInvolvedParty().setCidPersID("123");
        depositArrangement.setIsOverdraftRequired(false);
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("779129");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);

        Individual individual = new Individual();
        individual.getIndividualName().add(new IndividualName());

        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(individual);
        depositArrangement.getJointParties().add(jointPartyCustomer);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        depositArrangement.getFinancialInstitution().setChannel("LTB");
        depositArrangement.setIsJointParty(false);
        depositArrangement.setApplicationType(NEW_APPLICATION_TYPE);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("Y");
        depositArrangement.getConditions().add(ruleCondition);

        depositArrangement.setIsOverdraftRequired(false);
        depositArrangement.setAccountDetails(new AccountDetails());
        return depositArrangement;
    }

    public DepositArrangement createDepositArrangementFor1026AndOverdraftTrue(String arrangementId, String substatus) {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementId(arrangementId);
        depositArrangement.setArrangementType("CA");

        Product product = new Product();
        product.setProductIdentifier("92");

        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("502");

        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);

        product.getAssociatedFamily().add(productFamily);
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Customer customer = new Customer();
        customer.setIsRegistrationSelected(false);
        customer.setPassword("password1234");
        customer.setCustomerIdentifier("123");
        Individual individual1 = new Individual();
        individual1.setNationalInsuranceNumber("1 2");
        customer.setIsPlayedBy(individual1);
        List<PostalAddress> postalAddressList = new ArrayList<>();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("20120101");
        StructuredAddress structuredAddress = new StructuredAddress();
        //structuredAddress.setBuildingNumber("21");
        List<String> addressLinePAFdata = new ArrayList<>();
        addressLinePAFdata.add("PARK STREET");
        structuredAddress.getAddressLinePAFData().addAll(addressLinePAFdata);
        /*structuredAddress.setPostTown("LONDON");
        structuredAddress.setCountry("United Kingdom");*/
        structuredAddress.setPostCodeOut("SE1");
        structuredAddress.setPostCodeIn("2EA");
        structuredAddress.setPointSuffix("1B");
        postalAddress.setStructuredAddress(structuredAddress);
        postalAddressList.add(postalAddress);
        customer.getPostalAddress().addAll(postalAddressList);
        depositArrangement.setPrimaryInvolvedParty(customer);
        depositArrangement.getPrimaryInvolvedParty().setCidPersID("123");
        depositArrangement.setIsOverdraftRequired(false);
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("779129");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);

        Individual individual = new Individual();
        individual.getIndividualName().add(new IndividualName());

        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(individual);
        depositArrangement.getJointParties().add(jointPartyCustomer);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        depositArrangement.getFinancialInstitution().setChannel("LTB");
        depositArrangement.setIsJointParty(false);
        depositArrangement.setApplicationType(NEW_APPLICATION_TYPE);
        depositArrangement.setApplicationSubStatus(substatus);
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("Y");
        depositArrangement.getConditions().add(ruleCondition);
        depositArrangement.setApplicationSubStatus(substatus);
        depositArrangement.setIsOverdraftRequired(true);
        depositArrangement.setAccountDetails(new AccountDetails());
        return depositArrangement;
    }

    public List<ReferenceDataLookUp> createLookupData() {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 513L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "1", "Purpose of Account", 1091L, "SPORI", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "2", "Purpose of Account", 1092L, "BIEXP", "LTB", 1L));

        return referenceDataLookUpList;
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

    public ActivateProductArrangementRequest createApaRequestByDBEvent() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangementForDBEvent());
        activateProductArrangementRequest.setSourceSystemIdentifier(DB_EVENT_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestWithInvalidStatus(long arrangementId) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangement(String.valueOf(arrangementId)));
        activateProductArrangementRequest.setSourceSystemIdentifier(OAP_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    private ProductArrangement createDepositArrangementForDBEvent() {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementId("90261");
        depositArrangement.setArrangementType("CA");
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

    public StB766BRetrieveCBSAppGroup createStB766BRetrieveCBSAppGroup(String cbsAppGroup) {
        StB766BRetrieveCBSAppGroup stB766BRetrieveCBSAppGroup = new StB766BRetrieveCBSAppGroup();
        stB766BRetrieveCBSAppGroup.setCbsappgroup(cbsAppGroup);
        return stB766BRetrieveCBSAppGroup;
    }

    public E469Resp createE469Resp() {
        E469Resp e469Resp = new E469Resp();
        e469Resp.setAdditionalDataIn(1);
        e469Resp.setAlternateSortCodeBranchId("007505");
        e469Resp.setE469Result(new E469Result());
        e469Resp.getE469Result().setResultCondition(new ResultCondition());
        e469Resp.getE469Result().getResultCondition().setSeverityCode((byte) 0);
        e469Resp.getE469Result().getResultCondition().setReasonCode(131);
        e469Resp.getE469Result().getResultCondition().setReasonText("Error while getting response");
        return e469Resp;
    }

    public E229Resp createE229Resp() {
        E229Resp e229Resp = new E229Resp();
        e229Resp.setAdditionalDataIn(1);
        e229Resp.setStemId("779129");
        e229Resp.setE229Result(new E229Result());
        e229Resp.getE229Result().setResultCondition(new ResultCondition());
        e229Resp.getE229Result().getResultCondition().setSeverityCode((byte) 0);
        e229Resp.getE229Result().getResultCondition().setReasonCode(1458);
        e229Resp.getE229Result().getResultCondition().setReasonText("Error while getting response");
        return e229Resp;
    }

    public E229Resp createE229RespWithoutError() {
        E229Resp e229Resp = new E229Resp();
        e229Resp.setAdditionalDataIn(1);
        E229Result result = new E229Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(0);
        result.setResultCondition(resultCondition);
        e229Resp.setE229Result(result);
        CbsAcTypeChkDgt chkDgt = new CbsAcTypeChkDgt();
        chkDgt.setCBSAccountTypeCd("6");
        chkDgt.setCheckDigitId("112");
        e229Resp.getCbsAcTypeChkDgt().add(chkDgt);
        e229Resp.setStemId("779129");
        return e229Resp;
    }

    public String getAccountNumberFromE229Resp(E229Resp e229Resp) {
        String acc = null;
        if (!StringUtils.isEmpty(e229Resp.getStemId())) {
            for (CbsAcTypeChkDgt accType : e229Resp.getCbsAcTypeChkDgt()) {
                if ("6".equalsIgnoreCase(accType.getCBSAccountTypeCd())) {
                    acc = (e229Resp.getStemId() + "6" + accType.getCheckDigitId());
                }
            }
        }
        return acc;
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


    @Transactional
    public long createApplicationCA(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber) {
        return createApplication(applicationStatus, eidvStatus, asmDecision, applicationSubStatus, niNumber, productTypesCurrent).getId();
    }

    @Transactional
    public Applications createNewApplication(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        return createApplication(applicationStatus, eidvStatus, asmDecision, applicationSubStatus, niNumber, productType);
    }

    @Transactional
    public Applications createNewApplicationWithoutIbregistrationData(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        return createApplicationWithoutIbRegistrationData(applicationStatus, eidvStatus, asmDecision, applicationSubStatus, niNumber, productType);
    }

    @Transactional
    public Applications createNewApplicationWithIntendToSwitch(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
        return createApplicationWithIntendToSwitch(applicationStatus, eidvStatus, asmDecision, applicationSubStatus, niNumber, productType);
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

        //  ApplicationParameterValues applicationParameterValues14 = new ApplicationParameterValues(1L, applicationParameters12, applications, "345", null);

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
        //applicationParameterValuesDao.save(applicationParameterValues14);
        //applicationParameterValuesDao.findAll();

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
        //applicationParameterValuesSet.add(applicationParameterValues14);

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


        Set<PartyApplications> partyApplicationsSet = new HashSet<>();
        partyApplicationsSet.add(partyApplications);

        individuals.setPartyApplications(partyApplicationsSet);

        applications.setPartyApplications(partyApplicationsSet);
        return applications;
    }

    @Transactional
    @Modifying()
    public Applications createApplicationWithIntendToSwitch(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
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

    @Transactional
    public Applications createApplicationWithoutIbRegistrationData(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber, ProductTypes productType) {
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


        Set<PartyApplications> partyApplicationsSet = new HashSet<>();
        partyApplicationsSet.add(partyApplications);

        individuals.setPartyApplications(partyApplicationsSet);
        applications.setPartyApplications(partyApplicationsSet);
        return applications;
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
        kycStatusDao.deleteAll();
        appStatusDao.deleteAll();
        applicationTypesDao.deleteAll();
        productTypesDao.deleteAll();
        brandsDao.deleteAll();
        userTypesDao.deleteAll();
        promotionChannelsDao.deleteAll();
        channelsDao.deleteAll();
        promoPartyApplicationsDao.deleteAll();
        applicationRelationshipTypesDao.deleteAll();
        applicationParameterValuesDao.deleteAll();
        applicationParametersDao.deleteAll();
        telephoneAddressesDao.deleteAll();
        telephoneAddressTypesDao.deleteAll();
        applicationFeaturesDao.deleteAll();
        applicationFeatureTypesDao.deleteAll();
        abandonDeclineReasonDao.deleteAll();
        referralStatusDao.deleteAll();
        applicationPartyRolesDao.deleteAll();
    }

    public void createApplicationForTmsTaskId(ApplicationStatus applicationStatus, KycStatus eidvStatus, String asmDecision, String applicationSubStatus, String niNumber) {
        createReferrals(createApplication(applicationStatus, eidvStatus, asmDecision, applicationSubStatus, niNumber, productTypesCurrent));
    }

    private Set<Referrals> createReferrals(Applications applications) {
        referralTeamsDao.save(new ReferralTeams(101L, "PLD(LTSB)", "206531", 3L, "9286"));
        referralTeamsDao.findAll();
        referralStatusDao.save(new ReferralStatus("PEN", "Pending"));
        referralStatusDao.findAll();

        Referrals referrals = new Referrals(referralTeamsDao.findOne(101L), referralStatusDao.findOne("PEN"), applications, "101", 50037438L, new Date());
        //  refer
        Set<Referrals> referralsSet = new HashSet<>();
        referralsSet.add(referrals);
        return referralsSet;

    }

    public ActivateProductArrangementRequest createApaRequestForPcaWithTmsTaskId() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());

        activateProductArrangementRequest.setProductArrangement(createDepositArrangementWithTmsTaskId());
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;

    }

    public FinanceServiceArrangement createFSAForCC(long arrangementId) {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setArrangementId(String.valueOf(arrangementId));
        financeServiceArrangement.setArrangementType("CC");
        Product product = new Product();
        product.setProductIdentifier("10002");

        ProductOffer productOffer = new ProductOffer();
        productOffer.setProdOfferIdentifier("1000001");
        product.getProductoffer().add(productOffer);
        product.setProductName("Classic Credit Card");
        financeServiceArrangement.setAssociatedProduct(product);

        return financeServiceArrangement;
    }

    private DepositArrangement createDepositArrangementWithTmsTaskId() {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementType("CA");
        Product product = new Product();
        product.setProductIdentifier("92");
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Referral referral = new Referral();
        referral.setTmsTaskIdentifier("50037438");
        depositArrangement.getReferral().add(referral);

        Customer customer = new Customer();
        customer.setIsRegistrationSelected(false);
        customer.setPassword("password1234");
        customer.setCustomerIdentifier("123");
        Individual individual1 = new Individual();
        individual1.setNationalInsuranceNumber("1 2");
        customer.setIsPlayedBy(individual1);
        depositArrangement.setPrimaryInvolvedParty(customer);
        depositArrangement.getPrimaryInvolvedParty().setCidPersID("123");
        depositArrangement.setIsOverdraftRequired(false);
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("779129");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);
        depositArrangement.getFinancialInstitution().setChannel("LTB");
        Individual individual = new Individual();
        individual.getIndividualName().add(new IndividualName());

        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(individual);
        depositArrangement.getJointParties().add(jointPartyCustomer);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());

        depositArrangement.setIsJointParty(false);
        depositArrangement.setApplicationType(NEW_APPLICATION_TYPE);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("N");
        depositArrangement.getConditions().add(ruleCondition);

        depositArrangement.setIsOverdraftRequired(false);
        depositArrangement.setAccountDetails(new AccountDetails());
        return depositArrangement;
    }

    public StB750BAppPerCCRegCreate createB750Response() {
        StB750BAppPerCCRegCreate b750Response = new StB750BAppPerCCRegCreate();
        StError stError = new StError();
        stError.setErrorno(0);
        b750Response.setSterror(stError);
        b750Response.setAppid(new BigInteger("670779965"));
        b750Response.setAppverNew(new BigInteger("0"));
        b750Response.setAppstateNew("U");
        return b750Response;
    }

    public RetrieveProductConditionsRequest createRetrieveProductConditionsRequest() {
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        Product product = new Product();
        product.setProductIdentifier("92");
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
        extSysProdIdentifier.setSystemCode("00010");
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
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().add(extSysProdIdentifier);

        return retrieveProductConditionsResponse;
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
        appStatusDao.save(new ApplicationStatus(com.lloydsbanking.salsa.constant.ApplicationStatus.AWAITING_FULFILMENT.getValue(), "Awaiting Fulfilment"));
        appStatusDao.save(new ApplicationStatus("1010", "Fulfill"));
        appStatusDao.save(new ApplicationStatus("1008", "Awaiting Referral Processing"));
        appStatusDao.save(new ApplicationStatus("1004", "Decline"));
        appStatusDao.save(new ApplicationStatus("1007", "Awaiting Manual IDandV"));
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

        applicationFeatureTypesDao.findAll();

        referralStatusDao.save(new ReferralStatus("PEN", "Pending"));
        referralStatusDao.save(new ReferralStatus("COM", "Completed"));
        referralStatusDao.findAll();

        abandonDeclineReasonDao.save(new AbandonDeclineReasons("102", "ASM Decline"));
        abandonDeclineReasonDao.findAll();

    }

    public DirectDebit getAccountSwitchingDetails() {
        DirectDebit accountSwitchingDetails = new DirectDebit();
        accountSwitchingDetails.setCardNumber("1234567");
        accountSwitchingDetails.setSortCode("402715");
        accountSwitchingDetails.setAccountNumber("74885074");
        accountSwitchingDetails.setAccountHolderName("JohnyyDoeyy");
        accountSwitchingDetails.setCardExpiryDate("01/15");
        accountSwitchingDetails.setSwitchDate(new DateFactory().stringToXMLGregorianCalendar("2015-12-12", FastDateFormat.getInstance("yyyy-MM-dd")));
        accountSwitchingDetails.setOverdraftHeldIndicator("false");
        accountSwitchingDetails.setTextAlert("false");
        accountSwitchingDetails.setConsent("True");
        accountSwitchingDetails.setDebitCardHeld("false");
        return accountSwitchingDetails;
    }

    public StB766ARetrieveCBSAppGroup createB766Request(RequestHeader requestHeader, String sortCode) {
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders()).getBAPIHeader(), headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders()), headerRetriever.getContactPoint(requestHeader.getLloydsHeaders()).getContactPointId());
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = new StB766ARetrieveCBSAppGroup();
        stB766ARetrieveCBSAppGroup.setStheader(stHeader);
        stB766ARetrieveCBSAppGroup.setSortcode(sortCode);

        return stB766ARetrieveCBSAppGroup;

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

    public CBSAppGrp createCBSAppGroupFromSortCode(String sortCode) {
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        if (sortCodeAppGrpMap.containsKey(sortCode)) {
            cbsAppGrp.setCBSApplicationGroupNumber(sortCodeAppGrpMap.get(sortCode));
        } else {
            cbsAppGrp.setCBSApplicationGroupNumber("01");
        }
        return cbsAppGrp;
    }

    public String getCBSAppGrpForSortCode(String sortCode) {
        return sortCodeAppGrpMap.get(sortCode);
    }

    public StB766BRetrieveCBSAppGroup createB766Response(String appGroup, int errorNo) {
        StB766BRetrieveCBSAppGroup stB766BRetrieveCBSAppGroup = new StB766BRetrieveCBSAppGroup();
        com.lloydsbanking.salsa.soap.fs.account.StError stError = new com.lloydsbanking.salsa.soap.fs.account.StError();
        stError.setErrorno(errorNo);

        stB766BRetrieveCBSAppGroup.setSterror(stError);
        stB766BRetrieveCBSAppGroup.setCbsappgroup(appGroup);

        return stB766BRetrieveCBSAppGroup;

    }

    public E229Req createE229Request(String sortCode) {
        E229Req e229Request = new E229Req();
        e229Request.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY);
        CBSRequestGp2 cbsRequestGp2 = new CBSRequestGp2();
        cbsRequestGp2.setInputOfficerFlagStatusCd(INPUT_OFFICER_FLAG_STATUS_CD);
        cbsRequestGp2.setInputOfficerStatusCd(INPUT_OFFICER_STATUS_CD);
        cbsRequestGp2.setOverrideDetailsCd(OVERRIDE_DETAILS_CD);
        cbsRequestGp2.setOverridingOfficerStaffNo(OVERRIDING_OFFICER_STAFF_NO);
        e229Request.setCBSRequestGp2(cbsRequestGp2);
        e229Request.setSortCd(sortCode);
        return e229Request;
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


    public E469Req createE469Request(String contactPointID) {
        E469Req e469Request = new E469Req();
        e469Request.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY);
        com.lloydsbanking.salsa.soap.cbs.e469.objects.CBSRequestGp2 cbsRequestGp2 = new com.lloydsbanking.salsa.soap.cbs.e469.objects.CBSRequestGp2();
        cbsRequestGp2.setInputOfficerFlagStatusCd(INPUT_OFFICER_FLAG_STATUS_CD);
        cbsRequestGp2.setOverrideDetailsCd(OVERRIDE_DETAILS_CD);
        e469Request.setCBSRequestGp2(cbsRequestGp2);
        e469Request.setSortCd(contactPointID.substring(4, 10));
        return e469Request;

    }

    public C808Resp createC808Res() {
        C808Resp c808Resp = new C808Resp();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(1);
        resultCondition.setReasonText("Error");
        C808Result c808Result = new C808Result();
        c808Result.setResultCondition(resultCondition);
        c808Resp.setC808Result(c808Result);
        CardOrderCBSDecision cardOrderCBSDecision = new CardOrderCBSDecision();
        cardOrderCBSDecision.setCustomerDecisionTypeCd("R");
        cardOrderCBSDecision.setCustomerDecisionValueCd(50);
        cardOrderCBSDecision.setDebitCardRenewalCd(50);
        CardOrderNewAccount cardOrderNewAccount = new CardOrderNewAccount();
        cardOrderNewAccount.setExtProdIdTx("0071776000");
        cardOrderNewAccount.setProdExtSysId((short) 4);
        com.lloydsbanking.salsa.soap.cmas.c808.objects.CardOrderCBSCCA cardOrderCBSCCA = new com.lloydsbanking.salsa.soap.cmas.c808.objects.CardOrderCBSCCA();
        cardOrderCBSCCA.setCCAApplicableIn("1");
        cardOrderNewAccount.setCardOrderCBSCCA(cardOrderCBSCCA);
        c808Resp.setCardOrderNewAccount(cardOrderNewAccount);
        CardAuthoriserNew cardAuthoriserNew = new CardAuthoriserNew();
        cardAuthoriserNew.setCardOrderCBSDecision(cardOrderCBSDecision);
        c808Resp.setCardholderNew(createCardholderNew());
        c808Resp.setCardAuthoriserNew(cardAuthoriserNew);
        return c808Resp;

    }

    public C808Req createC808Request(String sortCode, String accountNumber, long customerIdentifier) {
        C808Req c808Req = new C808Req();
        c808Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY_CMAS);
        c808Req.setExtSysId(EXTERNAL_SYS_SALSA);
        c808Req.setCardAuthorisingPartyId(Long.valueOf(227323270));
        c808Req.setCardAthrsgPartyTypeCd(CARD_ATH_RSG_PARTY_TYPE_CD);
        c808Req.setCardHoldingPartyId(Long.valueOf(227323270));
        c808Req.setSortCd(sortCode);
        c808Req.setAccountNo8(accountNumber);
        return c808Req;
    }

    public C808Req createC808RequestWithSubStatus1025(String sortCode, String accountNumber, long customerIdentifier) {
        C808Req c808Req = new C808Req();
        c808Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY_CMAS);
        c808Req.setExtSysId(EXTERNAL_SYS_SALSA);
        c808Req.setCardAuthorisingPartyId(Long.valueOf(227323270));
        c808Req.setCardAthrsgPartyTypeCd(CARD_ATH_RSG_PARTY_TYPE_CD);
        c808Req.setCardHoldingPartyId(Long.valueOf(227323270));
        c808Req.setSortCd(sortCode);
        c808Req.setAccountNo8("7791296112");
        return c808Req;
    }

    public C846Req createC846Request(String productIdentifier, String cCAApplicableIndicator, String decisionText, int decisionCode, long customerIdentifier) {
        C846Req c846Req = new C846Req();
        c846Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY_C846);
        c846Req.setExtSysId(EXTERNAL_SYS_SALSA);
        c846Req.setExtProdIdTx(productIdentifier);
        c846Req.setCCAApplicableIn(cCAApplicableIndicator);
        c846Req.setCustomerDecisionTypeCd(decisionText);
        c846Req.setCustomerDecisionValueCd(decisionCode);
        c846Req.setCardAuthorisingPartyId(Long.valueOf(227323270));
        return c846Req;
    }

    public C846Resp createC846Response() {
        C846Resp c846Resp = new C846Resp();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(1);
        resultCondition.setReasonText("Error");
        C846Result c846Result = new C846Result();
        c846Result.setResultCondition(resultCondition);
        c846Resp.setC846Result(c846Result);
        c846Resp.setAdditionalDataIn(0);
        CardTypes cardTypes = new CardTypes();
        CardType cardType = new CardType();
        cardType.setCardTypeCd("2");
        cardType.setCardTypeNr("VISA Payment Card");
        cardTypes.getCardType().add(cardType);

        c846Resp.setCardTypes(cardTypes);
        PlasticTypes plasticTypes = new PlasticTypes();

        PlasticType plasticType = new PlasticType();
        plasticType.setCardTypeCd("226");
        plasticType.setPlasticTypeNr("Curr AccntDeb Ctless");
        plasticType.setCardholderNmFormattingCd(1);
        plasticType.setMaxCardholderNmLh(26);
        plasticType.setCardTypeCd("2");
        plasticType.setCardPersonalisationTypeCd("P");
        plasticType.setImageRequiredIn("0");
        plasticType.setNPFeeCollectionRequiredIn("0");
        plasticType.setPINServiceCd(1);
        plasticTypes.getPlasticType().add(plasticType);
        PlasticType plasticType1 = new PlasticType();
        plasticType1.setCardTypeCd("265");
        plasticType1.setPlasticTypeNr("Signature Curr AccntDeb Ctless");
        plasticType1.setCardholderNmFormattingCd(1);
        plasticType1.setMaxCardholderNmLh(26);
        plasticType1.setCardTypeCd("2");
        plasticType1.setCardPersonalisationTypeCd("P");
        plasticType1.setImageRequiredIn("0");
        plasticType1.setNPFeeCollectionRequiredIn("0");
        plasticType1.setPINServiceCd(1);
        plasticTypes.getPlasticType().add(plasticType1);
        c846Resp.setPlasticTypes(plasticTypes);
        return c846Resp;

    }

    public C812Req createC812Request(CardOrderNew cardOrderNew, CardOrderCBSData cardOrderCBSData, CardOrderCBSAddress cardOrderCBSAddress) {
        C812Req c812Req = new C812Req();
        c812Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY_CMAS);
        c812Req.setExtSysId(EXTERNAL_SYS_SALSA);
        c812Req.setOperatorNm(OPERATOR_NM);
        BSO2AuditControl bso2AuditControl = new BSO2AuditControl();
        bso2AuditControl.setBSO2AuditCd(BSO2_AUDIT_CD);
        bso2AuditControl.setBSO2ProgramNm(BSO2_PROGRAM_NM);
        c812Req.setBSO2AuditControl(bso2AuditControl);
        c812Req.setCardOrderNew(cardOrderNew);
        c812Req.setCardOrderCBSData(cardOrderCBSData);
        c812Req.setCardOrderCBSAddress(cardOrderCBSAddress);
        return c812Req;
    }

    public C812Req createC812RequestWithSubstatus2015(CardOrderNew cardOrderNew, CardOrderCBSData cardOrderCBSData, CardOrderCBSAddress cardOrderCBSAddress) {
        C812Req c812Req = new C812Req();
        c812Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY_CMAS);
        c812Req.setExtSysId(EXTERNAL_SYS_SALSA);
        c812Req.setOperatorNm(OPERATOR_NM);
        BSO2AuditControl bso2AuditControl = new BSO2AuditControl();
        bso2AuditControl.setBSO2AuditCd(BSO2_AUDIT_CD);
        bso2AuditControl.setBSO2ProgramNm(BSO2_PROGRAM_NM);
        c812Req.setBSO2AuditControl(bso2AuditControl);
        c812Req.setCardOrderNew(cardOrderNew);
        c812Req.setCardOrderCBSData(cardOrderCBSData);
        c812Req.setCardOrderCBSAddress(cardOrderCBSAddress);
        return c812Req;
    }

    public CardholderNew createCardholderNew() {
        CardholderNew cardholderNew = new CardholderNew();
        cardholderNew.setSurname("Fghi");
        cardholderNew.setFirstForeNm("Abcdefhi");
        cardholderNew.setInitials(new Initials());
        cardholderNew.getInitials().setFirstIt("A");
        cardholderNew.setPartyTl("Mr");
        cardholderNew.setBirthDt("26031987");
        return cardholderNew;
    }

    public C812Resp createC812Response() {
        C812Resp c812Resp = new C812Resp();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(1);
        resultCondition.setReasonText("Error");
        C812Result c812Result = new C812Result();
        c812Result.setResultCondition(resultCondition);
        c812Resp.setC812Result(c812Result);
        c812Resp.setAdditionalDataIn(0);
        CardOrderNewValid cardOrderNewValid = new CardOrderNewValid();
        CardDeliveryAddress cardDeliveryAddress = new CardDeliveryAddress();
        cardDeliveryAddress.setAddressLine1Tx("21 PARK STREET");
        cardDeliveryAddress.setAddressLine2Tx("LONDON-1");
        cardDeliveryAddress.setAddressLine3Tx("LONDON-2");
        cardDeliveryAddress.setAddressLine4Tx("LONDON-3");
        cardDeliveryAddress.setAddressLine5Tx("LONDON-4");
        cardDeliveryAddress.setAddressLine6Tx("LONDON-5");
        cardDeliveryAddress.setAddressLine7Tx("LONDON-6");
        cardDeliveryAddress.setPostCd("SE1  9EQ");
        CardNewDelivery cardNewDelivery = new CardNewDelivery();
        cardNewDelivery.setCardDeliveryReasonCd(7);
        cardNewDelivery.setCardDeliveryReasonNr("Direct Delivery");
        cardNewDelivery.setCardDeliveryMethodCd("SD2");
        cardNewDelivery.setCardDeliveryMethodNr("PO Disguised Mail");
        cardNewDelivery.setCardProductionDt("30072015");
        cardNewDelivery.setCardExpectedArrivalDt("06082015");
        cardNewDelivery.setChequeBookIn(0);
        cardNewDelivery.setDeliveryMethodGoodsCheckIn("0");
        cardNewDelivery.setReturnCardToOUCd(" ");
        cardNewDelivery.setCustomerCollectSortCd("779129");
        cardNewDelivery.setCustomerCollectOUNm("Lewisham (779129)");
        cardNewDelivery.setCardDeliveryAddress(cardDeliveryAddress);
        cardOrderNewValid.setCardNewDelivery(cardNewDelivery);
        cardOrderNewValid.setReferOverrideAllowableIn("1");
        cardOrderNewValid.setPlasticTypeServiceLevelCd("A");
        cardOrderNewValid.setCustomerIdCheckIn("1");
        cardOrderNewValid.setCollectFeesIn(" ");
        cardOrderNewValid.setCardNewDelivery(cardNewDelivery);
        c812Resp.setCardOrderNewValid(cardOrderNewValid);
        CardOrderReferralReasons cardOrderReferralReasons = new CardOrderReferralReasons();
        CardOrderReferralReason cardOrderReferralReason = new CardOrderReferralReason();
        cardOrderReferralReason.setCardOrderReferralReasonCd(6364);
        cardOrderReferralReason.setCardOrderReferralReasonDs("Awaiting Agreement/Authority");
        cardOrderReferralReason.setCardOrderRefrrlRsnTypeCd(1);
        cardOrderReferralReason.setCMASUserMessageLine1Tx("Signed Agreement/Authority");
        cardOrderReferralReason.setCMASUserMessageLine1Tx("received ?");
        cardOrderReferralReasons.getCardOrderReferralReason().add(cardOrderReferralReason);
        c812Resp.setCardOrderReferralReasons(cardOrderReferralReasons);
        return c812Resp;
    }

    public CardOrderCBSData createCardOrderCBSData() {
        CardOrderCBSData cardOrderCBSData = new CardOrderCBSData();
        com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderCBSDecision cardOrderCBSDecision = new com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderCBSDecision();
        cardOrderCBSDecision.setDebitCardRenewalCd(0);
        cardOrderCBSDecision.setCustomerDecisionTypeCd("R");
        cardOrderCBSDecision.setCustomerDecisionValueCd(50);
        cardOrderCBSData.setCardOrderCBSDecision(cardOrderCBSDecision);
        cardOrderCBSData.setCCAApplicableIn("0");
        return cardOrderCBSData;
    }

    public CardOrderNew createCardOrderNew() {
        CardOrderNew cardOrderNew = new CardOrderNew();
        CardOrderAccount cardOrderAccount = new CardOrderAccount();
        cardOrderAccount.setSortCd("779129");
        cardOrderAccount.setAccountNo8("09543160");
        cardOrderAccount.setProdExtSysId(4);
        cardOrderAccount.setExtProdIdTx("0071776000");
        cardOrderNew.setCardAuthorisingPartyId(137178748);
        cardOrderNew.setCardAthrsgPartyExtSysId(0);
        cardOrderNew.setCardAthrsgPartyTypeCd("P");
        cardOrderNew.setCardHoldingPartyId(137178748);
        cardOrderNew.setCardholderNm("FGHI/ABCDEFHI.MR");
        cardOrderNew.setCardClassificationCd("P");
        cardOrderNew.setCardOrderAccount(cardOrderAccount);
        cardOrderNew.setCardTypeCd("2");
        cardOrderNew.setPlasticTypeCd(266);
        cardOrderNew.setNPFeeCollectionRequiredIn("0");
        cardOrderNew.setImageRequiredIn("0");
        return cardOrderNew;
    }

    public CardOrderCBSAddress createCardOrderCBSAddress() {
        CardOrderCBSAddress cardOrderCBSAddress = new CardOrderCBSAddress();
        cardOrderCBSAddress.setAddressLine1Tx40("AddressLine1");
        cardOrderCBSAddress.setAddressLine2Tx40("AddressLine2");
        cardOrderCBSAddress.setAddressLine3Tx40("AddressLine3");
        cardOrderCBSAddress.setAddressLine4Tx40("AddressLine4");
        cardOrderCBSAddress.setPostCd("SE1  9EQ");
        return cardOrderCBSAddress;
    }

    public C818Req createC818Request(CardOrderAdd cardOrderAdd, CardOrderAddNew cardOrderAddNew, CardOrderCBSCCA cardOrderCBSCCA, com.lloydsbanking.salsa.soap.cmas.c818.objects.CardDeliveryAddress cardDeliveryAddress, CardOrderActions cardOrderActions) {
        C818Req c818Req = new C818Req();
        c818Req.setExtSysId(EXTERNAL_SYS_SALSA);
        c818Req.setOperatorNm(OPERATOR_NM);
        c818Req.setCardOrderAdd(cardOrderAdd);
        c818Req.setCardOrderAddNew(cardOrderAddNew);
        c818Req.setCardOrderCBSCCA(cardOrderCBSCCA);
        c818Req.setCardDeliveryAddress(cardDeliveryAddress);
        c818Req.setCardOrderActions(cardOrderActions);
        return c818Req;
    }

    public CardOrderCBSCCA createCardOrderCBSCCA() {
        CardOrderCBSCCA cardOrderCBSCCA = new CardOrderCBSCCA();
        cardOrderCBSCCA.setCCAApplicableIn("0");
        return cardOrderCBSCCA;
    }

    public CardOrderActions createCardOrderActions() {
        CardOrderActions cardOrderActions = new CardOrderActions();
        CardOrderAction cardOrderAction = new CardOrderAction();
        cardOrderAction.setCMASActionCd(1);
        cardOrderAction.setCMASActionTypeCd(6364);
        cardOrderAction.setCMASFunctionCd(5);
        cardOrderActions.getCardOrderAction().add(cardOrderAction);
        CardOrderAction cardOrderAction1 = new CardOrderAction();
        cardOrderAction1.setCMASActionCd(6);
        cardOrderAction1.setCMASActionTypeCd(0);
        cardOrderAction1.setCMASFunctionCd(5);
        cardOrderActions.getCardOrderAction().add(cardOrderAction1);
        return cardOrderActions;
    }

    public CardOrderAdd createCardOrderAdd() {
        CardOrderAdd cardOrderAdd = new CardOrderAdd();
        cardOrderAdd.setCardAuthorisingPartyId(137178748);
        cardOrderAdd.setCardHoldingPartyId(137178748);
        com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderAccount cardOrderAccount = new com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderAccount();
        cardOrderAccount.setSortCd("779129");
        cardOrderAccount.setAccountNo8("09543160");
        cardOrderAccount.setProdExtSysId(4);
        cardOrderAccount.setExtProdIdTx("0071776000");
        cardOrderAdd.setCardOrderAccount(cardOrderAccount);
        cardOrderAdd.setCardOrderStatusCd(1);
        cardOrderAdd.setCardOrderStatusDsCd(0);
        cardOrderAdd.setCardholderNm("FGHI/ABCDEFHI.MR");
        cardOrderAdd.setCardOrderAuthorityCd("Y");
        cardOrderAdd.setCardOrderConsentCd("A");
        cardOrderAdd.setCardOrderTypeCd(1);
        cardOrderAdd.setCardTypeCd("2");
        cardOrderAdd.setPlasticTypeCd(266);
        cardOrderAdd.setCustomerCollectIn(0);
        cardOrderAdd.setChequeBookOrderedIn("0");
        cardOrderAdd.setCardClassificationCd("P");
        cardOrderAdd.setPlasticTypeServiceLevelCd("A");
        return cardOrderAdd;
    }

    public CardOrderAddNew createCardOrderAddNew() {
        CardOrderAddNew cardOrderAddNew = new CardOrderAddNew();
        cardOrderAddNew.setPINRequiredIn("1");
        cardOrderAddNew.setPINServiceCd(1);
        return cardOrderAddNew;
    }

    public C818Resp createC818Resp() {
        C818Resp c818Resp = new C818Resp();
        c818Resp.setC818Result(new C818Result());
        c818Resp.getC818Result().setResultCondition(new ResultCondition());
        c818Resp.getC818Result().getResultCondition().setSeverityCode((byte) 0);
        c818Resp.setAdditionalDataIn(0);
        c818Resp.setCardOrderId(281254276);
        return c818Resp;
    }

    public com.lloydsbanking.salsa.soap.fs.application.StHeader createStHeader() {
        com.lloydsbanking.salsa.soap.fs.application.StHeader stHeader = new com.lloydsbanking.salsa.soap.fs.application.StHeader();
        int ocisId = 0;
        BigInteger bigIntegerOcisId = BigInteger.valueOf(ocisId);

        stHeader.setUseridAuthor("UNAUTHSALE");
        StParty stParty = new StParty();
        stParty.setHost("I");
        stParty.setPartyid("AAGATEWAY");
        stParty.setOcisid(bigIntegerOcisId);
        stHeader.setStpartyObo(stParty);
        stHeader.setChanid("IBL");

        stHeader.setChansecmode("PWD");
        stHeader.setSessionid("mBsicvvHKgZriAP1OzdStfP");
        stHeader.setIpAddressCaller("10.16.30.136");
        stHeader.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
        stHeader.setAcceptLanguage("0");
        stHeader.setInboxidClient("GX");

        stHeader.setChanctxt(new BigInteger("1"));
        stHeader.setCctmauthcd("");
        stHeader.setCallerlineid("");
        stHeader.setEncVerNo(new BigInteger("0"));
        return stHeader;
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

    public StB751BAppPerCCRegAuth createB751ResponseWithError() {
        StB751BAppPerCCRegAuth b751response = new StB751BAppPerCCRegAuth();
        StError stError = new StError();
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

    public C234Resp createC234Resp() {
        C234Resp c234Resp = new C234Resp();
        c234Resp.setC234Result(new C234Result());
        c234Resp.getC234Result().setResultCondition(new ResultCondition());
        c234Resp.getC234Result().getResultCondition().setReasonCode(0);
        c234Resp.getC234Result().getResultCondition().setSeverityCode(new Byte("0"));
        return c234Resp;
    }

    public BAPIHeader createBapiHeader() {
        BAPIHeader bapiHeader = new BAPIHeader();
        bapiHeader.setUseridAuthor("UNAUTHSALE");
        HostInformation hostInformation = new HostInformation();
        hostInformation.setHost("I");
        hostInformation.setPartyid("AAGATEWAY");
        hostInformation.setOcisid("0");
        bapiHeader.setStpartyObo(hostInformation);
        bapiHeader.setChanid("IBL");
        bapiHeader.setChansecmode("PWD");
        bapiHeader.setSessionid("mBsicvvHKgZriAP1OzdStfP");
        bapiHeader.setIpAddressCaller("10.16.30.136");
        bapiHeader.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
        bapiHeader.setAcceptLanguage("0");
        bapiHeader.setInboxidClient("GX");
        bapiHeader.setChanctxt(new BigInteger("0"));
        bapiHeader.setCctmauthcd("");
        bapiHeader.setCallerlineid("");
        bapiHeader.setEncVerNo(new BigInteger("0"));
        return bapiHeader;
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


    public StB765BAccCreateAccount createResponseB765WithErrorCode() {
        StB765BAccCreateAccount resp = new StB765BAccCreateAccount();
        StAccount stAccount = new StAccount();
        com.lloydsbanking.salsa.soap.fs.account.StError stError = new com.lloydsbanking.salsa.soap.fs.account.StError();
        resp.setStacc(stAccount);
        resp.setSterror(stError);
        resp.getStacc().setSortcode("779129");
        resp.setCustnum("C107550");
        resp.getSterror().setErrorno(131188);
        resp.getSterror().setErrormsg("rText");
        //resp.getStacc().setAccno("6574321");
        return resp;
    }

    public StCBSCustDtls setCustDtls() {
        StB765AAccCreateAccount stB765AAccCreateAccount = new StB765AAccCreateAccount();
        ObtainAddressProductAccountAndTariff obtainAddressandTariff = new ObtainAddressProductAccountAndTariff();

        Customer customer = new Customer();
        String addressLinePafDataType = "pafdata";

        PostalAddress postalAddress = new PostalAddress();
        StructuredAddress structuredAddress = new StructuredAddress();
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setStructuredAddress(structuredAddress);
        postalAddress.setUnstructuredAddress(unstructuredAddress);
        postalAddress.getStructuredAddress().setPostCodeOut("234");
        postalAddress.getStructuredAddress().setPostCodeIn("11");
        postalAddress.getStructuredAddress().getAddressLinePAFData().add(addressLinePafDataType);
        customer.getPostalAddress().add(postalAddress);

        Individual individual = new Individual();
        customer.setIsPlayedBy(individual);
        IndividualName individualName = new IndividualName();
        individualName.setFirstName("firstName");
        individualName.setPrefixTitle("prefixTitle");
        individualName.setLastName("lastName");
        String middleNames = "middleNames";
        individualName.getMiddleNames().add(middleNames);
        customer.getIsPlayedBy().getIndividualName().add(individualName);

        StCBSCustDtls custDtls = new StCBSCustDtls();
        custDtls.setFirstname(customer.getIsPlayedBy().getIndividualName().get(0).getFirstName());
        custDtls.setSurname(customer.getIsPlayedBy().getIndividualName().get(0).getLastName());
        custDtls.setSalutation(customer.getIsPlayedBy().getIndividualName().get(0).getPrefixTitle());
        custDtls.setSecondinitial(customer.getIsPlayedBy().getIndividualName().get(0).getMiddleNames().get(0));
        custDtls.setStaddress(obtainAddressandTariff.getStructureAddress(postalAddress.getStructuredAddress()));
        stB765AAccCreateAccount.setStCBScutomerdetails(custDtls);

        return custDtls;
    }

    public DepositArrangement createDepositArrangementResp() {
        DepositArrangement depositArrangement = new DepositArrangement();
        TestDataHelper testDataHelper = new TestDataHelper();
        Customer customer = new Customer();
        depositArrangement.setPrimaryInvolvedParty(customer);

        Organisation organisation = new Organisation();
        depositArrangement.setFinancialInstitution(organisation);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setResult("true");
        depositArrangement.getConditions().add(ruleCondition);

        OrganisationUnit unit = new OrganisationUnit();
        unit.setAreaCode("aCode");
        unit.setName("name");
        unit.setOrganisationUnitIdentifer("8765");
        unit.setRegionCode("78906");
        unit.setSortCode("sCode");
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().add(unit);
        return depositArrangement;
    }

    public StructuredAddress getStructureAddress() {
        StAddress stAddress = new StAddress();
        List<String> addresses = new ArrayList<>();

        String addressLinePafDataType = "pafdata";

        StructuredAddress structuredAddress = new StructuredAddress();

        structuredAddress.setOrganisation("SA");
        structuredAddress.setBuilding("Building1");
        structuredAddress.setSubBuilding("subBuilding");
        structuredAddress.setBuildingNumber("b1");
        structuredAddress.setHouseName("housename");
        structuredAddress.setHouseNumber("H20");
        structuredAddress.setPostTown("town");
        structuredAddress.setCountry("India");
        structuredAddress.setPostCodeIn("23");
        structuredAddress.setPostCodeOut("15");
        structuredAddress.getAddressLinePAFData().add(addressLinePafDataType);

        addresses.add(structuredAddress.getBuilding());
        addresses.add(structuredAddress.getBuildingNumber());
        addresses.add(structuredAddress.getCountry());
        addresses.add(structuredAddress.getHouseName());
        addresses.add(structuredAddress.getHouseNumber());
        addresses.add(structuredAddress.getPostTown());
        addresses.add(structuredAddress.getSubBuilding());
        addresses.add(structuredAddress.getOrganisation());
        addresses.add(structuredAddress.getAddressLinePAFData().get(0));

        int postLength = structuredAddress.getPostCodeIn().length() + structuredAddress.getPostCodeOut().length();
        if (postLength == 6 || postLength == 7 || postLength == 5) {
            stAddress.setPostcode(structuredAddress.getPostCodeOut() + " " + structuredAddress.getPostCodeIn());
        } else {
            stAddress.setPostcode(structuredAddress.getPostCodeOut() + structuredAddress.getPostCodeIn());
        }
        return structuredAddress;
    }

    public UnstructuredAddress getUnstructuredAddress() {
        StAddress b765StructureAddress = new StAddress();
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1("lin1");
        unstructuredAddress.setAddressLine2("line2");
        unstructuredAddress.setAddressLine3("line3");
        unstructuredAddress.setAddressLine4("line4");
        unstructuredAddress.setAddressLine5("line5");
        unstructuredAddress.setAddressLine6("line6");
        unstructuredAddress.setAddressLine7("line7");
        unstructuredAddress.setAddressLine8("line8");
        unstructuredAddress.setPostCode("postcode");

        StAddressLine stAddressLine = new StAddressLine();
        stAddressLine.setAddressline("address");
        b765StructureAddress.getAstaddressline().add(stAddressLine);
        b765StructureAddress.setPostcode(unstructuredAddress.getPostCode());
        return unstructuredAddress;
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

    public com.lloydsbanking.salsa.soap.fs.account.StHeader createStHeader1() {
        com.lloydsbanking.salsa.soap.fs.account.StHeader stHeader = new com.lloydsbanking.salsa.soap.fs.account.StHeader();
        int ocisId = 0;
        BigInteger bigIntegerOcisId = BigInteger.valueOf(ocisId);

        stHeader.setUseridAuthor("UNAUTHSALE");
        com.lloydsbanking.salsa.soap.fs.account.StParty stParty = new com.lloydsbanking.salsa.soap.fs.account.StParty();
        stParty.setHost("I");
        stParty.setPartyid("AAGATEWAY");
        stParty.setOcisid(bigIntegerOcisId);
        stHeader.setStpartyObo(stParty);
        stHeader.setChanid("IBL");

        stHeader.setChansecmode("PWD");
        stHeader.setSessionid("mBsicvvHKgZriAP1OzdStfP");
        stHeader.setIpAddressCaller("10.16.30.136");
        stHeader.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
        stHeader.setAcceptLanguage("0");
        stHeader.setInboxidClient("GX");

        stHeader.setChanctxt(new BigInteger("1"));
        stHeader.setCctmauthcd("");
        stHeader.setCallerlineid("");
        stHeader.setEncVerNo(new BigInteger("0"));
        return stHeader;
    }

    public ApplicationDetails createApplicationDetails() {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        TestDataHelper testDataHelper = new TestDataHelper();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode(testDataHelper.createF425Resp().getDecisionDetails().get(0).getCSDecisionReasonTypeCd());
        referralCode.setDescription(testDataHelper.createF425Resp().getDecisionDetails().get(0).getCSDecisnReasonTypeNr());
        applicationDetails.getReferralCodes().add(referralCode);
        ProductOptions productOption = new ProductOptions();
        productOption.setOptionsCode(testDataHelper.createF425Resp().getFacilitiesOffered().get(0).getCSFacilityOfferedAm());
        applicationDetails.getProductOptions().add(productOption);
        return applicationDetails;
    }

    public C658Resp createC658Resp() {
        C658Resp c658Resp = new C658Resp();
        c658Resp.setC658Result(new C658Result());
        c658Resp.getC658Result().setResultCondition(new ResultCondition());
        c658Resp.getC658Result().getResultCondition().setReasonCode(Integer.valueOf(0));
        return c658Resp;
    }

    public F060Req createF060Request() {
        F060Req f060Req = new F060Req();
        f060Req.setMaxRepeatGroupQy(1);
        f060Req.setExtSysId((short) 19);
        PartyInfo partyInfo = new PartyInfo();
        partyInfo.setPartyId(1022245653l);
        partyInfo.setPartyExtSysId((short) 19);
        partyInfo.setExtPartyIdTx("+00216450025");
        f060Req.setPartyInfo(partyInfo);
        CommsPrefData commsPrefData = new CommsPrefData();
        commsPrefData.setCommsOptCd("001");
        commsPrefData.setCommsTypeCd("007");
        commsPrefData.setAuditDt("03082015");
        commsPrefData.setAuditTm("083703");
        CommsPrefData commsPrefData1 = new CommsPrefData();
        commsPrefData1.setCommsOptCd("001");
        commsPrefData1.setCommsTypeCd("005");
        commsPrefData1.setAuditDt("03082015");
        commsPrefData1.setAuditTm("083703");
        CommsPrefData commsPrefData2 = new CommsPrefData();
        commsPrefData2.setCommsOptCd("001");
        commsPrefData2.setCommsTypeCd("002");
        commsPrefData2.setAuditDt("03082015");
        commsPrefData2.setAuditTm("083703");
        CommsPrefData commsPrefData3 = new CommsPrefData();
        commsPrefData3.setCommsOptCd("001");
        commsPrefData3.setCommsTypeCd("003");
        commsPrefData3.setAuditDt("03082015");
        commsPrefData3.setAuditTm("083703");
        List<CommsPrefData> commsPrefDataList = new ArrayList<>();
        commsPrefDataList.add(commsPrefData);
        commsPrefDataList.add(commsPrefData1);
        commsPrefDataList.add(commsPrefData2);
        commsPrefDataList.add(commsPrefData3);
        f060Req.getCommsPrefData().addAll(commsPrefDataList);
        return f060Req;
    }


    public C241Resp createC241Resp() {
        C241Resp c241Resp = new C241Resp();
        c241Resp.setC241Result(new C241Result());
        c241Resp.getC241Result().setResultCondition(new ResultCondition());
        c241Resp.getC241Result().getResultCondition().setSeverityCode((byte) 1);
        c241Resp.getC241Result().getResultCondition().setReasonCode(Integer.valueOf(0));
        return c241Resp;
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


    public StB765AAccCreateAccount createB765Request(RequestHeader header, ProductArrangement productArrangement) {
        Product product = new Product();
        InstructionDetails instructionDetails = new InstructionDetails();
        List<ExtSysProdIdentifier> extSysProdIdentifierList = new ArrayList<>();
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("01000");
        extSysProdIdentifier.setSystemCode("00010");
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


    public RecordInvolvedPartyDetailsRequest createRecordInvolvedPartyRequest() {
        RecordInvolvedPartyDetailsRequest request = new RecordInvolvedPartyDetailsRequest();
        request.setRequestHeader(new com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.RequestHeader());
        request.getRequestHeader().setDatasourceName("19");
        request.setInvolvedParty(new com.lloydsbanking.salsa.soap.soa.ipm.esb.ip.Individual());
        request.getInvolvedParty().setGrossTaxIndicator("001");
        return request;
    }


    private ProductArrangement createDepositArrangementWithChannel(String arrangementId) {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementId(arrangementId);
        depositArrangement.setArrangementType("CA");
        Product product = new Product();
        product.setProductIdentifier("92");
        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("502");

        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);

        product.getAssociatedFamily().add(productFamily);
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Customer customer = new Customer();
        customer.setIsRegistrationSelected(false);
        customer.setPassword("password1234");
        customer.setCustomerIdentifier("123");
        Individual individual1 = new Individual();
        individual1.setNationalInsuranceNumber("1 2");
        customer.setIsPlayedBy(individual1);
        depositArrangement.setPrimaryInvolvedParty(customer);
        depositArrangement.getPrimaryInvolvedParty().setCidPersID("123");
        depositArrangement.setIsOverdraftRequired(false);
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("779129");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);
        depositArrangement.getFinancialInstitution().setChannel("LTB");
        Individual individual = new Individual();
        individual.getIndividualName().add(new IndividualName());

        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(individual);
        depositArrangement.getJointParties().add(jointPartyCustomer);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());

        depositArrangement.setIsJointParty(false);
        depositArrangement.setApplicationType(NEW_APPLICATION_TYPE);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("N");
        depositArrangement.getConditions().add(ruleCondition);

        depositArrangement.setIsOverdraftRequired(false);
        depositArrangement.setAccountDetails(new AccountDetails());
        return depositArrangement;
    }

    public DepositArrangement createDepositArrangementWith1025(String arrangementId) {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setArrangementId(arrangementId);
        depositArrangement.setArrangementType("CA");
        Product product = new Product();
        product.setProductIdentifier("92");
        depositArrangement.setApplicationSubStatus("1025");
        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("502");

        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);

        product.getAssociatedFamily().add(productFamily);
        product.setProductName("Classic Account");
        depositArrangement.setAssociatedProduct(product);

        Customer customer = new Customer();
        customer.setIsRegistrationSelected(false);
        customer.setPassword("password1234");
        customer.setCustomerIdentifier("123");
        Individual individual1 = new Individual();
        individual1.setNationalInsuranceNumber("1 2");
        customer.setIsPlayedBy(individual1);
        depositArrangement.setPrimaryInvolvedParty(customer);
        depositArrangement.getPrimaryInvolvedParty().setCidPersID("123");
        depositArrangement.setIsOverdraftRequired(false);
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("779129");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);
        depositArrangement.getFinancialInstitution().setChannel("LTB");
        Individual individual = new Individual();
        individual.getIndividualName().add(new IndividualName());

        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(individual);
        depositArrangement.getJointParties().add(jointPartyCustomer);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());

        depositArrangement.setIsJointParty(false);
        depositArrangement.setApplicationType(NEW_APPLICATION_TYPE);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("N");
        depositArrangement.getConditions().add(ruleCondition);

        depositArrangement.setIsOverdraftRequired(false);
        depositArrangement.setAccountDetails(new AccountDetails());
        return depositArrangement;
    }

    public ActivateProductArrangementRequest createApaRequestForPcaWithSubStatus1025(long arrangementId) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangementWith1025(String.valueOf(arrangementId)));
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public E226Req createE226Request(long arrangementId) {
        DepositArrangementToE226Request depositArrangementToE226Request = new DepositArrangementToE226Request();
        DepositArrangement depositArrangement = createDepositArrangementWith1025(String.valueOf(arrangementId));
        OverdraftDetails overdraftDetails = assignOverdraftAmount(depositArrangement.isIsOverdraftRequired(), depositArrangement.getOverdraftDetails());
        E226Req request = depositArrangementToE226Request.getAddInterPartyRelationshipRequest(depositArrangement.getConditions(), depositArrangement.getPrimaryInvolvedParty().getCustomerNumber(), overdraftDetails.getAmount().getAmount());
        CustNoGp custNoGp = new CustNoGp();
        custNoGp.setCBSCustNo("GATEWAY");
        custNoGp.setNationalSortcodeId("AA");
        request.setCustNoGp(custNoGp);
        request.setCardOfferCd(50);
        return request;
    }


    public E226Resp createE226Resp() {
        E226Resp e226Resp = new E226Resp();
        E226Result e226Result = new E226Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(0);
        e226Result.setResultCondition(resultCondition);
        e226Resp.setE226Result(e226Result);
        return e226Resp;
    }

    private OverdraftDetails assignOverdraftAmount(Boolean isOverdraftRequired, OverdraftDetails overdraftDetails) {
        OverdraftDetails newOverdraftDetails = new OverdraftDetails();
        CurrencyAmount amount = new CurrencyAmount();
        amount.setAmount(BigDecimal.ZERO);
        newOverdraftDetails.setAmount(amount);
        return isOverdraftRequired ? overdraftDetails : newOverdraftDetails;
    }

    public E226Req createE226RequestWithNoSubstatus(long arrangementId) {
        DepositArrangementToE226Request depositArrangementToE226Request = new DepositArrangementToE226Request();
        DepositArrangement depositArrangement = (DepositArrangement) createDepositArrangementWithChannel(String.valueOf(arrangementId));
        OverdraftDetails overdraftDetails = new OverdraftDetails();
        CurrencyAmount amount = new CurrencyAmount();
        amount.setAmount(BigDecimal.ONE);
        overdraftDetails.setAmount(amount);
        depositArrangement.setOverdraftDetails(overdraftDetails);
        OverdraftDetails overdraftDetails1 = assignOverdraftAmount(depositArrangement.isIsOverdraftRequired(), depositArrangement.getOverdraftDetails());
        E226Req request = depositArrangementToE226Request.getAddInterPartyRelationshipRequest(depositArrangement.getConditions(), depositArrangement.getPrimaryInvolvedParty().getCustomerNumber(), overdraftDetails1.getAmount().getAmount());
        CustNoGp custNoGp = new CustNoGp();
        custNoGp.setCBSCustNo("7323270");
        custNoGp.setNationalSortcodeId("22");
        request.setCustNoGp(custNoGp);

        request.setCardOfferCd(50);
        return request;

    }

    public E226Req createE226RequestWithNoSubstatusInSecondScenario(long arrangementId) {
        DepositArrangementToE226Request depositArrangementToE226Request = new DepositArrangementToE226Request();
        DepositArrangement depositArrangement = (DepositArrangement) createDepositArrangementWithChannel(String.valueOf(arrangementId));
        OverdraftDetails overdraftDetails = new OverdraftDetails();
        CurrencyAmount amount = new CurrencyAmount();
        amount.setAmount(BigDecimal.ONE);
        overdraftDetails.setAmount(amount);
        depositArrangement.setOverdraftDetails(overdraftDetails);
        OverdraftDetails overdraftDetails1 = assignOverdraftAmount(depositArrangement.isIsOverdraftRequired(), depositArrangement.getOverdraftDetails());
        E226Req request = depositArrangementToE226Request.getAddInterPartyRelationshipRequest(depositArrangement.getConditions(), depositArrangement.getPrimaryInvolvedParty().getCustomerNumber(), overdraftDetails1.getAmount().getAmount());
        CustNoGp custNoGp = new CustNoGp();
        custNoGp.setCBSCustNo("7323270");
        custNoGp.setNationalSortcodeId("22");
        request.setCustNoGp(custNoGp);
        request.setShdwDcnFrmlOdrLmtAm("1");
        request.setCardOfferCd(50);
        return request;

    }


    public E226Resp createE226RespFailure() {
        E226Resp e226Resp = new E226Resp();
        E226Result e226Result = new E226Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(123456);
        e226Result.setResultCondition(resultCondition);
        e226Resp.setE226Result(e226Result);
        return e226Resp;
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

    public StB276AAccProcessOverdraft createB276Request(DepositArrangement depositArrangement, RequestHeader header) {
        StB276AAccProcessOverdraft request = b276RequestFactory.convert(depositArrangement, header);
        return request;
    }


    public StB276BAccProcessOverdraft createResponseB276() {
        StB276BAccProcessOverdraft stB276BAccProcessOverdraft = new StB276BAccProcessOverdraft();
        return stB276BAccProcessOverdraft;
    }

    public StB276BAccProcessOverdraft createResponseB276Fails() {
        StB276BAccProcessOverdraft stB276BAccProcessOverdraft = new StB276BAccProcessOverdraft();
        com.lloydsbanking.salsa.soap.fs.account.StError stError = new com.lloydsbanking.salsa.soap.fs.account.StError();
        stError.setErrorno(90);
        stB276BAccProcessOverdraft.setSterror(stError);
        return stB276BAccProcessOverdraft;
    }

    public ActivateProductArrangementRequest createApaRequestForPcaWithDepositArrangement(DepositArrangement arrangement) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(arrangement);
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestForPcaWithSubStatus(long arrangementId, String substatus) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangement(String.valueOf(arrangementId), substatus));
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestForPcaWithSubStatusWithDebitCardFlagTrue(long arrangementId) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangementFor1026(String.valueOf(arrangementId)));
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    public ActivateProductArrangementRequest createApaRequestForPcaWithSubStatusAndOverdraftTrue(long arrangementId, String s) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangementFor1026AndOverdraftTrue(String.valueOf(arrangementId), s));
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
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

    public F062Resp createF062Resp() {
        F062Resp f062Resp = new F062Resp();
        f062Resp.setPartyId(123l);
        return f062Resp;
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

    public RecordInvolvedPartyDetailsResponse createRecordInvolvedPartyResponse() {
        RecordInvolvedPartyDetailsResponse recordInvolvedPartyDetailsResponse = new RecordInvolvedPartyDetailsResponse();
        recordInvolvedPartyDetailsResponse.setResponseHeader(new com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.ResponseHeader());
        recordInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.ResultCondition());
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("3");
        return recordInvolvedPartyDetailsResponse;
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

    public RecordDocumentMetaContentResponse createRecordDocContentResponseFails() {
        RecordDocumentMetaContentResponse response = new RecordDocumentMetaContentResponse();
        com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResultCondition resultCondition = new com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResultCondition();
        resultCondition.setSeverityCode("1");
        com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResponseHeader responseHeader = new com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResponseHeader();
        responseHeader.setResultConditions(resultCondition);
        response.setResponseHeader(responseHeader);
        return response;
    }

    public ActivateBenefitArrangementRequest activateBenefitArrangementRequest() {
        ActivateBenefitArrangementRequest request = new ActivateBenefitArrangementRequest();
        request.setArrangementInput(new com.lloydstsb.schema.enterprise.lcsm_arrangement.BenefitArrangement());
        com.lloydstsb.schema.enterprise.lcsm_common.RuleCondition ruleCondition = new com.lloydstsb.schema.enterprise.lcsm_common.RuleCondition();
        ruleCondition.setName("PENDING_SELECTION");
        ruleCondition.setResult("CIN");
        com.lloydstsb.schema.enterprise.lcsm_common.RuleCondition ruleCondition1 = new com.lloydstsb.schema.enterprise.lcsm_common.RuleCondition();
        ruleCondition1.setName("TRIGGER");
        ruleCondition1.setResult("ACTIVATE_LIFESTYLE_BENEFIT");
        request.getArrangementInput().getHasObjectConditions().add(ruleCondition);
        request.getArrangementInput().getHasObjectConditions().add(ruleCondition1);
        request.getArrangementInput().setName("ADDED_VALUE_BENEFIT");

        InvolvedPartyRole involvedPartyRole = new InvolvedPartyRole();
        com.lloydstsb.schema.enterprise.lcsm_involvedparty.Individual involvedParty = new com.lloydstsb.schema.enterprise.lcsm_involvedparty.Individual();
        com.lloydstsb.schema.enterprise.lcsm_involvedparty.ElectronicAddress electronicAddress = new com.lloydstsb.schema.enterprise.lcsm_involvedparty.ElectronicAddress();
        electronicAddress.setContactMedium(com.lloydstsb.schema.enterprise.lcsm_involvedparty.ContactMedium.EMAIL);
        electronicAddress.setEmail("GalaxyTestAccount02@LloydsTSB.co.uk");
        involvedParty.getContactPoint().add(electronicAddress);
        com.lloydstsb.schema.enterprise.lcsm_involvedparty.TelephoneNumber telephoneNumber = new com.lloydstsb.schema.enterprise.lcsm_involvedparty.TelephoneNumber();
        telephoneNumber.setContactMedium(com.lloydstsb.schema.enterprise.lcsm_involvedparty.ContactMedium.PHONE);
        telephoneNumber.setFullNumber("9123456789");
        involvedParty.getContactPoint().add(telephoneNumber);
        involvedParty.setBirthDate(new DateFactory().stringToXMLGregorianCalendar("12071992", FastDateFormat.getInstance("ddMMyyyy")));
        com.lloydstsb.schema.enterprise.lcsm_involvedparty.IndividualName individualName = new com.lloydstsb.schema.enterprise.lcsm_involvedparty.IndividualName();
        individualName.setFirstName("meera");
        individualName.setPrefixTitle("Mr");
        individualName.getLastName().add("radha");
        involvedParty.setName(individualName);
        involvedPartyRole.setInvolvedParty(involvedParty);
        InvolvedPartyRoleType involvedPartyRoleType = new InvolvedPartyRoleType();
        involvedPartyRoleType.setValue("CUSTOMER");
        involvedPartyRole.setType(involvedPartyRoleType);
        request.getArrangementInput().getRoles().add(involvedPartyRole);

        com.lloydstsb.schema.enterprise.lcsm_arrangement.ArrangementAssociation arrangementAssociation = new com.lloydstsb.schema.enterprise.lcsm_arrangement.ArrangementAssociation();
        com.lloydstsb.schema.enterprise.lcsm_arrangement.DepositArrangement relatedArrangement = new com.lloydstsb.schema.enterprise.lcsm_arrangement.DepositArrangement();
        com.lloydstsb.schema.enterprise.lcsm_common.ObjectReference objectReference = new com.lloydstsb.schema.enterprise.lcsm_common.ObjectReference();
        AlternateId alternateId = new AlternateId();
        alternateId.setAttributeString("ACCOUNT_NUMBER");
        alternateId.setValue("7791296112");
        objectReference.getAlternateId().add(alternateId);
        relatedArrangement.setObjectReference(objectReference);

        InvolvedPartyRole involvedPartyRole1 = new InvolvedPartyRole();
        Organization organization = new Organization();
        com.lloydstsb.schema.enterprise.lcsm_common.ObjectReference objectReference1 = new com.lloydstsb.schema.enterprise.lcsm_common.ObjectReference();
        AlternateId alternateId1 = new AlternateId();
        alternateId1.setAttributeString("SORT_CODE");
        alternateId1.setValue("779129");
        objectReference1.getAlternateId().add(alternateId1);
        organization.setObjectReference(objectReference1);
        involvedPartyRole1.setInvolvedParty(organization);

        InvolvedPartyRoleType involvedPartyRoleType1 = new InvolvedPartyRoleType();
        involvedPartyRoleType1.setValue("FINANCIAL_INSTITUTION");
        involvedPartyRole1.setType(involvedPartyRoleType1);
        relatedArrangement.getRoles().add(involvedPartyRole1);

        arrangementAssociation.setRelatedArrangement(relatedArrangement);
        request.getArrangementInput().getArrangementAssociations().add(arrangementAssociation);

        com.lloydstsb.schema.enterprise.lcsm_arrangement.ArrangementType arrangementType = new com.lloydstsb.schema.enterprise.lcsm_arrangement.ArrangementType();
        arrangementType.setName("AVB");
        com.lloydstsb.schema.enterprise.lcsm_arrangement.ArrangementType arrangementType1 = new com.lloydstsb.schema.enterprise.lcsm_arrangement.ArrangementType();
        arrangementType1.setName("001");
        arrangementType.getSubType().add(arrangementType1);
        request.getArrangementInput().setHasArrangementType(arrangementType);
        return request;
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



}
