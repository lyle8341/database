https://www.jianshu.com/p/7003d58ea182

https://dev.mysql.com/doc/refman/8.0/en/xa-statements.html

#### 内部XA和外部Xa
> 1. For "external XA," a MySQL server acts as a Resource Manager and client programs act as Transaction Managers. 
> 2. For "Internal XA", storage engines within a MySQL server act as RMs, and the server itself acts as a TM

#### 内部xa
    xid: gtrid [, bqual [, formatID ]]
> 1. gtrid is a global transaction identifier, bqual is a branch qualifier, and formatID is a number that identifies the format used by the gtrid and bqual values. As indicated by the syntax, bqual and formatID are optional. The default bqual value is '' if not given. The default formatID value is 1 if not given.
> 2. gtrid and bqual must be string literals, each up to 64 bytes (not characters) long. gtrid and bqual can be specified in several ways. You can use a quoted string ('ab'), hex string (X'6162', 0x6162), or bit value (b'nnnn').formatID is an unsigned integer.

![xa](/img/xa-single-pc.png)

#### 如果xid的值包含不可打印字符
    XA RECOVER CONVERT XID
![xa-convert](/img/xa-convert.png)

**Reference**
>[mysql 对XA事务的支持][1]

[1]: http://www.tianshouzhi.com/api/tutorials/distributed_transaction/384 "XA事务"  