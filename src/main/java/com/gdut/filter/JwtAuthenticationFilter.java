package com.gdut.filter;

import com.gdut.entity.SysUser;
import com.gdut.service.SysUserService;
import com.gdut.utils.JwtUtil;
import com.gdut.utils.SpringContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author liujunliang
 * @date 2026/1/18
 * 认证过滤器，负责「身份认证」（还有授权过滤器AuthorizationFilter，5.7+后无需自定义）
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SysUserService sysUserService = SpringContextHolder.getBean(SysUserService.class);
        // 1. 获取请求头中的Token
        String header = request.getHeader(jwtUtil.getHeader());
        if (header == null || !header.startsWith(jwtUtil.getTokenPrefix())) {
            // 无有效Token，直接放行（交给Security后续处理）
            filterChain.doFilter(request, response);
            return;
        }
        // 2. 提取Token（去除前缀）
        String token = header.substring(jwtUtil.getTokenPrefix().length());
        // 3. 解析Token获取用户名
        String username = jwtUtil.getUsernameFromToken(token);
        // 4. 用户名不为空且未认证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 5. 查询用户信息
            SysUser sysUser = sysUserService.getUserByUsername(username);
            if (sysUser == null || sysUser.getStatus() == 0) {
                // 用户不存在或已禁用，放行（后续会报未授权）
                filterChain.doFilter(request, response);
                return;
            }
            // 6. 构建UserDetails（Spring Security需要）
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // 7. 验证Token有效性
            if (jwtUtil.validateToken(token, userDetails)) {
                // 8. 设置认证信息到SecurityContext
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 9. 放行
        filterChain.doFilter(request, response);
    }
}
