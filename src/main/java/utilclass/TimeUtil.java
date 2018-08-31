package utilclass;

import java.text.SimpleDateFormat;
import java.util.*;

public class TimeUtil {

    /**
     * @param type 时间类型
     * 如：yyyy-MM-dd HH:mm:ss (默认)
     * @return 格式化后的当前时间
     */
    public static String getTime(String type) {
        if (type == null)
            type = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(type);// 设置日期格式
        return df.format(new Date());
    }

    /**
     * 获取当前年份
     *
     * @return
     */
    public static Integer getCurrentYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        return Integer.parseInt(sdf.format(date));
    }

    /**
     * 返回10位时间戳
     *
     * @return
     */
    public static String getTimeStamp() {
        return String.valueOf(new Date().getTime() / 1000);
    }

    /**
     * 将时间戳转为时间
     *
     * @param s 10位时间戳
     * @return
     */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt * 1000);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 获取最近的几年
     *
     * @param number 3
     * @return [2018, 2017, 2016, 2015]
     */
    public static List<Integer> getNearYear(Integer number) {
        List<Integer> resultList = new ArrayList<Integer>();
        Calendar cal = Calendar.getInstance();
        //近两年
        int nowYear = cal.get(Calendar.YEAR);
        for (int i = 0; i <= number; i++) {
            resultList.add(nowYear - i);
        }
        return resultList;
    }


    public static void main(String[] args) {
        System.out.println(stampToDate(getTimeStamp()));
        System.out.println(getTimeStamp());
        System.out.println(getCurrentYear());
        System.out.println(getTimeStamp());
        System.out.println(getNearYear(5));
        System.out.println(getTime("yyyy-MM-dd"));
    }


}
