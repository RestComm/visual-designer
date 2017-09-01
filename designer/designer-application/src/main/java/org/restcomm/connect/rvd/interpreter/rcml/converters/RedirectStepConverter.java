package org.restcomm.connect.rvd.interpreter.rcml.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.restcomm.connect.rvd.interpreter.rcml.RcmlRedirectStep;

public class RedirectStepConverter implements Converter {

    @Override
    public boolean canConvert(Class elementClass) {
        return elementClass.equals(RcmlRedirectStep.class);
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        RcmlRedirectStep step = (RcmlRedirectStep) value;
        if ( step.getMethod() != null )
            writer.addAttribute("method", step.getMethod());
        writer.setValue(step.getUrl());
    }

    // will not need this for now
    @Override
    public Object unmarshal(HierarchicalStreamReader arg0, UnmarshallingContext arg1) {
        // TODO Auto-generated method stub
        return null;
    }

}
