package com.lloydsbanking.salsa.apaloans.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import lib_sim_bo.businessobjects.Organisation;
import lib_sim_bo.businessobjects.OrganisationUnit;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.Referral;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story03ApaLoansRetrievesReferralTeamsAndCreateTaskTest extends AbstractApaloansJBehaveTestBase {
    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    RequestHeader header;

    ApplicationStatus applicationStatus;

    ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableErrorMsg;

    ActivateProductArrangementInternalSystemErrorMsg internalSystemErrorMsg;

    ProductArrangement productArrangement;

    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        header = null;
        applicationStatus = new ApplicationStatus("1002", "Approved");
        productArrangement = null;
    }

    @Given("call to retrieveReferralTeamDetails succeeds")
    public void givenCallToRetrieveReferralTeamDetailsSucceeds() {
        mockScenarioHelper.expectReferenceDataForPAM();
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        request = testDataHelper.createApaRequestForLoans();
        header = testDataHelper.createApaRequestForLoans().getHeader();
        header.setChannelId(channelId);
        productArrangement = testDataHelper.createApaRequestForLoans().getProductArrangement();
        List<ReferralTeams> referralTeamsList = mockScenarioHelper.expectReferralsTeamDetails("PLDLRA(LTSB)");
        setReferralTeamDetailsInProductArrangement(productArrangement, referralTeamsList);
        mockScenarioHelper.expectX741Call(1, productArrangement, header);
    }

    @Given("call to retrieveReferralTeamDetails fails")
    public void givenCallToRetrieveReferralTeamDetailsFails() {
        mockScenarioHelper.expectReferenceDataForPAM();
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        request = testDataHelper.createApaRequestForLoans();
        header = testDataHelper.createApaRequestForLoans().getHeader();
        productArrangement = testDataHelper.createApaRequestForLoans().getProductArrangement();
        header.setChannelId(channelId);
        mockScenarioHelper.expectReferralsTeamDetails("PLDLRA(ABC)");
    }

    @Given("tms task creation succeeds")
    public void givenTmsTaskCreationSucceeds() {
        mockScenarioHelper.expectReferenceDataForPAM();
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        request = testDataHelper.createApaRequestForLoans();
        header = testDataHelper.createApaRequestForLoans().getHeader();
        header.setChannelId(channelId);
        productArrangement = testDataHelper.createApaRequestForLoans().getProductArrangement();
        List<ReferralTeams> referralTeamsList = mockScenarioHelper.expectReferralsTeamDetails("PLDLRA(LTSB)");
        setReferralTeamDetailsInProductArrangement(productArrangement, referralTeamsList);
        mockScenarioHelper.expectX741Call(1, productArrangement, header);
    }

    @Given("tms task creation fails")
    public void givenTmsTaskCreationFails() {
        mockScenarioHelper.expectReferenceDataForPAM();
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        request = testDataHelper.createApaRequestForLoans();
        header = testDataHelper.createApaRequestForLoans().getHeader();
        header.setChannelId(channelId);
        productArrangement = testDataHelper.createApaRequestForLoans().getProductArrangement();
        List<ReferralTeams> referralTeamsList = mockScenarioHelper.expectReferralsTeamDetailsWithInvalidTaskType("PLDLRA(LTSB)");
        setReferralTeamDetailsInProductArrangement(productArrangement, referralTeamsList);
        mockScenarioHelper.expectX741Call(null, productArrangement, header);
    }

    @When("there is a call to APALOANS")
    public void whenThereIsCallToAPASAWithValidRequest() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        mockControl.go();
        try {
            response = apaloansClient.activateProductArrangement(request);
        } catch (ActivateProductArrangementDataNotAvailableErrorMsg errorMsg) {
            this.dataNotAvailableErrorMsg = errorMsg;
        } catch (ActivateProductArrangementInternalSystemErrorMsg errorMsg) {
            this.internalSystemErrorMsg = errorMsg;
        }
    }

    @Then("service continues")
    public void thenServiceContinues() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("service set application status as Awaiting Referral Processing")
    public void thenServiceSetApplicationStatusAsAwaitingReferralProcessing() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("service returns data not available error")
    public void thenServiceReturnsDataNotAvailableError() {
        assertNotNull(dataNotAvailableErrorMsg);
    }

    @Then("service returns internal service error")
    public void thenServiceReturnsInternalServiceError() {
        assertNotNull(internalSystemErrorMsg);
    }

    private void setReferralTeamDetailsInProductArrangement(ProductArrangement productArrangement, List<ReferralTeams> referralTeamsList){
        Organisation organisation = new Organisation();
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setOrganisationUnitIdentifer(referralTeamsList.get(0).getOuId());
        organisation.getHasOrganisationUnits().add(organisationUnit);
        productArrangement.setFinancialInstitution(organisation);
        productArrangement.getReferral().add(new Referral());
        productArrangement.getReferral().get(0).setTaskTypeId(Integer.valueOf(referralTeamsList.get(0).getTaskType()));
        productArrangement.getReferral().get(0).setTaskTypeNarrative(referralTeamsList.get(0).getName());
    }

}
