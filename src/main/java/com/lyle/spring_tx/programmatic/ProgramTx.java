package com.lyle.spring_tx.programmatic;

import com.zaxxer.hikari.HikariDataSource;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 编程式事务 https://blog.csdn.net/liaohaojian/article/details/70139151
 *
 * @author Lyle
 * @version v1.0
 * @date 2019-02-14 上午11:17
 * @since 1.8
 */
@Repository
@Slf4j
public class ProgramTx {

  @Resource
  private PlatformTransactionManager txManager;
  @Resource
  private HikariDataSource hikariDataSource;

  private JdbcTemplate jdbcTemplate;
  private static final String INSERT_SQL = "insert into test_tx(num) values(?)";
  private static final String COUNT_SQL = "select count(id) from test_tx";

  /**
   * PlatformTransactionManager
   */
  public void test1(String value) {
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    TransactionStatus status = txManager.getTransaction(def);
    jdbcTemplate = new JdbcTemplate(hikariDataSource);
    Integer i = jdbcTemplate.queryForObject(COUNT_SQL, Integer.class);
    System.out.println("表中记录总数：" + i);
    try {
      jdbcTemplate.update(INSERT_SQL, value);
      txManager.commit(status);  //提交status中绑定的事务
    } catch (RuntimeException e) {
      txManager.rollback(status);  //回滚
      log.error("回滚事务...");
    }
    i = jdbcTemplate.queryForObject(COUNT_SQL, Integer.class);
    System.out.println("表中记录总数：" + i);
  }

  /**
   * TransactionTemplate
   */
  public void test2(String value) {
    jdbcTemplate = new JdbcTemplate(hikariDataSource);
    Integer i = jdbcTemplate.queryForObject(COUNT_SQL, Integer.class);
    System.out.println("表中记录总数：" + i);
    TransactionTemplate template = new TransactionTemplate(txManager);
    template.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
    template.execute(new TransactionCallbackWithoutResult() {
                       @Override
                       protected void doInTransactionWithoutResult(TransactionStatus status) {
                         jdbcTemplate
                             .update(INSERT_SQL, value);  //字段num为int型，所以插入肯定失败报异常，自动回滚，代表TransactionTemplate自动管理事务
                       }
                     }
    );
    i = jdbcTemplate.queryForObject(COUNT_SQL, Integer.class);
    System.out.println("表中记录总数：" + i);
  }
}