/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.restcomm.connect.rvd.model.steps.ussdcollect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.client.Step;
import org.restcomm.connect.rvd.model.steps.ussdsay.UssdSayStep;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class UssdCollectStep extends Step {

    public static class Mapping {
        String digits;
        String next;
    }
    public final class Menu {
        private List<Mapping> mappings;
    }
    public final class Collectdigits {
        private String next;
        private String collectVariable;
        private String scope;
    }

    String gatherType;
    String text;
    private Menu menu;
    private Collectdigits collectdigits;

    List<UssdSayStep> messages;

    public UssdCollectStep() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public UssdCollectRcml render(Interpreter interpreter) throws InterpreterException {
        // TODO Auto-generated method stub
        UssdCollectRcml rcml = new UssdCollectRcml();
        String newtarget = interpreter.getTarget().getNodename() + "." + getName() + ".handle";
        Map<String, String> pairs = new HashMap<String, String>();
        pairs.put("target", newtarget);

        rcml.action = interpreter.buildAction(pairs);
        for ( UssdSayStep message : messages ) {
            rcml.messages.add(message.render(interpreter));
        }

        return rcml;
    }

    @Override
    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        LoggingContext logging = interpreter.getRvdContext().logging;
        if (RvdLoggers.local.isLoggable(Level.INFO))
            RvdLoggers.local.log(Level.INFO, logging.getPrefix() + "handling UssdCollect action");

        if ("menu".equals(gatherType)) {

            boolean handled = false;
            for (Mapping mapping : menu.mappings) {
                // use a string for USSD collect. Alpha is supported too
                String digits = interpreter.getRequestParams().getFirst("Digits");

                if (RvdLoggers.local.isLoggable(Level.FINER))
                    RvdLoggers.local.log(Level.FINER, "{0} checking digits {1} - {2}", new Object[] {logging.getPrefix(), mapping.digits, digits });

                if (mapping.digits != null && mapping.digits.equals(digits)) {
                    // seems we found out menu selection
                    if (RvdLoggers.local.isLoggable(Level.FINER))
                        RvdLoggers.local.log(Level.FINER, "{0} seems we found our menu selection", new Object[] {logging.getPrefix(), digits} );
                    interpreter.interpret(mapping.next,null,null, originTarget);
                    handled = true;
                }
            }
            if (!handled) {
                interpreter.interpret(interpreter.getTarget().getNodename() + "." + interpreter.getTarget().getStepname(),null,null, originTarget);
            }
        }
        if ("collectdigits".equals(gatherType)) {
            String variableName = collectdigits.collectVariable;
            String variableValue = interpreter.getRequestParams().getFirst("Digits");
            if ( variableValue == null ) {
                if (RvdLoggers.local.isLoggable(Level.WARNING))
                    RvdLoggers.local.log(Level.WARNING, "{0} 'Digits' parameter was null. Is this a valid restcomm request?", logging.getPrefix());
                variableValue = "";
            }

            // is this an application-scoped variable ?
            if ( "application".equals(collectdigits.scope) ) {
                // if it is, create a sticky_* variable named after it
                interpreter.getVariables().put(RvdConfiguration.STICKY_PREFIX + variableName, variableValue);
            }
            // in any case initialize the module-scoped variable
            interpreter.getVariables().put(variableName, variableValue);

            interpreter.interpret(collectdigits.next,null,null, originTarget);
        }
    }
}
