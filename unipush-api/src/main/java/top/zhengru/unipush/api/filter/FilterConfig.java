package top.zhengru.unipush.api.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置
 *
 * @author zhengru
 */
@Configuration
public class FilterConfig {

    /**
     * 注册访问令牌认证过滤器
     *
     * @param filter 访问令牌认证过滤器
     * @return 过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<AccessTokenAuthFilter> accessTokenAuthFilterRegistration(AccessTokenAuthFilter filter) {
        FilterRegistrationBean<AccessTokenAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setName("accessTokenAuthFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 注册JWT认证过滤器
     *
     * @param filter JWT认证过滤器
     * @return 过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setName("jwtAuthFilter");
        registration.setOrder(2);
        return registration;
    }
}
