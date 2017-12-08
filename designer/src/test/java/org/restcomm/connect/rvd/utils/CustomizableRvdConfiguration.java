package org.restcomm.connect.rvd.utils;

import org.restcomm.connect.rvd.BaseRvdConfiguration;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.commons.http.SslMode;
import org.restcomm.connect.rvd.configuration.RvdMaxPerHost;

import java.io.File;
import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class CustomizableRvdConfiguration extends BaseRvdConfiguration {

    Boolean videoSupport;

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
    public Integer getExternalServiceMaxConns() {
        return null;
    }

    @Override
    public Integer getExternalServiceMaxConnsPerRoute() {
        return null;
    }

    @Override
    public Integer getExternalServiceTTL() {
        return null;
    }

    @Override
    public List<RvdMaxPerHost> getExternalServiceMaxPerRoute() {
        return null;
    }

    @Override
    public Integer getDefaultHttpTimeout() {
        return null;
    }

    @Override
    public Integer getDefaultHttpMaxConns() {
        return null;
    }

    @Override
    public Integer getDefaultHttpMaxConnsPerRoute() {
        return null;
    }

    @Override
    public Integer getDefaultHttpTTL() {
        return null;
    }

    @Override
    public List<RvdMaxPerHost> getDefaultHttpMaxPerRoute() {
        return null;
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
    public String getRestcommBaseUri() {
        return null;
    }

    @Override
    public Boolean getDynamicRestcommResolving() {
        return RvdConfiguration.DEFAULT_DYNAMIC_RESTCOMM_RESOLVING;
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
        return videoSupport;
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

    public void setVideoSupport(Boolean videoSupport) {
        this.videoSupport = videoSupport;
    }

    public void setProjectTemplatesWorkspacePath(String projectTemplatesWorkspacePath) {
        this.projectTemplatesWorkspacePath = projectTemplatesWorkspacePath;
    }


}
