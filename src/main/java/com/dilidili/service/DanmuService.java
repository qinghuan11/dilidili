package com.dilidili.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dilidili.api.mapper.DanmuDtoMapper;
import com.dilidili.dao.domain.Danmu;
import com.dilidili.dao.mapper.DanmuMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 弹幕相关服务
 */
@Service
public class DanmuService extends ServiceImpl<DanmuMapper, Danmu> {

    @Autowired
    private DanmuMapper danmuMapper;

    @Autowired
    private DanmuDtoMapper danmuDtoMapper;

    /**
     * 发布弹幕
     *
     * @param danmu 弹幕对象
     */
    public void addDanmu(Danmu danmu) {
        Danmu entity = danmuDtoMapper.toEntity(danmu);
        save(entity);
    }

    /**
     * 分页获取某视频的弹幕列表
     *
     * @param videoId 视频ID
     * @param page    页码
     * @param size    每页大小
     * @return 弹幕分页列表
     */
    public Page<Danmu> listDanmuByVideo(Long videoId, int page, int size) {
        Page<Danmu> danmuPage = new Page<>(page, size);
        QueryWrapper<Danmu> wrapper = new QueryWrapper<>();
        wrapper.eq("video_id", videoId)
                .orderByAsc("timestamp");
        page(danmuPage, wrapper);
        return danmuPage;
    }

    /**
     * 获取某视频所有弹幕（不分页）
     *
     * @param videoId 视频ID
     * @return 弹幕列表
     */
    public List<Danmu> getAllDanmuByVideo(Long videoId) {
        QueryWrapper<Danmu> wrapper = new QueryWrapper<>();
        wrapper.eq("video_id", videoId)
                .orderByAsc("timestamp");
        return danmuMapper.selectList(wrapper);
    }
}
