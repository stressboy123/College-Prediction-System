package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TEnrollmentPlan;
import com.gdut.mapper.TEnrollmentPlanMapper;
import com.gdut.service.ITEnrollmentPlanService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 各省招生计划表Service实现
 */
@Service
public class TEnrollmentPlanServiceImpl extends ServiceImpl<TEnrollmentPlanMapper, TEnrollmentPlan> implements ITEnrollmentPlanService {}

