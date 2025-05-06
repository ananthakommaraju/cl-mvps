package com.lloydsbanking.salsa.ppae.service.constant;


public interface PPAEServiceConstant {

    String SERVICE_NAME = "http://www.lloydstsb.com/Schema/Enterprise/LCSM_ArrangementNegotiation}ArrangementSetupService";

    String SERVICE_ACTION = "offerProductArrangement";

    String BUSINESS_TRANSACTION = "ProcessPendingArrangementEvent";

    String TELEPHONE_TYPE_UK = "7";

    int TELEPHONE_NUMBER_LENGTH_UK = 10;

    String SYSTEM_CODE = "00107";

    String SOURCE_SYSTEM_ID = "3";

    String PARTY_IDENTIFIER = "AAGATEWAY";

    String CREDIT_CARD_OFFERED_FLAG_FEAT = "CREDIT_CARD_OFFERED_FLAG";

    String DEBIT_CARD_RISK_CODE = "DEBIT_CARD_RISK_CODE";

    String OVERDRAFT_RISK_CODE = "OVERDRAFT_RISK_CODE";

    String CREDIT_CARD_LIMIT_AMOUNT_FEAT = "CREDIT_CARD_LIMIT_AMOUNT";

    //PAD Status
    String PAD_SATUS_QUOTATION_PENDING = "3";
    String PAD_SATUS_QUOTATION_GIVEN = "4";
    String PAD_STATUS_CCA_SIGNED = "6";
    String PAD_STATUS_OPEN = "7";

    //Application Status Code
    String APP_STATUS_CCA_SIGNED = "1015";
    String APP_STATUS_CCA_PENDING = "1016";
}
