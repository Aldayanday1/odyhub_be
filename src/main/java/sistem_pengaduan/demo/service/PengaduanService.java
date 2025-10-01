/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem_pengaduan.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import sistem_pengaduan.demo.model.Kategori;
import sistem_pengaduan.demo.model.Status;
import sistem_pengaduan.demo.model.StatusLaporan;
import sistem_pengaduan.demo.repository.StatusLaporanRepo;
import org.springframework.data.domain.Sort;
import sistem_pengaduan.demo.model.Pengaduan;
import sistem_pengaduan.demo.repository.PengaduanRepo;

/**
 *
 * @author ALDAYANDAY
 */
@Service
public class PengaduanService {

    @Autowired
    private PengaduanRepo pengaduanRepo;
    
    @Autowired
    private StatusLaporanRepo statusLaporanRepo;

    public List<Pengaduan> getAllPengaduan() {
        return pengaduanRepo.findAll();
    }

    public Optional<Pengaduan> getPengaduanById(int id) {
        return pengaduanRepo.findById(id);
    }
    
    // ---- STATUS DEFAULT SAAT PENGADUAN DI BUAT -> PENDING ----

    public Pengaduan addPengaduan(Pengaduan pengaduan) {
        pengaduan.setCreatedAt(LocalDateTime.now());
        pengaduan.setUpdatedAt(LocalDateTime.now());
        Pengaduan savedPengaduan = pengaduanRepo.save(pengaduan);
        
        StatusLaporan statusLaporan = new StatusLaporan();
        statusLaporan.setPengaduan(savedPengaduan);
        
        // status dan tanggapan di set ke Pending & Null
        
        statusLaporan.setStatusSebelumnya(Status.PENDING);
        statusLaporan.setStatusBaru(Status.PENDING);
        statusLaporan.setTanggapan(null);
        
        statusLaporan.setChangedAt(LocalDateTime.now());
        statusLaporanRepo.save(statusLaporan);

        return savedPengaduan;
    }

    public Pengaduan updatePengaduan(Pengaduan pengaduan) {
        pengaduan.setUpdatedAt(LocalDateTime.now());
        return pengaduanRepo.save(pengaduan);
    }

    public void deletePengaduan(int id) {
        pengaduanRepo.deleteById(id);
    }

    public List<Pengaduan> getPengaduanByKategori(Kategori kategori) {
        return pengaduanRepo.findByKategori(kategori);
    }

    public List<Pengaduan> searchPengaduanByJudul(String judul) {
        return pengaduanRepo.findByJudulContainingIgnoreCase(judul);
    }

    // mendapat data my-pengaduan dari id pengguna miliknya
    public List<Pengaduan> getPengaduanByUser(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt", "createdAt");
        return pengaduanRepo.findByUserId(userId, sort);
    }

    // urutan data berdasarkan waktu terbaru
    public List<Pengaduan> getAllPengaduansSortedByDate() {
        return pengaduanRepo.findAll(Sort.by(Sort.Direction.DESC, "updatedAt", "createdAt"));
    }
    
    // mendapatkan data pengaduan berdasarkan status laporan
    public List<Pengaduan> getPengaduanByStatus(Status status) {
        return pengaduanRepo.findByStatusLaporanStatus(status);
    }
    
    // Mengambil jumlah data pengaduan per hari berdasarkan tanggal
    public Integer countPengaduanByDate(LocalDate date) {
        return pengaduanRepo.countByCreatedAtDate(date);
    }
}
