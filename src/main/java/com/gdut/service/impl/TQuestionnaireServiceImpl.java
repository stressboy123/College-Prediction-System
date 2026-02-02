package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TQuestionnaire;
import com.gdut.mapper.TQuestionnaireMapper;
import com.gdut.service.ITQuestionnaireService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 问卷问题库Service实现
 */
@Service
public class TQuestionnaireServiceImpl extends ServiceImpl<TQuestionnaireMapper, TQuestionnaire> implements ITQuestionnaireService {}

