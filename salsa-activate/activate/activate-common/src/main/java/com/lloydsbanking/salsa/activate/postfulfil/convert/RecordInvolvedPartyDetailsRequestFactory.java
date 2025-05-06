package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsRequest;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.common.*;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.RequestHeader;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ip.*;
import com.lloydsbanking.xml.schema.enterprise.informationtechnology.ipm.esb.ifwxml_product.AdjustmentCondition;
import com.lloydsbanking.xml.schema.enterprise.informationtechnology.ipm.esb.ifwxml_product.Condition;
import lib_sim_bo.businessobjects.IdentificationDetails;
import lib_sim_bo.businessobjects.TaxResidencyDetails;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RecordInvolvedPartyDetailsRequestFactory {
    public static final String EXT_SYS_SALSA = "19";
    public static final String SOA_COUNTRY_OF_BIRTH = "5";
    public static final String SOA_CITIZENSHIP = "1";
    public static final String SOA_TAX_RESIDENCY = "8";
    public static final String SOA_COUNTRY_OF_RESIDENCE = "2";
    public static final String SOA_COUNTRY_OF_RESIDENCE_CODE = "GBR";
    public static final String TAX_REGISTRATION_PLACE = "USA";
    public static final String USA_TAX_IDENTIFICATION_NUMBER = "001";
    @Autowired
    TaxRegistrationFactory taxRegistrationFactory;

    public RecordInvolvedPartyDetailsRequest convert(InvolvedParty involvedParty, lib_sim_bo.businessobjects.Customer customer, String countryOfBirth, boolean isCRSSwitch) {
        String customerIdentifier = customer.getCustomerIdentifier();
        String nationality = customer.getIsPlayedBy().getNationality();
        TaxResidencyDetails taxResidencyDetails = customer.getTaxResidencyDetails();
        List<IdentificationDetails> identificationDetailsList = customer.getIdentificationDetails();
        List<String> previousNationalities = customer.getIsPlayedBy().getPreviousNationalities();
        Individual individual = new Individual();
        List<MaintenanceAuditData> maintenanceAuditListCBO = new ArrayList<>();
        List<MaintenanceAuditData> maintenanceAuditListPrevNationalities = new ArrayList<>();
        List<MaintenanceAuditData> maintenanceAuditListTaxRegistration = new ArrayList<>();
        List<MaintenanceAuditData> maintenanceAuditListCountryOfResidency = new ArrayList<>();
        List<MaintenanceAuditData> maintenanceAuditListTaxResidency = new ArrayList<>();
        List<TaxRegistration> taxRegistrationList = new ArrayList<>();
        String retrievePartyResponseTIN = null;
        for (Registration registration : involvedParty.getRegistration()) {
            if (registration != null) {
                if (registration instanceof NationalRegistration) {
                    taxRegistrationFactory.setAuditData(registration, maintenanceAuditListCBO, maintenanceAuditListPrevNationalities, maintenanceAuditListCountryOfResidency, maintenanceAuditListTaxResidency);
                } else if (registration instanceof TaxRegistration) {
                    retrievePartyResponseTIN = setTaxData(registration, maintenanceAuditListTaxRegistration, taxRegistrationList);
                }
            }
        }
        individual.setObjectReference(getInvolvedPartyObjectReference(customerIdentifier));
        List<PartyRegistration> partyRegistrations = new ArrayList<>();
        setNationalRegistration(nationality, SOA_CITIZENSHIP, maintenanceAuditListPrevNationalities, partyRegistrations);
        setNationalRegistration(SOA_COUNTRY_OF_RESIDENCE_CODE, SOA_COUNTRY_OF_RESIDENCE, maintenanceAuditListCountryOfResidency, partyRegistrations);
        setNationalRegistration(countryOfBirth, SOA_COUNTRY_OF_BIRTH, maintenanceAuditListCBO, partyRegistrations);
        if (previousNationalities != null) {
            for (String previousNationality : previousNationalities) {
                setNationalRegistration(previousNationality, SOA_CITIZENSHIP, maintenanceAuditListPrevNationalities, partyRegistrations);
            }
        }
        if (taxResidencyDetails != null) {
            for (String country : taxResidencyDetails.getTaxResidencyCountries()) {
                setNationalRegistration(country, SOA_TAX_RESIDENCY, maintenanceAuditListTaxResidency, partyRegistrations);
            }
        }
        RecordInvolvedPartyDetailsRequest request;
        if (isCRSSwitch) {
            request = setRegistrationDetailsForOnCRS(identificationDetailsList, maintenanceAuditListTaxRegistration, taxRegistrationList, partyRegistrations);
        } else {
            request = setTaxRegistrationDetailsForOffCRS(taxResidencyDetails, partyRegistrations, retrievePartyResponseTIN, maintenanceAuditListTaxRegistration);
        }
        individual.getRegistration().addAll(partyRegistrations);
        request.setInvolvedParty(individual);
        request.setRequestHeader(new RequestHeader());
        request.getRequestHeader().setDatasourceName(EXT_SYS_SALSA);
        return request;
    }

    private InvolvedPartyObjectReference getInvolvedPartyObjectReference(String customerIdentifier) {
        InvolvedPartyObjectReference objectReference = new InvolvedPartyObjectReference();
        if (!StringUtils.isEmpty(customerIdentifier)) {
            objectReference.setIdentifier(customerIdentifier);
        }
        AlternateID alternateID = new AlternateID();
        alternateID.setExternalSystemIdentifier(EXT_SYS_SALSA);
        objectReference.getAlternateId().add(alternateID);
        return objectReference;
    }

    private RecordInvolvedPartyDetailsRequest setRegistrationDetailsForOnCRS(List<IdentificationDetails> identificationDetailsList, List<MaintenanceAuditData> maintenanceAuditListTaxRegistration, List<TaxRegistration> taxRegistrationList, List<PartyRegistration> partyRegistrations) {
        RecordInvolvedPartyDetailsRequest request = new RecordInvolvedPartyDetailsRequest();
        if (!CollectionUtils.isEmpty(taxRegistrationList)) {
            if (!CollectionUtils.isEmpty(identificationDetailsList)) {
                for (TaxRegistration taxRegistration : taxRegistrationList) {
                    for (IdentificationDetails identificationDetails : identificationDetailsList) {
                        if (taxRegistration.getIsIssuedIn().getName().equalsIgnoreCase(identificationDetails.getCountryCode())) {
                            if (!taxRegistration.getTaxIdentificationNumber().getDescription().equalsIgnoreCase(identificationDetails.getValue())) {
                                TaxIdentificationNumberType taxIdentificationNumberType = taxRegistrationFactory.getTaxIdentificationNumberType(taxRegistration.getTaxIdentificationNumber().getDescription(), USA_TAX_IDENTIFICATION_NUMBER);
                                TaxRegistration taxRegistration1 = taxRegistrationFactory.setTaxPayID(taxIdentificationNumberType, identificationDetails.getCountryCode(), maintenanceAuditListTaxRegistration);
                                partyRegistrations.add(taxRegistration1);
                                partyRegistrations.add(getTaxRegistrationCreate(maintenanceAuditListTaxRegistration, identificationDetails.getValue(), taxIdentificationNumberType, taxRegistration1));
                                AdjustmentCondition adjustmentCondition = taxRegistrationFactory.getAdjustmentCondition();
                                List<Condition> commandAction = new ArrayList<>();
                                commandAction.add(adjustmentCondition);
                                request.getControlCommand().addAll(commandAction);
                            }
                        }
                    }
                }
            }
        } else if (!CollectionUtils.isEmpty(identificationDetailsList)) {
            for (IdentificationDetails identificationDetails : identificationDetailsList) {
                TaxIdentificationNumberType taxIdentificationNumberType = taxRegistrationFactory.getTaxIdentificationNumberType(USA_TAX_IDENTIFICATION_NUMBER, identificationDetails.getValue());
                TaxRegistration taxRegistration1 = taxRegistrationFactory.setTaxPayID(taxIdentificationNumberType, identificationDetails.getCountryCode(), maintenanceAuditListTaxRegistration);
                partyRegistrations.add(taxRegistration1);
                partyRegistrations.add(getTaxRegistrationCreate(maintenanceAuditListTaxRegistration, identificationDetails.getValue(), taxIdentificationNumberType, taxRegistration1));
            }
        }
        return request;
    }

    private TaxRegistration getTaxRegistrationCreate(List<MaintenanceAuditData> maintenanceAuditListTaxRegistration, String description, TaxIdentificationNumberType taxIdentificationNumberType, TaxRegistration taxRegistration1) {
        TaxRegistration taxRegistrationCreate = taxRegistrationFactory.copyTaxRegistration(taxRegistration1);
        TaxIdentificationNumberType taxIdentificationNumberTypeCreate = taxRegistrationFactory.copyTaxIdentificationNumberType(taxIdentificationNumberType);
        taxIdentificationNumberTypeCreate.setDescription(description);
        taxRegistrationCreate.setTaxIdentificationNumber(taxIdentificationNumberTypeCreate);
        if (taxRegistrationFactory.hasMaintenanceAuditData(maintenanceAuditListTaxRegistration)) {
            taxRegistrationCreate.getMaintenanceAuditData().addAll(maintenanceAuditListTaxRegistration);
        }
        return taxRegistrationCreate;
    }

    private String setTaxData(Registration registration, List<MaintenanceAuditData> maintenanceAuditListTaxRegistration, List<TaxRegistration> taxRegistrationList) {
        TaxRegistration taxRegistration = (TaxRegistration) registration;
        String retrievePartyResponseTIN = null;
        if (taxRegistration.getIsIssuedIn() != null && taxRegistration.getIsIssuedIn().getName() != null && taxRegistration.getIsIssuedIn().getName().equalsIgnoreCase(TAX_REGISTRATION_PLACE)) {
            if (taxRegistration.getTaxIdentificationNumber() != null && taxRegistration.getTaxIdentificationNumber().getName() != null && taxRegistration.getTaxIdentificationNumber().getName().equalsIgnoreCase(USA_TAX_IDENTIFICATION_NUMBER)) {
                List<MaintenanceAuditData> maintenanceAuditDatas = registration.getMaintenanceAuditData();
                retrievePartyResponseTIN = taxRegistration.getDescription();
                if (taxRegistrationFactory.hasMaintenanceAuditData(maintenanceAuditDatas)) {
                    maintenanceAuditListTaxRegistration.add(maintenanceAuditDatas.get(0));
                }
            }
        }
        taxRegistrationList.add(taxRegistration);
        return retrievePartyResponseTIN;
    }

    private RecordInvolvedPartyDetailsRequest setTaxRegistrationDetailsForOffCRS(TaxResidencyDetails taxResidencyDetails, List<PartyRegistration> partyRegistrations, String retrievePartyResponseTIN, List<MaintenanceAuditData> maintenanceAuditListTaxRegistration) {
        RecordInvolvedPartyDetailsRequest request = new RecordInvolvedPartyDetailsRequest();
        if (taxResidencyDetails != null && taxResidencyDetails.getTaxPayerIdNumber() != null) {
            if (!StringUtils.isEmpty(retrievePartyResponseTIN)) {
                if (!retrievePartyResponseTIN.equalsIgnoreCase(taxResidencyDetails.getTaxPayerIdNumber())) {
                    TaxIdentificationNumberType taxIdentificationNumberType = taxRegistrationFactory.getTaxIdentificationNumberType(USA_TAX_IDENTIFICATION_NUMBER, retrievePartyResponseTIN);
                    TaxRegistration taxRegistration = taxRegistrationFactory.setTaxPayID(taxIdentificationNumberType, TAX_REGISTRATION_PLACE, maintenanceAuditListTaxRegistration);
                    partyRegistrations.add(taxRegistration);
                    partyRegistrations.add(getTaxRegistrationCreate(maintenanceAuditListTaxRegistration, taxResidencyDetails.getTaxPayerIdNumber(), taxRegistration.getTaxIdentificationNumber(), taxRegistration));
                    AdjustmentCondition adjustmentCondition = taxRegistrationFactory.getAdjustmentCondition();
                    request.getControlCommand().add(adjustmentCondition);
                }
            } else {
                TaxIdentificationNumberType taxIdentificationNumberType = taxRegistrationFactory.getTaxIdentificationNumberType(USA_TAX_IDENTIFICATION_NUMBER, taxResidencyDetails.getTaxPayerIdNumber());
                TaxRegistration taxRegistration = taxRegistrationFactory.setTaxPayID(taxIdentificationNumberType, TAX_REGISTRATION_PLACE, maintenanceAuditListTaxRegistration);
                partyRegistrations.add(taxRegistration);
            }
        }
        return request;
    }

    public void setNationalRegistration(String nationality, String registrationType, List<MaintenanceAuditData> maintenanceAuditDatas, List<PartyRegistration> partyRegistrations) {
        if (!StringUtils.isEmpty(nationality)) {
            NationalRegistration nationalRegistration = new NationalRegistration();
            nationalRegistration.setCountryCode(nationality);
            nationalRegistration.setRegistrationType(new RegistrationType());
            nationalRegistration.getRegistrationType().setName(registrationType);
            if (taxRegistrationFactory.hasMaintenanceAuditData(maintenanceAuditDatas)) {
                nationalRegistration.getMaintenanceAuditData().addAll(maintenanceAuditDatas);
            }
            partyRegistrations.add(nationalRegistration);
        }
    }
}