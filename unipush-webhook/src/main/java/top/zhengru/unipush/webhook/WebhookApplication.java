package top.zhengru.unipush.webhook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Webhook投递执行器启动类
 *
 * @author zhengru
 */
@SpringBootApplication(scanBasePackages = "top.zhengru.unipush")
public class WebhookApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhookApplication.class, args);
        System.out.println("(♥◠‿◠)ノ゙  UniPush Webhook启动成功   ლ(´ڡ`ლ)゙");
    }
}
