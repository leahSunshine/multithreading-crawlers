package com.leah.ipproxy;

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
	 // 创建供上述变量使用的读写锁
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	public IPProxyThread(Queue<String> url,List<IPMessage> ipMessage,UrlParseHandle parseHandle) {
		this.urls=url;
		this.ipMessages=ipMessage;
		this.parseHandle=parseHandle;
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
            /**
             * 随机挑选代理IP(本步骤由于其他线程有可能在位置确定之后对ipMessages数量进行
             * 增加，虽说不会改变已经选择的ip代理的位置，但合情合理还是在对共享变量进行读写的时候要保证
             * 其原子性，否则极易发生脏读)
             */
            // 每个线程先将自己抓取下来的ip保存下来并进行过滤
            String url;

            // 任务队列是共享变量，对其的读写必须进行正确的同步
            synchronized (taskLock) {
                if (urls.isEmpty()) {
                    System.out.println("当前线程：" + Thread.currentThread().getName() + ", 发现任务队列已空");
                    break;
                }
                url = urls.poll();
            }
           
            String html = MyHttpResponse.getHtml(url, ipAddress, ipPort);

            // 将html解析成DOM结构
            if(html==null) {
            	readWriteLock.writeLock().lock();
            	ipMessage = ipMessages.remove(0);
                 ipAddress = ipMessage.getIPAddress();
                 ipPort = ipMessage.getIPPort();
                readWriteLock.writeLock().unlock();

                synchronized (taskLock) {
                    urls.offer(url);
                }
                continue;
            }
            Document document = Jsoup.parse(html);
            // 首先使用本机ip爬取xici代理网第一页
            List<IPMessage> ipMessages1=parseHandle.parseDocument(document);
            System.out.println("==================="+ipMessages1);
            // 如果ip代理池里面的ip不能用，或本页抓取失败，则切换下一个IP对本页进行重新抓取
            if (ipMessages1==null || ipMessages1.isEmpty()) {
                // 当抓取失败的时候重新拿取代理ip
                
            }
            System.err.println("==================="+ipMessages1);
            // 对ip重新进行过滤，只要速度在三秒以内的并且类型为HTTPS的
            ipMessages1 = IPFilter.Filter(ipMessages1);

            // 将质量合格的ip合并到共享变量ipMessages中，进行合并的时候保证原子性
            readWriteLock.writeLock().lock();
            System.out.println("当前线程：" + Thread.currentThread().getName() + ", 已进入合并区, " +
                    "待合并大小 ipMessages1：" + ipMessages1.size());
            ipMessages.addAll(ipMessages1);
            System.out.println("当前线程：" + Thread.currentThread().getName() + ", 已成功合并, " +
                    "合并后ipMessage大小：" + ipMessages.size());
            readWriteLock.writeLock().unlock();
        }
    }
}
