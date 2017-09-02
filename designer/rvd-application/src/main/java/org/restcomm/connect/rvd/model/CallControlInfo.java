package org.restcomm.connect.rvd.model;

import java.util.List;

/**
 * WebTrigger configuration form information
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class CallControlInfo {

    public class Lane {
        public StartPoint startPoint;
        //public List<InterruptPoint> interruptPoints;
    }
    public class StartPoint {
        public String rcmlUrl;
        public String to;
        public String from;
    }
    /*public class InterruptPoint {
        public String code;
    }*/

    public List<Lane> lanes;
    public String accessToken;
    public String userParamScope; // will user-supplied parameters be considered module-scoped or application-scoped (i.e. sticky) parameters. Defaults to 'module
}
