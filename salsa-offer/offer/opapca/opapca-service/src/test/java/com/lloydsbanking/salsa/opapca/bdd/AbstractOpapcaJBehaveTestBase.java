package com.lloydsbanking.salsa.opapca.bdd;

import com.lloydsbanking.salsa.SpringJndiJunit4TestRunner;
import com.lloydsbanking.salsa.bdd.jbehave.JBehaveTestBase;
import com.lloydsbanking.salsa.opapca.client.OpaPcaClient;
import com.lloydsbanking.salsa.opapca.service.ScenarioHelper;
import com.lloydsbanking.salsa.opapca.service.TestDataHelper;
import com.lloydsbanking.salsa.remotemock.MockControlServicePortType;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.BeforeScenario;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;



@RunWith(SpringJndiJunit4TestRunner.class)
@ContextConfiguration(locations = {"classpath:/com/lloydsbanking/salsa/opapca/service/application-context-with-downstream-profiles.xml"})
public abstract class AbstractOpapcaJBehaveTestBase extends JBehaveTestBase {
    @Autowired
    protected MockControlServicePortType mockControl;

    @Autowired
    protected OpaPcaClient opaPcaClient;

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
        mockControl.reset();
        scenarioNumber += 1;
        opaPcaClient.setSoapMessagesLogFilename(targetHostContainer + "/" + this.getClass().getSimpleName() + "-" + scenarioNumber + ".xml");
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
    }
}
