package top.zhengru.unipush.api.controller.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.zhengru.unipush.api.service.TemplateService;
import top.zhengru.unipush.api.util.JwtUtils;
import top.zhengru.unipush.common.model.vo.ResponseVO;
import top.zhengru.unipush.common.model.vo.TemplateInfoVO;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 消息模板管理控制器
 *
 * @author zhengru
 */
@Tag(name = "消息模板管理", description = "消息模板的增删改查操作")
@RestController
@RequestMapping("/api/web/template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 查询模板列表
     *
     * @return 模板列表
     */
    @Operation(summary = "查询模板列表", description = "获取所有消息模板")
    @GetMapping("/list")
    public ResponseVO<List<TemplateInfoVO>> list() {
        List<TemplateInfoVO> templates = templateService.listTemplates();
        return ResponseVO.ok(templates);
    }

    /**
     * 添加模板
     *
     * @param templateCode 模板编码
     * @param templateName 模板名称
     * @param channelCode  渠道编码
     * @param title        标题模板
     * @param content      内容模板
     * @param variables    变量说明（JSON）
     * @param description  描述
     * @return 操作结果
     */
    @Operation(summary = "添加模板", description = "创建新的消息模板")
    @PostMapping("/add")
    public ResponseVO<TemplateInfoVO> add(
            @Parameter(description = "模板编码", required = true, example = "server_warning_template")
            @RequestParam("templateCode") String templateCode,
            @Parameter(description = "模板名称", required = true, example = "服务器告警模板")
            @RequestParam("templateName") String templateName,
            @Parameter(description = "渠道编码", required = true, example = "webhook")
            @RequestParam("channelCode") String channelCode,
            @Parameter(description = "标题模板", required = true, example = "系统告警通知")
            @RequestParam("title") String title,
            @Parameter(description = "内容模板", required = true, example = "服务器 {{serverName}} 的 {{metric}} 超过 {{threshold}}%")
            @RequestParam("content") String content,
            @Parameter(description = "变量说明JSON", required = false)
            @RequestParam(value = "variables", required = false) String variables,
            @Parameter(description = "描述", required = false)
            @RequestParam(value = "description", required = false) String description,
            HttpServletRequest request) {
        // 从JWT Token中获取创建人ID
        Long creatorId = getUserIdFromToken(request);

        TemplateInfoVO template = templateService.createTemplate(
                templateCode, templateName, channelCode, title, content,
                variables, description, creatorId);
        return ResponseVO.ok(template, "添加成功");
    }

    /**
     * 更新模板
     *
     * @param id           模板ID
     * @param templateName 模板名称
     * @param title        标题模板
     * @param content      内容模板
     * @param variables    变量说明（JSON）
     * @param description  描述
     * @return 操作结果
     */
    @Operation(summary = "更新模板", description = "更新已有模板的信息")
    @PutMapping("/update/{id}")
    public ResponseVO<TemplateInfoVO> update(
            @Parameter(description = "模板ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "模板名称", required = true)
            @RequestParam("templateName") String templateName,
            @Parameter(description = "标题模板", required = true)
            @RequestParam("title") String title,
            @Parameter(description = "内容模板", required = true)
            @RequestParam("content") String content,
            @Parameter(description = "变量说明JSON")
            @RequestParam(value = "variables", required = false) String variables,
            @Parameter(description = "描述")
            @RequestParam(value = "description", required = false) String description) {
        TemplateInfoVO template = templateService.updateTemplate(
                id, templateName, title, content, variables, description);
        return ResponseVO.ok(template, "更新成功");
    }

    /**
     * 删除模板
     *
     * @param id 模板ID
     * @return 操作结果
     */
    @Operation(summary = "删除模板", description = "删除指定的消息模板")
    @DeleteMapping("/delete/{id}")
    public ResponseVO<Void> delete(
            @Parameter(description = "模板ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        boolean success = templateService.deleteTemplate(id);
        return success ? ResponseVO.ok(null, "删除成功") : ResponseVO.fail("模板不存在");
    }

    /**
     * 启用/禁用模板
     *
     * @param id     模板ID
     * @param status 状态：1-启用 0-禁用
     * @return 操作结果
     */
    @Operation(summary = "更新模板状态", description = "启用或禁用消息模板")
    @PutMapping("/status/{id}")
    public ResponseVO<Void> updateStatus(
            @Parameter(description = "模板ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "状态：1-启用 0-禁用", required = true, example = "1")
            @RequestParam("status") Integer status) {
        boolean success = templateService.updateTemplateStatus(id, status);
        return success ? ResponseVO.ok(null, "更新成功") : ResponseVO.fail("模板不存在");
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
