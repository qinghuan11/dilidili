/*
 Navicat Premium Data Transfer

 Source Server         : Local MySQL
 Source Server Type    : MySQL
 Source Server Version : 80200 (8.2.0)
 Source Host           : localhost:3306
 Source Schema         : bilibili
 Target Server Type    : MySQL
 Target Server Version : 80200 (8.2.0)
 File Encoding         : 65001

 Date: 16/03/2025
*/
-- ----------------------------
-- Create database dilidili
-- ----------------------------
DROP DATABASE IF EXISTS `dilidili`;
CREATE DATABASE `dilidili` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Use the dilidili database
USE `dilidili`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;

CREATE TABLE `t_user` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                          `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                          `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
                          `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址',
                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`) USING BTREE,
                          UNIQUE KEY `uk_username` (`username`) USING BTREE COMMENT '用户名唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC COMMENT = '用户信息表';

-- ----------------------------
-- Table structure for t_video
-- ----------------------------
DROP TABLE IF EXISTS `t_video`;

CREATE TABLE `t_video` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                           `title` VARCHAR(100) NOT NULL COMMENT '视频标题',
                           `description` TEXT DEFAULT NULL COMMENT '视频描述',
                           `file_path` VARCHAR(255) NOT NULL COMMENT '视频文件存储路径',
                           `user_id` BIGINT NOT NULL COMMENT '上传者ID（关联到t_user表）',
                           `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`) USING BTREE,
                           KEY `fk_video_user` (`user_id`) USING BTREE COMMENT '上传者外键索引',
                           CONSTRAINT `fk_video_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC COMMENT = '视频信息表';

-- ----------------------------
-- Table structure for t_danmu
-- ----------------------------
DROP TABLE IF EXISTS `t_danmu`;

CREATE TABLE `t_danmu` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                           `video_id` BIGINT NOT NULL COMMENT '视频ID（关联到t_video表）',
                           `content` VARCHAR(255) DEFAULT NULL COMMENT '弹幕内容',
                           `timestamp` INT NOT NULL COMMENT '弹幕出现时间（单位：秒）',
                           `user_id` BIGINT NOT NULL COMMENT '发送者ID（关联到t_user表）',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           PRIMARY KEY (`id`) USING BTREE,
                           KEY `fk_danmu_video` (`video_id`) USING BTREE COMMENT '视频外键索引',
                           KEY `fk_danmu_user` (`user_id`) USING BTREE COMMENT '用户外键索引',
                           CONSTRAINT `fk_danmu_video` FOREIGN KEY (`video_id`) REFERENCES `t_video` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                           CONSTRAINT `fk_danmu_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC COMMENT = '弹幕信息表';

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `t_user` (`username`, `password`, `email`)
VALUES
    ('alice', 'securepassword1', 'alice@example.com'),
    ('bob', 'securepassword2', 'bob@example.com');
