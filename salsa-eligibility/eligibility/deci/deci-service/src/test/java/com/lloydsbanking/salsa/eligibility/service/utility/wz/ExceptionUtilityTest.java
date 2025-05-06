package com.lloydsbanking.salsa.eligibility.service.utility.wz;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.*;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ExceptionUtilityTest {

    ExceptionUtility exceptionUtilityWZ;

    private String key = "key";

    private String field = "field";

    private String entity = "entity";


    private String reasonCode = "reasonCode";

    private String reasonText = "reasonText";


    private String errorCode = "errorCode";
    private String errorText = "errorText";
    private String errorDesc = "errorDesc";

    RequestHeader header;

    TestDataHelper dataHelper;

    @Before
    public void setUp()

    {
        dataHelper = new TestDataHelper();
        exceptionUtilityWZ = new ExceptionUtility(new RequestToResponseHeaderConverter());
        header = dataHelper.createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);
    }


    @Test
    public void testExternalBusinessErrorForReasonText() {
        ReasonText reasonText1 = new ReasonText("External Business Error");
        SalsaExternalBusinessException salsaExternalBusinessException = new SalsaExternalBusinessException("Error", reasonCode, reasonText1);
        DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg errorMsg = exceptionUtilityWZ.externalBusinessError("Error", salsaExternalBusinessException, header);

        assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals("External Business Error", errorMsg.getFaultInfo().getReasonText());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());

    }

    @Test
    public void testExternalBusinessErrorForDescription() {
        Description description = new Description("External Business Description");

        SalsaExternalBusinessException salsaExternalBusinessException = new SalsaExternalBusinessException("Error", reasonCode, description);
        DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg errorMsg = exceptionUtilityWZ.externalBusinessError("ERROR", salsaExternalBusinessException, header);

        assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals("External Business Description", errorMsg.getFaultInfo().getDescription());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());

    }

    @Test
    public void testExternalBusinessError() {


        DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg errorMsg = exceptionUtilityWZ.externalBusinessError("errorCode", "errorText", "description", header);

        assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals("errorCode", errorMsg.getFaultInfo().getReasonCode());
        assertEquals("errorText", errorMsg.getFaultInfo().getReasonText());
        assertEquals("description", errorMsg.getFaultInfo().getDescription());


    }

    @Test
    public void testInternalServiceErrorForDescriptionForNotNull() {
        Description description = new Description("Error");

        SalsaInternalServiceException cause = new SalsaInternalServiceException("Internal Service Error", reasonCode, description);

        DetermineEligibleCustomerInstructionsInternalServiceErrorMsg errorMsg = exceptionUtilityWZ.internalServiceError("ERROR", cause, header);


        assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());

        assertEquals("Error", errorMsg.getFaultInfo().getDescription());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());

    }

    @Test
    public void testInternalServiceErrorForDescription() {
        Description description = new Description("Error");
        DetermineEligibleCustomerInstructionsInternalServiceErrorMsg errorMsg = exceptionUtilityWZ.internalServiceError(reasonCode, description, header);
        assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals("Error", errorMsg.getFaultInfo().getDescription());

    }

    @Test
    public void testInternalServiceErrorForReasonText() {
        ReasonText reasonText1 = new ReasonText("ERROR");
        DetermineEligibleCustomerInstructionsInternalServiceErrorMsg errorMsg = exceptionUtilityWZ.internalServiceError(reasonCode, reasonText1, header);
        assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals("ERROR", errorMsg.getFaultInfo().getReasonText());


    }

    @Test
    public void testDataNotAvailableError() {
        DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg errorMsg = exceptionUtilityWZ.dataNotAvailableError(key, field, entity, errorText, header);

        assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(key, errorMsg.getFaultInfo().getKey());
        assertEquals(field, errorMsg.getFaultInfo().getField());
        assertEquals(entity, errorMsg.getFaultInfo().getEntity());
        assertEquals(errorText, errorMsg.getFaultInfo().getDescription());

    }

    @Test
    public void testResourceNotAvailableError() {
        DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg errorMsg = exceptionUtilityWZ.resourceNotAvailableError(header, "Resource Not Available");
        assertEquals("Resource Not Available", errorMsg.getFaultInfo().getDescription());
        assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());


    }

    @Test
    public void testExternalServiceError() {
        SalsaExternalServiceException cause = new SalsaExternalServiceException("External Service Error", reasonCode, reasonText);
        DetermineEligibleCustomerInstructionsExternalServiceErrorMsg errorMsg = exceptionUtilityWZ.externalServiceError(errorCode, cause, header);


        assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(reasonCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals(reasonText, errorMsg.getFaultInfo().getReasonText());


    }

    @Test
    public void testExternalServiceErrorForReasonCodeAndReasonText() {
        DetermineEligibleCustomerInstructionsExternalServiceErrorMsg errorMsg = exceptionUtilityWZ.externalServiceError(errorCode, reasonText, header);
        assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());
        assertEquals(errorCode, errorMsg.getFaultInfo().getReasonCode());
        assertEquals(reasonText, errorMsg.getFaultInfo().getReasonText());


    }


    @Test
    public void testinternalServiceErrorForInvalidRequest()

    {
        DetermineEligibleCustomerInstructionsInternalServiceErrorMsg errorMsg = exceptionUtilityWZ.internalServiceError(errorCode, new ReasonText(errorText), header);
        {
            assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());
            assertEquals(errorCode, errorMsg.getFaultInfo().getReasonCode());
            assertEquals(errorText, errorMsg.getFaultInfo().getReasonText());


        }
    }

    @Test
    public void testExternalBusinessErrorForInvalidRequest() {
        DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg errorMsg = exceptionUtilityWZ.externalBusinessError(errorCode, errorText, errorDesc, header);
        {

            assertEquals(exceptionUtilityWZ.requestToResponseHeaderConverterWZ.convert(header), errorMsg.getFaultInfo().getResponseHeader());
            assertEquals(errorCode, errorMsg.getFaultInfo().getReasonCode());
            assertEquals(errorDesc, errorMsg.getFaultInfo().getDescription());
        }
    }
}