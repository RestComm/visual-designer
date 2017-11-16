package org.restcomm.connect.rvd.identity;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.restcomm.connect.rvd.restcomm.RestcommAccountInfo;
import org.restcomm.connect.rvd.utils.RvdUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Provides accounts by querying Restcomm. It follows the request lifecycle since it depends on
 * restcommUrl that may change per request.
 *
 * @author Orestis Tsakiridis
 */
public class DefaultAccountProvider implements AccountProvider {

    URI restcommUrl = null;
    CloseableHttpClient client;

    /**
     * This constructor directly initializes restcommUrl without going through RvdConfiguration
     * and UriUtils. Make sure restcommUrl parameter is properly set.
     *
     * @param restcommUrl
     * @param client
     */
    public DefaultAccountProvider(URI restcommUrl, CloseableHttpClient client) {
        if (restcommUrl == null)
            throw new IllegalStateException("restcommUrl cannot be null");
        this.restcommUrl = restcommUrl;
        this.client = client;
    }

//    private String sanitizeRestcommUrl(String restcommUrl) {
//        restcommUrl = restcommUrl.trim();
//        if (restcommUrl.endsWith("/"))
//            return restcommUrl.substring(0,restcommUrl.length()-1);
//        return restcommUrl;
//    }

    private URI buildAccountQueryUrl(String usernameOrSid) {
        try {
            // TODO url-encode the username
            URI uri = new URIBuilder(this.restcommUrl.toString()).setPath("/restcomm/2012-04-24/Accounts.json/" + usernameOrSid).build();
            return uri;
        } catch (URISyntaxException e) {
            // something really wrong has happened
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves account 'accountName' from restcomm using creds as credentials.
     * If the authentication fails or the account is not found it returns null.
     *
     * TODO we need to treat differently missing accounts and failed authentications.
     *
     */
    @Override
    public RestcommAccountInfo getAccount(String accountName, String authorizationHeader) {
        HttpGet GETRequest = new HttpGet(buildAccountQueryUrl(accountName));
        GETRequest.addHeader("Authorization", authorizationHeader);
        try {
            CloseableHttpResponse response = client.execute(GETRequest);
            if (response.getStatusLine().getStatusCode() == 200 ) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String accountJson = EntityUtils.toString(entity);
                    Gson gson = new Gson();
                    RestcommAccountInfo accountResponse = gson.fromJson(accountJson, RestcommAccountInfo.class);
                    return accountResponse;
                }
            } else
                return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Something went wrong while retrieving account
        return null;
    }

    @Override
    public RestcommAccountInfo getActiveAccount(String accountName, String authorizationHeader) {
        RestcommAccountInfo response = getAccount(accountName, authorizationHeader);
        // if the account is not active, we need to set success status to false
        if ( !"active".equals(response.getStatus()) ) {
            return null;
        } else
            return response;
    }

    @Override
    public RestcommAccountInfo getActiveAccount(BasicAuthCredentials creds) {
        String header = "Basic " + RvdUtils.buildHttpAuthorizationToken(creds.getUsername(),creds.getPassword());
        return getActiveAccount(creds.getUsername(), header);
    }
}

