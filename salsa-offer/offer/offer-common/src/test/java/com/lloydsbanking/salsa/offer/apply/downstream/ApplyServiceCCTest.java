package com.lloydsbanking.salsa.offer.apply.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductFamilyTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.apply.convert.AsmResponseToProductFamilyConverter;
import com.lloydsbanking.salsa.offer.apply.convert.OfferToRpcRequestConverterForCreditCard;
import com.lloydsbanking.salsa.offer.apply.evaluate.ApplicationStatusEvaluator;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class ApplyServiceCCTest {
    ApplyServiceCC applyServiceCC;
    TestDataHelper dataHelper;


    @Before
    public void setUp() {
        applyServiceCC = new ApplyServiceCC();
        applyServiceCC.administerProductService = mock(AdministerProductService.class);
        applyServiceCC.applicationStatusEvaluator = mock(ApplicationStatusEvaluator.class);
        applyServiceCC.asmResponseToProductFamilyConverter = mock(AsmResponseToProductFamilyConverter.class);
        applyServiceCC.creditDecisionRetriever = mock(CreditDecisionRetriever.class);
        applyServiceCC.headerRetriever = new HeaderRetriever();
        applyServiceCC.offerToRpcRequestConverterForCreditCard = mock(OfferToRpcRequestConverterForCreditCard.class);
        applyServiceCC.rpcRetriever = mock(RpcRetriever.class);
        applyServiceCC.productFamilyTraceLog = mock(ProductFamilyTraceLog.class);
        applyServiceCC.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        applyServiceCC.productTraceLog = mock(ProductTraceLog.class);
        dataHelper = new TestDataHelper();

        when(applyServiceCC.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("Product Arrangement");
    }

    @Test
    public void testApplyCreditRatingScaleCC() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, DataNotAvailableErrorMsg, InternalServiceErrorMsg {
        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangementCC();
        financeServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().add(0, new CustomerScore());
        F424Resp f424Resp = new F424Resp();
        ProductFamily productFamily = new ProductFamily();
        ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
        extSysProdFamilyIdentifier.setProductFamilyIdentifier("232");
        extSysProdFamilyIdentifier.setSystemCode("00107");
        productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);
        List<ProductFamily> productFamilyList = new ArrayList<>();
        productFamilyList.add(productFamily);
        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();
        DataNotAvailableErrorMsg dataNotAvailableErrorMsg = new DataNotAvailableErrorMsg();
        when(applyServiceCC.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("Product Arrangement");
        when(applyServiceCC.creditDecisionRetriever.retrieveCreditDecision(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), (lib_sim_bo.businessobjects.Customer) anyObject(),
                anyString(), anyBoolean(),
                (List<lib_sim_bo.businessobjects.BalanceTransfer>) anyList(), (lib_sim_bo.businessobjects.CurrencyAmount) anyObject(), (lib_sim_gmo.messages.RequestHeader) anyObject())).thenReturn(f424Resp);
        when(applyServiceCC.applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC((CustomerScore) anyObject(), (F424Resp) anyObject(), (lib_sim_gmo.messages.RequestHeader) anyObject())).thenReturn(ApplicationStatus.APPROVED.getValue());
        when(applyServiceCC.asmResponseToProductFamilyConverter.creditDecisionResponseToProductFamilyConverter(f424Resp)).thenReturn(productFamilyList);
        when(applyServiceCC.offerToRpcRequestConverterForCreditCard.convertOfferToRpcRequestForCreditCard(dataHelper.createOpaccRequestHeader("LTB"), productFamilyList, financeServiceArrangement)).thenReturn(rpcRequest);
        when(applyServiceCC.productTraceLog.getProductTraceEventMessage(any(Product.class), any(String.class))).thenReturn("Product");
        when(applyServiceCC.productFamilyTraceLog.getProdFamilyListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Product Family");
        when(applyServiceCC.rpcRetriever.callRpcService(any(RetrieveProductConditionsRequest.class))).thenThrow(dataNotAvailableErrorMsg);
        when(applyServiceCC.productTraceLog.getProdListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Product List");
        try {
            applyServiceCC.applyCreditRatingScaleForCC("New", financeServiceArrangement, dataHelper.createOpaccRequestHeader("LTB"));
        } catch (OfferException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testApplyCcWhenOfferAmountNotCleared() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, OfferException, DataNotAvailableErrorMsg, InternalServiceErrorMsg {

        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangementCC();
        financeServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().add(0, new CustomerScore());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().add(new ReferralCode());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).setCode("213");
        financeServiceArrangement.getOfferedProducts().add(new Product());
        F424Resp f424Resp = new F424Resp();

        List<ProductFamily> productFamilyList = new ArrayList<>();
        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();
        RetrieveProductConditionsResponse rpcResp = new RetrieveProductConditionsResponse();
        rpcResp.getProduct().add(new Product());
        rpcResp.getProduct().get(0).setProductIdentifier("123");
        when(applyServiceCC.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("Product Arrangement");
        when(applyServiceCC.creditDecisionRetriever.retrieveCreditDecision(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), (lib_sim_bo.businessobjects.Customer) anyObject(),
                anyString(), anyBoolean(),
                (List<lib_sim_bo.businessobjects.BalanceTransfer>) anyList(), (lib_sim_bo.businessobjects.CurrencyAmount) anyObject(), (lib_sim_gmo.messages.RequestHeader) anyObject())).thenReturn(f424Resp);
        when(applyServiceCC.applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(any(CustomerScore.class), any(F424Resp.class), any(RequestHeader.class))).thenReturn("1003");
        when(applyServiceCC.asmResponseToProductFamilyConverter.creditDecisionResponseToProductFamilyConverter(any(F424Resp.class))).thenReturn(productFamilyList);
        when(applyServiceCC.offerToRpcRequestConverterForCreditCard.convertOfferToRpcRequestForCreditCard(any(RequestHeader.class), any(ArrayList.class), any(FinanceServiceArrangement.class))).thenReturn(rpcRequest);
        when(applyServiceCC.rpcRetriever.callRpcService(any(RetrieveProductConditionsRequest.class))).thenReturn(rpcResp);

        applyServiceCC.applyCreditRatingScaleForCC("New", financeServiceArrangement, dataHelper.createOpaccRequestHeader("LTB"));

        verify(applyServiceCC.administerProductService).callAdministerProductSelectionService(any(FinanceServiceArrangement.class), anyString());

    }


    @Test
    public void testApplyCcWhenAppStatusIsDecline() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, OfferException, DataNotAvailableErrorMsg, InternalServiceErrorMsg {

        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangementCC();
        financeServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().add(0, new CustomerScore());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().add(new ReferralCode());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).setCode("213");
        financeServiceArrangement.getOfferedProducts().add(new Product());
        financeServiceArrangement.getExistingProducts().add(new Product());
        F424Resp f424Resp = new F424Resp();

        List<ProductFamily> productFamilyList = new ArrayList<>();
        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();
        RetrieveProductConditionsResponse rpcResp = new RetrieveProductConditionsResponse();
        rpcResp.getProduct().add(new Product());
        rpcResp.getProduct().get(0).setProductIdentifier("123");
        when(applyServiceCC.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("Product Arrangement");
        when(applyServiceCC.creditDecisionRetriever.retrieveCreditDecision(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), (lib_sim_bo.businessobjects.Customer) anyObject(),
                anyString(), anyBoolean(),
                (List<lib_sim_bo.businessobjects.BalanceTransfer>) anyList(), (lib_sim_bo.businessobjects.CurrencyAmount) anyObject(), (lib_sim_gmo.messages.RequestHeader) anyObject())).thenReturn(f424Resp);
        when(applyServiceCC.applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(any(CustomerScore.class), any(F424Resp.class), any(RequestHeader.class))).thenReturn("1004");
        when(applyServiceCC.asmResponseToProductFamilyConverter.creditDecisionResponseToProductFamilyConverter(any(F424Resp.class))).thenReturn(productFamilyList);
        when(applyServiceCC.offerToRpcRequestConverterForCreditCard.convertOfferToRpcRequestForCreditCard(any(RequestHeader.class), any(ArrayList.class), any(FinanceServiceArrangement.class))).thenReturn(rpcRequest);
        when(applyServiceCC.rpcRetriever.callRpcService(any(RetrieveProductConditionsRequest.class))).thenReturn(rpcResp);

        applyServiceCC.applyCreditRatingScaleForCC("New", financeServiceArrangement, dataHelper.createOpaccRequestHeader("LTB"));

        assertTrue(financeServiceArrangement.getExistingProducts().isEmpty());


    }

    @Test(expected = OfferException.class)
    public void testApplyCcThrowsDataNotAvailableError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, OfferException, DataNotAvailableErrorMsg, InternalServiceErrorMsg {

        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangementCC();
        financeServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().add(0, new CustomerScore());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().add(new ReferralCode());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).setCode("213");
        financeServiceArrangement.getOfferedProducts().add(new Product());
        F424Resp f424Resp = new F424Resp();

        RetrieveProductConditionsResponse rpcResp = new RetrieveProductConditionsResponse();
        rpcResp.getProduct().add(new Product());
        rpcResp.getProduct().get(0).setProductIdentifier("123");
        when(applyServiceCC.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("Product Arrangement");
        when(applyServiceCC.creditDecisionRetriever.retrieveCreditDecision(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), (lib_sim_bo.businessobjects.Customer) anyObject(),
                anyString(), anyBoolean(),
                (List<lib_sim_bo.businessobjects.BalanceTransfer>) anyList(), (lib_sim_bo.businessobjects.CurrencyAmount) anyObject(), (lib_sim_gmo.messages.RequestHeader) anyObject())).thenReturn(f424Resp);
        when(applyServiceCC.applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(any(CustomerScore.class), any(F424Resp.class), any(RequestHeader.class))).thenThrow(DataNotAvailableErrorMsg.class);

        applyServiceCC.applyCreditRatingScaleForCC("New", financeServiceArrangement, dataHelper.createOpaccRequestHeader("LTB"));


    }

    @Test(expected = OfferException.class)
    public void testApplyCcThrowsInternalError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, OfferException, DataNotAvailableErrorMsg, InternalServiceErrorMsg {

        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangementCC();
        financeServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().add(0, new CustomerScore());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().add(new ReferralCode());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).setCode("213");
        financeServiceArrangement.getOfferedProducts().add(new Product());
        F424Resp f424Resp = new F424Resp();

        RetrieveProductConditionsResponse rpcResp = new RetrieveProductConditionsResponse();
        rpcResp.getProduct().add(new Product());
        rpcResp.getProduct().get(0).setProductIdentifier("123");
        when(applyServiceCC.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("Product Arrangement");
        when(applyServiceCC.creditDecisionRetriever.retrieveCreditDecision(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), (lib_sim_bo.businessobjects.Customer) anyObject(),
                anyString(), anyBoolean(),
                (List<lib_sim_bo.businessobjects.BalanceTransfer>) anyList(), (lib_sim_bo.businessobjects.CurrencyAmount) anyObject(), (lib_sim_gmo.messages.RequestHeader) anyObject())).thenReturn(f424Resp);
        when(applyServiceCC.applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(any(CustomerScore.class), any(F424Resp.class), any(RequestHeader.class))).thenThrow(InternalServiceErrorMsg.class);

        applyServiceCC.applyCreditRatingScaleForCC("New", financeServiceArrangement, dataHelper.createOpaccRequestHeader("LTB"));

    }

    @Test(expected = OfferException.class)
    public void testApplyCcThrowsResourceNotAvailableError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, OfferException, DataNotAvailableErrorMsg, InternalServiceErrorMsg {

        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangementCC();
        financeServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().add(0, new CustomerScore());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().add(new ReferralCode());
        financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).setCode("213");
        financeServiceArrangement.getOfferedProducts().add(new Product());
        F424Resp f424Resp = new F424Resp();

        RetrieveProductConditionsResponse rpcResp = new RetrieveProductConditionsResponse();
        rpcResp.getProduct().add(new Product());
        rpcResp.getProduct().get(0).setProductIdentifier("123");
        when(applyServiceCC.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("Product Arrangement");
        when(applyServiceCC.creditDecisionRetriever.retrieveCreditDecision(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), (lib_sim_bo.businessobjects.Customer) anyObject(),
                anyString(), anyBoolean(),
                (List<lib_sim_bo.businessobjects.BalanceTransfer>) anyList(), (lib_sim_bo.businessobjects.CurrencyAmount) anyObject(), (lib_sim_gmo.messages.RequestHeader) anyObject())).thenReturn(f424Resp);
        when(applyServiceCC.applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(any(CustomerScore.class), any(F424Resp.class), any(RequestHeader.class))).thenThrow(ResourceNotAvailableErrorMsg.class);

        applyServiceCC.applyCreditRatingScaleForCC("New", financeServiceArrangement, dataHelper.createOpaccRequestHeader("LTB"));

    }

}
