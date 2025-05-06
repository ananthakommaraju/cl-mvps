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
public class Story16DeciReturnsEligibilityOnBasisOfAccountOpeningDateTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    DetermineEligibleInstructionsExternalBusinessErrorMsg externalBusinessErrorMsg;

    String productMnemonic;

    int noOfDays = 0;

    String channel;

    @BeforeScenario
    public void reset() {
        response = null;
        request = null;
        productMnemonic = null;

    }


    @Given("he is applying for business $productType and rule is $rule")
    public void givenHeIsApplyingForBusinessLoanAndRuleIsCR053(String productType, String rule) {
        List<RefInstructionRulesDto> ruleList = new ArrayList();
        if (productType.equals("loan")) {
            productMnemonic = "P_BLN_RBB";
        }
        else if (productType.equals("Overdraft")) {
            productMnemonic = "P_BOD_RBB";

        }
        if (rule.equals("CR053")) {
            channel = "BBL";
            ruleList.add(new RefInstructionRulesDto("P_BOD_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", rule, "Customer does not hold a credit card opened in last given number of days", rule, "GRP", "AGA", "2", channel, BigDecimal.ONE));
            ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR022", "Customer is not eligible for Business Loan", "GR022", rule, "Customer does not hold a credit card opened in last given number of days", rule, "GRP", "AGA", "2", channel, BigDecimal.ONE));
        }
        else if (rule.equals("CR054")) {
            channel = "STS";
            ruleList.add(new RefInstructionRulesDto("P_BOD_RBB", "GR021", "Customer does not hold a loan account opened in last given number of days", "GR021", rule, "Customer does not hold a loan account opened in last given number of days", rule, "GRP", "AGA", "28", channel, BigDecimal.ONE));
            ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR022", "Customer is not eligible for Business Loan", "GR022", rule, "Customer does not hold a loan account opened in last given number of days", rule, "GRP", "AGA", "28", channel, BigDecimal.ONE));

        }
        else if (rule.equals("CR055")) {
            channel = "STV";
            ruleList.add(new RefInstructionRulesDto("P_BOD_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", rule, "Overdraft, on current account, has not started in given number of days", rule, "GRP", "AGA", "28", channel, BigDecimal.ONE));
            ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR022", "Customer is not eligible for Business Loan", "GR022", rule, "Customer does not hold a credit card opened in last given number of days", rule, "GRP", "AGA", "28", channel, BigDecimal.ONE));

        }
        mockScenarioHelper.expectRBBSlookupCall(channel);
        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);
    }

    @When("customer $hasOrNot opened $accountType in last $numOfDays days")
    public void whenCustomerHasNotOpenedCreditCardInLast2Days(String hasOrNot, String accountType, String numOfDays) {
        int noOfDays = 0;
        String arrangementType = "CURRENT";
        if (hasOrNot.equals("has")) {
            if (accountType.equals("credit card")) {
                arrangementType = "CREDIT_CARD";
            }
            else if (accountType.equals("loan account")) {
                arrangementType = "LOAN";
            }
            if (numOfDays.equals("2")) {
                noOfDays = 1;
            }
            else if (numOfDays.equals("28")) {
                noOfDays = 24;

            }

        }
        else if (hasOrNot.equals("has not")) {

            if (numOfDays.equals("2")) {
                noOfDays = 3;
            }
            else if (numOfDays.equals("28")) {
                noOfDays = 29;

            }

        }
        request = dataHelper.createBusinessEligibilityRequest(productMnemonic, arrangementType, dataHelper.TEST_OCIS_ID, channel, dataHelper.TEST_CONTACT_POINT_ID, noOfDays, 30);
    }

    @When("customer $hasOrNot opened Overdraft on current account in the last $numOfDays days")
    public void whenCustomerHasOpenedOverdraftOnCurrentAccountInTheLast28Days(String hasOrNot, String numOfDays) {

        if (hasOrNot.equals("has")) {
            if (numOfDays.equals("2")) {
                noOfDays = 1;
            }
            else if (numOfDays.equals("28")) {
                noOfDays = 24;

            }

        }
        else if (hasOrNot.equals("has not")) {

            if (numOfDays.equals("2")) {
                noOfDays = 3;
            }
            else if (numOfDays.equals("28")) {
                noOfDays = 29;

            }

        }
        request = dataHelper.createBusinessEligibilityRequest(productMnemonic, null, dataHelper.TEST_OCIS_ID, channel, dataHelper.TEST_CONTACT_POINT_ID, 40, noOfDays);

    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg {

        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, channel);

        mockControl.go();

        try {
            response = eligibilityClient.determineEligibleInstructions(request);

        }
        catch (DetermineEligibleInstructionsExternalBusinessErrorMsg externalBusinessErrorMsg) {
            this.externalBusinessErrorMsg = externalBusinessErrorMsg;
        }

    }

    @Then("DECI evaluates eligibility to $eligibility")
    public void thenDECIEvaluatesEligibilityToTrue(Boolean eligibility) {
        if (eligibility) {
            assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        }
        else {
            assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        }

    }
}
