package org.restcomm.connect.rvd.storage.json;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.restcomm.connect.rvd.model.steps.dial.BaseDialNoun;
import org.restcomm.connect.rvd.model.steps.dial.ClientDialNoun;
import org.restcomm.connect.rvd.model.steps.dial.ConferenceDialNoun;
import org.restcomm.connect.rvd.model.steps.dial.NumberDialNoun;
import org.restcomm.connect.rvd.model.steps.dial.SipuriDialNoun;

public class DialNounJsonSerializer implements JsonSerializer<BaseDialNoun> {
    @Override
    public JsonElement serialize(BaseDialNoun noun, Type arg1, JsonSerializationContext arg2) {
        Gson gson = new GsonBuilder().registerTypeAdapter(BaseDialNoun.class, new DialNounJsonSerializer()).create();
        JsonElement resultElement = null; // TODO update this default value to something or throw an exception or something
        if (noun.getClass().equals(NumberDialNoun.class)) {
            resultElement = gson.toJsonTree((NumberDialNoun) noun);
        } else if (noun.getClass().equals(ClientDialNoun.class)  ) {
            resultElement = gson.toJsonTree((ClientDialNoun) noun);
        } else if (noun.getClass().equals(ConferenceDialNoun.class)  ) {
            resultElement = gson.toJsonTree((ConferenceDialNoun) noun);
        } else if (noun.getClass().equals(SipuriDialNoun.class)  ) {
            resultElement = gson.toJsonTree((SipuriDialNoun) noun);
        }

        return resultElement;
    }

}
