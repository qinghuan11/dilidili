package com.dilidili.dao.domain;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_danmu")
public class Danmu {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotNull(message = "视频ID不能为空")
    private Long videoId;
    @NotBlank(message = "弹幕内容不能为空")
    private String content;
    @NotNull(message = "弹幕时间戳不能为空")
    private Integer timestamp; // 单位：秒
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}

