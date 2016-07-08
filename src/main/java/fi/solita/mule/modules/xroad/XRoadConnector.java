/**
 * MIT License Copyright (c) 2016 Solita Oy
 */
package fi.solita.mule.modules.xroad;

import static org.apache.commons.lang.StringUtils.defaultString;

import java.lang.reflect.Field;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.util.UUID;

import fi.solita.mule.modules.xroad.XRoadClient.Result;

/**
 * Connector implements 
 * {@see <a href="http://esuomi.fi/?mdocs-file=2268&mdocs-url=false">X-Road protocol for adapter server messaging v4.0</a>}
 * used in X-Road 6.0.
 * @author Ari Ruotsalainen, Solita Oy
 */
@Connector(name = "x-road", description = "X-Road Connector", friendlyName = "X-Road", minMuleVersion= "3.5")
public class XRoadConnector {

    public static final String X_ROAD_PROPERTY_PREFIX = "X-Road-";
	
    @Config
    private XRoadConnectorConfig config;


    /**
     * Custom processor that sends xroad message. Optional values are taken from connector but can be
     * overridden.
     * 
     * @param payload JAXB payload or {@link org.w3c.dom.Document}, which is sent
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
     * @param async X-Road header
     * @param userId X-Road header
     * @param protocolVersion X-Road header
     * @return returns reponse message. Following x-road headers are set to outbound properties: 
     *     X-Road-clientMemberClass, X-Road-serviceXroadInstance, X-Road-userId, X-Road-clientSubsystemCode, X-Road-serviceSubsystemCode, X-Road-id,
     *     X-Road-clientMemberCode, X-Road-serviceMemberCode, X-Road-serviceServiceCode, X-Road-protocolVersion, X-Road-serviceMemberClass, X-Road-serviceServiceVersion, X-Road-clientXroadInstance 
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
			@Optional Boolean async,
			@Optional String userId,
			@Optional String protocolVersion,
			//@OutboundHeaders Map<String, Object> outboundHeaders
			// workaround for http://forums.mulesoft.com/questions/32807/outboundheaders-parameter-naming-in-connection-pro.html
			MuleEvent muleEvent
			) {
	    XRoadHeaders overridedHeaders = new XRoadHeaders(defaultString(id, UUID.getUUID()), clientXroadInstance, clientMemberClass,
                clientMemberCode, clientSubsystemCode, serviceXroadInstance,
                serviceMemberClass, serviceMemberCode, serviceSubsystemCode,
                serviceServiceCode, serviceServiceVersion, async, userId, protocolVersion);
	
		Result response = config.getClient().send(payload, config.getXRoadHeaders().merge(overridedHeaders), config.getEndpointUrl());
		
        fillOutboundHeaders(muleEvent.getMessage(), response.headers);
        
		return response.payload;
	}

    private void fillOutboundHeaders(MuleMessage muleMessage, XRoadHeaders headers) {
        try {
            for (Field field : headers.getClass().getDeclaredFields()) {
            	if (!field.isSynthetic()) {
                   muleMessage.setOutboundProperty(X_ROAD_PROPERTY_PREFIX + field.getName(), field.get(headers));
            	}
                
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set headers", e);
        }
    }

    public XRoadConnectorConfig getConfig() {
        return config;
    }


    public void setConfig(XRoadConnectorConfig config) {
        this.config = config;
    }
}
