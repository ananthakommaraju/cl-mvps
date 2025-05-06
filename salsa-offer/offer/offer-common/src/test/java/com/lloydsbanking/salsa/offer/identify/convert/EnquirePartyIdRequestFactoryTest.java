package com.lloydsbanking.salsa.offer.identify.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Req;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_bo.businessobjects.Individual;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertEquals;

@Category(UnitTest.class)
public class EnquirePartyIdRequestFactoryTest {

    private EnquirePartyIdRequestFactory enquirePartyIdRequestFactory;
    private TestDataHelper testDataHelper;
    private ExceptionUtility exceptionUtility;

    @Before
    public void setUp() {
        enquirePartyIdRequestFactory = new EnquirePartyIdRequestFactory();
        testDataHelper = new TestDataHelper();
        exceptionUtility = new ExceptionUtility();
    }

    @Test
    public void testConvert() {
        Individual isPlayedBy = testDataHelper.createIsPlayedBy();

        try {
            F447Req f447Req = enquirePartyIdRequestFactory.convert(testDataHelper.createPostalAddressList(), isPlayedBy, exceptionUtility);

            assertEquals("lastname", f447Req.getSurname());
            assertEquals("19300303", f447Req.getBirthDt());
            assertEquals("M", f447Req.getGenderCd());
            assertEquals("f", f447Req.getFirstIt());
            assertEquals("m", f447Req.getSecondIt());
            assertEquals(null, f447Req.getThirdIt());
            assertEquals("SE1 9EQ", f447Req.getPostCd());
            assertEquals("1E", f447Req.getDelivPointSuffixCd());

            assertEquals("firstname", f447Req.getFirstForeNm());
            assertEquals("middleName", f447Req.getSecondForeNm());
            assertEquals(null, f447Req.getThirdForeNm());
        } catch (InternalServiceErrorMsg e) {
            fail();
        }

    }

    @Test
    public void testConvertThrowsException() {
        Individual isPlayedBy = testDataHelper.createIsPlayedBy();

        try {
            F447Req f447Req = enquirePartyIdRequestFactory.convert(testDataHelper.createPostalAddressList(), null, exceptionUtility);
            fail();

        } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            Assert.assertEquals("82001", internalServiceErrorMsg.getFaultInfo().getReasonCode());
        }

    }

}
