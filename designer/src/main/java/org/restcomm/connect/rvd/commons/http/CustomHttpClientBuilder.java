package org.restcomm.connect.rvd.commons.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.restcomm.connect.rvd.RvdConfiguration;

import javax.net.ssl.SSLContext;
import org.apache.http.HttpHost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.restcomm.connect.rvd.configuration.RvdMaxPerHost;

/**
 * Creates an HttpClient with the desired ssl behaviour according to
 * configuration
 *
 * @author orestis.tsakiridis@telestax.com (Orestis Tsakiridis)
 *
 */
public class CustomHttpClientBuilder {

    private RvdConfiguration configuration;

    public CustomHttpClientBuilder(RvdConfiguration configuration) {
        this.configuration = configuration;
    }

    // returns an apache http client
    public CloseableHttpClient buildExternalHttpClient() {
        return buildClient(configuration.getExternalServiceTimeout(),
                configuration.getExternalServiceMaxConns(),
                configuration.getExternalServiceMaxConnsPerRoute(),
                configuration.getExternalServiceTTL(),
                configuration.getExternalServiceMaxPerRoute());
    }

    public CloseableHttpClient buildHttpClient() {
        return buildClient(configuration.getDefaultHttpTimeout(),
                configuration.getDefaultHttpMaxConns(),
                configuration.getDefaultHttpMaxConnsPerRoute(),
                configuration.getDefaultHttpTTL(),
                configuration.getDefaultHttpMaxPerRoute());
    }

    private CloseableHttpClient buildClient(Integer timeout,
            Integer maxConns, Integer maxConnsPerRoute, Integer timeToLive,
            List<RvdMaxPerHost> routes) {
        HttpClientBuilder builder = HttpClients.custom();

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).setSocketTimeout(timeout);
        builder.setDefaultRequestConfig(configBuilder.build());

        builder.setMaxConnPerRoute(maxConnsPerRoute);
        builder.setMaxConnTotal(maxConns);
        builder.setConnectionTimeToLive(timeToLive, TimeUnit.MILLISECONDS);

        SSLConnectionSocketFactory sslsf = null;
        if (configuration.getSslMode() == SslMode.strict) {
            sslsf = buildStrictFactory();
        } else {
            sslsf = buildAllowallFactory();
        }
        if (sslsf != null) {
            builder.setSSLSocketFactory(sslsf);
        }
        if (routes != null && routes.size() > 0) {
            if (sslsf == null) {
                //strict mode with no system https properties
                //taken from apache buider code
                PublicSuffixMatcher publicSuffixMatcherCopy = PublicSuffixMatcherLoader.getDefault();
                DefaultHostnameVerifier hostnameVerifierCopy = new DefaultHostnameVerifier(publicSuffixMatcherCopy);
                sslsf = new SSLConnectionSocketFactory(
                        SSLContexts.createDefault(),
                        hostnameVerifierCopy);
            }
            Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)
                    .build();
            final PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager(
                    reg,
                    null,
                    null,
                    null,
                    timeToLive,
                    TimeUnit.MILLISECONDS);
            //ensure conn configuration is set again for new conn manager
            poolingmgr.setMaxTotal(maxConns);
            poolingmgr.setDefaultMaxPerRoute(maxConnsPerRoute);
            for (RvdMaxPerHost route : routes) {
                try {
                    URL url = new URL(route.getUrl());
                    HttpRoute r = new HttpRoute(new HttpHost(url.getHost(), url.getPort()));
                    poolingmgr.setMaxPerRoute(r, route.getMaxConnections());
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            builder.setConnectionManager(poolingmgr);
        }

        CloseableHttpClient httpclient = builder.build();
        return httpclient;

    }

    private SSLConnectionSocketFactory buildStrictFactory() {
        SSLConnectionSocketFactory sslsf = null;
        String[] protocols = getSSLPrototocolsFromSystemProperties();

        if (protocols != null) {
            // ssl properties
            SSLContext sslcontext = SSLContexts.createDefault();
            // Allow TLSv1 protocol only
            sslsf = new SSLConnectionSocketFactory(sslcontext, protocols, null, new DefaultHostnameVerifier());
        }
        return sslsf;
    }

    private SSLConnectionSocketFactory buildAllowallFactory() {
        String[] protocols = getSSLPrototocolsFromSystemProperties();

        // ssl properties
        SSLContext sslcontext;
        try {
            sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, protocols, null, new NoopHostnameVerifier());
        return sslsf;
    }

    private String[] getSSLPrototocolsFromSystemProperties() {
        String protocols = System.getProperty("jdk.tls.client.protocols");
        if (protocols == null) {
            protocols = System.getProperty("https.protocols");
        }

        if (protocols != null) {
            String[] protocolsArray = protocols.split(",");
            return protocolsArray;
        }
        return null;
    }

    // experimental support for Jersey http client
/*
    private static Client buildStrictJerseyClient() {
        return Client.create();
    }

    private static Client buildAllowallJerseyClient() {

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {}

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {}

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
             return null;
            }
        }};

        SSLContext ctx;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ClientConfig config = new DefaultClientConfig();
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        }, ctx));

        Client client = Client.create(config);
        return client;
    }
     */
}
