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

package org.restcomm.connect.rvd.model;

import java.lang.reflect.Type;
import org.apache.log4j.Logger;

import org.restcomm.connect.rvd.interpreter.steps.InterpretedControlStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedDialStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedEmailStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedExternalServiceStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedFaxStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedGatherStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedHungupStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedLogStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedPauseStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedPlayStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedRecordStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedRedirectStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedRejectStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedSayStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedSmsStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedUssdCollectStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedUssdLanguageStep;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedUssdSayStep;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.project.BaseStep;
import org.restcomm.connect.rvd.model.steps.dial.BaseDialNoun;
import org.restcomm.connect.rvd.storage.json.DialNounJsonDeserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class StepJsonDeserializer implements JsonDeserializer<BaseStep> {
    static final Logger logger = RvdLoggers.local;

    @Override
    public BaseStep deserialize(JsonElement rootElement, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {

        JsonObject step_object = rootElement.getAsJsonObject();
        String kind = step_object.get("kind").getAsString();

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(BaseStep.class, new StepJsonDeserializer())
            .registerTypeAdapter(BaseDialNoun.class, new DialNounJsonDeserializer())
            .create();

        BaseStep step;
        if ("say".equals(kind))
            step = gson.fromJson(step_object, InterpretedSayStep.class);
        else if ("gather".equals(kind))
            step = gson.fromJson(step_object, InterpretedGatherStep.class);
        else if ("dial".equals(kind))
            step = gson.fromJson(step_object, InterpretedDialStep.class);
        else if ("hungup".equals(kind))
            step = gson.fromJson(step_object, InterpretedHungupStep.class);
        else if ("play".equals(kind))
            step = gson.fromJson(step_object, InterpretedPlayStep.class);
        else if ("control".equals(kind))
            step = gson.fromJson(step_object, InterpretedControlStep.class);
        else if ("externalService".equals(kind))
            step = gson.fromJson(step_object, InterpretedExternalServiceStep.class);
        else if ("log".equals(kind))
            step = gson.fromJson(step_object, InterpretedLogStep.class);
        else if ("redirect".equals(kind))
            step = gson.fromJson(step_object, InterpretedRedirectStep.class);
        else if ("reject".equals(kind))
            step = gson.fromJson(step_object, InterpretedRejectStep.class);
        else if ("pause".equals(kind))
            step = gson.fromJson(step_object, InterpretedPauseStep.class);
        else if ("sms".equals(kind))
            step = gson.fromJson(step_object, InterpretedSmsStep.class);
        else if ("email".equals(kind))
            step = gson.fromJson(step_object, InterpretedEmailStep.class);
        else if ("record".equals(kind))
            step = gson.fromJson(step_object, InterpretedRecordStep.class);
        else if ("fax".equals(kind))
            step = gson.fromJson(step_object, InterpretedFaxStep.class);
        else if ("ussdSay".equals(kind))
            step = gson.fromJson(step_object, InterpretedUssdSayStep.class);
        else if ("ussdCollect".equals(kind))
            step = gson.fromJson(step_object, InterpretedUssdCollectStep.class);
        else if ("ussdLanguage".equals(kind))
            step = gson.fromJson(step_object, InterpretedUssdLanguageStep.class);
        else {
            step = null;
            logger.error("Cannot deserialize step. Unknown step found."); // TODO remove me and return a nice value!!!
        }

        return step;
    }

}
