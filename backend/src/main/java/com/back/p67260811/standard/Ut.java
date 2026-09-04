package com.back.p67260811.standard;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class Ut {

    public static class jwt {

        // 토큰 발급
        public static String toString(String secret, long expireMills, Map<String, Object> body) {
            Claims claims = Jwts.claims()
                    .add(body)
                    .build();

            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + expireMills);

            Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            String jwt = Jwts.builder()
                    .claims(claims)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();

            return jwt;
        }

        // 서명, 만료 검증
        public static boolean isValid(String jwt, String secretPattern) {

            SecretKey secretKey = Keys.hmacShaKeyFor(secretPattern.getBytes(StandardCharsets.UTF_8));

            try {
                Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(jwt);

            } catch (Exception e) {
                return false;
            }
            // 원래는 뭐 때문에 예외인지 catch문 더 거는 게 정석
            return true;
        }

        // 검증 후 페이로드 추출
        public static Map<String, Object> payloadOrNull(String jwt, String secretPattern) {

            SecretKey secretKey = Keys.hmacShaKeyFor(secretPattern.getBytes(StandardCharsets.UTF_8));

            if(isValid(jwt, secretPattern)) {
                return  Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();
            }

            return null;
        }
    }


}
