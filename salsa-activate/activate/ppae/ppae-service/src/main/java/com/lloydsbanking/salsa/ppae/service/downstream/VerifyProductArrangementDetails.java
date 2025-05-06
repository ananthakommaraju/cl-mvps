package com.lloydsbanking.salsa.ppae.service.downstream;


import com.lloydsbanking.salsa.downstream.arrangement.client.wz.ArrangementClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.service.convert.VerifyProductArrangementDetailsRequestFactory;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.VerifyProductArrangementDetailsRequest;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.VerifyProductArrangementDetailsResponse;
import com.lloydstsb.schema.enterprise.lcsm_common.wz.Condition;
import com.lloydstsb.schema.enterprise.lcsm_common.wz.RuleCondition;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.ContactPreference;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.BalanceTransfer;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import java.util.Arrays;
import java.util.List;

@Repository
public class VerifyProductArrangementDetails {

    private static final Logger LOGGER = Logger.getLogger(VerifyProductArrangementDetails.class);

    @Autowired
    ArrangementClient arrangementClient;
    @Autowired
    VerifyProductArrangementDetailsRequestFactory verifyProductArrangementDetailsRequestFactory;
    @Autowired
    HeaderRetriever headerRetriever;

    private static final String BALANCE_TRANSFER_SYS_ERROR_STATUS = "AVS SYSERR";
    private static final String BALANCE_TRANSFER_FAIL_STATUS = "AVS FAIL";
    private static final String CONDITION_RESULT_PASSED = "PASSED";
    private static final String CONDITION_RESULT_PASS = "Pass";
    private static final String CONDITION_NAME_BANK_ENHANCED_CHECK = "BANK_ENHANCED_CHECK_RESULT";
    private static final String CONDITION_NAME_AVS = "AVS_RESULT";
    private static final String CONDITION_NAME_FASTER_PAYMENT = "FASTER_PAYMENTS_STATUS";
    private static final String ROLE_FINANCIAL_INSTITUTION = "FINANCIAL_INSTITUTION";
    private static final List<String> PAYMENT_RESULT = Arrays.asList("M", "A");
    private static final String CONDITION_NAME_BANK_ACCOUNT_CLOSED = "BANK_ACCOUNT_CLOSED_WARNING";
    private static final String SERVICE_NAME = "http://www.lloydstsb.com/Schema/Enterprise/LCSM_ArrangementNegotiation/ArrangementSetupService";
    private static final String ACTION_NAME = "verifyProductArrangementDetails";

    public boolean verify(BalanceTransfer balanceTransfer, Customer customer, RequestHeader header) {
        VerifyProductArrangementDetailsRequest request = verifyProductArrangementDetailsRequestFactory.convert(balanceTransfer, customer);
        VerifyProductArrangementDetailsResponse response = null;
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), SERVICE_NAME, ACTION_NAME);
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders());
        boolean isVerifyCallSuccessful = false;
        try {
            LOGGER.info("Entering verifyProductArrangementDetails");
            response = arrangementClient.verifyProductArrangementDetails(request, contactPoint, serviceRequest, securityHeaderType, bapiInformation);
        } catch (WebServiceException | JAXBException | ErrorInfo e) {
            balanceTransfer.setStatus(BALANCE_TRANSFER_SYS_ERROR_STATUS);
            LOGGER.info("Error while calling verifyProductArrangementDetails: " + e);
        }
        if (response != null) {
            isVerifyCallSuccessful = setResponse(response, balanceTransfer);
        }
        LOGGER.info("Exiting Verify Product Arrangement Details");
        return isVerifyCallSuccessful;
    }

    private boolean setResponse(VerifyProductArrangementDetailsResponse response, BalanceTransfer balanceTransfer) {
        boolean isVerifyCallSuccessful = false;
        if (isErrorScenario(response)) {
            LOGGER.info("Error from verifyProductArrangementDetails ReasonCode : " + response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().get(0).getReasonCode());
            balanceTransfer.setStatus(BALANCE_TRANSFER_SYS_ERROR_STATUS);
        } else {
            isVerifyCallSuccessful = validateVerifyCall(response, balanceTransfer);
        }
        return isVerifyCallSuccessful;
    }

    private boolean validateVerifyCall(VerifyProductArrangementDetailsResponse response, BalanceTransfer balanceTransfer) {
        boolean avsStatus = validateAVStatus(response, balanceTransfer);
        boolean bankStatus = validateBankResult(response, balanceTransfer);
        if (!(bankStatus && avsStatus)) {
            balanceTransfer.setStatus(BALANCE_TRANSFER_FAIL_STATUS);
        }
        return avsStatus || bankStatus;
    }

    private boolean validateBankResult(VerifyProductArrangementDetailsResponse response, BalanceTransfer balanceTransfer) {
        boolean flag = false;
        if (balanceTransfer.getCurrentAccountDetails() != null && !StringUtils.isEmpty(balanceTransfer.getCurrentAccountDetails().getAccountNumber()) && !StringUtils.isEmpty(balanceTransfer.getCurrentAccountDetails().getSortCode())) {
            if (CONDITION_RESULT_PASS.equalsIgnoreCase(getConditionResultByName(response.getVerificationResult().get(0).getHasObjectConditions(), CONDITION_NAME_BANK_ENHANCED_CHECK))) {
                if (isValidFPS(response) && !getConditionRuleByName(response.getVerificationResult().get(0).getHasObjectConditions(), CONDITION_NAME_BANK_ACCOUNT_CLOSED)) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    private boolean validateAVStatus(VerifyProductArrangementDetailsResponse response, BalanceTransfer balanceTransfer) {
        boolean flag = false;
        if (!StringUtils.isEmpty(balanceTransfer.getCreditCardNumber())) {
            for (ContactPreference contactPreference : response.getVerificationResult().get(0).getRoles().get(1).getInvolvedParty().getContactPreferences()) {
                if (!contactPreference.getHasObjectConditions().isEmpty()) {
                    RuleCondition condition = getConditionByName(contactPreference.getHasObjectConditions().get(0), CONDITION_NAME_AVS);
                    if (condition != null && CONDITION_RESULT_PASSED.equalsIgnoreCase(condition.getResult())) {
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }

    private boolean isValidFPS(VerifyProductArrangementDetailsResponse response) {
        if (ROLE_FINANCIAL_INSTITUTION.equalsIgnoreCase(response.getVerificationResult().get(0).getRoles().get(0).getType().getValue())) {
            String result = getConditionResultByName(response.getVerificationResult().get(0).getRoles().get(0).getInvolvedParty().getHasObjectConditions(), CONDITION_NAME_FASTER_PAYMENT);
            if (PAYMENT_RESULT.contains(result)) {
                return true;
            }
        }
        return false;
    }

    private boolean isErrorScenario(VerifyProductArrangementDetailsResponse response) {
        if (response.getResponseHeader() != null && response.getResponseHeader().getResultCondition() != null && response.getResponseHeader().getResultCondition().getExtraConditions() != null) {
            if (!response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().isEmpty() && response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().get(0).getReasonCode() != null) {
                if (response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().get(0).getReasonCode() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean getConditionRuleByName(List<Condition> conditionList, String name) {
        boolean rule = false;
        for (Condition condition : conditionList) {
            RuleCondition ruleCondition = getConditionByName(condition, name);
            if (ruleCondition != null && ruleCondition.isUtiliseRule() != null) {
                rule = ruleCondition.isUtiliseRule();
            }
        }
        return rule;
    }

    private String getConditionResultByName(List<Condition> conditionList, String name) {
        String result = null;
        for (Condition condition : conditionList) {
            RuleCondition ruleCondition = getConditionByName(condition, name);
            if (ruleCondition != null) {
                result = ruleCondition.getResult();
            }
        }
        return result;
    }

    private RuleCondition getConditionByName(Condition condition, String name) {
        RuleCondition ruleCondition = null;
        if (condition instanceof RuleCondition && name.equalsIgnoreCase(condition.getName())) {
            ruleCondition = (RuleCondition) condition;
        }
        return ruleCondition;
    }
}