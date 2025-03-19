package com.dilidili.api;

import com.dilidili.common.JwtUtil;
import com.dilidili.common.Result;
import com.dilidili.dao.domain.Video;
import com.dilidili.service.VideoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @Value("${video.storage.path}")
    private String storagePath;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 上传视频
     *
     * @param file        视频文件
     * @param title       视频标题
     * @param description 视频描述
     * @param authorizationHeader 认证头
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<Result<String>> uploadVideo(
            @RequestPart MultipartFile file,
            @RequestPart @NotBlank(message = "Title cannot be empty") String title,
            @RequestPart String description,
            @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = getCurrentUserId(authorizationHeader);
        String result = videoService.uploadVideo(file, title, description, userId);
        return ResponseEntity.ok(Result.success(result));
    }

    /**
     * 播放视频
     *
     * @param id 视频ID
     * @return 视频文件流
     */
    @GetMapping(value = "/{id}/play", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> playVideo(@PathVariable Long id) {
        videoService.incrementViewCount(id); // 增加播放量
        Video video = videoService.getById(id);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(storagePath + video.getFilePath());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resource);
    }

    /**
     * 点赞视频
     *
     * @param id 视频ID
     * @param authorizationHeader 认证头
     * @return 点赞结果
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Result<String>> likeVideo(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        videoService.incrementLikeCount(id); // 增加点赞数
        return ResponseEntity.ok(Result.success("Video liked successfully"));
    }

    /**
     * 查询视频列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段（viewCount 或 likeCount）
     * @param order 排序方式（asc 或 desc）
     * @return 视频列表
     */
    @GetMapping("/list")
    public ResponseEntity<Result<Page<Video>>> listVideos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "viewCount") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        Page<Video> videoPage = videoService.listVideos(page, size, sortBy, order);
        return ResponseEntity.ok(Result.success(videoPage));
    }

    /**
     * 搜索视频
     *
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param order 排序方式
     * @return 视频列表
     */
    @GetMapping("/search")
    public ResponseEntity<Result<Page<Video>>> searchVideos(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "viewCount") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        Page<Video> videoPage = videoService.searchVideos(keyword, page, size, sortBy, order);
        return ResponseEntity.ok(Result.success(videoPage));
    }

    /**
     * 删除视频
     *
     * @param id 视频ID
     * @param authorizationHeader 认证头
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<String>> deleteVideo(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = getCurrentUserId(authorizationHeader);
        videoService.deleteVideo(id, userId);
        return ResponseEntity.ok(Result.success("Video deleted successfully"));
    }

    /**
     * 更新视频
     *
     * @param id 视频ID
     * @param request 更新请求
     * @param authorizationHeader 认证头
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Result<String>> updateVideo(
            @PathVariable Long id,
            @RequestBody UpdateVideoRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = getCurrentUserId(authorizationHeader);
        videoService.updateVideo(id, request.getTitle(), request.getDescription(), userId);
        return ResponseEntity.ok(Result.success("Video updated successfully"));
    }

    static class UpdateVideoRequest {
        @NotBlank(message = "Title cannot be empty")
        private String title;
        private String description;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    private Long getCurrentUserId(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtUtil.getUserIdFromToken(token);
    }
}
