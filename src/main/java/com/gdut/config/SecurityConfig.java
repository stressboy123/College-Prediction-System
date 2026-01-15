package com.gdut.config;

import com.gdut.filter.JwtAuthFilter;
import com.gdut.filter.JwtLoginFilter;
import com.gdut.service.impl.CustomUserDetailsService;
import com.gdut.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // 开启方法级权限控制（@PreAuthorize）
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 密码加密器（必须配置，否则Security报错）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器（用于登录过滤器）
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Security过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭CSRF（前后端分离无需CSRF）
                .csrf().disable()
                // 2. 关闭Session（基于Token无状态）
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 3. 配置权限规则（适配5.7.x版本的写法）
                .authorizeRequests(auth -> auth
                        // 放行登录/注册接口：使用antMatchers
                        .antMatchers("/api/auth/login").permitAll()
                        .antMatchers("/api/auth/register").permitAll()
                        // 管理员接口仅允许ADMIN角色访问
                        .antMatchers("/api/admin/**").hasRole("ADMIN")
                        // 普通用户接口仅允许USER/ADMIN角色访问
                        .antMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        // 其余接口需认证
                        .anyRequest().authenticated()
                )
                // 4. 添加JWT登录过滤器（替换默认的用户名密码登录）
                .addFilter(new JwtLoginFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), jwtUtil))
                // 5. 添加JWT授权过滤器（验证Token）
                .addFilterBefore(new JwtAuthFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                // 6. 配置跨域
                .cors().configurationSource(corsConfigurationSource());

        return http.build();
    }

    /**
     * 跨域配置（前后端联调核心）
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 允许所有域名（生产环境需指定具体域名）
        config.addAllowedMethod("*"); // 允许所有请求方法
        config.addAllowedHeader("*"); // 允许所有请求头
        config.setAllowCredentials(true); // 允许携带Cookie
        config.setMaxAge(3600L); // 预检请求缓存时间

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 所有接口生效
        return source;
    }
}
