package com.lloydsbanking.salsa.activate.registration.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.downstream.application.converter.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydstsb.ib.wsbridge.application.StB751AAppPerCCRegAuth;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigInteger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@Category(UnitTest.class)
public class B751RequestFactoryTest {
    TestDataHelper testDataHelper;
    RequestHeader requestHeader;
    ProductArrangement productArrangement;
    B751RequestFactory b751RequestFactory;
    StB751AAppPerCCRegAuth stB751AAppPerCCRegAuth;


    @Before
    public void setUp() {

        b751RequestFactory = new B751RequestFactory();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createApaRequestByDBEvent().getProductArrangement();
        requestHeader = testDataHelper.createApaRequestHeader();
        b751RequestFactory.bapiHeaderToStHeaderConverter = mock(BapiHeaderToStHeaderConverter.class);
        b751RequestFactory.headerRetriever = new HeaderRetriever();
        when(b751RequestFactory.bapiHeaderToStHeaderConverter.convertSalesUnauthHeader(any(BAPIHeader.class), any(ServiceRequest.class)
                , any(String.class), any(BigInteger.class), any(Brand.class))).thenReturn(testDataHelper.createStHeader());


    }

    @Test
    public void convertTest() {
        stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, requestHeader);
        assertTrue(stB751AAppPerCCRegAuth.isBNewToBank());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getApplicationVersion()), stB751AAppPerCCRegAuth.getAppver());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getRegistrationIdentifier()), stB751AAppPerCCRegAuth.getAppid());

    }

    @Test
    public void convertTestWithNullCustIdentifier() {
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(null);
        stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, requestHeader);
        assertNotNull(stB751AAppPerCCRegAuth);
        assertTrue(stB751AAppPerCCRegAuth.isBNewToBank());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getApplicationVersion()), stB751AAppPerCCRegAuth.getAppver());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getRegistrationIdentifier()), stB751AAppPerCCRegAuth.getAppid());

    }


    @Test
    public void convertTestWithNullCustSegment() {
        productArrangement.getPrimaryInvolvedParty().setCustomerSegment(null);
        stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, requestHeader);
        assertNotNull(stB751AAppPerCCRegAuth);
        assertFalse(stB751AAppPerCCRegAuth.isBNewToBank());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getApplicationVersion()), stB751AAppPerCCRegAuth.getAppver());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getRegistrationIdentifier()), stB751AAppPerCCRegAuth.getAppid());
    }

    @Test
    public void convertTestWithDifferentCustSegment() {
        productArrangement.getPrimaryInvolvedParty().setCustomerSegment("4");
        stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, requestHeader);
        assertNotNull(stB751AAppPerCCRegAuth);
        assertFalse(stB751AAppPerCCRegAuth.isBNewToBank());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getApplicationVersion()), stB751AAppPerCCRegAuth.getAppver());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getRegistrationIdentifier()), stB751AAppPerCCRegAuth.getAppid());
    }

    @Test
    public void convertTestWithNullRegisteredIn() {
        productArrangement.getPrimaryInvolvedParty().setIsRegisteredIn(null);
        stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, requestHeader);
        assertNull(stB751AAppPerCCRegAuth.getAppid());
        assertTrue(stB751AAppPerCCRegAuth.isBNewToBank());
        assertNull(stB751AAppPerCCRegAuth.getAppver());
    }

    @Test
    public void convertTestWithNullPrimaryInvParty() {
        productArrangement.setPrimaryInvolvedParty(null);
        stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, requestHeader);
        assertNotNull(stB751AAppPerCCRegAuth);

        assertFalse(stB751AAppPerCCRegAuth.isBNewToBank());
        assertNull(stB751AAppPerCCRegAuth.getAppver());
        assertNull(stB751AAppPerCCRegAuth.getAppid());
    }

    @Test
    public void convertTestWithNullArrangementType() {
        productArrangement.setArrangementType(null);
        stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, requestHeader);
        assertNotNull(stB751AAppPerCCRegAuth);
        assertTrue(stB751AAppPerCCRegAuth.isBNewToBank());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getApplicationVersion()), stB751AAppPerCCRegAuth.getAppver());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getRegistrationIdentifier()), stB751AAppPerCCRegAuth.getAppid());
    }

    @Test
    public void convertTestWithDifferentArrangementType() {
        productArrangement.setArrangementType("arr");
        stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, requestHeader);
        assertNotNull(stB751AAppPerCCRegAuth);
        assertTrue(stB751AAppPerCCRegAuth.isBNewToBank());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getApplicationVersion()), stB751AAppPerCCRegAuth.getAppver());
        assertEquals(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                getRegistrationIdentifier()), stB751AAppPerCCRegAuth.getAppid());
    }

    @Test
    public void convertTestWithNullArrangementTypeAndNullInvParty() {
        productArrangement.setPrimaryInvolvedParty(null);
        productArrangement.setArrangementType(null);
        stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, requestHeader);
        assertNotNull(stB751AAppPerCCRegAuth);
        assertFalse(stB751AAppPerCCRegAuth.isBNewToBank());
        assertNull(stB751AAppPerCCRegAuth.getAppver());
        assertNull(stB751AAppPerCCRegAuth.getAppid());
    }

    @Test
    public void convertTestWithSameArrangementType() {
        productArrangement.setArrangementType("CC");
        stB751AAppPerCCRegAuth = b751RequestFactory.convert(productArrangement, requestHeader);
        assertEquals("C", stB751AAppPerCCRegAuth.getStaccount().getProdtype());
    }
}
