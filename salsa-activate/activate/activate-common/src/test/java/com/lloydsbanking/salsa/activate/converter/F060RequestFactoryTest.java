package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Req;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class F060RequestFactoryTest {
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;
    private F060RequestFactory f060RequestFactory;


    @Before
    public void setUp() {
        f060RequestFactory = new F060RequestFactory();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
    }

    @Test
    public void testConvert() {
        F060Req f060Req = f060RequestFactory.convert(productArrangement(true));
        assertNotNull(f060Req);
        assertEquals((short) 19, f060Req.getExtSysId());
        assertEquals(1, f060Req.getMaxRepeatGroupQy());
        assertEquals("001", f060Req.getCommsPrefData().get(0).getCommsOptCd());
        assertEquals("98562", f060Req.getPartyInfo().getPartyId().toString());
    }

    @Test
    public void testGuardianDetailsIsNull() {
        ProductArrangement productArrangement = productArrangement(true);
        productArrangement.setGuardianDetails(null);
        F060Req f060Req = f060RequestFactory.convert(productArrangement);
        assertNotNull(f060Req);
        assertEquals((short) 19, f060Req.getExtSysId());
        assertEquals(1, f060Req.getMaxRepeatGroupQy());
        assertEquals("96524", f060Req.getPartyInfo().getPartyId().toString());
    }

    @Test
    public void testGuardianDetailsWithoutidentifier() {
        ProductArrangement productArrangement = testDataHelper.createDepositArrangement("9052");
        Customer customer = new Customer();
        productArrangement.setGuardianDetails(customer);
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.getPrimaryInvolvedParty().setCidPersID("65843");
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("96524");
        F060Req f060Req = f060RequestFactory.convert(productArrangement);
        assertNotNull(f060Req);
        assertEquals((short) 19, f060Req.getExtSysId());
        assertEquals(1, f060Req.getMaxRepeatGroupQy());
        assertEquals("96524", f060Req.getPartyInfo().getPartyId().toString());
    }

    private ProductArrangement productArrangement(boolean preference) {
        ProductArrangement productArrangement = testDataHelper.createDepositArrangement("9052");
        productArrangement.setGuardianDetails(new Customer());
        productArrangement.getGuardianDetails().setCidPersID("10254");
        productArrangement.getGuardianDetails().setCustomerIdentifier("98562");
        productArrangement.setPrimaryInvolvedParty(new Customer());
        productArrangement.getPrimaryInvolvedParty().setCidPersID("15489");
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("96524");
        productArrangement.setMarketingPreferenceByEmail(preference);
        productArrangement.setMarketingPreferenceByMail(preference);
        productArrangement.setMarketingPreferenceByPhone(preference);
        productArrangement.setMarketingPreferenceBySMS(preference);
        return productArrangement;
    }
}
