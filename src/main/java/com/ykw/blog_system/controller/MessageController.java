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

    @PostMapping("/chat/list")
    public ResultVO<PageVO<ChatVO>> getChatList(@RequestBody ChatListQueryDTO queryDTO) {
        return messageService.getChatList(queryDTO);
    }

    @PostMapping("/chat/count")
    public ResultVO<Long> getChatCount() {
        return messageService.getChatCount();
    }

    @PostMapping("/list")
    public ResultVO<PageVO<MessageVO>> getMessageList(@RequestBody MessageListQueryDTO queryDTO) {
        return messageService.getMessageList(queryDTO);
    }

    @PostMapping("/count")
    public ResultVO<Long> getMessageCount(@RequestBody MessageListQueryDTO queryDTO) {
        return messageService.getMessageCount(queryDTO.getChatId());
    }

    @PostMapping("/total/count")
    public ResultVO<Long> getTotalMessageCount() {
        return messageService.getTotalMessageCount();
    }

    @PostMapping("/unread/count")
    public ResultVO<Long> getUnreadMessageCount() {
        return messageService.getUnreadMessageCount();
    }

    @PostMapping("/chat/id")
    public ResultVO<Long> getChatId(@RequestBody ChatIdQueryDTO queryDTO) {
        return messageService.getChatId(queryDTO);
    }

    @PostMapping("/send")
    public ResultVO<Long> sendMessage(@RequestBody SendMessageDTO sendMessageDTO) {
        return messageService.sendMessage(sendMessageDTO);
    }
}
