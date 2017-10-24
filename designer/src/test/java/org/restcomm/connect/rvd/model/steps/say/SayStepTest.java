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
 *
 */

package org.restcomm.connect.rvd.model.steps.say;

import junit.framework.Assert;
import org.junit.Test;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.model.steps.StepTestBase;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class SayStepTest extends StepTestBase {

    /**
     * Check if all properties of a Say step get rendered
     */
    @Test
    public void defaultRendering() {
        Interpreter interpreter = buildInterpreter(appendMultivaluedMap(null,null,null));
        SayStep step = new SayStep("hello world", "man", "en", 1);
        RcmlSayStep rcmlStep = (RcmlSayStep)step.render(interpreter, null);
        Assert.assertEquals("hello world", rcmlStep.getPhrase());
        Assert.assertEquals("en", rcmlStep.getLanguage());
        Assert.assertEquals("man", rcmlStep.getVoice());
    }

    /**
     * Checks if a phrase of a Say step supports variables.
     */
    @Test
    public void phraseWithModuleVariable() {
        Interpreter interpreter = buildInterpreter(appendMultivaluedMap(null,null,null));
        interpreter.getVariables().put(RvdConfiguration.MODULE_PREFIX + "name", "alice");
        SayStep step = new SayStep("hello $name", null, null, null);
        RcmlSayStep rcmlStep = (RcmlSayStep)step.render(interpreter, null);
        Assert.assertEquals("hello alice", rcmlStep.getPhrase());
    }
}
