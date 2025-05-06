package com.lloydsbanking.salsa.offer.apply.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Req;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigDecimal;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class RetrieveCreditDecisionRequestFactoryTest {
    private RetrieveCreditDecisionRequestFactory f424RequestFactory;
    private F424Req f424Req;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        f424RequestFactory = new RetrieveCreditDecisionRequestFactory();
        testDataHelper = new TestDataHelper();
        f424Req = null;
    }

    @Test
    public void testCreate() throws ParseException, DatatypeConfigurationException {

        f424Req = f424RequestFactory.create(testDataHelper.TEST_RETAIL_CHANNEL_ID, "arrangementId", "Y", "5", "CO_HOLD", "subChannelcode", "afidentifier", testDataHelper.createPrimaryInvolvedParty(), "CC", true, "Y", testDataHelper.createCurrencyAmount(new BigDecimal(300)));
        assertEquals(1, f424Req.getMaxRepeatGroupQy());
        assertEquals("001", f424Req.getRequestDetails().getCSOrganisationCd());
        assertEquals("024", f424Req.getRequestDetails().getCreditScoreSourceSystemCd());
        assertEquals("004", f424Req.getRequestDetails().getApplicationSourceCd());
        assertEquals("N", f424Req.getFunctionDetails().getAddressTargetingIn());
        assertEquals("N", f424Req.getFunctionDetails().getAddressCorrectionIn());
        assertEquals("arrangementId", f424Req.getRequestDetails().getCreditScoreRequestNo());
        assertEquals("LTB", f424Req.getRequestDetails().getSortCd());

        assertEquals("subChannelcode", f424Req.getApplicationDetails().getCsSaleChannelCd());
        assertEquals("afidentifier", f424Req.getApplicationDetails().getProductIntroducerCd());
        assertEquals(5, f424Req.getApplicationDetails().getProductId());
        assertEquals("", f424Req.getApplicationDetails().getMarketingCd());
        assertEquals("2", f424Req.getApplicationDetails().getGuaranteedProductMailingCd());
        assertEquals("S", f424Req.getApplicationDetails().getReturnedChequesIn());
        assertEquals("Y", f424Req.getApplicationDetails().getDirectMarketingIn());
        assertEquals("30000", f424Req.getApplicationDetails().getPreferredLimitAm());
        assertEquals("Y", f424Req.getApplicationDetails().getDirectDebitIn());
        assertEquals("Y", f424Req.getApplicationDetails().getDebtBuyInIn());
        assertEquals("Y", f424Req.getApplicationDetails().getEmailAddressIn());
        assertEquals("100", f424Req.getApplicationDetails().getGuaranteedCreditCardLimitAm());

        assertEquals(308, f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(0).getAddressResidenceDr());
        assertEquals("postCodeOut postCodeIn", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(0).getPostCd());
        assertEquals("001", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(0).getAddressTypeCd());
        assertEquals("N", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(0).getUnformattedAddressIn());
        assertEquals("Y", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(0).getFormattedAddressIn());
        assertEquals("subBuilding", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(0).getFormattedAddress().getSubBuildingNm());
        assertEquals("postTown", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(0).getFormattedAddress().getAddressTownNm());
        assertEquals("country", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(0).getFormattedAddress().getAddressCountyNm());
        assertEquals("123", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(0).getFormattedAddress().getBuildingNo());
        assertEquals("building", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(0).getFormattedAddress().getBuildingNm());
        assertEquals("addressLine1Tx", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(0).getFormattedAddress().getAddressLine1Tx());


        assertEquals(408, f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(1).getAddressResidenceDr());
        assertEquals("postC ode", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(1).getPostCd());
        assertEquals("002", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(1).getAddressTypeCd());
        assertEquals("N", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(1).getFormattedAddressIn());
        assertEquals("Y", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(1).getUnformattedAddressIn());
        assertEquals("003", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(1).getUnformattedAddress().getUnstrdPostalAddrssTypeCd());
        assertEquals("address1", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(1).getUnformattedAddress().getUnfrmttdAddressLine1Tx());
        assertEquals("address2", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(1).getUnformattedAddress().getUnfrmttdAddressLine2Tx());
        assertEquals("address3", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(1).getUnformattedAddress().getUnfrmttdAddressLine3Tx());
        assertEquals("address4", f424Req.getDecisionPersonalDetails().get(0).getAddressDetailsWithResolution().get(1).getUnformattedAddress().getUnfrmttdAddressLine4Tx());


    }

}
