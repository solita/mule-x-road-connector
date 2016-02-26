package fi.solita.mule.modules.xroad.automation.functional;

import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;

public class XRoadFunctionalTest extends FunctionalTestCase {

    @Override
    protected String getConfigFile() {
        return "automation-test-flows.xml";
    }
    
    @Test
    public void foo() throws Exception {
       runFlow("send-message"); 
    }
}
