package fi.solita.mule.modules.xroad;


import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.util.UUID;

@Connector(name = "x-road", description = "X-Road Connector", friendlyName = "X-Road")
public class XRoadConnector {

	@Config
	private XRoadConnectorConfig config;
	

	/**
	 * Custom processor that sends xroad message. Optional values are taken from connector but can be
	 * overridden
	 * 
	 * @param payload JAXB payload or Document, which is sent
	 * @param id X-Road header
	 * @param clientXroadInstance X-Road header
	 * @param clientMemberClass X-Road header
     * @param clientMemberCode X-Road header
     * @param clientSubsystemCode X-Road header
     * @param serviceXroadInstance X-Road header
     * @param serviceMemberClass X-Road header
     * @param serviceMemberCode X-Road header
     * @param serviceSubsystemCode X-Road header
     * @param serviceServiceCode X-Road header
     * @param serviceServiceVersion X-Road header
     * @param userId X-Road header
	 * @return returns reponse message
	 */
	@Processor
	public Object sendMessage(@Default("#[payload]") Object payload,
			@Optional String id,
			@Optional String clientXroadInstance,
			@Optional String clientMemberClass,
			@Optional String clientMemberCode,
			@Optional String clientSubsystemCode,
			@Optional String serviceXroadInstance,
			@Optional String serviceMemberClass,
			@Optional String serviceMemberCode,
			@Optional String serviceSubsystemCode,
			@Optional String serviceServiceCode,
			@Optional String serviceServiceVersion,
			@Optional String userId
			) {
	    XRoadHeaders overridedHeaders = new XRoadHeaders(UUID.getUUID(), clientXroadInstance, clientMemberClass,
                clientMemberCode, clientSubsystemCode, serviceXroadInstance,
                serviceMemberClass, serviceMemberCode, serviceSubsystemCode,
                serviceServiceCode, serviceServiceVersion, userId);
	
		return config.getClient().send(payload, config.getXRoadHeaders().merge(overridedHeaders), config.getEndpointUrl());
	}

	public XRoadConnectorConfig getConfig() {
		return config;
	}


	public void setConfig(XRoadConnectorConfig config) {
		this.config = config;
	}
}
