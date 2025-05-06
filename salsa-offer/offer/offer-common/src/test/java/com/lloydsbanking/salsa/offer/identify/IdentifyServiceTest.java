package com.lloydsbanking.salsa.offer.identify;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.identify.convert.AuditDataFactory;
import com.lloydsbanking.salsa.offer.identify.convert.F061ToInvolvedPartyDetailsResponseConverter;
import com.lloydsbanking.salsa.offer.identify.downstream.InvolvedPartyMatchRetriever;
import com.lloydsbanking.salsa.offer.identify.downstream.InvolvedPartyRetriever;
import com.lloydsbanking.salsa.offer.identify.downstream.ProductHoldingRetriever;
import com.lloydsbanking.salsa.offer.identify.evaluate.KYCStatusEvaluator;
import com.lloydsbanking.salsa.offer.identify.utility.CustomerUtility;
import com.lloydsbanking.salsa.offer.identify.utility.DeliveryPointSuffixAnalyser;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.PartyEnqData;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Resp;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class IdentifyServiceTest {

    IdentifyService involvedPartyIdentifier;

    DepositArrangement depositArrangement;

    Customer customer;

    DeliveryPointSuffixAnalyser deliveryPointSuffixAnalyser;

    InvolvedPartyMatchRetriever involvedPartyMatchRetriever;

    TestDataHelper testDataHelper;

    private AuditDataFactory auditDataFactory;

    private ProductHoldingRetriever productHoldingRetriever;

    private InvolvedPartyRetriever involvedPartyRetriever;

    private CustomerUtility customerUtility;

    private KYCStatusEvaluator kycStatusEvaluator;

    private F061ToInvolvedPartyDetailsResponseConverter f061ToInvolvedPartyDetailsResponseConverter;

    private CustomerTraceLog customerTraceLog;

    private ProductTraceLog productTraceLog;

    RequestHeader requestHeader;

    @Before
    public void setUp() {
        deliveryPointSuffixAnalyser = new DeliveryPointSuffixAnalyser();
        involvedPartyMatchRetriever = mock(InvolvedPartyMatchRetriever.class);
        auditDataFactory = new AuditDataFactory();
        productHoldingRetriever = mock(ProductHoldingRetriever.class);
        involvedPartyRetriever = mock(InvolvedPartyRetriever.class);
        customerUtility = new CustomerUtility();
        kycStatusEvaluator = mock(KYCStatusEvaluator.class);
        f061ToInvolvedPartyDetailsResponseConverter = mock(F061ToInvolvedPartyDetailsResponseConverter.class);
        customerTraceLog = mock(CustomerTraceLog.class);
        productTraceLog = mock(ProductTraceLog.class);
        involvedPartyIdentifier = new IdentifyService(involvedPartyRetriever, productHoldingRetriever, deliveryPointSuffixAnalyser, involvedPartyMatchRetriever, customerUtility, kycStatusEvaluator, f061ToInvolvedPartyDetailsResponseConverter, customerTraceLog, productTraceLog);
        testDataHelper = new TestDataHelper();
        depositArrangement =testDataHelper.createDepositArrangement();
        customer = depositArrangement.getPrimaryInvolvedParty();
        requestHeader = testDataHelper.createOpaPcaRequestHeader("LTB");
    }

    @Test
    public void setsCustomerIdentifierForNotEmptyCustIdInRequest() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, ExternalServiceErrorMsg, OfferException {
        customer.setCustomerIdentifier("1129336");
        when(involvedPartyMatchRetriever.getInvolvedPartyMatch(customer.getPostalAddress(), customer.getIsPlayedBy(), requestHeader)).thenReturn(testDataHelper.createF447Response(20));
        when(productHoldingRetriever.getProductHoldings(requestHeader, customer.getCustomerIdentifier())).thenReturn(testDataHelper.createExistingProducts());
        when(involvedPartyRetriever.retrieveInvolvedPartyDetails(requestHeader, customer.getCustomerIdentifier())).thenReturn(testDataHelper.createF061Resp(20));
        when(kycStatusEvaluator.isKycCompliant(any(Customer.class), anyList(), any(PartyEnqData.class))).thenReturn(true);
        involvedPartyIdentifier.identifyInvolvedParty(requestHeader, depositArrangement
                .getPrimaryInvolvedParty());
        assertEquals(depositArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), customer.getCustomerIdentifier());
        assertEquals(depositArrangement.getPrimaryInvolvedParty().getCustomerSegment(), customer.getCustomerSegment());
        assertEquals(depositArrangement.getPrimaryInvolvedParty().getSourceSystemId(), customer.getSourceSystemId());
    }

    @Test
    public void setsCustomerIdentifierForNonEmptyCustIdAndDeliveryPointSuffixPresentInCurrentAddressWithPartyIdInRequest() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, ExternalServiceErrorMsg, OfferException {
        customer.setCustomerIdentifier("1129336");
        customer.setCustomerSegment("8");
        when(involvedPartyMatchRetriever.getInvolvedPartyMatch(customer.getPostalAddress(), customer.getIsPlayedBy(), requestHeader)).thenReturn(testDataHelper.createF447Response(20));
        when(productHoldingRetriever.getProductHoldings(requestHeader, customer.getCustomerIdentifier())).thenReturn(testDataHelper.createExistingProducts());
        when(involvedPartyRetriever.retrieveInvolvedPartyDetails(requestHeader, customer.getCustomerIdentifier())).thenReturn(testDataHelper.createF061Resp(20));

        involvedPartyIdentifier.identifyInvolvedParty(requestHeader,
                customer);
        List<ProductPartyData> productPartyData = new ArrayList<>();
        //verify(ocisDetailsConverter).setProductHoldingsAndIndentifyResponse(productArrangement, null, productPartyData,customer, true, null);
    }


    @Test
    public void setsCustomerIdentifierForEmptyCustIdAndDeliveryPointSuffixPresentInCurrentAddressWithPartyIdInRequest() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, ExternalServiceErrorMsg, OfferException {
        customer.setCustomerIdentifier(null);
        customer.setCustomerSegment("8");
        F447Resp involvedPartyMatch = testDataHelper.createF447Response(0);
        involvedPartyMatch.setPartyId(new Long("1234"));
        involvedPartyMatch.setCIDPersId("4567");
        when(involvedPartyMatchRetriever.getInvolvedPartyMatch(customer.getPostalAddress(), customer.getIsPlayedBy(), requestHeader)).thenReturn(testDataHelper.createF447Response(20));
        when(productHoldingRetriever.getProductHoldings(requestHeader, "12345")).thenReturn(testDataHelper.createExistingProducts());
        when(involvedPartyRetriever.retrieveInvolvedPartyDetails(requestHeader, "12345")).thenReturn(testDataHelper.createF061Resp(20));
        when(kycStatusEvaluator.isKycCompliant(any(Customer.class), anyList(), any(PartyEnqData.class))).thenReturn(true);
       /* when(deliveryPointSuffixAnalyser.isDeliveryPointSuffixPresent(any(ArrayList.class))).thenReturn(true);
        when(involvedPartyMatchRetriever.getInvolvedPartyMatch(any(ArrayList.class), any(Individual.class), any(RequestHeader.class))).thenReturn(involvedPartyMatch);
       */
        involvedPartyIdentifier.identifyInvolvedParty(requestHeader,
                customer);

        assertEquals("12345", customer.getCustomerIdentifier());


    }


    @Test
    public void setsCustomerIdentifierForEmptyCustIdAndDeliveryPointSuffixNotPresentInCurrentAddressInRequest() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, ExternalServiceErrorMsg, OfferException {
        customer.setCustomerIdentifier(null);
        customer.setCustomerSegment(null);
        customer.getPostalAddress().get(0).setIsPAFFormat(true);
        customer.getPostalAddress().get(0).getStructuredAddress().setPointSuffix(null);
        involvedPartyIdentifier.identifyInvolvedParty(requestHeader,
                customer);
        assertEquals("3", customer.getCustomerSegment());
    }

}
