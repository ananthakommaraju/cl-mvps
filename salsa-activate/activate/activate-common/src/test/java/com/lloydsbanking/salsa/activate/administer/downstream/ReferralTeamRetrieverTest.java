package com.lloydsbanking.salsa.activate.administer.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferralTeamsDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ReferralTeamRetrieverTest {
    private ReferralTeamRetriever referralTeamRetriever;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;

    @Before
    public void setUp() {
        referralTeamRetriever = new ReferralTeamRetriever();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        referralTeamRetriever.referralTeamsDao = mock(ReferralTeamsDao.class);
        referralTeamRetriever.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
    }

    @Test
    public void testRetrieveReferralTeams() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<ReferenceDataLookUp> refLookUpList = testDataHelper.createLookupData();
        List<ReferralTeams> referralTeamsList = new ArrayList<>();
        referralTeamsList.add(new ReferralTeams());
        when(referralTeamRetriever.referralTeamsDao.findByTaskTypeInOrderByPriorityAsc(any(ArrayList.class))).thenReturn(referralTeamsList);
        assertNotNull(referralTeamRetriever.retrieveReferralTeams(refLookUpList, requestHeader));
    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testRetrieveReferralTeamsThrowsDataNotAvailableErrorMsg() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        DataAccessException dataAccessException = mock(DataAccessException.class);
        List<ReferenceDataLookUp> refLookUpList = testDataHelper.createLookupData();
        when(referralTeamRetriever.referralTeamsDao.findByTaskTypeInOrderByPriorityAsc(any(ArrayList.class))).thenThrow(dataAccessException);
        when(referralTeamRetriever.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class) , any(String.class))).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        referralTeamRetriever.retrieveReferralTeams(refLookUpList, requestHeader);
    }

    @Test(expected = ActivateProductArrangementDataNotAvailableErrorMsg.class)
    public void testRetrieveReferralTeamsForEmptyList() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<ReferenceDataLookUp> refLookUpList = testDataHelper.createLookupData();
        List<ReferralTeams> referralTeamsList = new ArrayList<>();
        when(referralTeamRetriever.referralTeamsDao.findByTaskTypeInOrderByPriorityAsc(any(ArrayList.class))).thenReturn(referralTeamsList);
        when(referralTeamRetriever.exceptionUtilityActivate.dataNotAvailableError(any(String.class), any(String.class), any(String.class), any(RequestHeader.class))).thenThrow(ActivateProductArrangementDataNotAvailableErrorMsg.class);
        referralTeamRetriever.retrieveReferralTeams(refLookUpList, requestHeader);
    }

    @Test
    public void testRetrieveReferralTeamsForLRA() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        requestHeader.setChannelId("LTB");
        List<ReferralTeams> referralTeamsList = new ArrayList<>();
        referralTeamsList.add(new ReferralTeams());
        when(referralTeamRetriever.referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc(any(String.class))).thenReturn(referralTeamsList);
        assertNotNull(referralTeamRetriever.retrieveReferralTeamsForLRA(requestHeader));
    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testRetrieveReferralTeamsLRAThrowsDataNotAvailableErrorMsg() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        requestHeader.setChannelId("HLX");
        DataAccessException dataAccessException = mock(DataAccessException.class);
        when(referralTeamRetriever.referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc(any(String.class))).thenThrow(dataAccessException);
        when(referralTeamRetriever.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class),any(String.class))).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        referralTeamRetriever.retrieveReferralTeamsForLRA(requestHeader);
    }

    @Test(expected = ActivateProductArrangementDataNotAvailableErrorMsg.class)
    public void testRetrieveReferralTeamsLRAForEmptyList() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<ReferralTeams> referralTeamsList = new ArrayList<>();
        when(referralTeamRetriever.referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc((any(String.class)))).thenReturn(referralTeamsList);
        when(referralTeamRetriever.exceptionUtilityActivate.dataNotAvailableError(any(String.class),any(String.class),any(String.class),any(RequestHeader.class))).thenThrow(ActivateProductArrangementDataNotAvailableErrorMsg.class);
        referralTeamRetriever.retrieveReferralTeamsForLRA(requestHeader);
    }
}
