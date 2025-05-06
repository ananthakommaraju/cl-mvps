package com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate;


import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.PersonalUpdDataBuilder;
import com.lloydsbanking.salsa.offer.ApplicantType;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.MailMktType;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.PersonalUpdDataType;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.PhoneMktType;
import lib_sim_bo.businessobjects.Customer;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.util.Date;

public class EvaluatePersonalUpdData {

    private static final String INDICATOR_TYPE_YES = "Y";

    private static final String INDICATOR_TYPE_NO = "N";

    public PersonalUpdDataType generatePersonalUpdData(String arrangementType, Customer primaryInvolvedParty, boolean marketingPref) throws ParseException {
        PersonalUpdDataBuilder personalUpdDataBuilder = new PersonalUpdDataBuilder();
        PersonalUpdDataType personalUpdDataType;
        personalUpdDataType = personalUpdDataBuilder.birthDtGenderCdPartyTypeCd(getDateInDdMMYyyyFormat(primaryInvolvedParty.getIsPlayedBy()
                .getBirthDate()), primaryInvolvedParty.getIsPlayedBy().getGender())
                .namesAndPartyTI(primaryInvolvedParty.getIsPlayedBy().getIndividualName().get(0).getPrefixTitle(), primaryInvolvedParty.getIsPlayedBy()
                        .getIndividualName()
                        .get(0)
                        .getLastName(), primaryInvolvedParty.getIsPlayedBy().getIndividualName().get(0).getFirstName(), primaryInvolvedParty.getIsPlayedBy()
                        .getIndividualName()
                        .get(0)
                        .getMiddleNames())
                .build();
        setMarketingPref(primaryInvolvedParty.getApplicantType(), arrangementType, personalUpdDataType, marketingPref);
        return personalUpdDataType;
    }

    private void setMarketingPref(String applicantType, String arrangementType, PersonalUpdDataType personalUpdDataType, boolean marketingPref) {
        personalUpdDataType.setPhoneMkt(new PhoneMktType());
        personalUpdDataType.setMailMkt(new MailMktType());
        if (ArrangementType.SAVINGS.getValue().equalsIgnoreCase(arrangementType)) {
            if (!(ApplicantType.DEPENDENT.getValue()).equals(applicantType)) {

                if (marketingPref) {
                    personalUpdDataType.getMailMkt().setMktAuthMailIn(INDICATOR_TYPE_YES);
                    personalUpdDataType.getPhoneMkt().setMktAuthPhoneIn(INDICATOR_TYPE_YES);
                }
                else {
                    personalUpdDataType.getMailMkt().setMktAuthMailIn(INDICATOR_TYPE_NO);
                    personalUpdDataType.getPhoneMkt().setMktAuthPhoneIn(INDICATOR_TYPE_NO);
                }
            }
        }
    }

    private Date getDateInDdMMYyyyFormat(XMLGregorianCalendar date) throws ParseException {
        return DateFactory.toDate(date);
    }

}
