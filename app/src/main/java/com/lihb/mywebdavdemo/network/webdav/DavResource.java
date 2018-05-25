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

/**
 * <code>DavResource</code> provides standard WebDAV functionality as specified
 * by <a href="http://www.ietf.org/rfc/rfc2518.txt">RFC 2518</a>.
 */
public interface DavResource {

    /**
     * String constant representing the WebDAV 1 and 2 method set.
     */
    public static final String METHODS = "OPTIONS, GET, HEAD, POST, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK";

    /**
     * Returns true if this webdav resource has the resourcetype 'collection'.
     *
     * @return true if the resource represents a collection resource.
     */
    public boolean isCollection();

    /**
     * Returns the absolute href of this resource as returned in the
     * multistatus response body.
     *
     * @return href
     */
    public String getHref();

    /**
     * Add/Set the specified property on this resource.
     *
     * @param property
     * @throws DavException if an error occurs
     */
    public void setProperty(DavProperty<?> property) throws DavException;

    /**
     * Retrieve the resource this resource is internal member of.
     *
     * @return resource this resource is an internal member of. In case this resource
     * is the root <code>null</code> is returned.
     */
    public DavResource getCollection();


    /**
     * Copy this DavResource to the given destination resource
     *
     * @param destination
     * @param shallow
     * @throws DavException
     */
    public void copy(DavResource destination, boolean shallow) throws DavException;




}

