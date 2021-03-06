package com.lyle.db.mapper;

import com.lyle.db.bean.First;
import org.apache.ibatis.annotations.Select;

/**
 * @author Lyle
 * @version v1.0
 * @since 1.8
 */
public interface FirstMapper {

  @Select("select * from first where id = #{id}")
  First getById(int id);
}