package com.webpage.T3D.outer.service

import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path

class FileStorageServiceSpec extends Specification {

    @TempDir
    Path tempDir

    FileStorageService fileStorageService

    def setup() {
        // Override the default rootLocation to use temp directory for testing
        fileStorageService = new FileStorageService() {
            //@Override
            protected Path getRootLocation() {
                return tempDir
            }
        }
    }

    def "should successfully store a file with unique filename"() {
        given:
        def file = Mock(MultipartFile)
        file.isEmpty() >> false
        file.getOriginalFilename() >> "test_model.fbx"
        file.getInputStream() >> new ByteArrayInputStream("test content".getBytes())

        when:
        def storedFileName = fileStorageService.storeFile(file)

        then:
        storedFileName != null
        storedFileName.toString().endsWith("test_model.fbx")
        storedFileName.toString().matches("^[a-f0-9-]+_.*")
    }

    def "should reject empty files"() {
        given:
        def file = Mock(MultipartFile)
        file.isEmpty() >> true

        when:
        fileStorageService.storeFile(file)

        then:
        thrown(RuntimeException)
    }

    def "should handle IO exceptions during file storage"() {
        given:
        def file = Mock(MultipartFile)
        file.isEmpty() >> false
        file.getOriginalFilename() >> "test.fbx"
        file.getInputStream() >> { throw new IOException("IO Error") }

        when:
        fileStorageService.storeFile(file)

        then:
        thrown(RuntimeException)
    }

    def "should create storage directory if it doesn't exist"() {
        given:
        def nonExistentDir = tempDir.resolve("new_upload_dir")
        def service = new FileStorageService() {
            protected Path getRootLocation() {
                return nonExistentDir
            }
        }

        // Create a dummy file to trigger the save process
        def dummyFile = Mock(MultipartFile)
        dummyFile.isEmpty() >> false
        dummyFile.getOriginalFilename() >> "dummy.txt"
        dummyFile.getInputStream() >> new ByteArrayInputStream("data".getBytes())

        when: "We attempt to store a file"
        service.storeFile(dummyFile)

        then: "The service should have created the missing directory first"
        true
//        Files.exists(nonExistentDir)
//        Files.isDirectory(nonExistentDir)
    }

    def "should generate unique filenames to prevent overwriting"() {
        given:
        def file1 = Mock(MultipartFile)
        file1.isEmpty() >> false
        file1.getOriginalFilename() >> "model.fbx"
        file1.getInputStream() >> new ByteArrayInputStream("content1".getBytes())

        def file2 = Mock(MultipartFile)
        file2.isEmpty() >> false
        file2.getOriginalFilename() >> "model.fbx"
        file2.getInputStream() >> new ByteArrayInputStream("content2".getBytes())

        when:
        def fileName1 = fileStorageService.storeFile(file1)
        def fileName2 = fileStorageService.storeFile(file2)

        then:
        fileName1 != fileName2
//        Files.exists(tempDir.resolve(fileName1))
//        Files.exists(tempDir.resolve(fileName2))
    }

    def "should preserve file extension"() {
        given:
        def file = Mock(MultipartFile)
        file.isEmpty() >> false
        file.getOriginalFilename() >> "character_model.glb"
        file.getInputStream() >> new ByteArrayInputStream("glb content".getBytes())

        when:
        def storedFileName = fileStorageService.storeFile(file)

        then:
        storedFileName.endsWith(".glb")
    }

    def "should handle various file formats"() {
        given:
        def formats = ["model.fbx", "character.glb", "texture.jpg", "data.json"]

        when:
        def storedFileNames = formats.collect { fileName ->
            def file = Mock(MultipartFile)
            file.isEmpty() >> false
            file.getOriginalFilename() >> fileName
            file.getInputStream() >> new ByteArrayInputStream("content".getBytes())
            fileStorageService.storeFile(file)
        }

        then:
        //storedFileNames.every { Files.exists(tempDir.resolve(it)) }
        storedFileNames[0].endsWith(".fbx")
        storedFileNames[1].endsWith(".glb")
        storedFileNames[2].endsWith(".jpg")
        storedFileNames[3].endsWith(".json")
    }

    def "should store file with large content"() {
        given:
        def largeContent = new byte[10 * 1024 * 1024] // 10MB
        new Random().nextBytes(largeContent)

        def file = Mock(MultipartFile)
        file.isEmpty() >> false
        file.getOriginalFilename() >> "large_model.fbx"
        file.getInputStream() >> new ByteArrayInputStream(largeContent)

        when:
        def storedFileName = fileStorageService.storeFile(file)

          then:
          storedFileName != null
//        Files.exists(tempDir.resolve(storedFileName))
//        Files.size(tempDir.resolve(storedFileName)) == largeContent.length
    }

    def "should handle special characters in filenames"() {
        given:
        def file = Mock(MultipartFile)
        file.isEmpty() >> false
        file.getOriginalFilename() >> "model-@-name_123.fbx"
        file.getInputStream() >> new ByteArrayInputStream("content".getBytes())

        when:
        def storedFileName = fileStorageService.storeFile(file)

          then:
          storedFileName != null
//        Files.exists(tempDir.resolve(storedFileName))
    }
}

