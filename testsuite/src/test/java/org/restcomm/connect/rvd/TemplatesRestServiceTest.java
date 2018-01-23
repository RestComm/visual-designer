package org.restcomm.connect.rvd;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.ShrinkWrapMaven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.restcomm.connect.commons.Version;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
@RunWith(Arquillian.class)
public class TemplatesRestServiceTest extends RestServiceTest {

    private final static Logger logger = Logger.getLogger(TemplatesRestServiceTest.class);
    private static final String version = Version.getVersion();

    static final String username = "administrator@company.com";
    static final String password = "adminpass";

    @Test
    public void getSingleTemplate() {
        Client jersey = getClient(username, password);
        WebResource resource = jersey.resource( getResourceUrl("/api/templates/TL1234") );
        ClientResponse response = resource.get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());

        String json = response.getEntity(String.class);
        Assert.assertTrue(json.contains("TL1234"));
        JsonParser parser = new JsonParser();
        Assert.assertTrue(parser.parse(json).isJsonObject());
    }

    @Test
    public void getTemplateList() {
        Client jersey = getClient(username, password);
        WebResource resource = jersey.resource( getResourceUrl("/api/templates") );
        ClientResponse response = resource.get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());

        String json = response.getEntity(String.class);
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(json);
        Assert.assertTrue(root.isJsonObject());
        JsonElement results = root.getAsJsonObject().get("results");
        Assert.assertTrue(results.isJsonArray());
        Assert.assertEquals(3, results.getAsJsonArray().size());
    }

    @Deployment(name = "TemplatesRestServiceTest", managed = true, testable = false)
    public static WebArchive createWebArchiveNoGw() {
        logger.info("Packaging Test App");
        logger.info("version");
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "restcomm-rvd.war");
        final WebArchive restcommArchive = ShrinkWrapMaven.resolver()
                .resolve("org.restcomm:restcomm-connect-rvd:war:" + "1.0-SNAPSHOT").withoutTransitivity()
                .asSingle(WebArchive.class);
        archive = archive.merge(restcommArchive);

        archive.addAsWebInfResource("restcomm.xml", "restcomm.xml");
        archive.delete("/WEB-INF/rvd.xml");
        archive.addAsWebInfResource("rvd.xml", "rvd.xml");
        //StringAsset rvdxml = "<rvd><workspaceLocation>workspace</workspaceLocation><workspaceBackupLocation></workspaceBackupLocation><restcommBaseUrl>" +  </restcommBaseUrl></rvd>";


        logger.info("Packaged Test App");
        return archive;
    }
}
