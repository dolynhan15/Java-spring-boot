package com.techgeeknext.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.techgeeknext.service.JwtUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String token = request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;

		if (token != null && token.startsWith("Bearer ")) {
			jwtToken = token.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

					UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

					if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

						Authentication authentication = new UsernamePasswordAuthenticationToken(
								userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
						if (authentication.isAuthenticated()) {
							SecurityContextHolder.getContext().setAuthentication(authentication);
						}
					}
				}
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			}
		} else {
			logger.warn("First time to create JWT Token");
		}

		chain.doFilter(request, response);
	}

}
