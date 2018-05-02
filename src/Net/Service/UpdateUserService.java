package Net.Service;

import Net.RequestService;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/**
 * 更新用户信息，比如状态、密码等
 */
public class UpdateUserService implements RequestService {
    @Override
    public Object handleRequest(String content) {
        return null;
    }

    @Override
    public void response(Object rsp, HttpResponse response, HttpContext context) {

    }
}
