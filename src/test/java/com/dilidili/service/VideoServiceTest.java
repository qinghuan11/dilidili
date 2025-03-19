package com.dilidili.service;

import com.dilidili.dao.domain.Video;
import com.dilidili.dao.mapper.VideoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VideoServiceTest {

    @InjectMocks
    private VideoService videoService;

    @Mock
    private VideoMapper videoMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    public void setUp() {
        // 使用 lenient() 放宽严格性
        lenient().when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));

        // 设置 storagePath
        setField(videoService, "storagePath", "./tmp/test-storage/");
    }

    @Test
    void testUploadVideo() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "test content".getBytes());
        String title = "Test Video";
        String description = "Test Description";
        Long userId = 1L;

        when(videoMapper.insert(any(Video.class))).thenAnswer(invocation -> {
            Video video = invocation.getArgument(0);
            video.setId(1L);
            return 1;
        });

        MockMultipartFile mockFile = spy(file);
        doNothing().when(mockFile).transferTo(any(File.class));

        String filename = videoService.uploadVideo(mockFile, title, description, userId);
        assertNotNull(filename);
        assertTrue(filename.endsWith("test.mp4"));

        Video mockVideo = new Video();
        mockVideo.setId(1L);
        mockVideo.setTitle(title);
        mockVideo.setDescription(description);
        mockVideo.setFilePath(filename);
        mockVideo.setUserId(userId);
        mockVideo.setViewCount(0L);
        mockVideo.setLikeCount(0L);
        when(videoMapper.selectById(1L)).thenReturn(mockVideo);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);

        Video video = videoService.getById(1L);
        assertNotNull(video);
        assertEquals(title, video.getTitle());
    }

    @Test
    void testUploadInvalidFileType() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        assertThrows(IllegalArgumentException.class, () -> videoService.uploadVideo(file, "Title", "Description", 1L));
    }

    // 辅助方法：设置私有字段值
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}