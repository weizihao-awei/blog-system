# 博客系统 API 接口文档

本文档描述博客系统的所有 API 接口，供前端开发人员使用。

## 目录

- [基础信息](#基础信息)
- [统一响应格式](#统一响应格式)
- [认证说明](#认证说明)
- [接口列表](#接口列表)
  - [1. 认证模块](#1-认证模块)
  - [2. 文章模块](#2-文章模块)
  - [3. 评论模块](#3-评论模块)
  - [4. 分类模块](#4-分类模块)
  - [5. 标签模块](#5-标签模块)
  - [6. 用户模块](#6-用户模块)
  - [7. 图片管理模块](#7-图片管理模块)
  - [8. 统计模块](#8-统计模块)
- [数据结构说明](#数据结构说明)
- [错误处理](#错误处理)
- [使用说明](#使用说明)

---

## 基础信息

- **基础 URL**: `http://localhost:8080` (开发环境)
- **API 版本**: v1.0
- **数据格式**: JSON
- **字符编码**: UTF-8

### 认证方式

大部分接口需要 JWT Token 认证：
- **请求头格式**: `Authorization: Bearer <your_token>`
- **Token 获取**: 通过登录接口获取
- **Token 有效期**: 由后端配置决定

### 公开接口（无需认证）

以下接口无需认证即可访问：
- `/api/auth/**` - 认证相关接口
- `/api/tag/list` - 标签列表
- `/api/tag/hot` - 热门标签
- `/api/tag/detail/**` - 标签详情
- `/api/category/list` - 分类列表
- `/api/category/detail/**` - 分类详情
- `/api/comment/list/**` - 评论列表
- `/api/statistics` - 统计数据
- `/api/image/**` - 图片相关接口

---

## 统一响应格式

所有接口返回统一使用以下格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1710500000000
}
```

### 响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码：200-成功，500-失败，401-未授权，403-禁止访问 |
| message | String | 响应消息 |
| data | Object/Array | 响应数据（可能为对象、数组或 null） |
| timestamp | Long | 时间戳（毫秒） |

---

## 认证说明

### JWT Token 使用流程

1. 用户登录获取 Token
2. 将 Token 存储在本地（localStorage/sessionStorage）
3. 每次请求时在请求头中携带 Token
4. Token 过期后需重新登录

### 前端请求示例

```javascript
// 封装请求方法
async function request(url, options = {}) {
  const token = localStorage.getItem('token');
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    }
  };
  
  const response = await fetch(url, { ...defaultOptions, ...options });
  const result = await response.json();
  
  // 统一处理错误
  if (result.code === 401) {
    // Token 失效，跳转登录页
    localStorage.removeItem('token');
    window.location.href = '/login';
    throw new Error('未授权');
  }
  
  if (result.code !== 200) {
    throw new Error(result.message);
  }
  
  return result.data;
}
```

---

## 接口列表

### 1. 认证模块

**基础路径**: `/api/auth`

#### 1.1 用户登录

**接口说明**: 用户登录，获取 JWT Token

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/auth/login`
- **认证**: 不需要
- **Content-Type**: `application/json`

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名/邮箱 |
| password | String | 是 | 密码 |

**请求示例**:
```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "zhangsan",
      "nickname": "张三",
      "email": "zhangsan@example.com",
      "avatar": "/avatars/1.jpg"
    }
  },
  "timestamp": 1710500000000
}
```

**Data 字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| token | String | JWT Token（用于后续请求） |
| user | Object | 用户基本信息 |

**前端调用示例**:
```javascript
async function login(username, password) {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  
  const result = await response.json();
  if (result.code === 200) {
    localStorage.setItem('token', result.data.token);
    return result.data.user;
  } else {
    throw new Error(result.message);
  }
}
```

---

#### 1.2 用户注册

**接口说明**: 新用户注册

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/auth/register`
- **认证**: 不需要
- **Content-Type**: `application/json`

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名（唯一） |
| password | String | 是 | 密码 |
| email | String | 是 | 邮箱地址 |
| nickname | String | 否 | 昵称 |
| verificationCode | String | 是 | 验证码 |

**请求示例**:
```json
{
  "username": "newuser",
  "password": "password123",
  "email": "newuser@example.com",
  "nickname": "新用户",
  "verificationCode": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null,
  "timestamp": 1710500000000
}
```

---

#### 1.3 发送验证码

**接口说明**: 发送邮箱验证码（用于注册或重置密码）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/auth/send-code`
- **认证**: 不需要
- **Content-Type**: `application/json`

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | String | 是 | 邮箱地址 |
| type | String | 是 | 类型：register-注册，reset-重置密码 |

**请求示例**:
```json
{
  "email": "user@example.com",
  "type": "register"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "验证码已发送",
  "data": null,
  "timestamp": 1710500000000
}
```

---

#### 1.4 重置密码

**接口说明**: 忘记密码时重置密码

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/auth/reset-password`
- **认证**: 不需要
- **Content-Type**: `application/json`

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| email | String | 是 | 邮箱地址 |
| verificationCode | String | 是 | 验证码 |
| newPassword | String | 是 | 新密码 |

**请求示例**:
```json
{
  "email": "user@example.com",
  "verificationCode": "123456",
  "newPassword": "newpassword123"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null,
  "timestamp": 1710500000000
}
```

---

### 2. 文章模块

**基础路径**: `/api/article`

#### 2.1 获取最新文章

**接口说明**: 获取最新发布的博客文章列表

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/article/latest`
- **认证**: 不需要
- **Content-Type**: `application/json`

**请求参数** (可选):

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| pageNum | Integer | 1 | 页码 |
| pageSize | Integer | 10 | 每页数量 |
| categoryId | Long | - | 分类 ID（可选） |
| orderBy | String | CREATE_TIME_DESC | 排序方式 |

**请求示例**:
```json
{
  "pageNum": 1,
  "pageSize": 10
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "Spring Boot 实战教程",
        "summary": "本文详细介绍 Spring Boot 的核心特性和使用方法...",
        "coverImage": "/covers/article1.jpg",
        "authorId": 1,
        "categoryId": 1,
        "viewCount": 1200,
        "likeCount": 85,
        "commentCount": 23,
        "collectionCount": 45,
        "publishTime": "2026-03-10T09:00:00",
        "authorName": "张三",
        "authorAvatar": "/avatars/1.jpg",
        "categoryName": "技术教程",
        "tags": [
          {
            "id": 1,
            "name": "Spring Boot",
            "color": "#4CAF50"
          }
        ],
        "isLiked": false,
        "isCollected": false
      }
    ],
    "total": 50,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 5,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "timestamp": 1710500000000
}
```

---

#### 2.2 通用文章查询

**接口说明**: 支持多条件查询文章（分类、标签、关键字搜索、排序）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/article/query`
- **认证**: 不需要
- **Content-Type**: `application/json`

**请求参数** (可选):

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| pageNum | Integer | 1 | 页码 |
| pageSize | Integer | 10 | 每页数量 |
| categoryId | Long | - | 分类 ID |
| tagIds | Array<Long> | - | 标签 ID 列表 |
| keyword | String | - | 关键字搜索（标题/内容） |
| orderBy | String | CREATE_TIME_DESC | 排序方式 |

**请求示例**:
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "categoryId": 1,
  "tagIds": [1, 2],
  "keyword": "Spring Boot",
  "orderBy": "CREATE_TIME_DESC"
}
```

**响应示例**: 与"获取最新文章"相同

---

#### 2.3 获取文章详情

**接口说明**: 获取单篇文章的完整详情

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/article/detail/{articleId}`
- **认证**: 不需要

**路径参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| articleId | Long | 文章 ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "Spring Boot 实战教程",
    "summary": "本文详细介绍 Spring Boot 的核心特性和使用方法...",
    "content": "这里是文章的详细内容...",
    "htmlContent": "<h1>Spring Boot 实战教程</h1><p>这里是文章的详细内容...</p>",
    "coverImage": "/covers/article1.jpg",
    "authorId": 1,
    "categoryId": 1,
    "viewCount": 1201,
    "likeCount": 85,
    "commentCount": 23,
    "collectionCount": 45,
    "status": 1,
    "isTop": 0,
    "isRecommend": 1,
    "publishTime": "2026-03-10T09:00:00",
    "authorName": "张三",
    "authorAvatar": "/avatars/1.jpg",
    "categoryName": "技术教程",
    "tags": [
      {
        "id": 1,
        "name": "Spring Boot",
        "color": "#4CAF50"
      }
    ],
    "isLiked": true,
    "isCollected": false
  },
  "timestamp": 1710500000000
}
```

---

#### 2.4 创建文章

**接口说明**: 发布新文章（需要登录）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/article/create`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | 是 | 文章标题 |
| summary | String | 否 | 文章摘要 |
| content | String | 是 | 文章内容（Markdown 格式） |
| htmlContent | String | 否 | 文章内容（HTML 格式） |
| coverImage | String | 否 | 封面图片路径 |
| categoryId | Long | 是 | 分类 ID |
| tagIds | Array<Long> | 否 | 标签 ID 列表 |
| status | Integer | 否 | 状态：0-草稿，1-发布 |
| isTop | Integer | 否 | 是否置顶：0-否，1-是 |
| isRecommend | Integer | 否 | 是否推荐：0-否，1-是 |

**请求示例**:
```json
{
  "title": "我的新技术分享",
  "summary": "这是一篇技术分享文章",
  "content": "# 标题\n这里是 Markdown 内容...",
  "htmlContent": "<h1>标题</h1><p>这里是 HTML 内容...</p>",
  "coverImage": "/covers/article2.jpg",
  "categoryId": 1,
  "tagIds": [1, 2],
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "文章创建成功",
  "data": 2,
  "timestamp": 1710500000000
}
```

**Data 字段说明**: 返回新创建的文章 ID

---

#### 2.5 更新文章

**接口说明**: 更新已有文章（作者本人可操作）

**请求信息**:
- **方法**: `PUT`
- **路径**: `/api/article/update`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求参数**: 与"创建文章"相同，需额外传递 `id` 字段

**请求示例**:
```json
{
  "id": 1,
  "title": "更新后的标题",
  "summary": "更新后的摘要",
  "content": "更新后的内容",
  "categoryId": 2,
  "tagIds": [2, 3]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "文章更新成功",
  "data": null,
  "timestamp": 1710500000000
}
```

---

#### 2.6 删除文章

**接口说明**: 删除文章（作者本人可操作）

**请求信息**:
- **方法**: `DELETE`
- **路径**: `/api/article/delete/{articleId}`
- **认证**: 需要

**路径参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| articleId | Long | 文章 ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "文章删除成功",
  "data": null,
  "timestamp": 1710500000000
}
```

---

#### 2.7 文章操作（点赞/收藏）

**接口说明**: 统一操作接口（点赞、取消点赞、收藏、取消收藏）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/article/operate`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | Long | 是 | 文章 ID |
| operationType | String | 是 | 操作类型：LIKE-点赞，CANCEL_LIKE-取消点赞，COLLECT-收藏，CANCEL_COLLECT-取消收藏 |

**请求示例** (点赞):
```json
{
  "articleId": 1,
  "operationType": "LIKE"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null,
  "timestamp": 1710500000000
}
```

---

#### 2.8 获取热门文章

**接口说明**: 获取热门/高热度文章列表

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/article/hot`
- **认证**: 不需要
- **Content-Type**: `application/json`

**请求参数** (可选):

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| pageNum | Integer | 1 | 页码 |
| pageSize | Integer | 10 | 每页数量 |

**响应示例**: 与"获取最新文章"相同

---

#### 2.9 获取推荐文章

**接口说明**: 获取推荐文章列表

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/article/recommend`
- **认证**: 不需要
- **Content-Type**: `application/json`

**请求参数** (可选):

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| pageNum | Integer | 1 | 页码 |
| pageSize | Integer | 10 | 每页数量 |

**响应示例**: 与"获取最新文章"相同

---

### 3. 评论模块

**基础路径**: `/api/comment`

#### 3.1 获取文章评论列表

**接口说明**: 获取指定文章的所有评论（支持回复）

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/comment/list/{articleId}`
- **认证**: 不需要

**路径参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| articleId | Long | 文章 ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "articleId": 1,
      "userId": 2,
      "userName": "李四",
      "userAvatar": "/avatars/2.jpg",
      "content": "写得很好，学习了！",
      "parentId": null,
      "likeCount": 5,
      "createTime": "2026-03-15T10:00:00",
      "isLiked": false,
      "replyList": [
        {
          "id": 2,
          "userId": 1,
          "userName": "张三",
          "userAvatar": "/avatars/1.jpg",
          "content": "感谢支持！",
          "createTime": "2026-03-15T11:00:00"
        }
      ]
    }
  ],
  "timestamp": 1710500000000
}
```

---

#### 3.2 创建评论

**接口说明**: 发布新评论或回复评论（需要登录）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/comment/create`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| articleId | Long | 是 | 文章 ID |
| parentId | Long | 否 | 父评论 ID（回复时填写） |
| content | String | 是 | 评论内容 |

**请求示例** (发表评论):
```json
{
  "articleId": 1,
  "content": "这篇文章写得很好！"
}
```

**请求示例** (回复评论):
```json
{
  "articleId": 1,
  "parentId": 1,
  "content": "感谢支持！"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "评论成功",
  "data": 3,
  "timestamp": 1710500000000
}
```

**Data 字段说明**: 返回新创建的评论 ID

---

#### 3.3 删除评论

**接口说明**: 删除评论（作者本人或管理员可操作）

**请求信息**:
- **方法**: `DELETE`
- **路径**: `/api/comment/delete/{commentId}`
- **认证**: 需要

**路径参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| commentId | Long | 评论 ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "评论删除成功",
  "data": null,
  "timestamp": 1710500000000
}
```

---

### 4. 分类模块

**基础路径**: `/api/category`

#### 4.1 获取分类列表

**接口说明**: 获取所有分类列表

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/category/list`
- **认证**: 不需要

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "技术教程",
      "description": "各种技术相关的教程和文章",
      "icon": "/icons/tech.png"
    },
    {
      "id": 2,
      "name": "生活随笔",
      "description": "日常生活记录",
      "icon": "/icons/life.png"
    }
  ],
  "timestamp": 1710500000000
}
```

---

#### 4.2 获取分类详情

**接口说明**: 获取单个分类的详细信息

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/category/detail/{categoryId}`
- **认证**: 不需要

**路径参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| categoryId | Long | 分类 ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "技术教程",
    "description": "各种技术相关的教程和文章",
    "icon": "/icons/tech.png",
    "articleCount": 50
  },
  "timestamp": 1710500000000
}
```

---

### 5. 标签模块

**基础路径**: `/api/tag`

#### 5.1 获取标签列表

**接口说明**: 获取所有标签列表

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/tag/list`
- **认证**: 不需要

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "Spring Boot",
      "color": "#4CAF50"
    },
    {
      "id": 2,
      "name": "Java",
      "color": "#2196F3"
    }
  ],
  "timestamp": 1710500000000
}
```

---

#### 5.2 获取热门标签

**接口说明**: 获取使用频率最高的热门标签

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/tag/hot`
- **认证**: 不需要

**请求参数**:

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| limit | Integer | 10 | 返回数量限制 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "Spring Boot",
      "color": "#4CAF50"
    },
    {
      "id": 2,
      "name": "Java",
      "color": "#2196F3"
    }
  ],
  "timestamp": 1710500000000
}
```

---

#### 5.3 获取标签详情

**接口说明**: 获取单个标签的详细信息

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/tag/detail/{tagId}`
- **认证**: 不需要

**路径参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| tagId | Long | 标签 ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "Spring Boot",
    "color": "#4CAF50",
    "articleCount": 20
  },
  "timestamp": 1710500000000
}
```

---

### 6. 用户模块

**基础路径**: `/api/user`

#### 6.1 获取当前用户信息

**接口说明**: 获取当前登录用户的详细信息

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/user/info`
- **认证**: 需要

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "张三",
    "email": "zhangsan@example.com",
    "avatar": "/avatars/1.jpg",
    "gender": 1,
    "intro": "这是一个简介",
    "signature": "个性签名",
    "role": 1,
    "status": 1,
    "createTime": "2026-03-01T10:00:00",
    "updateTime": "2026-03-15T10:00:00"
  },
  "timestamp": 1710500000000
}
```

**Data 字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户 ID |
| username | String | 用户名（不可变） |
| nickname | String | 昵称 |
| email | String | 邮箱地址 |
| avatar | String | 头像图片路径 |
| gender | Integer | 性别：0-未知，1-男，2-女 |
| intro | String | 自我介绍/个人简介 |
| signature | String | 个性签名 |
| role | Integer | 用户角色 |
| status | Integer | 用户状态 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

---

#### 6.2 更新当前用户信息

**接口说明**: 更新当前登录用户的信息

**请求信息**:
- **方法**: `PUT`
- **路径**: `/api/user/info`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nickname | String | 否 | 昵称 |
| email | String | 否 | 邮箱地址 |
| avatar | String | 否 | 头像图片路径 |
| gender | Integer | 否 | 性别：0-未知，1-男，2-女 |
| intro | String | 否 | 自我介绍 |
| signature | String | 否 | 个性签名 |

**请求示例**:
```json
{
  "nickname": "新的昵称",
  "email": "newemail@example.com",
  "avatar": "/avatars/new.jpg",
  "gender": 1,
  "intro": "新的简介",
  "signature": "新的个性签名"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1710500000000
}
```

---

#### 6.3 查询用户收藏的文章

**接口说明**: 获取当前用户收藏的文章列表（分页）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/user/foot/collection`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求参数**:

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| pageNum | Integer | 1 | 页码 |
| pageSize | Integer | 10 | 每页大小 |
| order | String | desc | 排序方式：asc-从早到晚，desc-从晚到早 |
| documentType | Integer | 1 | 文档类型：1-文章，2-评论 |

**请求示例**:
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "order": "desc",
  "documentType": 1
}
```

**响应示例**: 与"获取最新文章"的响应结构相同

---

#### 6.4 查询用户点赞的文章

**接口说明**: 获取当前用户点赞的文章列表（分页）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/user/foot/praise`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求参数**: 与"查询用户收藏的文章"相同

**响应示例**: 与"获取最新文章"的响应结构相同

---

#### 6.5 查询用户浏览的文章

**接口说明**: 获取当前用户浏览过的文章列表（分页）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/user/foot/read`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求参数**: 与"查询用户收藏的文章"相同

**响应示例**: 与"获取最新文章"的响应结构相同

---

#### 6.6 查询用户发布的文章

**接口说明**: 获取当前用户发布的文章列表（分页）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/user/articles/published`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求参数**: 与"查询用户收藏的文章"相同

**响应示例**: 与"获取最新文章"的响应结构相同

---

### 7. 图片管理模块

**基础路径**: `/api/image`

#### 7.1 上传图片

**接口说明**: 上传单张图片到指定分类目录

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/image/upload`
- **认证**: 需要
- **Content-Type**: `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| category | String | 是 | 图片分类目录（如：avatar、article、cover 等） |
| file | File | 是 | 图片文件 |

**请求示例**:
```javascript
const formData = new FormData();
formData.append('category', 'avatar');
formData.append('file', fileInput.files[0]);

const response = await fetch('/api/image/upload', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  },
  body: formData
});
```

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "uri": "/api/image/avatar/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg",
    "filename": "a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg"
  },
  "timestamp": 1710500000000
}
```

**Data 字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| uri | String | 图片访问 URI（可直接用于 img 标签 src 属性） |
| filename | String | 保存后的文件名（UUID 生成） |

**限制说明**:

| 限制项 | 说明 |
|--------|------|
| 文件大小 | 最大 10MB |
| 文件格式 | 支持 jpg、jpeg、png、gif、webp |
| 命名规则 | 自动使用 UUID 重命名，防止文件名冲突 |

---

#### 7.2 更新图片

**接口说明**: 删除旧图片并上传新图片

**请求信息**:
- **方法**: `PUT`
- **路径**: `/api/image/update`
- **认证**: 需要
- **Content-Type**: `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| category | String | 是 | 图片分类目录 |
| oldUri | String | 否 | 旧图片 URI |
| file | File | 是 | 新图片文件 |

**请求示例**:
```javascript
const formData = new FormData();
formData.append('category', 'avatar');
formData.append('oldUri', '/api/image/avatar/old-image.jpg');
formData.append('file', newFile);

const response = await fetch('/api/image/update', {
  method: 'PUT',
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  },
  body: formData
});
```

**响应示例**: 与"上传图片"相同

---

#### 7.3 获取图片

**接口说明**: 根据分类和文件名获取图片

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/image/{category}/{filename}`
- **认证**: 不需要

**路径参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| category | String | 图片分类 |
| filename | String | 文件名 |

**响应**: 直接返回图片二进制数据

**使用示例**:
```html
<img src="/api/image/avatar/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg" alt="头像" />
```

---

#### 7.4 删除图片

**接口说明**: 删除指定图片

**请求信息**:
- **方法**: `DELETE`
- **路径**: `/api/image/delete?uri={uri}`
- **认证**: 需要

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| uri | String | 是 | 图片 URI |

**请求示例**:
```javascript
const response = await fetch('/api/image/delete?uri=/api/image/avatar/image.jpg', {
  method: 'DELETE',
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
});
```

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1710500000000
}
```

---

### 8. 统计模块

**基础路径**: `/api/statistics`

#### 8.1 获取统计数据

**接口说明**: 获取网站整体统计数据

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/statistics`
- **认证**: 不需要

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "articleCount": 150,
    "userCount": 500,
    "viewCount": 10000,
    "commentCount": 800
  },
  "timestamp": 1710500000000
}
```

**Data 字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| articleCount | Integer | 文章总数 |
| userCount | Integer | 用户总数 |
| viewCount | Integer | 总浏览量 |
| commentCount | Integer | 评论总数 |

---

## 数据结构说明

### ArticleVO - 文章视图对象

```json
{
  "id": 1,
  "title": "文章标题",
  "summary": "文章摘要",
  "content": "文章内容",
  "htmlContent": "HTML 格式内容",
  "coverImage": "/covers/article.jpg",
  "authorId": 1,
  "categoryId": 1,
  "viewCount": 1200,
  "likeCount": 85,
  "commentCount": 23,
  "collectionCount": 45,
  "status": 1,
  "isTop": 0,
  "isRecommend": 1,
  "publishTime": "2026-03-10T09:00:00",
  "authorName": "作者名",
  "authorAvatar": "/avatars/1.jpg",
  "categoryName": "分类名",
  "tags": [
    {
      "id": 1,
      "name": "标签名",
      "color": "#4CAF50"
    }
  ],
  "isLiked": false,
  "isCollected": false,
  "recommendScore": 95.5
}
```

### PageVO - 分页对象

```json
{
  "list": [],
  "total": 100,
  "pageNum": 1,
  "pageSize": 10,
  "totalPages": 10,
  "hasNextPage": true,
  "hasPreviousPage": false
}
```

### TagVO - 标签视图对象

```json
{
  "id": 1,
  "name": "Spring Boot",
  "color": "#4CAF50"
}
```

### CommentVO - 评论视图对象

```json
{
  "id": 1,
  "articleId": 1,
  "userId": 2,
  "userName": "用户名",
  "userAvatar": "/avatars/2.jpg",
  "content": "评论内容",
  "parentId": null,
  "likeCount": 5,
  "createTime": "2026-03-15T10:00:00",
  "isLiked": false,
  "replyList": []
}
```

---

## 错误处理

### 常见错误码

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 200 | 成功 | - |
| 400 | 请求参数错误 | 检查请求参数格式和内容 |
| 401 | 未授权（Token 无效或过期） | 清除本地 Token，跳转登录页 |
| 403 | 禁止访问（权限不足） | 提示用户无权限 |
| 404 | 资源不存在 | 检查资源 ID 是否正确 |
| 500 | 服务器内部错误 | 联系后端开发人员 |

### 错误响应示例

```json
{
  "code": 401,
  "message": "Token 无效或已过期",
  "data": null,
  "timestamp": 1710500000000
}
```

### 前端错误处理最佳实践

```javascript
// 封装统一的错误处理
function handleError(error) {
  if (error.message.includes('Token')) {
    // Token 失效
    localStorage.removeItem('token');
    window.location.href = '/login';
  } else if (error.message.includes('权限')) {
    // 权限不足
    alert('您没有操作权限');
  } else {
    // 其他错误
    alert('操作失败：' + error.message);
  }
}

// 使用示例
async function createArticle(articleData) {
  try {
    const response = await fetch('/api/article/create', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(articleData)
    });
    
    const result = await response.json();
    if (result.code === 200) {
      alert('文章创建成功');
      return result.data;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    handleError(error);
    throw error;
  }
}
```

---

## 使用说明

### 1. 认证流程

```javascript
// 1. 用户登录
async function login(username, password) {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  
  const result = await response.json();
  if (result.code === 200) {
    // 2. 存储 Token
    localStorage.setItem('token', result.data.token);
    return result.data.user;
  }
  throw new Error(result.message);
}

// 3. 在请求中使用 Token
async function getUserInfo() {
  const response = await fetch('/api/user/info', {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
  const result = await response.json();
  return result.data;
}
```

### 2. 分页使用建议

```javascript
// 封装分页查询逻辑
async function fetchWithPagination(apiFunc, initialPageSize = 10) {
  let currentPage = 1;
  const pageSize = initialPageSize;
  let allData = [];
  
  while (true) {
    const result = await apiFunc(currentPage, pageSize);
    allData = [...allData, ...result.list];
    
    if (!result.hasNextPage) break;
    currentPage++;
  }
  
  return allData;
}

// 使用示例：获取所有收藏的文章
async function getCollectionArticles(pageNum, pageSize) {
  const response = await fetch('/api/user/foot/collection', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ pageNum, pageSize, order: 'desc' })
  });
  const result = await response.json();
  return result.data;
}

const allCollections = await fetchWithPagination(getCollectionArticles);
console.log('总收藏数:', allCollections.length);
```

### 3. 排序参数说明

排序参数 `orderBy` 支持以下值：

| 值 | 说明 |
|------|------|
| CREATE_TIME_DESC | 创建时间降序（最新在前） |
| CREATE_TIME_ASC | 创建时间升序（最早在前） |
| UPDATE_TIME_DESC | 更新时间降序 |
| UPDATE_TIME_ASC | 更新时间升序 |

### 4. Vue 3 组合式 API 示例

```vue
<script setup>
import { ref, onMounted } from 'vue';

const articles = ref([]);
const loading = ref(false);
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  totalPages: 0
});

// 获取文章列表
async function loadArticles() {
  loading.value = true;
  try {
    const response = await fetch('/api/article/latest', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        pageNum: pagination.value.pageNum,
        pageSize: pagination.value.pageSize
      })
    });
    
    const result = await response.json();
    if (result.code === 200) {
      articles.value = result.data.list;
      pagination.value.total = result.data.total;
      pagination.value.totalPages = result.data.totalPages;
    }
  } catch (error) {
    console.error('加载文章失败:', error);
  } finally {
    loading.value = false;
  }
}

// 切换页码
function handlePageChange(newPage) {
  pagination.value.pageNum = newPage;
  loadArticles();
}

onMounted(() => {
  loadArticles();
});
</script>

<template>
  <div>
    <div v-if="loading">加载中...</div>
    <div v-else>
      <article-card 
        v-for="article in articles" 
        :key="article.id"
        :article="article"
      />
      <pagination 
        :current-page="pagination.pageNum"
        :total-pages="pagination.totalPages"
        @change="handlePageChange"
      />
    </div>
  </div>
</template>
```

---

## 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.0 | 2026-03-21 | 初始版本，包含所有核心模块接口 |

---

## 联系方式

如有问题，请联系开发团队或查看项目文档。

**技术支持**: 
- 项目仓库：查看 README.md
- 问题反馈：提交 Issue
- 开发文档：查看项目 docs 目录
