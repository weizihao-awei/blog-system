# 用户文章查询 API 文档

## 接口说明

该接口用于查询指定用户发布的文章列表，支持分页和排序。

---

## 查询用户发布的文章

### 接口地址
```
POST /api/user/articles/published
```

### 接口描述
- **功能**：查询指定用户发布的所有文章（已发布状态）
- **权限**：需要登录认证
- **适用范围**：可查询任意用户的文章，不仅限于当前登录用户

### 请求参数

**请求头：**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**请求体（JSON）：**

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| userId | Long | 是 | - | 要查询的文章作者 ID |
| pageNum | Integer | 否 | 1 | 页码，从 1 开始 |
| pageSize | Integer | 否 | 10 | 每页记录数 |
| order | String | 否 | "desc" | 排序方式：`asc`-从早到晚，`desc`-从晚到早 |

**请求示例：**
```json
{
  "userId": 123,
  "pageNum": 1,
  "pageSize": 10,
  "order": "desc"
}
```

### 响应结果

**响应格式：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "文章标题",
        "summary": "文章摘要",
        "content": "文章内容",
        "htmlContent": "<p>HTML 格式的文章内容</p>",
        "coverImage": "/uploads/cover/xxx.jpg",
        "authorId": 123,
        "categoryId": 1,
        "viewCount": 100,
        "likeCount": 50,
        "commentCount": 20,
        "collectionCount": 30,
        "status": 1,
        "isTop": 0,
        "isRecommend": 1,
        "publishTime": "2024-03-20T10:00:00",
        "createTime": "2024-03-19T10:00:00",
        "updateTime": "2024-03-20T10:00:00",
        "authorName": "作者昵称",
        "authorAvatar": "/uploads/avatar/xxx.png",
        "categoryName": "分类名称",
        "tags": [
          {
            "id": 1,
            "name": "标签名",
            "createTime": "2024-03-19T10:00:00"
          }
        ],
        "isLiked": true,
        "isCollected": false,
        "recommendScore": 0.95
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 10,
    "hasNextPage": true,
    "hasPreviousPage": false
  }
}
```

**响应字段说明：**

#### 外层响应
| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码，200 表示成功 |
| message | String | 响应消息 |
| data | Object | 响应数据 |

#### 分页数据（data）
| 字段名 | 类型 | 说明 |
|--------|------|------|
| list | Array | 文章列表 |
| total | Long | 总记录数 |
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页大小 |
| totalPages | Integer | 总页数 |
| hasNextPage | Boolean | 是否有下一页 |
| hasPreviousPage | Boolean | 是否有上一页 |

#### 文章对象（list[]）
| 字段名 | 类型 | 说明 |
|--------|------|------|
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
| authorName | String | 作者昵称 |
| authorAvatar | String | 作者头像 |
| categoryName | String | 分类名称 |
| tags | Array | 标签列表 |
| isLiked | Boolean | 当前用户是否已点赞 |
| isCollected | Boolean | 当前用户是否已收藏 |
| recommendScore | Double | 推荐得分 |

### 错误响应

**错误示例 1：未授权**
```json
{
  "code": 401,
  "message": "未授权",
  "data": null
}
```

**错误示例 2：用户不存在**
```json
{
  "code": 500,
  "message": "系统内部错误",
  "data": null
}
```

### 使用示例

#### JavaScript (Axios)
```javascript
const axios = require('axios');

async function getUserPublishedArticles(userId, pageNum = 1, pageSize = 10, order = 'desc') {
  try {
    const response = await axios.post(
      '/api/user/articles/published',
      {
        userId,
        pageNum,
        pageSize,
        order
      },
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      }
    );
    
    if (response.data.code === 200) {
      return response.data.data;
    } else {
      throw new Error(response.data.message);
    }
  } catch (error) {
    console.error('查询失败:', error);
    throw error;
  }
}

// 使用示例
getUserPublishedArticles(123, 1, 10, 'desc')
  .then(result => {
    console.log('文章列表:', result.list);
    console.log('总数:', result.total);
  })
  .catch(error => {
    console.error('错误:', error);
  });
```

#### Vue 3
```vue
<template>
  <div class="article-list">
    <div v-for="article in articles" :key="article.id" class="article-item">
      <h3>{{ article.title }}</h3>
      <p>{{ article.summary }}</p>
      <span>浏览：{{ article.viewCount }}</span>
      <span>点赞：{{ article.likeCount }}</span>
    </div>
    <div class="pagination">
      <button @click="loadPage(pageNum - 1)" :disabled="pageNum <= 1">上一页</button>
      <span>{{ pageNum }} / {{ totalPages }}</span>
      <button @click="loadPage(pageNum + 1)" :disabled="pageNum >= totalPages">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const articles = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const totalPages = ref(0);
const userId = ref(123); // 要查询的用户 ID

const loadArticles = async () => {
  try {
    const response = await axios.post(
      '/api/user/articles/published',
      {
        userId: userId.value,
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        order: 'desc'
      },
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );
    
    if (response.data.code === 200) {
      articles.value = response.data.data.list;
      totalPages.value = response.data.data.totalPages;
    }
  } catch (error) {
    console.error('加载文章失败:', error);
  }
};

const loadPage = (newPage) => {
  if (newPage >= 1 && newPage <= totalPages.value) {
    pageNum.value = newPage;
    loadArticles();
  }
};

onMounted(() => {
  loadArticles();
});
</script>
```

#### React
```jsx
import { useState, useEffect } from 'react';
import axios from 'axios';

function UserArticles({ userId }) {
  const [articles, setArticles] = useState([]);
  const [pageNum, setPageNum] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const loadArticles = async () => {
    setLoading(true);
    try {
      const response = await axios.post(
        '/api/user/articles/published',
        {
          userId,
          pageNum,
          pageSize: 10,
          order: 'desc'
        },
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      );

      if (response.data.code === 200) {
        setArticles(response.data.data.list);
        setTotalPages(response.data.data.totalPages);
      }
    } catch (error) {
      console.error('加载文章失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadArticles();
  }, [userId, pageNum]);

  return (
    <div className="article-list">
      {loading ? (
        <div>加载中...</div>
      ) : (
        <>
          {articles.map(article => (
            <div key={article.id} className="article-item">
              <h3>{article.title}</h3>
              <p>{article.summary}</p>
              <div className="stats">
                <span>浏览：{article.viewCount}</span>
                <span>点赞：{article.likeCount}</span>
                <span>评论：{article.commentCount}</span>
              </div>
            </div>
          ))}
          
          <div className="pagination">
            <button 
              onClick={() => setPageNum(p => p - 1)} 
              disabled={pageNum <= 1}
            >
              上一页
            </button>
            <span>{pageNum} / {totalPages}</span>
            <button 
              onClick={() => setPageNum(p => p + 1)} 
              disabled={pageNum >= totalPages}
            >
              下一页
            </button>
          </div>
        </>
      )}
    </div>
  );
}

export default UserArticles;
```

### 注意事项

1. **认证要求**：该接口需要用户登录后才能访问，请求时必须携带有效的 JWT Token
2. **参数说明**：
   - `userId` 为必填参数，用于指定要查询哪个用户的文章
   - 当 `userId` 为空或对应的用户不存在时，会返回空列表
   - 只返回状态为 1（已发布）的文章
3. **排序规则**：
   - `order` 参数支持 `asc` 和 `desc` 两个值
   - `asc`：按发布时间从早到晚排序
   - `desc`：按发布时间从晚到早排序（默认）
4. **分页规则**：
   - 页码从 1 开始
   - 如果请求的页码超过总页数，返回空列表
5. **性能建议**：
   - 建议前端实现虚拟滚动或懒加载，避免一次性加载大量数据
   - 合理使用分页，建议每页 10-20 条记录

### 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2024-03-21 | 初始版本，支持查询任意用户发布的文章 |
