package com.gdut.controller;

import com.gdut.entity.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    // 普通用户/管理员可访问
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/profile")
    public Result<?> userProfile() {
        return Result.success("用户个人信息");
    }
}
