package com.lloydsbanking.salsa.apaloans;

import com.lloydsbanking.salsa.activate.administer.convert.X741RequestFactory;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferralTeamsDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import com.lloydsbanking.salsa.downstream.tms.client.x741.X741Client;
import com.lloydsbanking.salsa.remotemock.MockControlServicePortType;
import com.lloydsbanking.salsa.remotemock.MockControlTmsX741ServicePortType;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreation;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreationResponse;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class RemoteMockScenarioHelper implements ScenarioHelper {
    @Autowired
    TestDataHelper dataHelper;

    @Value("${salsa.fs.boxid}")
    int boxId;

    @Value("${wps.cache.url}")
    String wpsCacheUrl;

    @Autowired
    X741RequestFactory x741RequestFactory;

    @Autowired
    ReferenceDataLookUpDao referenceDataLookUpDao;

    @Autowired
    ReferralTeamsDao referralTeamsDao;
    @Autowired
    MockControlTmsX741ServicePortType mockTmsX741Control;
    @Autowired
    MockControlServicePortType mockControl;
    @Autowired
    X741Client x741Client;

    @Override
    @Transactional
    @Modifying
    public void clearUp() {
        referenceDataLookUpDao.deleteAll();
        referralTeamsDao.deleteAll();
        dataHelper.cleanUp();
        clearWpsCache();
    }

    public void clearWpsCache() {
        try {
            System.out.println("wpsCacheUrl = " + wpsCacheUrl);
            URL wpsCache = new URL(wpsCacheUrl);
            URLConnection uc = wpsCache.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            in.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    @Override
    public String expectChannelIdByContactPointID() {
        String channelId = "LTB";
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("Cnt_Pnt_Prtflio", "0000777505", "Acquire Contry Name", new Long("132356"), "Bahrain", "LTB", new Long("1"));
        referenceDataLookUpDao.save(referenceDataLkp);
        return channelId;
    }

    @Override
    public void expectX741Call(Integer taskId, ProductArrangement productArrangement, RequestHeader header) {
        TaskCreation x741Req = x741RequestFactory.convert(productArrangement);
        mockControl.matching("actual.target == 'tmsX741'");
        x741Client.x741(x741Req, dataHelper.getContactPointFromRequestHeader(header), dataHelper.getServiceRequestFromRequestHeader(header), dataHelper.getSecurityHeaderTypeFromRequestHeader(header), dataHelper.getBapiInformationFromRequestHeader(header));
        TaskCreationResponse taskCreationResponse = dataHelper.createTaskCreationResponse();
        taskCreationResponse.getCreateTaskReturn().getTaskRoutingInformation().setTaskId(taskId);
        mockTmsX741Control.thenReturn(taskCreationResponse);
    }

    @Override
    public void expectReferenceDataForPAM() {
        dataHelper.createPamReferenceData();
    }

    @Override
    @Transactional
    public List<ReferralTeams> expectReferralsTeamDetails(String name) {
        ReferralTeams referralTeams1 = new ReferralTeams();
        referralTeams1.setId(1);
        referralTeams1.setPriority(6l);
        referralTeams1.setOuId("123");
        referralTeams1.setTaskType("209178");
        referralTeams1.setName(name);
        referralTeamsDao.save(referralTeams1);
        return referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc(name);
    }

    @Override
    @Transactional
    public List<ReferralTeams> expectReferralsTeamDetailsWithInvalidTaskType(String name) {
        ReferralTeams referralTeams1 = new ReferralTeams();
        referralTeams1.setId(1);
        referralTeams1.setPriority(6l);
        referralTeams1.setOuId("123");
        referralTeams1.setTaskType("3");
        referralTeams1.setName(name);
        referralTeamsDao.save(referralTeams1);
        return referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc(name);
    }

    @Override
    public void verifyExpectCalls() {
        String result = mockControl.verify();
        if (!result.equals("verified")  && result.equals("tmsX741")) {
            throw new IllegalStateException("Following Expect not called: " + result);
        }
    }



}