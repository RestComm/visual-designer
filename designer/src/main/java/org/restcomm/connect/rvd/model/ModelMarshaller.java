package org.restcomm.connect.rvd.model;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface ModelMarshaller {

    <T> T toModel( String jsonData, Class<T> modelClass );

    <T> T toModel( String jsonData, Type gsonType );

    String toData( Object model );

    Gson getGson();

}
