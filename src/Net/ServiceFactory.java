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
        if (type.toUpperCase(Locale.ROOT).equals("VCODE")) {
            return new VcodeService();
        }
        return null;
    }
}
