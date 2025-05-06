package com.lloydsbanking.salsa.opapca.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.opapca.service.TestDataHelper;
import com.synectics_solutions.sira.schemas.realtime.base.v2_0.enquiry.EnquiryBasicsType;
import com.synectics_solutions.sira.schemas.realtime.finance.v2_0.financeenquiry.FinanceEnquiryType;
import com.synectics_solutions.sira.schemas.realtime.finance.v2_0.financeenquirybasic.FinanceEnquiryBasicType;
import com.synectics_solutions.sira.schemas.realtime.variant.v2_0.datalibrary.DecisionType;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.Source;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.TelephoneNumber;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.bind.JAXBElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class SiraRequestFactoryTest {
    private SiraRequestFactory siraRequestFactory;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        siraRequestFactory = new SiraRequestFactory();
        siraRequestFactory.dateFactory = new DateFactory();
        siraRequestFactory.lookupDataRetriever=mock(LookupDataRetriever.class);
        siraRequestFactory.headerRetriever= new HeaderRetriever();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testConvert() throws DataNotAvailableErrorMsg {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().getTelephoneNumber().add(new TelephoneNumber());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        request.getProductArrangement().setIsJointParty(true);
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("1");
        List<ReferenceDataLookUp> lookUpValues=new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp=new ReferenceDataLookUp();
        referenceDataLookUp.setGroupCode("ISO_COUNTRY_CODE");
        when(siraRequestFactory.lookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class),any(ArrayList.class))).thenReturn(lookUpValues);
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(customerScore);
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().setCustomerDeviceDetails(testDataHelper.createCustomerDeviceDetails());
        Source source = siraRequestFactory.convert((DepositArrangement) request.getProductArrangement(),request.getHeader(),new Date());
        assertEquals(0, source.getSourceMessagePriority());
        EnquiryBasicsType enquiryBasicsType = ((JAXBElement<FinanceEnquiryType>) source.getSourceData().getContent().get(0)).getValue().getEnquiryBasics();

    }

    @Test
    public void testConvertASMRefer() throws DataNotAvailableErrorMsg {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().getTelephoneNumber().add(new TelephoneNumber());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        request.getProductArrangement().setIsJointParty(false);
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("2");
        List<ReferenceDataLookUp> lookUpValues=new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp=new ReferenceDataLookUp();
        referenceDataLookUp.setGroupCode("ISO_COUNTRY_CODE");
        referenceDataLookUp.setLookupValueDesc("IND");
        when(siraRequestFactory.lookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class),any(ArrayList.class))).thenReturn(lookUpValues);
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(customerScore);
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().setCustomerDeviceDetails(testDataHelper.createCustomerDeviceDetails());
        Source source = siraRequestFactory.convert((DepositArrangement) request.getProductArrangement(),request.getHeader(),new Date());
        assertEquals(0, source.getSourceMessagePriority());
        EnquiryBasicsType enquiryBasicsType = ((JAXBElement<FinanceEnquiryType>) source.getSourceData().getContent().get(0)).getValue().getEnquiryBasics();


    }

    @Test
    public void testConvertASMDecline() throws DataNotAvailableErrorMsg {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().getTelephoneNumber().add(new TelephoneNumber());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("3");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(customerScore);
        List<ReferenceDataLookUp> lookUpValues=new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp=new ReferenceDataLookUp();
        referenceDataLookUp.setGroupCode("ISO_COUNTRY_CODE");
        referenceDataLookUp.setLookupValueDesc("IND");
        referenceDataLookUp.setLookupText("Lookup");
        when(siraRequestFactory.lookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class),any(ArrayList.class))).thenReturn(lookUpValues);
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().setCustomerDeviceDetails(testDataHelper.createCustomerDeviceDetails());
        Source source = siraRequestFactory.convert((DepositArrangement) request.getProductArrangement(),request.getHeader(),new Date());
        assertEquals(0, source.getSourceMessagePriority());
        EnquiryBasicsType enquiryBasicsType = ((JAXBElement<FinanceEnquiryType>) source.getSourceData().getContent().get(0)).getValue().getEnquiryBasics();


    }
}
