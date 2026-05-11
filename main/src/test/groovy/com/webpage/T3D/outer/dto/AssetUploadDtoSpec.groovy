package com.webpage.T3D.outer.dto

import lombok.Data
import lombok.NoArgsConstructor
import spock.lang.Specification

@Data
@NoArgsConstructor
class AssetUploadDtoSpec extends Specification {

    def "should create DTO with all properties"() {
        when:
        def dto = new AssetUploadDto(
            creatorId: 1L,
            title: "3D Model",
            description: "A detailed 3D model",
            price: new BigDecimal("29.99"),
            fileFormat: "GLB",
            polyCount: 5000,
            modelFileUrl: "/uploads/model.glb",
            isPublished: true
        )

        then:
        dto.creatorId == 1L
        dto.title == "3D Model"
        dto.description == "A detailed 3D model"
        dto.price == new BigDecimal("29.99")
        dto.fileFormat == "GLB"
        dto.polyCount == 5000
        dto.modelFileUrl == "/uploads/model.glb"
        dto.isPublished == true
    }

    def "should allow setting and getting creator id"() {
        given:
        def dto = new AssetUploadDto()

        when:
        dto.creatorId = 123L

        then:
        dto.creatorId == 123L
    }

    def "should allow setting and getting title"() {
        given:
        def dto = new AssetUploadDto()

        when:
        dto.title = "Awesome Model"

        then:
        dto.title == "Awesome Model"
    }

    def "should allow setting and getting description"() {
        given:
        def dto = new AssetUploadDto()

        when:
        dto.description = "High quality 3D asset with textures"

        then:
        dto.description == "High quality 3D asset with textures"
    }

    def "should allow setting and getting price"() {
        given:
        def dto = new AssetUploadDto()

        when:
        dto.price = new BigDecimal("49.99")

        then:
        dto.price == new BigDecimal("49.99")
    }

    def "should support various price points"() {
        given:
        def dto = new AssetUploadDto()

        when:
        def prices = [
            BigDecimal.ZERO,
            new BigDecimal("0.99"),
            new BigDecimal("9.99"),
            new BigDecimal("99.99"),
            new BigDecimal("999.99")
        ]

        then:
        prices.every { price ->
            dto.price = price
            dto.price == price
        }
    }

    def "should allow setting and getting file format"() {
        given:
        def dto = new AssetUploadDto()

        when:
        dto.fileFormat = "FBX"

        then:
        dto.fileFormat == "FBX"
    }

    def "should support various file formats"() {
        given:
        def dto = new AssetUploadDto()

        when:
        def formats = ["GLB", "FBX", "OBJ", "GLTF"]

        then:
        formats.every { format ->
            dto.fileFormat = format
            dto.fileFormat == format
        }
    }

    def "should allow setting and getting poly count"() {
        given:
        def dto = new AssetUploadDto()

        when:
        dto.polyCount = 10000

        then:
        dto.polyCount == 10000
    }

    def "should support high poly count values"() {
        given:
        def dto = new AssetUploadDto()

        when:
        dto.polyCount = 5000000

        then:
        dto.polyCount == 5000000
    }

    def "should allow setting and getting model file URL"() {
        given:
        def dto = new AssetUploadDto()

        when:
        dto.modelFileUrl = "/uploads/character.glb"

        then:
        dto.modelFileUrl == "/uploads/character.glb"
    }

    def "should allow setting and getting published flag"() {
        given:
        def dto = new AssetUploadDto()

        when:
        dto.isPublished = true

        then:
        dto.isPublished == true
    }

    def "should toggle published state"() {
        given:
        def dto = new AssetUploadDto(isPublished: false)

        when:
        dto.isPublished = true

        then:
        dto.isPublished == true
    }

    def "should handle null values for optional fields"() {
        given:
        def dto = new AssetUploadDto()

        expect:
        dto.creatorId == null
        dto.title == null
        dto.description == null
        dto.price == null
        dto.fileFormat == null
        dto.polyCount == null
        dto.modelFileUrl == null
        dto.isPublished == null
    }

    def "should allow updating all properties"() {
        given:
        def dto = new AssetUploadDto(
            creatorId: 1L,
            title: "Original",
            description: "Original Description",
            price: new BigDecimal("10.00"),
            fileFormat: "OBJ",
            polyCount: 1000,
            modelFileUrl: "/uploads/old.obj",
            isPublished: false
        )

        when:
        dto.creatorId = 2L
        dto.title = "Updated"
        dto.description = "Updated Description"
        dto.price = new BigDecimal("20.00")
        dto.fileFormat = "GLB"
        dto.polyCount = 5000
        dto.modelFileUrl = "/uploads/new.glb"
        dto.isPublished = true

        then:
        dto.creatorId == 2L
        dto.title == "Updated"
        dto.description == "Updated Description"
        dto.price == new BigDecimal("20.00")
        dto.fileFormat == "GLB"
        dto.polyCount == 5000
        dto.modelFileUrl == "/uploads/new.glb"
        dto.isPublished == true
    }

    def "should handle long descriptions"() {
        given:
        def dto = new AssetUploadDto()
        def longDescription = "A" * 5000

        when:
        dto.description = longDescription

        then:
        dto.description == longDescription
        dto.description.length() == 5000
    }

    def "should support builder pattern if available"() {
        when:
        def dto = new AssetUploadDto()
        dto.creatorId = 1L
        dto.title = "Model"
        dto.price = new BigDecimal("29.99")
        dto.fileFormat = "GLB"

        then:
        dto.creatorId == 1L
        dto.title == "Model"
        dto.price == new BigDecimal("29.99")
        dto.fileFormat == "GLB"
    }
}

