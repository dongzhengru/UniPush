package top.zhengru.unipush.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zhengru.unipush.api.mapper.PushTemplateMapper;
import top.zhengru.unipush.common.model.entity.PushTemplate;
import top.zhengru.unipush.common.model.vo.TemplateInfoVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息模板服务
 *
 * @author zhengru
 */
@Service
public class TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    @Autowired
    private PushTemplateMapper pushTemplateMapper;

    /**
     * 查询所有模板
     *
     * @return 模板列表
     */
    public List<TemplateInfoVO> listTemplates() {
        LambdaQueryWrapper<PushTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PushTemplate::getCreateTime);
        List<PushTemplate> templates = pushTemplateMapper.selectList(wrapper);

        return templates.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 根据ID获取模板
     *
     * @param id 模板ID
     * @return 模板信息
     */
    public TemplateInfoVO getTemplateById(Long id) {
        PushTemplate template = pushTemplateMapper.selectById(id);
        return template != null ? convertToVO(template) : null;
    }

    /**
     * 根据模板编码获取模板
     *
     * @param templateCode 模板编码
     * @return 模板信息
     */
    public TemplateInfoVO getTemplateByCode(String templateCode) {
        LambdaQueryWrapper<PushTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTemplate::getTemplateCode, templateCode);
        PushTemplate template = pushTemplateMapper.selectOne(wrapper);
        return template != null ? convertToVO(template) : null;
    }

    /**
     * 创建模板
     *
     * @param templateCode 模板编码
     * @param templateName 模板名称
     * @param channelCode  渠道编码
     * @param title        标题模板
     * @param content      内容模板
     * @param variables    变量说明
     * @param description  描述
     * @param creatorId    创建人ID
     * @return 模板信息
     */
    public TemplateInfoVO createTemplate(String templateCode, String templateName, String channelCode,
                                         String title, String content, String variables,
                                         String description, Long creatorId) {
        PushTemplate template = new PushTemplate();
        template.setTemplateCode(templateCode);
        template.setTemplateName(templateName);
        template.setChannelCode(channelCode);
        template.setTitle(title);
        template.setContent(content);
        template.setVariables(variables);
        template.setDescription(description);
        template.setStatus(1);
        template.setCreatorId(creatorId);
        template.setCreateTime(LocalDateTime.now());

        pushTemplateMapper.insert(template);
        logger.info("创建模板成功: templateCode={}", templateCode);

        return convertToVO(template);
    }

    /**
     * 更新模板
     *
     * @param id           模板ID
     * @param templateName 模板名称
     * @param title        标题模板
     * @param content      内容模板
     * @param variables    变量说明
     * @param description  描述
     * @return 模板信息
     */
    public TemplateInfoVO updateTemplate(Long id, String templateName, String title,
                                         String content, String variables, String description) {
        PushTemplate template = new PushTemplate();
        template.setId(id);
        template.setTemplateName(templateName);
        template.setTitle(title);
        template.setContent(content);
        template.setVariables(variables);
        template.setDescription(description);

        pushTemplateMapper.updateById(template);
        logger.info("更新模板成功: id={}", id);

        return getTemplateById(id);
    }

    /**
     * 删除模板
     *
     * @param id 模板ID
     * @return 是否成功
     */
    public boolean deleteTemplate(Long id) {
        int rows = pushTemplateMapper.deleteById(id);
        logger.info("删除模板成功: id={}", id);
        return rows > 0;
    }

    /**
     * 更新模板状态
     *
     * @param id     模板ID
     * @param status 状态：1-启用 0-禁用
     * @return 是否成功
     */
    public boolean updateTemplateStatus(Long id, Integer status) {
        PushTemplate template = new PushTemplate();
        template.setId(id);
        template.setStatus(status);

        int rows = pushTemplateMapper.updateById(template);
        logger.info("更新模板状态: id={}, status={}", id, status);

        return rows > 0;
    }

    /**
     * 转换为VO
     *
     * @param template 模板实体
     * @return 模板VO
     */
    private TemplateInfoVO convertToVO(PushTemplate template) {
        TemplateInfoVO vo = new TemplateInfoVO();
        vo.setId(template.getId());
        vo.setTemplateCode(template.getTemplateCode());
        vo.setTemplateName(template.getTemplateName());
        vo.setChannelCode(template.getChannelCode());
        vo.setTitle(template.getTitle());
        vo.setContent(template.getContent());
        vo.setVariables(template.getVariables());
        vo.setDescription(template.getDescription());
        vo.setStatus(template.getStatus());
        vo.setCreateTime(template.getCreateTime());
        return vo;
    }
}
