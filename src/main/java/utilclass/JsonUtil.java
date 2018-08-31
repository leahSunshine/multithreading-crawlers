package utilclass;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;

/**
 * @Author: hanyunfei
 * @Description: fastjson 工具类
 * @Date: 2017/10/12
 * @Modified By:
 */
public class JsonUtil {
    /**
     * 对象转Json
     *
     * @param object
     * @return 转化后的Json字符串
     */
    public static String objectToJson(Object object) {
        String string = null;
        try {
            string = JSON.toJSONString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * Json字符串转Map
     *
     * @param json
     * @return 转化后的Map
     */
    public static Map<String, Object> jsonToMap(String json) {
        return JSON.parseObject(json, Map.class);
    }

    /**
     * json转对象
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonToBean(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    /**
     * json转对象的集合
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }

    public static void main(String[] args) {
       

    }
}
