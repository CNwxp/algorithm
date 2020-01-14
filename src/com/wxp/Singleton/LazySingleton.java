package com.wxp.Singleton;
/**
 * 懒汉式：可以实现懒加载,但是线程不安全。
 * @author xpwang
 *
 */
public class LazySingleton {
	private static LazySingleton Singleton;

	private LazySingleton() {
	}

	public LazySingleton getInstance() {
		if (Singleton == null) {
			Singleton = new LazySingleton();
		}
		return Singleton;
	}
}
