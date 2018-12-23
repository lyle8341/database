- MySQL InnoDB支持三种行锁定方式：
  - 行锁（Record Lock）：锁直接加在索引记录上面。
  - 间隙锁（Gap Lock）：锁加在不存在的空闲空间，可以是两个索引记录之间，也可能是第一个索引记录之前或最后一个索引之后的空间。
  - Next-Key Lock：行锁与间隙锁组合起来用就叫做Next-Key Lock。


http://blog.itpub.net/15498/viewspace-2141515/
