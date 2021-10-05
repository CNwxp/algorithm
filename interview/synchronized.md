### synchronized关键字

![markword内容](https://github.com/CNwxp/algorithm/edit/master/interview/markword布局.png)
----------------------------------------------------------------------------------------------------------------------------

#### 偏向锁
 jvm 延迟加载偏向锁
#### 逃逸分析
----------------------------------------------------------------------------------------------------------------------------
[这个博客已经讲得很清楚了](https://blog.csdn.net/love905661433/article/details/82871531)
----------------------------------------------------------------------------------------------------------------------------
### AQS
#### ReentrantLock加锁全过程
> ReentrantLock如何实现synchronized不具备的公平与非公平性呢？
> 在ReentrantLock内部定义了一个Sync的内部类，该类继承AbstractQueuedSynchronized，对该抽象类的部分方法做了实现；并且还定义了两个子类：
> 1、FairSync 公平锁的实现
> 2、NonfairSync 非公平锁的实现
> 这两个类都继承自Sync，也就是间接继承了AbstractQueuedSynchronized，所以这一个
> ReentrantLock同时具备公平与非公平特性。
> 上面主要涉及的设计模式：模板模式-子类根据需要做具体业务实现

```java
  ReentrantLock lock = new ReentrantLock();
		lock.lock();
  // 业务逻辑含要访问的临界资源，一次只有一个线程访问。
		lock.unlock();
```
> Reentrantlock的构造函数默认创建一个NonfairSync类，它继承了ReentrantLock的内部类Sync,Sync继承了AbstractQueuedSynchronizer类下面是几个重要的成员变量。
> state表示资源的可用状态
> State三种访问方式getState()、setState()、compareAndSetState()
> 一个存放线程的双向链表
```JAVA
  private volatile int state;
  static final class Node {
   static final Node SHARED = new Node();
  /** Marker to indicate a node is waiting in exclusive mode */
  static final Node EXCLUSIVE = null;

  /** waitStatus value to indicate thread has cancelled */
  static final int CANCELLED =  1;
  /** waitStatus value to indicate successor's thread needs unparking */
  static final int SIGNAL    = -1;
  /** waitStatus value to indicate thread is waiting on condition */
  static final int CONDITION = -2;
  /**
   * waitStatus value to indicate the next acquireShared should
   * unconditionally propagate
   */
   static final int PROPAGATE = -3;
   volatile int waitStatus;
   volatile Node prev;
   volatile Node next;
   volatile Thread thread;
   Node nextWaiter;
   
  }
  
```
- 首先先看一下NonfairSync的lock方法。
```JAVA
       final void lock() {
             // 当前线程进来先尝试一下能否直接获得锁，如果没有走 acquire(1);
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }
```
- 下面是acquire(1)中做的操作
tryAcquire()尝试直接去获取资源，如果成功则直接返回（这里体现了非公平锁，每个线程获取锁时会尝试直接抢占加塞一次，而CLH队列中可能还有别的线程在等待）；
addWaiter()将该线程加入等待队列的尾部，并标记为独占模式；
acquireQueued()使线程阻塞在等待队列中获取资源，一直获取到资源后才返回。如果在整个等待过程中被中断过，则返回true，否则返回false。
如果线程在等待过程中被中断过，它是不响应的。只是获取资源后才再进行自我中断selfInterrupt()，将中断补上。
```java
          if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
```
- 继续看tryAcquire()里面做了什么,如果资源没有被占用，则用cas直接抢到资源，如果成功了，将占用线程为当前线程，如果占用线程已经是当前线程了将当前状态继续+1（可重入锁）否则返回false，继续执行acquireQueued(addWaiter(Node.EXCLUSIVE), arg)
```java
   final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
``` 
- 接下来是acquireQueued(addWaiter(Node.EXCLUSIVE), arg)的内容。
```java
private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        // 将当前线程放在队尾
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        // 通过自旋的方式给队尾设置值。
        enq(node);
        return node;
    }
    
     final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                
                // 判断前置节点的状态，将线程进行阻塞，在unlock的时候阻塞的线程将被唤醒
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
    
     private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }
    

```
- 下面是解锁的过程unlock();
```java
 public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }

```
