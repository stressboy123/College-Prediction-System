package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TAdmissionData;
import com.gdut.mapper.TAdmissionDataMapper;
import com.gdut.service.ITAdmissionDataService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 各省投档录取数据表Service实现
 */
@Service
public class TAdmissionDataServiceImpl extends ServiceImpl<TAdmissionDataMapper, TAdmissionData> implements ITAdmissionDataService {}

