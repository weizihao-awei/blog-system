# 文章接口变更说明

## 变更概述

由于前端界面将改为时间轴展示方式，文章相关接口进行了以下调整：
- 删除了原有的 `/latest` 接口
- 新增了三个时间轴相关接口

---

## 删除的接口

### 1. 获取最新文章（已删除）

**接口地址**: `POST /api/article/latest`

**状态**: ❌ 已删除

**说明**: 该接口已不再使用，请使用新的时间轴接口替代

---

## 新增的接口

### 1. 获取月份文章统计

**接口地址**: `POST /api/article/timeline/month-statistics`

**功能**: 统计指定月份每天发布的文章数量

**请求参数**:
```json
{
  "year": 2024,
  "month": 3
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| year | Integer | 是 | 年份，如 2024 |
| month | Integer | 是 | 月份，1-12 |

**返回示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "year": 2024,
    "month": 3,
    "dailyCounts": [
      {
        "day": 1,
        "count": 5
      },
      {
        "day": 2,
        "count": 3
      },
      {
        "day": 15,
        "count": 8
      }
    ]
  }
}
```

**返回字段说明**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| year | Integer | 年份 |
| month | Integer | 月份 |
| dailyCounts | Array | 每日统计数组 |
| dailyCounts[].day | Integer | 日期（1-31） |
| dailyCounts[].count | Integer | 当天发布的文章数量 |

---

### 2. 获取指定日期及之前的文章（时间轴向前查询）

**接口地址**: `POST /api/article/timeline/before`

**功能**: 查询指定日期及之前的文章，按发布时间倒序排列（最新的在前）

**请求参数**:
```json
{
  "date": "2024-03-15",
  "pageNum": 1,
  "pageSize": 10
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| date | LocalDate | 否 | 查询的日期，格式为 yyyy-MM-dd。不传则默认为当天 |
| pageNum | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页数量，默认 10 |

**返回示例**:
```json
{
  "code": 202,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "文章标题",
        "summary": "文章摘要",
        "content": "文章内容",
        "htmlContent": "HTML内容",
        "coverImage": "封面图URL",
        "authorId": 1,
        "categoryId": 1,
        "viewCount": 100,
        "likeCount": 50,
        "commentCount": 10,
        "collectionCount": 20,
        "status": 1,
        "isTop": 0,
        "isRecommend": 0,
        "publishTime": "2024-03-15T10:30:00",
        "createTime": "2024-03-15T10:00:00",
        "updateTime": "2024-03-15T10:00:00",
        "authorName": "作者昵称",
        "authorAvatar": "作者头像URL",
        "categoryName": "分类名称",
        "tags": [
          {
            "id": 1,
            "name": "标签名"
          }
        ],
        "isLiked": false,
        "isCollected": false
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

**使用场景**:
- 时间轴向前翻页（查看更早的文章）
- 从某个日期开始往前浏览

---

### 3. 获取指定日期及之后的文章（时间轴向后查询）

**接口地址**: `POST /api/article/timeline/after`

**功能**: 查询指定日期及之后的文章，按发布时间正序排列（最早的在前）

**请求参数**:
```json
{
  "date": "2024-03-15",
  "pageNum": 1,
  "pageSize": 10
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| date | LocalDate | 否 | 查询的日期，格式为 yyyy-MM-dd。不传则默认为当天 |
| pageNum | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页数量，默认 10 |

**返回示例**: 同接口 2

**使用场景**:
- 时间轴向后翻页（查看更新的文章）
- 从某个日期开始往后浏览
- 按时间顺序查看文章

---

## 接口使用建议

### 时间轴页面实现逻辑

1. **初始化加载**:
   - 调用 `/timeline/month-statistics` 获取当前月份的每日文章统计
   - 在时间轴上标记有文章的日期

2. **点击日期查看文章**:
   - 如果点击的是今天或之前的日期，调用 `/timeline/before` 接口
   - 如果点击的是未来的日期，调用 `/timeline/after` 接口

3. **分页加载**:
   - 向前翻页：继续调用 `/timeline/before`，使用相同的日期参数，增加 pageNum
   - 向后翻页：继续调用 `/timeline/after`，使用相同的日期参数，增加 pageNum

### 排序说明

- **`/timeline/before`**: 按发布时间**倒序**（DESC），最新的文章在前
- **`/timeline/after`**: 按发布时间**正序**（ASC），最早的文章在前

---

## 其他保留的接口

以下接口保持不变，可以继续使用：

- `POST /api/article/query` - 通用文章查询
- `GET /api/article/detail/{articleId}` - 获取文章详情
- `POST /api/article/hot` - 获取热门文章
- `POST /api/article/recommend` - 获取推荐文章

---

## 注意事项

1. 所有接口的 `date` 参数格式必须为 `yyyy-MM-DD`（如：2024-03-15）
2. 分页参数 `pageNum` 从 1 开始
3. 新接口返回的文章数据结构与原接口保持一致，包含完整的文章信息、作者信息、标签等
4. 建议前端对月份统计数据进行缓存，避免频繁请求

---

## 联系方式

如有疑问，请联系后端开发人员。
