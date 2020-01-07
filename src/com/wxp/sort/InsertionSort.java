package com.wxp.sort;

import java.util.Arrays;

/**
 * 插入排序
 * @author amarsoft
 *
 */
public class InsertionSort {
public static void main(String[] args) {
	int s [] = {9,4,3,8,6};
	insertSort(s);
	System.out.println(Arrays.toString(s));
}
public static void insertSort(int s[]) {
	
	for (int i = 1; i < s.length; i++) {
		int insertvalue =  s [i];
		int insertindex = i-1;
		while (insertindex>=0&&insertvalue<s[insertindex]) {
			s[insertindex+1]=s[insertindex];
			insertindex=insertindex-1;
		}
		if(insertindex+1!=i) {
			s[insertindex+1]=insertvalue;
		}
	}
}
}
