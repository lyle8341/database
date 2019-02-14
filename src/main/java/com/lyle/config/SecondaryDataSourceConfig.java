package com.lyle.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Lyle
 * @version v1.0
 * @date 2019-02-14 下午5:03
 * @since 1.8
 */
@Configuration
//@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class SecondaryDataSourceConfig{

  @Bean(name = "secondDataSource")
  @ConfigurationProperties(prefix = "hikari.second")
  public HikariDataSource dataSource() {
    return new HikariDataSource();
  }

  @Bean(name = "secondJdbcTemplate")
  public JdbcTemplate jdbcTemplate() {
    return new JdbcTemplate(this.dataSource());
  }
}