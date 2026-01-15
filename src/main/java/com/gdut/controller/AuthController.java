package com.gdut.controller;

import com.gdut.entity.RegisterDTO;
import com.gdut.entity.Result;
import com.gdut.entity.SysUser;
import com.gdut.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        // 1. 校验用户名是否已存在
        boolean exists = sysUserService.existsByUsername(registerDTO.getUsername());
        if (exists) {
            return Result.failWithOnlyMsg("用户名已存在");
        }

        // 2. 密码加密
        String encryptPwd = passwordEncoder.encode(registerDTO.getPassword());

        // 3. 保存用户（默认分配USER角色）
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(encryptPwd);
        user.setNickname(registerDTO.getNickname());
        user.setStatus(1);
        sysUserService.saveUserWithRole(user, 1L);

        return Result.success("注册成功");
    }

    // 登录接口由JwtLoginFilter接管，无需手动实现
}
