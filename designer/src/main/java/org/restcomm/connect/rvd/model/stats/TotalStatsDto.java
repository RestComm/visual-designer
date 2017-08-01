package org.restcomm.connect.rvd.model.stats;

import java.util.Date;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class TotalStatsDto {
    Date startTime;
    Integer rcmlRequestsTotal;
    Integer esCallsTotal;
    Integer esCallsPending;
    Integer esCallsTimeout;
    Integer esCallsServerError;
    Integer esCallsSuccess;

    public void setStartTime(Long time) {
        this.startTime = new Date(time);
    }

    public void setRcmlRequestsTotal(Integer rcmlRequestsTotal) {
        this.rcmlRequestsTotal = rcmlRequestsTotal;
    }

    public void setEsCallsTotal(Integer esCallsTotal) {
        this.esCallsTotal = esCallsTotal;
    }

    public void setEsCallsPending(Integer esCallsPending) {
        this.esCallsPending = esCallsPending;
    }

    public void setEsCallsTimeout(Integer esCallsTimeout) {
        this.esCallsTimeout = esCallsTimeout;
    }

    public void setEsCallsServerError(Integer esCallsServerError) {
        this.esCallsServerError = esCallsServerError;
    }

    public void setEsCallsSuccess(Integer esCallsSuccess) {
        this.esCallsSuccess = esCallsSuccess;
    }
}