package ch.zhaw.integration.beacons.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    private final String signingKey;
    private final Integer accessTokenValiditySeconds;
    private final String tokenIssuer;

    public JwtTokenUtil(
            @Value("${beacons.jwt.token.signing.key}") String signingKey,
            @Value("${beacons.jwt.token.validity.seconds}") Integer accessTokenValiditySeconds,
            @Value("${beacons.jwt.token.issuer}") String tokenIssuer) {
        this.signingKey = signingKey;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.tokenIssuer = tokenIssuer;
    }

    public String getUserEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String email) {
        return doGenerateToken(email);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUserEmailFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private String doGenerateToken(String subject) {
        //TODO implement authorities https://www.toptal.com/spring/spring-security-tutorial
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("scopes", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(tokenIssuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValiditySeconds*1000))
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

}
