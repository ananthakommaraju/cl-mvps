package com.lloydsbanking.salsa.activate.registration.downstream;

import com.lloydsbanking.salsa.activate.postfulfil.downstream.CommunicatePostFulfilmentActivities;
import com.lloydsbanking.salsa.activate.registration.converter.B751RequestFactory;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.application.client.ApplicationClient;
import com.lloydstsb.ib.wsbridge.application.StB751AAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.InternetBankingProfile;
import lib_sim_bo.businessobjects.InternetBankingRegistration;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.xml.ws.WebServiceException;

@Repository
public class ActivateIBApplication {
    private static final Logger LOGGER = Logger.getLogger(ActivateIBApplication.class);

    @Autowired
    B751RequestFactory b751RequestFactory;
    @Autowired
    ApplicationClient applicationClient;
    @Autowired
    CommunicatePostFulfilmentActivities communicatePostFulfilmentActivities;

    public StB751BAppPerCCRegAuth retrieveActivateIBApplication(ProductArrangement productArrangement, RequestHeader header) {
        LOGGER.info("Entering ActivateIB Application(BAPI B751) with appId: " + productArrangement.getArrangementId());
        StB751BAppPerCCRegAuth b751Response = null;
        StB751AAppPerCCRegAuth b751Request = b751RequestFactory.convert(productArrangement, header);
        try {
            b751Response = applicationClient.createAppPerCCRegAuth(b751Request);

            if (b751Response != null && b751Response.getSterror() != null && b751Response.getSterror().getErrorno() != 0) {
                LOGGER.info("Technical Error Occurred in B751. Error Code: " + String.valueOf(b751Response.getSterror().getErrorno()));
            } else {
                if (b751Response != null && null != b751Response.getPartyidEmergingChannelUserId()) {
                    if (productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getProfile() == null) {
                        productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().setProfile(new InternetBankingProfile());
                    }
                    productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getProfile().setUserName(b751Response.getPartyidEmergingChannelUserId());
                    LOGGER.info("Exiting ActivateIB Application(BAPI B751) with userId: " + b751Response.getPartyidEmergingChannelUserId());
                    getUserNameForCreditCard(productArrangement, b751Response);
                    communicatePostFulfilmentActivities.communicateForIBRegistration(b751Response, productArrangement, header);
                }
            }
        } catch (WebServiceException e) {
            LOGGER.info("Exception occurred(ResourceNotAvailableError) while calling B751 ", e);
        }
        return b751Response;
    }

    private void getUserNameForCreditCard(ProductArrangement productArrangement, StB751BAppPerCCRegAuth b751Response) {
        if (ArrangementType.CREDITCARD.getValue().equals(productArrangement.getArrangementType())) {
            if (CollectionUtils.isEmpty(productArrangement.getJointParties())) {
                Customer customer = new Customer();
                productArrangement.getJointParties().add(customer);
            }
            if (productArrangement.getJointParties().get(0).getIsRegisteredIn() == null) {
                productArrangement.getJointParties().get(0).setIsRegisteredIn(new InternetBankingRegistration());
            }
            if (productArrangement.getJointParties().get(0).getIsRegisteredIn().getProfile() == null) {
                productArrangement.getJointParties().get(0).getIsRegisteredIn().setProfile(new InternetBankingProfile());
            }
            productArrangement.getJointParties().get(0).getIsRegisteredIn().getProfile().setUserName(b751Response.getPartyidEmergingChannelUserId());
        }
    }
}
