package org.restcomm.connect.rvd.model.steps.dial;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.utils.RvdUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Orestis Tsakiridis - otsakir@gmail.com
 */
public class ClientDialNoun extends DialNoun {
    private String destination;
    private String beforeConnectModule;
    private String statusCallback;

    public String getDestination() {
        return destination;
    }
    public String getBeforeConnectModule() {
        return beforeConnectModule;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public RcmlNoun render(Interpreter interpreter) throws InterpreterException {
        RcmlClientNoun rcmlNoun = new RcmlClientNoun();

        if ( ! RvdUtils.isEmpty(getBeforeConnectModule()) ) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", getBeforeConnectModule());
            rcmlNoun.setUrl( interpreter.buildAction(pairs) );
        }

        rcmlNoun.setDestination( interpreter.populateVariables(getDestination()) );
        if  (! RvdUtils.isEmpty(statusCallback))
            rcmlNoun.statusCallback = statusCallback;

        return rcmlNoun;
    }
}
