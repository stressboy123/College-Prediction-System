package com.gdut.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.QuestionnaireAnswerSubmitDTO;
import com.gdut.entity.TQuestionnaire;
import com.gdut.entity.TQuestionnaireAnswer;
import com.gdut.entity.UserQuestionnaireAnswerDTO;
import com.gdut.mapper.TQuestionnaireAnswerMapper;
import com.gdut.service.ITQuestionnaireAnswerService;
import com.gdut.service.ITQuestionnaireService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 问卷作答表Service实现
 */
@Service
public class TQuestionnaireAnswerServiceImpl extends ServiceImpl<TQuestionnaireAnswerMapper, TQuestionnaireAnswer> implements ITQuestionnaireAnswerService {
    @Resource
    private ITQuestionnaireService questionnaireService;

    /**
     * 批量保存答案（事务保证原子性）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveAnswer(QuestionnaireAnswerSubmitDTO submitDTO) {
        Long userId = submitDTO.getUserId();
        List<QuestionnaireAnswerSubmitDTO.AnswerItemDTO> answerList = submitDTO.getAnswerList();

        // 1. 先删除该用户已有的所有问卷答案（避免唯一索引冲突）
        LambdaQueryWrapper<TQuestionnaireAnswer> deleteWrapper = new LambdaQueryWrapper<TQuestionnaireAnswer>()
                .eq(TQuestionnaireAnswer::getUserId, userId);

        this.remove(deleteWrapper);

        // 2. 批量插入新答案
        if (CollectionUtils.isEmpty(answerList)) {
            return true;
        }
        List<TQuestionnaireAnswer> answerEntityList = new ArrayList<>();
        for (QuestionnaireAnswerSubmitDTO.AnswerItemDTO item : answerList) {
            TQuestionnaireAnswer answer = new TQuestionnaireAnswer();
            answer.setUserId(userId); // 核心修改：userInfoId→userId
            answer.setQuestionnaireId(item.getQuestionnaireId());
            answer.setAnswerContent(item.getAnswerContent());
            answerEntityList.add(answer);
        }

        try {
            return this.saveBatch(answerEntityList);
        } catch (DuplicateKeyException e) {
            // 捕获唯一索引冲突异常（兜底）
            throw new RuntimeException("该用户已提交过该问卷答案，请勿重复提交");
        }
    }

    @Override
    public List<UserQuestionnaireAnswerDTO> getUserAnswerByUserId(Long userId) {
        // 1. 查询该用户的所有答案
        LambdaQueryWrapper<TQuestionnaireAnswer> answerWrapper = new LambdaQueryWrapper<TQuestionnaireAnswer>()
                .eq(TQuestionnaireAnswer::getUserId, userId);

        List<TQuestionnaireAnswer> answerList = this.list(answerWrapper);
        if (CollectionUtils.isEmpty(answerList)) {
            return new ArrayList<>();
        }

        // 2. 关联查询问题信息，组装DTO
        List<UserQuestionnaireAnswerDTO> dtoList = new ArrayList<>();
        for (TQuestionnaireAnswer answer : answerList) {
            TQuestionnaire questionnaire = questionnaireService.getById(answer.getQuestionnaireId());
            if (questionnaire == null) {
                continue;
            }
            UserQuestionnaireAnswerDTO dto = new UserQuestionnaireAnswerDTO();
            dto.setQuestionnaireId(questionnaire.getId());
            dto.setQuestionTitle(questionnaire.getQuestionTitle());
            dto.setQuestionType(questionnaire.getQuestionType());
            dto.setAnswerContent(answer.getAnswerContent());
            dtoList.add(dto);
        }
        return dtoList;
    }
}

