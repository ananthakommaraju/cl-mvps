package com.lloydsbanking.salsa.apapca.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.downstream.fsou.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydstsb.ib.wsbridge.ou.StH071AGetSortCodeByCoordinates;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class H071RequestFactoryTest {
    H071RequestFactory requestFactory;

    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        requestFactory = new H071RequestFactory();
        requestFactory.headerRetriever=new HeaderRetriever();
        requestFactory.bapiHeaderToStHeaderConverter=new BapiHeaderToStHeaderConverter();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testConvert() {
        StH071AGetSortCodeByCoordinates request = requestFactory.convert("10", "20", testDataHelper.createApaRequestHeader());
        assertEquals("10", request.getSearchoriginLatitude());
        assertEquals("20", request.getSearchoriginLongitude());
        assertEquals("50", request.getSearchrangeInMiles());

    }

    @Test
    public void testConvertWhenLatitudeIsNull() {
        StH071AGetSortCodeByCoordinates request = requestFactory.convert(null, "20", testDataHelper.createApaRequestHeader());
        assertNull(request.getSearchoriginLatitude());
        assertEquals("20", request.getSearchoriginLongitude());
        assertNull(request.getSearchrangeInMiles());
        //assertEquals(21, request.getStlocatebranchNearBy().getBranchfunctioncodeBank());

    }

    @Test
    public void testConvertWhenLongitudeIsNull() {
        StH071AGetSortCodeByCoordinates request = requestFactory.convert("10", null, testDataHelper.createApaRequestHeader());
        assertNull(request.getSearchoriginLongitude());
        assertEquals("10", request.getSearchoriginLatitude());
        assertNull(request.getSearchrangeInMiles());
        //assertEquals(21, request.getStlocatebranchNearBy().getBranchfunctioncodeBank());

    }
}
