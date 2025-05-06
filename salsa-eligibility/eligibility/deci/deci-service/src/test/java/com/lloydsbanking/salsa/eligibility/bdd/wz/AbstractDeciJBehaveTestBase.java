package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.SpringJndiJunit4TestRunner;
import com.lloydsbanking.salsa.bdd.jbehave.JBehaveTestBase;
import com.lloydsbanking.salsa.eligibility.client.wz.EligibilityClient;
import com.lloydsbanking.salsa.eligibility.wz.ScenarioHelper;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
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
    protected EligibilityClient eligibilityClientWZ;

    @Autowired
    protected TestDataHelper dataHelperWZ;

    @Autowired
    protected ScenarioHelper mockScenarioHelperWZ;


    @Value("${targetHostContainer:unspecified}")
    String targetHostContainer;

    int scenarioNumber = 0;

    @BeforeScenario
    public final void setUp() {
        mockControl.reset();
        mockScenarioHelperWZ.expectPamData();
        scenarioNumber += 1;
        eligibilityClientWZ.setSoapMessagesLogFilename(targetHostContainer + "/" + this.getClass().getSimpleName() + "-" + scenarioNumber + ".xml");

    }

    @Before
    public void setSoapMessageLogFilename() {
        mockControl.setTargetHostContainer(targetHostContainer);
    }

    @AfterScenario
    public void clearUp() {
        mockScenarioHelperWZ.clearUp();
    }


}
