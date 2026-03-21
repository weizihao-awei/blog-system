package com.ykw.blog_system.vo;

import lombok.Data;

@Data
public class AuthorInfoVO {
    
    private Long id;
    
    private String nickname;
    
    private String avatar;
    
    private Integer gender;
    
    private String intro;
    
    private String signature;
    
    private Long followersCount;
    
    private Long followingCount;
    
    private Long articlesCount;
    
    private Long totalViews;
    
    private Long totalLikes;
    
    private Long totalCollections;
}
