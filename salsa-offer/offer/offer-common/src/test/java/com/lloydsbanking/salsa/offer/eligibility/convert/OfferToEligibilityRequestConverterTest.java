package com.lloydsbanking.salsa.offer.eligibility.convert;

import com.lloydsbanking.salsa.UnitTest;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class OfferToEligibilityRequestConverterTest {


    OfferToEligibilityRequestConverter offerToEligibilityRequestConverter;

    @Before
    public void setUp() {
        offerToEligibilityRequestConverter = new OfferToEligibilityRequestConverter();
    }

    @Test
    public void testConvertOfferToEligibilityRequest() throws Exception {
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setApplicationStatus("1001");
        productArrangement.setApplicationType("1");
        productArrangement.setArrangementType("type");
        Product product1 = new Product();
        product1.setInstructionDetails(new InstructionDetails());
        product1.getInstructionDetails().setInstructionMnemonic("P_CLASSIC");
        productArrangement.setAssociatedProduct(product1);
        productArrangement.setPrimaryInvolvedParty(new Customer());

        List<Product> productList = new ArrayList<>();
        Product product = new Product();
        product.setProductIdentifier("1");

        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("8");
        extSysProdIdentifier.setSystemCode("00010");
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);

        ExtSysProdIdentifier extSysProdIdentifier1 = new ExtSysProdIdentifier();
        extSysProdIdentifier1.setProductIdentifier("9");
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier1);

        productList.add(product);

        DetermineEligibleCustomerInstructionsRequest request = offerToEligibilityRequestConverter.convertOfferToEligibilityRequest(productArrangement, new RequestHeader(), true);

        assertEquals("P_CLASSIC", request.getCandidateInstructions().get(0));
        assertEquals("type", request.getArrangementType());

    }

    @Test
    public void testConvertOfferToEligibilityForOfferedProducts() throws Exception {
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setApplicationStatus("1001");
        productArrangement.setApplicationType("1");
        productArrangement.setArrangementType("type");
        productArrangement.setPrimaryInvolvedParty(new Customer());
        Product product1 = new Product();
        product1.setInstructionDetails(new InstructionDetails());
        product1.getInstructionDetails().setInstructionMnemonic("111");
        productArrangement.setAssociatedProduct(product1);

        List<Product> productList = new ArrayList<>();
        Product product = new Product();
        product.setProductIdentifier("1");

        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("8");
        extSysProdIdentifier.setSystemCode("00010");
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);

        ExtSysProdIdentifier extSysProdIdentifier1 = new ExtSysProdIdentifier();
        extSysProdIdentifier1.setProductIdentifier("9");
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier1);

        productList.add(product);

        DetermineEligibleCustomerInstructionsRequest request = offerToEligibilityRequestConverter.convertOfferToEligibilityForOfferedProducts(productArrangement, new RequestHeader(), productList, true);

        assertEquals("8", request.getCandidateInstructions().get(0));
        assertEquals("type", request.getArrangementType());

    }

    @Test
    public void testConvertOfferToEligibilityForOfferedProductsWithEmptyExternalProductHeldIdentifier() throws Exception {
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setApplicationStatus("1001");
        productArrangement.setApplicationType("1");
        productArrangement.setArrangementType("type");
        productArrangement.setPrimaryInvolvedParty(new Customer());
        Product product1 = new Product();
        product1.setInstructionDetails(new InstructionDetails());
        product1.getInstructionDetails().setInstructionMnemonic("111");
        productArrangement.setAssociatedProduct(product1);

        productArrangement.getExistingProducts().add(new Product());
        productArrangement.getExistingProducts().get(0).setExternalProductHeldIdentifier("");

        List<Product> productList = new ArrayList<>();
        Product product = new Product();
        product.setProductIdentifier("1");

        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("8");
        extSysProdIdentifier.setSystemCode("00010");
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);

        ExtSysProdIdentifier extSysProdIdentifier1 = new ExtSysProdIdentifier();
        extSysProdIdentifier1.setProductIdentifier("9");
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier1);

        productList.add(product);

        DetermineEligibleCustomerInstructionsRequest request = offerToEligibilityRequestConverter.convertOfferToEligibilityForOfferedProducts(productArrangement, new RequestHeader(), productList, false);

        assertEquals("8", request.getCandidateInstructions().get(0));
        assertEquals("type", request.getArrangementType());

    }
}