package com.lloydsbanking.salsa.activate.administer.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreation;
import com.lloydstsb.schema.personal.serviceplatform.tms.v0001.TMSTaskDetailBO;
import lib_sim_bo.businessobjects.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class X741RequestFactoryTest {
    X741RequestFactory x741RequestFactory;
    ProductArrangement productArrangement;
    TestDataHelper testDataHelper;
    TaskCreation taskCreation;
    TMSTaskDetailBO lastNameTaskDetail;


    @Before
    public void setUp() {
        x741RequestFactory = new X741RequestFactory();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createApaRequestByDBEvent().getProductArrangement();
        lastNameTaskDetail = new TMSTaskDetailBO();
    }

    @Test
    public void testConvert() {
        taskCreation = x741RequestFactory.convert(productArrangement);
        assertEquals(productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getOrganisationUnitIdentifer(), taskCreation.getRequest().getTaskBO().getOriginatorOUId());
        assertEquals(Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), taskCreation.getRequest().getPartyBO().getPartyIdentifier());
        assertEquals(null, taskCreation.getRequest().getTaskBO().getTheTaskDetailDataList().getTaskDetail().get(0).getTaskDetailId());
    }

    @Test
    public void testConvertWithSomeReferrel() {
        productArrangement.getReferral().add(new Referral());
        productArrangement.getReferral().get(0).setTaskTypeNarrative("15");
        productArrangement.getReferral().get(0).setTaskTypeId(78);
        taskCreation = x741RequestFactory.convert(productArrangement);
        assertEquals(productArrangement.getReferral().get(0).getTaskTypeNarrative(), taskCreation.getRequest().getTaskBO().getTaskTypeNarrative());
        assertEquals(productArrangement.getReferral().get(0).getTaskTypeId(), taskCreation.getRequest().getTaskBO().getTaskTypeId());
    }

    @Test
    public void testConvertWithSomePrimaryInvolvedParty() {
        productArrangement.setArrangementId("89");
        productArrangement.getReferral().add(new Referral());
        productArrangement.getReferral().get(0).setTaskTypeNarrative("15");
        productArrangement.getReferral().get(0).setTaskTypeId(78);
        productArrangement.setPrimaryInvolvedParty(new Customer());
        productArrangement.getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setLastName("Mj");
        taskCreation = x741RequestFactory.convert(productArrangement);
        assertEquals(productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).getLastName(), taskCreation.getRequest().getTaskBO().getTheTaskDetailDataList().getTaskDetail().get(1).getValue());
        assertEquals(productArrangement.getArrangementId(), taskCreation.getRequest().getTaskBO().getTheTaskDetailDataList().getTaskDetail().get(0).getValue());
    }

    @Test
    public void testConvertWithArrangementTypeLRA() {
        productArrangement.setArrangementType("LRA");
        productArrangement.getConditions().add(new RuleCondition());
        productArrangement.getConditions().get(0).setName("Mg");
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreIdentifier("789");
        taskCreation = x741RequestFactory.convert(productArrangement);
        assertEquals(productArrangement.getConditions().get(0).getName(), taskCreation.getRequest().getTaskBO().getTheTaskDetailDataList().getTaskDetail().get(5).getHeading());
        assertEquals(productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreIdentifier(), taskCreation.getRequest().getTaskBO().getTheTaskDetailDataList().getTaskDetail().get(7).getValue());
    }

    @Test
    public void testConvertWithTrueLoanRefinanceIndicator() {
        productArrangement.setArrangementType("LRA");
        productArrangement.setLoanRefinanceIndicator(true);
        taskCreation = x741RequestFactory.convert(productArrangement);
        assertEquals(productArrangement.isLoanRefinanceIndicator().toString(), taskCreation.getRequest().getTaskBO().getTheTaskDetailDataList().getTaskDetail().get(8).getValue());
    }

}