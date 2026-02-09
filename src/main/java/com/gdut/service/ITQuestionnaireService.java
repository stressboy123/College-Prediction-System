package com.gdut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.entity.QuestionnaireAddDTO;
import com.gdut.entity.QuestionnaireQuestionDTO;
import com.gdut.entity.QuestionnaireUpdateDTO;
import com.gdut.entity.TQuestionnaire;

import java.util.List;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 问卷问题库Service
 */
public interface ITQuestionnaireService extends IService<TQuestionnaire> {
    /**
     * 获取排序后的问卷列表（前端展示用）
     * @return 问卷问题DTO列表
     */
    List<QuestionnaireQuestionDTO> getSortedQuestionnaireList();

    /**
     * 新增问卷问题
     * @param addDTO 问卷问题
     * @return 是否添加成功
     */
    boolean addQuestionnaire(QuestionnaireAddDTO addDTO);

    /**
     * 修改问卷问题
     * @param updateDTO 要删除的问卷问题
     * @return 是否删除成功
     */
    boolean updateQuestionnaire(QuestionnaireUpdateDTO updateDTO);

    /**
     * 删除问卷问题
     * @param id 问卷id
     * @return 是否修改成功
     */
    boolean deleteQuestionnaire(Integer id);
}
