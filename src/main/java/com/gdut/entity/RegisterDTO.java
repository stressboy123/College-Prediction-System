package com.gdut.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度需在3-50之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在6-20之间")
    private String password;

    private String nickname;
}
