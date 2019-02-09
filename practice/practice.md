#### 实践

>select * from performance_schema.data_locks;
>> LOCK_DATA: 0x000000000203 表示其对应的6字节的rowid的位置指针

1 . 搜索时无法使用索引，即全表扫描时，InnoDB在表的全部行上都加锁
  + RR
  + DDL
    ```sql
    create table t_pk_uk(a varchar(10),b int,c int,d varchar(10),primary key(a),unique uq_b(b),index idx_c(c))engine = innodb;
    insert into t_pk_uk value('a',11,22,'dataa'),('b',111,222,'datab'),('c',1111,2222,'datac'),('d',11111,22222,'datad'),('e',111111,222222,'datae');
    ```
    ![p0](/practice/img/p0.png)
  + DML
    ```sql
    update t_pk_uk set b = 1113 where d = 'datac';
    ```
  + 效果
  ![p1](/practice/img/p1.png)
  + 总结
    + 搜索条件无法使用索引时，InnoDB不得不在表的全部行上都加锁。所以，索引实在太重要了，查询时，它能加快查询速度；更新时，除了快速找到指定行，它还能减少被锁定行的范围，提高插入时的并发性

2 . 唯一索引和非唯一索引、等值查询和范围查询加锁的不同
  + RR
  + DDL 同1
  + 示例1:使用非唯一索引 idx_c 搜索或扫描时
    + DML
      ```sql
      select * from t_pk_uk where c = 222 for update;
      ```
    + 效果
    ![p2](/practice/img/p2.png)
    + 总结
      + 使用非唯一索引 idx_c 搜索或扫描时，InnoDB要锁住索引本身，还要锁住索引记录前面的间隙，即next-key lock: X 和 gap lock: X,GAP。next-key lock既锁住索引记录本身，还锁住该索引记录前面的间隙，gap lock只锁住索引记录前面的间隙。等值条件时，在最后一个不满足条件的索引记录上设置gap lock
  + 示例2:使用唯一索引 iux_b 的唯一搜索条件
    + DML
      ```sql
      select * from t_pk_uk where b = 111 for update;
      ```
    + 效果
    ![p3](/practice/img/p3.png)
    + 总结
      + InnoDB只需锁住索引本身，即index record lock: X, REC_NOT_GAP，并不锁索引前面的间隙。
      
  + 示例3:使用唯一索引 iux_b 进行范围扫描时
    + DML
      ```sql
      select * from t_pk_uk where b>=11 and b<=111 for update;
      ```
    + 效果
    ![p4](/practice/img/p4.png)
    + 总结
      + 锁定扫描过的每一个索引记录，并且锁住每一条索引记录前面的间隙，即next-key lock: X。范围条件时，在最后一个不满足条件的索引记录上设置next-key lock  


3 . 不同隔离级别加锁的不同
  + DDL
    ```sql
    create table t_idx(id int,a int,b int,primary key(id),index idx_b(b)) engine = innodb;
    insert into t_idx value(1,1,2),(11,11,22),(111,111,222),(1111,1111,2222),(11111,11111,22222),(111111,111111,222222);
    ```
  ![p5](/practice/img/p5.png)
  + DML
    ```sql
    select * from t_idx where b>=2 and b<=222 and a=11 for update;
    ```
  + 示例1
    + RR
      ![p6](/practice/img/p6.png)
    
  + 示例2
    + RC
    ![p7](/practice/img/p7.png)
  + 总结
    + 上图中，在不同的隔离级别下，执行了相同的SQL。无论何种隔离级别，PRIMARY上的index record lock总是会加的，我们不讨论它。在idx_b上，隔离级别为RC时，InnoDB加了index record lock，即：X,REC_NOT_GAP，隔离级别为RR时，InnoDB加了next-key lock，即X。注意：RC时没有gap lock或next-key lock哦。
    + 上图演示了：事务的隔离级别也会影响到设置哪种锁。如我们前面所说，gap lock是用来阻止phantom row的，而RC时是允许phantom row，所以，RC时禁用了gap lock。因此，上图中，RC时没有在索引上设置gap lock或next-key lock。
  
4 . 操作不存在的索引记录时，也需要加锁
  + RR
  + DDL
    ```sql
    create table t_idx_2(k varchar(10),a int,b int,index idx_a(a),index idx_b(b),primary key(k))engine=innodb;
    insert into t_idx_2 value('a',1,2),('b',11,22),('c',111,222),('d',1111,2222),('e',11111,22222),('f',111111,222222);    
    ```
  + DML
    ![p9](/practice/img/p9.png)
    ```sql
    update t_idx_2 set a = 133 where b = 266;
    ```
    ![p8](/practice/img/p8.png)
    ![p10](/practice/img/p10.png)
  + 总结
    + InnoDB在第一个不满足搜索条件的索引记录上设置gap lock或next-key lock。一般，等值条件时设置gap lock，范围条件时设置next-key lock。上图中是等值条件，于是InnoDB设置gap lock，即上图的X,GAP，其范围是(226, 2222)，正是此gap lock使得并发的事务无法插入b列大于等于266的值，RC时，由于gap lock是被禁止的，因此，并不会加gap lock，并发的事务可以插入b列大于等于266的值。

5 . 重复键错误(duplicate-key error)时，会加共享锁。这可能会导致死锁
  +  shared next-key lock或shared index record loc
  + RR
  + DDL
    ```sql
    create table t1(i int,primary key(i))engine=innodb;
    ```
  + DML-1
    ```sql
    insert into t1 value (1);
    ```
    + trx-1
    ![i1](/practice/img/i1.png)
    + trx-2
    ![i2](/practice/img/i2.png)
    + trx-3
    ![i3](/practice/img/i3.png)
    + trx-1回滚后
    ![i4](/practice/img/i4.png)
    
  + DML-2
    ```sql
    delete from t1 where i = 1;
    ```
    + trx-1
    ![d1](/practice/img/d1.png)
    + trx-2
    ![d2](/practice/img/d2.png)
    + trx-3
    ![d3](/practice/img/d3.png)
    + trx-1提交后
    ![d4](/practice/img/d4.png)    
___
**Reference**
>[1.MySQL InnoDB锁介绍及不同SQL语句分别加什么样的锁][1]

[1]: https://blog.csdn.net/iceman1952/article/details/85504278 "锁介绍" 
