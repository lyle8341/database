package com.lyle;

import java.util.Map;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class MultiDataSourceTests extends BaseTest{

  @Autowired
//  @Qualifier("jdbcTemplateFirst")
  private JdbcTemplate jdbcTemplateFirst;

  @Autowired
//  @Qualifier("jdbcTemplateSec")
  private JdbcTemplate jdbcTemplateSec;

  @Test
  public void contextLoads() {
    final Map<String, Object> firstMap = jdbcTemplateFirst
        .queryForMap("select * from first where id = 1");
    System.out.println(firstMap);
    final Map<String, Object> secondMap = jdbcTemplateSec
        .queryForMap("select * from second where id = 1");
    System.out.println(secondMap);
  }
}