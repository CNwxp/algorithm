package com.wxp.Singleton;
/**
 * 饿汉式:在类的初始化创建对象,线程安全。
 * @author xpwang
 *
 */
public class HungrySingleton {
//在静态初始化的时候创建对象
	private static HungrySingleton uniqueHungrySingleton = new HungrySingleton();

//让构造参数私有化，用户无法通过new来创建对象。
	private HungrySingleton() {
	}

	public HungrySingleton getInstance() {
		return uniqueHungrySingleton;
	}
}
