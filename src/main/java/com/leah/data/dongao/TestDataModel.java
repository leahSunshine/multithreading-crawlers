package com.leah.data.dongao;

public class TestDataModel {
	private int part;
	private String contentType;//内容类型
	private String testType;//试题类型
	private String content="";//内容
	private String title;//题目
	private String option;//选项
	private String rightAns;//正确答案
	private String analysis;//解析
	private String images;//图片
	public int getPart() {
		return part;
	}
	public void setPart(int part) {
		this.part = part;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getTestType() {
		return testType;
	}
	public void setTestType(String testType) {
		this.testType = testType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getRightAns() {
		return rightAns;
	}
	public void setRightAns(String rightAns) {
		this.rightAns = rightAns;
	}
	public String getAnalysis() {
		return analysis;
	}
	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}
	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
	}
	@Override
	public String toString() {
		return "TestDataModel [part=" + part + ", contentType=" + contentType + ", testType=" + testType + ", content="
				+ content + ", title=" + title + ", option=" + option + ", rightAns=" + rightAns + ", analysis="
				+ analysis + ", images=" + images + "]";
	}
	
	
}
