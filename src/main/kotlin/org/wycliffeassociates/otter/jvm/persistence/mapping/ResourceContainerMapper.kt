package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.pojos.DublinCoreEntity
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import org.wycliffeassociates.otter.common.data.model.ResourceContainer
import org.wycliffeassociates.otter.jvm.persistence.repo.LanguageDao
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ResourceContainerMapper(private val languageDao: LanguageDao) : Mapper<Single<DublinCoreEntity>, Maybe<ResourceContainer>> {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())

    init {
        dateFormatter.timeZone = TimeZone.getDefault()
    }

    override fun mapFromEntity(type: Single<DublinCoreEntity>): Maybe<ResourceContainer> {
        return type
                .toMaybe()
                .flatMap { entity ->
                    languageDao
                            .getById(entity.languageFk)
                            .map { language ->
                                ResourceContainer(
                                        entity.conformsto,
                                        entity.creator,
                                        entity.description,
                                        entity.format,
                                        entity.identifier,
                                        with(Calendar.getInstance()) {
                                            time = dateFormatter.parse(entity.issued)
                                            this
                                        },
                                        language,
                                        with(Calendar.getInstance()) {
                                            time = dateFormatter.parse(entity.modified)
                                            this
                                        },
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

    override fun mapToEntity(type: Maybe<ResourceContainer>): Single<DublinCoreEntity> {
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
                    dublinCore.issued = dateFormatter.format(it.issued.time)
                    dublinCore.languageFk = it.language.id
                    dublinCore.modified = dateFormatter.format(it.modified.time)
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