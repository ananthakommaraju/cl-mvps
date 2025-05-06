package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.soap.soa.ipm.esb.common.MaintenanceAuditData;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.common.Place;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.common.Registration;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.common.TaxIdentificationNumberType;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ip.TaxRegistration;
import com.lloydsbanking.xml.schema.enterprise.informationtechnology.ipm.esb.ifwxml_product.AdjustmentCondition;
import com.lloydsbanking.xml.schema.enterprise.informationtechnology.ipm.esb.ifwxml_product.AttributeConditionValue;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaxRegistrationFactory {

    public static final String ATTRIBUTE_VALUE = "delete";
    public static final String ATTRIBUTE_CODE = "Action";
    public static final String SOA_COUNTRY_OF_BIRTH = "5";
    public static final String SOA_CITIZENSHIP = "1";
    public static final String SOA_TAX_RESIDENCY = "8";
    public static final String SOA_COUNTRY_OF_RESIDENCE = "2";

    public TaxRegistration copyTaxRegistration(TaxRegistration taxRegistration) {
        TaxRegistration taxRegistrationCreate = new TaxRegistration();
        taxRegistrationCreate.setTaxIdentificationNumber(taxRegistration.getTaxIdentificationNumber());
        taxRegistrationCreate.setIsIssuedIn(taxRegistration.getIsIssuedIn());
        taxRegistrationCreate.setDescription(taxRegistration.getDescription());
        taxRegistrationCreate.setRegistrationType(taxRegistration.getRegistrationType());
        taxRegistrationCreate.setEndDate(taxRegistration.getEndDate());
        taxRegistrationCreate.setHasRegistrationAuthority(taxRegistration.getHasRegistrationAuthority());
        taxRegistrationCreate.setIsBasicDataIncomplete(taxRegistration.isIsBasicDataIncomplete());
        taxRegistrationCreate.setIsDirty(taxRegistration.isIsDirty());
        taxRegistrationCreate.setJurisdiction(taxRegistration.getJurisdiction());
        taxRegistrationCreate.setLastUsedDate(taxRegistration.getLastUsedDate());
        taxRegistrationCreate.setLastVerifiedDate(taxRegistration.getLastVerifiedDate());
        taxRegistrationCreate.setObjectReference(taxRegistration.getObjectReference());
        taxRegistrationCreate.setStartDate(taxRegistration.getStartDate());
        taxRegistrationCreate.setRegistrationStatus(taxRegistration.getRegistrationStatus());
        taxRegistrationCreate.setRegistrationNumber(taxRegistration.getRegistrationNumber());
        taxRegistrationCreate.setPlaceOfIssue(taxRegistration.getPlaceOfIssue());
        return taxRegistrationCreate;
    }

    public TaxIdentificationNumberType copyTaxIdentificationNumberType(TaxIdentificationNumberType taxIdentificationNumberType) {
        TaxIdentificationNumberType taxIdentificationNumberTypeCreate = new TaxIdentificationNumberType();
        taxIdentificationNumberTypeCreate.setName(taxIdentificationNumberType.getName());
        taxIdentificationNumberTypeCreate.setDescription(taxIdentificationNumberType.getDescription());
        taxIdentificationNumberTypeCreate.setHasTaxIdentificationSupertype(taxIdentificationNumberType.getHasTaxIdentificationSupertype());
        return taxIdentificationNumberType;
    }

    public TaxRegistration setTaxPayID(TaxIdentificationNumberType taxIdentificationNumberType, String isIssuedInName, List<MaintenanceAuditData> maintenanceAuditListTaxRegistration) {
        TaxRegistration taxRegistration = new TaxRegistration();
        taxRegistration.setIsIssuedIn(new Place());
        taxRegistration.getIsIssuedIn().setName(isIssuedInName);
        taxRegistration.setTaxIdentificationNumber(taxIdentificationNumberType);
        if (hasMaintenanceAuditData(maintenanceAuditListTaxRegistration)) {
            taxRegistration.getMaintenanceAuditData().addAll(maintenanceAuditListTaxRegistration);
        }
        return taxRegistration;

    }

    public TaxIdentificationNumberType getTaxIdentificationNumberType(String name, String description) {
        TaxIdentificationNumberType taxIdentificationNumberType = new TaxIdentificationNumberType();
        taxIdentificationNumberType.setDescription(description);
        taxIdentificationNumberType.setName(name);
        return taxIdentificationNumberType;
    }

    public boolean hasMaintenanceAuditData(List<MaintenanceAuditData> maintenanceAuditDatas) {
        if (maintenanceAuditDatas != null && !maintenanceAuditDatas.isEmpty()) {
            return true;
        }
        return false;
    }

    public AdjustmentCondition getAdjustmentCondition() {
        AdjustmentCondition adjustmentCondition = new AdjustmentCondition();
        AttributeConditionValue attributeConditionValue = new AttributeConditionValue();
        adjustmentCondition.setDataItem("RecordInvolvedPartyDetailsRequest/involvedParty/Individual/registration/TaxRegistration[1]");
        attributeConditionValue.setValue(ATTRIBUTE_VALUE);
        attributeConditionValue.setCode(ATTRIBUTE_CODE);
        adjustmentCondition.getHasAttributeConditionValues().add(attributeConditionValue);
        return adjustmentCondition;
    }

    public void setAuditData(Registration registration, List<MaintenanceAuditData> maintenanceAuditListCBO, List<MaintenanceAuditData> maintenanceAuditListPrevNationalities, List<MaintenanceAuditData> maintenanceAuditListCountryOfResidency, List<MaintenanceAuditData> maintenanceAuditListTaxResidency) {
        if (registration.getRegistrationType() != null && !StringUtils.isEmpty(registration.getRegistrationType().getName())) {
            String countryAssociationType = registration.getRegistrationType().getName();
            List<MaintenanceAuditData> maintenanceAuditDatas = registration.getMaintenanceAuditData();
            if (!CollectionUtils.isEmpty(maintenanceAuditDatas)) {
                if (SOA_COUNTRY_OF_BIRTH.equalsIgnoreCase(countryAssociationType)) {
                    maintenanceAuditListCBO.add(maintenanceAuditDatas.get(0));
                } else if (SOA_CITIZENSHIP.equalsIgnoreCase(countryAssociationType)) {
                    maintenanceAuditListPrevNationalities.add(maintenanceAuditDatas.get(0));
                } else if (SOA_COUNTRY_OF_RESIDENCE.equalsIgnoreCase(countryAssociationType)) {
                    maintenanceAuditListCountryOfResidency.add(maintenanceAuditDatas.get(0));
                } else if (SOA_TAX_RESIDENCY.equalsIgnoreCase(countryAssociationType)) {
                    maintenanceAuditListTaxResidency.add(maintenanceAuditDatas.get(0));
                }
            }
        }
    }
}
