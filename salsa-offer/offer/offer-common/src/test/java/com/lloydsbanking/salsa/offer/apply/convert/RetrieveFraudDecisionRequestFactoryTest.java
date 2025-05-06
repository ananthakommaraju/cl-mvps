package com.lloydsbanking.salsa.offer.apply.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Req;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class RetrieveFraudDecisionRequestFactoryTest {
    private RetrieveFraudDecisionRequestFactory requestFactory;
    private F204Req f204Req;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        requestFactory = new RetrieveFraudDecisionRequestFactory();
        f204Req = null;
        testDataHelper = new TestDataHelper();

    }


    @Test
    public void testConvert() throws ParseException, DatatypeConfigurationException {

        List<ExtSysProdIdentifier> extSysProdIdentifierList = new ArrayList<>();
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode("00107");
        extSysProdIdentifier.setProductIdentifier("201");
        extSysProdIdentifierList.add(extSysProdIdentifier);

        f204Req = requestFactory.create("regionCode", "areaCode", "contactPointId", extSysProdIdentifierList, "123", testDataHelper.createIndividualName(), testDataHelper.primaryInvolvedParty(), testDataHelper.createPostalAddList());

        assertEquals(1, f204Req.getMaxRepeatGroupQy());
        assertEquals("regionCode", f204Req.getRequestDetails().getRegionCd());
        assertEquals("areaCode", f204Req.getRequestDetails().getAreaCd());
        assertEquals(201, f204Req.getApplicationDetails().getProductId());
        assertEquals("123", f204Req.getRequestDetails().getCreditScoreRequestNo());
        assertEquals("contactPointId", f204Req.getRequestDetails().getSortCd());

        assertEquals(12, f204Req.getPersonalDetails().get(0).getPartyId());
        assertEquals("200", f204Req.getPersonalDetails().get(0).getPartyBusinessRltnspCd());
        assertEquals("04041991", f204Req.getPersonalDetails().get(0).getBirthDt());
        assertEquals("456", f204Req.getPersonalDetails().get(0).getEmploymentStatusCd());
        assertEquals(1, f204Req.getPersonalDetails().get(0).getPartyIdentifiers().get(0).getExtSysId());
        assertEquals("2", f204Req.getPersonalDetails().get(0).getPartyIdentifiers().get(0).getExtPartyIdTx());
        assertEquals(4, f204Req.getPersonalDetails().get(0).getPartyIdentifiers().get(1).getExtSysId());
        assertEquals("3", f204Req.getPersonalDetails().get(0).getPartyIdentifiers().get(1).getExtPartyIdTx());

        assertEquals("m", f204Req.getPersonalDetails().get(0).getSecondIt());
        assertEquals("", f204Req.getPersonalDetails().get(0).getThirdIt());
        assertEquals("201504", f204Req.getPersonalDetails().get(0).getUKResidencyPermissionExpiryDt());
        assertEquals("Mr", f204Req.getPersonalDetails().get(0).getPartyTl());
        assertEquals("firstname", f204Req.getPersonalDetails().get(0).getFirstForeNm());
        assertEquals("lastname", f204Req.getPersonalDetails().get(0).getSurname());

        assertEquals("", f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getAddressResolutionCd());
        assertEquals(707, f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getAddressResidenceDr());
        assertEquals("001", f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getPostalAddressTypeCd());
        assertEquals("SE1 9EQ", f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getPostCd());
        assertEquals("Y", f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getFormattedAddressIn());
        assertEquals("N", f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getUnformattedAddressIn());
        assertEquals("PARK STREET", f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getFormattedAddress().getAddressFirstLineTx());
        assertEquals("district", f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getFormattedAddress().getAddressDistrictNm());
        assertEquals("town", f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getFormattedAddress().getAddressTownNm());
        assertEquals("county", f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getFormattedAddress().getAddressCountyNm());
        assertEquals("building", f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getFormattedAddress().getBuildingNm());
        assertEquals("23", f204Req.getPersonalDetails().get(0).getAddressDetails().get(0).getFormattedAddress().getBuildingNo());

    }

}
