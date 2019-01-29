#### 行锁

+ Record Lock
  + 总是会去锁定**主键**、**非空的唯一性索引**对应的索引记录，如果在建innodb表时并没有创建任何索引，innodb会对6字节的rowid的主键来进行锁定。Read-Uncommited/RC级别都是使用该方式来进行加锁

+ Gap Lock
  + 间隙锁只存在于**RR隔离级别**下的**辅助索引**中
  + 主键和唯一索引由于本身具有唯一约束，不需要gap锁，只有record lock

+ Next-Key Lock 
  + 结合了Gap Lock和Record Lock的合并，其设计目的主要是为解决RR级别下的幻读问题。该锁定方式相对于Gap Lock和Record Lock是带闭合区间的范围锁定
  + Innodb存储引擎使用Next-Key Locks 只在 REPEATABLE READ 隔离级别下
  + 当主键由多列构成时(组合主键)，我们只使用主键列中的一列进行查询时，依然使用到了Next_Key Lock
    + 我们都知道主键的键值是唯一的，但是我们这里定义的主键是primary key(id,xid) 表示的是(id,xid) 组成的键值是唯一的，并不能保证id或者xid的键值是唯一的，所以这里依然使用Next_Key Lock 来进行加锁并没有降级使用Record lock 来进行加锁
    + 那当使用主键所有列进行查询，Next_Key Lock 降级为Record Lock
    + 



**Reference**
>[innodb下的记录锁，间隙锁，next-key锁][1]

[1]: https://www.jianshu.com/p/bf862c37c4c9 "行锁"  
  
  
