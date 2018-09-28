//package org.wycliffeassociates.otter.jvm.persistence.repositories
//
//import io.reactivex.Completable
//
//import io.reactivex.Single
//import io.reactivex.schedulers.Schedulers
//import org.wycliffeassociates.otter.common.data.model.Chunk
//import org.wycliffeassociates.otter.common.data.model.Collection
//import org.wycliffeassociates.otter.common.data.model.Take
//import org.wycliffeassociates.otter.common.persistence.repositories.IChunkRepository
//import org.wycliffeassociates.otter.jvm.persistence.database.IAppDatabase
//import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ChunkMapper
//import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.MarkerMapper
//
//class ChunkRepository(
//        database: IAppDatabase,
//        private val chunkMapper: ChunkMapper,
//        private val takeMapper: TakeRepository,
//        private val markerMapper: MarkerMapper
//) : IChunkRepository {
//    private val chunkDao = database.getChunkDao()
//
//    override fun delete(obj: Chunk): Completable {
//        return Completable
//                .fromAction {
//                    chunkDao.delete(chunkMapper.mapToEntity(obj))
//                }
//                .subscribeOn(Schedulers.io())
//    }
//
//    override fun getAll(): Single<List<Chunk>> {
//        return Single
//                .fromCallable {
//                    chunkDao
//                            .fetchAll()
//                            .map {
//                                chunkMapper.mapFromEntity(it)
//                            }
//                }
//                .map {
//
//                }
//                .subscribeOn(Schedulers.io())
//    }
//
//    override fun getByCollection(collection: Collection): Single<List<Take>> {
//        return Single
//                .fromCallable {
//                    takeDao
//                            .fetchByChunkId(chunk.id)
//                            .map { takeEntity ->
//                                val markers = markerDao
//                                        .fetchByTakeId(takeEntity.id)
//                                        .map { markerMapper.mapFromEntity(it) }
//                                takeMapper.mapFromEntity(takeEntity, markers)
//                            }
//                }
//                .subscribeOn(Schedulers.io())
//    }
//
//    override fun insert(obj: Take): Single<Int> {
//        return Single
//                .fromCallable {
//                    val takeId = takeDao.insert(takeMapper.mapToEntity(obj))
//                    // Insert the markers
//                    obj.markers.forEach {
//                        val entity = markerMapper.mapToEntity(it)
//                        entity.id = 0
//                        entity.takeFk = takeId
//                        markerDao.insert(entity)
//                    }
//                    takeId
//                }
//                .subscribeOn(Schedulers.io())
//    }
//
//    override fun update(obj: Take): Completable {
//        return Completable
//                .fromAction {
//                    val existing = takeDao.fetchById(obj.id)
//                    val entity = takeMapper.mapToEntity(obj)
//                    entity.contentFk = existing.contentFk
//                    takeDao.update(entity)
//
//                    // Delete and replace markers
//                    markerDao
//                            .fetchByTakeId(obj.id)
//                            .forEach {
//                                markerDao.delete(it)
//                            }
//
//                    obj.markers.forEach {
//                        val markerEntity = markerMapper.mapToEntity(it)
//                        markerEntity.id = 0
//                        markerEntity.takeFk = obj.id
//                        markerDao.insert(markerEntity)
//                    }
//                }
//                .subscribeOn(Schedulers.io())
//    }
//
//}