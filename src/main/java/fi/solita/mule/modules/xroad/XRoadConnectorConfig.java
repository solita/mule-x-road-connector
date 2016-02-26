package fi.solita.mule.modules.xroad;

import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.param.Optional;

@Configuration(friendlyName = "Configuration", configElementName = "x-road")
public class XRoadConnectorConfig {

    private XRoadClient client = new XRoadClient();

    /**
     * Url of the x-road server
     */
    @Configurable
    private String endpointUrl;

    @Configurable
    public String clientXroadInstance;

    @Configurable
    public String clientMemberClass;

    @Configurable
    public String clientMemberCode;

    @Configurable
    public String clientSubsystemCode;

    @Configurable
    public String serviceXroadInstance;

    @Configurable
    public String serviceMemberClass;

    @Configurable
    public String serviceMemberCode;

    @Configurable
    public String serviceSubsystemCode;

    @Configurable
    @Optional
    public String serviceServiceCode;

    @Configurable
    @Optional
    public String serviceServiceVersion;

    @Configurable
    public String userId;
    
    @Configurable
    public String protocolVersion;

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getClientXroadInstance() {
        return clientXroadInstance;
    }

    public void setClientXroadInstance(String clientXroadInstance) {
        this.clientXroadInstance = clientXroadInstance;
    }

    public String getClientMemberClass() {
        return clientMemberClass;
    }

    public void setClientMemberClass(String clientMemberClass) {
        this.clientMemberClass = clientMemberClass;
    }

    public String getClientMemberCode() {
        return clientMemberCode;
    }

    public void setClientMemberCode(String clientMemberCode) {
        this.clientMemberCode = clientMemberCode;
    }

    public String getClientSubsystemCode() {
        return clientSubsystemCode;
    }

    public void setClientSubsystemCode(String clientSubsystemCode) {
        this.clientSubsystemCode = clientSubsystemCode;
    }

    public String getServiceXroadInstance() {
        return serviceXroadInstance;
    }

    public void setServiceXroadInstance(String serviceXroadInstance) {
        this.serviceXroadInstance = serviceXroadInstance;
    }

    public String getServiceMemberClass() {
        return serviceMemberClass;
    }

    public void setServiceMemberClass(String serviceMemberClass) {
        this.serviceMemberClass = serviceMemberClass;
    }

    public String getServiceMemberCode() {
        return serviceMemberCode;
    }

    public void setServiceMemberCode(String serviceMemberCode) {
        this.serviceMemberCode = serviceMemberCode;
    }

    public String getServiceSubsystemCode() {
        return serviceSubsystemCode;
    }

    public void setServiceSubsystemCode(String serviceSubsystemCode) {
        this.serviceSubsystemCode = serviceSubsystemCode;
    }

    public String getServiceServiceCode() {
        return serviceServiceCode;
    }

    public void setServiceServiceCode(String serviceServiceCode) {
        this.serviceServiceCode = serviceServiceCode;
    }

    public String getServiceServiceVersion() {
        return serviceServiceVersion;
    }

    public void setServiceServiceVersion(String serviceServiceVersion) {
        this.serviceServiceVersion = serviceServiceVersion;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setClient(XRoadClient client) {
        this.client = client;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public XRoadClient getClient() {
        return client;
    }

    public XRoadHeaders getXRoadHeaders() {
        return new XRoadHeaders(null, clientXroadInstance, clientMemberClass,
                clientMemberCode, clientSubsystemCode, serviceXroadInstance,
                serviceMemberClass, serviceMemberCode, serviceSubsystemCode,
                serviceServiceCode, serviceServiceVersion, userId, protocolVersion);

    }

}
