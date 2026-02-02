package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TVolunteerPlanDetail;
import com.gdut.mapper.TVolunteerPlanDetailMapper;
import com.gdut.service.ITVolunteerPlanDetailService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 志愿方案详情表Service实现
 */
@Service
public class TVolunteerPlanDetailServiceImpl extends ServiceImpl<TVolunteerPlanDetailMapper, TVolunteerPlanDetail> implements ITVolunteerPlanDetailService {}
