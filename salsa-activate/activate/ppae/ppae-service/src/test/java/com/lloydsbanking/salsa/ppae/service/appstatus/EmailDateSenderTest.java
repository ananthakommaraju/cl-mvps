package com.lloydsbanking.salsa.ppae.service.appstatus;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class EmailDateSenderTest {

    EmailDateSender emailDateSender;
    TestDataHelper testDataHelper;
    ProductArrangement productArrangement;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        emailDateSender = new EmailDateSender();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createProductArrangement();

    }

    @Test
    public void testGetSentEmailDate() throws DatatypeConfigurationException {
        List<ReferenceDataLookUp> referenceDataCustomerNOShowUpd = new ArrayList<>();
        referenceDataCustomerNOShowUpd.add(new ReferenceDataLookUp());
        referenceDataCustomerNOShowUpd.get(0).setLookupValueDesc("2");
        Date currentDate = new Date();
        DateFactory dateFactory = new DateFactory();
        Date modifiedDate = dateFactory.addDays(currentDate, -2);
        productArrangement.setLastModifiedDate(new DateFactory().dateToXMLGregorianCalendar(modifiedDate));
        assertEquals("2", String.valueOf(emailDateSender.getEmailDate(productArrangement.getLastModifiedDate(), referenceDataCustomerNOShowUpd)));

    }

    @Test
    public void testGetSentEmailDateForNullModifiedDate() throws DatatypeConfigurationException {
        List<ReferenceDataLookUp> referenceDataCustomerNOShowUpd = new ArrayList<>();
        referenceDataCustomerNOShowUpd.add(new ReferenceDataLookUp());
        referenceDataCustomerNOShowUpd.get(0).setLookupValueDesc("2");
        productArrangement.setLastModifiedDate(null);
        assertEquals("0", String.valueOf(emailDateSender.getEmailDate(productArrangement.getLastModifiedDate(), referenceDataCustomerNOShowUpd)));

    }
}
