package com.bnguimbo.springbootrestserver.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bnguimbo.springbootrestserver.dao.AafRegistrationUserReposirory;
import com.bnguimbo.springbootrestserver.exception.BusinessResourceException;
import com.bnguimbo.springbootrestserver.model.AafRegistrationUser;
import com.bnguimbo.springbootrestserver.service.AafRegistrationUserServiceImpl.COUNTRY;

public class AafRegistrationUserServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(AafRegistrationUserServiceImplTest.class);

	private AafRegistrationUserReposirory aafRegUserRep;
	private AafRegistrationUserService aafRegUserSrv;

	@Before
	public void setup() {
		aafRegUserRep = Mockito.mock(AafRegistrationUserReposirory.class);
		aafRegUserSrv = new AafRegistrationUserServiceImpl(aafRegUserRep);
	}

	@Test
	public void testSaveAafUSer() throws Exception {
		AafRegistrationUser user = null;
		AafRegistrationUser saved = null;

		final AafRegistrationUser userMock = new AafRegistrationUser("User 1",
				AafRegistrationUserServiceImpl.addYearsToDate(new Date(), -COUNTRY.FRANCE.getMajYear()), "FR");
		final AafRegistrationUser userMockRes = new AafRegistrationUser("User 1",
				AafRegistrationUserServiceImpl.addYearsToDate(new Date(), -COUNTRY.FRANCE.getMajYear()), "FR");
		userMockRes.setUserId(1L);
		Mockito.when(aafRegUserSrv.saveOrUpdateUser(userMock)).thenReturn(userMockRes);

		// null safe
		try {
			saved = aafRegUserSrv.saveOrUpdateUser(user);
		} catch (final Exception e) {
			logger.debug("testSaveAafUSer null safe");
			assertTrue("BusinessResourceException should be thrown", e instanceof BusinessResourceException);
		}
		assertNull(saved);
		// Mandatory fields
		user = new AafRegistrationUser();
		try {
			saved = aafRegUserSrv.saveOrUpdateUser(user);
		} catch (final Exception e) {
			logger.debug("testSaveAafUSer Mandatory fields (nulls)");
			assertTrue("BusinessResourceException should be thrown", e instanceof BusinessResourceException);
		}
		assertNull(saved);
		// UserName => mandatory not null or blank
		user = new AafRegistrationUser("", null, null);
		try {
			saved = aafRegUserSrv.saveOrUpdateUser(user);
		} catch (final Exception e) {
			logger.debug("testSaveAafUSer UserName => mandatory not null or blank #1");
			assertTrue("BusinessResourceException should be thrown", e instanceof BusinessResourceException);
		}
		assertNull(saved);
		user = new AafRegistrationUser("    ", null, null);
		try {
			saved = aafRegUserSrv.saveOrUpdateUser(user);
		} catch (final Exception e) {
			logger.debug("testSaveAafUSer UserName => mandatory not null or blank #2");
			assertTrue("BusinessResourceException should be thrown", e instanceof BusinessResourceException);
		}
		assertNull(saved);
		// BirthDate => mandatory not null, lower than today (to begin of the day) and give country majority to user
		Date birthDate = AafRegistrationUserServiceImpl.addDaysToDate(new Date(), +1);
		user = new AafRegistrationUser("User 1", birthDate, null);
		try {
			saved = aafRegUserSrv.saveOrUpdateUser(user);
		} catch (final Exception e) {
			logger.debug("testSaveAafUSer BirthDate => mandatory not null, greater than today (to begin of the day) and give country majority to user #1");
			assertTrue("BusinessResourceException should be thrown", e instanceof BusinessResourceException);
		}
		assertNull(saved);
		birthDate = AafRegistrationUserServiceImpl.addDaysToDate(new Date(), 0);
		user = new AafRegistrationUser("User 1", birthDate, null);
		try {
			saved = aafRegUserSrv.saveOrUpdateUser(user);
		} catch (final Exception e) {
			logger.debug("testSaveAafUSer BirthDate => mandatory not null, greater than today (to begin of the day) and give country majority to user #2");
			assertTrue("BusinessResourceException should be thrown", e instanceof BusinessResourceException);
		}
		assertNull(saved);
		// CountryCode => mandatory not null and in allowed countries
		birthDate = AafRegistrationUserServiceImpl.addDaysToDate(new Date(), -1);
		user = new AafRegistrationUser("User 1", birthDate, null);
		try {
			saved = aafRegUserSrv.saveOrUpdateUser(user);
		} catch (final Exception e) {
			logger.debug("testSaveAafUSer BirthDate => CountryCode => mandatory not null and in allowed countries #1");
			assertTrue("BusinessResourceException should be thrown", e instanceof BusinessResourceException);
		}
		assertNull(saved);
		user = new AafRegistrationUser("User 1", birthDate, "CH");
		try {
			saved = aafRegUserSrv.saveOrUpdateUser(user);
		} catch (final Exception e) {
			logger.debug("testSaveAafUSer BirthDate => CountryCode => mandatory not null and in allowed countries #2");
			assertTrue("BusinessResourceException should be thrown", e instanceof BusinessResourceException);
		}
		assertNull(saved);
		user = new AafRegistrationUser("User 1", birthDate, "DE");
		try {
			saved = aafRegUserSrv.saveOrUpdateUser(user);
		} catch (final Exception e) {
			logger.debug("testSaveAafUSer BirthDate => CountryCode => mandatory not null and in allowed countries #3");
			assertTrue("BusinessResourceException should be thrown", e instanceof BusinessResourceException);
		}
		assertNull(saved);
		user = new AafRegistrationUser("User 1", birthDate, "FR");
		try {
			saved = aafRegUserSrv.saveOrUpdateUser(user);
		} catch (final Exception e) {
			logger.debug("testSaveAafUSer BirthDate => mandatory not null, greater than today (to begin of the day) and give country majority to user #3");
			assertTrue("BusinessResourceException should be thrown", e instanceof BusinessResourceException);
		}
		assertNull(saved);

		birthDate = AafRegistrationUserServiceImpl.addYearsToDate(new Date(), -COUNTRY.FRANCE.getMajYear());
		user = new AafRegistrationUser("User 1", birthDate, "FR");
		try {
			saved = aafRegUserSrv.saveOrUpdateUser(user);
		} catch (final Exception e) {
			assertFalse("BusinessResourceException should not be thrown", !(e instanceof BusinessResourceException));
		}
		assertNotNull(saved);
		assertFalse("User not yet equals to saved user", user.equals(saved));
		user.setUserId(saved.getUserId());
		assertTrue("User equals to saved user", user.equals(saved));
		logger.debug("testSaveAafUSer Mandatory fields");
	}

}