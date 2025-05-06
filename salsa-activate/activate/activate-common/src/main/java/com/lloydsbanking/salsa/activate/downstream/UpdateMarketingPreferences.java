package com.lloydsbanking.salsa.activate.downstream;


import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.converter.F060RequestFactory;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.ocis.client.f060.F060Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Req;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class UpdateMarketingPreferences {
    public static final String SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/OCIS/F060_AddCommsPrefPartner";
    public static final String ACTION_F060 = "F060";
    private static final Logger LOGGER = Logger.getLogger(UpdateMarketingPreferences.class);

    @Autowired
    F060RequestFactory f060RequestFactory;
    @Autowired
    F060Client f060Client;
    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateAppDetails;

    public void marketingPreferencesUpdate(RequestHeader header, ProductArrangement productArrangement, ApplicationDetails applicationDetails) {
        LOGGER.info("Entering MarketingPreference (OCIS F060)");
        F060Req f060Req = f060RequestFactory.convert(productArrangement);
        try {
            invokeF060(header, f060Req);
        } catch (WebServiceException e) {
            LOGGER.info("Resource Not Available Exception thrown while calling F060, this Exception is consumed:", e);
            updateAppDetails.setApplicationDetails(productArrangement.getRetryCount(), null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.MARKETING_PREF_UPDATE_FAILURE, applicationDetails);
        }
        productArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
    }

    private void invokeF060(RequestHeader header, F060Req f060Req) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), SERVICE_NAME, ACTION_F060);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        f060Client.f060(f060Req, contactPoint, serviceRequest, securityHeaderType);
    }

}
