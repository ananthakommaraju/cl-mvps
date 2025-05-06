package com.lloydsbanking.salsa.aps.bdd;

import com.lloydsbanking.salsa.SpringJndiJunit4TestRunner;
import com.lloydsbanking.salsa.aps.client.ApsClient;
import com.lloydsbanking.salsa.aps.service.ScenarioHelper;
import com.lloydsbanking.salsa.aps.service.TestDataHelper;
import com.lloydsbanking.salsa.bdd.jbehave.JBehaveTestBase;
import com.lloydsbanking.salsa.remotemock.MockControlServicePortType;
import org.jbehave.core.annotations.BeforeScenario;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;


@RunWith(SpringJndiJunit4TestRunner.class)
@ContextConfiguration(locations = {"classpath:/com/lloydsbanking/salsa/aps/service/application-context-with-downstream-profiles.xml"})
public abstract class AbstractApsJBehaveTestBase extends JBehaveTestBase {
    @Autowired
    protected MockControlServicePortType mockControl;

    @Autowired
    protected ApsClient apsClient;

    @Autowired
    protected TestDataHelper dataHelper;

    @Autowired
    protected ScenarioHelper mockScenarioHelper;

    @Value("${targetHostContainer:unspecified}")
    String targetHostContainer;

    int scenarioNumber = 0;


    @BeforeScenario
    public final void setUp() {
        mockControl.reset();
        scenarioNumber += 1;
        apsClient.setSoapMessagesLogFilename(targetHostContainer + "/" + this.getClass().getSimpleName() + "-" + scenarioNumber + ".xml");
        mockScenarioHelper.clearUp();
    }

    @Before
    public void setSoapMessageLogFilename() {
        mockControl.setTargetHostContainer(targetHostContainer);
    }

}
