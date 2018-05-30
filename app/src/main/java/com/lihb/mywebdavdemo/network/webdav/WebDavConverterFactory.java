package com.lihb.mywebdavdemo.network.webdav;

import com.lihb.mywebdavdemo.DavResumeModel;
import com.lihb.mywebdavdemo.network.webdav.xml.DomUtil;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.xml.parsers.ParserConfigurationException;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class WebDavConverterFactory extends Converter.Factory {

    public static WebDavConverterFactory create() {
        return new WebDavConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (type == MultiStatus.class) {
            return new WebDavConverter();
        }else if (type == DavResumeModel.class) {
            return new DavResumeModelConverter();
        } else {
            return null;
        }
    }

    private static class WebDavConverter implements Converter<ResponseBody, MultiStatus> {
        WebDavConverter() {
        }

        //在这个方法中处理response
        @Override
        public MultiStatus convert(ResponseBody value) throws IOException {
            Document document = null;
            try {
                document = DomUtil.parseDocument(value.byteStream());
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            MultiStatus xml = com.lihb.mywebdavdemo.network.webdav.MultiStatus.createFromXml(document.getDocumentElement());
            return xml;

        }
    }

    private static class DavResumeModelConverter implements Converter<ResponseBody, DavResumeModel> {
        DavResumeModelConverter() {
        }

        //在这个方法中处理response
        @Override
        public DavResumeModel convert(ResponseBody value) throws IOException {
            DavResumeModel davResumeModel = null;
            try {
                Document document = DomUtil.parseDocument(value.byteStream());
                MultiStatus xml = MultiStatus.createFromXml(document.getDocumentElement());
                davResumeModel = DavResumeModel.create(xml);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            return davResumeModel;

        }
    }
}
