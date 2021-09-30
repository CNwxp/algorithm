- 表锁
> locl table (表名) read/write
> show open tables  查看表锁
> unlock table
- 行锁
>
读锁会阻塞写,但是不会阻塞读,写锁会把读和写都阻塞.
间隙锁
锁主要加在索引上，如果对索引更新行锁可能升级为表锁

### 索引优化
### 锁优化


### mvcc多版本并发控制机制
> undo日志
> 一致性试图read-view
> binlog和redolog的使用


**参考文献**
[MVCC多版本并发控制原理详解](https://blog.csdn.net/STILLxjy/article/details/112190576)
