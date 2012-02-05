package org.shamdata.text

import spock.lang.Specification

import spock.lang.Unroll

class MarkovGeneratorSpec extends Specification {

    MarkovGenerator generator

    def setup() {
        generator = new MarkovGenerator()
        generator.init()
    }

    def "nextSentence returns new sentence"() {
        when: 'ask for a sentence'
            def sentence = generator.nextSentence()

        then: 'get something looking like a sentence'
            sentence
            sentence.length() > 0
            sentence.substring(sentence.length() - 1) in ['!', '?', '.']
    }

    def "nextParagraph returns new paragraph"() {
        when: 'ask for a paragraph'
            def para = generator.nextParagraph()

        then: 'get something looking like a paragraph'
            looksLikeAParagraph(para)
    }

    private boolean looksLikeAParagraph(String para) {
        para.split("[!.?]").size() >= 2
    }

    def "nextParagraphs returns some number of paragraphs"() {
        when: 'ask for a paragraph'
            def paras = generator.nextParagraphs()

        then: 'some expected number of paragraphs'
            paras.size() >= 1
            paras.size() <= 8

        and: 'each item looks like a paragraph'
            paras.each {
                assert looksLikeAParagraph(it)
            }
    }

    @Unroll({"nextParagraphs with $num returns expected number of paragraphs"})
    def "nextParagraphs with parameter returns expected number of paragraphs"() {
        when: 'ask for a paragraph'
            def paras = generator.nextParagraphs(num)

        then: 'some expected number of paragraphs'
            paras.size() == num

        and: 'each item looks like a paragraph'
            paras.each {
                assert looksLikeAParagraph(it)
            }
        where:
            num << [1, 3, 5]
    }


}
