package com.gdut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.entity.TUserInfo;
import com.gdut.entity.UserInfoDTO;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 用户信息表Service
 */
public interface ITUserInfoService extends IService<TUserInfo> {
    UserInfoDTO addUserInfo(UserInfoDTO userInfoDTO);

    UserInfoDTO updateUserInfo(UserInfoDTO userInfoDTO);

    UserInfoDTO getUserInfoByUserId(Long userId);
}
