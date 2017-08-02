/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package org.restcomm.connect.rvd.stats;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class AggregateStats {

    public AggregateStats() {
        startTime = new AtomicLong(new Date().getTime());
        rcmlRequestsTotal = new AtomicInteger(0);
        esCallsTotal = new AtomicInteger(0);
        esCallsPending = new AtomicInteger(0);
        esCallsSuccess = new AtomicInteger(0);
        esCallsServerError = new AtomicInteger(0);
        esCallsTimeout = new AtomicInteger(0);
    }

    /**
     *
     */
    public AtomicLong startTime; // number of seconds since year 1970 GMT

    /**
     * Total number of incoming requests for RCML no matter what their outcome was
     */
    public AtomicInteger rcmlRequestsTotal;

    /**
     * Total number or ES requests
     *
     * All ES calls even those that failed with a URL parsing exception before making the HTTP request
     * are counted.
     */
    public AtomicInteger esCallsTotal;

    /**
     * Numbers of ES request that are pending
     * <p>
     *     When a request is sent, it's marked as pending. When the thread unblocks, pending counter
     *     is decremented.
     * </p>
     */
    public AtomicInteger esCallsPending;

    /**
     * Numbers of ES request that timed out
     * <p>
     *     Technically, this is the number of requests that threw SocketTimeoutException.
     * </p>
     */
    public AtomicInteger esCallsTimeout;

    /**
     * Number of ES requests that resulted in external server HTTP error
     *
     * All responses returning a 4xx or 5xx status code are counted. Such responses are coupled
     * with an RemoteServiceError exception thrown in the logs.
     */
    public AtomicInteger esCallsServerError;

    /**
     * Number of ES requests that were completed successfully.
     *
     * Requests that received a successfull response from external server (non 4xx or 5xx) and
     * had no problems parsing responses, performing assigning and routing will be counted as such
     */
    public AtomicInteger esCallsSuccess;

    /**
     * Number of ES calls for which an HTTP request was made and a response is received or an error is
     * thrown (i.e. they are no more blocked).
     *
     * This metric is used in conjuction with esCallsTotalDurationMilis to calculate average execution time
     */
    //public AtomicInteger esCallsRequested;

    /**
     *
     */
    //public AtomicLong esCallsTotalDurationMilis;
}
