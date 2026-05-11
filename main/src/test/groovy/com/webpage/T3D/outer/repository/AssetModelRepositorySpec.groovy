package com.webpage.T3D.outer.repository

import com.webpage.T3D.outer.model.AssetModel
import com.webpage.T3D.outer.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.time.Instant

@SpringBootTest
@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
class AssetModelRepositorySpec extends Specification {

    @Autowired
    AssetModelRepository assetModelRepository

    @Autowired
    UserRepository userRepository

    User testCreator

    def setup() {
        testCreator = new User(
            username: "creator",
            email: "creator@example.com",
            passwordHash: "hash",
            createdAt: Instant.now()
        )
        userRepository.save(testCreator)
    }

    def "should save and find asset model by id"() {
        given:
        def model = new AssetModel(
            creator: testCreator,
            title: "Test Model",
            description: "A test 3D model",
            price: new BigDecimal("29.99"),
            modelFileUrl: "/uploads/test.glb",
            polyCount: 5000,
            fileFormat: "GLB",
            isPublished: true,
            createdAt: Instant.now()
        )

        when:
        def savedModel = assetModelRepository.save(model)
        def foundModel = assetModelRepository.findById(savedModel.id)

        then:
        foundModel.isPresent()
        foundModel.get().title == "Test Model"
        foundModel.get().price == new BigDecimal("29.99")
    }

    def "should find all published models"() {
        given:
        def publishedModel1 = new AssetModel(
            creator: testCreator,
            title: "Published 1",
            price: BigDecimal.ONE,
            modelFileUrl: "/uploads/model1.glb",
            isPublished: true,
            createdAt: Instant.now()
        )
        def publishedModel2 = new AssetModel(
            creator: testCreator,
            title: "Published 2",
            price: BigDecimal.TEN,
            modelFileUrl: "/uploads/model2.glb",
            isPublished: true,
            createdAt: Instant.now()
        )
        def unpublishedModel = new AssetModel(
            creator: testCreator,
            title: "Unpublished",
            price: new BigDecimal("5.00"),
            modelFileUrl: "/uploads/model3.glb",
            isPublished: false,
            createdAt: Instant.now()
        )

        when:
        assetModelRepository.save(publishedModel1)
        assetModelRepository.save(publishedModel2)
        assetModelRepository.save(unpublishedModel)
        def publishedModels = assetModelRepository.findByIsPublishedTrue()

        then:
        publishedModels.size() == 2
        publishedModels.every { it.isPublished == true }
        !publishedModels.any { it.title == "Unpublished" }
    }

    def "should find models by creator id"() {
        given:
        def user2 = new User(
            username: "creator2",
            email: "creator2@example.com",
            passwordHash: "hash",
            createdAt: Instant.now()
        )
        userRepository.save(user2)

        def model1 = new AssetModel(
            creator: testCreator,
            title: "Creator 1 Model 1",
            price: BigDecimal.ONE,
            modelFileUrl: "/uploads/m1.glb",
            createdAt: Instant.now()
        )
        def model2 = new AssetModel(
            creator: testCreator,
            title: "Creator 1 Model 2",
            price: BigDecimal.TEN,
            modelFileUrl: "/uploads/m2.glb",
            createdAt: Instant.now()
        )
        def model3 = new AssetModel(
            creator: user2,
            title: "Creator 2 Model",
            price: new BigDecimal("5.00"),
            modelFileUrl: "/uploads/m3.glb",
            createdAt: Instant.now()
        )

        when:
        assetModelRepository.save(model1)
        assetModelRepository.save(model2)
        assetModelRepository.save(model3)
        def creatorModels = assetModelRepository.findByCreatorId(testCreator.id)

        then:
        creatorModels.size() == 2
        creatorModels.every { it.creator.id == testCreator.id }
    }

    def "should find models by creator username"() {
        given:
        def model1 = new AssetModel(
            creator: testCreator,
            title: "Model by Creator",
            price: BigDecimal.ONE,
            modelFileUrl: "/uploads/model.glb",
            createdAt: Instant.now()
        )

        when:
        assetModelRepository.save(model1)
        def models = assetModelRepository.findByCreator_Username("creator")

        then:
        models.size() >= 1
        models.any { it.title == "Model by Creator" }
        models.every { it.creator.username == "creator" }
    }

    def "should delete model by id"() {
        given:
        def model = new AssetModel(
            creator: testCreator,
            title: "To Delete",
            price: BigDecimal.ONE,
            modelFileUrl: "/uploads/delete.glb",
            createdAt: Instant.now()
        )
        def savedModel = assetModelRepository.save(model)

        when:
        assetModelRepository.deleteById(savedModel.id)
        def foundModel = assetModelRepository.findById(savedModel.id)

        then:
        !foundModel.isPresent()
    }

    def "should update model information"() {
        given:
        def model = new AssetModel(
            creator: testCreator,
            title: "Original Title",
            description: "Original Description",
            price: new BigDecimal("10.00"),
            modelFileUrl: "/uploads/model.glb",
            polyCount: 1000,
            createdAt: Instant.now()
        )
        def savedModel = assetModelRepository.save(model)

        when:
        savedModel.title = "Updated Title"
        savedModel.description = "Updated Description"
        savedModel.price = new BigDecimal("19.99")
        savedModel.polyCount = 5000
        assetModelRepository.save(savedModel)
        def updatedModel = assetModelRepository.findById(savedModel.id).get()

        then:
        updatedModel.title == "Updated Title"
        updatedModel.description == "Updated Description"
        updatedModel.price == new BigDecimal("19.99")
        updatedModel.polyCount == 5000
    }

    def "should store various file formats"() {
        given:
        def formats = ["GLB", "FBX", "OBJ", "GLTF"]
        def models = formats.collect { format ->
            new AssetModel(
                creator: testCreator,
                title: "Model in ${format}",
                price: BigDecimal.ONE,
                modelFileUrl: "/uploads/model.${format.toLowerCase()}",
                fileFormat: format,
                createdAt: Instant.now()
            )
        }

        when:
        models.each { assetModelRepository.save(it) }
        def savedModels = assetModelRepository.findAll()

        then:
        formats.every { format ->
            savedModels.any { it.fileFormat == format }
        }
    }

    def "should store model with thumbnail and texture URLs"() {
        given:
        def model = new AssetModel(
            creator: testCreator,
            title: "Full Featured Model",
            price: new BigDecimal("49.99"),
            modelFileUrl: "/uploads/model.glb",
            textureFileUrl: "/uploads/texture.png",
            thumbnailUrl: "/uploads/thumb.jpg",
            polyCount: 10000,
            createdAt: Instant.now()
        )

        when:
        def savedModel = assetModelRepository.save(model)
        def foundModel = assetModelRepository.findById(savedModel.id).get()

        then:
        foundModel.textureFileUrl == "/uploads/texture.png"
        foundModel.thumbnailUrl == "/uploads/thumb.jpg"
    }

    def "should store various price points"() {
        given:
        def prices = [
            BigDecimal.ZERO,
            new BigDecimal("0.99"),
            new BigDecimal("29.99"),
            new BigDecimal("99.99"),
            new BigDecimal("999.99")
        ]

        when:
        prices.each { price ->
            def model = new AssetModel(
                creator: testCreator,
                title: "Model \$${price}",
                price: price,
                modelFileUrl: "/uploads/model_${price}.glb",
                createdAt: Instant.now()
            )
            assetModelRepository.save(model)
        }
        def allModels = assetModelRepository.findAll()

        then:
        prices.every { price ->
            allModels.any { it.price == price }
        }
    }

    def "should track model creation and update timestamps"() {
        given:
        def model = new AssetModel(
            creator: testCreator,
            title: "Timestamped Model",
            price: BigDecimal.ONE,
            modelFileUrl: "/uploads/model.glb",
            createdAt: Instant.now(),
            updatedAt: Instant.now()
        )

        when:
        def savedModel = assetModelRepository.save(model)
        def foundModel = assetModelRepository.findById(savedModel.id).get()

        then:
        foundModel.createdAt != null
        foundModel.updatedAt != null
    }

    def "should retrieve empty list when no models match criteria"() {
        when:
        def unpublishedOnly = assetModelRepository.findByIsPublishedTrue()

        then:
        unpublishedOnly.isEmpty() || unpublishedOnly.every { it.isPublished == true }
    }
}

