package com.lyle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.lyle.db.mapper"})
public class DatabasesApplication {

  public static void main(String[] args) {
    SpringApplication.run(DatabasesApplication.class, args);
  }

}

