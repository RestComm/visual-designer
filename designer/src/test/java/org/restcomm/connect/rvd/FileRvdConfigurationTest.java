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

import java.net.URL;
import junit.framework.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.restcomm.connect.rvd.configuration.RvdConfig;

/**
 *
 * @author jimmy
 */
public class FileRvdConfigurationTest {

    public FileRvdConfigurationTest() {
    }

    @Test
    public void testLoadRvdXmlConfig() {
        URL url = this.getClass().getClassLoader().getResource("rvdHttpParams.xml");
        RvdConfig loadRvdXmlConfig = FileRvdConfiguration.loadRvdXmlConfig(url.getFile());
        Assert.assertEquals("1", loadRvdXmlConfig.getExternalServiceTimeout());
        Assert.assertEquals(Integer.valueOf(1), loadRvdXmlConfig.getExternalServiceMaxConns());
        Assert.assertEquals(Integer.valueOf(1), loadRvdXmlConfig.getExternalServiceTTL());
        Assert.assertEquals(Integer.valueOf(1), loadRvdXmlConfig.getExternalServiceMaxConnsPerRoute());
        Assert.assertEquals(Integer.valueOf(1), loadRvdXmlConfig.getDefaultHttpTimeout());
        Assert.assertEquals(Integer.valueOf(1), loadRvdXmlConfig.getDefaultHttpMaxConns());
        Assert.assertEquals(Integer.valueOf(1), loadRvdXmlConfig.getDefaultHttpMaxConnsPerRoute());
        Assert.assertEquals(Integer.valueOf(1), loadRvdXmlConfig.getDefaultHttpTTL());
        Assert.assertEquals(1,loadRvdXmlConfig.getDefaultHttpMaxPerRoute().size());
        Assert.assertEquals("http://127.0.0.1",loadRvdXmlConfig.getDefaultHttpMaxPerRoute().get(0).getUrl());
        Assert.assertEquals(Integer.valueOf(1),loadRvdXmlConfig.getDefaultHttpMaxPerRoute().get(0).getMaxConnections());
        Assert.assertEquals(2,loadRvdXmlConfig.getExternalServicepMaxPerRoute().size());
        Assert.assertEquals("http://127.0.0.1",loadRvdXmlConfig.getExternalServicepMaxPerRoute().get(1).getUrl());
        Assert.assertEquals(Integer.valueOf(2),loadRvdXmlConfig.getExternalServicepMaxPerRoute().get(1).getMaxConnections());        
    }

}
