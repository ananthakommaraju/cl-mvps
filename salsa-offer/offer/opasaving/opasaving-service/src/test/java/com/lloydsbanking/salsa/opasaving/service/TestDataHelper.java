package com.lloydsbanking.salsa.opasaving.service;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061RequestBuilder;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062RequestBuilder;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydsbanking.salsa.offer.createinvolvedparty.convert.*;
import com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate.EvaluateAddressUpdData;
import com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate.EvaluatePersonalUpdData;
import com.lloydsbanking.salsa.offer.eligibility.convert.OfferToEligibilityRequestConverter;
import com.lloydsbanking.salsa.offer.eligibility.downstream.EligibilityService;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.identify.convert.EnquirePartyIdRequestFactory;
import com.lloydsbanking.salsa.offer.verify.convert.RetrieveEIDVScoreRequestFactory;
import com.lloydsbanking.salsa.opasaving.client.*;
import com.lloydsbanking.salsa.soap.asm.f204.objects.CSAddressDetails;
import com.lloydsbanking.salsa.soap.asm.f204.objects.DecisionDetails;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Result;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Req;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Resp;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Result;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Req;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Resp;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Result;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.definitions.RequestGroup;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.productcustomermatching.v2.productcustomermatching.serviceparameters.iproductcustomermatching.DetermineCustomerProductConditionsRequest;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.productcustomermatching.v2.productcustomermatching.serviceparameters.iproductcustomermatching.DetermineCustomerProductConditionsResponse;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.transferobjects.productcustomermatching.v2.*;
import com.lloydsbanking.xml.schema.enterprise.structuralcomponents.InvolvedPartyIdentifier;
import com.lloydsbanking.xml.schema.enterprise.structuralcomponents.ProductIdentifier;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ExtraConditions;
import com.lloydstsb.schema.infrastructure.soap.ReasonDetail;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import lib_sim_bo.businessobjects.*;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyParty;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyPartyResp;
import lloydstsb.schema.personal.customer.partyidandv.serviceobjects.EvaluationEvidence;
import lloydstsb.schema.personal.customer.partyidandv.serviceobjects.IdentifyPartyOutput;
import lloydstsb.schema.personal.customer.partyidandv.serviceobjects.ReferralReason;
import lloydstsb.schema.personal.customer.partyidandv.serviceobjects.ReferralReasons;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class TestDataHelper {
    public static final String TEST_RETAIL_CHANNEL_ID = "LTB";
    public static final Short DEFAULT_PARTY_EXT_SYS_ID = 2;
    public static final Short PARTY_EXT_SYS_ID = 1;
    public static final long ARRANGEMENT_ID = 1L;
    private static final short EXTERNAL_SYS_ID = 19;
    private static final String CLOSURE_PERIOD_MONTHS_DR = "000";
    private static final String CLOSED_ONLY_IN = "0";
    private static final String CUSTOMER_CONSENT_IN = "2";
    private static final String TELEPHONE_TYPE_PERSONAL = "1";
    private static final int PARTY_PHONE_TYPE_PERSONAL = 1;
    private static final String TELEPHONE_TYPE_OFFICIAL = "4";
    private static final int PARTY_PHONE_TYPE_OFFICIAL = 3;
    private static final int PARTY_PHONE_TYPE_OTHER = 5;

    ProductTypes productTypes = new ProductTypes("102", "Current Account");
    ProductTypes productTypes1 = new ProductTypes("101", "Saving Account");


    Brands brands = new Brands("LTB");
    Brands brands1 = new Brands("IBL");
    UserTypes userTypes = new UserTypes("1001", "Customer");

    ApplicationStatus applicationStatus = new ApplicationStatus("1001", "Initialised");
    ApplicationStatus applicationStatus1 = new ApplicationStatus("1002", "Approved");
    ApplicationStatus applicationStatus2 = new ApplicationStatus("1003", "Referred");
    ApplicationStatus applicationStatus3 = new ApplicationStatus("1004", "Declined");
    ApplicationStatus applicationStatus4 = new ApplicationStatus("1005", "Not Scored");
    ApplicationStatus applicationStatus5 = new ApplicationStatus("1014", "Not Scored");

    ApplicationTypes applicationTypes = new ApplicationTypes("10001", "New Application");
    ApplicationTypes applicationTypes1 = new ApplicationTypes("10003", "Joint Application");

    PromotionChannels promotionChannelsIB = new PromotionChannels("001", "Internet Banking");
    PromotionChannels promotionChannels = new PromotionChannels("003", "Direct Mail");

    Channels channels = new Channels("004", "Internet");
    Channels channels1 = new Channels("005", "Marketing Pref (Phone)");
    Channels channels2 = new Channels("006", "Marketing Pref (Mail)");
    Channels channels3 = new Channels("007", "Marketing Pref (Text)");
    Channels channels4 = new Channels("008", "Marketing Pref (Email)");

    ApplicationFeatureTypes applicationFeatureTypes = new ApplicationFeatureTypes("BENPA");
    ApplicationFeatureTypes applicationFeatureTypes1 = new ApplicationFeatureTypes("CC");
    ApplicationFeatureTypes applicationFeatureTypes2 = new ApplicationFeatureTypes("R85");
    AbandonDeclineReasons abandonDeclineReasons = new AbandonDeclineReasons("102", "ASM decline. Based on credit scoring");
    AbandonDeclineReasons abandonDeclineReasons1 = new AbandonDeclineReasons("101", "EIDV decline. Failed identification");


    Demographics demographics = new Demographics("1001");
    DemographicValues demographicValues = new DemographicValues(1L, demographics);
    TelephoneAddressTypes telephoneAddressTypes = new TelephoneAddressTypes("7");

    KycStatus status = new KycStatus();
    KycStatus status1 = new KycStatus("N/A");
    KycStatus status2 = new KycStatus("1000");
    KycStatus status3 = new KycStatus("1001");
    KycStatus status4 = new KycStatus("ACCEPT");
    KycStatus status5 = new KycStatus("REFER");
    KycStatus status6 = new KycStatus("DECLINE");

    ProductPackageTypes productPackageTypes = new ProductPackageTypes("2004");
    ApprovalStatus approvalStatus = new ApprovalStatus("001");


    ParameterGroups parameterGroups = new ParameterGroups("100008", "OCIS Party Identifier");
    ParameterGroups parameterGroups1 = new ParameterGroups("CBS", "CBS Param Group");
    ParameterGroups parameterGroups2 = new ParameterGroups("ASM", "ASM Param Group");
    ParameterGroups parameterGroups3 = new ParameterGroups("CMAS", "CMAS Param Group");
    ParameterGroups parameterGroups4 = new ParameterGroups("PEGA", "PEGA Param Group");
    ParameterGroups parameterGroups5 = new ParameterGroups("CCD", "Call Credit Param Group");
    ParameterGroups parameterGroups6 = new ParameterGroups("EIDV", "EIDV Param Group");
    ParameterGroups parameterGroups7 = new ParameterGroups("IB", "Internet Banking");
    ParameterGroups parameterGroups8 = new ParameterGroups("OCIS", "OCIS Param Group");
    ParameterGroups parameterGroups9 = new ParameterGroups("OCIS", "Product Preferential Rate Identifier");

    //ApplicationParameters applicationParameters = new ApplicationParameters("100008", parameterGroups);
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
    ApplicationParameters applicationParameters12 = new ApplicationParameters("100041", parameterGroups2, "Gauranteed Offer Code");
    ApplicationParameters applicationParameters13 = new ApplicationParameters("100039", parameterGroups1, "Auth Customer");
    ApplicationParameters applicationParameters14 = new ApplicationParameters("100045", parameterGroups2, "Credit Limit Amount");
    ApplicationParameters applicationParameters15 = new ApplicationParameters("100007", parameterGroups2, "ASM REFER");
    ApplicationParameters applicationParameters16 = new ApplicationParameters("100001", parameterGroups2, "ASM decline code");
    ApplicationParameters applicationParameters17 = new ApplicationParameters("100004", parameterGroups2, "ASM decline code");
    ApplicationParameters applicationParameters18 = new ApplicationParameters("100016", parameterGroups2, "Not sure");
    ApplicationParameters applicationParameters19 = new ApplicationParameters("100047", parameterGroups9, "PPRID");

    ApplicationPartyRoles roles1 = new ApplicationPartyRoles("0001", "Key Party");
    ApplicationPartyRoles roles2 = new ApplicationPartyRoles("0002", "Additional Card Holder");
    ApplicationPartyRoles roles3 = new ApplicationPartyRoles("0003", "Promotional Party Affiliate");

    PartyRelationshipRoles partyRelationshipRoles = new PartyRelationshipRoles("20001");

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
    ApplicationPartyRolesDao applicationPartyRolesDao;
    @Autowired
    TelephoneAddressTypesDao telephoneAddressTypesDao;
    @Autowired
    ParameterGroupsDao parameterGroupsDao;
    @Autowired
    ApplicationParametersDao applicationParametersDao;
    @Autowired
    ApprovalStatusDao approvalStatusDao;
    @Autowired
    PromotionPartiesDao promotionPartiesDao;
    @Autowired
    CountryRolesDao countryRolesDao;
    @Autowired
    EvaluateAddressUpdData evaluateAddressUpdData;
    @Autowired
    EvaluatePersonalUpdData evaluatePersonalUpdData;
    @Autowired
    PhoneUpdDataFactory phoneUpdDataFactory;
    @Autowired
    EmployerAddressDataFactory employerAddressDataFactory;
    @Autowired
    StructuredAddressFactory structuredAddressFactory;
    @Autowired
    PromoPartyApplicationsDao promoPartyApplicationsDao;
    @Autowired
    IndividualsDao individualsDao;
    @Autowired
    ApplicationsDao applicationsDao;
    @Autowired
    PartyApplicationsDao partyApplicationsDao;
    @Autowired
    KycStatusDao kycStatusDao;
    @Autowired
    StreetAddressesDao streetAddressesDao;
    @Autowired
    AbandonDeclineReasonDao abandonDeclineReasonsDao;
    @Autowired
    ProductPackageTypesDao productPackageTypesDao;
    @Autowired
    PartyCountryAssociationsDao partyCountryAssociationsDao;
    @Autowired
    ApplicationRelationshipTypesDao applicationRelationshipTypesDao;
    @Autowired
    PartyRelationshipRolesDao partyRelationshipRolesDao;


    public static void main(String[] args) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
    }

    public RequestHeader createOpaSavingRequestHeader(String channelId) {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementSaving")
                .channelId(channelId)
                .interactionId("vbww2yofqtcx1qbzw8iz4gm19")
                .serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...")
                .contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer")
                .bapiInformation(channelId, "interactionId", "AAGATEWAY", "ns4")
                .securityHeader("ns4", "UNAUTHSALE")
                .build();
        header.setContactPointId("0000777505");
        return header;
    }

    public OfferProductArrangementRequest generateOfferProductArrangementSavingRequest(String channelId) {
        RequestHeader header = createOpaSavingRequestHeader(channelId);
        OfferProductArrangementRequest request = new OpaSavingRequestBuilder().requestHeader(header).depositArrangement(createDepositArrangement()).build();

        return request;
    }

    public OfferProductArrangementRequest generateOfferProductArrangementSavingRequest(String channelId, String emailAddr) {
        RequestHeader header = createOpaSavingRequestHeader(channelId);
        OfferProductArrangementRequest request = new OpaSavingRequestBuilder().requestHeader(header).depositArrangement(createDepositArrangement(emailAddr)).build();

        return request;
    }

    public DepositArrangement createDepositArrangement() {
        DepositArrangement depositArrangement = new DepositArrangementBuilder().arrangementType()
                .associatedProduct(createAssociatedProduct())
                .initiatedThrough(createChannel())
                .primaryInvolvedParty(new InvolvedPartyBuilder().partyIdentifier("AAGATEWAY")
                        .emailAddress("a@a.com")
                        .postalAddress(createPostalAddressList())
                        .telephoneNumber(createTelephoneNumber())
                        .isPlayedBy(createIsPlayedBy())
                        .userType("1001")
                        .internalUserIdentifier("127.0.0.1")
                        .partyRole("0001")
                        .customerSegment("3")
                        .otherBankDuration("0000")
                        .build())
                .marketingPreferenceBySMS(true)
                .applicationType("10001")
                .accountPurpose("BENPA")
                .fundingSource("1")
                .conditions(createRuleConditionList())
                .marketingPreferenceByEmail(false)
                .marketingPreferenceByPhone(false)
                .marketingPreferenceByMail(false)
                .build();
        //depositArrangement.setArrangementId(String.valueOf(ARRANGEMENT_ID));
        return depositArrangement;
    }

    public DepositArrangement createDepositArrangement(String emailAddr) {
        DepositArrangement depositArrangement = new DepositArrangementBuilder().arrangementType()
                .associatedProduct(createAssociatedProduct())
                .initiatedThrough(createChannel())
                .primaryInvolvedParty(new InvolvedPartyBuilder().partyIdentifier("AAGATEWAY")
                        .emailAddress(emailAddr)
                        .postalAddress(createPostalAddressList())
                        .telephoneNumber(createTelephoneNumber())
                        .isPlayedBy(createIsPlayedBy())
                        .userType("1001")
                        .internalUserIdentifier("127.0.0.1")
                        .partyRole("0001")
                        .customerSegment("3")
                        .otherBankDuration("0000")
                        .build())
                .marketingPreferenceBySMS(true)
                .applicationType("10001")
                .accountPurpose("BENPA")
                .fundingSource("1")
                .conditions(createRuleConditionList())
                .marketingPreferenceByEmail(false)
                .marketingPreferenceByPhone(false)
                .marketingPreferenceByMail(false)
                .build();
        //depositArrangement.setArrangementId(String.valueOf(ARRANGEMENT_ID));
        return depositArrangement;
    }

    public Customer createPrimaryInvolvedParty() {
        return new InvolvedPartyBuilder().partyIdentifier("AAGATEWAY")
                .emailAddress("a@a.com")
                .postalAddress(createPostalAddressList())
                .telephoneNumber(createTelephoneNumber())
                .isPlayedBy(createIsPlayedBy())
                .userType("1001")
                .applicationType("01")
                .internalUserIdentifier("127.0.0.1")
                .partyRole("0001")
                .customerSegment("3")
                .otherBankDuration("0000")
                .build();
    }

    private Product createAssociatedProduct() {
        Product associatedProduct = new AssociatedProductBuilder().productIdentifier("20198")
                .externalSystemProductIdentifier(createExtSysProdId())
                .instructionDetails(createInstructionDetails())
                .productName("Account B")
                .productOptions(createProductOptions())
                .build();
        associatedProduct.setBrandName("LTB");
        return associatedProduct;
    }

    private InstructionDetails createInstructionDetails() {
        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("P_CLUB");
        return instructionDetails;
    }

    public Individual createIsPlayedBy() {
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        Individual isPlayedBy = new IndividualBuilder().individualName(createIndividualName())
                .residentialStatus("002")
                .birthDate(datatypeFactory.newXMLGregorianCalendar("1930-03-03T06:40:56.046Z"))
                .nationality("GBR")
                .countryOfBirth("UK")
                .numberOfDependents(new BigInteger("3"))
                .maritalStatus("001")
                .gender("001")
                .employmentStatus("006")
                .currentEmploymentDuration("0707")
                .totalSavingsAmount(createCurrencyAmount())
                .netMonthlyIncome(createCurrencyAmount())
                .monthlyLoanRepaymentAmount(createCurrencyAmount())
                .monthlyMortgageAmount(createCurrencyAmount())
                .otherMonthlyIncomeAmount(createCurrencyAmount())
                .build();
        return isPlayedBy;
    }

    public Individual createIsPlayedByWithoutStayDuration() {
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        Individual isPlayedBy = new IndividualBuilder().individualName(createIndividualName())
                .residentialStatus("002")
                .birthDate(datatypeFactory.newXMLGregorianCalendar("1930-03-03T06:40:56.046Z"))
                .nationality("GBR")
                .countryOfBirth("UK")
                .numberOfDependents(new BigInteger("3"))
                .maritalStatus("001")
                .gender("001")
                .employmentStatus("006")
                .currentEmploymentDuration("0707")
                .totalSavingsAmount(createCurrencyAmount())
                .netMonthlyIncome(createCurrencyAmount())
                .monthlyLoanRepaymentAmount(createCurrencyAmount())
                .monthlyMortgageAmount(createCurrencyAmount())
                .otherMonthlyIncomeAmount(createCurrencyAmount())
                .build();
        return isPlayedBy;
    }

    private List<RuleCondition> createRuleConditionList() {
        RuleCondition condition = new RuleCondition();
        condition.setName("EIDV_REFERRAL_DISABLED_SWITCH");
        condition.setResult("DISABLED");
        List<RuleCondition> conditionList = new ArrayList<RuleCondition>();
        conditionList.add(0, condition);
        return conditionList;
    }

    public List<AffiliateDetails> createAffiliateDetailsList() {
        AffiliateDetails affiliateDetails = new AffiliateDetails();
        affiliateDetails.setAffiliateIdentifier("727");
        List<AffiliateDetails> affiliateDetailsList = new ArrayList<AffiliateDetails>();
        affiliateDetailsList.add(0, affiliateDetails);
        return affiliateDetailsList;
    }

    private CurrencyAmount createCurrencyAmount() {
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal(300));
        return currencyAmount;
    }

    public List<TelephoneNumber> createTelephoneNumber() {
        TelephoneNumber telephoneNumber = new TelephoneNumberBuilder().countryPhoneCode("44").phoneNumber("7440696125").telephoneType("7").deviceType("Mobile").build();
        List<TelephoneNumber> telephoneNumberList = new ArrayList<TelephoneNumber>();
        telephoneNumberList.add(0, telephoneNumber);
        return telephoneNumberList;
    }

    public List<PostalAddress> createPostalAddressList() {
        PostalAddress postalAddress = new PostalAddressBuilder().durationOfStay("0707")
                .isBFPOAddressBuilder(false)
                .isPAFFormat(true)
                .statusCode("CURRENT")
                .structuredAddress(createStructuredAddress())
                .build();
        List<PostalAddress> postalAddressList = new ArrayList<>();
        postalAddressList.add(0, postalAddress);
        return postalAddressList;
    }

    private StructuredAddress createStructuredAddress() {
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("23");
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

    private Channel createChannel() {
        Channel initiatedThrough = new Channel();
        initiatedThrough.setChannelCode("004");
        initiatedThrough.setSubChannelCode("003");
        return initiatedThrough;
    }

    private List<ProductOptions> createProductOptions() {
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("UP_SELL_DISPLAY_VALUE");
        productOptions.setOptionsValue("0");
        List<ProductOptions> productOptionsList = new ArrayList<ProductOptions>();
        productOptionsList.add(0, productOptions);
        return productOptionsList;
    }

    public List<ExtSysProdIdentifier> createExtSysProdId() {
        ExtSysProdIdentifier extSysProdIdentifierASM = new ExtSysProdIdentifier();
        extSysProdIdentifierASM.setProductIdentifier("901");
        extSysProdIdentifierASM.setSystemCode("00107");
        ExtSysProdIdentifier extSysProdIdentifierCAAS = new ExtSysProdIdentifier();
        extSysProdIdentifierCAAS.setProductIdentifier("201");
        extSysProdIdentifierCAAS.setSystemCode("10107");
        List<ExtSysProdIdentifier> extSysProdIdentifierList = new ArrayList<ExtSysProdIdentifier>();
        extSysProdIdentifierList.add(0, extSysProdIdentifierASM);
        extSysProdIdentifierList.add(1, extSysProdIdentifierCAAS);
        return extSysProdIdentifierList;
    }

    public List<IndividualName> createIndividualName() {
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

    public F447Resp createF447Response(int reasonCode) {

        F447Resp response = new F447Resp();
        response.setF447Result(new F447Result());
        response.getF447Result().setResultCondition(new ResultCondition());
        response.getF447Result().getResultCondition().setReasonCode(reasonCode);
        response.getF447Result().getResultCondition().setReasonText("abc");
        response.setPartyId(12345L);
        response.setCIDPersId("1234");
        return response;
    }

    public F447Resp createF447ResponseForNewCustomer(int reasonCode) {

        F447Resp response = new F447Resp();
        response.setF447Result(new F447Result());
        response.getF447Result().setResultCondition(new ResultCondition());
        response.getF447Result().getResultCondition().setReasonCode(reasonCode);
        response.getF447Result().getResultCondition().setReasonText("abc");
        response.setPartyId(0);
        return response;
    }

    public F336Resp createF336Response(int productOneGroupId, int productTwoGroupId) {
        F336Resp f336Resp = new F336Resp();
        f336Resp.setAdditionalDataIn(0);
        f336Resp.setF336Result(createF336Result());
        List<ProductPartyData> productPartyData = new ArrayList<>();
        ProductPartyData productPartyData1 = new ProductPartyData();
        productPartyData1.setProdGroupId(productOneGroupId);
        productPartyData1.setSellerLegalEntCd("LTB");
        productPartyData1.setAmdEffDt("05052015");
        productPartyData1.setProductHeldOpenDt("05052015");
        ProductPartyData productPartyData2 = new ProductPartyData();
        productPartyData2.setProdGroupId(productTwoGroupId);
        productPartyData2.setSellerLegalEntCd("LTB");
        productPartyData2.setAmdEffDt("05052015");
        productPartyData2.setProductHeldOpenDt("05052015");
        productPartyData.add(productPartyData1);
        productPartyData.add(productPartyData2);
        f336Resp.getProductPartyData().addAll(productPartyData);
        return f336Resp;
    }

    public F336Resp createF336Response2(int productOneGroupId, int productTwoGroupId) {
        F336Resp f336Resp = new F336Resp();
        f336Resp.setAdditionalDataIn(0);
        f336Resp.setF336Result(createF336Result());
        return f336Resp;
    }

    private F336Result createF336Result() {
        F336Result f336Result = new F336Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(0);
        resultCondition.setExtraConditions(new ExtraConditions());
        resultCondition.setReasonText("success");
        resultCondition.setSeverityCode((byte) 0);
        resultCondition.setReasonDetail(new ReasonDetail());
        f336Result.setResultCondition(resultCondition);
        return f336Result;
    }

    public F336Resp createF336ResponseWithExternalServiceError() {
        F336Resp f336Resp = new F336Resp();
        F336Result f336Result = new F336Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(1);
        resultCondition.setExtraConditions(new ExtraConditions());
        resultCondition.setReasonText("failure");
        resultCondition.setSeverityCode((byte) 1);
        resultCondition.setReasonDetail(new ReasonDetail());
        f336Result.setResultCondition(resultCondition);
        f336Resp.setF336Result(f336Result);
        return f336Resp;
    }

    public F336Req createF336Request(BapiInformation bapiInformation, String partyIdentifier) {
        String host = bapiInformation.getBAPIHeader().getStpartyObo().getHost();
        F336Req request = new F336Req();
        request.setExtSysId(EXTERNAL_SYS_ID);
        request.setExtPartyIdTx(partyIdentifier);
        request.setPartyId(null != partyIdentifier ? Long.valueOf(partyIdentifier) : 0l);
        request.setPartyExtSysId((null != host && host.endsWith("L")) ? (short) 1 : (short) 2);
        request.setClosurePeriodMonthsDr(CLOSURE_PERIOD_MONTHS_DR);
        request.setClosedOnlyIn(CLOSED_ONLY_IN);
        request.setCustomerConsentIn(CUSTOMER_CONSENT_IN);
        return request;
    }


    public F061Resp createF061Resp(int reasonCode) {

        F061Resp response = new F061Resp();
        response.setF061Result(new F061Result());
        response.getF061Result().setResultCondition(new ResultCondition());
        response.getF061Result().getResultCondition().setReasonCode(reasonCode);
        response.getF061Result().getResultCondition().setReasonText("reasonText");

        response.setPartyEnqData(new PartyEnqData());
        response.getPartyEnqData().setAddressData(new AddressData());
        response.getPartyEnqData().getAddressData().setAddressStatusCd("001");
        response.getPartyEnqData().getAddressData().setAddressTypeCd("001");
        response.getPartyEnqData().getAddressData().setAmdEffDt("01012015");

        com.lloydsbanking.salsa.soap.ocis.f061.objects.StructuredAddress structuredAddress = new com.lloydsbanking.salsa.soap.ocis.f061.objects.StructuredAddress();
        structuredAddress.setBuildingNo("23");
        structuredAddress.setInPostCd("9EQ");
        structuredAddress.setOutPostCd("SE1");
        structuredAddress.setAddressPostTownNm("London");
        structuredAddress.setDelivPointSuffixCd("1E");
        response.getPartyEnqData().getAddressData().setStructuredAddress(structuredAddress);

        response.getPartyEnqData().setEvidenceData(new EvidenceData());
        PartyEvid partyEvid = new PartyEvid();
        partyEvid.setPartyEvidPurposeCd("1");
        PartyEvidAuditData partyEvidAuditData = new PartyEvidAuditData();
        partyEvidAuditData.setAuditDt("12345");
        partyEvidAuditData.setAuditTm("12");
        partyEvid.setPartyEvidAuditData(partyEvidAuditData);
        AddrEvid addrEvid = new AddrEvid();
        addrEvid.setAddrEvidPurposeCd("2");
        AddrEvidAuditData addrEvidAuditData = new AddrEvidAuditData();
        addrEvidAuditData.setAuditDt("12345");
        addrEvidAuditData.setAuditTm("12");
        addrEvid.setAddrEvidAuditData(addrEvidAuditData);
        response.getPartyEnqData().getEvidenceData().getPartyEvid().add(partyEvid);
        response.getPartyEnqData().getEvidenceData().getAddrEvid().add(addrEvid);

        return response;
    }

    public F447Req createF447Request(List<PostalAddress> postalAddresses, Individual isPlayedBy) {
        try {
            ExceptionUtility exceptionUtility = new ExceptionUtility();
            return new EnquirePartyIdRequestFactory().convert(postalAddresses, isPlayedBy, exceptionUtility);
        } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            return null;
        }
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

    public F061Resp createF061RespWithoutEvidenceData(int reasonCode) {
        F061Resp response = new F061Resp();
        response.setF061Result(new F061Result());
        response.getF061Result().setResultCondition(new ResultCondition());
        response.getF061Result().getResultCondition().setReasonCode(reasonCode);
        response.getF061Result().getResultCondition().setReasonText("reasonText");

        response.setPartyEnqData(new PartyEnqData());
        response.getPartyEnqData().setAddressData(new AddressData());
        response.getPartyEnqData().getAddressData().setAddressStatusCd("001");
        response.getPartyEnqData().getAddressData().setAddressTypeCd("001");
        response.getPartyEnqData().getAddressData().setAmdEffDt("01012015");
        //response.getPartyEnqData().getAddressData().setUnstructuredAddress(new com.lloydsbanking.salsa.soap.ocis.f061.objects.UnstructuredAddress());
        response.getPartyEnqData().setEvidenceData(new EvidenceData());
        com.lloydsbanking.salsa.soap.ocis.f061.objects.StructuredAddress structuredAddress = new com.lloydsbanking.salsa.soap.ocis.f061.objects.StructuredAddress();
        structuredAddress.setBuildingNo("23");
        structuredAddress.setInPostCd("9EQ");
        structuredAddress.setOutPostCd("SE1");
        structuredAddress.setAddressPostTownNm("London");
        structuredAddress.setDelivPointSuffixCd("1E");
        structuredAddress.getAddressLinePaf().add(new AddressLinePaf());
        structuredAddress.getAddressLinePaf().get(0).setAddressLinePafTx("PARK STREET");
        response.getPartyEnqData().getAddressData().setStructuredAddress(structuredAddress);

        response.getPartyEnqData().setEvidenceData(new EvidenceData());

        return response;

    }

    public F204Resp createF204Response2(String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        F204Resp f204Resp = new F204Resp();
        f204Resp.setF204Result(new F204Result());
        f204Resp.getF204Result().setResultCondition(new ResultCondition());
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

    public List<ReferralCode> createReferralCodeList(String code, String description) {
        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode(code);
        referralCode.setDescription(description);
        referralCodeList.add(referralCode);
        return referralCodeList;
    }

    public F062Req createF062Req(String arrangementType, Customer primaryInvolvedParty, RequestHeader header, boolean marketingPref, String addressStrength, String identityStrength) throws OfferProductArrangementInternalServiceErrorMsg, ParseException, OfferProductArrangementDataNotAvailableErrorMsg {
        return convert(arrangementType, primaryInvolvedParty, header, addressStrength, identityStrength, marketingPref);
    }

    public F062Resp createF062Response(int errorNo) {
        F062Resp resp = new F062Resp();
        resp.setF062Result(new F062Result());
        resp.getF062Result().setResultCondition(new ResultCondition());
        resp.getF062Result().getResultCondition().setReasonCode(errorNo);
        resp.setPartyId(345l);
        resp.setCIDPersId("cidPersId");
        resp.setAddressAuditUpdData(createAddressAuditUpdData());
        resp.setEvidAuditUpdData(createEvidAuditUpdData());
        return resp;
    }

    private EvidAuditUpdDataType createEvidAuditUpdData() {

        EvidAuditUpdDataType evidAuditUpdDataType = new EvidAuditUpdDataType();
        evidAuditUpdDataType.getAddrEvidUpdData().add(createAddressEvidUpdData());

        return evidAuditUpdDataType;
    }

    private AddrEvidUpdData1Type createAddressEvidUpdData() {
        AddrEvidUpdData1Type addrEvidUpdData1Type = new AddrEvidUpdData1Type();
        addrEvidUpdData1Type.setAuditDt("12345678");
        addrEvidUpdData1Type.setAuditTm("123456");
        return addrEvidUpdData1Type;
    }

    private AddressAuditUpdData1Type createAddressAuditUpdData() {

        AddressAuditUpdData1Type addressAuditUpdData1Type = new AddressAuditUpdData1Type();
        addressAuditUpdData1Type.setAuditDt(01012015l);
        addressAuditUpdData1Type.setAuditTm(345678l);
        return addressAuditUpdData1Type;
    }

    public void createPamReferenceData() {
        productTypesDao.save(productTypes);
        productTypesDao.save(productTypes1);
        productTypesDao.findAll();

        brandsDao.save(brands);
        brandsDao.save(brands1);
        brandsDao.findAll();
        userTypesDao.save(userTypes);
        userTypesDao.findAll();
        appStatusDao.save(applicationStatus);
        appStatusDao.save(applicationStatus1);
        appStatusDao.save(applicationStatus2);
        appStatusDao.save(applicationStatus3);
        appStatusDao.save(applicationStatus4);
        appStatusDao.save(applicationStatus5);
        appStatusDao.findAll();
        applicationTypesDao.save(applicationTypes);
        applicationTypesDao.save(applicationTypes1);
        applicationTypesDao.findAll();
        promotionChannelsDao.save(promotionChannelsIB);
        promotionChannelsDao.save(promotionChannels);
        promotionChannelsDao.findAll();
        channelsDao.save(channels);
        channelsDao.save(channels1);
        channelsDao.save(channels2);
        channelsDao.save(channels3);
        channelsDao.save(channels4);
        channelsDao.findAll();
        kycStatusDao.save(status1);
        kycStatusDao.save(status2);
        kycStatusDao.save(status3);
        kycStatusDao.save(status4);
        kycStatusDao.save(status5);
        kycStatusDao.save(status6);
        kycStatusDao.findAll();
        applicationFeatureTypesDao.save(applicationFeatureTypes);
        applicationFeatureTypesDao.save(applicationFeatureTypes1);
        applicationFeatureTypesDao.save(applicationFeatureTypes2);
        applicationPartyRolesDao.save(createPartyRoleList());
        applicationPartyRolesDao.findAll();
        abandonDeclineReasonsDao.save(abandonDeclineReasons);
        abandonDeclineReasonsDao.save(abandonDeclineReasons1);

        abandonDeclineReasonsDao.findAll();
        demographicsDao.save(demographics);
        demographicsDao.findAll();
        demographicValuesDao.save(demographicValues);
        demographicValuesDao.findAll();
        parameterGroupsDao.save(parameterGroups1);
        parameterGroupsDao.save(parameterGroups2);
        parameterGroupsDao.save(parameterGroups8);
        parameterGroupsDao.save(parameterGroups7);
        parameterGroupsDao.save(parameterGroups9);
        parameterGroupsDao.findAll();
        //applicationParametersDao.save(applicationParameters);
        applicationParametersDao.save(applicationParameters1);
        applicationParametersDao.save(applicationParameters7);
        applicationParametersDao.save(applicationParameters9);
        applicationParametersDao.save(applicationParameters11);
        applicationParametersDao.save(applicationParameters12);
        applicationParametersDao.save(applicationParameters13);
        applicationParametersDao.save(applicationParameters14);
        applicationParametersDao.save(applicationParameters15);
        applicationParametersDao.save(applicationParameters16);
        applicationParametersDao.save(applicationParameters18);
        applicationParametersDao.save(applicationParameters19);
        applicationParametersDao.findAll();
        telephoneAddressTypesDao.save(telephoneAddressTypes);
        telephoneAddressTypesDao.findAll();
        productPackageTypesDao.save(productPackageTypes);
        approvalStatusDao.save(approvalStatus);
        approvalStatusDao.findAll();
        promotionPartiesDao.save(createPromotionPartyList());


        CountryRoles roles = new CountryRoles("0001");
        CountryRoles roles1 = new CountryRoles("0002");
        CountryRoles roles2 = new CountryRoles("0003");
        countryRolesDao.save(roles);
        countryRolesDao.save(roles1);
        countryRolesDao.save(roles2);
        countryRolesDao.findAll();

        ApplicationRelationshipTypes applicationRelationshipTypes = new ApplicationRelationshipTypes();
        applicationRelationshipTypes.setCode("20001");
        applicationRelationshipTypes.setDescription("Crosssell");

        applicationRelationshipTypesDao.save(applicationRelationshipTypes);

        applicationFeatureTypesDao.findAll();
        partyRelationshipRolesDao.save(partyRelationshipRoles);


    }

    private List<PromotionParties> createPromotionPartyList() {
        List<PromotionParties> promotionPartiesList = new ArrayList<>();
        PromotionParties promotionParties = new PromotionParties();
        promotionParties.setId(26l);
        promotionParties.setAffiliateId("727");
        ApprovalStatus approvalStatus = new ApprovalStatus("001");
        promotionParties.setApprovalStatus(approvalStatus);
        promotionParties.setEnabled("Y");
        promotionParties.setIsCreditIntermediary("Y");
        promotionParties.setIsNetwork('N');
        promotionParties.setName("Freemax Media");
        promotionParties.setLockId(0l);
        promotionPartiesList.add(promotionParties);
        return promotionPartiesList;
    }

    private List<ApplicationPartyRoles> createPartyRoleList() {
        List<ApplicationPartyRoles> applicationPartyRolesList = new ArrayList();
        ApplicationPartyRoles applicationPartyRoles = new ApplicationPartyRoles();
        applicationPartyRoles.setCode("0001");
        applicationPartyRoles.setDescription("Key Party");
        applicationPartyRolesList.add(applicationPartyRoles);
        ApplicationPartyRoles applicationPartyRoles2 = new ApplicationPartyRoles();
        applicationPartyRoles2.setCode("0003");
        applicationPartyRoles2.setDescription("Promotional Party Affiliate");
        ApplicationPartyRoles applicationPartyRoles3 = new ApplicationPartyRoles();
        applicationPartyRoles3.setCode("0005");
        applicationPartyRoles3.setDescription("Secondary Party");


        applicationPartyRolesList.add(applicationPartyRoles2);
        applicationPartyRolesList.add(applicationPartyRoles3);
        return applicationPartyRolesList;
    }

    /*public RetrieveProductConditionsRequest rpcRequest(RequestHeader header) {

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

    public RetrieveProductConditionsResponse rpcResponse() {
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
        offeredProducts.getProduct().add(product2);

        return offeredProducts;
    }

    public RetrieveProductConditionsResponse rpcResponseForDownsell() {
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
        offeredProducts.getProduct().add(product2);

        return offeredProducts;
    }

    public RetrieveProductConditionsResponse rpcResponseForUpsell() {
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

    public RetrieveProductConditionsResponse rpcResponseForNormal() {
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
    }*/

    public F062Req convert(String arrangementType, Customer primaryInvolvedParty, RequestHeader header, String addressStrength, String identityStrength, boolean marketingPref) throws ParseException {
        F062RequestBuilder requestBuilder = new F062RequestBuilder();
        F062Req request = requestBuilder.defaults().build();
        if (null != primaryInvolvedParty.getCustomerIdentifier() && !"".equals(primaryInvolvedParty.getCustomerIdentifier())) {
            request.setPartyId(Long.valueOf(primaryInvolvedParty.getCustomerIdentifier()));
        }
        PartyNonCoreUpdDataFactory partyNonCoreUpdDataFactory = new PartyNonCoreUpdDataFactory();
        request.setPartyUpdData(new PartyUpdDataType());
        request.getPartyUpdData().setPersonalUpdData(evaluatePersonalUpdData.generatePersonalUpdData(arrangementType, primaryInvolvedParty, marketingPref));
        request.getPartyUpdData().setPartyNonCoreUpdData(partyNonCoreUpdDataFactory.generatePartyNonCoreUpdData(primaryInvolvedParty.getIsPlayedBy()));
        AddressUpdDataType addressUpdDataType = new AddressUpdDataType();
        evaluateAddressUpdData.generateAddressUpdData(primaryInvolvedParty.getPostalAddress(), addressUpdDataType);
        if (!CollectionUtils.isEmpty(primaryInvolvedParty.getPostalAddress())) {
            if (null != primaryInvolvedParty.getPostalAddress().get(0).getUnstructuredAddress()) {
                addressUpdDataType.setUnstructuredAddress(new UnstructuredAddressFactory().generateUnstructuredAddress(primaryInvolvedParty.getPostalAddress().get(0)
                        .getUnstructuredAddress(), primaryInvolvedParty.getPostalAddress().get(0).isIsBFPOAddress()));
            }
            if (null != primaryInvolvedParty.getPostalAddress().get(0).getStructuredAddress()) {
                addressUpdDataType.setStructuredAddress(new StructuredAddressFactory().generateStructuredAddress(primaryInvolvedParty.getPostalAddress().get(0).getStructuredAddress()));
            }
        }
        request.getPartyUpdData().setAddressUpdData(addressUpdDataType);

        request.getPartyUpdData().getPhoneUpdData().addAll(phoneUpdDataFactory.generatePhoneUpdData(primaryInvolvedParty.getTelephoneNumber()));

        if (null != primaryInvolvedParty.getIsPlayedBy().getCurrentEmployer()) {
            if (null != primaryInvolvedParty.getIsPlayedBy().getCurrentEmployer().getName()) {
                request.getPartyUpdData().setKYCNonCorePartyUpdData(new KYCNonCorePartyUpdDataType());
                request.getPartyUpdData().getKYCNonCorePartyUpdData().setEmployerNm(primaryInvolvedParty.getIsPlayedBy().getCurrentEmployer().getName());
            }
            if (!primaryInvolvedParty.getIsPlayedBy()
                    .getCurrentEmployer()
                    .getHasPostalAddress().isEmpty()) {
                request.getPartyUpdData()
                        .setEmployersAddrUpdData(employerAddressDataFactory.generateEmployerAddress(primaryInvolvedParty.getIsPlayedBy()
                                        .getCurrentEmployer()
                                        .getHasPostalAddress()
                                        .get(0)
                                        .getUnstructuredAddress())
                        );
            }
        }
        if (null != addressStrength && null != identityStrength) {
            request.getPartyUpdData().setEvidenceUpdData(new EvidenceUpdDataType());
            request.getPartyUpdData().getEvidenceUpdData().getPartyEvidUpdData().add(getPartyEvidenceData(identityStrength));
            request.getPartyUpdData().getEvidenceUpdData().getAddrEvidUpdData().add(getAddrEvidenceData(addressStrength));
        }
        if (null != primaryInvolvedParty.getIsPlayedBy().getNationality()) {
            request.getPartyUpdData().setKYCPartyUpdData(new KYCPartyUpdDataType());
            request.getPartyUpdData().getKYCPartyUpdData().setFrstNtn(new FrstNtnType());
            request.getPartyUpdData().getKYCPartyUpdData().getFrstNtn().setFirstNationltyCd(primaryInvolvedParty.getIsPlayedBy().getNationality());
        }
        return request;
    }

    private AddrEvidUpdDataType getAddrEvidenceData(String addressStrength) {
        AddrEvidUpdDataType addrEvidUpdDataType = new AddrEvidUpdDataType();
        addrEvidUpdDataType.setAddrEvidPurposeCd(addressStrength.split(":")[0]);
        addrEvidUpdDataType.setAddrEvidTypeCd(addressStrength.split(":")[1]);
        return addrEvidUpdDataType;
    }

    private PartyEvidUpdDataType getPartyEvidenceData(String identityStrength) {
        PartyEvidUpdDataType partyEvidUpdDataType = new PartyEvidUpdDataType();
        partyEvidUpdDataType.setPartyEvidPurposeCd(identityStrength.split(":")[0]);
        partyEvidUpdDataType.setPartyEvidTypeCd(identityStrength.split(":")[1]);
        return partyEvidUpdDataType;
    }

    public HashMap<String, Long> createApplicationWithStatusAndProduct(boolean isDuplicate, String appStatus, String appDescription, String productId, String productName) {
        HashMap<String, Long> applicationsBO = new HashMap<>();

        ApplicationStatus applicationStatus = new ApplicationStatus(appStatus, appDescription);
        appStatusDao.save(applicationStatus);
        appStatusDao.findAll();


        status.setCode("DECLINE");
        status.setDescription("DECLINE");
        kycStatusDao.save(status);
        kycStatusDao.findAll();

        Applications applications = new Applications(applicationTypes, productTypes1, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        applications.setSortCode("773315");
        applications.setArrangementNumber("08676168");

        applications.setUserId("10.245.176.43");
        applications.setProductRequested(productId);
        applications.setProductName(productName);
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

        ApplicationParameterValues applicationParameterValues1 = new ApplicationParameterValues(1L, applicationParameters5, applications, "2974109HL890678330000003225830841102", null);
        ApplicationParameterValues applicationParameterValues2 = new ApplicationParameterValues(1L, applicationParameters9, applications, "AAGATEWAY", null);
        ApplicationParameterValues applicationParameterValues3 = new ApplicationParameterValues(1L, applicationParameters1, applications, "P_CLASSIC", null);
        ApplicationParameterValues applicationParameterValues4 = new ApplicationParameterValues(1L, applicationParameters11, applications, "true", null);

        ApplicationParameterValues applicationParameterValues5 = new ApplicationParameterValues(1L, applicationParameters7, applications, "601", "Accept");
        ApplicationParameterValues applicationParameterValues6 = new ApplicationParameterValues(1L, applicationParameters7, applications, "502", "Additional Data req - block A.");
        ApplicationParameterValues applicationParameterValues7 = new ApplicationParameterValues(1L, applicationParameters7, applications, "503", "Additional Data req - block B.");
        ApplicationParameterValues applicationParameterValues8 = new ApplicationParameterValues(1L, applicationParameters7, applications, "601", "Accept");

        ApplicationParameterValues applicationParameterValues9 = new ApplicationParameterValues(1L, applicationParameters4, applications, "50", null);
        ApplicationParameterValues applicationParameterValues10 = new ApplicationParameterValues(1L, applicationParameters10, applications, "0", null);
        ApplicationParameterValues applicationParameterValues11 = new ApplicationParameterValues(1L, applicationParameters6, applications, "971461460", null);
        ApplicationParameterValues applicationParameterValues12 = new ApplicationParameterValues(1L, applicationParameters2, applications, "Y", null);

        ApplicationParameterValues applicationParameterValues13 = new ApplicationParameterValues(1L, applicationParameters3, applications, "502", null);


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

        //applications.setApplicationParameterValues(applicationParameterValuesSet);


        ApplicationFeatureTypes applicationFeatureTypes1 = new ApplicationFeatureTypes("BT", "Balance Transfer");
        ApplicationFeatureTypes applicationFeatureTypes2 = new ApplicationFeatureTypes("CHQBK", "ChequeBook");
        ApplicationFeatureTypes applicationFeatureTypes3 = new ApplicationFeatureTypes("SPMNY", "Spending money");
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

        //applicationFeatures3.setExpiryDate("26-NOV-14 09.24.31");

        ApplicationFeatures applicationFeatures4 = new ApplicationFeatures();
        applicationFeatures4.setApplications(applications);
        applicationFeatures4.setApplicationFeatureTypes(applicationFeatureTypes);
        applicationFeatures4.setFeatureRequired("Y");

        Set<ApplicationFeatures> applicationFeaturesSet = new HashSet<>();
        applicationFeaturesSet.add(applicationFeatures1);
        applicationFeaturesSet.add(applicationFeatures2);
        applicationFeaturesSet.add(applicationFeatures3);
        applicationFeaturesSet.add(applicationFeatures4);
        applications.setApplicationFeatures(applicationFeaturesSet);


        ApplicationActivityHistory activityHistory = new ApplicationActivityHistory();
        activityHistory.setApplications(applications);
        activityHistory.setApplicationStatus(applicationStatus);
        activityHistory.setDateModified(new Date());
        activityHistory.setUserId("UNAUTHSALE");
        activityHistory.setUserTypes(userTypes);

        Set<ApplicationActivityHistory> applicationActivityHistorySet = new HashSet<>();
        applicationActivityHistorySet.add(activityHistory);


        applications.setApplicationActivityHistories(applicationActivityHistorySet);
        Iterable it = applicationsDao.findAll();

        Individuals individuals = new Individuals();
        //individuals.setPartyId(598711L);
        if(isDuplicate) {
            individuals.setOcisId("12345");
        }
        else{
            individuals.setOcisId("123456");
        }
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

        individualsDao.save(individuals);
        individualsDao.findAll();
        PartyApplications partyApplications = new PartyApplications();
        partyApplications.setApplicationPartyRoles(roles1);
        partyApplications.setScoringStatus("1");
        partyApplications.setCustomerSegments("3");
        partyApplications.setKycStatus(status);
        partyApplications.setLockId(0L);
        //partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);
        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();
        Set<PartyApplications> partyApplicationsSet = new HashSet<>();
        partyApplicationsSet.add(partyApplications);
        individuals.setPartyApplications(partyApplicationsSet);
        applicationsBO.put("appId", applications.getId());
        applicationsBO.put("partyId", individuals.getPartyId());
        return applicationsBO;
    }

    public HashMap<String, Long> createApplication(boolean isDuplicate) {
        return createApplicationWithStatusAndProduct(isDuplicate, "1002", "Approved", "20198", "Saving Account");
    }

    public DetermineEligibleCustomerInstructionsRequest createEligibilityRequestWhenBfpoNotPresentAndEligibilityIsTrue(OfferProductArrangementRequest request, RequestHeader requestHeader, boolean isBFPOIndicatorPresent) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {
        EligibilityService eligibilityService = new EligibilityService();
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = new OfferToEligibilityRequestConverter().convertOfferToEligibilityRequest(request.getProductArrangement(), requestHeader, isBFPOIndicatorPresent);
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
        eligibilityRequest.getCustomerDetails().setApplicantType("01");
        return eligibilityRequest;
    }

    public DetermineEligibleCustomerInstructionsRequest createEligibilityRequestWhenBfpoPresentAndEligibilityIsTrue(OfferProductArrangementRequest request, RequestHeader requestHeader, boolean isBFPOIndicatorPresent) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {
        EligibilityService eligibilityService = new EligibilityService();
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = new OfferToEligibilityRequestConverter().convertOfferToEligibilityRequest(request.getProductArrangement(), requestHeader, isBFPOIndicatorPresent);
        eligibilityRequest.getCustomerDetails().setInternalUserIdentifier("1");

        eligibilityRequest.getHeader().setContactPointId("0000777505");

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
        System.out.println("expectEligibility 6");
        return eligibilityRequest;
    }


    public DetermineEligibleCustomerInstructionsResponse eligibilityResponse(String isEligible) {
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = new DetermineEligibleCustomerInstructionsResponse();

        ProductEligibilityDetails productEligibilityDetails1 = new ProductEligibilityDetails();
        productEligibilityDetails1.setIsEligible(isEligible);
        Product product1 = new Product();
        InstructionDetails value = new InstructionDetails();
        value.setInstructionMnemonic("P_CLUB");
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

    public DetermineEligibleCustomerInstructionsResponse eligibilityResponseForFailue(int maxAge) {
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = new DetermineEligibleCustomerInstructionsResponse();

        ProductEligibilityDetails productEligibilityDetails1 = new ProductEligibilityDetails();
        productEligibilityDetails1.setIsEligible("false");
        Product product1 = new Product();
        InstructionDetails value = new InstructionDetails();
        value.setInstructionMnemonic("P_CLUB");
        product1.setInstructionDetails(value);
        productEligibilityDetails1.getProduct().add(product1);
        ReasonCode reasonCode = new ReasonCode();
        reasonCode.setCode("CR002");
        reasonCode.setDescription("Customer's age is null or  Customer cannot be younger than "+maxAge+" years.");
        productEligibilityDetails1.getDeclineReasons().add(reasonCode);

        eligibilityResponse.getProductEligibilityDetails().add(productEligibilityDetails1);


        return eligibilityResponse;
    }

    public IdentifyParty createX711Request(Customer customer, String contactPointId) {
        try {
            return new RetrieveEIDVScoreRequestFactory().create(customer, contactPointId);
        } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            return null;
        }
    }

    public IdentifyPartyResp createX711ResponseWithEidvApproved() {
        IdentifyPartyResp resp = getIdentifyPartyResp();
        return resp;
    }

    public IdentifyPartyResp createX711ResponseWithEidvRefer() {
        IdentifyPartyResp resp = getIdentifyPartyResp();
        ReferralReason referralReason = new ReferralReason();
        referralReason.setCode("003");
        referralReason.setDescription("Refer");
        ReferralReasons referralReasons = new ReferralReasons();
        referralReasons.getReferralReason().add(referralReason);
        resp.getIdentifyPartyReturn().getIdentifyPartyOutput().setReferralReasons(referralReasons);
        return resp;
    }

    private IdentifyPartyResp getIdentifyPartyResp() {
        IdentifyPartyResp resp = new IdentifyPartyResp();
        lloydstsb.schema.personal.customer.partyidandv.serviceobjects.IdentifyPartyResp partyResp = new lloydstsb.schema.personal.customer.partyidandv.serviceobjects.IdentifyPartyResp();
        IdentifyPartyOutput partyOutput = new IdentifyPartyOutput();
        partyOutput.setTxDesc("001");
        partyOutput.setTxRc("I");
        EvaluationEvidence evaluationEvidence = new EvaluationEvidence();
        //evaluationEvidence.setStrengthToken("3005325HL8906783390090089707311191");
        evaluationEvidence.setStrengthToken(null);
        evaluationEvidence.setIdentityStrength("900");
        evaluationEvidence.setAddressStrength("900");
        partyOutput.setEvaluationEvidence(evaluationEvidence);

        partyOutput.setDate("Fri Nov 27 14:49:26 GMT 2015");
        partyResp.setIdentifyPartyOutput(partyOutput);

        byte severityCode = 0;
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(0);
        resultCondition.setReasonText("001");
        resultCondition.setSeverityCode(severityCode);
        partyResp.setResultCondition(resultCondition);
        resp.setIdentifyPartyReturn(partyResp);
        return resp;
    }

    public F336Resp createF336ResponseWithProductHoldings(int productOneGroupId, int productTwoGroupId) {
        F336Resp f336Resp = new F336Resp();
        f336Resp.setAdditionalDataIn(0);
        f336Resp.setF336Result(createF336Result());
        List<ProductPartyData> productPartyData = new ArrayList<>();
        ProductPartyData productPartyData1 = new ProductPartyData();
        productPartyData1.setProdGroupId(productOneGroupId);
        productPartyData1.setSellerLegalEntCd("LTB");
        productPartyData1.setExtSysId((short) 7);
        productPartyData1.setExtProdIdTx(String.valueOf(1));
        productPartyData1.setProductHeldOpenDt("01012016");
        productPartyData1.setAmdEffDt("01012016");
        productPartyData1.setProdHeldStatusCd("001");
        ProductPartyData productPartyData2 = new ProductPartyData();
        productPartyData2.setProdGroupId(productTwoGroupId);
        productPartyData2.setSellerLegalEntCd("LTB");
        productPartyData2.setExtProdIdTx(String.valueOf(2));
        productPartyData2.setProductHeldOpenDt("01012016");
        productPartyData2.setAmdEffDt("01012016");
        ProductPartyData productPartyData3 = new ProductPartyData();
        productPartyData3.setProdGroupId(productOneGroupId);
        productPartyData3.setSellerLegalEntCd("LTB");
        productPartyData3.setExtSysId((short) 10107);
        productPartyData3.setProductHeldOpenDt("01012016");
        productPartyData3.setAmdEffDt("01012016");
        productPartyData3.setExtProdIdTx(String.valueOf(productOneGroupId));
        ProductPartyData productPartyData4 = new ProductPartyData();
        productPartyData4.setProdGroupId(3);
        productPartyData4.setSellerLegalEntCd("CAG");
        productPartyData4.setExtSysId((short) 10106);
        productPartyData4.setExtProdIdTx(String.valueOf(3));
        productPartyData4.setProductHeldOpenDt("01012016");
        productPartyData4.setAmdEffDt("01012016");
        productPartyData.add(productPartyData1);
        productPartyData.add(productPartyData2);
        productPartyData.add(productPartyData3);
        productPartyData.add(productPartyData4);

        f336Resp.getProductPartyData().addAll(productPartyData);
        return f336Resp;
    }

    public List<AffiliateDetails> createAffiliateDetailsListForUpdate() {
        List<AffiliateDetails> affiliateDetailsList = new ArrayList<>();
        AffiliateDetails affiliateDetails = new AffiliateDetails();
        affiliateDetails.setAffiliateIdentifier("727");
        affiliateDetails.setAffiliateDescription("Freemax Media");
        affiliateDetails.setIsCreditIntermediary(true);
        affiliateDetailsList.add(affiliateDetails);
        return affiliateDetailsList;
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

    public int getPartyApplicationsSize() {
        int count = 0;
        for (PartyApplications partyApplication : partyApplicationsDao.findAll()) {
            count++;
        }
        return count;
    }

    public int getStreetAddressesSize() {
        int count = 0;
        for (StreetAddresses streetAddresses : streetAddressesDao.findAll()) {
            count++;
        }
        return count;
    }

    public int getPromoPartyApplicationsSize() {
        int count = 0;
        for (PromoPartyApplications promoPartyApplications : promoPartyApplicationsDao.findAll()) {
            count++;
        }
        return count;
    }

    public int getApplicationSize() {
        int count = 0;
        for (Applications applications : applicationsDao.findAll()) {
            count++;
        }
        return count;
    }

    public int getApplicationParametersSize(long appId) {
        int count = 0;
        Applications applications = applicationsDao.findOne(appId);
        count = applications.getApplicationParameterValues().size();
        return count;
    }

    public int getApplicationFeaturesSize(long appId) {
        int count = 0;
        Applications applications = applicationsDao.findOne(appId);
        count = applications.getApplicationParameterValues().size();
        return count;
    }


    public RetrieveProductConditionsResponse rpcResponse() {
        RetrieveProductConditionsResponse rpcResponse = new RetrieveProductConditionsResponse();

        Product product1 = new Product();
        product1.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        product1.getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        product1.getExternalSystemProductIdentifier().get(0).setProductIdentifier("123456");
        ProductOptions productOption = new ProductOptions();
        productOption.setOptionsName("Tariff");
        productOption.setOptionsValue("optionValue");
        product1.getProductoptions().add(productOption);
        rpcResponse.getProduct().add(product1);

        return rpcResponse;
    }

    public RetrieveProductConditionsRequest rpcRequest(ProductArrangement productArrangement, RequestHeader requestHeader) {
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        retrieveProductConditionsRequest.setHeader(requestHeader);
        retrieveProductConditionsRequest.setProduct(new Product());
        retrieveProductConditionsRequest.getProduct().setProductIdentifier(productArrangement.getAssociatedProduct().getProductIdentifier());
        return retrieveProductConditionsRequest;
    }

    public DetermineCustomerProductConditionsRequest createDcpcRequest(RetrieveProductConditionsResponse rpcResp, ProductArrangement requestDepositArrangement, RequestHeader requestHeader) throws InternalServiceErrorMsg {
        DetermineCustomerProductConditionsRequest dcpcRequest = new DetermineCustomerProductConditionsRequest();
        dcpcRequest.setRequestHeader(new com.lloydsbanking.xml.schema.enterprise.lcsm5.services.definitions.RequestHeader());
        dcpcRequest.getRequestHeader().setRequestGroupQuantity(BigInteger.valueOf(1));
        dcpcRequest.getRequestHeader().getRequestGroup().add(new RequestGroup());
        dcpcRequest.getRequestHeader().getRequestGroup().get(0).setExternalApplicationId("AL02760");
        dcpcRequest.getPartyDetails().add(new PCMCustomer());
        dcpcRequest.getPartyDetails().get(0).getCategories().add(new PCMCategory());
        dcpcRequest.getPartyDetails().get(0).getCategories().get(0).setIdentifier("Y");
        dcpcRequest.getPartyDetails().get(0).getCategories().get(0).setDescription("PRIMARY_CATEGORYID");
        dcpcRequest.getPartyDetails().get(0).getConsentShareValues().add("y");
        try {
            dcpcRequest.getPartyDetails().get(0).setAuditDate(new DateFactory().dateToXMLGregorianCalendar(new DateFactory().currentDateTime()));
        } catch (DatatypeConfigurationException e) {
        }
        dcpcRequest.getPartyDetails().get(0).setCustomerIdentifiers(new InvolvedPartyIdentifier());
        dcpcRequest.getPartyDetails().get(0).getCustomerIdentifiers().setIdentifier("12345");
        dcpcRequest.getPartyDetails().get(0).setDescription("PRIMARY_CUSTOMER");
        dcpcRequest.getProductDetails().add(new PCMProduct());
        dcpcRequest.getProductDetails().get(0).setIdentifier(new ProductIdentifier());
        if (null != rpcResp && !org.springframework.util.CollectionUtils.isEmpty(rpcResp.getProduct())) {
            Product productFamilyMember = rpcResp.getProduct().get(0);
            List<ExtSysProdIdentifier> extSysProdIdentifierList = productFamilyMember.getExternalSystemProductIdentifier();
            if (!org.springframework.util.CollectionUtils.isEmpty(extSysProdIdentifierList)) {
                for (ExtSysProdIdentifier extSysProdIdentifier : extSysProdIdentifierList) {
                    if (null != extSysProdIdentifier.getSystemCode() && extSysProdIdentifier.getSystemCode().equalsIgnoreCase("00004") && null != extSysProdIdentifier.getProductIdentifier()) {
                        dcpcRequest.getProductDetails().get(0).getIdentifier().setIdentifier((extSysProdIdentifier.getProductIdentifier().substring(0, 4)));
                    }
                }
            }
            dcpcRequest.getProductDetails().get(0).getTariffs().add(new PCMTariff());
            dcpcRequest.getProductDetails().get(0).getTariffs().get(0).setIdentifier(getFeatureValue(productFamilyMember));
        }
        dcpcRequest.getProductDetails().get(0).setBrandName(requestHeader.getChannelId());
        return dcpcRequest;
    }


    private String getFeatureValue(Product productFamilyMember) {
        String featureValue = "";
        if (!org.springframework.util.CollectionUtils.isEmpty(productFamilyMember.getProductoptions())) {
            for (ProductOptions feature : productFamilyMember.getProductoptions()) {
                if (null != feature.getOptionsName() && feature.getOptionsName().contains("Tariff") && !StringUtils.isEmpty(feature.getOptionsValue())) {
                    featureValue = feature.getOptionsValue();
                }
            }
        }
        return featureValue;
    }

    public DetermineCustomerProductConditionsResponse createDcpcResponse() {
        DetermineCustomerProductConditionsResponse dcpcResponse = new DetermineCustomerProductConditionsResponse();
        dcpcResponse.getProductConditions().add(new PCMProduct());
        dcpcResponse.getProductConditions().get(0).getTariffs().add(new PCMTariff());
        dcpcResponse.getProductConditions().get(0).getTariffs().get(0).getPreferentialRates().add(new PCMPreferentialRate());
        dcpcResponse.getProductConditions().get(0).getTariffs().get(0).getPreferentialRates().get(0).setPreferentialRateIdentifier("pprid");
        return dcpcResponse;
    }

    public int getPartyCountryAssociationSize() {
        int count = 0;
        for (PartyCountryAssociations partyCountryAssociations : partyCountryAssociationsDao.findAll()) {
            count++;
        }
        return count;
    }

    public Applications createRelatedApplication() {
        ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");
        appStatusDao.save(applicationStatus);
        appStatusDao.findAll();


        status.setCode("DECLINE");
        status.setDescription("DECLINE");
        kycStatusDao.save(status);
        kycStatusDao.findAll();

        Applications applications = new Applications(applicationTypes, productTypes1, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        applications.setSortCode("773315");
        applications.setArrangementNumber("08676168");

        applications.setUserId("10.245.176.43");
        applications.setProductRequested("20198");
        applications.setProductName("Saving Account");
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

        ApplicationParameterValues applicationParameterValues1 = new ApplicationParameterValues(1L, applicationParameters5, applications, "2974109HL890678330000003225830841102", null);
        ApplicationParameterValues applicationParameterValues2 = new ApplicationParameterValues(1L, applicationParameters9, applications, "AAGATEWAY", null);
        ApplicationParameterValues applicationParameterValues3 = new ApplicationParameterValues(1L, applicationParameters1, applications, "P_CLASSIC", null);
        ApplicationParameterValues applicationParameterValues4 = new ApplicationParameterValues(1L, applicationParameters11, applications, "true", null);

        ApplicationParameterValues applicationParameterValues5 = new ApplicationParameterValues(1L, applicationParameters7, applications, "601", "Accept");
        ApplicationParameterValues applicationParameterValues6 = new ApplicationParameterValues(1L, applicationParameters7, applications, "502", "Additional Data req - block A.");
        ApplicationParameterValues applicationParameterValues7 = new ApplicationParameterValues(1L, applicationParameters7, applications, "503", "Additional Data req - block B.");
        ApplicationParameterValues applicationParameterValues8 = new ApplicationParameterValues(1L, applicationParameters7, applications, "601", "Accept");

        ApplicationParameterValues applicationParameterValues9 = new ApplicationParameterValues(1L, applicationParameters4, applications, "50", null);
        ApplicationParameterValues applicationParameterValues10 = new ApplicationParameterValues(1L, applicationParameters10, applications, "0", null);
        ApplicationParameterValues applicationParameterValues11 = new ApplicationParameterValues(1L, applicationParameters6, applications, "971461460", null);
        ApplicationParameterValues applicationParameterValues12 = new ApplicationParameterValues(1L, applicationParameters2, applications, "Y", null);

        ApplicationParameterValues applicationParameterValues13 = new ApplicationParameterValues(1L, applicationParameters3, applications, "502", null);


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

        //applications.setApplicationParameterValues(applicationParameterValuesSet);


        ApplicationFeatureTypes applicationFeatureTypes1 = new ApplicationFeatureTypes("BT", "Balance Transfer");
        ApplicationFeatureTypes applicationFeatureTypes2 = new ApplicationFeatureTypes("CHQBK", "ChequeBook");
        ApplicationFeatureTypes applicationFeatureTypes3 = new ApplicationFeatureTypes("SPMNY", "Spending money");
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

        //applicationFeatures3.setExpiryDate("26-NOV-14 09.24.31");

        ApplicationFeatures applicationFeatures4 = new ApplicationFeatures();
        applicationFeatures4.setApplications(applications);
        applicationFeatures4.setApplicationFeatureTypes(applicationFeatureTypes);
        applicationFeatures4.setFeatureRequired("Y");

        Set<ApplicationFeatures> applicationFeaturesSet = new HashSet<>();
        applicationFeaturesSet.add(applicationFeatures1);
        applicationFeaturesSet.add(applicationFeatures2);
        applicationFeaturesSet.add(applicationFeatures3);
        applicationFeaturesSet.add(applicationFeatures4);
        applications.setApplicationFeatures(applicationFeaturesSet);


        ApplicationActivityHistory activityHistory = new ApplicationActivityHistory();
        activityHistory.setApplications(applications);
        activityHistory.setApplicationStatus(applicationStatus);
        activityHistory.setDateModified(new Date());
        activityHistory.setUserId("UNAUTHSALE");
        activityHistory.setUserTypes(userTypes);

        Set<ApplicationActivityHistory> applicationActivityHistorySet = new HashSet<>();
        applicationActivityHistorySet.add(activityHistory);


        applications.setApplicationActivityHistories(applicationActivityHistorySet);
        Iterable it = applicationsDao.findAll();

        Individuals individuals = new Individuals();
        //individuals.setPartyId(598711L);
        individuals.setOcisId("123456");
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

        individualsDao.save(individuals);
        individualsDao.findAll();
        PartyApplications partyApplications = new PartyApplications();
        partyApplications.setApplicationPartyRoles(roles1);
        partyApplications.setScoringStatus("1");
        partyApplications.setCustomerSegments("3");
        partyApplications.setKycStatus(status);
        partyApplications.setLockId(0L);
        //partyApplications.setKycEvidenceDetails(kycEvidenceDetailsSet);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);
        partyApplicationsDao.save(partyApplications);
        partyApplicationsDao.findAll();
        Set<PartyApplications> partyApplicationsSet = new HashSet<>();
        partyApplicationsSet.add(partyApplications);
        individuals.setPartyApplications(partyApplicationsSet);


        return applications;
    }
}
