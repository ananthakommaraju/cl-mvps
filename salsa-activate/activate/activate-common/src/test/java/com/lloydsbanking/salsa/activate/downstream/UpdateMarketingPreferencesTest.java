package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.converter.F060RequestFactory;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.downstream.ocis.client.f060.F060Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Req;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class UpdateMarketingPreferencesTest {
    private UpdateMarketingPreferences updateMarketingPreferences;
    private TestDataHelper dataHelper;
    private ProductArrangement productArrangement;

    @Before
    public void setUp() {
        updateMarketingPreferences = new UpdateMarketingPreferences();
        dataHelper = new TestDataHelper();
        productArrangement = dataHelper.createDepositArrangement("9052");
        updateMarketingPreferences.f060RequestFactory = mock(F060RequestFactory.class);
        updateMarketingPreferences.f060Client = mock(F060Client.class);
        updateMarketingPreferences.headerRetriever = new HeaderRetriever();
        updateMarketingPreferences.updateAppDetails = new UpdateDepositArrangementConditionAndApplicationStatusHelper();
    }

    @Test
    public void testMarketingPreferencesUpdate() {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(updateMarketingPreferences.f060RequestFactory.convert(productArrangement)).thenReturn(dataHelper.createF060Request());
        when(updateMarketingPreferences.f060Client.f060(any(F060Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(new F060Resp());
        updateMarketingPreferences.marketingPreferencesUpdate(dataHelper.createApaRequestHeader(), productArrangement, applicationDetails);
        assertNull(applicationDetails.getApplicationSubStatus());
        verify(updateMarketingPreferences.f060Client).f060(any(F060Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }

    @Test
    public void testMarketingPreferencesUpdateWithException() {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(updateMarketingPreferences.f060RequestFactory.convert(productArrangement)).thenReturn(dataHelper.createF060Request());
        when(updateMarketingPreferences.f060Client.f060(any(F060Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(WebServiceException.class);
        updateMarketingPreferences.marketingPreferencesUpdate(dataHelper.createApaRequestHeader(), productArrangement, applicationDetails);
        assertEquals("1009", applicationDetails.getApplicationStatus());
        assertEquals("1029", applicationDetails.getApplicationSubStatus());
        verify(updateMarketingPreferences.f060Client).f060(any(F060Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }

}
