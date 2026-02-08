package com.gdut.entity;

import lombok.Data;

import java.util.List;

/**
 * @author liujunliang
 * @date 2026/2/8
 * 登录返回结果VO
 */
@Data
public class LoginResponseVO {
    /** JWT令牌 */
    private String token;
    /** 用户名 */
    private String username;
    /** 昵称 */
    private String nickname;
    /** 角色列表 */
    private List<String> roles;
}
