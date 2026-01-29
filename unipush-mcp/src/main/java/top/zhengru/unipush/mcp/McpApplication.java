package top.zhengru.unipush.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * MCP扩展服务启动类
 *
 * @author zhengru
 */
@SpringBootApplication(scanBasePackages = "top.zhengru.unipush")
@EnableDiscoveryClient
public class McpApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpApplication.class, args);
        System.out.println("(♥◠‿◠)ノ゙  UniPush MCP启动成功   ლ(´ڡ`ლ)゙");
    }
}
