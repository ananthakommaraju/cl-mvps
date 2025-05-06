package com.lloydsbanking.salsa.apaloans.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import lib_sim_bo.businessobjects.Organisation;
import lib_sim_bo.businessobjects.OrganisationUnit;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.Referral;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story01ApaLoansRespondsToRequestTest extends AbstractApaloansJBehaveTestBase {
    ActivateProductArrangementRequest request;
    ActivateProductArrangementResponse response;
    RequestHeader header = null;
    ProductArrangement productArrangement = null;

    @When("The UI calls APALOANS with valid request")
    public void whenTheUICallsAPALOANSWithValidRequest() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        mockScenarioHelper.expectReferenceDataForPAM();
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        request = testDataHelper.createApaRequestForLoans();
        header = testDataHelper.createApaRequestForLoans().getHeader();
        productArrangement = testDataHelper.createApaRequestForLoans().getProductArrangement();
        header.setChannelId(channelId);
        List<ReferralTeams> referralTeamsList = mockScenarioHelper.expectReferralsTeamDetails("PLDLRA(LTSB)");
        setReferralTeamDetailsInProductArrangement(productArrangement, referralTeamsList);
        mockScenarioHelper.expectX741Call(1, productArrangement, header);
        mockControl.go();
        response = apaloansClient.activateProductArrangement(request);
    }

    @Then("APALOANS returns valid response")
    public void thenAPALOANSReturnsValidResponse() {
        mockScenarioHelper.verifyExpectCalls();
        assertNotNull(response);
    }

    private void setReferralTeamDetailsInProductArrangement(ProductArrangement productArrangement, List<ReferralTeams> referralTeamsList) {
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
