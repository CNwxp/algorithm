package com.wxp.Dynamicprogramming;
/**
 * 字符串的最长公共前缀
 * @author amarsoft
 *
 */
public class Leetcode14 {
	public static void main(String[] args) {
		String [] s = {"dog","racecar","car"};
	String result =	longestCommonPrefix(s);
	System.out.println(result);
	}
	public static String longestCommonPrefix(String[] strs) {
		if(strs.length==0) {
			return "";
		}
		String temp = strs[0];//取第一个字符数组为暂存
		for (int i = 0; i < strs.length; i++) {
			int j =0;
			//循环比较第二个和第一个比较之后的公共部分，在和第三个比较就是整个数组中字符串最长的公共部分。
			for(;j<temp.length()&&j<strs[i].length();j++) {
				if(temp.charAt(j)!=strs[i].charAt(j)) {
					break;
				}
			}
			temp = temp.substring(0, j);
		}
     return temp;
 }
	/**
	 * 方法二，运行时间较短
	 * @param strs
	 * @return
	 */
	 public String longestCommonPrefix2(String[] strs) {
	        if(strs.length==0) return "";
	        String str=strs[0];
	        for(int i=1;i<strs.length;i++){
	            while(strs[i].indexOf(str)!=0){
	                str=str.substring(0,str.length()-1);//如果字符串不匹配则长度减一继续
	                }
	            }
	            return str;
	        }
	
}
