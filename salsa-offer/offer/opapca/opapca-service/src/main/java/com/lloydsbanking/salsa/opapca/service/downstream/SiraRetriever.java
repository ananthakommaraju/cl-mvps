package com.lloydsbanking.salsa.opapca.service.downstream;


import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationsDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.TmxDetailsDao;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.model.TmxDetails;
import com.lloydsbanking.salsa.downstream.pam.service.constant.PamConstant;
import com.lloydsbanking.salsa.downstream.sira.client.SiraClient;
import com.lloydsbanking.salsa.offer.AsmDecision;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.opapca.service.convert.SiraRequestFactory;
import com.lloydsbanking.salsa.opapca.service.utility.AppSubStatus;
import com.lloydsbanking.salsa.opapca.service.utility.SiraErrorCodes;
import com.lloydsbanking.salsa.opapca.service.utility.SiraHelper;
import com.lloydsbanking.salsa.opapca.service.utility.SiraStatus;
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
import javax.xml.soap.Detail;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class SiraRetriever {
    private static final String SIRA_GROUP_CODE = "SIRA_THRESHOLD_VALUE";
    private static final String CLIENT_NAME = "LBG";
    private static final Logger LOGGER = Logger.getLogger(SiraRetriever.class);
    private static final String CONDITION_FOR_PCA_RE_ENGINEERING = "INTEND_TO_SWITCH";
    private static final int THRESHOLD_VALUES_FOR_SIRA_SCORE = 7;
    private static final int LOWER_THRESHOLD_LIMIT_FOR_ACCEPT = 0;
    private static final int UPPER_THRESHOLD_LIMIT_FOR_ACCEPT= 1;
    private static final int LOWER_THRESHOLD_LIMIT_FOR_REFER_FRAUD = 2;
    private static final int UPPER_THRESHOLD_LIMIT_FOR_REFER_FRAUD = 3;
    private static final int LOWER_THRESHOLD_LIMIT_FOR_REFER_IDV = 4;
    private static final int UPPER_THRESHOLD_LIMIT_FOR_REFER_IDV = 5;
    private static final int LOWER_THRESHOLD_LIMIT_FOR_DECLINE = 6;
    @Autowired
    SiraClient siraClient;
    @Autowired
    LookupDataRetriever lookupDataRetriever;
    @Autowired
    TmxDetailsDao tmxDao;
    @Autowired
    ApplicationsDao applicationsDao;
    @Autowired
    SiraRequestFactory siraRequestFactory;
    @Autowired
    SiraHelper siraHelper;


    public void retrieveSiraDecision(DepositArrangement productArrangement, RequestHeader requestHeader) {
        if (isSiraCallRequired(productArrangement.isSIRAEnabledSwitch(), productArrangement.getConditions())) {
            Applications applications = applicationsDao.findOne(Long.valueOf(productArrangement.getArrangementId()));
            applications.setDateModified(new Date());
            TmxDetails tmx = new TmxDetails(applications, siraHelper.toSerializedString(productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerDeviceDetails()));
            tmxDao.save(tmx);
            Source source = siraRequestFactory.convert(productArrangement, requestHeader, applications.getDateModified());
            AuthenticationHeader authenticationHeader = new AuthenticationHeader();
            authenticationHeader.setClientName(CLIENT_NAME);
            authenticationHeader.setPassword("");
            authenticationHeader.setUsername("");
            String workFlowName = productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerDeviceDetails() != null ? productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerDeviceDetails().getWorkFlowName() : "";
            CustomerScore siraCustomerScore = new CustomerScore();
            siraCustomerScore.setCustomerDecision(new CustomerDecision());
            try {
                LOGGER.info("Calling SIRA ");
                SubmitWorkItemResponse.SubmitWorkItemResult submitWorkItemResult = siraClient.submitWorkItemResult(source, workFlowName, Boolean.FALSE, authenticationHeader);
                RealtimeResultType4Type realtimeResultType4Type = getRealTimeResultType4Object(submitWorkItemResult.getContent());
                if (null != realtimeResultType4Type) {
                    if (realtimeResultType4Type.getFault() == null) {
                        siraCustomerScore.setAssessmentType(PamConstant.SIRA_ASSESSMENT_TYPE);
                        BigInteger totalScore = realtimeResultType4Type.getTotalRuleScore();
                        siraCustomerScore.getCustomerDecision().setTotalRuleScore(String.valueOf(totalScore));
                        List<ReferenceDataLookUp> lookUpList = lookupDataRetriever.getLookupListFromChannelAndGroupCodeListAndSequence(requestHeader.getChannelId(), Arrays.asList(SIRA_GROUP_CODE));
                        if (lookUpList.size() == THRESHOLD_VALUES_FOR_SIRA_SCORE) {
                            siraCustomerScore.getCustomerDecision().setResultStatus(getSiraResultStatus(lookUpList, totalScore));
                        } else {
                            siraCustomerScore.getCustomerDecision().setResultStatus(null);
                        }
                        siraCustomerScore.getCustomerDecision().setWorkflowExecutionKey(realtimeResultType4Type.getWorkflowExecutionKey());
                        siraCustomerScore.getCustomerDecision().setTotalRuleMatchCount(String.valueOf(realtimeResultType4Type.getTotalRuleMatchCount()));
                        siraCustomerScore.getCustomerDecision().setTotalEnquiryMatchCount(String.valueOf(realtimeResultType4Type.getTotalEnquiryMatchCount()));
                        siraCustomerScore.getCustomerDecision().setConnectivityErrorFlag("False");
                        calculateFinalDecision(siraCustomerScore.getCustomerDecision().getResultStatus(), productArrangement);
                    } else if (SiraErrorCodes.isErrorForSira(realtimeResultType4Type.getFault().getCode())) {
                        siraCustomerScore.getCustomerDecision().setErrorReasonCode(realtimeResultType4Type.getFault().getCode());
                        siraCustomerScore.getCustomerDecision().setErrorDescription(realtimeResultType4Type.getFault().getType() + ":" + realtimeResultType4Type.getFault().getMessage());
                        siraCustomerScore.getCustomerDecision().setConnectivityErrorFlag("True");
                    }
                }
            } catch (SOAPFaultException soapFaultException) {
                LOGGER.info("Inside SoapFaultException ;" + soapFaultException);
                setCustomerScoreForSoapFaultException(siraCustomerScore, soapFaultException.getFault().getDetail());
            } catch (WebServiceException webServiceException) {
                LOGGER.info("Exception occurred while calling SIRA. Returning ResourceNotAvailableError ;", webServiceException);
                siraCustomerScore.getCustomerDecision().setConnectivityErrorFlag("True");
            } catch (DataNotAvailableErrorMsg datanotAvailableErrorMsg) {
                LOGGER.info("Returning DataNotAvailableError:" + datanotAvailableErrorMsg);
            }
            productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(siraCustomerScore);
        }
    }

    private void calculateFinalDecision(String siraScore, ProductArrangement productArrangement) {
        List<String> asmAndEidvScore = siraHelper.calculateEidvAndAsmScore(productArrangement.getPrimaryInvolvedParty().getCustomerScore());
        if (SiraStatus.DECLINE.getValue().equalsIgnoreCase(siraScore) || AsmDecision.DECLINED.getValue().equalsIgnoreCase(asmAndEidvScore.get(1))) {
            productArrangement.setApplicationStatus(ApplicationStatus.DECLINED.getValue());
            productArrangement.setApplicationSubStatus(calculateDeclineSubStatus(siraScore));
        } else if (SiraStatus.REFER_FRAUD.getValue().equalsIgnoreCase(siraScore) || AsmDecision.REFERRED.getValue().equalsIgnoreCase(asmAndEidvScore.get(1))) {
            productArrangement.setApplicationStatus(ApplicationStatus.REFERRED.getValue());
            productArrangement.setApplicationSubStatus(calculateReferSubStatus(siraScore, asmAndEidvScore.get(1)));
        } else {
            productArrangement.setApplicationStatus(ApplicationStatus.APPROVED.getValue());
            productArrangement.setApplicationSubStatus(null);
        }
    }

    private String calculateDeclineSubStatus(String siraScore) {
        return SiraStatus.DECLINE.getValue().equalsIgnoreCase(siraScore) ? AppSubStatus.SIRA_DECLINE.getValue() : AppSubStatus.ASM_DECLINE.getValue();
    }

    private String calculateReferSubStatus(String siraScore, String asmScore) {
        if (SiraStatus.REFER_FRAUD.getValue().equalsIgnoreCase(siraScore) && AsmDecision.REFERRED.getValue().equalsIgnoreCase(asmScore)) {
            return AppSubStatus.SIRA_AND_ASM_REFER.getValue();
        } else if (SiraStatus.REFER_FRAUD.getValue().equalsIgnoreCase(siraScore)) {
            return AppSubStatus.SIRA_REFER.getValue();
        } else {
            return AppSubStatus.ASM_REFER.getValue();
        }
    }

    private String getSiraResultStatus(List<ReferenceDataLookUp> lookUpList, BigInteger totalScore) {
        if (checkLimit(lookUpList.get(LOWER_THRESHOLD_LIMIT_FOR_ACCEPT).getLookupValueDesc(), lookUpList.get(UPPER_THRESHOLD_LIMIT_FOR_ACCEPT).getLookupValueDesc(), totalScore)) {
            return SiraStatus.ACCEPT.getValue();
        } else if (checkLimit(lookUpList.get(LOWER_THRESHOLD_LIMIT_FOR_REFER_FRAUD).getLookupValueDesc(), lookUpList.get(UPPER_THRESHOLD_LIMIT_FOR_REFER_FRAUD).getLookupValueDesc(), totalScore)) {
            return SiraStatus.REFER_FRAUD.getValue();
        } else if (checkLimit(lookUpList.get(LOWER_THRESHOLD_LIMIT_FOR_REFER_IDV).getLookupValueDesc(), lookUpList.get(UPPER_THRESHOLD_LIMIT_FOR_REFER_IDV).getLookupValueDesc(), totalScore)) {
            return SiraStatus.REFER_IDV.getValue();
        } else if (new BigInteger(lookUpList.get(LOWER_THRESHOLD_LIMIT_FOR_DECLINE).getLookupValueDesc()).compareTo(totalScore) <= 0) {
            return SiraStatus.DECLINE.getValue();
        }
        return null;
    }

    private boolean checkLimit(String lowerLimit, String upperLimit, BigInteger totalScore) {
        return new BigInteger(lowerLimit).compareTo(totalScore) <= 0 && new BigInteger(upperLimit).compareTo(totalScore) >= 0;
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

    private boolean isSiraCallRequired(Boolean siraEnabledSwitch, List<RuleCondition> ruleConditionList) {
        boolean isPCAReEngineering = false;
        for (RuleCondition condition : ruleConditionList) {
            if ((CONDITION_FOR_PCA_RE_ENGINEERING).equalsIgnoreCase(condition.getName())) {
                isPCAReEngineering = true;
            }
        }
        return null != siraEnabledSwitch && siraEnabledSwitch && isPCAReEngineering;
    }
}