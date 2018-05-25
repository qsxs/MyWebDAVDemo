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

import com.lihb.mywebdavdemo.network.webdav.xml.DomUtil;
import com.lihb.mywebdavdemo.network.webdav.xml.XmlSerializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>DavException</code> extends the {@link Exception} class in order
 * to simplify handling of exceptional situations occurring during processing
 * of WebDAV requests and provides possibility to retrieve an Xml representation
 * of the error.
 */
public class DavException extends Exception implements XmlSerializable {
    private static final String TAG = "DavException";
    //    private static Logger log = LoggerFactory.getLogger(DavException.class);
//    private static Properties statusPhrases = new Properties();
    private static Map<Integer, String> statusMap = new HashMap<>();

    static {
//        try {
//            statusPhrases.load(DavException.class.getResourceAsStream("statuscode.properties"));
        statusMap.put(100, "Continue");
        statusMap.put(101, "Switching Protocols");
        statusMap.put(102, "Processing");
        statusMap.put(200, "OK");
        statusMap.put(201, "Created");
        statusMap.put(202, "Accepted");
        statusMap.put(203, "Non-Authoritative Information");
        statusMap.put(204, "No Content");
        statusMap.put(205, "Reset Content");
        statusMap.put(206, "Partial Content");
        statusMap.put(207, "Multi-Status");
        statusMap.put(300, "Multiple Choices");
        statusMap.put(301, "Moved Permanently");
        statusMap.put(302, "Found");
        statusMap.put(303, "See Other");
        statusMap.put(304, "Not Modified");
        statusMap.put(305, "Use Proxy");
        statusMap.put(307, "Temporary Redirect");
        statusMap.put(400, "Bad Request");
        statusMap.put(401, "Unauthorized");
        statusMap.put(402, "Payment Required");
        statusMap.put(403, "Forbidden");
        statusMap.put(404, "Not Found");
        statusMap.put(405, "Method Not Allowed");
        statusMap.put(406, "Not Acceptable");
        statusMap.put(407, "Proxy Authentication Required");
        statusMap.put(408, "Request Time-out");
        statusMap.put(409, "Conflict");
        statusMap.put(410, "Gone");
        statusMap.put(411, "Length Required");
        statusMap.put(412, "Precondition Failed");
        statusMap.put(413, "Request Entity Too Large");
        statusMap.put(414, "Request-URI Too Large");
        statusMap.put(415, "Unsupported Media Type");
        statusMap.put(416, "Requested range not satisfiable");
        statusMap.put(417, "Expectation Failed");
        statusMap.put(420, "Method Failure");
        statusMap.put(422, "Unprocessable Entity");
        statusMap.put(423, "Locked");
        statusMap.put(424, "Failed Dependency");
        statusMap.put(500, "Internal Server Error");
        statusMap.put(501, "Not Implemented");
        statusMap.put(502, "Bad Gateway");
        statusMap.put(503, "Service Unavailable");
        statusMap.put(504, "Gateway Time-out");
        statusMap.put(505, "HTTP Version not supported");
        statusMap.put(507, "Insufficient Storage");
//        } catch (IOException e) {
//            Log.e(TAG, "static initializer: ", e);
//            log.error("Failed to load status properties: " + e.getMessage());
//        }
    }

    public static final String XML_ERROR = "error";

    //    private int errorCode = DavServletResponse.SC_INTERNAL_SERVER_ERROR;
    private int errorCode = 444;
    private Element errorCondition;

    /**
     * Create a new <code>DavException</code>.
     *
     * @param errorCode integer specifying any of the status codes defined by
     *                  { DavServletResponse}.
     * @param message   Human readable error message.
     * @see DavException#DavException(int, String, Throwable, Element)
     */
    public DavException(int errorCode, String message) {
        this(errorCode, message, null, null);
    }

    /**
     * Create a new <code>DavException</code>.
     *
     * @param errorCode integer specifying any of the status codes defined by
     *                  { DavServletResponse}.
     * @param cause     Cause of this DavException
     * @see DavException#DavException(int, String, Throwable, Element)
     */
    public DavException(int errorCode, Throwable cause) {
        this(errorCode, null, cause, null);
    }

    /**
     * Create a new <code>DavException</code>.
     *
     * @param errorCode integer specifying any of the status codes defined by
     *                  { DavServletResponse}.
     * @see DavException#DavException(int, String, Throwable, Element)
     */
    public DavException(int errorCode) {
//        this(errorCode, statusPhrases.getProperty(String.valueOf(errorCode)), null, null);
        this(errorCode, statusMap.get(errorCode), null, null);
    }

    /**
     * Create a new <code>DavException</code>.
     *
     * @param errorCode      integer specifying any of the status codes defined by
     *                       { DavServletResponse}.
     * @param message        Human readable error message.
     * @param cause          Cause of this <code>DavException</code>.
     * @param errorCondition Xml element providing detailed information about
     *                       the error. If the condition is not <code>null</code>, {@link #toXml(Document)}
     */
    public DavException(int errorCode, String message, Throwable cause, Element errorCondition) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorCondition = errorCondition;
//        log.debug("DavException: (" + errorCode + ") " + message);
    }

    /**
     * Return the error code attached to this <code>DavException</code>.
     *
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Return the status phrase corresponding to the error code attached to
     * this <code>DavException</code>.
     *
     * @return status phrase corresponding to the error code.
     * @see #getErrorCode()
     */
    public String getStatusPhrase() {
        return getStatusPhrase(errorCode);
    }

    /**
     * Returns the status phrase for the given error code.
     *
     * @param errorCode
     * @return status phrase corresponding to the given error code.
     */
    public static String getStatusPhrase(int errorCode) {
//        return statusPhrases.getProperty(errorCode + "");
        return statusMap.get(errorCode);
    }

    /**
     * @return true if a error condition has been specified, false otherwise.
     */
    public boolean hasErrorCondition() {
        return errorCondition != null;
    }

    /**
     * Returns a DAV:error element containing the error condition or
     * <code>null</code> if no specific condition is available. See
     * <a href="http://www.ietf.org/rfc/rfc3253.txt">RFC 3253</a>
     * Section 1.6 "Method Preconditions and Postconditions" for additional
     * information.
     *
     * @param document
     * @return A DAV:error element indicating the error cause or <code>null</code>.
     * org.apache.jackrabbit.webdav.xml.XmlSerializable#toXml(Document)
     */
    public Element toXml(Document document) {
        if (hasErrorCondition()) {
            Element error;
            if (DomUtil.matches(errorCondition, XML_ERROR, DavConstants.NAMESPACE)) {
                error = (Element) document.importNode(errorCondition, true);
            } else {
                error = DomUtil.createElement(document, XML_ERROR, DavConstants.NAMESPACE);
                error.appendChild(document.importNode(errorCondition, true));
            }
            return error;
        } else {
            return null;
        }
    }
}