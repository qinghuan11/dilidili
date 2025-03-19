package com.dilidili.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dilidili.dao.domain.Video;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
@Component
public class VideoMapper implements BaseMapper<Video> {
    @Override

    public int insert(Video entity) {
        return 0;
    }

    @Bean
    @Override
    public int deleteById(Serializable id) {
        return 0;
    }

    @Override
    public int deleteById(Video entity) {
        return 0;
    }

    @Override
    public int deleteByMap(Map<String, Object> columnMap) {
        return 0;
    }

    @Override
    public int delete(Wrapper<Video> queryWrapper) {
        return 0;
    }

    @Override
    public int deleteBatchIds(Collection<?> idList) {
        return 0;
    }

    @Override
    public int updateById(Video entity) {
        return 0;
    }

    @Override
    public int update(Video entity, Wrapper<Video> updateWrapper) {
        return 0;
    }

    @Override
    public Video selectById(Serializable id) {
        return null;
    }

    @Override
    public List<Video> selectBatchIds(Collection<? extends Serializable> idList) {
        return List.of();
    }

    @Override
    public List<Video> selectByMap(Map<String, Object> columnMap) {
        return List.of();
    }

    @Override
    public Long selectCount(Wrapper<Video> queryWrapper) {
        return 0L;
    }

    @Override
    public List<Video> selectList(Wrapper<Video> queryWrapper) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> selectMaps(Wrapper<Video> queryWrapper) {
        return List.of();
    }

    @Override
    public List<Object> selectObjs(Wrapper<Video> queryWrapper) {
        return List.of();
    }

    @Override
    public <P extends IPage<Video>> P selectPage(P page, Wrapper<Video> queryWrapper) {
        return null;
    }

    @Override
    public <P extends IPage<Map<String, Object>>> P selectMapsPage(P page, Wrapper<Video> queryWrapper) {
        return null;
    }
}