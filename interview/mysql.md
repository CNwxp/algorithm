- 表锁
> locl table (表名) read/write
> show open tables  查看表锁
> unlock table
- 行锁
>
读锁会阻塞写,但是不会阻塞读,写锁会把读和写都阻塞.
