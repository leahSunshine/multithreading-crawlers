package com.leah.ipproxy;

import java.util.Date;
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
	private static List<IPMessage> ipMessages;
	public void startCrawlerByTime(UrlParseHandle parseHandle1,Queue<String> urls1,long  period) {
		if(parseHandle1==null || urls1==null || urls1.isEmpty() || period<=0) {
			return;
		}
		if(ipMessages!=null) {
			System.err.println("已有线程在运行，请求任务中止。。。。");
		}
		System.out.println("当前线程：" + Thread.currentThread().getName() + ", 开始更新IP代理池");
        // 存放爬取下来的ip信息
		parseHandle=parseHandle1;
		urls=urls1;
        ipMessages = new LinkedList<>();
        String url=urls.poll();
    	String html = MyHttpResponse.getHtml(url);
    	if(html==null) {
    		System.err.println("本机获取第一个代理页面失败,请检查网络连接是否正常或者url是否能够访问正确.");
    		return;
    	}
        // 将html解析成DOM结构
        Document document = Jsoup.parse(html);
        if(document==null) {
        	System.err.println(Thread.currentThread().getName()+"====>请检查url:"+url+"是否返回正确的html。。。");
    		return;
        }
        // 首先使用本机ip爬取xici代理网第一页
        ipMessages=parseHandle.parseDocument(document);
        // 对得到的IP进行筛选，将IP速度在三秒以内的并且类型是https的留下，其余删除
        if(ipMessages.isEmpty()) {
        	System.err.println(Thread.currentThread().getName()+"====>请检查url:"+url+"的html的解析规则是否正确。。。");
    		return;
        }
        ipMessages = IPFilter.Filter(ipMessages);
        System.out.println("==========================================================");
        ipMessages.parallelStream().forEach(System.out::println);
        System.out.println("==========================================================");
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
        scheduledThreadPool.scheduleAtFixedRate(new Thread(this), 1, period, TimeUnit.SECONDS);
	}


	@Override
	public void run() {
		ExecutorService  scheduledThreadPool = Executors.newFixedThreadPool(10);
	        IPProxyThread thread=new IPProxyThread(urls, ipMessages, parseHandle);
	        for(int i = 0; i < 10; i++){
	        	scheduledThreadPool.execute(thread);
	        }
			scheduledThreadPool.shutdown();
	        while(true){
	            if(scheduledThreadPool.isTerminated()){
	            	//TODO 取出redis中所有的ipmessage加入到ipMessages，对ipmessages按照ip去重
	            	MyRedis redis=new MyRedis();
	            	System.out.println("===================================="+ipMessages.size());
	            	List<IPMessage> ipByList = redis.getIpsToList();
	            	if(ipByList!=null && !ipByList.isEmpty()) {
	            		redis.delKeys("ip-proxy-pool");
	            		ipMessages.addAll(ipByList);
	            	}
	            	ipMessages= ipMessages.parallelStream().distinct().collect(Collectors.toList());
	            	redis.setIPToList(ipMessages);
	            	System.out.println("===================================="+ipMessages.size());
	            	redis.close();
	                break;
	            }
	        }
	        System.out.println("当前线程：" + Thread.currentThread().getName() + ", 更新IP代理池完毕！");
	        System.out.println(new Date());
	}
}
