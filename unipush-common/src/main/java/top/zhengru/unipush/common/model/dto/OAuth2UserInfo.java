package top.zhengru.unipush.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * OAuth2用户信息DTO
 *
 * @author zhengru
 */
@Data
public class OAuth2UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String sub;

    /**
     * 姓名
     */
    private String name;

    /**
     * 用户名
     */
    @JsonProperty("preferred_username")
    private String preferredUsername;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String picture;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱是否验证
     */
    @JsonProperty("email_verified")
    private Boolean emailVerified;

    /**
     * 手机号
     */
    @JsonProperty("phone_number")
    private String phoneNumber;

    /**
     * 是否管理员
     */
    @JsonProperty("is_admin")
    private Boolean isAdmin;

    /**
     * 是否超级管理员
     */
    @JsonProperty("is_super_admin")
    private Boolean isSuperAdmin;
}
