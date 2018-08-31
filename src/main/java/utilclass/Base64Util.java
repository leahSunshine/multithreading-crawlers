package utilclass;

import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;

/**
 * Base64加密解密工具类
 */
public class Base64Util {
    //定义加密编码格式
    private static final String charset = "GB2312";

    /**
     * Base64解密
     *
     * @param data
     * @return
     * @author fanddong
     */
    public static String decode(String data) {
        try {
            if (null == data) {
                return null;
            }

            return new String(Base64.decodeBase64(data.getBytes(charset)), charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Base64加密
     *
     * @param data
     * @return
     * @author fanddong
     */
    public static String encode(String data) {
        try {
            if (null == data) {
                return null;
            }
            return new String(Base64.encodeBase64(data.getBytes(charset)), charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        String text = "hello";
        String encode = encode(text);
        System.out.println("明文：" + text);
        System.out.println("加密后：" + encode);
        System.out.println("解密后：" + decode(encode));
    }

}