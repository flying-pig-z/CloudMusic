package com.flyingpig.cloudmusic.util;


import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

public class JwtUtil {
    //有效期为
    public static final Long JWT_TTL = 30*24*60 * 60 * 1000L;// 60 * 60 *1000  一个小时
    //设置秘钥明文
    public static final String JWT_KEY = "Zmx5aW5ncGln";//flyingpig的base64编码

    /**
     * 生成加密后的秘钥 secretKey
     *
     * @return
     */
    public static SecretKey generalKey() {
        //将密钥明文base64解码后通过aes算法进行加密
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }


    public static String getUUID() {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }

    /**
     * 生成jtw
     *
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());// 设置过期时间
        return builder.compact();
    }

    /**
     * 生成jtw
     *
     * @param subject   token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());// 设置过期时间
        return builder.compact();
    }

    /**
     * 生成jtw
     *
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis,String id) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);// 设置过期时间
        return builder.compact();
    }

    //三个生成jwt方法中的核心代码
    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        //签名算法和密钥
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        //求出过期时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);

        //set方法都是设置payload的json，然后进行签名
        return Jwts.builder()
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer("flyingpig")     // 签发者
                .setIssuedAt(now)      // 签发时间
                .setExpiration(expDate)  //过期时间
                .signWith(signatureAlgorithm, secretKey); //使用HS256对称加密算法签名, 第二个参数为秘钥

    }



    //解析JWT令牌的payload自定义信息
    public static Claims parseJwt(String jwt) {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }


    //获取payload中的uuid
    public static String getUUIDFromJWT(String jwt) {
        Claims claims = parseJwt(jwt);
        return claims.getId();
    }

    //获取payload中存放的subject，即string版的userId
    public static String getSubjectFromJWT(String jwt) {
        Claims claims = parseJwt(jwt);
        return claims.getSubject();
    }


    //进行base64编码的测试类
    public static void main(String[] args) throws Exception {
        String jwtKey = "flyingpig";
        String encodedKey = Base64.getEncoder().encodeToString(jwtKey.getBytes());
        System.out.println(encodedKey);
    }

}