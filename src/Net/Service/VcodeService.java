package Net.Service;

import Net.RequestService;
import SmsService.SmsService;
import SmsService.VcodeManage;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

public class VcodeService implements RequestService {
    @Override
    public String handleRequest(String content) {
        try {
            JSONObject json = new JSONObject(content);
            String phone = json.getString("phone");
            String code = String.format("%06d", new Random().nextInt(999999));
            try {
                SendSmsResponse response = SmsService.sendSms(phone, code);
                String rspCode = response.getCode();
                System.out.println("rspCode:" + rspCode);
                if (rspCode.toUpperCase(Locale.ROOT).equals("OK")) {
                    System.out.println("发送验证码成功！");
                    VcodeManage.getInstance().add(phone, code);
                    return "{\"msg\"" + ":" + "\"发送验证码成功！\"}";
                }
            } catch (ClientException e) {
                e.printStackTrace();
                return null;
            }
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
        System.out.println("验证码无效！");
        return null;
    }
}
