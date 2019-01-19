package com.lyle.db.distributedLock.database;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javafx.util.Pair;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 基于mysql主键的锁
 *
 * @author Lyle
 * @version v1.0
 * @since 1.8
 */
@Slf4j
@Component
public class MysqlLockViaPk {

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
   * 获取锁
   */
  public boolean lock() {
    String sql = "insert ignore into mysql_pk_lock(id,count,thread_name,locked_time) VALUES (?,?,?,?)";
    while (true) {
      if (insertLock(sql)) {
        return true;
      }
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private boolean insertLock(String sql) {
    Connection connection = getConn();
    try {
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, LOCK_ID);
      statement.setInt(2, 1);
      statement.setString(3, "");
      statement.setLong(4, System.currentTimeMillis());
      final int i = statement.executeUpdate();
      System.out.println("=====插入记录数：======>" + i);
      return i > 0;//如果成功，那么就是获取到了锁
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }finally {
      if(null != connection){
        try {
          connection.close();
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
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static <T> Pair<Boolean, T> lockedWork(MysqlLockViaPk mysqlPKLock, Supplier<T> worker) {
    if (!mysqlPKLock.lock()) {
      log.error(String.format("未获取到锁，任务取消，锁：%s", LOCK_ID));
      return new Pair<>(false, null);
    }
    try {
      log.warn(String.format("已获取到锁，开始执行锁内程序，锁：%s", LOCK_ID));
      return new Pair<>(true,worker.get());
    } catch (Exception e) {
      log.error(String.format("未获取到锁，任务取消，锁：%s", LOCK_ID));
      return new Pair<>(false, null);
    }finally {
      mysqlPKLock.unlock();
      log.warn(String.format("释放锁：%s", LOCK_ID));
    }
  }
}