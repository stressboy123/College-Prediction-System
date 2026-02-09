package com.gdut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.entity.QuestionnaireAnswerSubmitDTO;
import com.gdut.entity.TQuestionnaireAnswer;
import com.gdut.entity.UserQuestionnaireAnswerDTO;

import java.util.List;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 问卷作答表Service
 */
public interface ITQuestionnaireAnswerService extends IService<TQuestionnaireAnswer> {
    /**
     * 批量保存用户问卷答案（先删除该用户已有答案，再批量插入）
     * @param submitDTO 提交的答案DTO
     * @return 是否保存成功
     */
    boolean batchSaveAnswer(QuestionnaireAnswerSubmitDTO submitDTO);

    /**
     * 根据用户ID查询已答问卷
     * @param userId  用户ID
     * @return 用户已答问卷列表
     */
    List<UserQuestionnaireAnswerDTO> getUserAnswerByUserId(Long userId);
}
