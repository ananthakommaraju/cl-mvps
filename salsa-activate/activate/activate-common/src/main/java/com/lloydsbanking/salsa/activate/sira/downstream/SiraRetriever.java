package com.lloydsbanking.salsa.activate.sira.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.sira.convert.SiraRequestFactory;
import com.lloydsbanking.salsa.activate.sira.utility.SiraErrorCodes;
import com.lloydsbanking.salsa.activate.sira.utility.SiraHelper;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.jdbc.TmxDetailsDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.model.TmxDetails;
import com.lloydsbanking.salsa.downstream.pam.service.constant.PamConstant;
import com.lloydsbanking.salsa.downstream.pam.service.update.UpdateApplicationParamValuesForSira;
import com.lloydsbanking.salsa.downstream.sira.client.SiraClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.synectics_solutions.dataservices.schemas.core.v1_1.dsfault.DSFaultType;
import com.synectics_solutions.sira.schemas.realtime.core.v1_0.realtimeresulttype4.RealtimeResultType4Type;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.AuthenticationHeader;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.Source;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.SubmitWorkItemResponse;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.Detail;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class SiraRetriever {
    @Autowired
    SiraRequestFactory siraRequestFactory;
    @Autowired
    TmxDetailsDao tmxDao;
    @Autowired
    SiraClient siraClient;
    @Autowired
    SiraHelper siraHelper;
    @Autowired
    UpdateApplicationParamValuesForSira updateApplicationParamValuesForSira;
    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateDepositArrangementConditionAndApplicationStatusHelper;
    private static final Logger LOGGER = Logger.getLogger(SiraRetriever.class);
    private static final String CLIENT_NAME = "LBG";
    private static final String SIRA_GROUP_CODE = "SIRA_THRESHOLD_VALUE";
    private static final List<String> SIRA_ERROR_CODE_LIST = Arrays.asList(SiraErrorCodes.INCORRECT_CREDENTIALS_SUPPLIED.getSiraErrorCode(), SiraErrorCodes.OPERATION_NOT_SUPPORTED_FOR_USER.getSiraErrorCode(), SiraErrorCodes.DUPLICATE_ITEM.getSiraErrorCode());
    private static final String LLOYDS_WORKFLOW_NAME = "LBG_ULLO_RT_WF1_RULES2";
    private static final String HALIFAX_AND_BOS_WORKFLOW_NAME = "LBG_UHBC_RT_WF1_RULES2";

    public void retrieveSiraDecision(DepositArrangement productArrangement, ApplicationDetails applicationDetails, RequestHeader requestHeader, boolean isAsyncCall) {
        LOGGER.info("In sira retriever ProductArrangement"+ productArrangement);
        TmxDetails tmxDetails = tmxDao.findByApplicationsId(Long.valueOf(productArrangement.getArrangementId()));
        siraHelper.addCustomerDeviceDetails(productArrangement.getPrimaryInvolvedParty().getIsPlayedBy(), tmxDetails);
        String userIdAuthor = getUserIdAuthor(requestHeader);
        XMLGregorianCalendar appDate = siraHelper.setAppDate(productArrangement.getArrangementId());
        XMLGregorianCalendar fulfilmentDate = siraHelper.setFulfilmentDate(productArrangement.getApplicationStatus(), productArrangement.getArrangementId());
        Source source = siraRequestFactory.convert(productArrangement, requestHeader.getChannelId(), userIdAuthor, appDate, fulfilmentDate);
        AuthenticationHeader authenticationHeader = new AuthenticationHeader();
        authenticationHeader.setClientName(CLIENT_NAME);
        authenticationHeader.setPassword("");
        authenticationHeader.setUsername("");
        String workFlowName = setWorkFlowName(productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerDeviceDetails(), requestHeader.getChannelId());
        CustomerScore siraCustomerScore = new CustomerScore();
        siraCustomerScore.setCustomerDecision(new CustomerDecision());
        try {
            LOGGER.info("Calling SIRA for Activate");
            SubmitWorkItemResponse.SubmitWorkItemResult submitWorkItemResult = siraClient.submitWorkItemResult(source, workFlowName, Boolean.FALSE, authenticationHeader);
            RealtimeResultType4Type realtimeResultType4Type = getRealTimeResultType4Object(submitWorkItemResult.getContent());
            if (null != realtimeResultType4Type) {
                if (realtimeResultType4Type.getFault() == null) {
                    setSiraCustomerScoreForCorrectScore(siraCustomerScore, realtimeResultType4Type, requestHeader.getChannelId());
                } else if (SiraErrorCodes.isErrorForSira(realtimeResultType4Type.getFault().getCode())) {
                    setCustomerScoreForErrorInSira(siraCustomerScore, realtimeResultType4Type.getFault(), productArrangement.getRetryCount(), ApplicationStatus.AWAITING_FULFILMENT.getValue(), applicationDetails, isAsyncCall);
                }
            }
        } catch (SOAPFaultException soapFaultException) {
            LOGGER.info("Inside SoapFaultException ;" + soapFaultException);
            setCustomerScoreForSoapFaultException(siraCustomerScore, soapFaultException.getFault().getDetail());
            updateApplicationDetails(isAsyncCall, applicationDetails, productArrangement.getRetryCount(),siraCustomerScore);
        } catch (WebServiceException webServiceException) {
            LOGGER.info("Exception occurred while calling SIRA. Returning ResourceNotAvailableError ;", webServiceException);
            siraCustomerScore.getCustomerDecision().setConnectivityErrorFlag("True");
            updateApplicationDetails(isAsyncCall, applicationDetails, productArrangement.getRetryCount(),siraCustomerScore);
        } catch (DataNotAvailableErrorMsg datanotAvailableErrorMsg) {
            LOGGER.info("Returning DataNotAvailableError:" + datanotAvailableErrorMsg);
        }
        List<RuleCondition> ruleConditions = new ArrayList<>();
        RuleCondition ruleConditionForSiraScore = new RuleCondition();
        ruleConditionForSiraScore.setResult(siraCustomerScore.getCustomerDecision().getTotalRuleScore());
        ruleConditionForSiraScore.setName("100062");
        RuleCondition ruleConditionForSiraStatus = new RuleCondition();
        ruleConditionForSiraStatus.setResult(siraCustomerScore.getCustomerDecision().getResultStatus());
        ruleConditionForSiraStatus.setName("100063");
        ruleConditions.add(ruleConditionForSiraScore);
        ruleConditions.add(ruleConditionForSiraStatus);
        updateApplicationParamValuesForSira.updateFromRuleConditionForSira(ruleConditions, productArrangement.getArrangementId());
        productArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(siraCustomerScore);
        LOGGER.info("Retry count  "+applicationDetails.getRetryCount()+ "status  "+applicationDetails.getApplicationStatus()+"sub status "+productArrangement.getApplicationSubStatus());
    }

    private void setSiraCustomerScoreForCorrectScore(CustomerScore siraCustomerScore, RealtimeResultType4Type realtimeResultType4Type, String channelId) throws DataNotAvailableErrorMsg {
        siraCustomerScore.setAssessmentType(PamConstant.SIRA_ASSESSMENT_TYPE);
        BigInteger totalScore = realtimeResultType4Type.getTotalRuleScore();
        List<ReferenceDataLookUp> lookUpList = siraHelper.getLookupListFromChannelAndGroupCodeListAndSequence(channelId, Arrays.asList(SIRA_GROUP_CODE));
        if (lookUpList.size() == 7) {
            siraCustomerScore.getCustomerDecision().setResultStatus(siraHelper.getSiraResultStatus(lookUpList, totalScore));
        } else {
            siraCustomerScore.getCustomerDecision().setResultStatus(null);
        }
        siraCustomerScore.getCustomerDecision().setTotalRuleScore(String.valueOf(totalScore));
        siraCustomerScore.getCustomerDecision().setWorkflowExecutionKey(realtimeResultType4Type.getWorkflowExecutionKey());
        siraCustomerScore.getCustomerDecision().setTotalRuleMatchCount(String.valueOf(realtimeResultType4Type.getTotalRuleMatchCount()));
        siraCustomerScore.getCustomerDecision().setTotalEnquiryMatchCount(String.valueOf(realtimeResultType4Type.getTotalEnquiryMatchCount()));
        siraCustomerScore.getCustomerDecision().setConnectivityErrorFlag("False");
    }

    private String setWorkFlowName(CustomerDeviceDetails customerDeviceDetails, String channelId) {
        if (customerDeviceDetails != null && customerDeviceDetails.getWorkFlowName() != null) {
            return customerDeviceDetails.getWorkFlowName();
        } else {
            return "LTB".equalsIgnoreCase(channelId) ? LLOYDS_WORKFLOW_NAME : HALIFAX_AND_BOS_WORKFLOW_NAME;
        }
    }

    private void updateApplicationDetails(boolean isAsyncCall, ApplicationDetails applicationDetails, Integer retryCount,CustomerScore siraCustomerScore) {
        if (isAsyncCall) {
            LOGGER.info("Inside Async Call Retry count  "+retryCount);
            updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(retryCount, "E50035", "Authentication Exception", ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.SIRA_FAILURE_SUB_STATUS, applicationDetails);
        } else {
            siraCustomerScore.getCustomerDecision().setResultStatus("FAILED");
        }
    }

    private void setCustomerScoreForErrorInSira(CustomerScore siraCustomerScore, DSFaultType dsFaultType, Integer retryCount, String appStatus, ApplicationDetails applicationDetails, boolean isAsyncCall) {
        siraCustomerScore.getCustomerDecision().setErrorReasonCode(dsFaultType.getCode());
        siraCustomerScore.getCustomerDecision().setErrorDescription(dsFaultType.getType() + ":" + dsFaultType.getMessage());
        siraCustomerScore.getCustomerDecision().setConnectivityErrorFlag("True");
        if (!SIRA_ERROR_CODE_LIST.contains(dsFaultType.getCode())) {
            if (isAsyncCall) {
                updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(retryCount, dsFaultType.getCode(), dsFaultType.getMessage(), appStatus, ActivateCommonConstant.AppSubStatus.SIRA_FAILURE_SUB_STATUS, applicationDetails);
            } else {
                siraCustomerScore.getCustomerDecision().setResultStatus("FAILED");
            }
        }
    }

    private RealtimeResultType4Type getRealTimeResultType4Object(List<Object> content) {
        if (!CollectionUtils.isEmpty(content)) {
            for (Object object : content) {
                if (object instanceof JAXBElement) {
                    JAXBElement jaxbElement = (JAXBElement) object;
                    Object obj = jaxbElement.getValue();
                    if (obj instanceof RealtimeResultType4Type) {
                        return (RealtimeResultType4Type) obj;
                    }
                }
            }
        }
        return null;
    }

    private void setCustomerScoreForSoapFaultException(CustomerScore siraCustomerScore, Detail detail) {
        siraCustomerScore.getCustomerDecision().setConnectivityErrorFlag("True");
        siraCustomerScore.getCustomerDecision().setErrorReasonCode("E50035");
        siraCustomerScore.getCustomerDecision().setErrorDescription("AuthenticationFailureException: Invalid username/password combination.");
    }

    private String getUserIdAuthor(RequestHeader requestHeader) {
        return headerRetriever.getBapiInformationHeader(requestHeader) != null && headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader() != null ? headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader().getUseridAuthor() : null;
    }
}