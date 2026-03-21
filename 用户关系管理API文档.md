# 用户关系管理 API 文档

## 基础信息

- **Base URL**: `/api/relation`
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
| 404 | 不能关注自己 |
| 405 | 已关注该用户 |
| 406 | 未关注该用户 |
| 407 | 参数错误 |

---

## 1. 关注或取消关注用户

### 接口信息

- **请求方法**: `POST`
- **请求路径**: `/api/relation/follow`

### 请求参数

**请求体 (Request Body)**:

```json
{
  "targetUserId": 123456789,
  "operation": 1
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| targetUserId | Long | 是 | 目标用户ID |
| operation | Integer | 是 | 操作类型：1-关注，2-取消关注 |

### 响应示例

**成功响应**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null,
  "timestamp": 1234567890123
}
```

**失败响应示例**:

```json
{
  "code": 404,
  "message": "不能关注自己",
  "data": null,
  "timestamp": 1234567890123
}
```

---

## 2. 获取粉丝列表

### 接口信息

- **请求方法**: `POST`
- **请求路径**: `/api/relation/followers`

### 请求参数

**请求体 (Request Body)**:

```json
{
  "userId": 123456789,
  "pageNum": 1,
  "pageSize": 10
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 要查询的用户ID |
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |

### 响应示例

**成功响应**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 123456789,
        "username": "user001",
        "nickname": "用户昵称",
        "avatar": "https://example.com/avatar.jpg",
        "email": "user@example.com",
        "gender": 1,
        "intro": "个人简介",
        "signature": "个性签名",
        "role": 0,
        "status": 1,
        "createTime": "2024-01-01T00:00:00",
        "updateTime": "2024-01-01T00:00:00"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 10,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "timestamp": 1234567890123
}
```

### 用户对象字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 用户ID |
| username | String | 用户名 |
| nickname | String | 昵称 |
| avatar | String | 头像URL |
| email | String | 邮箱 |
| gender | Integer | 性别：0-未知，1-男，2-女 |
| intro | String | 个人简介 |
| signature | String | 个性签名 |
| role | Integer | 角色：0-普通用户，1-管理员 |
| status | Integer | 状态：0-禁用，1-启用 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

---

## 3. 获取关注列表

### 接口信息

- **请求方法**: `POST`
- **请求路径**: `/api/relation/following`

### 请求参数

**请求体 (Request Body)**:

```json
{
  "userId": 123456789,
  "pageNum": 1,
  "pageSize": 10
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 要查询的用户ID |
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |

### 响应示例

**成功响应**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [
      {
        "id": 123456789,
        "username": "user001",
        "nickname": "用户昵称",
        "avatar": "https://example.com/avatar.jpg",
        "email": "user@example.com",
        "gender": 1,
        "intro": "个人简介",
        "signature": "个性签名",
        "role": 0,
        "status": 1,
        "createTime": "2024-01-01T00:00:00",
        "updateTime": "2024-01-01T00:00:00"
      }
    ],
    "total": 50,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 5,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "timestamp": 1234567890123
}
```

---

## 4. 获取粉丝数量

### 接口信息

- **请求方法**: `GET`
- **请求路径**: `/api/relation/followers/count`

### 请求参数

**Query Parameters**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 要查询的用户ID |

**请求示例**: `GET /api/relation/followers/count?userId=123456789`

### 响应示例

**成功响应**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 100,
  "timestamp": 1234567890123
}
```

---

## 5. 获取关注数量

### 接口信息

- **请求方法**: `GET`
- **请求路径**: `/api/relation/following/count`

### 请求参数

**Query Parameters**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 要查询的用户ID |

**请求示例**: `GET /api/relation/following/count?userId=123456789`

### 响应示例

**成功响应**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 50,
  "timestamp": 1234567890123
}
```

---

## 注意事项

1. **认证**: 所有接口都需要在请求头中携带有效的 Bearer Token
2. **用户ID**: 粉丝列表和关注列表接口可以查询任意用户的关注和粉丝信息，不限于当前用户
3. **分页**: 列表接口支持分页，建议前端实现分页加载
4. **密码**: 用户对象中不包含密码字段，确保安全性
5. **关注状态**: 只统计关注状态为"已关注"的记录，不包含"未关注"和"取消关注"的记录

## 错误处理

所有接口在遇到错误时都会返回相应的状态码和错误信息，前端应根据 `code` 字段判断请求是否成功，并根据 `message` 字段显示错误提示给用户。

常见错误处理建议：
- 401/402: 提示用户重新登录
- 404: 提示用户不能关注自己
- 405: 提示用户已关注该用户
- 406: 提示用户未关注该用户
- 407: 提示用户参数错误，检查请求参数是否正确
