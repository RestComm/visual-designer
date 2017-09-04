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

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */


public class StatsHelper {
    public static void countRcmlRequestIncoming(AggregateStats aggregateStats) {
        aggregateStats.rcmlRequestsTotal.incrementAndGet();
    }

    public static void countEsCallTotal(AggregateStats stats) {
        stats.esCallsTotal.incrementAndGet();
    }

    public static void countEsCallPending(AggregateStats stats, int delta ) {
        stats.esCallsPending.addAndGet(delta);
    }

    public static void countEsCallTimeout(AggregateStats stats) {
        stats.esCallsTimeout.incrementAndGet();
    }

    public static void countEsCallServerError(AggregateStats stats) {
        stats.esCallsServerError.incrementAndGet();
    }

    public static void countEsCallSuccess(AggregateStats stats) {
        stats.esCallsSuccess.incrementAndGet();
    }
}
