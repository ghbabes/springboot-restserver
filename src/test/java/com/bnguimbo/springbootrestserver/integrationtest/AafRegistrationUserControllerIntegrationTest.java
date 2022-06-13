package com.bnguimbo.springbootrestserver.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.bnguimbo.springbootrestserver.controller.AafRegistrationUserControllerTest;
import com.bnguimbo.springbootrestserver.model.AafRegistrationUser;
import com.bnguimbo.springbootrestserver.service.AafRegistrationUserServiceImpl;
import com.bnguimbo.springbootrestserver.service.AafRegistrationUserServiceImpl.COUNTRY;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AafRegistrationUserControllerIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;
	private static final String URL = "http://localhost:8080";// url du serveur REST. Cette url peut être celle d'un //
																// serveur distant

	Date refDate = AafRegistrationUserServiceImpl.addYearsToDate(new Date(), -COUNTRY.FRANCE.getMajYear());
	String refCountryCode = COUNTRY.FRANCE.getIsoCode();

	AafRegistrationUser user1 = new AafRegistrationUser("User 1", refDate, refCountryCode);
	AafRegistrationUser user2 = new AafRegistrationUser("User 2", refDate, refCountryCode);

	private String getURLWithPort(final String uri) {
		return URL + uri;
	}

	@Before
	public void setUp() {
		ResponseEntity<AafRegistrationUser> userEntitySaved = restTemplate
				.postForEntity(getURLWithPort("/springboot-restserver/aafRegistration/users/saveUser"), user1, AafRegistrationUser.class);
		userEntitySaved = restTemplate
				.postForEntity(getURLWithPort("/springboot-restserver/aafRegistration/users/saveUser"), user2, AafRegistrationUser.class);

	}

	@Test
	public void testFindAllUsers() throws Exception {

		final ResponseEntity<Object> responseEntity = restTemplate
				.getForEntity(getURLWithPort("/springboot-restserver/aafRegistration/users"), Object.class);
		assertNotNull(responseEntity);
		@SuppressWarnings("unchecked")
		final
		Collection<AafRegistrationUser> userCollections = (Collection<AafRegistrationUser>) responseEntity.getBody();
		assertEquals("Réponse inattendue", HttpStatus.FOUND.value(), responseEntity.getStatusCodeValue());
		assertNotNull(userCollections);
		assertEquals(2, userCollections.size());
	}

	@Test
	public void testSaveUser() throws Exception {

		final AafRegistrationUser userToSave = new AafRegistrationUser("UserToSave", refDate, refCountryCode);
		final ResponseEntity<AafRegistrationUser> userEntitySaved = restTemplate
				.postForEntity(getURLWithPort("/springboot-restserver/aafRegistration/users/saveUser"), userToSave, AafRegistrationUser.class);
		assertNotNull(userEntitySaved);
		// On vérifie le code de réponse HTTP est celui attendu
		assertEquals("Réponse inattendue", HttpStatus.CREATED.value(), userEntitySaved.getStatusCodeValue());
		final AafRegistrationUser userSaved = userEntitySaved.getBody();
		assertNotNull(userSaved);
		assertEquals(userToSave.getUserName(), userSaved.getUserName());
		assertEquals(AafRegistrationUserServiceImpl.convertToBeginOfDay(userToSave.getBirthDate()), userSaved.getBirthDate());
		assertEquals(userToSave.getCountryCode(), userSaved.getCountryCode());
		assertNotNull("ID should be initialized", userSaved.getUserId());
	}

	@Test
	public void testFindByUserNameAndBirthDateAndCountryCode() {
		final String rqParams = "?userName=User 1" +
						"&birthDate=" + AafRegistrationUserControllerTest.convertDateToString(refDate, "yyyy-MM-dd") +
						"&countryCode=FR";
		final ResponseEntity<AafRegistrationUser> responseEntity = restTemplate
				.getForEntity(getURLWithPort("/springboot-restserver/aafRegistration/users/getUser" + rqParams), AafRegistrationUser.class);
		assertNotNull(responseEntity);
		// On vérifie le code de réponse HTTP est celui attendu
		assertEquals("Réponse inattendue", HttpStatus.FOUND.value(), responseEntity.getStatusCodeValue());
		final AafRegistrationUser userFound = responseEntity.getBody();
		assertNotNull(userFound);
		assertEquals(userFound.getUserName(), "User 1");
		assertEquals(userFound.getBirthDate(), AafRegistrationUserServiceImpl.convertToBeginOfDay(refDate));
		assertEquals(userFound.getCountryCode(), refCountryCode);
		assertNotNull("ID should be initialized", userFound.getUserId());
	}

	@Test
	public void testNotFindByUserNameAndBirthDateAndCountryCode() {
		final String rqParams = "?userName=User111" +
						"&birthDate=" + AafRegistrationUserControllerTest.convertDateToString(refDate, "yyyy-MM-dd") +
						"&countryCode=FR";
		final ResponseEntity<AafRegistrationUser> responseEntity = restTemplate
				.getForEntity(getURLWithPort("/springboot-restserver/aafRegistration/users/getUser" + rqParams), AafRegistrationUser.class);
		assertNotNull(responseEntity);
		// On vérifie le code de réponse HTTP est celui attendu
		assertEquals("Réponse inattendue", HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCodeValue());
		final AafRegistrationUser userFound = responseEntity.getBody();
		assertNull(userFound);
	}

	@Test
	public void testUpdateUser() throws Exception {
		final AafRegistrationUser userToSave = new AafRegistrationUser("User 1", refDate, refCountryCode);
		userToSave.setGender("M");
		userToSave.setPhoneNumber("010203040506");
		final ResponseEntity<AafRegistrationUser> userEntitySaved = restTemplate
				.postForEntity(getURLWithPort("/springboot-restserver/aafRegistration/users/saveUser"), userToSave, AafRegistrationUser.class);
		assertNotNull(userEntitySaved);
		// On vérifie le code de réponse HTTP est celui attendu
		assertEquals("Réponse inattendue", HttpStatus.CREATED.value(), userEntitySaved.getStatusCodeValue());
		final AafRegistrationUser userSaved = userEntitySaved.getBody();
		assertNotNull(userSaved);
		assertEquals(user1.getUserName(), userSaved.getUserName());
		assertEquals(AafRegistrationUserServiceImpl.convertToBeginOfDay(user1.getBirthDate()), userSaved.getBirthDate());
		assertEquals(user1.getCountryCode(), userSaved.getCountryCode());
		assertNotNull("ID should be initialized", userSaved.getUserId());
		assertNotEquals(user1.getGender(), userSaved.getGender());
		assertNotEquals(user1.getPhoneNumber(), userSaved.getPhoneNumber());
	}
}
