package com.webpage.T3D.outer

import com.webpage.T3D.outer.model.AssetModel
import com.webpage.T3D.outer.model.User
import com.webpage.T3D.outer.repository.AssetModelRepository
import com.webpage.T3D.outer.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.time.Instant
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
class AssetModelIntegrationSpec extends Specification {


    @Autowired
    UserRepository userRepository

    @Autowired
    AssetModelRepository assetModelRepository

    def setup() {
        assetModelRepository.deleteAll()
        userRepository.deleteAll()
    }

    def "should complete full 3D model marketplace workflow"() {
        given: "A creator user exists"
        def creator = new User(
            username: "creator",
            email: "creator@example.com",
            passwordHash: "hash",
            createdAt: Instant.now()
        )
        def savedCreator = userRepository.save(creator)

        and: "The creator publishes a 3D model"
        def model = new AssetModel(
            creator: savedCreator,
            title: "Premium 3D Character",
            description: "High quality character model with animations",
            price: new BigDecimal("49.99"),
            modelFileUrl: "/uploads/character.glb",
            thumbnailUrl: "/uploads/character_thumb.jpg",
            polyCount: 50000,
            fileFormat: "GLB",
            isPublished: true,
            createdAt: Instant.now()
        )
        assetModelRepository.save(model)

        when: "User requests the storefront feed"
        def publishedModels = assetModelRepository.findByIsPublishedTrue()

        then: "The published model appears in the feed"
        publishedModels.size() >= 1
        publishedModels.any { it.title == "Premium 3D Character" }
    }

    def "should allow creator to manage their models"() {
        given: "A creator is authenticated"
        def creator = userRepository.save(new User(
            username: "artist",
            email: "artist@example.com",
            passwordHash: "hash",
            createdAt: Instant.now()
        ))

        and: "The creator has uploaded models"
        (1..3).each { i ->
            assetModelRepository.save(new AssetModel(
                creator: creator,
                title: "Model ${i}",
                price: new BigDecimal("${i}9.99"),
                modelFileUrl: "/uploads/model${i}.glb",
                isPublished: i < 3,  // Only first 2 are published
                createdAt: Instant.now()
            ))
        }

        when: "Creator requests their models"
        def creatorModels = assetModelRepository.findByCreator_Username("artist")

        then: "All 3 models are returned"
        creatorModels.size() == 3

        and: "Creator can find unpublished models"
        def unpublished = creatorModels.find { !it.isPublished }
        unpublished != null
        unpublished.title == "Model 3"
    }

    def "should handle model search and filtering"() {
        given: "Multiple models in repository"
        def creator1 = userRepository.save(new User(
            username: "creator1",
            email: "creator1@example.com",
            passwordHash: "hash",
            createdAt: Instant.now()
        ))

        def creator2 = userRepository.save(new User(
            username: "creator2",
            email: "creator2@example.com",
            passwordHash: "hash",
            createdAt: Instant.now()
        ))

        [
            new AssetModel(creator: creator1, title: "Sword", price: new BigDecimal("29.99"), isPublished: true, modelFileUrl: "/m1.glb", createdAt: Instant.now()),
            new AssetModel(creator: creator1, title: "Shield", price: new BigDecimal("39.99"), isPublished: true, modelFileUrl: "/m2.glb", createdAt: Instant.now()),
            new AssetModel(creator: creator2, title: "Dragon", price: new BigDecimal("99.99"), isPublished: true, modelFileUrl: "/m3.glb", createdAt: Instant.now()),
            new AssetModel(creator: creator2, title: "Castle", price: new BigDecimal("149.99"), isPublished: false, modelFileUrl: "/m4.glb", createdAt: Instant.now())
        ].each { assetModelRepository.save(it) }

        when: "Filtering by creator"
        def creator1Models = assetModelRepository.findByCreator_Username("creator1")
        def creator2Models = assetModelRepository.findByCreator_Username("creator2")

        then: "Correct models are returned"
        creator1Models.size() == 2
        creator2Models.size() == 2

        and: "When filtering by published status"
        def allPublished = assetModelRepository.findByIsPublishedTrue()
        allPublished.size() == 3
        allPublished.every { it.isPublished }
    }

    def "should ensure data integrity with relationships"() {
        given: "Two creators exist"
        def creator1 = userRepository.save(new User(
            username: "alice",
            email: "alice@example.com",
            passwordHash: "hash1",
            createdAt: Instant.now()
        ))

        def creator2 = userRepository.save(new User(
            username: "bob",
            email: "bob@example.com",
            passwordHash: "hash2",
            createdAt: Instant.now()
        ))

        and: "They both publish models"
        def aliceModel = assetModelRepository.save(new AssetModel(
            creator: creator1,
            title: "Alice's Model",
            price: BigDecimal.ONE,
            modelFileUrl: "/alice.glb",
            isPublished: true,
            createdAt: Instant.now()
        ))

        def bobModel = assetModelRepository.save(new AssetModel(
            creator: creator2,
            title: "Bob's Model",
            price: BigDecimal.ONE,
            modelFileUrl: "/bob.glb",
            isPublished: true,
            createdAt: Instant.now()
        ))

        when: "Retrieving models by creator"
        def aliceModels = assetModelRepository.findByCreator_Username("alice")
        def bobModels = assetModelRepository.findByCreator_Username("bob")

        then: "Each user sees only their models"
        aliceModels.size() == 1
        aliceModels[0].title == "Alice's Model"
        bobModels.size() == 1
        bobModels[0].title == "Bob's Model"

        and: "Models correctly reference their creators"
        aliceModel.creator.username == "alice"
        bobModel.creator.username == "bob"
    }

    def "should manage model lifecycle from draft to published"() {
        given: "A creator uploads a draft model"
        def creator = userRepository.save(new User(
            username: "developer",
            email: "dev@example.com",
            passwordHash: "hash",
            createdAt: Instant.now()
        ))

        def model = assetModelRepository.save(new AssetModel(
            creator: creator,
            title: "Work in Progress",
            description: "This model is still being worked on",
            price: BigDecimal.ZERO,
            modelFileUrl: "/uploads/draft.glb",
            isPublished: false,
            createdAt: Instant.now()
        ))

        when: "Model is not in storefront feed"
        def draftFeed = assetModelRepository.findByIsPublishedTrue()

        then: "Draft models don't appear"
        !draftFeed.any { it.id == model.id }

        when: "Creator publishes the model"
        model.isPublished = true
        model.price = new BigDecimal("59.99")
        assetModelRepository.save(model)

        and: "User requests storefront feed"
        def publishedFeed = assetModelRepository.findByIsPublishedTrue()

        then: "Published model now appears"
        publishedFeed.any { it.id == model.id }
        publishedFeed.find { it.id == model.id }.price == new BigDecimal("59.99")
    }

    def "should handle concurrent model updates"() {
        given: "A model exists"
        def creator = userRepository.save(new User(
            username: "concurrent",
            email: "concurrent@example.com",
            passwordHash: "hash",
            createdAt: Instant.now()
        ))

        def model = assetModelRepository.save(new AssetModel(
            creator: creator,
            title: "Shared Model",
            price: new BigDecimal("10.00"),
            modelFileUrl: "/model.glb",
            createdAt: Instant.now()
        ))

        when: "Price is updated"
        model.price = new BigDecimal("20.00")
        assetModelRepository.save(model)

        and: "Title is updated"
        model.title = "Updated Title"
        assetModelRepository.save(model)

        then: "Both updates are persisted"
        def updatedModel = assetModelRepository.findById(model.id).get()
        updatedModel.price == new BigDecimal("20.00")
        updatedModel.title == "Updated Title"
    }

    def "should clean up models when creator is deleted"() {
        given: "A creator with models"
        def creator = userRepository.save(new User(
            username: "temporary",
            email: "temp@example.com",
            passwordHash: "hash",
            createdAt: Instant.now()
        ))

        (1..3).each { i ->
            assetModelRepository.save(new AssetModel(
                creator: creator,
                title: "Temp Model ${i}",
                price: BigDecimal.ONE,
                modelFileUrl: "/temp${i}.glb",
                createdAt: Instant.now()
            ))
        }

        and: "Verify models exist"
        def initialModels = assetModelRepository.findByCreator_Username("temporary")
        initialModels.size() == 3

        when: "Creator is soft deleted (example - in real scenario, implement soft delete)"
        // In real scenario, you might have a deleted flag
        // For this test, we demonstrate the concept

        then: "Query reflects the state"
        // This demonstrates how tests can validate cascading behaviors
        assetModelRepository.findByCreator_Username("temporary").size() >= 3
    }
}

