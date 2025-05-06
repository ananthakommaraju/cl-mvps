package com.lloydsbanking.salsa.eligibility.service.rules.cst;

import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.ChannelSpecificArrangements;
import com.lloydsbanking.salsa.eligibility.service.downstream.ShadowLimitRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.fs.user.StAccountListDetail;
import lb_gbo_sales.messages.RequestHeader;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public class CR023ShadowLimitRule implements CSTEligibilityRule {
    @Autowired
    ShadowLimitRetriever shadowLimitRetriever;

    @Autowired
    AppGroupRetriever appGroupRetriever;

    @Autowired
    ChannelSpecificArrangements channelSpecificArrangements;

    @Autowired
    ChannelToBrandMapping channelToBrandMapping;

    private static final boolean IS_WZ_REQUEST = false;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) throws EligibilityException {

        RequestHeader header = ruleDataHolder.getHeader();

        String brand = channelToBrandMapping.getBrandForChannel(header.getChannelId()).equals("VER") ? "VTB" : channelToBrandMapping.getBrandForChannel(header.getChannelId());

        String shadowLimit;
        try {
            shadowLimit = shadowLimitRetriever.getShadowLimit(header, sortCode, customerId, appGroupRetriever.callRetrieveCBSAppGroup(header, sortCode, IS_WZ_REQUEST));
        }
        catch (SalsaInternalServiceException | SalsaInternalResourceNotAvailableException e) {
            throw new EligibilityException(e);
        }

        float thresholdAmount = Float.valueOf(ruleDataHolder.getRuleParamValue());
        if (Double.valueOf(shadowLimit) > 0.0 && Double.valueOf(shadowLimit) < thresholdAmount) {
            if (evaluateProductList(ruleDataHolder, brand)) {
                return new EligibilityDecision(DeclineReasons.CR023_DECLINE_REASON + ruleDataHolder.getRuleParamValue());
            }
        }
        return new EligibilityDecision(true);
    }

    private boolean evaluateProductList(RuleDataHolder ruleDataHolder, String brand) throws EligibilityException {
        final List<StAccountListDetail> productDetails;
        try {
            productDetails = this.channelSpecificArrangements.getChannelSpecificArrangements(ruleDataHolder.getHeader());
        }
        catch (SalsaInternalServiceException | SalsaInternalResourceNotAvailableException e) {
            throw new EligibilityException(e);
        }

        for (StAccountListDetail productArrangement : productDetails) {

            if (productArrangement.getAccountcategory().equals("C") && productArrangement.getBrandcode().trim().equalsIgnoreCase(brand)) {
                return true;
            }
        }
        return false;
    }

}
