package com.lloydsbanking.salsa.activate.registration;


import com.lloydsbanking.salsa.activate.registration.downstream.IbRegistrationRetriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegistrationService {

    @Autowired
    IbRegistrationRetriever ibRegistrationRetriever;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    private static final Logger LOGGER = Logger.getLogger(RegistrationService.class);

    private static final String ARRANGEMENT_TYPE_SAVINGS = "SA";

    private static final String PRODUCT_TYPE_FOR_SAVINGS = "A";

    private static final String PRODUCT_TYPE_FOR_DEFAULT = "C";

    private static final int MARKETING_INDICATOR_ON = 1;

    private static final int MARKETING_INDICATOR_OFF = 0;

    public void serviceCallForIBRegistration(RequestHeader header, ProductArrangement productArrangement) {
        LOGGER.info("is Registration Selected?");
        if (productArrangement.getPrimaryInvolvedParty().isIsRegistrationSelected() != null && productArrangement.getPrimaryInvolvedParty().isIsRegistrationSelected() &&
                boundaryConditionForRegistrationIdentifier(productArrangement)) {
            createIBApplication(header, productArrangement);
        }
    }

    private void createIBApplication(RequestHeader header, ProductArrangement productArrangement) {
        Customer primaryInvolvedParty = extractPrimaryInvolvedPartyFromProductArrangement(productArrangement);
        int marketingPreferenceIndicator = getPreferenceMarketingIndicator(productArrangement);
        String prodType = getProdType(productArrangement);
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(productArrangement, "Entering Create IB Application (B750)"));
        ibRegistrationRetriever.registerForInternetBanking(header, primaryInvolvedParty, productArrangement.getAccountNumber(), marketingPreferenceIndicator, prodType);
    }

    private String getProdType(ProductArrangement productArrangement) {
        if (ARRANGEMENT_TYPE_SAVINGS.equals(productArrangement.getArrangementType())) {
            return PRODUCT_TYPE_FOR_SAVINGS;
        } else {
            return PRODUCT_TYPE_FOR_DEFAULT;
        }
    }

    private int getPreferenceMarketingIndicator(ProductArrangement productArrangement) {
        if (productArrangement instanceof FinanceServiceArrangement) {
            Boolean isMarketingPreferenceIndicator = ((FinanceServiceArrangement) productArrangement).isMarketingPrefereceIndicator();
            if (null != isMarketingPreferenceIndicator && isMarketingPreferenceIndicator) {
                return MARKETING_INDICATOR_ON;
            } else {
                return MARKETING_INDICATOR_OFF;
            }
        }
        return MARKETING_INDICATOR_OFF;
    }


    private Customer extractPrimaryInvolvedPartyFromProductArrangement(ProductArrangement productArrangement) {
        Customer customer = new Customer();
        if (null != productArrangement.getPrimaryInvolvedParty()) {
            customer = productArrangement.getPrimaryInvolvedParty();
        }
        return customer;
    }


    private boolean boundaryConditionForRegistrationIdentifier(ProductArrangement productArrangement) {
        return (null == productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn() ||
                null == productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getRegistrationIdentifier());
    }

}
