package com.ykw.blog_system.service;

import com.ykw.blog_system.dto.TagDTO;
import com.ykw.blog_system.entity.Tag;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {
    
    ResultVO<List<Tag>> getTagList();
    
    ResultVO<List<Tag>> getHotTags(Integer limit);
    
    ResultVO<Tag> getTagDetail(Long tagId);
    
    ResultVO<Long> createTag(TagDTO tagDTO);
    
    ResultVO<Void> updateTag(TagDTO tagDTO);
    
    ResultVO<Void> deleteTag(Long tagId);
    
    ResultVO<PageVO<Tag>> getAdminTagList(Integer pageNum, Integer pageSize);
}
