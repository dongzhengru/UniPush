package top.zhengru.unipush.common.api;

import top.zhengru.unipush.common.model.dto.SendMessageDTO;
import top.zhengru.unipush.common.model.vo.MessageResultVO;

/**
 * Push核心服务接口(Dubbo)
 * API层通过Dubbo调用此接口
 *
 * @author zhengru
 */
public interface PushCoreService {

    /**
     * 创建推送任务
     *
     * @param request 发送请求
     * @return 消息ID
     */
    String createPushTask(SendMessageDTO request);

    /**
     * 查询消息发送结果
     *
     * @param messageId 消息ID
     * @return 消息结果
     */
    MessageResultVO getMessageResult(String messageId);

    /**
     * 处理投递结果(内部方法,被Kafka Consumer调用)
     *
     * @param resultMessage 投递结果消息
     */
    void handleDeliveryResult(String resultMessage);
}
