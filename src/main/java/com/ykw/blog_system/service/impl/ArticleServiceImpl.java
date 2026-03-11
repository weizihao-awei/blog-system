package com.ykw.blog_system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ykw.blog_system.dto.ArticleDTO;
import com.ykw.blog_system.dto.ArticleQueryDTO;
import com.ykw.blog_system.entity.*;
import com.ykw.blog_system.mapper.*;
import com.ykw.blog_system.service.ArticleService;
import com.ykw.blog_system.utils.SecurityUtil;
import com.ykw.blog_system.vo.ArticleVO;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import com.ykw.blog_system.vo.TagVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    
    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private TagMapper tagMapper;
    
    @Autowired
    private ArticleTagMapper articleTagMapper;
    
    @Autowired
    private UserFootMapper userFootMapper;
    
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Override
    public ResultVO<PageVO<Article>> queryArticles(ArticleQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        List<Article> list;
        
        // 根据标签 ID 查询
        if (queryDTO.getTagId() != null) {
            list = articleMapper.selectByTagId(queryDTO.getTagId(), 1);
        } 
        // 根据分类和关键字查询
        else {
            list = articleMapper.selectList(1, queryDTO.getCategoryId(), queryDTO.getKeyword());
        }
        
        PageInfo<Article> pageInfo = new PageInfo<>(list);
        
        // 加载标签
        for (Article article : list) {
            List<Tag> tags = tagMapper.selectByArticleId(article.getId());
            article.setTags(tags);
        }
        
        // 根据排序方式排序
        if ("hot".equals(queryDTO.getOrderBy())) {
            // 热门排序：按浏览量和点赞数
            list.sort((a1, a2) -> {
                int viewCompare = a2.getViewCount().compareTo(a1.getViewCount());
                if (viewCompare != 0) return viewCompare;
                return a2.getLikeCount().compareTo(a1.getLikeCount());
            });
        } else if ("recommend".equals(queryDTO.getOrderBy())) {
            // 推荐排序：按推荐标志和发布时间
            list.sort((a1, a2) -> {
                int recommendCompare = a2.getIsRecommend().compareTo(a1.getIsRecommend());
                if (recommendCompare != 0) return recommendCompare;
                return a2.getPublishTime().compareTo(a1.getPublishTime());
            });
        }
        // 默认最新排序（按发布时间）
        
        PageVO<Article> pageVO = new PageVO<>(list, pageInfo.getTotal(), 
                                               queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(pageVO);
    }
    
    @Override
    public ResultVO<ArticleVO> getArticleDetail(Long articleId, Long currentUserId) {
        Article article = articleMapper.selectByIdWithTags(articleId);
        if (article == null) {
            return ResultVO.error("文章不存在");
        }
        
        // 增加浏览次数
        articleMapper.increaseViewCount(articleId);
        
        // 记录用户浏览行为
        if (currentUserId != null) {
            recordUserBehavior(currentUserId, articleId, "view", new BigDecimal("1.0"));
        }
        
        // 加载标签
        List<Tag> tags = tagMapper.selectByArticleId(articleId);
        
        // 检查是否已点赞、已收藏
        Boolean isLiked = false;
        Boolean isCollected = false;
        if (currentUserId != null) {
            UserFoot foot = userFootMapper.selectByUserAndDocument(currentUserId, articleId, 1);
            if (foot != null) {
                isLiked = foot.getPraiseStat() == 1;
                isCollected = foot.getCollectionStat() == 1;
            }
        }
        
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        
        // 转换标签
        List<TagVO> tagVOList = tags.stream().map(tag -> {
            TagVO tagVO = new TagVO();
            BeanUtils.copyProperties(tag, tagVO);
            return tagVO;
        }).collect(Collectors.toList());
        articleVO.setTags(tagVOList);
        
        articleVO.setIsLiked(isLiked);
        articleVO.setIsCollected(isCollected);
        
        return ResultVO.success(articleVO);
    }
    
    @Override
    @Transactional
    public ResultVO<Long> createArticle(ArticleDTO articleDTO, Long authorId) {
        Article article = new Article();
        BeanUtils.copyProperties(articleDTO, article);
        article.setAuthorId(authorId);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        
        articleMapper.insert(article);
        
        // 保存标签关联
        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            articleTagMapper.insertBatch(article.getId(), articleDTO.getTagIds());
        }
        
        return ResultVO.success("创建成功", article.getId());
    }
    
    @Override
    @Transactional
    public ResultVO<Void> updateArticle(ArticleDTO articleDTO, Long currentUserId) {
        Article article = articleMapper.selectById(articleDTO.getId());
        if (article == null) {
            return ResultVO.error("文章不存在");
        }
        
        // 检查权限
        if (!article.getAuthorId().equals(currentUserId) && !SecurityUtil.isAdmin()) {
            return ResultVO.error("无权修改此文章");
        }
        
        BeanUtils.copyProperties(articleDTO, article);
        articleMapper.update(article);
        
        // 更新标签关联
        articleTagMapper.deleteByArticleId(article.getId());
        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            articleTagMapper.insertBatch(article.getId(), articleDTO.getTagIds());
        }
        
        return ResultVO.success();
    }
    
    @Override
    @Transactional
    public ResultVO<Void> deleteArticle(Long articleId, Long currentUserId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return ResultVO.error("文章不存在");
        }
        
        // 检查权限
        if (!article.getAuthorId().equals(currentUserId) && !SecurityUtil.isAdmin()) {
            return ResultVO.error("无权删除此文章");
        }
        
        articleMapper.deleteById(articleId);
        articleTagMapper.deleteByArticleId(articleId);
        commentMapper.deleteByArticleId(articleId);
        
        return ResultVO.success();
    }
    
    @Override
    @Transactional
    public ResultVO<Void> likeArticle(Long articleId, Long userId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return ResultVO.error("文章不存在");
        }
        
        UserFoot existingFoot = userFootMapper.selectByUserAndDocument(userId, articleId, 1);
        if (existingFoot != null && existingFoot.getPraiseStat() == 1) {
            return ResultVO.error("已点赞过");
        }
        
        if (existingFoot == null) {
            // 创建新的足迹记录
            UserFoot userFoot = new UserFoot();
            userFoot.setUserId(userId);
            userFoot.setDocumentId(articleId);
            userFoot.setDocumentType(1); // 1-文章
            userFoot.setDocumentUserId(article.getAuthorId());
            userFoot.setPraiseStat(1); // 1-已点赞
            userFoot.setReadStat(1); // 标记为已读
            userFoot.setCollectionStat(0);
            userFoot.setCommentStat(0);
            userFootMapper.insert(userFoot);
        } else {
            // 更新点赞状态
            existingFoot.setPraiseStat(1);
            userFootMapper.update(existingFoot);
        }
        
        articleMapper.increaseLikeCount(articleId, 1);
        
        // 记录用户点赞行为
        recordUserBehavior(userId, articleId, "like", new BigDecimal("3.0"));
        
        return ResultVO.success();
    }
    
    @Override
    @Transactional
    public ResultVO<Void> unlikeArticle(Long articleId, Long userId) {
        UserFoot existingFoot = userFootMapper.selectByUserAndDocument(userId, articleId, 1);
        if (existingFoot == null || existingFoot.getPraiseStat() != 1) {
            return ResultVO.error("未点赞过");
        }
        
        // 更新点赞状态为取消
        existingFoot.setPraiseStat(2); // 2-取消点赞
        userFootMapper.update(existingFoot);
        
        articleMapper.increaseLikeCount(articleId, -1);
        
        return ResultVO.success();
    }
    
    @Override
    @Transactional
    public ResultVO<Void> collectArticle(Long articleId, Long userId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return ResultVO.error("文章不存在");
        }
        
        UserFoot existingFoot = userFootMapper.selectByUserAndDocument(userId, articleId, 1);
        if (existingFoot != null && existingFoot.getCollectionStat() == 1) {
            return ResultVO.error("已收藏过");
        }
        
        if (existingFoot == null) {
            // 创建新的足迹记录
            UserFoot userFoot = new UserFoot();
            userFoot.setUserId(userId);
            userFoot.setDocumentId(articleId);
            userFoot.setDocumentType(1); // 1-文章
            userFoot.setDocumentUserId(article.getAuthorId());
            userFoot.setCollectionStat(1); // 1-已收藏
            userFoot.setReadStat(1); // 标记为已读
            userFoot.setPraiseStat(0);
            userFoot.setCommentStat(0);
            userFootMapper.insert(userFoot);
        } else {
            // 更新收藏状态
            existingFoot.setCollectionStat(1);
            userFootMapper.update(existingFoot);
        }
        
        // 记录用户收藏行为
        recordUserBehavior(userId, articleId, "collect", new BigDecimal("5.0"));
        
        return ResultVO.success();
    }
    
    @Override
    @Transactional
    public ResultVO<Void> uncollectArticle(Long articleId, Long userId) {
        UserFoot existingFoot = userFootMapper.selectByUserAndDocument(userId, articleId, 1);
        if (existingFoot == null || existingFoot.getCollectionStat() != 1) {
            return ResultVO.error("未收藏过");
        }
        
        // 更新收藏状态为取消
        existingFoot.setCollectionStat(2); // 2-取消收藏
        userFootMapper.update(existingFoot);
        
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<List<Article>> getHotArticles(Integer limit) {
        List<Article> list = articleMapper.selectHotArticles(limit);
        for (Article article : list) {
            List<Tag> tags = tagMapper.selectByArticleId(article.getId());
            article.setTags(tags);
        }
        return ResultVO.success(list);
    }
    
    @Override
    public ResultVO<List<Article>> getLatestArticles(Integer limit) {
        List<Article> list = articleMapper.selectLatestArticles(limit);
        for (Article article : list) {
            List<Tag> tags = tagMapper.selectByArticleId(article.getId());
            article.setTags(tags);
        }
        return ResultVO.success(list);
    }
    
    @Override
    public ResultVO<List<Article>> getRecommendArticles(Long userId, Integer limit) {
        List<Article> recommendArticles = new ArrayList<>();
        
        if (userId != null) {
            // 获取用户行为数据
            List<UserBehavior> userBehaviors = userBehaviorMapper.selectByUserId(userId);
            
            if (!userBehaviors.isEmpty()) {
                // 基于协同过滤的推荐算法
                recommendArticles = getCollaborativeFilteringRecommendations(userId, limit);
            }
        }
        
        // 如果推荐文章不足，补充热门文章
        if (recommendArticles.size() < limit) {
            List<Article> hotArticles = articleMapper.selectHotArticles(limit);
            for (Article article : hotArticles) {
                if (recommendArticles.stream().noneMatch(a -> a.getId().equals(article.getId()))) {
                    recommendArticles.add(article);
                    if (recommendArticles.size() >= limit) {
                        break;
                    }
                }
            }
        }
        
        // 加载标签
        for (Article article : recommendArticles) {
            List<Tag> tags = tagMapper.selectByArticleId(article.getId());
            article.setTags(tags);
        }
        
        return ResultVO.success(recommendArticles);
    }
    
    /**
     * 基于协同过滤的推荐算法
     */
    private List<Article> getCollaborativeFilteringRecommendations(Long userId, Integer limit) {
        // 获取用户浏览过的文章标签
        List<UserBehavior> behaviors = userBehaviorMapper.selectByUserId(userId);
        List<Long> viewedArticleIds = behaviors.stream()
                .map(UserBehavior::getArticleId)
                .distinct()
                .collect(Collectors.toList());
        
        // 获取这些文章的标签
        List<Long> interestedTagIds = new ArrayList<>();
        for (Long articleId : viewedArticleIds) {
            List<Tag> tags = tagMapper.selectByArticleId(articleId);
            interestedTagIds.addAll(tags.stream().map(Tag::getId).collect(Collectors.toList()));
        }
        
        // 根据标签权重排序获取推荐文章
        List<Article> allArticles = articleMapper.selectList(1, null, null);
        List<Article> scoredArticles = new ArrayList<>();
        
        for (Article article : allArticles) {
            if (viewedArticleIds.contains(article.getId())) {
                continue; // 跳过已浏览的文章
            }
            
            List<Tag> articleTags = tagMapper.selectByArticleId(article.getId());
            double score = calculateRecommendationScore(article, articleTags, interestedTagIds, behaviors);
            article.setRecommendScore(score);
            scoredArticles.add(article);
        }
        
        // 按推荐得分排序
        scoredArticles.sort((a1, a2) -> Double.compare(a2.getRecommendScore(), a1.getRecommendScore()));
        
        return scoredArticles.stream().limit(limit).collect(Collectors.toList());
    }
    
    /**
     * 计算推荐得分
     */
    private double calculateRecommendationScore(Article article, List<Tag> articleTags, 
                                                 List<Long> interestedTagIds, 
                                                 List<UserBehavior> behaviors) {
        double score = 0;
        
        // 标签匹配得分
        for (Tag tag : articleTags) {
            if (interestedTagIds.contains(tag.getId())) {
                score += 2;
            }
        }
        
        // 热度得分
        score += article.getViewCount() * 0.001;
        score += article.getLikeCount() * 0.01;
        
        // 时间衰减因子（新文章得分更高）
        long daysSincePublish = java.time.Duration.between(
                article.getPublishTime() != null ? article.getPublishTime() : article.getCreateTime(),
                java.time.LocalDateTime.now()).toDays();
        double timeFactor = Math.max(0.1, 1 - daysSincePublish * 0.01);
        score *= timeFactor;
        
        return score;
    }
    
    @Override
    public ResultVO<PageVO<Article>> getMyArticles(Long userId, Integer pageNum, Integer pageSize, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> list = articleMapper.selectByUserId(userId, status);
        PageInfo<Article> pageInfo = new PageInfo<>(list);
        
        for (Article article : list) {
            List<Tag> tags = tagMapper.selectByArticleId(article.getId());
            article.setTags(tags);
        }
        
        PageVO<Article> pageVO = new PageVO<>(list, pageInfo.getTotal(), pageNum, pageSize);
        return ResultVO.success(pageVO);
    }
    
    @Override
    public ResultVO<PageVO<Article>> getMyCollects(Long userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserFoot> foots = userFootMapper.selectByUserIdAndType(userId, 1); // 1-文章
        
        List<Article> articles = new ArrayList<>();
        for (UserFoot foot : foots) {
            Article article = articleMapper.selectByIdWithTags(foot.getDocumentId());
            if (article != null && article.getStatus() == 1) {
                List<Tag> tags = tagMapper.selectByArticleId(article.getId());
                article.setTags(tags);
                articles.add(article);
            }
        }
        
        PageVO<Article> pageVO = new PageVO<>(articles, (long) articles.size(), pageNum, pageSize);
        return ResultVO.success(pageVO);
    }
    
    /**
     * 记录用户行为
     */
    private void recordUserBehavior(Long userId, Long articleId, String behaviorType, BigDecimal weight) {
        UserBehavior existingBehavior = userBehaviorMapper.selectByUserAndArticleAndType(
                userId, articleId, behaviorType);
        
        if (existingBehavior != null) {
            existingBehavior.setBehaviorWeight(existingBehavior.getBehaviorWeight().add(weight));
            userBehaviorMapper.update(existingBehavior);
        } else {
            UserBehavior behavior = new UserBehavior();
            behavior.setUserId(userId);
            behavior.setArticleId(articleId);
            behavior.setBehaviorType(behaviorType);
            behavior.setBehaviorWeight(weight);
            userBehaviorMapper.insert(behavior);
        }
    }
}
