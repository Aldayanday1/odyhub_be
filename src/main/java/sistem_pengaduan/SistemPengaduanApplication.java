package sistem_pengaduan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemPengaduanApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemPengaduanApplication.class, args);
    }
}
