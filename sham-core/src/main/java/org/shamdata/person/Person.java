package org.shamdata.person;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Represents a randomly generated person.
 */
public class Person {
	private Gender gender;
	private List<String> givenNames;
	private String lastName;
    private String username;
    private String email;
    private String twitterUsername;
    private Date dob;

	private String getName(List<String> usedGivenNames) {
		StringBuilder sb = new StringBuilder();
		for(String givenName : usedGivenNames) {
			if(givenName != null) {
				sb.append(givenName);
				sb.append(' ');
			}

		}
		if(lastName != null) {
			sb.append(lastName);
		}
		return sb.toString().trim();
	}

    /**
     * Will return a standard Firstname Lastname representation of the person, with middle names
     * omitted.
     *
     * @return the common form name of the person
     */
	public String getName() {
		return getName(hasGivenNames() ? Collections.singletonList(givenNames.get(0)) : Collections.<String>emptyList());
	}

    /**
     * Returns the person's full name, including middle name
     *
     * @return full name
     */
	public String getFullName() {
		return getName(hasGivenNames() ? givenNames : Collections.<String>emptyList());
	}

	public String getFirstName() {
		return hasGivenNames() ? givenNames.get(0) : null;
	}

	public boolean hasGivenNames() {
		return (givenNames != null && !givenNames.isEmpty());
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<String> getGivenNames() {
		return givenNames;
	}

	public void setGivenNames(List<String> givenNames) {
		this.givenNames = givenNames;
	}
	
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }
}
