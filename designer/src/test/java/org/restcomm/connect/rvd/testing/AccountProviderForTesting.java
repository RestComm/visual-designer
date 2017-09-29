package org.restcomm.connect.rvd.testing;


import org.restcomm.connect.rvd.identity.DefaultAccountProvider;
import org.restcomm.connect.rvd.restcomm.RestcommAccountInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class AccountProviderForTesting extends DefaultAccountProvider {

    public AccountProviderForTesting() {
        super((String) null, null);
    }

    Map<String,RestcommAccountInfo> accounts = new HashMap<String, RestcommAccountInfo>();

    @Override
    public RestcommAccountInfo getAccount(String accountName, String authorizationHeader) {
        return accounts.get(accountName);
    }

    public void addAccountInfo(String sid, RestcommAccountInfo accountInfo) {
        accounts.put(sid, accountInfo);
    }
}
