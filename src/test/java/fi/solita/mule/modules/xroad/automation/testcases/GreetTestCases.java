package fi.solita.mule.modules.xroad.automation.testcases;

import static org.junit.Assert.*;

import org.mule.tools.devkit.ctf.junit.RegressionTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;


public class GreetTestCases extends AbstractTestCase {

    @Test
    //@Category({RegressionTests.class})
    public void testFlow() throws Exception {
    	
    	assertEquals(getConnector().sendMessage("Foo"), "Hello Foo. How are you?");
    }
}