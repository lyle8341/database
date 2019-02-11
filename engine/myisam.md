#### MyISAM
+ **MyISAM**只有表锁
  + 读锁  
  + 表锁
+ 没有事务，不用考虑并发问题
+ 锁粒度太大
+ 一定要碎片整理，**optimize table tableName**
+ 指数式复制表中数据
  ```sql
  insert into table_name select id,name from table_name;
  ```