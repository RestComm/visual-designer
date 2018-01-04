package org.restcomm.connect.rvd.model.steps.es;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.model.steps.StepTestBase;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.CustomizableRvdConfiguration;

import java.util.ArrayList;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class ExternalServiceStepTest extends StepTestBase {

    @Test
    public void testHeaderWithVariableExpansion() throws StorageException, InterpreterException {
        CustomizableRvdConfiguration config = new CustomizableRvdConfiguration();
        buildApplicationContext(config);
        // we create the interpreter and a module variable called 'thename' with content 'nick'
        Interpreter interpreter = buildInterpreter(appendMultivaluedMap(null,"module_thename","nick"), null);
        ExternalServiceStep step = new ExternalServiceStep();
        String esPath = "/external-service.php";
        step.setUrl("http://localhost:8099" + esPath);
        step.httpHeaders = new ArrayList<ExternalServiceStep.HttpHeader>();
        step.httpHeaders.add(new ExternalServiceStep.HttpHeader("name", "$thename"));

        // setup listening part
        stubFor(get(urlMatching(esPath)).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("")));
        // make request
        String next = step.process(interpreter, mockHttpServletRequest("http://localhost/restcomm-rvd/"));

        Assert.assertNull(next); // no rerouting
        // make sure an http header is added and that variable expansion has occured
        verify(getRequestedFor(urlEqualTo(esPath)).withHeader("name", equalTo("nick")));
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8099);
}
