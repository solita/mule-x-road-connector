package fi.solita.mule.modules.xroad;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;

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
				"http://localhost:8088/mockrovaDelegateBinding");

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
}
