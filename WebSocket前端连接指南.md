# WebSocket 前端连接指南

本文档为前端开发人员提供与后端 WebSocket 服务建立连接的完整说明。

## 1. WebSocket 连接地址

- **协议**: `ws`（开发环境）或 `wss`（生产环境）
- **基础路径**: `/ws/message`（需确保 Nginx 或网关正确转发）
- **认证方式**: 通过 URL 参数传递 Token

### 示例连接 URL
```text
// 开发环境
ws://localhost:8080/ws/message?token=your_jwt_token

// 生产环境
wss://your-domain.com/ws/message?token=your_jwt_token
```

> ⚠️ 注意：
> - `token` 为登录成功后获取的 JWT Token
> - 若 token 验证失败，连接将被拒绝

## 2. 消息格式

### 接收消息格式（服务端 → 客户端）
```json
{
  "type": "NOTIFICATION|CHAT_MESSAGE|SYSTEM_ALERT",
  "data": {},
  "timestamp": 1711111111
}
```

### 发送消息格式（客户端 → 服务端）
> 当前系统未启用 WebSocket 接收逻辑，所有写操作请走 HTTP API。

如需发送消息，请调用：
```http
POST /api/message/send
Content-Type: application/json

{
  "receiverId": 123,
  "content": "Hello"
}
```

## 3. 前端连接示例（JavaScript）

```javascript
let socket = null;
const token = localStorage.getItem('token'); // 获取登录 token

function connect() {
  if (socket && (socket.readyState === WebSocket.OPEN || socket.readyState === WebSocket.CONNECTING)) {
    console.log('WebSocket 已连接或正在连接中');
    return;
  }

  const url = `ws://localhost:8080/ws/message?token=${token}`;
  socket = new WebSocket(url);

  socket.onopen = () => {
    console.log('WebSocket 连接已建立');
  };

  socket.onmessage = (event) => {
    const message = JSON.parse(event.data);
    console.log('收到消息:', message);
    // TODO: 处理通知或消息
  };

  socket.onerror = (error) => {
    console.error('WebSocket 错误:', error);
  };

  socket.onclose = (event) => {
    console.log('WebSocket 连接关闭，将在 3 秒后重连', event.code, event.reason);
    setTimeout(connect, 3000); // 自动重连
  };
}

// 初始化连接
connect();
```

## 4. 心跳机制

前端建议每 30 秒发送一次心跳包以保持连接活跃（可选）：
```javascript
setInterval(() => {
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify({ type: 'PING' }));
  }
}, 30000);
```

## 5. 安全与错误处理

| 错误码 | 含义 | 建议操作 |
|-------|------|--------|
| 401   | Token 无效或缺失 | 跳转登录页重新登录 |
| 403   | 权限不足 | 提示用户无权限 |
| 1006  | 连接异常关闭 | 尝试重连 |

## 6. 注意事项

- 所有消息发送仍使用 HTTP 接口（如 `/api/message/send`），WebSocket 仅用于**服务端主动推送**。
- 确保前端在用户登录后才尝试建立连接。
- 生产环境必须使用 `wss` 加密连接。
- 若部署了 Nginx，需配置 WebSocket 支持（Upgrade 头部转发）。
