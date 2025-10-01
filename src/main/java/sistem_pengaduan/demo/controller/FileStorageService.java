/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem_pengaduan.demo.controller;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.stereotype.Service;

/**
 *
 * @author ALDAYANDAY
 */
@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final String fileStorageBaseUrl; // URL base untuk akses gambar

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        this.fileStorageBaseUrl = "http://192.168.56.1:8080/api/users/uploads/"; // Ganti dengan URL base yang sesuai
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile gambar) {
        if (gambar == null || gambar.isEmpty()) {
            return null; // Jika gambar kosong, langsung kembalikan null
        }

        String fileName = StringUtils.cleanPath(gambar.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(gambar.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Mengembalikan URL relatif ke direktori penyimpanan file eksternal
            return fileStorageBaseUrl + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    // Menampilkan gambar yang disimpan dalam server
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }

    public String getFileStorageBaseUrl() {
        return this.fileStorageBaseUrl;
    }
}
