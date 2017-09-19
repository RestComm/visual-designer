package org.restcomm.connect.rvd.utils;

import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.commons.http.SslMode;
import org.restcomm.connect.rvd.configuration.RvdConfig;

import java.io.File;
import java.net.URI;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class CustomizableRvdConfiguration implements RvdConfiguration {

    @Override
    public String getWorkspaceBasePath() {
        return "";
    }

    @Override
    public String getProjectBasePath(String projectName) {
        return getWorkspaceBasePath() + File.separator + projectName;
    }

    @Override
    public SslMode getSslMode() {
        return SslMode.strict;
    }

    @Override
    public Integer getExternalServiceTimeout() {
        return RvdConfiguration.DEFAULT_ES_TIMEOUT;
    }

    @Override
    public boolean getUseHostnameToResolveRelativeUrl() {
        return RvdConfiguration.DEFAULT_USE_HOSTNAME_TO_RESOLVE_RELATIVE_URL;
    }

    @Override
    public String getHostnameOverride() {
        return null;
    }

    @Override
    public URI getRestcommBaseUri() {
        return null;
    }

    @Override
    public String getApplicationsRelativeUrl() {
        return null;
    }

    @Override
    public Integer getMaxMediaFileSize() {
        return null;
    }

    @Override
    public Boolean getVideoSupport() {
        return null;
    }

    @Override
    public RvdConfig getRawRvdConfig() {
        return null;
    }

    @Override
    public String getBaseUrl() {
        return null;
    }

    @Override
    public Boolean useAbsoluteApplicationUrl() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public boolean isUssdSupport() {
        return false;
    }

    @Override
    public String getWelcomeMessage() {
        return null;
    }

    @Override
    public String getRvdInstanceId() {
        return null;
    }
}
