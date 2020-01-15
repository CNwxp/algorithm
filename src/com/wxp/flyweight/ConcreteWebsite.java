package com.wxp.flyweight;

import java.awt.Window.Type;

/**
 * 具体网站层
 * @author xpwang
 *
 */
public class ConcreteWebsite implements Website {
private String type ="";
public ConcreteWebsite(String type) {
	this.type=type;
}

	public String getType() {
	return type;
}

public void setType(String type) {
	type = this.type;
}

	@Override
	public void use() {
		// TODO Auto-generated method stub
		System.out.println("这个网站用来:"+type);
	}

}
