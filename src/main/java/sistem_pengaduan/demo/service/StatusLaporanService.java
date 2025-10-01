/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem_pengaduan.demo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import sistem_pengaduan.demo.model.StatusLaporan;
import sistem_pengaduan.demo.repository.StatusLaporanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ALDAYANDAY
 */
@Service
public class StatusLaporanService {

    @Autowired
    private StatusLaporanRepo statusLaporanRepo;

    @Value("${upload.folder}")
    private String uploadFolder;

    public StatusLaporan updateStatusLaporan(Long pengaduanId, StatusLaporan statusLaporanDetails, MultipartFile gambar) {

        // Cari StatusLaporan berdasarkan ID Pengaduan
        StatusLaporan statusLaporan = statusLaporanRepo.findByPengaduanId(pengaduanId)
                .orElseThrow(() -> new NoSuchElementException("Status laporan tidak ditemukan"));

        // Cek apakah ada perubahan pada status dan tanggapan
        boolean isChanged = false;

        if (!statusLaporan.getStatusBaru().equals(statusLaporanDetails.getStatusBaru())) {
            statusLaporan.setStatusSebelumnya(statusLaporan.getStatusBaru());
            statusLaporan.setStatusBaru(statusLaporanDetails.getStatusBaru());
            isChanged = true;
        }

        if (statusLaporan.getTanggapan() == null || !statusLaporan.getTanggapan().equals(statusLaporanDetails.getTanggapan())) {
            statusLaporan.setTanggapan(statusLaporanDetails.getTanggapan());
            isChanged = true;
        }

        // Set gambar jika ada yang diunggah
        if (gambar != null && !gambar.isEmpty()) {
            String gambarUrl = saveImage(gambar);
            statusLaporan.setGambar(gambarUrl);
            isChanged = true;
        }

        if (!isChanged) {
            throw new IllegalArgumentException("Tidak ada perubahan");
        }

        statusLaporan.setChangedAt(LocalDateTime.now()); // Pastikan changedAt diupdate dengan waktu saat ini

        // Simpan perubahan StatusLaporan
        statusLaporan = statusLaporanRepo.save(statusLaporan);
        statusLaporanRepo.flush();

        return statusLaporan;
    }

    private String saveImage(MultipartFile gambar) {
        try {
            // Mendapatkan nama file asli
            String originalFilename = gambar.getOriginalFilename();

            // Membuat path lengkap untuk menyimpan file
            Path path = Paths.get(uploadFolder + File.separator + originalFilename);

            // Menyimpan file ke folder yang telah ditentukan
            Files.write(path, gambar.getBytes());

            // Mengembalikan URL yang dapat diakses secara publik
            return "http://192.168.56.1:8080/api/users/uploads/" + originalFilename; // Sesuaikan dengan URL Anda
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan gambar: " + e.getMessage());
        }
    }
}

// this.fileStorageBaseUrl = "http://192.168.56.1:8080/api/users/uploads/"; // Ganti dengan URL base yang sesuai