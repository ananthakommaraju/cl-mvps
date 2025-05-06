package com.lloydsbanking.salsa.activate.communication.convert;

import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import com.lloydsbanking.salsa.constant.ArrangementType;
import lib_sim_bo.businessobjects.RuleCondition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationEmailTemplates {

    private static final String DECLINE_SOURCE_BUREAU = "Bureau";
    private static final String DECLINE_SOURCE_DUPLICATE_APPLICATION = "Duplicate application";
    private static final int CUSTOMER_LITE_VALUE = -4;
    private static final int CUSTOMER_ULTRALITE_VALUE = -5;
    private static final String APPLICATION_PARAMETER_LINKING = "LINKING";
    private static final String RESULT_FALSE = "FALSE";
    private static final int THRESHOLD_VALUE_FOR_CCA_PENDING_EMAIL_MESSAGE = 1;
    private static final int THRESHOLD_VALUE_FOR_CCA_SIGNED_EMAIL_MESSAGE = 2;
    private static final int THRESHOLD_VALUE_FOR_REMINDER_MESSAGE = 15;

    public String getNotificationEmailForReferredToBranch(String arrangementType) {
        String notificationEmail;
        if (ArrangementType.SAVINGS.getValue().equalsIgnoreCase(arrangementType)) {
            notificationEmail = EmailTemplateEnum.SA_REFER_TO_BRANCH_EMAIL.getTemplate();
        } else if (ArrangementType.CURRENT_ACCOUNT.getValue().equalsIgnoreCase(arrangementType)) {
            notificationEmail = EmailTemplateEnum.CA_REFERTOBRANCH_MSG.getTemplate();
        } else {
            notificationEmail = EmailTemplateEnum.REFER_TO_BRANCH_EMAIL.getTemplate();
        }
        return notificationEmail;
    }

    public String getNotificationEmailForReferred(String arrangementType) {
        String notificationEmail;
        if (ArrangementType.SAVINGS.getValue().equalsIgnoreCase(arrangementType)) {
            notificationEmail = EmailTemplateEnum.SA_ONLINE_TO_OFFLINE_NOTIFICATION.getTemplate();
        } else if (ArrangementType.CURRENT_ACCOUNT.getValue().equalsIgnoreCase(arrangementType)) {
            notificationEmail = EmailTemplateEnum.CA_ONLINE_TO_OFFLINE_NOTIFICATION.getTemplate();
        } else {
            notificationEmail = EmailTemplateEnum.ONLINE_TO_OFFLINE_NOTIFICATION.getTemplate();
        }
        return notificationEmail;
    }

    public String getNotificationEmailForDeclined(String arrangementType, String declineSource) {
        String notificationEmail;
        if (DECLINE_SOURCE_BUREAU.equalsIgnoreCase(declineSource)) {
            notificationEmail = getNotificationEmailByAccountType(arrangementType, EmailTemplateEnum.SA_BUREAU_DECLINE_EMAIL.getTemplate(), EmailTemplateEnum.CA_DECLINE_BUREAU_MSG.getTemplate(), EmailTemplateEnum.BUREAU_DECLINE_EMAIL.getTemplate());
        } else if (DECLINE_SOURCE_DUPLICATE_APPLICATION.equalsIgnoreCase(declineSource)) {
            notificationEmail = getNotificationEmailByAccountType(arrangementType, EmailTemplateEnum.SA_DUPLICATE_DECLINE_EMAIL.getTemplate(), EmailTemplateEnum.CA_DECLINE_DPLICT_MSG.getTemplate(), EmailTemplateEnum.DUPLICATE_DECLINE_EMAIL.getTemplate());
        } else {
            notificationEmail = getNotificationEmailByAccountType(arrangementType, EmailTemplateEnum.SA_BANK_DECLINE_EMAIL.getTemplate(), EmailTemplateEnum.CA_DECLINE_BANK_MSG.getTemplate(), EmailTemplateEnum.BANK_DECLINE_EMAIL.getTemplate());
        }
        return notificationEmail;
    }

    public String getNotificationEmailForIBRegistration(int accessLevel) {
        String notificationEmail = null;
        if (accessLevel == CUSTOMER_ULTRALITE_VALUE) {
            notificationEmail = EmailTemplateEnum.IB_STP_REGISTRATION_SUCCESS_MAIL.getTemplate();
        } else if (accessLevel == CUSTOMER_LITE_VALUE) {
            notificationEmail = EmailTemplateEnum.IB_STP_LITE_REGISTRATION_SUCCESS_MAIL.getTemplate();
        }
        return notificationEmail;
    }

    private String getNotificationEmailByAccountType(String arrangementType, String templateSA, String templateCA, String templateDefault) {
        String notificationEmail;
        if (ArrangementType.SAVINGS.getValue().equalsIgnoreCase(arrangementType)) {
            notificationEmail = templateSA;
        } else if (ArrangementType.CURRENT_ACCOUNT.getValue().equalsIgnoreCase(arrangementType)) {
            notificationEmail = templateCA;
        } else {
            notificationEmail = templateDefault;
        }
        return notificationEmail;
    }

    public String getWelcomeMessageNotificationEmailForSavingAccount(List<RuleCondition> ruleConditionList) {
        boolean linking = false;
        if (ruleConditionList != null) {
            for (RuleCondition ruleCondition : ruleConditionList) {
                if (APPLICATION_PARAMETER_LINKING.equalsIgnoreCase(ruleCondition.getName()) && !RESULT_FALSE.equalsIgnoreCase(ruleCondition.getResult())) {
                    linking = true;
                    break;
                }
            }
        }
        return linking ? EmailTemplateEnum.SA_WELCOME_LINK.getTemplate() : EmailTemplateEnum.SA_WELCOME_EMAIL.getTemplate();
    }

    public String getNotificationEmailForDifferenceLessThanFive(String arrangementType) {
        String notificationEmail;
        if (arrangementType.equalsIgnoreCase(ArrangementType.SAVINGS.getValue())) {
            notificationEmail = EmailTemplateEnum.SA_EIDNV_REMINDER_EMAIL.getTemplate();
        } else if (arrangementType.equalsIgnoreCase(ArrangementType.CURRENT_ACCOUNT.getValue())) {
            notificationEmail = EmailTemplateEnum.CA_EIDNV_REMINDER_MSG.getTemplate();
        } else {
            notificationEmail = EmailTemplateEnum.EIDNV_REMINDER_EMAIL.getTemplate();
        }
        return notificationEmail;
    }

    public String getNotificationEmailForAbandonedStatus(String arrangementType) {
        String notificationEmail;
        if (arrangementType.equalsIgnoreCase(ArrangementType.SAVINGS.getValue())) {
            notificationEmail = EmailTemplateEnum.SA_APPLICATION_ABANDENED_EMAIL.getTemplate();
        } else if (arrangementType.equalsIgnoreCase(ArrangementType.CURRENT_ACCOUNT.getValue())) {
            notificationEmail = EmailTemplateEnum.CA_APPL_ABANDONED_MSG.getTemplate();
        } else {
            notificationEmail = EmailTemplateEnum.APPLICATION_ABANDENED_EMAIL.getTemplate();
        }
        return notificationEmail;
    }

    public String getNotificationEmailForCcaSigned(int emailDate) {
        String notificationEmail = null;
        if (emailDate == THRESHOLD_VALUE_FOR_CCA_SIGNED_EMAIL_MESSAGE) {
            notificationEmail = EmailTemplateEnum.LOANS_DRAWDOWN_POST_CCA_MSG.getTemplate();
        } else if (emailDate == THRESHOLD_VALUE_FOR_REMINDER_MESSAGE) {
            notificationEmail = EmailTemplateEnum.LOANS_REMINDER_POST_CCA_MSG.getTemplate();
        }
        return notificationEmail;
    }

    public String getNotificationEmailForCcaPending(int emailDate) {
        String notificationEmail = null;
        if (emailDate == THRESHOLD_VALUE_FOR_CCA_PENDING_EMAIL_MESSAGE) {
            notificationEmail = EmailTemplateEnum.LOANS_COMPLETE_PRE_CCA_MSG.getTemplate();
        } else if (emailDate == THRESHOLD_VALUE_FOR_REMINDER_MESSAGE) {
            notificationEmail = EmailTemplateEnum.LOANS_REMINDER_PRE_CCA_MSG.getTemplate();
        }
        return notificationEmail;
    }

}


