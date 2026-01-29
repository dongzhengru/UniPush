package top.zhengru.unipush.api;

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
@MapperScan("top.zhengru.unipush.api.mapper")
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
        System.out.println("(♥◠‿◠)ノ゙  UniPush API启动成功   ლ(´ڡ`ლ)゙");
    }
}
