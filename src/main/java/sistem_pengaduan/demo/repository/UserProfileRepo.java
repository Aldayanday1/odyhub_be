/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistem_pengaduan.demo.repository;

import sistem_pengaduan.demo.model.User;
import sistem_pengaduan.demo.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ALDAYANDAY
 */
@Repository
public interface UserProfileRepo extends JpaRepository<UserProfile, Long> {
    // Mencari profile berdasarkan pengguna
    UserProfile findByUser(User user);
}
