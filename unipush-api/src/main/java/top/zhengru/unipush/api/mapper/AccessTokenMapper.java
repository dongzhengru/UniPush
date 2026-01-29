package top.zhengru.unipush.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.zhengru.unipush.common.model.entity.AccessToken;

/**
 * 访问令牌Mapper
 *
 * @author zhengru
 */
@Mapper
public interface AccessTokenMapper extends BaseMapper<AccessToken> {
}
