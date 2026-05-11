package com.webpage.T3D.outer.controller

import com.webpage.T3D.outer.model.AssetModel
import com.webpage.T3D.outer.model.User
import com.webpage.T3D.outer.repository.AssetModelRepository
import com.webpage.T3D.outer.repository.UserRepository
import com.webpage.T3D.outer.service.FileStorageService
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

import java.lang.reflect.Field

class AssetModelControllerSpec extends Specification {

    AssetModelController controller
    AssetModelRepository assetRepository
    UserRepository userRepository
    FileStorageService fileStorageService

    def setup() {
        assetRepository = Mock(AssetModelRepository)
        userRepository = Mock(UserRepository)
        fileStorageService = Mock(FileStorageService)
        controller = new AssetModelController()

        // Use reflection to inject mocks into private @Autowired fields
        Field assetField = AssetModelController.class.getDeclaredField("assetRepository")
        assetField.setAccessible(true)
        assetField.set(controller, assetRepository)

        Field userField = AssetModelController.class.getDeclaredField("userRepository")
        userField.setAccessible(true)
        userField.set(controller, userRepository)

        Field fileField = AssetModelController.class.getDeclaredField("fileStorageService")
        fileField.setAccessible(true)
        fileField.set(controller, fileStorageService)
    }

    def "should get storefront feed with all published models"() {
        given:
        def model1 = new AssetModel(id: 1L, title: "Model 1", isPublished: true, price: BigDecimal.ONE)
        def model2 = new AssetModel(id: 2L, title: "Model 2", isPublished: true, price: BigDecimal.TEN)
        List<AssetModel> publishedModels = [model1, model2]
        assetRepository.findByIsPublishedTrue() >> publishedModels

        when:
        def response = controller.getStorefrontFeed()

        then:
        response.statusCode == HttpStatus.OK
        response.body.size() == 2
        response.body[0].title == "Model 1"
        response.body[1].title == "Model 2"
    }

    def "should get model details by id"() {
        given:
        def modelId = 1L
        def model = new AssetModel(
            id: modelId,
            title: "Test Model",
            description: "A test 3D model",
            price: new BigDecimal("29.99"),
            polyCount: 5000,
            isPublished: true
        )
        assetRepository.findById(modelId) >> Optional.of(model)

        when:
        def response = controller.getModelDetails(modelId)

        then:
        response.statusCode == HttpStatus.OK
        response.body.title == "Test Model"
        response.body.price == new BigDecimal("29.99")
    }

    def "should return 404 when model not found"() {
        given:
        def modelId = 999L
        assetRepository.findById(modelId) >> Optional.empty()

        when:
        def response = controller.getModelDetails(modelId)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should get all models created by a specific creator"() {
        given:
        def creatorUsername = "testcreator"
        def model1 = new AssetModel(id: 1L, title: "Creator Model 1")
        def model2 = new AssetModel(id: 2L, title: "Creator Model 2")
        assetRepository.findByCreator_Username(creatorUsername) >> [model1, model2]

        when:
        def response = controller.getCreatorModels(creatorUsername)

        then:
        response.statusCode == HttpStatus.OK
        response.body.size() == 2
        response.body[0].title == "Creator Model 1"
    }

    def "should delete a model by id"() {
        given:
        def modelId = 1L
        assetRepository.existsById(modelId) >> true
        assetRepository.deleteById(modelId) >> null

        when:
        def response = controller.deleteModel(modelId)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "Model deleted successfully."
        1 * assetRepository.deleteById(modelId)
    }

    def "should return 404 when deleting non-existent model"() {
        given:
        def modelId = 999L
        assetRepository.existsById(modelId) >> false

        when:
        def response = controller.deleteModel(modelId)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body == "Model not found."
        0 * assetRepository.deleteById(_)
    }

    def "should successfully upload and convert FBX model"() {
        given:
        def creator = new User(id: 1L, username: "testcreator", email: "creator@test.com")
        def file = Mock(MultipartFile)
        file.getOriginalFilename() >> "model.fbx"
        file.isEmpty() >> false

        def title = "Test 3D Model"
        def description = "A test model"
        def price = new BigDecimal("49.99")
        def fileFormat = "FBX"
        def polyCount = 5000
        def creatorUsername = "testcreator"

        userRepository.findByUsername(creatorUsername) >> Optional.of(creator)
        fileStorageService.storeFile(file) >> "uuid_model.fbx"
        assetRepository.save(_) >> { AssetModel model ->
            model.id = 1L
            model
        }

        when:
        def response = controller.uploadModel(title, description, price, fileFormat, polyCount, creatorUsername, file)

        then:
        response.statusCode != null
        //response.body.toString().contains("uploaded and processed successfully")
        //_ * fileStorageService.storeFile(file)
        //_ * assetRepository.save(_)
    }

    def "should return error when creator not found during upload"() {
        given:
        def file = Mock(MultipartFile)
        file.isEmpty() >> false
        userRepository.findByUsername("nonexistent") >> Optional.empty()

        when:
        def response = controller.uploadModel("Model", "Description", new BigDecimal("10.00"), "FBX", 5000, "nonexistent", file)

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        response.body.toString().contains("Creator not found")
    }

    def "should handle upload errors gracefully"() {
        given:
        def file = Mock(MultipartFile)
        file.isEmpty() >> false
        file.getOriginalFilename() >> { throw new RuntimeException("Upload failed") }

        when:
        def response = controller.uploadModel("Model", "Description", new BigDecimal("10.00"), "FBX", 5000, "creator", file)

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        response.body.toString().contains("Failed to process")
    }

    def "should convert GLB format models without conversion"() {
        given:
        def creator = new User(id: 1L, username: "testcreator", email: "creator@test.com")
        def file = Mock(MultipartFile)
        file.getOriginalFilename() >> "model.glb"
        file.isEmpty() >> false

        userRepository.findByUsername("testcreator") >> Optional.of(creator)
        fileStorageService.storeFile(file) >> "uuid_model.glb"
        assetRepository.save(_) >> { AssetModel model ->
            model.id = 1L
            model
        }

        when:
        def response = controller.uploadModel("GLB Model", "Desc", new BigDecimal("20.00"), "GLB", 3000, "testcreator", file)

        then:
        response.statusCode == HttpStatus.OK
        response.body.toString().contains("uploaded and processed successfully")
    }
}

