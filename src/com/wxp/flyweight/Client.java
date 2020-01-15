package com.wxp.flyweight;

public class Client {
public static void main(String[] args) {
	WebsiteFactory websiteFactory = new WebsiteFactory();
	ConcreteWebsite concreteWebsite1 =(ConcreteWebsite) websiteFactory.getConcWebsite("博客");
	ConcreteWebsite concreteWebsite2 =(ConcreteWebsite) websiteFactory.getConcWebsite("网页");
	ConcreteWebsite concreteWebsite3 =(ConcreteWebsite) websiteFactory.getConcWebsite("网页");
	ConcreteWebsite concreteWebsite4 =(ConcreteWebsite) websiteFactory.getConcWebsite("网页");
	ConcreteWebsite concreteWebsite5 =(ConcreteWebsite) websiteFactory.getConcWebsite("网页");
	System.out.println(websiteFactory.getCacheMap().size());//只有两个，所以初始化了两个对象。
}
}
