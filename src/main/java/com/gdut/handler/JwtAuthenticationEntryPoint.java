package com.gdut.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdut.entity.Result;
import com.gdut.entity.ResultCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author liujunliang
 * @date 2026/1/18
 * 认证失败处理器（默认的AuthenticationEntryPoint会直接跳转登录页面）
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        Result<?> result;
        if (authException instanceof InsufficientAuthenticationException) {
            // 未登录访问受保护资源
            result = Result.fail(ResultCode.NO_AUTH);
        } else if (authException instanceof BadCredentialsException) {
            // 登录时用户名或密码错误
            result = Result.fail(ResultCode.INVALID_CREDENTIALS);
        } else if (authException instanceof DisabledException) {
            // 账户被禁用
            result = Result.fail(ResultCode.USER_DISABLED);
        } else {
            // 其他认证错误
            result = Result.failWithCustomMsg(ResultCode.NO_LOGIN, authException.getMessage());
        }
        out.write(new ObjectMapper().writeValueAsString(result));
        out.flush();
        out.close();
    }
}
