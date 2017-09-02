package org.restcomm.connect.rvd.storage.json;

import java.lang.reflect.Type;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedClientDialNoun;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedConferenceDialNoun;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedNumberDialNoun;
import org.restcomm.connect.rvd.interpreter.steps.InterpretedSipuriDialNoun;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.steps.dial.BaseDialNoun;

public class DialNounJsonDeserializer implements JsonDeserializer<BaseDialNoun> {
    static final Logger logger = RvdLoggers.local;

    @Override
    public BaseDialNoun deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        JsonObject noun_object = element.getAsJsonObject();
        String dialType = noun_object.get("dialType").getAsString();

        Gson gson = new GsonBuilder().create();

        BaseDialNoun noun;
        if ("number".equals(dialType) ) {
            noun = gson.fromJson(noun_object, InterpretedNumberDialNoun.class);
        } else
        if ("client".equals(dialType) ) {
            noun = gson.fromJson(noun_object, InterpretedClientDialNoun.class);
        } else
        if ("conference".equals(dialType) ) {
            noun = gson.fromJson(noun_object, InterpretedConferenceDialNoun.class);
        } else
        if ("sipuri".equals(dialType) ) {
            noun = gson.fromJson(noun_object, InterpretedSipuriDialNoun.class);
        } else {
            noun = null;
            logger.error(LoggingHelper.buildMessage(getClass(),"deserialize", "Cannot deserialize. Unknown noun found - "+ dialType)); // TODO remove me and return a nice value!!!
        }

        return noun;
    }

}
