package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TUserInfo;
import com.gdut.mapper.TUserInfoMapper;
import com.gdut.service.ITUserInfoService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 用户信息表Service实现
 */
@Service
public class TUserInfoServiceImpl extends ServiceImpl<TUserInfoMapper, TUserInfo> implements ITUserInfoService {}

