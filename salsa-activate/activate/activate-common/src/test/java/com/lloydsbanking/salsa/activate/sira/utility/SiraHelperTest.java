package com.lloydsbanking.salsa.activate.sira.utility;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.exception.ExceptionUtility;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationActivityHistoryDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationActivityHistory;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class SiraHelperTest {
    private SiraHelper siraHelper;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        siraHelper = new SiraHelper();
        siraHelper.referenceDataLookUpDao = mock(ReferenceDataLookUpDao.class);
        testDataHelper = new TestDataHelper();
        siraHelper.exceptionUtility = mock(ExceptionUtility.class);
        siraHelper.applicationActivityHistoryDao = mock(ApplicationActivityHistoryDao.class);
        siraHelper.dateFactory = new DateFactory();
    }

    @Test
    public void testGetLookupListFromChannelAndGroupCodeListAndSequence() throws DataNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("SIRA");
        request.getProductArrangement().getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        request.getProductArrangement().setArrangementId("123");
        List<ReferenceDataLookUp> referenceDataLookUpListExpected = new ArrayList<>();
        referenceDataLookUpListExpected.add(new ReferenceDataLookUp());
        referenceDataLookUpListExpected.get(0).setLookupValueDesc("0");
        when(siraHelper.referenceDataLookUpDao.findByChannelAndGroupCodeInOrderBySequenceAsc(request.getHeader().getChannelId(), groupCodeList)).thenReturn(referenceDataLookUpListExpected);
        assertFalse(siraHelper.getLookupListFromChannelAndGroupCodeListAndSequence(request.getHeader().getChannelId(), groupCodeList).isEmpty());
    }

    @Test
    public void testSetAppDate() {
        ApplicationActivityHistory applicationActivityHistory = new ApplicationActivityHistory();
        applicationActivityHistory.setDateModified(new Date());
        List<ApplicationActivityHistory> applicationActivityHistoryList = new ArrayList<>();
        applicationActivityHistoryList.add(applicationActivityHistory);
        when(siraHelper.applicationActivityHistoryDao.findByApplicationsIdAndApplicationStatusStatusInOrderByDateModifiedAsc(Long.valueOf("123"), Arrays.asList("1001"))).thenReturn(applicationActivityHistoryList);
        assertNotNull(siraHelper.setAppDate("123"));
        when(siraHelper.applicationActivityHistoryDao.findByApplicationsIdAndApplicationStatusStatusInOrderByDateModifiedAsc(Long.valueOf("123"), Arrays.asList("1001"))).thenReturn(new ArrayList<ApplicationActivityHistory>());
        assertNull(siraHelper.setAppDate("123"));
    }

    @Test
    public void testSetFulfilmentDate() {
        ApplicationActivityHistory applicationActivityHistory = new ApplicationActivityHistory();
        applicationActivityHistory.setDateModified(new Date());
        List<ApplicationActivityHistory> applicationActivityHistoryList = new ArrayList<>();
        applicationActivityHistoryList.add(applicationActivityHistory);
        when(siraHelper.applicationActivityHistoryDao.findByApplicationsIdAndApplicationStatusStatusInOrderByDateModifiedAsc(Long.valueOf("123"), Arrays.asList("1010"))).thenReturn(applicationActivityHistoryList);
        assertNotNull(siraHelper.setFulfilmentDate("1010", "123"));
        assertNotNull(siraHelper.setFulfilmentDate("1009", "123"));
    }

    @Test(expected = DataNotAvailableErrorMsg.class)
    public void testGetLookupListFromChannelAndGroupCodeListAndSequenceWhenListIsEmpty() throws DataNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("SIRA");
        request.getProductArrangement().getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        request.getProductArrangement().setArrangementId("123");
        when(siraHelper.exceptionUtility.dataNotAvailableError(any(String.class), any(String.class), any(String.class), any(String.class))).thenThrow(DataNotAvailableErrorMsg.class);
        when(siraHelper.referenceDataLookUpDao.findByChannelAndGroupCodeInOrderBySequenceAsc(request.getHeader().getChannelId(), groupCodeList)).thenReturn(new ArrayList<ReferenceDataLookUp>());
        siraHelper.getLookupListFromChannelAndGroupCodeListAndSequence(request.getHeader().getChannelId(), groupCodeList);
    }
}
