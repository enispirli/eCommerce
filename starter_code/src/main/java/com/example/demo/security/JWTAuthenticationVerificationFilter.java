package com.example.demo.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.demo.constant.SecurityConstants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import com.auth0.jwt.JWT;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
public class JWTAuthenticationVerificationFilter extends BasicAuthenticationFilter {
    public JWTAuthenticationVerificationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        logger.info("Received request to verify the authenticity of the presented token.");
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);

    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String token = req.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            try {
                String user = JWT.require(HMAC512(SecurityConstants.SECRET.getBytes())).build()
                        .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                        .getSubject();
                if (user != null) {
                    logger.info("Token validation for the user was successful {}  " + user);
                    return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                }
            } catch (JWTDecodeException decodeException) {
                logger.error("JWTDecodeException {} " + decodeException.getMessage());
            } catch (SignatureVerificationException signatureVerificationException) {
                logger.error("SignatureVerificationException {}" + signatureVerificationException.getMessage());
            }
        }
        return null;
    }
}
