/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem_pengaduan.demo.service;

import sistem_pengaduan.demo.model.User;
import sistem_pengaduan.demo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import sistem_pengaduan.demo.model.UserRole;

@Component
public class ScheduledTasks {

    @Autowired
    private UserRepo userRepo;

    @Scheduled(fixedRate = 60000) // Setiap 1 menit
    public void removeExpiredOtps() {
        List<User> users = userRepo.findAll();
        Date now = new Date();
        for (User user : users) {
            if (user.getOtpExpiry() != null && user.getOtpExpiry().before(now) && !user.getRole().equals(UserRole.ADMIN)) {
                if (user.getOtpCode() != null) {
                    if (user.isRegistering()) {
                        // Jika OTP kadaluarsa dan pengguna belum diverifikasi saat registrasi, hapus pengguna
                        userRepo.delete(user);
                    } else {
                        // Jika OTP kadaluarsa dan pengguna belum diverifikasi saat login, hapus hanya OTP dan OTP_EXPIRY
                        user.setOtpCode(null);
                        user.setOtpExpiry(null);
                        userRepo.save(user);
                    }
                }
            }
        }
    }
}
