package com.wxp.Singleton;

public enum Hungryenum {
//定义一个枚举元素，他就是该类的一个实例
	INSTANCE;
	   public void doSomeThing() {  
		     System.out.println("枚举方法实现单例");
	    } 
}
