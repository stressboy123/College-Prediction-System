package com.gdut.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Getter
@Component
public class JwtUtil {
    // 从配置文件读取参数
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expire}")
    private long expire;
    @Value("${jwt.prefix}")
    private String prefix;

    /**
     * 生成Token
     */
    public String generateToken(UserDetails userDetails) {
        // 构建JWT参数
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername()) // 用户名作为token主题
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expire)) // 过期时间
                .signWith(SignatureAlgorithm.HS256, secret) // 签名算法+密钥
                .compact();
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 验证Token有效性
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 检查Token是否过期
     */
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 移除Token前缀（如Bearer ）
     */
    public String removePrefix(String token) {
        if (token.startsWith(prefix)) {
            return token.replace(prefix + " ", "");
        }
        return token;
    }
}
