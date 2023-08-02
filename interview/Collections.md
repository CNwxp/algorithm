

#### hashMap
- hashMap的数组默认大小为16,如果输入数组初始化的值会强制转为2的n次幂
  
  0001 0101 0111 0010 1010(某个key的hash值)  
  0000 0000 0000 0001 0000(数组大小,由于是2的n次幂，所以只有某一位是1)  
  keyhash&lenth-1的范围就在0到15  
  0001 0101 0111 0010 1010  
  0000 0000 0000 0000 1111(16-1)  
  方便使用位运算、位运算的效率大概是取模运算的10倍。
- hashMap的扩容机制
  ![1690963500395](https://github.com/CNwxp/algorithm/assets/48647632/7e080138-1df6-443f-8ddb-412ab226fcc3)

```java

HashMap扩容，
当前hashmap存了多少element，size>=threshold
threshold扩容阈值 = capacity * 扩容阈值比率 0.75 = 16*0.75=12
扩容怎么扩？
扩容为原来的2倍。
转移数据
void transfer(Entry[] newTable, boolean rehash) {
        int newCapacity = newTable.length;
        for (Entry<K,V> e : table) {
            while(null != e) {
                Entry<K,V> next = e.next;
                if (rehash) { 
                    e.hash = null == e.key ? 0 : hash(e.key);//再一次进行hash计算？
                }
                int i = indexFor(e.hash, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            }
        }
    }
    
链表成环，死锁问题

hash扩容，有个加载因子？loadfactor = 0.75为什么是0.75
0.5
1
牛顿二项式：基于空间与时间的折中考虑0.5

引入红黑树！
容量>=64才会链表转红黑树，否则优先扩容
只有等链表过长，阈值设置TREEIFY_THRESHOLD = 8，不是代表链表长度，链表长度>8,链表9的时候转红黑树
Node<K,V> loHead = null, loTail = null;
Node<K,V> hiHead = null, hiTail = null;
Node<K,V> next;
    do {
        next = e.next;
        if ((e.hash & oldCap) == 0) {
            //yangguo.hashcode & 16 = 0，用低位指针
            if (loTail == null)
                loHead = e;
            else
                loTail.next = e;
            loTail = e;
        }
        else {
             //yangguo.hashcode & 16 》 0 高位指针
            if (hiTail == null)
                hiHead = e;
            else
                hiTail.next = e;
            hiTail = e;
        }
    } while ((e = next) != null);
if (loTail != null) {
    loTail.next = null; 
    newTab[j] = loHead;，移到新的数组上的同样的index位置
}
if (hiTail != null) {
    hiTail.next = null;
    newTab[j + oldCap] = hiHead; //index 3+16 = 19
}
完全绕开rehash，要满足高低位移动，必须数组容量是2的幂次方

    
分库分表，在线扩容
    2台master tuling（1,2,3,4） -> 4台
```
