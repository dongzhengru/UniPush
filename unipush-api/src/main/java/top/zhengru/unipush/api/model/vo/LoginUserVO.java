package top.zhengru.unipush.api.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户信息VO
 *
 * @author zhengru
 */
@Data
public class LoginUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色：ADMIN-管理员 USER-普通用户
     */
    private String role;

    /**
     * JWT令牌
     */
    private String token;
}
