package utilclass;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @Author: fanddong
 * @Description: 对url进行编码
 * @Date: Create in 11:39 2018/6/22
 * @Modified By:
 */
public class EncodeUtils {
    private static final String DEFAULT_URL_ENCODING = "UTF-8";

    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     */
    public static String urlDecode(String input) {
        try {
            return URLDecoder.decode(input, DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }
    }

    public static void main(String[] args) {
        String url = "http://www.baidu.com";
        String encode = urlEncode(url);
        System.out.println(url);
        System.out.println(encode);
        System.out.println(urlDecode(encode));
    }

}
