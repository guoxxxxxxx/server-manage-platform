/**
 * @Time: 2025/1/4 17:40
 * @Author: guoxun
 * @File: JwtTokenUtil
 * @Description: JWT工具类
 */

package com.iecas.servermanageplatform.utils;

import com.iecas.servermanageplatform.exception.LoginExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
public class JwtUtils {

    private static final String SECRET_KEY = "IJISDJIOSJIDJAIOJFIOASJDIOJASIODJIAO";
    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7L;


    /**
     * 创建token使用默认的过期时间 7 天
     * @param username 用户名
     * @param object 数据
     * @return token
     */
    public static String createToken(String username, Object object){
        Map<String, Object> claims = new HashMap<>();
        claims.put("data", object);
        return createToken(username, claims, EXPIRATION_TIME);
    }


    /**
     * 创建token使用默认的过期时间 7 天
     * @param username 用户名
     * @param claims 数据
     * @return token
     */
    public static String createToken(String username, Map<String, Object> claims){
        return createToken(username, claims, EXPIRATION_TIME);
    }


    /**
     * 生成token
     * @param username 用户名
     * @param claims 数据
     * @param expirationTime 过期时间
     * @return token
     */
    public static String createToken(String username, Map<String, Object> claims, long expirationTime) {
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.id(UUID.randomUUID().toString())
                .issuer("system")
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(expirationTime + new Date().getTime()))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)));
        jwtBuilder.header()
                .add("typ", "JWT")
                .add("alg", "HS256");
        return jwtBuilder.compact();
    }


    /**
     * 解析token
     * @param token token
     * @return Claims
     */
    public static Claims parseToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token);
            return jws.getPayload();
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException){
                log.info("Expired JWT token");
                throw new LoginExpiredException("token 已经过期, 请重新登录");
            }
            else if (e instanceof JwtException){
                log.info("Invalid JWT token");
                throw new RuntimeException("token不合法");
            }
            log.error("Token parsing error : {}", String.valueOf(e));
            throw new RuntimeException(e);
        }
    }
}
