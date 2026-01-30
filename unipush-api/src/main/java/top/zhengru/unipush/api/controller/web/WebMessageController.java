package top.zhengru.unipush.api.controller.web;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import top.zhengru.unipush.common.api.PushCoreService;
import top.zhengru.unipush.common.enums.ResponseCode;
import top.zhengru.unipush.common.exception.BusinessException;
import top.zhengru.unipush.common.model.dto.BatchSendMessageDTO;
import top.zhengru.unipush.common.model.dto.SendMessageDTO;
import top.zhengru.unipush.common.model.vo.BatchSendResultItemVO;
import top.zhengru.unipush.common.model.vo.MessageResultVO;
import top.zhengru.unipush.common.model.vo.ResponseVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Web消息接口
 *
 * @author zhengru
 */
@Tag(name = "Web消息管理", description = "需要JWT认证的消息发送接口")
@RestController
@RequestMapping("/api/web/message")
public class WebMessageController {

    @DubboReference
    private PushCoreService pushCoreService;

    /**
     * 查询消息发送结果
     *
     * @param messageId 消息ID
     * @return 消息结果
     */
    @Operation(summary = "查询消息发送结果", description = "根据消息ID查询消息的发送状态和结果")
    @GetMapping("/sendMessageResult")
    public ResponseVO<MessageResultVO> sendMessageResult(
            @Parameter(description = "消息ID", required = true, example = "1234567890")
            @RequestParam("messageId") String messageId) {
        // 调用Core服务查询消息结果
        MessageResultVO result = pushCoreService.getMessageResult(messageId);
        return ResponseVO.ok(result);
    }

    /**
     * 发送消息（Web接口）
     *
     * @param request 发送消息请求
     * @return 消息ID
     */
    @Operation(summary = "发送消息", description = "通过JWT认证发送单条消息")
    @PostMapping("/send")
    @SentinelResource(value = "web-send-message",
            blockHandler = "sendBlockHandler",
            fallback = "sendFallback")
    public ResponseVO<String> send(
            @Parameter(description = "发送消息请求参数", required = true)
            @Valid @RequestBody SendMessageDTO request) {
        // 1. 验证时间戳
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - request.getTimestamp()) > 300000) { // 5分钟
            throw new BusinessException(ResponseCode.VALIDATION_ERROR, "请求时间戳过期");
        }

        // 2. 调用Core服务创建推送任务
        String messageId = pushCoreService.createPushTask(request);

        // 3. 返回消息ID
        return ResponseVO.ok(messageId, "请求成功，请用messageId查询最终发送结果");
    }

    /**
     * 发送消息限流降级处理
     */
    public ResponseVO<String> sendBlockHandler(SendMessageDTO request, BlockException exception) {
        return ResponseVO.error(ResponseCode.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试");
    }

    /**
     * 发送消息异常降级处理
     */
    public ResponseVO<String> sendFallback(SendMessageDTO request, Throwable throwable) {
        return ResponseVO.error(ResponseCode.SERVICE_ERROR, "服务暂时不可用，请稍后再试");
    }

    /**
     * 批量发送消息（Web接口）
     *
     * @param request 批量发送消息请求
     * @return 批量发送结果
     */
    @Operation(summary = "批量发送消息", description = "通过JWT认证批量发送消息")
    @PostMapping("/send/batch")
    @SentinelResource(value = "web-send-batch-message",
            blockHandler = "sendBatchBlockHandler",
            fallback = "sendBatchFallback")
    public ResponseVO<List<BatchSendResultItemVO>> sendBatch(
            @Parameter(description = "批量发送消息请求参数", required = true)
            @Valid @RequestBody BatchSendMessageDTO request) {
        // 1. 验证时间戳
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - request.getTimestamp()) > 300000) { // 5分钟
            throw new BusinessException(ResponseCode.VALIDATION_ERROR, "请求时间戳过期");
        }

        // 2. 遍历渠道，依次调用Core服务创建推送任务
        List<BatchSendResultItemVO> results = new ArrayList<>();

        for (String channelStr : request.getChannel()) {
            try {
                // 为每个渠道创建SendMessageDTO
                SendMessageDTO sendRequest = new SendMessageDTO();
                sendRequest.setChannel(channelStr);
                sendRequest.setTitle(request.getTitle());
                sendRequest.setContent(request.getContent());
                // target需要类型转换
                if (request.getTarget() instanceof Map) {
                    sendRequest.setTarget((Map<String, Object>) request.getTarget());
                }
                sendRequest.setTemplate(request.getTemplate());
                sendRequest.setTopic(request.getTopic());
                sendRequest.setCallbackUrl(request.getCallbackUrl());
                sendRequest.setTimestamp(request.getTimestamp());

                // 调用Core服务
                String messageId = pushCoreService.createPushTask(sendRequest);

                // 创建成功结果
                BatchSendResultItemVO item = new BatchSendResultItemVO();
                item.setMessageId(messageId);
                item.setChannel(channelStr);
                item.setCode(200);
                item.setMsg("请求成功，请用messageId查询最终发送结果");
                results.add(item);

            } catch (Exception e) {
                // 创建失败结果
                BatchSendResultItemVO item = new BatchSendResultItemVO();
                item.setChannel(channelStr);
                item.setCode(500);
                item.setMsg("发送失败: " + e.getMessage());
                results.add(item);
            }
        }

        return ResponseVO.ok(results, "执行成功");
    }

    /**
     * 批量发送消息限流降级处理
     */
    public ResponseVO<List<BatchSendResultItemVO>> sendBatchBlockHandler(
            BatchSendMessageDTO request, BlockException exception) {
        return ResponseVO.error(ResponseCode.TOO_MANY_REQUESTS, "批量发送请求过于频繁，请稍后再试");
    }

    /**
     * 批量发送消息异常降级处理
     */
    public ResponseVO<List<BatchSendResultItemVO>> sendBatchFallback(
            BatchSendMessageDTO request, Throwable throwable) {
        return ResponseVO.error(ResponseCode.SERVICE_ERROR, "批量发送服务暂时不可用，请稍后再试");
    }
}
