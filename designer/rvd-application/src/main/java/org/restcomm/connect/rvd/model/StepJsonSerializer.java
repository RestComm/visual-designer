package org.restcomm.connect.rvd.model;

import java.lang.reflect.Type;

import org.restcomm.connect.rvd.model.project.BaseStep;
import org.restcomm.connect.rvd.model.steps.control.ControlStep;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class StepJsonSerializer implements JsonSerializer<BaseStep> {

    @Override
    public JsonElement serialize(BaseStep step, Type arg1, JsonSerializationContext context) {

        Gson gson = new GsonBuilder().registerTypeAdapter(BaseStep.class, new StepJsonSerializer()).create();
        JsonElement resultElement = null; // TODO update this default value to something or throw an exception or something
        if (step instanceof SayStep) {
            resultElement = gson.toJsonTree((SayStep) step);
        } else if (step instanceof PlayStep ) {
            resultElement = gson.toJsonTree((PlayStep) step);
        } else if (step instanceof GatherStep) {
            resultElement = gson.toJsonTree((GatherStep) step);
        } else if (step instanceof ControlStep) {
            resultElement = gson.toJsonTree((ControlStep) step);
        } else if (step instanceof ExternalServiceStep) {
            resultElement = gson.toJsonTree((ExternalServiceStep) step);
        } else if (step instanceof LogStep) {
            resultElement = gson.toJsonTree((LogStep) step);
        } else if (step instanceof DialStep) {
            resultElement = gson.toJsonTree((DialStep) step);
        } else if (step instanceof HungupStep) {
            resultElement = gson.toJsonTree((HungupStep) step);
        } else if (step instanceof RedirectStep) {
            resultElement = gson.toJsonTree((RedirectStep) step);
        } else if (step instanceof RejectStep) {
            resultElement = gson.toJsonTree((RejectStep) step);
        } else if (step instanceof PauseStep) {
            resultElement = gson.toJsonTree((PauseStep) step);
        } else if (step instanceof SmsStep) {
            resultElement = gson.toJsonTree((SmsStep) step);
        } else if (step instanceof EmailStep) {
            resultElement = gson.toJsonTree((EmailStep) step);
        } else if (step instanceof RecordStep) {
            resultElement = gson.toJsonTree((RecordStep) step);
        } else if (step instanceof FaxStep) {
            resultElement = gson.toJsonTree((FaxStep) step);
        } else if (step instanceof UssdSayStep) {
            resultElement = gson.toJsonTree((UssdSayStep) step);
        } else if (step instanceof UssdCollectStep) {
            resultElement = gson.toJsonTree((UssdCollectStep) step);
        } else if (step instanceof UssdLanguageStep) {
            resultElement = gson.toJsonTree((UssdLanguageStep) step);
        }

        return resultElement;
    }

}
