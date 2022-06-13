package com.bnguimbo.springbootrestserver.service;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.bnguimbo.springbootrestserver.dao.AafRegistrationUserReposirory;
import com.bnguimbo.springbootrestserver.exception.BusinessResourceException;
import com.bnguimbo.springbootrestserver.model.AafRegistrationUser;

@Service
public class AafRegistrationUserServiceImpl implements AafRegistrationUserService {

	public static enum COUNTRY {
		FRANCE("FR", 18),
		GERMANY("DE", 18); // ...

		private String isoCode;
		private int majYear;

		COUNTRY(final String isoCode, final int majYear) {
			this.isoCode = isoCode;
			this.majYear = majYear;
		}

		public String getIsoCode() {
			return isoCode;
		}

		public int getMajYear() {
			return majYear;
		}

		/**
		 * Check if the given date correspond to the majority date of the country
		 * @param checkDate
		 * @return
		 */
		// Should use other object for date
		public boolean isMajoriry(final Date checkDate) {
			if (checkDate == null) {
				return false;
			} else {
				@SuppressWarnings("deprecation")
				final
				Date majDate = convertToBeginOfDay(new Date(checkDate.getYear() + majYear, checkDate.getMonth(), checkDate.getDate()));
				if (majDate.after(convertToBeginOfDay(new Date()))) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Return the {@link COUNTRY} corresponding to the given isoCode
		 * @param isoCode
		 * @return
		 */
		public static COUNTRY getValueFor(final String isoCode) {
			if (!isBlank(isoCode)) {
				for (final COUNTRY country : values()) {
					if (country.isoCode.equals(isoCode)) {
						return country;
					}
				}
			}
			return null;
		}

		public static EnumSet<COUNTRY> ALLOWED_COUNTRIES = EnumSet.of(FRANCE);

	}

	private static final Logger logger = LoggerFactory.getLogger(AafRegistrationUserServiceImpl.class);

	@Autowired
	private AafRegistrationUserReposirory aafRegistrationUserReposirory;


	public AafRegistrationUserServiceImpl() {
		super();
	}

	@Autowired
	public AafRegistrationUserServiceImpl(final AafRegistrationUserReposirory aafRegistrationUserReposirory) {
		super();
		this.aafRegistrationUserReposirory = aafRegistrationUserReposirory;
	}

	@Override
	public AafRegistrationUser findByUserNameAndBirthDateAndCountryCode(final String userName, final Date birthDate, final String countryCode) {
		final Date dateOfBirth = convertToBeginOfDay(birthDate);
		return aafRegistrationUserReposirory.findByUserNameAndBirthDateAndCountryCode(userName, dateOfBirth, countryCode);
	}

	@Override
	public Collection<AafRegistrationUser> getAllAafRegistrationUsers() {
		return IteratorUtils.toList(aafRegistrationUserReposirory.findAll().iterator());
	}

	@Override
	public AafRegistrationUser getAafRegistrationUser(final String userName, final Date birthDate, final String countryCode) {
		AafRegistrationUser aafUserDB = null;
		final Date dateOfBirth = convertToBeginOfDay(birthDate);
		try {
			aafUserDB = findByUserNameAndBirthDateAndCountryCode(userName, dateOfBirth, countryCode);
		} catch(final DataIntegrityViolationException ex){
			logger.error("AafRegistrationUser not found", ex);
			throw new BusinessResourceException("DuplicateValueError", "AafRegistrationUser must be unique for : "
					+ userName + " / " + dateOfBirth + " / " + countryCode, HttpStatus.CONFLICT);
		} catch(final Exception ex){
			logger.error("Technical error while creating or updating AafRegistrationUser", ex);
			throw new BusinessResourceException("SaveOrUpdateError", "Technical error while creating or updating AafRegistrationUser : "
					+ userName + " / " + dateOfBirth + " / " + countryCode, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (aafUserDB != null) {
			return aafUserDB;
		} else {
			logger.error("AafRegistrationUser not found");
			throw new BusinessResourceException("NotFoundError", "AafRegistrationUser not found for : "
					+ userName + " / " + dateOfBirth + " / " + countryCode, HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public AafRegistrationUser saveOrUpdateUser(final AafRegistrationUser aafUser) {
		checkAafUSer(aafUser);
		try {
			final AafRegistrationUser aafUserDB = findByUserNameAndBirthDateAndCountryCode(aafUser.getUserName(), aafUser.getBirthDate(), aafUser.getCountryCode());
			if (aafUserDB != null) {
				// Mise à jour => copie de l'ID pour update, les autres champs proviennent de l'entrée
				aafUser.setUserId(aafUserDB.getUserId());
			}
			// Sauvegarde des données
			aafUser.setBirthDate(convertToBeginOfDay(aafUser.getBirthDate()));
			final AafRegistrationUser saved = aafRegistrationUserReposirory.save(aafUser);
			return  saved;
		} catch(final DataIntegrityViolationException ex){
			logger.error("AafRegistrationUser not found", ex);
			throw new BusinessResourceException("DuplicateValueError", "AafRegistrationUser must be unique for : " + aafUser, HttpStatus.CONFLICT);
		} catch(final Exception ex){
			logger.error("Technical error while creating or updating AafRegistrationUser", ex);
			throw new BusinessResourceException("SaveOrUpdateError", "Technical error while creating or updating AafRegistrationUser : "+ aafUser, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Check the given {@link AafRegistrationUser}'s properties
	 * @param aafUser
	 * @param birthDate
	 * @param countryCode
	 * @throws Exception
	 */
	private void checkAafUSer(final AafRegistrationUser aafUser) throws BusinessResourceException {
		if (aafUser == null) {
			logger.error("AafRegistrationUser is null");
			throw new BusinessResourceException("UserNull", "AafRegistrationUser is null", HttpStatus.NO_CONTENT);
		}
		// UserName => mandatory
		if (isBlank(aafUser.getUserName())) {
			logger.error("AafRegistrationUser.Username is null or blank");
			throw new BusinessResourceException("UserNameMandatory", "AafRegistrationUser.Username is null or blank", HttpStatus.PARTIAL_CONTENT);
		}
		// BirthDate => mandatory
		if (aafUser.getBirthDate() == null) {
			logger.error("AafRegistrationUser.BirthDate is null");
			throw new BusinessResourceException("BirthDateMandatory", "AafRegistrationUser.BirthDate is null", HttpStatus.PARTIAL_CONTENT);
		} else if (convertToBeginOfDay(aafUser.getBirthDate()).after(convertToBeginOfDay(new Date()))) {
			logger.error("AafRegistrationUser.BirthDate is greater than today");
			throw new BusinessResourceException("BirthDateBadData", "AafRegistrationUser.BirthDate is greater than today", HttpStatus.NOT_ACCEPTABLE);
		}
		// CountryCode => mandatory
		if (isBlank(aafUser.getCountryCode())) {
			logger.error("AafRegistrationUser.CountryCode is null or blank");
			throw new BusinessResourceException("CountryCodeMandatory", "AafRegistrationUser.CountryCode is null or blank", HttpStatus.PARTIAL_CONTENT);
		}
		// Allowed countries
		final COUNTRY country = COUNTRY.getValueFor(aafUser.getCountryCode());
		if (country == null) {
			logger.error("AafRegistrationUser.CountryCode not allowed");
			throw new BusinessResourceException("CountryCodeNotAllowed", "AafRegistrationUser.CountryCode is not allowed : " + aafUser.getCountryCode(), HttpStatus.NOT_ACCEPTABLE);
		} else if (!COUNTRY.ALLOWED_COUNTRIES.contains(country)) {
			logger.error("AafRegistrationUser.CountryCode not allowed");
			throw new BusinessResourceException("CountryCodeNotAllowed", "AafRegistrationUser.CountryCode is not allowed : " + aafUser.getCountryCode(), HttpStatus.NOT_ACCEPTABLE);
		} else if (!country.isMajoriry(aafUser.getBirthDate())) {
			// Allowed BirthDate
			logger.error("AafRegistrationUser.BirthDate not allowed (No majority today)");
			throw new BusinessResourceException("BirthDateNotAllowed", "AafRegistrationUser.BirthDate is not allowed : No majority today for " + aafUser.getBirthDate(), HttpStatus.NOT_ACCEPTABLE);
		}
	}

	 /**
     * Checks if the given string is whitespace, empty ("") or {@code null}.
     *
     * <pre>
     * isBlank(null)    == true
     * isBlank("")      == true
     * isBlank(" ")     == true
     * isBlank(" abc")  == false
     * isBlank("abc ")  == false
     * isBlank(" abc ") == false
     * </pre>
     *
     * @param str   the string to check, may be {@code null}
     * @return {@code true} if the string is whitespace, empty
     *    or {@code null}
     */
	public static boolean isBlank(final String str) {
        final int length = str != null ? str.length() : 0;
        if (length == 0) {
            return true;
        }
        for (int i = length - 1; i >= 0; i--) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

	/**
	 * Convert date to begin of the day
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date convertToBeginOfDay(final Date date) {
		if (date != null) {
			return new Date(date.getYear(), date.getMonth(), date.getDate(), 0, 0);
		}
		return date;
	}

	/**
	 * Add days to date (convenience code)
	 * @param date
	 * @param nbDays
	 * @return
	 */
	public static Date addDaysToDate(final Date date, final int nbDays) {
		if (date != null) {
			return new Date(date.getYear(), date.getMonth(), date.getDate() + nbDays, 0, 0);
		}
		return date;
	}

	/**
	 * Add years to date (convenience code)
	 * @param date
	 * @param nbDay
	 * @return
	 */
	public static Date addYearsToDate(final Date date, final int nbYears) {
		if (date != null) {
			return new Date(date.getYear() + nbYears, date.getMonth(), date.getDate(), 0, 0);
		}
		return date;
	}
}
