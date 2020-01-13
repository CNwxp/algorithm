package com.wxp.responsibility;
/**
 * 请假的具体实现
 * @author xpwang
 *
 */
public class Leave implements ILeave {
private String name;
private int num;
private String content;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public int getNum() {
	return num;
}
public void setNum(int num) {
	this.num = num;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public Leave(String name, int num, String content) {
	super();
	this.name = name;
	this.num = num;
	this.content = content;
}
}
