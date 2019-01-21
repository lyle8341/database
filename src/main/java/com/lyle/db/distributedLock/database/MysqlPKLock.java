package com.lyle.db.distributedLock.database;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javafx.util.Pair;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 基于mysql主键的锁
 * insert发生死锁：https://cloud.tencent.com/developer/article/1056372
 * https://www.cnblogs.com/olinux/p/5497176.html show engine innodb status
 * https://zzyongx.github.io/blogs/mysql-acid-lock.html
 * @author Lyle
 * @version v1.0
 * @since 1.8
 */
@Slf4j
@Component
public class MysqlPKLock {

  private static final String LOCK_ID = "lyle";

  @Resource
  private HikariDataSource hikariDataSource;

  private Connection getConn() {
    try {
      Connection conn = hikariDataSource.getConnection();
      System.out.println("获取连接..." + conn.hashCode());
      return conn;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("获取链接失败");
    }
  }

  /**
   * 加锁
   */
  private boolean lock() {
    return acquire();
  }

  /**
   * 加锁
   */
  private boolean lock(long timeout) {
    return acquire(timeout);
  }

  /**
   * 获取锁
   */
  private boolean acquire() {
    String sql = "insert ignore into mysql_pk_lock(id,count,thread_name,locked_time) VALUES (?,?,?,?)";
    while (true) {
      if (insertLock(sql)) {
        return true;
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 超时获取锁
   *
   * @param timeOuts 超时ms
   */
  private boolean acquire(long timeOuts) {
    String sql = "insert ignore into mysql_pk_lock(id,count,thread_name,locked_time) VALUES (?,?,?,?)";
    long futureTime = System.currentTimeMillis() + timeOuts;
    long remain;//距超时还有多久
    long timeRange = 500;//获取锁的间隔时间
    CountDownLatch latch = new CountDownLatch(1);
    while (true) {
      if (insertLock(sql)) {
        return true;
      }
      try {
        latch.await(timeRange, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      remain = futureTime - System.currentTimeMillis();
      if (remain <= 0) {
        break;
      }
      if (remain < timeRange) {
        timeRange = remain;
      }
    }
    return false;
  }

  private boolean insertLock(String sql) {
    Connection connection = getConn();
    int i;
    try {
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, LOCK_ID);
      statement.setInt(2, 1);
      statement.setString(3, "");
      statement.setLong(4, System.currentTimeMillis());
      i = statement.executeUpdate();//如果成功，那么就是获取到了锁

      return i > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    } finally {
      if (null != connection) {
        try {
          connection.close();
          System.out.println("关闭链接..." + connection.hashCode());
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * 释放锁
   */
  private void unlock() {
    try {
      String sql = "DELETE  from mysql_pk_lock where id = ?";
      PreparedStatement statement = getConn().prepareStatement(sql);
      statement.setString(1, LOCK_ID);
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static <T> Pair<Boolean, T> lockedWork(MysqlPKLock mysqlPKLock, Supplier<T> worker,
      Long timeout) {

    if (mysqlPKLock == null || worker == null) {
      throw new RuntimeException("本地锁或mysqlPKLock对象或锁内程序不能为空");
    }
    if (null == timeout) {
      if (!mysqlPKLock.lock()) {
        log.error(String.format("未获取到锁，任务取消，锁：%s", LOCK_ID));
        return new Pair<>(false, null);
      }
    } else {
      if (!mysqlPKLock.lock(timeout)) {
        log.error(String.format("未获取到锁，任务取消，锁：%s", LOCK_ID));
        return new Pair<>(false, null);
      }
    }
    try {
      log.warn(String.format("已获取到锁，开始执行锁内程序，锁：%s", LOCK_ID));
      return new Pair<>(true, worker.get());
    } finally {
      mysqlPKLock.unlock();
      log.warn(String.format("释放锁：%s", LOCK_ID));
    }
  }
}