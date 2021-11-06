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
