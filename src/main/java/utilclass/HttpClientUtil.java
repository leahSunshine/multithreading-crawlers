package utilclass;

import java.io.IOException;

import javax.xml.ws.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * <!--HttpClient依赖-->
 * <dependency>
 * <groupId>org.apache.httpcomponents</groupId>
 * <artifactId>httpclient</artifactId>
 * <version>4.5.5</version>
 * </dependency>
 */

/**
 * @Author: hanyunfei
 * @Description: HttpClient工具类，用于发送请求
 * @Date: 2017/10/12
 * @Modified By: fanddong
 */
public class HttpClientUtil {

    public static String METHOD_GET = "Get";

    public static String METHOD_POST = "Post";


    /**
     * 发送Get请求
     *
     * @param url 请求地址例如：http://www.baidu.com
     * @return
     */
    public static String get(String url) {
    	CloseableHttpClient httpClient = HttpClients.createDefault();
        // get method
        HttpGet httpGet = new HttpGet(url);

        //response
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get response into String
        String temp = "";
        try {
            HttpEntity entity = response.getEntity();
            temp = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	try {
				httpClient.close();
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        return temp;
    }

    /**
     * 发送Post请求
     *
     * @param url  请求地址例如：http://www.baidu.com
     * @param json 请求参数（json形式）
     * @return
     */
    public static String post(String url, String json) {
    	CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        CloseableHttpResponse response=null;
        try {
            httpClient =  HttpClients.createDefault();
            httpPost = new HttpPost(url);

            StringEntity entity = new StringEntity(json, "utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            response= httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
        	try {
				httpClient.close();
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        return result;
    }

    public static void main(String[] args) {
        System.out.println(get("http://www.baidu.com"));

        System.out.println(post("http://www.baidu.com", ""));
    }

}
