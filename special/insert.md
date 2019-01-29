#### insert
1 . create table
```sql
  CREATE TABLE `tt` (
    `a` int(11) NOT NULL AUTO_INCREMENT,
    `b` int(11) DEFAULT NULL,
    PRIMARY KEY (`a`),
    KEY `idx_b` (`b`)
  ) ENGINE=InnoDB
```
2 . insert record
```sql
insert into tt values(1,8),(2,3),(3,4),(4,1),(5,12);
```
3 . scene 1

|  no |                  tx1                            |                    tx2                       |
|:---:|:------------------------------------------------|:---------------------------------------------|
|  1  |     mysql> begin;                               |             mysql> begin;                    |
|  2  | mysql>select * from tt where b = 5 for update;  |                                              |
|  3  |                                                 |  mysql> insert into tt(b) values(6); 锁等待。。|
|  4  |            commit;                              |                                              |
|  5  |                                                 |  Query OK, 1 row affected (3.74 sec)         |
步骤2：
```
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 123183:1523
ENGINE_TRANSACTION_ID: 123183
            THREAD_ID: 66
             EVENT_ID: 375
        OBJECT_SCHEMA: databases
          OBJECT_NAME: tt
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140123828479512
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 123183:466:5:2
ENGINE_TRANSACTION_ID: 123183
            THREAD_ID: 66
             EVENT_ID: 375
        OBJECT_SCHEMA: databases
          OBJECT_NAME: tt
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_b
OBJECT_INSTANCE_BEGIN: 140123828160536
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP
          LOCK_STATUS: GRANTED
            LOCK_DATA: 8, 1
2 rows in set (0.00 sec)

```
步骤3：
___
>比步骤2多了两个
---
```
*************************** 1. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 123184:1523
ENGINE_TRANSACTION_ID: 123184
            THREAD_ID: 67
             EVENT_ID: 248
        OBJECT_SCHEMA: databases
          OBJECT_NAME: tt
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: NULL
OBJECT_INSTANCE_BEGIN: 140123828480280
            LOCK_TYPE: TABLE
            LOCK_MODE: IX
          LOCK_STATUS: GRANTED
            LOCK_DATA: NULL
*************************** 2. row ***************************
               ENGINE: INNODB
       ENGINE_LOCK_ID: 123184:466:5:2
ENGINE_TRANSACTION_ID: 123184
            THREAD_ID: 67
             EVENT_ID: 248
        OBJECT_SCHEMA: databases
          OBJECT_NAME: tt
       PARTITION_NAME: NULL
    SUBPARTITION_NAME: NULL
           INDEX_NAME: idx_b
OBJECT_INSTANCE_BEGIN: 140123828163608
            LOCK_TYPE: RECORD
            LOCK_MODE: X,GAP,INSERT_INTENTION
          LOCK_STATUS: WAITING
            LOCK_DATA: 8, 1
```


4 . scene 2




5 . scene 3



6 . scene 4





