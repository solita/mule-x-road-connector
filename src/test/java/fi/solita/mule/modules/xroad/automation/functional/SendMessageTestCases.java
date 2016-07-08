/**
 * MIT License Copyright (c) 2016 Solita
 */
package fi.solita.mule.modules.xroad.automation.functional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;
import org.w3c.dom.Document;

import fi.solita.mule.modules.xroad.LipaMockComponent;
import fi.solita.mule.modules.xroad.XRoadConnector;

public class SendMessageTestCases extends AbstractTestCase<XRoadConnector> {

	public SendMessageTestCases() {
		super(XRoadConnector.class);
	}
	
    @Test
    public void testSendMessage() throws Exception {
    	Document doc = getHelloServiceRequest();
        Object result = getConnector().sendMessage(doc, null, null, null, null, null, null, null, null, null, null, null, Boolean.FALSE, null, null, null);
        assertNotNull(result);
        assertTrue(result instanceof Document);
        System.out.println(LipaMockComponent.getStringFromDocument((Document) result));
    }

    @Test
    public void testRandomMessage() throws Exception {
    	Document doc = getRandomServiceRequest();
        Object result = getConnector().sendMessage(doc, null, null, null, null, null, null, null, null, null, "getRandom", "v1", null, null, null, null);
        assertNotNull(result);
        assertTrue(result instanceof Document);
        System.out.println(LipaMockComponent.getStringFromDocument((Document) result));
    }
    
    public Document getHelloServiceRequest() throws Exception {
        
        String content = "<ns2:helloService xmlns:ns2=\"http://test.x-road.fi/producer\">\n" +
                         "  <request>\n" + 
                         "    <name>Solita</name>\n" +
                         "  </request>\n" +
                         "</ns2:helloService>";

        return LipaMockComponent.getDocumentFromString(content);
    }

    public Document getRandomServiceRequest() throws Exception {
        
        String content = "<ns1:getRandom xmlns:ns1=\"http://test.x-road.fi/producer\">\n" +
                         "  <request/>\n" +
                         "</ns1:getRandom>";

        return LipaMockComponent.getDocumentFromString(content);
    }

}