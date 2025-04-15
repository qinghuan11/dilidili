# Dilidili - 视频分享平台 🎥

**Dilidili** 是一个仿照 **BiliBili** 基于 **Spring Boot** 和 **MyBatis** 构建的现代化视频分享平台，旨在提供一个便捷、安全的视频上传、观看和分享体验。平台具备用户认证、视频管理、评论互动等多种功能，并且使用 **Redis** 来加速数据访问，采用 **JWT** 实现安全的身份验证和授权机制。

## 📋 核心功能

- **视频上传与播放** 📤🎬：用户可以方便地上传视频，并通过平台进行观看和分享。
- **视频点赞与评论** 👍💬：支持视频的点赞与评论功能，让用户可以与其他观众互动。
- **用户管理** 👤🔑：使用 **JWT** 实现无状态的身份认证与授权，支持用户注册、登录和权限管理。
  - **新增**：用户注册时加密密码，提升安全性。
  - **新增**：用户登出时将 JWT 令牌加入 Redis 黑名单，防止令牌被重复使用。
- **视频存储与管理** 💾🗂️：支持视频文件的存储、查询、删除等管理操作，确保平台高效管理大量视频数据。
- **缓存机制** ⚡：通过 **Redis** 实现缓存加速，提升平台响应速度和用户体验。
  - **新增**：用户信息的 Redis 缓存管理，支持缓存存储和清除，优化查询性能。
  - **新增**：使用分布式锁防止缓存击穿，提高高并发场景下的稳定性。
- **安全机制** 🔒：利用 **Spring Security** 和 **JWT** 确保用户数据的安全性，防止未经授权的访问。
  - **新增**：增强的安全过滤器链，支持细粒度的权限控制，允许匿名访问注册、登录及 Swagger 文档端点。

## ⚙️ 技术栈

- **Spring Boot**：快速构建基于 RESTful 的 API，简化开发和部署。
- **MySQL**：存储用户数据、视频信息和评论等业务数据。
- **Redis**：缓存用户请求和视频数据，提高数据读取效率，减少数据库负载。
- **JWT**：用于实现安全的用户认证与授权，保证每个请求都是经过验证的。
- **MyBatis-Plus**：简化数据库操作，提供丰富的查询与 CRUD 功能。
- **Spring Security**：实现严格的访问控制和安全验证，保障平台的安全。

## 🏗️ 项目结构

- **后端**：基于 **Spring Boot** 构建的服务端应用，提供视频管理、用户认证、权限管理等核心功能。
- **数据库**：使用 **MySQL** 存储平台的核心数据，包括用户信息、视频记录和评论。
- **缓存**：**Redis** 用于存储热数据，提升平台的性能，减少数据库访问次数。
- **安全**：通过 **JWT** 进行用户身份验证，并结合 **Spring Security** 提供完善的权限管理。

## 🛠️ 部署与使用

1. 克隆该项目并导入到开发环境：

   ```bash
   git clone https://github.com/your-username/dilidili.git
   ```
2. 配置 **MySQL** 数据库与 **Redis** 服务。
3. 在 `application.yml` 文件中，设置数据库连接、Redis 配置和 JWT 密钥等参数。
4. 启动 **Spring Boot** 应用：

   ```bash
   mvn spring-boot:run
   ```
5. 在本地测试时，可以使用 Postman 或类似工具，模拟 API 请求。

## 📑 配置示例

在 `application.yml` 中，你可以找到如下配置：

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dilidili?useUnicode=true&characterEncoding=utf8
    username: root
    password: 123456
  jpa:
    hibernate:
      ddl-auto: update

# Redis 配置
spring:
  data:
    redis:
      host: localhost
      port: 6379

# JWT 配置
jwt:
  secret: test-secret-key
  expiration: 86400000  # 1天的过期时间
```

## 👨‍💻 开发与贡献

欢迎各位开发者贡献代码！你可以通过以下步骤参与该项目的开发：

1. Fork 这个仓库。
2. 创建一个新的功能分支：

   ```bash
   git checkout -b feature-branch
   ```
3. 提交更改：

   ```bash
   git commit -am 'Add new feature'
   ```
4. 推送到分支：

   ```bash
   git push origin feature-branch
   ```
5. 提交一个 Pull Request，加入我们一起改进项目！

## 🎯 未来功能

- **视频推荐系统**：根据用户观看历史推荐个性化视频。
- **评论与社交互动**：添加视频分享、社交互动功能，增加平台活跃度。
- **高可用性部署**：实现容器化部署和高可用架构，确保平台在高并发情况下的稳定性。
- **性能监控**：集成 Prometheus 和 Grafana，实时监控系统性能和用户行为。
- **异步视频上传**：使用消息队列（如 RabbitMQ）实现异步视频上传，提升上传效率。

---

