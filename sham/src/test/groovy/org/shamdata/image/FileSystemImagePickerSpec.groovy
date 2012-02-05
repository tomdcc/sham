package org.shamdata.image

import spock.lang.Specification
import spock.lang.Shared
import org.shamdata.test.TestUtil

class FileSystemImagePickerSpec extends Specification {

    @Shared File baseDir

    FileSystemImagePicker picker

    def setupSpec() {
        baseDir = new File("${TestUtil.projectBaseDir}/src/test/images")
        assert baseDir.isDirectory()
    }

    def setup() {
        picker = new FileSystemImagePicker()
        picker.baseDir = baseDir.toString()
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
            def validImages = baseDir.listFiles().findAll{it.isFile()}*.toURL()
            images.each {
                assert it in validImages
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
            def blenderDir = new File(baseDir, "blender")
            def blenderSet = imageSets.find{ !it.containsKey("otherImage2") }
            blenderSet
            blenderSet.size() == 3
            blenderSet.mainImage == new File(blenderDir, "mainImage.jpg").toURL()
            blenderSet.otherImage0 == new File(blenderDir, "otherImage0.jpg").toURL()
            blenderSet.otherImage1 == new File(blenderDir, "otherImage1.jpg").toURL()

            def speakersDir = new File(baseDir, "speakers")
            def speakersSet = imageSets.find{ it.containsKey("otherImage2") }
            speakersSet
            speakersSet.size() == 4
            speakersSet.mainImage == new File(speakersDir, "mainImage.png").toURL()
            speakersSet.otherImage0 == new File(speakersDir, "otherImage0.jpg").toURL()
            speakersSet.otherImage1 == new File(speakersDir, "otherImage1.jpg").toURL()
            speakersSet.otherImage2 == new File(speakersDir, "otherImage2.jpg").toURL()
    }

}
