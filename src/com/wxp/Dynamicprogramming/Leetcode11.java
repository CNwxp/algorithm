package com.wxp.Dynamicprogramming;
/**
 * 盛水最多的容器　　双指针法
 * @author amarsoft
 *
 */
public class Leetcode11 {
	public static void main(String[] args) {
		int [] height = {1,8,6,2,5,4,8,3,7};
		int maxarea = maxArea(height);
		System.out.println(maxarea);
	}
	
	
	 public final static int maxArea(int[] height) {
		 int maxarea=0,l=height.length-1,r=0;
	        while(r<l) {
	        	maxarea = Math.max(maxarea,Math.min(height[r],height[l])*(l-r));
	        if(height[r]<height[l]) {
	        	r++;
	        }else {
	        	l--;
	        }
	        }
	        return maxarea;
	    }
}

