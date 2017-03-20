package org.restcomm.connect.rvd.model.steps.dial;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.utils.RvdUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Orestis Tsakiridis - otsakir@gmail.com
 */
public class SipuriDialNoun extends DialNoun {
    private String destination;
    private String statusCallback;
    private String statusCallbackModule;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public RcmlNoun render(Interpreter interpreter) throws InterpreterException {
        RcmlSipuriNoun rcmlNoun = new RcmlSipuriNoun();
        rcmlNoun.setDestination( interpreter.populateVariables(getDestination() ));
        if (!RvdUtils.isEmpty(statusCallback))
            rcmlNoun.statusCallback = statusCallback;
        else
        if (!RvdUtils.isEmpty(statusCallbackModule)) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", statusCallbackModule);
            rcmlNoun.statusCallback = interpreter.buildAction(pairs);
        }
        return rcmlNoun;
    }
}
