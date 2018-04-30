package Net;

import Net.Service.LoginService;
import Net.Service.VcodeService;

import java.util.Locale;

/**
 * 提供服务的工厂类
 */
public class ServiceFactory {

    public RequestService getService(String type) {
        System.out.println(type);
        if (type == null) {
            return null;
        }
        //登录服务
        if (type.toUpperCase(Locale.ROOT).equals("LOGIN")) {
            return new LoginService();
        }
        //获取验证码服务
        if (type.toUpperCase(Locale.ROOT).equals("VCODE")) {
            return new VcodeService();
        }
        //查询快递信息服务
        if (type.toUpperCase(Locale.ROOT).equals("QUERY_EXPRESS_INFO")) {

        }
        return null;
    }
}
