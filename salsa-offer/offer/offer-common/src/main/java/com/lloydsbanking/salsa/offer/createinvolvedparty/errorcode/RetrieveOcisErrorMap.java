package com.lloydsbanking.salsa.offer.createinvolvedparty.errorcode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RetrieveOcisErrorMap {

    private static final Map<Integer, String> F062_ERROR_MAP;

    private static final String EXTERNAL_BUSINESS_ERROR = "External Business Error";

    private static final String EXTERNAL_SERVICE_ERROR = "External Service Error";


    public String getOcisErrorCode(Integer i) {
        return F062_ERROR_MAP.get(i);
    }

    static {
        Map<Integer, String> f062ErrorMap = new HashMap<>();
        f062ErrorMap.put(OcisErrorCodes.CHANNEL_OUTLET_ID_INVALID_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_EVIDENCE_PURPOSE_TYPE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_OUID_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_SPECIAL_MAILING_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_ADDRESS_LIFECYCLE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_TELLEPHONE_STATUS_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_TELLEPHONE_DEVICE_TYPE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_POST_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_UNSTRUCTURED_ADDRESS_TYPE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_COMMS_PREFERENCE_TYPE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_COMMS_PREFERENCE_OPTION.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_INTER_PARTY_RELATIONSHIPS_TYPE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_PARTY_ADDRESS_TYPE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_UNSTRUCTURED_ADDRESS_ID.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_ADDRESS_STATUS_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_PARTY_TYPE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_PARTY_STATUS_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_DECEASED_DATE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_DEATH_NOTIFIED_DATE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_NATIONAL_INSURANCE_NUMBER.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_EVIDENCE_TYPE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_EVIDENCE_PURPOSE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.MISSING_PARTY_TYPE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.MISSING_EVIDENCE_PURPOSE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.MISSING_COMMS_PREFERENCE_TYPE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.MISSING_COMMS_PREFERENCE_OPTION.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.MISSING_PARTY_TELEPHONE_TYPE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.EMPTY_EVIDENCE_TYPE_TYPE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.CHANNEL_OUTLET_ID_INVALID_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.CHANNEL_OUTLET_TYPE_INVALID_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_PARTY_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_COUNTRY_OF_RESIDENCE_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_NATIONALITY_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.USER_ID_NOT_SUPPLIED_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.EXTERNAL_SYSTEM_ID_NOT_PRESENT_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.PARTY_ID_NOT_SUPPLIED_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.PARTY_NOT_IN_OCIS_MASTER_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNKNOWN_EXTERNAL_PARTY_ID_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.RETRY_DUE_TO_DB_TIMEOUT.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNAVAILABLE_EVIDENCE_TYPE.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNAVAILABLE_COMMS_PREFERENCE_OPTION.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNAVAILABLE_INTER_PARTY_RELATIONSHIPS_TYPE_CODE.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNAVAILABLE_INTER_PARTY_TELEPHONE_TYPE_CODE.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNAVAILABLE_TELEPHONE_STATUS_CODE.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNAVAILABLE_TELEPHONE_DEVICE_TYPE_CODE.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNAVAILABLE_PARTY_TYPE_CODE.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNAVAILABLE_EVOIDENCE_PURPOSE_CODE.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.SEVERE_ERROR_OCCURED_CODE.getOcisErrorCode(), EXTERNAL_SERVICE_ERROR);
        f062ErrorMap.put(OcisErrorCodes.RETRY_DUE_TO_DB_TIMEOUT.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.INVALID_UNSTRUCTURED_ADDRESS_ID_CODE.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNAVAILABLE_ADDRESS_STATUS_CODE.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNAVAILABLE_SPECIAL_MAILING_CODE.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);
        f062ErrorMap.put(OcisErrorCodes.UNKNOWN_PRODUCT_HELD_ID.getOcisErrorCode(), EXTERNAL_BUSINESS_ERROR);


        F062_ERROR_MAP = Collections.unmodifiableMap(f062ErrorMap);

    }
}
