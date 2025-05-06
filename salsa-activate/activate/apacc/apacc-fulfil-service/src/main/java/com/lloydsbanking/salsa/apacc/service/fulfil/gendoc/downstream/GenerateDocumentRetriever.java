package com.lloydsbanking.salsa.apacc.service.fulfil.gendoc.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apacc.service.fulfil.gendoc.convert.GenerateDocumentRequestFactory;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.cm.client.GenerateDocumentClient;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_communicationmanager.ia_generatedocument.GenerateDocumentInternalServiceErrorMsg;
import lib_sim_communicationmanager.ia_generatedocument.GenerateDocumentResourceNotAvailableErrorMsg;
import lib_sim_communicationmanager.messages.GenerateDocumentRequest;
import lib_sim_communicationmanager.messages.GenerateDocumentResponse;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class GenerateDocumentRetriever {
    private static final Logger LOGGER = Logger.getLogger(GenerateDocumentRetriever.class);
    private static final String REASON_CODE = "007";
    private static final String REASON_TEXT = "Failed to send card details to Acquire";

    @Autowired
    GenerateDocumentRequestFactory generateDocumentRequestFactory;

    @Autowired
    GenerateDocumentClient generateDocumentClient;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper applicationStatusHelper;


    public GenerateDocumentResponse callGenerateDocumentService(FinanceServiceArrangement financeServiceArrangement, RequestHeader header, ApplicationDetails applicationDetails) {
        GenerateDocumentRequest generateDocumentRequest = generateDocumentRequestFactory.convert(financeServiceArrangement, header, financeServiceArrangement.getAssociatedProduct().getProductoffer());
        LOGGER.info("Entering GenerateDocument with documentItem name: " + generateDocumentRequest.getDocumentationItem().getName());
        GenerateDocumentResponse generateDocumentResponse = null;
        try {
            generateDocumentResponse = generateDocumentClient.generateDocument(generateDocumentRequest);
        } catch (WebServiceException | GenerateDocumentResourceNotAvailableErrorMsg | GenerateDocumentInternalServiceErrorMsg errorMsg) {
            applicationStatusHelper.setApplicationDetails(financeServiceArrangement.getRetryCount(), REASON_CODE, REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.ACQUIRE_CALL_FAILURE, applicationDetails);
            LOGGER.info("Error while calling generate document: " + errorMsg);
        }
        financeServiceArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        return generateDocumentResponse;
    }
}
