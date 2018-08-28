package com.leah.ipproxy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class IPProxyScheduleThread {
	public static void startCrawlerByTime(UrlParseHandle parseHandle,Queue<String> urls,long  period) {
		if(parseHandle==null || urls==null || urls.isEmpty() || period<=0) {
			return;
		}
		System.out.println("当前线程：" + Thread.currentThread().getName() + ", 开始更新IP代理池");
        // 存放爬取下来的ip信息
        List<IPMessage> ipMessages = new LinkedList<>();
        String html = MyHttpResponse.getHtml(urls.poll());

        // 将html解析成DOM结构
        Document document = Jsoup.parse(html);
        // 首先使用本机ip爬取xici代理网第一页
        ipMessages=parseHandle.parseDocument(document);
        // 对得到的IP进行筛选，将IP速度在三秒以内的并且类型是https的留下，其余删除
        ipMessages = IPFilter.Filter(ipMessages);
        for (IPMessage ipMessage : ipMessages) {
            System.out.println(ipMessage.toString());
        }
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
        IPProxyThread thread=new IPProxyThread(urls, ipMessages, parseHandle);
        for(int i = 0; i < 10; i++){
        	scheduledThreadPool.scheduleAtFixedRate(new Thread(thread), 0, period, TimeUnit.SECONDS);
        }
        
        scheduledThreadPool.shutdown();

        while(true){
            if(scheduledThreadPool.isTerminated()){
            	//TODO 取出redis中所有的ipmessage加入到ipMessages，对ipmessages按照ip去重
                break;
            }
        }
        System.out.println("当前线程：" + Thread.currentThread().getName() + ", 更新IP代理池完毕！");
	}
}
