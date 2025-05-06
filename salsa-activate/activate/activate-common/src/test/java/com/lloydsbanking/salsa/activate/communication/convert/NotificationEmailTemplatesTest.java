package com.lloydsbanking.salsa.activate.communication.convert;


import com.lloydsbanking.salsa.UnitTest;
import lib_sim_bo.businessobjects.RuleCondition;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class NotificationEmailTemplatesTest {

    private NotificationEmailTemplates notificationEmailTemplates;
    List<RuleCondition> ruleConditionList;
    RuleCondition ruleCondition;
    private static final int THRESHOLD_VALUE_FOR_CCA_SIGNED_EMAIL_MESSAGE = 2;
    private static final int THRESHOLD_VALUE_FOR_REMINDER_MESSAGE = 15;
    private static final int THRESHOLD_VALUE_FOR_CCA_PENDING_EMAIL_MESSAGE = 1;

    @Before
    public void setUp() {
        notificationEmailTemplates = new NotificationEmailTemplates();
        ruleConditionList = new ArrayList<>();
        ruleCondition = new RuleCondition();
    }

    @Test
    public void testGetNotificationEmailForReferredToBranch() {
        assertEquals("SA_REFERTOBRANCH_MSG", notificationEmailTemplates.getNotificationEmailForReferredToBranch("SA"));
        assertEquals("CA_REFERTOBRANCH_MSG", notificationEmailTemplates.getNotificationEmailForReferredToBranch("CA"));
        assertEquals("REFERTOBRANCH_MSG", notificationEmailTemplates.getNotificationEmailForReferredToBranch("FA"));
    }

    @Test
    public void testGetNotificationEmailForReferred() {
        assertEquals("SA_ONLINETOOFFLINE_MSG", notificationEmailTemplates.getNotificationEmailForReferred("SA"));
        assertEquals("CA_ONLINETOOFFLINE_MSG", notificationEmailTemplates.getNotificationEmailForReferred("CA"));
        assertEquals("ONLINETOOFFLINE_MSG", notificationEmailTemplates.getNotificationEmailForReferred("FA"));
    }

    @Test
    public void testGetNotificationEmailForDeclined() {
        assertEquals("SA_DECLINE_BUREAU_MSG", notificationEmailTemplates.getNotificationEmailForDeclined("SA", "Bureau"));
        assertEquals("CA_DECLINE_BUREAU_MSG", notificationEmailTemplates.getNotificationEmailForDeclined("CA", "Bureau"));
        assertEquals("DECLINE_BUREAU_MSG", notificationEmailTemplates.getNotificationEmailForDeclined("FA", "Bureau"));
        assertEquals("SA_DECLINE_DPLICT_MSG", notificationEmailTemplates.getNotificationEmailForDeclined("SA", "Duplicate application"));
        assertEquals("CA_DECLINE_DPLICT_MSG", notificationEmailTemplates.getNotificationEmailForDeclined("CA", "Duplicate application"));
        assertEquals("DECLINE_DPLICT_MSG", notificationEmailTemplates.getNotificationEmailForDeclined("FA", "Duplicate application"));
        assertEquals("SA_DECLINE_BANK_MSG", notificationEmailTemplates.getNotificationEmailForDeclined("SA", "Bank"));
        assertEquals("CA_DECLINE_BANK_MSG", notificationEmailTemplates.getNotificationEmailForDeclined("CA", "Bank"));
        assertEquals("DECLINE_BANK_MSG", notificationEmailTemplates.getNotificationEmailForDeclined("FA", "Bank"));
    }

    @Test
    public void testGetNotificationEmail() {
        assertEquals("IB_STP_REGISTRATION_SUCCESS_MAIL", notificationEmailTemplates.getNotificationEmailForIBRegistration(-5));
        assertEquals("IB_STP_LITE_REGISTRATION_SUCCESS_MAIL", notificationEmailTemplates.getNotificationEmailForIBRegistration(-4));
        assertNull(notificationEmailTemplates.getNotificationEmailForIBRegistration(1));
    }

    @Test
    public void testWelcomeMessageNotificationEmailForSavingAccountForLinking() {
        ruleCondition.setName("LINKING");
        ruleCondition.setResult("FALSE");
        ruleConditionList.add(ruleCondition);
        assertEquals("SA_WELCOME_MSG", notificationEmailTemplates.getWelcomeMessageNotificationEmailForSavingAccount(ruleConditionList));

    }

    @Test
    public void testWelcomeMessageNotificationEmailForSavingAccount() {
        ruleCondition.setName("LINKING");
        ruleCondition.setResult("TRUE");
        ruleConditionList.add(ruleCondition);
        assertEquals("SA_WELCOME_NOM_MSG", notificationEmailTemplates.getWelcomeMessageNotificationEmailForSavingAccount(ruleConditionList));

    }

    @Test
    public void testNotificationEmailForDifferenceLessThanFive() {
        assertEquals("SA_EIDNV_REMINDER_MSG", notificationEmailTemplates.getNotificationEmailForDifferenceLessThanFive("SA"));
        assertEquals("CA_EIDNV_REMINDER_MSG", notificationEmailTemplates.getNotificationEmailForDifferenceLessThanFive("CA"));
        assertEquals("EIDNV_REMINDER_MSG", notificationEmailTemplates.getNotificationEmailForDifferenceLessThanFive("CC"));

    }

    @Test
    public void testNotificationEmailForAbandonedStatus() {
        assertEquals("SA_APPL_ABANDONED_MSG", notificationEmailTemplates.getNotificationEmailForAbandonedStatus("SA"));
        assertEquals("CA_APPL_ABANDONED_MSG", notificationEmailTemplates.getNotificationEmailForAbandonedStatus("CA"));
        assertEquals("APPL_ABANDND_MSG", notificationEmailTemplates.getNotificationEmailForAbandonedStatus("CC"));

    }

    @Test
    public void testNotificationEmailForCcaSigned() {
        assertEquals("LOANS_DRAWDOWN_POST_CCA_MSG", notificationEmailTemplates.getNotificationEmailForCcaSigned(THRESHOLD_VALUE_FOR_CCA_SIGNED_EMAIL_MESSAGE));
        assertEquals("LOANS_REMINDER_POST_CCA_MSG", notificationEmailTemplates.getNotificationEmailForCcaSigned(THRESHOLD_VALUE_FOR_REMINDER_MESSAGE));
    }

    @Test
    public void testNotificationEmailForCcaPending() {
        assertEquals("LOANS_COMPLETE_PRE_CCA_MSG", notificationEmailTemplates.getNotificationEmailForCcaPending(THRESHOLD_VALUE_FOR_CCA_PENDING_EMAIL_MESSAGE));
        assertEquals("LOANS_REMINDER_PRE_CCA_MSG", notificationEmailTemplates.getNotificationEmailForCcaPending(THRESHOLD_VALUE_FOR_REMINDER_MESSAGE));

    }
}

