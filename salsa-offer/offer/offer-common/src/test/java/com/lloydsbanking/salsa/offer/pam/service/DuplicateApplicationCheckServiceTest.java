package com.lloydsbanking.salsa.offer.pam.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.RuleCondition;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class DuplicateApplicationCheckServiceTest {

    DuplicateApplicationCheckService duplicateApplicationCheckService;
    DateFactory dateFactory;
    TestDataHelper dataHelper;

    @Before
    public void setUp() throws Exception {
        duplicateApplicationCheckService = new DuplicateApplicationCheckService();
        duplicateApplicationCheckService.dateFactory = new DateFactory();
        duplicateApplicationCheckService.retrievePamService = mock(RetrievePamService.class);
        dateFactory = new DateFactory();
        dataHelper = new TestDataHelper();
    }

    @Test
    public void testCheckDuplicateApplicationForCA() throws Exception {
        List<ProductArrangement> productArrangementList = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setApplicationStatus("1002");
        productArrangement.setArrangementType("CA");
        Customer customer = new Customer();
        customer.setCidPersID("12345");
        customer.setCustomerIdentifier("1749738577");
        productArrangement.setPrimaryInvolvedParty(customer);
        Product product = new Product();
        product.setBrandName("LTB");
        productArrangement.setAssociatedProduct(product);
        productArrangementList.add(productArrangement);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(duplicateApplicationCheckService.retrievePamService.retrieveArrangementForCustomer("LTB", "1749738577")).thenReturn(productArrangementList);

        assertTrue(duplicateApplicationCheckService.checkDuplicateApplication(productArrangement,"LTB"));
        assertEquals("829001", productArrangement.getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getCode());
        assertEquals("Duplicate Application", productArrangement.getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getDescription());

    }

    @Test
    public void testCheckDuplicateApplicationForCC() throws Exception {
        List<ProductArrangement> productArrangementList = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        Customer customer = new Customer();
        customer.setCidPersID("12345");
        customer.setCustomerIdentifier("1749738577");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationStatus("1007");
        productArrangement.setArrangementType("CC");
        Product product = new Product();
        product.setBrandName("LTB");
        productArrangement.setAssociatedProduct(product);
        productArrangementList.add(productArrangement);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(duplicateApplicationCheckService.retrievePamService.retrieveArrangementForCustomer("LTB", "1749738577")).thenReturn(productArrangementList);

        assertTrue(duplicateApplicationCheckService.checkDuplicateApplication(productArrangement,"LTB"));
        assertEquals("829001", productArrangement.getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getCode());
        assertEquals("Duplicate Application", productArrangement.getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getDescription());

    }

    @Test
    public void testCheckDuplicateApplicationWithASMDeclineForCC() throws Exception {
        List<ProductArrangement> productArrangementList = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        Customer customer = new Customer();
        customer.setCidPersID("12345");
        customer.setCustomerIdentifier("1749738577");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationStatus("1004");
        productArrangement.setArrangementType("CC");
        Product product = new Product();
        product.setBrandName("LTB");
        productArrangement.setAssociatedProduct(product);
        productArrangement.setLastModifiedDate(dateFactory.getCurrentDate());
        productArrangementList.add(productArrangement);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("CREDIT_CARD_DUPLICATE_SWITCH");
        ruleCondition.setResult("1");

        productArrangement.getConditions().add(ruleCondition);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(duplicateApplicationCheckService.retrievePamService.retrieveArrangementForCustomer("LTB", "1749738577")).thenReturn(productArrangementList);

        assertTrue(duplicateApplicationCheckService.checkDuplicateApplication(productArrangement, "LTB"));
        assertEquals("829002", productArrangement.getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getCode());
        assertEquals("Duplicate Application with ASM Decline", productArrangement.getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getDescription());

    }

    @Test
    public void testCheckDuplicateApplicationWithSwitchFalse() throws Exception {
        List<ProductArrangement> productArrangementList = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        Customer customer = new Customer();
        customer.setCidPersID("12345");
        customer.setCustomerIdentifier("1749738577");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationStatus("1004");
        productArrangement.setArrangementType("CC");
        Product product = new Product();
        product.setBrandName("LTB");
        productArrangement.setAssociatedProduct(product);
        productArrangement.setLastModifiedDate(dateFactory.getCurrentDate());
        productArrangementList.add(productArrangement);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("CREDIT_CARD_DUPLICATE_SWITCH");
        ruleCondition.setResult("0");

        productArrangement.getConditions().add(ruleCondition);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(duplicateApplicationCheckService.retrievePamService.retrieveArrangementForCustomer("LTB", "1749738577")).thenReturn(productArrangementList);

        assertFalse(duplicateApplicationCheckService.checkDuplicateApplication(productArrangement, "LTB"));

    }

    @Test
    public void testCheckDuplicateApplicationCheckedIfRuleConditionNotPresent() throws Exception {
        List<ProductArrangement> productArrangementList = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        Customer customer = new Customer();
        customer.setCidPersID("12345");
        customer.setCustomerIdentifier("1749738577");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationStatus("1004");
        productArrangement.setArrangementType("CC");
        Product product = new Product();
        product.setBrandName("LTB");
        productArrangement.setAssociatedProduct(product);
        productArrangement.setLastModifiedDate(dateFactory.getCurrentDate());
        productArrangementList.add(productArrangement);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(duplicateApplicationCheckService.retrievePamService.retrieveArrangementForCustomer("LTB", "1749738577")).thenReturn(productArrangementList);

        assertTrue(duplicateApplicationCheckService.checkDuplicateApplication(productArrangement, "LTB"));

    }

    @Test
    public void testCheckDuplicateApplicationWithASMDeclineWithThirtyDays() throws Exception {
        List<ProductArrangement> productArrangementList = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        Customer customer = new Customer();
        customer.setCidPersID("12345");
        customer.setCustomerIdentifier("1749738577");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationStatus("1004");
        productArrangement.setArrangementType("CC");
        Product product = new Product();
        product.setBrandName("LTB");
        productArrangement.setAssociatedProduct(product);
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date date = calendar.getTime();
        productArrangement.setLastModifiedDate(dateFactory.dateToXMLGregorianCalendar(date));
        productArrangementList.add(productArrangement);

        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("CREDIT_CARD_DUPLICATE_SWITCH");
        ruleCondition.setResult("1");

        productArrangement.getConditions().add(ruleCondition);

        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(duplicateApplicationCheckService.retrievePamService.retrieveArrangementForCustomer("LTB", "1749738577")).thenReturn(productArrangementList);

        assertTrue(duplicateApplicationCheckService.checkDuplicateApplication(productArrangement,"LTB"));
        assertEquals("829002", productArrangement.getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getCode());
        assertEquals("Duplicate Application with ASM Decline", productArrangement.getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getDescription());

    }

    @Test
    public void testCheckDuplicateApplicationWithASMNotDeclineWithThirtyFiveDays() throws Exception {
        List<ProductArrangement> productArrangementList = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        Customer customer = new Customer();
        customer.setCidPersID("12345");
        customer.setCustomerIdentifier("1749738577");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationStatus("1004");
        productArrangement.setArrangementType("CC");
        Product product = new Product();
        product.setBrandName("LTB");
        productArrangement.setAssociatedProduct(product);
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -35);
        Date date = calendar.getTime();
        productArrangement.setLastModifiedDate(dateFactory.dateToXMLGregorianCalendar(date));
        productArrangementList.add(productArrangement);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("CREDIT_CARD_DUPLICATE_SWITCH");
        ruleCondition.setResult("1");

        productArrangement.getConditions().add(ruleCondition);
        when(duplicateApplicationCheckService.retrievePamService.retrieveArrangementForCustomer("LTB", "1749738577")).thenReturn(productArrangementList);

        assertFalse(duplicateApplicationCheckService.checkDuplicateApplication(productArrangement, "LTB"));
    }


    @Test
    public void testCheckDuplicateApplicationWithRetrieveArrangementForCustomerGivesException() throws Exception {
        List<ProductArrangement> productArrangementList = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        Customer customer = new Customer();
        customer.setCidPersID("12345");
        customer.setCustomerIdentifier("1749738577");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationStatus("1004");
        productArrangement.setArrangementType("CC");
        Product product = new Product();
        product.setBrandName("LTB");
        productArrangement.setAssociatedProduct(product);
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -35);
        Date date = calendar.getTime();
        productArrangement.setLastModifiedDate(dateFactory.dateToXMLGregorianCalendar(date));
        productArrangementList.add(productArrangement);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("CREDIT_CARD_DUPLICATE_SWITCH");
        ruleCondition.setResult("1");

        productArrangement.getConditions().add(ruleCondition);
        when(duplicateApplicationCheckService.retrievePamService.retrieveArrangementForCustomer("LTB", "1749738577")).thenThrow(DataNotAvailableErrorMsg.class);

        assertFalse(duplicateApplicationCheckService.checkDuplicateApplication(productArrangement, "LTB"));
    }

}
