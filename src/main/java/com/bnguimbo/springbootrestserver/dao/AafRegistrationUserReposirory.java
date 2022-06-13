package com.bnguimbo.springbootrestserver.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bnguimbo.springbootrestserver.model.AafRegistrationUser;

/**
 * DAO for an application of user registration
 *
 * @author stephaneb
 */
public interface AafRegistrationUserReposirory extends JpaRepository<AafRegistrationUser, Long> {

	//	@Query(value = "select aafRU from AafRegistrationUser aafRU where aafRU.userName = ?1 and aafRU.birthDate = ?2 and aafRU.countryCode = ?3")
	AafRegistrationUser findByUserNameAndBirthDateAndCountryCode(String userName, Date birthDate, String countryCode);

}
