package com.lloydsbanking.salsa.ppae.service.process;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.apacc.service.fulfil.FulfilPendingCreditCardArrangement;
import com.lloydsbanking.salsa.apapca.service.fulfil.FulfillPendingBankAccountArrangement;
import com.lloydsbanking.salsa.apasa.service.fulfil.FulfilPendingSavingsArrangement;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class AwaitingFulfilmentApplicationProcessorTest {

    AwaitingFulfilmentApplicationProcessor awaitingFulfilmentApplicationProcessor;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        awaitingFulfilmentApplicationProcessor = new AwaitingFulfilmentApplicationProcessor();
        awaitingFulfilmentApplicationProcessor.fulfilPendingCreditCardArrangement = mock(FulfilPendingCreditCardArrangement.class);
        awaitingFulfilmentApplicationProcessor.fulfilPendingSavingsArrangement = mock(FulfilPendingSavingsArrangement.class);
        awaitingFulfilmentApplicationProcessor.fulfillPendingBankAccountArrangement = mock(FulfillPendingBankAccountArrangement.class);
        awaitingFulfilmentApplicationProcessor.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        awaitingFulfilmentApplicationProcessor.appGroupRetriever = mock(AppGroupRetriever.class);
    }

    @Test
    public void processForCATest() throws DatatypeConfigurationException {
        ProductArrangement productArrangement = testDataHelper.createProductArrangement();
        productArrangement.setArrangementType("CA");
        RequestHeader header = testDataHelper.createPpaeRequestHeader("LTB");
        awaitingFulfilmentApplicationProcessor.process(productArrangement, header);
    }

    @Test
    public void processForSATest() throws DatatypeConfigurationException {
        ProductArrangement productArrangement = testDataHelper.createProductArrangement();
        productArrangement.setArrangementType("SA");
        RequestHeader header = testDataHelper.createPpaeRequestHeader("LTB");
        awaitingFulfilmentApplicationProcessor.process(productArrangement, header);
    }

    @Test
    public void processForCCTest() throws DatatypeConfigurationException {
        ProductArrangement productArrangement = testDataHelper.createProductArrangement();
        productArrangement.setArrangementType("CC");
        RequestHeader header = testDataHelper.createPpaeRequestHeader("LTB");
        awaitingFulfilmentApplicationProcessor.process(productArrangement, header);
    }

}
