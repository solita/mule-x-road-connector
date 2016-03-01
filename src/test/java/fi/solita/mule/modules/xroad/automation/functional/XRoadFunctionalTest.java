package fi.solita.mule.modules.xroad.automation.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mule.api.MuleEvent;
import org.mule.tck.junit4.FunctionalTestCase;

public class XRoadFunctionalTest extends FunctionalTestCase {

    @Override
    protected String getConfigFile() {
        return "automation-test-flows.xml";
    }
    
    @Test
    public void sendMessage() throws Exception {
       MuleEvent result = runFlow("send-message");
       assertTrue(result.getMessageAsString().contains("Greetings from adapter server!"));
       System.err.println(result.getMessage().getOutboundPropertyNames());
       assertEquals("test", result.getMessage().getOutboundProperty("X-ROAD-userId"));
    }
}
