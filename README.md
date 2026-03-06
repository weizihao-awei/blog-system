# 博客系统后端

## 项目介绍

这是一个基于 Spring Boot 3 开发的博客系统后端，作为毕业设计项目。系统提供了完整的博客功能，包括用户管理、文章管理、分类标签、评论互动、智能推荐等模块。

- **作者**: 伟字号
- **邮箱**: 1586839217@qq.com
- **项目类型**: 毕业设计

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.0.9 | 核心框架 |
| Spring Security | 3.0.x | 安全认证 |
| MyBatis | 3.0.3 | ORM 框架 |
| MySQL | 8.x | 数据库 |
| JWT | 0.12.3 | Token 认证 |
| Knife4j | 4.0.0 | API 文档 |
| Maven | - | 构建工具 |
| Java | 17 | 开发语言 |

## 功能特性

### 用户模块
- 用户注册/登录（支持邮箱验证码）
- JWT Token 认证
- 用户信息管理
- 密码修改/重置

### 文章模块
- 文章的增删改查
- 文章分类与标签
- 文章点赞与收藏
- 文章浏览量统计
- 热门/最新/推荐文章

### 评论模块
- 评论的增删改查
- 评论树形结构（支持回复）
- 评论审核机制

### 分类与标签
- 分类管理
- 标签管理
- 标签云展示

### 智能推荐
- 基于用户行为的协同过滤推荐
- 标签匹配推荐
- 热度加权算法

### 其他功能
- 邮件服务（注册/找回密码）
- 数据统计
- 接口文档（Knife4j）

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/yourusername/blog-system.git
cd blog-system
```

2. **创建数据库**
```sql
-- 执行 sql/blog_system.sql 文件创建数据库和表结构
```

3. **配置数据库**
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

4. **配置邮件服务（可选）**
如需使用邮箱验证码功能，配置 SMTP：
```yaml
spring:
  mail:
    host: smtp.qq.com
    username: 1586839217@qq.com
    password: your_auth_code
```

5. **运行项目**
```bash
mvn spring-boot:run
```

6. **访问接口文档**
- API 文档: http://localhost:8081/doc.html
- Knife4j: http://localhost:8081/doc.html

## 项目结构

```
blog-system
├── sql/                    # 数据库脚本
├── src/main/java/          # Java 源代码
│   └── com/ykw/blog_system/
│       ├── config/         # 配置类
│       ├── controller/     # 控制器
│       ├── service/        # 业务逻辑
│       ├── mapper/         # 数据访问层
│       ├── entity/         # 实体类
│       ├── dto/            # 数据传输对象
│       ├── vo/             # 视图对象
│       ├── security/       # 安全配置
│       └── utils/          # 工具类
├── src/main/resources/     # 配置文件
│   ├── mapper/             # MyBatis XML
│   ├── application.yml     # 主配置
│   └── application-prod.yml # 生产配置
└── pom.xml                 # Maven 配置
```

## API 接口

详细的 API 接口文档请查看 [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

主要接口模块：
- `/api/auth` - 认证模块（登录/注册）
- `/api/user` - 用户模块
- `/api/article` - 文章模块
- `/api/tag` - 标签模块
- `/api/category` - 分类模块
- `/api/comment` - 评论模块
- `/api/statistics` - 统计模块

## 开源声明

本项目作为毕业设计作品开源，遵循以下开源原则：

### 许可证

本项目采用 [MIT License](https://opensource.org/licenses/MIT) 开源协议。

```
MIT License

Copyright (c) 2025 伟字号

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

### 使用声明

1. 本项目仅供学习交流使用
2. 允许自由使用、修改和分发
3. 使用时请注明原作者信息
4. 作者不对使用本项目产生的任何问题负责

### 联系方式

如有问题或建议，欢迎联系：
- 邮箱: 1586839217@qq.com

## 参与贡献

欢迎提交 Issue 和 Pull Request 来改进本项目。

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
