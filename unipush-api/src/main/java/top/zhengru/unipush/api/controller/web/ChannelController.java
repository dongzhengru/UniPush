package top.zhengru.unipush.api.controller.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.zhengru.unipush.api.service.ChannelService;
import top.zhengru.unipush.api.util.JwtUtils;
import top.zhengru.unipush.common.model.vo.ChannelInfoVO;
import top.zhengru.unipush.common.model.vo.ResponseVO;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 推送渠道管理控制器
 *
 * @author zhengru
 */
@Tag(name = "推送渠道管理", description = "推送渠道的增删改查操作")
@RestController
@RequestMapping("/api/web/channel")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 查询渠道列表
     *
     * @return 渠道列表
     */
    @Operation(summary = "查询渠道列表", description = "获取所有推送渠道")
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
    @Operation(summary = "查询渠道详情", description = "根据ID获取渠道详细信息")
    @GetMapping("/{id}")
    public ResponseVO<ChannelInfoVO> getChannel(
            @Parameter(description = "渠道ID", required = true, example = "1")
            @PathVariable("id") Long id) {
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
    @Operation(summary = "添加渠道", description = "创建新的推送渠道")
    @PostMapping("/add")
    public ResponseVO<ChannelInfoVO> add(
            @Parameter(description = "渠道编码", required = true, example = "webhook")
            @RequestParam("channelCode") String channelCode,
            @Parameter(description = "渠道名称", required = true, example = "Webhook推送")
            @RequestParam("channelName") String channelName,
            @Parameter(description = "描述")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "渠道配置JSON", required = true, example = "{\"url\":\"https://example.com/webhook\"}")
            @RequestParam("config") String config,
            @Parameter(description = "优先级", example = "0")
            @RequestParam(value = "priority", defaultValue = "0") Integer priority,
            HttpServletRequest request) {
        // 从JWT Token中获取创建人ID
        Long creatorId = getUserIdFromToken(request);

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
    @Operation(summary = "更新渠道", description = "更新已有渠道的信息")
    @PutMapping("/update/{id}")
    public ResponseVO<ChannelInfoVO> update(
            @Parameter(description = "渠道ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "渠道名称", required = true)
            @RequestParam("channelName") String channelName,
            @Parameter(description = "描述")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "渠道配置JSON", required = true)
            @RequestParam("config") String config,
            @Parameter(description = "优先级")
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
    @Operation(summary = "删除渠道", description = "删除指定的推送渠道")
    @DeleteMapping("/delete/{id}")
    public ResponseVO<Void> delete(
            @Parameter(description = "渠道ID", required = true, example = "1")
            @PathVariable("id") Long id) {
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
    @Operation(summary = "更新渠道状态", description = "启用或禁用推送渠道")
    @PutMapping("/status/{id}")
    public ResponseVO<Void> updateStatus(
            @Parameter(description = "渠道ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "是否启用：1-启用 0-禁用", required = true, example = "1")
            @RequestParam("enabled") Integer enabled) {
        boolean success = channelService.updateChannelStatus(id, enabled);
        return success ? ResponseVO.ok(null, "更新成功") : ResponseVO.fail("渠道不存在");
    }

    /**
     * 从请求中获取用户ID
     *
     * @param request HTTP请求
     * @return 用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token != null && jwtUtils.validateToken(token)) {
                return jwtUtils.getUserIdFromToken(token);
            }
        } catch (Exception e) {
            // Token解析失败，返回默认用户ID
        }
        return 1L; // 默认返回系统管理员ID
    }

    /**
     * 从请求中提取Token
     *
     * @param request HTTP请求
     * @return Token字符串
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 也可以从查询参数中获取
        return request.getParameter("token");
    }
}
