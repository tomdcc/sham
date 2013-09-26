package org.shamdata.text

import spock.lang.Specification
import java.util.concurrent.atomic.AtomicInteger

class SpewGeneratorSpec extends Specification {

    def "spew can render class with constant"() {
        given: 'spew generator with simple constant class'
            def spewGenerator = new SpewGenerator(random: new Random())
            def spewClass = new SpewClass(generator: spewGenerator, name: 'MAIN')
            def spewInstance = new SpewInstance(spewClass: spewClass, text: 'o hai')
            spewClass.instances = [spewInstance]
            spewGenerator.mainClass = spewClass

        when:
            def result = spewGenerator.nextLine()

        then:
            result == 'o hai'
    }

    def "spew can render class with multiple constants"() {
        given: 'spew generator with simple constant class'
            def spewGenerator = new SpewGenerator(random: new Random())
            def spewClass = new SpewClass(generator: spewGenerator, name: 'MAIN')
            def spewInstance = new SpewInstance(spewClass: spewClass, text: 'o hai')
            def spewInstance2 = new SpewInstance(spewClass: spewClass, text: 'kthxbye')
            spewClass.instances = [spewInstance, spewInstance2]
            spewGenerator.mainClass = spewClass

        when: 'call 0 times'
            Set result = []
            10.times {
                result << spewGenerator.nextLine()
            }

        then: 'get both instances'
            result == ['o hai', 'kthxbye'] as Set
    }

    def "spew can render class with weights"() {
        given: 'spew generator with class with two unevenly weighted instances'
            def spewGenerator = new SpewGenerator(random: new Random())
            def spewClass = new SpewClass(generator: spewGenerator, name: 'MAIN')
            def normalInstance = new SpewInstance(spewClass: spewClass, text: 'o hai')
            def weightyInstance = new SpewInstance(spewClass: spewClass, text: 'kthxbye', weight: 4)
            spewClass.instances = [normalInstance, weightyInstance]
            spewGenerator.mainClass = spewClass

        when: 'call 1000 times'
            Map<String,AtomicInteger> result = ['o hai': new AtomicInteger(), 'kthxbye': new AtomicInteger()]
            5000.times {
                result[spewGenerator.nextLine()].incrementAndGet()
            }

        then: 'expect relative frequency to be close to weights'
            def weightRatio = ((double) weightyInstance.weight) / ((double) normalInstance.weight)
            def freqRatio = ((double) result[weightyInstance.text].get()) / ((double) result[normalInstance.text].get())
            freqRatio >= weightRatio - 0.5
            freqRatio <= weightRatio + 0.5
    }

    def "spew can render class with reference to other class"() {
        given: 'spew generator with simple constant class'
            def spewGenerator = new SpewGenerator(random: new Random())
            def mainClass = new SpewClass(generator: spewGenerator, name: 'MAIN')
            def fruitClass = new SpewClass(generator: spewGenerator, name: 'FRUIT')
            def mainInstance = new SpewInstance(spewClass: mainClass, text: 'o hai \\FRUIT kthxbye')
            mainClass.instances = [mainInstance]
            spewGenerator.mainClass = mainClass
            def appleInstance = new SpewInstance(spewClass: fruitClass, text: 'apple')
            def orangeInstance = new SpewInstance(spewClass: fruitClass, text: 'orange')
            fruitClass.instances = [appleInstance, orangeInstance]
            spewGenerator.spewClasses = [MAIN: mainClass, FRUIT: fruitClass]

        when: 'call 0 times'
            Set result = []
            10.times {
                result << spewGenerator.nextLine()
            }

        then: 'get both instances'
            result == ['o hai apple kthxbye', 'o hai orange kthxbye'] as Set
    }

    def "spew can render class with reference to other class which is passed in"() {
        given: 'spew generator with simple class'
            def spewGenerator = new SpewGenerator(random: new Random())
            def mainClass = new SpewClass(generator: spewGenerator, name: 'MAIN')
            def mainInstance = new SpewInstance(spewClass: mainClass, text: 'o hai \\FRUIT kthxbye')
            mainClass.instances = [mainInstance]
            spewGenerator.mainClass = mainClass
            spewGenerator.spewClasses = [MAIN: mainClass]
        and: 'fruit class passed in'
            def fruitClass = new SpewClass(generator: spewGenerator, name: 'FRUIT')
            def appleInstance = new SpewInstance(spewClass: fruitClass, text: 'apple')
            def orangeInstance = new SpewInstance(spewClass: fruitClass, text: 'orange')
            fruitClass.instances = [appleInstance, orangeInstance]

        when: 'call 0 times'
            Set result = []
            10.times {
                result << spewGenerator.nextLine([FRUIT: fruitClass])
            }

        then: 'get both instances'
            result == ['o hai apple kthxbye', 'o hai orange kthxbye'] as Set
    }

    def "spew can render class with reference constant which is passed in"() {
        given: 'spew generator with simple class'
            def spewGenerator = new SpewGenerator(random: new Random())
            def mainClass = new SpewClass(generator: spewGenerator, name: 'MAIN')
            def mainInstance = new SpewInstance(spewClass: mainClass, text: 'o hai \\FRUIT kthxbye')
            mainClass.instances = [mainInstance]
            spewGenerator.mainClass = mainClass
            spewGenerator.spewClasses = [MAIN: mainClass]

        expect: 'constant is interpolated'
            spewGenerator.nextLine([FRUIT: 'apple']) == 'o hai apple kthxbye'
    }

    def "spew can render class with reference to other class with 2 variants"() {
        given: 'spew generator with simple constant class'
            def spewGenerator = new SpewGenerator(random: new Random())

            def mainClassNull = new SpewClass(generator: spewGenerator, name: 'MAIN')
            def mainInstanceNull = new SpewInstance(spewClass: mainClassNull, text: 'o hai \\FRUIT kthxbye')
            mainClassNull.instances = [mainInstanceNull]

            def mainClassS = new SpewClass(generator: spewGenerator, name: 'MAINS')
            def mainInstanceS = new SpewInstance(spewClass: mainClassNull, text: 'o hai \\FRUIT/s kthxbye')
            mainClassS.instances = [mainInstanceS]

            def mainClassAnd = new SpewClass(generator: spewGenerator, name: 'MAINAND')
            def mainInstanceAnd = new SpewInstance(spewClass: mainClassNull, text: 'o hai \\FRUIT/& kthxbye')
            mainClassAnd.instances = [mainInstanceAnd]

            def fruitClass = new SpewClass(generator: spewGenerator, name: 'FRUIT', variants: [null, 's' as Character])
            def appleInstance = new SpewInstance(spewClass: fruitClass, text: '[apple{|s}]')
            fruitClass.instances = [appleInstance]

            spewGenerator.spewClasses = [MAIN: mainClassNull, FRUIT: fruitClass]

        expect: 'call fruit instance with null variant returns first case'
            '[apple]' == fruitClass.render(null)

        and: 'call fruite instance with s variant returns second case'
            '[apples]' == fruitClass.render('s' as Character)

        and: 'call to main class with null variant returns first case'
            'o hai [apple] kthxbye' == mainClassNull.render(null)

        and: 'call to main class with s variant returns second case'
            'o hai [apples] kthxbye' == mainClassS.render(null)

        and: 'call to main class with & variant and null param returns first case'
            'o hai [apple] kthxbye' == mainClassAnd.render(null)

        and: 'call to main class with & variant and s param returns second case'
            'o hai [apples] kthxbye' == mainClassAnd.render('s' as Character)
    }

    def "spew can render class with reference to other class with 3 variants"() {
        given: 'spew generator with simple constant class'
            def spewGenerator = new SpewGenerator(random: new Random())

            def fruitClass = new SpewClass(generator: spewGenerator, name: 'FRUIT', variants: [null, 'a' as Character, 's' as Character])
            def appleInstance = new SpewInstance(spewClass: fruitClass, text: '[{|an |}apple{||s}]')
            fruitClass.instances = [appleInstance]

            def verbClass = new SpewClass(generator: spewGenerator, name: 'verb', variants: [null, 's' as Character, 'd' as Character])
            def verbInstance = new SpewInstance(spewClass: verbClass, text: '[f{ind|inds|ound}]')
            verbClass.instances = [verbInstance]

        expect: 'call fruit instance with null variant returns first case'
            '[apple]' == fruitClass.render(null)

        and: 'call fruit instance with a variant returns second case'
            '[an apple]' == fruitClass.render('a' as Character)

        and: 'call fruit instance with a variant returns third case'
            '[apples]' == fruitClass.render('s' as Character)

        and: 'call verb instance with null variant returns first case'
            '[find]' == verbClass.render(null)

        and: 'call verb instance with s variant returns second case'
            '[finds]' == verbClass.render('s' as Character)

        and: 'call fruit instance with d variant returns third case'
            '[found]' == verbClass.render('d' as Character)

    }

}
