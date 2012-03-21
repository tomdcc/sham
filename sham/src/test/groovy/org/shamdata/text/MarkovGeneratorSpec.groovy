package org.shamdata.text

import spock.lang.Specification

import spock.lang.Unroll

class MarkovGeneratorSpec extends Specification {

    static final END_OF_SENTENCE_CHARS = ['!', '?', '.', '"']

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
            sentence.substring(sentence.length() - 1) in END_OF_SENTENCE_CHARS
    }

    def "nextSentence returns new no longer than specified length"() {
		when: 'have a max length in mind'
			def max = 30

        then: 'get something looking like a sentence no longer than specified length'
			1000.times {
				def sentence = generator.nextSentence(max)
				assert sentence.substring(sentence.length() - 1) in END_OF_SENTENCE_CHARS
				assert sentence
				assert sentence.length() > 0
				assert sentence.length() <= max
			}
    }

    def "nextSentence eventually gives up if max chars is set too low"() {
		when: 'have a max length in mind'
			generator.nextSentence(1)

        then: 'exception thrown'
			thrown(IllegalArgumentException)
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

    def "nextParagraph produces normal looking number of quotes"() {
        when: 'ask for a number of paragraphs'
            def paragraphs = generator.nextParagraphs(10000)

        then: "paragraphs don't have abnormal number of quotes"
            paragraphs.each { paragraph ->
                def wordCount = paragraph.split(/[!.? ]/).length
                def quoteCount = paragraph.toCharArray().findAll { it == '"'}.size()
                if(wordCount > 20) {
                    assert quoteCount < wordCount / 3
                }
            }
    }


}
