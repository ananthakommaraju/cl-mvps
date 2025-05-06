package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityPAMRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityRefDataRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.KycStatusRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR066UKResidencyCheckRuleTest {
    CR066UKResidencyCheckRule rule;

    RuleDataHolder ruleDataHolder;

    TestDataHelper dataHelper;

    com.lloydsbanking.salsa.eligibility.TestDataHelper dataHelperBZ;

    @Before
    public void setUp() {
        rule = new CR066UKResidencyCheckRule();
        ruleDataHolder = new RuleDataHolder();
        rule.refDataRetriever = mock(EligibilityRefDataRetriever.class);
        dataHelper=new TestDataHelper();
        rule.kycStatusRetriever=mock(KycStatusRetriever.class);
        dataHelperBZ=new com.lloydsbanking.salsa.eligibility.TestDataHelper();
        rule.pamRetriever = mock(EligibilityPAMRetriever.class);
    }

    @Test
    public void testEvaluateReturnsTrue() throws SalsaExternalBusinessException, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        Customer customer = dataHelper.createCustomerDetails(1992, 02, 22);
        customer.getPostalAddress().add(new PostalAddress());
        customer.getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        customer.getPostalAddress().get(0).getUnstructuredAddress().setPostCode("postCode");
        customer.setForeignAddressIndicator(false);
        ruleDataHolder.setCustomerDetails(customer);
        ruleDataHolder.setChannel("HLX");
        ruleDataHolder.setArrangementType("CA");
        ruleDataHolder.setHeader(dataHelperBZ.createEligibilityRequestHeader(dataHelperBZ.TEST_RETAIL_CHANNEL_ID, dataHelperBZ.TEST_INTERACTION_ID, dataHelperBZ.TEST_OCIS_ID, dataHelperBZ.TEST_CUSTOMER_ID, dataHelperBZ.TEST_CONTACT_POINT_ID));
        ruleDataHolder.setRuleInsMnemonic("G_INSURANCE");

        List<String> lookUpTexts = new ArrayList<>();
        lookUpTexts.add("lookupText");
        List<String> lookupValues = new ArrayList<>();
        lookupValues.add("lookUpValueDesc");

        when(rule.pamRetriever.getLookUpValues(ruleDataHolder.getHeader().getChannelId())).thenReturn(lookupValues);
        when(rule.kycStatusRetriever.getKycStatus(ruleDataHolder.getHeader(), "542107294", lookupValues, true)).thenReturn("F");
        when(rule.refDataRetriever.retrieveRestrictedPostCode("IBH")).thenReturn(lookUpTexts);

        EligibilityDecision result = rule.evaluate(ruleDataHolder, null, "111618", "542107294");

        assertTrue(result.isEligible());
        assertEquals("F", result.getKycStatus().getStatus());
    }

    @Test
    public void testEvaluateReturnsFalse() throws SalsaExternalBusinessException, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        Customer customer = new Customer();
        customer.getPostalAddress().add(new PostalAddress());
        customer.getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        customer.getPostalAddress().get(0).getUnstructuredAddress().setPostCode("postCode");
        customer.setForeignAddressIndicator(true);
        customer.setInternalUserIdentifier("34567");
        ruleDataHolder.setCustomerDetails(customer);
        ruleDataHolder.setChannel("LTB");
        ruleDataHolder.setArrangementType("CA");
        ruleDataHolder.setHeader(dataHelperBZ.createEligibilityRequestHeader(dataHelperBZ.TEST_RETAIL_CHANNEL_ID, dataHelperBZ.TEST_INTERACTION_ID, dataHelperBZ.TEST_OCIS_ID, dataHelperBZ.TEST_CUSTOMER_ID, dataHelperBZ.TEST_CONTACT_POINT_ID));
        ruleDataHolder.setRuleInsMnemonic("G_INSURANCE");

        List<String> lookUpTexts = new ArrayList<>();
        lookUpTexts.add("post");

        List<String> lookupValues = new ArrayList<>();
        lookupValues.add("lookUpValueDesc");

        when(rule.pamRetriever.getLookUpValues(ruleDataHolder.getHeader().getChannelId())).thenReturn(lookupValues);
        when(rule.kycStatusRetriever.getKycStatus(ruleDataHolder.getHeader(), "542107294", lookupValues, true)).thenReturn("F");
        when(rule.refDataRetriever.retrieveRestrictedPostCode("IBL")).thenReturn(lookUpTexts);

        EligibilityDecision result = rule.evaluate(ruleDataHolder, null, "111618", "542107294");

        assertEquals("Customer fails UK residency check", result.getReasonText());
        assertEquals("F", result.getKycStatus().getStatus());
    }

    @Test
    public void testEvaluateReturnsFalseWhenPostCodeAndLookupTextIsSame() throws SalsaExternalBusinessException, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        Customer customer = new Customer();
        customer.getPostalAddress().add(new PostalAddress());
        customer.getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        customer.getPostalAddress().get(0).getUnstructuredAddress().setPostCode("postCode");
        customer.setForeignAddressIndicator(false);
        customer.setInternalUserIdentifier("34567");
        ruleDataHolder.setCustomerDetails(customer);
        ruleDataHolder.setChannel("LTB");
        ruleDataHolder.setArrangementType("CA");
        ruleDataHolder.setHeader(dataHelperBZ.createEligibilityRequestHeader(dataHelperBZ.TEST_RETAIL_CHANNEL_ID, dataHelperBZ.TEST_INTERACTION_ID, dataHelperBZ.TEST_OCIS_ID, dataHelperBZ.TEST_CUSTOMER_ID, dataHelperBZ.TEST_CONTACT_POINT_ID));
        ruleDataHolder.setRuleInsMnemonic("G_INSURANCE");

        List<String> lookUpTexts = new ArrayList<>();
        lookUpTexts.add("po");
        List<String> lookupValues = new ArrayList<>();
        lookupValues.add("lookUpValueDesc");

        when(rule.pamRetriever.getLookUpValues(ruleDataHolder.getHeader().getChannelId())).thenReturn(lookupValues);
        when(rule.kycStatusRetriever.getKycStatus(ruleDataHolder.getHeader(), "542107294", lookupValues, true)).thenReturn("F");
        when(rule.refDataRetriever.retrieveRestrictedPostCode("IBL")).thenReturn(lookUpTexts);

        EligibilityDecision result = rule.evaluate(ruleDataHolder, null, "111618", "542107294");

        assertEquals("Customer fails UK residency check", result.getReasonText());
        assertEquals("F", result.getKycStatus().getStatus());
    }
}
