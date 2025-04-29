# Dilidili - 视频分享平台 🎥

目前已经有第二版，访问 [GitHub 仓库](https://github.com/qinghuan11/dilidili_springcloud) 查看源码。
---

Dilidili 是一个受BiliBili启发的视频分享平台，使用Spring Boot和MyBatis-Plus构建，支持用户认证、视频管理、评论、Redis缓存和JWT安全机制，为现代视频分享应用提供坚实基础！😊

## 核心功能 ✨

- **视频上传与播放**：轻松上传和流畅播放视频 📤
- **视频点赞与评论**：通过点赞和评论与视频互动 💬
- **用户管理**：使用JWT实现安全的用户注册与登录 🔒
- **密码加密**：通过Spring Security保护用户密码 🛡️
- **JWT黑名单**：登出时将失效的JWT存入Redis黑名单 🚫
- **视频存储**：使用MySQL管理视频元数据和文件 🎬
- **Redis缓存**：通过Redis缓存用户和视频数据，提升性能 ⚡
- **分布式锁**：使用Redis分布式锁防止缓存击穿 🔧
- **安全机制**：通过Spring Security和JWT实现基于角色的访问控制 🔐

## 技术栈 🛠️

- **Spring Boot**：后端框架（版本3.4.3）
- **MyBatis-Plus**：数据库操作的ORM框架
- **MySQL**：主数据库，用于数据存储
- **Redis**：用于缓存和会话管理
- **JWT**：基于令牌的认证
- **Spring Security**：保护API和端点安全
- **H2数据库**：测试用的内存数据库
- **Swagger**：通过Springdoc OpenAPI提供API文档

## 项目结构 📂

项目是一个基于Spring Boot的后端应用，结合MySQL存储数据、Redis缓存、JWT和Spring Security实现安全认证，适合快速开发和扩展。

## 快速开始 🚀

### 前置条件
- **Java 21**：确保安装了Java 21（项目依赖指定版本）
- **MySQL**：本地运行MySQL服务（默认端口3306）
- **Redis**：本地运行Redis服务（默认端口6379）
- **Maven**：用于构建和运行项目

### 安装步骤
1. 克隆仓库：
   ```bash
   git clone https://github.com/qinghuan11/dilidili.git
   cd dilidili
   ```
2. 安装依赖：
   ```bash
   mvn install
   ```
3. 配置`application.yml`（参考下方示例）。
4. 启动应用：
   ```bash
   mvn spring-boot:run
   ```
5. 访问API文档：启动后打开 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) 查看Swagger UI 📜

### 配置示例
在`src/main/resources/application.yml`中配置MySQL、Redis和JWT：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dilidili?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    redis:
      host: localhost
      port: 6379
jwt:
  secret: your_jwt_secret
  expiration: 86400000 # 24小时（毫秒）
```

**注意**：请根据实际环境替换`your_password`和`your_jwt_secret`。

## API测试 🧪

使用Postman或Swagger UI测试API端点：
- 注册：`POST /api/auth/register`
- 登录：`POST /api/auth/login`
- 上传视频：`POST /api/videos/upload`（需JWT认证）

Swagger UI提供完整的API文档，启动应用后访问 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)。

## 开发与贡献 🤝

欢迎为Dilidili贡献代码！以下是参与步骤：
1. Fork本仓库。
2. 创建特性分支：`git checkout -b feature/your-feature`。
3. 提交更改：`git commit -m "添加你的功能"`。
4. 推送到分支：`git push origin feature/your-feature`。
5. 提交Pull Request，描述你的更改。

我们欢迎测试、文档、新功能等贡献！😄

## 未来功能 🔮

- **视频推荐系统**：基于用户行为的智能推荐 🎯
- **社交互动**：关注、消息等社交功能 💌
- **容器化**：使用Docker部署 📦
- **监控**：集成Prometheus和Grafana 📊
- **异步上传**：计划使用RabbitMQ等消息队列实现异步视频上传 ⏩

