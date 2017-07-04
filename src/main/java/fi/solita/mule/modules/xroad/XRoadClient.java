/**
 * MIT License Copyright (c) 2017 Solita Oy
 */
package fi.solita.mule.modules.xroad;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.transport.http.HTTPConduit;
import org.mule.api.lifecycle.CreateException;
import org.mule.api.security.tls.TlsConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.x_road.xsd.identifiers.XRoadClientIdentifierType;
import eu.x_road.xsd.identifiers.XRoadObjectType;
import eu.x_road.xsd.identifiers.XRoadServiceIdentifierType;

public class XRoadClient {

    public static final String XROAD_NAMESPACE = "http://x-road.eu/xsd/xroad.xsd";
    private static final String X_ROAD_CONTEXT_PATH = "eu.x_road.xsd.xroad";

    public static class Result {
        public final Object payload;
        public final XRoadHeaders headers;

        public Result(Object payload, XRoadHeaders headers) {
            this.payload = payload;
            this.headers = headers;
        }
    }

    private JAXBContext xRoadContext = null;

    public Result send(Object payload, XRoadHeaders xRoadHeaders, XRoadConnectorConfig config) {
        try {
            xRoadHeaders.validate();
            MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

            SOAPMessage request = mf.createMessage();
            SOAPPart part = request.getSOAPPart();
            SOAPEnvelope env = part.getEnvelope();

            buildBody(payload, env.getBody());
            buildHeader(xRoadHeaders, env.getHeader());

            request.saveChanges();

            SOAPMessage response = configureDispatch(config).invoke(request);

            XRoadHeaders responseHeaders = parseHeaders(response.getSOAPHeader());
            Result result = new Result(response.getSOAPBody().extractContentAsDocument(),
                    responseHeaders);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message ", (e.getCause() != null ? e.getCause() : e));
        }
    }

    private Node single(NodeList nodeList) {
        if (nodeList.getLength() != 1) {
            throw new IllegalArgumentException("Assumed exactly one element in " + nodeList
                    + " but was " + nodeList.getLength());
        }

        return nodeList.item(0);
    }

    private XRoadHeaders parseHeaders(SOAPHeader soapHeader) throws JAXBException {
        Unmarshaller unmarshaller = getXRoadContext().createUnmarshaller();
        XRoadClientIdentifierType client = getXroadHeaderJaxbElement(soapHeader, "client",
                XRoadClientIdentifierType.class, unmarshaller);
        XRoadServiceIdentifierType service = getXroadHeaderJaxbElement(soapHeader, "service",
                XRoadServiceIdentifierType.class, unmarshaller);
        Boolean async = findXroadHeaderJaxbElement(soapHeader, "async", Boolean.class, unmarshaller);
        String userId = getXroadHeaderJaxbElement(soapHeader, "userId", String.class, unmarshaller);
        String id = getXroadHeaderJaxbElement(soapHeader, "id", String.class, unmarshaller);
        String protocolVersion = getXroadHeaderJaxbElement(soapHeader, "protocolVersion",
                String.class, unmarshaller);
        // Should requesthash be read also?

        XRoadHeaders result = new XRoadHeaders(id, client.getXRoadInstance(),
                client.getMemberClass(), client.getMemberCode(), client.getSubsystemCode(),
                service.getXRoadInstance(), service.getMemberClass(), service.getMemberCode(),
                service.getSubsystemCode(), service.getServiceCode(), service.getServiceVersion(),
                async, userId, protocolVersion);
        return result;
    }

    private <T> T getXroadHeaderJaxbElement(SOAPHeader soapHeader, String elementName,
            Class<T> elementClass, Unmarshaller unmarshaller) throws JAXBException {
        Node clientNode = getXroadHeaderNode(soapHeader, elementName);
        return unmarshaller.unmarshal(clientNode, elementClass).getValue();
    }

    private <T> T findXroadHeaderJaxbElement(SOAPHeader soapHeader, String elementName,
            Class<T> elementClass, Unmarshaller unmarshaller) throws JAXBException {
    	try {
            Node clientNode = getXroadHeaderNode(soapHeader, elementName);
            if (clientNode != null) {
                return unmarshaller.unmarshal(clientNode, elementClass).getValue();
            }
    	} catch (IllegalArgumentException iae) {
            // No-op
        }
        return null;
    }

    private Node getXroadHeaderNode(SOAPHeader soapHeader, String elementName) {
        return single(soapHeader.getElementsByTagNameNS(XROAD_NAMESPACE, elementName));
    }

    private Dispatch<SOAPMessage> configureDispatch(XRoadConnectorConfig config) throws IOException, KeyManagementException, NoSuchAlgorithmException, CreateException {
        QName serviceName = new QName("", "");
        QName portName = new QName("", "");
        Service service = Service.create(serviceName);
        service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, config.getEndpointUrl());
        DispatchImpl<SOAPMessage> dispatch = (DispatchImpl<SOAPMessage>) service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);        

        if (StringUtils.startsWithIgnoreCase(config.getEndpointUrl(), "https") && config.getTrustStorePath() != null) {
	    	HTTPConduit httpConduit = (HTTPConduit) dispatch.getClient().getConduit();
	    	TLSClientParameters tlsParams = new TLSClientParameters();
	    	SSLSocketFactory sslFact = initializeSocketFactory(config);
	    	tlsParams.setSSLSocketFactory(sslFact);
	    	tlsParams.setUseHttpsURLConnectionDefaultSslSocketFactory(false);
	    	httpConduit.setTlsClientParameters(tlsParams);
        }

        return dispatch;
    }

    private SSLSocketFactory initializeSocketFactory(XRoadConnectorConfig config) throws IOException, KeyManagementException, NoSuchAlgorithmException, CreateException {
    	TlsConfiguration tlsConfig = new TlsConfiguration("xrd_" + config.getTrustStorePath());
    	tlsConfig.setTrustStore(config.getTrustStorePath());
    	tlsConfig.setTrustStorePassword(config.getTrustStorePassword());
    	tlsConfig.setTrustStoreType(config.getTrustStoreType());
    	tlsConfig.initialise(true, "xrd");
    	return tlsConfig.getSocketFactory();
	}

	private void buildBody(Object payload, SOAPBody body) throws JAXBException, SOAPException {
        if (payload instanceof Document) {
            Document document = (Document) payload;
            body.addDocument(document);
        } else {
            // We might cache those contexts if performance is needed...
            JAXBContext context = JAXBContext.newInstance(payload.getClass());
            final Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(payload, body);
        }
    }

    private void buildHeader(XRoadHeaders xRoadHeaders, SOAPHeader header) throws SOAPException,
            JAXBException {
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
        if (xRoadHeaders.async != null) {
            marshaller.marshal(xroadOf.createAsync(xRoadHeaders.async), header);
        }
        marshaller.marshal(xroadOf.createUserId(xRoadHeaders.userId), header);
        marshaller.marshal(xroadOf.createId(xRoadHeaders.id), header);

        marshaller.marshal(xroadOf.createProtocolVersion(xRoadHeaders.protocolVersion), header);

    }

    private synchronized JAXBContext getXRoadContext() throws JAXBException {
        if (xRoadContext == null) {
            xRoadContext = JAXBContext.newInstance(X_ROAD_CONTEXT_PATH);
        }
        return xRoadContext;
    }
}
