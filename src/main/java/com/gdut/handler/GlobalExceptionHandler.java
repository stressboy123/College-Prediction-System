package com.gdut.handler;

import com.gdut.entity.Result;
import com.gdut.entity.ResultCode;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * @author liujunliang
 * @date 2026/1/19
 * 全局异常处理器，处理参数校验异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求参数校验异常（@Valid）
     * @param ex MethodArgumentNotValidException
     * @return Result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMsg.append(fieldError.getDefaultMessage()).append("; ");
        }

        return Result.failWithCustomCodeAndMsg(ResultCode.PARAM_ERROR.getCode(),
                errorMsg.toString().trim());
    }

    /**
     * 处理绑定异常（@Validated）
     * @param ex BindException
     * @return Result
     */
    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMsg.append(fieldError.getDefaultMessage()).append("; ");
        }

        return Result.failWithCustomCodeAndMsg(ResultCode.PARAM_ERROR.getCode(),
                errorMsg.toString().trim());
    }

    /**
     * 处理约束违反异常（@Validated）
     * @param ex ConstraintViolationException
     * @return Result
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        StringBuilder errorMsg = new StringBuilder();

        for (ConstraintViolation<?> violation : violations) {
            errorMsg.append(violation.getMessage()).append("; ");
        }

        return Result.failWithCustomCodeAndMsg(ResultCode.PARAM_ERROR.getCode(),
                errorMsg.toString().trim());
    }
}
