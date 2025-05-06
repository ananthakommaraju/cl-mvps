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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story02ApaLoansFetchesChannelIdFromPamTest extends AbstractApaloansJBehaveTestBase {
    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    RequestHeader header;

    ApplicationStatus applicationStatus;

    ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableErrorFaultMsg;

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

    @Given("Contact point id of request is mapped in PAM")
    public void givenContactPointIdOfRequestIsMappedInPAM() {
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

    @Given("Contact point id of request is not mapped in PAM")
    public void givenContactPointIdOfRequestIsNotMappedInPAM() {
        mockScenarioHelper.expectReferenceDataForPAM();
        request = testDataHelper.createApaRequestForLoans();
        request.setHeader(testDataHelper.createApaRequestHeaderWithInvalidContactPoint());
        header = testDataHelper.createApaRequestForLoans().getHeader();
    }

    @When("There is call to APALOANS with valid request")
    public void whenThereIsCallToAPALOANSWithValidRequest() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        mockControl.go();
        try {
            response = apaloansClient.activateProductArrangement(request);
        } catch (ActivateProductArrangementDataNotAvailableErrorMsg errorMsg) {
            this.dataNotAvailableErrorFaultMsg = errorMsg;
        }
    }

    @Then("APALOANS responds")
    public void thenAPALOANSResponds() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("APALOANS returns error and throws exception as dataNotAvailable")
    public void thenAPALOANSReturnsErrorAndThrowsExceptionAsDataNotAvailable() {
        assertNotNull(dataNotAvailableErrorFaultMsg);
        assertEquals("No matching records found, error code: ", dataNotAvailableErrorFaultMsg.getFaultInfo().getDescription());
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
