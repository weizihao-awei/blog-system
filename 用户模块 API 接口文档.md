# 用户模块 API 接口文档

本文档描述博客系统中用户相关的所有 API 接口，供前端开发人员使用。

## 基础信息

- **基础路径**: `/api/user`
- **认证方式**: JWT Token（在请求头中携带）
- **请求头格式**: `Authorization: Bearer <your_token>`
- **数据格式**: 所有请求和响应均为 JSON 格式

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
| code | Integer | 状态码：200-成功，500-失败 |
| message | String | 响应消息 |
| data | Object/Array | 响应数据 |
| timestamp | Long | 时间戳 |

---

## 接口列表

### 1. 获取当前用户信息

**接口说明**: 获取当前登录用户的详细信息

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/user/info`
- **认证**: 需要

**请求示例**:
```http
GET /api/user/info HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

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

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | - | 用户 ID |
| username | String | - | 用户名（不可变） |
| nickname | String | - | 昵称 |
| email | String | - | 邮箱地址 |
| avatar | String | - | 头像图片路径 |
| gender | Integer | - | 性别：0-未知，1-男，2-女 |
| intro | String | - | 自我介绍/个人简介 |
| signature | String | - | 个性签名 |
| role | Integer | - | 用户角色 |
| status | Integer | - | 用户状态 |
| createTime | LocalDateTime | - | 创建时间 |
| updateTime | LocalDateTime | - | 更新时间 |

**前端调用示例 (JavaScript)**:
```javascript
async function getCurrentUserInfo() {
  const response = await fetch('/api/user/info', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
  const result = await response.json();
  if (result.code === 200) {
    return result.data;
  } else {
    throw new Error(result.message);
  }
}
```

---

### 2. 更新当前用户信息

**接口说明**: 更新当前登录用户的信息

**请求信息**:
- **方法**: `PUT`
- **路径**: `/api/user/info`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求示例**:
```http
PUT /api/user/info HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "nickname": "新的昵称",
  "email": "newemail@example.com",
  "avatar": "/avatars/new.jpg",
  "gender": 1,
  "intro": "新的简介",
  "signature": "新的个性签名"
}
```

**请求参数说明**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nickname | String | 否 | 昵称 |
| email | String | 否 | 邮箱地址 |
| avatar | String | 否 | 头像图片路径 |
| gender | Integer | 否 | 性别：0-未知，1-男，2-女 |
| intro | String | 否 | 自我介绍 |
| signature | String | 否 | 个性签名 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null,
  "timestamp": 1710500000000
}
```

**注意事项**:
- ⚠️ `id`、`username`、`password`、`role`、`status` 等字段由系统管理，前端无需传递且不会生效
- ✅ 只需传递需要更新的字段即可（部分更新）

**前端调用示例 (JavaScript)**:
```javascript
async function updateUserInfo(userData) {
  const response = await fetch('/api/user/info', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      nickname: userData.nickname,
      email: userData.email,
      avatar: userData.avatar,
      gender: userData.gender,
      intro: userData.intro,
      signature: userData.signature
    })
  });
  const result = await response.json();
  if (result.code === 200) {
    return true;
  } else {
    throw new Error(result.message);
  }
}
```

---

### 3. 查询用户收藏的文章

**接口说明**: 获取当前用户收藏的文章列表（分页）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/user/foot/collection`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求示例**:
```http
POST /api/user/foot/collection HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "order": "desc",
  "documentType": 1
}
```

**请求参数说明**:

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码（从 1 开始） |
| pageSize | Integer | 否 | 10 | 每页大小 |
| order | String | 否 | "desc" | 排序方式：asc-从早到晚，desc-从晚到早 |
| documentType | Integer | 否 | 1 | 文档类型：1-文章，2-评论 |

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
        "content": "文章正文内容...",
        "htmlContent": "<h1>Spring Boot 实战教程</h1><p>本文详细...</p>",
        "coverImage": "/covers/article1.jpg",
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
        "createTime": "2026-03-09T15:00:00",
        "updateTime": "2026-03-10T09:00:00",
        "authorName": "张三",
        "authorAvatar": "/avatars/1.jpg",
        "categoryName": "技术教程",
        "tags": [
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
        "isLiked": false,
        "isCollected": true,
        "recommendScore": 95.5
      }
    ],
    "total": 25,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 3,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "timestamp": 1710500000000
}
```

**Data.list 数组项字段说明** (ArticleVO):

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 文章 ID |
| title | String | 文章标题 |
| summary | String | 文章摘要 |
| content | String | 文章内容（纯文本） |
| htmlContent | String | 文章内容（HTML 格式） |
| coverImage | String | 封面图片路径 |
| authorId | Long | 作者 ID |
| categoryId | Long | 分类 ID |
| viewCount | Integer | 浏览量 |
| likeCount | Integer | 点赞数 |
| commentCount | Integer | 评论数 |
| collectionCount | Integer | 收藏数 |
| status | Integer | 状态（0:草稿，1:发布，2:下架） |
| isTop | Integer | 是否置顶（0:否，1:是） |
| isRecommend | Integer | 是否推荐（0:否，1:是） |
| publishTime | LocalDateTime | 发布时间 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |
| authorName | String | 作者姓名 |
| authorAvatar | String | 作者头像 |
| categoryName | String | 分类名称 |
| tags | Array<TagVO> | 标签列表 |
| isLiked | Boolean | 当前用户是否已点赞 |
| isCollected | Boolean | 当前用户是否已收藏 |
| recommendScore | Double | 推荐得分 |

**分页字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| list | Array | 当前页的数据列表 |
| total | Long | 总记录数 |
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页大小 |
| totalPages | Integer | 总页数 |
| hasNextPage | Boolean | 是否有下一页 |
| hasPreviousPage | Boolean | 是否有上一页 |

**前端调用示例 (JavaScript)**:
```javascript
async function getCollectionArticles(pageNum = 1, pageSize = 10, order = 'desc') {
  const response = await fetch('/api/user/foot/collection', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      pageNum: pageNum,
      pageSize: pageSize,
      order: order,
      documentType: 1
    })
  });
  const result = await response.json();
  if (result.code === 200) {
    return result.data;
  } else {
    throw new Error(result.message);
  }
}

// 使用示例
getCollectionArticles(1, 10).then(data => {
  console.log('收藏的文章:', data.list);
  console.log('总数:', data.total);
  console.log('总页数:', data.totalPages);
});
```

---

### 4. 查询用户点赞的文章

**接口说明**: 获取当前用户点赞的文章列表（分页）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/user/foot/praise`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求示例**:
```http
POST /api/user/foot/praise HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "order": "desc",
  "documentType": 1
}
```

**请求参数说明**:

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码（从 1 开始） |
| pageSize | Integer | 否 | 10 | 每页大小 |
| order | String | 否 | "desc" | 排序方式：asc-从早到晚，desc-从晚到早 |
| documentType | Integer | 否 | 1 | 文档类型：1-文章，2-评论 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 2,
        "title": "Vue.js 组件通信详解",
        "summary": "深入理解 Vue.js 中的父子组件通信方式...",
        "coverImage": "/covers/article2.jpg",
        "authorId": 2,
        "categoryId": 1,
        "viewCount": 850,
        "likeCount": 62,
        "commentCount": 18,
        "collectionCount": 30,
        "status": 1,
        "isTop": 0,
        "isRecommend": 0,
        "publishTime": "2026-03-08T14:00:00",
        "authorName": "李四",
        "authorAvatar": "/avatars/2.jpg",
        "categoryName": "技术教程",
        "tags": [
          {
            "id": 3,
            "name": "Vue.js",
            "color": "#4FC08D"
          }
        ],
        "isLiked": true,
        "isCollected": false,
        "recommendScore": 88.0
      }
    ],
    "total": 15,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 2,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "timestamp": 1710500000000
}
```

**响应数据结构**: 与"查询用户收藏的文章"接口相同

**前端调用示例 (JavaScript)**:
```javascript
async function getPraiseArticles(pageNum = 1, pageSize = 10, order = 'desc') {
  const response = await fetch('/api/user/foot/praise', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      pageNum: pageNum,
      pageSize: pageSize,
      order: order,
      documentType: 1
    })
  });
  const result = await response.json();
  if (result.code === 200) {
    return result.data;
  } else {
    throw new Error(result.message);
  }
}
```

---

### 5. 查询用户浏览的文章

**接口说明**: 获取当前用户浏览过的文章列表（分页）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/user/foot/read`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求示例**:
```http
POST /api/user/foot/read HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "order": "desc",
  "documentType": 1
}
```

**请求参数说明**:

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码（从 1 开始） |
| pageSize | Integer | 否 | 10 | 每页大小 |
| order | String | 否 | "desc" | 排序方式：asc-从早到晚，desc-从晚到早 |
| documentType | Integer | 否 | 1 | 文档类型：1-文章，2-评论 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 3,
        "title": "MySQL 性能优化最佳实践",
        "summary": "分享 MySQL 数据库优化的实际经验...",
        "coverImage": "/covers/article3.jpg",
        "authorId": 1,
        "categoryId": 2,
        "viewCount": 2000,
        "likeCount": 150,
        "commentCount": 45,
        "collectionCount": 80,
        "status": 1,
        "isTop": 1,
        "isRecommend": 1,
        "publishTime": "2026-03-05T10:00:00",
        "authorName": "张三",
        "authorAvatar": "/avatars/1.jpg",
        "categoryName": "数据库",
        "tags": [
          {
            "id": 4,
            "name": "MySQL",
            "color": "#FF9800"
          }
        ],
        "isLiked": true,
        "isCollected": true,
        "recommendScore": 92.0
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

**响应数据结构**: 与"查询用户收藏的文章"接口相同

**前端调用示例 (JavaScript)**:
```javascript
async function getReadArticles(pageNum = 1, pageSize = 10, order = 'desc') {
  const response = await fetch('/api/user/foot/read', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      pageNum: pageNum,
      pageSize: pageSize,
      order: order,
      documentType: 1
    })
  });
  const result = await response.json();
  if (result.code === 200) {
    return result.data;
  } else {
    throw new Error(result.message);
  }
}
```

---

### 6. 查询用户发布的文章

**接口说明**: 获取当前用户发布的文章列表（分页）

**请求信息**:
- **方法**: `POST`
- **路径**: `/api/user/articles/published`
- **认证**: 需要
- **Content-Type**: `application/json`

**请求示例**:
```http
POST /api/user/articles/published HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "order": "desc",
  "documentType": 1
}
```

**请求参数说明**:

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码（从 1 开始） |
| pageSize | Integer | 否 | 10 | 每页大小 |
| order | String | 否 | "desc" | 排序方式：asc-从早到晚，desc-从晚到早 |
| documentType | Integer | 否 | 1 | 文档类型：1-文章，2-评论 |

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
          },
          {
            "id": 2,
            "name": "Java",
            "color": "#2196F3"
          }
        ],
        "isLiked": false,
        "isCollected": true,
        "recommendScore": 95.5
      }
    ],
    "total": 8,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 1,
    "hasNextPage": false,
    "hasPreviousPage": false
  },
  "timestamp": 1710500000000
}
```

**响应数据结构**: 与"查询用户收藏的文章"接口相同

**前端调用示例 (JavaScript)**:
```javascript
async function getPublishedArticles(pageNum = 1, pageSize = 10, order = 'desc') {
  const response = await fetch('/api/user/articles/published', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      pageNum: pageNum,
      pageSize: pageSize,
      order: order,
      documentType: 1
    })
  });
  const result = await response.json();
  if (result.code === 200) {
    return result.data;
  } else {
    throw new Error(result.message);
  }
}
```

---

## TagVO 数据结构说明

在所有包含标签的接口中，TagVO 的结构如下：

```json
{
  "id": 1,
  "name": "Spring Boot",
  "color": "#4CAF50"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 标签 ID |
| name | String | 标签名称 |
| color | String | 标签颜色（十六进制色值） |

---

## 错误处理

### 常见错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 401 | 未授权（Token 无效或过期） |
| 403 | 禁止访问（权限不足） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 错误响应示例

```json
{
  "code": 401,
  "message": "Token 无效或已过期",
  "data": null,
  "timestamp": 1710500000000
}
```

### 前端错误处理示例

```javascript
async function callApi() {
  try {
    const response = await fetch('/api/user/info', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      // 成功处理
      console.log('用户信息:', result.data);
    } else if (result.code === 401) {
      // Token 失效，跳转登录页
      localStorage.removeItem('token');
      window.location.href = '/login';
    } else {
      // 其他错误
      alert('错误：' + result.message);
    }
  } catch (error) {
    // 网络错误或其他异常
    console.error('请求失败:', error);
    alert('网络错误，请稍后重试');
  }
}
```

---

## 使用说明

### 1. 认证流程

1. 用户登录后获取 JWT Token
2. 将 Token 存储在本地（localStorage/sessionStorage）
3. 每次请求时在请求头中携带 Token

### 2. 分页使用建议

```javascript
// 封装分页查询逻辑
async function fetchWithPagination(apiFunc, initialPageSize = 10) {
  let currentPage = 1;
  const pageSize = initialPageSize;
  let allData = [];
  let hasMore = true;
  
  while (hasMore) {
    const result = await apiFunc(currentPage, pageSize);
    allData = [...allData, ...result.list];
    hasMore = result.hasNextPage;
    currentPage++;
  }
  
  return allData;
}

// 使用示例：获取所有收藏的文章
const allCollections = await fetchWithPagination(getCollectionArticles);
console.log('总收藏数:', allCollections.length);
```

### 3. 排序参数说明

- `order: "asc"` - 从早到晚（升序）
- `order: "desc"` - 从晚到早（降序，默认）

根据记忆中的规范，排序参数应支持字符串形式，后端会自动转换为枚举类型。

---

## 快速开始

### Vue 3 组合式 API 示例

```vue
<script setup>
import { ref, onMounted } from 'vue';

const userInfo = ref(null);
const collections = ref([]);
const loading = ref(false);

// 获取用户信息
async function loadUserInfo() {
  try {
    const res = await fetch('/api/user/info', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    const result = await res.json();
    if (result.code === 200) {
      userInfo.value = result.data;
    }
  } catch (error) {
    console.error('加载用户信息失败:', error);
  }
}

// 获取收藏列表
async function loadCollections() {
  loading.value = true;
  try {
    const res = await fetch('/api/user/foot/collection', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        pageNum: 1,
        pageSize: 10,
        order: 'desc'
      })
    });
    const result = await res.json();
    if (result.code === 200) {
      collections.value = result.data.list;
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadUserInfo();
  loadCollections();
});
</script>

<template>
  <div>
    <div v-if="userInfo">
      <h2>{{ userInfo.nickname }}</h2>
      <img :src="userInfo.avatar" alt="头像" />
    </div>
    
    <div v-if="loading">加载中...</div>
    <div v-else>
      <h3>我的收藏</h3>
      <ul>
        <li v-for="article in collections" :key="article.id">
          {{ article.title }}
        </li>
      </ul>
    </div>
  </div>
</template>
```

### React Hooks 示例

```jsx
import { useState, useEffect } from 'react';

function UserProfile() {
  const [userInfo, setUserInfo] = useState(null);
  const [collections, setCollections] = useState([]);
  const [loading, setLoading] = useState(false);

  // 获取用户信息
  useEffect(() => {
    async function fetchUserInfo() {
      try {
        const res = await fetch('/api/user/info', {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        const result = await res.json();
        if (result.code === 200) {
          setUserInfo(result.data);
        }
      } catch (error) {
        console.error('加载用户信息失败:', error);
      }
    }
    fetchUserInfo();
  }, []);

  // 获取收藏列表
  useEffect(() => {
    async function fetchCollections() {
      setLoading(true);
      try {
        const res = await fetch('/api/user/foot/collection', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            pageNum: 1,
            pageSize: 10,
            order: 'desc'
          })
        });
        const result = await res.json();
        if (result.code === 200) {
          setCollections(result.data.list);
        }
      } catch (error) {
        console.error('加载收藏列表失败:', error);
      } finally {
        setLoading(false);
      }
    }
    fetchCollections();
  }, []);

  return (
    <div>
      {userInfo && (
        <div>
          <h2>{userInfo.nickname}</h2>
          <img src={userInfo.avatar} alt="头像" />
        </div>
      )}
      
      {loading ? (
        <div>加载中...</div>
      ) : (
        <div>
          <h3>我的收藏</h3>
          <ul>
            {collections.map(article => (
              <li key={article.id}>{article.title}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
```

---

## 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.0 | 2026-03-15 | 初始版本，包含用户基本信息、更新及足迹查询接口 |

---

## 联系方式

如有问题，请联系开发团队或查看项目文档。
