package top.zhengru.unipush.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zhengru.unipush.api.mapper.AccessTokenMapper;
import top.zhengru.unipush.api.util.RedisUtils;
import top.zhengru.unipush.common.constant.RedisConstants;
import top.zhengru.unipush.common.model.entity.AccessToken;
import top.zhengru.unipush.common.model.vo.TokenInfoVO;
import top.zhengru.unipush.common.util.SnowflakeIdUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 访问令牌服务
 *
 * @author zhengru
 */
@Service
public class AccessTokenService {

    private static final Logger logger = LoggerFactory.getLogger(AccessTokenService.class);

    @Autowired
    private AccessTokenMapper accessTokenMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 查询所有令牌
     *
     * @return 令牌列表
     */
    public List<TokenInfoVO> listTokens() {
        LambdaQueryWrapper<AccessToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AccessToken::getCreateTime);
        List<AccessToken> tokens = accessTokenMapper.selectList(wrapper);

        return tokens.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 根据ID获取令牌
     *
     * @param id 令牌ID
     * @return 令牌信息
     */
    public TokenInfoVO getTokenById(Long id) {
        AccessToken token = accessTokenMapper.selectById(id);
        return token != null ? convertToVO(token) : null;
    }

    /**
     * 根据令牌字符串获取令牌
     *
     * @param token 令牌字符串
     * @return 令牌信息
     */
    public AccessToken getTokenByToken(String token) {
        // 先从Redis缓存获取
        String cacheKey = RedisConstants.ACCESS_TOKEN_KEY + token;
        AccessToken cachedToken = redisUtils.get(cacheKey);
        if (cachedToken != null) {
            return cachedToken;
        }

        // 从数据库查询
        LambdaQueryWrapper<AccessToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessToken::getToken, token);
        AccessToken accessToken = accessTokenMapper.selectOne(wrapper);

        // 放入缓存
        if (accessToken != null) {
            redisUtils.set(cacheKey, accessToken, 3600, java.util.concurrent.TimeUnit.SECONDS);
        }

        return accessToken;
    }

    /**
     * 创建令牌
     *
     * @param tokenName    令牌名称
     * @param description  描述
     * @param allowedIps   IP白名单
     * @param rateLimit    限流阈值
     * @param expireTime   过期时间
     * @param creatorId    创建人ID
     * @return 令牌信息
     */
    public TokenInfoVO createToken(String tokenName, String description, String allowedIps,
                                    Integer rateLimit, LocalDateTime expireTime, Long creatorId) {
        // 生成令牌
        String token = generateToken();

        AccessToken accessToken = new AccessToken();
        accessToken.setToken(token);
        accessToken.setTokenName(tokenName);
        accessToken.setDescription(description);
        accessToken.setAllowedIps(allowedIps);
        accessToken.setRateLimit(rateLimit != null ? rateLimit : 1000);
        accessToken.setStatus(1);
        accessToken.setExpireTime(expireTime);
        accessToken.setCreatorId(creatorId);

        accessTokenMapper.insert(accessToken);
        logger.info("创建令牌成功: tokenName={}", tokenName);

        // 放入缓存
        String cacheKey = RedisConstants.ACCESS_TOKEN_KEY + token;
        redisUtils.set(cacheKey, accessToken, 3600, java.util.concurrent.TimeUnit.SECONDS);

        return convertToVO(accessToken);
    }

    /**
     * 更新令牌
     *
     * @param id           令牌ID
     * @param tokenName    令牌名称
     * @param description  描述
     * @param allowedIps   IP白名单
     * @param rateLimit    限流阈值
     * @param expireTime   过期时间
     * @return 令牌信息
     */
    public TokenInfoVO updateToken(Long id, String tokenName, String description,
                                    String allowedIps, Integer rateLimit, LocalDateTime expireTime) {
        AccessToken accessToken = new AccessToken();
        accessToken.setId(id);
        accessToken.setTokenName(tokenName);
        accessToken.setDescription(description);
        accessToken.setAllowedIps(allowedIps);
        accessToken.setRateLimit(rateLimit);
        accessToken.setExpireTime(expireTime);

        accessTokenMapper.updateById(accessToken);
        logger.info("更新令牌成功: id={}", id);

        // 清除缓存
        AccessToken token = accessTokenMapper.selectById(id);
        if (token != null) {
            String cacheKey = RedisConstants.ACCESS_TOKEN_KEY + token.getToken();
            redisUtils.delete(cacheKey);
        }

        return getTokenById(id);
    }

    /**
     * 删除令牌
     *
     * @param id 令牌ID
     * @return 是否成功
     */
    public boolean deleteToken(Long id) {
        AccessToken token = accessTokenMapper.selectById(id);
        if (token == null) {
            return false;
        }

        accessTokenMapper.deleteById(id);
        logger.info("删除令牌成功: id={}", id);

        // 清除缓存
        String cacheKey = RedisConstants.ACCESS_TOKEN_KEY + token.getToken();
        redisUtils.delete(cacheKey);

        return true;
    }

    /**
     * 更新令牌状态
     *
     * @param id     令牌ID
     * @param status 状态：1-启用 0-禁用
     * @return 是否成功
     */
    public boolean updateTokenStatus(Long id, Integer status) {
        AccessToken accessToken = new AccessToken();
        accessToken.setId(id);
        accessToken.setStatus(status);

        int rows = accessTokenMapper.updateById(accessToken);
        logger.info("更新令牌状态: id={}, status={}", id, status);

        // 清除缓存
        AccessToken token = accessTokenMapper.selectById(id);
        if (token != null) {
            String cacheKey = RedisConstants.ACCESS_TOKEN_KEY + token.getToken();
            redisUtils.delete(cacheKey);
        }

        return rows > 0;
    }

    /**
     * 生成令牌
     *
     * @return 令牌
     */
    private String generateToken() {
        return "ut_" + SnowflakeIdUtils.getInstance().nextIdStr() + "_" +
               System.currentTimeMillis();
    }

    /**
     * 转换为VO
     *
     * @param token 令牌实体
     * @return 令牌VO
     */
    private TokenInfoVO convertToVO(AccessToken token) {
        TokenInfoVO vo = new TokenInfoVO();
        vo.setId(token.getId());
        // 脱敏显示，只显示前8位和后8位
        String tokenStr = token.getToken();
        if (tokenStr != null && tokenStr.length() > 16) {
            vo.setToken(tokenStr.substring(0, 8) + "****" + tokenStr.substring(tokenStr.length() - 8));
        } else {
            vo.setToken(tokenStr);
        }
        vo.setTokenName(token.getTokenName());
        vo.setDescription(token.getDescription());
        vo.setAllowedIps(token.getAllowedIps());
        vo.setRateLimit(token.getRateLimit());
        vo.setStatus(token.getStatus());
        vo.setExpireTime(token.getExpireTime());
        vo.setCreateTime(token.getCreateTime());
        return vo;
    }
}
