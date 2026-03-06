package com.ykw.blog_system.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码缓存工具类
 * 使用ConcurrentHashMap实现内存缓存
 */
@Slf4j
@Component
public class VerificationCodeCache {

    /**
     * 验证码有效期：5分钟
     */
    private static final long CODE_EXPIRE_MINUTES = 5;

    /**
     * 发送间隔：60秒
     */
    private static final long SEND_INTERVAL_SECONDS = 60;

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;

    /**
     * 验证码缓存
     * key: email
     * value: CodeInfo
     */
    private final Map<String, CodeInfo> codeCache = new ConcurrentHashMap<>();

    /**
     * 发送时间记录
     * key: email
     * value: 上次发送时间
     */
    private final Map<String, LocalDateTime> sendTimeCache = new ConcurrentHashMap<>();

    /**
     * 验证码信息
     */
    @Data
    private static class CodeInfo {
        private String code;
        private LocalDateTime createTime;

        public CodeInfo(String code, LocalDateTime createTime) {
            this.code = code;
            this.createTime = createTime;
        }

        /**
         * 检查验证码是否过期
         */
        public boolean isExpired() {
            return ChronoUnit.MINUTES.between(createTime, LocalDateTime.now()) > CODE_EXPIRE_MINUTES;
        }
    }

    /**
     * 生成随机验证码
     *
     * @return 6位数字验证码
     */
    public String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 检查是否可以发送验证码（60秒限制）
     *
     * @param email 邮箱
     * @return 如果可以发送返回true，否则返回false
     */
    public boolean canSend(String email) {
        LocalDateTime lastSendTime = sendTimeCache.get(email);
        if (lastSendTime == null) {
            return true;
        }
        long secondsSinceLastSend = ChronoUnit.SECONDS.between(lastSendTime, LocalDateTime.now());
        return secondsSinceLastSend >= SEND_INTERVAL_SECONDS;
    }

    /**
     * 获取剩余等待时间（秒）
     *
     * @param email 邮箱
     * @return 剩余等待秒数
     */
    public long getRemainingWaitTime(String email) {
        LocalDateTime lastSendTime = sendTimeCache.get(email);
        if (lastSendTime == null) {
            return 0;
        }
        long secondsSinceLastSend = ChronoUnit.SECONDS.between(lastSendTime, LocalDateTime.now());
        long remaining = SEND_INTERVAL_SECONDS - secondsSinceLastSend;
        return Math.max(remaining, 0);
    }

    /**
     * 保存验证码
     *
     * @param email 邮箱
     * @param code  验证码
     */
    public void saveCode(String email, String code) {
        // 清理过期数据
        cleanExpiredData();

        codeCache.put(email, new CodeInfo(code, LocalDateTime.now()));
        sendTimeCache.put(email, LocalDateTime.now());
        log.info("验证码已保存到缓存: {}", email);
    }

    /**
     * 验证验证码
     *
     * @param email 邮箱
     * @param code  验证码
     * @return 验证结果：0-验证成功，1-验证码错误，2-验证码过期，3-验证码不存在
     */
    public int verifyCode(String email, String code) {
        CodeInfo codeInfo = codeCache.get(email);
        if (codeInfo == null) {
            return 3; // 验证码不存在
        }
        if (codeInfo.isExpired()) {
            codeCache.remove(email);
            return 2; // 验证码过期
        }
        if (!codeInfo.getCode().equals(code)) {
            return 1; // 验证码错误
        }
        return 0; // 验证成功
    }

    /**
     * 清除验证码
     *
     * @param email 邮箱
     */
    public void removeCode(String email) {
        codeCache.remove(email);
    }

    /**
     * 清理过期数据
     */
    private void cleanExpiredData() {
        codeCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
