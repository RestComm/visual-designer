package org.restcomm.connect.rvd.http.resources;

import com.google.gson.Gson;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.model.ProjectParameters;
import org.restcomm.connect.rvd.storage.MemoryProjectDao;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class ProjectParametersTest {

    @Test
    public void testParameterRetrieval() throws StorageException {
        MemoryProjectDao projectDao = new MemoryProjectDao();

        // check response when no project parameters are set
        ProjectRestService restService = new ProjectRestServiceMocked(projectDao, null, new LoggingContext(""));
        Response response = restService.getProjectParameters("AP1234");
        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertEquals("{\"parameters\":[]}",response.getEntity().toString());

        // check response when a single project parameter is set
        ProjectParameters projectParameters = buildEmptyProjectParameters();
        projectParameters.getParameters().add(new ProjectParameters.Parameter("greeting","Hello there!", "The welcome message of the application"));
        projectDao.projectParameters.put("AP1235", projectParameters);
        response = restService.getProjectParameters("AP1235");
        Assert.assertEquals(response.getStatus(), 200);
        String stringResponse = response.getEntity().toString();
        Gson gson  = new Gson();
        ProjectParameters returnedParameters = gson.fromJson(stringResponse, ProjectParameters.class);
        Assert.assertEquals( projectParameters, returnedParameters);
    }

    @Test
    public void testParameterUpdate() throws StorageException, IOException {
        MemoryProjectDao projectDao = new MemoryProjectDao();

        ProjectParameters parameters1234 = buildEmptyProjectParameters();
        parameters1234.getParameters().add(new ProjectParameters.Parameter("greeting","Hello there!", "The welcome message of the application"));
        projectDao.projectParameters.put("AP1234", parameters1234 );
        projectDao.projectParameters.put("AP1235", buildEmptyProjectParameters());

        // update parameters of an application that already has some
        LoggingContext logging = new LoggingContext("[designer]");
        ProjectRestServiceMocked restService = new ProjectRestServiceMocked(projectDao, buildRequestWithBody("{\"parameters\":[{\"name\":\"greeting\",\"value\":\"changed greeting\",\"description\":\"Welcome message of the application\"}]}"), logging);
        Response response = restService.storeProjectParameters("AP1234");
        Assert.assertEquals(200, response.getStatus());
        String stringResponse = response.getEntity().toString();
        Gson gson  = new Gson();
        ProjectParameters changedParameters = gson.fromJson(stringResponse, ProjectParameters.class);
        // check returned value
        Assert.assertEquals("changed greeting", changedParameters.getParameters().get(0).getValue());
        // check stored value
        Assert.assertEquals("changed greeting", projectDao.projectParameters.get("AP1234").getParameters().get(0).getValue());

        // try to update a parameter (named 'missing') that does not exist
        restService.setRequest(buildRequestWithBody("{\"parameters\":[{\"name\":\"missing\",\"value\":\"changed greeting\",\"description\":\"Welcome message of the application\"}]}"));
        response = restService.storeProjectParameters("AP1234");
        Assert.assertEquals(200, response.getStatus()); // new parameters will be ignored but status won't be affected
        stringResponse = response.getEntity().toString();
        ProjectParameters missingParameters = gson.fromJson(stringResponse, ProjectParameters.class);
        Assert.assertEquals(changedParameters, missingParameters); // parameters should be not be altered

        // create parameters from scratch in a project should fail, but the 'parameters' entity should be created in the storage empty
        restService.setRequest(buildRequestWithBody("{\"parameters\":[{\"name\":\"greeting\",\"value\":\"changed greeting\",\"description\":\"Welcome message of the application\"}]}"));
        response = restService.storeProjectParameters("AP1236");
        Assert.assertEquals(200, response.getStatus());
        ProjectParameters emptyParameters = gson.fromJson(response.getEntity().toString(), ProjectParameters.class);
        Assert.assertEquals(0, emptyParameters.getParameters().size());
    }

    ProjectParameters buildEmptyProjectParameters() {
        ProjectParameters projectParameters = new ProjectParameters();
        List<ProjectParameters.Parameter> parameters = new ArrayList<ProjectParameters.Parameter>();
        return projectParameters;
    }

    /**
     * Create a mocked HttpServletRequest object with a specific string as a body to be consumed as a stream
     *
     * @param body
     */
    public HttpServletRequest buildRequestWithBody(String body) throws IOException {
        // mock input stream
        final ByteArrayInputStream is = new ByteArrayInputStream(body.getBytes());
        ServletInputStream sis = new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return is.read();
            }
        };
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(sis);
        return request;
    }
}
