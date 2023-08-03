#### redis数据一致性问题

- ##### 先删缓存再更新数据库
    A:删缓存
    B:查询缓存没有，就查数据库然后更新到缓存里
    A:更新数据库
    数据库和缓存不一致
- ##### 正常双删
   A:删缓存
   B:读取数据
   A:更新数据库
   A:删缓存
   B:更新缓存
 - #### 延迟双删
 ```JAVA
   public void write(String key,Object data){
      redis.delKey(key);
      db.updateData(data);
      Thread.sleep(1000);
      redis.delKey(key);
    }
 ```
 #### Redis的持久化的策略
 ##### Rdb快照方式
 在默认情况下， Redis 将内存数据库快照保存在名字为 dump.rdb 的二进制文件中。
你可以对 Redis 进行设置， 让它在“ N 秒内数据集至少有 M 个改动”这一条件被满足时， 自动保存一次数据集。
比如说， 以下设置会让 Redis 在满足“ 60 秒内有至少有 1000 个键被改动”这一条件时， 自动保存一次数据集：
**save 60 1000  **  //关闭RDB只需要将所有的save保存策略注释掉即可
还可以手动执行命令生成RDB快照，进入redis客户端执行命令save或bgsave可以生成dump.rdb文件，每次命令执行都会将所有redis内存快照到一个新的rdb文件里，并覆盖原有rdb快照文件。

**bgsave的写时复制(COW)机制**
Redis 借助操作系统提供的写时复制技术（Copy-On-Write, COW），在生成快照的同时，依然可以正常处理写命令。简单来说，bgsave 子进程是由主线程 fork 生成的，可以共享主线程的所有内存数据。bgsave 子进程运行后，开始读取主线程的内存数据，并把它们写入 RDB 文件。此时，如果主线程对这些数据也都是读操作，那么，主线程和 bgsave 子进程相互不影响。但是，如果主线程要修改一块数据，那么，这块数据就会被复制一份，生成该数据的副本。然后，bgsave 子进程会把这个副本数据写入 RDB 文件，而在这个过程中，主线程仍然可以直接修改原来的数据。



配置自动生成rdb文件后台使用的是bgsave方式。
|命令|save|bgsave|
|----|----|------|
|IO类型|同步|异步|
|是否阻塞redis其它命令|是|否|
|复杂度|O(n)|O(n)|
|优点|不会消耗额外内存|不阻塞客户端的命令|
|缺点|阻塞客户端命令|需要fork子线程消耗内存|

##### AOF（append-only file）

快照功能并不是非常耐久（durable）： 如果 Redis 因为某些原因而造成故障停机， 那么服务器将丢失最近写入、且仍未保存到快照中的那些数据。从 1.1 版本开始， Redis 增加了一种完全耐久的持久化方式： AOF 持久化，将修改的每一条指令记录进文件appendonly.aof中(先写入os cache，每隔一段时间fsync到磁盘)
比如执行命令“set zhuge 666”，aof文件里会记录如下数据
``` SHELL
*3
$3
set
$5
zhuge
$3
666

```

这是一种resp协议格式数据，星号后面的数字代表命令有多少个参数，$号后面的数字代表这个参数有几个字符
注意，如果执行带过期时间的set命令，aof文件里记录的是并不是执行的原始命令，而是记录key过期的时间戳
比如执行“set tuling 888 ex 1000”，对应aof文件里记录如下

```SHELL
*3
$3
set
$6
tuling
$3
888
*3
$9
PEXPIREAT
$6
tuling
$13
1604249786301
```

你可以通过修改配置文件来打开 AOF 功能：
```shell
**appendonly yes**
```
从现在开始， 每当 Redis 执行一个改变数据集的命令时（比如 SET）， 这个命令就会被追加到 AOF 文件的末尾。
这样的话， 当 Redis 重新启动时， 程序就可以通过重新执行 AOF 文件中的命令来达到重建数据集的目的。
你可以配置 Redis 多久才将数据 fsync 到磁盘一次。
有三个选项：
```shell
appendfsync always：每次有新命令追加到 AOF 文件时就执行一次 fsync ，非常慢，也非常安全。
appendfsync everysec：每秒 fsync 一次，足够快，并且在故障时只会丢失 1 秒钟的数据。
appendfsync no：从不 fsync ，将数据交给操作系统来处理。更快，也更不安全的选择。
```
推荐（并且也是默认）的措施为每秒 fsync 一次， 这种 fsync 策略可以兼顾速度和安全性。

**AOF重写**
AOF文件里可能有太多没用指令，所以AOF会定期根据内存的最新数据生成aof文件
例如，执行了如下几条命令：
```shell
127.0.0.1:6379> incr readcount
(integer) 1
127.0.0.1:6379> incr readcount
(integer) 2
127.0.0.1:6379> incr readcount
(integer) 3
127.0.0.1:6379> incr readcount
(integer) 4
127.0.0.1:6379> incr readcount
(integer) 5
```
重写后AOF文件里变成
```shell
*3
$3
SET
$2
readcount
$1
5
```
如下两个配置可以控制AOF自动重写频率
```shell
# auto-aof-rewrite-min-size 64mb   //aof文件至少要达到64M才会自动重写，文件太小恢复速度本来就很快，重写的意义不大
# auto-aof-rewrite-percentage 100  //aof文件自上一次重写后文件大小增长了100%则再次触发重写
```
当然AOF还可以手动重写，进入redis客户端执行命令bgrewriteaof重写AOF
注意，AOF重写redis会fork出一个子进程去做(与bgsave命令类似)，不会对redis正常命令处理有太多影响
|命令|RDB|AOF|
|---|---|---|
|启动优先级|低|高|
|体积|小|大|
|恢复速度|快|慢|
|数据安全性|容易丢数据|根据策略决定|
生产环境可以都启用，redis启动时如果既有rdb文件又有aof文件则优先选择aof文件恢复数据，因为aof一般来说数据更全一点。

**Redis 4.0 混合持久化**
重启 Redis 时，我们很少使用 RDB来恢复内存状态，因为会丢失大量数据。我们通常使用 AOF 日志重放，但是重放 AOF 日志性能相对 RDB来说要慢很多，这样在 Redis 实例很大的情况下，启动需要花费很长的时间。 Redis 4.0 为了解决这个问题，带来了一个新的持久化选项——混合持久化。
通过如下配置可以开启混合持久化(必须先开启aof)：
```shell
# aof-use-rdb-preamble yes   
```
如果开启了混合持久化，AOF在重写时，不再是单纯将内存数据转换为RESP命令写入AOF文件，而是将重写这一刻之前的内存做RDB快照处理，并且将RDB快照内容和增量的AOF修改内存数据的命令存在一起，都写入新的AOF文件，新的文件一开始不叫appendonly.aof，等到重写完新的AOF文件才会进行改名，覆盖原有的AOF文件，完成新旧两个AOF文件的替换。
于是在 Redis 重启的时候，可以先加载 RDB 的内容，然后再重放增量 AOF 日志就可以完全替代之前的 AOF 全量文件重放，因此重启效率大幅得到提升。
 

 **Redis数据备份策略：**
写crontab定时调度脚本，每小时都copy一份rdb或aof的备份到一个目录中去，仅仅保留最近48小时的备份
每天都保留一份当日的数据备份到一个目录中去，可以保留最近1个月的备份
每次copy备份的时候，都把太旧的备份给删了
每天晚上将当前机器上的备份复制一份到其他机器上，以防机器损坏

**redis主从架构搭建，配置从节点步骤：**
```shell
 1、复制一份redis.conf文件

2、将相关配置修改为如下值：
port 6380
pidfile /var/run/redis_6380.pid  # 把pid进程号写入pidfile配置的文件
logfile "6380.log"
dir /usr/local/redis-5.0.3/data/6380  # 指定数据存放目录
# 需要注释掉bind
# bind 127.0.0.1（bind绑定的是自己机器网卡的ip，如果有多块网卡可以配多个ip，代表允许客户端通过机器的哪些网卡ip去访问，内网一般可以不配置bind，注释掉即可）

3、配置主从复制
replicaof 192.168.0.60 6379   # 从本机6379的redis实例复制数据，Redis 5.0之前使用slaveof
replica-read-only yes  # 配置从节点只读

4、启动从节点
redis-server redis.conf

5、连接从节点
redis-cli -p 6380

6、测试在6379实例上写数据，6380实例是否能及时同步新修改数据

7、可以自己再配置一个6381的从节点
```

**Redis主从工作原理**
如果你为master配置了一个slave，不管这个slave是否是第一次连接上Master，它都会发送一个PSYNC命令给master请求复制数据。
master收到PSYNC命令后，会在后台进行数据持久化通过bgsave生成最新的rdb快照文件，持久化期间，master会继续接收客户端的请求，它会把这些可能修改数据集的请求缓存在内存中。当持久化进行完毕以后，master会把这份rdb文件数据集发送给slave，slave会把接收到的数据进行持久化生成rdb，然后再加载到内存中。然后，master再将之前缓存在内存中的命令发送给slave。
当master与slave之间的连接由于某些原因而断开时，slave能够自动重连Master，如果master收到了多个slave并发连接请求，它只会进行一次持久化，而不是一个连接一次，然后再把这一份持久化的数据发送给多个并发连接的slave。

![Redis主从数据同步](https://user-images.githubusercontent.com/48647632/140641374-c1efd23f-8a0b-4efd-ad65-352902c8fbe7.png)
**数据部分复制**
当master和slave断开重连后，一般都会对整份数据进行复制。但从redis2.8版本开始，redis改用可以支持部分数据复制的命令PSYNC去master同步数据，slave与master能够在网络连接断开重连后只进行部分数据复制(断点续传)。
master会在其内存中创建一个复制数据用的缓存队列，缓存最近一段时间的数据，master和它所有的slave都维护了复制的数据下标offset和master的进程id，因此，当网络连接断开后，slave会请求master继续进行未完成的复制，从所记录的数据下标开始。如果master进程id变化了，或者从节点数据下标offset太旧，已经不在master的缓存队列里了，那么将会进行一次全量数据的复制。
主从复制(部分复制，断点续传)流程图：

![redis主从复制断点续传](https://user-images.githubusercontent.com/48647632/140641450-4523b0b2-7532-4f2b-8b53-64bcb4f3a150.png)
如果有很多从节点，为了缓解主从复制风暴(多个从节点同时复制主节点导致主节点压力过大)，可以做如下架构，让部分从节点与从节点(与主节点同步)同步数据

**Redis哨兵高可用架构**

![Redis哨兵模式](https://user-images.githubusercontent.com/48647632/140641542-108c7e67-2703-466d-8520-28af06496e88.png)

sentinel哨兵是特殊的redis服务，不提供读写服务，主要用来监控redis实例节点。
哨兵架构下client端第一次从哨兵找出redis的主节点，后续就直接访问redis的主节点，不会每次都通过sentinel代理访问redis的主节点，当redis的主节点发生变化，哨兵会第一时间感知到，并且将新的redis主节点通知给client端(这里面redis的client端一般都实现了订阅功能，订阅sentinel发布的节点变动消息)
**redis哨兵架构搭建步骤：**

```shell
1、复制一份sentinel.conf文件
cp sentinel.conf sentinel-26379.conf

2、将相关配置修改为如下值：
port 26379
daemonize yes
pidfile "/var/run/redis-sentinel-26379.pid"
logfile "26379.log"
dir "/usr/local/redis-5.0.3/data"
# sentinel monitor <master-redis-name> <master-redis-ip> <master-redis-port> <quorum>
# quorum是一个数字，指明当有多少个sentinel认为一个master失效时(值一般为：sentinel总数/2 + 1)，master才算真正失效
sentinel monitor mymaster 192.168.0.60 6379 2   # mymaster这个名字随便取，客户端访问时会用到

3、启动sentinel哨兵实例
src/redis-sentinel sentinel-26379.conf

4、查看sentinel的info信息
src/redis-cli -p 26379
127.0.0.1:26379>info
可以看到Sentinel的info里已经识别出了redis的主从

5、可以自己再配置两个sentinel，端口26380和26381，注意上述配置文件里的对应数字都要修改

```

**redis的数据结构**
zset
- https://www.cnblogs.com/wowosong/p/16983986.html
