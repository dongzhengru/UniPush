package top.zhengru.unipush.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 核心调度服务启动类
 *
 * @author zhengru
 */
@SpringBootApplication(scanBasePackages = "top.zhengru.unipush")
@EnableDiscoveryClient
@MapperScan("top.zhengru.unipush.core.mapper")
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
        System.out.println("(♥◠‿◠)ノ゙  UniPush Core启动成功   ლ(´ڡ`ლ)゙");
    }
}
