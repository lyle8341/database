package com.lyle;

import java.util.Map;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * 必须使用@Qualifier指定
 */
@ActiveProfiles("multiDSHikari")
public class MultiDataSourceTests extends BaseTest{

  @Autowired
//  @Qualifier("primaryJdbcTemplate")
  private JdbcTemplate primaryJdbcTemplate;

  @Autowired
//  @Qualifier("secondJdbcTemplate")
  private JdbcTemplate secondJdbcTemplate;

  @Test
  public void contextLoads() {
    final Map<String, Object> firstMap = primaryJdbcTemplate
        .queryForMap("select * from first where id = 1");
    System.out.println(firstMap);
    final Map<String, Object> secondMap = secondJdbcTemplate
        .queryForMap("select * from second where id = 1");
    System.out.println(secondMap);
  }
}