#### 索引原理

+ Hash索引
  + 索引树上存储的是经过Hash计算之后的Hash值，Hash索引仅仅只在Memory引擎中
  + Hash 索引不能利用部分索引键查询
  + Hash 索引仅仅能满足"=","IN"和"<=>"查询，不能使用范围查询
  ![hash-index](/index/hash-index.png)


+ BTree
  + MyISAM(非聚簇索引)
    + 索引树上存放的是索引列的值+行在磁盘中的位置
    ![myisam-noncluster](/index/myisam-noncluster.png)
  + InnoDB(聚簇索引)
    + 数据和索引聚簇到一起
    + 所有的数据都在BTree索引的聚簇索引上，如果表没有主键——>找非空的唯一键——>自建一个隐藏的列
    ![innodb-cluster](/index/innodb-cluster.png)
  + InnoDB(二级索引)
    + 索引树上存放的是索引列的值+主键
    ![secondary-index](/index/secondary-index.png)
  
  