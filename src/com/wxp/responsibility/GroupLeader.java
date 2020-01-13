package com.wxp.responsibility;
/**
 * 小组领导的处理实现
 * @author xpwang
 *
 */
public class GroupLeader implements Ihander {

	@Override
	public void handler(ILeave leave) {
		// TODO Auto-generated method stub
		System.out.println(leave.getName()+"请假"+leave.getNum()+"天原因"+leave.getContent());
		System.out.println("小组领导同意");
	}

}
