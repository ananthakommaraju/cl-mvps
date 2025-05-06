package com.lloydsbanking.salsa.eligibility.client;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.header.RequestHeaderBuilder;
import lb_gbo_sales.DepositArrangement;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.*;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@org.junit.experimental.categories.Category(UnitTest.class)
public class EligibilityRequestBuilderTest {
    private EligibilityRequestBuilder requestBuilder = new EligibilityRequestBuilder();

    @Test
    public void testHeader() {
        RequestHeader header = new RequestHeaderBuilder().interactionId("ENixlWiKxlmZ8kZu4jGlAs3").channelId("IBL").businessTransaction("prepareFinanceServiceArrangementProposals").build();

        DetermineElegibileInstructionsRequest request = requestBuilder.header(header).build();

        assertEquals(header, request.getHeader());
    }

    @Test
    public void testCandidateInstructions() {

        List<String> candidateInstructions = new ArrayList();
        candidateInstructions.add("P_TRAV_MON");
        DetermineElegibileInstructionsRequest request = requestBuilder.candidateInstructions(candidateInstructions).build();

        assertEquals("P_TRAV_MON", request.getCandidateInstructions().get(0));

    }

    @Test
    public void testCustomerInstructions() {
        ProductArrangement creditCardFinanceServiceArrangement = new CreditCardFinanceServiceArrangement();
        creditCardFinanceServiceArrangement.setAccountHost("F");
        creditCardFinanceServiceArrangement.setAccountType("F120300552157");
        creditCardFinanceServiceArrangement.setHasEmbeddedInsurance(false);
        creditCardFinanceServiceArrangement.setStartDate(createXMLGregorianCalendar(2013, 9, 25));
        creditCardFinanceServiceArrangement.setProductType(ProductType.CREDIT_CARD);
        List<String> relatedEvents = new ArrayList();
        relatedEvents.add("64");
        relatedEvents.add("62");
        creditCardFinanceServiceArrangement.getRelatedEvents().addAll(relatedEvents);
        ProductArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setSortCode("772519");
        depositArrangement.setAccountNumber("04840860");
        depositArrangement.setAccountHost("T");
        depositArrangement.setAccountType("T0071776000");
        depositArrangement.setStartDate(createXMLGregorianCalendar(2013, 10, 7));
        List<String> relatedEventsForDA = new ArrayList();
        relatedEventsForDA.add("37");
        relatedEventsForDA.add("55");
        relatedEventsForDA.add("30");
        relatedEventsForDA.add("251");
        relatedEventsForDA.add("333");
        depositArrangement.getRelatedEvents().addAll(relatedEventsForDA);
        depositArrangement.setLifecycleStatus(ProductArrangementLifecycleStatus.EFFECTIVE);
        depositArrangement.setProductType(ProductType.ACCOUNT);

        Individual individual = new Individual();
        individual.setBirthDate(createXMLGregorianCalendar(1989, 01, 01));
        List<ProductArrangement> lstProductArrangement = new ArrayList<ProductArrangement>();
        lstProductArrangement.add(creditCardFinanceServiceArrangement);
        lstProductArrangement.add(depositArrangement);
        DetermineElegibileInstructionsRequest request = requestBuilder.customerArrangements(lstProductArrangement).build();
        assertEquals("F", request.getCustomerArrangements().get(0).getAccountHost());
        assertEquals("F120300552157", request.getCustomerArrangements().get(0).getAccountType());
        assertTrue(request.getCustomerArrangements().get(0).getRelatedEvents().contains("62"));
        assertEquals("772519", request.getCustomerArrangements().get(1).getSortCode());
        assertEquals("04840860", request.getCustomerArrangements().get(1).getAccountNumber());
        assertEquals("T", request.getCustomerArrangements().get(1).getAccountHost());
        assertEquals("T0071776000", request.getCustomerArrangements().get(1).getAccountType());
        assertTrue(request.getCustomerArrangements().get(1).getRelatedEvents().contains("333"));
        assertEquals(ProductType.ACCOUNT, request.getCustomerArrangements().get(1).getProductType());

    }

    @Test
    public void testIndividual() {

        Individual individual = new Individual();
        individual.setBirthDate(createXMLGregorianCalendar(1989, 01, 01));
        DetermineElegibileInstructionsRequest request = requestBuilder.individual(individual).build();
        assertNotNull(request.getIndividual().getBirthDate().getYear());

    }

    private XMLGregorianCalendar createXMLGregorianCalendar(int year, int month, int day) {
        try {

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar xcal = datatypeFactory.newXMLGregorianCalendar();
            xcal.setYear(year);
            xcal.setMonth(month);
            xcal.setDay(day);
            xcal.setTime(10, 5, 15, 0);
            xcal.setTimezone(0);
            return xcal;
        }
        catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Test
    public void testSelectedArrangement() {

        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("04840860");
        arrangementIdentifier.setSortCode("772519");
        DetermineElegibileInstructionsRequest request = requestBuilder.selectedArrangement(arrangementIdentifier).build();
        assertEquals("772519",request.getSelctdArr().getSortCode());
        assertEquals("04840860",request.getSelctdArr().getAccNum());

    }
}
