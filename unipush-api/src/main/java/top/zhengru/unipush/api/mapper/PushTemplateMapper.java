package top.zhengru.unipush.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.zhengru.unipush.common.model.entity.PushTemplate;

/**
 * 消息模板Mapper
 *
 * @author zhengru
 */
@Mapper
public interface PushTemplateMapper extends BaseMapper<PushTemplate> {
}
