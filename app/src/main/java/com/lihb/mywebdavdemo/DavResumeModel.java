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
            if (property != null) responsesBean.getlastmodified = property.getValue().toString();

            DavProperty<?> property1 = propSet.get(DavPropertyName.GETCONTENTLENGTH);
            if (property1 != null) responsesBean.getcontentlength = property1.getValue().toString();
            DavProperty<?> property2 = propSet.get(DavPropertyName.XML_OWNER);
            if (property2 != null) responsesBean.owner = property2.getValue().toString();
            DavProperty<?> property3 = propSet.get(DavPropertyName.GETCONTENTTYPE);
            if (property3 != null) responsesBean.getcontenttype = property3.getValue().toString();
            DavProperty<?> property4 = propSet.get(DavPropertyName.DISPLAYNAME);
            if (property4 != null) responsesBean.displayname = property4.getValue().toString();
            DavProperty<?> property5 = propSet.get(DavPropertyName.GETETAG);
            if (property5 != null) responsesBean.getetag = property5.getValue().toString();


            list.add(responsesBean);
        }
        davResumeModel.responses = list;
        return davResumeModel;
    }

    private List<ResponsesBean> responses;
    private MultiStatus multiStatus;

    static class ResponsesBean {
        String href;
        int status;

        String getlastmodified;
        String getcontentlength;
        String owner;
        String getcontenttype;
        String displayname;

        String getetag;
    }
}
