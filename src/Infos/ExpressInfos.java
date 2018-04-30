package Infos;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;

/**
 * 用于管理物流信息的类
 */
public class ExpressInfos {
    private enum State {
        WAITING_FOR_PICKING_UP("待取货"), FINISHING_PICKING_UP("已领取"), DELAY_PICKING_UP("已延期"), REFUSE_PICKING_UP("已拒收"), EXPIRE_PICKING_UP("已过期");
        private String name;

        private State(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private String company = "";//快递公司
    private String location = "";//取货地点
    private String code = "";//取货码
    private String deadline = "";//截止时间
    private String state = "";//状态

    public ExpressInfos(String json) {
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String toString() {
        String str = "{\"company\":\"" + getCompany() + "\"," + "\"location\":\"" + getLocation() + "\"," + "\"code\":\"" + getCode() + "\"," + "\"deadline\":\"" + getDeadline() + "\"," + "\"state\":\"" + getState() + "\"}";
        ;
        return str;
    }

    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("company", "12345678901");
        json.put("location", "test");
        json.put("code", "123@email.com");
        json.put("deadline", "123456");
        json.put("state", State.DELAY_PICKING_UP.getName());
        ExpressInfos expressInfos = new ExpressInfos(json.toString());
        System.out.println(expressInfos.toString());
    }

}
