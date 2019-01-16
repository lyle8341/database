/**
 * <ul>
 * 1.使用docker<br>
 * 2.启动  docker start container_id/names<br>
 * 3.客户端 docker exec -it redis(容器名) redis-cli
 * 4.SETNX 是(set if not exists):成功返回1,失败返回0，若给定的 key 已经存在，则 SETNX 不做任何动作
 * </ul>
 */
package com.lyle.db.distributedLock.cache.redis;
