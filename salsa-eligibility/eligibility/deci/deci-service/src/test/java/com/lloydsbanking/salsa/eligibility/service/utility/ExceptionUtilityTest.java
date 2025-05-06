package com.lloydsbanking.salsa.eligibility.service.utility;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.Description;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.RequestHeader;
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

    private String errorCode = "errorCode";


    TestDataHelper dataHelper = new TestDataHelper();

    ExceptionUtility exceptionUtility = new ExceptionUtility(new RequestToResponseHeaderConverter());

    RequestHeader header;

    @Before
    public void setUp() {
        header = dataHelper.createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);
    }

    @Test
    public void testExternalBusinessError() {
        DetermineEligibleInstructionsExternalBusinessErrorMsg errorMsg = exceptionUtility.externalBusinessError(reasonCode, reasonText, null, header);

        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(reasonText, errorMsg.getFaultInfo().getReasonText());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());
    }

    @Test
    public void testInternalServiceError() {
        DetermineEligibleInstructionsInternalServiceErrorMsg errorMsg = exceptionUtility.internalServiceError(reasonCode, new ReasonText(reasonText), header);

        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(reasonText, errorMsg.getFaultInfo().getReasonText());
    }

    @Test
    public void testDataNotAvailableError() {
        DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg errorMsg = exceptionUtility.dataNotAvailableError(key, field, entity, reasonText, header);

        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(key, errorMsg.getFaultInfo().getKey());
        assertEquals(field, errorMsg.getFaultInfo().getField());
        assertEquals(entity, errorMsg.getFaultInfo().getEntity());
        assertEquals(reasonText, errorMsg.getFaultInfo().getDescription());

    }

    @Test
    public void testResourceNotAvailableErrorMsg() {

        DetermineEligibleInstructionsResourceNotAvailableErrorMsg errorMsg = exceptionUtility.resourceNotAvailableError(header, message);
        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());

        assertEquals(message, errorMsg.getFaultInfo().getDescription());

    }

    @Test
    public void testExternalServiceErrorMsg() {
        DetermineEligibleInstructionsExternalServiceErrorMsg errorMsg = exceptionUtility.externalServiceError(errorCode, reasonText, header);

        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(errorCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals(reasonText, errorMsg.getFaultInfo().getReasonText());

    }

    @Test
    public void testInternalServiceErrorMsgForInvalidRequest() {
        DetermineEligibleInstructionsInternalServiceErrorMsg errorMsg = exceptionUtility.internalServiceError(errorCode, new Description(reasonText), header);

        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(errorCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals(reasonText, errorMsg.getFaultInfo().getDescription());

    }

    @Test
    public void testExternalBusinessErrorMsgForInvalidRequest() {
        DetermineEligibleInstructionsExternalBusinessErrorMsg errorMsg = exceptionUtility.externalBusinessError(errorCode, null, reasonText, header);

        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(errorCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals(reasonText, errorMsg.getFaultInfo().getDescription());

    }

    @Test
    public void testExternalBusinessErrorWithSalsaExternalBusinessExceptionAsTheCauseWithReasonText() {

        SalsaExternalBusinessException externalBusiness = new SalsaExternalBusinessException("ExternalBusiness", reasonCode, new ReasonText(reasonText));
        DetermineEligibleInstructionsExternalBusinessErrorMsg errorMsg = exceptionUtility.externalBusinessError(message, externalBusiness, header);

        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals(reasonText, errorMsg.getFaultInfo().getReasonText());

    }

    @Test
    public void testExternalBusinessErrorWithSalsaExternalBusinessExceptionAsTheCauseWithDescription() {

        SalsaExternalBusinessException externalBusiness = new SalsaExternalBusinessException("ExternalBusiness", reasonCode, new Description(reasonText));
        DetermineEligibleInstructionsExternalBusinessErrorMsg errorMsg = exceptionUtility.externalBusinessError(message, externalBusiness, header);

        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals(reasonText, errorMsg.getFaultInfo().getDescription());

    }

    @Test
    public void testInternalServiceErrorForDescriptionNull() {
        DetermineEligibleInstructionsInternalServiceErrorMsg errorMsg = exceptionUtility.internalServiceError(reasonCode, new SalsaInternalServiceException(message, reasonCode, new ReasonText(reasonText)), header);

        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(reasonText, errorMsg.getFaultInfo().getReasonText());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals(null, errorMsg.getFaultInfo().getDescription());
    }

    @Test
    public void testInternalServiceErrorForReasonTextNull() {
        DetermineEligibleInstructionsInternalServiceErrorMsg errorMsg = exceptionUtility.internalServiceError(reasonCode, new Description(reasonText), header);

        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(null, errorMsg.getFaultInfo().getReasonText());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals(reasonText, errorMsg.getFaultInfo().getDescription());
    }

    @Test
    public void testInternalServiceErrorForNullDescription() {
        DetermineEligibleInstructionsInternalServiceErrorMsg errorMsg = exceptionUtility.internalServiceError(reasonCode, new ReasonText(reasonText), header);

        assertEquals(exceptionUtility.requestToResponseHeaderConverter.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(reasonText, errorMsg.getFaultInfo().getReasonText());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals(null, errorMsg.getFaultInfo().getDescription());
    }
}
