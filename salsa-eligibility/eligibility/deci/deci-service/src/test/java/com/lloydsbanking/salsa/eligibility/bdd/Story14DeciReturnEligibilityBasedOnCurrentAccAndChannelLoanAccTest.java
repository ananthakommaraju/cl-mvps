package com.lloydsbanking.salsa.eligibility.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.DetermineElegibleInstructionsResponse;
import lb_gbo_sales.messages.RequestHeader;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story14DeciReturnEligibilityBasedOnCurrentAccAndChannelLoanAccTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    DetermineEligibleInstructionsExternalBusinessErrorMsg externalBusinessErrorMsg;

    String productMnemonic;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("FEPS loan is notAvailable and Current account is notAvailable and Channel loan account is notAvailable for the customer")
    public void givenFEPSLoanIsNotAvailableAndCurrentAccountIsNotAvailableAndChannelLoanAccountIsNotAvailableForTheCustomer() {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("G_LOAN", "Personal Loan", null, null, "IBH", "Personal Loan");
        mockScenarioHelper.expectGetParentInstructionCall("P_LOAN", "Personal Loan", null, "G_LOAN", "IBH", "Personal Loan");
        mockScenarioHelper.expectF336Call(header, 4, 5);
        mockScenarioHelper.expectB162Call(header, "1", "M", "P", "IBH");
        productMnemonic = "P_LOAN";

    }

    @Given("rule is $rule")
    public void givenRuleIsCR024(String rule) {
        List<RefInstructionRulesDto> ruleList = new ArrayList();
        if (rule.equals("CR024")) {
            ruleList.add(new RefInstructionRulesDto("G_LOAN", "GR001", "Customer not eligible for personal loan", "GR001", rule, "Customer holds no current account and no loan", rule, "GRP", "AGT", null, "IBH", null));
        }
        else if (rule.equals("CR025")) {
            ruleList.add(new RefInstructionRulesDto("G_ISA", "GR001", "Customer not eligible for personal loan", "GR001", rule, "Customer holds no current account but holds a loan", rule, "GRP", "AGT", null, "IBH", null));
        }
        else if (rule.equals("CR027")) {
            ruleList.add(new RefInstructionRulesDto("G_LOAN", "GR001", "Customer not eligible for product", "GR001", rule, "Customer holds current", rule, "GRP", "AGT", null, "IBH", null));
        }
        else if (rule.equals("CR031")) {
            ruleList.add(new RefInstructionRulesDto("G_CREDCARD", "GR005", "Customer  not eligible for credit card", "GR005", rule, "Customer has 2 or more credit cards", rule, "GRP", "AGT", "1", "IBH", BigDecimal.ONE));
        }

        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);

    }

    @When("the UI calls DECI for product")
    public void whenTheUICallsDECIForProduct() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg {
        request = dataHelper.createEligibilityRequest(productMnemonic, TestDataHelper.TEST_OCIS_ID, "IBH", TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, "IBH");

        mockControl.go();

        try {
            response = eligibilityClient.determineEligibleInstructions(request);
        }
        catch (DetermineEligibleInstructionsExternalBusinessErrorMsg externalBusinessErrorMsg) {
            this.externalBusinessErrorMsg = externalBusinessErrorMsg;
        }
    }

    @Given("FEPS loan is notAvailable and Current account is notAvailable and Channel loan account is available for the customer")
    public void givenFEPSLoanIsNotAvailableAndCurrentAccountIsNotAvailableAndChannelLoanAccountIsAvailableForTheCustomer() {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("G_ISA", "Personal Loan", null, null, "IBH", "Personal Loan");
        mockScenarioHelper.expectGetParentInstructionCall("P_CISA_SAV", "Personal Loan", null, "G_ISA", "IBH", "Personal Loan");
        mockScenarioHelper.expectF336Call(header, 3, 5);
        mockScenarioHelper.expectB162Call(header, "1", "L", "M", "HAL");
        productMnemonic = "P_CISA_SAV";
    }

    @Then("DECI evaluates eligibility to $eligibility")
    public void thenDECIEvaluatesEligibilityToFalse(Boolean eligibility) {
        if (eligibility) {
            assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        }
        else {
            assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        }

    }

    @Given("current account notAvailable for the customer")
    public void givenCurrentAccountNotAvailableForTheCustomer() {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("G_LOAN", "Personal Loan", null, null, "IBH", "Personal Loan");
        mockScenarioHelper.expectB162Call(header, "1", "L", "M", "IBH");
        mockScenarioHelper.expectF336Call(header, 3, 3);
        productMnemonic = "P_LOAN";

    }

    @Given("customer has more than 1 credit cards")
    public void givenCustomerHasMoreThan1CreditCards() {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("G_CREDCARD", "Credit Card", null, null, "IBH", "Credit Card");
        mockScenarioHelper.expectGetParentInstructionCall("P_CC_PLAT", "Halifax Balance Transfer Credit Card", null, "G_CREDCARD", "IBH", "halifaxbalancetransfercreditcard");
        mockScenarioHelper.expectF336Call(header, 3, 3);
        mockScenarioHelper.expectB162Call(header, "1", "M", "L", "IBH");
        productMnemonic = "P_CC_PLAT";
    }

    @Given("current account available for the customer")
    public void givenCurrentAccountAvailableForTheCustomer() {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("G_LOAN", "Personal Loan", null, null, "IBH", "Personal Loan");
        mockScenarioHelper.expectF336Call(header, 2, 3);
        mockScenarioHelper.expectB162Call(header, "1", "C", "M", "HAL");
        productMnemonic = "P_LOAN";
    }

    @Given("FEPS loan is available and Current account is available and Channel loan account is notAvailable for the customer")
    public void givenFEPSLoanIsAvailableAndCurrentAccountIsAvailableAndChannelLoanAccountIsNotAvailableForTheCustomer() {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("G_LOAN", "Personal Loan", null, null, "IBH", "Personal Loan");
        mockScenarioHelper.expectF336Call(header, 2, 3);
        mockScenarioHelper.expectB162Call(header, "1", "C", "M", "HAL");
        productMnemonic = "P_LOAN";
    }

    @Given("FEPS loan is available and Current account is available and Channel loan account is available for the customer")
    public void givenFEPSLoanIsAvailableAndCurrentAccountIsAvailableAndChannelLoanAccountIsAvailableForTheCustomer() {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("G_LOAN", "Personal Loan", null, null, "IBH", "Personal Loan");
        mockScenarioHelper.expectF336Call(header, 2, 3);
        mockScenarioHelper.expectB162Call(header, "1", "C", "L", "IBH");
        productMnemonic = "P_LOAN";
    }

    @Given("FEPS loan is available and Current account is notAvailable and Channel loan account is available for the customer")
    public void givenFEPSLoanIsAvailableAndCurrentAccountIsNotAvailableAndChannelLoanAccountIsAvailableForTheCustomer() {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("G_LOAN", "Personal Loan", null, null, "IBH", "Personal Loan");
        mockScenarioHelper.expectF336Call(header, 2, 3);
        mockScenarioHelper.expectB162Call(header, "1", "M", "L", "IBH");
        productMnemonic = "P_LOAN";
    }

    @Given("customer has less than 2 credit cards")
    public void givenCustomerHasLessThan2CreditCards() {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("G_CREDCARD", "Credit Card", null, null, "IBH", "Credit Card");
        mockScenarioHelper.expectGetParentInstructionCall("P_CC_PLAT", "Halifax Balance Transfer Credit Card", null, "G_CREDCARD", "IBH", "halifaxbalancetransfercreditcard");
        mockScenarioHelper.expectF336Call(header, 2, 3);
        mockScenarioHelper.expectB162Call(header, "1", "M", "L", "IBH");
        productMnemonic = "P_CC_PLAT";
    }

}
