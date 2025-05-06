package com.lloydsbanking.salsa.apapca.service.fulfil.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydstsb.ib.wsbridge.account.StB276AAccProcessOverdraft;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.OverdraftDetails;
import lib_sim_bo.businessobjects.Rates;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class B276RequestFactoryTest {
    TestDataHelper testDataHelper;
    RequestHeader requestHeader;
    DepositArrangement depositArrangement;
    B276RequestFactory b276RequestFactory;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        depositArrangement = testDataHelper.createDepositArrangement("1");
        b276RequestFactory = new B276RequestFactory();
        b276RequestFactory.headerRetriever = mock(HeaderRetriever.class);
        b276RequestFactory.bapiHeaderToStHeaderConverter = mock(BapiHeaderToStHeaderConverter.class);
        b276RequestFactory.stODRatesFactory = mock(StODRatesFactory.class);
        b276RequestFactory.addressDetails = mock(AddressDetails.class);

        BapiInformation bapiInfo = mock(BapiInformation.class);
        ContactPoint contactPoint = mock(ContactPoint.class);

        when(b276RequestFactory.headerRetriever.getBapiInformationHeader(requestHeader)).thenReturn(bapiInfo);
        when(bapiInfo.getBAPIHeader()).thenReturn(testDataHelper.createBapiHeader());
        when(b276RequestFactory.headerRetriever.getContactPoint(requestHeader)).thenReturn(contactPoint);
        when(contactPoint.getContactPointId()).thenReturn("123456");
        when(b276RequestFactory.headerRetriever.getServiceRequest(requestHeader)).thenReturn(testDataHelper.getServiceRequestFromRequestHeader(requestHeader));
        when(b276RequestFactory.bapiHeaderToStHeaderConverter.convert(testDataHelper.createBapiHeader(), testDataHelper.getServiceRequestFromRequestHeader(requestHeader), "123456")).thenReturn(testDataHelper.createStHeader1());

        OverdraftDetails overdraftDetails = new OverdraftDetails();
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        overdraftDetails.setExpiryDate(dateFactory.stringToXMLGregorianCalendar("1992-01-12", simpleDateFormat));
    }

    @Test
    public void testConvert() {
        assertNotNull(b276RequestFactory.convert(depositArrangement, requestHeader));
    }

    @Test
    public void testConvertWhenOverdraftDetailsArePresent() {
        depositArrangement.setOverdraftDetails(new OverdraftDetails());
        depositArrangement.getOverdraftDetails().setAmount(new CurrencyAmount());
        depositArrangement.getOverdraftDetails().getAmount().setAmount(BigDecimal.TEN);
        depositArrangement.getOverdraftDetails().setProductType("CC");
        depositArrangement.getOverdraftDetails().getInterestRates().add(new Rates());
        depositArrangement.getOverdraftDetails().getInterestRates().get(0).setType("Type");
        depositArrangement.getOverdraftDetails().getInterestRates().get(0).setValue(BigDecimal.ONE);
        depositArrangement.getOverdraftDetails().setIsChargingEnabled(true);

        StB276AAccProcessOverdraft b276AAccProcessOverdraft = b276RequestFactory.convert(depositArrangement, requestHeader);
        assertEquals("A", b276AAccProcessOverdraft.getStaccount().getProdtype());
        assertEquals("779129", b276AAccProcessOverdraft.getStaccount().getSortcode());
        assertEquals(BigDecimal.TEN, b276AAccProcessOverdraft.getAmtOverdraftNew());
        assertEquals("014", b276AAccProcessOverdraft.getRepaymentSource());
    }

}
