package com.lloydsbanking.salsa.eligibility.service.rules.cst;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.KycStatusRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;


public class CR026KnowYourCustomerRule implements CSTEligibilityRule {


    @Autowired
    KycStatusRetriever kycStatusRetriever;
    private static final boolean CHECK_FOR_PARTY_ID_EVIDENCE_STATUS = false;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode,
                                        String customerId) throws EligibilityException {

        String kycStatus;
        try {
            kycStatus = kycStatusRetriever.getKycStatus(ruleDataHolder.getHeader(), null, null, CHECK_FOR_PARTY_ID_EVIDENCE_STATUS);
        } catch (SalsaExternalBusinessException | SalsaInternalResourceNotAvailableException | SalsaInternalServiceException e) {
            throw new EligibilityException(e);
        }

        if (!"F".equalsIgnoreCase(kycStatus)) {
            return new EligibilityDecision(DeclineReasons.CR026_DECLINE_REASON);
        }
        return new EligibilityDecision(true);
    }

}
