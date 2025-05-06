package com.lloydsbanking.salsa.offer.apply.downstream;


import com.lloydsbanking.salsa.downstream.asm.client.f424.F424Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveCreditDecisionRequestFactory;
import com.lloydsbanking.salsa.offer.apply.errorcode.AsmErrorCodes;
import com.lloydsbanking.salsa.offer.apply.errorcode.ConditionCodes;
import com.lloydsbanking.salsa.offer.apply.errorcode.DB2ErrorCodes;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Req;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.BalanceTransfer;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreditDecisionRetriever {


    @Autowired
    RetrieveCreditDecisionRequestFactory f424RequestFactory;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired(required = false)
    F424Client f424Client;

    @Autowired
    HeaderRetriever headerRetriever;

    private static final Logger LOGGER = Logger.getLogger(FraudDecisionRetriever.class);
    private static final String EXTERNAL_BUSINESS_ERROR_CODE = "External Business Error";
    private static final String EXTERNAL_SERVICE_ERROR_CODE = "External Service Error";

    static Set<Integer> externalBusinessErrorCodes = new HashSet<>();
    static Set<Integer> externalServiceErrorCodes = new HashSet<>();

    static {
        externalBusinessErrorCodes.addAll(Arrays.asList(AsmErrorCodes.THE_NUMBER_OF_APPLICANTS_EXCEEDS_THE_CURRENT_SYSTEM_LIMIT_OF_5.getAsmErrorCode(), AsmErrorCodes.NO_MORE_JOINT_APPLICANTS_EXIST.getAsmErrorCode(), AsmErrorCodes.INVALID_COMBINATION_OF_OVERDRAUGHT_START_AND_END_DATES.getAsmErrorCode(), AsmErrorCodes.LIMIT_AMOUNT_NOT_VALID_FOR_FACILITY_TYPE.getAsmErrorCode(), AsmErrorCodes.NO_JOINT_APPLICANTS_EXIST.getAsmErrorCode(), AsmErrorCodes.CAUTION_ENSURE_PRODUCT_HAS_BEEN_UPDATED_IN_LINE_WITH_FACILITY.getAsmErrorCode(), AsmErrorCodes.LIMIT_AMOUNT_MANDATORY_FOR_FACILITY_TYPE.getAsmErrorCode(), AsmErrorCodes.LIMIT_AMOUNT_NOT_INCREMENT_OF_10.getAsmErrorCode(),
                AsmErrorCodes.INVALID_PRODUCT_TYPE_SELECTED_FOR_CURRENT_SYSTEM.getAsmErrorCode(), AsmErrorCodes.LIMIT_AMOUNT_MANDATORY_FOR_PRODUCT_TYPE.getAsmErrorCode(), AsmErrorCodes.NOT_ADDED_BY_APP_SCORE.getAsmErrorCode(), AsmErrorCodes.LIMIT_AMOUNT_MUST_BE_EQUAL_TO_OR_GREATER_THAN.getAsmErrorCode(), AsmErrorCodes.SECOND_APPLICANTS_EXTERNAL_PARTY_ID_SAME_AS_FIRST_APPLICANT.getAsmErrorCode(), AsmErrorCodes.TO_DATE_MUST_BE_LATER_THAN_FROM_DATE.getAsmErrorCode()));

        externalServiceErrorCodes.addAll(Arrays.asList(AsmErrorCodes.GROSS_ANNUAL_INCOME_MUST_BE_NUMERIC.getAsmErrorCode(), AsmErrorCodes.RESIDENTIAL_PROPERTY_VALUE_MUST_BE_NUMERIC.getAsmErrorCode(), AsmErrorCodes.LTSB_CREDIT_CARD_LIMIT_AMOUNT_MUST_BE_NUMERIC.getAsmErrorCode(), AsmErrorCodes.OTHER_NET_ANNUAL_INCOME_AMOUNT_MUST_BE_NUMERIC.getAsmErrorCode(), AsmErrorCodes.INAVLID_VALUE_OF_CURRENT_EMPLOYMENT_DURATION.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_OTHER_BANK_ASSOCIATION_DURATION.getAsmErrorCode(),
                AsmErrorCodes.NET_INCOME_AMOUNT_MUST_BE_NUMERIC.getAsmErrorCode(), AsmErrorCodes.LOAN_COMMITMENT_MONTHLY_AMOUNT_MUST_BE_NUMERIC.getAsmErrorCode(), AsmErrorCodes.MONTHLY_ACCOMODATION_PAYMENT_AMOUNT_MUST_BE_NUMERIC.getAsmErrorCode(), AsmErrorCodes.OUTSTANDING_APPLICATION_MONTHLY_AMOUNT_MUST_BE_NUMERIC.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_DIRECT_DEBIT_INDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_ADDITIONAL_CARDHOLDER_INDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_EMAIL_ADDRESS_INDICATOR.getAsmErrorCode(),
                AsmErrorCodes.INVALID_VALUE_OF_PENSIONABLE_EMPLOYMENT_INDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_SECONDARY_ACCOUNT_INDICATOR.getAsmErrorCode(),
                AsmErrorCodes.MESSAGE_NUMBER_COULD_NOT_BE_FOUND_ON_THE_MESSAGE_TABLE.getAsmErrorCode(), AsmErrorCodes.DUPLICATE_INSERT_ATTEMPTED_IN_DAC_KEY.getAsmErrorCode(), AsmErrorCodes.END_OF_FILE_REACHED_IN_DAC_KEY.getAsmErrorCode(), AsmErrorCodes.RECORD_NOT_FOUND_IN_DAC_KEY.getAsmErrorCode(),
                AsmErrorCodes.BUK1_RECEIVED_WITH_NO_ENHANCEMENT_FLAGS_SET.getAsmErrorCode(), AsmErrorCodes.ERR1_TEXT_FROM_LINKSM.getAsmErrorCode(), AsmErrorCodes.NO_ADDRESSES_FOR_REBUILT_LIST01.getAsmErrorCode(), AsmErrorCodes.NO_ADDRESSES_FOR_INITIAL_LIST01.getAsmErrorCode(), AsmErrorCodes.ADDRESS_PARSE_FAILED.getAsmErrorCode(), AsmErrorCodes.INPUT_ADDRESS_NOT_FOUND_IN_BUREAU_ADDRESS_LIST.getAsmErrorCode(), AsmErrorCodes.BUREAU_TRANSACTION_WAIT_TIME_EXCEEDED_FOR_AGENCY_CODE.getAsmErrorCode(), AsmErrorCodes.PREVIOUS_ADDRESS_VALIDATION_UNKNOWN_RESPONSE_FROM_DELPHI93.getAsmErrorCode(), AsmErrorCodes.SERIOUS_NO_USER_DATA_RETURNED_FROM_DELPHI93.getAsmErrorCode(),
                AsmErrorCodes.CURRENT_ADDRESS_VALIDATION_UNKNOWN_RESPONSE_FROM_DELPHI93.getAsmErrorCode(), AsmErrorCodes.INPUT_PREVIOUS_ADDRESS_NOT_FOUND_FOR_DELPHI193.getAsmErrorCode(), AsmErrorCodes.CURRENT_ADDRESS_VALIDATION_EXPECTING_A_MATCHED_ADDRESS.getAsmErrorCode(), AsmErrorCodes.SERIOUS_INPUT_CURRENT_ADDRESS_NOT_FOUND_FOR_DELPHI93.getAsmErrorCode(), AsmErrorCodes.SERIOUS_INPUT_ADDRESS_SEQ_NO_NOT_FOUND_FOR_DELPHI93.getAsmErrorCode(),
                AsmErrorCodes.TIME_INTERVAL_DATE_PARAMETER_FOR_TRANSACTION.getAsmErrorCode(), AsmErrorCodes.TIME_INTERVAL_EXCEEDED_BY_TRANSACTION.getAsmErrorCode(), AsmErrorCodes.TIME_INTERVAL_PARAMETER_FOR_TRANSACTION.getAsmErrorCode(), AsmErrorCodes.ATTEMPT_TO_MAP_PASSED_END_OF_EQUIFAX_DATA_BUFFER.getAsmErrorCode(), AsmErrorCodes.UNKNOWN_BLOCK_TYPE_FOUND_IN_LIST11.getAsmErrorCode(), AsmErrorCodes.NO_CHECK_ADDRESSES_FOUND_FOR_ADDRESS_RESOLUTION.getAsmErrorCode(), AsmErrorCodes.UNKNOWN_LIST_RECEIVED_FROM_LINKSM.getAsmErrorCode(), AsmErrorCodes.EQUIFAX_INVALID_ADDRESS_MATCHING_IND_IN_PROGRAM_IND.getAsmErrorCode(), AsmErrorCodes.RETRIEVING_DELPHI93_EXPERIAN_DATA_FOR_A_VALIDATED_ADDRESS.getAsmErrorCode(), AsmErrorCodes.INVALID_DATA_TYPE_FOR_MATCHED_ADDRESS.getAsmErrorCode(), AsmErrorCodes.INVALID_MATCHADDRESSINDICATOR_RETURNED_FROM_THE_BUREAU.getAsmErrorCode(), AsmErrorCodes.INVALID_DATA_TYPE_FOR_MULTIMATCHED_ADDRESS.getAsmErrorCode(), AsmErrorCodes.DATE_NOT_IN_DMZ68N01_FUNCTION_CODE_DATE.getAsmErrorCode(),
                AsmErrorCodes.INVALID_FUNCTION_CODE_DATE.getAsmErrorCode(), AsmErrorCodes.OPENING_DMZ68N01_FUNCTION_CODE_DATE_2.getAsmErrorCode(), AsmErrorCodes.OPENING_DMZ68N01_FUNCTION_CODE_DATE_1.getAsmErrorCode(), AsmErrorCodes.THE_CRITERIA_REQUIRED_TO_SEARCH_FOR_A_TBF_SUBJECT_WAS_NOT_SUPPLIED.getAsmErrorCode(), AsmErrorCodes.NO_CS_AGENCY_DETAILS_FOUND_ON_T000OY0_ALTHOUGH_THE_APPLICATION_IS_A_REPROCESS.getAsmErrorCode(), AsmErrorCodes.THE_INSERT_TO_TABLE_T000PS0_HAS_FAILED_AS_THE_CS_ID_ALREADY_EXISTS_ON_THE_CSDB.getAsmErrorCode(),
                AsmErrorCodes.UNEXPECTED_DUPLICATE_INSERT_ON_TABLE_IN_PROGRAM.getAsmErrorCode(), AsmErrorCodes.NO_CS_SEM_DETAILS_FOUND_ON_T001XK0_ALTHOUGH_THE_APPLICATION_IS_A_REPROCESS.getAsmErrorCode(), AsmErrorCodes.THE_NUMBER_OF_ROWS_SELECTED_FROM_TABLE_EXCEEDS_THE_PERMISSIBLE_OUTPUT.getAsmErrorCode(), AsmErrorCodes.INVALID_REQUEST_TYPE_FOR_TIMED_TRANSACTION_MONITOR_PROGRAM.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_OTHERBANKINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_REVERTOVERDRAFTLIMITINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_BUSINESSTELEPHONEINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_NUMBER_OF_MONTHS_IN_OTHERBANKASSOCIATIONDURATION.getAsmErrorCode(), AsmErrorCodes.MANDATORY_DATA_ITEM_NOT_POPULATED_COMPANYGROUPCODE.getAsmErrorCode(), AsmErrorCodes.MANDATORY_DATA_ITEM_NOT_POPULATED_CUSTOMERLENDINGPRIORITYCODE.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_LTSBMORTGAGEHELDINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_MANDATEDSALARYINDICATOR.getAsmErrorCode(),
                AsmErrorCodes.INVALID_VALUE_OF_IN_CHARGECARDHELDINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_STORECARDHELDINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_REQUEST.getAsmErrorCode(), AsmErrorCodes.PF4_OR_PF5_REQUIRED.getAsmErrorCode(), AsmErrorCodes.NOT_ALL_OBJECT_CODES_DISPLAYED.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_OTHERLTSBCARD_CLOSUREIDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_OTHERBANKOTHERACCOUNTINDICATOR.getAsmErrorCode(), AsmErrorCodes.MANDATORY_DATA_ITEM_NOT_POPULATED_BANKCODE.getAsmErrorCode(), AsmErrorCodes.MANDATORY_DATA_ITEM_NOT_POPULATED_MARKETING_SOURCE_CODE.getAsmErrorCode(),
                AsmErrorCodes.DELPHI96_DATA_VALIDATION_PROGRAM_CODE.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_RETURNED_CHEQUES_INDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_DEBT_BUY_IN_INDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_DIRECT_MARKETING_INDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_CARD_PROTECTION_INDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_INSURANCE_PROTECTION_INDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_UNFORMATTEDADDRESSINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_FORMATTEDADDRESSINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_LTSBCHEQUECARDHELDINDICATOR.getAsmErrorCode(), AsmErrorCodes.DUPLICATE_KEY_WARNING_FOR_3_ATTEMPTS_TO_INSERT_TBF_SUBJECT.getAsmErrorCode(), AsmErrorCodes.SERIOUS_SYSTEM_ABOUT_TO_INSERT_TBF_WITHOUT_A_SUBJECT.getAsmErrorCode(), AsmErrorCodes.ATTEMPTED_TO_DELETE_A_TEMPORARY_CS_HOLDING_RECORD_OUT_OF_RANGE.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_INCOMEEXPENDITUREINDICATOR.getAsmErrorCode(), AsmErrorCodes.A_BUREAU_HAS_OCCURRED_PLEASE_SEE_MESSAGE_LOG.getAsmErrorCode(), AsmErrorCodes.UNEXPECTED_INSERT_ON_TBF_DATABASE_IN_PROGRAM.getAsmErrorCode(), AsmErrorCodes.NO_SPACE_AVAILABLE_ON_THE_TEMPORARY_CREDIT_SCORE_DATABASE_HOLDING_FILE.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_INDICATIVEQUOTEINDICATOR.getAsmErrorCode(), AsmErrorCodes.A_CICS_HAS_OCCURRED_PLEASE_SEE_MESSAGE_LOG.getAsmErrorCode(), AsmErrorCodes.INVALID_REQUEST_PASSED_TO_THE_COMMON_DATE_HANDLER_JP0231A.getAsmErrorCode(), AsmErrorCodes.MANDATORY_DATA_ITEM_NOT_POPULATED_PURCHASEITEMAMOUNT.getAsmErrorCode(), AsmErrorCodes.AN_SQL_HAS_OCCURRED_PLEASE_SEE_MESSAGE_LOG.getAsmErrorCode(),
                AsmErrorCodes.ONLY_A_FORMATTED_OR_UNFORMATTED_ADDRESS_MUST_BE_SUPPLIED_NOT_BOTH.getAsmErrorCode(), AsmErrorCodes.UNKNOWN_BUREAU_CODE_IN_OPERATION_CODE.getAsmErrorCode(), AsmErrorCodes.UNKNOWN_SEM_CODE_IN_OPERATION_CODE.getAsmErrorCode(), AsmErrorCodes.NO_DATA_EXISTS_FOR_2ND_APPLICANT.getAsmErrorCode(), AsmErrorCodes.UNKNOWN_CICS_CODE_IN_OPERATION_CODE.getAsmErrorCode(), AsmErrorCodes.INVALID_NUMBER_OF_MONTHS_IN_ADDRESSRESIDENCEDURATION.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_OF_IN_ADDRESSTARGETINGINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_IN_OTHERBANKCURRENTACCOUNTIND.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_IN_CVRMRELEMPLOYMENTINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_IN_CVRMEMPLOYMENTINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_IN_ADDRESSCORRECTIONINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_IN_OTHERBANKLOANINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_IN_OTHERBANKDEBITCREDITINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_IN_OTHERBANKCHEQUECREDITIND.getAsmErrorCode(), AsmErrorCodes.INVALID_VALUE_IN_OTHERBANKDEPOSITACCOUNTINDICATOR.getAsmErrorCode(), AsmErrorCodes.INVALID_REASON_CODE.getAsmErrorCode(), AsmErrorCodes.NO_DATA_EXISTS_NO_FIELD_IS_CASE_SENSITIVE.getAsmErrorCode(), AsmErrorCodes.NO_DATA_EXISTS_NB_FIELD_IS_CASE_SENSITIVE.getAsmErrorCode(), AsmErrorCodes.ATTEMPT_TO_CANCEL_DELAY_FAILED_IN_JP0303A.getAsmErrorCode(), AsmErrorCodes.MANDATORY_DATA_ITEM_NOT_POPULATED_HOLDING_BRANCH_SORT_CODE.getAsmErrorCode(), AsmErrorCodes.DUPLICATE_EXTERNAL_SYSTEM_ID_FOUND_FOR_APPLICANT.getAsmErrorCode(),
                AsmErrorCodes.DATASOAP_FAILED_IN_MODULE_CODE_OFFSET.getAsmErrorCode(),
                DB2ErrorCodes.A_SINGLETON_SELECT_FOUND_MORE_THAN_1_ROW_IN_MODULE.getDB2ErrorCode(), DB2ErrorCodes.UNAVAILABLE_DB2_RESOURCE_IN_MODULE.getDB2ErrorCode(), DB2ErrorCodes.UNIT_OF_WORK_ROLLED_BACK_DUE_TO_DEADLOCK_OR_TIMEOUT_IN_MODULE.getDB2ErrorCode(), DB2ErrorCodes.UNSUCCESSFUL_EXECUTION_CAUSED_BY_DEADLOCK_OR_TIMEOUT_IN_MODULE.getDB2ErrorCode(), DB2ErrorCodes.INVALID_DATE_TIME_VALUE_IN_MODULE.getDB2ErrorCode(), DB2ErrorCodes.INVALID_STRING_REPRESENTATION_OF_A_DATE_OR_TIME_VALUE_IN_MODULE.getDB2ErrorCode(), DB2ErrorCodes.DB2_TIMESTAMP_ERROR_IN.getDB2ErrorCode(), DB2ErrorCodes.MODULE_NOT_IN_DB2_PLAN_OR_NEW_VERSION_NOT_BOUND.getDB2ErrorCode(), DB2ErrorCodes.USER_ID_ERROR_CONDITION_OCCURED_IN_COMMAND.getDB2ErrorCode(),
                ConditionCodes.ERROR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.RDATT_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.WRBRK_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.EOF_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.EODS_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.EOC_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.INBFMH_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.ENDINPT_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NONVAL_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NOSTART_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.TERMIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.FILENOTFOUND_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NOTFND_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.DUPREC_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.DUPKEY_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.INVREQ_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(),
                ConditionCodes.IOERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NOSPACE_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NOTOPEN_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.ENDFILE_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.LENGERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(),
                ConditionCodes.ILLOGIC_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.QZERO_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.SIGNAL_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.QBUSY_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.ITEMERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.PGMIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.TRANSIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.ENDDATA_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.EXPIRED_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.RETPAGE_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.RTEFAIL_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.RTESOME_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(),
                ConditionCodes.TSIOERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.MAPFAIL_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.INVERRTERM_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.INVMPSZ_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.IGREQID_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.OVERFLOW_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.INVLDC_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NOSTG_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.JIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.QIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NOJBUFSP_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.DSSTAT_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.SELNERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.FUNCERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.UNEXPIN_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NOPASSBKRD_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(),
                ConditionCodes.NOPASSBKWR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.SYSIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.ISCINVREQ_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.SESSIONERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.SYSBUSY_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.SESSBUSY_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NOTALLOC_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.CBIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.INVEXITREQ_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.INVPARTNSET_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.INVPARTN_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NOTAUTH_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.SUPPRESSED_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NOSPOOL_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.TERMERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.ROLLEDBACK_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.DISABLED_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.ALLOCERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.STRELERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.OPENERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.SPOLBUSY_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.SPOLERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NODEIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.TASKIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(),
                ConditionCodes.TCIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.DSNNOTFOUND_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.LOADING_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.MODELIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.OUTDESCERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.PARTNERIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.PROFILEIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(), ConditionCodes.NETNAMEIDERR_CONDITION_OCCURRED_IN_COMMAND.getErrorCode(),
                ConditionCodes.UNDEFINED_CONDITION_52.getErrorCode(), ConditionCodes.UNDEFINED_CONDITION_83.getErrorCode()
        ));
    }

    public F424Resp retrieveCreditDecision(String contactPointId, String arrangementId, String guaranteedOfferCode, String productIdentifier,
                                           String productEligibilityTypeCode, String subChannelCode, String affiliateIdentifier, Customer primaryInvolvedParty,
                                           String arrangementType, Boolean marketingPreferenceIndicator, List<BalanceTransfer> balanceTransfer, CurrencyAmount totalBalTrnsfrAmtList, RequestHeader requestHeader) throws ExternalServiceErrorMsg, ExternalBusinessErrorMsg, ResourceNotAvailableErrorMsg {
        String directDebitIn = (balanceTransfer != null && !balanceTransfer.isEmpty()) ? "Y" : "N";
        F424Req creditDecisionRequest = f424RequestFactory.create(contactPointId, arrangementId, guaranteedOfferCode, productIdentifier,
                productEligibilityTypeCode, subChannelCode, affiliateIdentifier, primaryInvolvedParty,
                arrangementType, marketingPreferenceIndicator, directDebitIn, totalBalTrnsfrAmtList);
        F424Resp creditDecisionResponse = retrieveF424Response(creditDecisionRequest, requestHeader);
        if (isError(creditDecisionResponse)) {
            throwErrorInResponse(creditDecisionResponse.getF424Result());
        }
        return creditDecisionResponse;
    }

    private void throwErrorInResponse(F424Result f424Result) throws ExternalServiceErrorMsg, ExternalBusinessErrorMsg {
        int reasonCode = f424Result.getResultCondition().getReasonCode();
        if (isExternalServiceError(reasonCode)) {
            throw exceptionUtility.externalServiceError(EXTERNAL_SERVICE_ERROR_CODE, f424Result.getResultCondition().getReasonCode() + ":" + f424Result.getResultCondition().getReasonText());
        } else if (isExternalBusinessError(reasonCode)) {
            throw exceptionUtility.externalBusinessError(EXTERNAL_BUSINESS_ERROR_CODE, f424Result.getResultCondition().getReasonCode() + ":" + f424Result.getResultCondition().getReasonText());
        }
    }

    private boolean isExternalServiceError(Integer reasonCode) {
        if (externalServiceErrorCodes.contains(reasonCode)) {
            return true;
        }
        return false;
    }

    private boolean isExternalBusinessError(Integer reasonCode) {
        if (externalBusinessErrorCodes.contains(reasonCode)) {
            return true;
        }
        return false;
    }


    private boolean isError(F424Resp response) {
        if (null != response && isResultConditionCheck(response)) {
            return true;
        }
        return false;
    }

    private boolean isResultConditionCheck(F424Resp response) {
        return null != response.getF424Result() && response.getF424Result().getResultCondition() != null
                && null != response.getF424Result().getResultCondition().getReasonCode()
                && 0 != response.getF424Result().getResultCondition().getReasonCode();
    }

    private F424Resp retrieveF424Response(F424Req creditDecisionRequest, RequestHeader header) throws ResourceNotAvailableErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header);
        F424Resp creditDecisionResponse = null;
        try {
            LOGGER.info("Calling ASM F424 retrieveCreditDecision ");
            creditDecisionResponse = f424Client.f424(creditDecisionRequest, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            //catching exceptions while calling client to throw ResourceNotAvailableError only in that case
            LOGGER.error("Exception occurred while calling ASM F424. Returning ResourceNotAvailableError ;", e);
            throw exceptionUtility.resourceNotAvailableError(e.getMessage());
        }
        return creditDecisionResponse;
    }
}
