package org.restcomm.connect.rvd.configuration;

import org.apache.log4j.Logger;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.http.utils.UriUtils;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.utils.RvdUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Resolves restcomm base url using various methods depending on configuration, current request
 * or other factors like JBoss connector information etc.
 *
 * If 'dynamicRestcommResolving' is enabled, it will use current request to guess restcomm location assuming
 * that restcomm runs under the same domain as RVD (which received the request).
 *
 * Otherwise it will first return restcommBaseUrl value from rvd.xml and if this is not set it will fallback
 * to JBoss connector information based on UriUtils. In all cases (when dynamic resolving is disabled)
 * it will cache the returned value for future reference.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class RestcommLocationResolver {
    static Logger logger = RvdLoggers.local;

    RvdConfiguration config;
    URI baseUrlCache = null;
    boolean cached = false;

    public RestcommLocationResolver(RvdConfiguration config) {
        this.config = config;
    }

    public URI resolveRestcommBaseUrl(HttpServletRequest request) {
        URI resultUri = null;
        if (config.getDynamicRestcommResolving()) {
            String origin = RvdUtils.getOriginFromRequest(request);
            try {
                resultUri = new URI(origin);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Couldn't parse '" + origin + "'", e);
            }

        } else {
            if (cached) {
                resultUri = baseUrlCache;
            } else {
                cached = true;
                // check rvd.xml first
                String rawUrl = config.getRestcommBaseUri();
                if (!RvdUtils.isEmpty(rawUrl)) {
                    try {
                        URI uri = new URI(rawUrl);
                        if (!RvdUtils.isEmpty(uri.getScheme()) && !RvdUtils.isEmpty(uri.getHost())) {
                            resultUri = uri;
                        } else {
                            logger.error("Invalid restcommBaseUrl in rvd.xml: " + rawUrl + ". Will use automatic value.");
                        }
                    } catch (URISyntaxException e) {
                        throw new RuntimeException("Couldn't parse '" + rawUrl + "'", e);
                    }
                }
                // if no value in rvd.xml (or an exception thrown) use the automatic way
                if (resultUri == null) {
                    UriUtils uriUtils = new UriUtils(config);
                    try {
                        URI uri = new URI("/");
                        resultUri = uriUtils.resolve(uri);
                    } catch (URISyntaxException e) {
                        /* we should never reach here */
                        throw new RuntimeException(e);
                    }
                }
                baseUrlCache = resultUri;
                logger.info("using fixed location for restcomm server - " + resultUri.toString());
            }
        }
        return resultUri;
    }
}
