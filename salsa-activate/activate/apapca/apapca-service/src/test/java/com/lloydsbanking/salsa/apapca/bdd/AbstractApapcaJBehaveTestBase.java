package com.lloydsbanking.salsa.apapca.bdd;

import com.lloydsbanking.salsa.SpringJndiJunit4TestRunner;
import com.lloydsbanking.salsa.apapca.ScenarioHelper;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.apapca.client.ApaPcaClient;
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
@ContextConfiguration(locations = {"classpath:/com/lloydsbanking/salsa/apapca/service/application-context-with-downstream-profiles.xml"})
public abstract class AbstractApapcaJBehaveTestBase extends JBehaveTestBase {

    @Autowired
    public ApaPcaClient apaPcaClient;

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
        apaPcaClient.setSoapMessagesLogFilename(targetHostContainer + "/" + this.getClass().getSimpleName() + "-" + scenarioNumber + ".xml");
    }

}
