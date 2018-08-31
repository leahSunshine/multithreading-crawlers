package com.leah.ipproxy;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class MainTest {
	public static void main(String[] args) {
		System.out.println(new Date());
		Queue<String> urls = new LinkedList<>();
		  for (int i = 1; i <= 21; i++) {
            urls.offer("http://www.xicidaili.com/nn/" + i);
        }
//		  System.out.println(urls.poll());
		new IPProxyScheduleThread().startCrawlerByTime(new UrlParseHandle() {
			
			@Override
			public List<IPMessage> parseDocument(Document document) {
				 List<IPMessage> newIPMessages = new LinkedList<>();
				 Elements trs = document.select("table[id=ip_list]").select("tbody").select("tr");
				 for (int i = 1; i < trs.size(); i++) {
			            IPMessage ipMessage = new IPMessage();

			            String ipAddress = trs.get(i).select("td").get(1).text();
			            String ipPort = trs.get(i).select("td").get(2).text();
			            String ipType = trs.get(i).select("td").get(5).text();
			            String ipSpeed = trs.get(i).select("td").get(6).select("div[class=bar]").
			                    attr("title");

			            ipMessage.setIPAddress(ipAddress);
			            ipMessage.setIPPort(ipPort);
			            ipMessage.setIPType(ipType);
			            ipMessage.setIPSpeed(ipSpeed);

			            newIPMessages.add(ipMessage);
			        }
				 return newIPMessages;
			}
		}, urls, 24*60*60);
		System.out.println(new Date());
	}
}
