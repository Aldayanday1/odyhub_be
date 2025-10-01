/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem_pengaduan.demo.service;

import static com.mysql.cj.conf.PropertyKey.logger;
import sistem_pengaduan.demo.model.User;
import sistem_pengaduan.demo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
//import java.util.Optional;
import java.util.Random;
import sistem_pengaduan.demo.model.UserProfile;
import sistem_pengaduan.demo.model.UserRole;
import sistem_pengaduan.demo.repository.UserProfileRepo;
import sistem_pengaduan.demo.util.JwtUtil;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserProfileRepo userProfileRepo;

    @Autowired
    private EmailServiceRegist emailServiceregist;

    @Autowired
    private EmailServiceLogin emailServicelogin;

    @Autowired
    private JwtUtil jwtUtil;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserService.class);

    // Algoritma hashing Argon2 dengan parameter kustom
    // Memory: 16 (jumlah memori yang digunakan oleh algoritma, semakin tinggi nilai
    // memori semakin sulit serangan brute force untuk memecahkan hash. )
    // Iterations: 32 (jumlah iterasi yang dilakukan oleh algoritma)
    // Parallelism: 1 (jumlah benang yang digunakan oleh algoritma)
    // Salt Length: 4096 (panjang salt yang digunakan dalam proses hashing)
    // Hash Length: 64 (panjang output hash yang dihasilkan oleh algoritma)
    private PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(
            16, 32, 1, 4096, 64);

    // ----------------------- REGISTRASI -----------------------
    public User registerUser(String nama, String email, String password) {
        // Periksa apakah email sudah terdaftar
        if (userRepo.findByEmail(email) != null) {
            throw new RuntimeException("Email sudah terdaftar");
        }

        // Hash password sebelum menyimpan
        String hashedPassword = passwordEncoder.encode(password);

        // Buat kode OTP
        String otp = generateOtp();

        // Buat entitas pengguna baru
        User user = new User();
        user.setNama(nama);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRole(UserRole.USER);
        user.setOtpCode(otp);
        user.setOtpExpiry(new Date(System.currentTimeMillis() + 60000)); // 1 menit
        user.setRegistering(true); // Set registering flag

        // Simpan pengguna ke database
        userRepo.save(user);

        System.out.println("Kode OTP di set pada waktu: " + new Date());

        // Kirim email OTP
        emailServiceregist.sendOTPRegist(email, otp, nama);

        return user;
    }

    public void verifyOtp(String otp) {
        User user = userRepo.findByOtpCode(otp);
        if (user == null || user.getOtpCode() == null || user.getOtpExpiry() == null) {
            throw new RuntimeException("OTP tidak valid");
        }

        Date now = new Date();
        // if (!user.getOtpCode().equals(otp)) {
        // throw new RuntimeException("Kode OTP salah");
        // }

        if (user.getOtpExpiry().before(now)) {
            // Jika OTP sudah kadaluwarsa, Hapus pengguna dari database jika dalam proses
            // registrasi
            userRepo.delete(user);
            throw new RuntimeException("Kode OTP sudah kadaluwarsa");
        }

        // Reset OTP setelah verifikasi berhasil
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        user.setRegistering(false); // Clear registering flag after successful verification

        // Simpan pengguna yang terverifikasi ke database
        userRepo.save(user);
    }

    // ----------------------- LOGIN USER -----------------------
    // Metode untuk mengirim OTP saat login
    public void sendOtpForLogin(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Pengguna tidak ditemukan");
        }

        // Generate kode OTP baru
        String otp = generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(new Date(System.currentTimeMillis() + 60000)); // 1 menit

        user.setRegistering(false); // Set registering flag to false for login

        userRepo.save(user);

        System.out.println("Kode OTP di set pada waktu: " + new Date());

        // Kirim email OTP
        emailServicelogin.sendOTPLogin(email, otp, user.getNama());
    }

    // Metode untuk login dengan verifikasi OTP
    public String verifyOtpLogin(String otp) {
        User user = userRepo.findByOtpCode(otp);
        if (user == null || !user.getOtpCode().equals(otp)) {
            throw new RuntimeException("OTP tidak valid");
        }

        if (user.getOtpExpiry().before(new Date())) {
            // Hapus hanya kode OTP dan waktu kadaluarsa jika tidak diverifikasi dalam waktu
            // yang ditentukan
            user.setOtpCode(null);
            user.setOtpExpiry(null);
            userRepo.save(user);
        }

        // Reset OTP setelah verifikasi berhasil
        user.setOtpCode(null);
        user.setOtpExpiry(null);

        userRepo.save(user);

        // Generate token JWT
        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    // Metode untuk login dengan kredensial email dan password
    public void loginUserWithCredentials(String email, String password) {
        User user = userRepo.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Email atau password salah");
        }

        // Kirim OTP untuk verifikasi
        sendOtpForLogin(email);
    }

    // ----------------------- LOGIN ADMIN -----------------------
    // Metode untuk login admin tanpa OTP
    public String loginAdmin(String nama, String password) {
        logger.info("Attempting to log in with name: {}", nama);

        User user = userRepo.findByNama(nama);
        if (user == null) {
            logger.error("User not found with name: {}", nama);
            throw new RuntimeException("Nama atau password salah");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.error("Password does not match for user: {}", nama);
            throw new RuntimeException("Nama atau password salah");
        }

        if (!"ADMIN".equals(user.getRole().name())) {
            logger.error("User is not an admin: {}", nama);
            throw new RuntimeException("Akses ditolak. Anda bukan admin.");
        }

        logger.info("Login successful for user: {}", nama);
        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    // ----------------------- CREATE ADMIN -----------------------
    public User createAdmin(String nama, String email, String password) {
        if (userRepo.findByEmail(email) != null) {
            throw new RuntimeException("Email sudah terdaftar");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User admin = new User();
        admin.setNama(nama);
        admin.setEmail(email);
        admin.setPassword(hashedPassword);
        admin.setRole(UserRole.ADMIN);

        userRepo.save(admin);

        return admin;
    }

    // ----------------------- GENERATE OTP -----------------------
    private String generateOtp() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000);
        return String.valueOf(otp);
    }

    // ----------------------- GET USER PROFILE -----------------------
    // Method to get user profile by email
    public UserProfile getUserProfile(String email) {
        User user = userRepo.findByEmail(email);
        if (user != null && user.getRole() == UserRole.USER) {
            UserProfile userProfile = userProfileRepo.findByUser(user);
            if (userProfile == null) {
                // Buat profil baru dengan nilai default jika belum ada
                userProfile = new UserProfile();
                userProfile.setUser(user);
                userProfileRepo.save(userProfile);
            }
            return userProfile;
        }
        return null;
    }

    // ----------------------- UPDATE USER PROFILE -----------------------
    public UserProfile updateUserProfile(String email, UserProfile updatedProfile) {
        // Mencari pengguna berdasarkan email
        User user = userRepo.findByEmail(email);

        // Memeriksa apakah pengguna ditemukan dan memiliki peran USER
        if (user != null && user.getRole() == UserRole.USER) {

            // Mencari profil pengguna berdasarkan pengguna
            UserProfile existingProfile = userProfileRepo.findByUser(user);

            // Jika profil pengguna sudah ada, perbarui gambar profil dan latar belakang
            if (existingProfile != null) {
                existingProfile.setProfileImage(updatedProfile.getProfileImage());
                existingProfile.setBackgroundImage(updatedProfile.getBackgroundImage());
                return userProfileRepo.save(existingProfile);
            } else {
                // Jika profil pengguna belum ada, buat profil baru dan kaitkan dengan pengguna
                updatedProfile.setUser(user);
                return userProfileRepo.save(updatedProfile);
            }
        }
        // Jika pengguna tidak ditemukan atau bukan USER, kembalikan null
        return null;
    }

    // public boolean validateToken(String token) {
    // try {
    // String email = jwtUtil.extractEmail(token);
    // return jwtUtil.validateToken(token, email);
    // } catch (Exception e) {
    // return false;
    // }
    // }
}
