**冒泡排序**：一次比较两个相邻的元素，如果第二个元素大于第一个元素则进行交换。这样每都会次循环都会将最大的元素放到列表的末尾位置，像气泡一样飘到上面。
```java
  public void bubbleSort(int[] nums) {                                              
      for (int i = 0; i < nums.length; i++) {                                       
          for (int j = 0; j < nums.length-i-1; j++) {                               
              if (nums[j] > nums[j+1]) {                                            
                  swap(nums, j, j+1);                                               
              }                                                                     
          }                                                                         
      }                                                                                                                                                               
  }
 public void swap(int[] nums, int i, int j) {            
     int temp = 0;                                       
     temp = nums[i];                                     
     nums[i] = nums[j];                                  
     nums[j] = temp;                                     
 }                                                                                                                                       
```
