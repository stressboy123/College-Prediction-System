package com.gdut.entity;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
