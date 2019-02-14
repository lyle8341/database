package com.lyle.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
public class PrimaryDataSourceConfig extends DataSourceAutoConfiguration {

  @Bean(name = "dataSourceFirst")
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource dataSourceFirst(DataSourceProperties properties) {
    return DataSourceBuilder.create(properties.getClassLoader()).type(HikariDataSource.class)
        .driverClassName(properties.determineDriverClassName())
        .url(properties.determineUrl()).username(properties.determineUsername())
        .password(properties.determinePassword()).build();
  }

  @Bean(name = "jdbcTemplateFirst")
  @Primary
  public JdbcTemplate jdbcTemplateFirst(@Qualifier("dataSourceFirst") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}