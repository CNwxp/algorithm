#### Explain使用与详解
- id列 
 id列的编号是 select 的序列号，有几个 select 就有几个id，并且id的顺序是按 select 出现的顺序增长的。 id列越大执行优先级越高，id相同则从上往下执行，id为NULL最后执行。
- select_type列 
- select_type 表示对应行是简单还是复杂的查询。 
    - simple：简单查询。查询不包含子查询和union 
     > mysql> explain select * from film where id = 2; 
    - primary：复杂查询中最外层的 select 
    - subquery：包含在 select 中的子查询（不在 from 子句中） 
    - derived：包含在 from 子句中的子查询。MySQL会将结果存放在一个临时表中，也称为派生表（derived的英文含 义）用这个例子来了解 primary、subquery 和 derived 类型
    > mysql> explain select (select 1 from actor where id = 1) from (select * from film where id = 1) der;
    - union：在 union 中的第二个和随后的 select
    > mysql> explain select 1 union all select 1;
- table列 
这一列表示 explain 的一行正在访问哪个表。 当 from 子句中有子查询时，table列是 <derivenN> 格式，表示当前查询依赖 id=N 的查询，于是先执行 id=N 的查询。当有 union 时，UNION RESULT 的 table 列的值为<union1,2>，1和2表示参与 union 的 select 行id。
- type列 
  这一列表示关联类型或访问类型，即MySQL决定如何查找表中的行，查找数据行记录的大概范围。 依次从最优到最差分别为：system > const > eq_ref > ref > range > index > ALL 一般来说，得保证查询达到range级别，最好达到ref NULL：mysql能够在优化阶段分解查询语句，在执行阶段用不着再访问表或索引。例如：在索引列中选取最小值，可 以单独查找索引来完成，不需要在执行时访问表
  >  mysql> explain select min(id) from film;
   - const, system
  mysql能对查询的某部分进行优化并将其转化成一个常量（可以看show warnings 的结果）。用于 primary key 或 unique key 的所有列与常数比较时，所以表最多有一个匹配行，读取1次，速度比较快。system是 const的特例，表里只有一条元组匹配时为system 1 
  > mysql> explain extended select * from (select * from film where id = 1) tmp;
   - eq_ref
  primary key 或 unique key 索引的所有部分被连接使用 ，最多只会返回一条符合条件的记录。这可能是在 const 之外最好的联接类型了，简单的 select 查询不会出现这种 type。
   >  mysql> explain select * from film_actor left join film on film_actor.film_id = film.id;
  -  range：范围扫描通常出现在 in(), between ,> ,<, >= 等操作中。使用一个索引来检索给定范围的行。
   >  mysql> explain select * from actor where id > 1;
  #### 避免索引失效
  1. 如果索引了多列，要遵守最左前缀法则。指的是查询从索引的最左前列开始并且不跳过索引中的列
  2. 不在索引列上做任何操作（计算、函数、（自动or手动）类型转换），会导致索引失效而转向全表扫描
  3.  mysql在使用不等于（！=或者<>），not in ，not exists 的时候无法使用索引会导致全表扫描 < 小于、 > 大于、 <=、>= 这些，mysql内部优化器会根据检索比例、表大小等多个因素整体评估是否使用索引。
  4. like以通配符开头（'$abc...'）mysql索引失效会变成全表扫描操作。
#### 事务及其ACID属性
- 原子性(Atomicity) ：事务是一个原子操作单元,其对数据的修改,要么全都执行,要么全都不执行。
- 一致性(Consistent) ：在事务开始和完成时,数据都必须保持一致状态。这意味着所有相关的数据规 则都必须应用于事务的修改,以保持数据的完整性。
- 隔离性(Isolation) ：数据库系统提供一定的隔离机制,保证事务在不受外部并发操作影响的“独 立”环境执行。这意味着事务处理过程中的中间状态对外部是不可见的,反之亦然。
- 持久性(Durable) ：事务完成之后,它对于数据的修改是永久性的,即使出现系统故障也能够保持。

#### 并发事务处理带来的问题
- 更新丢失(Lost Update)或脏写
> 当两个或多个事务选择同一行，然后基于最初选定的值更新该行时，由于每个事务都不知道其他事务的存 在，就会发生丢失更新问题–最后的更新覆盖了由其他事务所做的更新
- 脏读（Dirty Reads）
> 一个事务正在对一条记录做修改，在这个事务完成并提交前，这条记录的数据就处于不一致的状态；这 时，另一个事务也来读取同一条记录，如果不加控制，第二个事务读取了这些“脏”数据，并据此作进一步的 处理，就会产生未提交的数据依赖关系。这种现象被形象的叫做“脏读”。 一句话：**事务A读取到了事务B已经修改但尚未提交的数据，还在这个数据基础上做了操作。** 此时，如果B 事务回滚，A读取的数据无效，不符合一致性要求
- 不可重读（Non-Repeatable Reads）
> 一个事务在读取某些数据后的某个时间，再次读取以前读过的数据，却发现其读出的数据已经发生了改 变、或某些记录已经被删除了！这种现象就叫做“不可重复读”。
> **一句话：事务A内部的相同查询语句在不同时刻读出的结果不一致，不符合隔离性**
- 幻读（Phantom Reads）
> 一个事务按相同的查询条件重新读取以前检索过的数据，却发现其他事务插入了满足其查询条件的新数 据，这种现象就称为“幻读”。 
> **一句话：事务A读取到了事务B提交的新增数据，不符合隔离性**

#### 事务的隔离级别

|隔离级别|脏读|不可重复读|幻读|备注|
|--|--|--|--|--|
|读未提交|可能|可能|可能|可以读到未提交的事务|
|读已提交|不可能|可能|可能|A事务在B事务提交前读到了一条数据，之后B事务提交了之后两次数据不一样就不不可重复读|
|可重复读|不可能|不可能|可能|A事务只会读到一开始读到的数据不受其它事务的影响，符合隔离性，但是如果有事务新增了一条数据可能造成幻读用mvcc机制处理|
|串行化|不可能|不可能|不可能|加锁|


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
