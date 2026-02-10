package com.gdut.controller;

import com.gdut.entity.Result;
import com.gdut.entity.UserInfoDTO;
import com.gdut.service.ITUserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liujunliang
 * @date 2026/2/10
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private ITUserInfoService userInfoService;

    /**
     * 新增用户信息（存在则更新，返回最新数据）
     */
    @PostMapping("/add")
    public Result<UserInfoDTO> addUserInfo(@RequestBody UserInfoDTO userInfoDTO) {
        if (userInfoDTO == null) {
            return Result.failWithOnlyMsg("用户信息无效");
        }
        if (userInfoDTO.getSysUserId() == null || userInfoDTO.getSysUserId() <= 0) {
            return Result.failWithOnlyMsg("用户ID无效");
        }
        UserInfoDTO result = userInfoService.addUserInfo(userInfoDTO); // 改为返回DTO
        if (result != null) {
            return Result.success(result);
        } else {
            return Result.failWithOnlyMsg("用户信息添加失败（省份/选科组合不存在）");
        }
    }

    /**
     * 更新用户信息（返回最新数据）
     */
    @PutMapping("/update")
    public Result<UserInfoDTO> updateUserInfo(@RequestBody UserInfoDTO userInfoDTO) {
        if (userInfoDTO == null) {
            return Result.failWithOnlyMsg("用户信息无效");
        }
        if (userInfoDTO.getSysUserId() == null || userInfoDTO.getSysUserId() <= 0) {
            return Result.failWithOnlyMsg("用户ID无效");
        }
        UserInfoDTO userInfo = userInfoService.updateUserInfo(userInfoDTO);
        if (userInfo != null) {
            return Result.success(userInfo);
        } else {
            return Result.failWithOnlyMsg("用户信息更新失败（用户不存在/省份/选科组合错误）");
        }
    }

    /**
     * 查询用户信息（兜底暂无信息）
     */
    @GetMapping("/userInfo/{userId}")
    public Result<UserInfoDTO> getUserInfo(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            return Result.failWithOnlyMsg("用户ID无效");
        }
        UserInfoDTO userInfo = userInfoService.getUserInfoByUserId(userId);
        // 查不到时返回空DTO，前端统一展示“暂无信息”
        if (userInfo == null) {
            UserInfoDTO emptyDTO = new UserInfoDTO();
            emptyDTO.setSysUserId(userId);
            emptyDTO.setCandidateProvince("暂无信息");
            emptyDTO.setFirstSubject("暂无信息");
            emptyDTO.setSecondSubject("暂无信息");
            emptyDTO.setRealName("暂无信息");
            emptyDTO.setPhone("暂无信息");
            return Result.success(emptyDTO);
        }
        return Result.success(userInfo);
    }
}
