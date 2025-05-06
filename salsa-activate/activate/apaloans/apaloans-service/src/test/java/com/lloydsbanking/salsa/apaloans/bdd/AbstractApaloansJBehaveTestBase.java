package com.lloydsbanking.salsa.apaloans.bdd;

import com.lloydsbanking.salsa.SpringJndiJunit4TestRunner;
import com.lloydsbanking.salsa.apaloans.ScenarioHelper;
import com.lloydsbanking.salsa.apaloans.TestDataHelper;
import com.lloydsbanking.salsa.apaloans.client.ApaloansClient;
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
@ContextConfiguration(locations = {"classpath:/com/lloydsbanking/salsa/apaloans/service/application-context-with-downstream-profiles.xml"})

public abstract class AbstractApaloansJBehaveTestBase extends JBehaveTestBase {
    @Autowired
    public ApaloansClient apaloansClient;

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
        scenarioNumber += 1;
        apaloansClient.setSoapMessagesLogFilename(targetHostContainer + "/" + this.getClass().getSimpleName() + "-" + scenarioNumber + ".xml");
    }

}