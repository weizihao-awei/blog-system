-- 数据迁移脚本：将旧的点赞和收藏数据迁移到 user_foot 表
-- 执行前请确保已创建 user_foot 表

-- 1. 迁移文章点赞数据
INSERT INTO user_foot (user_id, document_id, document_type, document_user_id, praise_stat, read_stat, collection_stat, comment_stat)
SELECT 
    al.user_id,
    al.article_id AS document_id,
    1 AS document_type, -- 1-文章
    a.author_id AS document_user_id,
    1 AS praise_stat, -- 1-已点赞
    1 AS read_stat, -- 标记为已读
    0 AS collection_stat,
    0 AS comment_stat
FROM article_like al
LEFT JOIN article a ON al.article_id = a.id
ON DUPLICATE KEY UPDATE 
    praise_stat = VALUES(praise_stat);

-- 2. 迁移文章收藏数据
INSERT INTO user_foot (user_id, document_id, document_type, document_user_id, collection_stat, read_stat, praise_stat, comment_stat)
SELECT 
    ac.user_id,
    ac.article_id AS document_id,
   1 AS document_type, -- 1-文章
    a.author_id AS document_user_id,
    1 AS collection_stat, -- 1-已收藏
    1 AS read_stat, -- 标记为已读
    0 AS praise_stat,
    0 AS comment_stat
FROM article_collect ac
LEFT JOIN article a ON ac.article_id = a.id
ON DUPLICATE KEY UPDATE 
    collection_stat = VALUES(collection_stat);

-- 3. 合并同一用户的点赞和收藏记录（如果一个用户既点赞又收藏同一篇文章）
UPDATE user_foot uf
INNER JOIN (
    SELECT 
        user_id, 
        document_id, 
        document_type,
        MAX(CASE WHEN praise_stat = 1 THEN 1 ELSE 0 END) as has_praise,
        MAX(CASE WHEN collection_stat = 1 THEN 1 ELSE 0 END) as has_collection
    FROM user_foot
    GROUP BY user_id, document_id, document_type
    HAVING COUNT(*) > 1
) grouped 
ON uf.user_id = grouped.user_id 
   AND uf.document_id = grouped.document_id 
   AND uf.document_type = grouped.document_type
SET 
    uf.praise_stat = grouped.has_praise,
    uf.collection_stat = grouped.has_collection;

-- 4. 删除重复记录，保留一条
DELETE uf1 FROM user_foot uf1
INNER JOIN (
    SELECT 
        user_id, 
        document_id, 
        document_type,
        MIN(id) as min_id
    FROM user_foot
    GROUP BY user_id, document_id, document_type
    HAVING COUNT(*) > 1
) uf2 
ON uf1.user_id = uf2.user_id 
   AND uf1.document_id = uf2.document_id 
   AND uf1.document_type = uf2.document_type
   AND uf1.id > uf2.min_id;

-- 5. 更新文章的点赞数（从 user_foot 表统计）
UPDATE article a
INNER JOIN (
    SELECT 
        document_id,
        COUNT(*) as like_count
    FROM user_foot
    WHERE document_type = 1 AND praise_stat = 1
    GROUP BY document_id
) uf ON a.id = uf.document_id
SET a.like_count = uf.like_count;

-- 注意：旧的 article_like 和 article_collect 表可以先保留，确认系统运行正常后再删除
-- 如果需要删除旧表，请执行以下命令：
-- DROP TABLE IF EXISTS article_like;
-- DROP TABLE IF EXISTS article_collect;
