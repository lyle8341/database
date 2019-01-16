package com.lyle.db.distributedLock.cache.redis;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author Lyle
 * @version v1.0
 * @since 1.8
 */
@Slf4j
public class RedisLock {

  private static final Logger LOG = LoggerFactory.getLogger(RedisLock.class);
  private static final String LOCKED = "LOCKED";  // 存储到redis中的锁标志
  private volatile boolean isLocked = false;
  private StringRedisTemplate redisTemplate;
  private String key;
  private TimeUnit lockTimeoutAndExpireTimeUnit = TimeUnit.SECONDS;
  /**
   * 请求锁的超时时间
   */
  private int getLockTimeout = 10;
  /**
   * 获取到锁后，设置锁过期时间
   */
  private int lockExpire = 30;
  /**
   * 每次请求失败至少休眠时间（毫秒）
   */
  private int sleepMillis = 10;

  /**
   * @param redisTemplate Redis管理模板
   * @param key 锁定key
   * @param lockTimeoutAndExpireTimeUnit [获取锁超时/锁失效]对应的TimeUnit（默认：TimeUnit.SECONDS）
   * @param getLockTimeout 锁过期时间（避免程序问题而一直持有锁）
   * @param lockExpire 请求锁超时时间
   * @param sleepMillis 每次请求锁失败最少休眠时间（毫秒）
   */
  public RedisLock(StringRedisTemplate redisTemplate, String key, TimeUnit lockTimeoutAndExpireTimeUnit,
      int getLockTimeout, int lockExpire, int sleepMillis) {
    if (redisTemplate == null) {
      throw new RuntimeException("redis锁StringRedisTemplate不能为null");
    }
    if (key == null || "".equals(key.trim())) {
      throw new RuntimeException("redis锁的key不能为空");
    }
    this.redisTemplate = redisTemplate;
    this.key = key;
    if (lockTimeoutAndExpireTimeUnit != null) {
      this.lockTimeoutAndExpireTimeUnit = lockTimeoutAndExpireTimeUnit;
    }
    if (getLockTimeout > 0) {
      this.getLockTimeout = getLockTimeout;
    }
    if (lockExpire > 0) {
      this.lockExpire = lockExpire;
    }
    if (sleepMillis > 0) {
      this.sleepMillis = sleepMillis;
    }
  }

  public RedisLock(StringRedisTemplate redisTemplate, String key, int getLockTimeout, int lockExpire,
      int sleepMillis) {
    this(redisTemplate, key, null, getLockTimeout, lockExpire, sleepMillis);
  }

  public RedisLock(StringRedisTemplate redisTemplate, String key, TimeUnit lockTimeoutAndExpireTimeUnit,
      int getLockTimeout, int lockExpire) {
    this(redisTemplate, key, lockTimeoutAndExpireTimeUnit, getLockTimeout, lockExpire, 0);
  }

  public RedisLock(StringRedisTemplate redisTemplate, String key, int getLockTimeout, int lockExpire) {
    this(redisTemplate, key, null, getLockTimeout, lockExpire, 0);
  }

  public RedisLock(StringRedisTemplate redisTemplate, String key, int sleepMillis) {
    this(redisTemplate, key, null, 0, 0, sleepMillis);
  }

  public RedisLock(StringRedisTemplate redisTemplate, String key) {
    this(redisTemplate, key, null, 0, 0, 0);
  }

  public static <T> Pair<Boolean, T> lockedWork(RedisLock redisLock, Supplier<T> worker) {
    return lockedWork(redisLock, 0, 0, worker);
  }

  /**
   * @param redisLock 分布式锁对象
   * @param addSleepTimeMillis 每次获取锁失败后叠加等待时间
   * @param maxSleepTimeMillis 叠加等待时间的最大时间，超过则不再叠加
   * @param worker 获取到锁后的业务处理函数
   * @param <T>
   */
  public static <T> Pair<Boolean, T> lockedWork(RedisLock redisLock, int addSleepTimeMillis,
      int maxSleepTimeMillis, Supplier<T> worker) {
    if (redisLock == null || worker == null) {
      throw new RuntimeException("本地锁或redisLock对象或锁内程序不能为空");
    }
    if (!redisLock.lock(addSleepTimeMillis, maxSleepTimeMillis)) {
      LOG.error(String.format("未获取到锁，任务取消，锁：%s", redisLock.key));
      return new Pair<>(false, null);
    }
    LOG.warn(String.format("已获取到锁，开始执行锁内程序，锁：%s", redisLock.key));
    try {
      return new Pair<>(true, worker.get());
    } finally {
      redisLock.unlock();
      LOG.warn(String.format("释放锁：%s", redisLock.key));
    }
  }

  private boolean lock() {
    return this.lock(0, 0);
  }

  /**
   * 获取锁
   *
   * @param addSleepTimeMillis 递增睡眠时间
   * @param maxSleepTimeMillis 最大睡眠时间
   */
  private boolean lock(int addSleepTimeMillis, int maxSleepTimeMillis) {
    if (maxSleepTimeMillis < sleepMillis) {
      maxSleepTimeMillis = sleepMillis;
    }
    if (addSleepTimeMillis < 0) {
      addSleepTimeMillis = 0;
    }
    BoundValueOperations<String, String> ops = this.redisTemplate.boundValueOps(key);
    long timeout = TimeUnit.MILLISECONDS.convert(this.getLockTimeout, lockTimeoutAndExpireTimeUnit);
    long expire = TimeUnit.MILLISECONDS.convert(this.lockExpire, lockTimeoutAndExpireTimeUnit);
    long startTime = System.currentTimeMillis();
    int currSleepMillis = sleepMillis;
    while ((System.currentTimeMillis() - startTime) < timeout) {
      try {
        Boolean aBoolean = ops.setIfAbsent(LOCKED);
        if (null != aBoolean && aBoolean) {
          ops.expire(expire,TimeUnit.MILLISECONDS);
          this.isLocked = true;
          break;
        } else if (null != ops.getExpire() && ops.getExpire() == -1L) {
          ops.expire(expire,TimeUnit.MILLISECONDS);
          LOG.error("检测到已存在的锁未设置失效时间，为其设置失效时间，key：" + key);
        }
        if (currSleepMillis > maxSleepTimeMillis) {
          currSleepMillis = maxSleepTimeMillis;
        }
        LOG.warn(String.format("未获取到锁，%s，休眠：%s毫秒，key：%s", Thread.currentThread(), currSleepMillis, key));
        TimeUnit.MILLISECONDS.sleep(currSleepMillis);
        currSleepMillis = currSleepMillis + addSleepTimeMillis;
      } catch (Exception e) {
        LOG.error("", e);
      }
    }
    return this.isLocked;
  }

  private void unlock() {
    if (this.isLocked) {
      this.redisTemplate.delete(key);
    }
  }

}