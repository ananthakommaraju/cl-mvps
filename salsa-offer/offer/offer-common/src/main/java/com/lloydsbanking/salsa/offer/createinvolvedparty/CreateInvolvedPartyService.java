package com.lloydsbanking.salsa.offer.createinvolvedparty;

import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.createinvolvedparty.convert.AuditDataFactory;
import com.lloydsbanking.salsa.offer.createinvolvedparty.downstream.CreateOcisCustomer;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Resp;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateInvolvedPartyService {

    private static final Logger LOGGER = Logger.getLogger(CreateInvolvedPartyService.class);

    @Autowired
    CreateOcisCustomer createOcisCustomer;

    @Autowired
    AuditDataFactory auditFactory;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    CustomerTraceLog customerTraceLog;

    private static final String SOURCE_SYSTEM_ID = "3";
    private static final String INTERNAL_SERVICE_ERROR_CODE = "820001";

    public void createInvolvedParty(String arrangementType, boolean marketingPref, Customer primaryInvolvedParty, RequestHeader requestHeader) throws OfferException {
        LOGGER.info(customerTraceLog.getCustomerTraceEventMessage(primaryInvolvedParty, "Entering CreateInvolvedParty (OCIS F062) "));

        F062Resp response;
        try {
            response = createOcisCustomer.create(arrangementType, primaryInvolvedParty, requestHeader, marketingPref);
        } catch (InternalServiceErrorMsg | ResourceNotAvailableErrorMsg | ExternalBusinessErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        } catch (ExternalServiceErrorMsg errorMsg) {
            throw new OfferException(handleExternalServiceError(errorMsg));
        }
        primaryInvolvedParty.setCustomerIdentifier(String.valueOf(response.getPartyId()));
        primaryInvolvedParty.getAuditData().addAll(auditFactory.createAudtitData(response.getAddressAuditUpdData(), response.getEvidAuditUpdData()));
        primaryInvolvedParty.setSourceSystemId(SOURCE_SYSTEM_ID);
        primaryInvolvedParty.setCidPersID(response.getCIDPersId());
        LOGGER.info("Exiting CreateInvolvedParty (OCIS F062) with Customer ID | CidPers ID ; " + primaryInvolvedParty.getCustomerIdentifier() + " | " + primaryInvolvedParty.getCidPersID());
    }

    private InternalServiceErrorMsg handleExternalServiceError(ExternalServiceErrorMsg errorMsg) {
        if (null != errorMsg && null != errorMsg.getFaultInfo()) {
            return exceptionUtility.internalServiceError(INTERNAL_SERVICE_ERROR_CODE, errorMsg.getFaultInfo().getReasonCode() + " " + errorMsg.getFaultInfo().getReasonText());
        }
        return null;
    }

}
