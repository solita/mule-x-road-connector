/**
 * MIT License Copyright (c) 2016 Solita Oy
 */
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
    public void sendHelloMessage() throws Exception {
        MuleEvent result = runFlow("send-HelloServiceMessage");
        String messageAsString = result.getMessageAsString();
        assertTrue(messageAsString.contains("Greetings from adapter server!"));
        System.err.println(result.getMessage().getOutboundPropertyNames());
        assertEquals("automationTest", result.getMessage().getOutboundProperty("X-ROAD-userId"));
        logger.info(messageAsString);
    }

    @Test
    public void sendGetRandomMessage() throws Exception {
        MuleEvent result = runFlow("send-GetRandomMessage");
        String messageAsString = result.getMessageAsString();
        assertTrue(messageAsString.contains("data>"));
        assertEquals("FI-DEV", result.getMessage()
                .getOutboundProperty("X-Road-clientXroadInstance"));
        logger.info(messageAsString);
    }
}
