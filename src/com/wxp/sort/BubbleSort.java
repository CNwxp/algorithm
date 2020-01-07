package com.wxp.sort;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

public class BubbleSort {
public static void main(String[] args) {
	int arr[] =new int[80000];
	for (int i = 0; i < arr.length; i++) {
		arr[i]=(int)(Math.random()*80000);
	}
	
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	String time1 = simpleDateFormat.format(new Date());
	System.out.println(time1);
	bubbleSort(arr);
	SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	String time2 = simpleDateFormat1.format(new Date());
	System.out.println(time2);
	//System.out.println(Arrays.toString(arr));
}
public static void bubbleSort(int [] s) {
	for (int i = 0; i < s.length; i++) {
		for (int j = i+1; j < s.length; j++) {
			if(s[j]<s[i]) {
				int temp =0;
				temp = s[j];
				s[j]=s[i];
				s[i] = temp;
			}
		}
	}
}
}
