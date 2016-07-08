/**
 * MIT License Copyright (c) 2016 Solita Oy
 */
package fi.solita.mule.modules.xroad;

import static org.apache.commons.lang.StringUtils.defaultString;

import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;

public class XRoadHeaders {
	public final String id;
	public final String clientXroadInstance;
	public final String clientMemberClass;
	public final String clientMemberCode;
	public final String clientSubsystemCode;
	public final String serviceXroadInstance;
	public final String serviceMemberClass;
	public final String serviceMemberCode;
	public final String serviceSubsystemCode;
	public final String serviceServiceCode;
	public final String serviceServiceVersion;
	public final Boolean async;
	public final String userId;
	public final String protocolVersion;
	
	public XRoadHeaders(String id, String clientXroadInstance,
			String clientMemberClass, String clientMemberCode,
			String clientSubsystemCode, String serviceXroadInstance,
			String serviceMemberClass, String serviceMemberCode,
			String serviceSubsystemCode, String serviceServiceCode,
			String serviceServiceVersion, Boolean async, String userId, String protocolVersion) {
		this.id = id;
		this.clientXroadInstance = clientXroadInstance;
		this.clientMemberClass = clientMemberClass;
		this.clientMemberCode = clientMemberCode;
		this.clientSubsystemCode = clientSubsystemCode;
		this.serviceXroadInstance = serviceXroadInstance;
		this.serviceMemberClass = serviceMemberClass;
		this.serviceMemberCode = serviceMemberCode;
		this.serviceSubsystemCode = serviceSubsystemCode;
		this.serviceServiceCode = serviceServiceCode;
		this.serviceServiceVersion = serviceServiceVersion;
		this.async = async;
		this.userId = userId;
		this.protocolVersion = protocolVersion;
	}
	
	/**
	 * @param overridedHeaders Headers to merge. Non null values are taken from here
	 * @return New object of merged headers with values taken from overridedHeaders if they are not null.
	 * Otherwise current values are returned
	 */
	public XRoadHeaders merge(XRoadHeaders overridedHeaders) {
		
		XRoadHeaders result = new XRoadHeaders(
			defaultString(overridedHeaders.id, id),
			defaultString(overridedHeaders.clientXroadInstance, clientXroadInstance),
			defaultString(overridedHeaders.clientMemberClass, clientMemberClass),
		    defaultString(overridedHeaders.clientMemberCode, clientMemberCode),
		    defaultString(overridedHeaders.clientSubsystemCode, clientSubsystemCode),
		    defaultString(overridedHeaders.serviceXroadInstance, serviceXroadInstance),
		    defaultString(overridedHeaders.serviceMemberClass, serviceMemberClass),
		    defaultString(overridedHeaders.serviceMemberCode, serviceMemberCode),
		    defaultString(overridedHeaders.serviceSubsystemCode,serviceSubsystemCode),
		    defaultString(overridedHeaders.serviceServiceCode, serviceServiceCode),
		    defaultString(overridedHeaders.serviceServiceVersion, serviceServiceVersion),
		    (overridedHeaders.async != null ? overridedHeaders.async : null),
		    defaultString(overridedHeaders.userId, userId),
		    defaultString(overridedHeaders.protocolVersion, protocolVersion));
		return result;
	}
	
	public void validate() {
	    Objects.requireNonNull(id, "id must not be null");
	    Objects.requireNonNull(clientXroadInstance, "clientXroadInstance must not be null");
	    Objects.requireNonNull(clientMemberClass, "clientMemberClass must not be null");
        Objects.requireNonNull(clientMemberCode, "clientMemberCode must not be null");
        Objects.requireNonNull(clientSubsystemCode, "clientSubsystemCode must not be null");
        Objects.requireNonNull(serviceXroadInstance, "serviceXroadInstance must not be null");
        Objects.requireNonNull(serviceMemberClass, "serviceMemberClass must not be null");
        Objects.requireNonNull(serviceMemberCode, "serviceMemberCode must not be null");
        Objects.requireNonNull(serviceSubsystemCode, "serviceSubsystemCode must not be null");
        Objects.requireNonNull(serviceServiceCode, "serviceServiceCode must not be null");
        Objects.requireNonNull(serviceServiceVersion, "serviceServiceVersion must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(protocolVersion, "protocolVersion must not be null");
	}

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}