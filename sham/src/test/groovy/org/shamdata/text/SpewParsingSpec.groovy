package org.shamdata.text

import spock.lang.Specification
import spock.lang.Unroll

class SpewParsingSpec extends Specification {

    def "spew generator can parse file with comments"() {
        given: 'generator'
            def generator = new SpewGenerator()

        when: 'parse file'
            generator.parse(new ByteArrayInputStream(file.getBytes()))

        then: 'get valid parsing'
            generator.spewClasses.size() == 1
            generator.mainClass == generator.spewClasses.MAIN
            generator.mainClass.instances.size() == 1
            generator.mainClass.instances[0].text == 'o hai'
            generator.mainClass.instances[0].weight == 1

        where:
            file = """\\* o hai here is a comment
%MAIN\\* more commentage
\\* again
o hai\\*hmm
\\*askjdsa
%%\\*dgdf
\\*dgfd"""
    }

    def "spew generator can parse file with weights"() {
        given: 'generator'
            def generator = new SpewGenerator()

        when: 'parse file'
            generator.parse(new ByteArrayInputStream(file.getBytes()))

        then: 'get valid parsing'
            generator.spewClasses.size() == 1
            generator.mainClass == generator.spewClasses.MAIN
            generator.mainClass.instances.size() == 2
            generator.mainClass.instances[0].text == 'o hai'
            generator.mainClass.instances[0].weight == 1
            generator.mainClass.instances[1].text == 'kthxbye'
            generator.mainClass.instances[1].weight == 4

        where:
            file = """%MAIN
o hai
(4)kthxbye
"""
    }

    def "spew generator can parse file with parameterized classes"() {
        given: 'generator'
            def generator = new SpewGenerator()

        when: 'parse file'
            generator.parse(new ByteArrayInputStream(file.getBytes()))

        then: 'get valid parsing'
            generator.spewClasses.size() == 1
            generator.mainClass == generator.spewClasses.MAIN
            generator.mainClass.instances.size() == 1
            generator.mainClass.instances[0].text == 'o hai'
            generator.mainClass.variants == [null, 'a' as Character, 's' as Character]

        where:
            file = """%MAIN{as}
o hai
"""
    }

    @Unroll
    def "spew generator throws exception parsing file with no main class"() {
        given: 'generator'
            def generator = new SpewGenerator()

        when: 'parse file'
            generator.parse(new ByteArrayInputStream(file.getBytes()))

        then: 'get exception'
            thrown(IllegalArgumentException)

        where:
            file << ["", "%NOTMAIN\nhmm"]
    }

    @Unroll
    def "spew generator throws exception parsing file which does not start with a class"() {
        given: 'generator'
            def generator = new SpewGenerator()

        when: 'parse file'
            generator.parse(new ByteArrayInputStream(file.getBytes()))

        then: 'get exception'
            thrown(IllegalArgumentException)

        where:
            file << ['o hai\nlateclass', '/* comment\no hai%classname%%']
    }

    @Unroll
    def "spew generator throws exception parsing file with empty class"() {
        given: 'generator'
            def generator = new SpewGenerator()

        when: 'parse file'
            generator.parse(new ByteArrayInputStream(file.getBytes()))

        then: 'get exception'
            thrown(IllegalArgumentException)

        where:
            file << ['%classname\n%otherclass', '%classname%%']
    }

    def "spew generator can parse test headline reference and generate headlines without exceptions"() {
        given: 'generator'
            def generator = new SpewGenerator()
            generator.setBundleName('test-headline');

        when: 'parse headline'
            generator.init()

        then: 'can call nextLine many times with no exceptions'
            10000.times {
                generator.nextLine()
            }
    }

    def "spew generator can parse bundled headline reference and generate headlines without exceptions"() {
        given: 'generator'
            def generator = new SpewGenerator()

        when: 'parse headline'
            generator.init()

        then: 'can call nextLine many times with no exceptions'
            10000.times {
                generator.nextLine()
            }
    }

    def "spew generator can parse bundled product name reference and generate product names without exceptions"() {
        given: 'generator'
            def generator = new SpewGenerator()
			generator.setBundleName('product-name')

        when: 'parse headline'
            generator.init()

        then: 'can call nextLine many times with no exceptions'
            10000.times {
                generator.nextLine()
            }
    }
}
