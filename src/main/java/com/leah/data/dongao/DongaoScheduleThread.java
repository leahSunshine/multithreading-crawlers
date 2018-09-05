package com.leah.data.dongao;

import java.util.Queue;

public class DongaoScheduleThread implements Runnable {
	private static Queue<String> allUrls;
	public static void addUrls(String url,String ip ,String port) {
		String html=UrlParseDongao.getHtml(url,ip,port);
		Queue<String> urls = UrlParseDongao.getUrls(html);
//		urls.stream().forEach(System.out::println);
		allUrls.addAll(urls);
	}
	public static void addTestDate(String url,String ip ,String port){
		String html=UrlParseDongao.getHtml(url,ip,port);
		TestDataModel m=new TestDataModel();
		String nextUrl = UrlParseDongao.getTestModel(m, html);
		if(nextUrl!=null) {
			String html2=UrlParseDongao.getHtml(nextUrl,ip,port);
			UrlParseDongao.getTestModel(m, html2);
		}
	}
	@Override
	public void run() {

	}
	public static void main(String[] args) {
		String url="http://www.dongao.com/c/2017-09-14/794547.shtml";
		String html=UrlParseDongao.getHtml(url);
		TestDataModel m=new TestDataModel();
		String nextUrl = UrlParseDongao.getTestModel(m, html);
		if(nextUrl!=null) {
			String html2=UrlParseDongao.getHtml(nextUrl);
			UrlParseDongao.getTestModel(m, html2);
		}
		System.out.println(m);
	}
}
