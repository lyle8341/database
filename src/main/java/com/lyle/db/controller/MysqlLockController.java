package com.lyle.db.controller;

import com.lyle.db.distributedLock.database.MysqlLockViaPk;
import javafx.util.Pair;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Lyle
 * @version v1.0
 * @since 1.8
 */
@RestController
@Slf4j
public class MysqlLockController {

  @Resource
  private MysqlLockViaPk mysqlLockViaPk;

  private int count = 100;

  @GetMapping("/pkLock")
  public void mysqlPkLock(){
    for (int i = 0; i < 100; i++) {
      new Thread(()->{
        final Pair<Boolean, String> pair = MysqlLockViaPk.lockedWork(mysqlLockViaPk, () -> {
          //锁内程序
          return "我是锁内程序执行结果: " + count--;
        });
        if (pair.getKey()) {
          log.info("锁内程序成功调用，并返回数据：" + pair.getValue());
        } else {
          log.info("未获取到锁");
        }
      }).start();
    }
  }
}