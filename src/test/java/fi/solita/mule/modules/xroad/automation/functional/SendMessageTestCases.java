package fi.solita.mule.modules.xroad.automation.functional;

import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;

import org.dom4j.io.DocumentResult;
import org.dom4j.io.SAXContentHandler;

import fi.solita.mule.modules.xroad.XRoadConnector;
import fi.solita.mule.modules.xroad.XRoadConnectorConfig;

import org.junit.Test;
import org.mule.api.MuleRuntimeException;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transformer.TransformerMessagingException;
import org.mule.config.i18n.MessageFactory;
import org.mule.module.xml.util.XMLUtils;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

public class SendMessageTestCases extends AbstractTestCase<XRoadConnector> {

	public SendMessageTestCases() {
		super(XRoadConnector.class);
	}

	@Test
	public void verify() throws TransformerException, TransformerMessagingException, InitialisationException {
		String payloadString = "<ns2:getRandom xmlns:ns2=\"http://test.x-road.fi/producer\"><request/></ns2:getRandom>";
		Object payload = transformMessageToDocument(payloadString, "UTF-8");

		XRoadConnector connector = getConnector();
		XRoadConnectorConfig config = new XRoadConnectorConfig();
		config.setEndpointUrl("https://localhost:8090/");
		config.setTrustStorePath("keystore.jks");
		config.setTrustStorePassword("changeit");
		config.setTrustStoreType("JKS");

		try {
			connector.sendMessage(payload, "ID", "clientXroadInstance", "clientMemberClass", "clientMemberCode",
					"clientSubsystemCode", "serviceXroadInstance", "serviceMemberClass", "serviceMemberCode", "serviceSubsystemCode",
					"serviceServiceCode", "serviceServiceVersion", Boolean.FALSE, "userId", "protocolVersion", null);
			fail("No exception!");
		} catch (RuntimeException re) {
			assertTrue(re.getCause().getMessage().contains("Connection refused: connect"));
		}
	}
	
    private Object transformMessageToDocument(Object src, String encoding) {
        try {
            Source sourceDoc = XMLUtils.toXmlSource(XMLInputFactory.newInstance(), true, src);
            if (sourceDoc == null)
            {
                return null;
            }

            // If returnClass is not set, assume W3C DOM
            // This is the original behaviour
            ResultHolder holder = getResultHolder(Document.class);
            if (holder == null)
            {
                holder = getResultHolder(Document.class);
            }

            Result result = holder.getResult();

            if (result instanceof DocumentResult)
            {
                DocumentResult dr = (DocumentResult) holder.getResult();
                ContentHandler contentHandler = dr.getHandler();
                if (contentHandler instanceof SAXContentHandler)
                {
                    //The following code is used to avoid the splitting
                    //of text inside DOM elements.
                    ((SAXContentHandler) contentHandler).setMergeAdjacentText(true);
                }
            }

            Transformer idTransformer = XMLUtils.getTransformer();
            idTransformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            idTransformer.transform(sourceDoc, holder.getResult());

            return holder.getResultObject();
        } catch (Exception e) {
            System.err.println(e + " " + e.getCause());
        }
        return null;
    }

	private ResultHolder getResultHolder(Class<Document> class1) {
        final DOMResult result;
        try {
            result = new DOMResult(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        } catch (Exception e) {
            throw new MuleRuntimeException(MessageFactory.createStaticMessage("Could not create result document"), e);
        }
        return new ResultHolder() {
            public Result getResult() {
                return result;
            }
            public Object getResultObject() {
                return result.getNode();
            }
        };
	}
	
    private interface ResultHolder
    {
        Result getResult();
        Object getResultObject();
    }
}