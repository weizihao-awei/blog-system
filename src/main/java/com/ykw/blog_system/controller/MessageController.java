package com.ykw.blog_system.controller;

import com.ykw.blog_system.dto.ChatIdQueryDTO;
import com.ykw.blog_system.dto.ChatListQueryDTO;
import com.ykw.blog_system.dto.MessageListQueryDTO;
import com.ykw.blog_system.dto.SendMessageDTO;
import com.ykw.blog_system.service.MessageService;
import com.ykw.blog_system.vo.ChatVO;
import com.ykw.blog_system.vo.MessageVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取聊天会话列表
     *
     * @param queryDTO 查询参数
     * @return 聊天会话分页数据
     */
    @PostMapping("/chat/list")
    public ResultVO<PageVO<ChatVO>> getChatList(@RequestBody ChatListQueryDTO queryDTO) {
        return messageService.getChatList(queryDTO);
    }

    /**
     * 获取聊天会话总数
     *
     * @return 聊天会话总数
     */
    @PostMapping("/chat/count")
    public ResultVO<Long> getChatCount() {
        return messageService.getChatCount();
    }

    /**
     * 获取消息列表
     *
     * @param queryDTO 查询参数
     * @return 消息分页数据
     */
    @PostMapping("/list")
    public ResultVO<PageVO<MessageVO>> getMessageList(@RequestBody MessageListQueryDTO queryDTO) {
        return messageService.getMessageList(queryDTO);
    }

    /**
     * 获取指定会话的消息总数
     *
     * @param queryDTO 查询参数（包含会话 ID）
     * @return 消息总数
     */
    @PostMapping("/count")
    public ResultVO<Long> getMessageCount(@RequestBody MessageListQueryDTO queryDTO) {
        return messageService.getMessageCount(queryDTO.getChatId());
    }

    /**
     * 获取全站消息总数
     *
     * @return 全站消息总数
     */
    @PostMapping("/total/count")
    public ResultVO<Long> getTotalMessageCount() {
        return messageService.getTotalMessageCount();
    }

    /**
     * 获取未读消息数量
     *
     * @return 未读消息数量
     */
    @PostMapping("/unread/count")
    public ResultVO<Long> getUnreadMessageCount() {
        return messageService.getUnreadMessageCount();
    }

    /**
     * 获取或创建聊天会话 ID
     *
     * @param queryDTO 查询参数
     * @return 聊天会话 ID
     */
    @PostMapping("/chat/id")
    public ResultVO<Long> getChatId(@RequestBody ChatIdQueryDTO queryDTO) {
        return messageService.getChatId(queryDTO);
    }

    /**
     * 发送消息
     *
     * @param sendMessageDTO 发送消息参数
     * @return 新消息的 ID
     */
    @PostMapping("/send")
    public ResultVO<Long> sendMessage(@RequestBody SendMessageDTO sendMessageDTO) {
        return messageService.sendMessage(sendMessageDTO);
    }
}
