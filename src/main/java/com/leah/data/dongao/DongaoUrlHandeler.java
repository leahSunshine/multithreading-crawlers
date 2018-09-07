package com.leah.data.dongao;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.leah.ipproxy.IPMessage;
import com.leah.ipproxy.database.MyRedis;

public class DongaoUrlHandeler{
	private static Queue<String> allUrls=new ConcurrentLinkedQueue<>();;
	public static boolean addUrls(String url,String ip ,String port) {
		String html=UrlParseDongao.getHtml(url,ip,port);
		if(html==null) {
			return false;
		}
		Queue<String> urls = UrlParseDongao.getUrls(html);
//		urls.stream().forEach(System.out::println);
		allUrls.addAll(urls);
		System.out.println(Thread.currentThread().getName()+"success! 当前获取的url数量:"+urls.size()+"合并之后的总数为："+allUrls);
		return true;
	}
	
	public void startGetUrl() {
		ExecutorService  sc= Executors.newFixedThreadPool(10);
		ReentrantReadWriteLock lock=new ReentrantReadWriteLock();
		Queue<String> urls=new ConcurrentLinkedQueue<>();
		String url=UrlParseDongao.DONG_AO_URI+"/cma/xxcma/cmast/index";
		urls.offer(url+".shtml");
		for(int i=2;i<15;i++) {
			urls.offer(url+"_"+i+".shtml");
		}
		MyRedis redis=new MyRedis();
		List<IPMessage> ipList = redis.getIpsToList();
		redis.close();
		for(int i=0;i<10;i++) {
			sc.execute(new Runnable() {
				@Override
				public void run() {
					String url=urls.poll();
					while(true) {
						lock.writeLock().lock();
						if(urls.isEmpty()) {
							System.out.println(Thread.currentThread().getName()+": Url队列已空");
							break;
						}
						IPMessage ipMessage = ipList.remove(0);
						lock.writeLock().unlock();
						
						Boolean isSuccsecc=addUrls(url,ipMessage.getIPAddress(),ipMessage.getIPPort());
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
				}
			});
		}
		sc.shutdown();
		while(true) {
			if(sc.isTerminated()) {
				DongaoDao dao=new DongaoDao();
				dao.insertUrl(allUrls);
				System.out.println("获取url结束，总共url:"+allUrls.size());
				break;
			}
		}
				
	}
	public static void main(String[] args) {
		new  DongaoUrlHandeler().startGetUrl();
	}
}
