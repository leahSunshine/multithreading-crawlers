
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class LoginTest {
	public static String getHtml(String url,String cookie) {
        String entity = null;
        int httpStatus;
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 设置超时处理(猜测setConnectTimeout是与网站建立HTTP链接的时间，setSocketTimeout是从网站获取数据的时间)
        RequestConfig config = RequestConfig.custom().setConnectTimeout(3000).
                setSocketTimeout(3000).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                "q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "no-cache");
        httpGet.setHeader("Connection", "keep-alive");
//        httpGet.setHeader("Host", "www.xicidaili.com");
        httpGet.setHeader("Pragma", "no-cache");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        httpGet.setHeader("Cookie",cookie);
        try {
            // 客户端执行httpGet方法，返回响应
            httpResponse = httpClient.execute(httpGet);

            // 得到服务响应状态码
            httpStatus = httpResponse.getStatusLine().getStatusCode();
            if (httpStatus == 200) {
                entity = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            } else {
                System.out.println("本机IP抓取xici代理网第一页IP返回状态码：" + httpStatus);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entity;
    }
public static void main(String[] args) {
	String url="http://www.dongao.com/cma/xxcma/lnzt/";
	String cookie="";
System.out.println(getHtml(url,cookie));	
}
}
