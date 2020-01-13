package com.wxp.responsibility;
/**
 * 总经理处理请假
 * @author xpwang
 *
 */
public class ManagerLeader implements Ihander {

	@Override
	public void handler(ILeave leave) {
		// TODO Auto-generated method stub
		System.out.println(leave.getName()+"请假"+leave.getNum()+"天原因"+leave.getContent());
		System.out.println("总经理同意");
	}

}