package com.bnguimbo.springbootrestserver.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.bnguimbo.springbootrestserver.model.AafRegistrationUser;
import com.bnguimbo.springbootrestserver.service.AafRegistrationUserServiceImpl;
import com.bnguimbo.springbootrestserver.service.AafRegistrationUserServiceImpl.COUNTRY;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AafRegistrationUserReposiroryTest {

	private static final Logger logger = LoggerFactory.getLogger(AafRegistrationUserReposiroryTest.class);

	@Autowired
    private TestEntityManager entityManager;
	@Autowired
    private AafRegistrationUserReposirory aafRegUserRep;

	Date refDate = AafRegistrationUserServiceImpl.addYearsToDate(new Date(), -COUNTRY.FRANCE.getMajYear());
	String refCountryCode = COUNTRY.FRANCE.getIsoCode();

	AafRegistrationUser user1 = new AafRegistrationUser("User 1", refDate, refCountryCode);
	AafRegistrationUser user2 = new AafRegistrationUser("User 2", refDate, refCountryCode);

	@Before
	public void setup(){
	    entityManager.persist(user1);
	    entityManager.persist(user2);
	    entityManager.flush();
	}
	@Test
	public void testFindAllUsers() {
	    final List<AafRegistrationUser> users = aafRegUserRep.findAll();

	    logger.debug("users size = 2");
	    assertTrue("users size = 2", users.size() == 2);
	}

	@Test
    public void testSaveUser(){
		final AafRegistrationUser user3 = new AafRegistrationUser("User 3", refDate, refCountryCode);
		final AafRegistrationUser userSaved =  aafRegUserRep.save(user3);

		logger.debug("UserId is set");
		assertNotNull(userSaved.getUserId());

		logger.debug("UserName == User 3");
		assertTrue("UserName == User 3", "User 3".equals(user3.getUserName()));

		logger.debug("BirthDate == refDate");
		assertTrue("BirthDate == refDate", refDate.equals(user3.getBirthDate()));

		logger.debug("CountryCode == FR");
		assertTrue("CountryCode == FR", refCountryCode.equals(user3.getCountryCode()));
	}

	@Test
	public void testFindByUserNameAndBirthDateAndCountryCode() {
	    final AafRegistrationUser userFromDB = aafRegUserRep.findByUserNameAndBirthDateAndCountryCode(
	    		"User 1", refDate, refCountryCode);

	    logger.debug("Read User 1");
		assertTrue("Read User 1 != null", userFromDB != null);
		assertNotNull(userFromDB.getUserId());

		logger.debug("UserName == 'User 1'");
		assertTrue("UserName == 'User 1'", "User 1".equals(userFromDB.getUserName()));

		logger.debug("BirthDate == refDate");
		assertTrue("BirthDate == refDate", refDate.equals(userFromDB.getBirthDate()));

		logger.debug("CountryCode == 'FR'");
		assertTrue("CountryCode == 'FR'", refCountryCode.equals(userFromDB.getCountryCode()));
	}

	@Test
	public void testFindByUserNameAndBirthDateAndCountryCode_NotFound() {
	    AafRegistrationUser userFromDB = aafRegUserRep.findByUserNameAndBirthDateAndCountryCode(
	    		"User 3", refDate, refCountryCode);

	    logger.debug("Read User 3");
		assertTrue("Read User 3 == null", userFromDB == null);
		userFromDB = aafRegUserRep.findByUserNameAndBirthDateAndCountryCode(
	    		"User 1", new Date(), refCountryCode);

		logger.debug("Read User 1 today");
		assertTrue("Read User 1 today == null", userFromDB == null);
		userFromDB = aafRegUserRep.findByUserNameAndBirthDateAndCountryCode(
	    		"User 1", refDate, "DE");

		logger.debug("Read User 1 DE");
		assertTrue("Read User 1 DE == null", userFromDB == null);
	}

	@Test
	public void testUpdateUser() {
		 AafRegistrationUser userFromDB = aafRegUserRep.findByUserNameAndBirthDateAndCountryCode(
		    		"User 1", refDate, refCountryCode);

		logger.debug("Read User 1 != null");
		assertTrue("Read User 1 != null", userFromDB != null);
		assertNotNull(userFromDB.getUserId());

		logger.debug("Read User 1 PhoneNumber == null");
		assertTrue("Read User 1 PhoneNumber == null", userFromDB.getPhoneNumber() == null);

		logger.debug("Read User 1 Gender == null");
		assertTrue("Read User 1 Gender == null", userFromDB.getGender() == null);

		userFromDB.setUserName("User updated");
		userFromDB.setPhoneNumber("01 02 03 04 05 06");
		userFromDB.setGender("M");
		final AafRegistrationUser userUpdated=  aafRegUserRep.save(userFromDB);

		logger.debug("Read User updated != null");
	    assertNotNull(userUpdated);

	    logger.debug("Read userFromDB == userUpdated");
	    assertTrue("Read userFromDB == userUpdated", userFromDB.equals(userUpdated));

	    logger.debug("Read User updated UserName == 'User updated'");
		assertTrue("Read User updated UserName == 'User updated'", "User updated".equals(userUpdated.getUserName()));

	    logger.debug("Read User updated PhoneNumber == '01 02 03 04 05 06'");
		assertTrue("Read User updated PhoneNumber == '01 02 03 04 05 06'", "01 02 03 04 05 06".equals(userUpdated.getPhoneNumber()));

		logger.debug("Read User updated Gender == 'M'");
		assertTrue("Read User updated Gender == M", "M".equals(userUpdated.getGender()));

		userFromDB = aafRegUserRep.findByUserNameAndBirthDateAndCountryCode(
	    		"User 1", refDate, refCountryCode);
		logger.debug("Read User 1");
		assertTrue("Read User 1 == null", userFromDB == null);

		userFromDB = aafRegUserRep.findByUserNameAndBirthDateAndCountryCode(
	    		"User updated", refDate, refCountryCode);
		logger.debug("Read User updated");
		assertTrue("Read User updated != null", userFromDB != null);
	}
}
