package com.lloydsbanking.salsa.offer.apply;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;

import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.apply.convert.OfferToRpcRequestConverter;
import com.lloydsbanking.salsa.offer.apply.downstream.CreditScoreRetriever;
import com.lloydsbanking.salsa.offer.apply.downstream.FraudDecisionRetriever;
import com.lloydsbanking.salsa.offer.apply.downstream.ProductConditionsRetriever;
import com.lloydsbanking.salsa.offer.apply.evaluate.ApplicationStatusEvaluator;
import com.lloydsbanking.salsa.offer.apply.evaluate.ProductOptionsEvaluator;
import com.lloydsbanking.salsa.offer.apply.evaluate.RuleConditionsEvaluator;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ApplyServiceTest {
    private ApplyService applyService;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        applyService = new ApplyService();
        testDataHelper = new TestDataHelper();
        applyService.applicationStatusEvaluator = mock(ApplicationStatusEvaluator.class);
        applyService.fraudDecisionRetriever = mock(FraudDecisionRetriever.class);
        applyService.applicationStatusEvaluator = mock(ApplicationStatusEvaluator.class);
        applyService.headerRetriever = new HeaderRetriever();
        applyService.productOptionsEvaluator = mock(ProductOptionsEvaluator.class);
        applyService.offerLookupDataRetriever = mock(LookupDataRetriever.class);
        applyService.ruleConditionsEvaluator = mock(RuleConditionsEvaluator.class);
        applyService.offerToRpcRequestConverter = mock(OfferToRpcRequestConverter.class);
        applyService.creditScoreRetriever = mock(CreditScoreRetriever.class);
        applyService.productConditionsRetriever = mock(ProductConditionsRetriever.class);

    }

    @Test
    public void testApplyCreditRatingScale() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, DataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        RequestHeader header = testDataHelper.createOpaPcaRequestHeader("LTB");
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement();

        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(0, new CustomerScore());
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("N/A");
        F204Resp f204Resp = testDataHelper.createF204Response(0);
        f204Resp.setASMCreditScoreResultCd("2");

        F205Resp f205Resp = testDataHelper.createF205Response(0);
        f205Resp.setASMCreditScoreResultCd("1");

        List<ProductFamily> prodFamList = new ArrayList<>();
        ProductFamily productFamily = new ProductFamily();
        productFamily.setFamilyIdentifier("1");
        productFamily.setFamilyDescription("desc");
        prodFamList.add(productFamily);

        Product associatedProduct = new Product();
        associatedProduct.setProductIdentifier("20198");

        ExtSysProdIdentifier extSysProdIdentifier1 = new ExtSysProdIdentifier();
        extSysProdIdentifier1.setSystemCode("00107");
        extSysProdIdentifier1.setProductIdentifier("901");
        associatedProduct.getExternalSystemProductIdentifier().add(extSysProdIdentifier1);
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode("10107");
        extSysProdIdentifier.setProductIdentifier("201");
        associatedProduct.getExternalSystemProductIdentifier().add(extSysProdIdentifier);


        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsCode("UP_SELL_DISPLAY_VALUE");
        productOptions1.setOptionsValue("0");
        associatedProduct.getProductoptions().add(productOptions1);

        associatedProduct.setProductName("Account B");

        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("P_CLUB");
        associatedProduct.setInstructionDetails(instructionDetails);

        RetrieveProductConditionsResponse retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        retrieveProductConditionsResponse.setIsGauranteedOfferAvailable(true);
        retrieveProductConditionsResponse.getProduct().add(new Product());
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());

        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("1");
        productOptions.setOptionsValue("2");
        productOptionsList.add(productOptions);

        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();
        Product product = new Product();
        product.setProductIdentifier("1");
        rpcRequest.setProduct(product);
        RequestHeader requestHeader = testDataHelper.createOpaPcaRequestHeader("LTB");
        requestHeader.setArrangementId("1");
        rpcRequest.setHeader(requestHeader);
        ProductFamily productFamily1 = new ProductFamily();
        productFamily1.setFamilyDescription("desc");
        productFamily1.setFamilyIdentifier("11");
        rpcRequest.getProductFamily().add(productFamily1);

        when(applyService.fraudDecisionRetriever.getFraudDecision(any(String.class), any(String.class), any(String.class), any(ArrayList.class), any(String.class), any(ArrayList.class), any(Customer.class), any(ArrayList.class), any(RequestHeader.class))).thenReturn(f204Resp);
        when(applyService.creditScoreRetriever.retrieveCreditDecision(any(ProductArrangement.class), any(RequestHeader.class))).thenReturn(f205Resp);
        when(applyService.applicationStatusEvaluator.getApplicationStatusForAsmFraudDecision(any(CustomerScore.class), any(F204Resp.class), any(RequestHeader.class))).thenReturn("1002");
        when(applyService.applicationStatusEvaluator.getApplicationStatusForCreditScoreDecision(any(F205Resp.class), any(String.class), any(CustomerScore.class), any(RequestHeader.class))).thenReturn("1002");
        when(applyService.offerToRpcRequestConverter.convertOfferToRpcRequest(any(F205Resp.class), any(RequestHeader.class))).thenReturn(new RetrieveProductConditionsRequest());
        when(applyService.productOptionsEvaluator.getProductOptions(any(F205Resp.class), any(String.class))).thenReturn(productOptionsList);
        when(applyService.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");

        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        applyService.applyCreditRatingScale((DepositArrangement) depositArrangement, requestHeader, true, false);

        assertNotNull(depositArrangement.getOfferedProducts());
    }

    @Test
    public void testApplyCreditRatingScaleAppStatusDecline() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, DataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        RequestHeader header = testDataHelper.createOpaPcaRequestHeader("LTB");
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement();

        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(0, new CustomerScore());
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("N/A");
        F204Resp f204Resp = testDataHelper.createF204Response(0);
        f204Resp.setASMCreditScoreResultCd("2");

        F205Resp f205Resp = testDataHelper.createF205Response(0);
        f205Resp.setASMCreditScoreResultCd("1");

        List<ProductFamily> prodFamList = new ArrayList<>();
        ProductFamily productFamily = new ProductFamily();
        productFamily.setFamilyIdentifier("1");
        productFamily.setFamilyDescription("desc");
        prodFamList.add(productFamily);

        Product associatedProduct = new Product();
        associatedProduct.setProductIdentifier("20198");

        ExtSysProdIdentifier extSysProdIdentifier1 = new ExtSysProdIdentifier();
        extSysProdIdentifier1.setSystemCode("00107");
        extSysProdIdentifier1.setProductIdentifier("901");
        associatedProduct.getExternalSystemProductIdentifier().add(extSysProdIdentifier1);
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode("10107");
        extSysProdIdentifier.setProductIdentifier("201");
        associatedProduct.getExternalSystemProductIdentifier().add(extSysProdIdentifier);


        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsCode("UP_SELL_DISPLAY_VALUE");
        productOptions1.setOptionsValue("0");
        associatedProduct.getProductoptions().add(productOptions1);

        associatedProduct.setProductName("Account B");

        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("P_CLUB");
        associatedProduct.setInstructionDetails(instructionDetails);

        RetrieveProductConditionsResponse retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        retrieveProductConditionsResponse.setIsGauranteedOfferAvailable(true);
        retrieveProductConditionsResponse.getProduct().add(new Product());
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());

        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("1");
        productOptions.setOptionsValue("2");
        productOptionsList.add(productOptions);

        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();
        Product product = new Product();
        product.setProductIdentifier("1");
        rpcRequest.setProduct(product);
        RequestHeader requestHeader = testDataHelper.createOpaPcaRequestHeader("LTB");
        requestHeader.setArrangementId("1");
        rpcRequest.setHeader(requestHeader);
        ProductFamily productFamily1 = new ProductFamily();
        productFamily1.setFamilyDescription("desc");
        productFamily1.setFamilyIdentifier("11");
        rpcRequest.getProductFamily().add(productFamily1);


        when(applyService.fraudDecisionRetriever.getFraudDecision(any(String.class), any(String.class), any(String.class), any(ArrayList.class), any(String.class), any(ArrayList.class), any(Customer.class), any(ArrayList.class), any(RequestHeader.class))).thenReturn(f204Resp);
        when(applyService.creditScoreRetriever.retrieveCreditDecision(any(ProductArrangement.class), any(RequestHeader.class))).thenReturn(f205Resp);
        when(applyService.applicationStatusEvaluator.getApplicationStatusForAsmFraudDecision(any(CustomerScore.class), any(F204Resp.class), any(RequestHeader.class))).thenReturn("1004");
        when(applyService.applicationStatusEvaluator.getApplicationStatusForCreditScoreDecision(any(F205Resp.class), any(String.class), any(CustomerScore.class), any(RequestHeader.class))).thenReturn("1002");
        when(applyService.offerToRpcRequestConverter.convertOfferToRpcRequest(any(F205Resp.class), any(RequestHeader.class))).thenReturn(new RetrieveProductConditionsRequest());
        when(applyService.productOptionsEvaluator.getProductOptions(any(F205Resp.class), any(String.class))).thenReturn(productOptionsList);
        when(applyService.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");

        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        applyService.applyCreditRatingScale((DepositArrangement) depositArrangement, requestHeader, true, false);

        assertNotNull(depositArrangement.getOfferedProducts());
    }

    @Test( expected = OfferException.class)
    public void testApplyCreditRatingScaleDataNotAvailableError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, DataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        RequestHeader header = testDataHelper.createOpaPcaRequestHeader("LTB");
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement();

        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(0, new CustomerScore());
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("N/A");
        F204Resp f204Resp = testDataHelper.createF204Response(0);
        f204Resp.setASMCreditScoreResultCd("2");

        F205Resp f205Resp = testDataHelper.createF205Response(0);
        f205Resp.setASMCreditScoreResultCd("1");

        List<ProductFamily> prodFamList = new ArrayList<>();
        ProductFamily productFamily = new ProductFamily();
        productFamily.setFamilyIdentifier("1");
        productFamily.setFamilyDescription("desc");
        prodFamList.add(productFamily);

        Product associatedProduct = new Product();
        associatedProduct.setProductIdentifier("20198");

        ExtSysProdIdentifier extSysProdIdentifier1 = new ExtSysProdIdentifier();
        extSysProdIdentifier1.setSystemCode("00107");
        extSysProdIdentifier1.setProductIdentifier("901");
        associatedProduct.getExternalSystemProductIdentifier().add(extSysProdIdentifier1);
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode("10107");
        extSysProdIdentifier.setProductIdentifier("201");
        associatedProduct.getExternalSystemProductIdentifier().add(extSysProdIdentifier);


        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsCode("UP_SELL_DISPLAY_VALUE");
        productOptions1.setOptionsValue("0");
        associatedProduct.getProductoptions().add(productOptions1);

        associatedProduct.setProductName("Account B");

        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("P_CLUB");
        associatedProduct.setInstructionDetails(instructionDetails);

        RetrieveProductConditionsResponse retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        retrieveProductConditionsResponse.setIsGauranteedOfferAvailable(true);
        retrieveProductConditionsResponse.getProduct().add(new Product());
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());

        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("1");
        productOptions.setOptionsValue("2");
        productOptionsList.add(productOptions);

        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();
        Product product = new Product();
        product.setProductIdentifier("1");
        rpcRequest.setProduct(product);
        RequestHeader requestHeader = testDataHelper.createOpaPcaRequestHeader("LTB");
        requestHeader.setArrangementId("1");
        rpcRequest.setHeader(requestHeader);
        ProductFamily productFamily1 = new ProductFamily();
        productFamily1.setFamilyDescription("desc");
        productFamily1.setFamilyIdentifier("11");
        rpcRequest.getProductFamily().add(productFamily1);


        when(applyService.fraudDecisionRetriever.getFraudDecision(any(String.class), any(String.class), any(String.class), any(ArrayList.class), any(String.class), any(ArrayList.class), any(Customer.class), any(ArrayList.class), any(RequestHeader.class))).thenReturn(f204Resp);
        when(applyService.creditScoreRetriever.retrieveCreditDecision(any(ProductArrangement.class), any(RequestHeader.class))).thenReturn(f205Resp);
        when(applyService.applicationStatusEvaluator.getApplicationStatusForAsmFraudDecision(any(CustomerScore.class), any(F204Resp.class), any(RequestHeader.class))).thenThrow(DataNotAvailableErrorMsg.class);
        when(applyService.applicationStatusEvaluator.getApplicationStatusForCreditScoreDecision(any(F205Resp.class), any(String.class), any(CustomerScore.class), any(RequestHeader.class))).thenReturn("1002");
        when(applyService.offerToRpcRequestConverter.convertOfferToRpcRequest(any(F205Resp.class), any(RequestHeader.class))).thenReturn(new RetrieveProductConditionsRequest());
        when(applyService.productOptionsEvaluator.getProductOptions(any(F205Resp.class), any(String.class))).thenReturn(productOptionsList);
        when(applyService.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");

        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        applyService.applyCreditRatingScale((DepositArrangement) depositArrangement, requestHeader, true, false);

        assertNotNull(depositArrangement.getOfferedProducts());
    }


    @Test
    public void testApplyCreditRatingScaleNullF204Resp() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, DataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement();
        when(applyService.fraudDecisionRetriever.getFraudDecision(any(String.class), any(String.class), any(String.class), any(ArrayList.class), any(String.class), any(ArrayList.class), any(Customer.class), any(ArrayList.class), any(RequestHeader.class))).thenReturn(null);
        RequestHeader requestHeader = testDataHelper.createOpaPcaRequestHeader("LTB");
        requestHeader.setArrangementId("1");
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        applyService.applyCreditRatingScale(depositArrangement, requestHeader, true, false);
        assertTrue(depositArrangement.getOfferedProducts().isEmpty());
    }


    @Test(expected = OfferException.class )
    public void testApplyCreditRatingScaleF204ThrowsExternalServiceError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, DataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        when(applyService.fraudDecisionRetriever.getFraudDecision(any(String.class), any(String.class), any(String.class), any(ArrayList.class), any(String.class), any(ArrayList.class), any(Customer.class), any(ArrayList.class), any(RequestHeader.class))).thenThrow(ExternalServiceErrorMsg.class);
        RequestHeader requestHeader = testDataHelper.createOpaPcaRequestHeader("LTB");
        applyService.applyCreditRatingScale(depositArrangement, requestHeader, true, false);
        assertTrue(depositArrangement.getOfferedProducts().isEmpty());
    }

    @Test
    public void testApplyCreditRatingScaleF204ThrowsExternalBusinessError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, DataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        when(applyService.fraudDecisionRetriever.getFraudDecision(any(String.class), any(String.class), any(String.class), any(ArrayList.class), any(String.class), any(ArrayList.class), any(Customer.class), any(ArrayList.class), any(RequestHeader.class))).thenReturn(null);
        RequestHeader requestHeader = testDataHelper.createOpaPcaRequestHeader("LTB");
        applyService.applyCreditRatingScale(depositArrangement, requestHeader, true, false);
        assertTrue(depositArrangement.getOfferedProducts().isEmpty());
    }

    @Test
    public void testApplyCreditRatingScaleF204ThrowsResourceNotAvailableError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, DataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        when(applyService.fraudDecisionRetriever.getFraudDecision(any(String.class), any(String.class), any(String.class), any(ArrayList.class), any(String.class), any(ArrayList.class), any(Customer.class), any(ArrayList.class), any(RequestHeader.class))).thenReturn(null);
        RequestHeader requestHeader = testDataHelper.createOpaPcaRequestHeader("LTB");
        applyService.applyCreditRatingScale(depositArrangement, requestHeader, true, false);
        assertTrue(depositArrangement.getOfferedProducts().isEmpty());
    }

}
