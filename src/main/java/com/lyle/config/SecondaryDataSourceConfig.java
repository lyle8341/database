package com.lyle.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class SecondaryDataSourceConfig extends DataSourceAutoConfiguration {

  @Bean(name = "dataSourceSec")
  @ConfigurationProperties(prefix = "spring.datasource.secondary")
  public DataSource secondaryDataSource(DataSourceProperties properties) {
    return DataSourceBuilder.create(properties.getClassLoader()).type(HikariDataSource.class)
        .driverClassName(properties.determineDriverClassName())
        .url(properties.determineUrl()).username(properties.determineUsername())
        .password(properties.determinePassword()).build();
  }

  @Bean(name = "jdbcTemplateSec")
  public JdbcTemplate secondaryJdbcTemplate(@Qualifier("dataSourceSec") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}