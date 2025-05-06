package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ServiceArrangement;
import lib_sim_bo.businessobjects.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ServiceArrangementFactoryTest {
    ServiceArrangementFactory serviceArrangementFactory;
    TestDataHelper dataHelper;

    @Before
    public void setUp() {
        serviceArrangementFactory = new ServiceArrangementFactory();
        dataHelper = new TestDataHelper();
    }

    @Test
    public void testGetServiceArrangement() {
        ServiceArrangement serviceArrangement = serviceArrangementFactory.getServiceArrangement(createProductArrangement(),"004");
        assertEquals("AVB", serviceArrangement.getHasArrangementType().getName());
        assertEquals("001", serviceArrangement.getArrangementAssociations().get(0).getRelatedArrangement().getHasArrangementType().getName());
        assertEquals("ADDED_VALUE_BENEFIT", serviceArrangement.getArrangementAssociations().get(0).getRelatedArrangement().getName());
        assertEquals("004", serviceArrangement.getArrangementAssociations().get(0).getRelatedArrangement().getRelatedEvents().get(0).getEventCategory().getName());
    }

    private ProductArrangement createProductArrangement() {
        ProductArrangement productArrangement = new ProductArrangement();
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("LIFE_STYLE_BENEFIT_CODE");
        ruleCondition.setResult("004");
        productArrangement.getConditions().add(ruleCondition);
        Customer customer = new Customer();
        Individual individual = new Individual();
        IndividualName individualName = new IndividualName();
        individualName.setFirstName("zdfgdfg");
        individualName.setLastName("dfgdfg");
        individualName.setPrefixTitle("MR");
        individual.getIndividualName().add(individualName);
        customer.setIsPlayedBy(individual);
        customer.setEmailAddress("sdfgd@fgh.com");
        productArrangement.setPrimaryInvolvedParty(customer);
        return productArrangement;
    }
}
