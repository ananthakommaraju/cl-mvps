package com.lloydsbanking.salsa.apacc.service.fulfil;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.apacc.service.fulfil.downstream.AddNewProductForInvolvedParty;
import com.lloydsbanking.salsa.apacc.service.fulfil.downstream.CreateCreditCardAccountV1Retriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreateCardAndAddNewProductForInvolvedPartyTest {

    CreateCardAndAddNewProductForInvolvedParty createCardAndAddNewProductForInvolvedParty;

    ActivateProductArrangementRequest request;

    TestDataHelper testDataHelper;

    FinanceServiceArrangement financeServiceArrangement;

    ExtraConditions extraConditions;

    ApplicationDetails applicationDetails;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        request = testDataHelper.createApaRequestByDBEvent();
        createCardAndAddNewProductForInvolvedParty = new CreateCardAndAddNewProductForInvolvedParty();
        createCardAndAddNewProductForInvolvedParty.createCreditCardAccountV1Retriever = mock(CreateCreditCardAccountV1Retriever.class);
        createCardAndAddNewProductForInvolvedParty.addNewProductForInvolvedParty = mock(AddNewProductForInvolvedParty.class);
        createCardAndAddNewProductForInvolvedParty.validateFulfilPendingCreditCardArrangement = new ValidateFulfilPendingCreditCardArrangement();
        extraConditions = new ExtraConditions();
        applicationDetails = new ApplicationDetails();
        createCardAndAddNewProductForInvolvedParty.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(createCardAndAddNewProductForInvolvedParty.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");

    }

    @Test
    public void testCardCreationAndAddingNewProduct() {
        FinanceServiceArrangement financeServiceArrangementResponse = new FinanceServiceArrangement();
        when(createCardAndAddNewProductForInvolvedParty.createCreditCardAccountV1Retriever.createCreditCardAccount(financeServiceArrangement, request.getHeader(), applicationDetails)).thenReturn(testDataHelper.createF241V1Response("0"));
        createCardAndAddNewProductForInvolvedParty.cardCreationAndAddingNewProduct(request.getHeader(), financeServiceArrangement, applicationDetails, financeServiceArrangementResponse);
        assertEquals("3324", financeServiceArrangementResponse.getCreditCardNumber());
        assertEquals("0001203300013611675", financeServiceArrangementResponse.getAccountNumber());
    }

    @Test
    public void testCardCreationAndAddingNewProductWithError() {
        FinanceServiceArrangement financeServiceArrangementResponse = new FinanceServiceArrangement();
        applicationDetails.setApiFailureFlag(true);
        when(createCardAndAddNewProductForInvolvedParty.createCreditCardAccountV1Retriever.createCreditCardAccount(financeServiceArrangement, request.getHeader(), applicationDetails)).thenReturn(testDataHelper.createF241V1Response("2"));
        createCardAndAddNewProductForInvolvedParty.cardCreationAndAddingNewProduct(request.getHeader(), financeServiceArrangement, applicationDetails, financeServiceArrangementResponse);
        assertNull(financeServiceArrangementResponse.getAccountNumber());
        assertNull(financeServiceArrangementResponse.getCreditCardNumber());
    }

    @Test
    public void testCardCreationAndAddingNewProductWithNoCall() {
        FinanceServiceArrangement financeServiceArrangementResponse = new FinanceServiceArrangement();
        applicationDetails.setApiFailureFlag(true);
        when(createCardAndAddNewProductForInvolvedParty.createCreditCardAccountV1Retriever.createCreditCardAccount(financeServiceArrangement, request.getHeader(), applicationDetails)).thenReturn(null);
        createCardAndAddNewProductForInvolvedParty.cardCreationAndAddingNewProduct(request.getHeader(), financeServiceArrangement, applicationDetails,financeServiceArrangementResponse);
        assertNull(financeServiceArrangementResponse.getAccountNumber());
        assertNull(financeServiceArrangementResponse.getCreditCardNumber());
    }
}
