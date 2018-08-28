package com.leah.ipproxy;

import java.util.List;
import java.util.Queue;


public class IPProxyThread implements Runnable {
	private volatile Queue<String> urls ;
	private volatile List<IPMessage> ipMessages;
	private  UrlParseHandle parseHandle ;

	public IPProxyThread(Queue<String> url,List<IPMessage> ipMessage,UrlParseHandle parseHandle) {
		this.urls=url;
		this.ipMessages=ipMessage;
		this.parseHandle=parseHandle;
	}
	@Override
	public void run() {
		
	}

}
