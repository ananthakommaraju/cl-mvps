package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.downstream.pp.client.PaymentProcessingClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.service.convert.IssueInPaymentInstRequestFactory;
import com.lloydsbanking.salsa.soap.fs.ftp.IssueInpaymentInstructionRequest;
import com.lloydsbanking.salsa.soap.fs.ftp.IssueInpaymentInstructionResponse;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm_financialtransactionprocessing.ErrorInfo;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.BalanceTransfer;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.WebServiceException;

@Component
public class IssueInPaymentInstRetriever {

    private static final Logger LOGGER = Logger.getLogger(IssueInPaymentInstRetriever.class);

    @Autowired
    PaymentProcessingClient paymentProcessingClient;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    IssueInPaymentInstRequestFactory issueInPaymentInstRequestFactory;

    private static final String F216_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/VisionPlus/F216_AddBalCrdTransfer\"";

    private static final String F216_ACTION_NAME = "F216";

    private static final String STATUS_SUCCESS = "SUCCESS";

    public IssueInpaymentInstructionResponse invoke(String btOffAttributeValue, RequestHeader header, BalanceTransfer balanceTransfer, String sourceCreditCardNumber) {
        LOGGER.info("Entering Issue Payment Instruction");
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), F216_SERVICE_NAME, F216_ACTION_NAME);
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders());
        IssueInpaymentInstructionRequest issueInpaymentInstructionRequest = issueInPaymentInstRequestFactory.convert(btOffAttributeValue, balanceTransfer, sourceCreditCardNumber);
        IssueInpaymentInstructionResponse response = null;
        try {
            LOGGER.info("Entering Vision Plus addBalanceTransfer issueInPaymentInstruction ");
            response = paymentProcessingClient.issueInPaymentInstruction(issueInpaymentInstructionRequest, contactPoint, serviceRequest, securityHeaderType, bapiInformation);
        } catch (WebServiceException | ErrorInfo e) {
            LOGGER.info("Error while invoking issueInPaymentInstruction : " + e);
        }
        if (response != null && isValidF216Resp(response)) {
            balanceTransfer.setStatus(STATUS_SUCCESS);
            LOGGER.info("Exiting Vision Plus addBalanceTransfer issueInPaymentInstruction");
        }
        return response;
    }

    private boolean isValidF216Resp(IssueInpaymentInstructionResponse response) {
        if (null != response.getResponseHeader() && null != response.getResponseHeader().getResultCondition() && null != response.getResponseHeader().getResultCondition().getExtraConditions()) {
            if (!CollectionUtils.isEmpty(response.getResponseHeader().getResultCondition().getExtraConditions().getCondition()) && response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().get(0).getSeverityCode() != 0) {
                LOGGER.info("External Business Error while invoking F216 - IssueInpaymentInstruction : Error Code :817008");
                return false;
            }
        }
        return true;
    }
}
