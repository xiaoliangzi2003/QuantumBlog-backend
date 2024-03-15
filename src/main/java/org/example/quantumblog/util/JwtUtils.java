package org.example.quantumblog.util;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Jwt工具类：生成Token和解析Token
 * @author xiaol
 */
public class JwtUtils {
    //密钥
    private static final String SECRET_KEY="xiaoliangzi2003";

    //生成Token
    public static String generateToken(String username, String password) {
        Date now = new Date();

        //HashMap可以存储键值对
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("password", password);
        claims.put("secret", SECRET_KEY);
        //使用jwt生成器生成Token
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    //解析Token
    public static Claims parseToken(String token) {
        try{
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

        }catch (SignatureException e){
            throw new SignatureException("Invalid token");
        }catch (ExpiredJwtException e){
            throw new ExpiredJwtException(null,null,"Token expired");
        }
    }

}
