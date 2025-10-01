package sistem_pengaduan.demo.config;

//package kuliner_150.demo.config;
//
//import kuliner_150.demo.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private UserService userService;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf().disable()
//            .authorizeRequests()
//            .antMatchers("/api/users/admin/**").hasRole("ADMIN")
//            .antMatchers("/api/users/user/**").hasRole("USER")
//            .antMatchers("/api/users/register", "/api/users/verify-otp", "/api/users/login").permitAll()
//            .anyRequest().authenticated()
//            .and()
//            .formLogin()
//            .loginProcessingUrl("/api/users/login")
//            .defaultSuccessUrl("/api/users/welcome", true)
//            .failureUrl("/api/users/login?error=true")
//            .and()
//            .logout()
//            .logoutUrl("/api/users/logout")
//            .logoutSuccessUrl("/api/users/login?logout=true")
//            .permitAll();
//
//        return http.build();
//    }
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
