#### hashMap

初始容量为什么一定是2的指数次幂

hashmap
容量*扩容阈值比例
扩容为原来的两倍(保持为2的指数次幂)
转移数据，遍历table上的元素
loadfactor为什么是0.75 基于空间与时间的折中考虑  太小浪费空间 太大容易发生hash碰撞
扩容的时候死锁问题


容量>=64才会转为红黑树 否则优先扩容
java 8 链表过长会转成红黑树  有个阈值treeIFY_TREESHOLD=8


**熟悉扩容的过程**

#### concurrentMap
> 写同步
> jdk1.7的时候


#### 线程池
 ```java
      int corePoolSize,// 核心线程数
      int maximumPoolSize,// 最大线程数
      long keepAliveTime,// 最大允许线程休息时间
      TimeUnit unit,// 时间单位
      BlockingQueue<Runnable> workQueue// 存放未来得及执行的任务
      ThreadFactory // 创建线程的工厂
      RejectedExecutionHandler// 拒绝策略
 
 ```
 ##### 线程创建过程
  ![线程创建过程](https://github.com/CNwxp/algorithm/blob/master/interview/%E7%BA%BF%E7%A8%8B%E6%B1%A0%E7%9A%84%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B.jpg)
  如果有任务提交进来
   - 先看线程池里面核心线程有没有创建完成
   - 如果核心线程满了，就把任务放进阻塞队列里。
   - 如果阻塞队列也满了，就创建非核心线程去执行任务。
   - 线程池超过最大线程数，执行拒绝策略
  **线程池的拒绝策略**
   1、AbortPolicy：直接抛出异常，默认策略； 
   2、CallerRunsPolicy：用调用者所在的线程来执行任务；
   3、DiscardOldestPolicy：丢弃阻塞队列中靠最前的任务，并执行当前任务；
   4、DiscardPolicy：直接丢弃任务；
 
