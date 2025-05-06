package com.lloydsbanking.salsa.offer.apply.convert;

import com.lloydsbanking.salsa.downstream.asm.client.f204.AddressDetailsBuilder;
import com.lloydsbanking.salsa.downstream.asm.client.f204.F204RequestBuilder;
import com.lloydsbanking.salsa.downstream.asm.client.f204.PersonalDetailsBuilder;
import com.lloydsbanking.salsa.soap.asm.f204.objects.AddressDetails;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Req;
import com.lloydsbanking.salsa.soap.asm.f204.objects.PersonalDetails;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_bo.businessobjects.PostalAddress;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class RetrieveFraudDecisionRequestFactory {

    public F204Req create(String regionCode, String areaCode, String contactPointId, List<ExtSysProdIdentifier> extSysProdIdentifier, String creditScoreRequestNumber, List<IndividualName> individualNameList
            , Customer primaryInvolvedParty, List<PostalAddress> postalAddressList) {
        F204RequestBuilder builder = new F204RequestBuilder();
        F204Req f204Req = builder.defaults()
                .regionCode(regionCode)
                .areaCode(areaCode)
                .sortCode(contactPointId)
                .creditScoreRequestNumber(creditScoreRequestNumber)
                .productId(extSysProdIdentifier)
                .build();
        f204Req.getPersonalDetails().addAll(retrievePersonalDetails(individualNameList, primaryInvolvedParty));
        if (!f204Req.getPersonalDetails().isEmpty()) {
            f204Req.getPersonalDetails().get(0).getAddressDetails().addAll(retrieveAddressDetails(postalAddressList));
        }
        return f204Req;
    }

    private List<AddressDetails> retrieveAddressDetails(List<PostalAddress> postalAddressList) {
        List<AddressDetails> addressDetailsList = new ArrayList<>();
        for (PostalAddress postalAddress : postalAddressList) {
            AddressDetailsBuilder builder = new AddressDetailsBuilder();
            builder.defaults();
            if (postalAddress != null) {
                builder.address(postalAddress);
                if (postalAddress.getDurationofStay() != null) {
                    builder.addressResidenceDr(Short.parseShort(postalAddress.getDurationofStay())).build();
                }
                addressDetailsList.add(builder.build());
            }
        }
        return addressDetailsList;
    }


    private List<PersonalDetails> retrievePersonalDetails(List<IndividualName> individualNameList, Customer primaryInvolvedParty) {
        List<PersonalDetails> personalDetailsList = new ArrayList<>();
        for (IndividualName individualName : individualNameList) {
            PersonalDetailsBuilder builder = new PersonalDetailsBuilder();
            int customerIdentifier = 0;
            String employmentStatus = null;
            if (primaryInvolvedParty != null) {
                customerIdentifier = primaryInvolvedParty.getCustomerIdentifier() != null ? Integer.parseInt(primaryInvolvedParty.getCustomerIdentifier()) : 0;
                builder.partyIdentifiers(primaryInvolvedParty.getSourceSystemId(), primaryInvolvedParty.getCidPersID(), primaryInvolvedParty.getCbsCustomerNumber());
                builder.partyBusinessRltnspCd(primaryInvolvedParty.getCustomerSegment());
                if (primaryInvolvedParty.getIsPlayedBy() != null) {
                    builder.birthDate(convertXmlDateToUtilDate(primaryInvolvedParty.getIsPlayedBy().getBirthDate()));
                    builder.ukResidencyPermissionExpiryDt(convertXmlDateToUtilDate(primaryInvolvedParty.getIsPlayedBy().getVisaExpiryDate()));
                    if (primaryInvolvedParty.getIsPlayedBy().getEmploymentStatus() != null) {
                        employmentStatus = primaryInvolvedParty.getIsPlayedBy().getEmploymentStatus();
                    }
                }
            }
            builder.partyIdAndEmploymentStatusCd(customerIdentifier, employmentStatus);
            builder.middleNames(individualName.getMiddleNames())
                    .partyTi(individualName.getPrefixTitle())
                    .name(individualName.getFirstName(), individualName.getLastName());
            PersonalDetails personalDetails = builder.build();
            personalDetailsList.add(personalDetails);
        }
        return personalDetailsList;
    }

    private Date convertXmlDateToUtilDate(XMLGregorianCalendar xmlDate) {
        if (xmlDate == null) {
            return null;
        } else {
            return xmlDate.toGregorianCalendar().getTime();
        }
    }


}
