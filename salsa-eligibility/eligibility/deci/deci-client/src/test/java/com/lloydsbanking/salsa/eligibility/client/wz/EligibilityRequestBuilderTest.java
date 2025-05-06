package com.lloydsbanking.salsa.eligibility.client.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.client.wz.EligibilityRequestBuilder;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@org.junit.experimental.categories.Category(UnitTest.class)
public class EligibilityRequestBuilderTest {
    EligibilityRequestBuilder eligibilityRequestBuilderWZ;
    RequestHeader header;

    @Before
    public void setUp()

    {
        eligibilityRequestBuilderWZ = new EligibilityRequestBuilder();
        header = new RequestHeader();


    }


    @Test
    public void customerDetailsTest() {
        Customer customer = new Customer();
        DetermineEligibleCustomerInstructionsRequest request = eligibilityRequestBuilderWZ.customerDetails(customer).build();
        assertEquals(customer, request.getCustomerDetails());
    }


    @Test
    public void testexistingProductArrangments() {
        List<ProductArrangement> productArrangements = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangements.add(productArrangement);
        DetermineEligibleCustomerInstructionsRequest request = eligibilityRequestBuilderWZ.existingProductArrangments(productArrangements).build();
        assertEquals(productArrangements, request.getExistingProductArrangments());
    }

    @Test
    public void testcandidateInstructions() {


        List<String> candidateInstructions = new ArrayList();
        candidateInstructions.add("PAN");
        DetermineEligibleCustomerInstructionsRequest request = eligibilityRequestBuilderWZ.candidateInstructions(candidateInstructions).build();
        assertEquals(candidateInstructions, request.getCandidateInstructions());

    }


    @Test
    public void testHeader() {

        header.setArrangementId("Arrnagement");
        header.setBusinessTransaction("Business");
        header.setChannelId("IBL");
        header.setContactPointId("contact_pt_id");
        DetermineEligibleCustomerInstructionsRequest request = eligibilityRequestBuilderWZ.header(header).build();
        assertEquals(header, request.getHeader());

    }
}







