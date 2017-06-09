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
 */

package org.restcomm.connect.rvd.http.cors;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.configuration.RvdConfig;

import javax.ws.rs.ext.Provider;
import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    List<String> allowedOrigins; // If null, or [], no origins will be allowed

    public CorsFilter() {
        //String rootPath =  CorsFilter.class.getResource(".").getFile();
        URL url = CorsFilter.class.getClassLoader().getResource("rvd.version");
        String rootPath = url.getFile();

        String webInfPath = rootPath.substring(0, rootPath.indexOf("/classes/"));
        File rvdXmlFile = new File(webInfPath + "/rvd.xml");
        RvdConfig rvdConfig = RvdConfiguration.loadRvdXmlConfig(rvdXmlFile.getAbsolutePath());
        allowedOrigins = rvdConfig.getAllowedCorsOrigins();
    }

    @Override
    public ContainerResponse filter(ContainerRequest cres, ContainerResponse response) {
        String requestOrigin = cres.getHeaderValue("Origin");
        if (requestOrigin != null) { // is this is a cors request (ajax request that targets a different domain than the one the page was loaded from)
            if (allowedOrigins != null && !allowedOrigins.isEmpty()) {  // no cors restriction apply of allowedOrigins == null
                if (allowedOrigins.contains(requestOrigin) || allowedOrigins.contains("*")) {
                    // only return the origin the client informed
                    response.getHttpHeaders().add("Access-Control-Allow-Origin", requestOrigin);
                    response.getHttpHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
                    response.getHttpHeaders().add("Access-Control-Allow-Credentials", "true");
                    response.getHttpHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
                    response.getHttpHeaders().add("Access-Control-Max-Age", "1209600");
                }
            }
        }
        return response;

    }
}
