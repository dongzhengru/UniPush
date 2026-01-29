package top.zhengru.unipush.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 管理后台服务启动类
 *
 * @author zhengru
 */
@SpringBootApplication(scanBasePackages = "top.zhengru.unipush")
@EnableDiscoveryClient
@MapperScan("top.zhengru.unipush.admin.mapper")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("(♥◠‿◠)ノ゙  UniPush Admin启动成功   ლ(´ڡ`ლ)゙");
    }
}
