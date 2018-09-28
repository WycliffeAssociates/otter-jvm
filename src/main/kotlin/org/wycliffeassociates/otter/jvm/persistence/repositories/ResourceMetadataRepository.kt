package org.wycliffeassociates.otter.jvm.persistence.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.persistence.repositories.IResourceMetadataRepository
import org.wycliffeassociates.otter.jvm.persistence.database.IAppDatabase
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.LanguageMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ResourceMetadataMapper

class ResourceMetadataRepository(
        database: IAppDatabase,
        private val metadataMapper: ResourceMetadataMapper,
        private val languageMapper: LanguageMapper
) : IResourceMetadataRepository {
    private val resourceMetadataDao = database.getResourceMetadataDao()
    private val languageDao = database.getLanguageDao()

    override fun insert(obj: ResourceMetadata): Single<Int> {
        return Single
                .fromCallable {
                    resourceMetadataDao.insert(metadataMapper.mapToEntity(obj))
                }
                .subscribeOn(Schedulers.io())
    }

    override fun getAll(): Single<List<ResourceMetadata>> {
        return Single
                .fromCallable {
                    resourceMetadataDao
                            .fetchAll()
                            .map {
                                val language = languageMapper
                                        .mapFromEntity(languageDao.fetchById(it.languageFk))
                                metadataMapper.mapFromEntity(it, language)
                            }
                }
                .subscribeOn(Schedulers.io())
    }

    override fun update(obj: ResourceMetadata): Completable {
        return Completable
                .fromAction {
                    resourceMetadataDao.update(metadataMapper.mapToEntity(obj))
                }
                .subscribeOn(Schedulers.io())
    }

    override fun delete(obj: ResourceMetadata): Completable {
        return Completable
                .fromAction {
                    resourceMetadataDao.delete(metadataMapper.mapToEntity(obj))
                }
                .subscribeOn(Schedulers.io())
    }
}