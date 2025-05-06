package com.lloydsbanking.salsa.activate.postfulfil.convert;


import com.lloydsbanking.salsa.soap.soa.servicearrangement.CreateServiceArrangementRequest;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ProductArrangementIdentifier;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ServiceArrangement;
import com.lloydstsb.schema.enterprise.ifwxml_common.AlternateID;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateServiceArrangementRequestFactory {

    @Autowired
    ServiceArrangementFactory serviceArrangementFactory;

    public CreateServiceArrangementRequest convert(ProductArrangement productArrangement,String benefitLookupDesc) {
        CreateServiceArrangementRequest request = new CreateServiceArrangementRequest();
        com.lloydstsb.schema.enterprise.ifwxml_arrangement.ProductArrangement arrangement = getRelatedArrangement(productArrangement);
        ServiceArrangement serviceArrangement = serviceArrangementFactory.getServiceArrangement(productArrangement,benefitLookupDesc);
        request.setServiceArrangement(serviceArrangement);
        request.getRelatedArrangement().add(arrangement);
        return request;
    }

    private com.lloydstsb.schema.enterprise.ifwxml_arrangement.ProductArrangement getRelatedArrangement(ProductArrangement productArrangement) {
        com.lloydstsb.schema.enterprise.ifwxml_arrangement.ProductArrangement arrangement = new com.lloydstsb.schema.enterprise.ifwxml_arrangement.ProductArrangement();
        ProductArrangementIdentifier productArrangementIdentifier = new ProductArrangementIdentifier();
        productArrangementIdentifier.setAccountNumber(productArrangement.getAccountNumber());
        AlternateID alternateID = new AlternateID();
        alternateID.setValue(productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        productArrangementIdentifier.getAlternateId().add(alternateID);
        arrangement.setObjectReference(productArrangementIdentifier);
        return arrangement;
    }
}
