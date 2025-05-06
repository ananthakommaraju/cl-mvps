package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import lib_sim_bo.businessobjects.AssessmentEvidence;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CustomerSegmentCheckServiceTest {

    private CustomerSegmentCheckService customerSegmentCheckService;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;

    @Before
    public void setUp() {
        customerSegmentCheckService = new CustomerSegmentCheckService();
        customerSegmentCheckService.updateAppDetails = new UpdateDepositArrangementConditionAndApplicationStatusHelper();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        customerSegmentCheckService.createInvolvedParty = mock(CreateInvolvedParty.class);
        customerSegmentCheckService.lookUpValueRetriever = mock(LookUpValueRetriever.class);
    }

    @Test
    public void testUpdateCustomerAndGuardianId() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        ProductArrangement productArrangement = getProductArrangement();
        AssessmentEvidence assessmentEvidence = getAssessmentEvidence();
        ApplicationDetails applicationDetails = testDataHelper.createApplicationDetails();
        applicationDetails.setApiFailureFlag(false);
        List<String> groupCodes = Arrays.asList("PTY_EVIDENCE_CODE", "ADD_EVIDENCE_CODE", "ADD_PURPOSE_CODE", "PTY_PURPOSE_CODE");
        when(customerSegmentCheckService.lookUpValueRetriever.retrieveLookUpValues(requestHeader, groupCodes)).thenReturn(createLookupData());
        when(customerSegmentCheckService.createInvolvedParty.create(productArrangement.getPrimaryInvolvedParty(), productArrangement.getArrangementType(),
                assessmentEvidence, requestHeader, applicationDetails, productArrangement.getRetryCount())).thenReturn("102546");
        when(customerSegmentCheckService.createInvolvedParty.create(productArrangement.getGuardianDetails(), productArrangement.getArrangementType(),
                assessmentEvidence, requestHeader, applicationDetails, productArrangement.getRetryCount())).thenReturn("002489");
        customerSegmentCheckService.updateCustomerAndGuardianId("3", productArrangement, requestHeader, applicationDetails);
        verify(customerSegmentCheckService.lookUpValueRetriever).retrieveLookUpValues(requestHeader, groupCodes);
    }

    @Test
    public void testUpdateCustomerAndGuardianIdWithNewCustomer() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        ProductArrangement productArrangement = getProductArrangement();
        productArrangement.getPrimaryInvolvedParty().setCustomerConsentIndicator(true);
        productArrangement.getPrimaryInvolvedParty().setNewCustomerIndicator(true);
        productArrangement.setApplicationSubStatus("1019");
        customerSegmentCheckService.updateCustomerAndGuardianId("2", productArrangement, requestHeader, testDataHelper.createApplicationDetails());
        verify(customerSegmentCheckService.lookUpValueRetriever, times(0)).retrieveLookUpValues(requestHeader, new ArrayList<String>());
    }

    @Test
    public void testUpdateCustomerAndGuardianIdWithException() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        ProductArrangement productArrangement = getProductArrangement();
        productArrangement.getPrimaryInvolvedParty().setCustomerConsentIndicator(false);
        productArrangement.getPrimaryInvolvedParty().setNewCustomerIndicator(false);
        productArrangement.setApplicationSubStatus("1018");
        ApplicationDetails applicationDetails = testDataHelper.createApplicationDetails();
        applicationDetails.setApiFailureFlag(true);
        List<String> groupCodes = Arrays.asList("PTY_EVIDENCE_CODE", "ADD_EVIDENCE_CODE", "ADD_PURPOSE_CODE", "PTY_PURPOSE_CODE");
        productArrangement.getGuardianDetails().setCustomerIdentifier(null);
        productArrangement.getGuardianDetails().setNewCustomerIndicator(true);
        when(customerSegmentCheckService.createInvolvedParty.create(productArrangement.getPrimaryInvolvedParty(), productArrangement.getArrangementType(),
                getAssessmentEvidence(), requestHeader, applicationDetails, productArrangement.getRetryCount())).thenReturn("102546");
        when(customerSegmentCheckService.lookUpValueRetriever.retrieveLookUpValues(requestHeader, groupCodes)).thenThrow(ActivateProductArrangementDataNotAvailableErrorMsg.class);
        customerSegmentCheckService.updateCustomerAndGuardianId("2", productArrangement, requestHeader, applicationDetails);
        verify(customerSegmentCheckService.lookUpValueRetriever).retrieveLookUpValues(requestHeader, groupCodes);
    }

    private ProductArrangement getProductArrangement() {
        ProductArrangement productArrangement = new ProductArrangement();
        Customer involvedParty = new Customer();
        involvedParty.getCustomerScore().add(new CustomerScore());
        involvedParty.getCustomerScore().get(0).getAssessmentEvidence().add(new AssessmentEvidence());
        productArrangement.setPrimaryInvolvedParty(involvedParty);
        productArrangement.setRetryCount(2);
        productArrangement.getPrimaryInvolvedParty().setCustomerSegment("2");
        productArrangement.setArrangementType("CA");
        productArrangement.setGuardianDetails(involvedParty);
        productArrangement.getGuardianDetails().setCustomerIdentifier("1024");
        productArrangement.getGuardianDetails().setNewCustomerIndicator(false);
        return productArrangement;
    }

    private AssessmentEvidence getAssessmentEvidence() {
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        return assessmentEvidence;
    }


    private List<ReferenceDataLookUp> createLookupData() {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(new ReferenceDataLookUp("ENCRYPT_KEY_GROUP", "WZ_ESB_V1-sscert.pem", "Card Encryption Key", 513L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("PTY_EVIDENCE_CODE", "004", "Card Encryption Key", 513L, "CARD_ENCRYPT_KEY", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("ADD_EVIDENCE_CODE", "1", "Account", 1091L, "SPORI", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("ADD_PURPOSE_CODE", "2", "Purpose of Account", 1092L, "BIEXP", "LTB", 1L));
        referenceDataLookUpList.add(new ReferenceDataLookUp("PTY_PURPOSE_CODE", "3", "Purpose of Account", 1094L, "BIEXP", "LTB", 1L));
        return referenceDataLookUpList;
    }
}
