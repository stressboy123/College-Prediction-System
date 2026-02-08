package com.gdut.controller;

import com.gdut.entity.LoginDTO;
import com.gdut.entity.LoginResponseVO;
import com.gdut.entity.RegisterDTO;
import com.gdut.entity.Result;
import com.gdut.service.SysUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private SysUserService sysUserService;

    // 登录接口（无需认证）
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return sysUserService.login(loginDTO);
    }

    // 注册接口（无需认证）
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return sysUserService.register(registerDTO);
    }

    // 新增：登出接口（POST请求，需携带Token）
    @PostMapping("/logout")
    public Result<String> logout() {
        return sysUserService.logout();
    }
}