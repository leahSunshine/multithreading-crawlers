package com.leah.ipproxy;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;



public class IPProxyThread implements Runnable {
	private volatile Queue<String> urls ;
	private volatile List<IPMessage> ipMessages;
	private  UrlParseHandle parseHandle ;
	private Object taskLock=new Object();
	private IPMessage startIpMessages;
	 // 创建供上述变量使用的读写锁
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	public IPProxyThread(Queue<String> url,List<IPMessage> ipMessage,UrlParseHandle parseHandle) {
		this.urls=url;
		this.ipMessages=ipMessage;
		this.parseHandle=parseHandle;
		this.startIpMessages=ipMessages.get(0);
	}
	@Override
	public void run() {
		System.out.println("================="+Thread.currentThread().getName()+"start=========================!");
		saveIP();
		System.out.println("================="+Thread.currentThread().getName()+"end=========================!");
	}
	private void saveIP() {
        readWriteLock.writeLock().lock();
         IPMessage ipMessage = ipMessages.remove(0);
        String ipAddress = ipMessage.getIPAddress();
        String ipPort = ipMessage.getIPPort();
        readWriteLock.writeLock().unlock();

        while (true) {
            // 每个线程先将自己抓取下来的ip保存下来并进行过滤
            String url;

            // 任务队列是共享变量，对其的读写必须进行正确的同步
            synchronized (taskLock) {
                if (urls.isEmpty()) {
                	ipMessages.add(ipMessages.size(), ipMessage);//当前的ip加入到ipmessage中
                    System.err.println("当前线程：" + Thread.currentThread().getName() + ", 发现任务队列已空");
                    break;
                }
                url = urls.poll();
            }
           
            String html = MyHttpResponse.getHtml(url, ipAddress, ipPort);

            synchronized (taskLock) {
            	//每次取ip的时候从第一个取，如果第一个解析当前的网址不成功，那么将当前的ip放入最后一个，从新获取第一个；如果解析成功，继续将ip添加为第一个。
	            if(html==null) {
	            	urls.offer(url);
	            	System.err.println(Thread.currentThread().getName()+"==========url:"+url+""+"IPAddress"+ipMessage.getIPAddress()+"ipPort"+ipMessage.getIPPort());
//	            	if(!ipMessages.isEmpty()) {ipMessages不会为空，不用判空。
            		ipMessages.add(ipMessages.size(), ipMessage);//将当前的ip添加到最后。
            		ipMessage = ipMessages.remove(0);//取出第一个，继续解析。
	                 ipAddress = ipMessage.getIPAddress();
	                 ipPort = ipMessage.getIPPort();
//	            	}
		            //如果ipMessage就是startIpMessages,说明ipMessages集合已经轮询完毕,退出循环。当前的ipMessage添加到最后
	            	/*if(startIpMessages==ipMessage) {
	            		ipMessages.add(ipMessages.size(), ipMessage);
	            		System.err.println("当前ipMessage已经轮询使用完毕，退出线程ipMessage的大小:"+ipMessages.size());
	            		break;
	            	}*/
	            	
	            	continue;
	            }else {
	            	ipMessages.add(0, ipMessage);//能够使用的加入第一个
	            }
            }
            Document document = Jsoup.parse(html);
            if(document==null) {
            	System.err.println(Thread.currentThread().getName()+"====>请检查url:"+url+"是否返回正确的html。。。");
        		break;
            }
            List<IPMessage> ipMessages1=parseHandle.parseDocument(document);
            // 如果ip代理池里面的ip不能用，或本页抓取失败，则切换下一个IP对本页进行重新抓取
            if(ipMessages1.isEmpty()) {
            	System.err.println(Thread.currentThread().getName()+"====>请检查url:"+url+"的html的解析规则是否正确。。。");
            	break;
            }
            // 对ip重新进行过滤，只要速度在三秒以内的并且类型为HTTPS的
            ipMessages1 = IPFilter.Filter(ipMessages1);

            // 将质量合格的ip合并到共享变量ipMessages中，进行合并的时候保证原子性
            System.out.println("当前线程：" + Thread.currentThread().getName() + ", 已进入合并区, " +
                    "待合并大小 ipMessages1：" + ipMessages1.size());
            readWriteLock.writeLock().lock();
            ipMessages.addAll(ipMessages1);
            System.out.println("当前线程：" + Thread.currentThread().getName() + ", 已成功合并, " +
                    "合并后ipMessage大小：" + ipMessages.size());
            readWriteLock.writeLock().unlock();
        }
    }
	public static void main(String[] args) {
		//http://www.xicidaili.com/nn/8IPAddress222.248.243.33ipPort80
		String html = MyHttpResponse.getHtml("http://www.xicidaili.com/nn/2", "222.248.243.33", "80");
		System.out.println(html);
		
	}
}
