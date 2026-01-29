package top.zhengru.unipush.api.controller.web;

import org.springframework.web.bind.annotation.*;
import top.zhengru.unipush.common.model.vo.ResponseVO;
import top.zhengru.unipush.common.model.vo.TemplateInfoVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息模板管理控制器
 *
 * @author zhengru
 */
@RestController
@RequestMapping("/api/web/template")
public class TemplateController {

    /**
     * 查询模板列表
     *
     * @return 模板列表
     */
    @GetMapping("/list")
    public ResponseVO<List<TemplateInfoVO>> list() {
        // TODO: 查询数据库获取模板列表
        List<TemplateInfoVO> templates = new ArrayList<>();
        TemplateInfoVO template = new TemplateInfoVO();
        template.setId(1L);
        template.setTemplateCode("server_warning_template");
        template.setTemplateName("服务器告警模板");
        template.setChannelCode("webhook");
        template.setTitle("系统告警通知");
        template.setContent("服务器 {{serverName}} 的 {{metric}} 超过 {{threshold}}%，请及时处理。");
        template.setStatus(1);
        template.setCreateTime(LocalDateTime.now());
        templates.add(template);
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
    @PostMapping("/add")
    public ResponseVO<Void> add(@RequestParam("templateCode") String templateCode,
                                 @RequestParam("templateName") String templateName,
                                 @RequestParam("channelCode") String channelCode,
                                 @RequestParam("title") String title,
                                 @RequestParam("content") String content,
                                 @RequestParam(value = "variables", required = false) String variables,
                                 @RequestParam(value = "description", required = false) String description) {
        // TODO: 保存模板到数据库
        return ResponseVO.ok(null, "添加成功");
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
    @PutMapping("/update/{id}")
    public ResponseVO<Void> update(@PathVariable("id") Long id,
                                    @RequestParam("templateName") String templateName,
                                    @RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam(value = "variables", required = false) String variables,
                                    @RequestParam(value = "description", required = false) String description) {
        // TODO: 更新模板信息
        return ResponseVO.ok(null, "更新成功");
    }

    /**
     * 删除模板
     *
     * @param id 模板ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public ResponseVO<Void> delete(@PathVariable("id") Long id) {
        // TODO: 从数据库删除模板
        return ResponseVO.ok(null, "删除成功");
    }

    /**
     * 启用/禁用模板
     *
     * @param id     模板ID
     * @param status 状态：1-启用 0-禁用
     * @return 操作结果
     */
    @PutMapping("/status/{id}")
    public ResponseVO<Void> updateStatus(@PathVariable("id") Long id,
                                          @RequestParam("status") Integer status) {
        // TODO: 更新模板状态
        return ResponseVO.ok(null, "更新成功");
    }
}
