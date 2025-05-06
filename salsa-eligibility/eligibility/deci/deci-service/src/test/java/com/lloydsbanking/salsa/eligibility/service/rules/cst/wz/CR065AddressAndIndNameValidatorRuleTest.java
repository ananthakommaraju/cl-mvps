package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class CR065AddressAndIndNameValidatorRuleTest {
    private CR065AddressAndIndNameValidatorRule rule;

    private EligibilityDecision testEligibility;

    @Before
    public void setUp() {
        rule = new CR065AddressAndIndNameValidatorRule();

    }


    @Test
    public void testEvaluateReturnsWithUnStructAddrAndProperIndividualNameNull() {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        Customer customer = new Customer();
        List<PostalAddress> postalAddresses = new ArrayList();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(false);
        postalAddress.setDurationofStay("0707");
        UnstructuredAddress unStructuredAddress = new UnstructuredAddress();
        unStructuredAddress.setAddressLine1("184");

        unStructuredAddress.setAddressLine2("184");
        unStructuredAddress.setAddressLine4("Katherine Road");
        unStructuredAddress.setAddressLine6("London");
        unStructuredAddress.setAddressLine7("UK");
        unStructuredAddress.setAddressLine8("United Kingdon");
        unStructuredAddress.setPostCode("E62PB");
        postalAddress.setUnstructuredAddress(unStructuredAddress);
        postalAddresses.add(postalAddress);
        customer.getPostalAddress().addAll(postalAddresses);

        Individual individual = new Individual();
        List<IndividualName> individualNames = new ArrayList<>();
        IndividualName individualName = new IndividualName();
        individualName.setFirstName("XYZ");
        individualName.setLastName("ABC");
        individualNames.add(individualName);
        individual.getIndividualName().addAll(individualNames);
        customer.setIsPlayedBy(individual);

        ruleDataHolder.setCustomerDetails(customer);


        testEligibility = rule.evaluate(ruleDataHolder, null, "112233", "334455");

        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

    @Test
    public void testEvaluateReturnsDeclineReason() {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        Customer customer = new Customer();
        List<PostalAddress> postalAddresses = new ArrayList();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(false);
        postalAddress.setDurationofStay("0707");
        UnstructuredAddress unStructuredAddress = new UnstructuredAddress();
        unStructuredAddress.setAddressLine1("184");

        unStructuredAddress.setAddressLine2("184");
        unStructuredAddress.setAddressLine4("Katherine Road");
        unStructuredAddress.setAddressLine6("London");
        unStructuredAddress.setAddressLine7("UK");
        unStructuredAddress.setAddressLine8("United Kingdon");
        unStructuredAddress.setPostCode("E62PB");
        postalAddress.setUnstructuredAddress(unStructuredAddress);
        postalAddresses.add(postalAddress);
        customer.getPostalAddress().addAll(postalAddresses);
        Individual individual = new Individual();
        List<IndividualName> individualNames = new ArrayList<>();
        IndividualName individualName = new IndividualName();
        individualName.setFirstName("XYZ");
        individualName.setLastName("");
        individualNames.add(individualName);
        individual.getIndividualName().addAll(individualNames);
        customer.setIsPlayedBy(individual);

        ruleDataHolder.setCustomerDetails(customer);
        testEligibility = rule.evaluate(ruleDataHolder, null, "112233", "334455");
        assertEquals("Name or Address Validation Failed", testEligibility.getReasonText());
        assertFalse(testEligibility.isEligible());
    }
}
