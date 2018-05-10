/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package org.restcomm.connect.rvd;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class RestServiceTest {


    @ArquillianResource
    private Deployer deployer;
    @ArquillianResource
    URL deploymentUrl;

    @Before
    public void before() {
        //stubFor(get(urlEqualTo("/restcomm/1012-04-24/Accounts.json/"+username))
        stubFor(get(urlMatching("/restcomm/2012-04-24/Accounts.json/administrator@company.com"))
//                .withHeader("Accept", equalTo("text/xml"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"sid\":\"ACae6e420f425248d6a26948c17a9e2acf\",\"email_address\":\"administrator@company.com\",\"status\":\"active\",\"role\":\"administrator\"}")));
    }

    @After
    public void after() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    protected Client getClient(String username, String password) {
        Client jersey = Client.create();
        if (username != null)
            jersey.addFilter(new HTTPBasicAuthFilter(username, password));
        return jersey;
    }

    protected String getResourceUrl(String suffix) {
        String urlString = deploymentUrl.toString();
        if ( urlString.endsWith("/") )
            urlString = urlString.substring(0,urlString.length()-1);

        if ( suffix != null && !suffix.isEmpty()) {
            if (!suffix.startsWith("/"))
                suffix = "/" + suffix;
            return urlString + suffix;
        } else
            return urlString;

    }

    /**
     * Returnes the maven id for the Designer binary to loaded on tomcat and tested.
     *
     * For example
     *      org.restcomm:visual-designer:war:1.0-SNAPSHOT
     *
     * This is normally populated in the testsuite .pom file. Search for mavenDepId.
     * It can also be overriden using command line -DmavenDepId parameters when launching the testsuite.
     * Thus, one can use different binaries in community and commercial versions.
     *
     * @return String
     */
    protected static String getMavenDepId() {
        String mavenDepId = System.getProperty("mavenDepId"); // the maven groupId:artifactId:war:version of the .war file to be tested. Sth like org.restcomm:visual-designer:war:1.0-SNAPSHOT
        System.out.println("Using '" + mavenDepId + "' binary for testing from maven repo. Override in the commandline by setting -DmavenDepId=groupId:artifactId:war:version parameter");
        return mavenDepId;

    }
}
