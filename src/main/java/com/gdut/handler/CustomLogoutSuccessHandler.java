package com.gdut.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdut.entity.Result;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author liujunliang
 * @date 2026/2/7
 * 自定义登出成功处理器（返回JSON格式响应，替代默认页面跳转）
 */
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 1. 设置响应格式为JSON
        response.setContentType("application/json;charset=utf-8");
        // 2. 构建登出成功响应结果
        Result<String> result = Result.successWithCustomMsg("登出成功");
        // 3. 写入响应流
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(result));
        out.flush();
        out.close();
    }
}
