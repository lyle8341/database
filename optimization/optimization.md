#### SQL及索引优化
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
        + datetime类型"2019-01-21 13:16:44"(19字节)
      + bigint存储IP地址，inet_aton，inet_ntoa
    + 使用简单的数据类型，int要比varchar在mysql处理上简单
    + 尽可能的使用not null定义字段
    + 尽量少用text类型，非用不可时最好考虑分表

+ 范式化是指数据库设计的规范，一般是指第三范式
  + 要求表中不存在非关键字段对任意候选关键字段的传递函数依赖
+ 反范式
  + 为了查询效率的考虑把原本符合第三范式的表适当冗余，以达到优化查询效率，以空间换取时间
        
+ 垂直分表
  + 把不常用的字段单独存放到一个表中
  + 把大字段独立存放到一个表中
  + 把经常一起使用的字段放到一起
  + 原始表
    ```sql
    create table user(user_id int primary key,
                   title varchar(255),
                   description text,
                   name varchar(2),
                   age int 
    );
    ```
  + 垂直拆分
    ```sql
    create table user(
                   user_id int primary key,
                   name varchar(2),
                   age int 
    );
    create table user_text(
                   user_id int primary key,
                   title varchar(255),
                   description text
    );
    ```    
+ 水平分表
  + 比如对主键进行hash运算，mod(pk,n)
  + 不同的hashId存到不同的表中
    
+ 系统优化
  + 操作系统
    + /etc/sysctl.conf
      + 增加tcp支持的队列数
        ```properties
        net.ipv4.tcp_max_syn_backlog=65535
        ```
      + 减少断开连接时，资源回收
        ```properties
        net.ipv4.tcp_max_tw_buckets=8000
        net.ipv4.tcp_tw_reuse=1
        net.ipv4.tcp_tw_recycle=1
        net.ipv4.tcp_fin_timeout=10
        ```
    + /etc/security/limits.conf
      + 打开文件数的限制
        ```text
        * soft nofile 65535
        * hard nofile 65535
        ```
  + mysql系统本身
    + mysql查找配置文件的顺序
      ```bash
      mysqld --verbose --help |grep -A 1 'Default options'
      ```
    + innodb_buffer_pool_size
    + innodb_buffer_pool_instances
    + innodb_log_buffer_size
    + innodb_flush_log_at_trx_commit
    + innodb_read_io_threads
    + innodb_write_io_threads
    + innodb_file_per_table
    + innodb_stats_on_metadata
    + 查询数据及索引大小
      ```sql
      select ENGINE,ROUND(sum(data_length+index_length)/1024/1024,1) as "Total MB" from information_schema.tables where table_schema not in("information_schema","performance_schema") group by ENGINE;
      ```
+ cpu
  + 一个sql的执行，复制进程只能用到一个cpu  
  
  
  
  
  