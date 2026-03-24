# WebSocket前端连接指南

## 1. 连接地址

```text
// 开发环境
ws://localhost:8080/ws/message?token=your_jwt_token

// 生产环境
wss://your-domain.com/ws/message?token=your_jwt_token
```

**说明**：`token` 为登录成功后获取的 JWT Token。

## 2. 前端连接示例

```javascript
let socket = null;
const token = localStorage.getItem('token');

function connect() {
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

  socket.onclose = () => {
    console.log('WebSocket 连接关闭，将在 3 秒后重连');
    setTimeout(connect, 3000); // 自动重连
  };
}

// 初始化连接
connect();
```

## 3. 注意事项

- **消息发送**：所有消息发送仍使用 HTTP 接口（如 `/api/message/send`），WebSocket 仅用于**服务端主动推送**。
- **连接时机**：确保前端在用户登录后才尝试建立连接。
- **生产环境**：必须使用 `wss` 加密连接。
