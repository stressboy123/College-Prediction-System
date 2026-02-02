package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TProvince;
import com.gdut.mapper.TProvinceMapper;
import com.gdut.service.ITProvinceService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 省份表Service实现
 */
@Service
public class TProvinceServiceImpl extends ServiceImpl<TProvinceMapper, TProvince> implements ITProvinceService {}
