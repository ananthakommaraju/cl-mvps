package com.lloydsbanking.salsa.ppae.service.downstream;


import com.lloydsbanking.salsa.downstream.pad.client.q028.Q028Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Identifiers;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Req;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Resp;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class LoanDetailsRetriever {

    private static final Logger LOGGER = Logger.getLogger(LoanDetailsRetriever.class);

    @Autowired
    Q028Client q028Client;
    @Autowired
    HeaderRetriever headerRetriever;

    private static final String SOURCE_SYSTEM_CODE = "009";
    private static final String Q028_SERVICE_NAME = "http://xml.lloydsbanking.com/Schema/Enterprise/PAD";
    private static final String Q028_ACTION_NAME = "Q028";
    public static final int PAD_STATUS_CCA_SIGNED = 6;
    public static final int DATE_LENGTH = 8;
    public static final int DAY_START_INDEX = 0;
    public static final int MONTH_START_INDEX = 2;
    public static final int YEAR_START_INDEX = 4;
    public static final int DAY_MONTH_LENGTH = 2;
    public static final int YEAR_LENGTH = 4;

    public Q028Resp retrieve(ProductArrangement productArrangement, RequestHeader header) {
        Q028Req q028Req = createQ028Req(productArrangement.getPrimaryInvolvedParty());
        Q028Resp q028Resp = null;
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), Q028_SERVICE_NAME, Q028_ACTION_NAME);
        try {
            LOGGER.info("Entering retrieveLoanDetails PAD Q028");
            q028Resp = q028Client.retrieveLoanDetails(q028Req, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            LOGGER.info("Error while invoking Q028 - Retrieve loan Details : " + e);
        }
        if (q028Resp != null) {
            setCustomerIdAndCommunicationOption(productArrangement, q028Resp);
        }
        LOGGER.info("Exiting retrieveLoanDetails PAD Q028");
        return q028Resp;
    }

    private void setCustomerIdAndCommunicationOption(ProductArrangement productArrangement, Q028Resp q028Resp) {
        if (isErrorScenario(q028Resp.getQ028Result())) {
            LOGGER.info("External Business Error while invoking Q028 - Retrieve loan Details : Error Code " + q028Resp.getQ028Result().getResultCondition().getReasonCode());
        } else {
            productArrangement.setCommunicationOption(getCommunicationOption(q028Resp));
            if (q028Resp.getApplicantDetails() != null && !q028Resp.getApplicantDetails().getParty().isEmpty()) {
                productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(String.valueOf(q028Resp.getApplicantDetails().getParty().get(0).getPartyId()));
            }
        }
    }

    private boolean isErrorScenario(Q028Result q028Result) {
        if (null != q028Result && null != q028Result.getResultCondition() && null != q028Result.getResultCondition().getReasonCode()) {
            if (q028Result.getResultCondition().getReasonCode() > 0) {
                return true;
            }
        }
        return false;
    }

    private Q028Req createQ028Req(Customer primaryInvolvedParty) {
        Q028Req q028Req = new Q028Req();
        Identifiers identifiers = new Identifiers();
        if (!primaryInvolvedParty.getCustomerScore().isEmpty() && null != primaryInvolvedParty.getCustomerScore().get(0)) {
            identifiers.setRequestNo(primaryInvolvedParty.getCustomerScore().get(0).getScoreIdentifier());
        }
        identifiers.setLoanAgreementNo("0");
        identifiers.setSourceSystemCd(SOURCE_SYSTEM_CODE);
        q028Req.setIdentifiers(identifiers);
        return q028Req;
    }

    private String getCommunicationOption(Q028Resp q028Resp) {
        String communicationOption = null;
        if (q028Resp.getApplicationDetails() != null) {
            communicationOption = (PAD_STATUS_CCA_SIGNED == q028Resp.getApplicationDetails().getLoanApplnStatusCd())
                    ? q028Resp.getApplicationDetails().getLastCCAValidDt() : q028Resp.getApplicationDetails().getLastQteValidDt();
        }
        return getFormattedDate(communicationOption);
    }

    private String getFormattedDate(String date) {
        String formattedDate = null;
        if (date != null && date.length() == DATE_LENGTH) {
            formattedDate = date.substring(DAY_START_INDEX, DAY_START_INDEX + DAY_MONTH_LENGTH)
                    + "/" + date.substring(MONTH_START_INDEX, MONTH_START_INDEX + DAY_MONTH_LENGTH)
                    + "/" + date.substring(YEAR_START_INDEX, YEAR_START_INDEX + YEAR_LENGTH);
        }
        return formattedDate;

    }

}
