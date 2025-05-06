package com.lloydsbanking.salsa.offer.exception;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import lib_sim_gmo.exception.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ExceptionUtilityTest {
    private String reasonCode = "reasonCode";

    private String reasonText = "reasonText";

    private String key = "key";

    private String field = "field";

    private String entity = "entity";

    private String message = "message";

    private String errorCode = "errorcode";


    TestDataHelper dataHelper;

    ExceptionUtility exceptionUtility;

    @Before
    public void setUp() {
        dataHelper = new TestDataHelper();
        exceptionUtility = new ExceptionUtility();
    }

    @Test
    public void testExternalBusinessError() {
        ExternalBusinessErrorMsg errorMsg = exceptionUtility.externalBusinessError(reasonCode, reasonText);
        assertEquals(reasonText, errorMsg.getFaultInfo().getReasonText());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());
    }

    @Test
    public void testInternalServiceError() {
        InternalServiceErrorMsg errorMsg = exceptionUtility.internalServiceError(reasonCode, reasonText);
        assertEquals(reasonText, errorMsg.getFaultInfo().getReasonText());
    }

    @Test
    public void testDataNotAvailableError() {
        DataNotAvailableErrorMsg errorMsg = exceptionUtility.dataNotAvailableError(key, field, entity, reasonText);
        assertEquals(key, errorMsg.getFaultInfo().getKey());
        assertEquals(field, errorMsg.getFaultInfo().getField());
        assertEquals(entity, errorMsg.getFaultInfo().getEntity());
        assertEquals(reasonText, errorMsg.getFaultInfo().getDescription());
    }

    @Test
    public void testResourceNotAvailableErrorMsg() {
        ResourceNotAvailableErrorMsg errorMsg = exceptionUtility.resourceNotAvailableError(message);
        assertEquals(message, errorMsg.getFaultInfo().getDescription());
    }

    @Test
    public void testExternalServiceErrorMsg() {
        ExternalServiceErrorMsg errorMsg = exceptionUtility.externalServiceError(errorCode, reasonText);
        assertEquals(errorCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals(reasonText, errorMsg.getFaultInfo().getReasonText());
    }
}
