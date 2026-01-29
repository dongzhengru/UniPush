package top.zhengru.unipush.api.controller.web;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import top.zhengru.unipush.common.model.dto.SendMessageDTO;
import top.zhengru.unipush.common.model.vo.ResponseVO;

/**
 * Web消息接口
 *
 * @author zhengru
 */
@RestController
@RequestMapping("/api/web/message")
public class WebMessageController {

    /**
     * 发送消息（Web接口）
     *
     * @param request 发送消息请求
     * @return 消息ID
     */
    @PostMapping("/send")
    public ResponseVO<String> send(@Valid @RequestBody SendMessageDTO request) {
        // TODO: 调用Core服务发送消息
        // Web接口与开放接口的区别在于：
        // 1. 使用OAuth2/JWT认证，而不是access-token
        // 2. 可以进行用户权限校验
        // 3. 可以记录操作日志

        String messageId = "web_message_id_123";
        return ResponseVO.ok(messageId, "发送成功");
    }

    /**
     * 批量发送消息（Web接口）
     *
     * @param request 批量发送消息请求
     * @return 批量发送结果
     */
    @PostMapping("/send/batch")
    public ResponseVO<Object> sendBatch(@Valid @RequestBody SendMessageDTO request) {
        // TODO: 调用Core服务批量发送消息
        return ResponseVO.ok("批量发送成功");
    }
}
