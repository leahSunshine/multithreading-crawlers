package com.leah.data.dongao;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class UrlParseDongao {
	public static String DONG_AO_URI="http://www.dongao.com";
	public static String getHtml(String url, String ip, String port) {
        String entity = null;
        int httpStatus;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;

        // 设置代理访问和超时处理
        HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(1000).
                setSocketTimeout(1000).build();
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

        try {
            // 客户端执行httpGet方法，返回响应
            httpResponse = httpClient.execute(httpGet);

            // 得到服务响应状态码
            httpStatus = httpResponse.getStatusLine().getStatusCode();
            if (httpStatus == 200) {
                entity = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                System.out.println("当前线程：" + Thread.currentThread().getName() + ", 使用的代理IP：" +
                        ip + ":" + port + ", 成功抓取xici代理网：" + url);
            } else {
                System.out.println("当前线程：" + Thread.currentThread().getName() + ", 使用的代理IP：" +
                        ip + ":" + port + ", 抓取xici代理网：" + url + ", 返回状态码：" + httpStatus);
            }
        } catch (IOException e) {
            entity = null;
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
	public static String getHtml(String url) {
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
	public static Queue<String> getUrls(String html){
		Queue<String> urls = new LinkedList<>();
		 Document document = Jsoup.parse(html);
		 if(document==null) {
			 System.err.println(Thread.currentThread().getName()+"====>html为空。。");
			 return urls;
		 }
		 Elements a = document.select("ul[class=cma_list border_b]").select("li").select("a");
		a.parallelStream().forEach(o->
		urls.add(o.attr("href")));
		return urls;
	}
	public static String getTestModel(TestDataModel m,String html){
		 Document document = Jsoup.parse(html);
		 if(document==null) {
			 System.err.println(Thread.currentThread().getName()+"====>html为空。。");
			 return null;
		 }
		 Elements title = document.select("head").select("title");
		 if(title!=null && m.getTestType()==null) {
			 String titleCon=title.text();
			 int posi=titleCon.indexOf("：");
			String part= titleCon.substring(titleCon.indexOf(" ")+1,posi);
			String type= titleCon.substring(posi,titleCon.length());
			 m.setPart(Integer.valueOf(part));
			 m.setContentType(type);
		 }
		 Elements content = document.select("div[id=fontzoom]");
//		 m.setContent(m.getContent()+content.text()); //text()获取的是标签里面的内容
		 m.setContent(m.getContent()+content.toString()); //获取的所有内容，包括标签
		 Elements nextPage = document.select("div[class=page_number tc]").select("a[href~=^/c]");
		 if(nextPage!=null) {
			 return DONG_AO_URI+nextPage.attr("href");
		 }
		 return null;
	}
}
