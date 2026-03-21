# 作者信息 API 文档

## 基础信息

- **Base URL**: `/api/user`
- **Content-Type**: `application/json`
- **认证方式**: Bearer Token (从请求头获取)

## 统一响应格式

所有接口统一使用以下响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1234567890123
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 401 | token已过期 |
| 402 | token无效 |
| 407 | 参数错误 |

---

## 1. 获取作者信息

### 接口信息

- **请求方法**: `POST`
- **请求路径**: `/api/user/author/info`

### 请求参数

**请求体 (Request Body)**:

```json
{
  "userId": 123456789
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 要查询的用户ID |

### 响应示例

**成功响应**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 123456789,
    "nickname": "用户昵称",
    "avatar": "https://example.com/avatar.jpg",
    "gender": 1,
    "intro": "个人简介",
    "signature": "个性签名",
    "followersCount": 100,
    "followingCount": 50,
    "articlesCount": 20,
    "totalViews": 5000,
    "totalLikes": 300,
    "totalCollections": 150
  },
  "timestamp": 1234567890123
}
```

**失败响应**:

```json
{
  "code": 407,
  "message": "参数错误",
  "data": null,
  "timestamp": 1234567890123
}
```

### 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 用户ID |
| nickname | String | 昵称 |
| avatar | String | 头像URL |
| gender | Integer | 性别：0-未知，1-男，2-女 |
| intro | String | 个人简介 |
| signature | String | 个性签名 |
| followersCount | Long | 粉丝数量 |
| followingCount | Long | 关注数量 |
| articlesCount | Long | 文章数量 |
| totalViews | Long | 总浏览数 |
| totalLikes | Long | 总点赞数 |
| totalCollections | Long | 总收藏数 |

---

## 注意事项

1. **认证**: 接口需要在请求头中携带有效的 Bearer Token
2. **用户ID**: 可以查询任意用户的作者信息，不限于当前用户
3. **统计数据**: 统计数据只包含已发布的文章（status=1）
4. **数据汇总**: 总浏览数、总点赞数、总收藏数是该用户所有已发布文章的汇总数据

## 错误处理

接口在遇到错误时会返回相应的状态码和错误信息，前端应根据 `code` 字段判断请求是否成功，并根据 `message` 字段显示错误提示给用户。

常见错误处理建议：
- 401/402: 提示用户重新登录
- 407: 提示用户参数错误，检查请求参数是否正确
