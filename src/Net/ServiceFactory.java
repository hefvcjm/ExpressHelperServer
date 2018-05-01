package Net;

import Net.Service.*;

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
            return new ExpressInfoService();
        }
        //更新用户信息
        if (type.toUpperCase(Locale.ROOT).equals("UPDATE_USER")) {
            return new UpdateUserService();
        }
        if (type.toUpperCase(Locale.ROOT).equals("UPDATE_EXPRESS")) {
            return new UpdateExpressService();
        }
        return null;
    }
}
