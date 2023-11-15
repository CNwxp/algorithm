**快速排序的核心思想是**:通过一趟排序将要排序的数据分割成独立的两部分，其中一部分的所有数据都比另外一部分的所有数据都要小，然后再按此方法对这两部分数据分别进行快速排序，整个排序过程可以递归进行，以此达到整个数据变成有序序列。
使用分治的思想，分而治之。

-------------------------------------------------------------------------------------------------------
 假设下面这个数组是待排序数组：
```java 
int [] nums={6, 1, 2, 7, 9, 3, 4, 5, 10, 8}
```
 - 首先先选择一个基准元素key，一般选择第一个元素，然后定义两个指针

      <img width="392" alt="1700030262712" src="https://github.com/CNwxp/algorithm/assets/48647632/cc38b140-300b-416b-b2e6-1bf458207b2b">
 
 - 移动j指针直到找到一个比key小的数字，然后移动i指针直到找到一个比key大的数字，交换i与j。
 <img width="556" alt="1700030024074" src="https://github.com/CNwxp/algorithm/assets/48647632/c99f24cb-f73d-49ad-869c-0d25eb1bd362">
 
 - 最后j移动到3的位置，i和j重合。因为每次的是j先移动，所以重合的元素肯定是比key小的。与key交换放在头节点。这时候key左边都是比key小，key右边的都比key大。

 <img width="462" alt="1700031005397" src="https://github.com/CNwxp/algorithm/assets/48647632/68b75dc7-aade-48fe-9209-ae21917328f6">

 - 基准左边的右边的分别递归处理

   <img width="556" alt="1700031146047" src="https://github.com/CNwxp/algorithm/assets/48647632/e6d12a93-ad5d-4766-a3dc-c87f610954f0">

**代码如下**：
```java
 public void quickSort(int[] nums, int left, int right) {
        if (left >= right) {
            return;
        }
        int i = left, j = right;
        int key = nums[left];
        while (i < j) {
            while (nums[j] >= key && i < j) {
                j--;
            }
            while (nums[i] <= key && i < j) {
                i++;
            }
            if (i < j) {
                swap(nums, i, j);
            }
        }
        nums[left] = nums[i];
        nums[i] = key;
        quickSort(nums, i + 1, right);
        quickSort(nums, left, i - 1);
    }

    public void swap(int[] nums, int i, int j) {
        int temp = 0;
        temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

```
[快排演示地址](https://www.cs.usfca.edu/~galles/visualization/ComparisonSort.html)
