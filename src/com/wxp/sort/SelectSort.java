package com.wxp.sort;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
/**
 * 选择排序　每次将最小（最大）的数字进行排序
 * @author amarsoft
 *
 */
public class SelectSort {
  public static void main(String[] args) {
	  int s[] = new int[80000];
	  for (int i = 0; i < s.length; i++) {
		s[i]=(int) (Math.random()*80000);
	}
	  Date date1 = new Date();
	   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-mm-dd HH:mm:ss");
	   String time1= simpleDateFormat.format(date1);
    System.out.println(time1);
	   
	  selectSort(s);
	  Date date2 = new Date();
	  SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("YYYY-mm-dd HH:mm:ss");
	   String time2 =simpleDateFormat2.format(date2);
	   System.out.println(time2);
	  //System.out.println(Arrays.toString(s));
}
  public static void selectSort(int s []) {
	  
	for (int i = 0; i < s.length; i++) {
		int minindex = i;//将当前当做最小
		int  min =s[minindex];
		for (int j = i+1; j < s.length; j++) {
			 if (min>s[j]) {
				 min=s[j];
				 minindex = j;
			}
			 if(minindex!=i) {
				s[j]=s[i];
				s[i]=min;
			 }
		}
	}	
  }
}
