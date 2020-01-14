package com.wxp.Singleton;
/**
 * 双重检查（推荐）
 * @author xpwang
 *
 */
public class DoubleCheck {
		private volatile static  DoubleCheck Singleton;
		private DoubleCheck() {
		}
		public synchronized DoubleCheck getInstance() {
			if (Singleton == null) {
				synchronized (DoubleCheck.class) {
					Singleton = new DoubleCheck();
				}
			}
			return Singleton;
		}
	}

