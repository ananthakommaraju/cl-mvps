package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;
import java.util.List;

@Repository
public class RetrieveProductFeatures {

    private static final Logger LOGGER = Logger.getLogger(RetrieveProductFeatures.class);
    private static final String SYSTEM_CODE = "00010";

    @Autowired
    PrdClient prdClient;
    @Autowired
    ProductTraceLog productTraceLog;

    public Product getProduct(ProductArrangement productArrangement, ApplicationDetails applicationDetails, RequestHeader requestHeader) {
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = null;
        Product retrievedProduct = null;
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        retrieveProductConditionsRequest.setHeader(requestHeader);
        if (ArrangementType.CREDITCARD.getValue().equalsIgnoreCase(productArrangement.getArrangementType())) {
            retrieveProductConditionsRequest.setProduct(productArrangement.getAssociatedProduct());
        } else {
            Product product = new Product();
            product.setProductIdentifier(productArrangement.getAssociatedProduct().getProductIdentifier());
            retrieveProductConditionsRequest.setProduct(product);
        }
        try {
            LOGGER.info(productTraceLog.getProductTraceEventMessage(retrieveProductConditionsRequest.getProduct(), "Entering RetrieveProductConditions"));
            retrieveProductConditionsResponse = prdClient.retrieveProductConditions(retrieveProductConditionsRequest);
        } catch (RetrieveProductConditionsInternalServiceErrorMsg | RetrieveProductConditionsDataNotAvailableErrorMsg | RetrieveProductConditionsResourceNotAvailableErrorMsg | RetrieveProductConditionsExternalServiceErrorMsg | RetrieveProductConditionsExternalBusinessErrorMsg | WebServiceException ex) {
            LOGGER.info("Error calling RetrieveProductConditions.Catching and moving forward" + ex);
            if (ArrangementType.SAVINGS.getValue().equalsIgnoreCase(productArrangement.getArrangementType()) || ArrangementType.CREDITCARD.getValue().equalsIgnoreCase(productArrangement.getArrangementType())) {
                int retryCount = productArrangement.getRetryCount() != null ? productArrangement.getRetryCount() : 0;
                applicationDetails.setApplicationStatus(ApplicationStatus.AWAITING_FULFILMENT.getValue());
                applicationDetails.setRetryCount(retryCount + 1);
                applicationDetails.setApiFailureFlag(true);
            }
        }
        if (retrieveProductConditionsResponse != null && !CollectionUtils.isEmpty(retrieveProductConditionsResponse.getProduct())) {
            setInstructionDetails(retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier(), retrieveProductConditionsResponse.getProduct().get(0));
            retrievedProduct = retrieveProductConditionsResponse.getProduct().get(0);
            LOGGER.info(productTraceLog.getProductTraceEventMessage(retrievedProduct, "Exiting RetrieveProductConditions"));
        }
        return retrievedProduct;
    }


    private void setInstructionDetails(List<ExtSysProdIdentifier> extSysProdIdentifiers, Product product) {
        for (ExtSysProdIdentifier extSysProdIdentifier : extSysProdIdentifiers) {
            if (null != extSysProdIdentifier && SYSTEM_CODE.equalsIgnoreCase(extSysProdIdentifier.getSystemCode())) {
                InstructionDetails instructionDetails = new InstructionDetails();
                instructionDetails.setInstructionMnemonic(extSysProdIdentifier.getProductIdentifier());
                product.setInstructionDetails(instructionDetails);
                break;

            }
        }
    }
}
