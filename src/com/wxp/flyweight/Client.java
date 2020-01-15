package com.wxp.flyweight;

public class Client {
public static void main(String[] args) {
	WebsiteFactory websiteFactory = new WebsiteFactory();
	ConcreteWebsite concreteWebsite1 =(ConcreteWebsite) websiteFactory.getConcWebsite("博客");
	concreteWebsite1.use();
	ConcreteWebsite concreteWebsite2 =(ConcreteWebsite) websiteFactory.getConcWebsite("网页");
	concreteWebsite2.use();
	ConcreteWebsite concreteWebsite3 =(ConcreteWebsite) websiteFactory.getConcWebsite("网页");
	concreteWebsite3.use();
	ConcreteWebsite concreteWebsite4 =(ConcreteWebsite) websiteFactory.getConcWebsite("网页");
	concreteWebsite4.use();
	ConcreteWebsite concreteWebsite5 =(ConcreteWebsite) websiteFactory.getConcWebsite("网页");
	concreteWebsite5.use();
	System.out.println(websiteFactory.getCacheMap().size());//只有两个，所以初始化了两个对象。
}
}
