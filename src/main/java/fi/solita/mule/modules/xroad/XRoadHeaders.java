package fi.solita.mule.modules.xroad;

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
	public final String userId;
	
	public XRoadHeaders(String id, String clientXroadInstance,
			String clientMemberClass, String clientMemberCode,
			String clientSubsystemCode, String serviceXroadInstance,
			String serviceMemberClass, String serviceMemberCode,
			String serviceSubsystemCode, String serviceServiceCode,
			String serviceServiceVersion, String userId) {
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
		this.userId = userId;
	}
	
	
}