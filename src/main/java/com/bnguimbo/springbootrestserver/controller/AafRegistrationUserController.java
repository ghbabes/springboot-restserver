package com.bnguimbo.springbootrestserver.controller;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bnguimbo.springbootrestserver.model.AafRegistrationUser;
import com.bnguimbo.springbootrestserver.service.AafRegistrationUserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/aafRegistration/*")
public class AafRegistrationUserController {

	@Autowired
	private AafRegistrationUserService aafRegistrationUserService;


	@GetMapping(value = "/users")
	public ResponseEntity<Collection<AafRegistrationUser>> getAllAafRegistrationUsers() throws Exception {
		final Collection<AafRegistrationUser> users = aafRegistrationUserService.getAllAafRegistrationUsers();
		return new ResponseEntity<Collection<AafRegistrationUser>>(users, HttpStatus.FOUND);
	}

	@GetMapping(value = "/users/getUser")
	public ResponseEntity<AafRegistrationUser> getAafRegistrationUser(
			@RequestParam(value = "userName", required = true) final String userName,
			@RequestParam(value = "birthDate", required = true) @DateTimeFormat(iso = ISO.DATE) final Date birthDate,
			@RequestParam(value = "countryCode", required = true) final String countryCode) {
		final AafRegistrationUser saveAafUser = aafRegistrationUserService.getAafRegistrationUser(userName, birthDate, countryCode);
 		return new ResponseEntity<AafRegistrationUser>(saveAafUser, HttpStatus.FOUND);
 	}

	@PostMapping(value = "/users/saveUser")
	@Transactional
	public ResponseEntity<AafRegistrationUser> saveAafRegistrationUser(@RequestBody final AafRegistrationUser aafUser) {
		final AafRegistrationUser saveAafUser = aafRegistrationUserService.saveOrUpdateUser(aafUser);
 		return new ResponseEntity<AafRegistrationUser>(saveAafUser, HttpStatus.CREATED);
 	}

}
