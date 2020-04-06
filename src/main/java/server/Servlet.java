package server;

/**
 * @author quanle
 * @date 2020/4/4 11:06 PM
 */
public interface Servlet {
    void init() throws Exception;
    void destory() throws Exception;
    void  service(Request request,Response response) throws Exception;
}
