package com.dilidili.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dilidili.dao.domain.Danmu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface DanmuMapper extends BaseMapper<Danmu> {
    // 继承 BaseMapper 后即可获得 CRUD 方法
}

