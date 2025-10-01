/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem_pengaduan.demo.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import sistem_pengaduan.demo.model.Status;
import sistem_pengaduan.demo.model.StatusLaporan;
import sistem_pengaduan.demo.service.StatusLaporanService;
import sistem_pengaduan.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class StatusLaporanController {

    @Autowired
    private StatusLaporanService statusLaporanService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    // ----------------------- UPDATE STATUS LAPORAN ----------------------- ADMIN

    // Endpoint untuk mengupdate status laporan dengan tambahan gambar
    @PutMapping("update-status/{pengaduanId}")
    public ResponseEntity<?> updateStatusLaporanWithImage(
            @PathVariable Long pengaduanId,
            @RequestParam(value = "gambar", required = false) MultipartFile gambar,
            @RequestParam("statusBaru") Status statusBaru,
            @RequestParam("tanggapan") String tanggapan,
            HttpServletRequest request) {

        try {
            // Mendapatkan token JWT dari header permintaan
            String token = request.getHeader("Authorization").substring(7);

            // Mengecek apakah token sudah di-blacklist
            if (jwtUtil.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token sudah tidak valid.");
            }

            // Mengekstrak email pengguna dari token
            String email = jwtUtil.extractEmail(token);

            // Memeriksa apakah pengguna diotorisasi sebagai ADMIN
            if (!jwtUtil.validateToken(token, email) || !"ADMIN".equals(jwtUtil.extractRole(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Anda tidak diizinkan untuk mengakses fitur ini.");
            }

            try {
                StatusLaporan statusLaporanDetails = new StatusLaporan();
                statusLaporanDetails.setStatusBaru(statusBaru);
                statusLaporanDetails.setTanggapan(tanggapan);

                // Menyimpan perubahan status laporan dengan gambar jika ada
                StatusLaporan updatedStatusLaporan = statusLaporanService.updateStatusLaporan(pengaduanId, statusLaporanDetails, gambar);

                return ResponseEntity.ok(updatedStatusLaporan);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Status laporan tidak ditemukan.");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tidak ada perubahan.");
            }

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token kadaluwarsa.");
        } catch (Exception e) {
            // Menangani kesalahan yang tidak terduga
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }
}
