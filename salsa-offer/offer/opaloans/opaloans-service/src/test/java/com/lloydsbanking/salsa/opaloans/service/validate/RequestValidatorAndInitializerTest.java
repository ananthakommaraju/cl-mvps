package com.lloydsbanking.salsa.opaloans.service.validate;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.opaloans.service.TestDataHelper;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class RequestValidatorAndInitializerTest {
    private RequestValidatorAndInitializer validatorAndInitializer;
    private TestDataHelper dataHelper;

    @Before
    public void setUp() {
        validatorAndInitializer = new RequestValidatorAndInitializer();
        validatorAndInitializer.exceptionUtility = new ExceptionUtility();
        validatorAndInitializer.headerRetriever = new HeaderRetriever();
        validatorAndInitializer.offerLookupDataRetriever = mock(LookupDataRetriever.class);
        validatorAndInitializer.responseHeaderConverter = new RequestToResponseHeaderConverter();
        validatorAndInitializer.legalEntityMapUtility = mock(LegalEntityMapUtility.class);
        dataHelper = new TestDataHelper();
    }

    @Test
    public void testValidateRequestSuccessful() throws InternalServiceErrorMsg, ExternalBusinessErrorMsg {
        validatorAndInitializer.validateRequest(dataHelper.generateOfferProductArrangementLoansRequest("LTB"));
    }

    @Test(expected = InternalServiceErrorMsg.class)
    public void testValidateRequestInternalServiceError() throws InternalServiceErrorMsg, ExternalBusinessErrorMsg {
        validatorAndInitializer.validateRequest(null);
    }

    @Test(expected = InternalServiceErrorMsg.class)
    public void testValidateRequestExternalBusinessError() throws InternalServiceErrorMsg {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementLoansRequest("LTB");
        request.getProductArrangement().setPrimaryInvolvedParty(null);
        validatorAndInitializer.validateRequest(request);
    }

    @Test
    public void testInitialiseVariables() throws DataNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest request = dataHelper.generateOfferProductArrangementLoansRequest("LTB");
        OfferProductArrangementResponse response = new OfferProductArrangementResponse();
        RequestHeader header = dataHelper.createOpaLoansRequestHeader("LTB");
        header.setChannelId(null);
        request.setHeader(header);
        when(validatorAndInitializer.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        validatorAndInitializer.initialiseVariables(request, response);
        assertEquals("LTB", request.getHeader().getChannelId());
        assertEquals("LTB", request.getProductArrangement().getAssociatedProduct().getBrandName());
    }

    @Test(expected = OfferException.class)
    public void testInitialiseVariablesDataNotAvailableErrorMsg() throws DataNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest request = new OfferProductArrangementRequest();
        OfferProductArrangementResponse response = new OfferProductArrangementResponse();
        validatorAndInitializer.headerRetriever = mock(HeaderRetriever.class);
        when(validatorAndInitializer.headerRetriever.getContactPoint(any(RequestHeader.class))).thenThrow(DataNotAvailableErrorMsg.class);
        validatorAndInitializer.initialiseVariables(request, response);
    }

    @Test(expected = InternalServiceErrorMsg.class)
    public void testValidateRequestWhenProductArrangementIsNull() throws InternalServiceErrorMsg, ExternalBusinessErrorMsg {
        OfferProductArrangementRequest request = new OfferProductArrangementRequest();
        validatorAndInitializer.validateRequest(request);
    }


}
