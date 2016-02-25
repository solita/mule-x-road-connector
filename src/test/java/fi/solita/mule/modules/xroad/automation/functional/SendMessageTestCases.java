package fi.solita.mule.modules.xroad.automation.functional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.xml.transformer.jaxb.JAXBMarshallerTransformer;
import org.mule.module.xml.util.XMLUtils;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;
import org.mule.transformer.types.SimpleDataType;
import org.w3c.dom.Document;

import fi.solita.mule.modules.xroad.XRoadConnector;

public class SendMessageTestCases extends AbstractTestCase<XRoadConnector> {

	public SendMessageTestCases() {
		super(XRoadConnector.class);
	}
	
    @Test
    public void testSendMessage() throws Exception {
    	Document doc = getHelloServiceRequest();
        Object result = getConnector().sendMessage(doc, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertNotNull(result);
        assertTrue(result instanceof Document);
        System.err.println(getStringFromDocument((Document) result));
    }
    
    public Document getHelloServiceRequest() throws Exception {
        
        String content = "<ns2:helloService xmlns:ns2=\"http://test.x-road.fi/producer\">\n" +
                         "<request>\n" + 
                         "<name>FI-DEV/COM/1060155-5/mule@Solita!</name>\n" +
                         "</request>\n" +
                         "</ns2:helloService>";

        return getDocumentFromString(content);
    }

    private Document getDocumentFromString(String content)
            throws Exception {
        DOMResult result = new DOMResult(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
     
        Transformer transformer = XMLUtils.getTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        Source source = new StreamSource(new StringReader(content));
        transformer.transform(source, result);
        return (Document) result.getNode();
    }
    
    private String getStringFromDocument(Document document) throws Exception {
        DOMSource domSource = new DOMSource(document);
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        Transformer transformer = XMLUtils.getTransformer();
        transformer.transform(domSource, result);
        return sw.toString();
    }

	private <T> JAXBMarshallerTransformer getJaxbMarshallerTransformer(Object payload, Class<T> returnDataType)
			throws JAXBException, InitialisationException {
		JAXBContext context = JAXBContext.newInstance(payload.getClass());
    	JAXBMarshallerTransformer transformer = new JAXBMarshallerTransformer(context, new SimpleDataType<T>(returnDataType));
   
    	transformer.initialise();
		return transformer;
	}


}