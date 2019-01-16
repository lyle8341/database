package com.lyle.db.controller;

import com.lyle.db.distributedLock.cache.redis.RedisLock;
import java.util.concurrent.CountDownLatch;
import javafx.util.Pair;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试,CountDownLatch目的是测试完成恢复count的值
 *
 * @author Lyle
 * @version v1.0
 * @since 1.8
 */
@RestController
@Slf4j
public class TestController {

  private int count = 100;

  @Resource
  private StringRedisTemplate stringRedisTemplate;

  @GetMapping("/unlock")
  public void unlock() {
    CountDownLatch countDownLatch = new CountDownLatch(100);
    for (int i = 0; i < 100; i++) {
      new Thread(() -> {
        log.info("无锁状态: " + count--);
        countDownLatch.countDown();
      }).start();
    }
    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    count = 100;
  }

  @GetMapping("/redisLock")
  public void testRedisLock() {
    CountDownLatch countDownLatch = new CountDownLatch(100);
    for (int i = 0; i < 100; i++) {
      new Thread(() -> {
        Pair<Boolean, String> result = RedisLock.lockedWork(
            new RedisLock(stringRedisTemplate, "lockKey"),2,100,
            () -> {
              // 锁内程序
              return "我是锁内程序执行结果:" + count--;
            }
        );
        countDownLatch.countDown();
        if (result.getKey()) {
          log.info("锁内程序成功调用，并返回数据：" + result.getValue());
        } else {
          log.info("未获取到锁");
        }
      }).start();
    }
    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    count = 100;
  }
}