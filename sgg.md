#### 锁

+ MyISAM
  > MyISAM只支持表锁

  > 手动增加表锁
    + lock table 表名 read(write),表名2 read(write)
  
  > 查看表上加过的锁
    + show open tables;
    
  > 释放表锁
    + unlock tables;

  > 总结
                
      1. 加读锁，不会阻塞其他进程对同一表的读请求，但会阻塞对同一表的写请求
      2. 加写锁，会阻塞其他进程对同一表的读和写
 
      















**Reference**
>[尚硅谷][1]

[1]: https://xxx "锁"  