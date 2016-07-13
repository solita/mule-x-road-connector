/**
 * MIT License Copyright (c) 2016 Solita Oy
 */
package fi.solita.mule.modules.xroad;

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

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.module.xml.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LipaMockComponent implements Callable {

    private static final String DEFAULT_ENCODING = "UTF-8";

    @Override
    public Object onCall(MuleEventContext eventContext) throws Exception {
        MuleMessage msg = eventContext.getMessage();
        return handleResponse(msg);
    }

    private MuleMessage handleResponse(MuleMessage message) throws Exception {
        Document requestDocument = getDocumentFromString(message.getPayloadAsString(DEFAULT_ENCODING));
        Node bodyNode = findNodeFromDocument(requestDocument, "Body");
        Node serviceNode = bodyNode.getChildNodes().item(0);
        
        // TODO: Tapio ei tämä synnytä oikean näköistä SOAP-responsea. Mikä tässä on ideana?
        // Vastaukseksi tulee tyyliin <?xml version="1.0" encoding="UTF-8"?><ns2:getRandom xmlns:ns2="http://test.x-road.fi/producer"><request/><response><data>-549849087</data></response></ns2:getRandom>
        serviceNode.appendChild(createResponseNode(requestDocument, serviceNode));
        String response = getStringFromDocument(requestDocument);
        message.setPayload(response);
        return message;
        
    }

    private Node createResponseNode(Document doc, Node serviceNode) {
        Element response = doc.createElement("response");
        Element message = null;
       
        switch (serviceNode.getLocalName()) {
        case "helloService":
            message = createHelloServiceResponse(doc, serviceNode);
            break;
        case "getRandom":
            message = createRandomServiceResponse(doc);
            break;
        default:
            throw new IllegalArgumentException("Unknown operation " + serviceNode.getLocalName());
        }
        response.appendChild(message);
        return response;
    }

    private Element createRandomServiceResponse(Document doc) {
        Element message = doc.createElement("data");
        message.setTextContent(Integer.toString(new Random(Runtime.getRuntime().freeMemory())
                .nextInt()));
        return message;
    }

    private Element createHelloServiceResponse(Document doc, Node serviceNode) {
        Element message = doc.createElement("message");
        String callerName = serviceNode.getFirstChild().getFirstChild().getTextContent();
        message.setTextContent("Hello: " + callerName + "!! Greetings from adapter server!");
        return message;
    }

    public static Document getDocumentFromString(String content) throws Exception {
        DOMResult result = new DOMResult(DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .newDocument());

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

    public static Node findNodeFromDocument(Document doc, String nodeName) {
        NodeList childNodes = doc.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = findNodeFromNode(childNodes.item(i), nodeName);
            if (node != null) {
                return node;
            }
        }
        return null;
    }
    
    public static Node findNodeFromNode(Node node, String nodeName) {
        if (nodeName.equals(node.getLocalName())) {
            return node;
        }
            
        NodeList childNodes = node.getChildNodes();
        
        if (childNodes.getLength() < 1) {
            return null;
        }
        
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node descendantNode = findNodeFromNode(childNodes.item(i), nodeName);
            if (descendantNode != null) {
                return descendantNode;
            }
        }
        return null;
    }
}
