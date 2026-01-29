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
import top.zhengru.unipush.api.util.JwtUtils;
import top.zhengru.unipush.common.enums.ResponseCode;
import top.zhengru.unipush.common.exception.BusinessException;
import top.zhengru.unipush.common.model.vo.ResponseVO;
import top.zhengru.unipush.common.util.JsonUtils;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 用于Web接口的JWT Token验证
 *
 * @author zhengru
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class JwtAuthFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private static final String JWT_TOKEN_HEADER = "Authorization";
    private static final String JWT_TOKEN_PREFIX = "Bearer ";

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 只对Web接口进行JWT认证（排除OAuth2回调）
        String uri = httpRequest.getRequestURI();
        if (!uri.startsWith("/api/web/") || uri.equals("/api/web/oauth2/callback")) {
            chain.doFilter(request, response);
            return;
        }

        // 排除获取授权URL接口
        if (uri.equals("/api/web/oauth2/authorize")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 获取JWT Token
            String token = httpRequest.getHeader(JWT_TOKEN_HEADER);
            if (!StringUtils.hasText(token)) {
                // 尝试从参数中获取
                token = httpRequest.getParameter("token");
            }

            if (!StringUtils.hasText(token)) {
                throw new BusinessException(ResponseCode.UNAUTHORIZED, "缺少认证令牌");
            }

            // 移除Bearer前缀
            if (token.startsWith(JWT_TOKEN_PREFIX)) {
                token = token.substring(JWT_TOKEN_PREFIX.length());
            }

            // 验证Token
            if (!jwtUtils.validateToken(token)) {
                throw new BusinessException(ResponseCode.INVALID_TOKEN, "Token无效或已过期");
            }

            // 将用户信息放入请求属性
            Long userId = jwtUtils.getUserIdFromToken(token);
            String username = jwtUtils.getUsernameFromToken(token);
            httpRequest.setAttribute("userId", userId);
            httpRequest.setAttribute("username", username);

            // 放行
            chain.doFilter(request, response);

        } catch (BusinessException e) {
            handleError(httpResponse, e);
        } catch (Exception e) {
            logger.error("JWT认证失败", e);
            handleError(httpResponse, new BusinessException(ResponseCode.SYSTEM_ERROR, "系统异常"));
        }
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
