package com.dilidili.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dilidili.dao.domain.Video;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    // 可以添加自定义方法，例如：
    // Video findByTitle(String title);
}