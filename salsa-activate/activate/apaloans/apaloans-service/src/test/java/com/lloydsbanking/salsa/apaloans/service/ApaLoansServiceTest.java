package com.lloydsbanking.salsa.apaloans.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.administer.downstream.CreateTask;
import com.lloydsbanking.salsa.activate.administer.downstream.ReferralTeamRetriever;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.apaloans.TestDataHelper;
import com.lloydsbanking.salsa.apaloans.logging.ApaLoansLogService;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import com.lloydsbanking.salsa.downstream.pam.service.CreatePamService;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.Organisation;
import lib_sim_bo.businessobjects.OrganisationUnit;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.Referral;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ApaLoansServiceTest {
    ApaLoansService apaLoansService;

    TestDataHelper testDataHelper;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        apaLoansService = new ApaLoansService();
        apaLoansService.apaLoansLogService = mock(ApaLoansLogService.class);
        apaLoansService.referralTeamRetriever = mock(ReferralTeamRetriever.class);
        apaLoansService.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        apaLoansService.createTask = mock(CreateTask.class);
        apaLoansService.createPamService = mock(CreatePamService.class);
        apaLoansService.requestToResponseHeaderConverter = mock(RequestToResponseHeaderConverter.class);
        testDataHelper = new TestDataHelper();
        apaLoansService.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(apaLoansService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");

    }

    @Test
    public void testActivateProductArrangement() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForLoans();
        Organisation organisation = new Organisation();
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        request.getProductArrangement().setFinancialInstitution(organisation);

        Referral referral = new Referral();
        request.getProductArrangement().getReferral().add(referral);

        List<ReferralTeams> referralTeamsList = new ArrayList<>();
        ReferralTeams referralTeams = new ReferralTeams();
        referralTeams.setTaskType("123");
        referralTeamsList.add(referralTeams);
        when(apaLoansService.referralTeamRetriever.retrieveReferralTeamsForLRA(request.getHeader())).thenReturn(referralTeamsList);
        ActivateProductArrangementResponse activateProductArrangementResponse = apaLoansService.activateProductArrangement(request);
        assertNotNull(activateProductArrangementResponse);
    }
}