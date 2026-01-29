package top.zhengru.unipush.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) 配置
 *
 * @author zhengru
 */
@Configuration
public class OpenApiConfig {

    /**
     * OpenAPI 全局配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 接口信息
                .info(new Info()
                        .title("UniPush 统一消息推送平台 API")
                        .version("1.0.0")
                        .description("UniPush 统一消息推送平台接口文档，提供开放接口和管理接口")
                        .contact(new Contact()
                                .name("zhengru")
                                .email("admin@zhengru.top")
                                .url("https://zhengru.top"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // 服务器配置
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("本地开发环境"),
                        new Server()
                                .url("http://192.168.31.101:8080")
                                .description("内网测试环境")
                ))
                // 安全配置 - JWT Token 认证
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("请输入JWT Token，无需添加Bearer前缀")));
    }

    /**
     * 开放接口分组（不需要认证）
     */
    @Bean(name = "openApiGroup")
    public GroupedOpenApi openApi() {
        return GroupedOpenApi.builder()
                .group("开放接口")
                .pathsToMatch(
                        "/api/open/**",
                        "/api/web/oauth2/**"
                )
                .build();
    }

    /**
     * Web管理接口分组（需要JWT认证）
     */
    @Bean(name = "webApiGroup")
    public GroupedOpenApi webApi() {
        return GroupedOpenApi.builder()
                .group("管理接口")
                .pathsToMatch(
                        "/api/web/**",
                        "/api/web/message/**",
                        "/api/web/token/**",
                        "/api/web/channel/**",
                        "/api/web/template/**"
                )
                .pathsToExclude("/api/web/oauth2/**")
                .build();
    }
}
