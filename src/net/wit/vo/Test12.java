package net.wit.vo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by app_000 on 2015/10/26.
 */
public class Test12 {
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月 yyyy年");
        System.out.println(sdf.format(new Date()));
        Calendar calendar = Calendar.getInstance();
        int nowYear =  calendar.get(Calendar.YEAR);
        int nowMonth =  calendar.get(Calendar.MONTH)+1;

        calendar.set(Calendar.YEAR,2015);
        calendar.set(Calendar.MONTH,8);
        int beforeYear =  calendar.get(Calendar.YEAR);
        int beforeMonth =  calendar.get(Calendar.MONTH)+1;
    }
}
