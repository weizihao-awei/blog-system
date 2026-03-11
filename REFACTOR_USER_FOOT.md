# UserFoot 表重构说明

## 概述

本次重构将原有的 `article_like`（点赞表）和 `article_collect`（收藏表）合并为统一的 `user_foot`（用户足迹表），实现了更通用的用户行为管理。

## 主要改动

### 1. 数据库层面

#### 新增表：user_foot
```sql
CREATE TABLE `user_foot` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户 ID',
  `document_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '文档 ID（文章/评论）',
  `document_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '文档类型：1-文章，2-评论',
  `document_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '发布该文档的用户 ID',
  `collection_stat` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '收藏状态：0-未收藏，1-已收藏，2-取消收藏',
  `read_stat` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '阅读状态：0-未读，1-已读',
  `comment_stat` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '评论状态：0-未评论，1-已评论，2-删除评论',
  `praise_stat` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '点赞状态：0-未点赞，1-已点赞，2-取消点赞',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_document` (`user_id`,`document_id`,`document_type`),
  KEY `idx_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户足迹表';
```

#### 状态说明
- **点赞状态 (praise_stat)**: 0-未点赞，1-已点赞，2-取消点赞
- **收藏状态 (collection_stat)**: 0-未收藏，1-已收藏，2-取消收藏
- **阅读状态 (read_stat)**: 0-未读，1-已读
- **评论状态 (comment_stat)**: 0-未评论，1-已评论，2-删除评论

### 2. 实体类层面

#### 新增实体类：UserFoot
- 位置：`src/main/java/com/ykw/blog_system/entity/UserFoot.java`
- 功能：统一管理用户的点赞、收藏等行为

#### 移除依赖
- `ArticleLikeMapper` - 替换为 `UserFootMapper`
- `ArticleCollectMapper` - 替换为 `UserFootMapper`

### 3. Mapper 层面

#### 新增 Mapper：UserFootMapper
- 接口：`src/main/java/com/ykw/blog_system/mapper/UserFootMapper.java`
- XML: `src/main/resources/mapper/UserFootMapper.xml`

#### 主要方法
- `insert(UserFoot userFoot)` - 插入足迹记录
- `update(UserFoot userFoot)` - 更新足迹状态
- `selectByUserAndDocument()` - 查询用户的足迹
- `selectByUserIdAndType()` - 查询用户的收藏列表
- `countPraiseByDocumentId()` - 统计点赞数
- `countCollectionByDocumentId()` - 统计收藏数

### 4. 业务逻辑层面

#### 修改的服务类：ArticleServiceImpl
位置：`src/main/java/com/ykw/blog_system/service/impl/ArticleServiceImpl.java`

**核心改动：**

1. **获取文章详情时检查点赞/收藏状态**
```java
UserFoot foot = userFootMapper.selectByUserAndDocument(currentUserId, articleId, 1);
if (foot != null) {
    isLiked = foot.getPraiseStat() == 1;
    isCollected = foot.getCollectionStat() == 1;
}
```

2. **点赞文章**
- 如果不存在足迹记录，创建新记录
- 如果已存在，更新点赞状态为 1（已点赞）
- 使用 `praise_stat` 字段代替单独的点赞表

3. **取消点赞**
- 更新 `praise_stat` 为 2（取消点赞）
- 不再物理删除记录

4. **收藏文章**
- 类似点赞逻辑，使用 `collection_stat` 字段

5. **取消收藏**
- 更新 `collection_stat` 为 2（取消收藏）

6. **获取收藏列表**
- 查询 `collection_stat = 1` 的记录

## 接口兼容性

**重要：** 所有对外的 API 接口保持不变！

- `/api/article/like/{articleId}` (POST) - 点赞文章
- `/api/article/like/{articleId}` (DELETE) - 取消点赞
- `/api/article/collect/{articleId}` (POST) - 收藏文章
- `/api/article/collect/{articleId}` (DELETE) - 取消收藏
- `/api/article/my-collects` (GET) - 获取收藏列表

前端无需任何改动！

## 数据迁移

### 迁移步骤

1. **执行数据库结构变更**
```bash
# 先执行 blog_system.sql 创建 user_foot 表
mysql -u your_user -p your_password blog_system < sql/blog_system.sql
```

2. **迁移旧数据到新表**
```bash
# 执行数据迁移脚本
mysql -u your_user -p your_password blog_system < sql/migrate_to_user_foot.sql
```

3. **验证数据迁移**
```sql
-- 检查迁移后的数据
SELECT * FROM user_foot LIMIT 10;

-- 验证点赞数
SELECT document_id, praise_stat, COUNT(*) 
FROM user_foot 
WHERE document_type = 1 
GROUP BY document_id, praise_stat;
```

4. **确认系统运行正常后，可选择删除旧表**
```sql
-- 可选：删除旧表
DROP TABLE IF EXISTS article_like;
DROP TABLE IF EXISTS article_collect;
```

## 优势

1. **统一管理**: 点赞、收藏、阅读、评论等行为统一管理
2. **扩展性强**: 未来可以轻松添加更多用户行为类型
3. **减少表数量**: 从 2 张表减少到 1 张表
4. **支持更多场景**: 支持评论点赞等未来功能
5. **状态追踪**: 可以区分"已点赞"、"取消点赞"等状态
6. **向后兼容**: 对外接口完全不变，前端无感知

## 注意事项

1. **数据迁移前务必备份数据库！**
2. 迁移脚本中的 SQL 语句假设旧表结构为 BIGINT 主键，新表为 INT，需要确保数据类型兼容
3. 建议在测试环境先完整测试后再部署到生产环境
4. 迁移过程中会更新文章的点赞数字段，确保与 user_foot 表统计一致

## 回滚方案

如果需要回滚到原来的设计：

1. 恢复备份的数据库
2. 或者重新创建 article_like 和 article_collect 表
3. 从 user_foot 表反向迁移数据到旧表
4. 恢复旧的代码版本

## 相关文件

### 新增文件
- `entity/UserFoot.java` - 用户足迹实体
- `mapper/UserFootMapper.java` - Mapper 接口
- `resources/mapper/UserFootMapper.xml` - MyBatis 映射文件
- `sql/migrate_to_user_foot.sql` - 数据迁移脚本

### 修改文件
- `sql/blog_system.sql` - 数据库结构（替换旧表为新表）
- `service/impl/ArticleServiceImpl.java` - 业务逻辑实现
