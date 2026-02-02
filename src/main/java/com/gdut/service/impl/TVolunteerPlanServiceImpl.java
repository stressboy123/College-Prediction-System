package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TVolunteerPlan;
import com.gdut.mapper.TVolunteerPlanMapper;
import com.gdut.service.ITVolunteerPlanService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 志愿方案主表Service实现
 */
@Service
public class TVolunteerPlanServiceImpl extends ServiceImpl<TVolunteerPlanMapper, TVolunteerPlan> implements ITVolunteerPlanService {}

