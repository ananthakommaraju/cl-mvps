package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.soap.soa.servicearrangement.CreateServiceArrangementRequest;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ArrangementAssociation;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ArrangementType;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.BenefitArrangement;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ServiceArrangement;
import com.lloydstsb.schema.enterprise.ifwxml_common.ObjectReference;
import com.lloydstsb.schema.enterprise.ifwxml_event.Activity;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreateServiceArrangementRequestFactoryTest {
    CreateServiceArrangementRequestFactory requestFactory;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        requestFactory = new CreateServiceArrangementRequestFactory();
        testDataHelper = new TestDataHelper();
        requestFactory.serviceArrangementFactory = mock(ServiceArrangementFactory.class);
    }

    @Test
    public void testConvert() {
        when(requestFactory.serviceArrangementFactory.getServiceArrangement(any(ProductArrangement.class),any(String.class))).thenReturn(serviceArrangement());
        CreateServiceArrangementRequest createServiceArrangementRequest = requestFactory.convert(testDataHelper.createDepositArrangement("1"),"3");
        assertEquals("AVB", createServiceArrangementRequest.getServiceArrangement().getHasArrangementType().getName());
        assertEquals("ADDED_VALUE_BENEFIT", createServiceArrangementRequest.getServiceArrangement().getArrangementAssociations().get(0).getRelatedArrangement().getName());
        assertEquals("001", createServiceArrangementRequest.getServiceArrangement().getArrangementAssociations().get(0).getRelatedArrangement().getHasArrangementType().getName());
        assertEquals("PENDING_SELECTION", createServiceArrangementRequest.getServiceArrangement().getArrangementAssociations().get(0).getRelatedArrangement().getRelatedEvents().get(0).getObjectReference().getId());
        assertEquals("004", createServiceArrangementRequest.getServiceArrangement().getArrangementAssociations().get(0).getRelatedArrangement().getRelatedEvents().get(0).getEventCategory().getName());
    }

    private ServiceArrangement serviceArrangement() {
        ServiceArrangement serviceArrangement = new ServiceArrangement();
        ArrangementType arrangementType = new ArrangementType();
        arrangementType.setName("AVB");
        serviceArrangement.setHasArrangementType(arrangementType);
        ArrangementAssociation arrangementAssociation = new ArrangementAssociation();
        BenefitArrangement benefitArrangement = new BenefitArrangement();
        benefitArrangement.setName("ADDED_VALUE_BENEFIT");
        ArrangementType arrangementType1 = new ArrangementType();
        arrangementType1.setName("001");
        benefitArrangement.setHasArrangementType(arrangementType1);
        Activity activity = new Activity();
        com.lloydstsb.schema.enterprise.ifwxml_common.Category category = new com.lloydstsb.schema.enterprise.ifwxml_common.Category();
        category.setName("004");
        activity.setEventCategory(category);
        ObjectReference objectReference = new ObjectReference();
        objectReference.setId("PENDING_SELECTION");
        activity.setObjectReference(objectReference);
        benefitArrangement.getRelatedEvents().add(activity);
        arrangementAssociation.setRelatedArrangement(benefitArrangement);
        serviceArrangement.getArrangementAssociations().add(arrangementAssociation);
        return serviceArrangement;
    }
}
