package com.lloydsbanking.salsa.aps.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.aps.service.exception.ExceptionHelper;
import com.lloydsbanking.salsa.downstream.prd.service.AdministerProductSelectionService;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import lib_sim_productsalesreferencedatamanager.messages.AdministerProductSelectionRequest;
import lib_sim_productsalesreferencedatamanager.messages.AdministerProductSelectionResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ApsServiceTest {

    ApsService apsService;
    TestDataHelper dataHelper;

    @Before
    public void setUp() throws Exception {
        apsService = new ApsService();
        dataHelper = new TestDataHelper();
        apsService.administerProductSelectionService = mock(AdministerProductSelectionService.class);
        apsService.exceptionHelper = mock(ExceptionHelper.class);
        apsService.headerConverter = new RequestToResponseHeaderConverter();
    }

    @Test
    public void testAdministerProductSelection() throws Exception {
        AdministerProductSelectionRequest request = dataHelper.createAdministerRequestWithDifferentProductTypes();
        when(apsService.administerProductSelectionService.administerProductSelection(request.getExistingProduct(), request.getAppliedProduct(), request.getApplicationTypeCode())).thenReturn("CO_HOLD");
        AdministerProductSelectionResponse response = apsService.administerProductSelection(request);
        assertEquals("CO_HOLD", response.getProductEligibilityType());
    }


}