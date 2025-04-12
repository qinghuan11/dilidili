package com.dilidili.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dilidili.dao.domain.Danmu;
import com.dilidili.dao.mapper.DanmuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DanmuService extends ServiceImpl<DanmuMapper, Danmu> {

    @Autowired
    private DanmuMapper danmuMapper;

    /**
     * 发布弹幕
     */
    public void addDanmu(Danmu danmu) {
        save(danmu);
    }

    /**
     * 分页获取某视频的弹幕列表
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
     */
    public List<Danmu> getAllDanmuByVideo(Long videoId) {
        QueryWrapper<Danmu> wrapper = new QueryWrapper<>();
        wrapper.eq("video_id", videoId)
                .orderByAsc("timestamp");
        return danmuMapper.selectList(wrapper);
    }
}

