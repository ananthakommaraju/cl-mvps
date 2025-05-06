package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.ppae.service.downstream.ActivateProductManager;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessPendingApplications {

    private static final Logger LOGGER = Logger.getLogger(ProcessPendingApplications.class);

    @Autowired
    ActivateProductManager activateProductManager;

    @Autowired
    UpdatePamService updatePamService;

    public void modifyAndActivatePendingApplications(ProductArrangement productArrangement, RequestHeader requestHeader, PpaeInvocationIdentifier ppaeInvocationIdentifier) {

        if (ppaeInvocationIdentifier.getInvokeModifyProductArrangementFlag()) {
            LOGGER.info("Calling update Pam with AppId | appStatus | productType | RetryCount: " + productArrangement.getArrangementId() + " | " + productArrangement.getApplicationStatus() + " | " + productArrangement.getArrangementType() + " | " +productArrangement.getRetryCount());
            updatePamService.updateApplicationsInPam(productArrangement);
        }
        if (ppaeInvocationIdentifier.getInvokeActivateProductArrangementFlag()) {
            LOGGER.info("Calling activate with AppId | appStatus | productType | RetryCount: " + productArrangement.getArrangementId() + " | " + productArrangement.getApplicationStatus() + " | " + productArrangement.getArrangementType() + " | " +productArrangement.getRetryCount());
            try {
                activateProductManager.activateProduct(productArrangement, requestHeader);
            } catch (ActivateProductArrangementInternalSystemErrorMsg activateProductArrangementInternalSystemErrorMsg) {
                LOGGER.info("Error calling Activate Product Arrangement Service" + activateProductArrangementInternalSystemErrorMsg);
            } catch (ActivateProductArrangementExternalBusinessErrorMsg activateProductArrangementExternalBusinessErrorMsg) {
                LOGGER.info("Error calling Activate Product Arrangement Service" + activateProductArrangementExternalBusinessErrorMsg);
            } catch (ActivateProductArrangementExternalSystemErrorMsg activateProductArrangementExternalSystemErrorMsg) {
                LOGGER.info("Error calling Activate Product Arrangement Service" + activateProductArrangementExternalSystemErrorMsg);
            } catch (ActivateProductArrangementResourceNotAvailableErrorMsg activateProductArrangementResourceNotAvailableErrorMsg) {
                LOGGER.info("Error calling Activate Product Arrangement Service" + activateProductArrangementResourceNotAvailableErrorMsg);
            } catch (ActivateProductArrangementDataNotAvailableErrorMsg activateProductArrangementDataNotAvailableErrorMsg) {
                LOGGER.info("Error calling Activate Product Arrangement Service" + activateProductArrangementDataNotAvailableErrorMsg);
            }

        }


    }
}
