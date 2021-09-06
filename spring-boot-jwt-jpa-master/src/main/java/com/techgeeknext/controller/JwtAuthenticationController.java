package com.techgeeknext.controller;

import com.techgeeknext.config.JwtTokenUtil;
import com.techgeeknext.model.UserDao;
import com.techgeeknext.service.JwtUserDetailsService;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
//	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
	public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDao user) throws Exception {
		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
			if (authentication.isAuthenticated()) {
				JSONObject jsonObject = new JSONObject();
				UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
				String token = jwtTokenUtil.generateToken(userDetails);
				jsonObject.put("token", token);
				return new ResponseEntity<Object>(jsonObject, HttpStatus.OK);
			}
		}
		catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
		return null;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
//	 public ResponseEntity<?> saveUser(@RequestBody UserDto user) throws Exception {
//	 	return ResponseEntity.ok(userDetailsService.save(user));
//	 }
	public ResponseEntity<?> saveUser(@RequestBody UserDao user) throws Exception {
		System.out.println("register");
		System.out.println(user.getUsername());
		System.out.println(user.getPassword());
		return ResponseEntity.ok(userDetailsService.save(user));
	}
}
