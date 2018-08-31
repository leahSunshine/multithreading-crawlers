package utilclass;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Aes加密、解密工具类
 */
public class AesUtil {

    //默认秘钥（秘钥长度必须是16位）
    private static String password = "221287f3a020f001";

    public static void main(String[] args) throws Exception {
        String content = "123454324";
        // 加密  
        System.out.println("加密前：" + content);

        String tt4 = encrypt2Str(content);
        System.out.println("加密后：" + new String(tt4));
        // 解密  
        String d = decrypt2Str(tt4);
        System.out.println("解密后：" + d);
    }

    /**
     * 使用默认密钥加密
     *
     * @param 明文
     * @return 密文
     */
    public static String encrypt2Str(String content) {
        try {
            return Encrypt(content, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用默认密钥解密
     *
     * @param 密文
     * @return 明文
     */
    public static String decrypt2Str(String content) {

        return Decrypt(content, password);
    }

    /**
     * 使用参数中的密钥加密
     *
     * @param 明文
     * @param 密钥
     * @return 密文
     */
    public static String Encrypt(String sSrc, String sKey) {
        try {
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

            return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用参数中的密钥解密
     *
     * @param 密文
     * @param 密钥
     * @return 明文
     */
    public static String Decrypt(String sSrc, String sKey) {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }
}