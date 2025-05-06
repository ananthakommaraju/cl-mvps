package com.lloydsbanking.salsa.ppae.service.process;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationsDao;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.downstream.LoanDetailsRetriever;
import com.lloydsbanking.salsa.ppae.service.downstream.PersonalDetailsRetriever;
import com.lloydsbanking.salsa.ppae.service.downstream.PrepareFinanceServiceArrangement;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Resp;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class AwaitingReferralLRAApplicationProcessorTest {

    AwaitingReferralLRAApplicationProcessor awaitingReferralLRAApplicationProcessor;
    TestDataHelper testDataHelper;
    Q028Resp q028Resp;
    private static final String DECLINE_EMAIL_GROUP_CODE = "DECLINE_EMAILS_LRA";
    ProductArrangement productArrangement;


    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        awaitingReferralLRAApplicationProcessor = new AwaitingReferralLRAApplicationProcessor();
        awaitingReferralLRAApplicationProcessor.communicationManager = mock(CommunicationManager.class);
        awaitingReferralLRAApplicationProcessor.loanDetailsRetriever = mock(LoanDetailsRetriever.class);
        awaitingReferralLRAApplicationProcessor.personalDetailsRetriever = mock(PersonalDetailsRetriever.class);
        awaitingReferralLRAApplicationProcessor.applicationsDao = mock(ApplicationsDao.class);
        awaitingReferralLRAApplicationProcessor.updatePamService = mock(UpdatePamService.class);
        awaitingReferralLRAApplicationProcessor.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        awaitingReferralLRAApplicationProcessor.prepareFinanceServiceArrangement = mock(PrepareFinanceServiceArrangement.class);
        q028Resp = new Q028Resp();

    }


    @Test
    public void processTest() throws DatatypeConfigurationException {
        productArrangement = testDataHelper.createProductArrangement();
        RequestHeader header = testDataHelper.createPpaeRequestHeader("LTB");
        when(awaitingReferralLRAApplicationProcessor.applicationsDao.findOne(any(Long.class))).thenReturn(new Applications());
        awaitingReferralLRAApplicationProcessor.process(productArrangement, header);
        verify(awaitingReferralLRAApplicationProcessor.loanDetailsRetriever).retrieve(productArrangement, header);
    }

    @Test
    public void processTestWithQ028ResponseAccept() throws DatatypeConfigurationException {
        productArrangement = testDataHelper.createProductArrangement();
        RequestHeader header = testDataHelper.createPpaeRequestHeader("LTB");
        q028Resp = testDataHelper.createQ028Response();
        when(awaitingReferralLRAApplicationProcessor.loanDetailsRetriever.retrieve(productArrangement, header)).thenReturn(q028Resp);
        awaitingReferralLRAApplicationProcessor.process(productArrangement, header);
        verify(awaitingReferralLRAApplicationProcessor.communicationManager).callSendCommunicationService(productArrangement, "LOANS_LRA_ACCEPT_MSG", header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
    }

    @Test
    public void processTestWithQ028ResponseDecline() throws DatatypeConfigurationException {
        productArrangement = testDataHelper.createProductArrangement();
        RequestHeader header = testDataHelper.createPpaeRequestHeader("LTB");
        q028Resp = testDataHelper.createQ028Response();
        q028Resp.getApplicationDetails().setASMCreditScoreResultCd("3");
        when(awaitingReferralLRAApplicationProcessor.loanDetailsRetriever.retrieve(productArrangement, header)).thenReturn(q028Resp);
        awaitingReferralLRAApplicationProcessor.process(productArrangement, header);
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        when(awaitingReferralLRAApplicationProcessor.lookUpValueRetriever.retrieveLookUpValues(null, Brand.LLOYDS.asString(), Arrays.asList(DECLINE_EMAIL_GROUP_CODE))).thenReturn(referenceDataLookUpList);
        verify(awaitingReferralLRAApplicationProcessor.communicationManager).callSendCommunicationService(productArrangement, "LOANS_LRA_DECLINE_MSG", header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
    }

    @Test
    public void processTestWithQ028ResponseDeclineWithLookupValues() throws DatatypeConfigurationException {
        productArrangement = testDataHelper.createProductArrangement();
        RequestHeader header = testDataHelper.createPpaeRequestHeader("LTB");
        q028Resp = testDataHelper.createQ028Response();
        q028Resp.getApplicationDetails().setASMCreditScoreResultCd("3");
        when(awaitingReferralLRAApplicationProcessor.loanDetailsRetriever.retrieve(productArrangement, header)).thenReturn(q028Resp);
        awaitingReferralLRAApplicationProcessor.process(productArrangement, header);
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setLookupValueDesc("601");
        referenceDataLookUpList.add(referenceDataLookUp);
        when(awaitingReferralLRAApplicationProcessor.lookUpValueRetriever.retrieveLookUpValues(null, Brand.LLOYDS.asString(), Arrays.asList(DECLINE_EMAIL_GROUP_CODE))).thenReturn(referenceDataLookUpList);
        verify(awaitingReferralLRAApplicationProcessor.communicationManager).callSendCommunicationService(productArrangement, "LOANS_LRA_DECLINE_MSG", header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
    }

    @Test
    public void processWithB233call() throws DatatypeConfigurationException {

        productArrangement = testDataHelper.createProductArrangement();
        RequestHeader header = testDataHelper.createPpaeRequestHeader("LTB");
        q028Resp = testDataHelper.createQ028Response();
        q028Resp.getApplicationDetails().setASMCreditScoreResultCd("3");
        q028Resp.getApplicationDetails().setLoanApplnStatusCd(23);
        when(awaitingReferralLRAApplicationProcessor.loanDetailsRetriever.retrieve(productArrangement, header)).thenReturn(q028Resp);
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setLookupValueDesc("601");
        referenceDataLookUpList.add(referenceDataLookUp);
        awaitingReferralLRAApplicationProcessor.process(productArrangement, header);
        when(awaitingReferralLRAApplicationProcessor.lookUpValueRetriever.retrieveLookUpValues(null, Brand.LLOYDS.asString(), Arrays.asList(DECLINE_EMAIL_GROUP_CODE))).thenReturn(referenceDataLookUpList);
        verify(awaitingReferralLRAApplicationProcessor.prepareFinanceServiceArrangement).process(q028Resp, header);
    }
}
