package sistem_pengaduan.demo.util;

///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package kuliner_150.demo.util;
//
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.SignatureException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import kuliner_150.demo.repository.TokenBlacklistRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
///**
// *
// * @author ALDAYANDAY
// */
//@Component
//public class JwtRequestFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private TokenBlacklistRepository tokenBlacklistRepository;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        final String requestTokenHeader = request.getHeader("Authorization");
//
//        String email = null;
//        String jwtToken = null;
//
//        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
//            jwtToken = requestTokenHeader.substring(7);
//
//            try {
//                if (tokenBlacklistRepository.isTokenBlacklisted(jwtToken)) {
//                    throw new SignatureException("Token has been blacklisted");
//                }
//                email = jwtUtil.extractEmail(jwtToken);
//            } catch (IllegalArgumentException e) {
//                System.out.println("Unable to get JWT Token");
//            } catch (ExpiredJwtException e) {
//                System.out.println("JWT Token has expired");
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
//                return;
//            } catch (SignatureException e) {
//                System.out.println("JWT Token has been blacklisted or is invalid");
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid");
//                return;
//            }
//        } else {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Authorization header is missing or invalid");
//            return;
//        }
//
//        // Jika email tidak null dan token valid, lanjutkan dengan permintaan
//        if (email != null && jwtUtil.validateToken(jwtToken, email)) {
//            // Lakukan apa yang diperlukan untuk memberikan akses ke sumber daya yang dilindungi
//            chain.doFilter(request, response);
//        } else {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//        }
//    }
//}
