package org.shamdata.person

import spock.lang.Unroll

import spock.lang.Specification

class NameSpec extends Specification {

	@Unroll({"name, full name and first name name gives expected values for $expectedFullName"})
	def "name, full name and first name name gives expected values"() {
		given: 'name object'
		def name = new Person(givenNames: givenNames, lastName: lastName)

		expect: 'name is as expected'
		expectedName == name.name

		and: 'full name is as expected'
		expectedFullName == name.fullName

		and: 'first name is as expected'
		expectedFirstName == name.firstName

		where:
		givenNames     | lastName | expectedFirstName | expectedName | expectedFullName
		null           | 'Smith'  | null              | 'Smith'      | 'Smith'
		[]             | 'Smith'  | null              | 'Smith'      | 'Smith'
		['Ged']        | 'Smith'  | 'Ged'             | 'Ged Smith'  | 'Ged Smith'
		['Ged', 'Hmm'] | 'Smith'  | 'Ged'             | 'Ged Smith'  | 'Ged Hmm Smith'
	}
}
