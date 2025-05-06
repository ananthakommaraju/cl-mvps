package com.lloydsbanking.salsa.activate.administer.downstream;

import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferralTeamsDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ReferralTeamRetriever {
    private static final Logger LOGGER = Logger.getLogger(ReferralTeamRetriever.class);

    @Autowired
    ReferralTeamsDao referralTeamsDao;

    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;

    private static final String LRA_TASK_NAME_PREFIX = "PLDLRA";
    private static final String BRAND_LTSB = "LTSB";
    private static final String BRAND_HAL = "HAL";

    public List<ReferralTeams> retrieveReferralTeams(List<ReferenceDataLookUp> refLookUpList, RequestHeader header) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<String> taskTypeList = getTaskTypeListFromRefLookUp(refLookUpList);
        LOGGER.info("Entering RetrieveReferralTeams with taskType: " + (!taskTypeList.isEmpty() ? taskTypeList.get(0) : null));
        List<ReferralTeams> referralTeamsList;
        try {
            referralTeamsList = referralTeamsDao.findByTaskTypeInOrderByPriorityAsc(taskTypeList);
        } catch (DataAccessException e) {
            LOGGER.error("Error while retrieving referral Team Details: " + e);
            throw exceptionUtilityActivate.resourceNotAvailableError(header, e.getMessage());
        }
        if (referralTeamsList.isEmpty()) {
            LOGGER.error("Error while retrieving referral Team Details. No Records found.");
            throw exceptionUtilityActivate.dataNotAvailableError(null, null, "No referral team record found", header);
        }
        LOGGER.info("Exiting RetrieveReferralTeams with name | taskType: " + referralTeamsList.get(0).getName() + " | " + referralTeamsList.get(0).getTaskType());
        return referralTeamsList;
    }

    private List<String> getTaskTypeListFromRefLookUp(List<ReferenceDataLookUp> refLookUpList) {
        List<String> lookUpValueDescList = new ArrayList<>();
        for (ReferenceDataLookUp refLookUp : refLookUpList) {
            lookUpValueDescList.add(refLookUp.getLookupValueDesc());
        }
        return lookUpValueDescList;
    }

    public List<ReferralTeams> retrieveReferralTeamsForLRA(RequestHeader header) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        LOGGER.info("Entering RetrieveReferralTeams for LRA application");
        List<ReferralTeams> referralTeamsList;
        String brand = header.getChannelId();
        if (Brand.LLOYDS.asString().equalsIgnoreCase(brand)) {
            brand = BRAND_LTSB;
        } else if (Brand.HALIFAX.asString().equalsIgnoreCase(brand)) {
            brand = BRAND_HAL;
        }
        try {
            referralTeamsList = referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc(LRA_TASK_NAME_PREFIX + "(" + brand + ")");
        } catch (DataAccessException e) {
            LOGGER.error("Error while retrieving referral Team Details. Database Error:  " + e);
            throw exceptionUtilityActivate.resourceNotAvailableError(header, e.getMessage());
        }
        if (referralTeamsList.isEmpty()) {
            LOGGER.error("Error while retrieving referral Team Details. No Records found.");
            throw exceptionUtilityActivate.dataNotAvailableError(null, null, "No referral team record found", header);
        }
        LOGGER.info("Exiting RetrieveReferralTeams with name | taskType: " + referralTeamsList.get(0).getName() + " | " + referralTeamsList.get(0).getTaskType());
        return referralTeamsList;
    }
}
