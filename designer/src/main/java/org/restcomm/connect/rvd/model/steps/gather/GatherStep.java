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

package org.restcomm.connect.rvd.model.steps.gather;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.Target;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.client.Step;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class GatherStep extends Step {

    enum InputType {
        DTMF, SPEECH, DTMF_SPEECH;

        public static InputType parse(String value) {
            if ("dtmf".equalsIgnoreCase(value)) {
                return DTMF;
            }
            if ("speech".equalsIgnoreCase(value)) {
                return SPEECH;
            }
            return DTMF_SPEECH;
        }
    }

    private String action;
    private String method;
    private Integer timeout;
    private String finishOnKey;
    private Integer numDigits;
    private List<Step> steps;
    private Validation validation;
    private Step invalidMessage;
    private Menu menu;
    private Collectdigits collectdigits;
    private Collectdigits collectspeech;
    private String gatherType;
    private String inputType;
    private String hints;
    private String language;

    public final class Menu {
        private List<DtmfMapping> mappings;
        private List<SpeechMapping> speechMapping;
    }

    public final class Collectdigits {
        private String next;
        private String collectVariable;
        private String scope;
    }

    interface Mapping {
        String getKey();
        String getNext();
    }

    public class DtmfMapping implements Mapping {
        private String digits;
        private String next;

        @Override
        public String getKey() {
            return digits;
        }

        @Override
        public String getNext() {
            return next;
        }
    }

    public class SpeechMapping implements Mapping {
        private String key;
        private String next;

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getNext() {
            return next;
        }
    }

    public final class Validation {
        private String userPattern;
        private String regexPattern;
    }

    public RcmlGatherStep render(Interpreter interpreter) throws InterpreterException {

        RcmlGatherStep rcmlStep = new RcmlGatherStep();
        String newtarget = interpreter.getTarget().getNodename() + "." + getName() + ".handle";
        Map<String, String> pairs = new HashMap<String, String>();
        pairs.put("target", newtarget);
        String action = interpreter.buildAction(pairs);

        rcmlStep.setAction(action);
        rcmlStep.setTimeout(timeout);
        if (finishOnKey != null && !"".equals(finishOnKey))
            rcmlStep.setFinishOnKey(finishOnKey);
        rcmlStep.setMethod(method);
        rcmlStep.setNumDigits(numDigits);
        rcmlStep.setHints(hints);
        rcmlStep.setLanguage(language);
        rcmlStep.setInput(inputType);
        rcmlStep.setPartialResultCallback(action);
        rcmlStep.setPartialResultCallbackMethod(method);

        for (Step nestedStep : steps)
            rcmlStep.getSteps().add(nestedStep.render(interpreter));

        return rcmlStep;
    }

    private boolean handleMapping(Interpreter interpreter, Target originTarget, String key, List<? extends Mapping> mappings, boolean isPattern) throws StorageException, InterpreterException {
        LoggingContext logging = interpreter.getRvdContext().logging;
        if (mappings != null) {
            for (Mapping mapping : mappings) {

                if (RvdLoggers.local.isTraceEnabled())
                    RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(), "handleAction", "{0} checking key: {1} - {2}", new Object[]{logging.getPrefix(), mapping.getKey(), key}));

                if (key != null) {
                    //mapping.key is not null always
                    if (!isPattern && mapping.getKey().equals(key) || isPattern && Pattern.matches(mapping.getKey(), key)) {
                        // seems we found out menu selection
                        if (RvdLoggers.local.isTraceEnabled())
                            RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(), "handleAction", logging.getPrefix(), " seems we found our menu selection: " + key));
                        interpreter.interpret(mapping.getNext(), null, null, originTarget);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getPattern(final Interpreter interpreter) {
        if (validation == null) {
            return null;
        }
        String effectivePattern = null;
        if (validation.userPattern != null) {
            effectivePattern = "^[" + interpreter.populateVariables(validation.userPattern) + "]$";
        } else if (validation.regexPattern != null) {
            effectivePattern = interpreter.populateVariables(validation.regexPattern);
        } else {
            RvdLoggers.local.log(Level.WARN, LoggingHelper.buildMessage(getClass(), "handleAction", interpreter.getRvdContext().logging.getPrefix(), " Invalid validation information in gather. Validation object exists while other patterns are null"));
        }
        return effectivePattern;
    }

    private void putVariable(final Interpreter interpreter, final Collectdigits collect, String varName, String varValue) {
        // is this an application-scoped variable ?
        if ("application".equals(collect.scope)) {
            interpreter.putStickyVariable(varName, varValue);
        } else if ("module".equals(collect.scope)) {
            interpreter.putModuleVariable(varName, varValue);
        }

        // in any case initialize the module-scoped variable
        interpreter.getVariables().put(varName, varValue);
    }

    private boolean isMatchesPattern(String pattern, String value, final LoggingContext logging) {
        if (RvdLoggers.local.isTraceEnabled())
            RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(), "handleAction", "{0} validating '{1}' against '{}'", new Object[]{logging.getPrefix(), value, pattern}));
        return !StringUtils.isEmpty(value) && value.matches(pattern);
    }

    private boolean handleDigitsCollect(final Interpreter interpreter, Target originTarget, String digitsString, String effectivePattern) throws StorageException, InterpreterException {
        LoggingContext logging = interpreter.getRvdContext().logging;
        String variableDigitsName = collectdigits.collectVariable;
        String variableDigitsValue = digitsString;
        if (variableDigitsValue == null) {
            RvdLoggers.local.log(Level.WARN, LoggingHelper.buildMessage(getClass(), "handleAction", logging.getPrefix(), "'Digits' parameter was null. Is this a valid restcomm request?"));
            variableDigitsValue = "";
        }

        // validation for digits
        if (effectivePattern == null || isMatchesPattern(effectivePattern, variableDigitsValue, logging)) {
            putVariable(interpreter, collectdigits, variableDigitsName, variableDigitsValue);
            interpreter.interpret(collectdigits.next, null, null, originTarget);
            return true;
        } else {
            if (RvdLoggers.local.isTraceEnabled())
                RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(), "handleAction", logging.getPrefix(), "{0} Invalid input for gather/collectdigits. Will say the validation message and rerun the gather"));
            return false;
        }
    }

    private boolean handleSpeechCollect(final Interpreter interpreter, Target originTarget, String speechString, String effectivePattern) throws StorageException, InterpreterException {
        LoggingContext logging = interpreter.getRvdContext().logging;
        String variableSpeechName = collectspeech.collectVariable;
        String currentSpeechValue = interpreter.getVariables().get(collectspeech.scope + "_" + variableSpeechName);
        String variableSpeechValue = currentSpeechValue != null ? currentSpeechValue : "";
        if (speechString == null) {
            RvdLoggers.local.log(Level.WARN, LoggingHelper.buildMessage(getClass(), "handleAction", logging.getPrefix(), "'Speech' parameter was null. Is this a valid restcomm request?"));
        } else {
            variableSpeechValue += " " + speechString;
        }

        // validation for speech
        if (effectivePattern == null || isMatchesPattern(effectivePattern, variableSpeechValue.trim(), logging)) {
            putVariable(interpreter, collectspeech, variableSpeechName, variableSpeechValue.trim());
            interpreter.interpret(collectspeech.next, null, null, originTarget);
            return true;
        } else {
            if (RvdLoggers.local.isTraceEnabled())
                RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(), "handleAction", logging.getPrefix(), "{0} Invalid input for gather/collectdigits. Will say the validation message and rerun the gather"));
            return false;
        }
    }

    public void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException {
        LoggingContext logging = interpreter.getRvdContext().logging;
        if (RvdLoggers.local.isEnabledFor(Level.INFO))
            RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(), "handleAction", logging.getPrefix(), "handling gather action"));

        String digitsString = interpreter.getRequestParams().getFirst("Digits");
        String speechString = interpreter.getRequestParams().getFirst("Speech");
        if (digitsString != null)
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "Digits", digitsString);
        if (speechString != null)
            interpreter.getVariables().put(RvdConfiguration.CORE_VARIABLE_PREFIX + "Speech", speechString);

        boolean isValid = true;
        InputType inputTypeE = InputType.parse(inputType);
        if ("menu".equals(gatherType)) {
            switch (inputTypeE) {
                case DTMF:
                    isValid = handleMapping(interpreter, originTarget, digitsString, menu.mappings, false);
                    break;
                case SPEECH:
                    isValid = handleMapping(interpreter, originTarget, speechString, menu.speechMapping, true);
                    break;
                case DTMF_SPEECH:
                    if (!StringUtils.isEmpty(digitsString)) {
                        isValid = handleMapping(interpreter, originTarget, digitsString, menu.mappings, false);
                    } else if (!StringUtils.isEmpty(speechString)) {
                        isValid = handleMapping(interpreter, originTarget, speechString, menu.speechMapping, true);
                    } else {
                        isValid = false;
                    }
                    break;
            }
        } else if ("collectdigits".equals(gatherType)) {
            //validation pattern
            String effectivePattern = getPattern(interpreter);
            switch (inputTypeE) {
                case DTMF:
                    isValid = handleDigitsCollect(interpreter, originTarget, digitsString, effectivePattern);
                    break;
                case SPEECH:
                    isValid = handleSpeechCollect(interpreter, originTarget, speechString, effectivePattern);
                    break;
                case DTMF_SPEECH:
                    if (!StringUtils.isEmpty(digitsString)) {
                        isValid = handleDigitsCollect(interpreter, originTarget, digitsString, effectivePattern);
                    } else if (!StringUtils.isEmpty(speechString)) {
                        isValid = handleSpeechCollect(interpreter, originTarget, speechString, effectivePattern);
                    } else {
                        isValid = false;
                    }
                    break;
            }
        }

        if (!isValid) { // this should always be true
            interpreter.interpret(interpreter.getTarget().getNodename() + "." + interpreter.getTarget().getStepname(), null, (invalidMessage != null) ? invalidMessage : null, originTarget);
        }
    }
}
