package fi.solita.mule.modules.xroad;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
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
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;
import org.w3c.dom.Document;

import eu.x_road.xsd.identifiers.XRoadClientIdentifierType;
import eu.x_road.xsd.identifiers.XRoadObjectType;
import eu.x_road.xsd.identifiers.XRoadServiceIdentifierType;
import fi.solita.mule.modules.xroad.api.RovaDelegatePortType;
import fi.solita.mule.modules.xroad.api.RovaDelegateService;
import fi.solita.mule.modules.xroad.api.RovaDelegateServiceResponse;
import fi.solita.mule.modules.xroad.api.RovaDelegateService_Service;

public class XRoadClient {

	private RovaDelegatePortType port;

	private Client client;

	public void initialize() {
		RovaDelegateService_Service service = new RovaDelegateService_Service();
		port = service.getRovaDelegatePort();

		client = ClientProxy.getClient(port);
		client.getRequestContext().put(Message.ENDPOINT_ADDRESS,
				"http://localhost:18088/mockrovaDelegateBinding");

	}

	public Object send(Object payload, XRoadHeaders xRoadHeaders) {
		RovaDelegateService request = new RovaDelegateService();

		setHeaders(xRoadHeaders);

		RovaDelegateServiceResponse response = port
				.rovaDelegateService(request);
		return response;
	}

	private void setHeaders(XRoadHeaders xRoadHeaders) {
		try {
			BindingProvider bp = (BindingProvider) port;
			List<Header> headers = new ArrayList<Header>();

			eu.x_road.xsd.xroad.ObjectFactory xroadOf = new eu.x_road.xsd.xroad.ObjectFactory();
			addHeader(headers, xroadOf.createId(xRoadHeaders.id));
			bp.getRequestContext().put(Header.HEADER_LIST, headers);
		} catch (JAXBException e) {
			throw new RuntimeException("Failed to set headers", e);
		}

	}

	private void addHeader(List<Header> headers, JAXBElement<String> header)
			throws JAXBException {
		Header operationNameHeader = new Header(header.getName(), header,
				new JAXBDataBinding(header.getClass()));

		headers.add(operationNameHeader);
	}

	public Object sendRaw(Object payload, XRoadHeaders xRoadHeaders,
			String endpointUrl) {
		try {

			QName serviceName = new QName("", "");
			QName portName = new QName("", "");
			Service service = Service.create(serviceName);

			service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING,
					endpointUrl);
			Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
					SOAPMessage.class, Service.Mode.MESSAGE);

			MessageFactory mf = MessageFactory
					.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

			SOAPMessage request = mf.createMessage();
			SOAPPart part = request.getSOAPPart();
			SOAPEnvelope env = part.getEnvelope();

			buildBody(payload, env.getBody());

			buildHeader(xRoadHeaders, env.getHeader());

			request.saveChanges();

			dispatch.getRequestContext().put(
					BindingProvider.SOAPACTION_USE_PROPERTY, true);
			dispatch.getRequestContext().put(
					BindingProvider.SOAPACTION_URI_PROPERTY, "");

			SOAPMessage response = dispatch.invoke(request);
			return response.getSOAPBody().extractContentAsDocument();
		} catch (Exception e) {
			throw new RuntimeException("Failed to send message ", e);
		}
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
		JAXBContext xRoadContext = JAXBContext
				.newInstance("eu.x_road.xsd.xroad");
		final Marshaller marshaller = xRoadContext.createMarshaller();
		marshaller.marshal(xroadOf.createId(xRoadHeaders.id), header);
		marshaller.marshal(xroadOf.createUserId(xRoadHeaders.userId), header);
		marshaller.marshal(xroadOf.createClient(client), header);
		marshaller.marshal(xroadOf.createService(service), header);
	}
}
