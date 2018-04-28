package test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class TestTimer {
    public static void main(String[] args) {
        long ms = System.currentTimeMillis();
        Date time = new Date(ms + 10 * 1000L);
        Timer timer = new Timer();
        timer.schedule(new RemindTask(), time);
    }

}


class RemindTask extends TimerTask {

    public void run() {
        System.out.println("触发" + new Date());
    }
}
