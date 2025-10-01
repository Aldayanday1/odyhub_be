package sistem_pengaduan.demo.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import sistem_pengaduan.demo.model.Kategori;
import sistem_pengaduan.demo.model.Status;
import sistem_pengaduan.demo.model.User;
import sistem_pengaduan.demo.repository.UserRepo;
import sistem_pengaduan.demo.service.UserService;
import sistem_pengaduan.demo.util.JwtUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import sistem_pengaduan.demo.model.Pengaduan;
import sistem_pengaduan.demo.service.PengaduanService;

/**
 *
 * @author ALDAYANDAY
 */
@RestController
@RequestMapping("/api/users")
public class PengaduanController {

    @Autowired
    private PengaduanService pengaduanService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ----------------------- ADD PENGADUAN -----------------------
    
    @PostMapping("/add")
    public ResponseEntity<?> addPengaduan(HttpServletRequest request,
            @RequestParam("gambar") MultipartFile gambar,
            @RequestParam("judul") String judul,
            @RequestParam("alamat") String alamat,
            @RequestParam("deskripsi") String deskripsi,
            @RequestParam("kategori") Kategori kategori,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude) {

        try {
            // Mengambil token JWT dari header permintaan
            String token = request.getHeader("Authorization").substring(7);

            // Mengecek apakah token sudah di-blacklist
            if (jwtUtil.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token sudah tidak valid.");
            }

            // Mengekstrak email pengguna dari token
            String email = jwtUtil.extractEmail(token);

            // Memeriksa apakah pengguna diotorisasi
            if (!jwtUtil.validateToken(token, email) || !"USER".equals(jwtUtil.extractRole(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token tidak valid atau peran tidak sesuai.");
            }

            // Mengambil pengguna berdasarkan email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pengguna tidak ditemukan.");
            }

            // Menyimpan gambar ke penyimpanan dan mendapat path-nya
            String gambarPath = fileStorageService.storeFile(gambar);
            
            // Membuat instance baru dari Pengaduan
            Pengaduan pengaduan = new Pengaduan(judul, alamat, gambarPath, deskripsi, kategori, latitude, longitude);

            // Menetapkan pengguna untuk Pengaduan
            pengaduan.setUser(user);

            // Menyimpan Pengaduan
            Pengaduan savedPengaduan = pengaduanService.addPengaduan(pengaduan);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedPengaduan);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token kadaluwarsa.");
        } catch (Exception e) {
            // Tangani kesalahan yang tidak terduga
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    // ------------------------- GET ALL -------------------------
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllPengaduans() {
        try {
            List<Pengaduan> pengaduans = pengaduanService.getAllPengaduansSortedByDate();
            return ResponseEntity.ok(pengaduans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

   // ----------------------- GET MY-PENGADUAN -----------------------
    
    @GetMapping("/my-pengaduan")
    public ResponseEntity<?> getMyPengaduan(HttpServletRequest request) {
        try {
            // Mendapatkan token JWT dari header permintaan
            String token = request.getHeader("Authorization").substring(7);

            // Mengecek apakah token sudah di-blacklist
            if (jwtUtil.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token sudah tidak valid.");
            }

            // Mengekstrak email pengguna dari token
            String email = jwtUtil.extractEmail(token);

            // Memeriksa apakah pengguna diotorisasi sebagai USER
            if (!jwtUtil.validateToken(token, email) || !"USER".equals(jwtUtil.extractRole(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Anda tidak diizinkan untuk mengakses fitur ini.");
            }

            // Mengambil pengguna berdasarkan email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pengguna tidak ditemukan.");
            }

            // Mendapatkan daftar pengaduan yang dibuat oleh pengguna
            List<Pengaduan> pengaduanList = pengaduanService.getPengaduanByUser(user.getId());
            return ResponseEntity.ok(pengaduanList);

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token kadaluwarsa.");
        } catch (Exception e) {
            // Menangani kesalahan yang tidak terduga
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    // ------------- GET PENGADUAN BY STATUS (PENDING, PROGRESS, DONE) ----------- ADMIN
    
    @GetMapping("/pengaduan-by-status/{status}")
    public ResponseEntity<?> getPengaduanByStatus(@PathVariable Status status) {
        try {
            List<Pengaduan> pengaduans = pengaduanService.getPengaduanByStatus(status);
            return ResponseEntity.ok(pengaduans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    // ----------------------- GET GRAPH COUNT ----------------------- ADMIN
    
    @GetMapping("/daily-count")
    public ResponseEntity<?> getDailyPengaduanCount(HttpServletRequest request) {

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

            // Mendapatkan tanggal hari ini
            LocalDate today = LocalDate.now();

            // Map untuk menyimpan jumlah data pengaduan per hari
            Map<String, Integer> dailyCounts = new HashMap<>();

            // Loop untuk mengambil data pengaduan per hari untuk 7 hari terakhir, dimulai dari hari ini mundur
            for (int i = 0; i < 7; i++) {

                // Mengurangi hari dari tanggal hari ini
                LocalDate date = today.minusDays(i);

                // Menghitung jumlah data pengaduan untuk tanggal ini
                Integer count = pengaduanService.countPengaduanByDate(date);

                // Menyimpan jumlah data ke dalam map, menggunakan nama hari sebagai kunci
                dailyCounts.put(date.getDayOfWeek().toString(), count);
            }

            return ResponseEntity.ok(dailyCounts);
            
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token kadaluwarsa.");
        } catch (Exception e) {
            // Menangani kesalahan yang tidak terduga
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    // ---------------------------- GET BY ID ---------------------------- 
    
    @GetMapping("/{id}")
    public Optional<Pengaduan> getPengaduanById(@PathVariable("id") Integer id) {
        return pengaduanService.getPengaduanById(id);
    }
    
    // ----------------------- GET SERVE FILE (IMAGE) ----------------------- 
    
    // Serve File (Mengakses gambar yg ada di server lalu menampilkannya)
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = fileStorageService.loadFileAsResource(filename);
        try {
            // Mendapatkan ekstensi file untuk menentukan tipe konten respons
            String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);

            // Tentukan tipe konten respons berdasarkan ekstensi file
            MediaType mediaType;
            mediaType = switch (fileExtension.toLowerCase()) {
                case "jpg", "jpeg" ->
                    MediaType.IMAGE_JPEG;
                case "png" ->
                    MediaType.IMAGE_PNG;
                case "gif" ->
                    MediaType.IMAGE_GIF;
                default ->
                    MediaType.APPLICATION_OCTET_STREAM;
            };

            // Jika jenis file tidak dikenali, atur tipe konten respons menjadi APPLICATION_OCTET_STREAM
            if (mediaType.equals(MediaType.APPLICATION_OCTET_STREAM)) {
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline") // Mengatur untuk menampilkan gambar di browser
                        .body(file);
            } else {
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .body(file);
            }
        } catch (Exception e) {
            // Tangani jika file tidak ditemukan atau terjadi kesalahan lainnya
            return ResponseEntity.notFound().build();
        }
    }

    // ---------------------------- UPDATE PENGADUAN ---------------------------- 
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePengaduan(HttpServletRequest request, @PathVariable("id") Integer id,
            @RequestParam(value = "gambar", required = false) MultipartFile gambar,
            @RequestParam(value = "judul", required = false) String judul,
            @RequestParam(value = "alamat", required = false) String alamat,
            @RequestParam(value = "deskripsi", required = false) String deskripsi,
            @RequestParam(value = "kategori", required = false) Kategori kategori,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude) {

        try {
            // Mendapatkan token JWT dari header permintaan
            String token = request.getHeader("Authorization").substring(7);

            // Mengecek apakah token sudah di-blacklist
            if (jwtUtil.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token sudah tidak valid.");
            }

            // Mengekstrak email pengguna dari token
            String email = jwtUtil.extractEmail(token);

            // Memeriksa apakah pengguna diotorisasi sebagai USER
            if (!jwtUtil.validateToken(token, email) || !"USER".equals(jwtUtil.extractRole(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Anda tidak diizinkan untuk mengakses fitur ini.");
            }

            // Mengambil pengguna berdasarkan email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pengguna tidak ditemukan.");
            }

            // Memeriksa apakah pengguna memiliki hak akses untuk mengubah pengaduan
            Optional<Pengaduan> optionalPengaduan = pengaduanService.getPengaduanById(id);
            if (optionalPengaduan.isPresent()) {
                Pengaduan pengaduan = optionalPengaduan.get();

                // Memeriksa apakah pengaduan terkait dengan pengguna yang melakukan permintaan
                if (!pengaduan.getUser().getId().equals(user.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Anda tidak diizinkan untuk mengubah pengaduan ini.");
                }

                // Melakukan pembaruan data pengaduan jika terdapat perubahan
                boolean adaPerubahan = false;

                if (judul != null && !judul.equals(pengaduan.getJudul())) {
                    pengaduan.setJudul(judul);
                    adaPerubahan = true;
                }

                if (alamat != null && !alamat.equals(pengaduan.getAlamat())) {
                    pengaduan.setAlamat(alamat);
                    adaPerubahan = true;
                }

                // Sisipkan pengecekan untuk perubahan gambar
                if (gambar != null && !gambar.isEmpty()) {
                    String gambarPath = fileStorageService.storeFile(gambar);
                    pengaduan.setGambar(gambarPath);
                    adaPerubahan = true;
                }

                if (deskripsi != null && !deskripsi.equals(pengaduan.getDeskripsi())) {
                    pengaduan.setDeskripsi(deskripsi);
                    adaPerubahan = true;
                }

                if (kategori != null && !kategori.equals(pengaduan.getKategori())) {
                    pengaduan.setKategori(kategori);
                    adaPerubahan = true;
                }

                if (latitude != null && !latitude.equals(pengaduan.getLatitude())) {
                    pengaduan.setLatitude(latitude);
                    adaPerubahan = true;
                }

                if (longitude != null && !longitude.equals(pengaduan.getLongitude())) {
                    pengaduan.setLongitude(longitude);
                    adaPerubahan = true;
                }

                // Jika tidak ada perubahan pada data
                if (!adaPerubahan) {
                    return ResponseEntity.badRequest().body("Tidak ada perubahan yang dilakukan.");
                }

                // Menetapkan waktu pembaruan
                pengaduan.setUpdatedAt(LocalDateTime.now());

                // Menyimpan perubahan
                pengaduanService.updatePengaduan(pengaduan);

                return ResponseEntity.ok(pengaduan);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pengaduan tidak ditemukan.");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token kadaluwarsa.");
        } catch (Exception e) {
            // Menangani kesalahan yang tidak terduga
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    // ---------------------------- DELETE PENGADUAN ---------------------------- 
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePengaduan(HttpServletRequest request, @PathVariable("id") Integer id) {
        try {
            // Mendapatkan token JWT dari header permintaan
            String token = request.getHeader("Authorization").substring(7);

            // Mengecek apakah token sudah di-blacklist
            if (jwtUtil.isTokenBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token sudah tidak valid.");
            }

            // Mengekstrak email pengguna dari token
            String email = jwtUtil.extractEmail(token);

            // Memeriksa apakah pengguna diotorisasi sebagai USER
            if (!jwtUtil.validateToken(token, email) || !"USER".equals(jwtUtil.extractRole(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Anda tidak diizinkan untuk mengakses fitur ini.");
            }

            // Mengambil pengguna berdasarkan email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pengguna tidak ditemukan.");
            }

            // Memeriksa apakah pengguna memiliki hak akses untuk menghapus pengaduan
            Optional<Pengaduan> optionalPengaduan = pengaduanService.getPengaduanById(id);
            if (optionalPengaduan.isPresent()) {
                Pengaduan pengaduan = optionalPengaduan.get();

                // Memeriksa apakah pengaduan terkait dengan pengguna yang melakukan permintaan
                if (!pengaduan.getUser().getId().equals(user.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Anda tidak diizinkan untuk menghapus pengaduan ini.");
                }

                // Menghapus pengaduan
                pengaduanService.deletePengaduan(id);
                return ResponseEntity.ok("Pengaduan berhasil dihapus.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token kadaluwarsa.");
        } catch (Exception e) {
            // Menangani kesalahan yang tidak terduga
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    // ---------------------------- FILTERING BY CATEGORY ---------------------------- 
    
    @GetMapping("/kategori/infrastruktur")
    public List<Pengaduan> getInfrastruktur() {
        return pengaduanService.getPengaduanByKategori(Kategori.INFRASTRUKTUR);
    }

    @GetMapping("/kategori/lingkungan")
    public List<Pengaduan> getLingkungan() {
        return pengaduanService.getPengaduanByKategori(Kategori.LINGKUNGAN);
    }

    @GetMapping("/kategori/transportasi")
    public List<Pengaduan> getTransportasi() {
        return pengaduanService.getPengaduanByKategori(Kategori.TRANSPORTASI);
    }

    @GetMapping("/kategori/keamanan")
    public List<Pengaduan> getKeamanan() {
        return pengaduanService.getPengaduanByKategori(Kategori.KEAMANAN);
    }

    @GetMapping("/kategori/kesehatan")
    public List<Pengaduan> getKesehatan() {
        return pengaduanService.getPengaduanByKategori(Kategori.KESEHATAN);
    }

    @GetMapping("/kategori/pendidikan")
    public List<Pengaduan> getPendidikan() {
        return pengaduanService.getPengaduanByKategori(Kategori.PENDIDIKAN);
    }

    @GetMapping("/kategori/sosial")
    public List<Pengaduan> getSosial() {
        return pengaduanService.getPengaduanByKategori(Kategori.SOSIAL);
    }

    @GetMapping("/kategori/izin")
    public List<Pengaduan> getIzin() {
        return pengaduanService.getPengaduanByKategori(Kategori.IZIN);
    }

    @GetMapping("/kategori/birokrasi")
    public List<Pengaduan> getBirokrasi() {
        return pengaduanService.getPengaduanByKategori(Kategori.BIROKRASI);
    }

    @GetMapping("/kategori/lainnya")
    public List<Pengaduan> getLainnya() {
        return pengaduanService.getPengaduanByKategori(Kategori.LAINNYA);
    }

    @GetMapping("/kategori/{kategori}")
    public List<Pengaduan> getByKategori(@PathVariable("kategori") Kategori kategori) {
        return pengaduanService.getPengaduanByKategori(kategori);
    }

    // ------------- SEARCH --------------- /search?judul=...
    @GetMapping("/search")
    public List<Pengaduan> searchPengaduan(@RequestParam("judul") String judul) {
        return pengaduanService.searchPengaduanByJudul(judul);
    }

}
