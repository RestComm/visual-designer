package org.restcomm.connect.rvd.model.steps.dial;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DialNounJsonSerializer implements JsonSerializer<DialNoun> {
    @Override
    public JsonElement serialize(DialNoun noun, Type arg1, JsonSerializationContext arg2) {
        Gson gson = new GsonBuilder().registerTypeAdapter(DialNoun.class, new DialNounJsonSerializer()).create();
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
