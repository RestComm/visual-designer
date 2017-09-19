/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2016, Telestax Inc and individual contributors
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
package org.restcomm.connect.rvd.restcomm;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.commons.http.CustomHttpClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Rule;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.restcomm.connect.rvd.configuration.RvdMaxPerHost;
import org.restcomm.connect.rvd.exceptions.AccessApiException;

/**
 * @author Orestis Tsakiridis
 */
public class RestcommClientTest {

    private static URI fallbackUri;
    private static RvdConfiguration configuration;

    @BeforeClass
    public static void init() {
        // The following are disabled until the test is fixed
        //fallbackUri = URI.create("http://123.123.123.123:7070");
        //configuration = TestUtils.initRvdConfiguration();
    }

    @Ignore
    @Test(expected = RestcommClient.RestcommClientInitializationException.class)
    public void exceptionThrownWhenNoCredentialsCanBeDetermined() throws RestcommClient.RestcommClientInitializationException, URISyntaxException {
        CustomHttpClientBuilder httpClientBuilder = new CustomHttpClientBuilder(configuration);
        RestcommClient client = new RestcommClient(null, null, httpClientBuilder.buildHttpClient());
    }

    @Test(expected = RestcommClient.RestcommClientException.class)
    public void noMoreConnectionsInPool() throws Exception {
        int connsPerRoute= 5;
        int timeout  = 1000;
        final String path = "/noMoreConnections";
        List<RvdMaxPerHost> routes = new ArrayList();
        routes.add(new RvdMaxPerHost("http://127.0.0.1:8099", connsPerRoute));
        RvdConfiguration configuration = Mockito.mock(RvdConfiguration.class);
        when(configuration.getDefaultHttpMaxConns()).thenReturn(connsPerRoute * 2);
        when(configuration.getDefaultHttpMaxConnsPerRoute()).thenReturn(1);
        when(configuration.getDefaultHttpTimeout()).thenReturn(timeout);
        when(configuration.getDefaultHttpTTL()).thenReturn(3000);
        when(configuration.getDefaultHttpMaxPerRoute()).thenReturn(routes);
        CustomHttpClientBuilder httpClientBuilder = new CustomHttpClientBuilder(configuration);
        stubFor(get(urlMatching(path)).willReturn(aResponse()
                .withFixedDelay(timeout * 10)//delay will cause tiemout to happen on second request
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"sid\":\"" + 1234 + "\",\"email_address\":\"" + "jander@clander.com" + "\",\"status\":\"active\",\"role\":\"administrator\"}")));
        URI create = URI.create("http://127.0.0.1:8099");
        final RestcommClient client = new RestcommClient(create, "authHeader", httpClientBuilder.buildHttpClient());
        final Gson gson = new Gson();

        for (int i = 0; i < connsPerRoute; i++) {
            Thread newThread = new Thread() {
                public void run() {
                    try {
                        RestcommAccountInfo done = client.get(path).done(gson, RestcommAccountInfo.class);
                        Assert.fail("these request are expected to fail because of timeout.");
                    } catch (AccessApiException ex) {
                    }
                }
            };
            newThread.start();
        }
        Thread.sleep(200);
        client.get("/noMoreConnections").done(gson, RestcommAccountInfo.class);
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8099);

}
