package com.ykw.blog_system.vo;

import lombok.Data;

@Data
/**
 * 作者信息视图对象 (VO)
 * 用于封装和传输作者的相关展示数据
 */
public class AuthorInfoVO {

    /**
     * 作者唯一标识 ID
     */
    private Long id;

    /**
     * 作者昵称
     */
    private String nickname;

    /**
     * 作者头像 URL
     */
    private String avatar;

    /**
     * 性别 (0:未知，1:男，2:女)
     */
    private Integer gender;

    /**
     * 个人简介
     */
    private String intro;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 粉丝数量
     */
    private Long followersCount;

    /**
     * 关注数量
     */
    private Long followingCount;

    /**
     * 文章数量
     */
    private Long articlesCount;

    /**
     * 总浏览量
     */
    private Long totalViews;

    /**
     * 总获赞数
     */
    private Long totalLikes;

    /**
     * 总收藏数
     */
    private Long totalCollections;

    /**
     * 当前用户是否已关注该作者
     */
    private Boolean isFollowed;
}
