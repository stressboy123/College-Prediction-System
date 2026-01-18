package com.gdut.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret; // JWT密钥（从配置文件读取）

    @Value("${jwt.expire}")
    private long expire; // 过期时间（毫秒）

    @Value("${jwt.prefix}")
    private String prefix; // Token前缀

    @Value("${jwt.header}")
    private String header; // 请求头Key

    // 生成Token（基于用户名）
    public String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setSubject(username) // 用户名作为Subject
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expire)) // 过期时间
                .signWith(key) // 签名
                .compact();
    }

    // 从Token中获取用户名
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    // 验证Token有效性（用户名匹配 + 未过期）
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // 解析Token获取Claims
    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查Token是否过期
    private boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }

    // 获取Token前缀（带空格）
    public String getTokenPrefix() {
        return prefix + " ";
    }

    // 获取请求头Key
    public String getHeader() {
        return header;
    }
}
