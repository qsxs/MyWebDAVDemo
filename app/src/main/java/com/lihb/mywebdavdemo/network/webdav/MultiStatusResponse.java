/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lihb.mywebdavdemo.network.webdav;

import com.lihb.mywebdavdemo.network.webdav.property.DavProperty;
import com.lihb.mywebdavdemo.network.webdav.property.DavPropertyName;
import com.lihb.mywebdavdemo.network.webdav.property.DavPropertyNameSet;
import com.lihb.mywebdavdemo.network.webdav.property.DavPropertySet;
import com.lihb.mywebdavdemo.network.webdav.property.DefaultDavProperty;
import com.lihb.mywebdavdemo.network.webdav.property.PropContainer;
import com.lihb.mywebdavdemo.network.webdav.xml.DomUtil;
import com.lihb.mywebdavdemo.network.webdav.xml.ElementIterator;
import com.lihb.mywebdavdemo.network.webdav.xml.XmlSerializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Iterator;

/**
 * <code>MultiStatusResponse</code> represents the DAV:multistatus element defined
 * by RFC 2518:
 * <pre>
 * &lt;!ELEMENT response (href, ((href*, status)|(propstat+)), responsedescription?) &gt;
 * &lt;!ELEMENT status (#PCDATA) &gt;
 * &lt;!ELEMENT propstat (prop, status, responsedescription?) &gt;
 * &lt;!ELEMENT responsedescription (#PCDATA) &gt;
 * &lt;!ELEMENT prop ANY &gt;
 * </pre>
 */
public class MultiStatusResponse implements XmlSerializable, DavConstants {

    private static final int TYPE_PROPSTAT = 0;
    private static final int TYPE_HREFSTATUS = 1;

    /**
     * The type of MultiStatusResponse
     */
    private final int type;

    /**
     * The content the 'href' element for this response
     */
    private final String href;

    /**
     * An optional response description.
     */
    private final String responseDescription;

    /**
     * Type of MultiStatus response: Href + Status
     */
    private Status status;

    /**
     * Type of MultiStatus response: PropStat Hashmap containing all status
     */
    private HashMap<Integer, PropContainer> statusMap = new HashMap<Integer, PropContainer>();

    private MultiStatusResponse(String href, String responseDescription, int type) {
        if (!isValidHref(href)) {
            throw new IllegalArgumentException("Invalid href ('" + href + "')");
        }
        this.href = href;
        this.responseDescription = responseDescription;
        this.type = type;
    }

    /**
     * Constructs an WebDAV multistatus response
     *
     * @param href
     * @param status
     * @param responseDescription
     */
    public MultiStatusResponse(String href, Status status, String responseDescription) {
        this(href, responseDescription, TYPE_HREFSTATUS);
        if (status == null) {
            throw new IllegalArgumentException("Status must not be null in case of a multistatus reponse that consists of href + status only.");
        }
        this.status = status;
    }

    /**
     * Returns the href
     *
     * @return href
     * @see MultiStatusResponse#getHref()
     */
    public String getHref() {
        return href;
    }

    /**
     * @return responseDescription
     * @see MultiStatusResponse#getResponseDescription()
     */
    public String getResponseDescription() {
        return responseDescription;
    }

    /**
     * Return an array listing all 'status' available is this response object.
     * Note, that a the array contains a single element if this
     * <code>MultiStatusResponse</code> defines an response consisting of
     * href and status elements.
     *
     * @return
     */
    public Status[] getStatus() {
        Status[] sts;
        if (type == TYPE_PROPSTAT) {
            sts = new Status[statusMap.size()];
            Iterator<Integer> iter = statusMap.keySet().iterator();
            for (int i = 0; iter.hasNext(); i++) {
                Integer statusKey = iter.next();
                sts[i] = new Status(statusKey);
            }
        } else {
            sts = new Status[] {status};
        }
        return sts;
    }

    /**
     * @param document
//     * @see xml.XmlSerializable#toXml(org.w3c.dom.Document)
     */
    public Element toXml(Document document) {
        Element response = DomUtil.createElement(document, XML_RESPONSE, NAMESPACE);
        // add '<href>'
        response.appendChild(DomUtil.hrefToXml(getHref(), document));
        if (type == TYPE_PROPSTAT) {
            // add '<propstat>' elements
            for (Integer statusKey : statusMap.keySet()) {
                Status st = new Status(statusKey);
                PropContainer propCont = statusMap.get(statusKey);
                if (!propCont.isEmpty()) {
                    Element propstat = DomUtil.createElement(document, XML_PROPSTAT, NAMESPACE);
                    propstat.appendChild(propCont.toXml(document));
                    propstat.appendChild(st.toXml(document));
                    response.appendChild(propstat);
                }
            }
        } else {
            // add a single '<status>' element
            // NOTE: a href+status response cannot be created with 'null' status
            response.appendChild(status.toXml(document));
        }
        // add the optional '<responsedescription>' element
        String description = getResponseDescription();
        if (description != null) {
            Element desc = DomUtil.createElement(document, XML_RESPONSEDESCRIPTION, NAMESPACE);
            DomUtil.setText(desc, description);
            response.appendChild(desc);
        }
        return response;
    }
    //----------------------------------------------< type specific methods >---
    /**
     * Adds a property to this response '200' propstat set.
     *
     * @param property the property to add
     */
    public void add(DavProperty<?> property) {
        checkType(TYPE_PROPSTAT);
        PropContainer status200 = getPropContainer(DavServletResponse.SC_OK, false);
        status200.addContent(property);
    }

    /**
     * Adds a property name to this response '200' propstat set.
     *
     * @param propertyName the property name to add
     */
    public void add(DavPropertyName propertyName) {
        checkType(TYPE_PROPSTAT);
        PropContainer status200 = getPropContainer(DavServletResponse.SC_OK, true);
        status200.addContent(propertyName);
    }

    /**
     * Adds a property to this response
     *
     * @param property the property to add
     * @param status the status of the response set to select
     */
    public void add(DavProperty<?> property, int status) {
        checkType(TYPE_PROPSTAT);
        PropContainer propCont = getPropContainer(status, false);
        propCont.addContent(property);
    }

    /**
     * Adds a property name to this response
     *
     * @param propertyName the property name to add
     * @param status the status of the response set to select
     */
    public void add(DavPropertyName propertyName, int status) {
        checkType(TYPE_PROPSTAT);
        PropContainer propCont = getPropContainer(status, true);
        propCont.addContent(propertyName);
    }

    /**
     * @param status
     * @return
     */
    private PropContainer getPropContainer(int status, boolean forNames) {
        PropContainer propContainer = statusMap.get(status);
        if (propContainer == null) {
            if (forNames) {
                propContainer = new DavPropertyNameSet();
            } else {
                propContainer = new DavPropertySet();
            }
            statusMap.put(status, propContainer);
        }
        return propContainer;
    }

    private void checkType(int type) {
        if (this.type != type) {
            throw new IllegalStateException("The given MultiStatusResponse is not of the required type.");
        }
    }

    /**
     * Get properties present in this response for the given status code. In
     * case this MultiStatusResponse does not represent a 'propstat' response,
     * always an empty {@link DavPropertySet} will be returned.
     *
     * @param status
     * @return property set
     */
    public DavPropertySet getProperties(int status) {
        if (statusMap.containsKey(status)) {
            PropContainer mapEntry = statusMap.get(status);
            if (mapEntry != null && mapEntry instanceof DavPropertySet) {
                return (DavPropertySet) mapEntry;
            }
        }
        return new DavPropertySet();
    }

    /**
     * Get property names present in this response for the given status code. In
     * case this MultiStatusResponse does not represent a 'propstat' response,
     * always an empty {@link DavPropertyNameSet} will be returned.
     *
     * @param status
     * @return property names
     */
    public DavPropertyNameSet getPropertyNames(int status) {
        if (statusMap.containsKey(status)) {
            PropContainer mapEntry = statusMap.get(status);
            if (mapEntry != null) {
                if (mapEntry instanceof DavPropertySet) {
                    DavPropertyNameSet set = new DavPropertyNameSet();
                    for (DavPropertyName name : ((DavPropertySet) mapEntry).getPropertyNames()) {
                        set.add(name);
                    }
                    return set;
                } else {
                    // is already a DavPropertyNameSet
                    return (DavPropertyNameSet) mapEntry;
                }
            }
        }
        return new DavPropertyNameSet();
    }

    /**
     * Build a new response object from the given xml element.
     *
     * @param responseElement
     * @return new <code>MultiStatusResponse</code> instance
     * @throws IllegalArgumentException if the specified element is
     * <code>null</code> or not a DAV:response element or if the mandatory
     * DAV:href child is missing.
     */
    public static MultiStatusResponse createFromXml(Element responseElement) {
        if (!DomUtil.matches(responseElement, XML_RESPONSE, NAMESPACE)) {
            throw new IllegalArgumentException("DAV:response element required.");
        }
        String href = DomUtil.getChildTextTrim(responseElement, XML_HREF, NAMESPACE);
        if (href == null) {
            throw new IllegalArgumentException("DAV:response element must contain a DAV:href element expected.");
        }
        String statusLine = DomUtil.getChildText(responseElement, XML_STATUS, NAMESPACE);
        String responseDescription = DomUtil.getChildText(responseElement, XML_RESPONSEDESCRIPTION, NAMESPACE);

        MultiStatusResponse response;
        if (statusLine != null) {
            Status status = Status.parse(statusLine);
            response = new MultiStatusResponse(href, status, responseDescription);
        } else {
            response = new MultiStatusResponse(href, responseDescription, TYPE_PROPSTAT);
            // read propstat elements
            ElementIterator it = DomUtil.getChildren(responseElement, XML_PROPSTAT, NAMESPACE);
            while (it.hasNext()) {
                Element propstat = it.nextElement();
                String propstatus = DomUtil.getChildText(propstat, XML_STATUS, NAMESPACE);
                Element prop = DomUtil.getChildElement(propstat, XML_PROP, NAMESPACE);
                if (propstatus != null && prop != null) {
                    int statusCode = Status.parse(propstatus).getStatusCode();
                    ElementIterator propIt = DomUtil.getChildren(prop);
                    while (propIt.hasNext()) {
                        Element el = propIt.nextElement();
                        /*
                        always build dav property from the given element, since
                        distinction between prop-names and properties not having
                        a value is not possible.
                        retrieval of the set of 'property names' is possible from
                        the given prop-set by calling DavPropertySet#getPropertyNameSet()
                        */
                        DavProperty<?> property = DefaultDavProperty.createFromXml(el);
                        response.add(property, statusCode);
                    }
                }
            }
        }
        return response;
    }

    /**
     * @param href
     * @return false if the given href is <code>null</code> or empty string.
     */
    private static boolean isValidHref(String href) {
        return href != null && !"".equals(href);
    }
}
