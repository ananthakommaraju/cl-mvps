package com.lloydsbanking.salsa.offer.apply.convert;

import com.lloydsbanking.salsa.downstream.asm.client.f424.ApplicationDetailsBuilder;
import com.lloydsbanking.salsa.downstream.asm.client.f424.DecisionPersonalDetailsBuilder;
import com.lloydsbanking.salsa.downstream.asm.client.f424.F424RequestBuilder;
import com.lloydsbanking.salsa.downstream.asm.client.f424.IncomeExpenditureDetailsBuilder;
import com.lloydsbanking.salsa.soap.asm.f424.objects.ApplicationDetails;
import com.lloydsbanking.salsa.soap.asm.f424.objects.DecisionPersonalDetails;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Req;
import com.lloydsbanking.salsa.soap.asm.f424.objects.IncomeExpenditureDetails;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Individual;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

@Component
public class RetrieveCreditDecisionRequestFactory {
    private static final Logger LOGGER = Logger.getLogger(RetrieveCreditDecisionRequestFactory.class);

    public F424Req create(String contactPointId, String arrangementId, String guaranteedOfferCode, String productIdentifier,
                          String productEligibilityTypeCode, String subChannelCode, String affiliateIdentifier, Customer primaryInvolvedParty,
                          String arrangementType, Boolean marketingPreferenceIndicator, String directDebitIn, CurrencyAmount totalBalTrnsfrAmt) {
        LOGGER.info("Offer: OPACC: Entering  RetrieveCreditDecisionRequestFactory.create()");
        F424RequestBuilder builder = new F424RequestBuilder();
        F424Req f424Req = builder.build();
        if (primaryInvolvedParty != null) {
            boolean isAuthCustomer = primaryInvolvedParty.isIsAuthCustomer() != null ? primaryInvolvedParty.isIsAuthCustomer() : false;
            LOGGER.info("Offer: OPACC: Entering  RetrieveCreditDecisionRequestFactory.create():isAuthCustomer: " + isAuthCustomer);
            f424Req.getDecisionPersonalDetails().add(retrieveDecisionPersonalDetails(primaryInvolvedParty));
            f424Req = builder
                    .defaults()
                    .creditScoreRequestNoAndSortCd(arrangementId, contactPointId)
                    .addressDetailsWithResolution(primaryInvolvedParty.getPostalAddress(), isAuthCustomer)
                    .build();
        }
        //.addressDetailsWithResolutionCheckingPAFFormat(primaryInvolvedParty.getPostalAddress())
        f424Req.setApplicationDetails(retrieveApplicationDetails(primaryInvolvedParty, productIdentifier, productEligibilityTypeCode, arrangementType, marketingPreferenceIndicator, directDebitIn, totalBalTrnsfrAmt, guaranteedOfferCode, affiliateIdentifier, subChannelCode));
        f424Req.getDecisionPersonalDetails().get(0).setIncomeExpenditureDetails(retrieveIncomeExpenditureDetails(primaryInvolvedParty));
        return f424Req;
    }

    private ApplicationDetails retrieveApplicationDetails(Customer primaryInvolvedParty, String productIdentifier, String productEligibilityTypeCode, String arrangementType, Boolean marketingPreferenceIndicator, String directDebitIn, CurrencyAmount totalBalTrnsfrAmt, String guaranteedOfferCode, String affiliateIdentifier, String subChannelCode) {
        ApplicationDetails applicationDetails = null;
        if (primaryInvolvedParty != null) {
            boolean booleanMarketingPreferenceIndicator = marketingPreferenceIndicator != null ? marketingPreferenceIndicator : false;
            applicationDetails = new ApplicationDetailsBuilder().defaults()
                    .productId(productIdentifier)
                    .csSaleChannelCdAndProductIntroducerCd(subChannelCode, affiliateIdentifier)
                    .guaranteedProductMailingCd(guaranteedOfferCode)
                    .emailAddressIn(primaryInvolvedParty.getEmailAddress())
                    .guaranteedCreditCardLimitAm(guaranteedOfferCode)
                    .indicators(booleanMarketingPreferenceIndicator, directDebitIn, totalBalTrnsfrAmt)
                    .returnedChequesIn(productEligibilityTypeCode)
                    .build();
        }
        return applicationDetails;
    }

    private IncomeExpenditureDetails retrieveIncomeExpenditureDetails(Customer primaryInvolvedParty) {
        IncomeExpenditureDetailsBuilder incomeExpenditureDetailsBuilder = new IncomeExpenditureDetailsBuilder();
        if (primaryInvolvedParty != null && primaryInvolvedParty.getIsPlayedBy() != null) {
            incomeExpenditureDetailsBuilder.loanCmmtmntMnthlyAm(primaryInvolvedParty.getIsPlayedBy().getMonthlyLoanRepaymentAmount());
            incomeExpenditureDetailsBuilder.mnthlyAccmmnPaymntAm(primaryInvolvedParty.getIsPlayedBy().getMonthlyMortgageAmount());
            incomeExpenditureDetailsBuilder.grossAnnualIncomeAm(primaryInvolvedParty.getIsPlayedBy().getGrossAnnualIncome());
            incomeExpenditureDetailsBuilder.periodNetIncome(primaryInvolvedParty.getIsPlayedBy().getNetMonthlyIncome());
        }
        return incomeExpenditureDetailsBuilder.build();

    }

    private DecisionPersonalDetails retrieveDecisionPersonalDetails(Customer primaryInvolvedParty) {
        DecisionPersonalDetailsBuilder decisionPersonalDetailsBuilder = new DecisionPersonalDetailsBuilder();
        Individual isPlayedBy = primaryInvolvedParty.getIsPlayedBy();
        boolean isStaffMember = isPlayedBy.isIsStaffMember() != null ? isPlayedBy.isIsStaffMember() : false;
        boolean hasExistingCreditCard = primaryInvolvedParty.isHasExistingCreditCard() != null ? primaryInvolvedParty.isHasExistingCreditCard() : false;
        String currentEmployerName = isPlayedBy.getCurrentEmployer() != null ? isPlayedBy.getCurrentEmployer().getName() : null;
        decisionPersonalDetailsBuilder.defaults()
                .name(isPlayedBy.getIndividualName())
                .personalDetails(isPlayedBy.getBirthDate(), primaryInvolvedParty.getCustomerSegment(), isPlayedBy.getMaritalStatus(), isPlayedBy.getEmploymentStatus())
                .employerDetails(currentEmployerName, isPlayedBy.getCurrentEmploymentDuration(), isPlayedBy.getPreviousEmploymentDuration(), isPlayedBy.getGender())
                .partyDetails(primaryInvolvedParty.getSourceSystemId(), primaryInvolvedParty.getCidPersID(), primaryInvolvedParty.getCbsCustomerNumber(), primaryInvolvedParty.getCustomerIdentifier(), isStaffMember, isPlayedBy.getNumberOfDependents())
                .bankDetailsAndOtherBankAssctnDr(primaryInvolvedParty.getExistingSortCode(), primaryInvolvedParty.getExistingAccountNumber(), primaryInvolvedParty.getExistingAccountDuration())
                .phoneAreaCd(primaryInvolvedParty.getTelephoneNumber())
                .storeCardHeldInAndResidentialStatusCd(hasExistingCreditCard, primaryInvolvedParty.getIsPlayedBy().getResidentialStatus());
        return decisionPersonalDetailsBuilder.build();
    }

    private Date convertXMLGregorianCalenderToDate(XMLGregorianCalendar xmlDate) {
        if (xmlDate != null) {
            return xmlDate.toGregorianCalendar().getTime();
        }
        return null;
    }
}
