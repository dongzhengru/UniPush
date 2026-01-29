package top.zhengru.unipush.webhook.service;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import top.zhengru.unipush.webhook.mq.DeliveryResultProducer;
import top.zhengru.unipush.webhook.model.WebhookRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Webhook投递服务
 *
 * @author zhengru
 */
@Slf4j
@Service
public class WebhookDeliveryService {

    @Autowired
    private WebClient webClient;

    @Autowired
    private DeliveryResultProducer deliveryResultProducer;

    @Value("${webhook.timeout.connect:5000}")
    private int connectTimeout;

    @Value("${webhook.timeout.read:10000}")
    private int readTimeout;

    /**
     * 执行webhook投递
     *
     * @param messageId 消息ID
     * @param taskMessage 投递任务消息(JSON)
     */
    public void deliver(String messageId, String taskMessage) {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 解析投递任务
            WebhookRequest request = JSON.parseObject(taskMessage, WebhookRequest.class);

            log.info("开始webhook投递, messageId: {}, url: {}", messageId, request.getTargetUrl());

            // 2. 构造HTTP请求体
            Map<String, Object> body = new HashMap<>();
            body.put("messageId", messageId);
            body.put("title", request.getTitle());
            body.put("content", request.getContent());
            body.put("timestamp", System.currentTimeMillis());

            // 3. 发送HTTP请求
            String response = webClient.post()
                .uri(request.getTargetUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(readTimeout))
                .block();

            long costTime = System.currentTimeMillis() - startTime;

            log.info("webhook投递成功, messageId: {}, costTime: {}ms, response: {}",
                messageId, costTime, response);

            // 4. 发送成功结果
            sendDeliveryResult(messageId, true, null);

        } catch (WebClientResponseException e) {
            long costTime = System.currentTimeMillis() - startTime;
            HttpStatusCode statusCode = e.getStatusCode();
            String responseBody = e.getResponseBodyAsString();
            String errorMsg = String.format("HTTP错误: %d, %s, response: %s",
                statusCode.value(), e.getMessage(), responseBody);

            log.error("webhook投递失败, messageId: {}, costTime: {}ms, error: {}",
                messageId, costTime, errorMsg);

            // 发送失败结果
            sendDeliveryResult(messageId, false, errorMsg);

        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            String errorMsg = String.format("投递异常: %s", e.getMessage());

            log.error("webhook投递异常, messageId: {}, costTime: {}ms",
                messageId, costTime, e);

            // 发送失败结果
            sendDeliveryResult(messageId, false, errorMsg);
        }
    }

    /**
     * 发送投递结果到Kafka
     *
     * @param messageId 消息ID
     * @param success 是否成功
     * @param errorMessage 错误信息
     */
    private void sendDeliveryResult(String messageId, boolean success, String errorMessage) {
        Map<String, Object> result = new HashMap<>();
        result.put("messageId", messageId);
        result.put("success", success);
        result.put("errorMessage", errorMessage);
        result.put("channelCode", "webhook");
        result.put("timestamp", System.currentTimeMillis());

        String resultJson = JSON.toJSONString(result);
        deliveryResultProducer.sendDeliveryResult(messageId, resultJson);
    }
}
