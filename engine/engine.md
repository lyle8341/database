* 查看当前是那个库
  1. mysql> select database(); 
  2. mysql> status;


* Mysql在V5.1之前默认存储引擎是MyISAM；在此之后默认存储引擎是InnoDB

  * 查看当前mysql默认引擎: show variables like '%engine%';![默认引擎](/img/default-engine.png)
  * 如果修改本次会话的默认存储引擎(重启后失效)，只对本会话有效，其他会话无效：
    * mysql> set default_storage_engine=innodb;
  * 修改全局会话默认存储引擎(重启后失效)，对所有会话有效
    * mysql> set global default_storage_engine=innodb;
  * 希望重启后也有效，编辑/etc/my.cnf，[mysqld]下面任意位置添加配置；(所有对配置文件的修改，重启后生效)
    * default-storage-engine = InnoDB