### 72.编辑距离
**题目描述**：给你两个单词 word1 和 word2， 请返回将 word1 转换成 word2 所使用的最少操作数。  

你可以对一个单词进行如下三种操作：  

插入一个字符   
删除一个字符   
替换一个字符   
 

示例 1：  

输入：word1 = "horse", word2 = "ros"  
输出：3  
解释：  
horse -> rorse (将 'h' 替换为 'r')  
rorse -> rose (删除 'r')  
rose -> ros (删除 'e')  
示例 2：  

输入：word1 = "intention", word2 = "execution"  
输出：5  
解释：   
intention -> inention (删除 't')   
inention -> enention (将 'i' 替换为 'e')  
enention -> exention (将 'n' 替换为 'x')  
exention -> exection (将 'n' 替换为 'c')  
exection -> execution (插入 'u')  

**方法：动态规划**       
分析递归结构：根据最长公共子序列问题的学习经验，比较两个字符串的差异可以 根据它们**最后一个字符串的差异**进行穷举，因此状态定义如下：     
第 1 步：定义状态      
dp[i][j] 表示：将 word1[0..i) 转换成为 word2[0..j) 的方案数。        

说明：由于要考虑空字符串，这里的下标 i 不包括 word[i]，同理下标 j 不包括 word[j]。        

第 2 步：推导状态转移方程    
![image](https://github.com/CNwxp/algorithm/assets/48647632/0eb02067-1218-4329-8b60-0398d5b01ef6)
![image](https://github.com/CNwxp/algorithm/assets/48647632/99b41062-5157-4dea-a676-1765333b4a9d)
![image](https://github.com/CNwxp/algorithm/assets/48647632/40ba9dc6-c9eb-4582-a1ef-1023a9e7e9d8)
![image](https://github.com/CNwxp/algorithm/assets/48647632/5c5afbd7-1801-4bdc-bf23-f18035486f78)
```java
dp[i][j] = min(dp[i - 1][j - 1], dp[i][j - 1] + 1, dp[i - 1][j] + 1, dp[i - 1][j - 1] + 1)
```
第 3 步：考虑初始化
从一个字符串变成空字符串，非空字符串的长度就是编辑距离。因此初始化逻辑如下：
```java
// 从word1到word2（word2为空时需要删除的长度）dp[i]为word1[i-1]
for (int i = 0; i <= len1; i++) {
    dp[i][0] = i;
}
// 从word2到word1（word1为空时需要增加的长度）
for (int j = 0; j <= len2; j++) {
    dp[0][j] = j;
}
```
第 4 步：考虑输出     
输出：dp[len1][len2] 符合语义，即 word1[0..len) 转换成 word2[0..len2) 的最小操作数。

第 5 步：思考空间优化          
根据状态转移方程，当前要填写的单元格的数值，完全取决于它的左边一格、上边一格，左上边主对角线上一个的数值。如下图：


![image](https://github.com/CNwxp/algorithm/assets/48647632/dd498b69-2d5b-4a0a-8e13-792dae214f5b)
如果相等取左上角的值，如果替换取左上角的值+1，如果增加word1取上方的值+1,如果删除word1取左边的值+1

因此，有两种经典的空间优化方案：① 滚动数组；② 把主对角线上要参考的数值使用一个新变量记录下来，然后在一维表格上循环赋值。由于空间问题不是这道题的瓶颈，可以不做这样的空间优化。

```java
 public int minDistance(String word1, String word2) {
        int dp[][] = new int[word1.length() + 1][word2.length() + 1];
        // word2为空删除word1需要的步数      
        for (int i = 1; i <= word1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 1; j <= word2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= word1.length(); i++) {
            for (int j = 1; j <= word2.length(); j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j - 1], dp[i - 1][j]), dp[i][j - 1])+1;
                }
            }
        }
        return dp[word1.length()][word2.length()];
    }

```
链接：https://leetcode.cn/problems/edit-distance/
来源：力扣（LeetCode）

----------------------------------------------------------------------------------------------------------------------
