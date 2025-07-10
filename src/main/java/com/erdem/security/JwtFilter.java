package com.erdem.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    private static final Logger logger=LoggerFactory.getLogger(JwtFilter.class);

    public JwtFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path= request.getServletPath();
        System.out.println("Servlet path: "+path);

        return path.startsWith("api/auth/"); //Auth not filter
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header= request.getHeader("Authorization");
        String username =null;


        if (header ==null || !header.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        String token=header.substring(7);

        try {
            username=jwtUtils.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){

                UserDetails userDetails= userDetailsService.loadUserByUsername(username);

                if (jwtUtils.isTokenValid(token)){
                    UsernamePasswordAuthenticationToken authtoken= new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authtoken);
                }

            }
        }catch (ExpiredJwtException exception){
            logger.error("Expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }catch (Exception e){

            throw new RuntimeException(e.getMessage());
        }


            filterChain.doFilter(request,response);
    }
}
