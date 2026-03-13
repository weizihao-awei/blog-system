# 博客系统 API 接口文档

> 本文档为前端开发提供完整的API接口说明

## 基础信息

- **Base URL**: `http://localhost:8081`
- **Content-Type**: `application/json`
- **字符编码**: `UTF-8`
- **认证方式**: JWT Token (Bearer Token)

## 统一响应格式

所有接口返回统一格式：

```typescript
interface ResultVO<T> {
  code: number;      // 状态码：200-成功，其他-失败
  message: string;   // 响应消息
  data: T;          // 响应数据（泛型）
  timestamp: number; // 时间戳（毫秒）
}
```

### 成功响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1746518400000
}
```

### 失败响应示例
```json
{
  "code": 500,
  "message": "错误信息描述",
  "data": null,
  "timestamp": 1746518400000
}
```

### 常见状态码
| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token无效 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 一、认证模块

### 1.1 用户登录

**接口地址**: `POST /api/auth/login`

**接口描述**: 用户使用用户名和密码登录系统

**请求参数**:
```typescript
interface LoginDTO {
  username: string;  // 用户名（必填）
  password: string;  // 密码（必填）
}
```

**请求示例**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应数据**:
```typescript
interface LoginVO {
  token: string;       // JWT Token
  tokenType: string;   // Token类型，固定为"Bearer"
  userId: number;      // 用户ID
  username: string;    // 用户名
  nickname: string;    // 昵称
  avatar: string;      // 头像URL
  role: number;        // 角色：0-普通用户，1-管理员
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "nickname": "管理员",
    "avatar": null,
    "role": 1
  },
  "timestamp": 1746518400000
}
```

---

### 1.2 用户注册

**接口地址**: `POST /api/auth/register`

**接口描述**: 新用户通过邮箱验证码进行注册

**请求参数**:
```typescript
interface RegisterDTO {
  username: string;         // 用户名（必填，3-20字符）
  password: string;         // 密码（必填，6-20字符）
  confirmPassword: string;  // 确认密码（必填）
  nickname?: string;        // 昵称（可选）
  email: string;            // 邮箱（必填，有效邮箱格式）
  verificationCode: string; // 验证码（必填，6位数字）
}
```

**请求示例**:
```json
{
  "username": "testuser",
  "password": "123456",
  "confirmPassword": "123456",
  "nickname": "测试用户",
  "email": "test@example.com",
  "verificationCode": "123456"
}
```

**响应数据**: `null`

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null,
  "timestamp": 1746518400000
}
```

---

### 1.3 发送验证码

**接口地址**: `POST /api/auth/send-code`

**接口描述**: 向指定邮箱发送验证码，用于注册或重置密码

**请求参数**:
```typescript
interface SendCodeDTO {
  email: string;  // 邮箱（必填，有效邮箱格式）
  type: string;   // 验证码类型（必填）
                  // "register" - 注册
                  // "reset" - 重置密码
}
```

**请求示例**:
```json
{
  "email": "test@example.com",
  "type": "register"
}
```

**响应数据**: `null`

**响应示例**:
```json
{
  "code": 200,
  "message": "验证码已发送",
  "data": null,
  "timestamp": 1746518400000
}
```

**限制说明**:
- 同一邮箱发送间隔：60秒
- 同一邮箱1小时内最多发送：5次
- 验证码有效期：10分钟

---

### 1.4 重置密码

**接口地址**: `POST /api/auth/reset-password`

**接口描述**: 用户通过邮箱验证码重置登录密码

**请求参数**:
```typescript
interface ResetPasswordDTO {
  email: string;            // 邮箱（必填，有效邮箱格式）
  verificationCode: string; // 验证码（必填，6位数字）
  newPassword: string;      // 新密码（必填，6-20字符）
  confirmPassword: string;  // 确认密码（必填）
}
```

**请求示例**:
```json
{
  "email": "test@example.com",
  "verificationCode": "123456",
  "newPassword": "new123456",
  "confirmPassword": "new123456"
}
```

**响应数据**: `null`

**响应示例**:
```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null,
  "timestamp": 1746518400000
}
```

---

## 二、用户模块

> 以下接口需要携带Authorization Header: `Bearer {token}`

### 2.1 获取当前用户信息

**接口地址**: `GET /api/user/info`

**接口描述**: 获取当前登录用户的详细信息

**响应数据**:
```typescript
interface User {
  id: number;
  username: string;
  nickname: string;
  email: string;
  avatar: string;
  role: number;        // 0-普通用户，1-管理员
  status: number;      // 0-禁用，1-正常
  createTime: string;
  updateTime: string;
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "email": "admin@example.com",
    "avatar": null,
    "role": 1,
    "status": 1,
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-01T00:00:00"
  },
  "timestamp": 1746518400000
}
```

---

### 2.2 更新当前用户信息

**接口地址**: `PUT /api/user/info`

**接口描述**: 更新当前登录用户的信息

**请求参数**:
```typescript
interface User {
  nickname?: string;  // 昵称
  email?: string;     // 邮箱
  avatar?: string;    // 头像URL
}
```

**请求示例**:
```json
{
  "nickname": "新昵称",
  "email": "newemail@example.com",
  "avatar": "http://example.com/avatar.jpg"
}
```

**响应数据**: `null`

---

### 2.3 修改密码

**接口地址**: `PUT /api/user/password`

**接口描述**: 修改当前用户的密码

**请求参数**:
```typescript
{
  oldPassword: string;  // 旧密码（必填）
  newPassword: string;  // 新密码（必填）
}
```

**请求示例**:
```json
{
  "oldPassword": "old123456",
  "newPassword": "new123456"
}
```

**响应数据**: `null`

---

### 2.4 获取用户列表（管理员）

**接口地址**: `GET /api/user/list`

**接口描述**: 管理员获取用户列表，支持分页和关键词搜索

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | number | 否 | 页码，默认1 |
| pageSize | number | 否 | 每页数量，默认10 |
| keyword | string | 否 | 搜索关键词 |

**响应数据**:
```typescript
interface PageVO<T> {
  list: T[];              // 数据列表
  total: number;          // 总记录数
  pageNum: number;        // 当前页码
  pageSize: number;       // 每页数量
  totalPages: number;      // 总页数
  hasNextPage: boolean;   // 是否有下一页
  hasPreviousPage: boolean; // 是否有上一页
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [...],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 10,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "timestamp": 1746518400000
}
```

---

### 2.5 更新用户状态（管理员）

**接口地址**: `PUT /api/user/{userId}/status/{status}`

**接口描述**: 管理员更新用户状态

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| userId | number | 用户ID |
| status | number | 状态：1-正常，0-禁用 |

**响应数据**: `null`

---

### 2.6 删除用户（管理员）

**接口地址**: `DELETE /api/user/{userId}`

**接口描述**: 管理员删除指定用户

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| userId | number | 用户ID |

**响应数据**: `null`

---

## 三、文章模块

### 3.1 通用文章查询接口（公开）

**接口地址**: `POST /api/article/query`

**接口描述**: 通用文章查询接口，支持分类、标签、关键词搜索、排序

**请求参数**:
```typescript
interface ArticleQueryDTO {
  pageNum?: number;      // 页码，默认 1
  pageSize?: number;    // 每页数量，默认 10
  categoryId?: number;   // 分类 ID
  tagIds?: number[];     // 标签 ID 数组（支持多标签查询）
  keyword?: string;      // 搜索关键词
  orderBy?: string;     // 排序方式，默认"create_time_desc"
                        // "create_time_asc" - 按创建时间升序（最早发布）
                        // "create_time_desc" - 按创建时间降序（最新发布）
                        // "update_time_asc" - 按更新时间升序（最早编辑）
                        // "update_time_desc" - 按更新时间降序（最新编辑）
}
```

**请求示例**:
```json
// 获取默认文章列表（按创建时间降序，最新发布）
{}

// 查询特定分类的文章
{
  "pageNum": 1,
  "pageSize": 20,
  "categoryId": 1
}

// 查询特定标签的文章（单个标签）
{
  "pageNum": 1,
  "pageSize": 10,
  "tagIds": [5]
}

// 查询包含任意一个指定标签的文章（多标签）
{
  "pageNum": 1,
  "pageSize": 10,
  "tagIds": [1, 3, 5]
}

// 关键字搜索
{
  "pageNum": 1,
  "pageSize": 10,
  "keyword": "Java"
}

// 组合查询（分类 + 标签 + 关键字 + 按创建时间升序）
{
  "pageNum": 1,
  "pageSize": 10,
  "categoryId": 1,
  "tagIds": [1, 2],
  "keyword": "Spring Boot",
  "orderBy": "create_time_asc"
}

// 按更新时间降序（最新编辑）
{
  "pageNum": 1,
  "pageSize": 10,
  "orderBy": "update_time_desc"
}

// 按创建时间升序（最早发布）
{
  "pageNum": 1,
  "pageSize": 10,
  "orderBy": "create_time_asc"
}

// 按更新时间升序（最早编辑）
{
  "pageNum": 1,
  "pageSize": 10,
  "orderBy": "update_time_asc"
}
```

**响应数据**:
```typescript
interface Article {
  id: number;
  title: string;
  summary: string;
  coverImage: string;
  authorId: number;
  authorName: string;
  authorAvatar: string;
  categoryId: number;
  categoryName: string;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  isTop: number;        // 0-否，1-是
  isRecommend: number;   // 0-否，1-是
  publishTime: string;
  tags: Tag[];
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
        "title": "文章标题",
        "summary": "文章摘要...",
        "coverImage": "http://example.com/cover.jpg",
        "authorId": 1,
        "authorName": "作者昵称",
        "authorAvatar": "http://example.com/avatar.jpg",
        "categoryId": 1,
        "categoryName": "分类名称",
        "viewCount": 100,
        "likeCount": 20,
        "commentCount": 5,
        "isTop": 1,
        "isRecommend": 1,
        "publishTime": "2024-01-01T00:00:00",
        "tags": [
          {
            "id": 1,
            "name": "Java",
            "color": "#E74C3C"
          }
        ]
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 10,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "timestamp": 1746518400000
}
```

---

### 3.2 获取文章详情（公开）

**接口地址**: `GET /api/article/detail/{articleId}`

**接口描述**: 根据文章ID获取文章详细信息

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| articleId | number | 文章ID |

**响应数据**:
```typescript
interface ArticleVO {
  id: number;
  title: string;
  summary: string;
  content: string;          // Markdown内容
  htmlContent: string;      // HTML内容
  coverImage: string;
  authorId: number;
  authorName: string;
  authorAvatar: string;
  categoryId: number;
  categoryName: string;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  status: number;          // 0-草稿，1-发布，2-下架
  isTop: number;
  isRecommend: number;
  publishTime: string;
  createTime: string;
  updateTime: string;
  tags: Tag[];
  isLiked: boolean;        // 当前用户是否点赞
  isCollected: boolean;    // 当前用户是否收藏
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "文章标题",
    "summary": "文章摘要",
    "content": "# Markdown内容",
    "htmlContent": "<h1>HTML内容</h1>",
    "coverImage": "http://example.com/cover.jpg",
    "authorId": 1,
    "authorName": "作者",
    "authorAvatar": null,
    "categoryId": 1,
    "categoryName": "技术分享",
    "viewCount": 100,
    "likeCount": 20,
    "commentCount": 5,
    "status": 1,
    "isTop": 0,
    "isRecommend": 1,
    "publishTime": "2024-01-01T00:00:00",
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-01T00:00:00",
    "tags": [...],
    "isLiked": false,
    "isCollected": false
  },
  "timestamp": 1746518400000
}
```

---

### 3.3 创建文章

**接口地址**: `POST /api/article/create`

**接口描述**: 创建新文章

**请求参数**:
```typescript
interface ArticleDTO {
  title: string;          // 文章标题（必填）
  summary?: string;       // 文章摘要
  content: string;        // Markdown内容（必填）
  htmlContent?: string;   // HTML内容
  coverImage?: string;    // 封面图片URL
  categoryId?: number;    // 分类ID
  tagIds?: number[];      // 标签ID数组
  status?: number;        // 状态：0-草稿，1-发布
  isTop?: number;        // 是否置顶：0-否，1-是
  isRecommend?: number;   // 是否推荐：0-否，1-是
}
```

**请求示例**:
```json
{
  "title": "Spring Boot入门教程",
  "summary": "本文介绍Spring Boot基础知识",
  "content": "# Spring Boot入门\n\nSpring Boot是...",
  "htmlContent": "<h1>Spring Boot入门</h1>",
  "coverImage": "http://example.com/cover.jpg",
  "categoryId": 1,
  "tagIds": [1, 2, 3],
  "status": 1,
  "isTop": 0,
  "isRecommend": 1
}
```

**响应数据**: `number` (文章ID)

**响应示例**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": 1,
  "timestamp": 1746518400000
}
```

---

### 3.4 更新文章

**接口地址**: `PUT /api/article/update`

**接口描述**: 更新文章（作者或管理员）

**请求参数**: 同3.3，需要包含`id`字段

**请求示例**:
```json
{
  "id": 1,
  "title": "新标题",
  "summary": "新摘要",
  "content": "# 新内容",
  "categoryId": 1,
  "tagIds": [1, 2],
  "status": 1
}
```

**响应数据**: `null`

---

### 3.5 删除文章

**接口地址**: `DELETE /api/article/delete/{articleId}`

**接口描述**: 删除文章（作者或管理员）

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| articleId | number | 文章ID |

**响应数据**: `null`

---

### 3.6 点赞文章

**接口地址**: `POST /api/article/like/{articleId}`

**接口描述**: 为文章点赞

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| articleId | number | 文章ID |

**响应数据**: `null`

---

### 3.7 取消点赞

**接口地址**: `DELETE /api/article/like/{articleId}`

**接口描述**: 取消文章点赞

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| articleId | number | 文章ID |

**响应数据**: `null`

---

### 3.8 收藏文章

**接口地址**: `POST /api/article/collect/{articleId}`

**接口描述**: 收藏文章

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| articleId | number | 文章ID |

**响应数据**: `null`

---

### 3.9 取消收藏

**接口地址**: `DELETE /api/article/collect/{articleId}`

**接口描述**: 取消文章收藏

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| articleId | number | 文章ID |

**响应数据**: `null`

---

### 3.10 获取热门文章（公开）

**接口地址**: `POST /api/article/hot`

**接口描述**: 获取浏览量最高的文章，支持分页

**请求参数**:
```typescript
interface ArticleQueryDTO {
  pageNum?: number;      // 页码，默认1
  pageSize?: number;    // 每页数量，默认10
}
```

**请求示例**:
```json
{
  "pageNum": 1,
  "pageSize": 10
}
```

**响应数据**: `PageVO<Article>`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [...],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 10,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "timestamp": 1746518400000
}
```

---

### 3.11 获取最新文章（公开）

**接口地址**: `POST /api/article/latest`

**接口描述**: 获取最新发布的文章，支持分页

**请求参数**:
```typescript
interface ArticleQueryDTO {
  pageNum?: number;      // 页码，默认1
  pageSize?: number;    // 每页数量，默认10
}
```

**请求示例**:
```json
{
  "pageNum": 1,
  "pageSize": 10
}
```

**响应数据**: `PageVO<Article>`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [...],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 10,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "timestamp": 1746518400000
}
```

---

### 3.12 获取推荐文章（公开）

**接口地址**: `POST /api/article/recommend`

**接口描述**: 获取个性化推荐的文章（基于用户行为分析），支持分页

**请求参数**:
```typescript
interface ArticleQueryDTO {
  pageNum?: number;      // 页码，默认1
  pageSize?: number;    // 每页数量，默认10
}
```

**请求示例**:
```json
{
  "pageNum": 1,
  "pageSize": 10
}
```

**响应数据**: `PageVO<Article>`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [...],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 10,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "timestamp": 1746518400000
}
```

---

### 3.13 获取我的文章列表

**接口地址**: `GET /api/article/my`

**接口描述**: 获取当前用户发布的文章

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | number | 否 | 页码，默认1 |
| pageSize | number | 否 | 每页数量，默认10 |
| status | number | 否 | 文章状态：0-草稿，1-已发布，2-下架 |

**响应数据**: `PageVO<Article>`

---

### 3.14 获取我的收藏列表

**接口地址**: `GET /api/article/my-collects`

**接口描述**: 获取当前用户收藏的文章

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | number | 否 | 页码，默认1 |
| pageSize | number | 否 | 每页数量，默认10 |

**响应数据**: `PageVO<Article>`

---

## 四、标签模块

### 4.1 获取标签列表（公开）

**接口地址**: `GET /api/tag/list`

**接口描述**: 获取所有启用的标签

**响应数据**:
```typescript
interface Tag {
  id: number;
  name: string;
  color: string;
  status: number;    // 0-禁用，1-正常
  createTime: string;
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "Java",
      "color": "#E74C3C",
      "status": 1,
      "createTime": "2024-01-01T00:00:00"
    }
  ],
  "timestamp": 1746518400000
}
```

---

### 4.2 获取热门标签（公开）

**接口地址**: `GET /api/tag/hot`

**接口描述**: 获取文章数量最多的标签

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | number | 否 | 返回数量，默认10 |

**响应数据**: `Tag[]`

---

### 4.3 获取标签详情（公开）

**接口地址**: `GET /api/tag/detail/{tagId}`

**接口描述**: 根据标签ID获取标签详细信息

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| tagId | number | 标签ID |

**响应数据**: `Tag`

---

### 4.4 创建标签（管理员）

**接口地址**: `POST /api/tag/create`

**接口描述**: 管理员创建新标签

**请求参数**:
```typescript
interface TagDTO {
  name: string;      // 标签名称（必填）
  color?: string;    // 标签颜色（可选，默认#409EFF）
  status?: number;   // 状态（可选，默认1）
}
```

**请求示例**:
```json
{
  "name": "新标签",
  "color": "#409EFF",
  "status": 1
}
```

**响应数据**: `number` (标签ID)

---

### 4.5 更新标签（管理员）

**接口地址**: `PUT /api/tag/update`

**接口描述**: 管理员更新标签信息

**请求参数**:
```typescript
interface TagDTO {
  id: number;        // 标签ID（必填）
  name: string;      // 标签名称（必填）
  color?: string;    // 标签颜色
  status?: number;   // 状态
}
```

**响应数据**: `null`

---

### 4.6 删除标签（管理员）

**接口地址**: `DELETE /api/tag/delete/{tagId}`

**接口描述**: 管理员删除指定标签

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| tagId | number | 标签ID |

**响应数据**: `null`

---

### 4.7 获取标签列表（管理员分页）

**接口地址**: `GET /api/tag/admin-list`

**接口描述**: 管理员分页获取标签列表（包含禁用）

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | number | 否 | 页码，默认1 |
| pageSize | number | 否 | 每页数量，默认10 |

**响应数据**: `PageVO<Tag>`

---

## 五、分类模块

### 5.1 获取分类列表（公开）

**接口地址**: `GET /api/category/list`

**接口描述**: 获取所有启用的分类

**响应数据**:
```typescript
interface Category {
  id: number;
  name: string;
  description: string;
  sortOrder: number;  // 排序号
  status: number;     // 0-禁用，1-正常
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "技术分享",
      "description": "技术文章、编程经验分享",
      "sortOrder": 1,
      "status": 1
    }
  ],
  "timestamp": 1746518400000
}
```

---

### 5.2 获取分类详情（公开）

**接口地址**: `GET /api/category/detail/{categoryId}`

**接口描述**: 根据分类ID获取分类详细信息

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| categoryId | number | 分类ID |

**响应数据**: `Category`

---

### 5.3 创建分类（管理员）

**接口地址**: `POST /api/category/create`

**接口描述**: 管理员创建新分类

**请求参数**:
```typescript
interface CategoryDTO {
  name: string;          // 分类名称（必填）
  description?: string;  // 分类描述
  sortOrder?: number;    // 排序号
  status?: number;       // 状态
}
```

**请求示例**:
```json
{
  "name": "新分类",
  "description": "分类描述",
  "sortOrder": 1,
  "status": 1
}
```

**响应数据**: `number` (分类ID)

---

### 5.4 更新分类（管理员）

**接口地址**: `PUT /api/category/update`

**接口描述**: 管理员更新分类信息

**请求参数**:
```typescript
interface CategoryDTO {
  id: number;           // 分类ID（必填）
  name: string;         // 分类名称（必填）
  description?: string; // 分类描述
  sortOrder?: number;   // 排序号
  status?: number;      // 状态
}
```

**响应数据**: `null`

---

### 5.5 删除分类（管理员）

**接口地址**: `DELETE /api/category/delete/{categoryId}`

**接口描述**: 管理员删除指定分类

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| categoryId | number | 分类ID |

**响应数据**: `null`

---

### 5.6 获取分类列表（管理员分页）

**接口地址**: `GET /api/category/admin-list`

**接口描述**: 管理员分页获取分类列表（包含禁用）

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | number | 否 | 页码，默认1 |
| pageSize | number | 否 | 每页数量，默认10 |

**响应数据**: `PageVO<Category>`

---

## 六、评论模块

### 6.1 获取文章评论列表（公开）

**接口地址**: `GET /api/comment/list/{articleId}`

**接口描述**: 获取文章的评论列表（树形结构）

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| articleId | number | 文章ID |

**响应数据**:
```typescript
interface CommentVO {
  id: number;
  articleId: number;
  parentId: number;        // 父评论ID，null表示一级评论
  userId: number;
  username: string;
  userAvatar: string;
  content: string;
  replyToUsername: string; // 回复的用户名
  createTime: string;
  children: CommentVO[];   // 子评论列表
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "articleId": 1,
      "parentId": null,
      "userId": 2,
      "username": "用户A",
      "userAvatar": "http://...",
      "content": "评论内容",
      "replyToUsername": null,
      "createTime": "2024-01-01T00:00:00",
      "children": [
        {
          "id": 2,
          "articleId": 1,
          "parentId": 1,
          "userId": 3,
          "username": "用户B",
          "userAvatar": "http://...",
          "content": "回复内容",
          "replyToUsername": "用户A",
          "createTime": "2024-01-01T00:00:00",
          "children": []
        }
      ]
    }
  ],
  "timestamp": 1746518400000
}
```

---

### 6.2 创建评论

**接口地址**: `POST /api/comment/create`

**接口描述**: 发表评论

**请求参数**:
```typescript
interface CommentDTO {
  articleId: number;   // 文章ID（必填）
  parentId?: number;   // 父评论ID，回复时填写
  content: string;     // 评论内容（必填）
}
```

**请求示例**:
```json
{
  "articleId": 1,
  "parentId": null,
  "content": "这是一条评论"
}
```

**回复评论示例**:
```json
{
  "articleId": 1,
  "parentId": 1,
  "content": "这是对评论1的回复"
}
```

**响应数据**: `number` (评论ID)

---

### 6.3 删除评论

**接口地址**: `DELETE /api/comment/delete/{commentId}`

**接口描述**: 删除评论（评论作者或管理员）

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| commentId | number | 评论ID |

**响应数据**: `null`

---

### 6.4 更新评论状态（管理员）

**接口地址**: `PUT /api/comment/{commentId}/status/{status}`

**接口描述**: 管理员审核评论

**路径参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| commentId | number | 评论ID |
| status | number | 状态：0-待审核，1-已通过，2-已拒绝 |

**响应数据**: `null`

---

### 6.5 获取评论列表（管理员分页）

**接口地址**: `GET /api/comment/admin-list`

**接口描述**: 管理员分页获取评论列表

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | number | 否 | 页码，默认1 |
| pageSize | number | 否 | 每页数量，默认10 |
| status | number | 否 | 评论状态筛选 |

**响应数据**: `PageVO<CommentVO>`

---

## 七、统计模块

### 7.1 获取统计数据（公开）

**接口地址**: `GET /api/statistics`

**接口描述**: 获取系统统计数据

**响应数据**:
```typescript
interface StatisticsVO {
  userCount: number;        // 用户总数
  articleCount: number;     // 文章总数
  viewCount: number;       // 总浏览量
  commentCount: number;     // 评论总数
  todayViewCount: number;  // 今日浏览量
  weekArticleCount: number; // 本周文章数
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userCount": 100,
    "articleCount": 500,
    "viewCount": 10000,
    "commentCount": 2000,
    "todayViewCount": 500,
    "weekArticleCount": 10
  },
  "timestamp": 1746518400000
}
```

---

## 前端开发建议

### 1. Token 处理

```javascript
// 登录成功后保存 Token
const login = async (username, password) => {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  
  const result = await response.json();
  if (result.code === 200) {
    // 存储 Token
    localStorage.setItem('token', result.data.token);
    localStorage.setItem('userInfo', JSON.stringify(result.data));
    
    // 设置默认请求头
    setAuthHeader(result.data.token);
  }
};

// 设置请求头
const setAuthHeader = (token) => {
  // 使用 axios 示例
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
};

// 退出登录
const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userInfo');
  delete axios.defaults.headers.common['Authorization'];
};
```

### 2. 请求拦截器配置

```javascript
// axios 请求拦截器
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// axios 响应拦截器
axios.interceptors.response.use(
  response => {
    const res = response.data;
    
    // 如果返回的状态码不是 200，说明接口有错误
    if (res.code !== 200) {
      Message.error(res.message || 'Error');
      
      // 如果是 401，说明 token 过期，需要重新登录
      if (res.code === 401) {
        localStorage.removeItem('token');
        window.location.href = '/login';
      }
      
      return Promise.reject(new Error(res.message || 'Error'));
    }
    return res;
  },
  error => {
    Message.error(error.message || 'Network Error');
    return Promise.reject(error);
  }
);
```

### 3. 文章查询接口使用示例

```javascript
// 使用新的 POST /api/article/query 接口
const queryArticles = async (queryParams = {}) => {
  const response = await axios.post('/api/article/query', queryParams);
  return response.data;
};

// 获取默认文章列表
queryArticles({});

// 查询特定分类的文章
queryArticles({
  pageNum: 1,
  pageSize: 20,
  categoryId: 1
});

// 查询特定标签的文章
queryArticles({
  pageNum: 1,
  pageSize: 10,
  tagId: 5
});

// 关键字搜索
queryArticles({
  pageNum: 1,
  pageSize: 10,
  keyword: 'Java'
});

// 组合查询（分类 + 关键字 + 热门排序）
queryArticles({
  pageNum: 1,
  pageSize: 10,
  categoryId: 1,
  keyword: 'Spring Boot',
  orderBy: 'hot'
});

// 查询推荐文章
queryArticles({
  pageNum: 1,
  pageSize: 10,
  orderBy: 'recommend'
});

// 获取热门文章（分页）
const getHotArticles = async (pageNum = 1, pageSize = 10) => {
  const response = await axios.post('/api/article/hot', {
    pageNum,
    pageSize
  });
  return response.data;
};

// 获取最新文章（分页）
const getLatestArticles = async (pageNum = 1, pageSize = 10) => {
  const response = await axios.post('/api/article/latest', {
    pageNum,
    pageSize
  });
  return response.data;
};

// 获取推荐文章（分页）
const getRecommendArticles = async (pageNum = 1, pageSize = 10) => {
  const response = await axios.post('/api/article/recommend', {
    pageNum,
    pageSize
  });
  return response.data;
};
```

### 4. 验证码倒计时

```javascript
const [countdown, setCountdown] = useState(0);

const sendCode = async (email, type) => {
  if (countdown > 0) return;
  
  const response = await axios.post('/api/auth/send-code', { email, type });
  if (response.code === 200) {
    setCountdown(60);
    
    const timer = setInterval(() => {
      setCountdown(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  }
};
```

### 5. 表单验证

```javascript
// 注册表单验证
const validateRegisterForm = (formData) => {
  const errors = [];
  
  if (!formData.username) {
    errors.push('用户名不能为空');
  } else if (formData.username.length < 3 || formData.username.length > 20) {
    errors.push('用户名长度必须在3-20之间');
  }
  
  if (!formData.password) {
    errors.push('密码不能为空');
  } else if (formData.password.length < 6 || formData.password.length > 20) {
    errors.push('密码长度必须在6-20之间');
  }
  
  if (formData.password !== formData.confirmPassword) {
    errors.push('两次输入的密码不一致');
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!formData.email) {
    errors.push('邮箱不能为空');
  } else if (!emailRegex.test(formData.email)) {
    errors.push('邮箱格式不正确');
  }
  
  return errors;
};
```

---

## 常见问题

### Q1: Token 的有效期是多久？
A: Token 有效期为 24 小时（86400000 毫秒）。过期后需要重新登录。

### Q2: 如何判断用户是否已登录？
A: 检查本地是否存储了有效的 token。可以在每次应用启动时调用一个需要认证的接口来验证 token 是否有效。

### Q3: 验证码收不到怎么办？
A: 
1. 检查邮箱地址是否正确
2. 检查垃圾邮件箱
3. 确认发送频率限制（60秒间隔，1小时最多5次）
4. 开发环境会在控制台输出验证码，可以查看后端日志

### Q4: 文章内容支持什么格式？
A: 文章内容支持 Markdown 格式，同时保存 HTML 版本用于展示。

### Q5: 推荐算法是如何工作的？
A: 推荐功能基于以下算法：
1. 协同过滤推荐：分析用户的浏览、点赞、收藏行为
2. 标签匹配：根据用户浏览过的文章标签
3. 热度加权：考虑文章的浏览量和点赞数
4. 时间衰减：新发布的文章获得更高的推荐权重

### Q6: 文章查询接口为什么用POST？
A: 使用POST方式是因为：
1. 查询条件包含多个可选参数，使用POST + RequestBody更清晰
2. 避免GET请求URL过长的问题
3. 支持更复杂的查询条件扩展

### Q7: 热门、最新、推荐文章接口为什么改为POST？
A: 改为POST方式的原因：
1. 统一接口风格，与通用查询接口保持一致
2. 支持分页查询，不再限制返回数量
3. 使用RequestBody传递分页参数更加规范
4. 便于后续扩展更多查询条件

---

## 更新日志

| 版本 | 日期 | 更新内容 |
|-----|------|---------|
| v1.0 | 2026-03-11 | 初始版本，包含所有模块接口 |
| v1.1 | 2026-03-11 | 修正文章查询接口，使用POST /api/article/query |
| v1.2 | 2026-03-11 | 修改热门、最新、推荐文章接口为POST方法，并支持分页查询 |

---

## 联系方式

如有问题，请联系后端开发人员。
