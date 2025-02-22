package com.serhat.jwt.jwt;

import com.serhat.jwt.entity.Token;
import com.serhat.jwt.entity.enums.TokenStatus;
import com.serhat.jwt.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final TokenRepository tokenRepository;

    public void blacklistToken(String token) {
        Token storedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        storedToken.setTokenStatus(TokenStatus.LOGGED_OUT);
        storedToken.setExpired_at(new Date());
        tokenRepository.save(storedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenRepository.findByToken(token)
                .map(storedToken -> storedToken.getTokenStatus() != TokenStatus.ACTIVE)
                .orElse(true);
    }
}
