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

package org.restcomm.connect.rvd;

import org.apache.http.impl.client.CloseableHttpClient;
import org.restcomm.connect.rvd.commons.http.CustomHttpClientBuilder;
import org.restcomm.connect.rvd.concurrency.ProjectRegistry;
import org.restcomm.connect.rvd.identity.AccountProvider;
import org.restcomm.connect.rvd.stats.AggregateStats;

/**
 * This class holds all objects whose lifecycle follows the rvd application.
 * For example, RvdConfiguration, CustomHttpClient builder etc.
 * Typically, such objects used the singleton approach.
 *
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class ApplicationContext {
    RvdConfiguration configuration;
    CustomHttpClientBuilder httpClientBuilder;
    CloseableHttpClient defaultHttpClient;
    CloseableHttpClient externaltHttpClient;
    AccountProvider accountProvider;
    ProjectRegistry projectRegistry;
    AggregateStats globalStats;

    public ApplicationContext() {
        globalStats = new AggregateStats();
    }

    public RvdConfiguration getConfiguration() {
        return configuration;
    }

    public CustomHttpClientBuilder getHttpClientBuilder() {
        return httpClientBuilder;
    }

    public AccountProvider getAccountProvider() {
        return accountProvider;
    }

    public ProjectRegistry getProjectRegistry() {
        return projectRegistry;
    }

    public AggregateStats getGlobalStats() {
        return globalStats;
    }

    public void setGlobalStats(AggregateStats globalStats) {
        this.globalStats = globalStats;
    }

    public CloseableHttpClient getDefaultHttpClient() {
        return defaultHttpClient;
    }

    public CloseableHttpClient getExternaltHttpClient() {
        return externaltHttpClient;
    }
}
