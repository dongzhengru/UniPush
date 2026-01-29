package top.zhengru.unipush.webhook.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zhengru.unipush.webhook.mq.DeliveryResultProducer;
import top.zhengru.unipush.webhook.model.BarkRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Bark推送投递服务
 *
 * @author zhengru
 */
@Slf4j
@Service
public class BarkDeliveryService {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private DeliveryResultProducer deliveryResultProducer;

    private static final String BARK_API_BASE = "https://api.day.app";

    /**
     * 执行Bark推送投递
     *
     * @param messageId 消息ID
     * @param taskMessage 投递任务消息(JSON)
     */
    public void deliver(String messageId, String taskMessage) {
        long startTime = System.currentTimeMillis();

        // 1. 解析投递任务
        BarkRequest request = JSON.parseObject(taskMessage, BarkRequest.class);

        log.info("开始Bark推送投递, messageId: {}, key: {}", messageId, request.getBarkKey());

        // 2. 构造Bark API URL和查询参数
        String barkKey = request.getBarkKey();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BARK_API_BASE + "/" + barkKey).newBuilder();

        // 添加必需参数
        urlBuilder.addQueryParameter("title", request.getTitle() != null ? request.getTitle() : "");
        urlBuilder.addQueryParameter("body", request.getContent() != null ? request.getContent() : "");

        // 添加可选参数
        addOptionalParameter(urlBuilder, "sound", request.getSound());
        addOptionalParameter(urlBuilder, "icon", request.getIcon());
        addOptionalParameter(urlBuilder, "group", request.getGroup());
        addOptionalParameter(urlBuilder, "level", request.getLevel());
        addOptionalParameter(urlBuilder, "url", request.getUrl());
        addOptionalParameter(urlBuilder, "click", request.getClick());
        addOptionalParameter(urlBuilder, "autoCopy", request.getAutoCopy());
        addOptionalParameter(urlBuilder, "copy", request.getCopy());
        addOptionalParameter(urlBuilder, "badge", request.extractParam("badge"));
        addOptionalParameter(urlBuilder, "sound", request.extractParam("sound"));
        addOptionalParameter(urlBuilder, "isArchive", request.extractParam("isArchive"));
        addOptionalParameter(urlBuilder, "autoCancel", request.extractParam("autoCancel"));

        // 3. 构造GET请求
        Request httpRequest = new Request.Builder()
            .url(urlBuilder.build())
            .get()
            .build();

        // 4. 发送HTTP请求
        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            long costTime = System.currentTimeMillis() - startTime;

            if (response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "";

                // 5. 解析响应，判断是否成功
                boolean success = parseBarkResponse(responseBody);

                if (success) {
                    log.info("Bark推送投递成功, messageId: {}, costTime: {}ms, response: {}",
                        messageId, costTime, responseBody);
                    sendDeliveryResult(messageId, true, null);
                } else {
                    log.error("Bark推送投递失败, messageId: {}, costTime: {}ms, response: {}",
                        messageId, costTime, responseBody);
                    sendDeliveryResult(messageId, false, "Bark返回错误: " + responseBody);
                }
            } else {
                String errorMsg = String.format("HTTP错误: %d, %s",
                    response.code(), response.message());
                log.error("Bark推送投递失败, messageId: {}, costTime: {}ms, error: {}",
                    messageId, costTime, errorMsg);
                sendDeliveryResult(messageId, false, errorMsg);
            }

        } catch (IOException e) {
            long costTime = System.currentTimeMillis() - startTime;
            String errorMsg = String.format("投递异常: %s", e.getMessage());
            log.error("Bark推送投递异常, messageId: {}, costTime: {}ms",
                messageId, costTime, e);
            sendDeliveryResult(messageId, false, errorMsg);
        }
    }

    /**
     * 添加可选参数（非空时才添加）
     *
     * @param urlBuilder URL构建器
     * @param paramName 参数名
     * @param paramValue 参数值
     */
    private void addOptionalParameter(HttpUrl.Builder urlBuilder, String paramName, String paramValue) {
        if (paramValue != null && !paramValue.isEmpty()) {
            urlBuilder.addQueryParameter(paramName, paramValue);
        }
    }

    /**
     * 解析Bark响应
     * 成功响应: {"code":200,"message":"success"}
     *
     * @param response 响应JSON字符串
     * @return 是否成功
     */
    private boolean parseBarkResponse(String response) {
        try {
            JSONObject json = JSON.parseObject(response);
            Integer code = json.getInteger("code");
            return code != null && code == 200;
        } catch (Exception e) {
            log.error("解析Bark响应失败, response: {}", response, e);
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
        result.put("channelCode", "bark");
        result.put("timestamp", System.currentTimeMillis());

        String resultJson = JSON.toJSONString(result);
        deliveryResultProducer.sendDeliveryResult(messageId, resultJson);
    }
}
