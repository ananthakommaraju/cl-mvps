package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.brand.ChannelType;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityPAMRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityRefDataRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.KycStatusRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.KYCStatus;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.EligibilityServiceConstants;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.PostalAddress;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public class CR066UKResidencyCheckRule implements CSTEligibilityRule {
    @Autowired
    EligibilityRefDataRetriever refDataRetriever;

    @Autowired
    EligibilityPAMRetriever pamRetriever;

    @Autowired
    KycStatusRetriever kycStatusRetriever;

    private static final boolean IS_CHECK_FOR_PARTY_ID_EVIDENCE_STATUS = true;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) throws EligibilityException {

        String retrievedKYCStatus = null;
        Customer customer = ruleDataHolder.getCustomerDetails();
        if (null != ruleDataHolder.getArrangementType() && !EligibilityServiceConstants.ARRANGEMENT_TYPE_CREDITCARD.equals(ruleDataHolder.getArrangementType()) && null != customer.getInternalUserIdentifier() && !"1".equals(customer
                .getInternalUserIdentifier())) {
            if (Mnemonics.LIFE_INSURANCE.equalsIgnoreCase(ruleDataHolder.getRuleInsMnemonic())) {
                List<String> lookUpValues = pamRetriever.getLookUpValues(ruleDataHolder.getHeader().getChannelId());
                retrievedKYCStatus = getKYCStatus(ruleDataHolder, customerId, lookUpValues);
            }
        }

        Channel channel = Channel.getChannelFor(Brand.fromString(ruleDataHolder.getChannel()), ChannelType.PERSONAL);
        boolean restrictedPostCode = isRestrictedPostCode(channel, customer);
        if (!restrictedPostCode && !BooleanUtils.isTrue(customer.isForeignAddressIndicator())) {
            return new EligibilityDecision(true, new KYCStatus(retrievedKYCStatus));
        }
        return new EligibilityDecision(DeclineReasons.CR066_DECLINE_REASON, new KYCStatus(retrievedKYCStatus));

    }

    private String getKYCStatus(RuleDataHolder ruleDataHolder, String customerId, List<String> lookUpValues) throws EligibilityException {
        String retrievedKYCStatus;
        try {
            retrievedKYCStatus = kycStatusRetriever.getKycStatus(ruleDataHolder.getHeader(), customerId, lookUpValues, IS_CHECK_FOR_PARTY_ID_EVIDENCE_STATUS);
        }
        catch (SalsaInternalResourceNotAvailableException | SalsaInternalServiceException | SalsaExternalBusinessException e) {
            throw new EligibilityException(e);
        }
        return retrievedKYCStatus;
    }

    private boolean isRestrictedPostCode(Channel channel, Customer customer) {
        List<String> lookupTexts = refDataRetriever.retrieveRestrictedPostCode(channel.asString());
        if (null != lookupTexts && !lookupTexts.isEmpty()) {
            PostalAddress postalAddress = customer.getPostalAddress().get(0);
            if (null != postalAddress && null != postalAddress.getUnstructuredAddress() && !StringUtils.isEmpty(postalAddress.getUnstructuredAddress().getPostCode())) {
                for (String lookUpTxt : lookupTexts) {
                    String postCode = postalAddress.getUnstructuredAddress().getPostCode().substring(0, 2);
                    return postCode.equalsIgnoreCase(lookUpTxt);
                }
            }
        }
        return false;
    }
}
