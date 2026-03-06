# 博客系统 API 接口文档

## 基础信息

- **Base URL**: `http://localhost:8081/api`
- **Content-Type**: `application/json`
- **认证方式**: JWT Token (Bearer Token)

## 响应格式

所有接口返回统一格式：

```json
{
  "code": 200,        // 状态码
  "message": "操作成功 ", // 消息
  "data": {},         // 数据
  "timestamp": 1234567890 // 时间戳
}
```

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token无效 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 一、认证模块 (/auth)

### 1.1 用户登录

- **URL**: `POST /api/auth/login`
- **描述**: 用户登录获取JWT Token
- **请求参数**:

```json
{
  "username": "admin",      // 用户名 (必填)
  "password": "admin123"    // 密码 (必填)
}
```

- **响应数据**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "admin",
    "nickname": "管理员",
    "avatar": null,
    "role": 1
  }
}
```

### 1.2 用户注册

- **URL**: `POST /api/auth/register`
- **描述**: 用户注册
- **请求参数**:

```json
{
  "username": "testuser",      // 用户名 (必填, 3-20字符)
  "password": "123456",        // 密码 (必填, 6-20字符)
  "confirmPassword": "123456", // 确认密码 (必填)
  "nickname": "测试用户",       // 昵称 (选填)
  "email": "test@example.com"  // 邮箱 (选填)
}
```

- **响应数据**:

```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

---

## 二、用户模块 (/user)

> **注意**: 以下接口需要携带Authorization Header: `Bearer {token}`

### 2.1 获取当前用户信息

- **URL**: `GET /api/user/info`
- **描述**: 获取当前登录用户的信息
- **响应数据**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "email": "admin@blog.com",
    "avatar": null,
    "role": 1,
    "status": 1,
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-01T00:00:00"
  }
}
```

### 2.2 更新用户信息

- **URL**: `PUT /api/user/info`
- **描述**: 更新当前用户信息
- **请求参数**:

```json
{
  "nickname": "新昵称",
  "email": "newemail@example.com",
  "avatar": "http://example.com/avatar.jpg"
}
```

### 2.3 修改密码

- **URL**: `PUT /api/user/password`
- **描述**: 修改当前用户密码
- **请求参数**:

```json
{
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

### 2.4 获取用户列表 (管理员)

- **URL**: `GET /api/user/list`
- **描述**: 获取所有用户列表（仅管理员）
- **请求参数**:
  - `pageNum`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认10)
  - `keyword`: 搜索关键词 (可选)

- **响应数据**:

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
  }
}
```

### 2.5 更新用户状态 (管理员)

- **URL**: `PUT /api/user/{userId}/status/{status}`
- **描述**: 启用/禁用用户（仅管理员）
- **路径参数**:
  - `userId`: 用户ID
  - `status`: 状态 (0-禁用, 1-启用)

### 2.6 删除用户 (管理员)

- **URL**: `DELETE /api/user/{userId}`
- **描述**: 删除用户（仅管理员）

---

## 三、文章模块 (/article)

### 3.1 获取文章列表 (公开)

- **URL**: `GET /api/article/list`
- **描述**: 获取已发布的文章列表
- **请求参数**:
  - `pageNum`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认10)
  - `categoryId`: 分类ID (可选，用于筛选)
  - `keyword`: 搜索关键词 (可选，搜索标题和摘要)

- **响应数据**:

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
    "pageSize": 10
  }
}
```

### 3.2 根据标签获取文章列表 (公开)

- **URL**: `GET /api/article/tag/{tagId}`
- **描述**: 获取指定标签的文章列表
- **路径参数**:
  - `tagId`: 标签ID
- **请求参数**:
  - `pageNum`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认10)

### 3.3 获取文章详情 (公开)

- **URL**: `GET /api/article/detail/{articleId}`
- **描述**: 获取文章详情
- **路径参数**:
  - `articleId`: 文章ID

- **响应数据**:

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
  }
}
```

### 3.4 创建文章

- **URL**: `POST /api/article/create`
- **描述**: 创建新文章
- **请求参数**:

```json
{
  "title": "文章标题",           // 必填
  "summary": "文章摘要",         // 选填
  "content": "# Markdown内容",   // 必填
  "htmlContent": "<h1>HTML</h1>", // 选填
  "coverImage": "http://...",    // 选填
  "categoryId": 1,               // 选填
  "tagIds": [1, 2, 3],           // 标签ID数组
  "status": 1,                   // 状态: 0-草稿, 1-发布
  "isTop": 0,                    // 是否置顶: 0-否, 1-是
  "isRecommend": 0               // 是否推荐: 0-否, 1-是
}
```

- **响应数据**:

```json
{
  "code": 200,
  "message": "创建成功",
  "data": 1  // 文章ID
}
```

### 3.5 更新文章

- **URL**: `PUT /api/article/update`
- **描述**: 更新文章（作者或管理员）
- **请求参数**:

```json
{
  "id": 1,                       // 文章ID (必填)
  "title": "新标题",
  "summary": "新摘要",
  "content": "# 新内容",
  "htmlContent": "<h1>新HTML</h1>",
  "coverImage": "http://...",
  "categoryId": 1,
  "tagIds": [1, 2],
  "status": 1,
  "isTop": 0,
  "isRecommend": 1
}
```

### 3.6 删除文章

- **URL**: `DELETE /api/article/delete/{articleId}`
- **描述**: 删除文章（作者或管理员）

### 3.7 点赞文章

- **URL**: `POST /api/article/like/{articleId}`
- **描述**: 点赞文章

### 3.8 取消点赞

- **URL**: `DELETE /api/article/like/{articleId}`
- **描述**: 取消点赞

### 3.9 收藏文章

- **URL**: `POST /api/article/collect/{articleId}`
- **描述**: 收藏文章

### 3.10 取消收藏

- **URL**: `DELETE /api/article/collect/{articleId}`
- **描述**: 取消收藏

### 3.11 获取热门文章 (公开)

- **URL**: `GET /api/article/hot`
- **描述**: 获取浏览量最高的文章
- **请求参数**:
  - `limit`: 数量限制 (默认10)

### 3.12 获取最新文章 (公开)

- **URL**: `GET /api/article/latest`
- **描述**: 获取最新发布的文章
- **请求参数**:
  - `limit`: 数量限制 (默认10)

### 3.13 获取推荐文章 (公开)

- **URL**: `GET /api/article/recommend`
- **描述**: 获取智能推荐的文章（基于用户行为分析）
- **请求参数**:
  - `limit`: 数量限制 (默认10)

### 3.14 获取我的文章列表

- **URL**: `GET /api/article/my`
- **描述**: 获取当前用户发布的文章
- **请求参数**:
  - `pageNum`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认10)
  - `status`: 状态筛选 (可选: 0-草稿, 1-已发布, 2-下架)

### 3.15 获取我的收藏列表

- **URL**: `GET /api/article/my-collects`
- **描述**: 获取当前用户收藏的文章
- **请求参数**:
  - `pageNum`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认10)

---

## 四、标签模块 (/tag)

### 4.1 获取标签列表 (公开)

- **URL**: `GET /api/tag/list`
- **描述**: 获取所有启用的标签

- **响应数据**:

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
  ]
}
```

### 4.2 获取热门标签 (公开)

- **URL**: `GET /api/tag/hot`
- **描述**: 获取文章数量最多的标签
- **请求参数**:
  - `limit`: 数量限制 (默认10)

### 4.3 获取标签详情 (公开)

- **URL**: `GET /api/tag/detail/{tagId}`
- **描述**: 获取标签详情

### 4.4 创建标签 (管理员)

- **URL**: `POST /api/tag/create`
- **描述**: 创建新标签
- **请求参数**:

```json
{
  "name": "新标签",       // 必填
  "color": "#409EFF"     // 选填，默认#409EFF
}
```

### 4.5 更新标签 (管理员)

- **URL**: `PUT /api/tag/update`
- **描述**: 更新标签
- **请求参数**:

```json
{
  "id": 1,
  "name": "新名称",
  "color": "#FF0000",
  "status": 1
}
```

### 4.6 删除标签 (管理员)

- **URL**: `DELETE /api/tag/delete/{tagId}`
- **描述**: 删除标签

### 4.7 获取标签列表 (管理员分页)

- **URL**: `GET /api/tag/admin-list`
- **描述**: 获取所有标签（包含禁用）
- **请求参数**:
  - `pageNum`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认10)

---

## 五、分类模块 (/category)

### 5.1 获取分类列表 (公开)

- **URL**: `GET /api/category/list`
- **描述**: 获取所有启用的分类

- **响应数据**:

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
  ]
}
```

### 5.2 获取分类详情 (公开)

- **URL**: `GET /api/category/detail/{categoryId}`
- **描述**: 获取分类详情

### 5.3 创建分类 (管理员)

- **URL**: `POST /api/category/create`
- **描述**: 创建新分类
- **请求参数**:

```json
{
  "name": "新分类",           // 必填
  "description": "分类描述",   // 选填
  "sortOrder": 1              // 选填，排序号
}
```

### 5.4 更新分类 (管理员)

- **URL**: `PUT /api/category/update`
- **描述**: 更新分类

### 5.5 删除分类 (管理员)

- **URL**: `DELETE /api/category/delete/{categoryId}`
- **描述**: 删除分类

### 5.6 获取分类列表 (管理员分页)

- **URL**: `GET /api/category/admin-list`
- **描述**: 获取所有分类（包含禁用）

---

## 六、评论模块 (/comment)

### 6.1 获取文章评论列表 (公开)

- **URL**: `GET /api/comment/list/{articleId}`
- **描述**: 获取文章的评论列表（树形结构）

- **响应数据**:

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
          "content": "回复内容",
          "replyToUsername": "用户A",
          "createTime": "2024-01-01T00:00:00"
        }
      ]
    }
  ]
}
```

### 6.2 创建评论

- **URL**: `POST /api/comment/create`
- **描述**: 发表评论
- **请求参数**:

```json
{
  "articleId": 1,        // 文章ID (必填)
  "parentId": null,      // 父评论ID，回复时填写 (选填)
  "content": "评论内容"   // 评论内容 (必填)
}
```

### 6.3 删除评论

- **URL**: `DELETE /api/comment/delete/{commentId}`
- **描述**: 删除评论（评论作者或管理员）

### 6.4 更新评论状态 (管理员)

- **URL**: `PUT /api/comment/{commentId}/status/{status}`
- **描述**: 审核评论
- **路径参数**:
  - `commentId`: 评论ID
  - `status`: 状态 (0-待审核, 1-已通过, 2-已拒绝)

### 6.5 获取评论列表 (管理员分页)

- **URL**: `GET /api/comment/admin-list`
- **描述**: 获取所有评论
- **请求参数**:
  - `pageNum`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认10)
  - `status`: 状态筛选 (可选)

---

## 七、统计模块 (/statistics)

### 7.1 获取统计数据 (公开)

- **URL**: `GET /api/statistics`
- **描述**: 获取系统统计数据

- **响应数据**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userCount": 100,        // 用户总数
    "articleCount": 500,     // 文章总数
    "viewCount": 10000,      // 总浏览量
    "commentCount": 2000,    // 评论总数
    "todayViewCount": 500,   // 今日浏览量
    "weekArticleCount": 10   // 本周文章数
  }
}
```

---

## 使用示例

### 登录并访问受保护接口

```bash
# 1. 登录获取Token
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 响应: {"data":{"token":"eyJhbGciOiJIUzI1NiIs..."}}

# 2. 使用Token访问受保护接口
curl -X GET http://localhost:8081/api/user/info \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
```

### 创建文章

```bash
curl -X POST http://localhost:8081/api/article/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "title": "Spring Boot入门教程",
    "summary": "本文介绍Spring Boot基础知识",
    "content": "# Spring Boot入门\\n\\nSpring Boot是...",
    "categoryId": 1,
    "tagIds": [1, 2],
    "status": 1
  }'
```

### 根据标签筛选文章

```bash
# 获取标签ID为1的所有文章
curl -X GET "http://localhost:8081/api/article/tag/1?pageNum=1&pageSize=10"
```

---

## 推荐算法说明

博客系统的推荐功能基于以下算法：

1. **协同过滤推荐**: 分析用户的浏览、点赞、收藏行为，推荐相似用户喜欢的文章
2. **标签匹配**: 根据用户浏览过的文章标签，推荐具有相似标签的文章
3. **热度加权**: 考虑文章的浏览量和点赞数
4. **时间衰减**: 新发布的文章获得更高的推荐权重
5. **混合推荐**: 未登录用户或新用户会看到热门文章和最新文章的混合

推荐得分计算公式：
```
得分 = 标签匹配得分 + 热度得分 × 时间衰减因子
```

---

## 注意事项

1. **Token有效期**: JWT Token默认24小时有效
2. **图片上传**: 如需上传图片，需要额外实现文件上传接口
3. **Markdown**: 文章内容支持Markdown格式，同时保存HTML版本用于展示
4. **权限控制**: 
   - 普通用户只能管理自己的文章和评论
   - 管理员可以管理所有内容
