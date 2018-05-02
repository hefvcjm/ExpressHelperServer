package Net;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/**
 * 解析请求
 */
public interface RequestService {
    /**
     * @param content 请求传入的数据
     * @return 响应请求的数据
     */
    Object handleRequest(String content);

    /**
     * 将相应数据添加到HttpResponse对象中
     *
     * @param rsp      响应请求的数据
     * @param response HttpResponse对象
     */
    void response(Object rsp, final HttpResponse response, final HttpContext context);
}
