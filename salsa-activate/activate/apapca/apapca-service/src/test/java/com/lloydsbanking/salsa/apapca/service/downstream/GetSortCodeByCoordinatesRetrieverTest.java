package com.lloydsbanking.salsa.apapca.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.apapca.service.convert.H071RequestFactory;
import com.lloydsbanking.salsa.downstream.fsou.client.FsOuClient;
import com.lloydsbanking.salsa.soap.fs.ou.StBranchDetail;
import com.lloydsbanking.salsa.soap.fs.ou.StError;
import com.lloydstsb.ib.wsbridge.ou.StH071AGetSortCodeByCoordinates;
import com.lloydstsb.ib.wsbridge.ou.StH071BGetSortCodeByCoordinates;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.ws.WebServiceException;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class GetSortCodeByCoordinatesRetrieverTest {
    GetSortCodeByCoordinatesRetriever sortCodeByCoordinatesRetriever;
    TestDataHelper dataHelper;

    @Before
    public void setUp() {
        sortCodeByCoordinatesRetriever = new GetSortCodeByCoordinatesRetriever();
        sortCodeByCoordinatesRetriever.ouClient = mock(FsOuClient.class);
        sortCodeByCoordinatesRetriever.requestFactory = mock(H071RequestFactory.class);
        dataHelper = new TestDataHelper();
    }

    @Test
    public void testGetSortCode() {
        StH071AGetSortCodeByCoordinates request = new StH071AGetSortCodeByCoordinates();
        StH071BGetSortCodeByCoordinates response = new StH071BGetSortCodeByCoordinates();
        response.setSterror(new StError());
        response.getSterror().setErrorno(0);
        response.getStbranchdetails().add(new StBranchDetail());
        response.getStbranchdetails().get(0).setSortcode("779129");
        when(sortCodeByCoordinatesRetriever.requestFactory.convert("10", "20", dataHelper.createApaRequestHeader())).thenReturn(request);
        when(sortCodeByCoordinatesRetriever.ouClient.getSortCodeByCoordinates(request)).thenReturn(response);
        assertEquals("779129", sortCodeByCoordinatesRetriever.getSortCode("10", "20", dataHelper.createApaRequestHeader()));
    }

    @Test
    public void testGetSortCodeWhenH071ReturnsExternalBusinessError() {
        StH071AGetSortCodeByCoordinates request = new StH071AGetSortCodeByCoordinates();
        StH071BGetSortCodeByCoordinates response = new StH071BGetSortCodeByCoordinates();
        response.setSterror(new StError());
        response.getSterror().setErrorno(1);
        response.getStbranchdetails().add(new StBranchDetail());
        response.getStbranchdetails().get(0).setSortcode("779129");
        when(sortCodeByCoordinatesRetriever.requestFactory.convert("10", "20", dataHelper.createApaRequestHeader())).thenReturn(request);
        when(sortCodeByCoordinatesRetriever.ouClient.getSortCodeByCoordinates(request)).thenReturn(response);
        assertEquals("779129", sortCodeByCoordinatesRetriever.getSortCode("10", "20", dataHelper.createApaRequestHeader()));
    }

    @Test
    public void testGetSortCodeWhenH071ThrowsWebServiceException() {
        StH071AGetSortCodeByCoordinates request = new StH071AGetSortCodeByCoordinates();
        when(sortCodeByCoordinatesRetriever.requestFactory.convert("10", "20", dataHelper.createApaRequestHeader())).thenReturn(request);
        when(sortCodeByCoordinatesRetriever.ouClient.getSortCodeByCoordinates(request)).thenThrow(WebServiceException.class);
        assertNull(sortCodeByCoordinatesRetriever.getSortCode("10", "20", dataHelper.createApaRequestHeader()));
    }

}
