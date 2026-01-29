package top.zhengru.unipush.api;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API网关服务启动类
 *
 * @author zhengru
 */
@SpringBootApplication(scanBasePackages = "top.zhengru.unipush")
@EnableDiscoveryClient
@EnableDubbo
@MapperScan("top.zhengru.unipush.api.mapper")
public class ApiApplication {

    public static void main(String[] args) {
        // 强制指定Dubbo绑定的IP地址（解决多网卡IP识别错误问题）
        System.setProperty("dubbo.protocol.host", "192.168.31.101");
        System.setProperty("dubbo.registry.parameters.register.ip", "192.168.31.101");

        SpringApplication.run(ApiApplication.class, args);
        System.out.println("(♥◠‿◠)ノ゙  UniPush API启动成功   ლ(´ڡ`ლ)゙");
    }
}
