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

package org.restcomm.connect.rvd.configuration;

import java.util.List;

/**
 * Model class for loading configuration from rvd.xml
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class RvdConfig {
    private String workspaceLocation;
    private String workspaceBackupLocation;
    private String sslMode;
    private String hostnameOverride;
    private Boolean useHostnameToResolveRelativeUrl;
    private String restcommBaseUrl;
    private String externalServiceTimeout;
    private Boolean videoSupport;
    private Integer maxMediaFileSize;
    private String  baseUrl; //e.g. http://this:8080/restcomm-rvd
    private Boolean useAbsoluteApplicationUrl;
    private String ussdSupport;

    private List<String> allowedCorsOrigins;

    public RvdConfig() {
    }

    public RvdConfig(String externalServiceTimeout, String workspaceLocation, String workspaceBackupLocation, String restcommPublicIp, String sslMode) {
        super();
        this.workspaceLocation = workspaceLocation;
        this.workspaceBackupLocation = workspaceBackupLocation;
        this.sslMode = sslMode;
        this.externalServiceTimeout = externalServiceTimeout;
    }

    public String getWorkspaceLocation() {
        return workspaceLocation;
    }

    public String getWorkspaceBackupLocation() {
        return workspaceBackupLocation;
    }

    public String getSslMode() {
        return sslMode;
    }

    public String getRestcommBaseUrl() {
        return restcommBaseUrl;
    }

    public String getExternalServiceTimeout() {
        return externalServiceTimeout;
    }

    public Boolean getVideoSupport() {
        return videoSupport;
    }

    public Integer getMaxMediaFileSize() {
        return maxMediaFileSize;
    }

    public String getHostnameOverride() {
        return hostnameOverride;
    }

    public Boolean getUseHostnameToResolveRelativeUrl() {
        return useHostnameToResolveRelativeUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Boolean useAbsoluteApplicationUrl() {
        return useAbsoluteApplicationUrl;
    }

    public List<String> getAllowedCorsOrigins() {
        return allowedCorsOrigins;
    }

    public void setAllowedCorsOrigins(List<String> allowedCorsOrigins) {
        this.allowedCorsOrigins = allowedCorsOrigins;
    }

    public String getUssdSupport() {
        return ussdSupport;
    }

    public void setUssdSupport(String ussdSupport) {
        this.ussdSupport = ussdSupport;
    }

    public void setExternalServiceTimeout(String externalServiceTimeout) {
        this.externalServiceTimeout = externalServiceTimeout;
    }
}
