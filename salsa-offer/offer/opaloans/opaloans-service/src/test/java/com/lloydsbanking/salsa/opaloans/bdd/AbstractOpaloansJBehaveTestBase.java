package com.lloydsbanking.salsa.opaloans.bdd;

import com.lloydsbanking.salsa.SpringJndiJunit4TestRunner;
import com.lloydsbanking.salsa.bdd.jbehave.JBehaveTestBase;
import com.lloydsbanking.salsa.opaloans.client.OpaLoansClient;
import com.lloydsbanking.salsa.opaloans.service.ScenarioHelper;
import com.lloydsbanking.salsa.opaloans.service.TestDataHelper;
import com.lloydsbanking.salsa.remotemock.MockControlServicePortType;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.BeforeScenario;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SpringJndiJunit4TestRunner.class)
@ContextConfiguration(locations = {"classpath:/com/lloydsbanking/salsa/opaloans/service/application-context-with-downstream-profiles.xml"})
public abstract class AbstractOpaloansJBehaveTestBase extends JBehaveTestBase {
    @Autowired
    protected MockControlServicePortType mockControl;

    @Autowired
    protected OpaLoansClient opaLoansClient;

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
        opaLoansClient.setSoapMessagesLogFilename(targetHostContainer + "/" + this.getClass().getSimpleName() + "-" + scenarioNumber + ".xml");
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
