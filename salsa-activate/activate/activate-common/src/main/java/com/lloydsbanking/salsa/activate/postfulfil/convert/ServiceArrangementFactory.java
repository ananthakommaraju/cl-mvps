package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ArrangementAssociation;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ArrangementType;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.BenefitArrangement;
import com.lloydstsb.schema.enterprise.ifwxml_arrangement.ServiceArrangement;
import com.lloydstsb.schema.enterprise.ifwxml_common.Category;
import com.lloydstsb.schema.enterprise.ifwxml_common.ObjectReference;
import com.lloydstsb.schema.enterprise.ifwxml_event.Activity;
import com.lloydstsb.schema.enterprise.ifwxml_involvedparty.*;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceArrangementFactory {
    public static final String SERVICE_ARRANGEMENT_TYPE_LIFE_STYLE_BENEFIT = "AVB";
    public static final String ARRANGEMENT_TYPE_ADDED_VALUE_BENEFIT = "ADDED_VALUE_BENEFIT";
    public static final String ADDED_VALUE_BENEFIT_TYPE_LIFE_STYLE_BENEFIT = "001";
    public static final String ACTIVITY_PENDING_SELECTION = "PENDING_SELECTION";


    public ServiceArrangement getServiceArrangement(ProductArrangement productArrangement, String benefitLookupDesc) {
        ServiceArrangement serviceArrangement = new ServiceArrangement();
        ArrangementType arrangementType = new ArrangementType();
        arrangementType.setName(SERVICE_ARRANGEMENT_TYPE_LIFE_STYLE_BENEFIT);
        serviceArrangement.setHasArrangementType(arrangementType);
        ArrangementAssociation arrangementAssociation = new ArrangementAssociation();
        BenefitArrangement benefitArrangement = new BenefitArrangement();
        benefitArrangement.setName(ARRANGEMENT_TYPE_ADDED_VALUE_BENEFIT);
        ArrangementType arrangementType1 = new ArrangementType();
        arrangementType1.setName(ADDED_VALUE_BENEFIT_TYPE_LIFE_STYLE_BENEFIT);
        benefitArrangement.setHasArrangementType(arrangementType1);
        benefitArrangement.getRelatedEvents().add(getActivity(benefitLookupDesc));
        arrangementAssociation.setRelatedArrangement(benefitArrangement);
        Customer customer = getCustomer(productArrangement.getPrimaryInvolvedParty());
        serviceArrangement.getArrangementAssociations().add(arrangementAssociation);
        serviceArrangement.getRoles().add(customer);
        return serviceArrangement;
    }

    private Activity getActivity(String benefitLookupDesc) {
        Activity activity = new Activity();
        Category category = new Category();
        category.setName(benefitLookupDesc);
        activity.setEventCategory(category);

        ObjectReference objectReference = new ObjectReference();
        objectReference.setId(ACTIVITY_PENDING_SELECTION);
        activity.setObjectReference(objectReference);
        return activity;
    }

    private Customer getCustomer(lib_sim_bo.businessobjects.Customer primaryInvolvedParty) {
        Customer customer = new Customer();
        Individual individual = new Individual();
        lib_sim_bo.businessobjects.Individual individual1 = primaryInvolvedParty.getIsPlayedBy();
        if (individual1 != null) {
            if (individual1.getBirthDate() != null) {
                individual.setBirthDate(individual1.getBirthDate());
            }
            if (!individual1.getIndividualName().isEmpty()) {
                individual.getName().add(getIndividualName(individual1));
            }
        }
        ContactPreference contactPreference = new ContactPreference();
        ElectronicAddress electronicAddress = new ElectronicAddress();
        electronicAddress.setUserid(primaryInvolvedParty.getEmailAddress());
        TelephoneNumber telephoneNumber = getTelephoneNumber(primaryInvolvedParty.getTelephoneNumber());
        contactPreference.getContactPoints().add(electronicAddress);
        contactPreference.getContactPoints().add(telephoneNumber);
        individual.getContactPreferences().add(contactPreference);
        customer.setInvolvedParty(individual);
        return customer;
    }

    private TelephoneNumber getTelephoneNumber(List<lib_sim_bo.businessobjects.TelephoneNumber> telephoneNumbers) {
        TelephoneNumber telephoneNumber = new TelephoneNumber();
        for (lib_sim_bo.businessobjects.TelephoneNumber telephoneNumber1 : telephoneNumbers) {
            if ("7".equalsIgnoreCase(telephoneNumber1.getTelephoneType())) {
                telephoneNumber.setPhoneSequenceNumber(telephoneNumber1.getPhoneNumber());
            }
        }
        return telephoneNumber;
    }

    private IndividualName getIndividualName(lib_sim_bo.businessobjects.Individual individual1) {
        lib_sim_bo.businessobjects.IndividualName individualName = individual1.getIndividualName().get(0);
        IndividualName individualName1 = new IndividualName();
        individualName1.setFirstName(individualName.getFirstName());
        individualName1.setLastName(individualName.getLastName());
        if (!StringUtils.isEmpty(individualName.getPrefixTitle())) {
            individualName1.setPrefixTitle(InvolvedPartyNamePrefixType.valueOf(individualName.getPrefixTitle().toUpperCase()));
        }
        return individualName1;
    }
}
