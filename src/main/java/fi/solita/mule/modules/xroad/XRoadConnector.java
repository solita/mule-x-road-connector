package fi.solita.mule.modules.xroad;


import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;

@Connector(name = "x-road", friendlyName = "X-Road")
public class XRoadConnector {

	//@Inject
	//private XRoadConfiguration configuration;

	@Config
	private XRoadConnectionStrategy connectionStrategy;
	

	/**
	 * Custom processor that sends xroad message
	 *
	 * {@sample.xml ../../../doc/x-road-connector.xml.sample x-road:sendMessge}
	 *
	 * @param animal Name of the animal to be place in the barn
	 * @return returns processed message
	 */
	@Processor
	public Object sendMessage(@Default("#[payload]") Object payload) {
		XRoadHeaders xRoadHeaders = new XRoadHeaders("testi-id", null, null, null, null, null, null, null, null, null, null, null);
		String endpointUrl = "http://localhost:8088/mockrovaDelegateBinding";
		return connectionStrategy.getClient().sendRaw(payload, xRoadHeaders, endpointUrl);
	}


	public XRoadConnectionStrategy getConnectionStrategy() {
		return connectionStrategy;
	}


	public void setConnectionStrategy(XRoadConnectionStrategy connectionStrategy) {
		this.connectionStrategy = connectionStrategy;
	}

}
