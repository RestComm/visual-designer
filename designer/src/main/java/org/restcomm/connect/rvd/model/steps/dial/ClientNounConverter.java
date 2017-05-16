package org.restcomm.connect.rvd.model.steps.dial;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.restcomm.connect.rvd.utils.RvdUtils;

public class ClientNounConverter implements Converter {

    @Override
    public boolean canConvert(Class elementClass) {
        return elementClass.equals(RcmlClientNoun.class);
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        RcmlClientNoun step = (RcmlClientNoun) value;
        if (step.getUrl() != null)
            writer.addAttribute("url", step.getUrl());
        if (step.statusCallback != null)
            writer.addAttribute("statusCallback", step.statusCallback);
        if (step.video != null) {
            // if video attributes exist, we need to use 'name' attribute and not as the body/text of the noun element
            if (!RvdUtils.isEmpty(step.getDestination()))
                writer.addAttribute("name", step.getDestination());
            // video attributes
            writer.startNode("Video");
            if (step.video.enable != null)
                writer.addAttribute("enable", step.video.enable.toString());
            if (step.video.overlay != null)
                writer.addAttribute("overlay", step.video.overlay);
            writer.endNode();
        } else
            writer.setValue(step.getDestination());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader arg0, UnmarshallingContext arg1) {
        // TODO Auto-generated method stub
        return null;
    }

}
