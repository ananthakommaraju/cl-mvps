package com.lloydsbanking.salsa.apacc.service.fulfil.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.DSTRequestFactory;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sbo_cardacquire.interfaces.cardacquiremqservice.DST;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.nio.charset.Charset;

@Component
public class JmsQueueSender {
    private static final Logger LOGGER = Logger.getLogger(JmsQueueSender.class);

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    DSTRequestFactory dstRequestFactory;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper applicationStatusHelper;

    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    private static final String EXPRESSION_DST_START = "<ns2:DST xmlns:ns2=\"http://LIB_SBO_CardAcquire/interfaces/CardAcquireMQService\">";
    private static final String EXPRESSION_DST_END = "</ns2:DST>";
    private static final String SUBSTITUTE_DST_START = "<DST>";
    private static final String SUBSTITUTE_DST_END = "</DST>";
    private static final String EXPRESSION_TRANSACTION = "Transaction";
    private static final String SUBSTITUTE_SOURCE = "source";
    private static final String SOURCE_TYPE_CCA = "CCA";
    private static final String CARD_ACQUIRE_FAILURE_REASON_CODE = "007";
    private static final String CARD_ACQUIRE_FAILURE_REASON_TEXT = "Failed to send card details to Acquire";

    public void send(FinanceServiceArrangement financeServiceArrangement, String channelId, byte[] image, ApplicationDetails applicationDetails) {
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(financeServiceArrangement, "Entering CardAcquire"));
        try {
            byte[] msg = convertToInputStream(dstRequestFactory.convert(financeServiceArrangement, channelId, image));
            jmsTemplate.convertAndSend(msg);
        } catch (Exception e) { //NOSONAR
            setErrorResponse(financeServiceArrangement.getApplicationType(), financeServiceArrangement.getRetryCount(), applicationDetails);
            LOGGER.info("Exception occured while calling jms=" + e);
        }
    }

    private void setErrorResponse(String applicationType, Integer retryCount, ApplicationDetails applicationDetails) {
        if (ActivateCommonConstant.ApplicationType.TRADE.equalsIgnoreCase(applicationType)) {
            applicationStatusHelper.setApplicationDetails(retryCount, null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), null, applicationDetails);
        } else {
            applicationStatusHelper.setApplicationDetails(retryCount, CARD_ACQUIRE_FAILURE_REASON_CODE, CARD_ACQUIRE_FAILURE_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.ACQUIRE_CALL_FAILURE, applicationDetails);
        }
    }

    private byte[] convertToInputStream(DST dstObject) {
        String dstString = convertObjecttoXmlString(dstObject);
        dstString = trimNamespace(dstString);
        boolean isStoreCCA = isStoreCCA(dstObject);
        if (isStoreCCA) {
            dstString = dstString.replaceAll(EXPRESSION_TRANSACTION, SUBSTITUTE_SOURCE);
        }
        LOGGER.info("Entering MQ call for card acquire");
        return dstString.getBytes(Charset.forName("UTF-8"));
    }

    private String convertObjecttoXmlString(DST dstObject) {
        StringWriter writer = new StringWriter();
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(DST.class);

            Marshaller m = context.createMarshaller();
            m.marshal(dstObject, writer);
        } catch (JAXBException e) {
            LOGGER.info("Error while converting object to XML String: " + e);
        }
        // output string to console
        return writer.toString();
    }

    private String trimNamespace(String inputStr) {
        String outputStr = inputStr;
        if (outputStr != null) {
            outputStr = outputStr.replaceFirst(EXPRESSION_DST_START, SUBSTITUTE_DST_START);
            outputStr = outputStr.replaceFirst(EXPRESSION_DST_END, SUBSTITUTE_DST_END);
        }
        return outputStr;
    }

    private boolean isStoreCCA(DST dstObject) {
        String sourceType = dstObject.getAWD().getTransaction().getSourceType();
        return (SOURCE_TYPE_CCA).equals(sourceType);
    }
}
