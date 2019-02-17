package com.lyle;

import java.util.Map;
import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * <b>@Resource === @Autowired @Qualifier("beanName")</b>
 */
@ActiveProfiles("multiDSHikari")
public class MultiDataSourceTests extends BaseTest{

  @Resource
  /*@Autowired
  @Qualifier("primaryJdbcTemplate")*/
  private JdbcTemplate primaryJdbcTemplate;

  @Resource
  /*@Autowired
  @Qualifier("secondJdbcTemplate")*/
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