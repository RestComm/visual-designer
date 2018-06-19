package org.restcomm.connect.rvd;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.ShrinkWrapMaven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
@RunWith(Arquillian.class)
public class ConfigurationRestServiceTest extends RestServiceTest {



    @Test
    public void getPublicConfiguration() {
        Client jersey = getClient(null, null);
        WebResource resource = jersey.resource( getResourceUrl("/services/config") );
        ClientResponse response = resource.get(ClientResponse.class);
        Assert.assertEquals(200, response.getStatus());
        String json = response.getEntity(String.class);
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(json).getAsJsonObject();
        // check presense of 'projectVersion' field
        Assert.assertNotNull("projectVersion field should exist", object.get("projectVersion"));
    }

    @Deployment(name = "ProjectRestServiceTest", managed = true, testable = false)
    public static WebArchive createWebArchiveNoGw() {
        //logger.info("Packaging Test App");
        //logger.info("version");
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "visual-designer.war");

        final WebArchive restcommArchive = ShrinkWrapMaven.resolver()
                .resolve(getMavenDepId()).withoutTransitivity()
                .asSingle(WebArchive.class);
        archive = archive.merge(restcommArchive);

        archive.addAsWebInfResource("restcomm.xml", "restcomm.xml");
        archive.delete("/WEB-INF/rvd.xml");
        archive.addAsWebInfResource("rvd.xml", "rvd.xml");
        //StringAsset rvdxml = "<rvd><workspaceLocation>workspace</workspaceLocation><workspaceBackupLocation></workspaceBackupLocation><restcommBaseUrl>" +  </restcommBaseUrl></rvd>";


        //logger.info("Packaged Test App");
        return archive;
    }
}
