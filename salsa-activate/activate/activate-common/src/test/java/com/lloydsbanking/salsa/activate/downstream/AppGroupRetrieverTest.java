package com.lloydsbanking.salsa.activate.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.account.StError;
import com.lloydsbanking.salsa.soap.fs.account.StHeader;
import com.lloydstsb.ib.wsbridge.account.StB766ARetrieveCBSAppGroup;
import com.lloydstsb.ib.wsbridge.account.StB766BRetrieveCBSAppGroup;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.WebServiceException;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class AppGroupRetrieverTest {
    AppGroupRetriever appGroupRetriever;

    TestDataHelper testDataHelper;

    HeaderRetriever headerRetriever = new HeaderRetriever();

    BAPIHeader bapiHeader;

    ServiceRequest serviceRequest;

    StHeader stHeader;

    ContactPoint contactPoint;

    @Before
    public void setUp() throws DatatypeConfigurationException, ParseException {
        appGroupRetriever = new AppGroupRetriever();
        appGroupRetriever.accountClient = mock(AccountClient.class);
        appGroupRetriever.bapiHeaderToStHeaderConverterAccount = new BapiHeaderToStHeaderConverter();

        appGroupRetriever.headerRetriever = headerRetriever;
        testDataHelper = new TestDataHelper();
        bapiHeader = headerRetriever.getBapiInformationHeader(testDataHelper.createApaRequestHeader()).getBAPIHeader();

        serviceRequest = headerRetriever.getServiceRequest(testDataHelper.createApaRequestHeader().getLloydsHeaders());
        contactPoint = headerRetriever.getContactPoint(testDataHelper.createApaRequestHeader().getLloydsHeaders());
        stHeader = appGroupRetriever.bapiHeaderToStHeaderConverterAccount.convert(bapiHeader, serviceRequest, contactPoint.getContactPointId());

    }

    @Test
    public void testCallRetrieveCBSAppGroup() throws Exception {
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = createRequest(stHeader, "7791229");
        stB766ARetrieveCBSAppGroup.getStheader().setIpAddressCaller(serviceRequest.getFrom()+","+bapiHeader.getIpAddressCaller());
        when(appGroupRetriever.accountClient.retrieveCBSAppGroup(stB766ARetrieveCBSAppGroup)).thenReturn(new StB766BRetrieveCBSAppGroup());

        appGroupRetriever.callRetrieveCBSAppGroup(testDataHelper.createApaRequestHeader(), "7791229");
        verify(appGroupRetriever.accountClient).retrieveCBSAppGroup(stB766ARetrieveCBSAppGroup);
    }

    @Test
    public void testCreateRequest() throws Exception {
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = appGroupRetriever.createB766Request(stHeader, "7791229");
        stB766ARetrieveCBSAppGroup.getStheader().setIpAddressCaller(serviceRequest.getFrom()+","+bapiHeader.getIpAddressCaller());
        assertEquals(stHeader, stB766ARetrieveCBSAppGroup.getStheader());
        assertEquals("7791229", stB766ARetrieveCBSAppGroup.getSortcode());
    }

    @Test
    public void testCallRetrieveCBSAppGroupForException() {
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = createRequest(stHeader, "7791229");
        stB766ARetrieveCBSAppGroup.getStheader().setIpAddressCaller(serviceRequest.getFrom()+","+bapiHeader.getIpAddressCaller());
        when(appGroupRetriever.accountClient.retrieveCBSAppGroup(stB766ARetrieveCBSAppGroup)).thenThrow(WebServiceException.class);
        appGroupRetriever.callRetrieveCBSAppGroup(testDataHelper.createApaRequestHeader(), "7791229");
    }

    @Test
    public void testCbsAppGroup() {
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = createRequest(stHeader, "7791229");
        stB766ARetrieveCBSAppGroup.getStheader().setIpAddressCaller(serviceRequest.getFrom()+","+bapiHeader.getIpAddressCaller());
        StB766BRetrieveCBSAppGroup stB766BRetrieveCBSAppGroup = new StB766BRetrieveCBSAppGroup();
        stB766BRetrieveCBSAppGroup.setSterror(new StError());
        stB766BRetrieveCBSAppGroup.getSterror().setErrorno(0);
        stB766BRetrieveCBSAppGroup.getSterror().setErrormsg("error");
        when(appGroupRetriever.accountClient.retrieveCBSAppGroup(stB766ARetrieveCBSAppGroup)).thenReturn(stB766BRetrieveCBSAppGroup);
        appGroupRetriever.callRetrieveCBSAppGroup(testDataHelper.createApaRequestHeader(), "7791229");
        verify(appGroupRetriever.accountClient).retrieveCBSAppGroup(stB766ARetrieveCBSAppGroup);
    }

    private StB766ARetrieveCBSAppGroup createRequest(StHeader stHeader, String sortCode) {
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = new StB766ARetrieveCBSAppGroup();
        stB766ARetrieveCBSAppGroup.setStheader(stHeader);
        stB766ARetrieveCBSAppGroup.setSortcode(sortCode);
        return stB766ARetrieveCBSAppGroup;
    }
}

