package com.lloydsbanking.salsa.offer.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.faults.DatabaseServiceError;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class EligibilityRetrieverTest {

    private EligibilityRetriever eligibilityRetriever;
    private DetermineEligibleCustomerInstructionsRequest eligibilityRequest;
    private ExceptionUtility exceptionUtility;

    @Before
    public void setUp() {
        eligibilityRetriever = new EligibilityRetriever();
        eligibilityRetriever.eligibilityServiceClient = mock(EligibilityServiceClient.class);
        eligibilityRequest = new DetermineEligibleCustomerInstructionsRequest();
        eligibilityRetriever.exceptionUtility = new ExceptionUtility();
    }

    @Test
    public void testEligibilityService() throws ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        DetermineEligibleCustomerInstructionsRequest request = new DetermineEligibleCustomerInstructionsRequest();
        request.setArrangementType("abc");
        eligibilityRetriever.callEligibilityService(request);
        verify(eligibilityRetriever.eligibilityServiceClient).determineEligibility(request);

    }
    @Test(expected = DataNotAvailableErrorMsg.class)
    public void testCallEligibilityServiceDataNotAvailableError() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {
        eligibilityRetriever.exceptionUtility = mock(ExceptionUtility.class);
        DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg errorMsg = mock(DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg.class);
        when(errorMsg.getFaultInfo()).thenReturn(new DatabaseServiceError());
        when(eligibilityRetriever.eligibilityServiceClient.determineEligibility(any(DetermineEligibleCustomerInstructionsRequest.class))).thenThrow(errorMsg);
        when(eligibilityRetriever.exceptionUtility.dataNotAvailableError(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(new DataNotAvailableErrorMsg());
        eligibilityRetriever.callEligibilityService(eligibilityRequest);
    }

    @Test(expected = InternalServiceErrorMsg.class)
    public void testCallEligibilityServiceOfferProductArrangementInternalServiceErrorMsg() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {
        when(eligibilityRetriever.eligibilityServiceClient.determineEligibility(any(DetermineEligibleCustomerInstructionsRequest.class))).thenThrow(DetermineEligibleCustomerInstructionsInternalServiceErrorMsg.class);
        eligibilityRetriever.callEligibilityService(eligibilityRequest);
    }

    @Test(expected = ResourceNotAvailableErrorMsg.class)
    public void testCallEligibilityServiceExternalBusinessError() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {
        when(eligibilityRetriever.eligibilityServiceClient.determineEligibility(any(DetermineEligibleCustomerInstructionsRequest.class))).thenThrow(DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg.class);
        eligibilityRetriever.callEligibilityService(eligibilityRequest);
    }

    @Test(expected = ResourceNotAvailableErrorMsg.class)
    public void testCallEligibilityServiceResourceNotAvailableError() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {
        when(eligibilityRetriever.eligibilityServiceClient.determineEligibility(any(DetermineEligibleCustomerInstructionsRequest.class))).thenThrow(DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg.class);
        eligibilityRetriever.callEligibilityService(eligibilityRequest);
    }

    @Test(expected = ResourceNotAvailableErrorMsg.class)
    public void testCallEligibilityServiceExternalServiceErrorError() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {
        when(eligibilityRetriever.eligibilityServiceClient.determineEligibility(any(DetermineEligibleCustomerInstructionsRequest.class))).thenThrow(DetermineEligibleCustomerInstructionsExternalServiceErrorMsg.class);
        eligibilityRetriever.callEligibilityService(eligibilityRequest);
    }
}
