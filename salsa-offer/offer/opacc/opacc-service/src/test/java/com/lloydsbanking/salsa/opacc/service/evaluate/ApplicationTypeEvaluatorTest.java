package com.lloydsbanking.salsa.opacc.service.evaluate;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.prd.service.AdministerProductSelectionService;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.opacc.service.TestDataHelper;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ApplicationTypeEvaluatorTest {

    private ApplicationTypeEvaluator applicationTypeEvaluator;
    private TestDataHelper dataHelper;

    @Before
    public void setUp() {
        dataHelper = new TestDataHelper();
        applicationTypeEvaluator = new ApplicationTypeEvaluator();
        applicationTypeEvaluator.headerRetriever = new HeaderRetriever();
        applicationTypeEvaluator.offerLookupDataRetriever = mock(LookupDataRetriever.class);
        applicationTypeEvaluator.administerProductSelectionService = mock(AdministerProductSelectionService.class);
        applicationTypeEvaluator.productTraceLog = mock(ProductTraceLog.class);
        Map<String, String> legalEntityMap = new HashMap<>();
        legalEntityMap.put("LTB", "LTB");
        LegalEntityMapUtility.setLegalEntityMap(legalEntityMap);
    }

    @Test
    public void testGetApplicationTypeForAuthSwitchOn() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().setApplicationType("NEW");
        List<Product> responseExistingProducts = new ArrayList<>();
        when(applicationTypeEvaluator.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        List<String> applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(request.getProductArrangement(), true, true);
        assertEquals("NEW", applicationTypeAndEligibilityCode.get(0));
        assertEquals("NEW", applicationTypeAndEligibilityCode.get(1));
    }

    @Test
    public void testGetApplicationTypeForUnAuthSwitchOnWithCreditCardsGreaterThanZeroAdminReturnsCohold() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().setApplicationType("CO_HOLD");
        List<Product> responseExistingProducts = new ArrayList<>();
        responseExistingProducts.add(new Product());
        responseExistingProducts.get(0).setProductIdentifier("3");
        responseExistingProducts.get(0).setBrandName("LTB");
        request.getProductArrangement().getExistingProducts().addAll(responseExistingProducts);
        when(applicationTypeEvaluator.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        lookUpList.add(new ReferenceDataLookUp());
        lookUpList.get(0).setGroupCode("MAN_LEGAL_ENT_CODE");
        lookUpList.get(0).setLookupText("LTB");
        lookUpList.get(0).setLookupValueDesc("LTB");
        when(applicationTypeEvaluator.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), any(List.class))).thenReturn(lookUpList);
        when(applicationTypeEvaluator.productTraceLog.getProdListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Existing credit cards product list");
        when(applicationTypeEvaluator.productTraceLog.getProductTraceEventMessage(any(Product.class), any(String.class))).thenReturn("Product");
        when(applicationTypeEvaluator.administerProductSelectionService.administerProductSelection(any(ArrayList.class), any(Product.class), any(String.class))).thenReturn("CO_HOLD");
        List<String> applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(request.getProductArrangement(), false, true);
        assertEquals("10001", applicationTypeAndEligibilityCode.get(0));
        assertEquals("CO_HOLD", applicationTypeAndEligibilityCode.get(1));
    }

    @Test
    public void testGetApplicationTypeForUnAuthSwitchOnWithCreditCardsGreaterThanZeroAdminReturnsIneligible() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().setApplicationType("CO_HOLD");
        List<Product> responseExistingProducts = new ArrayList<>();
        responseExistingProducts.add(new Product());
        responseExistingProducts.get(0).setProductIdentifier("3");
        responseExistingProducts.get(0).setBrandName("LTB");
        request.getProductArrangement().getExistingProducts().addAll(responseExistingProducts);
        when(applicationTypeEvaluator.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        lookUpList.add(new ReferenceDataLookUp());
        lookUpList.get(0).setGroupCode("MAN_LEGAL_ENT_CODE");
        lookUpList.get(0).setLookupText("LTB");
        lookUpList.get(0).setLookupValueDesc("LTB");
        when(applicationTypeEvaluator.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), any(List.class))).thenReturn(lookUpList);
        when(applicationTypeEvaluator.productTraceLog.getProdListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Existing credit cards product list");
        when(applicationTypeEvaluator.productTraceLog.getProductTraceEventMessage(any(Product.class), any(String.class))).thenReturn("Product");
        when(applicationTypeEvaluator.administerProductSelectionService.administerProductSelection(any(ArrayList.class), any(Product.class), any(String.class))).thenReturn("INELIGIBLE");
        List<String> applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(request.getProductArrangement(), false, true);
        assertEquals("INELIGIBLE", applicationTypeAndEligibilityCode.get(0));
        assertEquals("INELIGIBLE", applicationTypeAndEligibilityCode.get(1));
    }

    @Test
    public void testGetApplicationTypeForAuthSwitchOff() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().setApplicationType("NEW");
        List<Product> responseExistingProducts = new ArrayList<>();
        when(applicationTypeEvaluator.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        List<String> applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(request.getProductArrangement(), true, false);
        assertEquals("NEW", applicationTypeAndEligibilityCode.get(0));
        assertEquals("NEW", applicationTypeAndEligibilityCode.get(1));
    }

    @Test
    public void testGetApplicationTypeForUnAuthSwitchOff() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().setApplicationType("NEW");
        List<Product> responseExistingProducts = new ArrayList<>();
        when(applicationTypeEvaluator.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        List<String> applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(request.getProductArrangement(), false, false);
        assertEquals("NEW", applicationTypeAndEligibilityCode.get(0));
        assertEquals("NEW", applicationTypeAndEligibilityCode.get(1));
    }

    @Test
    public void testGetApplicationTypeForUnAuthSwitchOn() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().setApplicationType("NEW");
        List<Product> responseExistingProducts = new ArrayList<>();
        when(applicationTypeEvaluator.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        List<String> applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(request.getProductArrangement(), false, true);
        assertEquals("NEW", applicationTypeAndEligibilityCode.get(0));
        assertEquals("NEW", applicationTypeAndEligibilityCode.get(1));
    }

    @Test
    public void testApplicationType() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {

        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().setApplicationType(null);
        List<Product> responseExistingProducts = new ArrayList<>();
        responseExistingProducts.add(new Product());
        responseExistingProducts.get(0).setProductIdentifier("2");
        responseExistingProducts.get(0).setBrandName("LTB");
        request.getProductArrangement().getExistingProducts().addAll(responseExistingProducts);
        when(applicationTypeEvaluator.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        lookUpList.add(new ReferenceDataLookUp());
        lookUpList.get(0).setGroupCode("MAN_LEGAL_ENT_CODE");
        lookUpList.get(0).setLookupText("LTB");
        lookUpList.get(0).setLookupValueDesc("LTB");
        when(applicationTypeEvaluator.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), any(List.class))).thenReturn(lookUpList);
        when(applicationTypeEvaluator.productTraceLog.getProdListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Existing credit cards product list");
        when(applicationTypeEvaluator.productTraceLog.getProductTraceEventMessage(any(Product.class), any(String.class))).thenReturn("Product");
        when(applicationTypeEvaluator.administerProductSelectionService.administerProductSelection(any(ArrayList.class), any(Product.class), any(String.class))).thenReturn("CO_HOLD");
        List<String> applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(request.getProductArrangement(), false, true);
        assertEquals("10001", applicationTypeAndEligibilityCode.get(0));
        assertEquals("NEW", applicationTypeAndEligibilityCode.get(1));
    }

    @Test
    public void testApplicationTypeWhenMultiCardSwitchIsEnabled() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {

        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().setApplicationType("CO_HOLD");
        List<Product> responseExistingProducts = new ArrayList<>();
        responseExistingProducts.add(new Product());
        responseExistingProducts.get(0).setProductIdentifier("3");
        responseExistingProducts.get(0).setBrandName("LTB");

        responseExistingProducts.add(1,new Product());
        responseExistingProducts.get(1).setProductIdentifier("3");
        responseExistingProducts.get(1).setBrandName("ILB");

        request.getProductArrangement().getExistingProducts().addAll(responseExistingProducts);
        when(applicationTypeEvaluator.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        lookUpList.add(new ReferenceDataLookUp());
        lookUpList.get(0).setGroupCode("MAN_LEGAL_ENT_CODE");
        lookUpList.get(0).setLookupText("LTB");
        lookUpList.get(0).setLookupValueDesc("LTB");
        when(applicationTypeEvaluator.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), any(List.class))).thenReturn(lookUpList);
        when(applicationTypeEvaluator.productTraceLog.getProdListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Existing credit cards product list");
        when(applicationTypeEvaluator.productTraceLog.getProductTraceEventMessage(any(Product.class), any(String.class))).thenReturn("Product");
        when(applicationTypeEvaluator.administerProductSelectionService.administerProductSelection(any(ArrayList.class), any(Product.class), any(String.class))).thenReturn("CO_HOLD");
        List<String> applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(request.getProductArrangement(), true, true);
        assertEquals("10001", applicationTypeAndEligibilityCode.get(0));
        assertEquals("CO_HOLD", applicationTypeAndEligibilityCode.get(1));


    }

    @Test
    public void testApplicationTypeWhenMultiCardSwitchIsNotEnabled() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {

        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().setApplicationType("CO_HOLD");
        List<Product> responseExistingProducts = new ArrayList<>();
        responseExistingProducts.add(new Product());
        responseExistingProducts.get(0).setProductIdentifier("3");
        responseExistingProducts.get(0).setBrandName("LTB");

        responseExistingProducts.add(1,new Product());
        responseExistingProducts.get(1).setProductIdentifier("3");
        responseExistingProducts.get(1).setBrandName("ILB");

        request.getProductArrangement().getExistingProducts().addAll(responseExistingProducts);
        when(applicationTypeEvaluator.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        lookUpList.add(new ReferenceDataLookUp());
        lookUpList.get(0).setGroupCode("MAN_LEGAL_ENT_CODE");
        lookUpList.get(0).setLookupText("LTB");
        lookUpList.get(0).setLookupValueDesc("LTB");
        when(applicationTypeEvaluator.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), any(List.class))).thenReturn(lookUpList);
        when(applicationTypeEvaluator.productTraceLog.getProdListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Existing credit cards product list");
        when(applicationTypeEvaluator.productTraceLog.getProductTraceEventMessage(any(Product.class), any(String.class))).thenReturn("Product");
        when(applicationTypeEvaluator.administerProductSelectionService.administerProductSelection(any(ArrayList.class), any(Product.class), any(String.class))).thenReturn("CO_HOLD");
        List<String> applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(request.getProductArrangement(), true, false);
        assertEquals("INELIGIBLE", applicationTypeAndEligibilityCode.get(0));
        assertEquals("INELIGIBLE", applicationTypeAndEligibilityCode.get(1));
    }
}
