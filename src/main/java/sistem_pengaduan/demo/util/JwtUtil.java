package sistem_pengaduan.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    // menyimpan secret key di environemt var (hanya entitas tertentu yg bisa akses)
    String secretKey = System.getenv("SECRET_KEY");
    
    private List<String> blacklist = new ArrayList<>();

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // Tambahkan informasi peran ke token
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 2400)) // 40 menit
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Extraction -> mengambil dan menguraikan informasi tertentu dari token JWT. Ini melibatkan dekode payload token untuk mendapatkan data yang disimpan di dalamnya.
    // Mengambil keseluruhan klaim (claims) dari payload token JWT (subject (email), role, dsb.)
    public Claims extractClaims(String token) {
        // Diambil sebagai string, kemudian di parsing (menjadi 3 bagian)
        return Jwts.parser()
                // menetapkan secret key yg sama
                .setSigningKey(secretKey)
                // proses parsing decode menjadi 3 bagian, beserta pengecekan signature 
                .parseClaimsJws(token)
                // jika valid, maka payload di ambil sbg body
                .getBody();
    }
    
    // w/controller
    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    // w/validatetoken  
    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }

    // Mengambil subjek (subject) dari token JWT, berupa email atau ID pengguna
    public String extractEmail(String token) {
        // mendapatkan klaim, lalu mengambil subjek (subject) dari klaim tersebut.
        return extractClaims(token).getSubject();
    }

    // Mengambil peran pengguna (role) dari token JWT. 
    public String extractRole(String token) {
        // mendapatkan klaim, lalu mengambil nilai "role" dari klaim tersebut.
        return extractClaims(token).get("role", String.class);
    }

    // Mengambil id pengguna dari token JWT
    public Long extractUserId(String token) {
        // Mendapatkan klaim, lalu mengambil nilai "userId" dari klaim tersebut.
        return extractClaims(token).get("userId", Long.class);
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String email) {
        return (extractEmail(token).equals(email) && !isTokenExpired(token) && !isTokenBlacklisted(token));
    }
}
