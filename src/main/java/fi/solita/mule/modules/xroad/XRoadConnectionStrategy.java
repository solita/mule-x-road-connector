package fi.solita.mule.modules.xroad;


import org.mule.api.ConnectionException;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.param.ConnectionKey;

@ConnectionManagement(configElementName = "config", friendlyName = "Connection Managament type strategy")
public class XRoadConnectionStrategy {

	private XRoadClient client;
	
	@Connect
	@TestConnectivity
	public void connect(@ConnectionKey String username,
			@Password String password) throws ConnectionException {
		client = new XRoadClient();
		client.initialize();
	}

	@Disconnect
	public void disconnect() {
		client = null;
	}

	@ValidateConnection
	public boolean isConnected() {
		return client != null;
	}

	@ConnectionIdentifier
	public String connectionId() {
		return "001";
	}
	
	public XRoadClient getClient() {
		return client;
	}
}
