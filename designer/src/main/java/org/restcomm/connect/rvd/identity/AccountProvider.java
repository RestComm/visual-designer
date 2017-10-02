package org.restcomm.connect.rvd.identity;

import org.restcomm.connect.rvd.restcomm.RestcommAccountInfo;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface AccountProvider {
    RestcommAccountInfo getAccount(String accountName, String authorizationHeader);

    RestcommAccountInfo getActiveAccount(String accountName, String authorizationHeader);

    RestcommAccountInfo getActiveAccount(BasicAuthCredentials creds);
}
