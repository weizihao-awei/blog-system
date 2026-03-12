package com.ykw.blog_system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.dto.ArticleDTO;
import com.ykw.blog_system.dto.ArticleOperationDTO;
import com.ykw.blog_system.dto.ArticleQueryDTO;
import com.ykw.blog_system.entity.*;
import com.ykw.blog_system.enums.ArticleOperationType;
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
import java.time.LocalDateTime;
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

    @Autowired
    private UserMapper userMapper;
    
    @Override
    public ResultVO<PageVO<ArticleVO>> queryArticles(ArticleQueryDTO queryDTO) {
        Page<Article> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1);
        
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId, queryDTO.getCategoryId());
        }
        
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(Article::getTitle, queryDTO.getKeyword())
                    .or().like(Article::getSummary, queryDTO.getKeyword()));
        }
        
        if ("hot".equals(queryDTO.getOrderBy())) {
            wrapper.orderByDesc(Article::getViewCount, Article::getLikeCount);
        } else if ("recommend".equals(queryDTO.getOrderBy())) {
            wrapper.orderByDesc(Article::getIsRecommend, Article::getPublishTime);
        } else {
            wrapper.orderByDesc(Article::getIsTop, Article::getPublishTime);
        }
        
        Page<Article> pageResult = articleMapper.selectPage(page, wrapper);
        List<Article> list = pageResult.getRecords();
        
        // 转换为 ArticleVO 并加载标签
        List<ArticleVO> voList = new ArrayList<>();
        for (Article article : list) {
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
            
            voList.add(articleVO);
        }
        
        PageVO<ArticleVO> pageVO = new PageVO<>(voList, pageResult.getTotal(), 
                                               queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(pageVO);
    }
    /**
     * 获取文章详情
     */
    
    @Override
    public ResultVO<ArticleVO> getArticleDetail(Long articleId, Long currentUserId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return ResultVO.error("文章不存在");
        }
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);

        List<Tag> tags = tagMapper.selectByArticleId(articleId);
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
        UserFoot userFoot = userFootMapper.selectByUserAndDocument(userId, articleId, 1);
        if (userFoot != null) {
            articleVO.setIsCollected(userFoot.getCollectionStat() == 1);
            articleVO.setIsLiked(userFoot.getPraiseStat() == 1);
        }


        // 2. 构建 Lambda 更新条件，使用 setSql 方法实现自增
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                // 核心：使用 setSql 实现 view_count = view_count + 1
                .setSql("view_count = view_count + 1")
                // 精准匹配文章 ID
                .eq(Article::getId, articleId);

        // 3. 执行更新并校验结果
        int affectedRows = articleMapper.update(null, updateWrapper);
        
        if (currentUserId != null) {
            recordUserBehavior(currentUserId, articleId, "view", new BigDecimal("1.0"));
        }
        

        

        
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
        
        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            List<ArticleTag> articleTags = articleDTO.getTagIds().stream()
                    .map(tagId -> {
                        ArticleTag articleTag = new ArticleTag();
                        articleTag.setArticleId(article.getId());
                        articleTag.setTagId(tagId);
                        return articleTag;
                    })
                    .collect(Collectors.toList());
            articleTags.forEach(articleTagMapper::insert);
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
        
        if (!article.getAuthorId().equals(currentUserId) && !SecurityUtil.isAdmin()) {
            return ResultVO.error("无权修改此文章");
        }
        
        BeanUtils.copyProperties(articleDTO, article);
        articleMapper.updateById(article);
        
        articleTagMapper.deleteByArticleId(article.getId());
        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            List<ArticleTag> articleTags = articleDTO.getTagIds().stream()
                    .map(tagId -> {
                        ArticleTag articleTag = new ArticleTag();
                        articleTag.setArticleId(article.getId());
                        articleTag.setTagId(tagId);
                        return articleTag;
                    })
                    .collect(Collectors.toList());
            articleTags.forEach(articleTagMapper::insert);
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
            UserFoot userFoot = new UserFoot();
            userFoot.setUserId(userId);
            userFoot.setDocumentId(articleId);
            userFoot.setDocumentType(1);
            userFoot.setDocumentUserId(article.getAuthorId());
            userFoot.setPraiseStat(1);
            userFoot.setReadStat(1);
            userFoot.setCollectionStat(0);
            userFoot.setCommentStat(0);
            userFootMapper.insert(userFoot);
        } else {
            existingFoot.setPraiseStat(1);
            userFootMapper.updateById(existingFoot);
        }
        
        articleMapper.update(null, 
            new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Article>()
                .setSql("like_count = like_count + 1")
                .eq(Article::getId, articleId));
        
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

        existingFoot.setPraiseStat(2);
        userFootMapper.updateById(existingFoot);

        articleMapper.update(null,
            new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Article>()
                .setSql("like_count = like_count - 1")
                .eq(Article::getId, articleId));

        return ResultVO.success();
    }

    @Override
    @Transactional
    public ResultVO<Void> operateArticle(ArticleOperationDTO operationDTO, Long userId) {
        Long articleId = operationDTO.getArticleId();
        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return ResultVO.error("文章不存在,无法执行点赞或收藏");
        }
        // 检查用户是否曾经访问过文章
        UserFoot userFoot = userFootMapper.selectByUserAndDocument(userId, articleId, 1);
        // 如果用户没有访问过文章，则创建用户访问记录
        if (userFoot == null) {
            userFoot = new UserFoot();
            userFoot.setUserId(userId);
            userFoot.setDocumentId(articleId);
            userFoot.setDocumentType(1);
            userFoot.setDocumentUserId(article.getAuthorId());
            userFoot.setPraiseStat(0);
            userFoot.setReadStat(1);
            userFoot.setCollectionStat(0);
            userFoot.setCommentStat(0);
            userFootMapper.insert(userFoot);
        }
        // 根据操作类型进行相应的处理
        ArticleOperationType operationType = operationDTO.getOperationType();
        switch (operationType) {
            case LIKE:
                if (userFoot.getPraiseStat() == 1) return ResultVO.error("已点赞过");
                userFoot.setPraiseStat(1);
                userFootMapper.updateById(userFoot);
                articleMapper.updateLikeCount(articleId, 1);
                recordUserBehavior(userId, articleId, "like", new BigDecimal("3.0"));
                break;
            case UNLIKE:
                if (userFoot.getPraiseStat() != 1) return ResultVO.error("未点赞过");
                userFoot.setPraiseStat(2);
                userFootMapper.updateById(userFoot);
                articleMapper.updateLikeCount(articleId, -1);
                break;
            case COLLECT:
                if ( userFoot.getCollectionStat() == 1) return ResultVO.error("已收藏过");
                userFoot.setCollectionStat(1);
                userFootMapper.updateById(userFoot);
                articleMapper.updateCollectionCount(articleId, 1);
                recordUserBehavior(userId, articleId, "collect", new BigDecimal("5.0"));
                break;
            case UNCOLLECT:
                if (userFoot.getCollectionStat() != 1) return ResultVO.error("未收藏过");
                userFoot.setCollectionStat(2);
                userFootMapper.updateById(userFoot);
                articleMapper.updateCollectionCount(articleId, -1);
                break;
        }

        return ResultVO.success();
    }
    /**
     * 收藏文章
     */

    @Override
    @Transactional
    public ResultVO<Void> collectArticle(Long articleId, Long userId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return ResultVO.error("文章不存在，无法进行收藏");
        }

        // 使用 LambdaQueryWrapper 查询
        LambdaQueryWrapper<UserFoot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFoot::getUserId, userId)
                .eq(UserFoot::getDocumentId, articleId)
                .eq(UserFoot::getDocumentType, 1);

        UserFoot existingFoot = userFootMapper.selectOne(wrapper);

        if (existingFoot != null && existingFoot.getCollectionStat() == 1) {
            return ResultVO.error("已收藏过");
        }

        if (existingFoot == null) {
            UserFoot userFoot = new UserFoot();
            userFoot.setUserId(userId);
            userFoot.setDocumentId(articleId);
            userFoot.setDocumentType(1);
            userFoot.setDocumentUserId(article.getAuthorId());
            userFoot.setCollectionStat(1);
            userFoot.setReadStat(1);
            userFoot.setPraiseStat(0);
            userFoot.setCommentStat(0);
            userFootMapper.insert(userFoot);
        } else {
            existingFoot.setCollectionStat(1);
            userFootMapper.updateById(existingFoot);
        }

        articleMapper.updateCollectionCount(articleId, 1);

        recordUserBehavior(userId, articleId, "collect", new BigDecimal("5.0"));

        return ResultVO.success();
    }

    /**
     * 取消收藏文章
     * @param articleId 文章 ID
     * @param userId 用户 ID
     * @return ResultVO
     */
    @Override
    @Transactional
    public ResultVO<Void> uncollectArticle(Long articleId, Long userId) {
        UserFoot existingFoot = userFootMapper.selectByUserAndDocument(userId, articleId, 1);
        if (existingFoot == null || existingFoot.getCollectionStat() != 1) {

            return ResultVO.error("未收藏过");
        }

        existingFoot.setCollectionStat(2);
        userFootMapper.updateById(existingFoot);

        articleMapper.updateCollectionCount(articleId, -1);

        return ResultVO.success();
    }

    @Override
    public ResultVO<PageVO<ArticleVO>> getHotArticles(ArticleQueryDTO queryDTO) {
        Page<Article> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
            
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1)
                .orderByDesc(Article::getViewCount, Article::getLikeCount);
            
        // 分类 ID 过滤
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId, queryDTO.getCategoryId());
        }
            
        // 关键字过滤
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(Article::getTitle, queryDTO.getKeyword())
                    .or().like(Article::getSummary, queryDTO.getKeyword()));
        }
            
        Page<Article> pageResult = articleMapper.selectPage(page, wrapper);
        List<Article> list = pageResult.getRecords();
            
        // 转换为 ArticleVO 并加载标签
        List<ArticleVO> voList = new ArrayList<>();
        for (Article article : list) {
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
                
            voList.add(articleVO);
        }
            
        PageVO<ArticleVO> pageVO = new PageVO<>(voList, pageResult.getTotal(), 
                                               queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(pageVO);
    }
    
    @Override
    public ResultVO<PageVO<ArticleVO>> getLatestArticles(ArticleQueryDTO queryDTO) {
        Page<Article> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1)
                .orderByDesc(Article::getIsTop, Article::getPublishTime);
        
        // 分类 ID 过滤
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId, queryDTO.getCategoryId());
        }
        
        // 关键字过滤
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(Article::getTitle, queryDTO.getKeyword())
                    .or().like(Article::getSummary, queryDTO.getKeyword()));
        }
        
        Page<Article> pageResult = articleMapper.selectPage(page, wrapper);
        List<Article> list = pageResult.getRecords();
        
        // 转换为 ArticleVO 并加载标签
        List<ArticleVO> voList = convertToVOList(list);
        
        PageVO<ArticleVO> pageVO = new PageVO<>(voList, pageResult.getTotal(), 
                                               queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(pageVO);
    }
    
    @Override
    public ResultVO<PageVO<ArticleVO>> getRecommendArticles(Long userId, ArticleQueryDTO queryDTO) {
        List<Article> recommendArticles = new ArrayList<>();
        
        if (userId != null) {
            List<UserBehavior> userBehaviors = userBehaviorMapper.selectByUserId(userId);
            
            if (!userBehaviors.isEmpty()) {
                recommendArticles = getCollaborativeFilteringRecommendations(userId, null);
            }
        }
        
        if (recommendArticles.size() < queryDTO.getPageSize()) {
            List<Article> hotArticles = articleMapper.selectHotArticles(null);
            for (Article article : hotArticles) {
                if (recommendArticles.stream().noneMatch(a -> a.getId().equals(article.getId()))) {
                    recommendArticles.add(article);
                }
            }
        }
        
        int total = recommendArticles.size();
        int startIndex = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        int endIndex = Math.min(startIndex + queryDTO.getPageSize(), total);
        
        List<Article> pagedArticles = new ArrayList<>();
        if (startIndex < total) {
            pagedArticles = recommendArticles.subList(startIndex, endIndex);
        }
        
        // 转换为 ArticleVO 并加载标签
        List<ArticleVO> voList = convertToVOList(pagedArticles);
        
        PageVO<ArticleVO> pageVO = new PageVO<>(voList, (long) total, 
                                               queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(pageVO);
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
        // 获取所有文章（排除已删除的），用于计算推荐得分
        List<Article> allArticles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1));
        
        // 计算每篇文章的推荐得分并排序
        return allArticles.stream()
                .filter(article -> !viewedArticleIds.contains(article.getId()))
                .sorted((a1, a2) -> {
                    double score1 = calculateRecommendationScore(a1, 
                        tagMapper.selectByArticleId(a1.getId()), interestedTagIds, behaviors);
                    double score2 = calculateRecommendationScore(a2, 
                        tagMapper.selectByArticleId(a2.getId()), interestedTagIds, behaviors);
                    return Double.compare(score2, score1);
                })
                .limit(limit != null ? limit : allArticles.size())
                .collect(Collectors.toList());
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
    public ResultVO<PageVO<ArticleVO>> getMyArticles(Long userId, Integer pageNum, Integer pageSize, Integer status) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getAuthorId, userId);
        if (status != null) {
            wrapper.eq(Article::getStatus, status);
        }
        wrapper.orderByDesc(Article::getCreateTime);
        
        Page<Article> pageResult = articleMapper.selectPage(page, wrapper);
        List<Article> list = pageResult.getRecords();
        
        // 转换为 ArticleVO 并加载标签
        List<ArticleVO> voList = convertToVOList(list);
        
        PageVO<ArticleVO> pageVO = new PageVO<>(voList, pageResult.getTotal(), pageNum, pageSize);
        return ResultVO.success(pageVO);
    }
    
    @Override
    public ResultVO<PageVO<ArticleVO>> getMyCollects(Long userId, Integer pageNum, Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.inSql(Article::getId, 
            "SELECT document_id FROM user_foot WHERE user_id = " + userId + 
            " AND document_type = 1 AND collection_stat = 1");
        wrapper.eq(Article::getStatus, 1);
        wrapper.orderByDesc(Article::getUpdateTime);
        
        Page<Article> pageResult = articleMapper.selectPage(page, wrapper);
        List<Article> list = pageResult.getRecords();
        
        // 转换为 ArticleVO 并加载标签
        List<ArticleVO> voList = convertToVOList(list);
        
        PageVO<ArticleVO> pageVO = new PageVO<>(voList, pageResult.getTotal(), pageNum, pageSize);
        return ResultVO.success(pageVO);
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

            voList.add(articleVO);
        }
        return voList;
    }
    /**
     * 记录用户行为
     */
    private void recordUserBehavior(Long userId, Long articleId, String behaviorType, BigDecimal weight) {
        UserBehavior existingBehavior = userBehaviorMapper.selectByUserAndArticleAndType(
                userId, articleId, behaviorType);
        
        if (existingBehavior != null) {
            existingBehavior.setBehaviorWeight(existingBehavior.getBehaviorWeight().add(weight));
            userBehaviorMapper.updateById(existingBehavior);
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
