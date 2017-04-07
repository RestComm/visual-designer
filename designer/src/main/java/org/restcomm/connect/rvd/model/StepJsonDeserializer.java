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

import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.client.Step;
import org.restcomm.connect.rvd.model.steps.control.ControlStep;
import org.restcomm.connect.rvd.model.steps.dial.DialNoun;
import org.restcomm.connect.rvd.model.steps.dial.DialNounJsonDeserializer;
import org.restcomm.connect.rvd.model.steps.dial.DialStep;
import org.restcomm.connect.rvd.model.steps.email.EmailStep;
import org.restcomm.connect.rvd.model.steps.es.ExternalServiceStep;
import org.restcomm.connect.rvd.model.steps.fax.FaxStep;
import org.restcomm.connect.rvd.model.steps.gather.GatherStep;
import org.restcomm.connect.rvd.model.steps.hangup.HungupStep;
import org.restcomm.connect.rvd.model.steps.log.LogStep;
import org.restcomm.connect.rvd.model.steps.pause.PauseStep;
import org.restcomm.connect.rvd.model.steps.play.PlayStep;
import org.restcomm.connect.rvd.model.steps.record.RecordStep;
import org.restcomm.connect.rvd.model.steps.redirect.RedirectStep;
import org.restcomm.connect.rvd.model.steps.reject.RejectStep;
import org.restcomm.connect.rvd.model.steps.say.SayStep;
import org.restcomm.connect.rvd.model.steps.sms.SmsStep;
import org.restcomm.connect.rvd.model.steps.ussdcollect.UssdCollectStep;
import org.restcomm.connect.rvd.model.steps.ussdlanguage.UssdLanguageStep;
import org.restcomm.connect.rvd.model.steps.ussdsay.UssdSayStep;

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
public class StepJsonDeserializer implements JsonDeserializer<Step> {
    static final Logger logger = RvdLoggers.local;

    @Override
    public Step deserialize(JsonElement rootElement, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {

        JsonObject step_object = rootElement.getAsJsonObject();
        String kind = step_object.get("kind").getAsString();

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Step.class, new StepJsonDeserializer())
            .registerTypeAdapter(DialNoun.class, new DialNounJsonDeserializer())
            .create();

        Step step;
        if ("say".equals(kind))
            step = gson.fromJson(step_object, SayStep.class);
        else if ("gather".equals(kind))
            step = gson.fromJson(step_object, GatherStep.class);
        else if ("dial".equals(kind))
            step = gson.fromJson(step_object, DialStep.class);
        else if ("hungup".equals(kind))
            step = gson.fromJson(step_object, HungupStep.class);
        else if ("play".equals(kind))
            step = gson.fromJson(step_object, PlayStep.class);
        else if ("control".equals(kind))
            step = gson.fromJson(step_object, ControlStep.class);
        else if ("externalService".equals(kind))
            step = gson.fromJson(step_object, ExternalServiceStep.class);
        else if ("log".equals(kind))
            step = gson.fromJson(step_object, LogStep.class);
        else if ("redirect".equals(kind))
            step = gson.fromJson(step_object, RedirectStep.class);
        else if ("reject".equals(kind))
            step = gson.fromJson(step_object, RejectStep.class);
        else if ("pause".equals(kind))
            step = gson.fromJson(step_object, PauseStep.class);
        else if ("sms".equals(kind))
            step = gson.fromJson(step_object, SmsStep.class);
        else if ("email".equals(kind))
            step = gson.fromJson(step_object, EmailStep.class);
        else if ("record".equals(kind))
            step = gson.fromJson(step_object, RecordStep.class);
        else if ("fax".equals(kind))
            step = gson.fromJson(step_object, FaxStep.class);
        else if ("ussdSay".equals(kind))
            step = gson.fromJson(step_object, UssdSayStep.class);
        else if ("ussdCollect".equals(kind))
            step = gson.fromJson(step_object, UssdCollectStep.class);
        else if ("ussdLanguage".equals(kind))
            step = gson.fromJson(step_object, UssdLanguageStep.class);
        else {
            step = null;
            logger.error("Cannot deserialize step. Unknown step found."); // TODO remove me and return a nice value!!!
        }

        return step;
    }

}
