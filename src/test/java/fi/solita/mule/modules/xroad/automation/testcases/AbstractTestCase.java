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
		
		//Initialization for single-test run
        ConnectorTestContext.initialize(XRoadConnector.class, false);
		
		//Context instance
		ConnectorTestContext<XRoadConnector> context = ConnectorTestContext.getInstance(XRoadConnector.class);
		
		//Connector dispatcher
		dispatcher = context.getConnectorDispatcher();
		
		connector = dispatcher.createMockup();
		
	}

}
