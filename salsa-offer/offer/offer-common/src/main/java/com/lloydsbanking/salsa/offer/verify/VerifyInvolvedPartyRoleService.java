package com.lloydsbanking.salsa.offer.verify;


import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.offer.ApplicantType;
import com.lloydsbanking.salsa.offer.EIDVStatus;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.ProductEligibilityType;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.offer.verify.downstream.EidvRetriever;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Component
public class VerifyInvolvedPartyRoleService {
    private static final Logger LOGGER = Logger.getLogger(VerifyInvolvedPartyRoleService.class);

    private static final String CURRENT_ADDRESS = "CURRENT";
    private static final String ASSESSMENT_TYPE_EIDV = "EIDV";
    private static final String ASSESSMENT_IDENTIFIER_REFER = "123";
    private static final String EMPLOYMENT_STATUS_STUDENT = "004";
    private static final String EIDV_ERROR_CODES = "EIDV_ERROR_CODES";

    @Autowired
    EidvRetriever eidvRetriever;
    @Autowired
    LookupDataRetriever lookupDataRetriever;
    @Autowired
    CustomerTraceLog customerTraceLog;

    public void verify(ProductArrangement productArrangement, RequestHeader requestHeader) throws OfferException {
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        LOGGER.info(customerTraceLog.getCustomerTraceEventMessage(customer, "Entering VerifyInvolvedPartyRole (To check EIDV status of primaryInvolvedParty/ Guardian) "));

        String applicationType = productArrangement.getApplicationType();
        CustomerScore customerScore = null;
        if (CollectionUtils.isEmpty(customer.getCustomerScore())) {
            if (ProductEligibilityType.TRADE.getValue().equalsIgnoreCase(applicationType)) {
                customerScore = createCustomerScore(EIDVStatus.ACCEPT.getValue());
                productArrangement.setApplicationStatus(ApplicationStatus.APPROVED.getValue());
            } else if (isEIDVStatusReferForBFPOAddress(customer.getPostalAddress())) {
                customerScore = createCustomerScore(EIDVStatus.REFER.getValue());
                customer.setNewCustomerIndicator(true);
            } else if (ApplicantType.DEPENDENT.getValue().equals(customer.getApplicantType())) {
                customerScore = createCustomerScore("N/A");
            } else {
                try {
                    customerScore = eidvRetriever.getEidvScore(customer, requestHeader);
                } catch (InternalServiceErrorMsg | ResourceNotAvailableErrorMsg | DataNotAvailableErrorMsg errorMsg) {
                    LOGGER.error("Exception thrown from EIDV X711 ", errorMsg);
                    customerScore = getCustomerScoreWhenEidvError(requestHeader, errorMsg);
                }
            }
            customer.getCustomerScore().add(0, customerScore);
        }
        LOGGER.info(customerTraceLog.getCustomerTraceEventMessage(customer, "Exiting VerifyInvolvedPartyRole (EIDV status received) "));

        setEidvStatusForKids(null != productArrangement.getAssociatedProduct().getInstructionDetails() ? productArrangement.getAssociatedProduct()
                .getInstructionDetails()
                .getInstructionMnemonic() : null, customer);
        setEidvStatusForStudentAccount(customer, productArrangement.getArrangementType(), customer.getCustomerScore().get(0));
    }

    private CustomerScore getCustomerScoreWhenEidvError(RequestHeader requestHeader, Exception errorMsg) throws OfferException {
        CustomerScore customerScore;
        if (errorMsg instanceof InternalServiceErrorMsg) {
            InternalServiceErrorMsg internalServiceErrorMsg = (InternalServiceErrorMsg) errorMsg;
            try {
                customerScore = createCustomerScore(getEIDVStatusInCaseOfError(requestHeader.getChannelId(),
                        (null != internalServiceErrorMsg.getFaultInfo()) ? internalServiceErrorMsg.getFaultInfo().getReasonText() : null));
            } catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
                LOGGER.error("Exception occurred while calling EIDV X711 DataNotAvailable. Setting EIDV Status as Refer ;" + dataNotAvailableErrorMsg.getMessage(), dataNotAvailableErrorMsg);
                customerScore = setEidvStatusAndAssessmentEvidenceToRefer();
            }
        } else {
            customerScore = setEidvStatusAndAssessmentEvidenceToRefer();
        }
        return customerScore;
    }

    private CustomerScore setEidvStatusAndAssessmentEvidenceToRefer() {
        CustomerScore customerScore = createCustomerScore(EIDVStatus.REFER.getValue());
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        assessmentEvidence.setEvidenceIdentifier(ASSESSMENT_IDENTIFIER_REFER);
        customerScore.getAssessmentEvidence().add(assessmentEvidence);
        return customerScore;
    }

    private void setEidvStatusForKids(String instructionMnemonic, Customer customer) {
        if (ApplicantType.DEPENDENT.getValue().equals(customer.getApplicantType()) && !CollectionUtils.isEmpty(customer.getCustomerScore())) {
            if (null != instructionMnemonic && ("P_KRS".equals(instructionMnemonic) || "P_YNG_SVR".equals(instructionMnemonic))) {
                customer.getCustomerScore().get(0).setScoreResult(EIDVStatus.REFER.getValue());
            }
        }
    }

    private void setEidvStatusForStudentAccount(Customer customer, String arrangementType, CustomerScore customerScore) {

        if (ArrangementType.CURRENT_ACCOUNT.getValue().equals(arrangementType) && EMPLOYMENT_STATUS_STUDENT.equalsIgnoreCase(customer.getIsPlayedBy().getEmploymentStatus())) {
            if (EIDVStatus.ACCEPT.getValue().equals(customerScore.getScoreResult())) {
                customerScore.setScoreResult(EIDVStatus.REFER.getValue());
            }
        }
    }

    private boolean isEIDVStatusReferForBFPOAddress(List<PostalAddress> postalAddressList) {
        if (postalAddressList != null) {
            for (PostalAddress postalAddress : postalAddressList) {
                if (CURRENT_ADDRESS.equalsIgnoreCase(postalAddress.getStatusCode()) && null != postalAddress.isIsBFPOAddress() && postalAddress.isIsBFPOAddress()) {
                    return true;
                }
            }
        }
        return false;
    }

    private CustomerScore createCustomerScore(String scoreResult) {
        CustomerScore customerScoreEidv = new CustomerScore();
        customerScoreEidv.setAssessmentType(ASSESSMENT_TYPE_EIDV);
        customerScoreEidv.setScoreResult(scoreResult);
        return customerScoreEidv;
    }

    private String getEIDVStatusInCaseOfError(String channelId, String message) throws DataNotAvailableErrorMsg {
        String status = EIDVStatus.REFER.getValue();
        List<ReferralCode> errorCodesList = new ArrayList<>();
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(EIDV_ERROR_CODES);
        List<ReferenceDataLookUp> lookUpList = lookupDataRetriever.getLookupListFromChannelAndGroupCodeList(channelId, groupCodeList);
        for (ReferenceDataLookUp lookUp : lookUpList) {
            ReferralCode referralCode = new ReferralCode();
            referralCode.setCode(lookUp.getLookupValueDesc());
            errorCodesList.add(referralCode);
        }
        for (ReferralCode errorCode : errorCodesList) {
            String code = errorCode.getCode();
            StringTokenizer tokenizer = new StringTokenizer(message);
            while (tokenizer.hasMoreElements()) {
                if (null != code && code.equalsIgnoreCase((String) tokenizer.nextElement())) {
                    status = EIDVStatus.DECLINE.getValue();
                    return status;
                }
            }
        }
        return status;
    }
}
