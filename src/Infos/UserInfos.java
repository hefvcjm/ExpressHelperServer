package Infos;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;

/**
 * 用于管理用户信息的类
 */
public class UserInfos {

    private String phone = "";//手机号码
    private String name = "";//用户名
    private String email = "";//邮箱
    private String password = "";//密码

    public UserInfos(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Set<String> keys = jsonObject.keySet();
            for (String key : keys) {
                Class<?> userInfos = this.getClass();
                String methodName = "set" + (key.toLowerCase().charAt(0) + "").toUpperCase(Locale.ROOT) + key.toLowerCase().substring(1);
                System.out.println("methodName:" + methodName);
                Method method = userInfos.getMethod(methodName, String.class);
                method.invoke(this, jsonObject.getString(key));
            }
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        String str = "{\"phone\":\"" + getPhone() + "\"," + "\"name\":\"" + getName() + "\"," + "\"email\":\"" + getEmail() + "\"," + "\"password\":\"" + getPassword() + "\"}";
        return str;
    }

    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("phone", "12345678901");
        json.put("name", "test");
        json.put("email", "123@email.com");
        json.put("password", "123456");
        UserInfos userInfos = new UserInfos(json.toString());
        System.out.println(userInfos.toString());
        //change for testing git remote
    }
}
