# 文章查询接口使用说明

## 接口变更说明

**原接口：**
- `GET /api/article/list` - 获取文章列表（按分类、关键字）
- `GET /api/article/tag/{tagId}` - 根据标签获取文章

**新接口：**
- `POST /api/article/query` - 通用文章查询接口 ✨

## 为什么改用 POST？

1. **参数复杂性**：查询条件包含多个可选参数，使用 POST + RequestBody 更清晰
2. **URL 长度限制**：避免 GET 请求参数过长的问题
3. **语义化**：复杂查询更适合使用 POST
4. **扩展性**：方便未来添加更多查询条件

## 新接口使用方式

### 接口地址
```
POST /api/article/query
Content-Type: application/json
```

### 请求参数（全部可选）

```json
{
  "pageNum": 1,           // 页码，默认 1
  "pageSize": 10,         // 每页数量，默认 10
  "categoryId": 1,        // 分类 ID（可选）
  "tagId": 5,             // 标签 ID（可选）
  "keyword": "Spring",    // 搜索关键字（可选）
  "orderBy": "latest"     // 排序方式：latest-最新，hot-热门，recommend-推荐
}
```

### 排序方式说明

| orderBy 值 | 说明 | 排序规则 |
|-----------|------|---------|
| `latest` (默认) | 最新 | 按发布时间降序 |
| `hot` | 热门 | 按浏览量 → 点赞数降序 |
| `recommend` | 推荐 | 按推荐标志 → 发布时间降序 |

### 请求示例

#### 1. 获取默认文章列表（第一页，每页 10 条）
```bash
POST /api/article/query
Content-Type: application/json

{}
```

#### 2. 查询特定分类的文章
```json
{
  "pageNum": 1,
  "pageSize": 20,
  "categoryId": 1
}
```

#### 3. 查询特定标签的文章
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "tagId": 5
}
```

#### 4. 关键字搜索
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "keyword": "Java"
}
```

#### 5. 组合查询（分类 + 关键字 + 热门排序）
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "categoryId": 1,
  "keyword": "Spring Boot",
  "orderBy": "hot"
}
```

#### 6. 查询推荐文章
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "orderBy": "recommend"
}
```

### 响应格式

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
        "coverImage": "/uploads/cover.jpg",
        "authorId": 1,
        "categoryId": 1,
        "viewCount": 1000,
        "likeCount": 50,
        "commentCount": 10,
        "publishTime": "2024-01-01T10:00:00",
        "tags": [
          {
            "id": 1,
            "name": "Java",
            "color": "#E74C3C"
          }
        ],
        "authorName": "作者名",
        "categoryName": "技术分享"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 10,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "timestamp": 1234567890000
}
```

## 前端调用示例

### JavaScript / Axios

```javascript
// 封装查询函数
async function queryArticles(queryParams = {}) {
  const response = await axios.post('/api/article/query', queryParams);
  return response.data;
}

// 使用示例
queryArticles({
  pageNum: 1,
  pageSize: 10,
  categoryId: 1,
  keyword: 'Spring',
  orderBy: 'hot'
}).then(result => {
  console.log('查询结果:', result.data);
});
```

### Vue 3 示例

```vue
<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const articles = ref([]);
const loading = ref(false);

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  orderBy: 'latest'
});

// 查询函数
const fetchArticles = async () => {
  loading.value = true;
  try {
    const res = await axios.post('/api/article/query', queryParams.value);
    articles.value = res.data.data.list;
  } catch (error) {
    console.error('查询失败:', error);
  } finally {
    loading.value = false;
  }
};

// 切换分类
const changeCategory = (categoryId) => {
  queryParams.value.categoryId = categoryId;
  queryParams.value.pageNum = 1; // 重置页码
  fetchArticles();
};

// 切换排序
const changeOrderBy = (orderBy) => {
  queryParams.value.orderBy = orderBy;
  fetchArticles();
};

onMounted(() => {
  fetchArticles();
});
</script>
```

### React 示例

```jsx
import { useState, useEffect } from 'react';
import axios from 'axios';

function ArticleList() {
  const [articles, setArticles] = useState([]);
  const [queryParams, setQueryParams] = useState({
    pageNum: 1,
    pageSize: 10,
    orderBy: 'latest'
  });

  const fetchArticles = async () => {
    const res = await axios.post('/api/article/query', queryParams);
    setArticles(res.data.data.list);
  };

  useEffect(() => {
    fetchArticles();
  }, [queryParams]);

  return (
    <div>
      {/* 渲染文章列表 */}
    </div>
  );
}
```

## 迁移指南

### 旧代码（GET）
```javascript
// 原来的调用方式
axios.get('/api/article/list', {
  params: {
    pageNum: 1,
    pageSize: 10,
    categoryId: 1,
    keyword: 'Java'
  }
});

// 或者带标签的
axios.get('/api/article/tag/5', {
  params: {
    pageNum: 1,
    pageSize: 10
  }
});
```

### 新代码（POST）
```javascript
// 新的调用方式
axios.post('/api/article/query', {
  pageNum: 1,
  pageSize: 10,
  categoryId: 1,
  keyword: 'Java'
});

// 或者查询标签
axios.post('/api/article/query', {
  pageNum: 1,
  pageSize: 10,
  tagId: 5
});
```

## 注意事项

1. ✅ **向后兼容**：旧接口暂时保留，建议尽快迁移到新接口
2. ✅ **参数验证**：所有参数都是可选的，后端有默认值
3. ✅ **空请求体**：可以发送空对象 `{}`，会使用默认参数
4. ⚠️ **标签查询**：原来通过 path 变量传递 tagId，现在改为 body 参数
5. ✅ **分页参数**：pageNum 和 pageSize 有默认值，可以不传

## 常见问题

**Q: 为什么要删除原来的接口？**  
A: 整合接口可以减少 API 数量，简化维护成本，统一查询逻辑。

**Q: 如果我只需要默认列表，怎么传参？**  
A: 发送空对象即可：`POST /api/article/query {}`

**Q: 排序方式还有哪些选项？**  
A: 目前支持三种：`latest`（最新）、`hot`（热门）、`recommend`（推荐）

**Q: 可以同时使用 categoryId 和 tagId 吗？**  
A: 当前实现中，tagId 优先级更高。如果同时传入，会使用 tagId 查询。

---

**更新时间**：2026-03-11  
**版本**：v2.0
