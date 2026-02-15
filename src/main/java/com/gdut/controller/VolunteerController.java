package com.gdut.controller;

import com.gdut.entity.UserInfoDTO;
import com.gdut.service.VolunteerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liujunliang
 * @date 2026/2/12
 */
@RestController
@RequestMapping("/excel")
public class VolunteerController {
    @Resource
    private VolunteerService volunteerService;

    @PostMapping("/predict")
    public void predict(@RequestBody UserInfoDTO dto) {
//        if (userInfoDTO == null) {
//            return Result.failWithOnlyMsg("用户信息无效");
//        }
//        if (userInfoDTO.getSysUserId() == null || userInfoDTO.getSysUserId() <= 0) {
//            return Result.failWithOnlyMsg("用户ID无效");
//        }
        volunteerService.predict(dto);
//        if (result != null) {
//            return Result.success(result);
//        } else {
//            return Result.failWithOnlyMsg("用户信息添加失败（省份/选科组合不存在）");
//        }
    }
}
