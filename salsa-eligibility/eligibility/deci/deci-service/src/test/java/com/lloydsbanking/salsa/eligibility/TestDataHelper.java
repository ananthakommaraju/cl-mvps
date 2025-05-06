package com.lloydsbanking.salsa.eligibility;

import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.client.EligibilityRequestBuilder;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.header.RequestHeaderBuilder;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Req;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Resp;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Result;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.Indicator1Gp;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.StandardIndicators1Gp;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.CustNoGp;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.DecisionGp;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.DecnSubGp;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Req;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Resp;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Result;
import com.lloydsbanking.salsa.soap.fs.account.StHeader;
import com.lloydsbanking.salsa.soap.fs.user.StAccountListDetail;
import com.lloydsbanking.salsa.soap.fs.user.StError;
import com.lloydsbanking.salsa.soap.fs.user.StSpendingReward;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Req;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Resp;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Result;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.KYCControlData;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Req;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Resp;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Result;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import com.lloydstsb.ib.wsbridge.account.StB766ARetrieveCBSAppGroup;
import com.lloydstsb.ib.wsbridge.account.StB766BRetrieveCBSAppGroup;
import com.lloydstsb.ib.wsbridge.system.StB093AEventLogReadList;
import com.lloydstsb.ib.wsbridge.system.StB093BEventLogReadList;
import com.lloydstsb.ib.wsbridge.system.TEventLogReadList;
import com.lloydstsb.ib.wsbridge.user.StB162AUserAccReadList;
import com.lloydstsb.ib.wsbridge.user.StB162BUserAccReadList;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm.ResponseHeader;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.ProductAccessArrangement;
import com.lloydstsb.schema.enterprise.lcsm_common.AlternateId;
import com.lloydstsb.schema.enterprise.lcsm_common.ObjectReference;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.InvolvedPartyRole;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.OrganisationUnitIdentification;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.OrganizationUnit;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.RetrieveAccessibleArrangementsRequest;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.RetrieveAccessibleArrangementsResponse;
import com.lloydstsb.schema.enterprise.lcsm_product.ProductGroup;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.Condition;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ExtraConditions;
import com.lloydstsb.schema.infrastructure.soap.ReasonDetail;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.Customer;
import lb_gbo_sales.DepositArrangement;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.ArrangementIdentifier;
import lb_gbo_sales.businessobjects.BusinessArrangement;
import lb_gbo_sales.businessobjects.CreditCardFinanceServiceArrangement;
import lb_gbo_sales.businessobjects.CreditCardStatus;
import lb_gbo_sales.businessobjects.Individual;
import lb_gbo_sales.businessobjects.OvrdrftDtls;
import lb_gbo_sales.businessobjects.ProductArrangementLifecycleStatus;
import lb_gbo_sales.businessobjects.ProductType;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.joda.time.LocalDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class TestDataHelper {
    public final static String TEST_OCIS_ID = "00770901396";

    public final static long TEST_MAIN_ACC_NUMBER = 5L;

    public final static String TEST_OCIS_ID_VER = "0099090139679662";

    public final static String BRAND = "LLOYDS";

    public static final String TEST_CUSTOMER_ID = "OX982035 ";

    public static final String TEST_INTERACTION_ID = "1234567890";

    public static final String TEST_CONTACT_POINT_ID = "123456";

    public static final String TEST_RETAIL_CHANNEL_ID = "IBL";

    public static final String TEST_BUSINESS_TRANSACTION = "determineEligibleCustomerInstruction";

    public static final String TEST_INTERACTION = "ENixlWiKxlmZ8kZu4jGlAs3";

    public static final int MAX_REPEAT_GROUP_QTY = 15;

    public static final short EXTERNAL_SYS_ID = 19;

    public static final String KYC_STATUS_PARTIAL = "P";

    public static final String KYC_STATUS_FULL = "F";

    public static final int ERROR_CODE_163004 = 163004;

    public static final String SHADOW_LIMIT_ZERO = "0.0";

    public static final String DEFAULT_SHADOW_DECISION_SCR_CODE = "A";

    public static final int DEFAULT_SHADOW_DECISION_SCR_FLAG_CODE = 1;

    public static final String SHADOW_LIMIT_NON_ZERO = "1";

    private HashMap<String, String> sortCodeAppGrpMap;

    private static final String CLOSURE_PERIOD_MONTHS_DR = "000";

    private static final String CLOSED_ONLY_IN = "0";

    private static final String CUSTOMER_CONSENT_IN = "1";

    private static final BigInteger ACC_MOR_KEY = BigInteger.valueOf(0);

    public static final String TEST_COMMERCIAL_CHANNEL_ID = "STL";

    public static final String TEST_SORT_CODE = "772519";

    public static final String TEST_ACCOUNT_NUMBER = "04840860";

    public static final String TEST_CBS_APP_GRP = "01";

    public static final String TEST_CHANNEL_ID_CBS = "LTB";

    public static final int TEST_CBS_INDICATOR = 646;

    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter = new BapiHeaderToStHeaderConverter();

    HeaderRetriever headerRetriever = new HeaderRetriever();

    ChannelToBrandMapping channelToBrandMapping = new ChannelToBrandMapping();


    public TestDataHelper() {
        generateSortCodeAppGrpMap();
    }

    public RequestHeader createEligibilityRequestHeader(final String channelId, final String interactionId, final String ocisId, final String customerId, final String contactPointId) {

        RequestHeaderBuilder elgReqHeaderBuilder = new RequestHeaderBuilder();
        return elgReqHeaderBuilder.businessTransaction(TestDataHelper.TEST_BUSINESS_TRANSACTION)
                .channelId(channelId)
                .interactionId(interactionId)
                .bapiInformation(channelId, interactionId, ocisId, "ns5")
                .securityHeader("lgsm", customerId)
                .serviceRequest("ns4", TestDataHelper.TEST_BUSINESS_TRANSACTION, "10.245.240.154", interactionId)
                .contactPoint("ns4", "003", contactPointId, "Internet Banking", "Browser", "127.0.0.1", "Customer")
                .build();
    }

    public RetrieveAccessibleArrangementsRequest createRetrieveAccessibleArrangementsRequest() {
        RetrieveAccessibleArrangementsRequest request = new RetrieveAccessibleArrangementsRequest();
        request.setSourceArrangement(new com.lloydstsb.schema.enterprise.lcsm_arrangement.DepositArrangement());
        request.getSourceArrangement().setObjectReference(new ObjectReference());
        request.getSourceArrangement().getObjectReference().setKeyGroupType("ProductArrangement");
        AlternateId alternateId = new AlternateId();
        alternateId.setAttributeString("ACCOUNT_NUMBER_SMM");
        request.getSourceArrangement().getObjectReference().getAlternateId().add(alternateId);
        return request;
    }

    public RetrieveAccessibleArrangementsResponse createRetrieveAccessibleArrangementsResponse(String productOneAccountType, String productTwoAccountType, String sellerEntity) {
        RetrieveAccessibleArrangementsResponse retrieveAccessibleArrangementsResponse = new RetrieveAccessibleArrangementsResponse();
        InvolvedPartyRole involvedPartyRole = new InvolvedPartyRole();
        involvedPartyRole.setInvolvedParty(new OrganizationUnit());
        ((OrganizationUnit) involvedPartyRole.getInvolvedParty()).getIdentificationValues().add(new OrganisationUnitIdentification());
        ((OrganizationUnit) involvedPartyRole.getInvolvedParty()).getIdentificationValues().get(0).setIdentificationValue(sellerEntity);

        InvolvedPartyRole involvedPartyRole1 = new InvolvedPartyRole();
        involvedPartyRole1.setInvolvedParty(new OrganizationUnit());
        ((OrganizationUnit) involvedPartyRole1.getInvolvedParty()).getIdentificationValues().add(new OrganisationUnitIdentification());
        ((OrganizationUnit) involvedPartyRole1.getInvolvedParty()).getIdentificationValues().get(0).setIdentificationValue(sellerEntity);


        List<InvolvedPartyRole> involvedPartiesRoles = new ArrayList();
        involvedPartiesRoles.add(involvedPartyRole);
        involvedPartiesRoles.add(involvedPartyRole1);
        ProductAccessArrangement productAccessArrangement = new ProductAccessArrangement();
        productAccessArrangement.getRoles().addAll(involvedPartiesRoles);
        retrieveAccessibleArrangementsResponse.getAccessibleArrangement().add(productAccessArrangement);
        retrieveAccessibleArrangementsResponse.setHasOldISA(true);

        ProductGroup productGroup = new ProductGroup();
        productGroup.setName(productOneAccountType);

        ProductGroup productGroup1 = new ProductGroup();
        productGroup1.setName(productTwoAccountType);

        com.lloydstsb.schema.enterprise.lcsm_product.Product product = new com.lloydstsb.schema.enterprise.lcsm_product.Product();
        product.getProductGroup().add(productGroup);
        product.getProductGroup().add(productGroup1);
        productAccessArrangement.setProduct(product);
        retrieveAccessibleArrangementsResponse.getAccessibleArrangement()
                .get(retrieveAccessibleArrangementsResponse.getAccessibleArrangement().size() - 1)
                .setObjectReference(new ObjectReference());

        retrieveAccessibleArrangementsResponse.getAccessibleArrangement()
                .get(retrieveAccessibleArrangementsResponse.getAccessibleArrangement().size() - 1)
                .getObjectReference()
                .setSendMeMore(false);

        ResponseHeader responseHeader = new ResponseHeader();
        ExtraConditions extraConditions = new ExtraConditions();
        Condition e = new Condition();
        e.setReasonCode(0);
        extraConditions.getCondition().add(e);
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setExtraConditions(extraConditions);
        responseHeader.setResultCondition(resultCondition);
        retrieveAccessibleArrangementsResponse.setResponseHeader(responseHeader);
        return retrieveAccessibleArrangementsResponse;
    }

    public DetermineElegibileInstructionsRequest createEligibilityRequest(String insMnemonic, String ocisId, String channel, String contactPointId) {
        RequestHeader header = createEligibilityRequestHeader(channel, TestDataHelper.TEST_INTERACTION, ocisId, TEST_CUSTOMER_ID, contactPointId);
        EligibilityRequestBuilder requestBuilder = new EligibilityRequestBuilder();

        List<String> candidateInstructions = new ArrayList();
        candidateInstructions.add(insMnemonic);

        Individual individual = new Individual();
        individual.setBirthDate(createXMLGregorianCalendar(1989, 01, 01));
        List<ProductArrangement> lstProductArrangement = new ArrayList<ProductArrangement>();
        lstProductArrangement.add(createDepositArrangement());
        lstProductArrangement.add(createCreditCardFinanaceServiceArrangement());


        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("04840860");
        arrangementIdentifier.setSortCode("772519");

        return requestBuilder.header(header)
                .candidateInstructions(candidateInstructions)
                .customerArrangements(lstProductArrangement)
                .individual(individual)
                .selectedArrangement(arrangementIdentifier)
                .build();

    }

    public DepositArrangement createArrangementOfSpecProduct(String accHost, String accType) {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setSortCode("772519");
        depositArrangement.setAccountNumber("04840860");
        depositArrangement.setAccountHost(accHost);
        depositArrangement.setAccountType(accHost.concat(accType));
        depositArrangement.setStartDate(createXMLGregorianCalendar(2013, 10, 7));
        List<String> relatedEventsForDA = new ArrayList();
        relatedEventsForDA.add("37");
        relatedEventsForDA.add("55");
        relatedEventsForDA.add("30");
        relatedEventsForDA.add("251");
        relatedEventsForDA.add("333");
        depositArrangement.getRelatedEvents().addAll(relatedEventsForDA);
        depositArrangement.setLifecycleStatus(ProductArrangementLifecycleStatus.EFFECTIVE);
        depositArrangement.setProductType(ProductType.ACCOUNT);
        return depositArrangement;
    }


    public DetermineElegibileInstructionsRequest createEligibilityRequestWithCreditCardStatusCode(String insMnemonic, String ocisId, String channel, String contactPointId, String statusCode) {
        DetermineElegibileInstructionsRequest request = createEligibilityRequest(insMnemonic, ocisId, channel, contactPointId);
        if (!statusCode.equals("null")) {
            ((CreditCardFinanceServiceArrangement) (request.getCustomerArrangements().get(1))).setCardStatus(CreditCardStatus.fromValue(statusCode));
        }
        return request;

    }

    public DetermineElegibileInstructionsRequest createEligibilityRequestWithLifeCycleStatusAndRelatedEvent(String insMnemonic, String ocisId, String channel, String contactPointId, String statusCode, String relatedEvent) {
        DetermineElegibileInstructionsRequest request = createEligibilityRequest(insMnemonic, ocisId, channel, contactPointId);
        request.getCustomerArrangements().clear();
        request.getCustomerArrangements().add(createDepositArrangement());
        request.getCustomerArrangements().get(0).getRelatedEvents().clear();
        if (!relatedEvent.equals("null")) {
            request.getCustomerArrangements().get(0).getRelatedEvents().add(relatedEvent);
        }
        request.getCustomerArrangements().get(0).setLifecycleStatus(ProductArrangementLifecycleStatus.fromValue(statusCode));
        return request;
    }

    public DetermineElegibileInstructionsRequest createEligibilityRequestWithLifeCycleStatus(String insMnemonic, String ocisId, String channel, String contactPointId, String statusCode) {
        DetermineElegibileInstructionsRequest request = createEligibilityRequest(insMnemonic, ocisId, channel, contactPointId);
        request.getCustomerArrangements().clear();
        request.getCustomerArrangements().add(createDepositArrangement());
        if (statusCode.equals("null")) {
            request.getCustomerArrangements().get(0).setLifecycleStatus(null);
        }
        else {
            request.getCustomerArrangements().get(0).setLifecycleStatus(ProductArrangementLifecycleStatus.fromValue(statusCode));
        }

        return request;
    }

    public DetermineElegibileInstructionsRequest createEligibilityRequestForCreditCard(String insMnemonic, String ocisId, String channel, String contactPointId) {
        DetermineElegibileInstructionsRequest request = createEligibilityRequest(insMnemonic, ocisId, channel, contactPointId);
        request.getCustomerArrangements().clear();
        request.getCustomerArrangements().add(createCreditCardFinanaceServiceArrangement());
        return request;
    }

    public DetermineElegibileInstructionsRequest createEligibilityRequestWithDepositArrangementHavingSortCode(String insMnemonic, String ocisId, String channel, String contactPointId, String sortCode) {
        DetermineElegibileInstructionsRequest request = createEligibilityRequest(insMnemonic, ocisId, channel, contactPointId);
        request.getCustomerArrangements().clear();
        request.getCustomerArrangements().add(createDepositArrangement());
        request.getCustomerArrangements().get(0).setSortCode(sortCode);
        return request;
    }

    public CreditCardFinanceServiceArrangement createCreditCardFinanaceServiceArrangement() {
        CreditCardFinanceServiceArrangement creditCardFinanceServiceArrangement = new CreditCardFinanceServiceArrangement();
        creditCardFinanceServiceArrangement.setAccountHost("F");
        creditCardFinanceServiceArrangement.setAccountType("F120300552157");
        creditCardFinanceServiceArrangement.setHasEmbeddedInsurance(false);
        creditCardFinanceServiceArrangement.setStartDate(createXMLGregorianCalendar(2013, 9, 25));
        creditCardFinanceServiceArrangement.setArrangementType("CURRENT");
        creditCardFinanceServiceArrangement.setCapAccountRestricted(true);
        creditCardFinanceServiceArrangement.setProductType(ProductType.CREDIT_CARD);
        List<String> relatedEvents = new ArrayList();
        relatedEvents.add("64");
        relatedEvents.add("62");
        creditCardFinanceServiceArrangement.setLifecycleStatus(ProductArrangementLifecycleStatus.EFFECTIVE);
        creditCardFinanceServiceArrangement.getRelatedEvents().addAll(relatedEvents);
        return creditCardFinanceServiceArrangement;
    }

    public DepositArrangement createDepositArrangement() {
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setSortCode("772519");
        depositArrangement.setAccountNumber("04840860");
        depositArrangement.setAccountHost("T");
        depositArrangement.setAccountType("T0071776000");
        depositArrangement.setArrangementType("CURRENT");
        depositArrangement.setStartDate(createXMLGregorianCalendar(2013, 10, 7));
        List<String> relatedEventsForDA = new ArrayList();
        relatedEventsForDA.add("55");
        relatedEventsForDA.add("30");
        relatedEventsForDA.add("251");
        relatedEventsForDA.add("333");
        depositArrangement.getRelatedEvents().addAll(relatedEventsForDA);
        depositArrangement.setLifecycleStatus(ProductArrangementLifecycleStatus.EFFECTIVE);
        depositArrangement.setProductType(ProductType.ACCOUNT);
        depositArrangement.setArrangementType("CURRENT");
        depositArrangement.setCapAccountRestricted(false);
        return depositArrangement;
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
        }
        catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public F075Resp createKYCResponse(String status) {
        F075Resp f075Resp = new F075Resp();
        f075Resp.setF075Result(new F075Result());
        f075Resp.getF075Result().setResultCondition(new ResultCondition());
        f075Resp.getF075Result().getResultCondition().setReasonCode(0);
        f075Resp.setKYCControlData(new KYCControlData());
        f075Resp.getKYCControlData().setDataCollectedStatusCd(status);
        return f075Resp;
    }

    public F075Req createF075Request(BapiInformation bapiInformation) throws DetermineEligibleInstructionsInternalServiceErrorMsg {

        String host = bapiInformation.getBAPIHeader().getStpartyObo().getHost();
        F075Req request = new F075Req();
        request.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QTY);
        request.setExtSysId(EXTERNAL_SYS_ID);
        request.setExtPartyIdTx(bapiInformation.getBAPIHeader().getStpartyObo().getPartyid());
        request.setPartyExtSysId(host.endsWith("L") ? (short) 1 : (short) 2);
        return request;
    }

    public F075Resp createKYCErrorResponse(int errorCode) {
        F075Resp f075Resp = new F075Resp();
        f075Resp.setF075Result(new F075Result());
        f075Resp.getF075Result().setResultCondition(new ResultCondition());
        f075Resp.getF075Result().getResultCondition().setReasonCode(errorCode);
        f075Resp.getF075Result().getResultCondition().setReasonText("KYC status unavailable");
        return f075Resp;
    }

    public ProductArrangement createCustomerArrangements(String participantId) {
        ProductArrangement customerArrangement = new DepositArrangement();
        customerArrangement.setSortCode("772519");
        customerArrangement.setAccountNumber("04824368");
        customerArrangement.setAccountHost("T");
        customerArrangement.setAccountType("T0071776000");
        customerArrangement.setStartDate(createXMLGregorianCalendar(2013, 9, 25));
        customerArrangement.setLifecycleStatus(ProductArrangementLifecycleStatus.EFFECTIVE);
        customerArrangement.setProductType(ProductType.ACCOUNT);
        customerArrangement.getParticipantCusomters().add(createParticipantCustomer(participantId));
        customerArrangement.setCapAccountRestricted(false);
        customerArrangement.setArrangementType("CURRENT");
        customerArrangement.getRelatedEvents().add("25");
        return customerArrangement;
    }

    private Customer createParticipantCustomer(String participantId) {

        Customer participantCustomer = new Customer();
        participantCustomer.setPartyId(participantId);

        return participantCustomer;
    }

    public E220Req createShadowLimitRequest(String sortCode, String customerId) {
        E220Req request = new E220Req();
        request.setCustNoGp(new CustNoGp());
        if (customerId.length() > 12) {
            customerId = customerId.substring(2, customerId.length());
        }
        request.getCustNoGp().setCBSCustNo(customerId);

        request.getCustNoGp().setNationalSortcodeId(sortCode.substring(0, 2));
        request.setCAPSShdwDecnScrCd(DEFAULT_SHADOW_DECISION_SCR_CODE);
        request.setCAPSShdwDecnScrFlagCd(DEFAULT_SHADOW_DECISION_SCR_FLAG_CODE);
        return request;
    }

    private HashMap<String, String> generateSortCodeAppGrpMap() {
        sortCodeAppGrpMap = new HashMap<String, String>();
        sortCodeAppGrpMap.put("35345", "01");
        sortCodeAppGrpMap.put("2342345", "01");
        sortCodeAppGrpMap.put("124234", "07");
        sortCodeAppGrpMap.put("24345", "07");
        sortCodeAppGrpMap.put("456457", "09");
        sortCodeAppGrpMap.put("1313124", "09");
        sortCodeAppGrpMap.put("772519", "01");
        return sortCodeAppGrpMap;

    }

    public String getCBSAppGrpForSortCode(String sortCode) {
        return sortCodeAppGrpMap.get(sortCode);
    }

    public CBSAppGrp createCBSAppGroupFromSortCode(String sortCode) {
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        if (sortCodeAppGrpMap.containsKey(sortCode)) {
            cbsAppGrp.setCBSApplicationGroupNumber(sortCodeAppGrpMap.get(sortCode));
        }
        else {
            cbsAppGrp.setCBSApplicationGroupNumber("01");
        }
        return cbsAppGrp;
    }

    public E220Resp createShadowLimitResponse(String shadowLimitZero, String strictFlag) {
        E220Resp e220Resp = new E220Resp();
        e220Resp.setE220Result(new E220Result());
        e220Resp.getE220Result().setResultCondition(new ResultCondition());
        e220Resp.getE220Result().getResultCondition().setReasonCode(0);
        e220Resp.setDecisionGp(new DecisionGp());
        e220Resp.getDecisionGp().getDecnSubGp().add(createDecisionSubGroup(shadowLimitZero, strictFlag));

        return e220Resp;
    }

    private DecnSubGp createDecisionSubGroup(String shadowLimit, String strictFlag) {
        DecnSubGp decnSubGp = new DecnSubGp();
        decnSubGp.setShdwDcnLoanLwrLmtAm(shadowLimit);
        decnSubGp.setStrictCd(Integer.valueOf(strictFlag));
        return decnSubGp;
    }

    public String getBrandForChannel(String channel) {
        try {
            return channelToBrandMapping.getBrandForChannel(channel);
        }
        catch (Exception e) {
            //need this for tescases where channel doesn't match sellerLegalEntity from E220
            //for channels returned from branch channel will match
            return channel;
        }
    }

    public F336Resp createF336Response(int productOneGroupId, int productTwoGroupId) {
        F336Resp f336Resp = new F336Resp();
        f336Resp.setAdditionalDataIn(0);
        f336Resp.setF336Result(createF336Result());
        List<ProductPartyData> productPartyData = new ArrayList<>();
        ProductPartyData productPartyData1 = new ProductPartyData();
        productPartyData1.setProdGroupId(productOneGroupId);
        ProductPartyData productPartyData2 = new ProductPartyData();
        productPartyData2.setProdGroupId(productTwoGroupId);
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

    public F336Resp createF336ResponseWithInternalServiceError() {
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

    public StB162BUserAccReadList createB162ResponseWithError(int errorNo, String id) {
        StB162BUserAccReadList response = new StB162BUserAccReadList();
        response.setBHasOldISA(true);
        response.setMoreind("abc");
        response.getAstacclistdetail().add(new StAccountListDetail());
        response.getAstacclistdetail().get(0).setAccountcategory("Account");
        response.setStSpndngRwrd(new StSpendingReward());
        response.getStSpndngRwrd().setSpendingRewardId(id);
        response.setSterror(new StError());
        response.getSterror().setErrorno(errorNo);
        return response;

    }

    public StB162BUserAccReadList createB162Response(String id, String productOneAccountType, String productTwoAccountType, String sellerEntity) {
        StB162BUserAccReadList response = new StB162BUserAccReadList();
        List<StAccountListDetail> productArrangements = new ArrayList();
        StAccountListDetail stAccountListDetail = new StAccountListDetail();
        stAccountListDetail.setAccountcategory(productOneAccountType);
        stAccountListDetail.setBrandcode(getBrandForChannel(sellerEntity));
        StAccountListDetail stAccountListDetailTwo = new StAccountListDetail();
        stAccountListDetailTwo.setAccountcategory(productTwoAccountType);
        stAccountListDetailTwo.setBrandcode(getBrandForChannel(sellerEntity));
        productArrangements.add(stAccountListDetail);
        productArrangements.add(stAccountListDetailTwo);
        response.setBHasOldISA(true);
        response.setMoreind("abc");
        response.getAstacclistdetail().addAll(productArrangements);


        response.setStSpndngRwrd(new StSpendingReward());
        response.getStSpndngRwrd().setSpendingRewardId(id);

        return response;

    }

    public StB766ARetrieveCBSAppGroup createB766Request(RequestHeader requestHeader, String sortCode) {
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders());
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId());
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

    public List<RefInstructionRulesDto> createRuleDtoList() {
        List<RefInstructionRulesDto> ruleList = new ArrayList<>();
        ruleList.add(new RefInstructionRulesDto("G_TRAV_MON", "GR011", "Customer is not eligible for Travel money ", "GR011", "CR004", "Customer does not have an existing product with the Pay bills event enabled", "CR004", "GRP", "AGT", "30", TestDataHelper.TEST_RETAIL_CHANNEL_ID, BigDecimal.ONE));
        ruleList.add(new RefInstructionRulesDto("G_SAVINGS", "GR011", "Customer is not eligible for savings product", "GR011", "CR026", "Customer is not eligible for savings product", "CR026", "GRP", "CST", "30", "IBH", BigDecimal.ONE));
        ruleList.add(new RefInstructionRulesDto("G_LOAN", "GR001", "Customer not eligible for personal loan", "GR001", "CR001", "Customer cannot be older that 74 years", "CR001", "GRP", "CST", "74", "IBL", BigDecimal.ONE));
        ruleList.add(new RefInstructionRulesDto("G_LOAN", "GR002", "Customer not eligible for personal loan", "GR002", "CR002", "Customer cannot be younger than 18 years", "CR002", "GRP", "CST", "18", "IBH", BigDecimal.ONE));
        ruleList.add(new RefInstructionRulesDto("G_LOAN", "GR002", "Customer not eligible for personal loan", "GR002", "CR002", "Customer cannot be younger than 18 years", "CR002", "GRP", "CST", "18", "STL", BigDecimal.ONE));

        ruleList.add(new RefInstructionRulesDto("G_CASH_ISA", "GR003", "Customer not eligible for cash ISA", "GR003", "CR003", "Customer cannot be younger than 16 years", "CR003", "GRP", "CST", "16", "IBL", BigDecimal.ONE));

        return ruleList;
    }

    public RefInstructionRulesDto createRuleDto(String insMnemonic, String groupRule, String groupDesc, String groupCmsReason, String rule, String ruleDesc, String cmsReason, String groupRuleType, String ruleType, String ruleParamValue, String channel, BigDecimal ruleParamSeq) {
        return new RefInstructionRulesDto(insMnemonic, groupRule, groupDesc, groupCmsReason, rule, ruleDesc, cmsReason, groupRuleType, ruleType, ruleParamValue, channel, ruleParamSeq);
    }

    public DetermineElegibileInstructionsRequest createEligibilityRequestForLoans(String mnemonic, String ocisId, String channel, String contactPointId, int year, int month, int day) {
        RequestHeader header = createEligibilityRequestHeader(channel, TestDataHelper.TEST_INTERACTION_ID, ocisId, TEST_CUSTOMER_ID, contactPointId);
        EligibilityRequestBuilder requestBuilder = new EligibilityRequestBuilder();
        requestBuilder.header(header);
        List<String> candidateInstructions = new ArrayList();
        candidateInstructions.add(mnemonic);
        requestBuilder.candidateInstructions(candidateInstructions);

        List<ProductArrangement> prodArrList = new ArrayList<>();
        prodArrList.add(createCustomerArrangements("77251902224906"));
        prodArrList.add(createCustomerArrangements("77251902224906"));
        requestBuilder.customerArrangements(prodArrList);

        Individual individual = new Individual();
        individual.setBirthDate(createXMLGregorianCalendar(year, month, day));

        requestBuilder.individual(individual);
        return requestBuilder.build();
    }

    public F336Req createF336Request(BapiInformation bapiInformation) {
        String host = bapiInformation.getBAPIHeader().getStpartyObo().getHost();
        F336Req request = new F336Req();
        request.setExtSysId(EXTERNAL_SYS_ID);
        String partyId = null;
        if (null != bapiInformation && null != bapiInformation.getBAPIHeader() && null != bapiInformation.getBAPIHeader().getStpartyObo()) {
            host = bapiInformation.getBAPIHeader().getStpartyObo().getHost();
            partyId = bapiInformation.getBAPIHeader().getStpartyObo().getPartyid();
        }
        request.setExtPartyIdTx(partyId);
        request.setPartyId(0l);
        request.setPartyExtSysId((null != host && host.endsWith("L")) ? (short) 1 : (short) 2);
        request.setClosurePeriodMonthsDr(CLOSURE_PERIOD_MONTHS_DR);
        request.setClosedOnlyIn(CLOSED_ONLY_IN);
        request.setCustomerConsentIn(CUSTOMER_CONSENT_IN);
        return request;

    }

    public StB162AUserAccReadList createB162Request(com.lloydsbanking.salsa.soap.fs.user.StHeader stHeader) {
        StB162AUserAccReadList request = new StB162AUserAccReadList();

        request.setStheader(stHeader);
        request.setAccmorekey(ACC_MOR_KEY);
        request.setBForceHostCall(false);
        return request;
    }

    public E184Resp createE141Response(int indicatorCd) {
        E184Resp e184Resp = new E184Resp();

        StandardIndicators1Gp standardIndicators1Gp = new StandardIndicators1Gp();
        standardIndicators1Gp.setIndicator1Cd(indicatorCd);

        Indicator1Gp indicator1Gp = new Indicator1Gp();
        List<StandardIndicators1Gp> standardIndicators1Gps = indicator1Gp.getStandardIndicators1Gp();
        standardIndicators1Gps.add(standardIndicators1Gp);

        e184Resp.setIndicator1Gp(indicator1Gp);
        return e184Resp;
    }

    public StB093AEventLogReadList createRequest(String threshold, String eventId, RequestHeader header) throws DatatypeConfigurationException, DetermineEligibleInstructionsInternalServiceErrorMsg {
        BapiInformation bapiInformation = new HeaderRetriever().getBapiInformationHeader(header.getLloydsHeaders());
        ServiceRequest serviceRequest = new HeaderRetriever().getServiceRequest(header.getLloydsHeaders());
        ContactPoint contactPoint = new HeaderRetriever().getContactPoint(header.getLloydsHeaders());
        return createB093Request(getStartDate(threshold), getDateInXMLGregorianCalendar(new Date(new Date().getTime())), createStHeader(bapiInformation, serviceRequest, contactPoint
                .getContactPointId()), bapiInformation.getBAPIHeader().getUseridAuthor(), eventId);

    }

    public StB093AEventLogReadList createB093Request(XMLGregorianCalendar startDate, XMLGregorianCalendar endDate, com.lloydsbanking.salsa.soap.fs.system.StHeader stHeader, String useridAuthor, String eventId) {
        StB093AEventLogReadList b093Request = new StB093AEventLogReadList();
        b093Request.setStheader(stHeader);
        if (null != eventId) {
            b093Request.setEvttype(eventId);
        }
        b093Request.setTmstmpEnd(endDate);
        b093Request.setTmstmpStart(startDate);
        b093Request.setUserid(useridAuthor);
        return b093Request;
    }

    public com.lloydsbanking.salsa.soap.fs.system.StHeader createStHeader(BapiInformation bapiInformation, ServiceRequest serviceRequest, String contactPointId) {
        com.lloydsbanking.salsa.downstream.fsystem.convert.BapiHeaderToStHeaderConverter converter = new com.lloydsbanking.salsa.downstream.fsystem.convert.BapiHeaderToStHeaderConverter();
        return converter.convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPointId);
    }

    public XMLGregorianCalendar getStartDate(String threshold) throws DatatypeConfigurationException {
        String[] dateArray = threshold.split(":");
        Calendar date = new GregorianCalendar();
        date.add(Calendar.DATE, -Integer.parseInt(dateArray[0]));
        return getDateInXMLGregorianCalendar(date.getTime());

    }

    public XMLGregorianCalendar getDateInXMLGregorianCalendar(Date dateTime) throws DatatypeConfigurationException {

        GregorianCalendar gregorianCalInstance = (GregorianCalendar) GregorianCalendar.getInstance();
        gregorianCalInstance.setTime(dateTime);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalInstance);
    }

    public StB093BEventLogReadList createB093Response(boolean isAboveThresold) {
        StB093BEventLogReadList response = new StB093BEventLogReadList();
        response.setSterror(new com.lloydsbanking.salsa.soap.fs.system.StError());
        response.getSterror().setErrorno(0);
        response.getAsteventlogreadlist().addAll(createAsteventlogreadlist(isAboveThresold));
        return response;
    }

    private List<TEventLogReadList> createAsteventlogreadlist(boolean isAboveThresold) {
        List<TEventLogReadList> eventLogReadList = new ArrayList<>();
        TEventLogReadList tEventLogReadList1 = new TEventLogReadList();
        if (isAboveThresold) {
            tEventLogReadList1.setEvtlogtext("B_LN_ACCPT");
        }
        else {
            tEventLogReadList1.setEvtlogtext("110099977");
        }
        eventLogReadList.add(tEventLogReadList1);
        TEventLogReadList tEventLogReadList2 = new TEventLogReadList();
        if (isAboveThresold) {
            tEventLogReadList2.setEvtlogtext("B_LN_DEC");
        }
        else {
            tEventLogReadList2.setEvtlogtext("112233440");
        }

        eventLogReadList.add(tEventLogReadList2);
        return eventLogReadList;
    }

    public DetermineElegibileInstructionsRequest createBusinessEligibilityRequest(String insMnemonic, String arrangementType, String ocisId, String channel, String contactPointId, int accountStartDate, int ovrdrftStartDate) {
        RequestHeader header = createEligibilityRequestHeader(channel, TestDataHelper.TEST_INTERACTION, ocisId, TEST_CUSTOMER_ID, contactPointId);
        EligibilityRequestBuilder requestBuilder = new EligibilityRequestBuilder();

        List<String> candidateInstructions = new ArrayList();
        candidateInstructions.add(insMnemonic);


        ProductArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setSortCode("770908");
        depositArrangement.setAccountNumber("22562768");
        depositArrangement.setAccountHost("T");
        depositArrangement.setAccountType("T8000776000");
        depositArrangement.setStartDate(addToCurrentDate(accountStartDate));
        List<String> relatedEventsForDA = new ArrayList();
        relatedEventsForDA.add("37");
        relatedEventsForDA.add("55");
        relatedEventsForDA.add("30");
        relatedEventsForDA.add("251");
        relatedEventsForDA.add("333");
        depositArrangement.getRelatedEvents().addAll(relatedEventsForDA);
        depositArrangement.setLifecycleStatus(ProductArrangementLifecycleStatus.EFFECTIVE);
        if (null == arrangementType || arrangementType.equals("")) {
            depositArrangement.setArrangementType("CURRENT");
        }
        else {
            depositArrangement.setArrangementType(arrangementType);
        }
        depositArrangement.setProductType(ProductType.ACCOUNT);
        OvrdrftDtls ovrdrftDtls = new OvrdrftDtls();
        ovrdrftDtls.setStartDate(addToCurrentDate(ovrdrftStartDate));

        depositArrangement.setOvrdrftDtls(ovrdrftDtls);
        Customer customer = new Customer();
        customer.setPartyId("77090801293407");
        List<Customer> customers = new ArrayList();
        customers.add(customer);

        depositArrangement.getParticipantCusomters().addAll(customers);
        depositArrangement.setCapAccountRestricted(false);

        Individual individual = new Individual();
        individual.setBirthDate(createXMLGregorianCalendar(1968, 01, 16));
        List<ProductArrangement> lstProductArrangement = new ArrayList();
        lstProductArrangement.add(depositArrangement);

        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setBusinessId("+00641085068");
        businessArrangement.setEnttyTyp("004");
        businessArrangement.setRolesInCtxt("CUS");
        List<BusinessArrangement> businessArrangements = new ArrayList();
        businessArrangements.add(businessArrangement);

        String selctdBusnsId = "+00641085068";


        return requestBuilder.header(header)
                .customerArrangements(lstProductArrangement)
                .candidateInstructions(candidateInstructions)
                .businessArrangement(businessArrangements)
                .individual(individual)
                .selctdBusnsId(selctdBusnsId)
                .build();
    }

    public E184Resp createE184Response(int indicatorCd) {
        E184Resp e184Resp = new E184Resp();
        e184Resp.setE184Result(new E184Result());
        e184Resp.getE184Result().setResultCondition(new ResultCondition());
        e184Resp.getE184Result().getResultCondition().setReasonCode(0);
        StandardIndicators1Gp standardIndicators1Gp = new StandardIndicators1Gp();
        standardIndicators1Gp.setIndicator1Cd(indicatorCd);

        Indicator1Gp indicator1Gp = new Indicator1Gp();
        List<StandardIndicators1Gp> standardIndicators1Gps = indicator1Gp.getStandardIndicators1Gp();
        standardIndicators1Gps.add(standardIndicators1Gp);


        e184Resp.setIndicator1Gp(indicator1Gp);
        return e184Resp;
    }

    public RefInstructionRulesDto createRefInstructionRulesDto(String ruleCode, String ruleParamValue) {
        RefInstructionRulesDto refInstructionRulesDto = new RefInstructionRulesDto();
        refInstructionRulesDto.setCmsReason(ruleCode);
        refInstructionRulesDto.setRule(ruleCode);
        refInstructionRulesDto.setRuleParamValue(ruleParamValue);
        return refInstructionRulesDto;
    }

    public E184Req createE184Request(String sortCode, String accountNo) {
        E184Req e184Req = new E184Req();

        e184Req.setCBSAccountNoId(sortCode + accountNo);
        return e184Req;
    }

    public XMLGregorianCalendar subtractFromCurrentDate(int numberOfDays) {

        LocalDate dateTime = new LocalDate();
        LocalDate newDate = dateTime.minusDays(numberOfDays);
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        return datatypeFactory.newXMLGregorianCalendar(newDate.toString());
    }

    public XMLGregorianCalendar addToCurrentDate(int numberOfDays) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, numberOfDays);

        DatatypeFactory datatypeFactory;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        return datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
    }

}
