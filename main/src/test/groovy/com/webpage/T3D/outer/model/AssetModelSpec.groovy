package com.webpage.T3D.outer.model

import spock.lang.Specification

import java.time.Instant

class AssetModelSpec extends Specification {

    def "should create asset model with all properties"() {
        given:
        def creator = new User(username: "creator", email: "creator@example.com", passwordHash: "hash")
        def now = Instant.now()

        when:
        def model = new AssetModel(
            id: 1L,
            creator: creator,
            title: "3D Character Model",
            description: "A detailed 3D character model",
            price: new BigDecimal("49.99"),
            modelFileUrl: "/uploads/character.glb",
            textureFileUrl: "/uploads/texture.png",
            thumbnailUrl: "/uploads/thumb.jpg",
            polyCount: 10000,
            fileFormat: "GLB",
            isPublished: true,
            createdAt: now,
            updatedAt: now
        )

        then:
        model.id == 1L
        model.creator == creator
        model.title == "3D Character Model"
        model.description == "A detailed 3D character model"
        model.price == new BigDecimal("49.99")
        model.modelFileUrl == "/uploads/character.glb"
        model.textureFileUrl == "/uploads/texture.png"
        model.thumbnailUrl == "/uploads/thumb.jpg"
        model.polyCount == 10000
        model.fileFormat == "GLB"
        model.isPublished == true
        model.createdAt == now
        model.updatedAt == now
    }

    def "should have default values for optional fields"() {
        when:
        def model = new AssetModel(
            creator: new User(),
            title: "Model"
        )

        then:
        model.price == BigDecimal.ZERO
        model.isPublished == false
        model.createdAt != null
        model.updatedAt != null
    }

    def "should allow setting and getting title"() {
        given:
        def model = new AssetModel()

        when:
        model.title = "Sword Model"

        then:
        model.title == "Sword Model"
    }

    def "should allow setting and getting description"() {
        given:
        def model = new AssetModel()

        when:
        model.description = "A detailed sword with PBR textures"

        then:
        model.description == "A detailed sword with PBR textures"
    }

    def "should allow setting and getting price"() {
        given:
        def model = new AssetModel()

        when:
        model.price = new BigDecimal("29.99")

        then:
        model.price == new BigDecimal("29.99")
    }

    def "should allow setting various price points"() {
        given:
        def model = new AssetModel()

        when:
        def prices = [
            BigDecimal.ZERO,
            new BigDecimal("0.99"),
            new BigDecimal("9.99"),
            new BigDecimal("99.99")
        ]

        then:
        prices.every { price ->
            model.price = price
            model.price == price
        }
    }

    def "should allow setting file URLs"() {
        given:
        def model = new AssetModel()

        when:
        model.modelFileUrl = "/uploads/model.glb"
        model.textureFileUrl = "/uploads/texture.png"
        model.thumbnailUrl = "/uploads/thumb.jpg"

        then:
        model.modelFileUrl == "/uploads/model.glb"
        model.textureFileUrl == "/uploads/texture.png"
        model.thumbnailUrl == "/uploads/thumb.jpg"
    }

    def "should allow setting poly count"() {
        given:
        def model = new AssetModel()

        when:
        model.polyCount = 5000

        then:
        model.polyCount == 5000
    }

    def "should support various file formats"() {
        given:
        def model = new AssetModel()

        when:
        def formats = ["GLB", "FBX", "OBJ", "GLTF"]

        then:
        formats.every { format ->
            model.fileFormat = format
            model.fileFormat == format
        }
    }

    def "should allow publishing models"() {
        given:
        def model = new AssetModel()

        when:
        model.isPublished = true

        then:
        model.isPublished == true
    }

    def "should allow unpublishing models"() {
        given:
        def model = new AssetModel(isPublished: true)

        when:
        model.isPublished = false

        then:
        model.isPublished == false
    }

    def "should track creator relationship"() {
        given:
        def creator1 = new User(username: "creator1", email: "creator1@example.com", passwordHash: "hash1")
        def creator2 = new User(username: "creator2", email: "creator2@example.com", passwordHash: "hash2")
        def model = new AssetModel(creator: creator1)

        when:
        model.creator = creator2

        then:
        model.creator.username == "creator2"
    }

    def "should support null optional fields"() {
        given:
        def model = new AssetModel(title: "Basic Model", creator: new User())

        expect:
        model.description == null
        model.textureFileUrl == null
        model.thumbnailUrl == null
        model.polyCount == null
    }

    def "should track timestamps"() {
        given:
        def now = Instant.now()
        def model = new AssetModel(createdAt: now, updatedAt: now)

        expect:
        model.createdAt == now
        model.updatedAt == now
    }

    def "should allow updating model information"() {
        given:
        def model = new AssetModel(
            title: "Original Title",
            description: "Original",
            price: new BigDecimal("10.00")
        )

        when:
        model.title = "Updated Title"
        model.description = "Updated Description"
        model.price = new BigDecimal("20.00")

        then:
        model.title == "Updated Title"
        model.description == "Updated Description"
        model.price == new BigDecimal("20.00")
    }

    def "should support high precision prices"() {
        given:
        def model = new AssetModel()

        when:
        model.price = new BigDecimal("999.99")

        then:
        model.price == new BigDecimal("999.99")
    }

    def "should support high poly count models"() {
        given:
        def model = new AssetModel()

        when:
        model.polyCount = 1000000

        then:
        model.polyCount == 1000000
    }
}

