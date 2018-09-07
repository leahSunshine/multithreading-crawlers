package com.leah.data.dongao;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.leah.ipproxy.IPMessage;
import com.leah.ipproxy.database.MyRedis;

public class DongaoTestHandeler {
	private Queue<TestDataModel> allTest=new ConcurrentLinkedQueue<>();
	public boolean addTestDate(String url,String ip ,String port){
		String html=UrlParseDongao.getHtml(url,ip,port);
		TestDataModel m=new TestDataModel();
		if(html ==null) {
			return false;
		}
		String nextUrl = UrlParseDongao.getTestModel(m, html);
		//2页的情况，第二页
		if(nextUrl!=null) {
			String html2=UrlParseDongao.getHtml(nextUrl,ip,port);
			if(html2 ==null) {
				return false;
			}
			UrlParseDongao.getTestModel(m, html2);
		}
		allTest.offer(m);
		return true;
	}
	public void startGetTestData() {
		DongaoDao dao=new DongaoDao();
		CountDownLatch sCountDownLatch=new CountDownLatch(10);
		ExecutorService  sc= Executors.newFixedThreadPool(10);
		ReentrantReadWriteLock lock=new ReentrantReadWriteLock();
		Queue<String> urls=dao.getAllUrls();
		MyRedis redis=new MyRedis();
		List<IPMessage> ipList = redis.getIpsToList();
		redis.close();
		for(int i=0;i<10;i++) {
			sc.execute(new Runnable() {
				
				@Override
				public void run() {
					String url=urls.poll();
					while (true) {
						lock.writeLock().lock();
						if(urls.isEmpty()) {
							System.out.println(Thread.currentThread().getName()+": Url队列已空");
							break;
						}
						IPMessage ipMessage = ipList.remove(0);
						lock.writeLock().unlock();
						Boolean isSuccsecc=addTestDate(url,ipMessage.getIPAddress(),ipMessage.getIPPort());
						if(!isSuccsecc) {
							lock.writeLock().lock();
							ipList.add(ipList.size(), ipMessage);
							lock.writeLock().unlock();
							System.out.println(Thread.currentThread().getName()+": Failed url:"+url+"ip:"+ipMessage.getIPAddress());
						}else {
							url=urls.poll();
							lock.writeLock().lock();
							ipList.add(0, ipMessage);
							lock.writeLock().unlock();
						}
					}
					sCountDownLatch.countDown();
				}
			});
		}
		try {
			sCountDownLatch.await();
			dao.insertTest(allTest);
			//TODO 数据处理进入这个表rel_dongaoTest
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
