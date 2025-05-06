package com.lloydsbanking.salsa.eligibility.service.validator;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class RequestValidatorTest {
    DetermineEligibleInstructionsInternalServiceErrorMsg internalServiceErrorMsg;

    DetermineEligibleInstructionsExternalBusinessErrorMsg externalBusinessErrorMsg;

    TestDataHelper dataHelper = new TestDataHelper();

    RequestValidator validator = new RequestValidator(new ExceptionUtility(new RequestToResponseHeaderConverter()));

    @Test
    public void testValidateRequestWithNoCustomerArrangements() {
        DetermineElegibileInstructionsRequest request = new DetermineElegibileInstructionsRequest();
        request.setHeader(dataHelper.createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID));
        request.getCustomerArrangements().addAll(new ArrayList<ProductArrangement>());
        try {
            validator.validateRequest(request);
        } catch (DetermineEligibleInstructionsInternalServiceErrorMsg determineEligibleInstructionsInternalServiceErrorMsg) {
            assertEquals("No product arrangements supplied", determineEligibleInstructionsInternalServiceErrorMsg.getFaultInfo().getDescription());
        } catch (DetermineEligibleInstructionsExternalBusinessErrorMsg determineEligibleInstructionsExternalBusinessErrorMsg) {
            assertEquals("AccountType passed is NULL", determineEligibleInstructionsExternalBusinessErrorMsg.getFaultInfo().getDescription());
        }
    }

    @Test
    public void testValidateProperRequest() {

        DetermineElegibileInstructionsRequest request = dataHelper.createEligibilityRequest("P_TRAV_MON", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);

        try {
            validator.validateRequest(request);
        } catch (DetermineEligibleInstructionsInternalServiceErrorMsg internalServiceErrorMsg) {
            this.internalServiceErrorMsg = internalServiceErrorMsg;
        } catch (DetermineEligibleInstructionsExternalBusinessErrorMsg externalBusinessErrorMsg) {
            this.externalBusinessErrorMsg = externalBusinessErrorMsg;
        }
        assertNull(externalBusinessErrorMsg);
        assertNull(internalServiceErrorMsg);
        assertNotNull(request);
        assertNotNull(request.getCustomerArrangements());

    }

    @Test
    public void testValidateRequestWithNoAccountType() {

        DetermineElegibileInstructionsRequest request = dataHelper.createEligibilityRequest("P_TRAV_MON", dataHelper.TEST_OCIS_ID, dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_CONTACT_POINT_ID);
        request.getCustomerArrangements().get(0).setAccountType(null);

        try {
            validator.validateRequest(request);
        } catch (DetermineEligibleInstructionsInternalServiceErrorMsg determineEligibleInstructionsInternalServiceErrorMsg) {
            assertEquals("No product arrangements supplied", determineEligibleInstructionsInternalServiceErrorMsg.getFaultInfo().getDescription());
        } catch (DetermineEligibleInstructionsExternalBusinessErrorMsg determineEligibleInstructionsExternalBusinessErrorMsg) {
            assertEquals("Account Type passed is NULL", determineEligibleInstructionsExternalBusinessErrorMsg.getFaultInfo().getDescription());
            assertNotNull(request);
            assertNotNull(request.getCustomerArrangements());
        }
    }

    @Test(expected = DetermineEligibleInstructionsExternalBusinessErrorMsg.class)
    public void testValidateRequestWithEmptyAccountType() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {

        DetermineElegibileInstructionsRequest request = dataHelper.createEligibilityRequest("P_TRAV_MON", dataHelper.TEST_OCIS_ID, dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_CONTACT_POINT_ID);
        request.getCustomerArrangements().get(0).setAccountType("");

        validator.validateRequest(request);
    }
}




