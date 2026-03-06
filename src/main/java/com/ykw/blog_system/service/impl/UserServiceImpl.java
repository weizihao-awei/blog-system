package com.ykw.blog_system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ykw.blog_system.entity.User;
import com.ykw.blog_system.mapper.UserMapper;
import com.ykw.blog_system.service.UserService;
import com.ykw.blog_system.vo.PageVO;
import com.ykw.blog_system.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
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
        
        userMapper.update(user);
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<Void> updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ResultVO.error("用户不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResultVO.error("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.update(user);
        
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<PageVO<User>> getUserList(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectList(keyword);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        
        // 清除密码
        list.forEach(user -> user.setPassword(null));
        
        PageVO<User> pageVO = new PageVO<>(list, pageInfo.getTotal(), pageNum, pageSize);
        return ResultVO.success(pageVO);
    }
    
    @Override
    public ResultVO<Void> updateUserStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        userMapper.update(user);
        return ResultVO.success();
    }
    
    @Override
    public ResultVO<Void> deleteUser(Long userId) {
        userMapper.deleteById(userId);
        return ResultVO.success();
    }
}
