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

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
import org.restcomm.connect.rvd.exceptions.XmlParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {
    private XPathFactory xPathfactory;
    private Document doc;
    private String xmlFilePath;

    public XmlParser(String filepath) throws XmlParserException {
        this.xmlFilePath = filepath;
        xPathfactory = XPathFactory.newInstance();
        File xmlFile = new File(filepath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(xmlFile);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new XmlParserException("Error parsing xml file: " + xmlFilePath, e);
        }
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();
    }

    public Document getDocument() {
        return doc;
    }

    /**
     * Return the text content of the element specified in xpathString.
     * If there is no such element it will return null. Use it for single-valued elements
     *
     * @param xpathString
     * @return content of element of null
     */
    public String getElementContent(String xpathString) throws XmlParserException {
        Node node = getElement(xpathString);
        if (node != null) {
            return node.getTextContent();
        }
        return null;
    }


    /**
     * Returns the node specified in xpathString or null if this does not exist
     *
     * @param xpathString
     * @return a Node object or null
     */
    public Node getElement(String xpathString) throws XmlParserException {
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr;
        try {
            expr = xpath.compile(xpathString);
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            return node;
        } catch (XPathExpressionException e) {
            throw new XmlParserException("Error parsing xml file: " + xmlFilePath, e);
        }
    }

    /**
     * Returns a list of strings targeted by an path expression. If the elements do not exist
     * it returns an empty list [].
     *
     * e.g. For xpath '/rvd/cors/whitelist/origin' will return a list of the text content of <origin/> elements.
     *
     * @param xpathString
     * @return
     */
    public List<String> getElementList(String xpathString) throws XmlParserException {
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr;
        try {
            expr = xpath.compile(xpathString);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            if (nodes != null) {
                List list = new ArrayList();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    list.add(node.getTextContent());
                }
                return list;
            }
        } catch (XPathExpressionException e) {
            throw new XmlParserException("Error parsing xml file: " + xmlFilePath, e);
        }
        return null;
    }
}
