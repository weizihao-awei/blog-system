package com.ykw.blog_system.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ArticleOperationType {

    LIKE("like", "点赞"),

    UNLIKE("unlike", "取消点赞"),

    COLLECT("collect", "收藏"),

    UNCOLLECT("uncollect", "取消收藏");

    private final String code;
    private final String description;

    ArticleOperationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ArticleOperationType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (ArticleOperationType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
