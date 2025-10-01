/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem_pengaduan.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author ALDAYANDAY
 */
@Entity
@Table(name = "status_laporan")
public class StatusLaporan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pengaduan_id", nullable = false)
    @JsonBackReference
    private Pengaduan pengaduan;

    // STATUS ENUM DEFAULT SAAT PENGADUAN DI BUAT -> PENDING
    @Enumerated(EnumType.STRING)
    @Column(name = "status_sebelumnya")
    private Status statusSebelumnya = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_baru")
    private Status statusBaru = Status.PENDING;

    @Column(name = "tanggapan")
    private String tanggapan;

    @CreationTimestamp
    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "gambar")
    private String gambar;

    // Getter dan Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pengaduan getPengaduan() {
        return pengaduan;
    }

    public void setPengaduan(Pengaduan pengaduan) {
        this.pengaduan = pengaduan;
    }

    public Status getStatusSebelumnya() {
        return statusSebelumnya;
    }

    public void setStatusSebelumnya(Status statusSebelumnya) {
        this.statusSebelumnya = statusSebelumnya;
    }

    public Status getStatusBaru() {
        return statusBaru;
    }

    public void setStatusBaru(Status statusBaru) {
        this.statusBaru = statusBaru;
    }

    public String getTanggapan() {
        return tanggapan;
    }

    public void setTanggapan(String tanggapan) {
        this.tanggapan = tanggapan;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

}
