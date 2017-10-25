package org.restcomm.connect.rvd.model.steps;

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import org.mockito.Mockito;
import org.restcomm.connect.rvd.ApplicationContext;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.logging.MockedCustomLogger;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.model.ProjectSettings;
import org.restcomm.connect.rvd.storage.ProjectDao;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class StepTestBase {

    protected HttpServletRequest mockHttpServletRequest(String requestUrl) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        URI uri;
        try {
            uri = new URI(requestUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Mockito.when(request.getContextPath()).thenReturn(requestUrl);
        Mockito.when(request.getScheme()).thenReturn(uri.getScheme());
        Mockito.when(request.getLocalAddr()).thenReturn(uri.getHost()); //TODO check if localAddr is the same as host
        Mockito.when(request.getServerPort()).thenReturn(uri.getPort());
        return request;
    }

    /**
     * Creates or updates a multivalue map with key value pairs. You may chain mail calls together. Set map parameter to
     * null in order to create a new map.
     *
     * @param map
     * @param key
     * @param value
     * @return a multivalue map
     */
    protected MultivaluedMap<String,String> appendMultivaluedMap(MultivaluedMap<String,String> map, String key, String value) {
        if (map == null )
            map = new StringKeyIgnoreCaseMultivaluedMap<String>();
        if ( key != null )
            map.put(key, Arrays.asList(value));
        return map;
    }

    protected Interpreter buildInterpreter(MultivaluedMap<String,String> params, ProjectDao dao) throws StorageException {
        ApplicationContext appContext = new ApplicationContext();
        // TODO init appContext ??
        Interpreter interpreter = new Interpreter(
                "testapp",
                mockHttpServletRequest("http://localhost/restcomm-rvd/"),
                params,
                appContext,
                new LoggingContext("log-prefix"),
                new MockedCustomLogger(),
                ProjectSettings.createDefault(),
                dao
        );
        return interpreter;

    }

    /*
    protected ProjectDao buildEmptyProjectDao() throws StorageException {
        ProjectDao dao = Mockito.mock(ProjectDao.class);
        Mockito.when(dao.loadBootstrapInfo()).thenReturn(null);
        Mockito.when(dao.loadNode(Mockito.anyString())).thenThrow(new StorageException("Empty ProjectDao is not supposed to return modules"));
        return dao;
    }
    */


}
