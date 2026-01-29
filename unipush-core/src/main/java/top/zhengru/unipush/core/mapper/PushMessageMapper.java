package top.zhengru.unipush.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.zhengru.unipush.common.model.entity.PushMessage;

/**
 * 推送消息Mapper
 *
 * @author zhengru
 */
@Mapper
public interface PushMessageMapper extends BaseMapper<PushMessage> {
    // MyBatis-Plus自动提供CRUD方法
}
