
package fi.solita.mule.modules.xroad.automation;

import fi.solita.mule.modules.xroad.XRoadConnector;
import org.junit.After;
import org.junit.Before;
import org.mule.tools.devkit.ctf.mockup.ConnectorDispatcher;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

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
    public void init() {
        //Initialization for single-test run
        ConnectorTestContext.initialize(XRoadConnector.class, false);
        //Context instance
        ConnectorTestContext<XRoadConnector> context = ConnectorTestContext.getInstance(XRoadConnector.class);
        //Connector dispatcher
        dispatcher = context.getConnectorDispatcher();
        connector = dispatcher.createMockup();
    }

    @After
    public void shutDown() {
        ConnectorTestContext.shutDown(false);
    }

}
