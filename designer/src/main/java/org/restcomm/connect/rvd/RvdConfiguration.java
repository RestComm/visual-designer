package org.restcomm.connect.rvd;

import org.restcomm.connect.rvd.commons.http.SslMode;
import org.restcomm.connect.rvd.configuration.RvdConfig;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.restcomm.connect.rvd.configuration.RvdMaxPerHost;

/**
 * Implement this interface to provide custom configuration for testing.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface RvdConfiguration {
    SslMode DEFAULT_SSL_MODE = SslMode.strict;
    boolean DEFAULT_USE_HOSTNAME_TO_RESOLVE_RELATIVE_URL = true;
    boolean DEFAULT_USE_ABSOLUTE_APPLICATION_URL = false;
    boolean DEFAULT_USSD_SUPPORT = true;
    String DEFAULT_WELCOME_MESSAGE = "Welcome to Telestax Restcom Visual Designer Demo";

    String WORKSPACE_DIRECTORY_NAME = "workspace";
    String PROTO_DIRECTORY_PREFIX = "_proto";
    String REST_SERVICES_PATH = "services"; // the "services" from the /restcomm-rvd/services/apps/... path
    String USERS_DIRECTORY_NAME = "@users";

    String WAVS_DIRECTORY_NAME = "wavs";
    String RVD_PROJECT_VERSION = "1.12"; // version for rvd project syntax
    String PACKAGING_VERSION = "1.0";
    String RAS_APPLICATION_VERSION = "2"; // version of the RAS application specification
    String STICKY_PREFIX = "sticky_"; // a  prefix for rvd sticky variable names
    String MODULE_PREFIX = "module_"; // a  prefix for rvd module-scoped variable names
    String CORE_VARIABLE_PREFIX = "core_"; // a prefix for rvd variables that come from Restcomm parameters
    String PACKAGING_DIRECTORY_NAME = "packaging";
    // http client (ES)
    int DEFAULT_ES_TIMEOUT = 5000; // milliseconds
    int DEFAULT_ES_MAX_CONNS = 2000; // connections
    int DEFAULT_ES_MAX_CONNS_PER_ROUTE = 10; // connections
    int DEFAULT_ES_TTL = 30; // seconds
    // http client (default)
    int DEFAULT_HTTP_TIMEOUT = 1000; // milliseconds
    int DEFAULT_HTTP_MAX_CONNS = 200; // connections
    int DEFAULT_HTTP_MAX_CONNS_PER_ROUTE = 100; // connections
    int DEFAULT_HTTP_TTL = 300; // seconds
    String[] DEFAULT_HTTP_MAX_PER_ROUTE = {};

    // application logging
    String PROJECT_LOG_FILENAME = "rvdapp"; //will produce rvdapp.log, rvdapp-1.log etc.
    int PROJECT_LOG_BACKLOG_COUNT = 3; // the number of rotated files besides the main log file
    int PROJECT_LOG_ROTATION_SIZE = 300000;
    // App Store
    String DEFAULT_APPSTORE_DOMAIN = "apps.restcomm.com";
    // the names of the parameters supplied by restcomm request when starting an application
    HashSet<String> builtinRestcommParameters = new HashSet<String>(Arrays.asList(new String[]
            {"CallSid","AccountSid","From","To","Body","CallStatus","ApiVersion","Direction","CallerName",
                    "CallTimestamp", "ForwardedFrom", "SmsSid", "SmsStatus", "InstanceId","ReferTarget","Transferor","Transferee"}));
    String RESTCOMM_HEADER_PREFIX = "SipHeader_"; // the prefix added to HTTP headers from Restcomm
    String RESTCOMM_HEADER_PREFIX_DIAL = "DialSipHeader_"; // another prefix
    // File upload
    String DEFAULT_MEDIA_ALLOWED_EXTENSIONS = "wav|mp4"; // only allow upload of media files whose name matches this pattern i.e. file extension ends in .wav or .mp4
    // Security/Authentication options related to Restcomm
    String ADMINISTRATOR_ROLE = "Administrator"; // restcomm role that means _administrator_ access

    String getWorkspaceBasePath();

    String getProjectBasePath(String projectName);

    SslMode getSslMode();

    Integer getExternalServiceTimeout();
    Integer getExternalServiceMaxConns();
    Integer getExternalServiceMaxConnsPerRoute();
    Integer getExternalServiceTTL();
    List<RvdMaxPerHost> getExternalServiceMaxPerRoute();
    Integer getDefaultHttpTimeout();
    Integer getDefaultHttpMaxConns();
    Integer getDefaultHttpMaxConnsPerRoute();
    Integer getDefaultHttpTTL();
    List<RvdMaxPerHost> getDefaultHttpMaxPerRoute();

    boolean getUseHostnameToResolveRelativeUrl();

    String getHostnameOverride();

    URI getRestcommBaseUri();

    String getApplicationsRelativeUrl();

    Integer getMaxMediaFileSize();

    Boolean getVideoSupport();

    RvdConfig getRawRvdConfig();

    String getBaseUrl();

    Boolean useAbsoluteApplicationUrl();

    String getContextPath();

    boolean isUssdSupport();

    String getWelcomeMessage();

    String getRvdInstanceId();
}
