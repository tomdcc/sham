package org.shamdata.person;

import org.shamdata.text.SpewGenerator;

import java.util.*;

public class PersonGenerator {
	private Locale locale = Locale.getDefault();
	private Gender gender = null;

    private String fakeDomainSuffix = ".bv";

	private boolean initialized = false;
	private Random random;
	private List<String> lastNames;
	private List<String> givenFemaleNames;
	private List<String> givenMaleNames;
    private SpewGenerator usernameGenerator;
    private SpewGenerator emailGenerator;

	public void init() {
        if(random == null) {
            throw new IllegalArgumentException("init should be called after random number generator set");
        }
		readNames();
        usernameGenerator = initSpewGenerator("username");
        emailGenerator = initSpewGenerator("email");
        initialized = true;
	}

    private SpewGenerator initSpewGenerator(String bundle) {
        SpewGenerator gen = new SpewGenerator();
        gen.setRandom(random);
        gen.setBundleName(bundle);
        gen.init();
        return gen;
    }

	public PersonGenerator() { }

	private void readNames() {
		ResourceBundle bundle = ResourceBundle.getBundle(getClass().getPackage().getName() + ".names", locale);
		lastNames = readNamesFromBundle("lastName", bundle);
		givenFemaleNames = readNamesFromBundle("givenName.female", bundle);
		givenMaleNames = readNamesFromBundle("givenName.male", bundle);
	}

	private List<String> readNamesFromBundle(String nameType, ResourceBundle bundle) {
		List<String> names = new ArrayList<String>();
		for(int i = 0; ; i++) {
			try {
				names.add(bundle.getString(String.format("%s.%d", nameType, i)));
			} catch(MissingResourceException ex) {
				break;
			}
		}
		return names;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	public Person nextPerson() {
		if(!initialized) { init(); }

		Person person = new Person();
		Gender gender = this.gender == null ? (random.nextBoolean() ? Gender.FEMALE : Gender.MALE) : this.gender;
		person.setGender(gender);
		List<String> givenNamesPool = gender == Gender.FEMALE ? givenFemaleNames : givenMaleNames;
		List<String> givenNames = new ArrayList<String>(2);
		givenNames.add(getNameWord(givenNamesPool));
		givenNames.add(getNameWord(givenNamesPool));
		person.setGivenNames(givenNames);
		person.setLastName(getNameWord(lastNames));
        generateDOB(person);
        Map<String, Object> spewDetails = generateSpewDetails(person);
        person.setUsername(usernameGenerator.nextLine(spewDetails));
        spewDetails.put("USERNAME", person.getUsername());
        person.setEmail(emailGenerator.nextLine(spewDetails));
        person.setTwitterUsername(random.nextInt(2) == 1 ? "@" + usernameGenerator.nextLine(spewDetails) : null);
        return person;
	}

    private void generateDOB(Person person) {
        Calendar cal = Calendar.getInstance(locale);
         // just gen adults for now
        cal.set(Calendar.YEAR, 1930);
        int sixtyIshYears = 365 * 60;
        cal.add(Calendar.DAY_OF_YEAR, random.nextInt(sixtyIshYears));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        person.setDob(cal.getTime());
    }

    private Map<String, Object> generateSpewDetails(Person p) {
        Map<String,Object> personDetails = new HashMap<String, Object>();
        personDetails.put("INITIAL1", p.hasGivenNames() ? p.getFirstName().toLowerCase().substring(0, 1) : "");
        personDetails.put("INITIAL2", p.getGivenNames().size() > 1 ? p.getGivenNames().get(1).toLowerCase().substring(0, 1) : "");
        personDetails.put("FIRSTNAME", p.hasGivenNames() ? p.getFirstName().toLowerCase() : "");
        personDetails.put("LASTNAME", p.getLastName().toLowerCase());
        personDetails.put("LASTNAMEINITIAL", p.getLastName().toLowerCase().substring(0, 1));
        Calendar cal = Calendar.getInstance(locale);
        cal.setTime(p.getDob());
        String yob = String.valueOf(cal.get(Calendar.YEAR));
        personDetails.put("FULLYOB", yob);
        personDetails.put("SHORTYOB", yob.substring(2));
        personDetails.put("FAKEDOMAINSUFFIX", fakeDomainSuffix == null ? "" : fakeDomainSuffix);
        return personDetails;
    }

    private String getNameWord(List<String> names) {
		return names.get(random.nextInt(names.size()));
	}

	public List<Person> nextPeople(int num) {
		List<Person> names = new ArrayList<Person>(num);
		for(int i = 0; i < num; i++) {
			names.add(nextPerson());
		}
		return names;
	}

    public void setRandom(Random random) {
        this.random = random;
    }

	public void setGender(Gender gender) {
		this.gender = gender;
	}

    public void setFakeDomainSuffix(String fakeDomainSuffix) {
        this.fakeDomainSuffix = fakeDomainSuffix;
    }

}
