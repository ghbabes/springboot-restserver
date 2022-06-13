package com.bnguimbo.springbootrestserver.service;

import java.util.Collection;
import java.util.Date;

import com.bnguimbo.springbootrestserver.model.AafRegistrationUser;

/**
 * Service for an application of user registration
 *
 * @author stephaneb
 */
public interface AafRegistrationUserService {

	Collection<AafRegistrationUser> getAllAafRegistrationUsers();

	AafRegistrationUser findByUserNameAndBirthDateAndCountryCode(String userName, Date birthDate, String countryCode);

	AafRegistrationUser getAafRegistrationUser(String userName, Date birthDate, String countryCode);

	AafRegistrationUser saveOrUpdateUser(AafRegistrationUser aafUser);

}
