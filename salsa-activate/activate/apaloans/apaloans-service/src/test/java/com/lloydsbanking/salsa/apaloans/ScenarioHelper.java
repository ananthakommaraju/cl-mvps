package com.lloydsbanking.salsa.apaloans;

import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ScenarioHelper {
    void clearUp();

    @Transactional
    public String expectChannelIdByContactPointID();

    void expectX741Call(Integer taskId, ProductArrangement productArrangement, RequestHeader header);

    @Transactional
    public void expectReferenceDataForPAM();

    @Transactional
    public List<ReferralTeams> expectReferralsTeamDetails(String name);

    @Transactional
    public List<ReferralTeams> expectReferralsTeamDetailsWithInvalidTaskType(String name);

    void verifyExpectCalls();
}
