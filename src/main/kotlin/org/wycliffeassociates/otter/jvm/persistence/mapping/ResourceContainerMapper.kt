package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Observable
import jooq.tables.pojos.DublinCoreEntity
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceContainer
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ResourceContainerMapper(private val languageDao: Dao<Language>) : Mapper<Observable<DublinCoreEntity>, Observable<ResourceContainer>> {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())

    init {
        dateFormatter.timeZone = TimeZone.getDefault()
    }

    override fun mapFromEntity(type: Observable<DublinCoreEntity>): Observable<ResourceContainer> {
        return type
                .flatMap { entity ->
                    languageDao
                            .getById(entity.languageFk)
                            .map { language ->
                                ResourceContainer(
                                        entity.id,
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
                                        File(entity.path)
                                )
                            }
                }
    }

    override fun mapToEntity(type: Observable<ResourceContainer>): Observable<DublinCoreEntity> {
        return type
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
    }

}