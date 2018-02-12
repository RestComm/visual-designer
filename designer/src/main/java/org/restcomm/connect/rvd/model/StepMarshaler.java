package org.restcomm.connect.rvd.model;

import org.restcomm.connect.rvd.model.project.Step;
import org.restcomm.connect.rvd.model.packaging.RappInfo;

import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;

public class StepMarshaler extends SimpleMarshaller {

    private XStream xstream;

    public StepMarshaler() {
        super(new GsonBuilder()
            .registerTypeAdapter(Step.class, new StepJsonDeserializer())
            .registerTypeAdapter(Step.class, new StepJsonSerializer())
        .create());
    }

    // lazy singleton function
    public XStream getXStream() {
        if (xstream == null) {
            xstream = new XStream();
            xstream.alias("restcommApplication", RappInfo.class);
        }
        return xstream;
    }

}
