package com.lloydsbanking.salsa.activate;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.downstream.application.converter.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.model.TmxDetails;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydsbanking.salsa.soap.asm.f425.objects.*;
import com.lloydsbanking.salsa.soap.fs.account.StAddress;
import com.lloydsbanking.salsa.soap.fs.account.StAddressLine;
import com.lloydsbanking.salsa.soap.fs.application.StAccount;
import com.lloydsbanking.salsa.soap.fs.application.StError;
import com.lloydsbanking.salsa.soap.fs.application.StHeader;
import com.lloydsbanking.salsa.soap.fs.application.StParty;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Resp;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Result;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Req;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Resp;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Result;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.StPartyRelData;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Resp;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Result;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.CommsPrefData;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Req;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.PartyInfo;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.*;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsRequest;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.RequestHeader;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ip.Individual;
import com.lloydsbanking.salsa.soap.soa.servicearrangement.CreateServiceArrangementRequest;
import com.lloydstsb.ib.wsbridge.account.StB765BAccCreateAccount;
import com.lloydstsb.ib.wsbridge.application.StB750AAppPerCCRegCreate;
import com.lloydstsb.ib.wsbridge.application.StB750BAppPerCCRegCreate;
import com.lloydstsb.ib.wsbridge.application.StB751AAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ArrangementAssociation;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ArrangementType;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.BenefitArrangement;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ServiceArrangement;
import com.lloydstsb.schema.enterprise.ifwxml_common.ObjectReference;
import com.lloydstsb.schema.enterprise.ifwxml_event.Activity;
import com.lloydstsb.schema.enterprise.ifwxml_involvedparty.ContactPreference;
import com.lloydstsb.schema.enterprise.ifwxml_involvedparty.ElectronicAddress;
import com.lloydstsb.schema.enterprise.ifwxml_involvedparty.InvolvedPartyNamePrefixType;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import com.lloydstsb.schema.personal.serviceplatform.tms.CreateTaskResponse;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreationResponse;
import com.lloydstsb.schema.personal.serviceplatform.tms.v0001.TMSRoutingInformationBO;
import com.synectics_solutions.sira.schemas.realtime.core.v1_0.realtimeresulttype4.RealtimeResultType4Type;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.SubmitWorkItemResponse;
import lib_sim_bo.businessobjects.*;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
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

    public static final int EXTERNAL_SYS_SALSA = 19;

    public static final List<String> groupCodeList = Arrays.asList(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE, ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE);

    public static final String OAP_SOURCE_SYSTEM_IDENTIFIER = "3";

    public static final String DB_EVENT_SOURCE_SYSTEM_IDENTIFIER = "2";

    @Autowired
    public BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter;

    @Autowired
    public HeaderRetriever headerRetriever;

    public lib_sim_gmo.messages.RequestHeader createApaRequestHeader() {

        RequestHeaderBuilder requestHeaderBuilder = new RequestHeaderBuilder();
        return requestHeaderBuilder.businessTransaction(TEST_BUSINESS_TRANSACTION).channelId(TEST_RETAIL_CHANNEL_ID).interactionId(TEST_INTERACTION_ID).bapiInformation(TEST_RETAIL_CHANNEL_ID, TEST_INTERACTION_ID, TEST_OCIS_ID, "ns5").securityHeader("lgsm", TEST_CUSTOMER_ID).serviceRequest("ns4", TEST_BUSINESS_TRANSACTION, "10.245.182.105", TEST_MESSAGE_ID).contactPoint("ns4", "003", TEST_CONTACT_POINT_ID, "Internet Banking", "Browser", "127.0.0.1", "Customer").build();
    }

    public SecurityHeaderType getSecurityHeaderTypeFromRequestHeader(lib_sim_gmo.messages.RequestHeader requestHeader) {
        return headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
    }

    public ServiceRequest getServiceRequestFromRequestHeader(lib_sim_gmo.messages.RequestHeader requestHeader) {
        return headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders());
    }

    public List<ReferenceDataLookUp> createLookupData() {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 513L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "1", "Purpose of Account", 1091L, "SPORI", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("PURPOSE_OF_ACCOUNT", "2", "Purpose of Account", 1092L, "BIEXP", "LTB", 1L));

        return referenceDataLookUpList;
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

    public StB765BAccCreateAccount createResponseB765(String accountNumber, String sortCode) {
        StB765BAccCreateAccount resp = new StB765BAccCreateAccount();
        com.lloydsbanking.salsa.soap.fs.account.StAccount stAccount = new com.lloydsbanking.salsa.soap.fs.account.StAccount();
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

    public DepositArrangement createDepositArrangementResp() {
        DepositArrangement depositArrangement = new DepositArrangement();
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
        lib_sim_bo.businessobjects.Individual individual1 = new lib_sim_bo.businessobjects.Individual();
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

        lib_sim_bo.businessobjects.Individual individual = new lib_sim_bo.businessobjects.Individual();
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

        depositArrangement.setIsOverdraftRequired(false);
        depositArrangement.setAccountDetails(new AccountDetails());
        return depositArrangement;
    }

    public List<ReferenceDataLookUp> createChannelIdLookupData() {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(new ReferenceDataLookUp("Cnt_Pnt_Prtflio", "0000777505", "Display Contact_Point_Portfolio", 26L, "CONTACT_POINT_ID", "LTB", 1L));

        return referenceDataLookUpList;
    }

    public DepositArrangement createDepositArrangementAfterPAMCall() {
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

        lib_sim_bo.businessobjects.Individual isPlayedBy = new lib_sim_bo.businessobjects.Individual();
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

        lib_sim_bo.businessobjects.Individual individual = new lib_sim_bo.businessobjects.Individual();
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

    public StB750AAppPerCCRegCreate createB750Request(StHeader stHeader) {
        StB750AAppPerCCRegCreate b750Request = new StB750AAppPerCCRegCreate();
        b750Request.setStheader(stHeader);
        StAccount stAcc = new StAccount();
        stAcc.setProdtype("C");
        b750Request.setStacc(stAcc);
        b750Request.setPbktyp("N");
        b750Request.setSurname("Fghi");
        b750Request.setFirstname("AbcdeFHI");
        b750Request.setPostcode("SE19EQ");
        b750Request.setPwdEmergingChannel("****");
        b750Request.setTitle("Mr");
        b750Request.setDateOfBirth(createXMLGregorianCalendar(1935, 1, 1));
        b750Request.setEmailaddr("GalaxyTestAccount02@LloydsTSB.co.uk");
        b750Request.setMktgindEmail(0);
        return b750Request;

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
            return xcal;
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
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

    public F061Resp createF061RespForKYCNoFrstNtn() {
        F061Resp f061Resp = createF061Resp();
        f061Resp.getPartyEnqData().getKYCPartyData().setFrstNtn(null);
        return f061Resp;

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

    public ActivateProductArrangementRequest createApaRequestForPca() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());

        activateProductArrangementRequest.setProductArrangement(createDepositArrangement());
        activateProductArrangementRequest.setSourceSystemIdentifier(GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;

    }

    public DepositArrangement createDepositArrangement() {
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
        customer.setIsRegistrationSelected(false);
        customer.setPassword("password1234");
        depositArrangement.setPrimaryInvolvedParty(customer);

        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("779129");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);

        lib_sim_bo.businessobjects.Individual individual = new lib_sim_bo.businessobjects.Individual();
        individual.getIndividualName().add(new IndividualName());
        Customer jointPartyCustomer = new Customer();
        jointPartyCustomer.setIsPlayedBy(individual);
        depositArrangement.getJointParties().add(jointPartyCustomer);

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

    public ContactPoint getContactPointId(lib_sim_gmo.messages.RequestHeader requestHeader) {
        return headerRetriever.getContactPoint(requestHeader);
    }

    public ActivateProductArrangementRequest createApaRequestWithInvalidStatus() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangementForDBEvent());
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

        Customer customer = new Customer();
        customer.setIsRegistrationSelected(Boolean.FALSE);
        depositArrangement.setPrimaryInvolvedParty(customer);

        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("306521");

        Organisation organisation = new Organisation();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        depositArrangement.setFinancialInstitution(organisation);

        depositArrangement.setMarketingPreferenceByEmail(false);
        depositArrangement.setIsJointParty(false);
        depositArrangement.setApplicationSubStatus("1023");

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

    public ProductArrangement createProductArrangementAsAnInstanceOfFinanceServiceArrangement() {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setPrimaryInvolvedParty(createDepositArrangementAfterPAMCall().getPrimaryInvolvedParty());
        financeServiceArrangement.setArrangementType("SA");
        financeServiceArrangement.setMarketingPrefereceIndicator(true);
        ProductArrangement productArrangement = financeServiceArrangement;
        return productArrangement;
    }

    public ProductArrangement createProductArrangement() {
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setPrimaryInvolvedParty(createDepositArrangementAfterPAMCall().getPrimaryInvolvedParty());
        productArrangement.setArrangementType("SA");
        return productArrangement;
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
        extSysProdIdentifier.setSystemCode("2Vm");
        extSysProdIdentifierList.add(extSysProdIdentifier);
        product.setProductIdentifier("01000");
        product.setInstructionDetails(instructionDetails);
        product.setProductType("ABS");

        retrieveProductConditionsResponse.getProduct().add(product);
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier();

        return retrieveProductConditionsResponse;
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

        lib_sim_bo.businessobjects.Individual isPlayedBy = new lib_sim_bo.businessobjects.Individual();
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
        lib_sim_bo.businessobjects.Individual individual = new lib_sim_bo.businessobjects.Individual();
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

    public ActivateProductArrangementRequest createApaRequestByDBEvent() {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setHeader(createApaRequestHeader());
        activateProductArrangementRequest.getHeader().setChannelId("LTB");
        activateProductArrangementRequest.setProductArrangement(createDepositArrangementForDBEvent2());
        activateProductArrangementRequest.setSourceSystemIdentifier(DB_EVENT_SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }

    private ProductArrangement createDepositArrangementForDBEvent2() {
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

        lib_sim_bo.businessobjects.Individual individual = new lib_sim_bo.businessobjects.Individual();

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

    public StHeader createStHeader() {
        StHeader stHeader = new StHeader();
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
        StHeader stHeader = new StHeader();
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
        StAccount stAccount = new StAccount();
        stAccount.setHost("I");
        stAccount.setProdtype("AAGATEWAY");
        stAccount.setAccno("09545468");
        b751request.setStaccount(stAccount);
        return b751request;

    }

    public CreateServiceArrangementRequest createServiceArrangement() {
        CreateServiceArrangementRequest createServiceArrangementRequest = new CreateServiceArrangementRequest();
        ServiceArrangement serviceArrangement = new ServiceArrangement();
        ArrangementType arrangementType = new ArrangementType();
        arrangementType.setName("AVB");
        serviceArrangement.setHasArrangementType(arrangementType);
        ArrangementAssociation arrangementAssociation = new ArrangementAssociation();
        BenefitArrangement benefitArrangement = new BenefitArrangement();
        benefitArrangement.setName("ADDED_VALUE_BENEFIT");
        ArrangementType arrangementType1 = new ArrangementType();
        arrangementType1.setName("001");
        benefitArrangement.setHasArrangementType(arrangementType1);
        Activity activity = new Activity();
        com.lloydstsb.schema.enterprise.ifwxml_common.Category category = new com.lloydstsb.schema.enterprise.ifwxml_common.Category();
        category.setName("004");
        activity.setEventCategory(category);
        ObjectReference objectReference = new ObjectReference();
        objectReference.setId("PENDING_SELECTION");
        activity.setObjectReference(objectReference);
        benefitArrangement.getRelatedEvents().add(activity);
        arrangementAssociation.setRelatedArrangement(benefitArrangement);
        com.lloydstsb.schema.enterprise.ifwxml_involvedparty.Individual individual = new com.lloydstsb.schema.enterprise.ifwxml_involvedparty.Individual();
        com.lloydstsb.schema.enterprise.ifwxml_involvedparty.IndividualName individualName1 = new com.lloydstsb.schema.enterprise.ifwxml_involvedparty.IndividualName();
        individualName1.setFirstName("zdfgdfg");
        individualName1.setLastName("dfgdfg");
        individualName1.setPrefixTitle(InvolvedPartyNamePrefixType.MR);
        individual.getName().add(individualName1);
        ContactPreference contactPreference = new ContactPreference();
        ElectronicAddress electronicAddress = new ElectronicAddress();
        electronicAddress.setUserid("sdfgd@fgh.com");
        contactPreference.getContactPoints().add(electronicAddress);
        individual.getContactPreferences().add(contactPreference);
        com.lloydstsb.schema.enterprise.ifwxml_involvedparty.Customer customer = new com.lloydstsb.schema.enterprise.ifwxml_involvedparty.Customer();
        customer.setInvolvedParty(individual);
        serviceArrangement.getArrangementAssociations().add(arrangementAssociation);
        serviceArrangement.getRoles().add(customer);
        createServiceArrangementRequest.setServiceArrangement(serviceArrangement);
        return createServiceArrangementRequest;
    }

    public C241Req createC241Request(String extPartyIdTx, long partyId, long stPartyId, String relTypeCd) {
        C241Req c241Req = new C241Req();
        c241Req.setMaxRepeatGroupQy(1);
        c241Req.setExtSysId((short) EXTERNAL_SYS_SALSA);
        c241Req.setExtPartyIdTx(extPartyIdTx);
        c241Req.setPartyId(partyId);
        c241Req.setPartyExtSysId((short) EXTERNAL_SYS_SALSA);
        StPartyRelData stPartyRelData = new StPartyRelData();
        stPartyRelData.setPartyId(stPartyId);
        stPartyRelData.setExtSysId((short) EXTERNAL_SYS_SALSA);
        stPartyRelData.setRelTypeCd(relTypeCd);
        c241Req.setStPartyRelData(stPartyRelData);
        return c241Req;

    }

    public C241Resp createC241Resp() {
        C241Resp c241Resp = new C241Resp();
        c241Resp.setC241Result(new C241Result());
        c241Resp.getC241Result().setResultCondition(new ResultCondition());
        c241Resp.getC241Result().getResultCondition().setSeverityCode((byte) 1);
        c241Resp.getC241Result().getResultCondition().setReasonCode(Integer.valueOf(0));
        return c241Resp;
    }

    public C658Resp createC658Resp() {
        C658Resp c658Resp = new C658Resp();
        c658Resp.setC658Result(new C658Result());
        c658Resp.getC658Result().setResultCondition(new ResultCondition());
        c658Resp.getC658Result().getResultCondition().setReasonCode(Integer.valueOf(0));
        return c658Resp;
    }

    public RecordInvolvedPartyDetailsRequest createRecordInvolvedPartyRequest() {
        RecordInvolvedPartyDetailsRequest request = new RecordInvolvedPartyDetailsRequest();
        request.setRequestHeader(new RequestHeader());
        request.getRequestHeader().setDatasourceName("19");
        request.setInvolvedParty(new Individual());
        request.getInvolvedParty().setGrossTaxIndicator("001");
        return request;
    }

    public C234Resp createC234Resp() {
        C234Resp c234Resp = new C234Resp();
        c234Resp.setC234Result(new C234Result());
        c234Resp.getC234Result().setResultCondition(new ResultCondition());
        c234Resp.getC234Result().getResultCondition().setReasonCode(0);
        c234Resp.getC234Result().getResultCondition().setSeverityCode(new Byte("0"));
        return c234Resp;
    }

    public FinanceServiceArrangement createFinanceServiceArrangement(){
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        Customer customer = new Customer();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("PREVIOUS");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setStructuredAddress(new StructuredAddress());
        postalAddress.getStructuredAddress().setCountry("Country");
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.getUnstructuredAddress().setAddressLine8("Street");
        customer.getPostalAddress().add(postalAddress);
        financeServiceArrangement.setPrimaryInvolvedParty(customer);
        return financeServiceArrangement;
    }
    public CustomerDeviceDetails createCustomerDeviceDetails(){
        CustomerDeviceDetails deviceDetails= new CustomerDeviceDetails();
        deviceDetails.setBrowserLanguage("en-US,en;q=0.8");
        deviceDetails.setAccountLogin("Account Login");
        deviceDetails.setDnsIPGeo("GB");
        deviceDetails.setSmartDeviceId("4375a7307be74273b0f6e57a29a57126");
        deviceDetails.setSmartDeviceIdConfidence("100");
        deviceDetails.setTmxReviewStatus("Pass");
        deviceDetails.setTmxReasonCode("BlacklistCountries");
        deviceDetails.setTmxRiskRating("High");
        deviceDetails.setTmxSummaryRiskScore("10");
        deviceDetails.setProxyIpGeo("10");
        return deviceDetails;
    }


    public SubmitWorkItemResponse.SubmitWorkItemResult createSubmitWorkItemResult(BigInteger totalRuleScore)
    {
        SubmitWorkItemResponse.SubmitWorkItemResult submitWorkItemResult=new SubmitWorkItemResponse.SubmitWorkItemResult();
        RealtimeResultType4Type realtimeResultType4Type=new RealtimeResultType4Type();
        realtimeResultType4Type.setTotalRuleScore(totalRuleScore);
        JAXBElement<RealtimeResultType4Type> realtimeResultType4TypeJAXBElement=new JAXBElement<RealtimeResultType4Type>(new QName("RealtimeResultType4Type"),RealtimeResultType4Type.class,realtimeResultType4Type);
        submitWorkItemResult.getContent().add(realtimeResultType4TypeJAXBElement);
        return submitWorkItemResult;
    }

    public TmxDetails createTmxDetails(){
        String deviceData="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CustomerDeviceDetails xmlns:xsi=\"_http://www.w3.org/2001/XMLSchema-instance\" xmlns:ns3=\"http://LIB_SIM_BO/BusinessObjects\" xsi:type=\"ns3:CustomerDeviceDetails\"><exactDeviceId></exactDeviceId><smartDeviceIdConfidence></smartDeviceIdConfidence><smartDeviceId></smartDeviceId><trueIp></trueIp><accountLogin></accountLogin><tmxSummaryRiskScore></tmxSummaryRiskScore><tmxSummaryReasonCode></tmxSummaryReasonCode><tmxPolicyScore></tmxPolicyScore><tmxReasonCode></tmxReasonCode><tmxRiskRating></tmxRiskRating><tmxReviewStatus></tmxReviewStatus><deviceFirstSeen></deviceFirstSeen><deviceLastEvent></deviceLastEvent><trueIpGeo></trueIpGeo><trueIpIsp></trueIpIsp><trueIpOrganization></trueIpOrganization><proxyIpGeo></proxyIpGeo><dnsIPGeo></dnsIPGeo><browserLanguage></browserLanguage><workFlowName>LBG_ULLO_RT_WF1_RULES1</workFlowName><customerDecision><totalRuleMatchCount></totalRuleMatchCount><totalEnquiryMatchCount></totalEnquiryMatchCount><totalRuleScore></totalRuleScore><workflowExecutionKey></workflowExecutionKey><errorReasonCode></errorReasonCode><errorDescription></errorDescription><connectivityErrorFlag></connectivityErrorFlag><resultStatus></resultStatus></customerDecision></CustomerDeviceDetails>";
        TmxDetails tmxDetails = new TmxDetails();
        tmxDetails.setDeviceData(deviceData);
        return tmxDetails;
    }
}
