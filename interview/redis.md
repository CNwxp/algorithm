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
