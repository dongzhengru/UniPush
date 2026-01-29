package top.zhengru.unipush.api.controller.open;

import com.alibaba.fastjson2.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.zhengru.unipush.common.enums.ResponseCode;
import top.zhengru.unipush.common.exception.BusinessException;
import top.zhengru.unipush.common.model.dto.BatchSendMessageDTO;
import top.zhengru.unipush.common.model.dto.SendMessageDTO;
import top.zhengru.unipush.common.model.vo.BatchSendResultItemVO;
import top.zhengru.unipush.common.model.vo.MessageResultVO;
import top.zhengru.unipush.common.model.vo.ResponseVO;
import top.zhengru.unipush.common.util.JsonUtils;

import java.util.List;

/**
 * 开放消息接口
 *
 * @author zhengru
 */
@Tag(name = "消息发送", description = "开放消息发送接口，通过AccessToken认证")
@RestController
@RequestMapping("/api/open/message")
public class OpenMessageController {

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
        // TODO: 调用Core服务查询消息结果
        MessageResultVO result = new MessageResultVO();
        result.setStatus(2);
        result.setErrorMessage("");
        return ResponseVO.ok(result);
    }

    /**
     * 发送消息
     *
     * @param request 发送消息请求
     * @return 消息ID
     */
    @Operation(summary = "发送单条消息", description = "通过指定渠道发送单条消息")
    @PostMapping("/send")
    public ResponseVO<String> send(
            @Parameter(description = "发送消息请求参数", required = true)
            @Valid @RequestBody SendMessageDTO request) {
        // TODO: 调用Core服务发送消息
        // 1. 验证时间戳
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - request.getTimestamp()) > 300000) { // 5分钟
            throw new BusinessException(ResponseCode.VALIDATION_ERROR, "请求时间戳过期");
        }

        // 2. 调用Core服务创建推送任务
        // String messageId = coreService.createPushTask(request);

        // 3. 返回消息ID（模拟）
        String messageId = "075074e3c17e449e9a0cb79cc6f3fc83";
        return ResponseVO.ok(messageId, "请求成功，请用messageId查询最终发送结果");
    }

    /**
     * 多渠道发送消息
     *
     * @param request 批量发送消息请求
     * @return 批量发送结果列表
     */
    @Operation(summary = "批量发送消息", description = "通过多个渠道同时发送消息")
    @PostMapping("/send/batch")
    public ResponseVO<List<BatchSendResultItemVO>> sendBatch(
            @Parameter(description = "批量发送消息请求参数", required = true)
            @Valid @RequestBody BatchSendMessageDTO request) {
        // TODO: 调用Core服务批量发送消息
        // 1. 验证时间戳
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - request.getTimestamp()) > 300000) { // 5分钟
            throw new BusinessException(ResponseCode.VALIDATION_ERROR, "请求时间戳过期");
        }

        // 2. 遍历渠道，依次调用Core服务创建推送任务
        List<BatchSendResultItemVO> results = List.of(
                createResultItem("f9117123dc31434fa38917b7e4c6c3ff", "dingtalk"),
                createResultItem("39821494381133a3a19a5bcd3c2bf0d7", "webhook"),
                createResultItem("d12c767e882922eeaa17e70e0e1cfb15", "sms")
        );

        return ResponseVO.ok(results, "执行成功");
    }

    /**
     * 创建批量发送结果项（模拟）
     *
     * @param messageId 消息ID
     * @param channel   渠道
     * @return 结果项
     */
    private BatchSendResultItemVO createResultItem(String messageId, String channel) {
        BatchSendResultItemVO item = new BatchSendResultItemVO();
        item.setMessageId(messageId);
        item.setMsg("请求成功，请用messageId查询最终发送结果");
        item.setCode(200);
        item.setChannel(channel);
        return item;
    }
}
