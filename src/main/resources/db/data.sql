-- 博客系统测试数据

-- 插入默认管理员用户（密码：admin123）
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '管理员', 'admin@blog.com', 1, 1);

-- 插入测试用户（密码：123456）
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `role`, `status`) VALUES
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '张三', 'zhangsan@example.com', 0, 1),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '李四', 'lisi@example.com', 0, 1),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '王五', 'wangwu@example.com', 0, 1);

-- 插入分类
INSERT INTO `category` (`name`, `description`, `sort_order`, `status`) VALUES
('技术分享', '技术文章、编程经验分享', 1, 1),
('生活随笔', '日常生活、感悟记录', 2, 1),
('学习笔记', '学习过程中的笔记整理', 3, 1),
('项目实战', '项目开发经验总结', 4, 1),
('面试经验', '面试题解析、求职经验', 5, 1);

-- 插入标签
INSERT INTO `tag` (`name`, `status`) VALUES
('Java', 1),
('Spring Boot', 1),
('MySQL', 1),
('Redis', 1),
('前端', 1),
('Vue', 1),
('React', 1),
('算法', 1),
('架构', 1),
('面试', 1),
('微服务', 1),
('Docker', 1),
('Linux', 1),
('Git', 1),
('设计模式', 1);

-- 插入文章
INSERT INTO `article` (`title`, `summary`, `content`, `html_content`, `author_id`, `category_id`, `view_count`, `like_count`, `comment_count`, `status`, `is_top`, `is_recommend`, `publish_time`) VALUES
('Spring Boot入门教程', '本文介绍Spring Boot的基础知识和快速入门方法，适合初学者阅读。', '# Spring Boot入门教程\n\nSpring Boot是一个简化Spring应用开发的框架。\n\n## 特点\n- 自动配置\n- 起步依赖\n- 内嵌服务器\n\n## 快速开始\n```java\n@SpringBootApplication\npublic class DemoApplication {\n    public static void main(String[] args) {\n        SpringApplication.run(DemoApplication.class, args);\n    }\n}\n```', '<h1>Spring Boot入门教程</h1><p>Spring Boot是一个简化Spring应用开发的框架。</p><h2>特点</h2><ul><li>自动配置</li><li>起步依赖</li><li>内嵌服务器</li></ul>', 1, 1, 1250, 89, 12, 1, 1, 1, NOW()),
('MySQL性能优化指南', '分享MySQL数据库的性能优化技巧，包括索引优化、查询优化等内容。', '# MySQL性能优化\n\n## 索引优化\n- 选择合适的索引类型\n- 避免索引失效\n- 联合索引的使用\n\n## 查询优化\n- 避免SELECT *\n- 使用EXPLAIN分析查询', '<h1>MySQL性能优化</h1><h2>索引优化</h2><ul><li>选择合适的索引类型</li></ul>', 1, 1, 980, 67, 8, 1, 0, 1, NOW()),
('Vue3组合式API详解', '详细介绍Vue3的组合式API，包括setup函数、响应式API等核心概念。', '# Vue3组合式API\n\n## setup函数\nsetup是组合式API的入口。\n\n## 响应式API\n- ref\n- reactive\n- computed', '<h1>Vue3组合式API</h1><p>setup是组合式API的入口。</p>', 2, 3, 756, 45, 6, 1, 0, 0, NOW()),
('Redis缓存实战', 'Redis在实际项目中的应用，包括缓存设计、分布式锁等场景。', '# Redis缓存实战\n\n## 缓存设计\n- 缓存穿透\n- 缓存击穿\n- 缓存雪崩\n\n## 分布式锁\n使用Redisson实现分布式锁。', '<h1>Redis缓存实战</h1><h2>缓存设计</h2>', 1, 1, 1100, 78, 15, 1, 0, 1, NOW()),
('算法学习之路', '记录我的算法学习历程，分享刷题经验和常见算法题解。', '# 算法学习之路\n\n## 数组\n## 链表\n## 树\n## 动态规划', '<h1>算法学习之路</h1>', 2, 3, 543, 32, 4, 1, 0, 0, NOW()),
('微服务架构设计', '探讨微服务架构的设计原则和实践方案。', '# 微服务架构\n\n## 服务拆分\n## 服务治理\n## 分布式事务', '<h1>微服务架构</h1>', 1, 4, 890, 56, 9, 1, 0, 1, NOW()),
('Docker容器化部署', '使用Docker进行应用容器化部署的完整教程。', '# Docker部署\n\n## Dockerfile编写\n## 镜像构建\n## 容器编排', '<h1>Docker部署</h1>', 1, 4, 670, 43, 5, 1, 0, 0, NOW()),
('设计模式总结', '23种设计模式的总结和实际应用案例。', '# 设计模式\n\n## 创建型模式\n## 结构型模式\n## 行为型模式', '<h1>设计模式</h1>', 1, 1, 1200, 92, 18, 1, 1, 1, NOW()),
('前端性能优化', '前端页面性能优化的各种技巧和最佳实践。', '# 前端优化\n\n## 代码优化\n## 资源优化\n## 网络优化', '<h1>前端优化</h1>', 2, 5, 820, 61, 7, 1, 0, 0, NOW()),
('Java并发编程', 'Java并发编程的核心概念和常用工具类。', '# 并发编程\n\n## 线程池\n## 锁机制\n## 并发容器', '<h1>并发编程</h1>', 1, 1, 1350, 105, 22, 1, 1, 1, NOW());

-- 插入文章标签关联
INSERT INTO `article_tag` (`article_id`, `tag_id`) VALUES
(1, 1), (1, 2), -- Spring Boot教程：Java, Spring Boot
(2, 1), (2, 3), -- MySQL优化：Java, MySQL
(3, 5), (3, 6), -- Vue3：前端, Vue
(4, 1), (4, 4), -- Redis：Java, Redis
(5, 8), -- 算法：算法
(6, 1), (6, 9), (6, 11), -- 微服务：Java, 架构, 微服务
(7, 12), (7, 13), -- Docker：Docker, Linux
(8, 1), (8, 15), -- 设计模式：Java, 设计模式
(9, 5), (9, 6), (9, 7), -- 前端优化：前端, Vue, React
(10, 1), (10, 8); -- 并发编程：Java, 算法

-- 插入评论
INSERT INTO `comment` (`article_id`, `parent_id`, `user_id`, `content`, `status`, `create_time`) VALUES
(1, NULL, 2, '写得很详细，受益匪浅！', 1, NOW()),
(1, NULL, 3, 'Spring Boot确实简化了开发', 1, NOW()),
(1, 2, 4, '同意，特别是自动配置功能', 1, NOW()),
(2, NULL, 2, '索引优化部分讲得很好', 1, NOW()),
(3, NULL, 3, 'Vue3的组合式API比Options API好用多了', 1, NOW()),
(4, NULL, 2, '缓存雪崩的解决方案很实用', 1, NOW()),
(8, NULL, 3, '设计模式是程序员的必修课', 1, NOW()),
(10, NULL, 2, '并发编程一直是难点，这篇文章讲得很清楚', 1, NOW());

-- 插入系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('site_name', '我的博客', '网站名称'),
('site_description', '一个基于Spring Boot开发的博客系统', '网站描述'),
('site_logo', '/logo.png', '网站Logo'),
('recommend_algorithm', 'hybrid', '推荐算法：hot-热门，latest-最新，hybrid-混合，personal-个性化');
