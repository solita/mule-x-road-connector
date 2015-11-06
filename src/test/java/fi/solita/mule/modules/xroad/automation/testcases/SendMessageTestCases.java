
package fi.solita.mule.modules.xroad.automation.testcases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transformer.TransformerException;
import org.mule.module.xml.transformer.jaxb.JAXBMarshallerTransformer;
import org.mule.transformer.types.SimpleDataType;
import org.w3c.dom.Document;

import fi.solita.mule.modules.xroad.api.RovaDelegateService;
import fi.solita.mule.modules.xroad.automation.AbstractTestCase;

public class SendMessageTestCases
    extends AbstractTestCase
{


    @Test
    //@Category(RegressionTests.class)
    public void testSendMessage() throws JAXBException, InitialisationException, TransformerException {
    	RovaDelegateService request = new RovaDelegateService();
    	
    	JAXBMarshallerTransformer transformer = getJaxbMarshallerTransformer(Document.class);
    	Document doc = (Document) transformer.transform(request);
        Object result = getConnector().sendMessage(doc);
        assertNotNull(result);
        assertTrue(result instanceof Document);
    }

	private <T> JAXBMarshallerTransformer getJaxbMarshallerTransformer(Class<T> returnDataType)
			throws JAXBException, InitialisationException {
		JAXBContext context = JAXBContext.newInstance(RovaDelegateService.class);
    	JAXBMarshallerTransformer transformer = new JAXBMarshallerTransformer(context, new SimpleDataType<T>(returnDataType));
   
    	transformer.initialise();
		return transformer;
	}

}
