package com.lloydsbanking.salsa.activate.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class CommunicationTemplateSet {

    private static Set<String> emailTemplateSet;

    private CommunicationTemplateSet() {
    }

    static {
        emailTemplateSet = new HashSet<>();
        emailTemplateSet.addAll(Arrays.asList(EmailTemplateEnum.WELCOME_EMAIL.getTemplate(), EmailTemplateEnum.EIDNV_REFERAL_DECLINE_EMAIL.getTemplate(), EmailTemplateEnum.SA_WELCOME_EMAIL.getTemplate(),
                EmailTemplateEnum.SA_FUNDING_REM_EMAIL.getTemplate(), EmailTemplateEnum.SA_FUNDING_REMD_EMAIL.getTemplate(), EmailTemplateEnum.SA_EIDNV_DECLINE_EMAIL.getTemplate(), EmailTemplateEnum.BANK_DECLINE_EMAIL.getTemplate(), EmailTemplateEnum.DUPLICATE_DECLINE_EMAIL.getTemplate(),
                EmailTemplateEnum.BUREAU_DECLINE_EMAIL.getTemplate(), EmailTemplateEnum.SA_BANK_DECLINE_EMAIL.getTemplate(), EmailTemplateEnum.EIDNV_REMINDER_EMAIL.getTemplate(), EmailTemplateEnum.SA_EIDNV_REMINDER_EMAIL.getTemplate(), EmailTemplateEnum.SA_APPLICATION_ABANDENED_EMAIL.getTemplate(),
                EmailTemplateEnum.APPLICATION_ABANDENED_EMAIL.getTemplate(), EmailTemplateEnum.SA_EIDNV_CANCEL_EMAIL.getTemplate(), EmailTemplateEnum.CAR_FINANCE_DD_REMINDER_EMAIL.getTemplate(), EmailTemplateEnum.SA_WELCOME_LINK.getTemplate(), EmailTemplateEnum.REFER_TO_BRANCH_EMAIL.getTemplate(),
                EmailTemplateEnum.SA_REFER_TO_BRANCH_EMAIL.getTemplate(), EmailTemplateEnum.ONLINE_TO_OFFLINE_NOTIFICATION.getTemplate(), EmailTemplateEnum.SA_ONLINE_TO_OFFLINE_NOTIFICATION.getTemplate(), EmailTemplateEnum.CA_DECLINE_BANK_MSG.getTemplate(),
                EmailTemplateEnum.CA_APPL_ABANDONED_MSG.getTemplate(), EmailTemplateEnum.CA_DECLINE_BUREAU_MSG.getTemplate(), EmailTemplateEnum.CA_DECLINE_DPLICT_MSG.getTemplate(), EmailTemplateEnum.CA_EIDNV_DECLINE_MSG.getTemplate(), EmailTemplateEnum.CA_EIDNV_REMINDER_MSG.getTemplate(), EmailTemplateEnum.CA_ONLINETOOFFLINE_MSG.getTemplate(),
                EmailTemplateEnum.CA_REFERTOBRANCH_MSG.getTemplate(), EmailTemplateEnum.CA_WELCOME_MSG.getTemplate(), EmailTemplateEnum.CA_BENEFITS_MSG.getTemplate(), EmailTemplateEnum.CA_WELCOME_MSG_PDF.getTemplate(), EmailTemplateEnum.CA_EIDNV_CANCEL_MSG.getTemplate(), EmailTemplateEnum.LOANS_COMPLETE_PRE_CCA_MSG.getTemplate(),
                EmailTemplateEnum.LOANS_DRAWDOWN_POST_CCA_MSG.getTemplate(), EmailTemplateEnum.LOANS_REMINDER_POST_CCA_MSG.getTemplate(), EmailTemplateEnum.LOANS_REMINDER_PRE_CCA_MSG.getTemplate(), EmailTemplateEnum.LOANS_REMINDER_PRE_CCA_MSG.getTemplate(),
                EmailTemplateEnum.LRA_DECLINE_MSG.getTemplate(), EmailTemplateEnum.LRA_DECLINE_MSG.getTemplate() + "F04", EmailTemplateEnum.LRA_DECLINE_MSG.getTemplate() + "F03", EmailTemplateEnum.LRA_DECLINE_MSG.getTemplate() + "F02", EmailTemplateEnum.LRA_DECLINE_MSG.getTemplate() + "A14", EmailTemplateEnum.LRA_DECLINE_MSG.getTemplate() + "F07",
                EmailTemplateEnum.EIDNV_REFERAL_CANCEL_EMAIL.getTemplate(), EmailTemplateEnum.LRA_ACCEPT_MSG.getTemplate()));
    }

    public static boolean isEmailTemplate(String emailTemplate) {
        return emailTemplateSet.contains(emailTemplate) ? true : false;
    }

}
