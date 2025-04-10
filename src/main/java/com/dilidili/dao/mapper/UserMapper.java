package com.dilidili.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dilidili.dao.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 可以添加自定义方法，例如：
    // List<User> findByName(String name);
}