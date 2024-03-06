package org.example.quantumblog.util.token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaol
 */
public class JwtToken {
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
                .signWith(SignatureAlgorithm.HS256, "secret")
                .compact();
    }

    //解析Token
    public static Claims parseToken(String token) {
        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token);
        return jws.getBody();
    }

}
