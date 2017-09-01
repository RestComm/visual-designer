package org.restcomm.connect.rvd.interpreter.steps;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpretable;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.rcml.Rcml;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlNumberNoun;
import org.restcomm.connect.rvd.model.steps.dial.NumberDialNoun;
import org.restcomm.connect.rvd.utils.RvdUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class InterpretedNumberDialNoun extends NumberDialNoun implements Interpretable {

    @Override
    public Rcml render(Interpreter interpreter) throws InterpreterException {
        RcmlNumberNoun rcmlNoun = new RcmlNumberNoun();

        if ( ! RvdUtils.isEmpty(getBeforeConnectModule()) ) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", getBeforeConnectModule());
            rcmlNoun.setUrl( interpreter.buildAction(pairs) );
        }

        rcmlNoun.setSendDigits( getSendDigits() );
        rcmlNoun.setDestination( interpreter.populateVariables( getDestination() ));
        if (!RvdUtils.isEmpty(getStatusCallback()))
            rcmlNoun.setStatusCallback(getStatusCallback());
        else
        if (!RvdUtils.isEmpty(getBeforeConnectModule())) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", getBeforeConnectModule());
            rcmlNoun.setStatusCallback(interpreter.buildAction(pairs));
        }

        return rcmlNoun;
    }
}
