package top.zhengru.unipush.api.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.zhengru.unipush.api.service.ChannelService;
import top.zhengru.unipush.common.model.vo.ChannelInfoVO;
import top.zhengru.unipush.common.model.vo.ResponseVO;

import java.util.List;

/**
 * 推送渠道管理控制器
 *
 * @author zhengru
 */
@RestController
@RequestMapping("/api/web/channel")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    /**
     * 查询渠道列表
     *
     * @return 渠道列表
     */
    @GetMapping("/list")
    public ResponseVO<List<ChannelInfoVO>> list() {
        List<ChannelInfoVO> channels = channelService.listChannels();
        return ResponseVO.ok(channels);
    }

    /**
     * 根据ID查询渠道
     *
     * @param id 渠道ID
     * @return 渠道信息
     */
    @GetMapping("/{id}")
    public ResponseVO<ChannelInfoVO> getChannel(@PathVariable("id") Long id) {
        ChannelInfoVO channel = channelService.getChannelById(id);
        return ResponseVO.ok(channel);
    }

    /**
     * 添加渠道
     *
     * @param channelCode  渠道编码
     * @param channelName  渠道名称
     * @param description  描述
     * @param config       配置（JSON）
     * @param priority     优先级
     * @return 操作结果
     */
    @PostMapping("/add")
    public ResponseVO<ChannelInfoVO> add(@RequestParam("channelCode") String channelCode,
                                          @RequestParam("channelName") String channelName,
                                          @RequestParam(value = "description", required = false) String description,
                                          @RequestParam("config") String config,
                                          @RequestParam(value = "priority", defaultValue = "0") Integer priority) {
        // TODO: 从JWT Token中获取创建人ID
        Long creatorId = 1L;

        ChannelInfoVO channel = channelService.createChannel(channelCode, channelName, description, config, priority, creatorId);
        return ResponseVO.ok(channel, "添加成功");
    }

    /**
     * 更新渠道
     *
     * @param id           渠道ID
     * @param channelName  渠道名称
     * @param description  描述
     * @param config       配置（JSON）
     * @param priority     优先级
     * @return 操作结果
     */
    @PutMapping("/update/{id}")
    public ResponseVO<ChannelInfoVO> update(@PathVariable("id") Long id,
                                             @RequestParam("channelName") String channelName,
                                             @RequestParam(value = "description", required = false) String description,
                                             @RequestParam("config") String config,
                                             @RequestParam(value = "priority", required = false) Integer priority) {
        ChannelInfoVO channel = channelService.updateChannel(id, channelName, description, config, priority);
        return ResponseVO.ok(channel, "更新成功");
    }

    /**
     * 删除渠道
     *
     * @param id 渠道ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public ResponseVO<Void> delete(@PathVariable("id") Long id) {
        boolean success = channelService.deleteChannel(id);
        return success ? ResponseVO.ok(null, "删除成功") : ResponseVO.fail("渠道不存在");
    }

    /**
     * 启用/禁用渠道
     *
     * @param id      渠道ID
     * @param enabled 是否启用：1-启用 0-禁用
     * @return 操作结果
     */
    @PutMapping("/status/{id}")
    public ResponseVO<Void> updateStatus(@PathVariable("id") Long id,
                                          @RequestParam("enabled") Integer enabled) {
        boolean success = channelService.updateChannelStatus(id, enabled);
        return success ? ResponseVO.ok(null, "更新成功") : ResponseVO.fail("渠道不存在");
    }
}
