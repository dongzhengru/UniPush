package top.zhengru.unipush.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.zhengru.unipush.common.model.entity.SysUser;

/**
 * 系统用户Mapper
 *
 * @author zhengru
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
