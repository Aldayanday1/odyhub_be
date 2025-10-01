package sistem_pengaduan.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.System.Logger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import sistem_pengaduan.demo.model.User;
import sistem_pengaduan.demo.model.UserProfile;
import sistem_pengaduan.demo.repository.UserProfileRepo;
import sistem_pengaduan.demo.repository.UserRepo;
import sistem_pengaduan.demo.service.UserService;
import sistem_pengaduan.demo.util.JwtUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserService.class);

    // ----------------------- REGISTRASI -----------------------
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user.getNama(), user.getEmail(), user.getPassword());
            return ResponseEntity.ok("Registrasi berhasil. Silakan cek email Anda untuk kode OTP.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String otp) {
        try {
            userService.verifyOtp(otp);
            return ResponseEntity.ok("Verifikasi OTP berhasil. Silakan login.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/check-otp-status")
    public ResponseEntity<Boolean> checkOtpStatus(@RequestParam String email) {
        User user = userRepo.findByEmail(email);
        if (user == null || user.getOtpCode() == null || user.getOtpExpiry() == null) {
            return ResponseEntity.ok(false);
        } else {
            return ResponseEntity.ok(true);
        }
    }

    // ----------------------- LOGIN USER -----------------------
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String email, @RequestParam String password) {
        try {
            userService.loginUserWithCredentials(email, password);
            return ResponseEntity.ok("OTP telah dikirimkan ke email Anda. Silakan cek email Anda untuk kode OTP.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login-with-otp")
    public ResponseEntity<Map<String, Object>> loginWithOtp(@RequestBody Map<String, String> payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String otp = payload.get("otp");
            String token = userService.verifyOtpLogin(otp);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Login berhasil");
            response.put("token", token);

            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ----------------------- LOGIN ADMIN -----------------------
    @PostMapping("/admin-login")
    public ResponseEntity<Map<String, Object>> adminLogin(@RequestBody Map<String, String> payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String nama = payload.get("nama");
            String password = payload.get("password");

            logger.info("Received login request for name: {}", nama);

            String token = userService.loginAdmin(nama, password);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Login berhasil");
            response.put("token", token);

            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            logger.error("Login error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ----------------------- CREATE ADMIN -----------------------
    @PostMapping("/create-admin")
    public User createAdmin(@RequestBody User createUserRequest) {
        return userService.createAdmin(createUserRequest.getNama(), createUserRequest.getEmail(), createUserRequest.getPassword());
    }

    // ----------------------- LOGOUT & BLACKLIST TOKEN -----------------------
    // Endpoint untuk logout dan blacklist token
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String tokenHeader) {
        try {
            // Mendapatkan token JWT dari header Authorization
            String token = tokenHeader.substring(7); // Menghilangkan "Bearer " dari awal token

            // Masukkan token ke dalam daftar blacklist
            jwtUtil.addToBlacklist(token);

            return ResponseEntity.ok("Logout berhasil");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ----------------------- CHECKED -----------------------
    @GetMapping("/admin")
    public ResponseEntity<String> adminAccess(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7); // Menghapus "Bearer "

            // Mengecek apakah token sudah di-blacklist
            if (jwtUtil.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token sudah tidak valid.");
            }

            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            // Mengecek apakah token sudah di-blacklist
            if (jwtUtil.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token sudah tidak valid.");
            }

            if (jwtUtil.validateToken(token, email) && "ADMIN".equals(role)) {
                return ResponseEntity.ok("Selamat datang di halaman admin");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Akses ditolak");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token kadaluwarsa.");
        } catch (Exception e) {
            // Menangani kesalahan yang tidak terduga
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<String> userAccess(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7); // Menghapus "Bearer "

            // Mengecek apakah token sudah di-blacklist
            if (jwtUtil.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token sudah tidak valid.");
            }

            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            if (jwtUtil.validateToken(token, email) && "USER".equals(role)) {
                return ResponseEntity.ok("Selamat datang di halaman pengguna");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Akses ditolak");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token kadaluwarsa.");
        } catch (Exception e) {
            // Menangani kesalahan yang tidak terduga
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

}
