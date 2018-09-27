package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.pojos.DublinCoreEntity
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.persistence.repo.LanguageDao
import java.io.File
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.*

class ResourceMetadataMapper(
        private val languageDao: LanguageDao
) : Mapper<Single<DublinCoreEntity>, Maybe<ResourceMetadata>>
{
    override fun mapFromEntity(type: Single<DublinCoreEntity>): Maybe<ResourceMetadata> {
        return type
                .toMaybe()
                .flatMap { entity ->
                    languageDao
                            .getById(entity.languageFk)
                            .map { language ->
                                ResourceMetadata(
                                        entity.conformsto,
                                        entity.creator,
                                        entity.description,
                                        entity.format,
                                        entity.identifier,
                                        ZonedDateTime.parse(entity.issued),
                                        language,
                                        ZonedDateTime.parse(entity.modified),
                                        entity.publisher,
                                        entity.subject,
                                        entity.type,
                                        entity.title,
                                        entity.version,
                                        File(entity.path),
                                        entity.id
                                )
                            }
                }
                .subscribeOn(Schedulers.io())
    }

    override fun mapToEntity(type: Maybe<ResourceMetadata>): Single<DublinCoreEntity> {
        return type
                .toSingle()
                .map {
                    val dublinCore = DublinCoreEntity()
                    dublinCore.id = it.id
                    dublinCore.conformsto = it.conformsTo
                    dublinCore.creator = it.creator
                    dublinCore.description = it.description
                    dublinCore.format = it.format
                    dublinCore.identifier = it.identifier
                    dublinCore.issued = it.issued.toString()
                    dublinCore.languageFk = it.language.id
                    dublinCore.modified = it.modified.toString()
                    dublinCore.publisher = it.publisher
                    dublinCore.subject = it.subject
                    dublinCore.title = it.title
                    dublinCore.type = it.type
                    dublinCore.version = it.version
                    dublinCore.path = it.path.path
                    dublinCore
                }
                .subscribeOn(Schedulers.io())

    }
}