#### 锁类型

>*共享锁或排它锁*它并不是一种锁的类型，而是其他各种锁的模式，每种锁都有shard或exclusive两种模式

+ 锁
  + 表锁
    + 意向锁(Intention Locks)
      + 含义是已经持有了表锁，稍候将获取该表上某个/些行的行锁。有shard或exclusive两种模式
    + 自增锁(AUTO-INC Locks)
      + 插入语句开始时请求该锁，插入语句结束后释放该锁（注意：是语句结束后，而不是事务结束后）
  + 行锁
    + record lock
      + S,REC_NOT_GAP或X,REC_NOT_GAP
    + Gap Locks
      + S,GAP或X,GAP(二者等价)