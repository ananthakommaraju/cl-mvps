package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class ApplicationStatusIdentifierTest {

    ApplicationStatusIdentifier applicationStatusIdentifier;
    TestDataHelper testDataHelper;
    ProductArrangement productArrangement;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        applicationStatusIdentifier = new ApplicationStatusIdentifier();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createProductArrangement();

    }

    @Test
    public void testRetrieveAppStatusEqualToAwaitingRescore() {
        productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_RESCORE.getValue());
        String appStatusToProceedForPpae = applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getApplicationType(), productArrangement.getReferral());
        assertEquals("AWAITING_RESCORE", appStatusToProceedForPpae);

    }

    @Test
    public void testRetrieveAppStatusEqualToAwaitingReferral() {
        productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_REFERRAL_PROCESSING.getValue());
        String appStatusToProceedForPpae = applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getApplicationType(), productArrangement.getReferral());
        assertEquals("AWAITING_REFERRAL", appStatusToProceedForPpae);

    }

    @Test
    public void testRetrieveAppStatusEqualToAppStatusManualIdV() {
        productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_MANUAL_ID_V.getValue());
        String appStatusToProceedForPpae = applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getApplicationType(), productArrangement.getReferral());
        assertEquals("AWAITING_MANUAL_ID_V", appStatusToProceedForPpae);

    }

    @Test
    public void testRetrieveAppStatusEqualToAbandoned() {
        productArrangement.setApplicationStatus(ApplicationStatus.ABANDONED.getValue());
        String appStatusToProceedForPpae = applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getApplicationType(), productArrangement.getReferral());
        assertEquals("ABANDONED", appStatusToProceedForPpae);

    }

    @Test
    public void testRetrieveAppStatusEqualToPostFulfilmentProcess() {
        productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_POST_FULFILMENT_PROCESS.getValue());
        String appStatusToProceedForPpae = applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getApplicationType(), productArrangement.getReferral());
        assertEquals("AWAITING_POST_FULFILMENT_PROCESS", appStatusToProceedForPpae);

    }

    @Test
    public void testRetrieveAppStatusEqualToAwaitingFulfilment() {
        productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_FULFILMENT.getValue());
        String appStatusToProceedForPpae = applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getApplicationType(), productArrangement.getReferral());
        assertEquals("AWAITING_FULFILMENT", appStatusToProceedForPpae);

    }

    @Test
    public void testRetrieveAppStatusEqualToArrangementTypeLoan() {
        productArrangement.setApplicationStatus(ArrangementType.LOAN.getValue());
        String appStatusToProceedForPpae = applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getApplicationType(), productArrangement.getReferral());
        assertNull(appStatusToProceedForPpae);
    }

    @Test
    public void testRetrieveAppStatusEqualToCcaSigned() {
        productArrangement.setApplicationStatus(ApplicationStatus.CCA_SIGNED.getValue());
        String appStatusToProceedForPpae = applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getApplicationType(), productArrangement.getReferral());
        assertNull(appStatusToProceedForPpae);
    }

    @Test
    public void testRetrieveAppStatusEqualToApprovedDeliveryDatePending() {
        productArrangement.setApplicationStatus(ApplicationStatus.ACCEPT_PND.getValue());
        String appStatusToProceedForPpae = applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getApplicationType(), productArrangement.getReferral());
        assertEquals("NEW_CAR", appStatusToProceedForPpae);

    }

}
