package com.lloydsbanking.salsa.ppae.bdd;

import com.lloydsbanking.salsa.SpringJndiJunit4TestRunner;
import com.lloydsbanking.salsa.bdd.jbehave.JBehaveTestBase;
import com.lloydsbanking.salsa.config.JndiProperties;
import com.lloydsbanking.salsa.ppae.ScenarioHelper;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.client.PpaeClient;
import com.lloydsbanking.salsa.remotemock.MockControlServicePortType;
import org.jbehave.core.annotations.BeforeScenario;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SpringJndiJunit4TestRunner.class)
@JndiProperties("")
@ContextConfiguration(locations = {"classpath:/com/lloydsbanking/salsa/ppae/service/application-context-with-downstream-profiles.xml"})
public abstract class AbstractPpaeJBehaveTestBase extends JBehaveTestBase {

    @Autowired
    public PpaeClient ppaeClient;

    @Autowired
    public MockControlServicePortType mockControl;

    @Autowired
    public TestDataHelper testDataHelper;

    @Autowired
    public ScenarioHelper mockScenarioHelper;


    @Value("${targetHostContainer:unspecified}")
    String targetHostContainer;

    int scenarioNumber = 0;

    @BeforeScenario
    public final void setUp() {
        mockControl.reset();
        scenarioNumber += 1;
        ppaeClient.setSoapMessagesLogFilename(targetHostContainer + "/" + this.getClass().getSimpleName() + "-" + scenarioNumber + ".xml");
    }

}
