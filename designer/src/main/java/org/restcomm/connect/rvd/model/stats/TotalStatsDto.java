package org.restcomm.connect.rvd.model.stats;

import java.util.Date;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class TotalStatsDto {
    Date startTime;
    Integer rcmlRequestsTotal;

    public void setStartTime(Long time) {
        this.startTime = new Date(time);
    }

    public void setRcmlRequestsTotal(Integer rcmlRequestsTotal) {
        this.rcmlRequestsTotal = rcmlRequestsTotal;
    }
}