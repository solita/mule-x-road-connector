package fi.solita.mule.modules.xroad.automation.testcases;

import org.junit.Before;
import org.mule.tools.devkit.ctf.mockup.ConnectorDispatcher;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

import fi.solita.mule.modules.xroad.XRoadConnector;

public abstract class AbstractTestCase {
	
	private XRoadConnector connector;
	private ConnectorDispatcher<XRoadConnector> dispatcher;
	
	
	protected XRoadConnector getConnector() {
		return connector;
	}


	protected ConnectorDispatcher<XRoadConnector> getDispatcher() {
		return dispatcher;
	}

	@Before
	public void init() throws Exception {
        ConnectorTestContext.initialize(XRoadConnector.class, false);
	
		ConnectorTestContext<XRoadConnector> context = ConnectorTestContext.getInstance();
		dispatcher = context.getConnectorDispatcher();
		connector = dispatcher.createMockup();
		
	}

}
