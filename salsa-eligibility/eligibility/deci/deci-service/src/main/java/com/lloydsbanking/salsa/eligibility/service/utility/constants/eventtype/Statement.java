package com.lloydsbanking.salsa.eligibility.service.utility.constants.eventtype;


public interface Statement {

    String STMT_GET_SHEET = "14";

    String STMT_SUPPRESSION_GET = "15";

    String STMT_PAPER_SUPPRESS = "16";

    String STMT_COPY_SHT_REF_GET = "17";

    String STMT_COPY_ORDER = "18";

    String STMT_COPY_COST = "19";

    String STMT_SEARCH = "20";

    String STATEMENT_SUMMARY = "541";

    String GET_STATEMENT_SHEET = "620";

    String SEARCH_STATEMENT = "621";

    String STATEMENT_DEFAULT = "685";

    String STMT_GET_ACC_CHARGES = "516";

    String STMT_ENTRY_READ_LIST = "12";

    String CARD_STMT_ENTRY_READ_LIST = "13";

    String STM_RISK = "382";

    String STM_RISK_ASYNC = "384";

    String STM_NOTIFY_LOGON = "387";

    String STM_NOTIFY_LOGON_ASYNC = "406";

    String STM_READ_DELAY_RECORDS = "417";

    String STM_SUSPEND_MANDATES = "418";

    String STM_COUNT_DELAYED_TRX = "419";

    String STM_UPDATE_DELAYED_TRX = "420";

    String STM_CHECK_DELAYED_TRX = "421";
}
