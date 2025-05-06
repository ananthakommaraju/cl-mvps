package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import com.lloydsbanking.salsa.eligibility.service.utility.wz.ExceptionUtility;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class EligibilityPAMRetriever {
    static final Logger LOGGER = Logger.getLogger(EligibilityPAMRetriever.class);

    ReferenceDataLookUpDao refLookUpDao;

    ExceptionUtility exceptionUtilityWZ;

    RetrievePamService retrievePamService;

    private static final String CONTACT_POINTID_GROUP_CD = "Cnt_Pnt_Prtflio";

    private static final String FULLFILLED_APPLICATIONS = "1010";

    private static final int APPLICATION_PENDING_DAYS = 30;

    private static final String PRODUCT_TYPE_REFINANCE = "106";

    private static final String APPLICATION_RECORD_CREATED = "0001";

    private static final String GROUP_CODE_PARTY_EVIDENCE = "PARTY_EVID_TYPE_CODE";

    @Autowired
    public EligibilityPAMRetriever(ReferenceDataLookUpDao refLookUpDao, ExceptionUtility exceptionUtilityWZ, RetrievePamService retrievePamService) {
        this.refLookUpDao = refLookUpDao;
        this.exceptionUtilityWZ = exceptionUtilityWZ;
        this.retrievePamService = retrievePamService;
    }

    @Transactional(readOnly = true)
    public String getChannelIdFromContactPointId(String contactPointId, RequestHeader requestHeader) throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg {
        List<ReferenceDataLookUp> referenceLookup = refLookUpDao.findByGroupCodeAndLookupValueDesc(CONTACT_POINTID_GROUP_CD, contactPointId);
        if (null == referenceLookup || referenceLookup.isEmpty() || null == referenceLookup.get(0).getChannel()) {
            LOGGER.error("No matching records found in REF_DATA_LOOKUP_VW for contactPointId: " + contactPointId);
            throw exceptionUtilityWZ.dataNotAvailableError(null, "CONTACT_POINT_ID", "REF_DATA_LOOKUP_VW", "No matching records found, error code: ", requestHeader);
        }

        return referenceLookup.get(0).getChannel();

    }

    @Transactional(readOnly = true)
    public List<String> getLookUpValues(String channelId) {
        List<String> lookUpValues = new ArrayList<>();
        List<String> groupCodes = new ArrayList<>();
        groupCodes.add(GROUP_CODE_PARTY_EVIDENCE);
        List<ReferenceDataLookUp> referenceLookUps = refLookUpDao.findByChannelAndGroupCodeIn(channelId, groupCodes);
        if (!CollectionUtils.isEmpty(referenceLookUps)) {
            for (ReferenceDataLookUp referenceDataLookUp : referenceLookUps) {
                lookUpValues.add(referenceDataLookUp.getLookupValueDesc());
            }
        }
        return lookUpValues;
    }

    @Transactional(readOnly = true)
    public Integer getNumberOfFulfilledFinanceApplication(String ocisId) {
        LOGGER.info("Calling RetrievePamService: countOfApplicationExistForCustomer for ocisId: " + ocisId);
        return retrievePamService.countOfApplicationExistForCustomer(ocisId, FULLFILLED_APPLICATIONS, APPLICATION_PENDING_DAYS, PRODUCT_TYPE_REFINANCE, APPLICATION_RECORD_CREATED);
    }

}


