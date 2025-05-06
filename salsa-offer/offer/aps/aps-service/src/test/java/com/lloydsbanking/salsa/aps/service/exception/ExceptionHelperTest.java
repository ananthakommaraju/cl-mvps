package com.lloydsbanking.salsa.aps.service.exception;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.aps.service.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.faults.DatabaseServiceError;
import lib_sim_gmo.faults.InternalServiceError;
import lib_sim_gmo.faults.ResourceNotAvailableError;
import lib_sim_gmo.messages.ResponseHeader;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionDataNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionInternalServiceErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;
@Category(UnitTest.class)
public class ExceptionHelperTest {
    ExceptionHelper exceptionHelper;

    TestDataHelper testDataHelper;

    ResponseHeader responseHeader;

    @Before
    public void setUp() {
        exceptionHelper = new ExceptionHelper();
        testDataHelper = new TestDataHelper();
        responseHeader = new RequestToResponseHeaderConverter().convert(testDataHelper.createRequestHeader("IBL"));
    }

    @Test(expected = AdministerProductSelectionInternalServiceErrorMsg.class)
    public void testSetResponseHeaderAndThrowInternalServiceError() throws AdministerProductSelectionDataNotAvailableErrorMsg, AdministerProductSelectionInternalServiceErrorMsg, AdministerProductSelectionResourceNotAvailableErrorMsg {
        InternalServiceError internalServiceError = new InternalServiceError();
        InternalServiceErrorMsg internalServiceErrorMsg = new InternalServiceErrorMsg("failure", internalServiceError);
        exceptionHelper.setResponseHeaderAndThrowException(internalServiceErrorMsg, responseHeader);
    }

    @Test(expected = AdministerProductSelectionResourceNotAvailableErrorMsg.class)
    public void testSetResponseHeaderAndThrowResourceNotAvailableError() throws AdministerProductSelectionDataNotAvailableErrorMsg, AdministerProductSelectionInternalServiceErrorMsg, AdministerProductSelectionResourceNotAvailableErrorMsg {
        ResourceNotAvailableError resourceNotAvailableError = new ResourceNotAvailableError();
        ResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg = new ResourceNotAvailableErrorMsg("failure", resourceNotAvailableError);
        exceptionHelper.setResponseHeaderAndThrowException(resourceNotAvailableErrorMsg, responseHeader);
    }

    @Test(expected = AdministerProductSelectionDataNotAvailableErrorMsg.class)
    public void testSetResponseHeaderAndThrowDataNotAvailableErrorMsg() throws AdministerProductSelectionDataNotAvailableErrorMsg, AdministerProductSelectionInternalServiceErrorMsg, AdministerProductSelectionResourceNotAvailableErrorMsg {
        DatabaseServiceError databaseServiceError = new DatabaseServiceError();
        DataNotAvailableErrorMsg dataNotAvailableErrorMsg = new DataNotAvailableErrorMsg("failure", databaseServiceError);
        exceptionHelper.setResponseHeaderAndThrowException(dataNotAvailableErrorMsg, responseHeader);
    }
}