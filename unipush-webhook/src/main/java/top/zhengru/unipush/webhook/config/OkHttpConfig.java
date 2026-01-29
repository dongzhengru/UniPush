package top.zhengru.unipush.webhook.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * OkHttp配置(用于HTTP投递)
 *
 * @author zhengru
 */
@Configuration
public class OkHttpConfig {

    @Value("${webhook.timeout.connect:5000}")
    private int connectTimeout;

    @Value("${webhook.timeout.read:10000}")
    private int readTimeout;

    @Value("${webhook.pool.maxIdle:5}")
    private int maxIdleConnections;

    @Value("${webhook.pool.keepAlive:300000}")
    private long keepAliveDuration;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
            // 连接池配置
            .connectionPool(new ConnectionPool(
                maxIdleConnections,
                keepAliveDuration,
                TimeUnit.MILLISECONDS
            ))
            // 连接超时
            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            // 读取超时
            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            // 写入超时
            .writeTimeout(readTimeout, TimeUnit.MILLISECONDS)
            // 失败后重试（可选）
            .retryOnConnectionFailure(true)
            .build();
    }
}
