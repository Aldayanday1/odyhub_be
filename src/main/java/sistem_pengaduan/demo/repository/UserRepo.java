/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem_pengaduan.demo.repository;

import sistem_pengaduan.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ALDAYANDAY
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    // for JWT
    User findByEmail(String email);
    // for Search by Name Admin
    User findByNama(String nama);
    // for OTP code
    User findByOtpCode(String otpCode);
}