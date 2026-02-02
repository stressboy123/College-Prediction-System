package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TSubjectCombination;
import com.gdut.mapper.TSubjectCombinationMapper;
import com.gdut.service.ITSubjectCombinationService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 选科组合表Service实现
 */
@Service
public class TSubjectCombinationServiceImpl extends ServiceImpl<TSubjectCombinationMapper, TSubjectCombination> implements ITSubjectCombinationService {}
