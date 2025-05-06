package com.lloydsbanking.salsa.activate.utility;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class ExceptionUtilityActivateTest {
    TestDataHelper testDataHelper;

    RequestHeader requestHeader;

    ExceptionUtilityActivate exceptionUtilityActivate;

    RequestToResponseHeaderConverter requestToResponseHeaderConverter;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        requestToResponseHeaderConverter = new RequestToResponseHeaderConverter();
        exceptionUtilityActivate = new ExceptionUtilityActivate();
        exceptionUtilityActivate.requestToResponseHeaderConverter = mock(RequestToResponseHeaderConverter.class);
    }

    @Test
    public void testDataNotAvailableError() {
        String reasonText = "Data Not Available Error";
        String field = "1";
        String entity = "ID";
        ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableErrorMsg = exceptionUtilityActivate.dataNotAvailableError(field, entity, reasonText, requestHeader);
        assertEquals(entity, dataNotAvailableErrorMsg.getFaultInfo().getEntity());
        assertEquals(reasonText, dataNotAvailableErrorMsg.getFaultInfo().getDescription());
    }

    @Test
    public void testInternalServiceError() {
        String reasonText = "Internal Service Error";
        String errorCode = "1";
        ActivateProductArrangementInternalSystemErrorMsg internalServiceError = exceptionUtilityActivate.internalServiceError(errorCode, reasonText, requestHeader);
        assertEquals(errorCode, internalServiceError.getFaultInfo().getReasonCode());
        assertEquals(reasonText, internalServiceError.getFaultInfo().getReasonText());
    }

    @Test
    public void testExternalBusinessError() {
        String reasonText = "External Business Error";
        String errorCode = "1";
        ActivateProductArrangementExternalBusinessErrorMsg externalBusinessError = exceptionUtilityActivate.externalBusinessError(requestHeader,reasonText,errorCode);
        assertEquals(errorCode, externalBusinessError.getFaultInfo().getReasonCode());
        assertEquals(reasonText, externalBusinessError.getFaultInfo().getReasonText());
    }

    @Test
    public void testResourceNotAvailableError() {
        String reasonText = "Resource Not Available Error";
        ActivateProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg = exceptionUtilityActivate.resourceNotAvailableError(requestHeader, reasonText);
        assertEquals(reasonText, resourceNotAvailableErrorMsg.getFaultInfo().getDescription());
    }
    @Test
    public void testExternalServiceError() {
        String reasonText = "External Service Error";
        String errorCode="1";
        ActivateProductArrangementExternalSystemErrorMsg externalServiceErrorMsg = exceptionUtilityActivate.externalServiceError(requestHeader, reasonText,errorCode);
        assertEquals(reasonText, externalServiceErrorMsg.getFaultInfo().getReasonText());
        assertEquals(errorCode, externalServiceErrorMsg.getFaultInfo().getReasonCode());

    }
}
