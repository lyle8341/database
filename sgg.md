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
 
+ InnoDB
  > 索引失效，行锁变表锁
  
  | Innodb_row_lock_current_waits | 当前正在等待锁定的数量                 |
  | Innodb_row_lock_time          | 从系统启动到现在锁定总时间长度           |
  | Innodb_row_lock_time_avg      | 每次等待所花平均时间                   |
  | Innodb_row_lock_time_max      | 从系统启动到现在等待最长的一次所花的时间   |
  | Innodb_row_lock_waits         | 系统启动后到现在总共等待的次数           |


原理












**Reference**
>[尚硅谷][1]

[1]: https://xxx "锁"  