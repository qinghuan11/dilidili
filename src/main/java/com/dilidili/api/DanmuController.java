package com.dilidili.api;

import com.dilidili.common.Result;
import com.dilidili.dao.domain.Danmu;
import com.dilidili.service.DanmuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos/{videoId}/danmu")
public class DanmuController {

    @Autowired
    private DanmuService danmuService;

    /**
     * 发布一条弹幕
     */
    @PostMapping
    public ResponseEntity<Result<String>> addDanmu(
            @PathVariable Long videoId,
            @Valid @RequestBody Danmu danmu) {
        danmu.setVideoId(videoId);
        danmuService.addDanmu(danmu);
        return ResponseEntity.ok(Result.success("弹幕发布成功"));
    }

    /**
     * 分页获取弹幕
     */
    @GetMapping("/page")
    public ResponseEntity<Result<?>> listDanmu(
            @PathVariable Long videoId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size) {
        return ResponseEntity.ok(Result.success(danmuService.listDanmuByVideo(videoId, page, size)));
    }

    /**
     * 获取全部弹幕（前端可用于一次性加载）
     */
    @GetMapping
    public ResponseEntity<Result<?>> getAllDanmu(@PathVariable Long videoId) {
        return ResponseEntity.ok(Result.success(danmuService.getAllDanmuByVideo(videoId)));
    }
}

