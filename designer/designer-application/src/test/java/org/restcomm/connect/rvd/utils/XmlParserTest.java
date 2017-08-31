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
 */

package org.restcomm.connect.rvd.utils;

import junit.framework.Assert;
import org.junit.Test;
import org.restcomm.connect.rvd.exceptions.XmlParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class XmlParserTest {
    @Test
    public void test1() throws XmlParserException {
        String filepath = XmlParserTest.class.getResource("rvd-test.xml").getFile();
        XmlParser xml = new XmlParser(filepath);
        Document doc = xml.getDocument();
        Node root = doc.getDocumentElement();
        Assert.assertEquals("rvd", root.getNodeName());

        Assert.assertEquals("workspace", xml.getElementContent("/rvd/workspaceLocation"));

        List<String> items = xml.getElementList("/rvd/corsWhitelist/origin");
        Assert.assertEquals(2, items.size());
        Assert.assertEquals("http://this:8080", items.get(0));
        Assert.assertEquals("http://that:8080", items.get(1));

        Assert.assertEquals("",xml.getElementContent("/rvd/empty"));

        Assert.assertNull(xml.getElementContent("/rvd/not-existent"));

        Assert.assertNotNull(xml.getElementList("/rvd/not-existent"));
        Assert.assertEquals(0, xml.getElementList("/rvd/not-existent").size());

        Assert.assertNull(xml.getElement("/rvd/not-existing"));
    }

    @Test(expected = XmlParserException.class)
    public void errorIfXmlFileDoesNotExist() throws XmlParserException {
        String filepath = "/crappy/path";
        XmlParser xml = new XmlParser(filepath);
    }

    @Test(expected = XmlParserException.class)
    public void errorIfInvalidXml() throws XmlParserException {
        String filepath = XmlParserTest.class.getResource("invalid.xml").getFile();
        XmlParser xml = new XmlParser(filepath);
    }
}
