package org.restcomm.connect.rvd.model.steps.dial;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;

public class DialNounJsonDeserializer implements JsonDeserializer<DialNoun> {
    static final Logger logger = RvdLoggers.system;

    @Override
    public DialNoun deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        JsonObject noun_object = element.getAsJsonObject();
        String dialType = noun_object.get("dialType").getAsString();

        Gson gson = new GsonBuilder().create();

        DialNoun noun;
        if ("number".equals(dialType) ) {
            noun = gson.fromJson(noun_object, NumberDialNoun.class);
        } else
        if ("client".equals(dialType) ) {
            noun = gson.fromJson(noun_object, ClientDialNoun.class);
        } else
        if ("conference".equals(dialType) ) {
            noun = gson.fromJson(noun_object, ConferenceDialNoun.class);
        } else
        if ("sipuri".equals(dialType) ) {
            noun = gson.fromJson(noun_object, SipuriDialNoun.class);
        } else {
            noun = null;
            logger.severe("Cannot deserialize. Unknown noun found - "+ dialType); // TODO remove me and return a nice value!!!
        }

        return noun;
    }

}
