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
@RequestMapping("/api/admin")
public class AdminController {
    // 仅管理员可访问
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public Result<?> adminDashboard() {
        return Result.success("管理员面板数据");
    }
}
