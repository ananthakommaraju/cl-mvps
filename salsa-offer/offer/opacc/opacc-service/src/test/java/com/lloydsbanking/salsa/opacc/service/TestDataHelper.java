package com.lloydsbanking.salsa.opacc.service;

import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061RequestBuilder;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062RequestBuilder;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydsbanking.salsa.offer.createinvolvedparty.convert.*;
import com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate.EvaluateAddressUpdData;
import com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate.EvaluatePersonalUpdData;
import com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate.EvaluateStrength;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.identify.convert.EnquirePartyIdRequestFactory;
import com.lloydsbanking.salsa.offer.verify.convert.RetrieveEIDVScoreRequestFactory;
import com.lloydsbanking.salsa.opacc.client.*;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Req;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Result;
import com.lloydsbanking.salsa.soap.asm.f424.objects.ProductOffered;
import com.lloydsbanking.salsa.soap.encrpyt.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Req;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Resp;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Result;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Req;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Resp;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Result;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ExtraConditions;
import com.lloydstsb.schema.infrastructure.soap.ReasonDetail;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import lib_sim_bo.businessobjects.*;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_gmo.messages.ResponseHeader;
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

    public static final String CALLER_IP_ADDRESS = "127.0.0.1";

    public static final Short PARTY_EXT_SYS_ID = 1;

    public static final Short DEFAULT_PARTY_EXT_SYS_ID = 2;

    public static final String ENCRYPTED_MEMORABLE_INFO = "rO0ABXNyAEBjb20ubGxveWRzYmFua2luZy5zYWxzYS5zb2FwLmVuY3JweXQub2JqZWN0cy5FbmNy" + "eXB0RGF0YVJlc3BvbnNlAAAAAAAAAAECAAFMAApvdXRkZXRhaWxzdAAQTGphdmEvdXRpbC9MaXN0" + "O3hwc3IAE2phdmEudXRpbC5BcnJheUxpc3R4gdIdmcdhnQMAAUkABHNpemV4cAAAAAF3BAAAAAFz" + "cgA3Y29tLmxsb3lkc2Jhbmtpbmcuc2Fsc2Euc29hcC5lbmNycHl0Lm9iamVjdHMuT3V0ZGV0YWls" + "cwAAAAAAAAABAgAFTAAJYXN5bW1DZXJ0dAASTGphdmEvbGFuZy9TdHJpbmc7TAAIYXN5bW1LZXlx" + "AH4ABkwACW91dEVuY29kZXEAfgAGTAAHb3V0dGV4dHQAEkxqYXZhL2xhbmcvT2JqZWN0O0wADm91" + "dHRleHREZXRhaWxzcQB+AAZ4cHBwcHQAA0FCQ3B4";

    public static final String ENCRYPTED_MEMORABLE_INFO2 = "rO0ABXNyAEBjb20ubGxveWRzYmFua2luZy5zYWxzYS5zb2FwLmVuY3JweXQub2JqZWN0cy5FbmNy" + "eXB0RGF0YVJlc3BvbnNlAAAAAAAAAAECAAFMAApvdXRkZXRhaWxzdAAQTGphdmEvdXRpbC9MaXN0" + "O3hwc3IAE2phdmEudXRpbC5BcnJheUxpc3R4gdIdmcdhnQMAAUkABHNpemV4cAAAAAF3BAAAAApz" + "cgA3Y29tLmxsb3lkc2Jhbmtpbmcuc2Fsc2Euc29hcC5lbmNycHl0Lm9iamVjdHMuT3V0ZGV0YWls" + "cwAAAAAAAAABAgAFTAAJYXN5bW1DZXJ0dAASTGphdmEvbGFuZy9TdHJpbmc7TAAIYXN5bW1LZXlx" + "AH4ABkwACW91dEVuY29kZXEAfgAGTAAHb3V0dGV4dHQAEkxqYXZhL2xhbmcvT2JqZWN0O0wADm91" + "dHRleHREZXRhaWxzcQB+AAZ4cHBwcHQAA0FCQ3B4";

    public static final String MEMORABLE_INFO = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><EncryptDataResponse xmlns:ns2=\"http://www.lloydstsb.com/Schema/InternetBanking/EncryptData\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:EncryptDataResponse\"><ns2:outdetails><ns2:outtext xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">ABC</ns2:outtext></ns2:outdetails></EncryptDataResponse>";

    private static final short EXTERNAL_SYS_ID = 19;

    private static final String CLOSURE_PERIOD_MONTHS_DR = "000";

    private static final String CLOSED_ONLY_IN = "0";

    private static final String CUSTOMER_CONSENT_IN = "2";

    ProductTypes productTypes = new ProductTypes("100", "Credit Card");

    Brands brands = new Brands("LTB");

    UserTypes userTypes = new UserTypes("1001", "Customer");

    @Autowired
    ApplicationRelationshipTypesDao applicationRelationshipTypesDao;

    @Autowired
    AbandonDeclineReasonDao abandonDeclineReasonDao;

    @Autowired
    PartyNonCoreUpdDataFactory partyNonCoreUpdDataFactory;

    @Autowired
    EvaluateStrength evaluateStrength;

    ApplicationStatus applicationStatus = new ApplicationStatus("1001", "Initialised");

    ApplicationStatus applicationStatus1 = new ApplicationStatus("1002", "Approved");

    ApplicationStatus applicationStatus2 = new ApplicationStatus("1003", "Referred");

    ApplicationStatus applicationStatus3 = new ApplicationStatus("1004", "Declined");

    ApplicationStatus applicationStatus4 = new ApplicationStatus("1005", "Not Scored");

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

    AbandonDeclineReasons abandonDeclineReasons = new AbandonDeclineReasons("102", "ASM decline. Based on credit scoring");

    Demographics demographics = new Demographics("1001");

    DemographicValues demographicValues = new DemographicValues(1L, demographics);

    TelephoneAddressTypes telephoneAddressTypes = new TelephoneAddressTypes("7");

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

    ApplicationParameters applicationParameters12 = new ApplicationParameters("100041", parameterGroups2, "Gauranteed Offer Code");

    ApplicationParameters applicationParameters13 = new ApplicationParameters("100039", parameterGroups1, "Auth Customer");

    ApplicationParameters applicationParameters14 = new ApplicationParameters("100045", parameterGroups2, "Credit Limit Amount");

    ApplicationParameters applicationParameters15 = new ApplicationParameters("100007", parameterGroups2, "ASM REFER");

    ApplicationParameters applicationParameters16 = new ApplicationParameters("100001", parameterGroups2, "ASM decline code");

    ApplicationParameters applicationParameters17 = new ApplicationParameters("100002", parameterGroups6, "EIDV decline code");
    ApplicationParameters applicationParameters18 = new ApplicationParameters("100003", parameterGroups6, "EIDV referral code");


    ApplicationPartyRoles roles1 = new ApplicationPartyRoles("0001", "Key Party");

    ApplicationPartyRoles roles2 = new ApplicationPartyRoles("0002", "Additional Card Holder");

    ApplicationPartyRoles roles3 = new ApplicationPartyRoles("0003", "Promotional Party Affiliate");

    ApplicationPartyRoles roles4 = new ApplicationPartyRoles("0004", "Promotional Party Affiliate Network Provider");

    KycStatus status = new KycStatus();

    KycStatus status1 = new KycStatus("N/A");

    KycStatus status2 = new KycStatus("1000");

    KycStatus status3 = new KycStatus("1001");

    KycStatus status4 = new KycStatus("ACCEPT");

    KycStatus status5 = new KycStatus("REFER");

    KycStatus status6 = new KycStatus("DECLINE");

    ProductPackageTypes productPackageTypes = new ProductPackageTypes("2004", "Typical");

    ProductPackageTypes productPackageTypes1 = new ProductPackageTypes("2001", "Normal");

    ProductPackageTypes productPackageTypes2 = new ProductPackageTypes("2002", "Upsell");

    ProductPackageTypes productPackageTypes3 = new ProductPackageTypes("2003", "Downsell");

    ApprovalStatus approvalStatus = new ApprovalStatus("001");

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
    EvaluateAddressUpdData evaluateAddressUpdData;

    @Autowired
    EvaluatePersonalUpdData evaluatePersonalUpdData;

    @Autowired
    PhoneUpdDataFactory phoneUpdDataFactory;

    @Autowired
    EmployerAddressDataFactory employerAddressDataFactory;

    @Autowired
    PromoPartyApplicationsDao promoPartyApplicationsDao;

    @Autowired
    AbandonDeclineReasonDao abandonDeclineReasonsDao;

    @Autowired
    ProductPackageTypesDao productPackageTypesDao;

    @Autowired
    PromotionPartyExtSystemsDao promotionPartyExtSystemsDao;

    private Product createAssociatedProduct() {
        Product associatedProduct = new AssociatedProductBuilder().productIdentifier("20198")
                .externalSystemProductIdentifier(createExtSysProdId())
                .instructionDetails(createInstructionDetails())
                .productName("Account B")
                .productOptions(createProductOptions())
                .build();
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

    private List<RuleCondition> createRuleConditionList() {
        RuleCondition condition = new RuleCondition();
        condition.setName("EIDV_REFERRAL_DISABLED_SWITCH");
        condition.setResult("DISABLED");
        List<RuleCondition> conditionList = new ArrayList<RuleCondition>();
        conditionList.add(0, condition);
        return conditionList;
    }

    private List<AffiliateDetails> createAffiliateDetailsList() {
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
        productPartyData1.setProdHeldStatusCd("001");
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
        structuredAddress.setInPostCd("se1");
        structuredAddress.setOutPostCd("2ea");
        structuredAddress.setBuildingNm("subBuilding");
        structuredAddress.setAddressPostTownNm("postTown");
        structuredAddress.setAddressCountyNm("country");
        structuredAddress.setBuildingNo("123");
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
        } catch (InternalServiceErrorMsg e) {
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
        response.getPartyEnqData().getAddressData().setUnstructuredAddress(new com.lloydsbanking.salsa.soap.ocis.f061.objects.UnstructuredAddress());
        response.getPartyEnqData().setEvidenceData(new EvidenceData());

        response.getPartyEnqData().setPersonalData(new PersonalData());
        response.getPartyEnqData().getPersonalData().setCIDPersId("1234");

        return response;
    }

    public F061Resp createF061RespWithoutEvidenceData2(int reasonCode) {

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
        response.getPartyEnqData().getAddressData().setUnstructuredAddress(new com.lloydsbanking.salsa.soap.ocis.f061.objects.UnstructuredAddress());
        response.getPartyEnqData().setEvidenceData(new EvidenceData());

        response.getPartyEnqData().setPersonalData(new PersonalData());
        response.getPartyEnqData().getPersonalData().setCIDPersId("1234");

        return response;
    }

    public List<ReferralCode> createReferralCodeList(String code, String description) {

        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode(code);
        referralCode.setDescription(description);
        referralCodeList.add(referralCode);
        return referralCodeList;
    }

    public List<ReferralCode> createReferralCodeListForDirectDebitRequiredAccept(String code, String description) {

        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("613");
        referralCode.setDescription("descriptionForDebit");
        referralCodeList.add(referralCode);
        ReferralCode referralCode2 = new ReferralCode();
        referralCode2.setCode(code);
        referralCode2.setDescription(description);
        referralCodeList.add(referralCode2);
        return referralCodeList;
    }

    public List<ReferralCode> createReferralCodeListForDirectDebitRequiredRefer(String code, String description) {

        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("213");
        referralCode.setDescription("descriptionForDebit");
        referralCodeList.add(referralCode);
        ReferralCode referralCode2 = new ReferralCode();
        referralCode2.setCode(code);
        referralCode2.setDescription(description);
        referralCodeList.add(referralCode2);
        return referralCodeList;
    }


    public CurrencyAmount createCurrencyAmount(BigDecimal amount) {
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(amount);
        return currencyAmount;
    }

    public RequestHeader createOpaccRequestHeader(String channelId) {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCC")
                .channelId(channelId)
                .interactionId("vbww2yofqtcx1qbzw8iz4gm19")
                .serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...")
                .contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer")
                .bapiInformation(channelId, "interactionId", "AAGATEWAY", "ns4")
                .securityHeader("ns4", "UNAUTHSALE")
                .build();
        header.setContactPointId(channelId);
        return header;
    }

    public RequestHeader createOpaccRequestHeaderForUnAuth(String channelId) {
        RequestHeader header = new OpaccRequestHeaderBuilder().businessTransaction("OfferProductArrangementPCC")
                .channelId(channelId)
                .interactionId("vbww2yofqtcx1qbzw8iz4gm19")
                .serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...")
                .contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer")
                .bapiInformationUnAuth(channelId, "interactionId", "AAGATEWAY", "ns4", "UNAUTHSALE")
                .securityHeader("ns4", "UNAUTHSALE")
                .build();
        header.setContactPointId(channelId);
        return header;
    }

    public OfferProductArrangementRequest generateOfferProductArrangementPCCRequestForUnAuth(String channelId) {
        RequestHeader header = createOpaccRequestHeaderForUnAuth(channelId);
        OfferProductArrangementRequest request = new OpaccRequestBuilder().
                requestHeader(header).financeServiceArrangement(createFinanceServiceArrangement()).build();
        return request;
    }

    public OfferProductArrangementRequest generateOfferProductArrangementPCCRequest(String channelId) {
        RequestHeader header = createOpaccRequestHeaderForUnAuth(channelId);
        OfferProductArrangementRequest request = new OpaccRequestBuilder().
                requestHeader(header).financeServiceArrangement(createFinanceServiceArrangement()).build();
        return request;
    }

    public OfferProductArrangementRequest generateOfferProductArrangementPCCRequestAuth(String channelId) {
        RequestHeader header = createOpaccRequestHeader(channelId);
        OfferProductArrangementRequest request = new OpaccRequestBuilder().
                requestHeader(header).financeServiceArrangement(createFinanceServiceArrangement()).build();
        return request;
    }

    private FinanceServiceArrangement createFinanceServiceArrangement() {
        AccessToken accessToken = new AccessToken();
        accessToken.setMemorableInfo("aaa");
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangementBuilder().arrangementType()
                .associatedProduct(createAssociatedProductCC())
                .initiatedThrough(createInitiatedThroughCC())
                .campaignCode("HXAA01B001RPCC5")
                .primaryInvolvedParty(new InvolvedPartyBuilder().partyIdentifier("+00883965244")
                        .emailAddress("a@a.com")
                        .postalAddress(createPostalAddressListCC())
                        .sourceSystemId("2")
                        .customerIdentifier("1985856187")
                        .existingSortCode("110135")
                        .existingAccountNumber("50005769")
                        .userType("1001")
                        .internalUserIdentifier("BE205960")
                        .partyRole("0001")
                        .customerSegment("1")
                        .accessToken(accessToken)
                        .hasExistingCreditCard(false)
                        .isPlayedBy(createIsPlayedByCC())
                        .build())
                .marketingPreferenceBySMS(true)
                .affiliateDetails(createAffiliateDetailsListCC())
                .applicationType("10001")
                .affiliateId("000")
                .conditions(createRuleConditionListCC())
                .marketingPreferenceByEmail(true)
                .marketingPreferenceByPhone(true)
                .marketingPreferenceByMail(true)
                .marketingPreferenceIndicator(true)
                .balanceTransferAmount(createCurrencyAmount(new BigDecimal(0)))
                .build();
        return financeServiceArrangement;
    }

    private Product createAssociatedProductCC() {
        Product associatedProduct = new AssociatedProductBuilder().productIdentifier("20042")
                .guaranteedOfferCode("N")
                .externalSystemProductIdentifier(createExtSysProdId())
                .productOffer(createProductOfferCC())
                .productName("Clarity Credit Card")
                .productPropositionIdentifier("42")
                .build();
        //associatedProduct.setBrandName("LTB");
        return associatedProduct;
    }

    private Channel createInitiatedThroughCC() {
        Channel initiatedThrough = new Channel();
        initiatedThrough.setChannelCode("004");
        initiatedThrough.setSubChannelCode("001");
        return initiatedThrough;
    }

    public List<PostalAddress> createPostalAddressListCC() {
        PostalAddress postalAddress = new PostalAddressBuilder().durationOfStay("0505")
                .isPAFFormat(false)
                .statusCode("CURRENT")
                .unstructuredAddress(createUnstructuredAddressCC())
                .build();


        //*************  not in request
        postalAddress.setIsBFPOAddress(false);


        List<PostalAddress> postalAddressList = new ArrayList<>();
        postalAddressList.add(0, postalAddress);
        return postalAddressList;
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

    public Individual createIsPlayedByCC() {
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        Employer currentEmployer = new Employer();
        currentEmployer.setName("hghjgj");
        Individual isPlayedBy = new IndividualBuilder().individualName(createIndividualName())
                .residentialStatus("002")
                .birthDate(datatypeFactory.newXMLGregorianCalendar("1948-01-01T06:40:56.046Z"))
                .nationality("GBR")
                .maritalStatus("001")
                .gender("001")
                .employmentStatus("003")
                .currentEmploymentDuration("1212")
                .grossAnnualIncome(createCurrencyAmount(new BigDecimal(2555)))
                .occupation("1")
                .currentEmployer(currentEmployer)
                .build();
        return isPlayedBy;
    }

    private List<RuleCondition> createRuleConditionListCC() {
        RuleCondition condition = new RuleCondition();
        condition.setName("CREDIT_CARD_DUPLICATE_SWITCH");
        condition.setResult("0");
        List<RuleCondition> conditionList = new ArrayList<>();
        conditionList.add(0, condition);
        return conditionList;
    }


    private List<AffiliateDetails> createAffiliateDetailsListCC() {
        AffiliateDetails affiliateDetails = new AffiliateDetails();
        affiliateDetails.setAffiliateIdentifier("000");
        List<AffiliateDetails> affiliateDetailsList = new ArrayList<AffiliateDetails>();
        affiliateDetailsList.add(0, affiliateDetails);
        return affiliateDetailsList;
    }

    private List<ProductOffer> createProductOfferCC() {
        ProductOffer productOffer = new ProductOffer();
        productOffer.setProdOfferIdentifier("1000086");
        productOffer.setOfferType("2004");
        List<ProductOffer> productOfferList = new ArrayList<>();
        productOfferList.add(0, productOffer);
        return productOfferList;
    }

    public F424Resp createF424Response(String asmCreditScoreResultCd, List<ReferralCode> referralCodeList) {
        F424Resp f424Resp = new F424Resp();
        f424Resp.setF424Result(new F424Result());
        f424Resp.getF424Result().setResultCondition(new ResultCondition());
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


    public F424Resp createF424ResponseWithExternalServiceErrorCode() {
        F424Resp f424Resp = new F424Resp();
        f424Resp.setF424Result(new F424Result());
        f424Resp.getF424Result().setResultCondition(new ResultCondition());
        f424Resp.getF424Result().getResultCondition().setReasonCode(159259);
        f424Resp.getF424Result().getResultCondition().setReasonText("abc");
        f424Resp.getF424Result().getResultCondition().setSeverityCode((byte) 0);
        return f424Resp;
    }

    public F424Resp createF424ResponseWithExternalBusinessErrorCode() {
        F424Resp f424Resp = new F424Resp();
        f424Resp.setF424Result(new F424Result());
        f424Resp.getF424Result().setResultCondition(new ResultCondition());
        f424Resp.getF424Result().getResultCondition().setReasonCode(159179);
        f424Resp.getF424Result().getResultCondition().setReasonText("abc");
        f424Resp.getF424Result().getResultCondition().setSeverityCode((byte) 0);
        return f424Resp;
    }

    public EncryptDataResponse createEncryptDataRequestResponse(int reasonCode) {
        EncryptDataResponse encryptDataResponse = new EncryptDataResponse();
        encryptDataResponse.getOutdetails().add(new Outdetails());
        encryptDataResponse.getOutdetails().get(0).setOuttext("ABC");
        return encryptDataResponse;
    }

    public EncryptDataRequest createEncryptDataServiceRequest(String memorableInfo, RequestHeader header) {
        List<String> encryptValue = new ArrayList<>();
        encryptValue.add(memorableInfo);
        EncryptDataRequest encryptDataRequest = new EncryptDataRequest();
        for (String itemCardNum : encryptValue) {
            Indetails indetails = new Indetails();
            indetails.setIntext(itemCardNum);
            indetails.setEncryptKey("encryptKey");
            indetails.setEncryptType(EncryptionType.valueOf("ASYMM"));
            indetails.setInpEncode("base64");
            encryptDataRequest.getIndetails().add(indetails);
        }
        return encryptDataRequest;
    }

    public void savePromotionPartyExtSystems(String extId) {
        PromotionPartyExtSystems promotionPartyExtSystems = new PromotionPartyExtSystems();
        promotionPartyExtSystems.setExtId(extId);
        promotionPartyExtSystems.setPromotionPartiesByPpId(promotionPartiesDao.findByApprovalStatusCodeAndAffiliateId(approvalStatus.getCode(), "000"));
        promotionPartyExtSystems.setPromotionPartiesByPpNetworkId(promotionPartiesDao.findByApprovalStatusCodeAndAffiliateId(approvalStatus.getCode(), "000"));
        promotionPartyExtSystemsDao.save(promotionPartyExtSystems);
        promotionPartyExtSystemsDao.findAll();
    }

    public void createPamReferenceData() {
        productTypesDao.save(productTypes);
        productTypesDao.findAll();
        brandsDao.save(brands);
        brandsDao.findAll();
        userTypesDao.save(userTypes);
        userTypesDao.findAll();
        appStatusDao.save(applicationStatus);
        appStatusDao.save(applicationStatus1);
        appStatusDao.save(applicationStatus2);
        appStatusDao.save(applicationStatus3);
        appStatusDao.save(applicationStatus4);
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
        kycStatusDao.findAll();
        kycStatusDao.save(status4);
        kycStatusDao.save(status5);
        kycStatusDao.save(status6);
        kycStatusDao.findAll();
        applicationFeatureTypesDao.save(applicationFeatureTypes);
        applicationFeatureTypesDao.findAll();
        applicationPartyRolesDao.save(createPartyRoleList());
        applicationPartyRolesDao.save(roles4);
        applicationPartyRolesDao.findAll();
        abandonDeclineReasonsDao.save(abandonDeclineReasons);
        abandonDeclineReasonsDao.findAll();
        demographicsDao.save(demographics);
        demographicsDao.findAll();
        demographicValuesDao.save(demographicValues);
        demographicValuesDao.findAll();
        parameterGroupsDao.save(parameterGroups1);
        parameterGroupsDao.save(parameterGroups2);
        parameterGroupsDao.save(parameterGroups6);
        parameterGroupsDao.save(parameterGroups8);
        parameterGroupsDao.findAll();
        applicationParametersDao.save(applicationParameters7);
        applicationParametersDao.save(applicationParameters9);
        applicationParametersDao.save(applicationParameters11);
        applicationParametersDao.save(applicationParameters12);
        applicationParametersDao.save(applicationParameters13);
        applicationParametersDao.save(applicationParameters14);
        applicationParametersDao.save(applicationParameters15);
        applicationParametersDao.save(applicationParameters16);
        applicationParametersDao.save(applicationParameters17);
        applicationParametersDao.save(applicationParameters18);

        applicationParametersDao.findAll();
        telephoneAddressTypesDao.save(telephoneAddressTypes);
        telephoneAddressTypesDao.findAll();
        productPackageTypesDao.save(productPackageTypes);
        productPackageTypesDao.save(productPackageTypes1);

        productPackageTypesDao.save(productPackageTypes2);

        productPackageTypesDao.save(productPackageTypes3);

        approvalStatusDao.save(approvalStatus);
        approvalStatusDao.findAll();
        promotionPartiesDao.save(createPromotionPartyList());

        ApplicationRelationshipTypes applicationRelationshipTypes = new ApplicationRelationshipTypes();
        applicationRelationshipTypes.setCode("20001");
        applicationRelationshipTypes.setDescription("Crosssell");

        applicationRelationshipTypesDao.save(applicationRelationshipTypes);

        applicationFeatureTypesDao.findAll();

        createAbandonDeclineReasons();
    }

    private List<PromotionParties> createPromotionPartyList() {
        List<PromotionParties> promotionPartiesList = new ArrayList<>();
        PromotionParties promotionParties = new PromotionParties();
        promotionParties.setId(26l);
        promotionParties.setAffiliateId("000");
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
        applicationPartyRolesList.add(applicationPartyRoles2);
        return applicationPartyRolesList;
    }


    public Applications createDuplicateApplicationWithASMDecline() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH.mm.ss");
        ApplicationStatus applicationStatus = new ApplicationStatus("1004", "Decline");
        Applications applications = createApplication(applicationStatus);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date today = cal.getTime();
        String fromdate = formatter.format(today);
        Date d = null;
        try {
            d = (formatter.parse(fromdate));

            applications.setDateModified(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public Applications createDuplicateApplicationCC() {

        ApplicationStatus applicationStatus = new ApplicationStatus("1018", "Cancelled");
        return createApplication(applicationStatus);

    }

    public Applications createApplication(ApplicationStatus applicationStatus) {

        appStatusDao.save(applicationStatus);
        appStatusDao.findAll();


        status.setCode("DECLINE");
        status.setDescription("DECLINE");
        kycStatusDao.save(status);
        kycStatusDao.findAll();

        Applications applications = new Applications(applicationTypes, productTypes, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        applications.setSortCode("773315");
        applications.setArrangementNumber("08676168");

        applications.setUserId("10.245.176.43");
        applications.setProductRequested("10005");
        applications.setProductName("Advance Credit card");
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
        // individuals.setPartyId(59871L);
        individuals.setOcisId("12345");
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

    public List<AffiliateDetails> createAffiliateDetailsListForUpdate() {
        List<AffiliateDetails> affiliateDetailsList = createAffiliateDetailsListCC();

        AffiliateDetails affiliateDetails = new AffiliateDetails();
        affiliateDetails.setAffiliateIdentifier("000");
        affiliateDetails.setAffiliateDescription("Freemax Media");
        affiliateDetails.setIsCreditIntermediary(true);
        affiliateDetailsList.add(affiliateDetails);
        return affiliateDetailsList;
    }

    public List<CustomerScore> createCustomerScoreList() {
        List<CustomerScore> customerScoreList = new ArrayList<>();
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("N/A");
        customerScore.setAssessmentType("EIDV");

        CustomerScore score = new CustomerScore();
        score.setScoreResult("1");
        score.setScoreIdentifier("12345");
        score.setAssessmentType("ASM");
        score.getReferralCode().add(0, new ReferralCode());
        score.getReferralCode().get(0).setCode("613");
        score.getReferralCode().get(0).setDescription("descriptionForDebit");
        score.getReferralCode().add(1, new ReferralCode());
        score.getReferralCode().get(1).setCode("code");
        score.getReferralCode().get(1).setDescription("description");

        customerScoreList.add(customerScore);
        customerScoreList.add(score);
        return customerScoreList;
    }

    public DetermineEligibleCustomerInstructionsResponse eligibilityResponse(String isEligible) {
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = new DetermineEligibleCustomerInstructionsResponse();

        ProductEligibilityDetails productEligibilityDetails1 = new ProductEligibilityDetails();
        productEligibilityDetails1.setIsEligible(isEligible);
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
        productPartyData1.setProdHeldStatusCd("001");
        ProductPartyData productPartyData2 = new ProductPartyData();
        productPartyData2.setProdGroupId(productTwoGroupId);
        productPartyData2.setSellerLegalEntCd("LTB");
        productPartyData2.setExtProdIdTx(String.valueOf(2));
        productPartyData2.setProdHeldStatusCd("001");
        ProductPartyData productPartyData3 = new ProductPartyData();
        productPartyData3.setProdGroupId(productOneGroupId);
        productPartyData3.setSellerLegalEntCd("LTB");
        productPartyData3.setExtSysId((short) 10107);
        productPartyData3.setProdHeldStatusCd("001");
        productPartyData3.setExtProdIdTx(String.valueOf(productOneGroupId));
        ProductPartyData productPartyData4 = new ProductPartyData();
        productPartyData4.setProdGroupId(3);
        productPartyData4.setSellerLegalEntCd("CAG");
        productPartyData4.setExtSysId((short) 10106);
        productPartyData4.setProdHeldStatusCd("001");
        productPartyData4.setExtProdIdTx(String.valueOf(3));
        productPartyData.add(productPartyData1);
        productPartyData.add(productPartyData2);
        productPartyData.add(productPartyData3);
        productPartyData.add(productPartyData4);
        f336Resp.getProductPartyData().addAll(productPartyData);
        return f336Resp;
    }


    public Customer createPrimaryInvolvedParty() throws ParseException, DatatypeConfigurationException {

        Customer customer = new Customer();
        customer.setCustomerIdentifier("12");
        customer.setIsPlayedBy(createIsPlayedBy());
        customer.setSourceSystemId("1");
        customer.setCidPersID("2");
        customer.setCbsCustomerNumber("3");
        customer.setCustomerSegment("200");

        customer.setCustomerIdentifier("122323");

        customer.getIsPlayedBy().setCurrentEmployer(new Employer());
        customer.getIsPlayedBy().setPreviousEmployer(new Employer());
        customer.getIsPlayedBy().setIsStaffMember(true);
        customer.getIsPlayedBy().setNumberOfDependents(BigInteger.valueOf(2));
        customer.getIsPlayedBy().setResidentialStatus("residentialStatus");
        customer.setHasExistingCreditCard(true);

        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(BigDecimal.valueOf(500));
        customer.getIsPlayedBy().setMonthlyLoanRepaymentAmount(currencyAmount);
        customer.getIsPlayedBy().setMonthlyMortgageAmount(currencyAmount);
        customer.getIsPlayedBy().setGrossAnnualIncome(currencyAmount);
        customer.getIsPlayedBy().setNetMonthlyIncome(currencyAmount);
        customer.setEmailAddress("nate@gmail.com");
        List<PostalAddress> postalAddressList = new ArrayList<>();

        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setDurationofStay("0308");
        postalAddress.setIsPAFFormat(true);
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setPostCodeIn("postCodeIn");
        structuredAddress.setPostCodeOut("postCodeOut");
        structuredAddress.setSubBuilding("subBuilding");
        structuredAddress.setPostTown("postTown");
        structuredAddress.setCounty("country");
        structuredAddress.setBuilding("building");
        structuredAddress.setBuildingNumber("123");
        structuredAddress.getAddressLinePAFData().add("addressLine1Tx");
        postalAddress.setStructuredAddress(structuredAddress);
        postalAddressList.add(postalAddress);

        PostalAddress postalAddress1 = new PostalAddress();
        postalAddress1.setDurationofStay("0408");
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setPostCode("postCode");
        postalAddress1.setIsPAFFormat(false);
        unstructuredAddress.setAddressLine8("abc");
        unstructuredAddress.setAddressLine1("address1");
        unstructuredAddress.setAddressLine2("address2");
        unstructuredAddress.setAddressLine3("address3");
        unstructuredAddress.setAddressLine4("address4");
        postalAddress1.setUnstructuredAddress(unstructuredAddress);
        postalAddressList.add(postalAddress1);

        customer.getPostalAddress().addAll(postalAddressList);
        customer.setIsAuthCustomer(true);

        return customer;

    }


    public DetermineEligibleCustomerInstructionsRequest createEligibilityRequest(OfferProductArrangementRequest request, RequestHeader requestHeader) throws OfferProductArrangementInternalServiceErrorMsg {
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = new DetermineEligibleCustomerInstructionsRequest();
        requestHeader.setChannelId("0000777505");
        eligibilityRequest.setHeader(requestHeader);
        eligibilityRequest.getExistingProductArrangments().add(new ProductArrangement());
        eligibilityRequest.getExistingProductArrangments().add(new ProductArrangement());

        eligibilityRequest.getExistingProductArrangments().get(0).setFinancialInstitution(new Organisation());
        eligibilityRequest.getExistingProductArrangments().get(1).setFinancialInstitution(new Organisation());
        eligibilityRequest.getExistingProductArrangments().get(0).getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        eligibilityRequest.getExistingProductArrangments().get(1).getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        eligibilityRequest.getExistingProductArrangments().get(0).setAssociatedProduct(new Product());
        eligibilityRequest.getExistingProductArrangments().get(1).setAssociatedProduct(new Product());
        eligibilityRequest.getExistingProductArrangments().get(1).setAssociatedProduct(new Product());
        eligibilityRequest.getExistingProductArrangments().get(0).getAssociatedProduct().setBrandName("LTB");
        eligibilityRequest.getExistingProductArrangments().get(1).getAssociatedProduct().setBrandName("LTB");
        eligibilityRequest.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("1");
        eligibilityRequest.getExistingProductArrangments().get(1).getAssociatedProduct().setProductType("2");
        eligibilityRequest.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        eligibilityRequest.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        eligibilityRequest.getExistingProductArrangments().get(1).getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        eligibilityRequest.getExistingProductArrangments().get(1).getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        eligibilityRequest.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00000");
        eligibilityRequest.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(1).setSystemCode("00000");
        eligibilityRequest.getExistingProductArrangments().get(1).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00000");
        eligibilityRequest.getExistingProductArrangments().get(1).getAssociatedProduct().getExternalSystemProductIdentifier().get(1).setSystemCode("00000");
        eligibilityRequest.getExistingProductArrangments().get(0).getAssociatedProduct().getProductoffer().add(new ProductOffer());
        eligibilityRequest.getExistingProductArrangments().get(1).getAssociatedProduct().getProductoffer().add(new ProductOffer());
       /* HeaderRetriever headerRetriever = new HeaderRetriever();
        headerRetriever.getBapiInformationHeader(eligibilityRequest.getHeader()).getBAPIHeader().setChanid("0000777505");
        headerRetriever.getBapiInformationHeader(eligibilityRequest.getHeader()).getBAPIHeader().setUseridAuthor("UNAUTHSALE");*/

        eligibilityRequest.setCustomerDetails(request.getProductArrangement().getPrimaryInvolvedParty());
        String instructionMnemonic = request.getProductArrangement().getAssociatedProduct().getInstructionDetails() != null ? request.getProductArrangement()
                .getAssociatedProduct()
                .getInstructionDetails()
                .getInstructionMnemonic() : null;
        eligibilityRequest.getCandidateInstructions().add(0, instructionMnemonic);
        eligibilityRequest.setArrangementType(request.getProductArrangement().getArrangementType());
        eligibilityRequest.setAssociatedProduct(request.getProductArrangement().getAssociatedProduct());

        eligibilityRequest.getCustomerDetails().getCustomerScore().add(new CustomerScore());
        eligibilityRequest.getCustomerDetails().getCustomerScore().get(0).setScoreResult("1001");
        eligibilityRequest.getCustomerDetails().getCustomerScore().get(0).setAssessmentType("KYCCompliance");


        eligibilityRequest.getCustomerDetails().getCustomerScore().add(new CustomerScore());
        eligibilityRequest.getCustomerDetails().getCustomerScore().get(1).setScoreResult("ACCEPT");
        eligibilityRequest.getCustomerDetails().getCustomerScore().get(1).setAssessmentType("EIDV");

        eligibilityRequest.getHeader().setContactPointId("0000777505");

        eligibilityRequest.getCustomerDetails().setSourceSystemId("3");
        eligibilityRequest.getCustomerDetails().getPostalAddress().add(0, new PostalAddress());
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine8("n");
        eligibilityRequest.getCustomerDetails().getAuditData().add(new AuditData());
        eligibilityRequest.getCustomerDetails().getAuditData().get(0).setAuditType("PARTY_EVIDENCE");
        eligibilityRequest.getCustomerDetails().getAuditData().get(0).setAuditDate("12345");
        eligibilityRequest.getCustomerDetails().getAuditData().get(0).setAuditTime("12");
        eligibilityRequest.getCustomerDetails().getAuditData().add(new AuditData());
        eligibilityRequest.getCustomerDetails().getAuditData().get(1).setAuditType("ADDRESS_EVIDENCE");
        eligibilityRequest.getCustomerDetails().getAuditData().get(1).setAuditDate("12345");
        eligibilityRequest.getCustomerDetails().getAuditData().get(1).setAuditTime("12");
        eligibilityRequest.getCustomerDetails().setCustomerIdentifier("12345");
        eligibilityRequest.getHeader().setChannelId("LTB");
        eligibilityRequest.getCustomerDetails().setApplicantType("2");
        eligibilityRequest.getCustomerDetails().getAccessToken().setMemorableInfo(MEMORABLE_INFO);
        eligibilityRequest.getCustomerDetails().setCustomerSegment("1");

        eligibilityRequest.getCustomerDetails().getPostalAddress().clear();
        eligibilityRequest.getCustomerDetails().getPostalAddress().add(new PostalAddress());
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).setStatusCode("CURRENT");
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).setIsPAFFormat(false);
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).setDurationofStay("0505");
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine1("96 EDGEHILL ROAD");
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine2("CHISLEHURST");
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine3("KENT");
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine6("United Kingdom");
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine8(null);
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).getUnstructuredAddress().setPostCode("BR7  6LB");
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).getUnstructuredAddress().setPointSuffix("1E");
        eligibilityRequest.getCustomerDetails().getPostalAddress().get(0).setIsBFPOAddress(false);

        return eligibilityRequest;
    }

    public DetermineEligibleCustomerInstructionsRequest createEligibilityRequestForNewCustomer(OfferProductArrangementRequest request, RequestHeader requestHeader) {
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = new DetermineEligibleCustomerInstructionsRequest();
        String instructionMnemonic = request.getProductArrangement().getAssociatedProduct().getInstructionDetails() != null ? request.getProductArrangement()
                .getAssociatedProduct()
                .getInstructionDetails()
                .getInstructionMnemonic() : null;
        eligibilityRequest.getCandidateInstructions().add(0, instructionMnemonic);
        requestHeader.setContactPointId("0000777505");
        eligibilityRequest.setHeader(requestHeader);
        eligibilityRequest.setArrangementType("CC");
        eligibilityRequest.setAssociatedProduct(request.getProductArrangement().getAssociatedProduct());


        eligibilityRequest.setCustomerDetails(request.getProductArrangement().getPrimaryInvolvedParty());

        eligibilityRequest.getCustomerDetails().getCustomerScore().add(new CustomerScore());
        eligibilityRequest.getCustomerDetails().getCustomerScore().get(0).setScoreResult(null);
        eligibilityRequest.getCustomerDetails().getCustomerScore().get(0).setAssessmentType("EIDV");

        eligibilityRequest.getCustomerDetails().getPostalAddress().clear();
        eligibilityRequest.getCustomerDetails().setCustomerIdentifier(null);
        eligibilityRequest.getCustomerDetails().setCustomerSegment("3");
        eligibilityRequest.getCustomerDetails().setApplicantType("2");

        eligibilityRequest.getCustomerDetails().getAccessToken().setMemorableInfo(MEMORABLE_INFO);
        return eligibilityRequest;
    }

    public IdentifyParty createX711Request(Customer customer, String contactPointId) throws OfferProductArrangementInternalServiceErrorMsg {

        try {
            return new RetrieveEIDVScoreRequestFactory().create(customer, contactPointId);
        } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            return null;
        }
    }

    public lloydstsb.schema.personal.customer.partyidandv.IdentifyPartyResp createX711ResponseWithEidvApproved() {
        IdentifyPartyResp resp = getIdentifyPartyResp();
        return resp;
    }

    public lloydstsb.schema.personal.customer.partyidandv.IdentifyPartyResp createX711ResponseWithEidvRefer() {
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


    public F062Req createF062Req(String arrangementType, Customer primaryInvolvedParty, RequestHeader header, boolean marketingPref, String addressStrength, String identityStrength, String eidvStatus, boolean assessmentEvidNull) throws OfferProductArrangementInternalServiceErrorMsg, ParseException, OfferProductArrangementDataNotAvailableErrorMsg {
        return convert(arrangementType, primaryInvolvedParty, header, addressStrength, identityStrength, marketingPref, eidvStatus, assessmentEvidNull);

    }

    public F062Req convert(String arrangementType, Customer primaryInvolvedParty, RequestHeader header, String addressStrength, String identityStrength, boolean marketingPref, String eidvStatus, boolean assessmentEvidNull) throws ParseException {
        AddressUpdDataType addressUpdDataType = new AddressUpdDataType();
        F062RequestBuilder requestBuilder = new F062RequestBuilder();
        F062Req request = requestBuilder.defaults().build();
        if (null != primaryInvolvedParty.getCustomerIdentifier()) {
            request.setPartyId(Long.valueOf(primaryInvolvedParty.getCustomerIdentifier()));
        }
        request.setPartyUpdData(new PartyUpdDataType());
        request.getPartyUpdData().setPersonalUpdData(evaluatePersonalUpdData.generatePersonalUpdData(arrangementType, primaryInvolvedParty, marketingPref));
        request.getPartyUpdData().getPhoneUpdData().addAll(phoneUpdDataFactory.generatePhoneUpdData(primaryInvolvedParty.getTelephoneNumber()));
        request.getPartyUpdData().setPartyNonCoreUpdData(partyNonCoreUpdDataFactory.generatePartyNonCoreUpdData(primaryInvolvedParty.getIsPlayedBy()));
        request.getPartyUpdData().setKYCNonCorePartyUpdData(new KYCNonCorePartyUpdDataType());
        evaluateAddressUpdData.generateAddressUpdData(primaryInvolvedParty.getPostalAddress(), addressUpdDataType);
        setStructuredUnstructuredAddress(primaryInvolvedParty.getPostalAddress(), addressUpdDataType);
        //Address setting done here to avoid cyclic dependency if same is moved in EvaluateAddressUpdData
        request.getPartyUpdData().setAddressUpdData(addressUpdDataType);

        if (null != primaryInvolvedParty.getIsPlayedBy().getCurrentEmployer()) {
            if (null != primaryInvolvedParty.getIsPlayedBy().getCurrentEmployer().getName()) {
                request.getPartyUpdData().getKYCNonCorePartyUpdData().setEmployerNm(primaryInvolvedParty.getIsPlayedBy().getCurrentEmployer().getName());
            }
            if (!org.springframework.util.CollectionUtils.isEmpty(primaryInvolvedParty.getIsPlayedBy().getCurrentEmployer().getHasPostalAddress())) {
                request.getPartyUpdData()
                        .setEmployersAddrUpdData(employerAddressDataFactory.generateEmployerAddress(primaryInvolvedParty.getIsPlayedBy()
                                .getCurrentEmployer()
                                .getHasPostalAddress()
                                .get(0)
                                .getUnstructuredAddress()));
            }
        }
        EvidenceUpdDataType evidenceUpdDataType = new EvidenceUpdDataType();

        if ("ACCEPT".equalsIgnoreCase(eidvStatus) && assessmentEvidNull) {
            AddrEvidUpdDataType addrEvidUpdDataType = new AddrEvidUpdDataType();
            PartyEvidUpdDataType partyEvidUpdDataType = new PartyEvidUpdDataType();
            String[] addressStrengtharray = addressStrength.split(":");
            addrEvidUpdDataType.setAddrEvidTypeCd(addressStrengtharray[0]);
            addrEvidUpdDataType.setAddrEvidPurposeCd(addressStrengtharray[1]);

            String[] identityStrengtharray = identityStrength.split(":");
            partyEvidUpdDataType.setPartyEvidTypeCd(identityStrengtharray[0]);
            partyEvidUpdDataType.setPartyEvidPurposeCd(identityStrengtharray[1]);
            evidenceUpdDataType.getAddrEvidUpdData().add(addrEvidUpdDataType);
            evidenceUpdDataType.getPartyEvidUpdData().add(partyEvidUpdDataType);


        }

        request.getPartyUpdData().setEvidenceUpdData(evidenceUpdDataType);
        if (null != primaryInvolvedParty.getIsPlayedBy().getNationality()) {
            request.getPartyUpdData().setKYCPartyUpdData(new KYCPartyUpdDataType());
            request.getPartyUpdData().getKYCPartyUpdData().setFrstNtn(new FrstNtnType());
            request.getPartyUpdData().getKYCPartyUpdData().getFrstNtn().setFirstNationltyCd(primaryInvolvedParty.getIsPlayedBy().getNationality());
        }

        return request;
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

    public RetrieveProductConditionsRequest rpcRequest(RequestHeader requestHeader) {
        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();

        rpcRequest.setHeader(requestHeader);
        rpcRequest.getHeader().setContactPointId("0000777505");

        Product product = new Product();
        product.setProductPropositionIdentifier("42");

        product.getProductoffer().add(0, new ProductOffer());
        ProductOffer productOffer = new ProductOffer();
        productOffer.setOfferType(null);
        product.getProductoffer().add(productOffer);

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

    public RetrieveProductConditionsRequest rpcRequestWhenApplicationStatusIsUnscored(RequestHeader requestHeader) {
        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();

        rpcRequest.setHeader(requestHeader);
        rpcRequest.getHeader().setContactPointId("0000777505");

        Product product = new Product();
        product.setProductPropositionIdentifier("42");

        product.getProductoffer().add(0, new ProductOffer());
        ProductOffer productOffer = new ProductOffer();
        productOffer.setOfferType(null);
        productOffer.setProdOfferIdentifier("23120");
        product.getProductoffer().add(productOffer);

        rpcRequest.setProduct(product);
        return rpcRequest;

    }

    public RetrieveProductConditionsResponse rpcResponse() {
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
        product1.getProductoffer().add(productOffer1);
        //product1.getExternalSystemProductIdentifier().get(0).setProductIdentifier();
        rpcResponse.getProduct().add(product1);

        Product product2 = new Product();
        product2.setProductIdentifier("20052");
        product2.setProductType("3");
        product2.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        ProductOffer productOffer2 = new ProductOffer();
        CurrencyAmount currencyAmount2 = new CurrencyAmount();
        currencyAmount2.setAmount(BigDecimal.valueOf(200));
        productOffer2.setOfferAmount(currencyAmount);
        product2.getProductoffer().add(productOffer2);
        rpcResponse.getProduct().add(product2);

        Product product3 = new Product();
        product3.setProductIdentifier("20053");
        product3.setProductType("1");
        rpcResponse.getProduct().add(product3);
        return rpcResponse;
    }

    public RetrieveProductConditionsResponse rpcResponse2() {
        RetrieveProductConditionsResponse rpcResponse = new RetrieveProductConditionsResponse();
        rpcResponse.setIsGauranteedOfferAvailable(true);
        ResponseHeader header = new ResponseHeader();
        header.setArrangementId("1");
        rpcResponse.setHeader(header);

        Product product1 = new Product();
        product1.setProductIdentifier("20042");
        ProductOffer productOffer1 = new ProductOffer();
        productOffer1.setProdOfferIdentifier("20042");
        productOffer1.getPricepoint().add(new PricePoint());
        productOffer1.getPricepoint().get(0).setSystemCode("00107");
        productOffer1.getPricepoint().get(0).setExternalSystemIdentifier("200");
        productOffer1.getTemplate().add(new Template());
        productOffer1.getTemplate().get(0).setTemplateIdentifier("1");
        productOffer1.getTemplate().get(0).setSystemCode("00001");
        productOffer1.getTemplate().get(0).setExternalTemplateIdentifier("CCA_Clarity");
        productOffer1.setOfferType("2004");
        productOffer1.setOfferAmount(new CurrencyAmount());
        productOffer1.getOfferAmount().setAmount(BigDecimal.valueOf(100.00).setScale(2));
        product1.getProductoffer().add(productOffer1);
        product1.setProductPropositionIdentifier("42");
        rpcResponse.getProduct().add(product1);

        Product product2 = new Product();
        product2.setProductIdentifier("20040");
        ProductOffer productOffer2 = new ProductOffer();
        productOffer2.setProdOfferIdentifier("20042");
        productOffer2.getPricepoint().add(new PricePoint());
        productOffer2.getPricepoint().get(0).setSystemCode("00107");
        productOffer2.getPricepoint().get(0).setExternalSystemIdentifier("400");
        productOffer2.setOfferType("2002");
        productOffer2.setOfferAmount(new CurrencyAmount());
        productOffer2.getOfferAmount().setAmount(BigDecimal.valueOf(200.00).setScale(2));
        product2.getProductoffer().add(productOffer2);
        product2.setProductPropositionIdentifier("32");
        rpcResponse.getProduct().add(product2);

        Product product3 = new Product();
        product3.setProductIdentifier("20043");
        ProductOffer productOffer3 = new ProductOffer();
        productOffer3.setProdOfferIdentifier("20043");
        productOffer3.getPricepoint().add(new PricePoint());
        productOffer3.getPricepoint().get(0).setSystemCode("00107");
        productOffer3.getPricepoint().get(0).setExternalSystemIdentifier("600");
        productOffer3.getTemplate().add(new Template());
        productOffer3.getTemplate().get(0).setTemplateIdentifier("1");
        productOffer3.getTemplate().get(0).setSystemCode("00001");
        productOffer3.getTemplate().get(0).setExternalTemplateIdentifier("CCA_Generic");
        productOffer3.setOfferType("2002");
        productOffer3.setOfferAmount(new CurrencyAmount());
        productOffer3.getOfferAmount().setAmount(BigDecimal.valueOf(300.00).setScale(2));
        product3.getProductoffer().add(productOffer3);
        product3.setProductPropositionIdentifier("33");
        rpcResponse.getProduct().add(product3);
        return rpcResponse;
    }

    public RetrieveProductConditionsResponse rpcResponseForUnscored() {
        RetrieveProductConditionsResponse rpcResponse = new RetrieveProductConditionsResponse();
        rpcResponse.setIsGauranteedOfferAvailable(true);
        ResponseHeader header = new ResponseHeader();
        header.setArrangementId("1");
        rpcResponse.setHeader(header);

        Product product1 = new Product();
        product1.setProductIdentifier("20042");
        ProductOffer productOffer1 = new ProductOffer();
        productOffer1.setProdOfferIdentifier("71");
        productOffer1.getTemplate().add(new Template());
        productOffer1.getTemplate().get(0).setTemplateIdentifier("1");
        productOffer1.getTemplate().get(0).setSystemCode("00001");
        productOffer1.getTemplate().get(0).setExternalTemplateIdentifier("CCA_Clarity");
        productOffer1.setOfferType("2004");
        product1.getProductoffer().add(productOffer1);
        product1.setProductPropositionIdentifier("42");
        rpcResponse.getProduct().add(product1);
        return rpcResponse;
    }

    public F424Resp createF424Response2(int reasonCode) {
        F424Resp response = new F424Resp();
        response.setF424Result(new F424Result());
        response.getF424Result().setResultCondition(new ResultCondition());
        response.getF424Result().getResultCondition().setReasonCode(reasonCode);
        response.getF424Result().getResultCondition().setReasonText("reasonText");
        return response;
    }

    public F424Req createF424Req() {

        F424Req f424Req = new F424Req();
        f424Req.setMaxRepeatGroupQy(1);
        return f424Req;
    }

    public void createAbandonDeclineReasons() {
        AbandonDeclineReasons declineReasons1 = new AbandonDeclineReasons("109", "Customer no longer wants to progress with the application");
        AbandonDeclineReasons declineReasons2 = new AbandonDeclineReasons("110", "Wrong Details Entered");
        AbandonDeclineReasons declineReasons3 = new AbandonDeclineReasons("111", "Technical Error");
        AbandonDeclineReasons declineReasons4 = new AbandonDeclineReasons("101", "abc");
        AbandonDeclineReasons declineReasons5 = new AbandonDeclineReasons("102", "ASm Decline");
        AbandonDeclineReasons declineReasons6 = new AbandonDeclineReasons("103", "dsd");
        AbandonDeclineReasons declineReasons7 = new AbandonDeclineReasons("104", "ds");
        AbandonDeclineReasons declineReasons8 = new AbandonDeclineReasons("105", "dfrdfdf");
        AbandonDeclineReasons declineReasons9 = new AbandonDeclineReasons("106", "dssd");
        AbandonDeclineReasons declineReasons10 = new AbandonDeclineReasons("107", "ds");
        AbandonDeclineReasons declineReasons11 = new AbandonDeclineReasons("108", "ds");


        abandonDeclineReasonDao.save(declineReasons1);
        abandonDeclineReasonDao.save(declineReasons2);
        abandonDeclineReasonDao.save(declineReasons3);
        abandonDeclineReasonDao.save(declineReasons4);
        abandonDeclineReasonDao.save(declineReasons5);
        abandonDeclineReasonDao.save(declineReasons6);
        abandonDeclineReasonDao.save(declineReasons7);
        abandonDeclineReasonDao.save(declineReasons8);
        abandonDeclineReasonDao.save(declineReasons9);
        abandonDeclineReasonDao.save(declineReasons10);
        abandonDeclineReasonDao.save(declineReasons11);

        abandonDeclineReasonDao.findAll();

    }

    private void setStructuredUnstructuredAddress(List<PostalAddress> postalAddressList, AddressUpdDataType addressUpdDataType) {
        for (PostalAddress postalAddress : postalAddressList) {
            if ("CURRENT".equalsIgnoreCase(postalAddress.getStatusCode())) {
                if (null != postalAddress.getUnstructuredAddress()) {
                    addressUpdDataType.setUnstructuredAddress(new UnstructuredAddressFactory().generateUnstructuredAddress(postalAddress.getUnstructuredAddress(), postalAddress.isIsBFPOAddress()));
                }
                if (null != postalAddress.getStructuredAddress()) {
                    addressUpdDataType.setStructuredAddress(new StructuredAddressFactory().generateStructuredAddress(postalAddress.getStructuredAddress()));
                }
            }
        }
    }

    public F447Resp createF447ResponseForNewCustomer(int reasonCode) {

        F447Resp response = new F447Resp();
        response.setF447Result(new F447Result());
        response.getF447Result().setResultCondition(new ResultCondition());
        response.getF447Result().getResultCondition().setReasonCode(reasonCode);
        response.getF447Result().getResultCondition().setReasonText("abc");
        response.setCIDPersId("1234");
        return response;
    }
}
