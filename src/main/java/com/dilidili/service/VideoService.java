package com.dilidili.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dilidili.dao.domain.Video;
import com.dilidili.dao.mapper.VideoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class VideoService extends ServiceImpl<VideoMapper, Video> {
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);
    private static final String VIDEO_CACHE_KEY_PREFIX = "video:id:";
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("mp4", "avi", "mkv");

    @Autowired
    private VideoMapper videoMapper;

    @Value("${video.storage.path}")
    private String storagePath;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 上传视频
     *
     * @param file        视频文件
     * @param title       视频标题
     * @param description 视频描述
     * @param userId      上传用户ID
     * @return 视频文件名
     */
    public String uploadVideo(MultipartFile file, String title, String description, Long userId) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidExtension(originalFilename)) {
            throw new IllegalArgumentException("Invalid file type. Allowed types: " + ALLOWED_EXTENSIONS);
        }

        String filename = UUID.randomUUID().toString() + "_" + originalFilename;
        String filePath = storagePath + filename;
        try {
            File directory = new File(storagePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload video: " + e.getMessage());
        }

        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setFilePath(filename);
        video.setUserId(userId);
        video.setViewCount(0L);
        video.setLikeCount(0L);
        videoMapper.insert(video);

        cacheVideo(video);
        logger.info("Video uploaded successfully: {} by user {}", filename, userId);
        return filename;
    }

    /**
     * 根据ID获取视频
     *
     * @param id 视频ID
     * @return 视频对象，未找到返回 null
     */
    public Video getById(Long id) {
        String cacheKey = VIDEO_CACHE_KEY_PREFIX + id;
        Video cachedVideo = (Video) redisTemplate.opsForValue().get(cacheKey);
        if (cachedVideo != null) {
            logger.debug("Video found in cache: {}", id);
            return cachedVideo;
        }

        Video video = videoMapper.selectById(id);
        if (video != null) {
            cacheVideo(video);
        }
        return video;
    }

    /**
     * 查询视频列表
     *
     * @param page   页码
     * @param size   每页大小
     * @param sortBy 排序字段（viewCount 或 likeCount）
     * @param order  排序方式（asc 或 desc）
     * @return 视频列表
     */
    public Page<Video> listVideos(int page, int size, String sortBy, String order) {
        Page<Video> videoPage = new Page<>(page, size);
        QueryWrapper<Video> wrapper = new QueryWrapper<>();
        wrapper.orderBy(true, "asc".equalsIgnoreCase(order), sortBy);
        page(videoPage, wrapper);
        return videoPage;
    }

    /**
     * 增加播放量
     *
     * @param id 视频ID
     */
    public void incrementViewCount(Long id) {
        Video video = getById(id);
        if (video != null) {
            video.setViewCount(video.getViewCount() + 1);
            updateById(video);
            evictVideoCache(id);
        }
    }

    /**
     * 增加点赞数
     *
     * @param id 视频ID
     */
    public void incrementLikeCount(Long id) {
        Video video = getById(id);
        if (video != null) {
            video.setLikeCount(video.getLikeCount() + 1);
            updateById(video);
            evictVideoCache(id);
        }
    }

    /**
     * 删除视频
     *
     * @param id     视频ID
     * @param userId 用户ID
     */
    public void deleteVideo(Long id, Long userId) {
        Video video = getById(id);
        if (video == null) {
            throw new IllegalArgumentException("Video not found");
        }
        if (!video.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own videos");
        }
        File file = new File(storagePath + video.getFilePath());
        if (file.exists()) {
            file.delete();
        }
        removeById(id);
        evictVideoCache(id);
        logger.info("Video deleted successfully: {} by user {}", id, userId);
    }

    /**
     * 更新视频
     *
     * @param id          视频ID
     * @param title       标题
     * @param description 描述
     * @param userId      用户ID
     */
    public void updateVideo(Long id, String title, String description, Long userId) {
        Video video = getById(id);
        if (video == null) {
            throw new IllegalArgumentException("Video not found");
        }
        if (!video.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own videos");
        }
        video.setTitle(title);
        video.setDescription(description);
        updateById(video);
        evictVideoCache(id);
        logger.info("Video updated successfully: {} by user {}", id, userId);
    }

    /**
     * 搜索视频
     *
     * @param keyword 关键词
     * @param page    页码
     * @param size    每页大小
     * @param sortBy  排序字段
     * @param order   排序方式
     * @return 视频列表
     */
    public Page<Video> searchVideos(String keyword, int page, int size, String sortBy, String order) {
        Page<Video> videoPage = new Page<>(page, size);
        QueryWrapper<Video> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("title", keyword);
        }
        wrapper.orderBy(true, "asc".equalsIgnoreCase(order), sortBy);
        page(videoPage, wrapper);
        return videoPage;
    }

    /**
     * 缓存视频信息到 Redis
     *
     * @param video 视频对象
     */
    private void cacheVideo(Video video) {
        String cacheKey = VIDEO_CACHE_KEY_PREFIX + video.getId();
        try {
            redisTemplate.opsForValue().set(cacheKey, video, 1, TimeUnit.HOURS);
            logger.debug("Video cached: {}", video.getId());
        } catch (Exception e) {
            logger.error("Failed to cache video: {}", video.getId(), e);
        }
    }

    /**
     * 清除视频缓存
     *
     * @param id 视频ID
     */
    public void evictVideoCache(Long id) {
        String cacheKey = VIDEO_CACHE_KEY_PREFIX + id;
        try {
            redisTemplate.delete(cacheKey);
            logger.debug("Video cache evicted: {}", id);
        } catch (Exception e) {
            logger.error("Failed to evict video cache: {}", id, e);
        }
    }

    /**
     * 校验文件扩展名
     *
     * @param filename 文件名
     * @return 是否有效
     */
    private boolean isValidExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    @Override
    public boolean save(Video entity) {
        return super.save(entity);
    }
}