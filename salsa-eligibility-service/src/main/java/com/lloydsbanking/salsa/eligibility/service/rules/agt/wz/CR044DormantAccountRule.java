package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasCBSIndicator;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsExistingSortCodeAndAccountNumberPresent;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsSortCodePresent;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.AGTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import lib_sim_bo.businessobjects.Customer;
import org.springframework.beans.factory.annotation.Autowired;

public class CR044DormantAccountRule implements AGTEligibilityRule {
    @Autowired
    AppGroupRetriever appGroupRetriever;

    @Autowired
    CheckBalanceRetriever checkBalanceRetriever;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {
        String threshold = ruleDataHolder.getRuleParamValue();
        Customer customer = ruleDataHolder.getCustomerDetails();
        String sortCode = "";
        String accountNumber = "";

        if (QuestionIsExistingSortCodeAndAccountNumberPresent.pose()
            .givenAnExistingAccountNumber(customer.getExistingAccountNumber())
            .givenAnExistingSortCode(customer.getExistingSortCode())
            .ask()) {
            sortCode = customer.getExistingSortCode();
            accountNumber = customer.getExistingAccountNumber();
        }
        else if (QuestionIsSortCodePresent.pose()
            .givenAProductList(ruleDataHolder.getProductArrangements())
            .ask()){
            sortCode = ruleDataHolder.getProductArrangements().get(0).getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
            accountNumber = ruleDataHolder.getProductArrangements().get(0).getAccountNumber();
        }
        if (QuestionHasCBSIndicator.pose()
            .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
            .givenAnAccountNumber(accountNumber)
            .givenASortCode(sortCode)
            .givenAppGroupRetrieverClientInstance(appGroupRetriever)
            .givenRequestHeader(ruleDataHolder.getHeader())
            .givenAProductList(ruleDataHolder.getProductArrangements())
            .givenAValue(threshold).ask()) {
            return new EligibilityDecision(DeclineReasons.CR044_DECLINE_REASON);
        }
        return new EligibilityDecision(true);
    }
}

