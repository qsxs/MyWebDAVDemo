package com.lihb.mywebdavdemo;

import com.lihb.mywebdavdemo.network.webdav.MultiStatus;
import com.lihb.mywebdavdemo.network.webdav.MultiStatusResponse;
import com.lihb.mywebdavdemo.network.webdav.property.DavProperty;
import com.lihb.mywebdavdemo.network.webdav.property.DavPropertyName;
import com.lihb.mywebdavdemo.network.webdav.property.DavPropertySet;

import java.util.ArrayList;
import java.util.List;

public class DavResumeModel {
    public static final String METHODS = "OPTIONS, GET, HEAD, POST, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK";

    public static DavResumeModel create(MultiStatus multiStatus) {
        DavResumeModel davResumeModel = new DavResumeModel();
        davResumeModel.multiStatus = multiStatus;
        List<ResponsesBean> list = new ArrayList<>();
        for (MultiStatusResponse multiStatusResponse : multiStatus.getResponses()) {
            ResponsesBean responsesBean = new ResponsesBean();
            responsesBean.href = multiStatusResponse.getHref();
            responsesBean.status = multiStatusResponse.getStatus()[0].getStatusCode();

            DavPropertySet propSet = multiStatusResponse.getProperties(responsesBean.status);
            DavProperty<?> property = propSet.get(DavPropertyName.GETLASTMODIFIED);
            if (property != null && property.getValue() != null)
                responsesBean.getlastmodified = property.getValue().toString();
            DavProperty<?> property1 = propSet.get(DavPropertyName.GETCONTENTLENGTH);
            if (property1 != null && property1.getValue() != null)
                responsesBean.getcontentlength = property1.getValue().toString();
            DavProperty<?> property2 = propSet.get(DavPropertyName.XML_OWNER);
            if (property2 != null && property2.getValue() != null)
                responsesBean.owner = property2.getValue().toString();
            DavProperty<?> property3 = propSet.get(DavPropertyName.GETCONTENTTYPE);
            if (property3 != null && property3.getValue() != null)
                responsesBean.getcontenttype = property3.getValue().toString();
            DavProperty<?> property4 = propSet.get(DavPropertyName.DISPLAYNAME);
            if (property4 != null && property4.getValue() != null)
                responsesBean.displayname = property4.getValue().toString();
            DavProperty<?> property5 = propSet.get(DavPropertyName.GETETAG);
            if (property5 != null && property5.getValue() != null)
                responsesBean.getetag = property5.getValue().toString();


            list.add(responsesBean);
        }
        davResumeModel.responses = list;
        return davResumeModel;
    }

    public List<ResponsesBean> getResponses() {
        return responses;
    }

    public void setResponses(List<ResponsesBean> responses) {
        this.responses = responses;
    }

    public MultiStatus getMultiStatus() {
        return multiStatus;
    }

    public void setMultiStatus(MultiStatus multiStatus) {
        this.multiStatus = multiStatus;
    }

    private List<ResponsesBean> responses;
    private MultiStatus multiStatus;

    public static class ResponsesBean {
        String href;
        int status;

        String getlastmodified;
        String getcontentlength;
        String owner;
        String getcontenttype;
        String displayname;

        String getetag;

        public boolean isDir() {
            return "httpd/unix-directory".equals(getcontenttype);
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getGetlastmodified() {
            return getlastmodified;
        }

        public void setGetlastmodified(String getlastmodified) {
            this.getlastmodified = getlastmodified;
        }

        public String getGetcontentlength() {
            return getcontentlength;
        }

        public void setGetcontentlength(String getcontentlength) {
            this.getcontentlength = getcontentlength;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getGetcontenttype() {
            return getcontenttype;
        }

        public void setGetcontenttype(String getcontenttype) {
            this.getcontenttype = getcontenttype;
        }

        public String getDisplayname() {
            return displayname;
        }

        public void setDisplayname(String displayname) {
            this.displayname = displayname;
        }

        public String getGetetag() {
            return getetag;
        }

        public void setGetetag(String getetag) {
            this.getetag = getetag;
        }
    }
}
