package com.lloydsbanking.salsa.apapca.service.fulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apapca.TestDataHelper;

import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.OrderAccessService;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ReasonCode;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class OrderAccessItemRetrieverTest {
    OrderAccessItemRetriever orderAccessItemRetriever;

    ApplicationDetails applicationDetails;

    DepositArrangement depositArrangement;

    RequestHeader requestHeader;

    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        orderAccessItemRetriever = new OrderAccessItemRetriever();
        applicationDetails = new ApplicationDetails();
        depositArrangement = testDataHelper.createDepositArrangement("123");
        orderAccessItemRetriever.orderAccessService = mock(OrderAccessService.class);
        requestHeader = testDataHelper.createApaRequestHeader();
        orderAccessItemRetriever.updateDepositArrangementConditionAndApplicationStatusHelper = mock(UpdateDepositArrangementConditionAndApplicationStatusHelper.class);

    }

    @Test
    public void testOrderAccessItem() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        when(orderAccessItemRetriever.orderAccessService.orderAccessServiceResponse("779129", null, depositArrangement.getPrimaryInvolvedParty(), requestHeader)).thenReturn("00000");
        applicationDetails = new ApplicationDetails();
        orderAccessItemRetriever.orderAccessItem(depositArrangement, requestHeader, applicationDetails);
        assertNotNull(applicationDetails);
        assertEquals("00000", depositArrangement.getAssociatedProduct().getOrderIdentifier());
    }

    @Test
    public void testOrderAccessItemException() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        when(orderAccessItemRetriever.orderAccessService.orderAccessServiceResponse("779129", null, depositArrangement.getPrimaryInvolvedParty(), requestHeader)).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        applicationDetails = new ApplicationDetails();
        orderAccessItemRetriever.orderAccessItem(depositArrangement, requestHeader, applicationDetails);
        assertNotNull(applicationDetails);

    }

    @Test
    public void testOrderAccessItemWithReasonCode() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        depositArrangement.setReasonCode(new ReasonCode());
        depositArrangement.getReasonCode().setCode("3332");
        when(orderAccessItemRetriever.orderAccessService.orderAccessServiceResponse("779129", null, depositArrangement.getPrimaryInvolvedParty(), requestHeader)).thenReturn("00000");
        assertNotNull(applicationDetails);
        assertEquals(null, depositArrangement.getAssociatedProduct().getOrderIdentifier());
    }

    @Test
    public void testOrderAccessItemWithNoCode() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        depositArrangement.setReasonCode(new ReasonCode());
        when(orderAccessItemRetriever.orderAccessService.orderAccessServiceResponse("779129", null, depositArrangement.getPrimaryInvolvedParty(), requestHeader)).thenReturn("00000");
        applicationDetails = new ApplicationDetails();
        orderAccessItemRetriever.orderAccessItem(depositArrangement, requestHeader, applicationDetails);
        assertNotNull(applicationDetails);
        assertEquals("00000", depositArrangement.getAssociatedProduct().getOrderIdentifier());
    }


}
