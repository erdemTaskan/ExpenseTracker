package com.erdem.service;

import com.erdem.model.RefreshToken;
import com.erdem.model.User;
import com.erdem.repository.IRefreshTokenRepository;
import com.erdem.repository.IUserRepository;
import exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_DURATION= 1000L * 60 * 60 * 4;

    private final IUserRepository userRepository;
    private final IRefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(IUserRepository userRepository, IRefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(String username){
        User user= userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found "+username));

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();

        RefreshToken refreshToken=new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION));

        return refreshTokenRepository.save(refreshToken);
    }


    public RefreshToken verifyExpiration(RefreshToken token){

        if (token.getExpiryDate().isBefore(Instant.now())){
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh Token has expired...");
        }

        return token;
    }

    @Transactional
    public void deleteByUsername(String username){
        User user= userRepository.findByUsername(username).orElseThrow(()->new NotFoundException("User not found "+username));

        refreshTokenRepository.deleteByUser(user);
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }
}
