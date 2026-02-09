package com.gdut.controller;

import com.gdut.entity.QuestionnaireAddDTO;
import com.gdut.entity.QuestionnaireAnswerSubmitDTO;
import com.gdut.entity.QuestionnaireQuestionDTO;
import com.gdut.entity.QuestionnaireUpdateDTO;
import com.gdut.entity.Result;
import com.gdut.entity.UserQuestionnaireAnswerDTO;
import com.gdut.service.ITQuestionnaireAnswerService;
import com.gdut.service.ITQuestionnaireService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liujunliang
 * @date 2026/2/9
 */
@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {
    @Resource
    private ITQuestionnaireService questionnaireService;

    @Resource
    private ITQuestionnaireAnswerService questionnaireAnswerService;

    /**
     * 获取问卷列表（前端展示问卷用）
     * 请求方式：GET
     * 请求路径：/api/questionnaire/list
     * 返回：排序后的问卷问题列表
     */
    @GetMapping("/list")
    public Result<List<QuestionnaireQuestionDTO>> getQuestionnaireList() {
        List<QuestionnaireQuestionDTO> list = questionnaireService.getSortedQuestionnaireList();
        return Result.success(list);
    }

    /**
     * 提交问卷答案
     * 请求方式：POST
     * 请求路径：/api/questionnaire/answer/submit
     * 请求体：用户ID + 答案列表
     * 返回：提交结果
     */
    @PostMapping("/answer/submit")
    public Result<Void> submitQuestionnaireAnswer(@Validated @RequestBody QuestionnaireAnswerSubmitDTO submitDTO) {
        boolean success = questionnaireAnswerService.batchSaveAnswer(submitDTO);
        if (success) {
            return Result.success();
        } else {
            return Result.failWithOnlyMsg("问卷答案提交失败");
        }
    }

    /**
     * 查询用户已答问卷
     * 请求方式：GET
     * 请求路径：/api/questionnaire/answer/{userId}
     * 路径参数：userId - 用户ID（sys_user.id）
     * 返回：用户已答问卷列表
     */
    @GetMapping("/answer/{userId}")
    public Result<List<UserQuestionnaireAnswerDTO>> getUserAnswer(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            return Result.failWithOnlyMsg("用户ID无效");
        }
        List<UserQuestionnaireAnswerDTO> answerList = questionnaireAnswerService.getUserAnswerByUserId(userId);
        return Result.success(answerList);
    }

    /**
     * 新增问卷问题（仅管理员/指定角色可操作）
     * 请求方式：POST
     * 请求路径：/api/questionnaire/add
     * 权限：ROLE_ADMIN / ROLE_USER
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')") // 角色权限校验核心注解
    public Result<Void> addQuestion(@Validated @RequestBody QuestionnaireAddDTO addDTO) {
        boolean success = questionnaireService.addQuestionnaire(addDTO);
        if (success) {
            return Result.successWithCustomMsg("问卷问题添加成功");
        } else {
            return Result.failWithOnlyMsg("问卷问题添加失败");
        }
    }

    /**
     * 修改问卷问题
     * 请求方式：PUT
     * 请求路径：/api/questionnaire/update
     * 权限：ROLE_ADMIN
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> updateQuestion(@Validated @RequestBody QuestionnaireUpdateDTO updateDTO) {
        boolean success = questionnaireService.updateQuestionnaire(updateDTO);
        if (success) {
            return Result.successWithCustomMsg("问卷问题修改成功");
        } else {
            return Result.failWithOnlyMsg("问卷问题修改失败");
        }
    }

    /**
     * 删除问卷问题
     * 请求方式：DELETE
     * 请求路径：/api/questionnaire/delete/{id}
     * 路径参数：id - 问卷问题ID
     * 权限：ROLE_ADMIN
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteQuestion(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return Result.failWithOnlyMsg("问卷问题ID无效");
        }
        boolean success = questionnaireService.deleteQuestionnaire(id);
        if (success) {
            return Result.successWithCustomMsg("问卷问题删除成功");
        } else {
            return Result.failWithOnlyMsg("问卷问题删除失败");
        }
    }
}
