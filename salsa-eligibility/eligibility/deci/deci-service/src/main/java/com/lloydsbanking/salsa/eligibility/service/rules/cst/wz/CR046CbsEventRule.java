package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasCBSIndicator;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsExistingSortCodeAndAccountNumberPresent;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsSortCodePresent;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import lib_sim_bo.businessobjects.Customer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;

public class CR046CbsEventRule implements CSTEligibilityRule {
    @Autowired
    AppGroupRetriever appGroupRetriever;

    @Autowired
    CheckBalanceRetriever checkBalanceRetriever;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) throws EligibilityException {
        String threshold = ruleDataHolder.getRuleParamValue();
        Customer customer = ruleDataHolder.getCustomerDetails();
        String sortCd = "";
        String accountNumber = "";
        if (QuestionIsExistingSortCodeAndAccountNumberPresent.pose()
            .givenAnExistingAccountNumber(customer.getExistingAccountNumber())
            .givenAnExistingSortCode(customer.getExistingSortCode())
            .ask()) {
            sortCd = customer.getExistingSortCode();
            accountNumber = customer.getExistingAccountNumber();
        }
        else if (QuestionIsSortCodePresent.pose()
            .givenAProductList(ruleDataHolder.getProductArrangements())
            .ask()){
            sortCd = ruleDataHolder.getProductArrangements().get(0).getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
            accountNumber = ruleDataHolder.getProductArrangements().get(0).getAccountNumber();
        }
        if (QuestionHasCBSIndicator.pose()
            .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
            .givenAnAccountNumber(accountNumber)
            .givenASortCode(sortCd)
            .givenAppGroupRetrieverClientInstance(appGroupRetriever)
            .givenRequestHeader(ruleDataHolder.getHeader())
            .givenAProductList(ruleDataHolder.getProductArrangements())
            .givenAValue(threshold)
            .ask()) {
            return new EligibilityDecision(DeclineReasons.CR046_DECLINE_REASON);
        }
        return new EligibilityDecision(true);
    }

}
