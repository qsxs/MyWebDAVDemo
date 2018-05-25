public class BaseModel {
    public static final String METHODS = "OPTIONS, GET, HEAD, POST, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK";

    private ResponsesBean response;

    class ResponsesBean{
        String href;
        PropstatBean propstat;
    }

    class PropstatBean{
        PropBean prop;
        String status;
    }

    class PropBean{
        String getlastmodified;
        String getcontentlength;
        String owner;
        String getcontenttype;
        String displayname;

        String getetag;
    }
}
