package com.lloydsbanking.salsa.opaloans.service.identify.evaluate;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.opaloans.service.TestDataHelper;
import com.lloydsbanking.salsa.opaloans.service.identify.downstream.CustomerRetriever;
import com.lloydsbanking.salsa.soap.ocis.c216.objects.PartyProdDataType;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.*;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CustomerEvaluatorTest {
    CustomerEvaluator customerEvaluator;

    RequestHeader header;

    TestDataHelper dataHelper;

    ProductArrangement productArrangement;

    @Before
    public void setUp() {
        customerEvaluator = new CustomerEvaluator();
        customerEvaluator.customerRetriever = mock(CustomerRetriever.class);
        dataHelper = new TestDataHelper();
        header = dataHelper.createOpaLoansRequestHeader("IBL");
        productArrangement = dataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
    }

    @Test
    public void testIsBirthDateReturnsTrueWhenBirthDateIsUnique() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg, OfferException {
        List<PartyProdDataType> partyProdDataTypes = new ArrayList<>();
        partyProdDataTypes.add(dataHelper.createPartyProdDataType(456662112l));
        List<Customer> customers = getCustomerList(partyProdDataTypes);
        when(customerEvaluator.customerRetriever.retrieveCustomer(header, "777146", "03182268")).thenReturn(customers);

        boolean result = customerEvaluator.isBirthDate(header, productArrangement);

        assertTrue(result);
    }

    @Test
    public void testIsBirthDateReturnsTrueWithMultipleBirthDates() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg, OfferException {
        List<PartyProdDataType> partyProdDataTypes = new ArrayList<>();
        partyProdDataTypes.add(dataHelper.createPartyProdDataType(456662112l));
        partyProdDataTypes.add(dataHelper.createPartyProdDataType(456662112l));
        partyProdDataTypes.get(0).setFirstForeNm("xyz");
        partyProdDataTypes.get(0).setSurname("abc");
        partyProdDataTypes.get(1).setFirstForeNm("abc");
        partyProdDataTypes.get(1).setSurname("abc");

        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setFirstName("xyz");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setLastName("abc");

        List<Customer> customers = getCustomerList(partyProdDataTypes);
        when(customerEvaluator.customerRetriever.retrieveCustomer(header, "777146", "03182268")).thenReturn(customers);

        boolean result = customerEvaluator.isBirthDate(header, productArrangement);

        assertTrue(result);
    }

    @Test
    public void testIsBirthDateReturnsFalseWhenNameIsNotCaptured() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg, OfferException {
        List<PartyProdDataType> partyProdDataTypes = new ArrayList<>();
        partyProdDataTypes.add(dataHelper.createPartyProdDataType(456662112l));
        partyProdDataTypes.add(dataHelper.createPartyProdDataType(456662112l));
        List<Customer> customers = getCustomerList(partyProdDataTypes);
        when(customerEvaluator.customerRetriever.retrieveCustomer(header, "777146", "03182268")).thenReturn(customers);

        boolean result = customerEvaluator.isBirthDate(header, productArrangement);

        assertFalse(result);
        assertEquals("ADDITIONAL_DATA_REQUIRED_INDICATOR", productArrangement.getConditions().get(0).getName());
        assertEquals("true", productArrangement.getConditions().get(0).getResult());
    }

    @Test
    public void testIsBirthDateReturnsFalseWhenNameDoesNotMatch() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg, OfferException {
        List<PartyProdDataType> partyProdDataTypes = new ArrayList<>();
        partyProdDataTypes.add(dataHelper.createPartyProdDataType(456662112l));
        partyProdDataTypes.add(dataHelper.createPartyProdDataType(456662112l));
        List<Customer> customers = getCustomerList(partyProdDataTypes);

        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setFirstName("xyz");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setLastName("xyz");

        when(customerEvaluator.customerRetriever.retrieveCustomer(header, "777146", "03182268")).thenReturn(customers);

        boolean result = customerEvaluator.isBirthDate(header, productArrangement);

        assertFalse(result);
        assertEquals("01", productArrangement.getReasonCode().getCode());
        assertEquals("FirstName and LastName Not Matched with OCIS", productArrangement.getReasonCode().getDescription());
    }

    @Test
    public void testIsBirthDateReturnsFalseWhenBirthDateNotMatched() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg, OfferException {
        List<PartyProdDataType> partyProdDataTypes = new ArrayList<>();
        partyProdDataTypes.add(dataHelper.createPartyProdDataType(456662112l));
        partyProdDataTypes.get(0).setBirthDt("22021992");
        List<Customer> customers = getCustomerList(partyProdDataTypes);
        IndividualName name = new IndividualName();
        name.setFirstName("xyz");
        name.setLastName("abc");
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(name);
        when(customerEvaluator.customerRetriever.retrieveCustomer(header, "777146", "03182268")).thenReturn(customers);

        boolean result = customerEvaluator.isBirthDate(header, productArrangement);

        assertFalse(result);
        assertEquals("01", productArrangement.getReasonCode().getCode());
        assertEquals("BirthDate Not Matched", productArrangement.getReasonCode().getDescription());
    }

    private List<Customer> getCustomerList(List<PartyProdDataType> partyProdDataTypes) {
        List<Customer> customerList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(partyProdDataTypes)) {
            for (PartyProdDataType partyProdDataType : partyProdDataTypes) {
                Customer customer = new Customer();
                customer.setCustomerIdentifier(String.valueOf(partyProdDataType.getPartyId()));
                customer.setCidPersID(partyProdDataType.getExtPartyIdTx());
                customer.setIsPlayedBy(new Individual());
                customer.getIsPlayedBy().setBirthDate(!StringUtils.isEmpty(partyProdDataType.getBirthDt()) ? getBirthDate(partyProdDataType.getBirthDt()) : null);
                customer.getIsPlayedBy().getIndividualName().add(0, new IndividualName());
                customer.getIsPlayedBy().getIndividualName().get(0).setFirstName(partyProdDataType.getFirstForeNm());
                customer.getIsPlayedBy().getIndividualName().get(0).setLastName(partyProdDataType.getSurname());
                customer.getIsPlayedBy().getIndividualName().get(0).setPrefixTitle(partyProdDataType.getPartyTl());
                customerList.add(customer);
            }
        }
        return customerList;
    }

    private XMLGregorianCalendar getBirthDate(final String birthDt) {
        XMLGregorianCalendar birthDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        Date date = null;
        try {
            date = formatter.parse(birthDt);
        } catch (ParseException e1) {
        }
        GregorianCalendar calender = new GregorianCalendar();
        calender.setTime(date);
        try {
            birthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
        } catch (DatatypeConfigurationException e1) {
        }
        return birthDate;
    }

}
