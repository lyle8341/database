package com.lyle;

import com.lyle.db.bean.First;
import com.lyle.db.mapper.FirstMapper;
import com.lyle.spring_tx.programmatic.ProgramTx;
import javax.annotation.Resource;
import org.junit.Test;

public class DatabasesApplicationTests extends BaseTest{

  @Resource
  private FirstMapper firstMapper;
  @Resource
  private ProgramTx programTx;

  @Test
  public void contextLoads() {
    final First f = firstMapper.getById(1);
    System.out.println(f);
  }

  @Test
  public void testProgram1() {
    programTx.test1("1");
  }

  @Test
  public void testProgram2() {
    programTx.test1("异常");
  }

}

