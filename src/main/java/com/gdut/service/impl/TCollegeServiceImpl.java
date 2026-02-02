package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TCollege;
import com.gdut.mapper.TCollegeMapper;
import com.gdut.service.ITCollegeService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 院校表Service实现
 */
@Service
public class TCollegeServiceImpl extends ServiceImpl<TCollegeMapper, TCollege> implements ITCollegeService {}

