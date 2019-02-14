package com.lyle.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 
 * @author Lyle
 * @date 2019-02-14 下午5:03
 * @version v1.0
 * @since 1.8  
 */
@Configuration
public class PrimaryDataSourceConfig{

  @Bean(name = "primaryDataSource")
  @Primary
  @ConfigurationProperties(prefix = "hikari.primary")
  public HikariDataSource dataSource() {
    return new HikariDataSource();
  }

  @Bean(name = "primaryJdbcTemplate")
  @Primary
  public JdbcTemplate jdbcTemplate() {
    return new JdbcTemplate(this.dataSource());
  }
}