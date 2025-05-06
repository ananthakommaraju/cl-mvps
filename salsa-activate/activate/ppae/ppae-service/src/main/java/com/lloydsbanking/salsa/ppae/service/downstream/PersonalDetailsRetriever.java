package com.lloydsbanking.salsa.ppae.service.downstream;


import com.lloydsbanking.salsa.downstream.ocis.client.f595.F595Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.F595Req;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.F595Resp;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.PersonalDetails;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class PersonalDetailsRetriever {
    private static final Logger LOGGER = Logger.getLogger(PersonalDetailsRetriever.class);

    @Autowired
    F595Client f595Client;

    @Autowired
    HeaderRetriever headerRetriever;

    private static final int MAX_REPEAT_QTY = 16;
    private static final short EXT_SYS_ID = 4;
    private static final int POST_CODE_LENGTH = 8;
    private static final int POST_CODE_OUT_START_INDEX = 0;
    private static final int POST_CODE_IN_START_INDEX = 5;
    private static final int POST_CODE_IN_OUT_LENGTH = 3;
    private static final String ADDRESS_CODE_CURRENT = "CURRENT";
    private static final String F595_SERVICE_NAME = "http://xml.lloydsbanking.com/Schema/Enterprise/CustomerManufacturing/OCIS/F595_EnqPersAddrTelMkgAPI";
    private static final String F595_ACTION_NAME = "F595";

    public void retrieve(Customer primaryInvolvedParty, RequestHeader header) {
        F595Req f595Req = createF595Request(primaryInvolvedParty.getCustomerIdentifier());
        F595Resp f595Resp = null;
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), F595_SERVICE_NAME, F595_ACTION_NAME);
        try {
            LOGGER.info("Entering retrievePersonalDetails F595 Client");
            f595Resp = f595Client.retrievePersonalDetails(f595Req, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            LOGGER.info("Error while invoking F595 - Retrieve personal Details : " + e);
        }
        if (f595Resp != null) {
            setPersonalDetails(f595Resp, primaryInvolvedParty);
        }
        LOGGER.info("Exiting retrievePersonalDetails F595 Client");
    }

    private F595Req createF595Request(String customerId) {
        F595Req f595Req = new F595Req();
        f595Req.setPartyId(Long.valueOf(customerId));
        f595Req.setMaxRepeatGroupQy(MAX_REPEAT_QTY);
        f595Req.setExtSysId(EXT_SYS_ID);
        f595Req.setKeyExtSysId(EXT_SYS_ID);
        f595Req.setExtPartyIdTx("");
        return f595Req;
    }

    private void setPersonalDetails(F595Resp f595Resp, Customer primaryInvolvedParty) {
        primaryInvolvedParty.setEmailAddress(null);
        if (f595Resp.getF595Result() != null && f595Resp.getF595Result().getResultCondition() != null && f595Resp.getF595Result().getResultCondition().getSeverityCode() != 0) {
            LOGGER.info("External Service Error while invoking F595 - Retrieve personal Details : Error Code " + f595Resp.getF595Result().getResultCondition().getSeverityCode());
        } else if (f595Resp.getPartyGroup() != null) {
            PersonalDetails personalDetails = f595Resp.getPartyGroup().getPersonalDetails();
            primaryInvolvedParty.setEmailAddress(personalDetails.getEmailAddressTx());
            if (primaryInvolvedParty.getIsPlayedBy().getIndividualName().isEmpty()) {
                primaryInvolvedParty.getIsPlayedBy().getIndividualName().add(new IndividualName());
            }
            primaryInvolvedParty.getIsPlayedBy().getIndividualName().get(0).setLastName(personalDetails.getSurname());
            primaryInvolvedParty.getIsPlayedBy().getIndividualName().get(0).setPrefixTitle(personalDetails.getPartyTl());

            if (primaryInvolvedParty.getPostalAddress().isEmpty()) {
                primaryInvolvedParty.getPostalAddress().add(new PostalAddress());
            }
            primaryInvolvedParty.getPostalAddress().get(0).setStatusCode(ADDRESS_CODE_CURRENT);
            if (primaryInvolvedParty.getPostalAddress().get(0).getUnstructuredAddress() == null) {
                primaryInvolvedParty.getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
            }

            primaryInvolvedParty.getPostalAddress().get(0).getUnstructuredAddress().setPostCode(getPostCode(f595Resp.getPartyGroup().getAddressGroup().getPostCd()));
        }
    }

    private String getPostCode(String postCode) {
        if (!StringUtils.isEmpty(postCode) && postCode.length() == POST_CODE_LENGTH) {
            return postCode.substring(POST_CODE_OUT_START_INDEX, POST_CODE_OUT_START_INDEX + POST_CODE_IN_OUT_LENGTH) + postCode.substring(POST_CODE_IN_START_INDEX, POST_CODE_IN_START_INDEX + POST_CODE_IN_OUT_LENGTH);
        } else {
            return postCode;
        }
    }
}
