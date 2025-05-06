package com.lloydsbanking.salsa.activate.administer.convert;

import com.lloydsbanking.salsa.downstream.tms.client.x741.X741RequestBuilder;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreation;
import com.lloydstsb.schema.personal.serviceplatform.tms.v0001.TMSTaskDetailBO;
import com.lloydstsb.schema.personal.serviceplatform.tms.v0001.TaskDetailDataList;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.RuleCondition;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class X741RequestFactory {
    private static final Logger LOGGER = Logger.getLogger(X741RequestFactory.class);
    private static final String ARRANGEMENT_TYPE_LOAN_REFERRAL_AUTOMATION = "LRA";
    private static final String ADDRESS_TYPE_CURRENT = "CURRENT";
    private static final String TASK_HEADING_APP_REF_NUM = "Application Reference Number";
    private static final String TASK_HEADING_LAST_NAME = "Last Name";
    private static final String TASK_HEADING_POSTCODE = "PostCode";
    private static final String TASK_HEADING_POST_CODE = "Post Code";
    private static final String TASK_HEADING_TIME_DATE_OF_APP = "Time & Date of Application";
    private static final String TASK_HEADING_TIME_AND_DATE_OF_APP = "Time and Date of the application";
    private static final String TASK_HEADING_CREDIT_SCORE_REQ_NUM = "Credit Score Request Number";
    private static final String TASK_HEADING_CREDIT_SCORE_NUM = "Credit Score Number";
    private static final String TASK_HEADING_REFINANCE_INDICATOR = "Refinance Indicator";
    private static final String TASK_HEADING_EMAIL_ADD = "Email Address";
    private static final String PARTY_TYPE_PRIMARY = "P";
    private static final String RESOURCE_BROWSER = "BROWSER";
    private static final String WORKFLOW_USER_ID_DEFAULT = "1582406719";
    private static final String COMMENT_TYPE_CODE_ONE = "1";
    private static final String ORIGINATOR_INTERNET_BANKING = "Galaxy Internet Banking";

    public TaskCreation convert(ProductArrangement productArrangement) {
        X741RequestBuilder builder = new X741RequestBuilder();

        String ouId = null;
        if (productArrangement.getFinancialInstitution() != null && !productArrangement.getFinancialInstitution().getHasOrganisationUnits().isEmpty()) {
            ouId = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getOrganisationUnitIdentifer();
        }

        String customerIdentifier = null;
        if (productArrangement.getPrimaryInvolvedParty() != null) {
            customerIdentifier = productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier();
        }

        String taskTypeNarrative = null;
        Integer taskTypeId = null;
        if (!productArrangement.getReferral().isEmpty()) {
            taskTypeNarrative = productArrangement.getReferral().get(0).getTaskTypeNarrative();
            taskTypeId = productArrangement.getReferral().get(0).getTaskTypeId();
        }
        builder.taskCommentBO(ouId, RESOURCE_BROWSER, WORKFLOW_USER_ID_DEFAULT, COMMENT_TYPE_CODE_ONE, getText(productArrangement))
                .partyBO(PARTY_TYPE_PRIMARY, customerIdentifier)
                .taskBO(ORIGINATOR_INTERNET_BANKING, ouId, getCurrentDateAndTime(), getTaskDetailDataList(productArrangement),
                        taskTypeNarrative, taskTypeId, getTransactionId(productArrangement.getArrangementId()));
        return builder.build();
    }

    private String getTransactionId(String arrangementId) {
        String currentDateTimeString = String.valueOf(getCurrentDateAndTime()).replaceAll("[^\\d]", "");
        if (!StringUtils.isEmpty(arrangementId)) {
            return arrangementId + currentDateTimeString;
        } else {
            return currentDateTimeString;
        }
    }

    private TaskDetailDataList getTaskDetailDataList(ProductArrangement productArrangement) {
        TaskDetailDataList taskDetailDataList = new TaskDetailDataList();
        int sequence = 1;
        if (!StringUtils.isEmpty(productArrangement.getArrangementId())) {
            taskDetailDataList.getTaskDetail().add(createTsmTaskDetail(TASK_HEADING_APP_REF_NUM, productArrangement.getArrangementId(), sequence++));
        }
        if (productArrangement.getPrimaryInvolvedParty() != null) {
            if (productArrangement.getPrimaryInvolvedParty().getIsPlayedBy() != null && !productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().isEmpty()) {
                taskDetailDataList.getTaskDetail().add(createTsmTaskDetail(TASK_HEADING_LAST_NAME, productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).getLastName(), sequence++));
            }
            String postCode = getPostCode(productArrangement.getArrangementType(), productArrangement.getPrimaryInvolvedParty().getPostalAddress());
            taskDetailDataList.getTaskDetail().add(createTsmTaskDetail(TASK_HEADING_POSTCODE, postCode, sequence++));
            taskDetailDataList.getTaskDetail().add(createTsmTaskDetail(TASK_HEADING_EMAIL_ADD, productArrangement.getPrimaryInvolvedParty().getEmailAddress(), sequence++));
        }

        String currentDate = FastDateFormat.getInstance("yyyy.MM.dd'G'HH:mm:SS'Z'").format(new Date());
        taskDetailDataList.getTaskDetail().add(createTsmTaskDetail(TASK_HEADING_TIME_DATE_OF_APP, currentDate, sequence++));

        if (ARRANGEMENT_TYPE_LOAN_REFERRAL_AUTOMATION.equalsIgnoreCase(productArrangement.getArrangementType())) {
            for (RuleCondition condition : productArrangement.getConditions()) {
                taskDetailDataList.getTaskDetail().add(createTsmTaskDetail(condition.getName(), String.valueOf(condition.getResult()), sequence++));
            }

            if (productArrangement.getPrimaryInvolvedParty() != null) {
                for (CustomerScore customerScore : productArrangement.getPrimaryInvolvedParty().getCustomerScore()) {
                    taskDetailDataList.getTaskDetail().add(createTsmTaskDetail(TASK_HEADING_CREDIT_SCORE_REQ_NUM, customerScore.getScoreIdentifier(), sequence++));
                }
            }
            if (productArrangement.isLoanRefinanceIndicator() != null) {
                taskDetailDataList.getTaskDetail().add(createTsmTaskDetail(TASK_HEADING_REFINANCE_INDICATOR, String.valueOf(productArrangement.isLoanRefinanceIndicator()), sequence++));
            }
        }
        return taskDetailDataList;
    }

    private TMSTaskDetailBO createTsmTaskDetail(final String tsmTaskType, final String tmsTaskValue, int sequence) {
        TMSTaskDetailBO tmsTaskDetailBO = new TMSTaskDetailBO();
        tmsTaskDetailBO.setHeading(tsmTaskType);
        tmsTaskDetailBO.setSequence(sequence);
        tmsTaskDetailBO.setValue(tmsTaskValue);
        return tmsTaskDetailBO;
    }


    private XMLGregorianCalendar getCurrentDateAndTime() {
        try {
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            return datatypeFactory.newXMLGregorianCalendar(new GregorianCalendar());
        } catch (DatatypeConfigurationException e) {
            LOGGER.info("Error while getting Current Date & Time: " + e);
        }
        return null;
    }

    private String getText(ProductArrangement productArrangement) {
        String lastName = null;
        if (productArrangement.getPrimaryInvolvedParty().getIsPlayedBy() != null && !CollectionUtils.isEmpty(productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName())) {
            lastName = productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).getLastName();
        }
        List<PostalAddress> postalAddressList = productArrangement.getPrimaryInvolvedParty().getPostalAddress();
        String emailAddress = productArrangement.getPrimaryInvolvedParty().getEmailAddress();
        List<CustomerScore> customerScoreList = productArrangement.getPrimaryInvolvedParty().getCustomerScore();
        return getTMSCommentsString(productArrangement.getArrangementId(), lastName, postalAddressList, emailAddress, customerScoreList, productArrangement.isLoanRefinanceIndicator(), productArrangement.getArrangementType());
    }

    private String getTMSCommentsString(String applicationId, String lastName, List<PostalAddress> postalAddressList, String emailAddress,
                                        List<CustomerScore> customerScoreList, Boolean refinanceIndicator, String arrangementType) {
        StringBuffer commentString = new StringBuffer("");
        if (!StringUtils.isEmpty(applicationId)) {
            commentString = commentString.append(TASK_HEADING_APP_REF_NUM + " : " + applicationId + ", ");
        }
        commentString = commentString.append(TASK_HEADING_LAST_NAME + " : " + lastName + ", " + TASK_HEADING_POST_CODE + " : " + getPostCode(arrangementType, postalAddressList) + ", ");
        if (!StringUtils.isEmpty(emailAddress)) {
            commentString = commentString.append(TASK_HEADING_EMAIL_ADD + " : " + emailAddress + ", ");
        }
        commentString = commentString.append(TASK_HEADING_TIME_AND_DATE_OF_APP + " : " + Calendar.getInstance().getTime());
        if (ARRANGEMENT_TYPE_LOAN_REFERRAL_AUTOMATION.equalsIgnoreCase(arrangementType)) {
            if (customerScoreList != null && !customerScoreList.isEmpty() && !StringUtils.isEmpty(customerScoreList.get(0).getScoreIdentifier())) {
                commentString = commentString.append(", " + TASK_HEADING_CREDIT_SCORE_NUM + " : " + customerScoreList.get(0).getScoreIdentifier());
            }
            if (null != refinanceIndicator) {
                commentString = commentString.append(", " + TASK_HEADING_REFINANCE_INDICATOR + " : " + refinanceIndicator);
            }
        }
        return commentString.toString();
    }

    private String getPostCode(String arrangementType, List<PostalAddress> postalAddressList) {
        String postCode = null;
        if (!CollectionUtils.isEmpty(postalAddressList) && null != postalAddressList.get(0).getStructuredAddress()) {
            if (ARRANGEMENT_TYPE_LOAN_REFERRAL_AUTOMATION.equalsIgnoreCase(arrangementType)) {
                postCode = postalAddressList.get(0).getStructuredAddress().getPostCodeOut() + " " + postalAddressList.get(0).getStructuredAddress().getPostCodeIn();
            } else {
                for (PostalAddress postalAddress : postalAddressList) {
                    if (ADDRESS_TYPE_CURRENT.equalsIgnoreCase(postalAddress.getStatusCode())) {
                        postCode = postalAddress.isIsPAFFormat() ?
                                postalAddress.getStructuredAddress().getPostCodeOut() + " " + postalAddress.getStructuredAddress().getPostCodeIn() :
                                postalAddress.getUnstructuredAddress().getPostCode();
                    }
                }
            }
        }
        return postCode != null ? postCode : "";
    }
}