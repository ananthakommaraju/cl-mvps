package com.lloydsbanking.salsa.opacc.service.utility;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.opacc.service.TestDataHelper;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.faults.DatabaseServiceError;
import lib_sim_gmo.faults.ExternalBusinessError;
import lib_sim_gmo.faults.ExternalServiceError;
import lib_sim_gmo.faults.InternalServiceError;
import lib_sim_gmo.faults.ResourceNotAvailableError;
import lib_sim_gmo.messages.ResponseHeader;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ExceptionHelperTest {
    ExceptionHelper exceptionHelper;

    TestDataHelper testDataHelper;

    ResponseHeader responseHeader;

    @Before
    public void setUp() {
        exceptionHelper = new ExceptionHelper();
        testDataHelper = new TestDataHelper();
        responseHeader = new RequestToResponseHeaderConverter().convert(testDataHelper.createOpaccRequestHeader("IBL"));
    }

    @Test(expected = OfferProductArrangementInternalServiceErrorMsg.class)
    public void testSetResponseHeaderAndThrowInternalServiceError() throws OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        InternalServiceError internalServiceError = new InternalServiceError();
        InternalServiceErrorMsg internalServiceErrorMsg = new InternalServiceErrorMsg("failure", internalServiceError);
        OfferException offerException = new OfferException(internalServiceErrorMsg);
        exceptionHelper.setResponseHeaderAndThrowException(offerException, responseHeader);
    }

    @Test(expected = OfferProductArrangementResourceNotAvailableErrorMsg.class)
    public void testSetResponseHeaderAndThrowResourceNotAvailableError() throws OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        ResourceNotAvailableError resourceNotAvailableError = new ResourceNotAvailableError();
        ResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg = new ResourceNotAvailableErrorMsg("failure", resourceNotAvailableError);
        OfferException offerException = new OfferException(resourceNotAvailableErrorMsg);
        exceptionHelper.setResponseHeaderAndThrowException(offerException, responseHeader);
    }

    @Test(expected = OfferProductArrangementDataNotAvailableErrorMsg.class)
    public void testSetResponseHeaderAndThrowDataNotAvailableError() throws OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        DatabaseServiceError databaseServiceError = new DatabaseServiceError();
        DataNotAvailableErrorMsg dataNotAvailableErrorMsg = new DataNotAvailableErrorMsg("failure", databaseServiceError);
        OfferException offerException = new OfferException(dataNotAvailableErrorMsg);
        exceptionHelper.setResponseHeaderAndThrowException(offerException, responseHeader);
    }

    @Test(expected = OfferProductArrangementExternalServiceErrorMsg.class)
    public void testSetResponseHeaderAndThrowExternalServiceError() throws OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        ExternalServiceError externalServiceError = new ExternalServiceError();
        ExternalServiceErrorMsg externalServiceErrorMsg = new ExternalServiceErrorMsg("failure", externalServiceError);
        OfferException offerException = new OfferException(externalServiceErrorMsg);
        exceptionHelper.setResponseHeaderAndThrowException(offerException, responseHeader);
    }

    @Test(expected = OfferProductArrangementExternalBusinessErrorMsg.class)
    public void testSetResponseHeaderAndThrowExternalBusinessError() throws OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        ExternalBusinessError externalBusinessError = new ExternalBusinessError();
        ExternalBusinessErrorMsg externalBusinessErrorMsg = new ExternalBusinessErrorMsg("failure", externalBusinessError);
        OfferException offerException = new OfferException(externalBusinessErrorMsg);
        exceptionHelper.setResponseHeaderAndThrowException(offerException, responseHeader);
    }
}
