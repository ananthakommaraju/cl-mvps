package com.lloydsbanking.salsa.eligibility.bdd;

import com.lloydsbanking.salsa.SpringJndiJunit4TestRunner;
import com.lloydsbanking.salsa.bdd.jbehave.JBehaveTestBase;
import com.lloydsbanking.salsa.eligibility.ScenarioHelper;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.client.EligibilityClient;
import com.lloydsbanking.salsa.remotemock.MockControlCbsE184ServicePortType;
import com.lloydsbanking.salsa.remotemock.MockControlServicePortType;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.BeforeScenario;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SpringJndiJunit4TestRunner.class)
@ContextConfiguration(locations = {"classpath:/com/lloydsbanking/salsa/eligibility/service/application-context-with-downstream-profiles.xml"})
public abstract class AbstractDeciJBehaveTestBase extends JBehaveTestBase {

    @Autowired
    protected MockControlServicePortType mockControl;

    @Autowired
    protected EligibilityClient eligibilityClient;

    @Autowired
    protected TestDataHelper dataHelper;

    @Autowired
    protected ScenarioHelper mockScenarioHelper;

    @Autowired
    protected MockControlCbsE184ServicePortType mockControlCbsE184ServicePortType;


    @Value("${targetHostContainer:unspecified}")
    String targetHostContainer;

    int scenarioNumber = 0;

    @BeforeScenario
    public void setUp() {
        mockControl.reset();
        mockScenarioHelper.expectRefDataAvailable();
        scenarioNumber += 1;
        eligibilityClient.setSoapMessagesLogFilename(targetHostContainer + "/" + this.getClass().getSimpleName() + "-" + scenarioNumber + ".xml");

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
