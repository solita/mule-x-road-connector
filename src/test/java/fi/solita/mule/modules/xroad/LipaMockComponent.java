/**
 * MIT License Copyright (c) 2016 Solita Oy
 */
package fi.solita.mule.modules.xroad;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.helpers.IOUtils;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.module.xml.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


public class LipaMockComponent implements Callable {

	private static final String DEFAULT_ENCODING = "UTF-8";
	
	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		MuleMessage msg = eventContext.getMessage();		
		return handleResponse(msg);
	}

    private MuleMessage handleResponse(MuleMessage message) throws Exception {
        Object payloadObject = message.getPayload();        
        try (InputStream bis = (InputStream) payloadObject) {
            byte[] bData = IOUtils.readBytesFromStream(bis);            

            Document requestDocument = getDocumentFromString(new String(bData, DEFAULT_ENCODING));            
            Node serviceNode = findNodeFromDocument(requestDocument, "helloService");
            if (serviceNode == null) {
            	serviceNode = findNodeFromDocument(requestDocument, "getRandom");
            }
            serviceNode.appendChild(createResponseNode(requestDocument, serviceNode));            
            String response = getStringFromDocument(requestDocument);            
            message.setPayload(response);
            return message;
        } catch (Exception e) {
            throw e;
        }
    }
    
    private Node createResponseNode(Document doc, Node serviceNode) {
        Element response = doc.createElement("response");
        Element message = null;
        String operation = serviceNode.getNodeName();
        if (operation.contains("helloService")) {
        	message = doc.createElement("message");
        	String callerName = serviceNode.getFirstChild().getFirstChild().getTextContent();
        	message.setTextContent("Hello: " + callerName + "!! Greetings from adapter server!");
        } else if (operation.contains("getRandom")) {
        	message = doc.createElement("data");
        	message.setTextContent(Integer.toString(new Random(Runtime.getRuntime().freeMemory()).nextInt()));
        }
        response.appendChild(message);
        return response;
    }

    public static Document getDocumentFromString(String content)
            throws Exception {
        DOMResult result = new DOMResult(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
     
        Transformer transformer = XMLUtils.getTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
        Source source = new StreamSource(new StringReader(content));
        transformer.transform(source, result);
        return (Document) result.getNode();
    }
    
    public static String getStringFromDocument(Document document) throws Exception {
        DOMSource domSource = new DOMSource(document);
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        Transformer transformer = XMLUtils.getTransformer();
        transformer.transform(domSource, result);
        return sw.toString();
    }
   
    public static Node findNodeFromDocument(Document doc, String expectedElementName) {
        Element rootElement = doc.getDocumentElement();
        NodeList nodeList = rootElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = findNodeFromChildren(nodeList.item(i), expectedElementName);
            if (child != null) {
                return child;
            }

        }
        return null;
    }

    public static Node findNodeFromChildren(Node node, String expectedElementName) {
        if (node instanceof Text) {
            return null;
        }
        NodeList nodeList = node.getChildNodes();
        if (nodeList == null || nodeList.getLength() < 1) {
            return null;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            if (child instanceof Text) {
                continue;
            }
            if (child != null && expectedElementName.equalsIgnoreCase(child.getLocalName())) {
                return child;
            } else if (child != null) {
                NodeList childChildren = child.getChildNodes();
                if (childChildren == null || childChildren.getLength() < 1) {
                    continue;
                }
                for (int j = 0; j < childChildren.getLength(); j++) {
                    Node responseChild = findNodeFromChildren(childChildren.item(j), expectedElementName);
                    if (responseChild != null) {
                        return responseChild;
                    }
                }
            }
        }
        return null;
    }
}
