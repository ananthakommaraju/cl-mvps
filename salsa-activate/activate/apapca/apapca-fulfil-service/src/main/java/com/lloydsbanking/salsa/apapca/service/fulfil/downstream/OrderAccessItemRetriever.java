package com.lloydsbanking.salsa.apapca.service.fulfil.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.OrderAccessService;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderAccessItemRetriever {
    private static final Logger LOGGER = Logger.getLogger(CBSCustDetailsTrialRetriever.class);

    @Autowired
    OrderAccessService orderAccessService;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateDepositArrangementConditionAndApplicationStatusHelper;

    public void orderAccessItem(DepositArrangement depositArrangement, RequestHeader header, ApplicationDetails applicationDetails) {
        LOGGER.info("Entering orderAccessItem" + depositArrangement.getAccountNumber());
        try {
            String orderIdentifier = orderAccessService.orderAccessServiceResponse(depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode(), depositArrangement.getAccountNumber(), depositArrangement.getPrimaryInvolvedParty(), header);
            if (depositArrangement.getReasonCode() == null || StringUtils.isEmpty(depositArrangement.getReasonCode().getCode())) {
                depositArrangement.getAssociatedProduct().setOrderIdentifier(orderIdentifier);
            }
        } catch (ActivateProductArrangementResourceNotAvailableErrorMsg | ActivateProductArrangementExternalSystemErrorMsg e) {
            LOGGER.info("Exception occurred while calling Order Access Service. Catching it and updating ApplicationStatus ", e);
            updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(depositArrangement.getRetryCount(), ActivateCommonConstant.ApaPcaServiceConstants.ORDER_ACCESS_SERVICE_FAILURE_REASON_CODE, ActivateCommonConstant.ApaPcaServiceConstants.ORDER_ACCESS_SERVICE_FAILURE_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.FAILED_TO_CREATE_CARD_ORDER, applicationDetails);
        }
        depositArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
    }

}
