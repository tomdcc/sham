package org.shamdata.person

import spock.lang.Specification
import static org.shamdata.person.Gender.*
import spock.lang.Unroll

class PersonGeneratorSpec extends Specification {

	@Unroll({"generate person for non-random seed $seed}"})
	def "generate predictable person for non-random seed"() {
		given: 'a generator'
			def gen = new PersonGenerator()
			gen.locale = Locale.UK
			gen.random = new Random(seed)
            if(!isFakeEmail) {
                gen.setFakeDomainSuffix(null)
            }
            gen.init()

		when: 'we ask for the next person'
			def person = gen.nextPerson()

		then: 'the person has names / gender as expected'
			gender == person.gender
			givenNames == person.givenNames
			lastName == person.lastName
            dob == person.dob
            username == person.username
            email == person.email
            twitterUsername == person.twitterUsername

		where:
			seed | isFakeEmail | gender | givenNames              | lastName | username       | email                    | dob                  | twitterUsername
			0    | true        | FEMALE | ['Louise', 'Elizabeth'] | 'Foster' | 'louise46'     | 'lfoster@gmail.com.bv'   | new Date(46, 2, 12)  | '@louisefoster'
			-20  | false       | MALE   | ['Richard', 'Jack']     | 'Taylor' | 'shyrichard71' | 'richard.taylor@aol.com' | new Date(72, 11, 30) | '@gorgeousdude72'
	}

	@Unroll({"generate gender as expected for specified gender $gender"})
	def "generate gender as expected"() {
		given: 'a generator'
			def gen = new PersonGenerator()
			gen.locale = Locale.UK
            gen.gender = gender
            gen.random = new Random(seed)
            gen.init()

		when: 'we ask for the next 100 people'
			def people = gen.nextPeople(100)

		then: 'they have the expected gender breakdown'
			expectedFemales == people.count { it.gender == FEMALE }
			expectedMales == people.count { it.gender == MALE }

		where:
			seed = 0 // force predictable seed fpr
			gender | expectedMales | expectedFemales
			MALE   | 100           | 0
			FEMALE | 0             | 100
			null   | 61            | 39 // gender breakdown for this specific seed
	}

}
