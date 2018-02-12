package org.restcomm.connect.rvd.model;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class SimpleMarshaller implements ModelMarshaller {
    Gson gsonUtil;

    public SimpleMarshaller(Gson gsonUtil) {
        this.gsonUtil = gsonUtil;
    }

    public SimpleMarshaller() {
        this.gsonUtil = new Gson();
    }

    @Override
    public <T> T toModel( String jsonData, Class<T> modelClass ) {
        T instance = gsonUtil.fromJson(jsonData, modelClass);
        return instance;
    }

    @Override
    public <T> T toModel( String jsonData, Type gsonType ) {
        T instance = gsonUtil.fromJson(jsonData, gsonType);
        return instance;
    }

    @Override
    public String toData( Object model ) {
        return gsonUtil.toJson(model);
    }

    @Override
    public Gson getGson() {
        return gsonUtil;
    }
}
