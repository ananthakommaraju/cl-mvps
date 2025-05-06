package com.lloydsbanking.salsa.eligibility.wz;

import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.*;
import com.lloydsbanking.salsa.eligibility.client.wz.EligibilityRequestBuilder;
import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.*;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.IndicatorGp;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.StandardIndicatorsGp;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Req;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.*;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.*;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm.ResponseHeader;
import com.lloydstsb.schema.enterprise.lcsm_authorization.AccessPermissionDetail;
import com.lloydstsb.schema.enterprise.lcsm_authorization.AccessPermissionDetailType;
import com.lloydstsb.schema.enterprise.lcsm_authorization.AccessPermissions;
import com.lloydstsb.schema.enterprise.lcsm_authorization.Mandate;
import com.lloydstsb.schema.enterprise.lcsm_common.*;
import com.lloydstsb.schema.enterprise.lcsm_common.RuleCondition;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.RetrieveMandateAccessDetailsRequest;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.RetrieveMandateAccessDetailsResponse;
import com.lloydstsb.schema.infrastructure.soap.Condition;
import com.lloydstsb.schema.infrastructure.soap.ExtraConditions;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.*;

import com.lloydstsb.schema.infrastructure.soap.Condition;

public class TestDataHelper {
    public final static String TEST_OCIS_ID = "00770901396";

    public static final String TEST_CUSTOMER_ID = "OX982035 ";

    public static final String TEST_INTERACTION_ID = "1234567890";

    public static final String TEST_CONTACT_POINT_ID = "123456";

    public static final String TEST_RETAIL_CHANNEL_ID = "IBL";

    public static final String TEST_INTERACTION = "ENixlWiKxlmZ8kZu4jGlAs3";

    public static final String TEST_BUSINESS_TRANSACTION = "determineEligibleCustomerInstruction";

    public static final String TEST_PROD_ID = "1524306000";

    public static final String TEST_BRAND = "IBL";

    public static final String SHADOW_LIMIT_ZERO = "0.0";

    @Autowired
    ApplicationsDao applicationsDao;

    @Autowired
    IndividualsDao individualsDao;

    @Autowired
    ApplicationStatusDao appStatusDao;

    @Autowired
    ApplicationTypesDao applicationTypesDao;

    @Autowired
    PartyApplicationsDao partyApplicationsDao;

    @Autowired
    ApplicationPartyRolesDao applicationPartyRolesDao;

    @Autowired
    ProductTypesDao productTypesDao;

    @Autowired
    BrandsDao brandsDao;

    @Autowired
    UserTypesDao userTypesDao;

    @Autowired
    PromotionChannelsDao promotionChannelsDao;

    @Autowired
    ChannelsDao channelsDao;

    @Autowired
    KycStatusDao kycStatusDao;

    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter = new BapiHeaderToStHeaderConverter();

    public RequestHeader createEligibilityRequestHeader(final String channelId, final String interactionId, final String ocisId, final String customerId, final String contactPointId) {

        RequestHeaderBuilder elgReqHeaderBuilder = new RequestHeaderBuilder();
        return elgReqHeaderBuilder.businessTransaction(TestDataHelper.TEST_BUSINESS_TRANSACTION).interactionId(interactionId).bapiInformation(channelId, interactionId, ocisId, "ns5").securityHeader("lgsm", customerId).serviceRequest("ns4", TestDataHelper.TEST_BUSINESS_TRANSACTION, "10.245.240.154", interactionId).contactPoint("ns4", "003", contactPointId, "Internet Banking", "Browser", "127.0.0.1", "Customer").build();
    }

    public DetermineEligibleCustomerInstructionsRequest createEligibilityRequest(String insMnemonic, String ocisId, String channel, String contactPointId) {
        RequestHeader header = createEligibilityRequestHeader(channel, TestDataHelper.TEST_INTERACTION, ocisId, TEST_CUSTOMER_ID, contactPointId);
        EligibilityRequestBuilder requestBuilder = new EligibilityRequestBuilder();

        List<String> candidateInstructions = new ArrayList();
        candidateInstructions.add(insMnemonic);
        List<ProductArrangement> productArrangementlist = new ArrayList();
        productArrangementlist.add(createExistingProductArrangments("3001116000", "00004", null, "50001762", 2014));
        return requestBuilder.header(header).candidateInstructions(candidateInstructions).existingProductArrangments(productArrangementlist).customerDetails(createCustomerDetails(1988, 01, 01)).build();

    }

    public ProductArrangement createExistingProductArrangments(String productIdentifier, String systemCode, String productType, String accountNum, int arngmntStartYear) {
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAccountNumber(accountNum);
        Product associateProduct = new Product();
        associateProduct.setProductIdentifier(productIdentifier);
        ExtSysProdIdentifier externalSystemProductIdentifier = new ExtSysProdIdentifier();
        externalSystemProductIdentifier.setSystemCode(systemCode);

        List<ExtSysProdIdentifier> externalSysPricePointIdentifierList = new ArrayList();
        externalSysPricePointIdentifierList.add(externalSystemProductIdentifier);

        associateProduct.getExternalSystemProductIdentifier().addAll(externalSysPricePointIdentifierList);
        associateProduct.setProductType(productType);
        productArrangement.setAssociatedProduct(associateProduct);
        productArrangement.setLifecycleStatus("Effective");

        Organisation financialInstitution = new Organisation();
        List<OrganisationUnit> hasOrganisationUnits = new ArrayList();
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("111618");
        hasOrganisationUnits.add(organisationUnit);
        financialInstitution.getHasOrganisationUnits().addAll(hasOrganisationUnits);

        productArrangement.setFinancialInstitution(financialInstitution);
        productArrangement.setArrangementStartDate(createXMLGregorianCalendar(arngmntStartYear, 10, 20));
        List<String> relatedEvents = new ArrayList();
        relatedEvents.add("37");
        relatedEvents.add("55");
        relatedEvents.add("30");
        relatedEvents.add("251");
        relatedEvents.add("333");

        productArrangement.getRelatedEvents().addAll(relatedEvents);

        return productArrangement;
    }

    public Customer createCustomerDetails(int year, int month, int day) {

        Customer customerDetails = new Customer();
        Individual individual = new Individual();
        individual.setBirthDate(createXMLGregorianCalendar(year, month, day));
        customerDetails.setIsPlayedBy(individual);
        customerDetails.setCustomerIdentifier("542107294");
        customerDetails.setInternalUserIdentifier("HE857942");
        customerDetails.setCustomerNumber("11161850000901");

        return customerDetails;
    }

    public List<PostalAddress> createStructuredPostalAddress() {
        List<PostalAddress> postalAddresses = new ArrayList();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("0707");
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("2");
        ;
        structuredAddress.setHouseNumber("2");
        structuredAddress.setDistrict("BALHAM");
        structuredAddress.setPostTown("LONDON");
        structuredAddress.getAddressLinePAFData().add("LINNET MEWS");
        structuredAddress.setPostCodeIn("8JE");
        structuredAddress.setPostCodeOut("SW12");
        structuredAddress.setPointSuffix("1Q");
        postalAddress.setStructuredAddress(structuredAddress);

        postalAddresses.add(postalAddress);

        return postalAddresses;
    }

    public List<PostalAddress> createUnstructuredPostalAddress() {
        List<PostalAddress> postalAddresses = new ArrayList();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(false);
        postalAddress.setDurationofStay("0707");
        UnstructuredAddress unStructuredAddress = new UnstructuredAddress();
        unStructuredAddress.setAddressLine1("184");

        unStructuredAddress.setAddressLine2("184");
        unStructuredAddress.setAddressLine4("Katherine Road");
        unStructuredAddress.setAddressLine6("London");
        unStructuredAddress.setAddressLine7("UK");
        unStructuredAddress.setAddressLine8("United Kingdon");
        unStructuredAddress.setPostCode("E62PB");
        postalAddress.setUnstructuredAddress(unStructuredAddress);
        postalAddresses.add(postalAddress);
        return postalAddresses;
    }

    public Individual createIndividual() {

        Individual individual = new Individual();
        List<IndividualName> individualNames = new ArrayList<>();
        IndividualName individualName = new IndividualName();
        individualName.setFirstName("SALSA");
        individualName.setLastName("DECI");
        individualNames.add(individualName);
        individual.getIndividualName().addAll(individualNames);
        return individual;

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

    public ReferenceDataLookUp createReferenceDataLookUp() {

        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setChannel("IBL");
        referenceDataLookUp.setLookupText("lookUp");
        referenceDataLookUp.setDescription("Desc");
        referenceDataLookUp.setSequence(123l);

        return referenceDataLookUp;
    }

    public DetermineEligibleCustomerInstructionsRequest createEligibilityRequestBOD(String insMnemonic, String ocisId, String channel, String contactPointId, int year, int month, int day) {
        RequestHeader header = createEligibilityRequestHeader(channel, TestDataHelper.TEST_INTERACTION, ocisId, TEST_CUSTOMER_ID, contactPointId);
        EligibilityRequestBuilder requestBuilder = new EligibilityRequestBuilder();

        List<String> candidateInstructions = new ArrayList();
        candidateInstructions.add(insMnemonic);
        List<ProductArrangement> productArrangementlist = new ArrayList();
        productArrangementlist.add(createExistingProductArrangments("3001116000", "0004", null, "50001762", 2014));

        return requestBuilder.header(header).candidateInstructions(candidateInstructions).existingProductArrangments(productArrangementlist).customerDetails(createCustomerDetails(year, month, day)).build();

    }

    public ProductArrangement createExistingDepositArrangements() {

        DepositArrangement productArrangement = new DepositArrangement();
        productArrangement.setAccountNumber("50001763");
        Product associateProduct = new Product();
        associateProduct.setProductIdentifier("3001116001");
        ExtSysProdIdentifier externalSystemProductIdentifier = new ExtSysProdIdentifier();
        externalSystemProductIdentifier.setSystemCode("00004");

        List<ExtSysProdIdentifier> externalSysPricePointIdentifierList = new ArrayList();
        externalSysPricePointIdentifierList.add(externalSystemProductIdentifier);

        associateProduct.getExternalSystemProductIdentifier().addAll(externalSysPricePointIdentifierList);

        productArrangement.setAssociatedProduct(associateProduct);
        ISABalance isaBalance = new ISABalance();
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal(0.0));
        isaBalance.setMaximumLimitAmount(currencyAmount);
        productArrangement.setISABalance(isaBalance);
        productArrangement.setLifecycleStatus("Effective");

        Organisation financialInstitution = new Organisation();
        List<OrganisationUnit> hasOrganisationUnits = new ArrayList();
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setSortCode("111619");
        hasOrganisationUnits.add(organisationUnit);
        financialInstitution.getHasOrganisationUnits().addAll(hasOrganisationUnits);

        productArrangement.setFinancialInstitution(financialInstitution);
        productArrangement.setArrangementStartDate(createXMLGregorianCalendar(2014, 10, 20));
        List<String> relatedEvents = new ArrayList();
        relatedEvents.add("37");
        relatedEvents.add("55");
        relatedEvents.add("30");
        relatedEvents.add("251");
        relatedEvents.add("333");

        productArrangement.getRelatedEvents().addAll(relatedEvents);

        return productArrangement;
    }

    public E591Req createE591Request(String customerId) {
        E591Req e591Req = new E591Req();
        e591Req.setMaxRepeatGroupQy(10);
        e591Req.setCustNoGp(new CustNoGp());
        e591Req.getCustNoGp().setNationalSortcodeId(customerId.substring(0, 2));
        if (customerId.length() > 14) {
            e591Req.getCustNoGp().setCBSCustNo(customerId.substring(2, 14));
        } else {
            e591Req.getCustNoGp().setCBSCustNo(customerId.substring(2, customerId.length()));
        }
        e591Req.setCAPSShdwDecnScrFlagCd(1);
        e591Req.setCAPSShdwDecnScrCd("A");
        return e591Req;
    }

    public E591Resp createE591Response(String shadowLimit) {
        E591Resp e591Resp = new E591Resp();
        e591Resp.setDecisionGp(new DecisionGp());
        e591Resp.getDecisionGp().getDecnSubGp().add(new DecnSubGp());
        e591Resp.getDecisionGp().getDecnSubGp().get(0).setCarLoanMnhShdwlmtIn(shadowLimit);
        e591Resp.getDecisionGp().getDecnSubGp().get(0).setDcnCdCarLoanFinancIn("R");
        e591Resp.getDecisionGp().getDecnSubGp().get(0).setRskBndCdCarLoanIn(6);
        return e591Resp;
    }

    public E141Req createE141Request(String sortCode, String accountNumber) {
        E141Req request = new E141Req();

        request.setMaxRepeatGroupQy(0);
        request.setCBSRequestGp(new CBSRequestGp());
        request.getCBSRequestGp().setInputOfficerFlagStatusCd(0);
        request.getCBSRequestGp().setOverrideDetailsCd(0);
        request.setCBSAccountNoId(sortCode.concat(accountNumber));
        return request;
    }

    public E141Resp createE141Response(List<Integer> indicators, String maxLimitAmt) {
        E141Resp e141Resp = new E141Resp();
        E141Result e141Result = new E141Result();

        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode(new Byte("0"));
        resultCondition.setReasonCode(0);
        e141Result.setResultCondition(resultCondition);

        Indicator2Gp indicator2Gp = getIndicator2Gp(indicators);

        e141Resp.setE141Result(e141Result);
        e141Resp.setIndicator2Gp(indicator2Gp);

        ISADetailsGp isaDetailsGp = new ISADetailsGp();
        isaDetailsGp.setISADetailsSubGp(new ISADetailsSubGp());
        isaDetailsGp.getISADetailsSubGp().setTaxYearTotalDepositAm(maxLimitAmt);
        isaDetailsGp.getISADetailsSubGp().setISARmnDpsAm("10");
        e141Resp.setISADetailsGp(isaDetailsGp);

        return e141Resp;

    }


    public List<ProductArrangementIndicator> getProdIndicators(int indicator) {
        List<ProductArrangementIndicator> productArrangementIndicators = new ArrayList<>();
        ProductArrangementIndicator prodIndicator = new ProductArrangementIndicator();
        prodIndicator.setCode(indicator);
        productArrangementIndicators.add(prodIndicator);
        return productArrangementIndicators;
    }

    public Indicator2Gp getIndicator2Gp(List<Integer> indicators) {
        Indicator2Gp indicator2Gp = new Indicator2Gp();

        List<StandardIndicators2Gp> standardIndicators2Gps = new ArrayList<>();

        for (Integer indicator : indicators) {
            StandardIndicators2Gp standardIndicators2Gp = new StandardIndicators2Gp();
            standardIndicators2Gp.setIndicator2Cd(indicator);
            standardIndicators2Gps.add(standardIndicators2Gp);

        }
        indicator2Gp.getStandardIndicators2Gp().addAll(standardIndicators2Gps);
        return indicator2Gp;
    }


    public IndicatorGp getIndicatorGp(List<Integer> indicators) {
        IndicatorGp indicatorGp = new IndicatorGp();

        List<StandardIndicatorsGp> standardIndicatorsGps = new ArrayList<>();

        for (Integer indicator : indicators) {
            StandardIndicatorsGp standardIndicatorsGp = new StandardIndicatorsGp();
            standardIndicatorsGp.setIndicatorCd(indicator);
            standardIndicatorsGps.add(standardIndicatorsGp);

        }
        indicatorGp.getStandardIndicatorsGp().addAll(standardIndicatorsGps);
        return indicatorGp;
    }

    public F075Req createF075Request(BapiInformation bapiInformation, String customerId) throws DetermineEligibleInstructionsInternalServiceErrorMsg {

        String host = bapiInformation.getBAPIHeader().getStpartyObo().getHost();
        F075Req request = new F075Req();
        request.setMaxRepeatGroupQy(15);
        request.setExtSysId((short) 19);
        request.setPartyExtSysId(host.equals("L") ? (short) 1 : (short) 2);
        request.setPartyId(Long.parseLong(customerId));
        return request;
    }

    public F075Resp createKYCResponse(String status, String partyEvidenceTypeCd, String partyEvidenceRefTx, String addrEvidenceTypeCd, String addrEvidenceRefTx) {
        F075Resp f075Resp = new F075Resp();
        f075Resp.setF075Result(new F075Result());
        f075Resp.getF075Result().setResultCondition(new ResultCondition());
        f075Resp.getF075Result().getResultCondition().setReasonCode(0);
        f075Resp.setKYCControlData(new KYCControlData());
        f075Resp.getKYCControlData().setDataCollectedStatusCd(status);
        f075Resp.setEvidenceData(new EvidenceData());
        PartyEvidence partyIdEvidence = new PartyEvidence();
        partyIdEvidence.setPartyEvidenceTypeCd(partyEvidenceTypeCd);
        partyIdEvidence.setPartyEvidenceRefTx(partyEvidenceRefTx);
        f075Resp.getEvidenceData().getPartyEvidence().add(partyIdEvidence);
        AddrEvidence addrEvidence = new AddrEvidence();
        addrEvidence.setAddrEvidenceTypeCd(addrEvidenceTypeCd);
        addrEvidence.setAddrEvidenceRefTx(addrEvidenceRefTx);
        f075Resp.getEvidenceData().getAddrEvidence().add(addrEvidence);
        return f075Resp;
    }

    public void createPamPartyApplicationsData(String ocisId, int daysPrior) {
        ApplicationTypes applicationTypes = new ApplicationTypes("102", "New Application");
        ProductTypes productTypes = new ProductTypes("106", "Finance Account");
        Brands brands = new Brands("LTB", "Lloyds");
        UserTypes userTypes = new UserTypes("1001", "Customer");
        ApplicationStatus applicationStatus = new ApplicationStatus("1010", "Fullfilled");
        PromotionChannels promotionChannels = new PromotionChannels("003", "Direct Mail");
        Channels channels = new Channels("004", "Internet");
        applicationTypesDao.save(applicationTypes);
        applicationTypesDao.findAll();

        productTypesDao.save(productTypes);
        productTypesDao.findAll();
        brandsDao.save(brands);
        brandsDao.findAll();
        userTypesDao.save(userTypes);
        userTypesDao.findAll();
        appStatusDao.save(applicationStatus);
        appStatusDao.findAll();
        promotionChannelsDao.save(promotionChannels);
        promotionChannelsDao.findAll();
        channelsDao.save(channels);
        channelsDao.findAll();
        Applications applications = new Applications(applicationTypes, productTypes, brands, userTypes, applicationStatus, promotionChannels, channels, "10005");
        applications.setSortCode("773315");
        applications.setArrangementNumber("08676168");

        applications.setUserId("10.245.176.43");
        applications.setProductRequested("92");
        applications.setProductName("Classic Account");
        applications.setProductTypes(new ProductTypes());
        applications.getProductTypes().setCode("106");
        applications.setApplicationStatus(new ApplicationStatus());
        applications.getApplicationStatus().setStatus("1010");
        LocalDate localDate = new LocalDate();
        LocalDate modifiedDate = localDate.minusDays(daysPrior);
        applications.setDateModified(modifiedDate.toDate());

        applicationsDao.save(applications);
        applicationsDao.findAll();

        Individuals individuals = new Individuals();
        individuals.setOcisId(ocisId);
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
        ApplicationPartyRoles roles = new ApplicationPartyRoles("0001", "Key Party created");
        applicationPartyRolesDao.save(roles);
        applicationPartyRolesDao.findAll();

        KycStatus status = new KycStatus();
        status.setCode("ACCEPT");
        status.setDescription("ACCEPT");
        kycStatusDao.save(status);
        kycStatusDao.findAll();

        PartyApplications partyApplications = new PartyApplications();
        partyApplications.setApplicationPartyRoles(roles);
        partyApplications.setScoringStatus("1");
        partyApplications.setCustomerSegments("3");
        partyApplications.setKycStatus(status);
        partyApplications.setLockId(0L);
        partyApplications.setApplications(applications);
        partyApplications.setIndividuals(individuals);
        partyApplicationsDao.save(partyApplications);

        Set<PartyApplications> partyApplicationses = partyApplicationsDao.findByApplicationPartyRolesCodeAndIndividualsPartyId("0001", individuals.getPartyId());
        for (PartyApplications partyApplications1 : partyApplicationses) {

            Applications applications1 = partyApplications1.getApplications();
        }
    }

    public void cleanUpPam() {
        partyApplicationsDao.deleteAll();
        applicationsDao.deleteAll();
        individualsDao.deleteAll();
        applicationTypesDao.deleteAll();
        productTypesDao.deleteAll();
        brandsDao.deleteAll();
        userTypesDao.deleteAll();
        appStatusDao.deleteAll();
        promotionChannelsDao.deleteAll();
        channelsDao.deleteAll();
        applicationPartyRolesDao.deleteAll();
    }

    public RetrieveMandateAccessDetailsRequest createRetrieveMandateAccessDetailsRequest(String accType) {
        RetrieveMandateAccessDetailsRequest request = new RetrieveMandateAccessDetailsRequest();
        request.setMandate(new Mandate());
        request.getMandate().getConditionContext().add(new ConditionContext());
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("MandateEventDetail");
        RuleCondition subRule = new RuleCondition();
        subRule.setName("MandateEventsForArrangementType");
        ruleCondition.getSubrules().add(subRule);
        request.getMandate().getConditionContext().get(0).setCondition(ruleCondition);
        request.getMandate().setRegistrationType(new RegistrationType());
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

    public E220Req createShadowLimitRequest(String sortCode, String customerId) {
        E220Req request = new E220Req();
        request.setCustNoGp(new com.lloydsbanking.salsa.soap.cbs.e220.objects.CustNoGp());
        if (customerId.length() > 14) {
            customerId = customerId.substring(2, 14);
        } else {
            customerId = customerId.substring(2, customerId.length());
        }
        request.getCustNoGp().setCBSCustNo(customerId);

        request.getCustNoGp().setNationalSortcodeId(sortCode.substring(0, 2));
        request.setCAPSShdwDecnScrCd(" ");
        request.setCAPSShdwDecnScrFlagCd(1);
        return request;
    }

}
