package top.zhengru.unipush.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.zhengru.unipush.common.model.entity.PushChannel;

/**
 * 推送渠道Mapper
 *
 * @author zhengru
 */
@Mapper
public interface PushChannelMapper extends BaseMapper<PushChannel> {
    // MyBatis-Plus自动提供CRUD方法
}
