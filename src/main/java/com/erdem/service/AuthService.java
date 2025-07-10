package com.erdem.service;

import com.erdem.dto.JwtResponse;
import com.erdem.dto.LoginRequest;
import com.erdem.dto.RegisterRequest;
import com.erdem.model.RefreshToken;
import com.erdem.model.User;
import com.erdem.repository.IRefreshTokenRepository;
import com.erdem.repository.IUserRepository;
import com.erdem.security.JwtUtils;
import exception.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final IRefreshTokenRepository refreshTokenRepository;

    public AuthService(PasswordEncoder passwordEncoder, IUserRepository userRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenService refreshTokenService, IRefreshTokenRepository refreshTokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void register(RegisterRequest request){
        User user= new User();

        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

    }

    public JwtResponse login(LoginRequest request){
        Authentication authentication= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),request.getPassword()
                )
        );

        UserDetails userDetails= (UserDetails) authentication.getPrincipal();
        String accessToken= jwtUtils.generateToken(userDetails.getUsername(),
                userDetails.getAuthorities().stream().findFirst().get().getAuthority()
                );
        RefreshToken refreshToken= refreshTokenService.createRefreshToken(userDetails.getUsername());

        return new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                userDetails.getUsername(),
                userDetails.getAuthorities().stream().findFirst().get().getAuthority()
        );
    }

    public JwtResponse refreshToken(String refreshTokenStr){

        RefreshToken oldRefresh= refreshTokenService.findByToken(refreshTokenStr).orElseThrow(()->new NotFoundException("Refresh token not found"));

        refreshTokenService.verifyExpiration(oldRefresh);

        User user= oldRefresh.getUser();

        refreshTokenService.deleteByUsername(user.getUsername());

        RefreshToken newRefresh=refreshTokenService.createRefreshToken(user.getUsername());

        String newAccessToken= jwtUtils.generateToken(user.getUsername(), "ROLE_"+user.getRole().name());

        return new JwtResponse(newAccessToken,newRefresh.getToken(), user.getUsername(), "ROLE_"+user.getRole().name());
    }

    public void logout(String refreshToken){
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            refreshTokenService.deleteByUsername(token.getUser().getUsername());
        });
    }
}
