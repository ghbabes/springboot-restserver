package com.bnguimbo.springbootrestserver.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import com.bnguimbo.springbootrestserver.model.AafRegistrationUser;
import com.bnguimbo.springbootrestserver.service.AafRegistrationUserService;
import com.bnguimbo.springbootrestserver.service.AafRegistrationUserServiceImpl;
import com.bnguimbo.springbootrestserver.service.AafRegistrationUserServiceImpl.COUNTRY;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AafRegistrationUserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AafRegistrationUserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	Date refDate = AafRegistrationUserServiceImpl.addYearsToDate(new Date(), -COUNTRY.FRANCE.getMajYear());
	String refCountryCode = COUNTRY.FRANCE.getIsoCode();

	AafRegistrationUser user1 = new AafRegistrationUser("User 1", refDate, refCountryCode);
	AafRegistrationUser user2 = new AafRegistrationUser("User 2", refDate, refCountryCode);

	@MockBean
	private AafRegistrationUserService aafRegUserSrv;

	@Before
	public void setUp() {
		final List<AafRegistrationUser> allUsers = Arrays.asList(user1, user2);
		objectMapper = new ObjectMapper();

		when(aafRegUserSrv.getAllAafRegistrationUsers()).thenReturn(allUsers);
		when(aafRegUserSrv.findByUserNameAndBirthDateAndCountryCode("User 1", refDate, refCountryCode)).thenReturn(user1);
		when(aafRegUserSrv.findByUserNameAndBirthDateAndCountryCode("User 2", refDate, refCountryCode)).thenReturn(user2);

	}

	@Test
	public void testFindAllUsers() throws Exception {

		final MvcResult result = mockMvc.perform(get("/aafRegistration/users").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isFound()).andReturn();

		assertEquals("Réponse incorrecte", HttpStatus.FOUND.value(), result.getResponse().getStatus());
		verify(aafRegUserSrv).getAllAafRegistrationUsers();
		assertNotNull(result);
		final Collection<AafRegistrationUser> users = objectMapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Collection<AafRegistrationUser>>() {
				});
		assertNotNull(users);
		assertEquals(2, users.size());
		final Iterator<AafRegistrationUser> usersIt = users.iterator();
		AafRegistrationUser userResult = usersIt.next();
		assertEquals(user1.getUserName(), userResult.getUserName());
		assertEquals(user1.getBirthDate(), userResult.getBirthDate());
		assertEquals(user1.getCountryCode(), userResult.getCountryCode());
		userResult = usersIt.next();
		assertEquals(user2.getUserName(), userResult.getUserName());
		assertEquals(user2.getBirthDate(), userResult.getBirthDate());
		assertEquals(user2.getCountryCode(), userResult.getCountryCode());
	}

	@Test
	public void testSaveUser() throws Exception {

		final AafRegistrationUser userToSave = new AafRegistrationUser("User 1", refDate, refCountryCode);
		final String jsonContent = objectMapper.writeValueAsString(userToSave);
		when(aafRegUserSrv.saveOrUpdateUser(any(AafRegistrationUser.class))).thenReturn(user1);
		final MvcResult result = mockMvc
				.perform(post("/aafRegistration/users/saveUser").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated()).andReturn();

		assertEquals("Erreur de sauvegarde", HttpStatus.CREATED.value(), result.getResponse().getStatus());
		verify(aafRegUserSrv).saveOrUpdateUser(any(AafRegistrationUser.class));
		final AafRegistrationUser userResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<AafRegistrationUser>() {
		});
		assertNotNull(userResult);
		assertEquals(user1.getUserName(), userResult.getUserName());
		assertEquals(user1.getBirthDate(), userResult.getBirthDate());
		assertEquals(user1.getCountryCode(), userResult.getCountryCode());
	}

	@Test
	public void testFindByUserNameAndBirthDateAndCountryCode() throws Exception {
		when(aafRegUserSrv.getAafRegistrationUser("User 1", refDate, refCountryCode)).thenReturn(user1);
		final AafRegistrationUser userToFind = new AafRegistrationUser("User 1", refDate, refCountryCode);
		final LinkedMultiValueMap<String, String> rqParams = new LinkedMultiValueMap<>();
		rqParams.add("userName", "User 1");
		rqParams.add("birthDate", convertDateToString(refDate, "yyyy-MM-dd"));
		rqParams.add("countryCode", refCountryCode);

		// on execute la requête
		final MvcResult result = mockMvc
				.perform(get("/aafRegistration/users/getUser").contentType(MediaType.APPLICATION_JSON)
				.params(rqParams))
				.andExpect(status().isFound()).andReturn();

		assertEquals("Erreur de sauvegarde", HttpStatus.FOUND.value(), result.getResponse().getStatus());
		verify(aafRegUserSrv).getAafRegistrationUser(any(String.class),
				any(Date.class) , any(String.class));
		final AafRegistrationUser userResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<AafRegistrationUser>() {
		});
		assertNotNull(userResult);
		assertEquals(userToFind.getUserName(), userResult.getUserName());
		assertEquals(userToFind.getBirthDate(), userResult.getBirthDate());
		assertEquals(userToFind.getCountryCode(), userResult.getCountryCode());
	}

	@Test
	public void testUpdateUser() throws Exception {

		final AafRegistrationUser userToUpdate = new AafRegistrationUser("User 1", refDate, refCountryCode);
		final AafRegistrationUser userUpdated = new AafRegistrationUser("User 2", refDate, "DE");
		final String jsonContent = objectMapper.writeValueAsString(userToUpdate);
		when(aafRegUserSrv.saveOrUpdateUser(userToUpdate)).thenReturn(userUpdated);
		// on execute la requête
		final MvcResult result = mockMvc
				.perform(post("/aafRegistration/users/saveUser").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
				.andExpect(status().isCreated()).andReturn();

		verify(aafRegUserSrv).saveOrUpdateUser(any(AafRegistrationUser.class));
		final AafRegistrationUser userResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<AafRegistrationUser>() {
		});
		assertNotNull(userResult);
		assertEquals(userUpdated.getUserName(), userResult.getUserName());
		assertEquals(userUpdated.getBirthDate(), userResult.getBirthDate());
		assertEquals(userUpdated.getCountryCode(), userResult.getCountryCode());
	}

	public static String convertDateToString(final Date date, final String format) {
		String str;
		DateFormat df;

		if (date == null) {
			str = "";
		} else {
			// Transform date from java format to String
			df = new SimpleDateFormat(format, Locale.getDefault());
			df.setTimeZone(TimeZone.getDefault());
			str = df.format(date);
		}
		return str;
	}

}
