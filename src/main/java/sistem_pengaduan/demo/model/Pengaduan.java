package sistem_pengaduan.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "pengaduan")
public class Pengaduan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "judul")
    private String judul;

    @Column(name = "alamat")
    private String alamat;

    @Column(name = "gambar")
    private String gambar;

    @Column(name = "deskripsi")
    private String deskripsi;

    @Enumerated(EnumType.STRING)
    @Column(name = "kategori")
    private Kategori kategori;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    // kelola referensi melingkar (referensi maju)
    @JsonBackReference
    private User user;

    // @CreationTimestamp = digunakan untuk menandai atribut yang nilainya harus
    // diisi secara otomatis saat entitas dibuat dan tidak dapat diubah
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // @UpdateTimestamp = digunakan untuk menandai atribut yang nilainya harus
    // diperbarui secara otomatis setiap kali entitas diperbarui
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "pengaduan", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<StatusLaporan> statusLaporanList = new ArrayList<>();

    public Pengaduan() {
    }

    public Pengaduan(String judul, String alamat, String gambar, String deskripsi, Kategori kategori, Double latitude,
            Double longitude) {
        this.judul = judul;
        this.alamat = alamat;
        this.gambar = gambar;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return this.createdAt.format(formatter);
    }

    public String getUpdatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return this.updatedAt.format(formatter);
    }

    public LocalDateTime setCreatedAt(LocalDateTime now) {
        return createdAt;
    }

    public LocalDateTime setUpdatedAt(LocalDateTime now) {
        return updatedAt;
    }

    // Metode getter untuk statusLaporanList
    public List<StatusLaporan> getStatusLaporanList() {
        return statusLaporanList;
    }

    // Metode setter jika diperlukan
    public void setStatusLaporanList(List<StatusLaporan> statusLaporanList) {
        this.statusLaporanList = statusLaporanList;
    }

    // -------- GETTER UNTUK MENDAPATKAN NAMA & PROFILE IMAGE PEMBUAT --------

    public String getNamaPembuat() {
        return user != null ? user.getNama() : null;
    }

    public String getProfileImagePembuat() {
        return user != null && user.getUserProfile() != null
                ? user.getUserProfile().getProfileImage()
                : null;
    }

    // -------- GETTER UNTUK MENDAPATKAN STATUS LAPORAN & TANGGAPAN --------

    // @Transient
    // // getStatus -> status (get nya dihilangkan, Status s nya lower case)
    // public Status getStatus() {
    // if (getStatusLaporanList() != null && !getStatusLaporanList().isEmpty()) {
    // // .size() - 1) -> mengakses daftar terakhir dari list
    // return getStatusLaporanList().get(getStatusLaporanList().size() -
    // 1).getStatusBaru();
    // }
    // return null;
    // }

    // @Transient
    // // getTanggapan -> tanggapan (get nya dihilangkan, Tanggapan t nya lower
    // case)
    // public String getTanggapan() {
    // // .size() - 1) -> mengakses daftar terakhir dari list
    // if (getStatusLaporanList() != null && !getStatusLaporanList().isEmpty()) {
    // return getStatusLaporanList().get(getStatusLaporanList().size() -
    // 1).getTanggapan();
    // }
    // return null;
    // }

    @Transient
    public String getStatus() {
        if (statusLaporanList != null && !statusLaporanList.isEmpty()) {
            // Get the latest status (last in the list)
            StatusLaporan latestStatus = statusLaporanList.get(statusLaporanList.size() - 1);
            return latestStatus.getStatusBaru() != null ? latestStatus.getStatusBaru().toString() : null;
        }
        return null;
    }

    @Transient
    public String getTanggapan() {
        if (statusLaporanList != null && !statusLaporanList.isEmpty()) {
            // Get the latest status response
            StatusLaporan latestStatus = statusLaporanList.get(statusLaporanList.size() - 1);
            return latestStatus.getTanggapan();
        }
        return null;
    }

    @Transient
    public String getGambarTanggapan() {
        if (statusLaporanList != null && !statusLaporanList.isEmpty()) {
            // Get the latest status image
            StatusLaporan latestStatus = statusLaporanList.get(statusLaporanList.size() - 1);
            return latestStatus.getGambar();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Pengaduan{"
                + "id=" + id
                + ", judul='" + judul + '\''
                + ", alamat='" + alamat + '\''
                + ", gambar='" + gambar + '\''
                + ", deskripsi='" + deskripsi + '\''
                + ", kategori=" + kategori
                + ", latitude=" + latitude
                + ", longitude=" + longitude
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
