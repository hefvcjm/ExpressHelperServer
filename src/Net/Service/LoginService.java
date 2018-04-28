package Net.Service;

import Net.RequestService;
import SmsService.VcodeManage;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginService implements RequestService {
    @Override
    public String handleRequest(String content) {
        try {
            JSONObject json = new JSONObject(content);
            String phone = json.getString("phone");
            String code = json.getString("code");
            if (VcodeManage.getInstance().check(phone, code)) {
                System.out.println("登录成功！");
                VcodeManage.getInstance().remove(phone);
                return "{\"msg\"" + ":" + "\"登录成功！\"}";
            }
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
        System.out.println("验证码无效！");
        return null;
    }
}
