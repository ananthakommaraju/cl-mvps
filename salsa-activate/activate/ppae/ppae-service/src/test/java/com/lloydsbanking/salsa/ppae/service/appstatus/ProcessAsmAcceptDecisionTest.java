package com.lloydsbanking.salsa.ppae.service.appstatus;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import lib_sim_bo.businessobjects.*;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class ProcessAsmAcceptDecisionTest {

    ProcessAsmAcceptDecision processAsmAcceptDecision;
    ProductArrangement productArrangement;
    TestDataHelper testDataHelper;
    ProcessPendingArrangementEventRequest request;
    ProductOffer productOffer;

    String productOfferIdentifier = null;
    String productIdentifier = null;


    @Before
    public void setUp() throws DatatypeConfigurationException {

        processAsmAcceptDecision = new ProcessAsmAcceptDecision();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createProductArrangement();
        request = testDataHelper.createPpaeRequest("1", "LTB");
        productOffer = new ProductOffer();
        processAsmAcceptDecision.communicationManager = mock(CommunicationManager.class);
        processAsmAcceptDecision.updatePamService = mock(UpdatePamService.class);
        productOfferIdentifier = productArrangement.getAssociatedProduct().getProductoffer().get(0).getProdOfferIdentifier();
        productIdentifier = productArrangement.getAssociatedProduct().getProductIdentifier();
    }

    @Test
    public void testCheckProductAndStatusForCA() {
        productArrangement.setArrangementType("CA");
        processAsmAcceptDecision.checkProductAndStatus(productArrangement, request.getHeader(), productOfferIdentifier, productIdentifier, new PpaeInvocationIdentifier());

    }

    @Test
    public void testForArrTypeCCAndCheckOptionCode() {
        productArrangement.setArrangementType("CC");
        Product product = new Product();
        product.setStatusCode("isAccepted");
        ProductOffer productOffer = new ProductOffer();
        productOffer.setOfferType("2004");
        productOffer.setProdOfferIdentifier("1000006");
        productOffer.setStatus("23e445");
        product.getProductoffer().add(productOffer);
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("abc");
        productArrangement.getOfferedProducts().add(product);

        productArrangement.getOfferedProducts().get(0).getProductoptions().add(productOptions);
        productArrangement.getOfferedProducts().get(0).getProductoptions().get(0).setOptionsCode("CREDIT_CARD_OFFERED_FLAG");
        productArrangement.getOfferedProducts().get(0).getProductoptions().get(0).setOptionsCode("DEBIT_CARD_RISK_CODE");
        processAsmAcceptDecision.checkProductAndStatus(productArrangement, request.getHeader(), productOfferIdentifier, productIdentifier, new PpaeInvocationIdentifier());

    }

    @Test
    public void testForStatusOtherThanCCorCA() {
        productArrangement.setArrangementType("SA");
        processAsmAcceptDecision.checkProductAndStatus(productArrangement, request.getHeader(), productOfferIdentifier, productIdentifier, new PpaeInvocationIdentifier());
        assertEquals("0", productArrangement.getRetryCount().toString());
    }

    @Test
    public void testCheckReferralCodeForCA() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangementForSA();
        depositArrangement.setArrangementType("CA");
        Product product = new Product();
        product.setStatusCode("isAccepted");
        ProductOffer productOffer = new ProductOffer();
        productOffer.setOfferType("2004");
        productOffer.setProdOfferIdentifier("1000006");
        productOffer.setStatus("23e445");
        product.setProductIdentifier("addasddsads");
        product.getProductoffer().add(productOffer);
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("abc");
        depositArrangement.getOfferedProducts().add(product);
        depositArrangement.getOfferedProducts().get(0).getProductoptions().add(productOptions);
        depositArrangement.getOfferedProducts().get(0).getProductoptions().get(0).setOptionsCode("CREDIT_CARD_OFFERED_FLAG");
        processAsmAcceptDecision.checkProductAndStatus(depositArrangement, request.getHeader(), productOfferIdentifier, productIdentifier, new PpaeInvocationIdentifier());

    }

    @Test
    public void testCheckReferralCodeForCAForDebitCardRiskCode() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangementForSA();
        depositArrangement.setArrangementType("CA");
        Product product = new Product();
        product.setStatusCode("isAccepted");
        ProductOffer productOffer = new ProductOffer();
        productOffer.setOfferType("2004");
        productOffer.setProdOfferIdentifier("1000006");
        productOffer.setStatus("23e445");
        product.setProductIdentifier("addasddsads");
        product.getProductoffer().add(productOffer);
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("abc");
        depositArrangement.getOfferedProducts().add(product);
        depositArrangement.getOfferedProducts().get(0).getProductoptions().add(productOptions);
        depositArrangement.getOfferedProducts().get(0).getProductoptions().get(0).setOptionsCode("DEBIT_CARD_RISK_CODE");
        processAsmAcceptDecision.checkProductAndStatus(depositArrangement, request.getHeader(), productOfferIdentifier, productIdentifier, new PpaeInvocationIdentifier());

    }
}


