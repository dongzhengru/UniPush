package top.zhengru.unipush.webhook.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zhengru.unipush.webhook.mq.DeliveryResultProducer;
import top.zhengru.unipush.webhook.model.DingTalkRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉机器人投递服务
 *
 * @author zhengru
 */
@Slf4j
@Service
public class DingTalkDeliveryService {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private DeliveryResultProducer deliveryResultProducer;

    public static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    /**
     * 执行钉钉机器人投递
     *
     * @param messageId 消息ID
     * @param taskMessage 投递任务消息(JSON)
     */
    public void deliver(String messageId, String taskMessage) {
        long startTime = System.currentTimeMillis();

        // 1. 解析投递任务
        DingTalkRequest request = JSON.parseObject(taskMessage, DingTalkRequest.class);

        log.info("开始钉钉机器人投递, messageId: {}, url: {}", messageId, request.getTargetUrl());

        // 2. 构造钉钉机器人请求体
        Map<String, Object> body = new HashMap<>();
        body.put("msgtype", request.getMsgType() != null ? request.getMsgType() : "text");

        // 构造文本内容
        Map<String, String> textContent = new HashMap<>();
        String content = buildContent(request.getTitle(), request.getContent());
        textContent.put("content", content);
        body.put("text", textContent);

        String jsonBody = JSON.toJSONString(body);

        // 3. 构造请求
        Request httpRequest = new Request.Builder()
            .url(request.getTargetUrl())
            .post(RequestBody.create(jsonBody, JSON_TYPE))
            .build();

        // 4. 发送HTTP请求
        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            long costTime = System.currentTimeMillis() - startTime;

            if (response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "";

                // 5. 解析响应，判断是否成功
                boolean success = parseDingTalkResponse(responseBody);

                if (success) {
                    log.info("钉钉机器人投递成功, messageId: {}, costTime: {}ms, response: {}",
                        messageId, costTime, responseBody);
                    sendDeliveryResult(messageId, true, null);
                } else {
                    log.error("钉钉机器人投递失败, messageId: {}, costTime: {}ms, response: {}",
                        messageId, costTime, responseBody);
                    sendDeliveryResult(messageId, false, "钉钉返回错误: " + responseBody);
                }
            } else {
                String errorMsg = String.format("HTTP错误: %d, %s",
                    response.code(), response.message());
                log.error("钉钉机器人投递失败, messageId: {}, costTime: {}ms, error: {}",
                    messageId, costTime, errorMsg);
                // 发送失败结果
                sendDeliveryResult(messageId, false, errorMsg);
            }

        } catch (IOException e) {
            long costTime = System.currentTimeMillis() - startTime;
            String errorMsg = String.format("投递异常: %s", e.getMessage());
            log.error("钉钉机器人投递异常, messageId: {}, costTime: {}ms",
                messageId, costTime, e);
            // 发送失败结果
            sendDeliveryResult(messageId, false, errorMsg);
        }
    }

    /**
     * 构建钉钉消息内容
     *
     * @param title 标题
     * @param content 内容
     * @return 完整消息内容
     */
    private String buildContent(String title, String content) {
        if (title != null && !title.isEmpty()) {
            return "【" + title + "】\n" + content;
        }
        return content;
    }

    /**
     * 解析钉钉机器人响应
     * 成功响应: {"errcode":0,"errmsg":"ok"}
     *
     * @param response 响应JSON字符串
     * @return 是否成功
     */
    private boolean parseDingTalkResponse(String response) {
        try {
            JSONObject json = JSON.parseObject(response);
            Integer errcode = json.getInteger("errcode");
            return errcode != null && errcode == 0;
        } catch (Exception e) {
            log.error("解析钉钉响应失败, response: {}", response, e);
            return false;
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
        result.put("channelCode", "dingtalk");
        result.put("timestamp", System.currentTimeMillis());

        String resultJson = JSON.toJSONString(result);
        deliveryResultProducer.sendDeliveryResult(messageId, resultJson);
    }
}
