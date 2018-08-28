package com.leah.ipproxy;

import java.util.List;

import org.jsoup.nodes.Document;

public interface UrlParseHandle {
	List<IPMessage> parseDocument(Document document);
}