-- 为 article 表添加收藏计数字段
ALTER TABLE `article` 
ADD COLUMN `collection_count` INT DEFAULT 0 COMMENT '收藏次数' AFTER `comment_count`;

-- 更新现有数据的收藏数量（根据 user_foot 表统计）
UPDATE article a
SET a.collection_count = (
    SELECT COUNT(*) 
    FROM user_foot uf 
    WHERE uf.document_id = a.id 
    AND uf.document_type = 1 
    AND uf.collection_stat = 1
);
