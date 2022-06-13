package com.bnguimbo.springbootrestserver.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



/**
 * Object for an application of user registration
 *
 * @author stephaneb
 */
@Entity
@Table(name = "AAF_REGISTRATION_USER")
public class AafRegistrationUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID", updatable = false, nullable = false)
    private Long userId;

	@Column(name = "USER_NAME", unique=false, insertable=true, updatable=true, nullable=false)
	private String userName;

	@Column(name = "BIRTH_DATE", unique=false, insertable=true, updatable=true, nullable=false)
	private Date birthDate;

	@Column(name = "COUNTRY_CODE", unique=false, insertable=true, updatable=true, nullable=false)
	private String countryCode;

	@Column(name = "PHONE_NUMBER", unique=false, insertable=true, updatable=true, nullable=true)
	private String phoneNumber;

	@Column(name = "GENDER", unique=false, insertable=true, updatable=true, nullable=true)
	private String gender;

	public AafRegistrationUser() {
		super();
	}

	public AafRegistrationUser(final String userName, final Date birthDate, final String countryCode) {
		this();
		setUserName(userName);
		setBirthDate(birthDate);
		setCountryCode(countryCode);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(final Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return userName + " / " + birthDate + " / " + countryCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (userId == null ? 0 : userId.hashCode());
		result = prime * result + (userName == null ? 0 : userName.hashCode());
		result = prime * result + (birthDate == null ? 0 : birthDate.hashCode());
		result = prime * result + (countryCode == null ? 0 : countryCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AafRegistrationUser other = (AafRegistrationUser) obj;
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		if (userName == null) {
			if (other.userName != null) {
				return false;
			}
		} else if (!userName.equals(other.userName)) {
			return false;
		}
		if (birthDate == null) {
			if (other.birthDate != null) {
				return false;
			}
		} else if (!birthDate.equals(other.birthDate)) {
			return false;
		}
		if (countryCode == null) {
			if (other.countryCode != null) {
				return false;
			}
		} else if (!countryCode.equals(other.countryCode)) {
			return false;
		}
		return true;
	}
}

