/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem_pengaduan.demo.repository;

import java.util.List;
import java.util.Optional;
import sistem_pengaduan.demo.model.StatusLaporan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ALDAYANDAY
 */
@Repository
public interface StatusLaporanRepo extends JpaRepository<StatusLaporan, Long> {
    // mencari status laporan berdasarkan id pengaduan
    public Optional<StatusLaporan> findByPengaduanId(Long pengaduanId);
    
}
