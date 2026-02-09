package com.gdut.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.QuestionTypeEnum;
import com.gdut.entity.QuestionnaireAddDTO;
import com.gdut.entity.QuestionnaireQuestionDTO;
import com.gdut.entity.QuestionnaireUpdateDTO;
import com.gdut.entity.TQuestionnaire;
import com.gdut.mapper.TQuestionnaireMapper;
import com.gdut.service.ITQuestionnaireService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 问卷问题库Service实现
 */
@Service
public class TQuestionnaireServiceImpl extends ServiceImpl<TQuestionnaireMapper, TQuestionnaire> implements ITQuestionnaireService {
    @Override
    public List<QuestionnaireQuestionDTO> getSortedQuestionnaireList() {
        // 1. 查询所有问卷问题，按sort升序排列
        LambdaQueryWrapper<TQuestionnaire> queryWrapper = new LambdaQueryWrapper<TQuestionnaire>()
                .orderByAsc(TQuestionnaire::getSort);
        List<TQuestionnaire> questionnaireList = this.list(queryWrapper);

        // 2. 转换为DTO（适配前端展示）
        List<QuestionnaireQuestionDTO> dtoList = new ArrayList<>();
        for (TQuestionnaire questionnaire : questionnaireList) {
            QuestionnaireQuestionDTO dto = new QuestionnaireQuestionDTO();
            dto.setId(questionnaire.getId());
            dto.setQuestionTitle(questionnaire.getQuestionTitle());
            dto.setQuestionType(questionnaire.getQuestionType());
            // 转换问题类型编码为名称（如single_choice → 单选）
            QuestionTypeEnum typeEnum = QuestionTypeEnum.getByCode(questionnaire.getQuestionType());
            dto.setQuestionTypeName(typeEnum != null ? typeEnum.getName() : "未知类型");
            // 拆分选项字符串为列表（逗号分隔）
            if (StringUtils.hasText(questionnaire.getOptions())) {
                List<String> options = Arrays.stream(questionnaire.getOptions().split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
                dto.setOptions(options);
            }
            dto.setSort(questionnaire.getSort());
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public boolean addQuestionnaire(QuestionnaireAddDTO addDTO) {
        // 1. 校验问题类型合法性
        QuestionTypeEnum typeEnum = QuestionTypeEnum.getByCode(addDTO.getQuestionType());
        if (typeEnum == null) {
            throw new RuntimeException("问题类型不合法，仅支持：single_choice(单选)、multiple_choice(多选)、fill_blank(填空)");
        }

        // 2. 校验单选/多选必须传选项，填空不能传选项
        if ((typeEnum == QuestionTypeEnum.SINGLE_CHOICE || typeEnum == QuestionTypeEnum.MULTIPLE_CHOICE)
                && !StringUtils.hasText(addDTO.getOptions())) {
            throw new RuntimeException("单选/多选问题必须填写选项（逗号分隔）");
        }
        if (typeEnum == QuestionTypeEnum.FILL_BLANK && StringUtils.hasText(addDTO.getOptions())) {
            throw new RuntimeException("填空问题无需填写选项");
        }

        // 3. 转换DTO为实体类并保存
        TQuestionnaire questionnaire = new TQuestionnaire();
        BeanUtils.copyProperties(addDTO, questionnaire);
        return this.save(questionnaire);
    }

    @Override
    public boolean updateQuestionnaire(QuestionnaireUpdateDTO updateDTO) {
        // 1. 校验问题是否存在
        Integer questionId = updateDTO.getId();
        TQuestionnaire existQuestion = this.getById(questionId);
        if (existQuestion == null) {
            throw new RuntimeException("待修改的问卷问题不存在");
        }

        // 2. 若传了问题类型，校验类型合法性
        if (StringUtils.hasText(updateDTO.getQuestionType())) {
            QuestionTypeEnum typeEnum = QuestionTypeEnum.getByCode(updateDTO.getQuestionType());
            if (typeEnum == null) {
                throw new RuntimeException("问题类型不合法，仅支持：single_choice(单选)、multiple_choice(多选)、fill_blank(填空)");
            }
            // 校验选项规则（单选/多选需传选项，填空不能传选项）
            if ((typeEnum == QuestionTypeEnum.SINGLE_CHOICE || typeEnum == QuestionTypeEnum.MULTIPLE_CHOICE)
                    && !StringUtils.hasText(updateDTO.getOptions())) {
                throw new RuntimeException("单选/多选问题必须填写选项（逗号分隔）");
            }
            if (typeEnum == QuestionTypeEnum.FILL_BLANK && StringUtils.hasText(updateDTO.getOptions())) {
                throw new RuntimeException("填空问题无需填写选项");
            }
        }

        // 3. 构建更新条件（仅更新传了值的字段）
        LambdaUpdateWrapper<TQuestionnaire> updateWrapper = new LambdaUpdateWrapper<TQuestionnaire>()
                .eq(TQuestionnaire::getId, questionId);

        if (StringUtils.hasText(updateDTO.getQuestionTitle())) {
            updateWrapper.set(TQuestionnaire::getQuestionTitle, updateDTO.getQuestionTitle());
        }
        if (StringUtils.hasText(updateDTO.getQuestionType())) {
            updateWrapper.set(TQuestionnaire::getQuestionType, updateDTO.getQuestionType());
        }
        if (updateDTO.getOptions() != null) { // 允许传空（填空问题）
            updateWrapper.set(TQuestionnaire::getOptions, updateDTO.getOptions());
        }
        if (updateDTO.getSort() != null) {
            updateWrapper.set(TQuestionnaire::getSort, updateDTO.getSort());
        }

        // 4. 执行更新
        return this.update(updateWrapper);
    }

    @Override
    public boolean deleteQuestionnaire(Integer id) {
        // 1. 校验问题是否存在
        TQuestionnaire existQuestion = this.getById(id);
        if (existQuestion == null) {
            throw new RuntimeException("待删除的问卷问题不存在");
        }

        // 2. 执行删除（物理删除，若需逻辑删除可改为更新del_flag字段）
        return this.removeById(id);
    }
}

