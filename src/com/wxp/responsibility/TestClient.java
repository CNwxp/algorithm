package com.wxp.responsibility;
/**
 * 测试类：请假天数小于两天的在小组组长哪里请假就可以了。
 * 使多个对象都有机会处理请求，从而避免请求的发送者和接收者之间的耦合关系。将整个对象连成一条链，并沿着这条链传递该请求，直到有一个对象处理它为止。
 * @author xpwang
 *
 */
public class TestClient {
public static void main(String[] args) {
	ILeave leave =   new Leave("小明", 3,"结婚");
	if(leave.getNum()<=2) {
		Ihander handel = new GroupLeader();
		handel.handler(leave);
	}else {
		Ihander handel = new ManagerLeader();
		handel.handler(leave);
	}
}
}
