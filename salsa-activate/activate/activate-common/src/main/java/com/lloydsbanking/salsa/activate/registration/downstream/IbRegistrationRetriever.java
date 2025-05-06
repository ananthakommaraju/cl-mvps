package com.lloydsbanking.salsa.activate.registration.downstream;

import com.lloydsbanking.salsa.activate.registration.converter.B750RequestResponseConverter;
import com.lloydsbanking.salsa.downstream.application.client.ApplicationClient;
import com.lloydstsb.ib.wsbridge.application.StB750AAppPerCCRegCreate;
import com.lloydstsb.ib.wsbridge.application.StB750BAppPerCCRegCreate;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.WebServiceException;

@Component
public class IbRegistrationRetriever {
    @Autowired
    B750RequestResponseConverter b750RequestResponseConverter;
    @Autowired
    ApplicationClient applicationClient;

    private static final Logger LOGGER = Logger.getLogger(IbRegistrationRetriever.class);

    private static final int NO_ERROR = 0;

    public void registerForInternetBanking(RequestHeader header, Customer primaryInvolvedParty, String accountNumber, int marketingPreferenceIndicator, String prodType) {
        StB750AAppPerCCRegCreate b750Request = b750RequestResponseConverter.createB750Request(header, primaryInvolvedParty, accountNumber, marketingPreferenceIndicator, prodType);
        StB750BAppPerCCRegCreate b750Response = serviceCallToB750(b750Request);
        if (null != b750Response.getSterror() && NO_ERROR != b750Response.getSterror().getErrorno()) {
            LOGGER.info("Error occured in B750. Error Code | Error Text: " + b750Response.getSterror().getErrorno() + " | " + b750Response.getSterror().getErrormsg());
        } else {
            b750RequestResponseConverter.mapB750ResponseAttributesToProductArrangement(primaryInvolvedParty, b750Response);
            LOGGER.info("Exiting Create IB Application (B750) AppId | AppVer: " + primaryInvolvedParty.getIsRegisteredIn().getRegistrationIdentifier() + " | " + primaryInvolvedParty.getIsRegisteredIn().getApplicationVersion());
        }
    }

    private StB750BAppPerCCRegCreate serviceCallToB750(StB750AAppPerCCRegCreate b750Request) {
        StB750BAppPerCCRegCreate b750Response = new StB750BAppPerCCRegCreate();
        try {
            b750Response = applicationClient.createIBApplication(b750Request);
        } catch (WebServiceException e) {
            LOGGER.info("Resource Not Available Error occurred while calling B750. Exception consumed ", e);

        }
        return b750Response;
    }


}
