#### lock
+ 表级锁:每次操作锁住整张表，开销小，加锁快;不会出现死锁，锁定粒度大，发生锁冲突概率最高，并发度最低；
  + 使用表锁的主要引擎MyISAM，MEMORY，CSV
+ 行级锁:每次操作锁住一行数据，开销大，加锁慢；会出现死锁，锁定粒度小，发生锁冲突的概率最低，并发度也最高;
  + 主要是InnoDB,NDBCluster
  + 行级锁定不是MySQL自己实现的锁定方式，而是由其他存储引擎自己所实现的
+ 页面锁:开销和加锁时间界于表锁和行锁之间；会出现死锁；锁定粒度界于二者之间，并发度一般；
  + 主要是BerkeleyDB
  
  
  