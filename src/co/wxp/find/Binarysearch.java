package co.wxp.find;

import java.lang.reflect.Array;

/**
 * 二分查找，前提必须是有序的数组
 * @author amarsoft
 *
 */
public class Binarysearch {
public static void main(String[] args) {
	int [] sArrays = {1,4,6,8,12,45,46};
	System.out.println(binaryserch(sArrays,0,sArrays.length-1,12));
	
}
public static int binaryserch(int arr[],int left,int right,int findvalue) {
	int midvalueindex = (left+right)/2;
	int midvalue = arr[midvalueindex];
	if(left>right) {
		return -1;
	}
	if(findvalue>midvalue) {
		return binaryserch(arr, midvalueindex+1, right, findvalue);
	}else if (findvalue<midvalue) {
		return binaryserch(arr, left,midvalueindex-1 , findvalue);
	}else {
		return midvalueindex;
	}
}
}
