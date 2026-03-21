package com.ykw.blog_system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.dto.UserFootQueryDTO;
import com.ykw.blog_system.entity.*;
import com.ykw.blog_system.enums.FollowStateEnum;
import com.ykw.blog_system.enums.OrderEnum;
import com.ykw.blog_system.enums.ResultCodeEnum;
import com.ykw.blog_system.mapper.*;
import com.ykw.blog_system.service.UserService;
import com.ykw.blog_system.utils.SecurityUtil;
import com.ykw.blog_system.vo.ArticleVO;
import com.ykw.blog_system.vo.AuthorInfoVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import com.ykw.blog_system.vo.TagVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserFootMapper userFootMapper;
    
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserRelationMapper userRelationMapper;



    
    @Override
    public ResultVO<User> getCurrentUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ResultVO.error("用户不存在");
        }
        user.setPassword(null);
        return ResultVO.success(user);
    }
    
    @Override
    public ResultVO<Void> updateUserInfo(User user) {
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            return ResultVO.error("用户不存在");
        }
        
        // 检查邮箱是否已被其他用户使用
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            User emailUser = userMapper.selectByEmail(user.getEmail());
            if (emailUser != null && !emailUser.getId().equals(user.getId())) {
                return ResultVO.error("邮箱已被其他用户使用");
            }
        }
        
        userMapper.updateById(user);
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<PageVO<User>> getUserList(Integer pageNum, Integer pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> pageResult = userMapper.selectPage(page, wrapper);
        List<User> list = pageResult.getRecords();
        
        list.forEach(user -> user.setPassword(null));
        
        PageVO<User> pageVO = new PageVO<>(list, pageResult.getTotal(), pageNum, pageSize);
        return ResultVO.success(pageVO);
    }
    
    @Override
    public ResultVO<Void> updateUserStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        userMapper.updateById(user);
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<Void> deleteUser(Long userId) {
        userMapper.deleteById(userId);
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<PageVO<ArticleVO>> getCollectionArticlesPage(Long userId, UserFootQueryDTO queryDTO) {
        // 查询用户收藏的足迹记录
        Page<UserFoot> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        String orderValue = queryDTO.getOrder() != null ? queryDTO.getOrder().getValue() : OrderEnum.DESC.getValue();
        Page<UserFoot> footPageResult = userFootMapper.selectCollectionPage(page, userId, 
                queryDTO.getDocumentType(), orderValue);
        
        // 从足迹记录中提取文章 ID 列表
        List<Long> articleIds = footPageResult.getRecords().stream()
                .map(UserFoot::getDocumentId)
                .collect(Collectors.toList());
        
        // 批量查询文章信息
        List<ArticleVO> articleVOList = new ArrayList<>();
        if (!articleIds.isEmpty()) {
            List<Article> articles = articleMapper.selectBatchIds(articleIds);
            articleVOList = convertToVOList(articles);
        }
        
        PageVO<ArticleVO> pageVO = new PageVO<>(articleVOList, footPageResult.getTotal(), 
                queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(pageVO);
    }
    
    @Override
    public ResultVO<PageVO<ArticleVO>> getPraiseArticlesPage(Long userId, UserFootQueryDTO queryDTO) {
        // 查询用户点赞的足迹记录
        Page<UserFoot> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        String orderValue = queryDTO.getOrder() != null ? queryDTO.getOrder().getValue() : OrderEnum.DESC.getValue();
        Page<UserFoot> footPageResult = userFootMapper.selectPraisePage(page, userId, 
                queryDTO.getDocumentType(), orderValue);
        
        // 从足迹记录中提取文章 ID 列表
        List<Long> articleIds = footPageResult.getRecords().stream()
                .map(UserFoot::getDocumentId)
                .collect(Collectors.toList());
        
        // 批量查询文章信息
        List<ArticleVO> articleVOList = new ArrayList<>();
        if (!articleIds.isEmpty()) {
            List<Article> articles = articleMapper.selectBatchIds(articleIds);
            articleVOList = convertToVOList(articles);
        }
        
        PageVO<ArticleVO> pageVO = new PageVO<>(articleVOList, footPageResult.getTotal(), 
                queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(pageVO);
    }
    
    @Override
    public ResultVO<PageVO<ArticleVO>> getReadArticlesPage(Long userId, UserFootQueryDTO queryDTO) {
        // 查询用户浏览的足迹记录
        Page<UserFoot> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        String orderValue = queryDTO.getOrder() != null ? queryDTO.getOrder().getValue() : OrderEnum.DESC.getValue();
        Page<UserFoot> footPageResult = userFootMapper.selectReadPage(page, userId, 
                queryDTO.getDocumentType(), orderValue);
        
        // 从足迹记录中提取文章 ID 列表
        List<Long> articleIds = footPageResult.getRecords().stream()
                .map(UserFoot::getDocumentId)
                .collect(Collectors.toList());
        
        // 批量查询文章信息
        List<ArticleVO> articleVOList = new ArrayList<>();
        if (!articleIds.isEmpty()) {
            List<Article> articles = articleMapper.selectBatchIds(articleIds);
            articleVOList = convertToVOList(articles);
        }
        
        PageVO<ArticleVO> pageVO = new PageVO<>(articleVOList, footPageResult.getTotal(), 
                queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(pageVO);
    }
    
    @Override
    public ResultVO<PageVO<ArticleVO>> getUserPublishedArticlesPage(UserFootQueryDTO queryDTO) {
        // 分页查询文章，直接按作者 ID 查询
        Page<Article> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getAuthorId, queryDTO.getUserId())
                .eq(Article::getStatus, 1); // 只查询已发布的文章
        
        OrderEnum order = queryDTO.getOrder() != null ? queryDTO.getOrder() : OrderEnum.DESC;
        if (order == OrderEnum.ASC) {
            wrapper.orderByAsc(Article::getPublishTime);
        } else {
            wrapper.orderByDesc(Article::getPublishTime);
        }
        
        Page<Article> pageResult = articleMapper.selectPage(page, wrapper);
        List<ArticleVO> articleVOList = convertToVOList(pageResult.getRecords());
        
        PageVO<ArticleVO> pageVO = new PageVO<>(articleVOList, pageResult.getTotal(), 
                queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(pageVO);
    }

    @Override
    public ResultVO<AuthorInfoVO> getAuthorInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ResultVO.error(ResultCodeEnum.PARAM_ERROR);
        }

        AuthorInfoVO authorInfoVO = new AuthorInfoVO();
        authorInfoVO.setId(user.getId());
        authorInfoVO.setNickname(user.getNickname());
        authorInfoVO.setAvatar(user.getAvatar());
        authorInfoVO.setGender(user.getGender());
        authorInfoVO.setIntro(user.getIntro());
        authorInfoVO.setSignature(user.getSignature());

        // 使用 Mapper 直接查询粉丝数
        LambdaQueryWrapper<UserRelation> followerWrapper = new LambdaQueryWrapper<>();
        followerWrapper.eq(UserRelation::getUserId, userId)
                .eq(UserRelation::getFollowState, FollowStateEnum.FOLLOWED.getCode());
        Long followersCount = userRelationMapper.selectCount(followerWrapper);
        authorInfoVO.setFollowersCount(followersCount != null ? followersCount : 0L);

        // 使用 Mapper 直接查询关注数
        LambdaQueryWrapper<UserRelation> followingWrapper = new LambdaQueryWrapper<>();
        followingWrapper.eq(UserRelation::getFollowUserId, userId)
                .eq(UserRelation::getFollowState, FollowStateEnum.FOLLOWED.getCode());
        Long followingCount = userRelationMapper.selectCount(followingWrapper);
        authorInfoVO.setFollowingCount(followingCount != null ? followingCount : 0L);

        // 查询用户发布的文章列表
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getAuthorId, userId)
                .eq(Article::getStatus, 1);
        List<Article> articles = articleMapper.selectList(articleWrapper);
        
        // 使用集合大小作为文章数量，避免额外查询
        Long articlesCount = (long) articles.size();
        authorInfoVO.setArticlesCount(articlesCount);

        // 计算总浏览量、总点赞数、总收藏数
        Long totalViews = articles.stream()
                .mapToLong(article -> article.getViewCount() != null ? article.getViewCount() : 0L)
                .sum();
        authorInfoVO.setTotalViews(totalViews);

        Long totalLikes = articles.stream()
                .mapToLong(article -> article.getLikeCount() != null ? article.getLikeCount() : 0L)
                .sum();
        authorInfoVO.setTotalLikes(totalLikes);

        Long totalCollections = articles.stream()
                .mapToLong(article -> article.getCollectionCount() != null ? article.getCollectionCount() : 0L)
                .sum();
        authorInfoVO.setTotalCollections(totalCollections);

        return ResultVO.success(ResultCodeEnum.SUCCESS, authorInfoVO);
    }

    /**
     * 将 Article 列表转换为 ArticleVO 列表，并加载标签和用户信息
     */
    //todo： 后面要改成连表查询或者缓存不然差一条记录就要调用单次sql连接
    private List<ArticleVO> convertToVOList(List<Article> articles) {
        List<ArticleVO> voList = new ArrayList<>();
        for (Article article : articles) {
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(article, articleVO);

            // 加载标签
            List<Tag> tags = tagMapper.selectByArticleId(article.getId());
            List<TagVO> tagVOList = tags.stream().map(tag -> {
                TagVO tagVO = new TagVO();
                BeanUtils.copyProperties(tag, tagVO);
                return tagVO;
            }).collect(Collectors.toList());
            articleVO.setTags(tagVOList);

            // 加载作者信息
            User author = userMapper.selectById(article.getAuthorId());
            articleVO.setAuthorName(author.getNickname());
            articleVO.setAuthorAvatar(author.getAvatar());

            //加载用户信息,用户id从token中获取
            Long userId = SecurityUtil.getCurrentUserId();
            //
            UserFoot userFoot = userFootMapper.selectByUserAndDocument(userId, article.getAuthorId(), 1);
            if (userFoot != null) {
                articleVO.setIsCollected(userFoot.getCollectionStat() == 1);
                articleVO.setIsLiked(userFoot.getPraiseStat() == 1);
            }

            // 加载分类信息
            Category category = categoryMapper.selectById(article.getCategoryId());
            articleVO.setCategoryName(category.getName());

            voList.add(articleVO);
        }
        return voList;
    }

}