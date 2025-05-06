package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public class LookUpValueRetriever {
    ReferenceDataLookUpDao referenceDataLookUpDao;

    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    HeaderRetriever headerRetriever;

    private static final List<String> GROUP_CODE_LIST = Arrays.asList(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE, ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE);

    private static final Logger LOGGER = Logger.getLogger(LookUpValueRetriever.class);

    @Autowired
    public LookUpValueRetriever(ReferenceDataLookUpDao referenceDataLookUpDao) {
        this.referenceDataLookUpDao = referenceDataLookUpDao;
    }

    public void retrieveChannelId(String sourceSystemId, RequestHeader header, String arrangementType) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<ReferenceDataLookUp> referenceDateLookUpList = null;
        if (!ActivateCommonConstant.SourceSystemIdentifier.GALAXY_DB_EVENT_SOURCE_SYSTEM_IDENTIFIER.equals(sourceSystemId) || ArrangementType.LOAN_REFERRAL_AUTOMATION.getValue().equals(arrangementType)) {
            ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
            if (contactPoint != null) {
                String contactPointId = contactPoint.getContactPointId();
                LOGGER.info("Entering RetrieveChannelId with Contact Point Id: " + contactPointId);
                try {
                    referenceDateLookUpList = getChannelId(contactPointId, header);
                } catch (DataAccessException e) {
                    LOGGER.error("Exception occurred while calling DB for " + sourceSystemId + ". Returning ResourceNotAvailable Error ", e);
                    throw exceptionUtilityActivate.resourceNotAvailableError(header, e.getMessage());
                }
                if (null == referenceDateLookUpList || referenceDateLookUpList.isEmpty() || null == referenceDateLookUpList.get(0).getChannel()) {
                    LOGGER.info("No matching records found for " + sourceSystemId);
                    throw exceptionUtilityActivate.dataNotAvailableError("CONTACT_POINT_ID", "REF_DATA_LOOKUP_VW", "No matching records found, error code: ", header);
                }
                header.setChannelId(referenceDateLookUpList.get(0).getChannel());
                LOGGER.info("Exiting RetrieveChannelId with Channel ID: " + header.getChannelId());
            }
        }

    }


    public Map<String, Map<String, String>> retrieveEncryptionKeyAndAccountPurposeMap(RequestHeader requestHeader) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        String encryptionKey = null;
        Map<String, String> accountPurposeMap = new HashMap<>();
        Map<String, String> encryptionKeyMap = new HashMap<>();
        Map<String, Map<String, String>> keyAndPurposeMap = new HashMap<>();
        for (ReferenceDataLookUp referenceDataLookUp : retrieveLookUpValues(requestHeader)) {
            if (ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE.equalsIgnoreCase(referenceDataLookUp.getGroupCode()) && null != referenceDataLookUp.getLookupValueDesc()) {
                encryptionKey = referenceDataLookUp.getLookupValueDesc();
                encryptionKeyMap.put(referenceDataLookUp.getGroupCode(), encryptionKey);
                keyAndPurposeMap.put(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE, encryptionKeyMap);
            }
            if (ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE.equalsIgnoreCase(referenceDataLookUp.getGroupCode()) && null != referenceDataLookUp.getLookupValueDesc() && null != referenceDataLookUp.getLookupText()) {
                accountPurposeMap.put(referenceDataLookUp.getLookupText(), referenceDataLookUp.getLookupValueDesc());
                keyAndPurposeMap.put(ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE, accountPurposeMap);
            }
        }
        LOGGER.info("encryptionKey " + encryptionKey);
        return keyAndPurposeMap;
    }

    private List<ReferenceDataLookUp> retrieveLookUpValues(RequestHeader requestHeader) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {

        List<ReferenceDataLookUp> referenceDateLookUpList;
        try {

            referenceDateLookUpList = getLookUpValues(GROUP_CODE_LIST, requestHeader.getChannelId());
        } catch (DataAccessException e) {
            LOGGER.error("Exception occurred while calling DB for " + GROUP_CODE_LIST.get(0) + ". Returning ResourceNotAvailable Error ", e);
            throw exceptionUtilityActivate.resourceNotAvailableError(requestHeader, e.getMessage());
        }

        if (null == referenceDateLookUpList || referenceDateLookUpList.isEmpty()) {
            throw exceptionUtilityActivate.dataNotAvailableError(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE + ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE, "REF_DATA_LOOKUP_VW", "No matching records found, error code:", requestHeader);
        }

        return referenceDateLookUpList;

    }

    @Transactional(readOnly = true)
    public List<ReferenceDataLookUp> getLookUpValues(List<String> groupCodeList, String channelId) {
        return referenceDataLookUpDao.findByChannelAndGroupCodeIn(channelId, groupCodeList);

    }

    @Transactional(readOnly = true)
    public List<ReferenceDataLookUp> getChannelId(String lookupValueDesc, RequestHeader header) {
        return referenceDataLookUpDao.findByGroupCodeAndLookupValueDesc(ActivateCommonConstant.PamGroupCodes.CONTACT_POINTID_GROUP_CD, lookupValueDesc);
    }

    @Transactional(readOnly = true)
    public String retrieveContactPointId(String channelid, RequestHeader header) {
        ArrayList groupCodeList = new ArrayList();
        List<ReferenceDataLookUp> referenceDateLookUpList = new ArrayList<>();
        groupCodeList.add(ActivateCommonConstant.PamGroupCodes.CONTACT_POINTID_GROUP_CD);
        String contactPointId = null;
        try {
            LOGGER.info("Entering RetrieveContactPointId with Channel | GroupCode " + channelid + " | " + groupCodeList.get(0));
            referenceDateLookUpList = referenceDataLookUpDao.findByChannelAndGroupCodeIn(channelid, groupCodeList);
        } catch (DataAccessException e) {
            LOGGER.info("Error while retrieving lookUpValue " + e);
        }
        if (!CollectionUtils.isEmpty(referenceDateLookUpList) && null != referenceDateLookUpList.get(0)) {
            contactPointId = referenceDateLookUpList.get(0).getLookupValueDesc();
        }
        LOGGER.info("Exiting retrieveContactPointID with ContactPointId " + contactPointId);
        return contactPointId;
    }

    @Transactional(readOnly = true)
    public List<ReferenceDataLookUp> retrieveLookUpValues(List<String> lookupValueText, String channelId, List<String> groupCodes) {
        List<ReferenceDataLookUp> referenceDataLookUps = new ArrayList<>();
        if (!StringUtils.isEmpty(groupCodes)) {
            if (StringUtils.isEmpty(lookupValueText)) {
                referenceDataLookUps = referenceDataLookUpDao.findByChannelAndGroupCodeIn(channelId, groupCodes);
            } else {
                referenceDataLookUps = referenceDataLookUpDao.findByChannelAndGroupCodeInAndLookupTextIn(channelId, groupCodes, lookupValueText);
            }
        }
        return referenceDataLookUps;
    }

    @Transactional(readOnly = true)
    public List<ReferenceDataLookUp> retrieveLookUpValues(RequestHeader header, List<String> groupCodes) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<ReferenceDataLookUp> referenceDateLookUpList;
        try {
            referenceDateLookUpList = getLookUpValues(groupCodes, header.getChannelId());
        } catch (DataAccessException e) {
            LOGGER.error("Exception occurred while calling DB for " + groupCodes.get(0) + ". Returning ResourceNotAvailable Error ", e);
            throw exceptionUtilityActivate.resourceNotAvailableError(header, e.getMessage());
        }
        if (null == referenceDateLookUpList || referenceDateLookUpList.isEmpty()) {
            throw exceptionUtilityActivate.dataNotAvailableError(groupCodes.get(0), "REF_DATA_LOOKUP_VW", "No matching records found, error code:", header);
        }
        return referenceDateLookUpList;
    }

    public String populateLookUpData(List<ReferenceDataLookUp> lookUpValues, FinanceServiceArrangement financeServiceArrangement) {
        String previousAddressCountry = populatePostalAddress(financeServiceArrangement.getPrimaryInvolvedParty());
        String countryThreeLetterCd = "";
        for (ReferenceDataLookUp lookUp : lookUpValues) {
            if (previousAddressCountry != null) {
                if (lookUp.getGroupCode().equals(ActivateCommonConstant.PamGroupCodes.ACQUIRE_COUNTRY_GROUP_NAME) && lookUp.getLookupValueDesc().equalsIgnoreCase(previousAddressCountry)) {
                    countryThreeLetterCd = lookUp.getLookupText();
                }
                if (lookUp.getGroupCode().equals(ActivateCommonConstant.PamGroupCodes.ACQUIRE_COUNTRY_GROUP_CODE) && lookUp.getLookupText().equalsIgnoreCase(countryThreeLetterCd)) {
                    previousAddressCountry = lookUp.getLookupValueDesc();
                }
            }
        }
        return previousAddressCountry;
    }

    private String populatePostalAddress(Customer customer) {
        if (customer != null && customer.getPostalAddress() != null) {
            for (PostalAddress postalAddress : customer.getPostalAddress()) {
                if (("PREVIOUS").equalsIgnoreCase(postalAddress.getStatusCode())) {
                    return postalAddress.isIsPAFFormat() ? postalAddress.getStructuredAddress().getCountry() : postalAddress.getUnstructuredAddress().getAddressLine8();
                }
            }
        }
        return null;
    }
}