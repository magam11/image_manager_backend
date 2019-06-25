package am.arssystems.image_manager_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_AUDIENCE = "aud";
    static final String CLAIM_KEY_CREATED = "iat";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;


    public String getPhoneNumberFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getAudienceFromToken(String token) {
        return getClaimFromToken(token, Claims::getAudience);
    }

    public   <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }


    public String generateToken(String phoneNumber) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, phoneNumber);
    }
    public String generateTokenP(String email, String password) {//թոկենի գեներացիա ըստ էլ-հասցեի, որում պահվում է նաև կոդավորված գաղտնաբառը, գաղտնաբառը պահվում է "password" բանալիով
        Map<String, Object> claims = new HashMap<>();
        claims.put("password",password);
        return doGenerateToken(claims, email);
    }
    public String generateTokenPassAndId(String phoneNumber, String password,int id) {//թոկենի գեներացիա ըստ էլ-հասցեի, որում պահվում է նաև կոդավորված գաղտնաբառը, գաղտնաբառը պահվում է "password" բանալիով
        Map<String, Object> claims = new HashMap<>();
        claims.put("password",password);
        claims.put("id",id);
        return doGenerateToken(claims, phoneNumber);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }


    private String refreshToken(String token) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate);

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean validateToken(String token, String email) {
        final String username = getPhoneNumberFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);
        return (username.equals(email)
                && !isTokenExpired(token));
    }

    public Date calculateExpirationDate(Date createdDate) {
//        return new Date(createdDate.getTime() + expiration * 1000);
        return new Date(createdDate.getTime() + expiration * 1000);
    }
}
