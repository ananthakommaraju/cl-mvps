package com.lloydsbanking.salsa.activate.constants;

public interface ActivateCommonConstant {

    interface PamGroupCodes {
        String ENCRYPT_KEY_GROUP_CODE = "ENCRYPT_KEY_GROUP";
        String PURPOSE_OF_ACCOUNT_CODE = "PURPOSE_OF_ACCOUNT";
        String CONTACT_POINTID_GROUP_CD = "Cnt_Pnt_Prtflio";
        String PTY_EVIDENCE_GROUP_CODE = "PTY_EVIDENCE_CODE";
        String ADD_EVIDENCE_GROUP_CODE = "ADD_EVIDENCE_CODE";
        String PTY_PURPOSE_GROUP_CODE = "PTY_PURPOSE_CODE";
        String ADD_PURPOSE_GROUP_CODE = "ADD_PURPOSE_CODE";
        String ACQUIRE_COUNTRY_GROUP_CODE = "ACQUIRE_COUNTRY_CODE";
        String ACQUIRE_COUNTRY_GROUP_NAME = "ACQUIRE_COUNTRY_NAME";
    }

    interface SourceSystemIdentifier {
        String GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER = "1";
        String GALAXY_DB_EVENT_SOURCE_SYSTEM_IDENTIFIER = "2";
        String GALAXY_OAP_SOURCE_SYSTEM_IDENTIFIER = "3";
        String GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER = "4";
    }

    interface AppSubStatus {
        String AWAITING_CRS_FULFILLMENT_FAILURE = "1033";
        String STEM_NOT_AVAILABLE = "1023";
        String FAILED_TO_CREATE_OVERDRAFT = "1027";
        String FAILED_TO_UPDATE_NI_NUMBER = "1021";
        String FAILED_TO_UPDATE_EMAIL_ADDRESS = "1030";
        String PARTY_RELATIONSHIP_UPDATE_FAILURE = "1031";
        String CURRENT_ACCOUNT_CREATION_FAILURE = "1024";
        String FAILED_TO_UPDATE_DECISION_TRAILERS = "1025";
        String FAILED_TO_CREATE_CARD_ORDER = "1026";
        String CUSTOMER_DETAILS_UPDATE_FAILURE = "1032";
        String UPDATE_CUSTOMER_RECORD_FAILURE = "1018";
        String ACCOUNT_SWITCHING_FAILURE = "1028";
        String MARKETING_PREF_UPDATE_FAILURE = "1029";
        String STANDING_ORDER_CREATION_FAILURE = "1020";
        String ADD_OMS_OFFERS = "1015";
        String ADD_CARD_HOLDER_FAILURE = "1014";
        String ACQUIRE_CALL_FAILURE = "1016";
        String CARD_CREATION_FAILURE = "1013";
        String APP_SUB_STATUS_PEGA_FAILURE = "1034";
        String SIRA_FAILURE_SUB_STATUS = "5008";
    }

    interface ApplicationType {
        String NEW = "10001";
        String JOINT = "10003";
        String TRADE = "10002";
    }

    interface ProdIdentifier {
        String CLASSIC_ACC_PROD_ID_OLD = "0071";
        String CLASSIC_ACC_PROD_NUMBER_OLD = "449";
        String CLASSIC_ACC_PROD_ID_NEW = "2071";
        String CLASSIC_ACC_PROD_NUMBER_NEW = "2449";
    }

    interface TariffTypes {
        String ONE_YEAR_TARIFF = "1YTRF";
        String SECOND_YEAR_TARIFF = "2YTRF";
        String THIRD_YEAR_TARIFF = "3YTRF";
        String FOURTH_YEAR_TARIFF = "4YTRF";
        String FIFTH_YEAR_TARIFF = "5YTRF";
        String SIXTH_YEAR_TARIFF = "6YTRF";
        String DEFAULT_TARIFF = "NYTRF";
    }

    interface CommunicationTypes {
        String EMAIL = "Email";
        String ATTACHMENT = "AttachmentPDF";
        String SMS = "SMS";
    }

    interface CommunicationSource {
        String STP_SAV_SOURCE = "STPSAVINGS";
    }

    interface TelephoneTypes {
        String MOBILE = "7";
        String BUSINESS = "4";
        String HOME = "1";
    }

    interface InternetBankingMandate {
        int LITE = -4;
        int ULTRALITE = -5;
    }

    interface Operation {
        String OFFLINE = "updateOffline";
        String SAVINGS = "updateSavings";
    }

    interface AsmDecision {
        String ACCEPT = "1";
        String REFERRED = "2";
        String DECLINED = "3";
        String UNSCORED = "4";
    }

    interface ApaPcaServiceConstants {
        String OVERDRAFT_PURPOSE_CREATE = "075";
        String REPAYMENT_SOURCE_14 = "014";
        String HOST_TSB_ACCOUNT = "T";
        String PRODUCT_TYPE_ACCOUNT = "A";
        String CURRENCY_CODE_GBP = "GBP";
        String OVERDRAFT_FAILED_REASON_CODE = "007";
        String OVERDRAFT_FAILED_REASON_TEXT = "Failed to create overdraft";
        String ADDRESS_TYPE_CURRENT = "CURRENT";
        String ADDRESS_TYPE_PREVIOUS = "PREVIOUS";
        String E226_FAILURE_REASON_CODE = "004";
        String E226_FAILURE_REASON_TEXT = "Failed to update decision trailer";
        String ORDER_ACCESS_SERVICE_FAILURE_REASON_CODE = "005";
        String ORDER_ACCESS_SERVICE_FAILURE_REASON_TEXT = "Failed to create card order";
        String CREATE_CASE_FAILURE_REASON_CODE = "1034";
        String CREATE_CASE_FAILURE_REASON_TEXT = "Create Case Failure";
    }

    interface AddressType {
        String CURRENT = "CURRENT";
        String PREVIOUS = "PREVIOUS";
    }

}


