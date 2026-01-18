package com.gdut.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdut.entity.Result;
import com.gdut.entity.ResultCode;
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
        // 区分"未登录"和"缺少认证信息"
        if (authException instanceof InsufficientAuthenticationException) {
            result = Result.fail(ResultCode.NO_AUTH);
        } else {
            result = Result.fail(ResultCode.NO_LOGIN);
        }
        out.write(new ObjectMapper().writeValueAsString(result));
        out.flush();
        out.close();
    }
}
