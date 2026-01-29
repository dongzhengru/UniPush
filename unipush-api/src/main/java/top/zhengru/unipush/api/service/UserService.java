package top.zhengru.unipush.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zhengru.unipush.api.mapper.SysUserMapper;
import top.zhengru.unipush.common.model.entity.SysUser;

import java.time.LocalDateTime;

/**
 * 用户服务
 *
 * @author zhengru
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 根据手机号获取用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    public SysUser getUserByPhone(String phone) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, phone);
        return sysUserMapper.selectOne(wrapper);
    }

    /**
     * 根据用户ID获取用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    public SysUser getUserById(Long userId) {
        return sysUserMapper.selectById(userId);
    }

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建后的用户信息
     */
    public SysUser createUser(SysUser user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
        logger.info("创建用户成功: phone={}", user.getPhone());
        return user;
    }

    /**
     * 更新用户
     *
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    public SysUser updateUser(SysUser user) {
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
        logger.info("更新用户成功: id={}", user.getId());
        return user;
    }

    /**
     * 更新最后登录信息
     *
     * @param userId 用户ID
     * @param ip     登录IP
     */
    public void updateLastLoginInfo(Long userId, String ip) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        sysUserMapper.updateById(user);
        logger.info("更新最后登录信息: userId={}, ip={}", userId, ip);
    }
}
