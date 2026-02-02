package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TMajor;
import com.gdut.mapper.TMajorMapper;
import com.gdut.service.ITMajorService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 专业信息表Service实现
 */
@Service
public class TMajorServiceImpl extends ServiceImpl<TMajorMapper, TMajor> implements ITMajorService {}
