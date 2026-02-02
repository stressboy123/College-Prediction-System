package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TQuestionnaireAnswer;
import com.gdut.mapper.TQuestionnaireAnswerMapper;
import com.gdut.service.ITQuestionnaireAnswerService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 问卷作答表Service实现
 */
@Service
public class TQuestionnaireAnswerServiceImpl extends ServiceImpl<TQuestionnaireAnswerMapper, TQuestionnaireAnswer> implements ITQuestionnaireAnswerService {}

