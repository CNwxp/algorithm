package com.wxp.Dynamicprogramming;
/**
 * 背包问题，每个物体有相应的价值，背包容量有限，求最大价值的组合。
 * @author amarsoft
 *
 */
public class Backpackproblem {
	  private static int [] v = {0,2,4,3,7};
	  private static int [] w = {0,2,3,5,5};
   public static void main(String[] args) {
	   int result = ks(4,10);
	   System.out.println(result);
}
	public static  int ks(int i,int c) {
		//前i个物品的最优解
		int result = 0;
		if(i==0) {
			result =0;
		}else if(w[i]>c) {
		result =	ks(i-1,c);
		}else {
		int temp1 =	ks(i-1,c);
		int temp2 =	ks(i-1,c-w[i])+v[i];
		result =Math.max(temp1,temp2);
		}
		return result;
	}
}
