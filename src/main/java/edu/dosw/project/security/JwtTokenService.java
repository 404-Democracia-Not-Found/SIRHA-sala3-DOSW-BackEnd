package edu.dosw.project.security;

public interface JwtTokenService {
    String generateToken(String userId, String username);
    boolean validateToken(String token);
    String getUserIdFromToken(String token);
}