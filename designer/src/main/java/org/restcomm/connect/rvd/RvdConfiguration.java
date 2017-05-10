package org.restcomm.connect.rvd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import org.restcomm.connect.rvd.commons.http.SslMode;
import org.restcomm.connect.rvd.configuration.CustomIntegerConverter;
import org.restcomm.connect.rvd.configuration.RestcommConfig;
import org.restcomm.connect.rvd.exceptions.RestcommConfigNotFound;
import org.restcomm.connect.rvd.exceptions.RestcommConfigurationException;
import org.restcomm.connect.rvd.http.utils.UriUtils;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.configuration.RvdConfig;
import org.restcomm.connect.rvd.utils.RvdUtils;

import com.thoughtworks.xstream.XStream;

/**
 * Configuration settings for RVD. Contains both static hardcoded and loaded values.
 *
 * Besides hardcoded values, information form rvd.xml as well as proxied values from restcomm.xml
 * are contained. It also provides vary basic logic so that default values are returned too.
 * For example if 'videoSupport' configuration option is missing, it will return false (and not null).
 *
 * rvd.xml and restcomm.xml configuration options are applied in the following way:
 *
 *  restcomm.xml based will be loaded first if available. Any option defined in rvd.xml will override
 *  these values if the option is defined (i.e.the XML element is there even if empty).
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class RvdConfiguration {
    static Logger logger = RvdLoggers.local;

    public static final SslMode DEFAULT_SSL_MODE = SslMode.strict;
    public static final boolean DEFAULT_USE_HOSTNAME_TO_RESOLVE_RELATIVE_URL = true;

    private static final String WORKSPACE_DIRECTORY_NAME = "workspace";
    public static final String PROTO_DIRECTORY_PREFIX = "_proto";
    public static final String REST_SERVICES_PATH = "services"; // the "services" from the /restcomm-rvd/services/apps/... path
    public static final String USERS_DIRECTORY_NAME = "@users";

    public static final String WAVS_DIRECTORY_NAME = "wavs";
    private static final String RVD_PROJECT_VERSION = "1.10"; // version for rvd project syntax
    private static final String PACKAGING_VERSION = "1.0";
    private static final String RAS_APPLICATION_VERSION = "2"; // version of the RAS application specification
    public static final String STICKY_PREFIX = "sticky_"; // a  prefix for rvd sticky variable names
    public static final String MODULE_PREFIX = "module_"; // a  prefix for rvd module-scoped variable names
    public static final String CORE_VARIABLE_PREFIX = "core_"; // a prefix for rvd variables that come from Restcomm parameters
    public static final String PACKAGING_DIRECTORY_NAME = "packaging";
    // http client (ES)
    public static final int DEFAULT_ES_TIMEOUT = 5000; // milliseconds
    // application logging
    public static final String PROJECT_LOG_FILENAME = "rvdapp"; //will produce rvdapp.log, rvdapp-1.log etc.
    public static final int PROJECT_LOG_BACKLOG_COUNT = 3; // the number of rotated files besides the main log file
    public static final int PROJECT_LOG_ROTATION_SIZE = 300000;
    // App Store
    public static final String DEFAULT_APPSTORE_DOMAIN = "apps.restcomm.com";
    // the names of the parameters supplied by restcomm request when starting an application
    public static final HashSet<String> builtinRestcommParameters = new HashSet<String>(Arrays.asList(new String[]
            {"CallSid","AccountSid","From","To","Body","CallStatus","ApiVersion","Direction","CallerName",
                    "CallTimestamp", "ForwardedFrom", "SmsSid", "SmsStatus", "InstanceId","ReferTarget","Transferor","Transferee"}));
    public static final String RESTCOMM_HEADER_PREFIX = "SipHeader_"; // the prefix added to HTTP headers from Restcomm
    public static final String RESTCOMM_HEADER_PREFIX_DIAL = "DialSipHeader_"; // another prefix
    // File upload
    public static final String DEFAULT_MEDIA_ALLOWED_EXTENSIONS = "wav|mp4"; // only allow upload of media files whose name matches this pattern i.e. file extension ends in .wav or .mp4
    private Integer maxMediaFileSize; // Maximum size allowed for media file uploads (in bytes). If set to null no limit is enforced

    private String workspaceBasePath;
    private RvdConfig rvdConfig;  // the configuration settings from rvd.xml
    private RestcommConfig restcommConfig;

    private String contextRootPath;
    private URI restcommBaseUri;
    private Integer externalServiceTimeout;
    private Boolean videoSupport;
    private SslMode sslMode;
    private String hostnameOverride;
    private Boolean useHostnameToResolveRelativeUrl;

    // package-private constructor to be used from RvdConfigurationBuilder
    RvdConfiguration() {
    }

    public RvdConfiguration(ServletContext servletContext) {
        contextRootPath = servletContext.getRealPath("/");
        logger.info("context root path is " + contextRootPath);
        load();
    }

    public RvdConfiguration(String contextRootPath) {
        this.contextRootPath = contextRootPath;
        logger.info("context root path is " + contextRootPath);
        load();
    }

    public RvdConfiguration(String workspaceBasePath, RvdConfig rvdConfig, RestcommConfig restcommConfig, String contextRootPath, URI restcommBaseUri) {
        this.workspaceBasePath = workspaceBasePath;
        this.rvdConfig = rvdConfig;
        this.restcommConfig = restcommConfig;
        this.contextRootPath = contextRootPath;
        this.restcommBaseUri = restcommBaseUri;
    }

    private void load() {
        // load configuration from rvd.xml file
        rvdConfig = loadRvdXmlConfig(contextRootPath + "WEB-INF/rvd.xml");
        // workspaceBasePath option
        String workspaceBasePath = contextRootPath + WORKSPACE_DIRECTORY_NAME;
        if (rvdConfig.getWorkspaceLocation() != null  &&  !"".equals(rvdConfig.getWorkspaceLocation()) ) {
            if ( rvdConfig.getWorkspaceLocation().startsWith("/") )
                workspaceBasePath = rvdConfig.getWorkspaceLocation(); // this is an absolute path
            else
                workspaceBasePath = contextRootPath + rvdConfig.getWorkspaceLocation(); // this is a relative path hooked under RVD context
        }
        this.workspaceBasePath = workspaceBasePath;
        RvdLoggers.global.info("workspace located under " + workspaceBasePath);
        // try load configuration from restcomm.war/.../restcomm.xml file
        try {
            restcommConfig = loadRestcommXmlConfig(contextRootPath + "../restcomm.war/WEB-INF/conf/restcomm.xml");
        } catch (RestcommConfigNotFound e) {
            // fallback to local configuration
            try {
                restcommConfig = loadRestcommXmlConfig(contextRootPath + "WEB-INF/restcomm.xml");
            } catch (RestcommConfigNotFound restcommConfigNotFound) {
                restcommConfig = null;
                logger.log(Level.WARN, "could not load restcomm configuration");
            }
        }
        // video support
        this.videoSupport = rvdConfig.getVideoSupport();
        // maxMediaFileSize
        maxMediaFileSize = rvdConfig.getMaxMediaFileSize();
        // sslMode
        if (restcommConfig != null)
            sslMode = restcommConfig.getSslMode();
        if (rvdConfig.getSslMode() != null)
            sslMode = SslMode.valueOf(rvdConfig.getSslMode());
        if (sslMode == null)
            sslMode = DEFAULT_SSL_MODE;
        // hostnameOverride (hostname in restcomm.xml)
        if (restcommConfig != null)
            hostnameOverride = restcommConfig.getHostname();
        if (rvdConfig.getHostnameOverride() != null)
            hostnameOverride = rvdConfig.getHostnameOverride();
        // useHostnameToResolveRelativeUrl
        if (restcommConfig != null)
            useHostnameToResolveRelativeUrl = restcommConfig.getUseHostnameToResolveRelativeUrl();
        if (rvdConfig.getUseHostnameToResolveRelativeUrl() != null)
            useHostnameToResolveRelativeUrl = rvdConfig.getUseHostnameToResolveRelativeUrl();
        if (useHostnameToResolveRelativeUrl == null)
            useHostnameToResolveRelativeUrl = DEFAULT_USE_HOSTNAME_TO_RESOLVE_RELATIVE_URL;

    }

    /**
     * Loads rvd.xml into an RvdConfig. Returns null if the file is not found
     * @param pathToXml
     * @return
     */
    private RvdConfig loadRvdXmlConfig(String pathToXml) {
        try {
            FileInputStream input = new FileInputStream(pathToXml);
            XStream xstream = new XStream();
            xstream.alias("rvd", RvdConfig.class);
            xstream.registerConverter(new CustomIntegerConverter());
            rvdConfig = (RvdConfig) xstream.fromXML( input );
            return rvdConfig;
        } catch (FileNotFoundException e) {
            logger.warn(LoggingHelper.buildMessage(getClass(),"loadRvdXmlConfig",null,"RVD configuration file not found: " + pathToXml));
            return null;
        }
    }

    /**
     * Load configuration options from restcomm.xml that are needed by RVD. Return null in case of failure.
     *
     * @param pathToXml
     * @return a valid RestcommConfig object or null
     */
    private RestcommConfig loadRestcommXmlConfig(String pathToXml) throws RestcommConfigNotFound {
        try {
            RestcommConfig restcommConfig = new RestcommConfig(pathToXml);
            return restcommConfig;
        } catch (RestcommConfigNotFound e) {
            throw e;
        } catch (RestcommConfigurationException e) {
            logger.log(Level.ERROR, e.getMessage(), e);
            return null;
        }
    }

    public String getWorkspaceBasePath() {
        return this.workspaceBasePath;
    }

    public String getProjectBasePath(String projectName) {
        return this.workspaceBasePath + File.separator + projectName;
    }

    public static String getRvdProjectVersion() {
        return RVD_PROJECT_VERSION;
    }

    public static String getPackagingVersion() {
        return PACKAGING_VERSION;
    }

    public static String getRasApplicationVersion() {
        return RAS_APPLICATION_VERSION;
    }

    public static Set<String> getRestcommParameterNames() {
        return builtinRestcommParameters;
    }

    public SslMode getSslMode() {
        return sslMode;
    }

    public Integer getExternalServiceTimeout() {
        if (externalServiceTimeout != null)
            return externalServiceTimeout;
        if (rvdConfig != null && rvdConfig.getExternalServiceTimeout() != null && rvdConfig.getExternalServiceTimeout().trim().length() > 0) {
            try {
                this.externalServiceTimeout = Integer.parseInt(rvdConfig.getExternalServiceTimeout());
            } catch (NumberFormatException e) {
                logger.warn(LoggingHelper.buildMessage(getClass(),"getExternalServiceTimeout",null,"Cannot parse RVD ES timeout configuration setting. Will use default: " + DEFAULT_ES_TIMEOUT + (e.getMessage() != null ? " - " + e.getMessage() : "")));
                this.externalServiceTimeout = DEFAULT_ES_TIMEOUT;
            }
        } else {
            this.externalServiceTimeout = DEFAULT_ES_TIMEOUT;
        }
        return this.externalServiceTimeout;
    }

    public boolean getUseHostnameToResolveRelativeUrl() {
        return useHostnameToResolveRelativeUrl;
    }

    public String getHostnameOverride() {
        return hostnameOverride;
    }

    // this is lazy loaded because HttpConnector enumeration (done in resolve()) fails otherwise
    public URI getRestcommBaseUri() {
        if (this.restcommBaseUri == null) {
            // check rvd.xml override first
            String rawUrl = rvdConfig.getRestcommBaseUrl();
            if ( ! RvdUtils.isEmpty(rawUrl) ) {
                try {
                    URI uri = new URI(rawUrl);
                    if ( ! RvdUtils.isEmpty(uri.getScheme()) && !RvdUtils.isEmpty(uri.getHost()) )
                        this.restcommBaseUri = uri;
                } catch (URISyntaxException e) { /* do nothing */}
            }
            // if no override value in rvd.xml use the automatic way
            if (this.restcommBaseUri == null) {
                UriUtils uriUtils = new UriUtils(this);
                try {
                    URI uri = new URI("/");
                    this.restcommBaseUri = uriUtils.resolve(uri);
                } catch (URISyntaxException e) { /* we should never reach here */
                    throw new IllegalStateException();
                }
            }
            logger.info("using restcomm server at " + this.restcommBaseUri.toString());
        }
        return restcommBaseUri;
    }

    /*
     * Returns a relative url to the base of the application service. Controllers are located under it.
     * Currently hardcoded to /restcomm-rvd/services/apps/
     *
     * @return relative url to the application service
     */
    public String getApplicationsRelativeUrl() {
        return "/restcomm-rvd/services/apps";
    }

    // package private setter to be used from RvdConfigurationBuilder only
    void setRestcommBaseUri(URI uri) {
        this.restcommBaseUri = uri;
    }

    public Integer getMaxMediaFileSize() {
        return maxMediaFileSize;
    }

    public Boolean getVideoSupport() {
        return RvdUtils.isTrue(videoSupport);
    }
}