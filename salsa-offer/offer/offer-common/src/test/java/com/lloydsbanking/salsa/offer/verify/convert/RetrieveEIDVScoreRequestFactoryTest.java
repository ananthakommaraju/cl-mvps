package com.lloydsbanking.salsa.offer.verify.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyParty;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class RetrieveEIDVScoreRequestFactoryTest {

    private RetrieveEIDVScoreRequestFactory retrieveEIDVScoreRequestFactory;

    @Before
    public void setUp() {
        retrieveEIDVScoreRequestFactory = new RetrieveEIDVScoreRequestFactory();
    }

    @Test
    public void testCreate() throws InternalServiceErrorMsg {
        ProductArrangement productArrangement = new TestDataHelper().createFinanceServiceArrangement();
        IdentifyParty identifyParty = retrieveEIDVScoreRequestFactory.create(productArrangement.getPrimaryInvolvedParty(), "0000777505");
        System.out.println(identifyParty);
        assertEquals("777505",identifyParty.getIdentifyParty().getIdentifyPartyInput().getSortcode());
        assertEquals("001",identifyParty.getIdentifyParty().getIdentifyPartyInput().getInteractionStyle());
        assertEquals("004",identifyParty.getIdentifyParty().getIdentifyPartyInput().getChannelContext());
        assertEquals("1985856187",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getPartyId());
        assertEquals("1948-01-01",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getDateOfBirth());
        assertEquals("GBR",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getNationality());
        assertEquals("a@a.com",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getEmailAddress());
        assertEquals(0,identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getNumberOfDependents());
        assertEquals("003",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getEmploymentStatus());
        assertEquals("hghjgj",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getEmployersName());
        assertEquals("firstname",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getPersonalCustomerNames().getPersonalCustomerName().get(0).getForename());
        assertEquals("lastname",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getPersonalCustomerNames().getPersonalCustomerName().get(0).getSurname());
        assertEquals("Mr",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getPersonalCustomerNames().getPersonalCustomerName().get(0).getTitle());
        assertTrue(identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getPersonalCustomerNames().getPersonalCustomerName().get(0).isIsCurrentName());
        assertEquals("001",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getPersonalCustomerNames().getPersonalCustomerName().get(0).getGender());
        assertEquals("96 EDGEHILL ROAD",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getPersonalCustomerAddresses().getPersonalCustomerAddress().get(0).getBuildingNumber());
        assertEquals("CHISLEHURST",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getPersonalCustomerAddresses().getPersonalCustomerAddress().get(0).getBuildingName());
        assertEquals("KENT",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getPersonalCustomerAddresses().getPersonalCustomerAddress().get(0).getSubBuildingName());
        assertEquals("United Kingdom",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getPersonalCustomerAddresses().getPersonalCustomerAddress().get(0).getTownCity());
        assertEquals("BR7  6LB",identifyParty.getIdentifyParty().getIdentifyPartyInput().getPersonalCustomerDetail().getPersonalCustomerAddresses().getPersonalCustomerAddress().get(0).getPostcode());
    }
}
