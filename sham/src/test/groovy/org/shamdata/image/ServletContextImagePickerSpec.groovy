package org.shamdata.image

import spock.lang.Specification
import org.springframework.mock.web.MockServletContext
import spock.lang.Shared

import org.shamdata.test.TestUtil

class ServletContextImagePickerSpec extends Specification {
    def servletContext
    def picker

    @Shared File baseDir
    def setupSpec() {
        baseDir = new File("${TestUtil.projectBaseDir}/src/test/images")
        assert baseDir.isDirectory()
    }

    def setup() {
        servletContext = new MockServletContext("file:${TestUtil.projectBaseDir}/src/test")
        picker = new ServletContextImagePicker()
        picker.servletContext = servletContext
        picker.baseDir = '/images'
        picker.init()
    }

    def "image picker picks random image from directory"() {
        when: 'ask for an image 10 times'
            def images = new HashSet();
            10.times {
                images << picker.nextImage()
            }

        then: "at least 3 different images returned"
            images.size() >= 3

        and: 'each one is valid'
            def validFiles = baseDir.listFiles().findAll{it.isFile()}.collect{new URL("file:$it")}
            images.each { URL imageUrl ->
                imageUrl in validFiles
            }
    }

    def "image picker picks random image set from directory"() {
        when: 'ask for an image 10 times'
            def imageSets = new HashSet();
            10.times {
                imageSets << picker.nextImageSet()
            }

        then: "at least 3 different images returned"
            imageSets.size() == 2

        and: 'each one is valid'
            def blenderDir = "$baseDir/blender"
            def blenderSet = imageSets.find{ !it.containsKey("otherImage2") }
            blenderSet
            blenderSet.size() == 3
            blenderSet.mainImage   == new URL("file:$blenderDir/mainImage.jpg")
            blenderSet.otherImage0 == new URL("file:$blenderDir/otherImage0.jpg")
            blenderSet.otherImage1 == new URL("file:$blenderDir/otherImage1.jpg")

            def speakersDir = "$baseDir/speakers"
            def speakersSet = imageSets.find{ it.containsKey("otherImage2") }
            speakersSet
            speakersSet.size() == 4
            speakersSet.mainImage   == new URL("file:$speakersDir/mainImage.png")
            speakersSet.otherImage0 == new URL("file:$speakersDir/otherImage0.jpg")
            speakersSet.otherImage1 == new URL("file:$speakersDir/otherImage1.jpg")
            speakersSet.otherImage2 == new URL("file:$speakersDir/otherImage2.jpg")
    }

}
