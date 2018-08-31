package utilclass;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: fanddong
 * @Description:
 * @Date: Create in 16:34 2018/7/18
 * @Modified By:
 */
public class StringUtil {

    /**
     * 根据flag标识进行拼接字符串
     *
     * @param list<Object> T仅代表基本数据类型
     * @param flag
     * @return
     */
    public static String strJoinByFlag(List<Object> list, String flag) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size() - 1; i++) {
            buffer.append(list.get(i).toString() + flag);
        }
        buffer.append(list.get(list.size() - 1));
        return buffer.toString();
    }

    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("id_" + i);
        }
        System.out.println(list.size());
        System.out.println(strJoinByFlag(list, "-"));
    }
}
