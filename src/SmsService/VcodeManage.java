package SmsService;

import java.util.*;

/**
 * 管理验证码
 */
public class VcodeManage {
    //保存登录手机号-验证码对
    private static Map<String, String> codeMap;
    //手机号与定时器对
    private static Map<String, Timer> timerMap;
    //定时时长
    private final static long TIME_LENGTH = 60 * 1000L;

    private volatile static VcodeManage instance;

    private VcodeManage() {
        codeMap = new HashMap<>();
        timerMap = new HashMap<>();
    }

    /**
     * 单例模式实例化对象
     *
     * @return
     */
    public static VcodeManage getInstance() {
        if (instance == null) {
            synchronized (VcodeManage.class) {
                if (instance == null) {
                    instance = new VcodeManage();
                }
            }
        }
        return instance;
    }

    /**
     * 向codeMap添加phone-code对
     *
     * @param phone 手机号码
     * @param code  验证码
     */
    public synchronized void add(String phone, String code) {
        if (codeMap.containsKey(phone)) {
            return;
        }
        codeMap.put(phone, code);
        setTimingRemove(phone);//定时删除
    }

    /**
     * 移除codeMap中的phone对应的键值对
     *
     * @param phone 手机号码
     */
    public synchronized void remove(String phone) {
        if (!codeMap.containsKey(phone)) {
            return;
        }
        cancelTimer(phone);
        codeMap.remove(phone);
    }

    /**
     * 检查codeMap中是否存在给定的键值对
     *
     * @param phone 手机号码
     * @param code  验证码
     * @return true，如果存在，否则为false
     */
    public synchronized boolean check(String phone, String code) {
        if (!codeMap.containsKey(phone)) {
            return false;
        }
        if (codeMap.get(phone).equals(code)) {
            return true;
        }
        return false;
    }

    /**
     * 获取指定号码的验证码
     *
     * @param phone 手机号码
     * @return 对应的验证码，如果没有则返回null
     */
    public String getCode(String phone) {
        if (!codeMap.containsKey(phone)) {
            return null;
        }
        return codeMap.get(phone);
    }

    /**
     * 设置定时删除codeMap中的键值对
     *
     * @param phone 手机号码
     */
    private synchronized void setTimingRemove(String phone) {
        if (!codeMap.containsKey(phone)) {
            return;
        }
        long ms = System.currentTimeMillis();
        Date time = new Date(ms + TIME_LENGTH);
        Timer timer = new Timer();
        if (!timerMap.containsKey(phone)) {
            timerMap.put(phone, timer);
        } else {
            timerMap.replace(phone, timer);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!codeMap.containsKey(phone)) {
                    return;
                }
                codeMap.remove(phone);
            }
        }, time);
    }

    /**
     * 删除phone关联的schedule任务
     *
     * @param phone 手机号码
     */
    private synchronized void cancelTimer(String phone) {
        if (!timerMap.containsKey(phone)) {
            return;
        }
        timerMap.get(phone).cancel();
        timerMap.remove(phone);
    }
}
