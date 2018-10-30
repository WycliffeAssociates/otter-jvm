package org.wycliffeassociates.otter.jvm.persistence.entities

data class TakeEntity(
        var id: Int,
        var contentFk: Int,
        var filename: String,
        var filepath: String,
        var number: Int,
        var timestamp: String,
        var played: Int
)