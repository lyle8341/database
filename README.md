# databases

* 本工程软件版本

|  mysql  |  redis  |  mongo  |
|:-------:|:-------:|:-------:|
|  8.0.13 |         |         |

><a href="#mysql_architecture">mysql架构</a>  
<a href="#tablespace">innodb数据存储</a>  
<a href="#store_record">存储记录</a>  
<a href="#lock_compatible">锁兼容</a>  
<a href="#innodb_page">innodb page</a>  
<a href="#variables">variables</a>  
<a href="#not_null_default">not_null_default</a>  
<a href="#start_trx">开启事务</a>  
<a href="#db_inst">数据库和实例</a>  
<a href="#tx_isolation">查看事务隔离级别</a>  
<a href="#optimize_mysql">如何优化mysql</a>  

>select CONNECTION_ID();

>InnoDB所有数据是schema的集合，schema是表的集合，表是行的集合

>SQL语句执行时，都是先由InnoDB执行索引扫描，然后，返回结果集给MySQL服务器，MySQL服务器再对该索引条件之外的其他查询条件进行求值，从而得到最终结果集














><a name="mysql_architecture">mysql架构</a>

![mysql_architecture](/img/mysql-architecture.jpg)
+ 最上层用于连接、线程处理的部分并不是MySQL『发明』的，很多服务都有类似的组成部分；
+ 第二层中包含了大多数 MySQL 的核心服务，包括了对 SQL 的解析、分析、优化和缓存等功能，存储过程、触发器和视图都是在这里实现的；
+ 第三层就是MySQL中真正负责数据的存储和提取的存储引擎，例如：InnoDB、MyISAM 等

><a name="tablespace">innodb数据存储</a>

![tablespace](/img/tablespace-segment-extent-page-row.jpg)
+ 所有的数据都被逻辑地存放在表空间中，表空间（tablespace）是存储引擎中最高的存储逻辑单位，在表空间的下面又包括段（segment）、区（extent）、页（page）：
+ 同一个数据库实例的所有表空间都有相同的页大小；默认情况下，表空间中的页大小都为 16KB，当然也可以通过改变 innodb_page_size 选项对默认大小进行修改
![page_size](/img/relation_between_page_size_extent_size.png)

><a name="store_record">存储记录</a>
+ 数据在 InnoDB 存储引擎中都是按行存储的，每个 16KB 大小的页中可以存放 2-200 行的记录。

><a name="lock_compatible">锁兼容</a>

![page_size](/img/Lock-Type-Compatibility-Matrix.jpg)
1. 在上面的兼容性矩阵中，S是表的(不是行的)共享锁，X是表的(不是行的)排它锁。
2. 意向锁IS和IX和任何行锁都兼容（即：和行的X锁或行的S锁都兼容）。所以，意向锁只会阻塞**全表**请求（例如：LOCK TABLES ... WRITE），不会阻塞其他任何东西。因为LOCK TABLES ... WRITE需要设置**X表锁**，这会被意向锁IS或IX所阻塞。

><a name="innodb_page">innodb page</a>
+ 一个 InnoDB 页有以下七个部分
![page_structure](/img/innodb-b-tree-node.jpg)
+ Page Header/Page Directory 关心的是页的状态信息
+ Fil Header/Fil Trailer 关心的是记录页的头信息
+ 每一个数据页中都包含 Infimum 和 Supremum 这两个虚拟的记录（可以理解为占位符），Infimum 记录是比该页中任何主键值都要小的值，Supremum 是该页中的最大值   
![page_structure2](/img/Infimum-Rows-Supremum.jpg)
+ User Records 就是整个页面中真正用于存放行记录的部分
+ Free Space 就是空余空间了，它是一个链表的数据结构，为了保证插入和删除的效率，整个页面并不会按照主键顺序对所有记录进行排序，它会自动从左侧向右寻找空白节点进行插入
+ 行记录在物理存储上并不是按照顺序的，它们之间的顺序是由 next_record 这一指针控制的。
+ B+ 树在查找对应的记录时，并不会直接从树中找出对应的行记录，它只能获取记录所在的页，将整个页加载到内存中，再通过 Page Directory 中存储的稀疏索引和 n_owned、next_record 属性取出对应的记录，不过因为这一操作是在内存中进行的，所以通常会忽略这部分查找的耗时。
   
><a name="variables">variables</a>
+ 查看mysql的datadir
  + mysql> show variables like 'datadir';

+ mysql console清屏
  + mysql> system clear

+ LENGTH()
  + 检查字段实际占用的**字节**长度
  + returns the length of the string measured in bytes. 
+ CHAR_LENGTH()
  + 检查字段实际占用的**字符**长度
  + returns the length of the string measured in characters.

+ explain中的key_len计算
  
  |  field type  |      null    |                             formula                       |      
  |:------------:|:------------:|:----------------------------------------------------------|
  |  varchr(10)  |      yes     |10*(Character Set：utf8=3,gbk=2,latin1=1)+1(NULL)+2(变长字段)|
  |  varchr(10)  |      no      |10*(Character Set：utf8=3,gbk=2,latin1=1)+2(变长字段)        |
  |   char(10)   |      yes     |10*(Character Set：utf8=3,gbk=2,latin1=1)+1(NULL)          |
  |   char(10)   |      no      |10*(Character Set：utf8=3,gbk=2,latin1=1)                  |


https://www.cnblogs.com/liujiacai/p/7753324.html


  
  
段落的前后要有空行，所谓的空行是指没有文字内容。若想在段内强制换行的方式是使用**两个以上**空格加上回车（引用中换行省略回车）。


> xx

> xx

> xx
>> xx


    void main()    
    {    
        printf("Hello, Markdown.");    
}  


> *斜体*，_斜体_    
> **粗体**，__粗体__


+
+
+

* `*`
* \*

- `-`
- \-

- + this

- + - + - + this

1. g



1 . d

分割线
***

><a name="start_trx">开启事务</a>
+ *start transaction*,*begin*语句都可以在mysql命令行下显式地开启一个事务。但是在存储过程中，MySQL分析会自动将*begin*识别为*begin……end*。因此在存储过程中，只能使用*start transaction*语句来开启一个事务
+ 当你显式**开启一个新的事务**，或者执行一条**非临时表的DDL**语句时，就会隐式的将上一个事务提交掉 
+ 当以BEGIN开启一个事务时，首先会去检查是否有活跃的事务还未提交，如果有，则调用ha_commit_trans提交之前的事务，并释放之前事务持有的MDL锁
+ 执行BEGIN命令并不会真的去引擎层开启一个事务，仅仅是为当前线程设定标记，表示为显式开启的事务。 


><a name="db_inst">数据库和实例</a>
+ 物理操作文件系统或其他形式文件类型的集合
+ MySQL 数据库由后台线程以及一个共享内存区组成
+ 在MySQL中，实例和数据库往往都是一一对应的，而我们也无法直接操作数据库，而是要通过数据库实例来操作数据库文件，可以理解为数据库实例是数据库为上层提供的一个专门用于操作的接口
+ 在 Unix 上，启动一个 MySQL 实例往往会产生两个进程，mysqld 就是真正的数据库服务守护进程，而 mysqld_safe 是一个用于检查和设置 mysqld 启动的控制程序，它负责监控 MySQL 进程的执行，当 mysqld 发生错误时，mysqld_safe 会对其状态进行检查并在合适的条件下重启。


><a name="tx_isolation">查看事务隔离级别</a>
```sql
-- MySQL 5.7.19及之前使用tx_isolation
-- 查看事务的 全局和session 隔离级别
select @@global.transaction_isolation, @@session.transaction_isolation;
-- 设置 全局 事务隔离级别为repeatable read
set global transaction isolation level repeatable read;
-- 设置 当前session 事务隔离级别为read uncommitted
set session transaction isolation level read uncommitted;
set session transaction isolation level repeatable read;
```

><a name="optimize_mysql">如何优化mysql</a>
1. 数据库设计要合理(3F)
   + 1F 原子约束（每列不可再分），是否保证原子（看业务）
   + 2F 要求表中的所有列，都必须依赖于主键，而不能有任何一列与主键没有关系，也就是说*一个表只描述一件事情*
   + 3F 表中的每一列只与主键直接相关而不是间接相关，（表中的每一列只能依赖于主键），不要有冗余数据
   
2. 添加索引
3. 分表分库
4. 读写分离
5. 存储过程
6. 配置最大连接数
7. mysql服务器升级
8. 清理碎片化
9. sql语句优化













><a name="not_null_default">not_null_default</a>

![not null default](/img/not-null-default.png)
```sql
insert into lyle(sex)values('男');
# ERROR 1364 (HY000): Field 'name' doesn't have a default value
insert into lyle(name)values('gg');
# Query OK, 1 row affected (0.29 sec)
insert into lyle(name,sex)values('ggg',null);
# ERROR 1048 (23000): Column 'sex' cannot be null
```


分割线
---

============
___
**Reference**
>[1.mysql-innodb][1]  
>[2.innodb下的记录锁，间隙锁，next-key][2]  
>[3.MySQL InnoDB锁介绍及不同SQL语句分别加什么样的锁][3]

[1]: https://draveness.me/mysql-innodb "mysql-innodb" 
[2]: https://www.jianshu.com/p/bf862c37c4c9 "行锁" 
[3]: https://blog.csdn.net/iceman1952/article/details/85504278 "锁介绍" 


