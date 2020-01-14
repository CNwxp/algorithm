package com.wxp.Singleton;
/**
 * 线程安全懒汉式：可以实现懒加载,但是性能较差容易线程堵塞。
 * @author xpwang
 *
 */
public class SynchronizedLazy {
	private static SynchronizedLazy Singleton;

	private SynchronizedLazy() {
	}

	public synchronized SynchronizedLazy getInstance() {
		if (Singleton == null) {
			Singleton = new SynchronizedLazy();
		}
		return Singleton;
	}
}
