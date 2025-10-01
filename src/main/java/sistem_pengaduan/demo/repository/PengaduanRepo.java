/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package sistem_pengaduan.demo.repository;

import java.time.LocalDate;
import sistem_pengaduan.demo.model.Kategori;
import java.util.List;
import sistem_pengaduan.demo.model.Status;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sistem_pengaduan.demo.model.Pengaduan;

/**
 *
 * @author ALDAYANDAY
 */
@Repository
public interface PengaduanRepo extends JpaRepository<Pengaduan, Integer> {

    // mencari daftar pengaduan berdasarkan kategori yang diberikan
    public List<Pengaduan> findByKategori(Kategori kategori);

    // mencari daftar pengaduan berdasarkan String judul dengan mengabaikan besar kecil huruf tsb   
    List<Pengaduan> findByJudulContainingIgnoreCase(String judul);

    // mencari data my-pengaduan dari id pengguna miliknya
    List<Pengaduan> findByUserId(Long userId, Sort sort);

    // memilih pengaduan 'alias p', lalu menggabungkannya dengan status baru di 'sl'/entitas status laporan
    @Query("SELECT p FROM Pengaduan p JOIN p.statusLaporanList sl WHERE sl.statusBaru = :status ORDER BY sl.changedAt DESC")
    List<Pengaduan> findByStatusLaporanStatus(@Param("status") Status status);
    
    // menghitung jumlah data pengaduan berdasarkan hari pembuatan
    @Query("SELECT COUNT(p) FROM Pengaduan p WHERE DATE(p.createdAt) = :date")
    Integer countByCreatedAtDate(@Param("date") LocalDate date);
}
