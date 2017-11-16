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
    private Integer externalServiceMaxConns;
    private Integer externalServiceMaxConnsPerRoute;
    private Integer externalServiceTTL;
    private List<RvdMaxPerHost> externalServicepMaxPerRoute;
    private Integer defaultHttpTimeout;
    private Integer defaultHttpMaxConns;
    private Integer defaultHttpMaxConnsPerRoute;
    private Integer defaultHttpTTL;
    private List<RvdMaxPerHost> defaultHttpMaxPerRoute;
    private Boolean videoSupport;
    private Integer maxMediaFileSize;
    private String  baseUrl; //e.g. http://this:8080/restcomm-rvd
    private Boolean useAbsoluteApplicationUrl;
    private String ussdSupport;
    private String instanceId;
    private Boolean dynamicRestcommResolving;

    private List<String> allowedCorsOrigins;

    public RvdConfig() {
    }

    public RvdConfig(String workspaceLocation, String workspaceBackupLocation, String restcommPublicIp, String sslMode) {
        super();
        this.workspaceLocation = workspaceLocation;
        this.workspaceBackupLocation = workspaceBackupLocation;
        this.sslMode = sslMode;
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

    public String getInstanceId() {
        return instanceId;
    }

    public Integer getExternalServiceMaxConns() {
        return externalServiceMaxConns;
    }

    public void setExternalServiceMaxConns(Integer externalServiceMaxConns) {
        this.externalServiceMaxConns = externalServiceMaxConns;
    }

    public Integer getExternalServiceMaxConnsPerRoute() {
        return externalServiceMaxConnsPerRoute;
    }

    public void setExternalServiceMaxConnsPerRoute(Integer externalServiceMaxConnsPerRoute) {
        this.externalServiceMaxConnsPerRoute = externalServiceMaxConnsPerRoute;
    }

    public Integer getExternalServiceTTL() {
        return externalServiceTTL;
    }

    public void setExternalServiceTTL(Integer externalServiceTTL) {
        this.externalServiceTTL = externalServiceTTL;
    }

    public Integer getDefaultHttpTimeout() {
        return defaultHttpTimeout;
    }

    public void setDefaultHttpTimeout(Integer defaultHttpTimeout) {
        this.defaultHttpTimeout = defaultHttpTimeout;
    }

    public Integer getDefaultHttpMaxConns() {
        return defaultHttpMaxConns;
    }

    public void setDefaultHttpMaxConns(Integer defaultHttpMaxConns) {
        this.defaultHttpMaxConns = defaultHttpMaxConns;
    }

    public Integer getDefaultHttpMaxConnsPerRoute() {
        return defaultHttpMaxConnsPerRoute;
    }

    public void setDefaultHttpMaxConnsPerRoute(Integer defaultHttpMaxConnsPerRoute) {
        this.defaultHttpMaxConnsPerRoute = defaultHttpMaxConnsPerRoute;
    }

    public Integer getDefaultHttpTTL() {
        return defaultHttpTTL;
    }

    public void setDefaultHttpTTL(Integer defaultHttpTTL) {
        this.defaultHttpTTL = defaultHttpTTL;
    }

    public Boolean getUseAbsoluteApplicationUrl() {
        return useAbsoluteApplicationUrl;
    }

    public void setUseAbsoluteApplicationUrl(Boolean useAbsoluteApplicationUrl) {
        this.useAbsoluteApplicationUrl = useAbsoluteApplicationUrl;
    }

    public List<RvdMaxPerHost> getExternalServicepMaxPerRoute() {
        return externalServicepMaxPerRoute;
    }

    public void setExternalServicepMaxPerRoute(List<RvdMaxPerHost> externalServicepMaxPerRoute) {
        this.externalServicepMaxPerRoute = externalServicepMaxPerRoute;
    }

    public List<RvdMaxPerHost> getDefaultHttpMaxPerRoute() {
        return defaultHttpMaxPerRoute;
    }

    public void setDefaultHttpMaxPerRoute(List<RvdMaxPerHost> defaultHttpMaxPerRoute) {
        this.defaultHttpMaxPerRoute = defaultHttpMaxPerRoute;
    }

    public Boolean getDynamicRestcommResolving() {
        return dynamicRestcommResolving;
    }
}
