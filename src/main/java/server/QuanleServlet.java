package server;

import java.io.IOException;

/*
 * @author quanle
 * @date 2020/4/4 11:09 PM
*
 */
public class QuanleServlet extends HttpServlet {

    @Override
    public void doGet(Request request, Response response) {
        String content = "<h1>QuanleServlet get</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        String content = "<h1>QuanleServlet post</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void destory() throws Exception {

    }
}
