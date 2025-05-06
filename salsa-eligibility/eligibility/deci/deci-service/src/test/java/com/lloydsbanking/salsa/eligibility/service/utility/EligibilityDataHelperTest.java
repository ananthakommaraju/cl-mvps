package com.lloydsbanking.salsa.eligibility.service.utility;

import com.lloydsbanking.salsa.UnitTest;
import lb_gbo_sales.Customer;
import lb_gbo_sales.ProductArrangement;
import lib_sim_bo.businessobjects.Organisation;
import lib_sim_bo.businessobjects.OrganisationUnit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class EligibilityDataHelperTest {
    EligibilityDataHelper dataHelper = new EligibilityDataHelper();

    @Test
    public void testGetCustomerIdReturnsValue() {
        ProductArrangement custArr = new ProductArrangement();
        Customer participantCust = new Customer();
        participantCust.setPartyId("partyId");
        custArr.getParticipantCusomters().add(participantCust);

        List<ProductArrangement> prodArrList = new ArrayList<>();
        prodArrList.add(custArr);
        String custId = dataHelper.getCustomerId(prodArrList);
        assertEquals("partyId", custId);

    }

    @Test
    public void testGetCustomerIdReturnsNull() {
        ProductArrangement custArr = new ProductArrangement();
        Customer participantCust = new Customer();

        custArr.getParticipantCusomters().add(participantCust);

        List<ProductArrangement> prodArrList = new ArrayList<>();
        prodArrList.add(custArr);
        String custId = dataHelper.getCustomerId(prodArrList);
        assertNull(custId);

    }
    @Test
    public void testGetSortCodeReturnsValue() {
        ProductArrangement custArr = new ProductArrangement();
        Customer participantCust = new Customer();
        participantCust.setPartyId("partyId");
        custArr.getParticipantCusomters().add(participantCust);
        custArr.setSortCode("sortCode");
        List<ProductArrangement> prodArrList = new ArrayList<>();
        prodArrList.add(custArr);
        String sortCode = dataHelper.getSortCode(prodArrList);
        assertEquals("sortCode", sortCode);

    }

    @Test
    public void testGetSortCodeReturnsNull() {
        ProductArrangement custArr = new ProductArrangement();
        Customer participantCust = new Customer();
        participantCust.setPartyId("partyId");
        custArr.getParticipantCusomters().add(participantCust);

        List<ProductArrangement> prodArrList = new ArrayList<>();
        prodArrList.add(custArr);
        String sortCode = dataHelper.getSortCode(prodArrList);
        assertNull(sortCode);

    }

    @Test
    public void testGetCustomerIdWZ(){
        lib_sim_bo.businessobjects.Customer customer=new lib_sim_bo.businessobjects.Customer();
        customer.setCustomerIdentifier("1234");

        String customerId=dataHelper.getCustomerIdWZ(customer);

        assertEquals("1234", customerId);
    }

    @Test
    public void testGetSortCodeWZ(){
        List<lib_sim_bo.businessobjects.ProductArrangement> productArrangements=new ArrayList<>();
        lib_sim_bo.businessobjects.ProductArrangement productArrangement = new lib_sim_bo.businessobjects.ProductArrangement();
        productArrangement.setFinancialInstitution(new Organisation());
        productArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode("6789");
        productArrangements.add(productArrangement);

        String sortCode= dataHelper.getSortCodeWZ(productArrangements);

        assertEquals("6789", sortCode);
    }
}
