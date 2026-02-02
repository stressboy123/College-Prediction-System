package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TMajorSubject;
import com.gdut.mapper.TMajorSubjectMapper;
import com.gdut.service.ITMajorSubjectService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 各省专业选科要求表Service实现
 */
@Service
public class TMajorSubjectServiceImpl extends ServiceImpl<TMajorSubjectMapper, TMajorSubject> implements ITMajorSubjectService {}

