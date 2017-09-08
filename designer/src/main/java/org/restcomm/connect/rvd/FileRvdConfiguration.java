package org.restcomm.connect.rvd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import org.restcomm.connect.rvd.commons.http.SslMode;
import org.restcomm.connect.rvd.configuration.CustomIntegerConverter;
import org.restcomm.connect.rvd.configuration.RestcommConfig;
import org.restcomm.connect.rvd.exceptions.RestcommConfigNotFound;
import org.restcomm.connect.rvd.exceptions.RestcommConfigurationException;
import org.restcomm.connect.rvd.exceptions.XmlParserException;
import org.restcomm.connect.rvd.http.utils.UriUtils;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.configuration.RvdConfig;
import org.restcomm.connect.rvd.utils.RvdUtils;

import com.thoughtworks.xstream.XStream;
import org.restcomm.connect.rvd.utils.XmlParser;

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
public class FileRvdConfiguration implements RvdConfiguration {
    static Logger logger = RvdLoggers.local;
    // these defaults are used when there are no values defined in the configuration files
    private Integer maxMediaFileSize; // Maximum size allowed for media file uploads (in bytes). If set to null no limit is enforced

    private String workspaceBasePath;
    private RvdConfig rvdConfig;  // the configuration settings from rvd.xml
    private RestcommConfig restcommConfig;

    private String contextRootPath; // e.g. a/path/ending/in/slash/
    private String contextPath; // e.g. /restcomm-rvd
    private URI restcommBaseUri;
    private Integer externalServiceTimeout;
    private Boolean videoSupport;
    private SslMode sslMode;
    private String hostnameOverride;
    private Boolean useHostnameToResolveRelativeUrl;
    private String baseUrl;
    private Boolean useAbsoluteApplicationUrl;
    private boolean ussdSupport;
    // whitelabeling configuration
    private String welcomeMessage;

    // package-private constructor to be used from RvdConfigurationBuilder
    FileRvdConfiguration() {
    }

    public FileRvdConfiguration(ServletContext servletContext) {
        this(servletContext.getContextPath(), servletContext.getRealPath("/"));
    }

    public FileRvdConfiguration(String contextPath, String contextRootPath) {
        this.contextRootPath = RvdUtils.addTrailingSlashIfMissing(contextRootPath);
        this.contextPath = contextPath;
        logger.info("context filesystem root path is " + this.contextRootPath);
        logger.info("context path is " + this.contextPath);
        load();
    }

    public FileRvdConfiguration(String workspaceBasePath, RvdConfig rvdConfig, RestcommConfig restcommConfig, String contextPath, String contextRootPath, URI restcommBaseUri) {
        this.workspaceBasePath = workspaceBasePath;
        this.rvdConfig = rvdConfig;
        this.restcommConfig = restcommConfig;
        this.contextPath = contextPath;
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
        // baseUrl
        if (! RvdUtils.isEmpty(rvdConfig.getBaseUrl()) )
            baseUrl = rvdConfig.getBaseUrl();
        // useAbsoluteApplicationUrl
        if (! RvdUtils.isEmpty(rvdConfig.useAbsoluteApplicationUrl()))
            useAbsoluteApplicationUrl = rvdConfig.useAbsoluteApplicationUrl();
        else
            useAbsoluteApplicationUrl = DEFAULT_USE_ABSOLUTE_APPLICATION_URL;
        // ussd support
        if ( RvdUtils.isEmpty(rvdConfig.getUssdSupport()) )
            ussdSupport = DEFAULT_USSD_SUPPORT;
        else {
            try {
                ussdSupport = Boolean.parseBoolean(rvdConfig.getUssdSupport());
            } catch ( Exception e) {
                ussdSupport = DEFAULT_USSD_SUPPORT;
                logger.warn(LoggingHelper.buildMessage(RvdConfiguration.class,"load",null,"Error parsing rvd.xml:ussd/enabled option. Falling back to default: " + ussdSupport),e);
            }
        }
        // load whitelabeling configuration
        loadWhitelabelConfig(contextRootPath + "WEB-INF/whitelabel.xml");
    }

    /**
     * Loads rvd.xml into an RvdConfig. Returns null if the file is not found
     * @param pathToXml
     * @return
     */
    public static RvdConfig loadRvdXmlConfig(String pathToXml) {
        try {
            FileInputStream input = new FileInputStream(pathToXml);
            XStream xstream = new XStream();
            xstream.alias("rvd", RvdConfig.class);
            xstream.omitField(RvdConfig.class, "corsWhitelist");
            xstream.omitField(RvdConfig.class, "ussd");
            xstream.registerConverter(new CustomIntegerConverter());
            RvdConfig rvdConfig = (RvdConfig) xstream.fromXML( input );
            // read some more configuration options that xstream fails to read in a clean way
            XmlParser xml = new XmlParser(pathToXml);
            rvdConfig.setAllowedCorsOrigins(xml.getElementList("/rvd/corsWhitelist/origin"));
            rvdConfig.setUssdSupport(xml.getElementContent("/rvd/ussdSupport"));
            return rvdConfig;
        } catch (FileNotFoundException e) {
            logger.warn(LoggingHelper.buildMessage(RvdConfiguration.class,"loadRvdXmlConfig",null,"RVD configuration file not found: " + pathToXml));
            return null;
        } catch (XmlParserException e) {
            logger.warn(LoggingHelper.buildMessage(RvdConfiguration.class,"loadRvdXmlConfig",null,"Error parsing RVD configuration file: " + pathToXml), e);
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

    private void loadWhitelabelConfig(String pathToXml) {
        welcomeMessage = DEFAULT_WELCOME_MESSAGE;
        try {
            XmlParser xml = new XmlParser(pathToXml);
            String value = xml.getElementContent("/whitelabel/welcomeMessage");
            if (value != null)
                welcomeMessage = value;
            logger.info("Loaded whitelabeling information: " + pathToXml);
        } catch (XmlParserException e) {
            if ( e.getCause() instanceof FileNotFoundException)
                logger.info("No whitelabeling file found (" + pathToXml + "). Hardcoded defaults will be used.");
            else
                logger.error(LoggingHelper.buildMessage(RvdConfiguration.class,"loadWhitelabelConfig",null,"Error parsing whitelabeling configuration file: " + pathToXml), e);
            return;
        }
    }

    @Override
    public String getWorkspaceBasePath() {
        return this.workspaceBasePath;
    }

    @Override
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

    @Override
    public SslMode getSslMode() {
        return sslMode;
    }

    @Override
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

    @Override
    public boolean getUseHostnameToResolveRelativeUrl() {
        return useHostnameToResolveRelativeUrl;
    }

    @Override
    public String getHostnameOverride() {
        return hostnameOverride;
    }

    // this is lazy loaded because HttpConnector enumeration (done in resolve()) fails otherwise
    @Override
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
    @Override
    public String getApplicationsRelativeUrl() {
        return "/restcomm-rvd/services/apps";
    }

    // package private setter to be used from RvdConfigurationBuilder only
    void setRestcommBaseUri(URI uri) {
        this.restcommBaseUri = uri;
    }

    @Override
    public Integer getMaxMediaFileSize() {
        return maxMediaFileSize;
    }

    @Override
    public Boolean getVideoSupport() {
        return RvdUtils.isTrue(videoSupport);
    }

    @Override
    public RvdConfig getRawRvdConfig() {
        return rvdConfig;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public Boolean useAbsoluteApplicationUrl() {
        return useAbsoluteApplicationUrl;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public boolean isUssdSupport() {
        return ussdSupport;
    }

    @Override
    public String getWelcomeMessage() {
        return welcomeMessage;
    }
}