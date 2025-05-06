package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story70DeciReturnsEligibilityBasedOnCreditCardStartDateTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    Date currentDate;

    DateFactory dateFactory;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
        dateFactory = new DateFactory();
        currentDate = new Date();
    }

    @Given("customer does not hold a credit card opened in last")

    public void givenCustomerDoesNotHoldACreditCardOpenedInLast() throws DatatypeConfigurationException {
        Date newDate = dateFactory.addDays(currentDate, 365);
        request = dataHelperWZ.createEligibilityRequest("G_CREDCARD", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("3");
        request.getExistingProductArrangments().get(0).setArrangementStartDate(dateFactory.dateToXMLGregorianCalendar(newDate));
    }

    @Given("rule is CR064")

    public void givenRuleIsCR064() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("G_CREDCARD", "GR005", "Customer  not eligible for credit card", "CR064", "GRP", "Customer does not hold a credit card opened in last given", "60", "AGA", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CC_CLSC", "Classic", "G_CREDCARD", "Credit Card", null, "LTB");

    }

    @When("the UI calls DECI with valid request")

    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")

    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));

    }

    @Given("customer holds a credit card opened in last")

    public void givenCustomerHoldsACreditCardOpenedInLast() throws DatatypeConfigurationException {
        request = dataHelperWZ.createEligibilityRequest("G_CREDCARD", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("3");
        request.getExistingProductArrangments().get(0).setArrangementStartDate(dateFactory.dateToXMLGregorianCalendar(currentDate));
    }

    @Then("DECI evaluates eligibility to false and returns customer holds a credit card opened in last")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerHoldsACreditCardOpenedInLast() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

}

