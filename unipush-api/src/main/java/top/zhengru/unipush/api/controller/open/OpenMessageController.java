package top.zhengru.unipush.api.controller.open;

import com.alibaba.fastjson2.JSON;
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
@RestController
@RequestMapping("/api/open/message")
public class OpenMessageController {

    /**
     * 查询消息发送结果
     *
     * @param messageId 消息ID
     * @return 消息结果
     */
    @GetMapping("/sendMessageResult")
    public ResponseVO<MessageResultVO> sendMessageResult(@RequestParam("messageId") String messageId) {
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
    @PostMapping("/send")
    public ResponseVO<String> send(@Valid @RequestBody SendMessageDTO request) {
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
    @PostMapping("/send/batch")
    public ResponseVO<List<BatchSendResultItemVO>> sendBatch(@Valid @RequestBody BatchSendMessageDTO request) {
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
