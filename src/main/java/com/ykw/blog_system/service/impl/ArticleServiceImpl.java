package com.ykw.blog_system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.dto.ArticleDTO;
import com.ykw.blog_system.dto.ArticleOperationDTO;
import com.ykw.blog_system.dto.ArticleQueryDTO;
import com.ykw.blog_system.entity.*;
import com.ykw.blog_system.enums.ArticleOperationType;
import com.ykw.blog_system.enums.ArticleOrderEnum;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryMapper categoryMapper;
    
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

    @Autowired
    private ArticleDetailMapper articleDetailMapper;

    @Override
    public ResultVO<PageVO<ArticleVO>> queryArticles(ArticleQueryDTO queryDTO) { 
        Page<Article> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
            
        // 使用 XML 多表查询，支持标签过滤
        Page<Article> pageResult = articleMapper.selectArticlesByCondition(page, queryDTO);
        List<Article> list = pageResult.getRecords();
            
        List<ArticleVO> voList = convertToVOList(list);
    
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

        if (article.getArticleContentId() != null) {
            ArticleDetail articleDetail = articleDetailMapper.selectById(article.getArticleContentId());
            if (articleDetail != null) {
                articleVO.setContent(articleDetail.getContent());
                articleVO.setHtmlContent(articleDetail.getHtmlContent());
            }
        }

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
        article.setCollectionCount(0);

        articleMapper.insert(article);

        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setArticleId(article.getId());
        articleDetail.setVersion(0);
        articleDetail.setContent(articleDTO.getContent());
        articleDetail.setHtmlContent(articleDTO.getHtmlContent());
        articleDetail.setDeleted(0);
        articleDetailMapper.insert(articleDetail);

        article.setArticleContentId(articleDetail.getId());
        articleMapper.updateById(article);

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

        article.setTitle(articleDTO.getTitle());
        article.setSummary(articleDTO.getSummary());
        article.setCoverImage(articleDTO.getCoverImage());
        article.setCategoryId(articleDTO.getCategoryId());
        article.setStatus(articleDTO.getStatus());
        article.setIsTop(articleDTO.getIsTop());
        article.setIsRecommend(articleDTO.getIsRecommend());

        Integer maxVersion = articleDetailMapper.getMaxVersion(article.getId());
        if (maxVersion == null) {
            maxVersion = -1;
        }

        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setArticleId(article.getId());
        articleDetail.setVersion(maxVersion + 1);
        articleDetail.setContent(articleDTO.getContent());
        articleDetail.setHtmlContent(articleDTO.getHtmlContent());
        articleDetail.setDeleted(0);
        articleDetailMapper.insert(articleDetail);

        article.setArticleContentId(articleDetail.getId());
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
        // 使用 BaseMapper 的标准 delete 方法，通过 LambdaQueryWrapper 构建条件
        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getArticleId, articleId));
        
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


    @Override
    public ResultVO<PageVO<ArticleVO>> getHotArticles(ArticleQueryDTO queryDTO) {
        Page<Article> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
            
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1)
                .orderByDesc(Article::getViewCount, Article::getLikeCount);



        Page<Article> pageResult = articleMapper.selectPage(page, wrapper);
        List<Article> list = pageResult.getRecords();
        List<ArticleVO> voList = convertToVOList(list);


        PageVO<ArticleVO> pageVO = new PageVO<>(voList, pageResult.getTotal(), 
                                               queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(pageVO);
    }

    /**
     * 获取最新文章
     */
    @Override
    public ResultVO<PageVO<ArticleVO>> getLatestArticles(ArticleQueryDTO queryDTO) {
        Page<Article> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1)
                .orderByDesc(Article::getPublishTime,Article::getIsTop);
        
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
    /**
     * 获取推荐文章（核心优化方法）
     */
    @Override
    public ResultVO<PageVO<ArticleVO>> getRecommendArticles(Long userId, ArticleQueryDTO queryDTO) {

        Page<Article> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1)
                .orderByDesc(Article::getCollectionCount, Article::getLikeCount);



        Page<Article> pageResult = articleMapper.selectPage(page, wrapper);
        List<Article> list = pageResult.getRecords();
        List<ArticleVO> voList = convertToVOList(list);


        PageVO<ArticleVO> pageVO = new PageVO<>(voList, pageResult.getTotal(),
                queryDTO.getPageNum(), queryDTO.getPageSize());
        return ResultVO.success(pageVO);
//        // 1. 初始化推荐文章列表
//        List<Article> recommendArticles = new ArrayList<>();
//
//        // 2. 优先基于用户行为的协同过滤推荐
//        if (Objects.nonNull(userId)) {
//            recommendArticles = getPersonalizedRecommendations(userId, queryDTO.getPageSize());
//        }
//
//        // 3. 补充热门文章（当个性化推荐数量不足时）
//        fillWithHotArticles(recommendArticles, queryDTO.getPageSize());
//
//        // 4. 分页处理
//        PageVO<ArticleVO> pageVO = paginateRecommendArticles(recommendArticles, queryDTO);

//        return ResultVO.success(pageVO);
    }

    /**
     * 基于用户行为的个性化推荐（协同过滤核心逻辑）
     */
    private List<Article> getPersonalizedRecommendations(Long userId, int targetSize) {
        // 1. 获取用户行为记录（过滤无效数据）
        List<UserBehavior> userBehaviors = userBehaviorMapper.selectByUserId(userId);
        if (userBehaviors.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 提取用户感兴趣的标签（批量查询，减少SQL调用）
        Set<Long> interestedTagIds = extractInterestedTagIds(userBehaviors);
        if (interestedTagIds.isEmpty()) {
            return Collections.emptyList();
        }



        List<Article> candidateArticles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1));

        // 4. 计算得分并排序（取目标数量）
        return candidateArticles.stream()
                .map(article -> new ArticleScoreWrapper(article, calculateArticleScore(article, interestedTagIds)))
                .sorted((w1, w2) -> Double.compare(w2.getScore(), w1.getScore()))
                .limit(targetSize)
                .map(ArticleScoreWrapper::getArticle)
                .collect(Collectors.toList());
    }

    /**
     * 提取用户感兴趣的标签ID（批量查询优化）
     */
    private Set<Long> extractInterestedTagIds(List<UserBehavior> userBehaviors) {
        // 1. 提取用户浏览过的文章ID
        Set<Long> viewedArticleIds = userBehaviors.stream()
                .map(UserBehavior::getArticleId)
                .collect(Collectors.toSet());

        // 2. 批量查询所有文章的标签（减少循环SQL调用）
        return tagMapper.selectByArticleIds(new ArrayList<>(viewedArticleIds)).stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
    }

    /**
     * 计算单篇文章的推荐得分
     */
    private double calculateArticleScore(Article article, Set<Long> interestedTagIds) {
        // 1. 获取文章标签（单篇查询，可后续优化为批量）
        List<Tag> articleTags = tagMapper.selectByArticleId(article.getId());

        // 2. 标签匹配得分（核心权重）
        long matchedTagCount = articleTags.stream()
                .map(Tag::getId)
                .filter(interestedTagIds::contains)
                .count();
        double tagScore = matchedTagCount * 2.0;

        // 3. 热度得分（加权处理，避免极端值）
        double viewScore = Math.min(article.getViewCount() * 0.001, 5.0); // 封顶5分
        double likeScore = Math.min(article.getLikeCount() * 0.01, 3.0);  // 封顶3分
        double hotScore = viewScore + likeScore;

        // 4. 时间衰减因子（新文章权重更高）
        LocalDateTime publishTime = Optional.ofNullable(article.getPublishTime()).orElse(article.getCreateTime());
        long daysSincePublish = java.time.Duration.between(publishTime, LocalDateTime.now()).toDays();
        double timeFactor = Math.max(0.1, 1 - (daysSincePublish * 0.01)); // 最低保留10%权重

        // 5. 总得分
        return (tagScore + hotScore) * timeFactor;
    }

    /**
     * 补充热门文章（当个性化推荐不足时）
     */
    private void fillWithHotArticles(List<Article> recommendArticles, int targetSize) {
        if (recommendArticles.size() >= targetSize) {
            return;
        }

        // 计算需要补充的数量
        int needSupplement = targetSize - recommendArticles.size();

        // 查询热门文章（排除已推荐的）
        List<Article> hotArticles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .notIn(Article::getId, recommendArticles.stream().map(Article::getId).collect(Collectors.toList()))
                .orderByDesc(Article::getViewCount, Article::getLikeCount)
                .last("LIMIT " + needSupplement));

        // 补充到推荐列表
        recommendArticles.addAll(hotArticles);
    }

    /**
     * 推荐文章分页处理
     */
    private PageVO<ArticleVO> paginateRecommendArticles(List<Article> allRecommendArticles, ArticleQueryDTO queryDTO) {
        int total = allRecommendArticles.size();
        int pageNum = queryDTO.getPageNum();
        int pageSize = queryDTO.getPageSize();

        // 计算分页索引（处理边界情况）
        int startIndex = Math.max((pageNum - 1) * pageSize, 0);
        int endIndex = Math.min(startIndex + pageSize, total);

        // 分页截取
        List<Article> pagedArticles = startIndex < total ?
                allRecommendArticles.subList(startIndex, endIndex) :
                Collections.emptyList();

        // 转换为VO（此处复用原有convertToVOList方法，实际可优化为批量转换）
        List<ArticleVO> voList = convertToVOList(pagedArticles);

        return new PageVO<>(voList, (long) total, pageNum, pageSize);
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

            if (article.getArticleContentId() != null) {
                ArticleDetail articleDetail = articleDetailMapper.selectById(article.getArticleContentId());
                if (articleDetail != null) {
                    articleVO.setContent(articleDetail.getContent());
                    articleVO.setHtmlContent(articleDetail.getHtmlContent());
                }
            }

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

    /**
     * 文章得分包装类（用于推荐算法排序）
     */
    private static class ArticleScoreWrapper {
        private final Article article;
        private final double score;

        public ArticleScoreWrapper(Article article, double score) {
            this.article = article;
            this.score = score;
        }

        public Article getArticle() {
            return article;
        }

        public double getScore() {
            return score;
        }
    }
}
