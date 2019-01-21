####SQL及索引优化
+ 如何发现有问题的sql
  ```sql
  set global slow_query_log=on;
  show variables like 'slow_query_log_file';
  set global log_queries_not_using_indexes=on;
  set global long_query_time=1;
  ```
+ 慢日志分析
  + mysqldumpslow
    ```bash
    mysqldumpslow -t 3 /var/lib/mysql/lyle-pc-slow.log | more
    ```
  + pt-query-digest
    + 输出到文件
      ```bash
       pt-query-digest slow-log > slow-log.report
      ```
    + 输出到数据库表
      ```bash
       pt-query-digest slow-log -review h=localhost,D=test,p=root,P=3306,u=root,t=query_review --create-reviewtable --review-history t=hostname_slow
      ```
  + 那些sql有问题
    + 查询次数多且每次查询占用时间长的sql
    + IO大的sql,pt-query-digest分析中的rows examine
    + 未命中索引的sql，rows examine和rows send的对比
  + explain
    + table：表
    + type：连接使用的类型，最好到最差（const->reg->ref->range->index->all）
    + possible_keys:可用的索引
    + key:实际使用的索引
    + key_len：索引的长度，在不损失精确性的情况下，越短越好
    + ref:索引的那一列被使用了
    + rows:扫描的行数
    + extra：Using filesort/Using temporary
  + max
    + 加索引
  + count，同时查出a年和b年某某的数量
    ```sql
    select count(year='a' or null) as 'a年某某数量',count(year='b' or null) as 'b年某某数量' from tbl;
    ```
  + 子查询
    + 优化为join，需要注意关联表是否一对多，有重复数据
  + group by
    + [原始]explain select actor.first_name,actor.last_name,count(*) from sakila.film_actor inner join sakila.actor using(actor_id) group by film_actor.actor_id;
    + [优化]explain select actor.first_name,actor.last_name,c.cnt from sakila.actor inner join (select actor_id count(*) as cnt from sakila.film_actor group by actor_id) as c using(actor_id);
  + limit
    + 使用有索引的列或主键进行order by
    + 记录上次返回的主键，下次查询时使用主键过滤
      ```sql
      select film_id,description from sakila.film where film_id > 55 and film_id <= 60 order by film_id limit 1,5;
      ```
  + 如何选择合适的列建立索引
    + 在where从句，group by从句，order by从句，on从句中出现的列
    + 索引字段长度越小越好
    + 离散度大的列放到联合索引的前面
  + 查找重复及冗余索引
    ```sql
    select a.TABLE_SCHEMA as '数据名',a.TABLE_NAME as '表名',a.INDEX_NAME as '索引1',b.INDEX_NAME as '索引2',a.COLUMN_NAME as '重复 列名' from information_schema.STATISTICS a join information_schema.STATISTICS b using(TABLE_SCHEMA,TABLE_NAME,SEQ_IN_INDEX,COLUMN_NAME) where a.SEQ_IN_INDEX = 1 and a.INDEX_NAME <> b.INDEX_NAME;
    ``` 
    ```bash
    pt-duplicate-key-checker -uroot -p -hlocalhost
    pt-index-usage -uroot -p slow-log
    ```
  + 选择合适的数据类型
    + 使用可以存下你的数据的最小数据类型
      + int存储日期，unix_timestamp,from_unixtime
      + bigint存储IP地址，inet_aton，inet_ntoa
    + 使用简单的数据类型，int要比varchar在mysql处理上简单
    + 尽可能的使用not null定义字段
    + 尽量少用text类型，非用不可时最好考虑分表
      
  
  
  
  
  
  
  
  
  
  