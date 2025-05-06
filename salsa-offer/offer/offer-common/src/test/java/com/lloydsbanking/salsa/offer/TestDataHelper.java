package com.lloydsbanking.salsa.offer;

import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061RequestBuilder;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydsbanking.salsa.offer.createinvolvedparty.convert.UnstructuredAddressFactory;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.identify.convert.EnquirePartyIdRequestFactory;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Req;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Result;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Result;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Req;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Result;
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
import lloydstsb.schema.personal.customer.partyidandv.IdentifyPartyResp;
import lloydstsb.schema.personal.customer.partyidandv.serviceobjects.EvaluationEvidence;
import lloydstsb.schema.personal.customer.partyidandv.serviceobjects.IdentifyPartyOutput;
import lloydstsb.schema.personal.customer.partyidandv.serviceobjects.ReferralReason;
import lloydstsb.schema.personal.customer.partyidandv.serviceobjects.ReferralReasons;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class TestDataHelper {
    public static final String TEST_RETAIL_CHANNEL_ID = "LTB";

    private static final short EXTERNAL_SYS_ID = 19;

    private static final String CLOSURE_PERIOD_MONTHS_DR = "000";

    private static final String CLOSED_ONLY_IN = "0";

    private static final String CUSTOMER_CONSENT_IN = "2";

    public static final Short DEFAULT_PARTY_EXT_SYS_ID = 2;

    public static final Short PARTY_EXT_SYS_ID = 1;

    DepositArrangementBuilder depositArrangementBuilder = new DepositArrangementBuilder();

    AssociatedProductBuilder associatedProductBuilder = new AssociatedProductBuilder();

    InvolvedPartyBuilder involvedPartyBuilder = new InvolvedPartyBuilder();

    PostalAddressBuilder postalAddressBuilder = new PostalAddressBuilder();

    TelephoneNumberBuilder telephoneNumberBuilder = new TelephoneNumberBuilder();

    IndividualBuilder individualBuilder = new IndividualBuilder();


    public RequestHeader createOpaPcaRequestHeader(String channelId) {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCA")
                .channelId(channelId)
                .interactionId("vbww2yofqtcx1qbzw8iz4gm19")
                .serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...")
                .contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer")
                .bapiInformation(channelId, "interactionId", "AAGATEWAY", "ns4")
                .securityHeader("ns4", "UNAUTHSALE")
                .build();
        return header;
    }

    public DepositArrangement createDepositArrangement() {
        DepositArrangement depositArrangement = depositArrangementBuilder.arrangementType()
                .associatedProduct(createAssociatedProduct())
                .initiatedThrough(createChannel())
                .primaryInvolvedParty(involvedPartyBuilder.partyIdentifier("AAGATEWAY")
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
                .affiliateDetails(createAffiliateDetailsList())
                .conditions(createRuleConditionList())
                .marketingPreferenceByEmail(false)
                .marketingPreferenceByPhone(false)
                .marketingPreferenceByMail(false)
                .build();
        return depositArrangement;
    }

    private Product createAssociatedProduct() {
        Product associatedProduct = associatedProductBuilder.productIdentifier("20198")
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
        Individual isPlayedBy = individualBuilder.individualName(createIndividualName())
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

    public Individual createIsPlayedByWithOccupationType() {
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        Individual isPlayedBy = individualBuilder.individualName(createIndividualName())
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
                .occupation("1")
                .build();
        return isPlayedBy;
    }

    public List<RuleCondition> createRuleConditionList() {
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
        TelephoneNumber telephoneNumber = telephoneNumberBuilder.countryPhoneCode("44").phoneNumber("7440696125").telephoneType("7").deviceType("Mobile").build();
        List<TelephoneNumber> telephoneNumberList = new ArrayList<TelephoneNumber>();
        telephoneNumberList.add(0, telephoneNumber);
        return telephoneNumberList;
    }

    public List<PostalAddress> createPostalAddressList() {
        PostalAddress postalAddress = postalAddressBuilder.durationOfStay("0707")
                .isBFPOAddressBuilder(false)
                .isPAFFormat(true)
                .statusCode("CURRENT")
                .structuredAddress(createStructuredAddress())
                .build();
        List<PostalAddress> postalAddressList = new ArrayList<PostalAddress>();
        postalAddressList.add(0, postalAddress);
        return postalAddressList;
    }

    public List<PostalAddress> createPostalAddressListForUnstructuredAddress() {
        PostalAddress postalAddress = postalAddressBuilder.durationOfStay("0707")
                .isBFPOAddressBuilder(false)
                .isPAFFormat(true)
                .statusCode("CURRENT")
                .unstructuredAddress(createUnstructuredAddressWithAllAddressLines())
                .build();
        List<PostalAddress> postalAddressList = new ArrayList<PostalAddress>();
        postalAddressList.add(0, postalAddress);
        return postalAddressList;
    }

    public UnstructuredAddressType generateUnstructuredType(UnstructuredAddress unstructuredAddress, boolean bfpoAddressIndicator) {
        return new UnstructuredAddressFactory().generateUnstructuredAddress(unstructuredAddress, bfpoAddressIndicator);
    }

    public StructuredAddress createStructuredAddress() {
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
        productPartyData2.setSellerLegalEntCd("IIL");
        productPartyData2.setAmdEffDt("05052015");
        productPartyData2.setProductHeldOpenDt("05052015");
        productPartyData2.setProdHeldStatusCd("001");
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

    public List<PostalAddress> createPostalAddressListWhenPaffIsFalse() {
        PostalAddress postalAddress = postalAddressBuilder.durationOfStay("0707")
                .isBFPOAddressBuilder(false)
                .isPAFFormat(false)
                .statusCode("CURRENT")
                .unstructuredAddress(createUnstructuredAddress())
                .build();
        List<PostalAddress> postalAddressList = new ArrayList<PostalAddress>();
        postalAddressList.add(0, postalAddress);
        return postalAddressList;
    }

    public UnstructuredAddress createUnstructuredAddress() {
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1("1");
        unstructuredAddress.setPointSuffix("12");
        return unstructuredAddress;
    }

    public UnstructuredAddress createUnstructuredAddressWithAllAddressLines() {
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1("1");
        unstructuredAddress.setAddressLine2("2");
        unstructuredAddress.setAddressLine3("3");
        unstructuredAddress.setAddressLine4("4");
        unstructuredAddress.setAddressLine5("5");
        unstructuredAddress.setAddressLine6("6");
        unstructuredAddress.setAddressLine7("7");
        unstructuredAddress.setPointSuffix("12");
        return unstructuredAddress;
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

        List auditDataList = new ArrayList<AuditData>();
        AuditData auditData = new AuditData();
        auditData.setAuditType("type");
        auditDataList.add(auditData);
        customer.getAuditData().addAll(auditDataList);

        return customer;

    }

    public List<Product> createExistingProducts() {
        List<Product> existingProducts = new ArrayList<>();
        Product product = new Product();
        product.setProductIdentifier("id");
        product.setBrandName("brand");
        product.setGuaranteedOfferCode("offerCd");
        existingProducts.add(product);
        return existingProducts;
    }

    public DepositArrangement generateOfferProductArrangementPCARequest2() throws ParseException, DatatypeConfigurationException {
        DepositArrangement depositArrangement = createDepositArrangement();
        Individual isPlayedBy = createIsPlayedBy();
        isPlayedBy.setMaritalStatus("003");
        isPlayedBy.setEmploymentStatus("000");
        isPlayedBy.setGender("003");
        isPlayedBy.setIsStaffMember(true);
        isPlayedBy.setOccupation("occupation");
        isPlayedBy.setNumberOfDependents(BigInteger.valueOf(3));
        isPlayedBy.setResidentialStatus("resiStatus");
        isPlayedBy.setCurrentYearOfStudy(BigInteger.valueOf(1992));
        Date sampleDate = (new SimpleDateFormat("yyyyMMdd")).parse("19921213");
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(sampleDate);
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);
        isPlayedBy.setUKResidenceStartDate(calendar);
        isPlayedBy.setBirthDate(calendar);
        isPlayedBy.setAnticipateDateOfGraduation("1991");

        List<TelephoneNumber> telephoneNumberList = new ArrayList<>();
        TelephoneNumber telephoneNumber = new TelephoneNumber();
        telephoneNumber.setTelephoneType("1");
        telephoneNumber.setAreaCode("areaCode");
        telephoneNumber.setCountryPhoneCode("countryCode");
        telephoneNumber.setPhoneNumber("telephoneNumber");
        telephoneNumberList.add(telephoneNumber);

        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setDurationofStay("0308");
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1("addressline1");
        unstructuredAddress.setAddressLine2("addressline2");
        unstructuredAddress.setAddressLine3("addressline3");
        unstructuredAddress.setAddressLine4("addressline4");
        unstructuredAddress.setAddressLine5("addressline5");
        unstructuredAddress.setAddressLine6("addressline6");
        unstructuredAddress.setAddressLine7("addressline7");
        unstructuredAddress.setAddressLine8("UK");
        postalAddress.setIsPAFFormat(false);
        postalAddress.setUnstructuredAddress(unstructuredAddress);
        postalAddress.setIsBFPOAddress(true);
        List<PostalAddress> postalAddressList = new ArrayList<>();
        postalAddressList.add(postalAddress);

        Customer primaryInvolvedParty = new Customer();
        primaryInvolvedParty.setSourceSystemId("1234");
        primaryInvolvedParty.setCustomerIdentifier("122323");
        primaryInvolvedParty.setCbsCustomerNumber("cbsCustomerNo");
        primaryInvolvedParty.setCidPersID("5678");
        primaryInvolvedParty.setCustomerSegment("cusseg");
        primaryInvolvedParty.setOtherBankDuration("0708");
        primaryInvolvedParty.setIsPlayedBy(isPlayedBy);
        primaryInvolvedParty.getTelephoneNumber().addAll(telephoneNumberList);
        primaryInvolvedParty.getPostalAddress().addAll(postalAddressList);
        depositArrangement.setArrangementId("arrangementId");
        depositArrangement.setPrimaryInvolvedParty(primaryInvolvedParty);
        depositArrangement.getAssociatedProduct().setBrandName("associatedproductbrandname");
        depositArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().addAll(createExtSysProdId());
        depositArrangement.setFinancialInstitution(new Organisation());
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());

        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setAreaCode("areaCd");
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setAreaCode("regionCd");
        depositArrangement.setAccountPurpose("1");
        depositArrangement.getExistingProducts().addAll(createExistingProducts());
        return depositArrangement;
    }

    public List<PostalAddress> createPostalAddList() {
        PostalAddress postalAddress = postalAddressBuilder.durationOfStay("0707")
                .isBFPOAddressBuilder(false)
                .isPAFFormat(true)
                .statusCode("CURRENT")
                .structuredAddress(createStructuredAdd())
                .build();
        List<PostalAddress> postalAddressList = new ArrayList<PostalAddress>();
        postalAddressList.add(0, postalAddress);
        return postalAddressList;
    }

    private StructuredAddress createStructuredAdd() {
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("23");
        structuredAddress.setHouseNumber("23");
        structuredAddress.setPostCodeIn("9EQ");
        structuredAddress.setPostCodeOut("SE1");
        structuredAddress.setPostTown("London");
        structuredAddress.setPointSuffix("1E");
        structuredAddress.setDistrict("district");
        structuredAddress.setPostTown("town");
        structuredAddress.setCounty("county");
        structuredAddress.setBuilding("building");
        List<String> addressLinePaf = new ArrayList<String>();
        addressLinePaf.add(0, "PARK STREET");
        structuredAddress.getAddressLinePAFData().addAll(addressLinePaf);
        return structuredAddress;
    }


    public Customer primaryInvolvedParty() throws ParseException, DatatypeConfigurationException {

        DatatypeFactory df = DatatypeFactory.newInstance().newInstance();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(new SimpleDateFormat("yyyyMMdd").parse("19910404").getTime());
        XMLGregorianCalendar birthDate = df.newXMLGregorianCalendar(gc);

        GregorianCalendar gc1 = new GregorianCalendar();
        gc1.setTimeInMillis(new SimpleDateFormat("yyyyMMdd").parse("20150404").getTime());
        XMLGregorianCalendar visaExpiryDate = df.newXMLGregorianCalendar(gc1);

        Customer customer = new Customer();
        customer.setCustomerIdentifier("12");
        customer.setIsPlayedBy(new Individual());
        customer.getIsPlayedBy().setEmploymentStatus("456");
        customer.setSourceSystemId("1");
        customer.setCidPersID("2");
        customer.setCbsCustomerNumber("3");
        customer.getIsPlayedBy().setBirthDate(birthDate);
        customer.setCustomerSegment("200");
        customer.getIsPlayedBy().setVisaExpiryDate(visaExpiryDate);
        return customer;
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

    public F205Resp createF205Response(int reasonCode) {
        F205Resp response = new F205Resp();
        response.setF205Result(new F205Result());
        response.getF205Result().setResultCondition(new ResultCondition());
        response.getF205Result().getResultCondition().setReasonCode(reasonCode);
        response.getF205Result().getResultCondition().setReasonText("abc");
        return response;
    }

    public F204Resp createF204Response(int reasonCode) {
        F204Resp response = new F204Resp();
        response.setF204Result(new F204Result());
        response.getF204Result().setResultCondition(new ResultCondition());
        response.getF204Result().getResultCondition().setReasonCode(reasonCode);
        response.getF204Result().getResultCondition().setReasonText("reasonText");
        return response;
    }

    public F204Req createF204Req() {

        F204Req f204Req = new F204Req();
        f204Req.setMaxRepeatGroupQy(1);
        return f204Req;
    }

    public StructuredAddressType generateStructuredAddress(StructuredAddress address) {
        StructuredAddressType structuredAddressType = new StructuredAddressType();
        structuredAddressType.setOrganisationNm(address.getOrganisation());
        structuredAddressType.setSubBuildingNm(address.getSubBuilding());
        structuredAddressType.setBuildingNm(address.getBuildingNumber());
        structuredAddressType.setAddressDistrictNm(address.getDistrict());
        structuredAddressType.setAddressPostTownNm(address.getPostTown());
        structuredAddressType.setAddressCountyNm(address.getCountry());
        structuredAddressType.setOutPostCd(address.getPostCodeOut());
        structuredAddressType.setInPostCd(address.getPostCodeIn());
        structuredAddressType.setDelivPointSuffixCd(address.getPointSuffix());
        structuredAddressType.getAddressLinePaf().add(getAddressTypePAF(address.getAddressLinePAFData()));
        return structuredAddressType;
    }

    private AddressLinePafType getAddressTypePAF(List<String> addressLinePAFData) {
        AddressLinePafType addressLinePafType = new AddressLinePafType();
        for (String addressLine : addressLinePAFData) {
            addressLinePafType.setAddressLinePafTx(addressLine);
        }
        return addressLinePafType;
    }

    public IndividualName setIndividualName(int middleNameLength) {
        IndividualName individualName = new IndividualName();
        individualName.setFirstName("Amita");
        individualName.setLastName("Jaiswal");
        individualName.setSalutation("Mrs");
        if (middleNameLength == 1) {
            individualName.getMiddleNames().add("sunil");
        }
        if (middleNameLength == 2) {
            List<String> middlenameList = new ArrayList<>();
            middlenameList.add("sunil");
            middlenameList.add("kumar");
            individualName.getMiddleNames().addAll(middlenameList);
        }
        individualName.setPrefixTitle("prefix");
        return individualName;

    }

    public CurrencyAmount createCurrencyAmount(BigDecimal amount) {
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(amount);
        return currencyAmount;
    }

    public RequestHeader createOpaccRequestHeader(String channelId) {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCC").channelId(channelId).interactionId("vbww2yofqtcx1qbzw8iz4gm19").serviceRequest("ns4", "OfferProductArrangement", "10.1.1.1", "...").contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer").bapiInformation(channelId, "interactionId", "AAGATEWAY", "ns4").securityHeader("ns4", "UNAUTHSALE").build();
        header.setContactPointId(channelId);
        return header;
    }


    public FinanceServiceArrangement createFinanceServiceArrangement() {
        AccessToken accessToken = new AccessToken();
        accessToken.setMemorableInfo("aaa");
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangementBuilder().arrangementType().associatedProduct(createAssociatedProductCC()).initiatedThrough(createInitiatedThroughCC()).campaignCode("HXAA01B001RPCC5").primaryInvolvedParty(new InvolvedPartyBuilder().partyIdentifier("+00883965244").emailAddress("a@a.com").postalAddress(createPostalAddressListCC()).sourceSystemId("2").customerIdentifier("1985856187").existingSortCode("110135").existingAccountNumber("50005769").userType("1001").internalUserIdentifier("BE205960").partyRole("0001").customerSegment("1").accessToken(accessToken).hasExistingCreditCard(false).isPlayedBy(createIsPlayedByCC()).build()).marketingPreferenceBySMS(true).affiliateDetails(createAffiliateDetailsListCC()).applicationType("10001").affiliateId("000").conditions(createRuleConditionListCC()).marketingPreferenceByEmail(true).marketingPreferenceByPhone(true).marketingPreferenceByMail(true).marketingPreferenceIndicator(true).balanceTransferAmount(createCurrencyAmount(new BigDecimal(0))).build();
        return financeServiceArrangement;
    }

    private Product createAssociatedProductCC() {
        Product associatedProduct = new AssociatedProductBuilder().productIdentifier("20042").guaranteedOfferCode("N").externalSystemProductIdentifier(createExtSysProdId()).productOffer(createProductOfferCC()).productName("Clarity Credit Card").productPropositionIdentifier("42").build();
        return associatedProduct;
    }

    private Channel createInitiatedThroughCC() {
        Channel initiatedThrough = new Channel();
        initiatedThrough.setChannelCode("004");
        initiatedThrough.setSubChannelCode("001");
        return initiatedThrough;
    }

    public List<PostalAddress> createPostalAddressListCC() {
        PostalAddress postalAddress = new PostalAddressBuilder().durationOfStay("0505").isPAFFormat(false).statusCode("CURRENT").unstructuredAddress(createUnstructuredAddressCC()).build();
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
        Individual isPlayedBy = new IndividualBuilder().individualName(createIndividualName()).residentialStatus("002").birthDate(datatypeFactory.newXMLGregorianCalendar("1948-01-01T06:40:56.046Z")).nationality("GBR").maritalStatus("001").gender("001").employmentStatus("003").currentEmploymentDuration("0505").grossAnnualIncome(createCurrencyAmount(new BigDecimal(2555))).occupation("1").currentEmployer(currentEmployer).build();
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


    public F424Resp createF424Response(int reasonCode) {
        F424Resp response = new F424Resp();
        response.setF424Result(new F424Result());
        response.getF424Result().setResultCondition(new ResultCondition());
        response.getF424Result().getResultCondition().setReasonCode(reasonCode);
        response.getF424Result().getResultCondition().setReasonText("abc");
        return response;
    }

    public IdentifyPartyResp getIdentifyPartyResp() {
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

        ReferralReasons referralReasons = new ReferralReasons();
        ReferralReason reason = new ReferralReason();
        reason.setCode("code");
        reason.setDescription("Desc");
        referralReasons.getReferralReason().add(reason);
        partyOutput.setReferralReasons(referralReasons);
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

    public F424Resp createF424Response2(int reasonCode) {
        F424Resp response = new F424Resp();
        response.setF424Result(new F424Result());
        response.getF424Result().setResultCondition(new ResultCondition());
        response.getF424Result().getResultCondition().setReasonCode(reasonCode);
        response.getF424Result().getResultCondition().setReasonText("reasonText");
        return response;
    }

    public FinanceServiceArrangement createFinanceServiceArrangementCC() {
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

    public F424Req createF424Req() {

        F424Req f424Req = new F424Req();
        f424Req.setMaxRepeatGroupQy(1);
        return f424Req;
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

    private AddressAuditUpdData1Type createAddressAuditUpdData() {

        AddressAuditUpdData1Type addressAuditUpdData1Type = new AddressAuditUpdData1Type();
        addressAuditUpdData1Type.setAuditDt(01012015l);
        addressAuditUpdData1Type.setAuditTm(345678l);
        return addressAuditUpdData1Type;
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



}
