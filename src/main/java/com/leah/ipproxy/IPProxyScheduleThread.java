package com.leah.ipproxy;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.leah.ipproxy.database.MyRedis;



public class IPProxyScheduleThread implements Runnable{
	private UrlParseHandle parseHandle;
	private Queue<String> urls;
	private List<IPMessage> ipMessages;
	public void startCrawlerByTime(UrlParseHandle parseHandle1,Queue<String> urls1,long  period) {
		if(parseHandle==null || urls==null || urls.isEmpty() || period<=0) {
			return;
		}
		System.out.println("当前线程：" + Thread.currentThread().getName() + ", 开始更新IP代理池");
        // 存放爬取下来的ip信息
		parseHandle=parseHandle1;
		urls=urls1;
        ipMessages = new LinkedList<>();
        
        while(ipMessages.isEmpty() && !urls.isEmpty()) {
        	String html = MyHttpResponse.getHtml(urls.poll());

            // 将html解析成DOM结构
            Document document = Jsoup.parse(html);
            // 首先使用本机ip爬取xici代理网第一页
            ipMessages=parseHandle.parseDocument(document);
            // 对得到的IP进行筛选，将IP速度在三秒以内的并且类型是https的留下，其余删除
            ipMessages = IPFilter.Filter(ipMessages);
        }
        System.out.println("==========================================================");
        ipMessages.parallelStream().forEach(System.out::println);
        System.out.println("==========================================================");
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
        	scheduledThreadPool.scheduleAtFixedRate(new Thread(this), 1, period, TimeUnit.SECONDS);
	}


	@Override
	public void run() {
		 ExecutorService scheduledThreadPool = Executors.newFixedThreadPool(10);
	        IPProxyThread thread=new IPProxyThread(urls, ipMessages, parseHandle);
	        for(int i = 0; i < 20; i++){
	        	scheduledThreadPool.execute(thread);
	        }
		  try {
				Thread.sleep(1000000);
				scheduledThreadPool.shutdown();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        while(true){
	            if(scheduledThreadPool.isTerminated()){
	            	//TODO 取出redis中所有的ipmessage加入到ipMessages，对ipmessages按照ip去重
	            	MyRedis redis=new MyRedis();
	            	List<IPMessage> ipByList = redis.getIpsToList();
	            	redis.delKeys("ip-proxy-pool");
	            	ipMessages.addAll(ipByList);
	            	ipMessages= ipMessages.parallelStream().distinct().collect(Collectors.toList());
	            	redis.setIPToList(ipMessages);
	                break;
	            }
	        }
	        System.out.println("当前线程：" + Thread.currentThread().getName() + ", 更新IP代理池完毕！");
	}
}
