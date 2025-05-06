package com.lloydsbanking.salsa.ppae.service.convert;


import com.lloydsbanking.salsa.soap.pad.f263.objects.AIdentifiers;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Req;
import lib_sim_bo.businessobjects.Customer;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class F263RequestFactory {

    private static final Logger LOGGER = Logger.getLogger(F263RequestFactory.class);

    private static final String SOURCE_CODE = "009";

    private static final String LOAN_NUMBER = "0";

    public F263Req createF263Req(Customer primaryInvolvedParty) {
        F263Req f263Req = new F263Req();
        AIdentifiers aIdentifiers = new AIdentifiers();
        if (null != primaryInvolvedParty && !primaryInvolvedParty.getCustomerScore().isEmpty() && null != primaryInvolvedParty.getCustomerScore().get(0)) {
            aIdentifiers.setRequestNo(primaryInvolvedParty.getCustomerScore().get(0).getScoreIdentifier());
        }
        aIdentifiers.setLoanAgreementNo(LOAN_NUMBER);
        aIdentifiers.setSourceSystemCd(SOURCE_CODE);
        f263Req.setAIdentifiers(aIdentifiers);
        return f263Req;
    }
}
