
package fi.solita.mule.modules.xroad.automation.testcases;

import static org.junit.Assert.*;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import fi.solita.mule.modules.xroad.api.RovaDelegateService;
import fi.solita.mule.modules.xroad.automation.AbstractTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.tools.devkit.ctf.junit.RegressionTests;

public class SendMessageTestCases
    extends AbstractTestCase
{


    @Before
    public void setUp() {
        //TODO: Add code to add @Before behaviour to your test.
    }

    @After
    public void tearDown()
        throws Exception
    {
        //TODO: Add code to reset sandbox state to the one before the test was run or remove.
    }

    @Test
    //@Category(RegressionTests.class)
    public void testSendMessage() throws JAXBException {
    	RovaDelegateService request = new RovaDelegateService();
    	
    	
    	
        Object result = getConnector().sendMessage(request);
        assertNotNull(result);
    }

}
