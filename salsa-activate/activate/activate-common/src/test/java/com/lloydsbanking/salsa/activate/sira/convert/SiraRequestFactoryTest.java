package com.lloydsbanking.salsa.activate.sira.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationParameterValuesDao;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.synectics_solutions.sira.schemas.realtime.base.v2_0.enquiry.EnquiryBasicsType;
import com.synectics_solutions.sira.schemas.realtime.finance.v2_0.financeenquiry.FinanceEnquiryType;
import com.synectics_solutions.sira.schemas.realtime.finance.v2_0.financeenquirybasic.FinanceEnquiryBasicType;
import com.synectics_solutions.sira.schemas.realtime.variant.v2_0.datalibrary.DecisionType;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.Source;
import lib_sim_bo.businessobjects.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigDecimal;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class SiraRequestFactoryTest {
    private SiraRequestFactory siraRequestFactory;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        siraRequestFactory = new SiraRequestFactory();
        testDataHelper = new TestDataHelper();
        siraRequestFactory.dateFactory = new DateFactory();
        siraRequestFactory.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        siraRequestFactory.applicationParameterValuesDao=mock(ApplicationParameterValuesDao.class);
    }


    @Test
    public void testConvert() throws DatatypeConfigurationException {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().setNetMonthlyIncome(new CurrencyAmount());
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().setBirthDate(DatatypeFactory.newInstance().newXMLGregorianCalendarDate(1991, 01, 01, DatatypeConstants.FIELD_UNDEFINED));
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getNetMonthlyIncome().setAmount(new BigDecimal("1000"));
        request.getProductArrangement().getPrimaryInvolvedParty().getTelephoneNumber().add(new TelephoneNumber());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("1");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(customerScore);
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().setCustomerDeviceDetails(testDataHelper.createCustomerDeviceDetails());
        Source source = siraRequestFactory.convert((DepositArrangement) request.getProductArrangement(), request.getHeader().getChannelId(),"UNAUTHSALE",null,null);
        assertEquals(0, source.getSourceMessagePriority());
        EnquiryBasicsType enquiryBasicsType = ((JAXBElement<FinanceEnquiryType>) source.getSourceData().getContent().get(0)).getValue().getEnquiryBasics();
        assertEquals(DecisionType.ACCEPT, ((JAXBElement<FinanceEnquiryBasicType>) (enquiryBasicsType.getEnquiryBasic().get(0))).getValue().getDecisions().getDecision().get(1).getDecision());
    }

}
