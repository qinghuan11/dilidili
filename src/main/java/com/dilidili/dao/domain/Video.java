package com.dilidili.dao.domain;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Date;

@Data
@TableName("t_video")
public class Video {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "Title cannot be empty")
    private String title;
    private String description;
    @NotBlank(message = "File path cannot be empty")
    private String filePath;
    private Long userId;
    private Long viewCount = 0L;
    private Long likeCount = 0L;
    @TableField(fill = FieldFill.INSERT)
    private Date uploadTime;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}