package org.restcomm.connect.rvd;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.ShrinkWrapMaven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Tests cross site ajax requests with various configurations in rvd.xml
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */

//  Sample cors OPTIONS request

//    Request URL:http://172.17.0.1:8081/restcomm-rvd/services/ras/apps/metadata
//    Request Method:OPTIONS
//
//    Accept:*/*
//    Accept-Encoding:gzip, deflate, sdch
//    Accept-Language:en-US,en;q=0.8,el;q=0.6
//    Access-Control-Request-Headers:authorization,content-type
//    Access-Control-Request-Method:POST
//    Cache-Control:no-cache
//    Connection:keep-alive
//    Host:172.17.0.1:8081
//    Origin:http://this:8080
//    Pragma:no-cache
//    Referer:http://this:8080/
//    User-Agent:Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/58.0.3029.110 Chrome/58.0.3029.110 Safari/537.36
//
//  Sample cors response
//
//    Access-Control-Allow-Credentials:true
//    Access-Control-Allow-Headers:origin, content-type, accept, authorization
//    Access-Control-Allow-Methods:GET, POST, PUT, DELETE, OPTIONS, HEAD
//    Access-Control-Allow-Origin:http://this:8080
//    Access-Control-Max-Age:1209600
//    Allow:OPTIONS,POST
//    Connection:keep-alive
//    Content-Length:588
//    Content-Type:application/vnd.sun.wadl+xml
//    Date:Thu, 08 Jun 2017 08:32:45 GMT
//    Last-modified:Thu, 08 Jun 2017 08:12:15 UTC
//    Server:WildFly/10
//    X-Powered-By:Undertow/1
//
// Sample cors POST request
//
//    Accept:application/json, text/plain, */*
//    Accept-Encoding:gzip, deflate
//    Accept-Language:en-US,en;q=0.8,el;q=0.6
//    Authorization:Basic YWRtaW5pc3RyYXRvckBjb21wYW55LmNvbTo3MGZkZGUzMThiODk5Yzk4MjIwNDVjNGFlNTRlYWRjZQ==
//    Cache-Control:no-cache
//    Connection:keep-alive
//    Content-Length:112
//    Content-Type:application/json
//    Host:172.17.0.1:8081
//    Origin:http://this:8080
//    Pragma:no-cache
//    Referer:http://this:8080/
//    User-Agent:Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/58.0.3029.110 Chrome/58.0.3029.110 Safari/537.36
//
// Sample cors POST response
//
//    Access-Control-Allow-Credentials:true
//    Access-Control-Allow-Headers:origin, content-type, accept, authorization
//    Access-Control-Allow-Methods:GET, POST, PUT, DELETE, OPTIONS, HEAD
//    Access-Control-Allow-Origin:http://this:8080
//    Access-Control-Max-Age:1209600
//    Connection:keep-alive
//    Content-Type:application/json
//    Date:Thu, 08 Jun 2017 08:32:45 GMT
//    Server:WildFly/10
//    Transfer-Encoding:chunked
//    X-Powered-By:Undertow/1

@RunWith(Arquillian.class)
public class CorsAccessAnyOriginTest extends RestServiceTest {
    private final static Logger logger = Logger.getLogger(ProjectRestServiceTest.class);

    static final String username = "administrator@company.com";
    static final String password = "adminpass";

    @Test
    public void testCorsRequest() {
        // Bypass jersey restriction for "Origin" header. By default it can't be added to a WebResource object.
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        // from rvd.xml: <origin>*</origin>
        Client jersey = getClient(username, password);

        WebResource resource = jersey.resource(getResourceUrl("/services/projects"));
        ClientResponse response = resource.header("Origin", "http://host.restcomm.com").options(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://host.restcomm.com",response.getHeaders().getFirst("Access-Control-Allow-Origin"));

        resource = jersey.resource(getResourceUrl("/services/projects"));
        response = resource.header("Origin", "http://otherhost.restcomm.com").options(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://otherhost.restcomm.com", response.getHeaders().getFirst("Access-Control-Allow-Origin"));
    }

    @Deployment(name = "CorsAccessAnyOriginTest", managed = true, testable = false)
    public static WebArchive createWebArchiveNoGw() {
        logger.info("Packaging Test App");
        logger.info("version");
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "visual-designer.war");
        final WebArchive restcommArchive = ShrinkWrapMaven.resolver()
                .resolve(getMavenDepId()).withoutTransitivity()
                .asSingle(WebArchive.class);
        archive = archive.merge(restcommArchive);

        archive.delete("/WEB-INF/rvd.xml");
        archive.delete("/WEB-INF/web.xml");
        archive.addAsWebInfResource("CorsAccessTest_any.xml", "rvd.xml");
        archive.addAsWebInfResource("enable_cors_filter_web.xml", "web.xml");
        logger.info("Packaged Test App");
        return archive;
    }
}
