
package fi.solita.mule.modules.xroad.automation.testrunners;

import fi.solita.mule.modules.xroad.XRoadConnector;
import fi.solita.mule.modules.xroad.automation.testcases.SendMessageTestCases;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mule.tools.devkit.ctf.junit.RegressionTests;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

@RunWith(org.junit.experimental.categories.Categories.class)
@org.junit.experimental.categories.Categories.IncludeCategory(RegressionTests.class)
@org.junit.runners.Suite.SuiteClasses({
    SendMessageTestCases.class
})
public class RegressionTestSuite {


    @BeforeClass
    public static void initialiseSuite() {
        ConnectorTestContext.initialize(XRoadConnector.class);
    }

    @AfterClass
    public static void shutdownSuite() {
        ConnectorTestContext.shutDown();
    }

}
