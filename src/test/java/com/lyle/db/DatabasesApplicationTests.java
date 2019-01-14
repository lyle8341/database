package com.lyle.db;

import com.lyle.db.bean.First;
import com.lyle.db.mapper.FirstMapper;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabasesApplicationTests {

  @Resource
  private FirstMapper firstMapper;

  @Test
  public void contextLoads() {
    final First f = firstMapper.getById(1);
    System.out.println(f);
  }

}

