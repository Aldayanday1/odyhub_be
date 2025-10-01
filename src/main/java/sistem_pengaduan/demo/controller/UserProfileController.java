/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem_pengaduan.demo.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import sistem_pengaduan.demo.model.User;
import sistem_pengaduan.demo.model.UserProfile;
import sistem_pengaduan.demo.repository.UserProfileRepo;
import sistem_pengaduan.demo.repository.UserRepo;
import sistem_pengaduan.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ALDAYANDAY
 */
@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserProfileRepo userProfileRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FileStorageService fileStorageService;
    
    // ----------------------- GET USER PROFILE -----------------------

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        try {
            // Mendapatkan token JWT dari header permintaan
            String token = request.getHeader("Authorization").substring(7);

            // Mengecek apakah token sudah di-blacklist
            if (jwtUtil.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token sudah tidak valid.");
            }

            // Mengekstrak email pengguna dari token
            String email = jwtUtil.extractEmail(token);

            // Memvalidasi token JWT
            if (!jwtUtil.validateToken(token, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token tidak valid.");
            }

            // Mengambil pengguna berdasarkan email
            User user = userRepo.findByEmail(email);
            if (user != null) {
                UserProfile userProfile = userProfileRepo.findByUser(user);

                // Jika profil pengguna tidak ditemukan, buat profil default
                if (userProfile == null) {
                    userProfile = new UserProfile();
                    userProfile.setUser(user);
                    userProfile.setProfileImage("http://192.168.56.1:8080/images/profile.png"); // URL default
                    userProfile.setBackgroundImage("http://192.168.56.1:8080/images/background.jpg"); // URL default
                    userProfileRepo.save(userProfile);
                } else {
                    // Set default jika profil pengguna ada tapi tidak ada gambar
                    if (userProfile.getProfileImage() == null || userProfile.getProfileImage().isEmpty()) {
                        userProfile.setProfileImage("http://192.168.56.1:8080/images/profile.png");
                    }
                    if (userProfile.getBackgroundImage() == null || userProfile.getBackgroundImage().isEmpty()) {
                        userProfile.setBackgroundImage("http://192.168.56.1:8080/images/background.jpg");
                    }
                }

                Map<String, Object> profileData = new HashMap<>();
                profileData.put("nama", user.getNama());
                profileData.put("email", user.getEmail());
                profileData.put("profileImage", userProfile.getProfileImage());
                profileData.put("backgroundImage", userProfile.getBackgroundImage());
                return ResponseEntity.ok(profileData);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pengguna tidak ditemukan.");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token kadaluwarsa.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    // ----------------------- UPDATE USER PROFILE -----------------------
    
    @PutMapping("/profile/update")
    public ResponseEntity<?> updateUserProfile(HttpServletRequest request,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "backgroundImage", required = false) MultipartFile backgroundImage) {
        try {
            // Mendapatkan token JWT dari header permintaan
            String token = request.getHeader("Authorization").substring(7);

            // Mengecek apakah token sudah di-blacklist
            if (jwtUtil.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token sudah tidak valid.");
            }

            // Mengekstrak email dari token
            String email = jwtUtil.extractEmail(token);

            // Memvalidasi token JWT
            if (!jwtUtil.validateToken(token, email) || !"USER".equals(jwtUtil.extractRole(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Anda tidak diizinkan untuk mengakses fitur ini.");
            }

            // Mengambil pengguna berdasarkan email
            User user = userRepo.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pengguna tidak ditemukan.");
            }

            // Mengambil UserProfile berdasarkan User
            UserProfile userProfile = userProfileRepo.findByUser(user);
            if (userProfile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profil pengguna tidak ditemukan.");
            }

            // Menyimpan path gambar yang ada saat ini
            String currentProfileImage = userProfile.getProfileImage();
            String currentBackgroundImage = userProfile.getBackgroundImage();

            boolean adaPerubahan = false;

            // Update gambar profil jika parameter tidak null dan tidak kosong
            if (profileImage != null && !profileImage.isEmpty()) {
                String profileImagePath = fileStorageService.storeFile(profileImage);
                if (!profileImagePath.equals(currentProfileImage)) {
                    userProfile.setProfileImage(profileImagePath);
                    adaPerubahan = true;
                }
            }

            // Update gambar latar belakang jika parameter tidak null dan tidak kosong
            if (backgroundImage != null && !backgroundImage.isEmpty()) {
                String backgroundImagePath = fileStorageService.storeFile(backgroundImage);
                if (!backgroundImagePath.equals(currentBackgroundImage)) {
                    userProfile.setBackgroundImage(backgroundImagePath);
                    adaPerubahan = true;
                }
            }

            // Jika tidak ada perubahan pada data
            if (!adaPerubahan) {
                return ResponseEntity.badRequest().body("Tidak ada perubahan yang dilakukan.");
            }

            userProfileRepo.save(userProfile);
            return ResponseEntity.ok("Profil pengguna berhasil diperbarui.");

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token kadaluwarsa.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }
}
