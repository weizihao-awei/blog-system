# 图片管理模块 API 接口文档

本文档描述博客系统中图片管理相关的所有 API 接口，供前端开发人员使用。

## 基础信息

- **基础路径**: `/api/image`
- **认证方式**: JWT Token（在请求头中携带）
- **请求头格式**: `Authorization: Bearer <your_token>`
- **数据格式**: multipart/form-data（上传）、JSON（响应）

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

### 1. 上传图片

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
```http
POST /api/image/upload HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="category"

avatar
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="file"; filename="profile.jpg"
Content-Type: image/jpeg

(binary data)
------WebKitFormBoundary7MA4YWxkTrZu0gW--
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

**前端调用示例 (JavaScript)**:
```javascript
async function uploadImage(category, file) {
  const formData = new FormData();
  formData.append('category', category);
  formData.append('file', file);
  
  try {
    const response = await fetch('/api/image/upload', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formData
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      console.log('图片 URI:', result.data.uri);
      return result.data.uri;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('上传失败:', error);
    throw error;
  }
}

// 使用示例
const fileInput = document.getElementById('imageInput');
const file = fileInput.files[0];

if (file) {
  // 验证文件大小
  if (file.size > 10 * 1024 * 1024) {
    alert('文件大小不能超过 10MB');
  } else {
    uploadImage('avatar', file)
      .then(uri => {
        console.log('上传成功:', uri);
        // 更新头像
        document.getElementById('avatar').src = uri;
      })
      .catch(error => {
        alert('上传失败：' + error.message);
      });
  }
}
```

**Vue 3 组件示例**:
```vue
<script setup>
import { ref } from 'vue';

const uploading = ref(false);
const imageUrl = ref('');

async function handleFileChange(event) {
  const file = event.target.files[0];
  if (!file) return;
  
  // 验证文件
  if (file.size > 10 * 1024 * 1024) {
    alert('文件大小不能超过 10MB');
    return;
  }
  
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (!allowedTypes.includes(file.type)) {
    alert('只支持 jpg、png、gif、webp 格式的图片');
    return;
  }
  
  uploading.value = true;
  
  try {
    const formData = new FormData();
    formData.append('category', 'avatar');
    formData.append('file', file);
    
    const response = await fetch('/api/image/upload', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formData
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      imageUrl.value = result.data.uri;
      alert('上传成功！');
    } else {
      alert('上传失败：' + result.message);
    }
  } catch (error) {
    console.error('上传错误:', error);
    alert('上传失败：' + error.message);
  } finally {
    uploading.value = false;
  }
}
</script>

<template>
  <div>
    <input 
      type="file" 
      accept="image/*"
      @change="handleFileChange"
      :disabled="uploading"
    />
    <div v-if="uploading">上传中...</div>
    <img v-if="imageUrl" :src="imageUrl" alt="上传的图片" />
  </div>
</template>
```

**注意事项**:
- ⚠️ category 参数会作为目录名，建议使用小写字母和数字
- ✅ 系统会自动创建不存在的目录
- ✅ 文件名会自动使用 UUID 重命名，无需担心文件名冲突
- ✅ 上传成功后，返回的 uri 可以直接用于 `<img>` 标签的 src 属性

---

### 2. 更新图片

**接口说明**: 替换已有图片（自动删除旧图片并上传新图片）

**请求信息**:
- **方法**: `PUT`
- **路径**: `/api/image/update`
- **认证**: 需要
- **Content-Type**: `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| category | String | 是 | 图片分类目录 |
| oldUri | String | 否 | 旧图片 URI（用于删除） |
| file | File | 是 | 新图片文件 |

**请求示例**:
```http
PUT /api/image/update HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="category"

avatar
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="oldUri"

/api/image/avatar/old-image.jpg
------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="file"; filename="new-profile.jpg"
Content-Type: image/jpeg

(binary data)
------WebKitFormBoundary7MA4YWxkTrZu0gW--
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "uri": "/api/image/avatar/b2c3d4e5-f6a7-8901-bcde-f12345678901.jpg",
    "filename": "b2c3d4e5-f6a7-8901-bcde-f12345678901.jpg"
  },
  "timestamp": 1710500000000
}
```

**Data 字段说明**: 与"上传图片"接口相同

**限制说明**: 与"上传图片"接口相同

**前端调用示例 (JavaScript)**:
```javascript
async function updateImage(category, oldUri, file) {
  const formData = new FormData();
  formData.append('category', category);
  formData.append('oldUri', oldUri || '');
  formData.append('file', file);
  
  try {
    const response = await fetch('/api/image/update', {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formData
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      console.log('新图片 URI:', result.data.uri);
      return result.data.uri;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('更新失败:', error);
    throw error;
  }
}

// 使用示例：更新用户头像
const currentAvatarUri = '/api/image/avatar/old-image.jpg';
const newFile = document.getElementById('avatarInput').files[0];

updateImage('avatar', currentAvatarUri, newFile)
  .then(newUri => {
    console.log('头像更新成功:', newUri);
    // 更新页面显示
    document.getElementById('userAvatar').src = newUri;
  })
  .catch(error => {
    alert('更新失败：' + error.message);
  });
```

**Vue 3 组件示例**:
```vue
<script setup>
import { ref } from 'vue';

const props = defineProps({
  currentUri: String
});

const updating = ref(false);
const newImageUrl = ref('');

async function handleUpdate(event) {
  const file = event.target.files[0];
  if (!file) return;
  
  // 验证文件
  if (file.size > 10 * 1024 * 1024) {
    alert('文件大小不能超过 10MB');
    return;
  }
  
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (!allowedTypes.includes(file.type)) {
    alert('只支持 jpg、png、gif、webp 格式的图片');
    return;
  }
  
  updating.value = true;
  
  try {
    const formData = new FormData();
    formData.append('category', 'avatar');
    formData.append('oldUri', props.currentUri || '');
    formData.append('file', file);
    
    const response = await fetch('/api/image/update', {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formData
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      newImageUrl.value = result.data.uri;
      // 触发事件通知父组件
      emit('update', result.data.uri);
      alert('更新成功！');
    } else {
      alert('更新失败：' + result.message);
    }
  } catch (error) {
    console.error('更新错误:', error);
    alert('更新失败：' + error.message);
  } finally {
    updating.value = false;
  }
}
</script>

<template>
  <div>
    <input 
      type="file" 
      accept="image/*"
      @change="handleUpdate"
      :disabled="updating"
    />
    <div v-if="updating">更新中...</div>
  </div>
</template>
```

**注意事项**:
- ⚠️ 如果 oldUri 为空或 null，则只上传新图片，不删除旧图片
- ✅ 建议传入旧的 URI，以便系统清理无用文件
- ✅ 适用于头像更换、封面图替换等场景

---

### 3. 获取图片

**接口说明**: 根据分类和文件名获取图片（二进制流）

**请求信息**:
- **方法**: `GET`
- **路径**: `/api/image/{category}/{filename}`
- **认证**: 不需要（公开访问）
- **响应类型**: 图片二进制流

**请求参数**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| category | String | Path | 是 | 图片分类目录 |
| filename | String | Path | 是 | 文件名 |

**请求示例**:
```http
GET /api/image/avatar/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg HTTP/1.1
```

**响应示例**:
```
HTTP/1.1 200 OK
Content-Type: image/jpeg
Content-Disposition: inline; filename="a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg"

(图片二进制数据)
```

**错误响应**:
```http
# 文件不存在
HTTP/1.1 404 Not Found

# 服务器错误
HTTP/1.1 500 Internal Server Error
```

**前端使用方式**:

**1. 直接在 img 标签中使用**:
```html
<img src="/api/image/avatar/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg" alt="头像" />
```

**2. 在 Vue 组件中使用**:
```vue
<template>
  <img :src="imageUrl" alt="用户头像" />
</template>

<script setup>
const props = defineProps({
  filename: String
});

const imageUrl = `/api/image/avatar/${props.filename}`;
</script>
```

**3. 动态加载图片**:
```javascript
function loadImage(category, filename) {
  return `/api/image/${category}/${filename}`;
}

// 使用
const avatarUrl = loadImage('avatar', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg');
document.getElementById('avatar').src = avatarUrl;
```

**注意事项**:
- ✅ 该接口不需要认证，任何用户都可以访问
- ✅ 系统会自动识别图片类型并设置正确的 Content-Type
- ✅ 支持浏览器缓存（可通过添加缓存头优化）
- ⚠️ 如果文件不存在，返回 404 状态码

---

### 4. 删除图片

**接口说明**: 根据 URI 删除指定的图片

**请求信息**:
- **方法**: `DELETE`
- **路径**: `/api/image/delete`
- **认证**: 需要
- **Content-Type**: `application/x-www-form-urlencoded` 或 `multipart/form-data`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| uri | String | 是 | 图片 URI（如：/api/image/avatar/xxx.jpg） |

**请求示例**:
```http
DELETE /api/image/delete?uri=/api/image/avatar/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**或使用 form-data**:
```http
DELETE /api/image/delete HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/x-www-form-urlencoded

uri=%2Fapi%2Fimage%2Favatar%2Fa1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
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

**错误响应**:
```json
{
  "code": 500,
  "message": "文件不存在或删除失败",
  "data": null,
  "timestamp": 1710500000000
}
```

**前端调用示例 (JavaScript)**:
```javascript
async function deleteImage(uri) {
  try {
    const response = await fetch(`/api/image/delete?uri=${encodeURIComponent(uri)}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      console.log('删除成功');
      return true;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('删除失败:', error);
    throw error;
  }
}

// 使用示例
const oldAvatarUri = '/api/image/avatar/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg';

deleteImage(oldAvatarUri)
  .then(() => {
    console.log('旧头像已删除');
  })
  .catch(error => {
    console.error('删除失败:', error.message);
  });
```

**注意事项**:
- ⚠️ URI 需要进行 URL 编码（使用 encodeURIComponent）
- ⚠️ 如果文件不存在，会返回错误消息但不会抛出异常
- ✅ 建议在更新图片时使用（配合 update 接口）
- ⚠️ 删除操作不可恢复，请谨慎使用

---

## ImageVO 数据结构说明

所有图片相关接口返回的 ImageVO 结构如下：

```json
{
  "uri": "/api/image/avatar/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg",
  "filename": "a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| uri | String | 图片完整访问路径（可用于 img 标签 src） |
| filename | String | 保存后的文件名（UUID 格式） |

---

## 常见图片分类建议

根据业务场景，常用的图片分类包括：

| 分类名 | 用途 | 示例 |
|--------|------|------|
| avatar | 用户头像 | /api/image/avatar/xxx.jpg |
| article | 文章内容图片 | /api/image/article/xxx.png |
| cover | 文章封面 | /api/image/cover/xxx.jpg |
| banner | 轮播图/Banner | /api/image/banner/xxx.jpg |
| temp | 临时图片（定期清理） | /api/image/temp/xxx.png |

**使用建议**:
- ✅ 根据业务合理划分分类，便于后期管理和清理
- ✅ 可以设置定时任务清理 temp 分类中的过期图片
- ✅ 不同分类可以设置不同的存储策略（如 CDN 加速）

---

## 错误处理

### 常见错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误（文件为空、格式不支持等） |
| 401 | 未授权（Token 无效或过期） |
| 404 | 文件不存在 |
| 413 | 文件过大（超过 10MB） |
| 500 | 服务器内部错误（IO 异常等） |

### 错误响应示例

**文件为空**:
```json
{
  "code": 500,
  "message": "文件不能为空",
  "data": null,
  "timestamp": 1710500000000
}
```

**文件格式不支持**:
```json
{
  "code": 500,
  "message": "只支持 jpg、png、gif、webp 格式的图片",
  "data": null,
  "timestamp": 1710500000000
}
```

**文件过大**:
```json
{
  "code": 500,
  "message": "文件大小不能超过 10MB",
  "data": null,
  "timestamp": 1710500000000
}
```

**上传失败**:
```json
{
  "code": 500,
  "message": "上传失败：IOException details...",
  "data": null,
  "timestamp": 1710500000000
}
```

### 前端错误处理示例

```javascript
async function uploadImageWithValidation(category, file) {
  // 前置验证
  if (!file) {
    throw new Error('请选择要上传的文件');
  }
  
  if (file.size > 10 * 1024 * 1024) {
    throw new Error('文件大小不能超过 10MB');
  }
  
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (!allowedTypes.includes(file.type)) {
    throw new Error('只支持 jpg、png、gif、webp 格式的图片');
  }
  
  // 上传
  const formData = new FormData();
  formData.append('category', category);
  formData.append('file', file);
  
  try {
    const response = await fetch('/api/image/upload', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formData
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data.uri;
    } else if (result.code === 401) {
      // Token 失效，跳转登录
      localStorage.removeItem('token');
      window.location.href = '/login';
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('上传错误:', error);
    throw error;
  }
}
```

---

## 完整使用流程示例

### 场景：用户上传/更新头像

```javascript
class AvatarManager {
  constructor() {
    this.currentAvatarUri = null;
  }
  
  // 上传新头像
  async uploadAvatar(file) {
    try {
      const uri = await this.uploadImage('avatar', file);
      this.currentAvatarUri = uri;
      return uri;
    } catch (error) {
      console.error('上传头像失败:', error);
      throw error;
    }
  }
  
  // 更新头像
  async updateAvatar(file) {
    try {
      const uri = await this.updateImage('avatar', this.currentAvatarUri, file);
      this.currentAvatarUri = uri;
      return uri;
    } catch (error) {
      console.error('更新头像失败:', error);
      throw error;
    }
  }
  
  // 删除头像
  async deleteAvatar() {
    try {
      if (this.currentAvatarUri) {
        await this.deleteImage(this.currentAvatarUri);
        this.currentAvatarUri = null;
      }
    } catch (error) {
      console.error('删除头像失败:', error);
      throw error;
    }
  }
  
  // 通用上传方法
  async uploadImage(category, file) {
    const formData = new FormData();
    formData.append('category', category);
    formData.append('file', file);
    
    const response = await fetch('/api/image/upload', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formData
    });
    
    const result = await response.json();
    if (result.code === 200) {
      return result.data.uri;
    } else {
      throw new Error(result.message);
    }
  }
  
  // 通用更新方法
  async updateImage(category, oldUri, file) {
    const formData = new FormData();
    formData.append('category', category);
    formData.append('oldUri', oldUri || '');
    formData.append('file', file);
    
    const response = await fetch('/api/image/update', {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formData
    });
    
    const result = await response.json();
    if (result.code === 200) {
      return result.data.uri;
    } else {
      throw new Error(result.message);
    }
  }
  
  // 通用删除方法
  async deleteImage(uri) {
    const response = await fetch(`/api/image/delete?uri=${encodeURIComponent(uri)}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    
    const result = await response.json();
    if (result.code === 200) {
      return true;
    } else {
      throw new Error(result.message);
    }
  }
}

// 使用示例
const avatarManager = new AvatarManager();

// 上传头像
const fileInput = document.getElementById('avatarInput');
fileInput.addEventListener('change', async (e) => {
  const file = e.target.files[0];
  if (file) {
    try {
      const uri = await avatarManager.uploadAvatar(file);
      document.getElementById('userAvatar').src = uri;
      console.log('头像上传成功:', uri);
    } catch (error) {
      alert('头像上传失败：' + error.message);
    }
  }
});
```

---

## React Hooks 示例

```jsx
import { useState, useCallback } from 'react';

function ImageUploader({ category, onUploadSuccess }) {
  const [uploading, setUploading] = useState(false);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [error, setError] = useState(null);

  const handleUpload = useCallback(async (file) => {
    if (!file) return;
    
    // 验证
    if (file.size > 10 * 1024 * 1024) {
      setError('文件大小不能超过 10MB');
      return;
    }
    
    const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    if (!allowedTypes.includes(file.type)) {
      setError('只支持 jpg、png、gif、webp 格式的图片');
      return;
    }
    
    setUploading(true);
    setError(null);
    
    try {
      const formData = new FormData();
      formData.append('category', category);
      formData.append('file', file);
      
      const response = await fetch('/api/image/upload', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: formData
      });
      
      const result = await response.json();
      
      if (result.code === 200) {
        setPreviewUrl(result.data.uri);
        onUploadSuccess?.(result.data.uri);
      } else {
        setError(result.message);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setUploading(false);
    }
  }, [category, onUploadSuccess]);

  return (
    <div>
      <input
        type="file"
        accept="image/*"
        onChange={(e) => handleUpload(e.target.files[0])}
        disabled={uploading}
      />
      
      {uploading && <div>上传中...</div>}
      {error && <div style={{ color: 'red' }}>{error}</div>}
      
      {previewUrl && (
        <img src={previewUrl} alt="预览" style={{ maxWidth: '200px' }} />
      )}
    </div>
  );
}

export default ImageUploader;
```

---

## 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.0 | 2026-03-15 | 初始版本，包含图片上传、更新、获取、删除接口 |

---

## 联系方式

如有问题，请联系开发团队或查看项目文档。
