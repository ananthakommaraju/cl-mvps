package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.StructuredAddress;
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
public class CR047AddressValidatorRuleTest {
    private CR047AddressValidatorRule rule;
    private EligibilityDecision testEligibility;

    @Before
    public void setUp() {
        rule = new CR047AddressValidatorRule();
    }

    @Test
    public void testEvaluateReturnsWithStructuredAddressNull() {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        Customer customer = new Customer();
        List<PostalAddress> postalAddresses = new ArrayList();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setDurationofStay("0707");
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("2");
        ;
        structuredAddress.setHouseNumber("2");
        structuredAddress.setDistrict("BALHAM");
        structuredAddress.setPostTown("LONDON");
        structuredAddress.getAddressLinePAFData().add("LINNET MEWS");
        structuredAddress.setPostCodeIn("8JE");
        structuredAddress.setPostCodeOut("SW12");
        structuredAddress.setPointSuffix("1Q");
        postalAddress.setStructuredAddress(structuredAddress);

        postalAddresses.add(postalAddress);
        customer.getPostalAddress().addAll(postalAddresses);
        ruleDataHolder.setCustomerDetails(customer);

        testEligibility = rule.evaluate(ruleDataHolder, null, "112233", "334455");

        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

    @Test
    public void testEvaluateReturnsWithUnStructuredAddressNull() {
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
        postalAddress.setUnstructuredAddress(unStructuredAddress);
        postalAddresses.add(postalAddress);
        customer.getPostalAddress().addAll(postalAddresses);
        ruleDataHolder.setCustomerDetails(customer);
        testEligibility = rule.evaluate(ruleDataHolder, null, "112233", "334455");
        assertEquals("Address Validation Failed", testEligibility.getReasonText());
        assertFalse(testEligibility.isEligible());
    }

    @Test
    public void testEvaluateReturnsTrueWhenPostalAddressIsNull() {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        Customer customer = new Customer();
        ruleDataHolder.setCustomerDetails(customer);

        testEligibility = rule.evaluate(ruleDataHolder, null, "112233", "334455");

        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }
}
