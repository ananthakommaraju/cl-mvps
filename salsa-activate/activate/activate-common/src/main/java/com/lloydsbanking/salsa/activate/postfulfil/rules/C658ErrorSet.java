package com.lloydsbanking.salsa.activate.postfulfil.rules;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
@Component
public class C658ErrorSet {

    private static final int OCIS_ERR_SEVERE_ERROR = 160999;

    private static final int OCIS_ILLEGAL_REQUEST = 161013;

    private static final int OCIS_STORAGE_FREE_SPACE_FAILED= 161023;

    private static final int OCIS_ERR_DB2_TIMEOUT = 161031;

    private static final int OCIS_OMI_BUFFER_EXCEEDED = 161033;

    private static final int OCIS_ILLEGAL_OMI_SEGMENT_CREATED = 161034;

    private static final int OCIS_NO_OMI_SEGMENTS = 161035;

    private static final int OCIS_NO_MORE_OMI_SEGMENTS = 161036;

    private static final int OCIS_OMI_SEGMENT_NOT_FOUND = 161037;

    private static final int OCIS_NO_OMI_CONTROL_SEGMENT= 161038;

    private static final int OCIS_OMI_PARTY_SEGMENT_NOT_PRESENT= 161040;

    private static final int OCIS_OMI_NOT_ALLOCATED = 161041;

    private static final int OCIS_OMI_NON_PERSONAL_PARTY_EXISTS = 161052;

    private static final int OCIS_OMI_PERSONAL_PARTY_EXISTS= 161053;

    private static final int OCIS_OMI_LENGTH_INVALID = 161070;

    private static final int USER_ID_MUST_BE_SUPPLIED = 163000;

    private static final int USER_ID_TYPE_CODE_INVALID= 163001;

    private static final int CHANNEL_OUTLET_ID_INVALID= 163002;

    private static final int CHANNEL_OUTLET_TYPE_CODE_INVALID= 163003;

    private static final int EXTERNAL_SYSTEM_ID_NOT_ON_OCIS_REFERENCE_DATA= 163004;

    private static final int NO_PARTY_ID= 163006;

    private static final int DEMERGED_PARTY_ID= 163007;

    private static final int UNKNOWN_EXTERNAL_PARTY_ID = 163009;

    private static final int INVALID_ADDRESS_STATUS_CODE = 165033;

    private static final int UNAVAILABLE_ADDRESS_STATUS_CODE = 165034;

    private static final int DELETE_PARTY_NOT_SUPPORTED = 165089;

    private static final int EMPTY_PARTY_TELEPHONIC_ADDRESS_TYPE = 165107;

    private static final int INVALID_PARTY_TELEPHONIC_ADDRESS_TYPE = 165108;

    private static final int UNAVAILABLE_PARTY_TELEPHONIC_ADDRESS_TYPE = 165109;

    private static final int EMPTY_TELEPHONIC_ADDRESS_TYPE_CODE = 165110;

    private static final int INVALID_TELEPHONIC_ADDRESS_TYPE_CODE = 165111;

    private static final int UNAVAILABLE_TELEPHONIC_ADDRESS_TYPE_CODE = 165112;

    private static final int INVALID_EMAIL_ADDRESS = 165113;

    private static final int INVALID_TELEPHONIC_ADDRESS_TEXT = 165114;

    private Set<Integer> externalServiceErrorSet;

    public C658ErrorSet() {
        populateErrorSet();
    }

    public boolean isExternalServiceError(int errorCode) {
        return externalServiceErrorSet.contains(errorCode) ? true : false;
    }

    private void populateErrorSet() {
        externalServiceErrorSet = new HashSet<>();
        externalServiceErrorSet.addAll(Arrays.asList(EMPTY_PARTY_TELEPHONIC_ADDRESS_TYPE, EMPTY_TELEPHONIC_ADDRESS_TYPE_CODE, INVALID_ADDRESS_STATUS_CODE, INVALID_TELEPHONIC_ADDRESS_TEXT, INVALID_PARTY_TELEPHONIC_ADDRESS_TYPE, UNAVAILABLE_PARTY_TELEPHONIC_ADDRESS_TYPE, INVALID_TELEPHONIC_ADDRESS_TYPE_CODE, UNAVAILABLE_TELEPHONIC_ADDRESS_TYPE_CODE, UNAVAILABLE_ADDRESS_STATUS_CODE, INVALID_EMAIL_ADDRESS, CHANNEL_OUTLET_ID_INVALID, CHANNEL_OUTLET_TYPE_CODE_INVALID, NO_PARTY_ID, OCIS_ERR_SEVERE_ERROR, OCIS_ERR_DB2_TIMEOUT, USER_ID_MUST_BE_SUPPLIED, OCIS_ILLEGAL_REQUEST, OCIS_STORAGE_FREE_SPACE_FAILED, OCIS_OMI_BUFFER_EXCEEDED, OCIS_ILLEGAL_OMI_SEGMENT_CREATED, OCIS_NO_OMI_SEGMENTS, OCIS_NO_MORE_OMI_SEGMENTS, OCIS_OMI_SEGMENT_NOT_FOUND, OCIS_NO_OMI_CONTROL_SEGMENT, OCIS_OMI_PARTY_SEGMENT_NOT_PRESENT, OCIS_OMI_NOT_ALLOCATED, OCIS_OMI_NON_PERSONAL_PARTY_EXISTS, OCIS_OMI_PERSONAL_PARTY_EXISTS, OCIS_OMI_LENGTH_INVALID, USER_ID_TYPE_CODE_INVALID, EXTERNAL_SYSTEM_ID_NOT_ON_OCIS_REFERENCE_DATA, DEMERGED_PARTY_ID, UNKNOWN_EXTERNAL_PARTY_ID, DELETE_PARTY_NOT_SUPPORTED));
    }
}
