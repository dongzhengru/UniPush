package top.zhengru.unipush.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.zhengru.unipush.api.service.AccessTokenService;
import top.zhengru.unipush.api.util.RedisUtils;
import top.zhengru.unipush.common.constant.RedisConstants;
import top.zhengru.unipush.common.enums.ResponseCode;
import top.zhengru.unipush.common.exception.BusinessException;
import top.zhengru.unipush.common.model.entity.AccessToken;
import top.zhengru.unipush.common.model.vo.ResponseVO;
import top.zhengru.unipush.common.util.JsonUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 访问令牌认证过滤器
 *
 * @author zhengru
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AccessTokenAuthFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AccessTokenAuthFilter.class);

    private static final String ACCESS_TOKEN_HEADER = "access-token";

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 只对开放接口进行认证
        String uri = httpRequest.getRequestURI();
        if (!uri.startsWith("/api/open/")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 获取访问令牌
            String token = httpRequest.getHeader(ACCESS_TOKEN_HEADER);
            if (!StringUtils.hasText(token)) {
                throw new BusinessException(ResponseCode.UNAUTHORIZED, "缺少访问令牌");
            }

            // 从数据库获取令牌信息
            AccessToken accessToken = accessTokenService.getTokenByToken(token);

            if (accessToken == null) {
                throw new BusinessException(ResponseCode.UNAUTHORIZED, "无效的访问令牌");
            }

            // 检查令牌状态
            if (accessToken.getStatus() == 0) {
                throw new BusinessException(ResponseCode.UNAUTHORIZED, "访问令牌已禁用");
            }

            // 检查令牌是否过期
            if (accessToken.getExpireTime() != null && accessToken.getExpireTime().isBefore(java.time.LocalDateTime.now())) {
                throw new BusinessException(ResponseCode.UNAUTHORIZED, "访问令牌已过期");
            }

            // 检查IP白名单
            if (StringUtils.hasText(accessToken.getAllowedIps())) {
                String clientIp = getClientIp(httpRequest);
                String[] allowedIps = accessToken.getAllowedIps().split(",");
                boolean ipAllowed = false;
                for (String allowedIp : allowedIps) {
                    if (allowedIp.trim().equals(clientIp)) {
                        ipAllowed = true;
                        break;
                    }
                }
                if (!ipAllowed) {
                    throw new BusinessException(ResponseCode.IP_FORBIDDEN, "请求IP未授权");
                }
            }

            // 限流检查
            String rateLimitKey = RedisConstants.RATE_LIMIT_KEY + token;
            long currentCount = redisUtils.increment(rateLimitKey);
            if (currentCount == 1) {
                // 设置过期时间为1分钟
                redisUtils.expire(rateLimitKey, 60);
            }
            if (currentCount > accessToken.getRateLimit()) {
                throw new BusinessException(ResponseCode.ACCOUNT_RESTRICTED, "请求次数过多，请稍后再试");
            }

            // 将令牌信息放入请求属性，供后续使用
            httpRequest.setAttribute("accessToken", accessToken);

            // 放行
            chain.doFilter(request, response);

        } catch (BusinessException e) {
            handleError(httpResponse, e);
        } catch (Exception e) {
            logger.error("访问令牌认证失败", e);
            handleError(httpResponse, new BusinessException(ResponseCode.SYSTEM_ERROR, "系统异常"));
        }
    }

    /**
     * 获取客户端IP
     *
     * @param request HTTP请求
     * @return 客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 处理错误
     *
     * @param response HTTP响应
     * @param e        异常
     * @throws IOException IO异常
     */
    private void handleError(HttpServletResponse response, BusinessException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        ResponseVO<Void> result = ResponseVO.fail(e.getCode(), e.getMessage());
        String json = JsonUtils.toJsonString(result);
        response.getWriter().write(json);
    }
}
