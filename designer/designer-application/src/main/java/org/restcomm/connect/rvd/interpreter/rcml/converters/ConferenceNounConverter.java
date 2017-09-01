package org.restcomm.connect.rvd.interpreter.rcml.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.restcomm.connect.rvd.utils.RvdUtils;

public class ConferenceNounConverter implements Converter {

    @Override
    public boolean canConvert(Class elementClass) {
        return elementClass.equals(RcmlConferenceNoun.class);
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        RcmlConferenceNoun step = (RcmlConferenceNoun) value;

        if (step.getBeep() != null)
            writer.addAttribute("beep", step.getBeep().toString());
        if (step.getMuted() != null)
            writer.addAttribute("muted", step.getMuted().toString());
        if (step.getEndConferenceOnExit() != null)
            writer.addAttribute("endConferenceOnExit", step.getEndConferenceOnExit().toString());
        if (step.getStartConferenceOnEnter() != null)
            writer.addAttribute("startConferenceOnEnter", step.getStartConferenceOnEnter().toString());
        if (step.getMaxParticipants() != null)
            writer.addAttribute("maxParticipants", step.getMaxParticipants().toString());
        if (step.getWaitUrl() != null)
            writer.addAttribute("waitUrl", step.getWaitUrl());
        if (step.getWaitMethod() != null)
            writer.addAttribute("waitMethod", step.getWaitMethod());
        if (step.getStatusCallback() != null)
            writer.addAttribute("statusCallback", step.getStatusCallback());
        if (step.video != null) {
            // if video attributes exist, we need to use 'name' conference name as the 'name' attribute and not as the body/text of the noun element
            if (!RvdUtils.isEmpty(step.getDestination()))
                writer.addAttribute("name", step.getDestination());
            // video attributes
            writer.startNode("Video");
            if (step.video.enable != null)
                writer.addAttribute("enable", step.video.enable.toString());
            if (step.video.mode != null)
                writer.addAttribute("mode", step.video.mode);
            if (step.video.resolution != null)
                writer.addAttribute("resolution", step.video.resolution);
            if (step.video.layout != null)
                writer.addAttribute("layout", step.video.layout);
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
