package com.ykw.blog_system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ykw.blog_system.dto.TagDTO;
import com.ykw.blog_system.entity.Tag;
import com.ykw.blog_system.mapper.ArticleTagMapper;
import com.ykw.blog_system.mapper.TagMapper;
import com.ykw.blog_system.service.TagService;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现类
 */
@Service
public class TagServiceImpl implements TagService {
    
    @Autowired
    private TagMapper tagMapper;
    
    @Autowired
    private ArticleTagMapper articleTagMapper;
    
    @Override
    public ResultVO<List<Tag>> getTagList() {
        List<Tag> list = tagMapper.selectList(1);
        return ResultVO.success(list);
    }
    
    @Override
    public ResultVO<List<Tag>> getHotTags(Integer limit) {
        List<Tag> list = tagMapper.selectHotTags(limit);
        return ResultVO.success(list);
    }
    
    @Override
    public ResultVO<Tag> getTagDetail(Long tagId) {
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            return ResultVO.error("标签不存在");
        }
        return ResultVO.success(tag);
    }
    
    @Override
    @Transactional
    public ResultVO<Long> createTag(TagDTO tagDTO) {
        // 检查标签名是否已存在
        if (tagMapper.selectByName(tagDTO.getName()) != null) {
            return ResultVO.error("标签名称已存在");
        }
        
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagDTO, tag);
        tag.setStatus(1);
        
        tagMapper.insert(tag);
        
        return ResultVO.success("创建成功", tag.getId());
    }
    
    @Override
    @Transactional
    public ResultVO<Void> updateTag(TagDTO tagDTO) {
        Tag existingTag = tagMapper.selectById(tagDTO.getId());
        if (existingTag == null) {
            return ResultVO.error("标签不存在");
        }
        
        // 检查新名称是否与其他标签重复
        Tag nameTag = tagMapper.selectByName(tagDTO.getName());
        if (nameTag != null && !nameTag.getId().equals(tagDTO.getId())) {
            return ResultVO.error("标签名称已存在");
        }
        
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagDTO, tag);
        tagMapper.update(tag);
        
        return ResultVO.success();
    }
    
    @Override
    @Transactional
    public ResultVO<Void> deleteTag(Long tagId) {
        // 删除标签与文章的关联
        articleTagMapper.deleteByTagId(tagId);
        // 删除标签
        tagMapper.deleteById(tagId);
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<PageVO<Tag>> getAdminTagList(Integer pageNum, Integer pageSize) {
        // 创建 Page 对象
        Page<Tag> page = new Page<>(pageNum, pageSize);
        
        // 执行分页查询
        List<Tag> list = tagMapper.selectPage(page);
        
        // 构建分页结果
        PageVO<Tag> pageVO = new PageVO<>(
            list, 
            page.getTotal(), 
            pageNum, 
            pageSize
        );
        
        return ResultVO.success(pageVO);
    }
}
