package com.atixlabs.semillasmiddleware.security;

import com.atixlabs.semillasmiddleware.security.util.JwtTokenControlUtil;
import com.atixlabs.semillasmiddleware.util.DateUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private JwtTokenControlUtil jwtTokenControlUtil;

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public String generateToken(UserDetails userDetails) throws UnsupportedEncodingException {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private byte[] getSecret() throws UnsupportedEncodingException {
        return (secret).getBytes("UTF-8");
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) throws UnsupportedEncodingException {

        Date now = dateUtil.getDateNow();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        String token =  Jwts.builder()
                        //.setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(now)
                        .setExpiration(expiryDate)
                        .signWith(SignatureAlgorithm.HS512, this.getSecret())
                        .compact();
        jwtTokenControlUtil.setToken(subject,token);
        return token;
    }


    public Boolean validateToken(String token, UserDetails userDetails) throws UnsupportedEncodingException {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token))&&(jwtTokenControlUtil.isTokenValid(token));
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

    public String getUsernameFromToken(String token) throws UnsupportedEncodingException {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) throws UnsupportedEncodingException {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws UnsupportedEncodingException {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) throws UnsupportedEncodingException {
        return Jwts.parser().setSigningKey(getSecret()).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) throws UnsupportedEncodingException {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(dateUtil.getDateNow());
    }

    public boolean revoqueToken(String username) {
         return jwtTokenControlUtil.revoqueToken(username);
    }

}


