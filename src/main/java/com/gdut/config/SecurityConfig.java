package com.gdut.config;

import com.gdut.entity.SysUser;
import com.gdut.filter.JwtAuthenticationFilter;
import com.gdut.handler.CustomLogoutSuccessHandler;
import com.gdut.handler.JwtAccessDeniedHandler;
import com.gdut.handler.JwtAuthenticationEntryPoint;
import com.gdut.service.SysUserService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Configuration
@EnableWebSecurity // 启用Spring Security
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法级权限控制
public class SecurityConfig {

    @Resource
    private ObjectProvider<JwtAuthenticationFilter> jwtAuthenticationFilterProvider;
    @Resource
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Resource
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @Resource
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    @Resource
    private SysUserService sysUserService;

    // 密码编码器（BCrypt加密）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 认证管理器（用于登录认证）
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // UserDetailsService（Spring Security加载用户信息）
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            SysUser sysUser = sysUserService.getUserByUsername(username);
            if (sysUser == null) {
                throw new UsernameNotFoundException("用户名不存在");
            }
            // 构建UserDetails，包含用户名、密码、权限（这里简化，实际从角色表查询）
            return User.withUsername(sysUser.getUsername())
                    .password(sysUser.getPassword())
                    .authorities("ROLE_USER")
                    .build();
        };
    }

    // 认证提供者（关联UserDetailsService和PasswordEncoder）
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // 安全规则配置
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭CSRF（前后端分离项目无需）
                .csrf().disable()
                // 2. 关闭Session（JWT无状态认证）
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 3. 配置权限规则
                .authorizeRequests()
                // 放行登录、注册接口
                .antMatchers("/auth/login", "/auth/register").permitAll()
                // 放行Swagger（如果后续集成）
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 放行excel静态资源（如果后续集成）
                .antMatchers("/excel/**").permitAll()
                // 其他接口需认证
                .anyRequest().authenticated()
                .and()
                // 4. 配置异常处理器
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 未授权
                .accessDeniedHandler(jwtAccessDeniedHandler) // 权限不足
                .and()
                // 7. 登出配置
                .logout()
                .logoutUrl("/auth/logout") // 登出接口URL
                .logoutSuccessHandler(customLogoutSuccessHandler) // 绑定自定义登出处理器
                .clearAuthentication(true) // 清除认证信息
                .invalidateHttpSession(true) // 清空Session（无状态场景仅为兜底）
                .and()
                // 5. 添加JWT过滤器（在用户名密码认证过滤器之前）
                .addFilterBefore(jwtAuthenticationFilterProvider.getObject(), UsernamePasswordAuthenticationFilter.class)
                // 6. 启用认证提供者
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
