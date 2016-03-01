package fi.solita.mule.modules.xroad;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.x_road.xsd.identifiers.XRoadClientIdentifierType;
import eu.x_road.xsd.identifiers.XRoadObjectType;
import eu.x_road.xsd.identifiers.XRoadServiceIdentifierType;

public class XRoadClient {

    public static class Result {
        public final Object payload;
        public final XRoadHeaders headers;
        
        
        public Result(Object payload, XRoadHeaders headers) {
            this.payload = payload;
            this.headers = headers;
        }
    }
    
	public Result send(Object payload, XRoadHeaders xRoadHeaders,
			String endpointUrl) {
		try {
		    xRoadHeaders.validate();
			MessageFactory mf = MessageFactory
					.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

			SOAPMessage request = mf.createMessage();
			SOAPPart part = request.getSOAPPart();
			SOAPEnvelope env = part.getEnvelope();

			buildBody(payload, env.getBody());

			buildHeader(xRoadHeaders, env.getHeader());

			request.saveChanges();
			
			SOAPMessage response = getDispatch(endpointUrl).invoke(request);
			
			XRoadHeaders responseHeaders = parseHeaders(response.getSOAPHeader());
			Result result = new Result(response.getSOAPBody().extractContentAsDocument(), responseHeaders);
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Failed to send message ", e);
		}
	}
	
	private Node single(NodeList nodeList) {
	    if (nodeList.getLength() != 1) {
	        throw new IllegalArgumentException("Assumed exactly one element in " + nodeList + " but was " + nodeList.getLength());
	    }
	        
	    return nodeList.item(0);
	}

    private XRoadHeaders parseHeaders(SOAPHeader soapHeader) throws JAXBException {
        XRoadClientIdentifierType client = getXroadHeaderJaxbElement(soapHeader, "client", XRoadClientIdentifierType.class);
        XRoadServiceIdentifierType service = getXroadHeaderJaxbElement(soapHeader, "service", XRoadServiceIdentifierType.class);
        String userId = getXroadHeaderJaxbElement(soapHeader, "userId", String.class);
        String id = getXroadHeaderJaxbElement(soapHeader, "id", String.class);
        String protocolVersion = getXroadHeaderJaxbElement(soapHeader, "protocolVersion", String.class);
        // Should requesthash be readed also?
        
        XRoadHeaders result = new XRoadHeaders(id, client.getXRoadInstance(), client.getMemberClass(),
                client.getMemberCode(), client.getSubsystemCode(), service.getXRoadInstance(),
                service.getMemberClass(), service.getMemberCode(), service.getSubsystemCode(),
                service.getServiceCode(), service.getServiceVersion(), userId, protocolVersion);
        return result;
    }

    private <T> T getXroadHeaderJaxbElement(
            SOAPHeader soapHeader, String elementName, Class<T> elementClass) throws JAXBException {
        Node clientNode = getXroadHeaderNode(soapHeader, elementName);
        System.err.println(clientNode);
        return getXRoadContext().createUnmarshaller().unmarshal(clientNode, elementClass).getValue();
    }

    private Node getXroadHeaderNode(SOAPHeader soapHeader, String elementName) {
        return single(soapHeader.getElementsByTagNameNS("http://x-road.eu/xsd/xroad.xsd", elementName));
    }

    private Dispatch<SOAPMessage> getDispatch(String endpointUrl) {
        QName serviceName = new QName("", "");
        QName portName = new QName("", "");
        Service service = Service.create(serviceName);

        service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING,
        		endpointUrl);
        Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
        		SOAPMessage.class, Service.Mode.MESSAGE);
        return dispatch;
    }

	private void buildBody(Object payload, SOAPBody body) throws JAXBException,
			SOAPException {
		if (payload instanceof Document) {
			Document document = (Document) payload;
			body.addDocument(document);
		} else {
			JAXBContext context = JAXBContext.newInstance(payload.getClass());
			final Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(payload, body);
		}
	}

	private void buildHeader(XRoadHeaders xRoadHeaders, SOAPHeader header)
			throws SOAPException, JAXBException {
		XRoadClientIdentifierType client = new XRoadClientIdentifierType();
		client.setObjectType(XRoadObjectType.SUBSYSTEM);
		client.setXRoadInstance(xRoadHeaders.clientXroadInstance);
		client.setMemberClass(xRoadHeaders.clientMemberClass);

		client.setMemberCode(xRoadHeaders.clientMemberCode);
		client.setSubsystemCode(xRoadHeaders.clientSubsystemCode);

		XRoadServiceIdentifierType service = new XRoadServiceIdentifierType();
		service.setObjectType(XRoadObjectType.SERVICE);
		service.setXRoadInstance(xRoadHeaders.serviceXroadInstance);
		service.setMemberClass(xRoadHeaders.serviceMemberClass);
		service.setMemberCode(xRoadHeaders.serviceMemberCode);
		service.setSubsystemCode(xRoadHeaders.serviceSubsystemCode);
		service.setServiceCode(xRoadHeaders.serviceServiceCode);
		service.setServiceVersion(xRoadHeaders.serviceServiceVersion);

		eu.x_road.xsd.xroad.ObjectFactory xroadOf = new eu.x_road.xsd.xroad.ObjectFactory();
		final Marshaller marshaller = getXRoadContext().createMarshaller();
		marshaller.marshal(xroadOf.createClient(client), header);
		marshaller.marshal(xroadOf.createService(service), header);
		marshaller.marshal(xroadOf.createUserId(xRoadHeaders.userId), header);
		marshaller.marshal(xroadOf.createId(xRoadHeaders.id), header);
		marshaller.marshal(xroadOf.createProtocolVersion(xRoadHeaders.protocolVersion), header);
        
	}

    private JAXBContext getXRoadContext() throws JAXBException {
        JAXBContext xRoadContext = JAXBContext
				.newInstance("eu.x_road.xsd.xroad");
        return xRoadContext;
    }
}
