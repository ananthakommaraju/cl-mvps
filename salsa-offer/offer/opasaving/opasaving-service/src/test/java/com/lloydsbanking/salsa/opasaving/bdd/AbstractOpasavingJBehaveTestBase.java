package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.SpringJndiJunit4TestRunner;
import com.lloydsbanking.salsa.bdd.jbehave.JBehaveTestBase;
import com.lloydsbanking.salsa.opasaving.client.OpaSavingClient;
import com.lloydsbanking.salsa.opasaving.service.ScenarioHelper;
import com.lloydsbanking.salsa.opasaving.service.TestDataHelper;
import com.lloydsbanking.salsa.remotemock.MockControlServicePortType;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.BeforeScenario;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;


@RunWith(SpringJndiJunit4TestRunner.class)
@ContextConfiguration(locations = {"classpath:/com/lloydsbanking/salsa/opasaving/service/application-context-with-downstream-profiles.xml"})
public abstract class AbstractOpasavingJBehaveTestBase extends JBehaveTestBase {
    @Autowired
    protected MockControlServicePortType mockControl;

    @Autowired
    protected OpaSavingClient opasavingClient;

    @Autowired
    protected TestDataHelper dataHelper;

    @Autowired
    protected ScenarioHelper mockScenarioHelper;

    @Value("${targetHostContainer:unspecified}")
    String targetHostContainer;

    int scenarioNumber = 0;

    @BeforeScenario
    public final void setUp() {
        mockScenarioHelper.clearUp();
        mockScenarioHelper.clearWpsCache();
        mockControl.reset();
        scenarioNumber += 1;
        opasavingClient.setSoapMessagesLogFilename(targetHostContainer + "/" + this.getClass().getSimpleName() + "-" + scenarioNumber + ".xml");
        mockScenarioHelper.expectPrdDbCalls();
        mockScenarioHelper.expectPAMReferenceData();
    }

    @Before
    public void setSoapMessageLogFilename() {
        mockControl.setTargetHostContainer(targetHostContainer);
    }

    @AfterScenario
    public void clearUp() {
        mockScenarioHelper.clearUp();
        mockScenarioHelper.clearWpsCache();
    }
}
