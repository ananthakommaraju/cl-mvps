package com.lloydsbanking.salsa.offer.pam;


import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class LookupDataRetriever {
    private static final Logger LOGGER = Logger.getLogger(LookupDataRetriever.class);

    ReferenceDataLookUpDao referenceDataLookUpDao;

    ExceptionUtility exceptionUtility;


    @Autowired
    public LookupDataRetriever(ReferenceDataLookUpDao referenceDataLookUpDao, ExceptionUtility exceptionUtility) {
        this.referenceDataLookUpDao = referenceDataLookUpDao;
        this.exceptionUtility = exceptionUtility;
    }

    @Transactional(readOnly = true)
    public String getChannelIdFromContactPointId(String contactPointId) throws DataNotAvailableErrorMsg {
        LOGGER.info("Entering RetrieveChannelId with Contact Point ID: " + contactPointId);

        List<ReferenceDataLookUp> lookupList = referenceDataLookUpDao.findByGroupCodeAndLookupValueDesc("Cnt_Pnt_Prtflio", contactPointId);
        if (CollectionUtils.isEmpty(lookupList)) {
            LOGGER.error("No matching records found in PAM DB for Contact Point ID: " + contactPointId);
            throw exceptionUtility.dataNotAvailableError(contactPointId, "CONTACT_POINT_ID", "REF_DATA_LOOKUP_VW", "No matching records found, error code: ");
        }

        LOGGER.info("Exiting RetrieveChannelId with Channel ID: " + lookupList.get(0).getChannel());
        return lookupList.get(0).getChannel();
    }

    @Transactional(readOnly = true)
    public List<ReferenceDataLookUp> getLookupListFromChannelAndGroupCodeList(String channel, List<String> groupCdList) throws DataNotAvailableErrorMsg {
        LOGGER.info("Entering RetrieveLookupValues with Channel ID: " + channel + " and Group Code List: " + groupCdList);

        List<ReferenceDataLookUp> lookupList = referenceDataLookUpDao.findByChannelAndGroupCodeIn(channel, groupCdList);
        if (CollectionUtils.isEmpty(lookupList)) {
            LOGGER.error("No matching records found in PAM DB for Channel ID: " + channel + " and Group Code List: " + groupCdList);
            throw exceptionUtility.dataNotAvailableError(groupCdList.get(0), "GROUP_CODE", "REF_DATA_LOOKUP_VW", "No matching records found, error code: ");
        }

        LOGGER.info("Exiting RetrieveLookupValues");
        return lookupList;
    }

    @Transactional(readOnly = true)
    public List<ReferenceDataLookUp> getLookupListFromChannelAndGroupCodeListAndSequence(String channel, List<String> groupCdList) throws DataNotAvailableErrorMsg {
        List<ReferenceDataLookUp> lookupList = referenceDataLookUpDao.findByChannelAndGroupCodeInOrderBySequenceAsc(channel, groupCdList);
        if (CollectionUtils.isEmpty(lookupList)) {
            throw exceptionUtility.dataNotAvailableError(groupCdList.get(0), "GROUP_CODE", "REF_DATA_LOOKUP_VW", "No matching records found, error code: ");
        }
        return lookupList;
    }

    @Transactional(readOnly = true)
    public List<ReferenceDataLookUp> getLookupListFromGroupCodeAndChannelAndLookUpText(String groupCd, String channel, List<String> lookUpText) throws DataNotAvailableErrorMsg {
        LOGGER.info("Entering RetrieveLookupValues with Channel ID: " + channel + " and Group Code: " + groupCd);

        List<ReferenceDataLookUp> lookupList = referenceDataLookUpDao.findByGroupCodeAndChannelAndLookupTextIn(groupCd, channel, lookUpText);
        if (CollectionUtils.isEmpty(lookupList)) {
            LOGGER.error("No matching records found in PAM DB for Channel ID: " + channel + " and Group Code: " + groupCd);
            throw exceptionUtility.dataNotAvailableError(groupCd, "GROUP_CODE", "REF_DATA_LOOKUP_VW", "No matching records found, error code: ");
        }

        LOGGER.info("Exiting RetrieveLookupValues");
        return lookupList;
    }

}
