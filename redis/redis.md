# redis

### String 
```text
set key value
get key
SETNX key value
```

### hash
```text
HMSET key field value [field value ...]
HMGET key field [field ...]
----------------------------------------
HSET key field value
HGET key field
```

### list
```text
LINDEX key index
LPUSH key value1 [value2 ...] 插入到头部
RPUSH key value1 [value2 ...]
-------------------------------
LRANGE key start stop
```

### set
```text





```


### pub/sub
```text
SUBSCRIBE 主题名称 (订阅某一个主题)
PUBLISH 主题名称 消息内容 (向指定的主题中发送一条消息)

测试:两个客户端,先订阅,再发布
```

### redis key失效机制
    key失效时候,会发送一些通知
+ 开启事件通知(修改启动的配置文件)
  - redis.conf(notify-keyspace-events Ex)
+ 主题名称:`__keyevent@dbindex__:expired`

