package com.wxp.flyweight;

import java.util.HashMap;
import java.util.Map;
/**
 * 创建网站的工厂类
 * @author xpwang
 *s
 */
public class WebsiteFactory {
	private ConcreteWebsite website;
	Map<String, ConcreteWebsite> cacheMap = new HashMap<String, ConcreteWebsite>();// 用于存放共享的缓存

	public Website getConcWebsite(String type) {
		if (!cacheMap.containsKey(type)) {
			website = new ConcreteWebsite(type);
			cacheMap.put(type, website);
		} else {
			return (Website) cacheMap.get(type);
		}
		return (Website) website;
	}

	public Map<String, ConcreteWebsite> getCacheMap() {
		return cacheMap;
	}

	public void setCacheMap(Map<String, ConcreteWebsite> cacheMap) {
		this.cacheMap = cacheMap;
	}
	
	
}
