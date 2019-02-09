#### 加锁处理分析

+ 快照读：简单的select操作，属于快照读，不加锁。(当然，也有例外，下面会分析)

  + 在默认隔离级别REPEATABLE READ下，同一事务的所有一致性读只会读取**第一次查询时创建的快照**
  
  
      select * from table where ?;

+ 当前读：特殊的读操作，插入/更新/删除操作，属于当前读，需要加锁。

      select * from table where ? lock in share mode;   S锁
      select * from table where ? for update;           X锁
      insert into table values (…);                     X锁
      update table set ? where ?;                       X锁
      delete from table where ?;                        X锁

+ 为什么将 插入/更新/删除操作，都归为当前读？可以看看下面这个更新操作，在数据库中的执行流程
![current_read](/img/current_read.jpg)
+ 一个Update操作的具体流程。当Update SQL被发给MySQL后，MySQL Server会根据where条件，读取第一条满足条件的记录，然后InnoDB引擎会将第一条记录返回，并加锁 (current read)。待MySQL Server收到这条加锁的记录之后，会再发起一个Update请求，更新这条记录。一条记录操作完成，再读取下一条记录，直至没有满足条件的记录为止。因此，Update操作内部，就包含了一个当前读。同理，Delete操作也一样。Insert操作会稍微有些不同，简单来说，就是Insert操作可能会触发Unique Key的冲突检查，也会进行一个当前读
+ **注：根据上图的交互，针对一条当前读的SQL语句，InnoDB与MySQL Server的交互，是一条一条进行的，因此，加锁也是一条一条进行的。先对一条满足条件的记录加锁，返回给MySQL Server，做一些DML操作；然后在读取下一条加锁，直至读取完毕。**











**Reference**
>[加锁处理分析][1]

[1]: http://hedengcheng.com/?p=771 "加锁处理分析"  