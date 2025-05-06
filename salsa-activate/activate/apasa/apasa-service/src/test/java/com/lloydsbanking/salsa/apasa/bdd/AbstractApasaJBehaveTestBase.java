package com.lloydsbanking.salsa.apasa.bdd;

import com.lloydsbanking.salsa.SpringJndiJunit4TestRunner;
import com.lloydsbanking.salsa.apasa.ScenarioHelper;
import com.lloydsbanking.salsa.apasa.TestDataHelper;
import com.lloydsbanking.salsa.apasa.client.ApasaClient;
import com.lloydsbanking.salsa.bdd.jbehave.JBehaveTestBase;
import com.lloydsbanking.salsa.config.JndiProperties;
import com.lloydsbanking.salsa.remotemock.MockControlServicePortType;
import org.jbehave.core.annotations.BeforeScenario;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SpringJndiJunit4TestRunner.class)
@JndiProperties("")
@ContextConfiguration(locations = {"classpath:/com/lloydsbanking/salsa/apasa/service/application-context-with-downstream-profiles.xml"})

public abstract class AbstractApasaJBehaveTestBase extends JBehaveTestBase {
    @Autowired
    public ApasaClient apaSaClient;

    @Autowired
    public TestDataHelper testDataHelper;

    @Autowired
    public ScenarioHelper mockScenarioHelper;

    @Autowired
    public MockControlServicePortType mockControl;

    @Value("${targetHostContainer:unspecified}")
    String targetHostContainer;

    int scenarioNumber = 0;

    @BeforeScenario
    public final void setUp() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        scenarioNumber += 1;
        apaSaClient.setSoapMessagesLogFilename(targetHostContainer + "/" + this.getClass().getSimpleName() + "-" + scenarioNumber + ".xml");
    }

}